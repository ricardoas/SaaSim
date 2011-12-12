#!/bin/bash

rm saasim.log*

bash saasim.sh config-ranjan-1.properties
mv saasim.log ranjan.log.01
bash saasim.sh config-urgaonkar-1.properties
mv saasim.log urgaonkar.log.1
bash saasim.sh config-urgaonkar-20.properties
mv saasim.log urgaonkar.log.20
bash saasim.sh config-urgaonkar-21.properties
mv saasim.log urgaonkar.log.21
bash saasim.sh config-urgaonkar-22.properties
mv saasim.log urgaonkar.log.22
bash saasim.sh config-urgaonkar-23.properties
mv saasim.log urgaonkar.log.23
bash saasim.sh config-urgaonkar-24.properties
mv saasim.log urgaonkar.log.24
bash saasim.sh config-urgaonkar-30.properties
mv saasim.log urgaonkar.log.30
bash saasim.sh config-urgaonkar-31.properties
mv saasim.log urgaonkar.log.31
bash saasim.sh config-urgaonkar-32.properties
mv saasim.log urgaonkar.log.32
bash saasim.sh config-urgaonkar-33.properties
mv saasim.log urgaonkar.log.33
bash saasim.sh config-urgaonkar-34.properties
mv saasim.log urgaonkar.log.34

grep -H ACCOUNTING saasim.log* >> table.xls
echo "-------" >> table.xls
