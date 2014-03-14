#!/bin/bash

find_self() {
        SELF="`pwd`/`dirname $0`"
}

set_classpath() {
        LIB_DIR="$SELF/lib"
		for lib in ${LIB_DIR}/*; do
			CLASSPATH="$CLASSPATH:$lib"
		done
}

find_self && set_classpath

if [ -f ".je.dat" ]; then
	rm .je.dat
fi 

java -server -cp $CLASSPATH saasim.util.SimpleUserPropertiesGenerator $*
