#!/bin/bash
# Esse script contem todo o fluxo de execucao do simulador

if [ $# -lt 6 ]; then
	echo "usage: $0 -u <number of saas clients> -d <simulation period in days> -p <peak days>"
        exit 1
fi

NUM_USERS=""
SIM_PERIOD=""
PEAK_DAYS=""

while [ "$1" != "" ]; do
    case $1 in
        -u )           		shift
                                NUM_USERS=$1
                                ;;
	-d )    		shift
				SIM_PERIOD=$1
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


./gera_entrada.sh -d ${SIM_PERIOD} -u ${NUM_USERS} -o users.properties -p ${PEAK_DAYS}

./executaPlanning.sh ${NUM_USERS} ${SIM_PERIOD}
