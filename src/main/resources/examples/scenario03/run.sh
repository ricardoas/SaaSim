#!/bin/bash

rm saasim.log*

bash saasim.sh config-static-small-1.properties
mv saasim.log saasim.log.01
bash saasim.sh config-static-small-2.properties
mv saasim.log saasim.log.02
bash saasim.sh config-static-small-3.properties
mv saasim.log saasim.log.03
bash saasim.sh config-static-small-4.properties
mv saasim.log saasim.log.04
bash saasim.sh config-static-large-1.properties
mv saasim.log saasim.log.05
bash saasim.sh config-static-large-2.properties
mv saasim.log saasim.log.06
bash saasim.sh config-static-xlarge.properties
mv saasim.log saasim.log.07
bash saasim.sh config-ranjan-MIN.properties
mv saasim.log saasim.log.08
bash saasim.sh config-ranjan-WO-SETUP-MIN.properties
mv saasim.log saasim.log.09
bash saasim.sh config-ranjan-HOUR.properties
mv saasim.log saasim.log.10
bash saasim.sh config-ranjan-WO-SETUP-HOUR.properties
mv saasim.log saasim.log.11

echo "-------" >> table.xls

grep -H ACCOUNTING saasim.log* >> table.xls
