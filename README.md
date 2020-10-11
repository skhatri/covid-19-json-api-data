[![Build](https://travis-ci.com/skhatri/covid-19-json-api-data.svg?branch=master)](https://travis-ci.com/github/skhatri/covid-19-json-api-data)

# covid-19-json-api-data
COVID-19 Dataset with Api Endpoints. Spark/Scala Transformation Tasks to convert to JSON Api payloads for mobile/web client.

### Quicklinks


[![World](https://img.shields.io/static/v1?label=World&message=confirmed:%2036.9m&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/totals.json) [![World](https://img.shields.io/static/v1?label=World&message=recovered:%2025.7m&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/totals.json) [![World](https://img.shields.io/static/v1?label=World&message=deaths:%201.1m&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/totals.json)
 
[![AU](https://img.shields.io/static/v1?label=AU&message=confirmed:%2027.3k&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Australia.json) [![AU](https://img.shields.io/static/v1?label=AU&message=recovered:%2025.0k&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Australia.json) [![AU](https://img.shields.io/static/v1?label=AU&message=deaths:%20898&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Australia.json)
 
[![NSW](https://img.shields.io/static/v1?label=NSW&message=confirmed:%204.3k&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Australia_New_South_Wales.json) [![NSW](https://img.shields.io/static/v1?label=NSW&message=recovered:%203.1k&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Australia_New_South_Wales.json) [![NSW](https://img.shields.io/static/v1?label=NSW&message=deaths:%2053&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Australia_New_South_Wales.json)
 
[![USA](https://img.shields.io/static/v1?label=USA&message=confirmed:%207.7m&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/US.json) [![USA](https://img.shields.io/static/v1?label=USA&message=recovered:%203.1m&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/US.json) [![USA](https://img.shields.io/static/v1?label=USA&message=deaths:%20214.4k&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/US.json)
 
[![Italy](https://img.shields.io/static/v1?label=Italy&message=confirmed:%20349.5k&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Italy.json) [![Italy](https://img.shields.io/static/v1?label=Italy&message=recovered:%20238.5k&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Italy.json) [![Italy](https://img.shields.io/static/v1?label=Italy&message=deaths:%2036.1k&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Italy.json)
 
[![Spain](https://img.shields.io/static/v1?label=Spain&message=confirmed:%20861.1k&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Spain.json) [![Spain](https://img.shields.io/static/v1?label=Spain&message=recovered:%20150.4k&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Spain.json) [![Spain](https://img.shields.io/static/v1?label=Spain&message=deaths:%2032.9k&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Spain.json)
 
[![Nepal](https://img.shields.io/static/v1?label=Nepal&message=confirmed:%20105.7k&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Nepal.json) [![Nepal](https://img.shields.io/static/v1?label=Nepal&message=recovered:%2074.3k&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Nepal.json) [![Nepal](https://img.shields.io/static/v1?label=Nepal&message=deaths:%20614&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Nepal.json)
 
[![Taiwan](https://img.shields.io/static/v1?label=Taiwan&message=confirmed:%20527&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Taiwan.json) [![Taiwan](https://img.shields.io/static/v1?label=Taiwan&message=recovered:%20488&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Taiwan.json) [![Taiwan](https://img.shields.io/static/v1?label=Taiwan&message=deaths:%207&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Taiwan.json)
 
[![Singapore](https://img.shields.io/static/v1?label=Singapore&message=confirmed:%2057.9k&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Singapore.json) [![Singapore](https://img.shields.io/static/v1?label=Singapore&message=recovered:%2057.7k&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Singapore.json) [![Singapore](https://img.shields.io/static/v1?label=Singapore&message=deaths:%2027&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Singapore.json)
 
[![India](https://img.shields.io/static/v1?label=India&message=confirmed:%207.0m&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/India.json) [![India](https://img.shields.io/static/v1?label=India&message=recovered:%206.0m&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/India.json) [![India](https://img.shields.io/static/v1?label=India&message=deaths:%20107.4k&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/India.json)


### Source
https://github.com/CSSEGISandData/COVID-19
```
https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv
https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv
https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv
```

### Generate Dataset
```
#1. Download Latest csv files
bash ./download.sh

#2. Run DatasetExtractor or 
./gradlew extract 
```

### Api
The files may be returned as text/plain. Using a prefix of https://raw.githack.com/ can be one way around it so all files are served with content-type of "application/json"


#### World Totals
Running Total of the cases by status

https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/totals.json


#### List of Countries/Provinces
This can be used as menu item for retrieving dataset by country

https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/country.json

#### Confirmed, Recovered, Death Counters

- Confirmed - https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/confirmed.json
- Recovered - https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/recovered.json
- Death - https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/death.json

#### Data for the last day
Api to find the current standings
https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/latest_counters.json
Also includes Province (for Australia, Canada, China)
https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/latest_counters_with_province.json

#### All available time series Dataset

https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/all_counters.json

#### View for a particular country/province
Use data returned by "List of Countries/Province" call as key (country_province_key) for this one
```https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/{country_province_key}```

https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Australia_New_South_Wales.json
https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Australia.json
https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/US.json
https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Italy.json
https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Spain.json
https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/United_Kingdom.json

#### View for a particular date
Format is ```https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-date/{date}.json```

https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-date/2020-04-11.json

#### Population
Population Data sourced from https://data.worldbank.org/indicator/SP.POP.TOTL?most_recent_value_desc=false

Latest Population of each country
https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/population/population.json

Population History by Country
https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/population/history.json


