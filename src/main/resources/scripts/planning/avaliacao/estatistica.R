#!/bin/Rscript

on_100_10 <- read.table("on_100_10/profits.dat")$V1
op_100_10 <- read.table("op_100_10/profits.dat")$V1
ov_100_10 <- read.table("ov_100_10/profits.dat")$V1
ut_100_10 <- read.table("ut_100_10/profits.dat")$V1

# Testes de hipoteses
print("100 us 10%: hi == on")
wilcox.test(ut_100_10, on_100_10, paired=TRUE, var.equal=FALSE)
print("hi > on")
wilcox.test(ut_100_10, on_100_10, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == ov")
wilcox.test(ut_100_10, ov_100_10, paired=TRUE, var.equal=FALSE)
print("hi > ov")
wilcox.test(ut_100_10, ov_100_10, paired=TRUE, var.equal=FALSE, alternative="less")

print("ov == on")
wilcox.test(on_100_10, ov_100_10, paired=TRUE, var.equal=FALSE)
print("on > ov")
wilcox.test(on_100_10, ov_100_10, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == op")
wilcox.test(ut_100_10, op_100_10, paired=TRUE, var.equal=FALSE)
print("hi > op")
wilcox.test(ut_100_10, op_100_10, paired=TRUE, var.equal=FALSE, alternative="less")

print("op == on")
wilcox.test(op_100_10, on_100_10, paired=TRUE, var.equal=FALSE)
print("op > on")
wilcox.test(op_100_10, on_100_10, paired=TRUE, var.equal=FALSE, alternative="less")



on_100_5 <- read.table("on_100_5/profits.dat")$V1
op_100_5 <- read.table("op_100_5/profits.dat")$V1
ov_100_5 <- read.table("ov_100_5/profits.dat")$V1
ut_100_5 <- read.table("ut_100_5/profits.dat")$V1

# Testes de hipoteses
print("100 us 5%: hi == on")
wilcox.test(ut_100_5, on_100_5, paired=TRUE, var.equal=FALSE)
print("hi > on")
wilcox.test(ut_100_5, on_100_5, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == ov")
wilcox.test(ut_100_5, ov_100_5, paired=TRUE, var.equal=FALSE)
print("hi > ov")
wilcox.test(ut_100_5, ov_100_5, paired=TRUE, var.equal=FALSE, alternative="less")

print("ov == on")
wilcox.test(on_100_5, ov_100_5, paired=TRUE, var.equal=FALSE)
print("on > ov")
wilcox.test(on_100_5, ov_100_5, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == op")
wilcox.test(ut_100_5, op_100_5, paired=TRUE, var.equal=FALSE)
print("hi > op")
wilcox.test(ut_100_5, op_100_5, paired=TRUE, var.equal=FALSE, alternative="less")

print("op == on")
wilcox.test(op_100_5, on_100_5, paired=TRUE, var.equal=FALSE)
print("op > on")
wilcox.test(op_100_5, on_100_5, paired=TRUE, var.equal=FALSE, alternative="less")


on_100_15 <- read.table("on_100_15/profits.dat")$V1
op_100_15 <- read.table("op_100_15/profits.dat")$V1
ov_100_15 <- read.table("ov_100_15/profits.dat")$V1
ut_100_15 <- read.table("ut_100_15/profits.dat")$V1

# Testes de hipoteses
print("100 us 15%: hi == on")
wilcox.test(ut_100_15, on_100_15, paired=TRUE, var.equal=FALSE)
print("hi > on")
wilcox.test(ut_100_15, on_100_15, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == ov")
wilcox.test(ut_100_15, ov_100_15, paired=TRUE, var.equal=FALSE)
print("hi > ov")
wilcox.test(ut_100_15, ov_100_15, paired=TRUE, var.equal=FALSE, alternative="less")

print("ov == on")
wilcox.test(on_100_15, ov_100_15, paired=TRUE, var.equal=FALSE)
print("on > ov")
wilcox.test(on_100_15, ov_100_15, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == op")
wilcox.test(ut_100_15, op_100_15, paired=TRUE, var.equal=FALSE)
print("hi > op")
wilcox.test(ut_100_15, op_100_15, paired=TRUE, var.equal=FALSE, alternative="less")

print("op == on")
wilcox.test(op_100_15, on_100_15, paired=TRUE, var.equal=FALSE)
print("op > on")
wilcox.test(op_100_15, on_100_15, paired=TRUE, var.equal=FALSE, alternative="less")



on_10_10 <- read.table("on_10_10/profits.dat")$V1
op_10_10 <- read.table("op_10_10/profits.dat")$V1
ov_10_10 <- read.table("ov_10_10/profits.dat")$V1
ut_10_10 <- read.table("ut_10_10/profits.dat")$V1

# Testes de hipoteses
print("10 us 10%: hi == on")
wilcox.test(ut_10_10, on_10_10, paired=TRUE, var.equal=FALSE)
print("hi > on")
wilcox.test(ut_10_10, on_10_10, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == ov")
wilcox.test(ut_10_10, ov_10_10, paired=TRUE, var.equal=FALSE)
print("hi > ov")
wilcox.test(ut_10_10, ov_10_10, paired=TRUE, var.equal=FALSE, alternative="less")

print("ov == on")
wilcox.test(on_10_10, ov_10_10, paired=TRUE, var.equal=FALSE)
print("on > ov")
wilcox.test(on_10_10, ov_10_10, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == op")
wilcox.test(ut_10_10, op_10_10, paired=TRUE, var.equal=FALSE)
print("hi > op")
wilcox.test(ut_10_10, op_10_10, paired=TRUE, var.equal=FALSE, alternative="less")

print("op == on")
wilcox.test(op_10_10, on_10_10, paired=TRUE, var.equal=FALSE)
print("op > on")
wilcox.test(op_10_10, on_10_10, paired=TRUE, var.equal=FALSE, alternative="less")




on_10_5 <- read.table("on_10_5/profits.dat")$V1
op_10_5 <- read.table("op_10_5/profits.dat")$V1
ov_10_5 <- read.table("ov_10_5/profits.dat")$V1
ut_10_5 <- read.table("ut_10_5/profits.dat")$V1

# Testes de hipoteses
print("10 us 5%: hi == on")
wilcox.test(ut_10_5, on_10_5, paired=TRUE, var.equal=FALSE)
print("hi > on")
wilcox.test(ut_10_5, on_10_5, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == ov")
wilcox.test(ut_10_5, ov_10_5, paired=TRUE, var.equal=FALSE)
print("hi > ov")
wilcox.test(ut_10_5, ov_10_5, paired=TRUE, var.equal=FALSE, alternative="less")

print("ov == on")
wilcox.test(on_10_5, ov_10_5, paired=TRUE, var.equal=FALSE)
print("on > ov")
wilcox.test(on_10_5, ov_10_5, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == op")
wilcox.test(ut_10_5, op_10_5, paired=TRUE, var.equal=FALSE)
print("hi > op")
wilcox.test(ut_10_5, op_10_5, paired=TRUE, var.equal=FALSE, alternative="less")

print("op == on")
wilcox.test(op_10_5, on_10_5, paired=TRUE, var.equal=FALSE)
print("op > on")
wilcox.test(op_10_5, on_10_5, paired=TRUE, var.equal=FALSE, alternative="less")




on_10_15 <- read.table("on_10_15/profits.dat")$V1
op_10_15 <- read.table("op_10_15/profits.dat")$V1
ov_10_15 <- read.table("ov_10_15/profits.dat")$V1
ut_10_15 <- read.table("ut_10_15/profits.dat")$V1

# Testes de hipoteses
print("10 us 15%: hi == on")
wilcox.test(ut_10_15, on_10_15, paired=TRUE, var.equal=FALSE)
print("hi > on")
wilcox.test(ut_10_15, on_10_15, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == ov")
wilcox.test(ut_10_15, ov_10_15, paired=TRUE, var.equal=FALSE)
print("hi > ov")
wilcox.test(ut_10_15, ov_10_15, paired=TRUE, var.equal=FALSE, alternative="less")

print("ov == on")
wilcox.test(on_10_15, ov_10_15, paired=TRUE, var.equal=FALSE)
print("on > ov")
wilcox.test(on_10_15, ov_10_15, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == op")
wilcox.test(ut_10_15, op_10_15, paired=TRUE, var.equal=FALSE)
print("hi > op")
wilcox.test(ut_10_15, op_10_15, paired=TRUE, var.equal=FALSE, alternative="less")

print("op == on")
wilcox.test(op_10_15, on_10_15, paired=TRUE, var.equal=FALSE)
print("op > on")
wilcox.test(op_10_15, on_10_15, paired=TRUE, var.equal=FALSE, alternative="less")





on_50_10 <- read.table("on_50_10/profits.dat")$V1
op_50_10 <- read.table("op_50_10/profits.dat")$V1
ov_50_10 <- read.table("ov_50_10/profits.dat")$V1
ut_50_10 <- read.table("ut_50_10/profits.dat")$V1

# Testes de hipoteses
print("50 us 10%: hi == on")
wilcox.test(ut_50_10, on_50_10, paired=TRUE, var.equal=FALSE)
print("hi > on")
wilcox.test(ut_50_10, on_50_10, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == ov")
wilcox.test(ut_50_10, ov_50_10, paired=TRUE, var.equal=FALSE)
print("hi > ov")
wilcox.test(ut_50_10, ov_50_10, paired=TRUE, var.equal=FALSE, alternative="less")

print("ov == on")
wilcox.test(on_50_10, ov_50_10, paired=TRUE, var.equal=FALSE)
print("on > ov")
wilcox.test(on_50_10, ov_50_10, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == op")
wilcox.test(ut_50_10, op_50_10, paired=TRUE, var.equal=FALSE)
print("hi > op")
wilcox.test(ut_50_10, op_50_10, paired=TRUE, var.equal=FALSE, alternative="less")

print("op == on")
wilcox.test(op_50_10, on_50_10, paired=TRUE, var.equal=FALSE)
print("op > on")
wilcox.test(op_50_10, on_50_10, paired=TRUE, var.equal=FALSE, alternative="less")



on_50_5 <- read.table("on_50_5/profits.dat")$V1
op_50_5 <- read.table("op_50_5/profits.dat")$V1
ov_50_5 <- read.table("ov_50_5/profits.dat")$V1
ut_50_5 <- read.table("ut_50_5/profits.dat")$V1

# Testes de hipoteses
print("50 us 5%: hi == on")
wilcox.test(ut_50_5, on_50_5, paired=TRUE, var.equal=FALSE)
print("hi > on")
wilcox.test(ut_50_5, on_50_5, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == ov")
wilcox.test(ut_50_5, ov_50_5, paired=TRUE, var.equal=FALSE)
print("hi > ov")
wilcox.test(ut_50_5, ov_50_5, paired=TRUE, var.equal=FALSE, alternative="less")

print("ov == on")
wilcox.test(on_50_5, ov_50_5, paired=TRUE, var.equal=FALSE)
print("on > ov")
wilcox.test(on_50_5, ov_50_5, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == op")
wilcox.test(ut_50_5, op_50_5, paired=TRUE, var.equal=FALSE)
print("hi > op")
wilcox.test(ut_50_5, op_50_5, paired=TRUE, var.equal=FALSE, alternative="less")

print("op == on")
wilcox.test(op_50_5, on_50_5, paired=TRUE, var.equal=FALSE)
print("op > on")
wilcox.test(op_50_5, on_50_5, paired=TRUE, var.equal=FALSE, alternative="less")





on_50_15 <- read.table("on_50_15/profits.dat")$V1
op_50_15 <- read.table("op_50_15/profits.dat")$V1
ov_50_15 <- read.table("ov_50_15/profits.dat")$V1
ut_50_15 <- read.table("ut_50_15/profits.dat")$V1

# Testes de hipoteses
print("50 us 15%: hi == on")
wilcox.test(ut_50_15, on_50_15, paired=TRUE, var.equal=FALSE)
print("hi > on")
wilcox.test(ut_50_15, on_50_15, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == ov")
wilcox.test(ut_50_10, ov_50_15, paired=TRUE, var.equal=FALSE)
print("hi > ov")
wilcox.test(ut_50_10, ov_50_15, paired=TRUE, var.equal=FALSE, alternative="less")

print("ov == on")
wilcox.test(on_50_15, ov_50_15, paired=TRUE, var.equal=FALSE)
print("on > ov")
wilcox.test(on_50_15, ov_50_15, paired=TRUE, var.equal=FALSE, alternative="less")

print("hi == op")
wilcox.test(ut_50_15, op_50_15, paired=TRUE, var.equal=FALSE)
print("hi > op")
wilcox.test(ut_50_15, op_50_15, paired=TRUE, var.equal=FALSE, alternative="less")

print("op == on")
wilcox.test(op_50_15, on_50_15, paired=TRUE, var.equal=FALSE)
print("op > on")
wilcox.test(op_50_15, on_50_15, paired=TRUE, var.equal=FALSE, alternative="less")


# >>>>>> Resumos dos profits
print(">>>>>>>> LUCROS 100 10%")
paste("OP", mean(op_100_10), "[", 1.96*sd(op_100_10)/sqrt(length(op_100_10))+mean(op_100_10), ":", mean(op_100_10) - 1.96*sd(op_100_10)/sqrt(length(op_100_10)), "]")
paste("UT", mean(ut_100_10), "[", 1.96*sd(ut_100_10)/sqrt(length(ut_100_10))+mean(ut_100_10), ":", mean(ut_100_10) - 1.96*sd(ut_100_10)/sqrt(length(ut_100_10)), "]")
paste("ON", mean(on_100_10), "[", 1.96*sd(on_100_10)/sqrt(length(on_100_10))+mean(on_100_10), ":", mean(on_100_10) - 1.96*sd(on_100_10)/sqrt(length(on_100_10)), "]")
paste("OV", mean(ov_100_10), "[", 1.96*sd(ov_100_10)/sqrt(length(ov_100_10))+mean(ov_100_10), ":", mean(ov_100_10) - 1.96*sd(ov_100_10)/sqrt(length(ov_100_10)), "]")

print(">>>>>>>> LUCROS 10 10%")
paste("OP", mean(op_10_10), "[", 1.96*sd(op_10_10)/sqrt(length(op_10_10))+mean(op_10_10), ":", mean(op_10_10) - 1.96*sd(op_10_10)/sqrt(length(op_10_10)), "]")
paste("UT", mean(ut_10_10), "[", 1.96*sd(ut_10_10)/sqrt(length(ut_10_10))+mean(ut_10_10), ":", mean(ut_10_10) - 1.96*sd(ut_10_10)/sqrt(length(ut_10_10)), "]")
paste("ON", mean(on_10_10), "[", 1.96*sd(on_10_10)/sqrt(length(on_10_10))+mean(on_10_10), ":", mean(on_10_10) - 1.96*sd(on_10_10)/sqrt(length(on_10_10)), "]")
paste("OV", mean(ov_10_10), "[", 1.96*sd(ov_10_10)/sqrt(length(ov_10_10))+mean(ov_10_10), ":", mean(ov_10_10) - 1.96*sd(ov_10_10)/sqrt(length(ov_10_10)), "]")

print(">>>>>>>> LUCROS 50 10%")
paste("OP", mean(op_50_10), "[", 1.96*sd(op_50_10)/sqrt(length(op_50_10))+mean(op_50_10), ":", mean(op_50_10) - 1.96*sd(op_50_10)/sqrt(length(op_50_10)), "]")
paste("UT", mean(ut_50_10), "[", 1.96*sd(ut_50_10)/sqrt(length(ut_50_10))+mean(ut_50_10), ":", mean(ut_50_10) - 1.96*sd(ut_50_10)/sqrt(length(ut_50_10)), "]")
paste("ON", mean(on_50_10), "[", 1.96*sd(on_50_10)/sqrt(length(on_50_10))+mean(on_50_10), ":", mean(on_50_10) - 1.96*sd(on_50_10)/sqrt(length(on_50_10)), "]")
paste("OV", mean(ov_50_10), "[", 1.96*sd(ov_50_10)/sqrt(length(ov_50_10))+mean(ov_50_10), ":", mean(ov_50_10) - 1.96*sd(ov_50_10)/sqrt(length(ov_50_10)), "]")

print(">>>>>>>> LUCROS 100 15%")
paste("OP", mean(op_100_15), "[", 1.96*sd(op_100_15)/sqrt(length(op_100_15))+mean(op_100_15), ":", mean(op_100_15) - 1.96*sd(op_100_15)/sqrt(length(op_100_15)), "]")
paste("UT", mean(ut_100_15), "[", 1.96*sd(ut_100_15)/sqrt(length(ut_100_15))+mean(ut_100_15), ":", mean(ut_100_15) - 1.96*sd(ut_100_15)/sqrt(length(ut_100_15)), "]")
paste("ON", mean(on_100_15), "[", 1.96*sd(on_100_15)/sqrt(length(on_100_15))+mean(on_100_15), ":", mean(on_100_15) - 1.96*sd(on_100_15)/sqrt(length(on_100_15)), "]")
paste("OV", mean(ov_100_15), "[", 1.96*sd(ov_100_15)/sqrt(length(ov_100_15))+mean(ov_100_15), ":", mean(ov_100_15) - 1.96*sd(ov_100_15)/sqrt(length(ov_100_15)), "]")

print(">>>>>>>> LUCROS 10 15%")
paste("OP", mean(op_10_15), "[", 1.96*sd(op_10_15)/sqrt(length(op_10_15))+mean(op_10_15), ":", mean(op_10_15) - 1.96*sd(op_10_15)/sqrt(length(op_10_15)), "]")
paste("UT", mean(ut_10_15), "[", 1.96*sd(ut_10_15)/sqrt(length(ut_10_15))+mean(ut_10_15), ":", mean(ut_10_15) - 1.96*sd(ut_10_15)/sqrt(length(ut_10_15)), "]")
paste("ON", mean(on_10_15), "[", 1.96*sd(on_10_15)/sqrt(length(on_10_15))+mean(on_10_15), ":", mean(on_10_15) - 1.96*sd(on_10_15)/sqrt(length(on_10_15)), "]")
paste("OV", mean(ov_10_15), "[", 1.96*sd(ov_10_15)/sqrt(length(ov_10_15))+mean(ov_10_15), ":", mean(ov_10_15) - 1.96*sd(ov_10_15)/sqrt(length(ov_10_15)), "]")

print(">>>>>>>> LUCROS 50 15%")
paste("OP", mean(op_50_15), "[", 1.96*sd(op_50_15)/sqrt(length(op_50_15))+mean(op_50_15), ":", mean(op_50_15) - 1.96*sd(op_50_15)/sqrt(length(op_50_15)), "]")
paste("UT", mean(ut_50_15), "[", 1.96*sd(ut_50_15)/sqrt(length(ut_50_15))+mean(ut_50_15), ":", mean(ut_50_15) - 1.96*sd(ut_50_15)/sqrt(length(ut_50_15)), "]")
paste("ON", mean(on_50_15), "[", 1.96*sd(on_50_15)/sqrt(length(on_50_15))+mean(on_50_15), ":", mean(on_50_15) - 1.96*sd(on_50_15)/sqrt(length(on_50_15)), "]")
paste("OV", mean(ov_50_15), "[", 1.96*sd(ov_50_15)/sqrt(length(ov_50_15))+mean(ov_50_15), ":", mean(ov_50_15) - 1.96*sd(ov_50_15)/sqrt(length(ov_50_15)), "]")

print(">>>>>>>> LUCROS 100 5%")
paste("OP", mean(op_100_5), "[", 1.96*sd(op_100_5)/sqrt(length(op_100_5))+mean(op_100_5), ":", mean(op_100_5) - 1.96*sd(op_100_5)/sqrt(length(op_100_5)), "]")
paste("UT", mean(ut_100_5), "[", 1.96*sd(ut_100_5)/sqrt(length(ut_100_5))+mean(ut_100_5), ":", mean(ut_100_5) - 1.96*sd(ut_100_5)/sqrt(length(ut_100_5)), "]")
paste("ON", mean(on_100_5), "[", 1.96*sd(on_100_5)/sqrt(length(on_100_5))+mean(on_100_5), ":", mean(on_100_5) - 1.96*sd(on_100_5)/sqrt(length(on_100_5)), "]")
paste("OV", mean(ov_100_5), "[", 1.96*sd(ov_100_5)/sqrt(length(ov_100_5))+mean(ov_100_5), ":", mean(ov_100_5) - 1.96*sd(ov_100_5)/sqrt(length(ov_100_5)), "]")

print(">>>>>>>> LUCROS 10 5%")
paste("OP", mean(op_10_5), "[", 1.96*sd(op_10_5)/sqrt(length(op_10_5))+mean(op_10_5), ":", mean(op_10_5) - 1.96*sd(op_10_5)/sqrt(length(op_10_5)), "]")
paste("UT", mean(ut_10_5), "[", 1.96*sd(ut_10_5)/sqrt(length(ut_10_5))+mean(ut_10_5), ":", mean(ut_10_5) - 1.96*sd(ut_10_5)/sqrt(length(ut_10_5)), "]")
paste("ON", mean(on_10_5), "[", 1.96*sd(on_10_5)/sqrt(length(on_10_5))+mean(on_10_5), ":", mean(on_10_5) - 1.96*sd(on_10_5)/sqrt(length(on_10_5)), "]")
paste("OV", mean(ov_10_5), "[", 1.96*sd(ov_10_5)/sqrt(length(ov_10_5))+mean(ov_10_5), ":", mean(ov_10_5) - 1.96*sd(ov_10_5)/sqrt(length(ov_10_5)), "]")

print(">>>>>>>> LUCROS 50 5%")
paste("OP", mean(op_50_5), "[", 1.96*sd(op_50_5)/sqrt(length(op_50_5))+mean(op_50_5), ":", mean(op_50_5) - 1.96*sd(op_50_5)/sqrt(length(op_50_5)), "]")
paste("UT", mean(ut_50_5), "[", 1.96*sd(ut_50_5)/sqrt(length(ut_50_5))+mean(ut_50_5), ":", mean(ut_50_5) - 1.96*sd(ut_50_5)/sqrt(length(ut_50_5)), "]")
paste("ON", mean(on_50_5), "[", 1.96*sd(on_50_5)/sqrt(length(on_50_5))+mean(on_50_5), ":", mean(on_50_5) - 1.96*sd(on_50_5)/sqrt(length(on_50_5)), "]")
paste("OV", mean(ov_50_5), "[", 1.96*sd(ov_50_5)/sqrt(length(ov_50_5))+mean(ov_50_5), ":", mean(ov_50_5) - 1.96*sd(ov_50_5)/sqrt(length(ov_50_5)), "]")




#>>>>>>>>> Ganhos para risco de 10%

gop_100_10 <- (op_100_10 - on_100_10) / abs(on_100_10)
gut_100_10 <- (ut_100_10 - on_100_10) / abs(on_100_10)
gov_100_10 <- (ov_100_10 - on_100_10) / abs(on_100_10)

print(">>>> GANHOS 100 10%")
paste("Ganhos hi --> on", mean(ut_100_10 - on_100_10), " ", mean(gut_100_10), " [", 1.96*sd(gut_100_10)/sqrt(length(gut_100_10))+mean(gut_100_10), ":",  mean(gut_100_10) - 1.96*sd(gut_100_10)/sqrt(length(gut_100_10)), "]" )
print(paste(gut_100_10))

paste("Ganhos op --> on", mean(op_100_10 - on_100_10), " ", mean(gop_100_10), " [", 1.96*sd(gop_100_10)/sqrt(length(gop_100_10))+mean(gop_100_10), ":",  mean(gop_100_10) - 1.96*sd(gop_100_10)/sqrt(length(gop_100_10)), "]" )
print(paste(gop_100_10))

paste("Ganhos ov --> on", mean(ov_100_10 - on_100_10), " ", mean(gov_100_10), " [", 1.96*sd(gov_100_10)/sqrt(length(gov_100_10))+mean(gov_100_10), ":",  mean(gov_100_10) - 1.96*sd(gov_100_10)/sqrt(length(gov_100_10)), "]" )
print(paste(gov_100_10))



gop_10_10 <- (op_10_10 - on_10_10) / abs(on_10_10)
gut_10_10 <- (ut_10_10 - on_10_10) / abs(on_10_10)
gov_10_10 <- (ov_10_10 - on_10_10) / abs(on_10_10)

print(">>>> GANHOS 10 10%")
paste("Ganhos hi --> on", mean(ut_10_10 - on_10_10), " ", mean(gut_10_10), " [", 1.96*sd(gut_10_10)/sqrt(length(gut_10_10))+mean(gut_10_10), ":",  mean(gut_10_10) - 1.96*sd(gut_10_10)/sqrt(length(gut_10_10)), "]" )
print(paste(gut_10_10))

paste("Ganhos op --> on", mean(op_10_10 - on_10_10), " ", mean(gop_10_10), " [", 1.96*sd(gop_10_10)/sqrt(length(gop_10_10))+mean(gop_10_10), ":",  mean(gop_10_10) - 1.96*sd(gop_10_10)/sqrt(length(gop_10_10)), "]" )
print(paste(gop_10_10))

paste("Ganhos ov --> on", mean(ov_10_10 - on_10_10), " ", mean(gov_10_10), " [", 1.96*sd(gov_10_10)/sqrt(length(gov_10_10))+mean(gov_10_10), ":",  mean(gov_10_10) - 1.96*sd(gov_10_10)/sqrt(length(gov_10_10)), "]" )
print(paste(gov_10_10))



gop_50_10 <- (op_50_10 - on_50_10) / abs(on_50_10)
gut_50_10 <- (ut_50_10 - on_50_10) / abs(on_50_10)
gov_50_10 <- (ov_50_10 - on_50_10) / abs(on_50_10)

print(">>>> GANHOS 50 10%")
paste("Ganhos hi --> on", mean(ut_50_10 - on_50_10), " ", mean(gut_50_10), " [", 1.96*sd(gut_50_10)/sqrt(length(gut_50_10))+mean(gut_50_10), ":",  mean(gut_50_10) - 1.96*sd(gut_50_10)/sqrt(length(gut_50_10)), "]" )
print(paste(gut_50_10))

paste("Ganhos op --> on", mean(op_50_10 - on_50_10), " ", mean(gop_50_10), " [", 1.96*sd(gop_50_10)/sqrt(length(gop_50_10))+mean(gop_50_10), ":",  mean(gop_50_10) - 1.96*sd(gop_50_10)/sqrt(length(gop_50_10)), "]" )
print(paste(gop_50_10))

paste("Ganhos ov --> on", mean(ov_50_10 - on_50_10), " ", mean(gov_50_10), " [", 1.96*sd(gov_50_10)/sqrt(length(gov_50_10))+mean(gov_50_10), ":",  mean(gov_50_10) - 1.96*sd(gov_50_10)/sqrt(length(gov_50_10)), "]" )
print(paste(gov_50_10))



#Ganhos para risco de 15%

gop_100_15 <- (op_100_15 - on_100_15) / abs(on_100_15)
gut_100_15 <- (ut_100_15 - on_100_15) / abs(on_100_15)
gov_100_15 <- (ov_100_15 - on_100_15) / abs(on_100_15)

print(">>>> GANHOS 100 15%")
paste("Ganhos hi --> on", mean(ut_100_15 - on_100_15), " ", mean(gut_100_15), " [", 1.96*sd(gut_100_15)/sqrt(length(gut_100_15))+mean(gut_100_15), ":",  mean(gut_100_15) - 1.96*sd(gut_100_15)/sqrt(length(gut_100_15)), "]" )
print(paste(gut_100_15))

paste("Ganhos op --> on", mean(op_100_15 - on_100_15), " ", mean(gop_100_15), " [", 1.96*sd(gop_100_15)/sqrt(length(gop_100_15))+mean(gop_100_15), ":",  mean(gop_100_15) - 1.96*sd(gop_100_15)/sqrt(length(gop_100_15)), "]" )
print(paste(gop_100_15))

paste("Ganhos ov --> on", mean(ov_100_15 - on_100_15), " ", mean(gov_100_15), " [", 1.96*sd(gov_100_15)/sqrt(length(gov_100_15))+mean(gov_100_15), ":",  mean(gov_100_15) - 1.96*sd(gov_100_15)/sqrt(length(gov_100_15)), "]" )
print(paste(gov_100_15))



gop_10_15 <- (op_10_15 - on_10_15) / abs(on_10_15)
gut_10_15 <- (ut_10_15 - on_10_15) / abs(on_10_15)
gov_10_15 <- (ov_10_15 - on_10_15) / abs(on_10_15)

print(">>>> GANHOS 10 15%")
paste("Ganhos hi --> on", mean(ut_10_15 - on_10_15), " ", mean(gut_10_15), " [", 1.96*sd(gut_10_15)/sqrt(length(gut_10_15))+mean(gut_10_15), ":",  mean(gut_10_15) - 1.96*sd(gut_10_15)/sqrt(length(gut_10_15)), "]" )
print(paste(gut_10_15))

paste("Ganhos op --> on", mean(op_10_15 - on_10_15), " ", mean(gop_10_15), " [", 1.96*sd(gop_10_15)/sqrt(length(gop_10_15))+mean(gop_10_15), ":",  mean(gop_10_15) - 1.96*sd(gop_10_15)/sqrt(length(gop_10_15)), "]" )
print(paste(gop_10_15))

paste("Ganhos ov --> on", mean(ov_10_15 - on_10_15), " ", mean(gov_10_15), " [", 1.96*sd(gov_10_15)/sqrt(length(gov_10_15))+mean(gov_10_15), ":",  mean(gov_10_15) - 1.96*sd(gov_10_15)/sqrt(length(gov_10_15)), "]" )
print(paste(gov_10_15))



gop_50_15 <- (op_50_15 - on_50_15) / abs(on_50_15)
gut_50_15 <- (ut_50_15 - on_50_15) / abs(on_50_15)
gov_50_15 <- (ov_50_15 - on_50_15) / abs(on_50_15)

print(">>>> GANHOS 50 15%")
paste("Ganhos hi --> on", mean(ut_50_15 - on_50_15), " ", mean(gut_50_15), " [", 1.96*sd(gut_50_15)/sqrt(length(gut_50_15))+mean(gut_50_15), ":",  mean(gut_50_15) - 1.96*sd(gut_50_15)/sqrt(length(gut_50_15)), "]" )
print(paste(gut_50_15))

paste("Ganhos op --> on", mean(op_50_15 - on_50_15), " ", mean(gop_50_15), " [", 1.96*sd(gop_50_15)/sqrt(length(gop_50_15))+mean(gop_50_15), ":",  mean(gop_50_15) - 1.96*sd(gop_50_15)/sqrt(length(gop_50_15)), "]" )
print(paste(gop_50_15))

paste("Ganhos ov --> on", mean(ov_50_15 - on_50_15), " ", mean(gov_50_15), " [", 1.96*sd(gov_50_15)/sqrt(length(gov_50_15))+mean(gov_50_15), ":",  mean(gov_50_15) - 1.96*sd(gov_50_15)/sqrt(length(gov_50_15)), "]" )
print(paste(gov_50_15))






#Ganhos para risco de 5%

gop_100_5 <- (op_100_5 - on_100_5) / abs(on_100_5)
gut_100_5 <- (ut_100_5 - on_100_5) / abs(on_100_5)
gov_100_5 <- (ov_100_5 - on_100_5) / abs(on_100_5)

print(">>>> GANHOS 100 5%")
paste("Ganhos hi --> on", mean(ut_100_5 - on_100_5), " ", mean(gut_100_5), " [", 1.96*sd(gut_100_5)/sqrt(length(gut_100_5))+mean(gut_100_5), ":",  mean(gut_100_5) - 1.96*sd(gut_100_5)/sqrt(length(gut_100_5)), "]" )
print(paste(gut_100_5))

paste("Ganhos op --> on", mean(op_100_5 - on_100_5), " ", mean(gop_100_5), " [", 1.96*sd(gop_100_5)/sqrt(length(gop_100_5))+mean(gop_100_5), ":",  mean(gop_100_5) - 1.96*sd(gop_100_5)/sqrt(length(gop_100_5)), "]" )
print(paste(gop_100_5))

paste("Ganhos ov --> on", mean(ov_100_5 - on_100_5), " ", mean(gov_100_5), " [", 1.96*sd(gov_100_5)/sqrt(length(gov_100_5))+mean(gov_100_5), ":",  mean(gov_100_5) - 1.96*sd(gov_100_5)/sqrt(length(gov_100_5)), "]" )
print(paste(gov_100_5))




gop_10_5 <- (op_10_5 - on_10_5) / abs(on_10_5)
gut_10_5 <- (ut_10_5 - on_10_5) / abs(on_10_5)
gov_10_5 <- (ov_10_5 - on_10_5) / abs(on_10_5)

print(">>>> GANHOS 10 5%")
paste("Ganhos hi --> on", mean(ut_10_5 - on_10_5), " ", mean(gut_10_5), " [", 1.96*sd(gut_10_5)/sqrt(length(gut_10_5))+mean(gut_10_5), ":",  mean(gut_10_5) - 1.96*sd(gut_10_5)/sqrt(length(gut_10_5)), "]" )
print(paste(gut_10_5))

paste("Ganhos op --> on", mean(op_10_5 - on_10_5), " ", mean(gop_10_5), " [", 1.96*sd(gop_10_5)/sqrt(length(gop_10_5))+mean(gop_10_5), ":",  mean(gop_10_5) - 1.96*sd(gop_10_5)/sqrt(length(gop_10_5)), "]" )
print(paste(gop_10_5))

paste("Ganhos ov --> on", mean(ov_10_5 - on_10_5), " ", mean(gov_10_5), " [", 1.96*sd(gov_10_5)/sqrt(length(gov_10_5))+mean(gov_10_5), ":",  mean(gov_10_5) - 1.96*sd(gov_10_5)/sqrt(length(gov_10_5)), "]" )
print(paste(gov_10_5))




gop_50_5 <- (op_50_5 - on_50_5) / abs(on_50_5)
gut_50_5 <- (ut_50_5 - on_50_5) / abs(on_50_5)
gov_50_5 <- (ov_50_5 - on_50_5) / abs(on_50_5)

print(">>>> GANHOS 50 5%")
paste("Ganhos hi --> on", mean(ut_50_5 - on_50_5), " ", mean(gut_50_5), " [", 1.96*sd(gut_50_5)/sqrt(length(gut_50_5))+mean(gut_50_5), ":",  mean(gut_50_5) - 1.96*sd(gut_50_5)/sqrt(length(gut_50_5)), "]" )
print(paste(gut_50_5))

paste("Ganhos op --> on", mean(op_50_5 - on_50_5), " ", mean(gop_50_5), " [", 1.96*sd(gop_50_5)/sqrt(length(gop_50_5))+mean(gop_50_5), ":",  mean(gop_50_5) - 1.96*sd(gop_50_5)/sqrt(length(gop_50_5)), "]" )
print(paste(gop_50_5))

paste("Ganhos ov --> on", mean(ov_50_5 - on_50_5), " ", mean(gov_50_5), " [", 1.96*sd(gov_50_5)/sqrt(length(gov_50_5))+mean(gov_50_5), ":",  mean(gov_50_5) - 1.96*sd(gov_50_5)/sqrt(length(gov_50_5)), "]" )
print(paste(gov_50_5))
