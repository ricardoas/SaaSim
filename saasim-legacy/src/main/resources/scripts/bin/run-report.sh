#!/bin/bash

report_file="report_`date +%y_%m_%d_%H_%M`.txt"
config_file="config.properties"

print_usage(){
	echo "usage $0 -c <config_file> -o <outputfile> <number_of_rounds>" >&2
}

if [ $# -lt 1 ]; then
    print_usage
    exit 1
fi

while getopts c:o: o; do
	case $o in
		c)	config_file=$OPTARG;;
		o)	report_file=$OPTARG;;
		[?]) print_usage
			exit 1;;
	esac
done

shift `expr $OPTIND - 1`


n_exec=$1

if [ -w $report_file ]; then
	rm $report_file
fi

for exec in `seq 1 ${n_exec}`; do	
	( time bash saasim.sh ${config_file} ) 2> tmp
	t=`cat tmp | tail -3 | head -1 | awk -F [^0-9] '{print 60000 * $6 + 1000 * $7 + $8}'`
	echo "${t}" >> ${report_file}
	head -n-4 tmp >&2
done

rm tmp
