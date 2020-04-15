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

case class BadgeRequirement(key: String, display_name: String)

case class BadgeData(key: String, display_name: String, confirmed: Int, recovered: Int, deaths: Int) {

  def totalDeaths(): BadgePayload = BadgePayload(display_name, s"deaths:%20${BadgeData.humanise(deaths)}", "critical")

  def totalConfirmed(): BadgePayload = BadgePayload(display_name, s"confirmed:%20${BadgeData.humanise(confirmed)}", "yellow")

  def totalRecovered(): BadgePayload = BadgePayload(display_name, s"recovered:%20${BadgeData.humanise(recovered)}", "green")
}

object BadgeData {
  def humanise(num: Int): String = num match {
    case x: Int if x >= 1000000 => f"${x / 1000000.0}%.1fm"
    case x: Int if x >= 1000 => f"${x / 1000.0}%.1fk"
    case x => s"$x"
  }
}


case class BadgePayload(label: String, message: String, color: String) {
  val schemaVersion: Int = 1
}
