#!/bin/bash

lines=`wc -l saas.log | awk '{print $1}'`

result=`R --slave --vanilla <<EOF
	file="saas.log"
	linhas=${lines}
	data <- read.table(file, header= FALSE, nrows=5, sep=" ")
	classes <- sapply(data, class)
	data <- read.table(file, header= FALSE, nrows=linhas, colClasses=classes, comment.char="", sep=" ")
	cat(quantile(data[,2], .95))
EOF`

echo $result
