#!/bin/bash

for lib in lib/*; do
	CLASSPATH="$CLASSPATH:$lib"
done

if [ -f ".je.dat" ]; then
	rm .je.dat
fi 

java -server -cp $CLASSPATH provisioning.Main $*

while [ -f ".je.dat" ] ; do
	java -server -cp $CLASSPATH provisioning.Main $*
done
