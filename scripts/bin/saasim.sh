#!/bin/bash

for lib in lib/*; do
	CLASSPATH="$CLASSPATH:$lib"
done

java -Xmx1024m -server -cp $CLASSPATH provisioning.Main $*

while [ -f ".je.dat" ] ; do
	java -Xmx1024m -server -cp $CLASSPATH provisioning.Main $*
done
