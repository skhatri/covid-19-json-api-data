package com.github.covid.dataset

import java.io.FileWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.spark.sql.{DataFrame, SparkSession}

case class CovidItem(_type: String, province_state: String, country_region: String, lat: Double, lon: Double, timeline: Map[String, Int])

case class CovidAggregate(province_state: String, country_region: String, lat: Double, lon: Double,
                          timeline: Map[String, Map[String, Int]]) {
  private val suffix = Option(this.province_state).map(ps => s"_$ps").getOrElse("")
  val country_province_key = s"${this.country_region}$suffix"
    .replace(' ', '_')
    .replaceAllLiterally("*", "")
}

object DatasetExtractor extends App {

  val outputDateFormatValue = "'d'_yyyy_MM_dd"
  val baseUrl = "https://raw.githubusercontent.com/skhatri/covid-19-csv-to-api-data/master/data"
  val sparkSession = SparkSession.builder()
    .appName("data-analyser")
    .master("local[2]")
    .getOrCreate()


  import org.apache.spark.sql.types._
  import sparkSession.implicits._

  /**
   * Loads csv file.
   * Gathers column names with / and converts dates into d_* and other slashes are replaced with _
   * The date attributes have numeric values and need to be converted to int.
   * columnMapping value holds the first element of the tuple as new column name to rename to
   * and the second element represents the select expression to be applied later
   *
   * @param name
   * @return
   */
  def loadDataset(name: String) = {
    val SingleSlash = """(.*)/(.*)""".r
    val DateLike = """([0-9]+)/([0-9]+)/([0-9]+)""".r
    val ds = sparkSession.read
      .option("header", "true")
      .csv(name)
      .withColumnRenamed("Long", "Lon")

    val columnsMapping = ds.columns.map(c => {
      val transformInstructions = c match {
        case DateLike(_, _, _) => {
          val inputFormat = DateTimeFormatter.ofPattern("M/d/yy")
          val outputFormat = DateTimeFormatter.ofPattern(outputDateFormatValue)
          val renameTo = LocalDate.parse(c, inputFormat)
            .format(outputFormat).toLowerCase
          (renameTo, s"int($renameTo) as $renameTo")
        }
        case SingleSlash(a, b) => {
          val newName = s"${a}_$b".toLowerCase
          (newName, newName)
        }
        case _ => {
          val renameTo = c.toLowerCase
          (renameTo, renameTo)
        }
      }
      (c, transformInstructions)
    }).toMap

    val columnsToRename = columnsMapping.map(kv => (kv._1, kv._2._1))

    val renamed = columnsToRename.foldLeft(ds)((cumulative, kv) => cumulative.withColumnRenamed(kv._1, kv._2))
    renamed.selectExpr(columnsMapping.values.map(_._2).toSeq: _*)
  }

  val confirmedDataset = loadDataset("dataset/global_confirmed.csv").selectExpr("'confirmed' as _type", "*")
  val deathDataset = loadDataset("dataset/global_deaths.csv").selectExpr("'deaths' as _type", "*")
  val recoveredDataset = loadDataset("dataset/global_recovered.csv").selectExpr("'recovered' as _type", "*")


  confirmedDataset.show(2, true)
  deathDataset.show(2, true)
  recoveredDataset.show(2, true)


  def saveFile(name: String, content: String) = {
    val fw = new FileWriter(name)
    fw.write(content)
    fw.flush()
    fw.close()
  }

  val objectMapper = new ObjectMapper()
  val scalaModule = new DefaultScalaModule()
  objectMapper.registerModule(scalaModule)

  val OutputDatePattern = """d_([0-9]{4}).+""".r

  def asCovidItems(dataframe: DataFrame): List[CovidItem] = {
    val cols = dataframe.columns
    dataframe.collect().foldLeft(List.empty[CovidItem])((list, row) => {
      val timeline = cols.foldLeft(Map.empty[String, Int])((tm, name) => name match {
        case OutputDatePattern(_) => tm + (name -> row.getAs[Int](name))
        case _ => tm
      })
      list :+ CovidItem(row.getAs[String]("_type"), row.getAs[String]("province_state"), row.getAs[String]("country_region"),
        row.getAs[String]("lat").toDouble, row.getAs[String]("lon").toDouble, timeline)
    })
  }

  //save three datasets separately as JSON values
  val confirmedItems = asCovidItems(confirmedDataset)
  val recoveredItems = asCovidItems(recoveredDataset)
  val deathItems = asCovidItems(deathDataset)

  //save each type separately
  saveFile("data/confirmed.json", objectMapper.writeValueAsString(confirmedItems))
  saveFile("data/recovered.json", objectMapper.writeValueAsString(recoveredItems))
  saveFile("data/death.json", objectMapper.writeValueAsString(deathItems))

  type Counter = Map[String, Int]

  //aggregate them all into one
  val aggregate = confirmedItems ++ recoveredItems ++ deathItems
  val allCounters: Seq[CovidAggregate] = aggregate.groupBy(cv => (cv.country_region, cv.province_state))
    .mapValues(itemsInCategory => {
      assert(itemsInCategory.nonEmpty)
      val first = itemsInCategory.head
      val counters = itemsInCategory.foldLeft(Map.empty[String, Counter])((ctr, cv) => {
        val dateCounts: Map[String, Map[String, Int]] = cv.timeline.map(kv => {
          ctr.get(kv._1) match {
            case Some(existing) => (kv._1, existing ++ Map(cv._type -> kv._2))
            case None => (kv._1, Map(cv._type -> kv._2))
          }
        })
        ctr ++ dateCounts
      })
      CovidAggregate(first.province_state, first.country_region, first.lat, first.lon, counters)
    }).values.toSeq
  saveFile("data/all_counters.json", objectMapper.writeValueAsString(allCounters))

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


  implicit val epochOrder: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

  //latest counts
  val latestDate = allCounters.head.timeline.keys.map(dateKeys => {
    LocalDate.parse(dateKeys, DateTimeFormatter.ofPattern(outputDateFormatValue))
  }).max.format(DateTimeFormatter.ofPattern(outputDateFormatValue))

  val latestCounters = allCounters.map(covAgg => {
    val timeline: Map[String, Int] = covAgg.timeline.getOrElse(latestDate, Map.empty[String, Int])
    covAgg.copy(timeline = Map(latestDate -> timeline))
  })
  saveFile("data/latest_counters.json", objectMapper.writeValueAsString(latestCounters))

  //store aggregate by date
  allCounters.flatMap(covAgg => {
    covAgg.timeline.map(dateKeyCount => {
      (dateKeyCount._1, covAgg.copy(timeline = Map(dateKeyCount._1 -> dateKeyCount._2)))
    })
  }).groupBy(_._1)
    .mapValues(_.map(_._2))
    .foreach(tuple => {
      val dateKey = LocalDate.parse(tuple._1, DateTimeFormatter.ofPattern(outputDateFormatValue))
          .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
      saveFile(s"data/by-date/$dateKey.json", objectMapper.writeValueAsString(tuple._2))
    })


}
