#!/bin/bash
cwd=$(dirname $0)

global_deaths="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv"

global_confirmed="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv"
global_recovered="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv"


download() {
  local dataset=$1
  local name=$2
  echo downloading ${name}
  curl -L -s -o ${name} ${dataset}
}

dataset="${cwd}/dataset"
if [[ ! -d ${dataset} ]];
then
  mkdir -p ${dataset}
fi;
download ${global_confirmed} "${dataset}/global_confirmed.csv"
download ${global_recovered} "${dataset}/global_recovered.csv"
download ${global_deaths} "${dataset}/global_deaths.csv"

