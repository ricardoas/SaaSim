#!/bin/bash

print_usage(){
	echo "usage $0 <uri> <trace_file_path>" >&2
}

if [ $# -lt 2 ]; then
    print_usage
    exit 1
fi

url=$1
input_file=$2
report_file=output_${input_file}
before=`head -1 ${input_file} | awk '{print $3}'`

while read line; do
	now=`echo $line | awk '{print $3}'`
	sleep `echo "scale=3; ($now - $before)/1000" | bc`
	before=$now
	nohup bash run-request.sh ${url}/`echo $line | awk '{print $6}'` ${report_file} &
done < ${input_file}
