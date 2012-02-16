#!/bin/bash
# Este script coleta os dados referentes ao total de horas consumidas na simulacao, bem como a quantidade de recursos reservados em cada cenario

set_classpath() {
        LIB_DIR="lib"
        CLASSPATH=""
        for lib in $LIB_DIR/*; do
                CLASSPATH="$CLASSPATH:$lib"
        done
}

set_classpath
#CLASSPATH="./saasim-0.0.2-SNAPSHOT.jar"

#Consumptions
for heur in ut op on ov; do

	for risco in 5 10 15; do
		rm ${heur}_100_${risco}/consumption.dat

		for index in `seq 1 70`; do
			tail -n 12 ${heur}_100_${risco}/data_${index}.output >> ${heur}_100_${risco}/consumption.dat
		done

	done
done

for heur in ut op on ov; do

	for risco in 5 10 15; do
		rm ${heur}_50_${risco}/consumption.dat

		for index in `seq 100 170`; do
			tail -n 12 ${heur}_50_${risco}/data_${index}.output >> ${heur}_50_${risco}/consumption.dat
		done

	done
done

for heur in ut op on ov; do

	for risco in 5 10 15; do
		rm ${heur}_10_${risco}/consumption.dat

		for index in `seq 200 270`; do
			tail -n 12 ${heur}_10_${risco}/data_${index}.output >> ${heur}_10_${risco}/consumption.dat
		done

	done
done

#Plans
for heur in ut op on ov; do

	for risco in 5 10 15; do
		rm ${heur}_100_${risco}/plans.dat
		for index in `seq 1 70`; do
			a=`java -cp ${CLASSPATH} commons.util.ParsePlan ${heur}_100_${risco}/model_${index}.plan | tail -n 1`
			echo ${a} >> ${heur}_100_${risco}/plans.dat
		done

	done
done

for heur in ut op on ov; do

	for risco in 5 10 15; do
		rm ${heur}_50_${risco}/plans.dat

		for index in `seq 100 170`; do
			java -cp ${CLASSPATH} commons.util.ParsePlan ${heur}_50_${risco}/model_${index}.plan | tail -n 1 >> ${heur}_50_${risco}/plans.dat
		done

	done
done

for heur in ut op on ov; do

	for risco in 5 10 15; do
		rm ${heur}_10_${risco}/plans.dat

		for index in `seq 200 270`; do
			java -cp ${CLASSPATH} commons.util.ParsePlan ${heur}_10_${risco}/model_${index}.plan | tail -n 1 >> ${heur}_10_${risco}/plans.dat
		done

	done
done
