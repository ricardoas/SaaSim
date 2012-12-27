#!/bin/Rscript

#Testando ganho com quatro fatores
print("@@@@@@@@@@@@@@@@ UT")
us=c(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1) 
ris=c(-1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1)
sin=c(1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, -1)
err=c(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1,  1, 1, 1, 1, 1, 1, 1, 1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1,  1, 1, 1, 1, 1, 1, 1, 1, 1)

usR = us*ris
usE = us*err
usSin = us*sin
rE = ris*err
rSin = ris*sin
eSin = err*sin

usRE = us*ris*err
usRS = us*ris*sin
usES = us*err*sin
rES = ris*err*sin

usRES = us*ris*err*sin

ut_5_1_15 <- read.table("ut_5_1_15/result.dat")$V1
ut_5_1_m15 <- read.table("ut_5_1_m15/result.dat")$V1
ut_5_15_15 <- read.table("ut_5_15_15/result.dat")$V1
ut_5_15_m15 <- read.table("ut_5_15_m15/result.dat")$V1
ut_5_1_40 <- read.table("ut_5_1_40/result.dat")$V1
ut_5_1_m40 <- read.table("ut_5_1_m40/result.dat")$V1
ut_5_15_40 <- read.table("ut_5_15_40/result.dat")$V1
ut_5_15_m40 <- read.table("ut_5_15_m40/result.dat")$V1

ut_100_1_15 <- read.table("ut_100_1_15/result.dat")$V1
ut_100_1_m15 <- read.table("ut_100_1_m15/result.dat")$V1
ut_100_15_15 <- read.table("ut_100_15_15/result.dat")$V1
ut_100_15_m15 <- read.table("ut_100_15_m15/result.dat")$V1
ut_100_1_40 <- read.table("ut_100_1_40/result.dat")$V1
ut_100_1_m40 <- read.table("ut_100_1_m40/result.dat")$V1
ut_100_15_40 <- read.table("ut_100_15_40/result.dat")$V1
ut_100_15_m40 <- read.table("ut_100_15_m40/result.dat")$V1

rf_5_1_15 <- read.table("rf_5_1_15/result.dat")$V1
rf_5_1_m15 <- read.table("rf_5_1_m15/result.dat")$V1
rf_5_15_15 <- read.table("rf_5_15_15/result.dat")$V1
rf_5_15_m15 <- read.table("rf_5_15_m15/result.dat")$V1
rf_5_1_40 <- read.table("rf_5_1_40/result.dat")$V1
rf_5_1_m40 <- read.table("rf_5_1_m40/result.dat")$V1
rf_5_15_40 <- read.table("rf_5_15_40/result.dat")$V1
rf_5_15_m40 <- read.table("rf_5_15_m40/result.dat")$V1

rf_100_1_15 <- read.table("rf_100_1_15/result.dat")$V1
rf_100_1_m15 <- read.table("rf_100_1_m15/result.dat")$V1
rf_100_15_15 <- read.table("rf_100_15_15/result.dat")$V1
rf_100_15_m15 <- read.table("rf_100_15_m15/result.dat")$V1
rf_100_1_40 <- read.table("rf_100_1_40/result.dat")$V1
rf_100_1_m40 <- read.table("rf_100_1_m40/result.dat")$V1
rf_100_15_40 <- read.table("rf_100_15_40/result.dat")$V1
rf_100_15_m40 <- read.table("rf_100_15_m40/result.dat")$V1

g_5_1_15 = (rf_5_1_15 - ut_5_1_15) / abs(ut_5_1_15)
g_5_1_m15 = (rf_5_1_m15 - ut_5_1_m15) / abs(ut_5_1_m15)
g_5_15_15 = (rf_5_15_15 - ut_5_15_15) / abs(ut_5_15_15)
g_5_15_m15 = (rf_5_15_m15 - ut_5_15_m15) / abs(ut_5_15_m15)
g_5_1_40 = (rf_5_1_40 - ut_5_1_40) / abs(ut_5_1_40)
g_5_1_m40 = (rf_5_1_m40 - ut_5_1_m40) / abs(ut_5_1_m40)
g_5_15_40 = (rf_5_15_40 - ut_5_15_40) / abs(ut_5_15_40)
g_5_15_m40 = (rf_5_15_m40 - ut_5_15_m40) / abs(ut_5_15_m40)
g_100_1_15 = (rf_100_1_15 - ut_100_1_15) / abs(ut_100_1_15)
g_100_1_m15 = (rf_100_1_m15 - ut_100_1_m15) / abs(ut_100_1_m15)
g_100_15_15 = (rf_100_15_15 - ut_100_15_15) / abs(ut_100_15_15)
g_100_15_m15 = (rf_100_15_m15 - ut_100_15_m15) / abs(ut_100_15_m15)
g_100_1_40 = (rf_100_1_40 - ut_100_1_40) / abs(ut_100_1_40)
g_100_1_m40 = (rf_100_1_m40 - ut_100_1_m40) / abs(ut_100_1_m40)
g_100_15_40 = (rf_100_15_40 - ut_100_15_40) / abs(ut_100_15_40)
g_100_15_m40 = (rf_100_15_m40 - ut_100_15_m40) / abs(ut_100_15_m40)

Y = c(g_5_1_15, g_5_1_m15, g_5_15_15, g_5_15_m15, g_5_1_40, g_5_1_m40, g_5_15_40, g_5_15_m40, g_100_1_15, g_100_1_m15, g_100_15_15, g_100_15_m15, g_100_1_40, g_100_1_m40, g_100_15_40, g_100_15_m40)

g = lm(Y ~ us + ris + sin + err + usR + usE + usSin + rE + rSin + eSin + usRE + usRS + usES + rES + usRES)

print(">>>> Coeficientes")
g

print(">>>> Somas de quadrados")
a = anova(g)
a

SSu = a[1,"Sum Sq"]
SSr = a[2,"Sum Sq"]
SSsin = a[3,"Sum Sq"]
SSer = a[3,"Sum Sq"]
SSur = a[4,"Sum Sq"]
SSue = a[5,"Sum Sq"]
SSus = a[6,"Sum Sq"]
SSre = a[7,"Sum Sq"]
SSrs = a[8,"Sum Sq"]
SSes = a[9,"Sum Sq"]
SSure = a[10,"Sum Sq"]
SSurs = a[11,"Sum Sq"]
SSues = a[12,"Sum Sq"]
SSres = a[13,"Sum Sq"]
SSures = a[14,"Sum Sq"]
SSE = a[15,"Sum Sq"]

SST = SSu + SSr + SSsin + SSer + SSur + SSue + SSus + SSre + SSrs + SSes + SSure + SSurs + SSues + SSres + SSures + SSE
print(c("Us", SSu / SST))
print(c("Risco", SSr / SST))
print(c("Us + Risco", SSur / SST))
print(c("Us + Sinal", SSus / SST))
print(c("Risco + Sinal", SSrs / SST))
print(c("Us + Risco + Sinal", SSurs / SST))

print(">>>> Valores significativos")
summary(g)


sink("ganho.dat")
print(Y)

postscript("ganho.ps")
plot(Y, ylim=c(-150000, 150000))

residuals <- c(g_5_1_15 - mean(g_5_1_15), g_5_1_m15 - mean(g_5_1_m15), g_5_15_15 - mean(g_5_15_15), g_5_15_m15 - mean(g_5_15_m15), g_5_1_40 - mean(g_5_1_40), g_5_1_m40 - mean( g_5_1_m40), g_5_15_40 - mean(g_5_15_40), g_5_15_m40 - mean(g_5_15_m40), g_100_1_15 - mean(g_100_1_15), g_100_1_m15 - mean(g_100_1_m15), g_100_15_15 - mean(g_100_15_15), g_100_15_m15 - mean(g_100_15_m15), g_100_1_40 - mean(g_100_1_40), g_100_1_m40 - mean(g_100_1_m40), g_100_15_40 - mean(g_100_15_40), g_100_15_m40 - mean(g_100_15_40))

jpeg("qqnorm_ganhos.jpg")
qqnorm( residuals )
qqline( residuals )
dev.off()
