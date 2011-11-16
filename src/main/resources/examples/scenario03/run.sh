#!/bin/bash

rm saasim.log*

bash saasim.sh config-ranjan-MIN.properties
mv saasim.log saasim.log.01
bash saasim.sh config-ranjan-WO-SETUP-MIN.properties
mv saasim.log saasim.log.02
bash saasim.sh config-ranjan-HOUR.properties
mv saasim.log saasim.log.03
bash saasim.sh config-ranjan-WO-SETUP-HOUR.properties
mv saasim.log saasim.log.04
bash saasim.sh config-urgaonkar.properties
mv saasim.log saasim.log.04

echo "-------" >> table.xls

grep -H ACCOUNTING saasim.log* >> table.xls
