#!/bin/bash

rm saasim.log*

bash saasim.sh config-ranjan-1.properties
mv saasim.log ranjan.log.01
bash saasim.sh config-ranjan-2.properties
mv saasim.log ranjan.log.02
bash saasim.sh config-ranjan-3.properties
mv saasim.log ranjan.log.03
grep -H ACCOUNTING ranjan.log* >> table.xls
echo "-------" >> table.xls

bash saasim.sh config-urgaonkar-1.properties
mv saasim.log urgaonkar.log.1
bash saasim.sh config-urgaonkar-20.properties
mv saasim.log urgaonkar.log.20
bash saasim.sh config-urgaonkar-24.properties
mv saasim.log urgaonkar.log.24
bash saasim.sh config-urgaonkar-30.properties
mv saasim.log urgaonkar.log.30
bash saasim.sh config-urgaonkar-34.properties
mv saasim.log urgaonkar.log.34

grep -H ACCOUNTING urgaonkar.log* >> table.xls
