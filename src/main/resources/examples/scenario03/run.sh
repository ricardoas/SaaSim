#!/bin/bash

rm saasim.log*

bash saasim.sh config-ranjan-4.properties
mv saasim.log saasim.log.07
bash saasim.sh config-urgaonkar-4.properties
mv saasim.log saasim.log.08
bash saasim.sh config-ranjan-1.properties
mv saasim.log saasim.log.01
bash saasim.sh config-ranjan-2.properties
mv saasim.log saasim.log.02
bash saasim.sh config-ranjan-3.properties
mv saasim.log saasim.log.03
bash saasim.sh config-urgaonkar-1.properties
mv saasim.log saasim.log.04
bash saasim.sh config-urgaonkar-2.properties
mv saasim.log saasim.log.05
bash saasim.sh config-urgaonkar-3.properties
mv saasim.log saasim.log.06

grep -H ACCOUNTING saasim.log* >> table.xls
echo "-------" >> table.xls
