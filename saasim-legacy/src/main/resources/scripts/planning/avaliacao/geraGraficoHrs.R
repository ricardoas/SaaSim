#!/bin/Rscript

#Avaliacao de reservas
op_100_10 <- read.table("op_100_10/plans.dat")
ov_100_10 <- read.table("ov_100_10/plans.dat")
ut_100_10 <- read.table("ut_100_10/plans.dat")

op_100_5 <- read.table("op_100_5/plans.dat")
ov_100_5 <- read.table("ov_100_5/plans.dat")
ut_100_5 <- read.table("ut_100_5/plans.dat")

op_100_15 <- read.table("op_100_15/plans.dat")
ov_100_15 <- read.table("ov_100_15/plans.dat")
ut_100_15 <- read.table("ut_100_15/plans.dat")

jpeg("reservas_100.jpg")
par(mfrow=c(3, 3))

barplot(op_100_10$V1, main="op_100_10", col="red")
barplot(op_100_10$V2*2, add=TRUE, col="blue")
barplot(op_100_10$V3*2, add=TRUE, col="green")
barplot(op_100_10$V4*4, add=TRUE, col="black")

barplot(ov_100_10$V2*2, main="ov_100_10", col="blue")
barplot(ov_100_10$V1, add=TRUE, col="red")
barplot(ov_100_10$V3*2, add=TRUE, col="green")
barplot(ov_100_10$V4*4, add=TRUE, col="black")

barplot(ut_100_10$V1, main="ut_100_10", col="red")
barplot(ut_100_10$V2*2, add=TRUE, col="blue")
barplot(ut_100_10$V3*2, add=TRUE, col="green")
barplot(ut_100_10$V4*4, add=TRUE, col="black")

barplot(op_100_5$V1, main="op_100_5", col="red")
barplot(op_100_5$V2*2, add=TRUE, col="blue")
barplot(op_100_5$V3*2, add=TRUE, col="green")
barplot(op_100_5$V4*4, add=TRUE, col="black")

barplot(ov_100_5$V2*2, main="ov_100_5", col="blue")
barplot(ov_100_5$V1, add=TRUE, col="red")
barplot(ov_100_5$V3*2, add=TRUE, col="green")
barplot(ov_100_5$V4*4, add=TRUE, col="black")

barplot(ut_100_5$V1, main="ut_100_5", col="red")
barplot(ut_100_5$V2*2, add=TRUE, col="blue")
barplot(ut_100_5$V3*2, add=TRUE, col="green")
barplot(ut_100_5$V4*4, add=TRUE, col="black")

barplot(op_100_15$V1, main="op_100_15", col="red")
barplot(op_100_15$V2*2, add=TRUE, col="blue")
barplot(op_100_15$V3*2, add=TRUE, col="green")
barplot(op_100_15$V4*4, add=TRUE, col="black")

barplot(ov_100_15$V2*2, main="ov_100_15", col="blue")
barplot(ov_100_15$V1, add=TRUE, col="red")
barplot(ov_100_15$V3*2, add=TRUE, col="green")
barplot(ov_100_15$V4*4, add=TRUE, col="black")

barplot(ut_100_15$V1, main="ut_100_15", col="red")
barplot(ut_100_15$V2*2, add=TRUE, col="blue")
barplot(ut_100_15$V3*2, add=TRUE, col="green")
barplot(ut_100_15$V4*4, add=TRUE, col="black")

dev.off()

#10 usuarios
op_10_10 <- read.table("op_10_10/plans.dat")
ov_10_10 <- read.table("ov_10_10/plans.dat")
ut_10_10 <- read.table("ut_10_10/plans.dat")

op_10_5 <- read.table("op_10_5/plans.dat")
ov_10_5 <- read.table("ov_10_5/plans.dat")
ut_10_5 <- read.table("ut_10_5/plans.dat")

op_10_15 <- read.table("op_10_15/plans.dat")
ov_10_15 <- read.table("ov_10_15/plans.dat")
ut_10_15 <- read.table("ut_10_15/plans.dat")

jpeg("reservas_10.jpg")
par(mfrow=c(3, 3))

barplot(op_10_10$V1, main="op_100_10", col="red")
barplot(op_10_10$V2*2, add=TRUE, col="blue")
barplot(op_10_10$V3*2, add=TRUE, col="green")
barplot(op_10_10$V4*4, add=TRUE, col="black")

barplot(ov_10_10$V2*2, main="ov_100_10", col="blue")
barplot(ov_10_10$V1, add=TRUE, col="red")
barplot(ov_10_10$V3*2, add=TRUE, col="green")
barplot(ov_10_10$V4*4, add=TRUE, col="black")

barplot(ut_10_10$V1, main="ut_100_10", col="red")
barplot(ut_10_10$V2*2, add=TRUE, col="blue")
barplot(ut_10_10$V3*2, add=TRUE, col="green")
barplot(ut_10_10$V4*4, add=TRUE, col="black")

barplot(op_10_5$V1, main="op_100_5", col="red")
barplot(op_10_5$V2*2, add=TRUE, col="blue")
barplot(op_10_5$V3*2, add=TRUE, col="green")
barplot(op_10_5$V4*4, add=TRUE, col="black")

barplot(ov_10_5$V2*2, main="ov_100_5", col="blue")
barplot(ov_10_5$V1, add=TRUE, col="red")
barplot(ov_10_5$V3*2, add=TRUE, col="green")
barplot(ov_10_5$V4*4, add=TRUE, col="black")

barplot(ut_10_5$V1, main="ut_100_5", col="red")
barplot(ut_10_5$V2*2, add=TRUE, col="blue")
barplot(ut_10_5$V3*2, add=TRUE, col="green")
barplot(ut_10_5$V4*4, add=TRUE, col="black")

barplot(op_10_15$V1, main="op_100_15", col="red")
barplot(op_10_15$V2*2, add=TRUE, col="blue")
barplot(op_10_15$V3*2, add=TRUE, col="green")
barplot(op_10_15$V4*4, add=TRUE, col="black")

barplot(ov_10_15$V2*2, main="ov_100_15", col="blue")
barplot(ov_10_15$V1, add=TRUE, col="red")
barplot(ov_10_15$V3*2, add=TRUE, col="green")
barplot(ov_10_15$V4*4, add=TRUE, col="black")

barplot(ut_10_15$V1, main="ut_100_15", col="red")
barplot(ut_10_15$V2*2, add=TRUE, col="blue")
barplot(ut_10_15$V3*2, add=TRUE, col="green")
barplot(ut_10_15$V4*4, add=TRUE, col="black")

dev.off()

#50 usuarios
op_50_10 <- read.table("op_50_10/plans.dat")
ov_50_10 <- read.table("ov_50_10/plans.dat")
ut_50_10 <- read.table("ut_50_10/plans.dat")

op_50_5 <- read.table("op_50_5/plans.dat")
ov_50_5 <- read.table("ov_50_5/plans.dat")
ut_50_5 <- read.table("ut_50_5/plans.dat")

op_50_15 <- read.table("op_50_15/plans.dat")
ov_50_15 <- read.table("ov_50_15/plans.dat")
ut_50_15 <- read.table("ut_50_15/plans.dat")

jpeg("reservas_50.jpg")
par(mfrow=c(3, 3))

barplot(op_50_10$V1, main="op_100_10", col="red")
barplot(op_50_10$V2*2, add=TRUE, col="blue")
barplot(op_50_10$V3*2, add=TRUE, col="green")
barplot(op_50_10$V4*4, add=TRUE, col="black")

barplot(ov_50_10$V2*2, main="ov_100_10", col="blue")
barplot(ov_50_10$V1, add=TRUE, col="red")
barplot(ov_50_10$V3*2, add=TRUE, col="green")
barplot(ov_50_10$V4*4, add=TRUE, col="black")

barplot(ut_50_10$V1, main="ut_100_10", col="red")
barplot(ut_50_10$V2*2, add=TRUE, col="blue")
barplot(ut_50_10$V3*2, add=TRUE, col="green")
barplot(ut_50_10$V4*4, add=TRUE, col="black")

barplot(op_50_5$V1, main="op_100_5", col="red")
barplot(op_50_5$V2*2, add=TRUE, col="blue")
barplot(op_50_5$V3*2, add=TRUE, col="green")
barplot(op_50_5$V4*4, add=TRUE, col="black")

barplot(ov_50_5$V2*2, main="ov_100_5", col="blue")
barplot(ov_50_5$V1, add=TRUE, col="red")
barplot(ov_50_5$V3*2, add=TRUE, col="green")
barplot(ov_50_5$V4*4, add=TRUE, col="black")

barplot(ut_50_5$V1, main="ut_100_5", col="red")
barplot(ut_50_5$V2*2, add=TRUE, col="blue")
barplot(ut_50_5$V3*2, add=TRUE, col="green")
barplot(ut_50_5$V4*4, add=TRUE, col="black")

barplot(op_50_15$V1, main="op_100_15", col="red")
barplot(op_50_15$V2*2, add=TRUE, col="blue")
barplot(op_50_15$V3*2, add=TRUE, col="green")
barplot(op_50_15$V4*4, add=TRUE, col="black")

barplot(ov_50_15$V2*2, main="ov_100_15", col="blue")
barplot(ov_50_15$V1, add=TRUE, col="red")
barplot(ov_50_15$V3*2, add=TRUE, col="green")
barplot(ov_50_15$V4*4, add=TRUE, col="black")

barplot(ut_50_15$V1, main="ut_100_15", col="red")
barplot(ut_50_15$V2*2, add=TRUE, col="blue")
barplot(ut_50_15$V3*2, add=TRUE, col="green")
barplot(ut_50_15$V4*4, add=TRUE, col="black")

dev.off()

#>>>>>>>>>> Analise de consumo de horas 100 usuarios
jpeg("consumo_100.jpg")
par(mfrow=c(3, 3))

reserved <- c(1:70)
ondemand <- c(1:70)

op_100_10 <- read.table("op_100_10/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(op_100_10[initial:end, 1120]+op_100_10[initial:end, 1125] + op_100_10[initial:end, 1130]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(op_100_10[initial:end, 1118]+op_100_10[initial:end, 1123] + op_100_10[initial:end, 1128]))
	initial = initial + 12
	end = end + 12
}

plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="op_100_10", ylim=c(0, 350000))
lines(reserved, col="blue")
lines(ondemand, col="black")



ov_100_10 <- read.table("ov_100_10/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ov_100_10[initial:end, 1120]+ov_100_10[initial:end, 1125] + ov_100_10[initial:end, 1130]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ov_100_10[initial:end, 1118]+ov_100_10[initial:end, 1123] + ov_100_10[initial:end, 1128]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ov_100_10", ylim=c(0, 350000))
lines(reserved, col="blue")
lines(ondemand, col="black")




ut_100_10 <- read.table("ut_100_10/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ut_100_10[initial:end, 1120]+ut_100_10[initial:end, 1125] + ut_100_10[initial:end, 1130]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ut_100_10[initial:end, 1118]+ut_100_10[initial:end, 1123] + ut_100_10[initial:end, 1128]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ut_100_10", ylim=c(0, 350000))
lines(reserved, col="blue")
lines(ondemand, col="black")



op_100_5 <- read.table("op_100_5/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(op_100_5[initial:end, 1120]+op_100_5[initial:end, 1125] + op_100_5[initial:end, 1130]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(op_100_5[initial:end, 1118]+op_100_5[initial:end, 1123] + op_100_5[initial:end, 1128]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="op_100_5", ylim=c(0, 350000))
lines(reserved, col="blue")
lines(ondemand, col="black")



ov_100_5 <- read.table("ov_100_5/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ov_100_5[initial:end, 1120]+ov_100_5[initial:end, 1125] + ov_100_5[initial:end, 1130]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ov_100_5[initial:end, 1118]+ov_100_5[initial:end, 1123] + ov_100_5[initial:end, 1128]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ov_100_5", ylim=c(0, 350000))
lines(reserved, col="blue")
lines(ondemand, col="black")



ut_100_5 <- read.table("ut_100_5/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ut_100_5[initial:end, 1120]+ut_100_5[initial:end, 1125] + ut_100_5[initial:end, 1130]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ut_100_5[initial:end, 1118]+ut_100_5[initial:end, 1123] + ut_100_5[initial:end, 1128]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ut_100_5", ylim=c(0, 350000))
lines(reserved, col="blue")
lines(ondemand, col="black")



op_100_15 <- read.table("op_100_15/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(op_100_15[initial:end, 1120]+op_100_15[initial:end, 1125] + op_100_15[initial:end, 1130]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(op_100_15[initial:end, 1118]+op_100_15[initial:end, 1123] + op_100_15[initial:end, 1128]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="op_100_15", ylim=c(0, 350000))
lines(reserved, col="blue")
lines(ondemand, col="black")


ov_100_15 <- read.table("ov_100_15/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ov_100_15[initial:end, 1120]+ov_100_15[initial:end, 1125] + ov_100_15[initial:end, 1130]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ov_100_15[initial:end, 1118]+ov_100_15[initial:end, 1123] + ov_100_15[initial:end, 1128]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ov_100_15", ylim=c(0, 350000))
lines(reserved, col="blue")
lines(ondemand, col="black")

ut_100_15 <- read.table("ut_100_15/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ut_100_15[initial:end, 1120]+ut_100_15[initial:end, 1125] + ut_100_15[initial:end, 1130]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ut_100_15[initial:end, 1118]+ut_100_15[initial:end, 1123] + ut_100_15[initial:end, 1128]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ut_100_15", ylim=c(0, 350000))
lines(reserved, col="blue")
lines(ondemand, col="black")


dev.off()









#>>>>>>>>>> Analise de consumo de horas 50 usuarios
jpeg("consumo_50.jpg")
par(mfrow=c(3, 3))

reserved <- c(1:70)
ondemand <- c(1:70)

op_50_10 <- read.table("op_50_10/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(op_50_10[initial:end, 570]+op_50_10[initial:end, 575] + op_50_10[initial:end, 580]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(op_50_10[initial:end, 568]+op_50_10[initial:end, 573] + op_50_10[initial:end, 578]))
	initial = initial + 12
	end = end + 12
}

plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="op_50_10", ylim=c(0, 200000))
lines(reserved, col="blue")
lines(ondemand, col="black")



ov_50_10 <- read.table("ov_50_10/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ov_50_10[initial:end, 570]+ov_50_10[initial:end, 575] + ov_50_10[initial:end, 580]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ov_50_10[initial:end, 568]+ov_50_10[initial:end, 573] + ov_50_10[initial:end, 578]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ov_50_10", ylim=c(0, 200000))
lines(reserved, col="blue")
lines(ondemand, col="black")




ut_50_10 <- read.table("ut_50_10/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ut_50_10[initial:end, 570]+ut_50_10[initial:end, 575] + ut_50_10[initial:end, 580]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ut_50_10[initial:end, 568]+ut_50_10[initial:end, 573] + ut_50_10[initial:end, 578]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ut_50_10", ylim=c(0, 200000))
lines(reserved, col="blue")
lines(ondemand, col="black")



op_50_5 <- read.table("op_50_5/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(op_50_10[initial:end, 570]+op_50_10[initial:end, 575] + op_50_10[initial:end, 580]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(op_50_10[initial:end, 568]+op_50_10[initial:end, 573] + op_50_10[initial:end, 578]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="op_50_5", ylim=c(0, 200000))
lines(reserved, col="blue")
lines(ondemand, col="black")



ov_50_5 <- read.table("ov_50_5/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ov_50_10[initial:end, 570]+ov_50_10[initial:end, 575] + ov_50_10[initial:end, 580]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ov_50_10[initial:end, 568]+ov_50_10[initial:end, 573] + ov_50_10[initial:end, 578]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ov_50_5", ylim=c(0, 200000))
lines(reserved, col="blue")
lines(ondemand, col="black")



ut_50_5 <- read.table("ut_50_5/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ut_50_10[initial:end, 570]+ut_50_10[initial:end, 575] + ut_50_10[initial:end, 580]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ut_50_10[initial:end, 568]+ut_50_10[initial:end, 573] + ut_50_10[initial:end, 578]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ut_50_5", ylim=c(0, 200000))
lines(reserved, col="blue")
lines(ondemand, col="black")



op_50_15 <- read.table("op_50_15/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(op_50_10[initial:end, 570]+op_50_10[initial:end, 575] + op_50_10[initial:end, 580]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(op_50_10[initial:end, 568]+op_50_10[initial:end, 573] + op_50_10[initial:end, 578]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="op_50_15", ylim=c(0, 200000))
lines(reserved, col="blue")
lines(ondemand, col="black")


ov_50_15 <- read.table("ov_50_15/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ov_50_10[initial:end, 570]+ov_50_10[initial:end, 575] + ov_50_10[initial:end, 580]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ov_50_10[initial:end, 568]+ov_50_10[initial:end, 573] + ov_50_10[initial:end, 578]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ov_50_15", ylim=c(0, 200000))
lines(reserved, col="blue")
lines(ondemand, col="black")

ut_50_15 <- read.table("ut_50_15/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ut_50_10[initial:end, 570]+ut_50_10[initial:end, 575] + ut_50_10[initial:end, 580]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ut_50_10[initial:end, 568]+ut_50_10[initial:end, 573] + ut_50_10[initial:end, 578]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ut_50_15", ylim=c(0, 200000))
lines(reserved, col="blue")
lines(ondemand, col="black")


dev.off()








#>>>>>>>>>> Analise de consumo de horas 10 usuarios
jpeg("consumo_10.jpg")
par(mfrow=c(3, 3))

reserved <- c(1:70)
ondemand <- c(1:70)

op_10_10 <- read.table("op_10_10/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(op_10_10[initial:end, 130]+op_10_10[initial:end, 135] + op_10_10[initial:end, 140]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(op_10_10[initial:end, 128]+op_10_10[initial:end, 133] + op_10_10[initial:end, 138]))
	initial = initial + 12
	end = end + 12
}

plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="op_10_10", ylim=c(0, 100000))
lines(reserved, col="blue")
lines(ondemand, col="black")



ov_10_10 <- read.table("ov_10_10/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ov_10_10[initial:end, 130]+ov_10_10[initial:end, 135] + ov_10_10[initial:end, 140]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ov_10_10[initial:end, 128]+ov_10_10[initial:end, 133] + ov_10_10[initial:end, 138]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ov_10_10", ylim=c(0, 100000))
lines(reserved, col="blue")
lines(ondemand, col="black")




ut_10_10 <- read.table("ut_10_10/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ut_10_10[initial:end, 130]+ut_10_10[initial:end, 135] + ut_10_10[initial:end, 140]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ut_10_10[initial:end, 128]+ut_10_10[initial:end, 133] + ut_10_10[initial:end, 138]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ut_10_10", ylim=c(0, 100000))
lines(reserved, col="blue")
lines(ondemand, col="black")



op_10_5 <- read.table("op_10_5/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(op_10_10[initial:end, 130]+op_10_10[initial:end, 135] + op_10_10[initial:end, 140]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(op_10_10[initial:end, 128]+op_10_10[initial:end, 133] + op_10_10[initial:end, 138]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="op_10_5", ylim=c(0, 100000))
lines(reserved, col="blue")
lines(ondemand, col="black")



ov_10_5 <- read.table("ov_10_5/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ov_10_10[initial:end, 130]+ov_10_10[initial:end, 135] + ov_10_10[initial:end, 140]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ov_10_10[initial:end, 128]+ov_10_10[initial:end, 133] + ov_10_10[initial:end, 138]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ov_10_5", ylim=c(0, 100000))
lines(reserved, col="blue")
lines(ondemand, col="black")



ut_10_5 <- read.table("ut_10_5/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ut_10_10[initial:end, 130]+ut_10_10[initial:end, 135] + ut_10_10[initial:end, 140]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ut_10_10[initial:end, 128]+ut_10_10[initial:end, 133] + ut_10_10[initial:end, 138]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ut_10_5", ylim=c(0, 100000))
lines(reserved, col="blue")
lines(ondemand, col="black")



op_10_15 <- read.table("op_10_15/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(op_10_10[initial:end, 130]+op_10_10[initial:end, 135] + op_10_10[initial:end, 140]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(op_10_10[initial:end, 128]+op_10_10[initial:end, 133] + op_10_10[initial:end, 138]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="op_10_15", ylim=c(0, 100000))
lines(reserved, col="blue")
lines(ondemand, col="black")


ov_10_15 <- read.table("ov_10_15/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ov_10_10[initial:end, 130]+ov_10_10[initial:end, 135] + ov_10_10[initial:end, 140]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ov_10_10[initial:end, 128]+ov_10_10[initial:end, 133] + ov_10_10[initial:end, 138]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ov_10_15", ylim=c(0, 100000))
lines(reserved, col="blue")
lines(ondemand, col="black")

ut_10_15 <- read.table("ut_10_15/consumption.dat")
#Total reserved hrs
initial <- 1
end <- 12
for (i in 1:70) {
	reserved[i] <- sum(as.numeric(ut_10_10[initial:end, 130]+ut_10_10[initial:end, 135] + ut_10_10[initial:end, 140]))
	initial = initial + 12
	end = end + 12
}

#Total on-demand hrs
initial <- 1
end <- 12
for (i in 1:70) {
	ondemand[i] <- sum(as.numeric(ut_10_10[initial:end, 128]+ut_10_10[initial:end, 133] + ut_10_10[initial:end, 138]))
	initial = initial + 12
	end = end + 12
}
plot(reserved+ondemand, col="red", type="l", xlab="Repetições", ylab="Total de hrs", main="ut_10_15", ylim=c(0, 100000))
lines(reserved, col="blue")
lines(ondemand, col="black")


dev.off()

