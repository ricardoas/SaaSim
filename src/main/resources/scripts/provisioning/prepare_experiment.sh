#!/bin/bash

sim_zip=$1
configs_folder=$2
users_folder=$3
new_dir=$4
exec_exp=$5
parallel_executions=$6

mkdir ${new_dir}
cp ${sim_zip} ${new_dir}
cp ${exec_exp} ${new_dir}/

for config in `ls ${configs_folder}`; do
	for user in `ls ${users_folder}`; do
		cat ${configs_folder}/${config} ${users_folder}/${user} > ${new_dir}/${user%.users}_${config}
	done
done

n=`ls ${new_dir}/*properties | wc -l`
n_per_file=$(($n/${parallel_executions}))

echo -e "#!/bin/bash\n" >> run-all.sh
for i in `seq ${n_per_file} ${n_per_file} $((${n_per_file}*${parallel_executions}))`; do
	echo -e "#!/bin/bash\n" > run_${i}.sh 
	for file in `ls ${new_dir}/*properties | head -n $i | tail -n ${n_per_file}`; do
		echo -e "bash ${exec_exp##*/} $i ${sim_zip##*/} ${file##*/}\n">> run_${i}.sh 
	done
	echo -e "nohup bash run_${i}.sh &" >> run-all.sh
done;

i=$((i+${n_per_file}))

for file in `ls ${new_dir}/*properties | tail -n $((n%${parallel_executions}))`; do
        echo -e "bash ${exec_exp##*/} $i ${sim_zip##*/} ${file##*/}\n">> run_${n_per_file}.sh
done

mv run*.sh ${new_dir}
