#!/bin/bash

: "${TRAVIS_BUILD_NUMBER=0}"
: "${TRAVIS_EVENT_TYPE=trigger}"
: "${GITHUB_TOKEN='invalid'}"

git status
now=$(date "+%Y-%m-%dT%H:%M:%S%z")
changes=$(git status -s|wc -l)
echo "Time: ${now}, changes: ${changes}"
if [[ $changes -ne 0 ]];
then
  email=$(git config user.email)

  ec=$(git config user.email|wc -l)
  if [[ $ec -eq 0 ]];
  then
      git config --global user.email "travis@travis-ci.com"
  fi;

  nc=$(git config user.name|wc -l)
  if [[ $nc -eq 0 ]];
  then
    git config --global user.name "Travis CI"
  fi;

  git add data
  git add dataset
  git commit -m"dataset update run at ${now} build: #${TRAVIS_BUILD_NUMBER}, trigger_type: ${TRAVIS_EVENT_TYPE}"
  git remote add gh https://"${GITHUB_TOKEN}"@github.com/skhatri/covid-19-csv-to-api-data.git/
  tag_msg='{"time": "'"${now}"'", "build_number": '"${TRAVIS_BUILD_NUMBER}"', "trigger_type": "'"${TRAVIS_EVENT_TYPE}"'", "message": "dataset update"}'
  git tag -a -m"${tag_msg}" build/"${TRAVIS_BUILD_NUMBER}"
  git push gh master --tags
fi

#debug:
#git tag -d build/0 && git remote remove gh && git reset --soft HEAD~1 && sh upload.sh && git log -1 && git tag -n
