#!/bin/bash

for user in 5 100; do

	for risco in 1 15; do
	
		for erro in m40 m15 15 40; do
			cat rf_${user}_${risco}_${erro}/result.dat | awk '{print $1}' > tmp1.dat
			cat ut_${user}_${risco}_${erro}/result.dat | awk '{print $1}' > tmp2.dat
			paste tmp1.dat tmp2.dat > tmp.dat			
			echo "Cenario ${user} ${risco}% ${erro}" >> ganhos.dat
			cat tmp.dat >> ganhos.dat
		done

	done

done
