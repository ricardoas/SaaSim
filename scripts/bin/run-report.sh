#!/bin/bash

n_exec=$1
report_file=$2

if [ -w $report_file ]; then
	rm $report_file
fi

for exec in `seq 1 ${n_exec}`; do	
	( time bash saasim.sh config.properties ) 2> tmp
	t=`cat tmp | tail -3 | head -1 | awk -F [^0-9] '{print 60000 * $6 + 1000 * $7 + $8}'`
	echo "${t}" >> ${report_file}
done

rm tmp
