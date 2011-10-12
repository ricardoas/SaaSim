#!/bin/bash
# Esse script contem todo o fluxo de execucao do simulador

if [ $# -lt 2 ]; then
        echo "usage: $0 <number of saas clients> <simulation period in days>"
        exit 1
fi

NUM_USERS=$1
SIM_DAYS=$2

./gera_entrada.sh -d ${SIM_DAYS} -u ${NUM_USERS} -o users.properties -p 0

./executaPlanning.sh users.properties ${SIM_DAYS}
