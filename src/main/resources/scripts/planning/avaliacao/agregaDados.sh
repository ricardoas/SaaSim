#!/bin/bash
# Este script agrupa os profits obtidos em cada uma das 70 execucoes dos varios cenarios em um arquivo unico por cenario

for heur in ut op on ov; do

	for risco in 5 10 15; do

		rm ${heur}_100_${risco}/profits.dat
		for index in `seq 1 70`; do
			head -n 2 ${heur}_100_${risco}/data_${index}.output | tail -n 1 >> ${heur}_100_${risco}/profits.dat
		done

	done
done

for heur in ut op on ov; do

	for risco in 5 10 15; do
		
		rm ${heur}_50_${risco}/profits.dat
		for index in `seq 100 170`; do
			head -n 2 ${heur}_50_${risco}/data_${index}.output | tail -n 1 >> ${heur}_50_${risco}/profits.dat
		done

	done
done

for heur in ut op on ov; do

	for risco in 5 10 15; do

		rm ${heur}_10_${risco}/profits.dat
		for index in `seq 200 270`; do
			head -n 2 ${heur}_10_${risco}/data_${index}.output | tail -n 1 >> ${heur}_10_${risco}/profits.dat
		done

	done
done
