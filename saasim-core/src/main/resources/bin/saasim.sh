#!/bin/bash

for lib in lib/*; do
	CLASSPATH="$CLASSPATH:$lib"
done

java -server -cp $CLASSPATH Main $*
