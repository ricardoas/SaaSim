#!/usr/bin/Rscript

args <- commandArgs(TRUE)

file=args[1]

trace <- read.table(file, header= FALSE, nrows=5)
classes <- sapply(trace, class)
trace <- read.table(file, header= FALSE, colClasses=classes, comment.char="")

trace[,1] <- as.numeric(floor(1000 * (trace[,2] - min(trace[,2]))))

trace[,4] <- 10000

trace[,5] <- apply(trace, 1, function(x) cpu())

trace <- trace[,c(1, 2, 4, 3, 5)]

trace[,c(6:10)] <- 0

colnames(trace) <- c("user", "time", "reqsize", "respsize", "old", "web", "app", "db", "web.resp", "app.resp")

trace$web <- round(rexp(n=nrow(trace), rate=1/10))
trace[trace$old != 10,]$web.resp <- round(rexp(n=nrow(trace[trace$old != 10,]), rate=1))
trace[trace$old == 11,]$app <- round(rexp(n=nrow(trace[trace$old == 11,]), rate=1))
trace[trace$old == 570,]$app <- round(rexp(n=nrow(trace[trace$old == 570,]), rate=1/500))
trace[trace$old == 570,]$app.resp <- round(rexp(n=nrow(trace[trace$old == 570,]), rate=1))
trace[trace$old == 570,]$db <- round(rexp(n=nrow(trace[trace$old == 570,]), rate=1/60))
trace[trace$old == 170,]$app <- round(rexp(n=nrow(trace[trace$old == 170,]), rate=1/100))
trace[trace$old == 170,]$app.resp <- round(rexp(n=nrow(trace[trace$old == 170,]), rate=1))
trace[trace$old == 170,]$db <- round(rexp(n=nrow(trace[trace$old == 170,]), rate=1/60))
trace[trace$old == 50,]$app <- round(rexp(n=nrow(trace[trace$old == 50,]), rate=1/40))
trace[trace$old == 30,]$app <- round(rexp(n=nrow(trace[trace$old == 30,]), rate=1/20))
trace$old <- NULL

trace <- format(trace[trace$time > 600000,], scientific=FALSE, trim=TRUE)

write.csv(trace, file=args[2], row.names=FALSE, quote=FALSE)
