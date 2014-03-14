#!/bin/bash

url=$1
report_file=$2

( time wget -O /dev/null ${url} 2>&1 | grep "200 OK" | wc -l >> ok ) 2>&1 | tail -3 | head -1 | awk -F [^0-9] '{print 60000 * $6 + 1000 * $7 + $8}' >> ${report_file}
