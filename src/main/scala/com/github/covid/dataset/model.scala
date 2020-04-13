package com.github.covid.dataset

case class CovidItem(status: String, province_state: String, country_region: String, lat: Double, lon: Double, timeline: Map[String, Int])

case class CovidAggregate(province_state: String, country_region: String, lat: Double, lon: Double,
                          timeline: Map[String, Map[String, Int]]) {
  private val suffix = Option(this.province_state).map(ps => s"_$ps").getOrElse("")
  val country_province_key = s"${this.country_region}$suffix"
    .replace(' ', '_')
    .replaceAllLiterally("*", "")
}

object R {
  val outputDateFormatValue = "'d'_yyyy_MM_dd"
}