#!/bin/Rscript

#Testando com tres fatores para erros positivos: UT
print("@@@@@@@@@@@@@@@@ Erros positivos UT")
#ris=c(-1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1)
#err=c(-1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1)
ris=c(-1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1)
err=c(-1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1)

#usR = us*ris
#usE = us*err
rE = ris*err
#usRE = ris*err

#ut_5_1_15 <- read.table("ut_5_1_15/result.dat")$V1
#ut_5_1_40 <- read.table("ut_5_1_40/result.dat")$V1
#ut_5_15_15 <- read.table("ut_5_15_15/result.dat")$V1
#ut_5_15_40 <- read.table("ut_5_15_40/result.dat")$V1

ut_100_1_15 <- read.table("ut_100_1_15/result.dat")$V1
ut_100_1_40 <- read.table("ut_100_1_40/result.dat")$V1
ut_100_15_15 <- read.table("ut_100_15_15/result.dat")$V1
ut_100_15_40 <- read.table("ut_100_15_40/result.dat")$V1

Y = c(ut_100_1_15, ut_100_1_40, ut_100_15_15, ut_100_15_40)

g = lm(Y ~ ris + err + rE)
print(">>>> Coeficientes")
g

print(">>>> Somas de quadrados")
a = anova(g)
a

SSr = a[1,"Sum Sq"]
SSe = a[2,"Sum Sq"]
SSre = a[3,"Sum Sq"]
SSE = a[4,"Sum Sq"]

SST = SSr + SSe + SSre + SSE
print(c("Risco", SSr / SST))
print(c("Erro", SSe / SST))
print(c("Risco + Error", SSre / SST))

print(">>>> Valores significativos")
summary(g)

postscript("ut_pos_semU.ps")
plot(ut_100_1_15, ylim=c(-150000, 150000))
points(ut_100_1_40)
points(ut_100_15_15)
points(ut_100_15_40)

residuals <- c(ut_100_1_15 - mean(ut_100_1_15), ut_100_15_15 - mean(ut_100_15_15), ut_100_15_40 - mean(ut_100_15_40))

jpeg("qqnorm_ut_pos_semUs.jpg")
qqnorm( residuals )
qqline( residuals )
dev.off()

#Testando com tres fatores para erros positivos: RF
print("@@@@@@@@@@@@@@@@ Erros positivos RF")
#us=c(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
#ris=c(-1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1)
#err=c(-1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1)
ris=c(-1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1)
err=c(-1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1)

#usR = us*ris
#usE = us*err
rE = ris*err
#usRE = us*ris*err

#rf_5_1_15 <- read.table("rf_5_1_15/result.dat")$V1
#rf_5_1_40 <- read.table("rf_5_1_40/result.dat")$V1
#rf_5_15_15 <- read.table("rf_5_15_15/result.dat")$V1
#rf_5_15_40 <- read.table("rf_5_15_40/result.dat")$V1

rf_100_1_15 <- read.table("rf_100_1_15/result.dat")$V1
rf_100_1_40 <- read.table("rf_100_1_40/result.dat")$V1
rf_100_15_15 <- read.table("rf_100_15_15/result.dat")$V1
rf_100_15_40 <- read.table("rf_100_15_40/result.dat")$V1

Y = c(rf_100_1_15, rf_100_1_40, rf_100_15_15, rf_100_15_40)

g = lm(Y ~ ris + err + rE)
print(">>>> Coeficientes")
g

print(">>>> Somas de quadrados")
a = anova(g)
a

SSr = a[1,"Sum Sq"]
SSe = a[2,"Sum Sq"]
SSre = a[3,"Sum Sq"]
SSE = a[4,"Sum Sq"]

SST = SSr + SSe + SSre + SSE
print(c("Risco", SSr / SST))
print(c("Erro", SSe / SST))
print(c("Risco + Error", SSre / SST))

print(">>>> Valores significativos")
summary(g)

postscript("rf_pos_semU.ps")
plot(rf_100_1_15, ylim=c(-150000, 150000))
points(rf_100_1_40)
points(rf_100_15_15)
points(rf_100_15_40)

residuals <- c(rf_100_1_15 - mean(rf_100_1_15), rf_100_15_15 - mean(rf_100_15_15), rf_100_15_40 - mean(rf_100_15_40))

jpeg("qqnorm_rf_pos_semUs.jpg")
qqnorm( residuals )
qqline( residuals )
dev.off()

#Testando com tres fatores para erros negativos: UT
print("@@@@@@@@@@@@@@@@ Erros negativos UT")
#us=c(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
#ris=c(-1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1)
#err=c(-1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1)
ris=c(-1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1)
err=c(-1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1)

#usR = us*ris
#usE = us*err
rE = ris*err
#usRE = us*ris*err

#ut_5_1_m40 <- read.table("ut_5_1_m40/result.dat")$V1
#ut_5_1_m15 <- read.table("ut_5_1_m15/result.dat")$V1
#ut_5_15_m40 <- read.table("ut_5_15_m40/result.dat")$V1
#ut_5_15_m15 <- read.table("ut_5_15_m15/result.dat")$V1

ut_100_1_m40 <- read.table("ut_100_1_m40/result.dat")$V1
ut_100_1_m15 <- read.table("ut_100_1_m15/result.dat")$V1
ut_100_15_m40 <- read.table("ut_100_15_m40/result.dat")$V1
ut_100_15_m15 <- read.table("ut_100_15_m15/result.dat")$V1

Y = c(ut_100_1_m40, ut_100_1_m15, ut_100_15_m40, ut_100_15_m15)

g = lm(Y ~ ris + err + rE )
print(">>>> Coeficientes")
g

print(">>>> Somas de quadrados")
a = anova(g)
a

SSr = a[1,"Sum Sq"]
SSe = a[2,"Sum Sq"]
SSre = a[3,"Sum Sq"]
SSE = a[4,"Sum Sq"]

SST = SSr + SSe + SSre + SSE
print(c("Risco", SSr / SST))
print(c("Erro", SSe / SST))
print(c("Risco + Error", SSre / SST))

print(">>>> Valores significativos")
summary(g)

postscript("ut_neg_semU.ps")
plot(ut_100_1_m15, ylim=c(-150000, 150000))
points(ut_100_1_m40)
points(ut_100_15_m15)
points(ut_100_15_m40)

residuals <- c(ut_100_1_m15 - mean(ut_100_1_m15), ut_100_15_m15 - mean(ut_100_15_m15), ut_100_15_m40 - mean(ut_100_15_m40))

jpeg("qqnorm_ut_neg_semUs.jpg")
qqnorm( residuals )
qqline( residuals )
dev.off()

#Testando com tres fatores para erros negativos: RF
print("@@@@@@@@@@@@@@@@ Erros negativos RF")
#us=c(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
#ris=c(-1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1)
#err=c(-1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1)
ris=c(-1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1)
err=c(-1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1)

#usR = us*ris
#usE = us*err
rE = ris*err
#usRE = us*ris*err

#rf_5_1_m40 <- read.table("rf_5_1_m40/result.dat")$V1
#rf_5_1_m15 <- read.table("rf_5_1_m15/result.dat")$V1
#rf_5_15_m40 <- read.table("rf_5_15_m40/result.dat")$V1
#rf_5_15_m15 <- read.table("rf_5_15_m15/result.dat")$V1

rf_100_1_m40 <- read.table("rf_100_1_m40/result.dat")$V1
rf_100_1_m15 <- read.table("rf_100_1_m15/result.dat")$V1
rf_100_15_m40 <- read.table("rf_100_15_m40/result.dat")$V1
rf_100_15_m15 <- read.table("rf_100_15_m15/result.dat")$V1

Y = c(rf_100_1_m40, rf_100_1_m15, rf_100_15_m40, rf_100_15_m15)

g = lm(Y ~ ris + err + rE)
print(">>>> Coeficientes")
g

print(">>>> Somas de quadrados")
a = anova(g)
a

SSr = a[1,"Sum Sq"]
SSe = a[2,"Sum Sq"]
SSre = a[3,"Sum Sq"]
SSE = a[4,"Sum Sq"]

SST = SSr + SSe + SSre + SSE
print(c("Risco", SSr / SST))
print(c("Erro", SSe / SST))
print(c("Risco + Error", SSre / SST))

print(">>>> Valores significativos")
summary(g)

postscript("rf_neg_semU.ps")
plot(rf_100_1_m15, ylim=c(-150000, 150000))
points(rf_100_1_m40)
points(rf_100_15_m15)
points(rf_100_15_m40)

residuals <- c(rf_100_1_m15 - mean(rf_100_1_m15), rf_100_15_m15 - mean(rf_100_15_m15), rf_100_15_m40 - mean(rf_100_15_m40))

jpeg("qqnorm_rf_neg_semUs.jpg")
qqnorm( residuals )
qqline( residuals )
dev.off()

