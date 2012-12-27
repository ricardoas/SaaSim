#!/bin/bash


for index in `seq 1 70` ; do

	br=`head -n 4 op_100_10/data_${index}.output | tail -n 1 | grep -o bronze | wc -l`
	go=`head -n 4 op_100_10/data_${index}.output | tail -n 1 | grep -o gold | wc -l`
	di=`head -n 4 op_100_10/data_${index}.output | tail -n 1 | grep -o diamond | wc -l`

	di_p=`echo "scale=5; ${di} / (${di} + ${go} + ${br})" | bc`
	br_p=`echo "scale=5; ${br} / (${di} + ${go} + ${br})" | bc`
	go_p=`echo "scale=5; ${go} / (${di} + ${go} + ${br})" | bc`

	echo "DI: ${di_p} BR: ${br_p} GO: ${go_p}"

done

echo
echo ">>>>>>>>>> 50 usuarios"
echo

for index in `seq 100 170` ; do

	br=`head -n 4 op_50_10/data_${index}.output | tail -n 1 | grep -o bronze | wc -l`
	go=`head -n 4 op_50_10/data_${index}.output | tail -n 1 | grep -o gold | wc -l`
	di=`head -n 4 op_50_10/data_${index}.output | tail -n 1 | grep -o diamond | wc -l`

	di_p=`echo "scale=5; ${di} / (${di} + ${go} + ${br})" | bc`
	br_p=`echo "scale=5; ${br} / (${di} + ${go} + ${br})" | bc`
	go_p=`echo "scale=5; ${go} / (${di} + ${go} + ${br})" | bc`

	echo "DI: ${di_p} BR: ${br_p} GO: ${go_p}"

done

echo
echo ">>>>>>>>>> 10 usuarios"
echo

for index in `seq 200 270` ; do

	br=`head -n 4 op_10_10/data_${index}.output | tail -n 1 | grep -o bronze | wc -l`
	go=`head -n 4 op_10_10/data_${index}.output | tail -n 1 | grep -o gold | wc -l`
	di=`head -n 4 op_10_10/data_${index}.output | tail -n 1 | grep -o diamond | wc -l`

	di_p=`echo "scale=5; ${di} / (${di} + ${go} + ${br})" | bc`
	br_p=`echo "scale=5; ${br} / (${di} + ${go} + ${br})" | bc`
	go_p=`echo "scale=5; ${go} / (${di} + ${go} + ${br})" | bc`

	echo "DI: ${di_p} BR: ${br_p} GO: ${go_p}"

done
