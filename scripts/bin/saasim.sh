#!/bin/bash

for lib in lib/*; do
	CLASSPATH="$CLASSPATH:$lib"
done

echo "dia: 1"
java -Xmx1024m -server -cp $CLASSPATH provisioning.Main $*

i=1
while [ -f ".je.dat" ] ; do
	echo "dia: $i"
	i=$(($i+1))
	java -Xmx1024m -server -cp $CLASSPATH provisioning.Main $*
done
