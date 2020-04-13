#!/bin/bash

pwd
ls -lhtr
changes=$(git status -s|wc -l)
if [[ $changes -ne 0 ]];
then
  git config --global user.email "travis@travis-ci.com"
  git config --global user.name "Travis CI"

  git add data
  git add dataset
  now=$(date "+%Y-%m-%d %H:%M:%S%z")
  git commit -m"dataset update run at ${now} build: #${TRAVIS_BUILD_NUMBER}"
  git push origin master
fi
