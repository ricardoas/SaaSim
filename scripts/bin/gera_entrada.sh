#!/bin/bash
# Este script é responsável por requisitar a geração do trace a ser utilizado na simulação com base em pools de traces existentes.

if [ $# -lt 6 ]; then
        echo "usage: $0 -u <number of saas clients> -d <simulation period in days> -o <output file> -p <peak days>"
        exit 1
fi

NUM_USERS=""
SIM_PERIOD=""
OUTPUT=""
PEAK_DAYS=""

while [ "$1" != "" ]; do
    case $1 in
        -u )           		shift
                                NUM_USERS=$1
                                ;;
	-d )    		shift
				SIM_PERIOD=$1
                                ;;
	-o )    		shift
				OUTPUT=$1
                                ;;
	-p )    		shift
				PEAK_DAYS=$1
                                ;;
    esac
    shift
done

if [ "${NUM_USERS}" == "" ]; then
	echo "Invalid number of users"
	exit 1
fi

if [ "${SIM_PERIOD}" == "" ]; then
	echo "Invalid number of days to simulate"
	exit 1
fi

if [ "${OUTPUT}" == "" ]; then
	echo "Invalid output file"
	exit 1
fi


find_self() {
        SELF=`dirname $0`
}

set_classpath() {
        LIB_DIR="$SELF/lib"
        CLASSPATH="$SELF/bin"
        for lib in $LIB_DIR/*; do
                CLASSPATH="$CLASSPATH:$lib"
        done
}

find_self && set_classpath


NORM_POOL="/home/david/workspace/SaaSim/norm"
PEAK_POOL="/home/david/workspace/SaaSim/peak"
TRANS_POOL="/home/david/workspace/SaaSim/trans"

java -server -cp $CLASSPATH commons.util.UserPropertiesGenerator ${NUM_USERS} ${SIM_PERIOD} ${OUTPUT} ${NORM_POOL} ${TRANS_POOL} ${PEAK_POOL} ${PEAK_DAYS}






