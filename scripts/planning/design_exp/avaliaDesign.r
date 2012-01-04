#!/bin/Rscript

#Testando com tres fatores para erros positivos: UT
print("@@@@@@@@@@@@@@@@ Erros positivos UT")
us=c(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
ris=c(-1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1)
err=c(-1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1)

usR = us*ris
usE = us*err
rE = ris*err
usRE = us*ris*err

ut_5_1_15 <- read.table("ut_5_1_15/result.dat")$V1
ut_5_1_40 <- read.table("ut_5_1_40/result.dat")$V1
ut_5_15_15 <- read.table("ut_5_15_15/result.dat")$V1
ut_5_15_40 <- read.table("ut_5_15_40/result.dat")$V1

ut_100_1_15 <- read.table("ut_100_1_15/result.dat")$V1
ut_100_1_40 <- read.table("ut_100_1_40/result.dat")$V1
ut_100_15_15 <- read.table("ut_100_15_15/result.dat")$V1
ut_100_15_40 <- read.table("ut_100_15_40/result.dat")$V1

Y = c(ut_5_1_15, ut_5_1_40, ut_5_15_15, ut_5_15_40, ut_100_1_15, ut_100_1_40, ut_100_15_15, ut_100_15_40)

g = lm(Y ~ us + ris + err + usR + usE + rE + usRE)
print(">>>> Coeficientes")
g

print(">>>> Somas de quadrados")
a = anova(g)
a

SSu = a[1,"Sum Sq"]
SSr = a[2,"Sum Sq"]
SSe = a[3,"Sum Sq"]
SSur = a[4,"Sum Sq"]
SSue = a[5,"Sum Sq"]
SSre = a[6,"Sum Sq"]
SSure = a[7,"Sum Sq"]
SSE = a[8,"Sum Sq"]

SST = SSu + SSr + SSe + SSur + SSue + SSre + SSure + SSE
print(c("Us", SSu / SST))
print(c("Risco", SSr / SST))
print(c("Us + Risco", SSur / SST))

print(">>>> Valores significativos")
summary(g)

#Testando com tres fatores para erros positivos: RF
print("@@@@@@@@@@@@@@@@ Erros positivos RF")
us=c(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
ris=c(-1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1)
err=c(-1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1)

usR = us*ris
usE = us*err
rE = ris*err
usRE = us*ris*err

rf_5_1_15 <- read.table("rf_5_1_15/result.dat")$V1
rf_5_1_40 <- read.table("rf_5_1_40/result.dat")$V1
rf_5_15_15 <- read.table("rf_5_15_15/result.dat")$V1
rf_5_15_40 <- read.table("rf_5_15_40/result.dat")$V1

rf_100_1_15 <- read.table("rf_100_1_15/result.dat")$V1
rf_100_1_40 <- read.table("rf_100_1_40/result.dat")$V1
rf_100_15_15 <- read.table("rf_100_15_15/result.dat")$V1
rf_100_15_40 <- read.table("rf_100_15_40/result.dat")$V1

Y = c(rf_5_1_15, rf_5_1_40, rf_5_15_15, rf_5_15_40, rf_100_1_15, rf_100_1_40, rf_100_15_15, rf_100_15_40)

g = lm(Y ~ us + ris + err + usR + usE + rE + usRE)
print(">>>> Coeficientes")
g

print(">>>> Somas de quadrados")
a = anova(g)
a

SSu = a[1,"Sum Sq"]
SSr = a[2,"Sum Sq"]
SSe = a[3,"Sum Sq"]
SSur = a[4,"Sum Sq"]
SSue = a[5,"Sum Sq"]
SSre = a[6,"Sum Sq"]
SSure = a[7,"Sum Sq"]
SSE = a[8,"Sum Sq"]

SST = SSu + SSr + SSe + SSur + SSue + SSre + SSure + SSE
print(c("Us", SSu / SST))
print(c("Risco", SSr / SST))
print(c("Us + Risco", SSur / SST))

print(">>>> Valores significativos")
summary(g)

#Testando com tres fatores para erros negativos: UT
print("@@@@@@@@@@@@@@@@ Erros negativos UT")
us=c(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
ris=c(-1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1)
err=c(-1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1)

usR = us*ris
usE = us*err
rE = ris*err
usRE = us*ris*err

ut_5_1_m40 <- read.table("ut_5_1_m40/result.dat")$V1
ut_5_1_m15 <- read.table("ut_5_1_m15/result.dat")$V1
ut_5_15_m40 <- read.table("ut_5_15_m40/result.dat")$V1
ut_5_15_m15 <- read.table("ut_5_15_m15/result.dat")$V1

ut_100_1_m40 <- read.table("ut_100_1_m40/result.dat")$V1
ut_100_1_m15 <- read.table("ut_100_1_m15/result.dat")$V1
ut_100_15_m40 <- read.table("ut_100_15_m40/result.dat")$V1
ut_100_15_m15 <- read.table("ut_100_15_m15/result.dat")$V1

Y = c(ut_5_1_m40, ut_5_1_m15, ut_5_15_m40, ut_5_15_m15, ut_100_1_m40, ut_100_1_m15, ut_100_15_m40, ut_100_15_m15)

g = lm(Y ~ us + ris + err + usR + usE + rE + usRE)
print(">>>> Coeficientes")
g

print(">>>> Somas de quadrados")
a = anova(g)
a

SSu = a[1,"Sum Sq"]
SSr = a[2,"Sum Sq"]
SSe = a[3,"Sum Sq"]
SSur = a[4,"Sum Sq"]
SSue = a[5,"Sum Sq"]
SSre = a[6,"Sum Sq"]
SSure = a[7,"Sum Sq"]
SSE = a[8,"Sum Sq"]

SST = SSu + SSr + SSe + SSur + SSue + SSre + SSure + SSE
print(c("Us", SSu / SST))
print(c("Risco", SSr / SST))
print(c("Us + Risco", SSur / SST))

print(">>>> Valores significativos")
summary(g)

#Testando com tres fatores para erros negativos: RF
print("@@@@@@@@@@@@@@@@ Erros negativos RF")
us=c(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
ris=c(-1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1)
err=c(-1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1)

usR = us*ris
usE = us*err
rE = ris*err
usRE = us*ris*err

rf_5_1_m40 <- read.table("rf_5_1_m40/result.dat")$V1
rf_5_1_m15 <- read.table("rf_5_1_m15/result.dat")$V1
rf_5_15_m40 <- read.table("rf_5_15_m40/result.dat")$V1
rf_5_15_m15 <- read.table("rf_5_15_m15/result.dat")$V1

rf_100_1_m40 <- read.table("rf_100_1_m40/result.dat")$V1
rf_100_1_m15 <- read.table("rf_100_1_m15/result.dat")$V1
rf_100_15_m40 <- read.table("rf_100_15_m40/result.dat")$V1
rf_100_15_m15 <- read.table("rf_100_15_m15/result.dat")$V1

Y = c(rf_5_1_m40, rf_5_1_m15, rf_5_15_m40, rf_5_15_m15, rf_100_1_m40, rf_100_1_m15, rf_100_15_m40, rf_100_15_m15)

g = lm(Y ~ us + ris + err + usR + usE + rE + usRE)
print(">>>> Coeficientes")
g

print(">>>> Somas de quadrados")
a = anova(g)
a

SSu = a[1,"Sum Sq"]
SSr = a[2,"Sum Sq"]
SSe = a[3,"Sum Sq"]
SSur = a[4,"Sum Sq"]
SSue = a[5,"Sum Sq"]
SSre = a[6,"Sum Sq"]
SSure = a[7,"Sum Sq"]
SSE = a[8,"Sum Sq"]

SST = SSu + SSr + SSe + SSur + SSue + SSre + SSure + SSE
print(c("Us", SSu / SST))
print(c("Risco", SSr / SST))
print(c("Us + Risco", SSur / SST))

print(">>>> Valores significativos")
summary(g)

