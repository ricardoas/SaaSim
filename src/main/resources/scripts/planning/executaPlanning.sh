#!/bin/bash
#Esse script é responsável por executar as várias heurísticas de planejamento para um cenário de X clientes SaaS durante um período Y de tempo.

#if [ $# -lt 2 ]; then
#        echo "usage: $0 <saas users configuration file> <simulation period in days>"
#        exit 1
#fi

if [ $# -lt 2 ]; then
        echo "usage: $0 <scenarios dir> <planning heuristic: Optimal, Over, History or On-demand>"
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

#USERS=$1
#PERIOD=$2

#for plan_heur in EVOLUTIONARY OVERPROVISIONING OPTIMAL; do
#
#	sed "s/#heur#/${plan_heur}/g" model.properties > ${USERS}_${PERIOD}_${plan_heur}.properties
#	sed -i "s/#period#/${PERIOD}/g" ${USERS}_${PERIOD}_${plan_heur}.properties
#	sed -i "s/#users#/users.properties/g" ${USERS}_${PERIOD}_${plan_heur}.properties
#
#	if [ ${plan_heur} == EVOLUTIONARY ] || [ ${plan_heur} == OPTIMAL ]; then
#		java -server -cp $CLASSPATH commons.util.AggregateWorkload ${USERS}_${PERIOD}_${plan_heur}.properties
#		sed -i "s/users.properties/newUsers.properties/g" ${USERS}_${PERIOD}_${plan_heur}.properties
#	fi 
#
#	#Running planning
#	java -Xmx1024m -server -cp $CLASSPATH planning.main.Main ${USERS}_${PERIOD}_${plan_heur}.properties
#	mv output.plan model.plan	
#
#	#Running normal simulation
#	java -Xmx1024m -server -cp $CLASSPATH provisioning.Main ${USERS}_${PERIOD}_${plan_heur}.properties > ${USERS}_${PERIOD}_${plan_heur}.output
#
#	mkdir result_${USERS}_${PERIOD}_${plan_heur}
#	mv model.plan ${USERS}_${PERIOD}_${plan_heur}.output result_${USERS}_${PERIOD}_${plan_heur}
#	touch model.plan
#done

dir=$1
plan_heur=$2

mkdir result_${plan_heur}

for value in `seq 1`; do

	cp ${dir}/scenario_${value}/users.properties .
	cp ${dir}/scenario_${value}/*.trc .
	rm .je.dat
	
	if [ ${plan_heur} = "Optimal" ] ; then
		java -Xmx2024m -server -cp $CLASSPATH commons.util.AggregateWorkload david.properties
		cp david.properties ${plan_heur}.properties
		sed -i "s/users.properties/newUsers.properties/g" ${plan_heur}.properties
		
		java -Xmx2024m -server -cp $CLASSPATH planning.main.Main ${plan_heur}.properties
		mv output.plan model.plan
		echo "Aggregated and planned"
	elif [ ${plan_heur} = "Over" ] || [ ${plan_heur} = "History" ] ; then
		
		#Running planning
		for i in `seq 1 365`; do
			java -Xmx2024m -server -cp $CLASSPATH planning.main.Main david.properties
		done
		rm .je.dat
		mv output.plan model.plan
		echo "Planned"
	fi


	#Running normal simulation
	for i in `seq 1 365`; do
		java -Xmx2024m -server -cp $CLASSPATH provisioning.Main david.properties > data_${value}.output
	done

	mv model.plan model_${value}.plan
	mv model_${value}.plan data_${value}.output result_${plan_heur}
	touch model.plan	

done
