[![Build](https://travis-ci.com/skhatri/covid-19-json-api-data.svg?branch=master)](https://travis-ci.com/github/skhatri/covid-19-json-api-data)

# covid-19-json-api-data
COVID-19 Dataset with Api Endpoints. Spark/Scala Transformation Tasks to convert to JSON Api payloads for mobile/web client.

### Quicklinks


[![World](https://img.shields.io/static/v1?label=World&message=confirmed:%2058.8m&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/totals.json) [![World](https://img.shields.io/static/v1?label=World&message=recovered:%2037.9m&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/totals.json) [![World](https://img.shields.io/static/v1?label=World&message=deaths:%201.4m&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/totals.json)
 
[![AU](https://img.shields.io/static/v1?label=AU&message=confirmed:%2027.8k&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Australia.json) [![AU](https://img.shields.io/static/v1?label=AU&message=recovered:%2025.5k&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Australia.json) [![AU](https://img.shields.io/static/v1?label=AU&message=deaths:%20907&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Australia.json)
 
[![NSW](https://img.shields.io/static/v1?label=NSW&message=confirmed:%204.5k&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Australia_New_South_Wales.json) [![NSW](https://img.shields.io/static/v1?label=NSW&message=recovered:%203.2k&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Australia_New_South_Wales.json) [![NSW](https://img.shields.io/static/v1?label=NSW&message=deaths:%2053&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Australia_New_South_Wales.json)
 
[![USA](https://img.shields.io/static/v1?label=USA&message=confirmed:%2012.4m&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/US.json) [![USA](https://img.shields.io/static/v1?label=USA&message=recovered:%204.6m&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/US.json) [![USA](https://img.shields.io/static/v1?label=USA&message=deaths:%20257.8k&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/US.json)
 
[![Italy](https://img.shields.io/static/v1?label=Italy&message=confirmed:%201.4m&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Italy.json) [![Italy](https://img.shields.io/static/v1?label=Italy&message=recovered:%20584.5k&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Italy.json) [![Italy](https://img.shields.io/static/v1?label=Italy&message=deaths:%2050.5k&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Italy.json)
 
[![Spain](https://img.shields.io/static/v1?label=Spain&message=confirmed:%201.6m&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Spain.json) [![Spain](https://img.shields.io/static/v1?label=Spain&message=recovered:%20150.4k&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Spain.json) [![Spain](https://img.shields.io/static/v1?label=Spain&message=deaths:%2043.1k&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Spain.json)
 
[![Nepal](https://img.shields.io/static/v1?label=Nepal&message=confirmed:%20222.3k&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Nepal.json) [![Nepal](https://img.shields.io/static/v1?label=Nepal&message=recovered:%20202.1k&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Nepal.json) [![Nepal](https://img.shields.io/static/v1?label=Nepal&message=deaths:%201.3k&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Nepal.json)
 
[![Taiwan](https://img.shields.io/static/v1?label=Taiwan&message=confirmed:%20618&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Taiwan.json) [![Taiwan](https://img.shields.io/static/v1?label=Taiwan&message=recovered:%20549&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Taiwan.json) [![Taiwan](https://img.shields.io/static/v1?label=Taiwan&message=deaths:%207&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Taiwan.json)
 
[![Singapore](https://img.shields.io/static/v1?label=Singapore&message=confirmed:%2058.2k&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Singapore.json) [![Singapore](https://img.shields.io/static/v1?label=Singapore&message=recovered:%2058.1k&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Singapore.json) [![Singapore](https://img.shields.io/static/v1?label=Singapore&message=deaths:%2028&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/Singapore.json)
 
[![India](https://img.shields.io/static/v1?label=India&message=confirmed:%209.2m&color=yellow)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/India.json) [![India](https://img.shields.io/static/v1?label=India&message=recovered:%208.6m&color=green)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/India.json) [![India](https://img.shields.io/static/v1?label=India&message=deaths:%20134.2k&color=critical)](https://raw.githubusercontent.com/skhatri/covid-19-json-api-data/master/data/by-country/India.json)


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


