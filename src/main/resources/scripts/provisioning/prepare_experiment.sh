#!/bin/bash

sim_zip=$1
configs_folder=$2
users_folder=$3
new_dir=$4

mkdir ${new_dir}
cp ${sim_zip} ${new_dir}
mkdir ${new_dir}/tmp 

for config in `ls ${configs_folder}`; do
	for user in `ls ${users_folder}`; do
		cat ${configs_folder}/${config} ${users_folder}/${user} > ${new_dir}/${config}_${user}
		cp ${new_dir}/${config}_${user} ${new_dir}/tmp 
	done
done

unzip ${new_dir}/${sim_zip} -d ${new_dir}/tmp
cp ${new_dir}/tmp/config_1.properties_saas_1.user ${new_dir}/tmp/saasim/ 
cd ${new_dir}/tmp/saasim/
bash saasim.sh config_1.properties_saas_1.user
mv saasim.log ${new_dir}
#rm tmp 
