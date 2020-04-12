# covid-19-csv-to-api-data
Scripts and Spark Tasks to Transform CSV to something easily consumable by a mobile/web client

### Source
```
https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv
https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv
https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv
```

### Generate Dataset
```
#1. Download Latest csv files
bash ./download.sh

#2. Run DatasetExtractor
```

### Api
The files may be returned as text/plain.

#### List of Countries/Provinces
```
curl https://raw.githubusercontent.com/skhatri/covid-19-csv-to-api-data/master/data/country.json
```
#### Confirmed, Recovered, Death Counters
```
https://raw.githubusercontent.com/skhatri/covid-19-csv-to-api-data/master/data/recovered.json
https://raw.githubusercontent.com/skhatri/covid-19-csv-to-api-data/master/data/confirmed.json
https://raw.githubusercontent.com/skhatri/covid-19-csv-to-api-data/master/data/death.json
```

#### Latest Available Dataset (1 day data)
```
https://raw.githubusercontent.com/skhatri/covid-19-csv-to-api-data/master/data/latest_counters.json
```

#### All available time series Dataset
```
https://raw.githubusercontent.com/skhatri/covid-19-csv-to-api-data/master/data/all_counters.json
```

#### View for a particular country/province
Use data returned by "List of Countries/Province" call as key (country_province_key) for this one
https://raw.githubusercontent.com/skhatri/covid-19-csv-to-api-data/master/data/by-country/{country_province_key}
```
https://raw.githubusercontent.com/skhatri/covid-19-csv-to-api-data/master/data/by-country/Australia_New_South_Wales.json
```
#### View for a particular date
```
https://raw.githubusercontent.com/skhatri/covid-19-csv-to-api-data/master/data/by-date/2020-04-11.json
```