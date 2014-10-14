#!/usr/bin/Rscript

args <- commandArgs(TRUE)

file=args[1]

trace <- read.table(file, header= FALSE, nrows=5)
classes <- sapply(trace, class)
trace <- read.table(file, header= FALSE, colClasses=classes, comment.char="")

p1=.187
p2=.405 + p1
p3=.174 + p2
p4=.0808 + p3
p5=.102 + p4

cpu <- function(){
x = runif(1, 0, 1)
if(x <= p1){
10
}else if( x <= p2 ){
11
}else if( x <= p3 ){
570
}else if( x <= p4){
170
}else if( x <= p5){
50
}else{ 
30
}
}

trace[,2] <- as.numeric(floor(1000 * (trace[,2] - min(trace[,2]))))

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
