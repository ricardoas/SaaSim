#!/bin/bash

tmp_dir=tmp_$1
sim=$2
config=$3

run_sim() {
	config=$1
	unzip ${sim} -d ${tmp_dir}
	cp ${config} ${tmp_dir}/saasim
	cd ${tmp_dir}/saasim/
	bash saasim.sh ${config}
	mv saasim.log ../../${config%.*}.log
	cd ../../
	rm -rf ${tmp_dir}
}

run_sim $config
