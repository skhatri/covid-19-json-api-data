package com.github.covid.dataset

import java.io.{File, FileWriter}
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.commons.io.FileUtils

class Scenarios {


  type Counter = Map[String, Int]
  val baseUrl = "https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data"

  val objectMapper = new ObjectMapper()
  val scalaModule = new DefaultScalaModule()
  objectMapper.registerModule(scalaModule)

  private[this] def saveFile(name: String, content: String) = {
    val fw = new FileWriter(name)
    fw.write(content)
    fw.flush()
    fw.close()
  }

  def generatePopulationDataset(datasetParser: DatasetParser): Unit = {
    val countryPopulationHistory = datasetParser.parsePopulation()
    val currentPop: Map[String, Map[String, Long]] = countryPopulationHistory.map(cph => (cph.country, Map(
      "year" -> cph.year.toLong,
      "population" -> cph.population
    ))).toMap
    saveFile("data/population/population.json", objectMapper.writeValueAsString(currentPop))

    val pairMap: Map[Int, Long] => Seq[Map[String, Long]] = m => m.foldLeft(Seq.empty[Map[String, Long]])((acc, item) => {
      acc :+ Map("year" -> item._1.toLong, "population" -> item._2)
    })
    val history: Map[String, Seq[Map[String, Long]]] = countryPopulationHistory.map(cph => (cph.country, pairMap(cph.history))).toMap
    saveFile("data/population/history.json", objectMapper.writeValueAsString(history))
  }

  def getBadgeRequirements(datasetParser: DatasetParser): Seq[BadgeRequirement] = datasetParser.parseBadgeFile()

  def generateBadges(badgeRequirements: Seq[BadgeRequirement], stats: (Seq[CovidAggregate], Map[String, Int])): Unit = {
    val latestCounters = stats._1
    val globalCounters = stats._2
    val counters: Map[String, Map[String, Int]] = latestCounters.flatMap(cv => cv.timeline.get("counts") match {
      case Some(counterMap) => Some((cv.country_province_key, counterMap))
      case None => None
    }).toMap ++ Map("World" -> globalCounters)
    val badgeList = badgeRequirements.flatMap(br => {
      counters.get(br.key) match {
        case Some(valueMap) => {
          for {
            confirmed <- valueMap.get("confirmed")
            recovered <- valueMap.get("recovered")
            deaths <- valueMap.get("deaths")
          }
            yield CovidBadgeData(br.key, br.display_name, confirmed, recovered, deaths, br.link)
        }
        case None => None
      }
    })
    badgeList.foreach(bd => {
      saveFile(s"data/badges/${bd.key}-confirmed.json", objectMapper.writeValueAsString(bd.totalConfirmed()))
      saveFile(s"data/badges/${bd.key}-recovered.json", objectMapper.writeValueAsString(bd.totalRecovered()))
      saveFile(s"data/badges/${bd.key}-deaths.json", objectMapper.writeValueAsString(bd.totalDeaths()))
    })
    val readme = FileUtils.readFileToString(new File(".README.tmpl"))
    val badgeMessage = badgeList.map(bd => {
      s"""
         |${bd.totalConfirmed()} ${bd.totalRecovered()} ${bd.totalDeaths()}
         |""".stripMargin
    }).mkString(" ")
    saveFile("README.md", readme.replaceAllLiterally("{{badges}}", badgeMessage))
  }

  private[this] def generateDatasetByStatus(datasetParser: DatasetParser, save: Boolean = true): Seq[CovidItem] = {
    val (confirmedItems, recoveredItems, deathItems) = datasetParser.parseDatasets()
    if (save) {
      saveFile("data/confirmed.json", objectMapper.writeValueAsString(confirmedItems))
      saveFile("data/recovered.json", objectMapper.writeValueAsString(recoveredItems))
      saveFile("data/deaths.json", objectMapper.writeValueAsString(deathItems))
    }
    confirmedItems ++ recoveredItems ++ deathItems
  }


  //aggregate them all into one
  def mergeAggregate(items: Seq[CovidAggregate]): CovidAggregate = {
    val first = items.head
    val template = CovidAggregate(null, first.country_region, first.lat, first.lon, Map.empty[String, Counter], true)

    def mergeCounts(m1: Map[String, Int], m2: Map[String, Int]): Map[String, Int] = {
      m2.foldLeft(m1)((acc, kv) => {
        acc.get(kv._1) match {
          case Some(count) => acc + (kv._1 -> (count + kv._2))
          case None => acc + (kv._1 -> kv._2)
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
  private[this] def mergeDatasetCountersByDateHierarchy(unionData: Seq[CovidItem]): Seq[CovidAggregate] = {
    val allCounters: Seq[CovidAggregate] = unionData.groupBy(cv => (cv.country_region, cv.province_state))
      .mapValues(itemsInCategory => {
        assert(itemsInCategory.nonEmpty)
        val first = itemsInCategory.head
        val counters = itemsInCategory.foldLeft(Map.empty[String, Counter])((ctr, cv) => {
          val dateCounts: Map[String, Map[String, Int]] = cv.timeline.map(kv => {
            ctr.get(kv._1) match {
              case Some(existing) => (kv._1, existing + (cv.status -> kv._2))
              case None => (kv._1, Map(cv.status -> kv._2))
            }
          })
          ctr ++ dateCounts
        })
        CovidAggregate(first.province_state, first.country_region, first.lat, first.lon, counters)
      }).values.toSeq


    val countryRolledUp: Map[String, CovidAggregate] = allCounters.groupBy(_.country_region).filter(_._2.size > 1).mapValues(mergeAggregate)
    val countryProvincesAgg = allCounters ++ countryRolledUp.values


    //totals should exclude province data
    val totals = createTotals(countryProvincesAgg.filter(_.isCountry))
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
    def saveCovidData(covAgg: CovidAggregate) = {
      val items = Seq(covAgg)
      val totals = createTotals(items)
      val output = Map("counts" -> totals, "items" -> items)
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
   * {counts: {}, items: []
   * Output Totals:
   * {counts: {}, date: ""}
   */
  private[this] def generateLatestAvailableDateStats(allCounters: Seq[CovidAggregate]): (Seq[CovidAggregate], Map[String, Int]) = {
    val anyAggregateInstance = allCounters.head
    val maxDate: LocalDate = findMaxDate(anyAggregateInstance)

    val dateKey = maxDate.format(DateTimeFormatter.ofPattern(R.outputDateFormatValue))

    val latestTotals = createTotals(allCounters.filter(_.isCountry))

    val latestCounters: Seq[CovidAggregate] = allCounters.map(covAgg => {
      val timeline: Map[String, Int] = covAgg.timeline.getOrElse(dateKey, Map.empty[String, Int])
      covAgg.copy(timeline = Map("counts" -> timeline))
    })
    val output = Map("counts" -> latestTotals, "date" -> maxDate.format(DateTimeFormatter.ISO_DATE), "items" -> latestCounters.filter(_.isCountry))
    saveFile("data/latest_counters.json", objectMapper.writeValueAsString(output))

    val outputWithProvince = Map("counts" -> latestTotals, "date" -> maxDate.format(DateTimeFormatter.ISO_DATE), "items" -> latestCounters)
    saveFile("data/latest_counters_with_province.json", objectMapper.writeValueAsString(outputWithProvince))


    val totals: Map[String, Int] = createTotals(allCounters.filter(_.isCountry), dateKey)
    val summary = Map("counts" -> totals, "date" -> maxDate.format(DateTimeFormatter.ISO_DATE))
    saveFile("data/totals.json", objectMapper.writeValueAsString(summary))
    (latestCounters, latestTotals)
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
        val totals = createTotals(tuple._2.filter(_.isCountry))
        val output = Map("counts" -> totals, "items" -> tuple._2)
        saveFile(s"data/by-date/$dateKey.json", objectMapper.writeValueAsString(output))
      })
  }

  /**
   * Generate all data files
   */
  def execute(): Unit = {

    val datasetParser = new DatasetParser()
    this.generatePopulationDataset(datasetParser)
    val unionData = this.generateDatasetByStatus(datasetParser)
    val badgeRequirements = this.getBadgeRequirements(datasetParser)
    datasetParser.cleanup()

    val mergedAggregate: Seq[CovidAggregate] = this.mergeDatasetCountersByDateHierarchy(unionData)
    this.generateDatasetByCountry(mergedAggregate)
    val latestCounters = this.generateLatestAvailableDateStats(mergedAggregate)
    this.generateDatasetByDate(mergedAggregate)
    this.generateBadges(badgeRequirements, latestCounters)
  }

}
