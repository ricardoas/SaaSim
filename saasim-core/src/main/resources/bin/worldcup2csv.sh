#!/bin/bash

folder=`dirname $0`
day=$1

tools_dir=$2

gzip -dc wc_day${day}_* | ${tools_dir}/bin/recreate ${tools_dir}/state/object_mappings.sort > ${day}.out

cat ${day}.out | awk -F " " '{print $1, $4, $9}' | sed 's/\[//g' | sed 's/\]//g' | sed 's/-/0/g'> tmp${day}

Rscript plot.R tmp${day}

Rscript ${folder}/worldcup2csv.R tmp${day} ${day}.out

rm tmp${day}
