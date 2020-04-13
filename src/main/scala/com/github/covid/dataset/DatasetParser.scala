package com.github.covid.dataset

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.apache.spark.sql.{DataFrame, SparkSession}

class DatasetParser {
  val OutputDatePattern = """d_([0-9]{4}).+""".r

  def asCovidItems(dataFrame: DataFrame): List[CovidItem] = {
    val cols = dataFrame.columns
    dataFrame.collect().foldLeft(List.empty[CovidItem])((list, row) => {
      val timeline = cols.foldLeft(Map.empty[String, Int])((tm, name) => name match {
        case OutputDatePattern(_) => tm + (name -> row.getAs[Int](name))
        case _ => tm
      })
      list :+ CovidItem(row.getAs[String]("status"), row.getAs[String]("province_state"), row.getAs[String]("country_region"),
        row.getAs[String]("lat").toDouble, row.getAs[String]("lon").toDouble, timeline)
    })
  }

  def parseDatasets(): (List[CovidItem], List[CovidItem], List[CovidItem]) = {
    val sparkSession = SparkSession.builder()
      .appName("data-analyser")
      .master("local[2]")
      .getOrCreate()

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
    def loadDataset(name: String):DataFrame = {
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
            val outputFormat = DateTimeFormatter.ofPattern(R.outputDateFormatValue)
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

    val confirmedDataset = loadDataset("dataset/global_confirmed.csv").selectExpr("'confirmed' as status", "*")
    val deathDataset = loadDataset("dataset/global_deaths.csv").selectExpr("'deaths' as status", "*")
    val recoveredDataset = loadDataset("dataset/global_recovered.csv").selectExpr("'recovered' as status", "*")

    confirmedDataset.show(2, true)
    deathDataset.show(2, true)
    recoveredDataset.show(2, true)
    //save three datasets separately as JSON values
    val confirmedItems = asCovidItems(confirmedDataset)
    val recoveredItems = asCovidItems(recoveredDataset)
    val deathItems = asCovidItems(deathDataset)
    sparkSession.close()
    (confirmedItems, recoveredItems, deathItems)
  }
}
