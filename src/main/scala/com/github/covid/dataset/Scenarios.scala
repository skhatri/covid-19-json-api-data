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
    saveFile("data/all_counters.json", objectMapper.writeValueAsString(allCounters))
    allCounters
  }

  /**
   * Generates the list of countries to be used as index or menu at ./data/country.json
   * It also produces historical data by country at ./data/by-country/{country_key}.json
   * The attribute key in ./data/country.json is used as {country_key} when retrieving dataset for a country/province
   */
  private[this] def generateDatasetByCountry(allCounters: Seq[CovidAggregate]): Unit = {
    //by country
    allCounters.foreach(covAgg => {
      saveFile(s"data/by-country/${covAgg.country_province_key}.json", objectMapper.writeValueAsString(covAgg))
    })
    //save country names as index
    val countryIndex = allCounters.map(covAgg => {
      Map("key" -> covAgg.country_province_key,
        "province_state" -> covAgg.province_state,
        "country_region" -> covAgg.country_region,
        "_self" -> s"${baseUrl}/by-country/${covAgg.country_province_key}.json"
      )
    })
    saveFile("data/country.json", objectMapper.writeValueAsString(countryIndex))
  }

  /**
   * Dataset for the latest available date
   */
  private[this] def generateLatestAvailableDateStats(allCounters: Seq[CovidAggregate]): Unit = {
    implicit val epochOrder: Ordering[LocalDate] = Ordering.by(_.toEpochDay)
    val maxDate = allCounters.head.timeline.keys.map(dateKeys => {
      LocalDate.parse(dateKeys, DateTimeFormatter.ofPattern(R.outputDateFormatValue))
    }).max

    val dateKey = maxDate.format(DateTimeFormatter.ofPattern(R.outputDateFormatValue))

    val latestCounters = allCounters.map(covAgg => {
      val timeline: Map[String, Int] = covAgg.timeline.getOrElse(dateKey, Map.empty[String, Int])
      covAgg.copy(timeline = Map(dateKey -> timeline))
    })
    saveFile("data/latest_counters.json", objectMapper.writeValueAsString(latestCounters))

    val totals = allCounters.foldLeft(Map.empty[String, Int])((agg, covAgg) => {
      val timeline: Map[String, Int] = covAgg.timeline.getOrElse(dateKey, Map.empty[String, Int])
      val currRecover = timeline.getOrElse("recovered", 0)
      val currConfirmed = timeline.getOrElse("confirmed", 0)
      val currDeaths = timeline.getOrElse("deaths", 0)
      Map(
        "recovered" -> agg.get("recovered").map(tot => currRecover + tot).getOrElse(currRecover),
        "confirmed" -> agg.get("confirmed").map(tot => currConfirmed + tot).getOrElse(currConfirmed),
        "deaths" -> agg.get("deaths").map(tot => currDeaths + tot).getOrElse(currDeaths)
      )
    })
    val summary = Map("counts" -> totals, "date" -> maxDate.format(DateTimeFormatter.ISO_DATE))
    saveFile("data/totals.json", objectMapper.writeValueAsString(summary))

  }


  /**
   * Generates date-wise dataset
   * Resource available at ./data/by-date/{yyyy-MM-dd}.json
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
        saveFile(s"data/by-date/$dateKey.json", objectMapper.writeValueAsString(tuple._2))
      })
  }

  private[this] def generateTotals(allCounters: Seq[CovidAggregate]): Unit = {

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
