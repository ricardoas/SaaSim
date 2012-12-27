#!/bin/bash

output_dir=$1

sim_zip=$2

config=$3

mkdir -p ${output_dir}

number_of_diamond=$2
number_of_gold=$3
number_of_bronze=$4

total_number_of_users=$((${number_of_diamond}+${number_of_gold}+${number_of_bronze}))

for c in `ls ${config}`; do
	for user in `ls ${users_folder}`; do
		cat ${configs_folder}/${config} ${users_folder}/${user} > ${new_dir}/${user%.users}_${config}
	done
done





