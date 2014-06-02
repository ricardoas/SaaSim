#!/usr/bin/Rscript

# converts GEIST generated file to saasim.ext.ioCSVTraceReader format

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

args <- commandArgs(TRUE)

filename=args[1]

size <- as.integer(c(100*1:9,1000*1:9,10000*1:9,100000*1:9))

trace <- read.table(filename)

trace$V5 <- size[as.numeric(substring(trace$V6, 15, 18))]

trace$V2 <- as.numeric(floor(trace$V2 * 1000))

trace$V6 <- apply(trace, 1, function(x) cpu())

trace <- trace[,c(1, 2, 3, 5, 6)]

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

write.csv(trace, file=args[1], row.names=FALSE, quote=FALSE)

