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

total=`ls ${new_dir}/*properties | wc -l`
n=$((${total}/${parallel_executions}))
nplus=$(($n+1))
rem=$((${total}%${parallel_executions}))

echo -e "#!/bin/bash\n" >> run-all.sh

for (( i=${nplus}; i <= ${rem}*${nplus}; i+=${nplus} )); do
	echo -e "#!/bin/bash\n" > run_${i}.sh
	for file in `ls ${new_dir}/*properties | head -n $i | tail -n ${nplus}`; do
                echo -e "bash ${exec_exp##*/} $i ${sim_zip##*/} ${file##*/}\n">> run_${i}.sh
        done
        echo -e "nohup bash run_${i}.sh &" >> run-all.sh

done

for (( i=1+(${rem}*${nplus}); i <= ${total}; i+=${n} )); do
        echo -e "#!/bin/bash\n" > run_${i}.sh
        for file in `ls ${new_dir}/*properties | head -n $i | tail -n ${n}`; do
                echo -e "bash ${exec_exp##*/} $i ${sim_zip##*/} ${file##*/}\n">> run_${i}.sh
        done
        echo -e "nohup bash run_${i}.sh &" >> run-all.sh

done

mv run*.sh ${new_dir}
