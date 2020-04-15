package com.github.covid.dataset


case class CovidItem(status: String, province_state: String, country_region: String, lat: Double, lon: Double, timeline: Map[String, Int])

case class CovidAggregate(province_state: String, country_region: String, lat: Double, lon: Double,
                          timeline: Map[String, Map[String, Int]], has_province: Boolean = false) {
  private val suffix = Option(this.province_state).map(ps => s"_$ps").getOrElse("")
  val country_province_key = s"${this.country_region}$suffix"
    .replace(' ', '_')
    .replaceAllLiterally("*", "")

  @transient
  val isProvince = Option(this.province_state).map(_.nonEmpty).getOrElse(false)
  @transient
  val isCountry = !isProvince
}

object R {
  val outputDateFormatValue = "'d'_yyyy_MM_dd"
}

case class CountryPopulation(country: String, population: Long)

case class CountryPopulationHistory(country: String, year: Int, population: Long, history: Map[Int, Long])

case class BadgeRequirement(key: String, display_name: String, link: String)

case class CovidBadgeData(key: String, display_name: String, confirmed: Int, recovered: Int, deaths: Int, link: String = "") {

  val resourceDataLink = Option(link).map(_.trim).filter(_.nonEmpty).getOrElse(CovidBadgeData.countryDataLink(key))

  def totalDeaths(): String =
    BadgePayload(display_name, s"deaths: ${CovidBadgeData.humanise(deaths)}", "critical")
      .badgeText(resourceDataLink)

  def totalConfirmed(): String = BadgePayload(display_name, s"confirmed: ${CovidBadgeData.humanise(confirmed)}", "yellow")
    .badgeText(resourceDataLink)

  def totalRecovered(): String = BadgePayload(display_name, s"recovered: ${CovidBadgeData.humanise(recovered)}", "green")
    .badgeText(resourceDataLink)

}

object CovidBadgeData {
  def humanise(num: Int): String = num match {
    case x: Int if x >= 1000000 => f"${x / 1000000.0}%.1fm"
    case x: Int if x >= 1000 => f"${x / 1000.0}%.1fk"
    case x => s"$x"
  }

  def countryDataLink(key: String) = s"https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/$key.json"

  def badgeUrlLink(key: String, suffix: String) = s"https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/badges/$key-$suffix.json"

}

case class BadgePayload(label: String, message: String, color: String) {
  val schemaVersion: Int = 1

  def badgeText(resourceLink: String): String =
    s"""[![$label](https://img.shields.io/static/v1?label=$label&message=${message.replaceAllLiterally(" ", "%20")}&color=$color)]($resourceLink)"""

  def badgeText(resourceLink: String, badgeDataLocation: String): String =
    s"""[![AU](https://img.shields.io/endpoint?url=$badgeDataLocation)]($resourceLink)"""
}
