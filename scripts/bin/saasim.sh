#!/bin/bash

for lib in lib/*; do
	CLASSPATH="$CLASSPATH:$lib"
done

java -cp $CLASSPATH provisioning.Main $*
 
