#!/bin/bash

if [ $# -lt 3 ]; then
        echo "usage: $0 -u=<number of saas clients> -d=<simulation period in days> -o=<output file> -p=<peak days>"
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

java -server -cp $CLASSPATH commons.util.UserPropertiesGenerator $1 $2 $3 ${NORM_POOL} ${TRANS_POOL} ${PEAK_POOL} $4






