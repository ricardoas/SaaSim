#!/usr/bin/Rscript

args <- commandArgs(TRUE)

data <- read.table(args[1])

data[,c(7, 8, 9, 10, 11)] <- 0

#colnames(data) <- c("user", "id", "time", "reqsize", "respsize", "old", "web", "web.resp", "app", "app.resp", "db")

colnames(data) <- c("user", "id", "time", "reqsize", "respsize", "old", "web", "app", "db", "web.resp", "app.resp")

data$web <- round(rexp(n=nrow(data), rate=1/10))

data[data$old != 10,]$web.resp <- round(rexp(n=nrow(data[data$old != 10,]), rate=1))


data[data$old == 11,]$app <- round(rexp(n=nrow(data[data$old == 11,]), rate=1))


data[data$old == 570,]$app <- round(rexp(n=nrow(data[data$old == 570,]), rate=1/500))

data[data$old == 570,]$app.resp <- round(rexp(n=nrow(data[data$old == 570,]), rate=1))

data[data$old == 570,]$db <- round(rexp(n=nrow(data[data$old == 570,]), rate=1/60))


data[data$old == 170,]$app <- round(rexp(n=nrow(data[data$old == 170,]), rate=1/100))

data[data$old == 170,]$app.resp <- round(rexp(n=nrow(data[data$old == 170,]), rate=1))

data[data$old == 170,]$db <- round(rexp(n=nrow(data[data$old == 170,]), rate=1/60))


data[data$old == 50,]$app <- round(rexp(n=nrow(data[data$old == 50,]), rate=1/40))


data[data$old == 30,]$app <- round(rexp(n=nrow(data[data$old == 30,]), rate=1/20))

data$old <- NULL

write.table(data, file=args[1], append=F, col.names=F, row.names=F)





