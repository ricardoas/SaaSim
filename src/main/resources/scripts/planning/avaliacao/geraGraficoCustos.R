#!/bin/Rscript

#>>>>>>>>>> Analise de custo 100 usuarios
jpeg("custos_100.jpg")
par(mfrow=c(3, 4))

reserved <- c(1:70)
ondemand <- c(1:70)
receipt <- c(1:70)




fee <- read.table("op_100_10/profits.dat")$V3
data <- read.table("op_100_10/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(data[initial:end, 1121]+data[initial:end, 1126] + data[initial:end, 1131]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 1119]+data[initial:end, 1124] + data[initial:end, 1129]))
	initial = initial + 12
	end = end + 12
}

plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="op_100_10", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("ov_100_10/profits.dat")$V3
data <- read.table("ov_100_10/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(data[initial:end, 1121]+data[initial:end, 1126] + data[initial:end, 1131]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 1119]+data[initial:end, 1124] + data[initial:end, 1129]))
	initial = initial + 12
	end = end + 12
}

plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ov_100_10", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")

fee <- read.table("on_100_10/profits.dat")$V3
data <- read.table("on_100_10/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(data[initial:end, 1121]+data[initial:end, 1126] + data[initial:end, 1131]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 1119]+data[initial:end, 1124] + data[initial:end, 1129]))
	initial = initial + 12
	end = end + 12
}

plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="on_100_10", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")

fee <- read.table("ut_100_10/profits.dat")$V3
data <- read.table("ut_100_10/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(data[initial:end, 1121]+data[initial:end, 1126] + data[initial:end, 1131]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 1119]+data[initial:end, 1124] + data[initial:end, 1129]))
	initial = initial + 12
	end = end + 12
}

plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ut_100_10", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")





fee <- read.table("op_100_5/profits.dat")$V3
data <- read.table("op_100_5/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(data[initial:end, 1121]+data[initial:end, 1126] + data[initial:end, 1131]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 1119]+data[initial:end, 1124] + data[initial:end, 1129]))
	initial = initial + 12
	end = end + 12
}

plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="op_100_5", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("ov_100_5/profits.dat")$V3
data <- read.table("ov_100_5/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(data[initial:end, 1121]+data[initial:end, 1126] + data[initial:end, 1131]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 1119]+data[initial:end, 1124] + data[initial:end, 1129]))
	initial = initial + 12
	end = end + 12
}

plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ov_100_5", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")



fee <- read.table("on_100_5/profits.dat")$V3
data <- read.table("on_100_5/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(data[initial:end, 1121]+data[initial:end, 1126] + data[initial:end, 1131]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 1119]+data[initial:end, 1124] + data[initial:end, 1129]))
	initial = initial + 12
	end = end + 12
}

plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="on_100_5", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("ut_100_5/profits.dat")$V3
data <- read.table("ut_100_5/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(data[initial:end, 1121]+data[initial:end, 1126] + data[initial:end, 1131]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 1119]+data[initial:end, 1124] + data[initial:end, 1129]))
	initial = initial + 12
	end = end + 12
}

plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ut_100_5", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")



fee <- read.table("op_100_15/profits.dat")$V3
data <- read.table("op_100_15/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(data[initial:end, 1121]+data[initial:end, 1126] + data[initial:end, 1131]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 1119]+data[initial:end, 1124] + data[initial:end, 1129]))
	initial = initial + 12
	end = end + 12
}

plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="op_100_15", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("ov_100_15/profits.dat")$V3
data <- read.table("ov_100_15/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(data[initial:end, 1121]+data[initial:end, 1126] + data[initial:end, 1131]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 1119]+data[initial:end, 1124] + data[initial:end, 1129]))
	initial = initial + 12
	end = end + 12
}

plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ov_100_15", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("on_100_15/profits.dat")$V3
data <- read.table("on_100_15/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(data[initial:end, 1121]+data[initial:end, 1126] + data[initial:end, 1131]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 1119]+data[initial:end, 1124] + data[initial:end, 1129]))
	initial = initial + 12
	end = end + 12
}

plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="on_100_15", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")



fee <- read.table("ut_100_15/profits.dat")$V3
data <- read.table("ut_100_15/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(data[initial:end, 1121]+data[initial:end, 1126] + data[initial:end, 1131]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 1119]+data[initial:end, 1124] + data[initial:end, 1129]))
	initial = initial + 12
	end = end + 12
}

plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ut_100_15", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")





#>>>>>>>> Analise custos 50 usuarios
jpeg("custos_50.jpg")
par(mfrow=c(3, 4))

reserved <- c(1:71)
ondemand <- c(1:71)
receipt <- c(1:71)


fee <- read.table("op_50_10/profits.dat")$V3
data <- read.table("op_50_10/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 571]+data[initial:end, 576] + data[initial:end, 581]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 569]+data[initial:end, 574] + data[initial:end, 579]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="op_50_10", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("ut_50_10/profits.dat")$V3
data <- read.table("ut_50_10/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 571]+data[initial:end, 576] + data[initial:end, 581]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 569]+data[initial:end, 574] + data[initial:end, 579]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ut_50_10", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")

fee <- read.table("ov_50_10/profits.dat")$V3
data <- read.table("ov_50_10/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 571]+data[initial:end, 576] + data[initial:end, 581]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 569]+data[initial:end, 574] + data[initial:end, 579]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ov_50_10", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("on_50_10/profits.dat")$V3
data <- read.table("on_50_10/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 571]+data[initial:end, 576] + data[initial:end, 581]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 569]+data[initial:end, 574] + data[initial:end, 579]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="on_50_10", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")





fee <- read.table("op_50_5/profits.dat")$V3
data <- read.table("op_50_5/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 571]+data[initial:end, 576] + data[initial:end, 581]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 569]+data[initial:end, 574] + data[initial:end, 579]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="op_50_5", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("ut_50_5/profits.dat")$V3
data <- read.table("ut_50_5/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 571]+data[initial:end, 576] + data[initial:end, 581]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 569]+data[initial:end, 574] + data[initial:end, 579]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ut_50_5", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")

fee <- read.table("ov_50_5/profits.dat")$V3
data <- read.table("ov_50_5/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 571]+data[initial:end, 576] + data[initial:end, 581]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 569]+data[initial:end, 574] + data[initial:end, 579]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ov_50_5", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("on_50_5/profits.dat")$V3
data <- read.table("on_50_5/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 571]+data[initial:end, 576] + data[initial:end, 581]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 569]+data[initial:end, 574] + data[initial:end, 579]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="on_50_5", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")





fee <- read.table("op_50_15/profits.dat")$V3
data <- read.table("op_50_15/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 571]+data[initial:end, 576] + data[initial:end, 581]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 569]+data[initial:end, 574] + data[initial:end, 579]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="op_50_15", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("ut_50_15/profits.dat")$V3
data <- read.table("ut_50_15/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 571]+data[initial:end, 576] + data[initial:end, 581]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 569]+data[initial:end, 574] + data[initial:end, 579]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ut_50_15", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")

fee <- read.table("ov_50_15/profits.dat")$V3
data <- read.table("ov_50_15/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 571]+data[initial:end, 576] + data[initial:end, 581]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 569]+data[initial:end, 574] + data[initial:end, 579]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ov_50_15", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("on_50_15/profits.dat")$V3
data <- read.table("on_50_15/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 571]+data[initial:end, 576] + data[initial:end, 581]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 569]+data[initial:end, 574] + data[initial:end, 579]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="on_50_5", ylim=c(0, 200000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")





#>>>>>>>> Analise custos 10 usuarios
jpeg("custos_10.jpg")
par(mfrow=c(3, 4))

reserved <- c(1:71)
ondemand <- c(1:71)
receipt <- c(1:71)


fee <- read.table("op_10_10/profits.dat")$V3
data <- read.table("op_10_10/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 131]+data[initial:end, 136] + data[initial:end, 141]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 129]+data[initial:end, 134] + data[initial:end, 139]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="op_10_10", ylim=c(0, 20000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("ut_10_10/profits.dat")$V3
data <- read.table("ut_10_10/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 131]+data[initial:end, 136] + data[initial:end, 141]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 129]+data[initial:end, 134] + data[initial:end, 139]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ut_10_10", ylim=c(0, 20000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")

fee <- read.table("ov_10_10/profits.dat")$V3
data <- read.table("ov_10_10/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 131]+data[initial:end, 136] + data[initial:end, 141]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 129]+data[initial:end, 134] + data[initial:end, 139]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ov_10_10", ylim=c(0, 20000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("on_10_10/profits.dat")$V3
data <- read.table("on_10_10/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 131]+data[initial:end, 136] + data[initial:end, 141]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 129]+data[initial:end, 134] + data[initial:end, 139]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="on_10_10", ylim=c(0, 20000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")





fee <- read.table("op_10_5/profits.dat")$V3
data <- read.table("op_10_5/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 131]+data[initial:end, 136] + data[initial:end, 141]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 129]+data[initial:end, 134] + data[initial:end, 139]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="op_10_5", ylim=c(0, 20000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("ut_10_5/profits.dat")$V3
data <- read.table("ut_10_5/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 131]+data[initial:end, 136] + data[initial:end, 141]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 129]+data[initial:end, 134] + data[initial:end, 139]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ut_10_5", ylim=c(0, 20000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")

fee <- read.table("ov_10_5/profits.dat")$V3
data <- read.table("ov_10_5/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 131]+data[initial:end, 136] + data[initial:end, 141]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 129]+data[initial:end, 134] + data[initial:end, 139]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ov_10_5", ylim=c(0, 20000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("on_10_5/profits.dat")$V3
data <- read.table("on_10_5/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 131]+data[initial:end, 136] + data[initial:end, 141]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 129]+data[initial:end, 134] + data[initial:end, 139]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="on_10_5", ylim=c(0, 20000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")





fee <- read.table("op_10_15/profits.dat")$V3
data <- read.table("op_10_15/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 131]+data[initial:end, 136] + data[initial:end, 141]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 129]+data[initial:end, 134] + data[initial:end, 139]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="op_10_15", ylim=c(0, 20000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("ut_10_15/profits.dat")$V3
data <- read.table("ut_10_15/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 131]+data[initial:end, 136] + data[initial:end, 141]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 129]+data[initial:end, 134] + data[initial:end, 139]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ut_10_15", ylim=c(0, 20000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")

fee <- read.table("ov_10_15/profits.dat")$V3
data <- read.table("ov_10_15/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 131]+data[initial:end, 136] + data[initial:end, 141]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 129]+data[initial:end, 134] + data[initial:end, 139]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="ov_10_15", ylim=c(0, 20000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")


fee <- read.table("on_10_15/profits.dat")$V3
data <- read.table("on_10_15/consumption.dat")

#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:71) {
	reserved[i] <- sum(as.numeric(data[initial:end, 131]+data[initial:end, 136] + data[initial:end, 141]))
	receipt[i] <- sum(as.numeric(data[initial:end, 3]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:71) {
	ondemand[i] <- sum(as.numeric(data[initial:end, 129]+data[initial:end, 134] + data[initial:end, 139]))
	initial = initial + 12
	end = end + 12
}


plot(receipt - (reserved+fee) - ondemand, col="green", type="l", xlab="Repetições", ylab="Valores Monetarios", main="on_10_5", ylim=c(0, 20000))
lines(receipt, col="blue")
lines(reserved+fee, col="red")
lines(ondemand, col="black")



dev.off()

