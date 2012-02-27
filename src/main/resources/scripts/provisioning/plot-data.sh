#!/bin/bash

for i in user_?.*log; do mv $i user_0${i#*_}; done

for i in *log; do grep -H UTILITY $i > ${i}.utility; done

for i in *log; do grep -H ACCOUNTING $i >> ${i}.accounting; done



a <- (read.table("user_01.properties_config-urgaonkar-2.log.accounting") +
read.table("user_01.properties_config-urgaonkar-2.log.accounting") +
read.table("user_01.properties_config-urgaonkar-2.log.accounting") +
read.table("user_01.properties_config-urgaonkar-2.log.accounting") +
read.table("user_01.properties_config-urgaonkar-2.log.accounting") +
read.table("user_01.properties_config-urgaonkar-2.log.accounting") ) /6

write.table(a, file=args[1])

for i in `seq 0 6 36`; do

	for j in `seq ${i} $((${i}+5))`; do
		grep -H UTILITY user_${i}.*urgaonkar-1.log > 
	done

done
