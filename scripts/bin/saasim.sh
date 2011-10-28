#!/bin/bash

for lib in lib/*; do
	CLASSPATH="$CLASSPATH:$lib"
done

if [ -f ".je.dat" ]; then
	rm .je.dat
fi 

echo "dia: 1"
java -server -cp $CLASSPATH provisioning.Main $*

i=2

while [ -f ".je.dat" ] ; do
	echo "dia: $i"
	i=$(($i+1))
	java -server -cp $CLASSPATH provisioning.Main $*
done
