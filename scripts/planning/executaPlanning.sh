#!/bin/bash
#Esse script é responsável por executar as várias heurísticas de planejamento para um cenário de X clientes SaaS durante um período Y de tempo.

if [ $# -lt 2 ]; then
        echo "usage: $0 <number of saas clients> <simulation period in days>"
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

USERS=$1
PERIOD=$2

for plan_heur in EVOLUTIONARY OVERPROVISIONING OPTIMAL; do
	#Replacing constants in files
	sed "s/#clients#/${USERS}/g" model.users > ${USERS}.users

	sed "s/#heur#/${plan_heur}/g" model.properties > ${USERS}_${PERIOD}_${plan_heur}.properties
	sed -i "s/#period#/${PERIOD}/g" ${USERS}_${PERIOD}_${plan_heur}.properties
	sed -i "s/#users#/${USERS}.users/g" ${USERS}_${PERIOD}_${plan_heur}.properties

	#Running planning
	java -Xmx1024m -server -cp $CLASSPATH planning.main.Main ${USERS}_${PERIOD}_${plan_heur}.properties
	mv output.plan model.plan	

	#Running normal simulation
	java -Xmx1024m -server -cp $CLASSPATH provisioning.Main ${USERS}_${PERIOD}_${plan_heur}.properties > ${USERS}_${PERIOD}_${plan_heur}.output

	mkdir result_${USERS}_${PERIOD}_${plan_heur}
	mv model.plan ${USERS}_${PERIOD}_${plan_heur}.output result_${USERS}_${PERIOD}_${plan_heur}
	touch model.plan
done
