package com.github.covid.dataset

import java.io.FileWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

class Scenarios {
  type Counter = Map[String, Int]
  val baseUrl = "https://raw.githubusercontent.com/skhatri/covid-19-csv-to-api-data/master/data"

  val objectMapper = new ObjectMapper()
  val scalaModule = new DefaultScalaModule()
  objectMapper.registerModule(scalaModule)

  private[this] def saveFile(name: String, content: String) = {
    val fw = new FileWriter(name)
    fw.write(content)
    fw.flush()
    fw.close()
  }

  private[this] def generateDatasetByStatus(save: Boolean = true): List[CovidItem] = {
    val (confirmedItems, recoveredItems, deathItems) = new DatasetParser().parseDatasets()
    if (save) {
      saveFile("data/confirmed.json", objectMapper.writeValueAsString(confirmedItems))
      saveFile("data/recovered.json", objectMapper.writeValueAsString(recoveredItems))
      saveFile("data/deaths.json", objectMapper.writeValueAsString(deathItems))
    }
    confirmedItems ++ recoveredItems ++ deathItems
  }


  //aggregate them all into one
  def mergeAggregate(items: Seq[CovidAggregate]) : CovidAggregate = {
    val first = items.head
    val template = CovidAggregate(null, first.country_region, first.lat, first.lon, Map.empty[String, Counter], true)

    def mergeCounts(m1:Map[String, Int], m2:Map[String, Int]):Map[String, Int] = {
      m2.foldLeft(m1)((acc, kv) => {
        acc.get(kv._1) match {
          case Some(count) => acc ++ Map(kv._1 -> (count + kv._2))
          case None => acc ++ Map(kv._1 -> kv._2)
        }
      })
    }

    items.foldLeft(template)((agg, item) => {
      val runningTimeline = item.timeline.map(kv => {
        agg.timeline.get(kv._1) match {
          case Some(existing) => (kv._1, mergeCounts(existing, kv._2))
          case None => (kv._1, kv._2)
        }
      })
      agg.copy(timeline = runningTimeline)
    })
  }

  /**
   * Output: {"counts": {}, "items": []}
   *
   * @param unionData
   * @return
   */
  private[this] def mergeDatasetCountersByDateHierarchy(unionData: List[CovidItem]): Seq[CovidAggregate] = {
    val allCounters: Seq[CovidAggregate] = unionData.groupBy(cv => (cv.country_region, cv.province_state))
      .mapValues(itemsInCategory => {
        assert(itemsInCategory.nonEmpty)
        val first = itemsInCategory.head
        val counters = itemsInCategory.foldLeft(Map.empty[String, Counter])((ctr, cv) => {
          val dateCounts: Map[String, Map[String, Int]] = cv.timeline.map(kv => {
            ctr.get(kv._1) match {
              case Some(existing) => (kv._1, existing ++ Map(cv.status -> kv._2))
              case None => (kv._1, Map(cv.status -> kv._2))
            }
          })
          ctr ++ dateCounts
        })
        CovidAggregate(first.province_state, first.country_region, first.lat, first.lon, counters)
      }).values.toSeq


    val countryRolledUp:Map[String, CovidAggregate] = allCounters.groupBy(_.country_region).filter(_._2.size > 1).mapValues(mergeAggregate)
    val countryProvincesAgg = allCounters ++ countryRolledUp.values


    //totals should exclude province data
    val totals = createTotals(countryProvincesAgg.filter(_.isCountryLevel))
    val output = Map("counts" -> totals, "items" -> countryProvincesAgg)
    saveFile("data/all_counters.json", objectMapper.writeValueAsString(output))
    countryProvincesAgg
  }


  /**
   * Generates the list of countries to be used as index or menu at ./data/country.json
   * It also produces historical data by country at ./data/by-country/{country_key}.json
   * The attribute key in ./data/country.json is used as {country_key} when retrieving dataset for a country/province
   *
   * Output Country Dataset:
   * {"counts": {}, "items": []}
   *
   * Output Country List:
   * [{ "key": "", "province_state": "", "country_region": "", "_self": ""}]
   */
  private[this] def generateDatasetByCountry(allCounters: Seq[CovidAggregate]): Unit = {
    def saveCovidData(covAgg: CovidAggregate ) = {
      val items = Seq(covAgg)
      val totals = createTotals(items)
      val output = Map("counts" -> totals, "items" -> items )
      saveFile(s"data/by-country/${covAgg.country_province_key}.json", objectMapper.writeValueAsString(output))
    }
    //by country
    allCounters.foreach(saveCovidData)
    //save country names as index
    val countryIndex = allCounters.map(covAgg => {
      Map("key" -> covAgg.country_province_key,
        "province_state" -> covAgg.province_state,
        "country_region" -> covAgg.country_region,
        "_self" -> s"$baseUrl/by-country/${covAgg.country_province_key}.json",
        "has_province" -> covAgg.has_province
      )
    })
    saveFile("data/country.json", objectMapper.writeValueAsString(countryIndex))
  }

  /**
   * Dataset for the latest available date
   *
   * Output Latest Counters:
   *  {counts: {}, items: []
   * Output Totals:
   *  {counts: {}, date: ""}
   */
  private[this] def generateLatestAvailableDateStats(allCounters: Seq[CovidAggregate]): Unit = {
    val anyAggregateInstance = allCounters.head
    val maxDate: LocalDate = findMaxDate(anyAggregateInstance)

    val dateKey = maxDate.format(DateTimeFormatter.ofPattern(R.outputDateFormatValue))

    val latestTotals = createTotals(allCounters.filter(_.isCountryLevel))

    val latestCounters: Seq[CovidAggregate] = allCounters.map(covAgg => {
      val timeline: Map[String, Int] = covAgg.timeline.getOrElse(dateKey, Map.empty[String, Int])
      covAgg.copy(timeline = Map("counts" -> timeline))
    })
    val output = Map("counts" -> latestTotals, "items" -> latestCounters)

    saveFile("data/latest_counters.json", objectMapper.writeValueAsString(output))

    val totals: Map[String, Int] = createTotals(allCounters.filter(_.isCountryLevel), dateKey)
    val summary = Map("counts" -> totals, "date" -> maxDate.format(DateTimeFormatter.ISO_DATE))
    saveFile("data/totals.json", objectMapper.writeValueAsString(summary))

  }

  private[this] def createTotals(allCounters: Seq[CovidAggregate], dateKey: String = ""): Map[String, Int] = {
    val key = if (dateKey == "") {
      findMaxDate(allCounters.head).format(DateTimeFormatter.ofPattern(R.outputDateFormatValue))
    } else {
      dateKey
    }

    allCounters.foldLeft(Map.empty[String, Int])((agg, covAgg) => {
      val timeline: Map[String, Int] = covAgg.timeline.getOrElse(key, Map.empty[String, Int])
      val currRecover = timeline.getOrElse("recovered", 0)
      val currConfirmed = timeline.getOrElse("confirmed", 0)
      val currDeaths = timeline.getOrElse("deaths", 0)
      Map(
        "recovered" -> agg.get("recovered").map(tot => currRecover + tot).getOrElse(currRecover),
        "confirmed" -> agg.get("confirmed").map(tot => currConfirmed + tot).getOrElse(currConfirmed),
        "deaths" -> agg.get("deaths").map(tot => currDeaths + tot).getOrElse(currDeaths)
      )
    })
  }

  private[this] def findMaxDate(anyAggregateInstance: CovidAggregate) = {
    implicit val epochOrder: Ordering[LocalDate] = Ordering.by(_.toEpochDay)
    val maxDate = anyAggregateInstance.timeline.keys.map(dateKeys => {
      LocalDate.parse(dateKeys, DateTimeFormatter.ofPattern(R.outputDateFormatValue))
    }).max
    maxDate
  }

  /**
   * Generates date-wise dataset
   * Resource available at ./data/by-date/{yyyy-MM-dd}.json
   * Output:
   * {counts: {}, items: []}
   */
  private[this] def generateDatasetByDate(allCounters: Seq[CovidAggregate]): Unit = {
    allCounters.flatMap(covAgg => {
      covAgg.timeline.map(dateKeyCount => {
        (dateKeyCount._1, covAgg.copy(timeline = Map(dateKeyCount._1 -> dateKeyCount._2)))
      })
    }).groupBy(_._1)
      .mapValues(_.map(_._2))
      .foreach(tuple => {
        val dateKey = LocalDate.parse(tuple._1, DateTimeFormatter.ofPattern(R.outputDateFormatValue))
          .format(DateTimeFormatter.ISO_DATE)
        val totals = createTotals(tuple._2.filter(_.isCountryLevel))
        val output = Map("counts" -> totals, "items" -> tuple._2)
        saveFile(s"data/by-date/$dateKey.json", objectMapper.writeValueAsString(output))
      })
  }

  /**
   * Generate all data files
   */
  def execute(): Unit = {
    val unionData = this.generateDatasetByStatus()
    val mergedAggregate = this.mergeDatasetCountersByDateHierarchy(unionData)
    this.generateDatasetByCountry(mergedAggregate)
    this.generateLatestAvailableDateStats(mergedAggregate)
    this.generateDatasetByDate(mergedAggregate)
  }

}
