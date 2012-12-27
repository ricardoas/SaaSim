#!/usr/bin/Rscript

#Different machine
l_5000_inf_t1 <- read.table("100_360_OPTIMAL.output")$V1
l_5000_inf_t2 <- read.table("LINEAR_5000_430_t2")$V1

l_5000_20_t1 <- read.table("LINEAR_5000_20_t1")$V1
l_5000_20_t2 <- read.table("LINEAR_5000_20_t2")$V1

e_5000_inf_t1 <- read.table("EXPO_5000_430_t1")$V1
e_5000_inf_t2 <- read.table("EXPO_5000_430_t2")$V1

e_5000_20_t1 <- read.table("EXPO_5000_20_t1")$V1
e_5000_20_t2 <- read.table("EXPO_5000_20_t2")$V1

#Utility 50
l_50_inf_t1 <- read.table("LINEAR_50_430_t1")$V1
l_50_inf_t2 <- read.table("LINEAR_50_430_t2")$V1

l_50_20_t1 <- read.table("LINEAR_50_20_t1")$V1
l_50_20_t2 <- read.table("LINEAR_50_20_t2")$V1

e_50_inf_t1 <- read.table("EXPO_50_430_t1")$V1
e_50_inf_t2 <- read.table("EXPO_50_430_t2")$V1

e_50_20_t1 <- read.table("EXPO_50_20_t1")$V1
e_50_20_t2 <- read.table("EXPO_50_20_t2")$V1


#Factors
q0 <- c(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
a <- c(-1, -1, -1, -1, 1, 1, 1, 1, -1, -1, -1, -1, 1, 1, 1, 1)#Machines
b <- c(-1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1)#Utility type
c <- c(-1 ,-1, 1, 1, 1, 1, -1, -1, -1, -1, 1, 1, 1, 1, -1, -1)#Max Utility
d <- c(-1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1)#App

#Variables (x1:machines; x2:utility type; x3:max utility; x4: app) Means
#Linear
mean_m1_m1_m1_m1 = mean(l_50_20_t1)
mean_m1_m1_m1_1 = mean(l_50_20_t2)

mean_m1_m1_1_m1 = mean(l_5000_20_t1)
mean_m1_m1_1_1 = mean(l_5000_20_t2)

mean_1_m1_1_m1 = mean(l_5000_inf_t1)
mean_1_m1_1_1 = mean(l_5000_inf_t2)

mean_1_m1_m1_m1 = mean(l_50_inf_t1)
mean_1_m1_m1_1 = mean(l_50_inf_t2)

#Expo
mean_m1_1_m1_m1 = mean(e_50_20_t1)
mean_m1_1_m1_1 = mean(e_50_20_t2)

mean_m1_1_1_m1 = mean(e_5000_20_t1)
mean_m1_1_1_1 = mean(e_5000_20_t2)

mean_1_1_1_m1 = mean(e_5000_inf_t1)
mean_1_1_1_1 = mean(e_5000_inf_t2)

mean_1_1_m1_m1 = mean(e_50_inf_t1)
mean_1_1_m1_1 = mean(e_50_inf_t2)

result <- c(mean_m1_m1_m1_m1, mean_m1_m1_m1_1, mean_m1_m1_1_m1, mean_m1_m1_1_1, mean_1_m1_1_m1, mean_1_m1_1_1, mean_1_m1_m1_m1, mean_1_m1_m1_1, mean_m1_1_m1_m1, mean_m1_1_m1_1, mean_m1_1_1_m1, mean_m1_1_1_1, mean_1_1_1_m1, mean_1_1_1_1, mean_1_1_m1_m1, mean_1_1_m1_1)

#Residuals calculus
residuals <- c(l_50_20_t1 - mean_m1_m1_m1_m1, l_50_20_t2 - mean_m1_m1_m1_1, l_5000_20_t1 - mean_m1_m1_1_m1, l_5000_20_t2 - mean_m1_m1_1_1, l_5000_inf_t1 - mean_1_m1_1_m1, l_5000_inf_t2 - mean_1_m1_1_1, l_50_inf_t1 - mean_1_m1_m1_m1, l_50_inf_t2 - mean_1_m1_m1_1, e_50_20_t1 - mean_m1_1_m1_m1, e_50_20_t2 - mean_m1_1_m1_1, e_5000_20_t1 - mean_m1_1_1_m1,  e_5000_20_t2 - mean_m1_1_1_1, e_5000_inf_t1 - mean_1_1_1_m1, e_5000_inf_t2 - mean_1_1_1_1, e_50_inf_t1 - mean_1_1_m1_m1, e_50_inf_t2 - mean_1_1_m1_1)

jpeg("boxplots.jpg")
par(mfrow=c(4,4))
boxplot(l_50_20_t1)
boxplot(l_50_20_t2)
boxplot(l_5000_20_t1)
boxplot(l_5000_20_t2)
boxplot(l_5000_inf_t1)
boxplot(l_5000_inf_t2)
boxplot(l_50_inf_t1)
boxplot(l_50_inf_t2)
boxplot(e_50_20_t1)
boxplot(e_50_20_t2)
boxplot(e_5000_20_t1)
boxplot(e_5000_20_t2)
boxplot(e_5000_inf_t1)
boxplot(e_5000_inf_t2)
boxplot(e_50_inf_t1)
boxplot(e_50_inf_t2)
dev.off()

#Residuals
jpeg("residuals.jpg")
x <- c(array(mean_m1_m1_m1_m1, 5), array(mean_m1_m1_m1_1, 5), array(mean_m1_m1_1_m1, 5), array(mean_m1_m1_1_1, 5), array(mean_1_m1_1_m1, 5), array(mean_1_m1_1_1, 5), array(mean_1_m1_m1_m1, 5), array(mean_1_m1_m1_1, 5), array(mean_m1_1_m1_m1, 5), array(mean_m1_1_m1_1, 5), array(mean_m1_1_1_m1, 5), array(mean_m1_1_1_1, 5), array(mean_1_1_1_m1, 5), array(mean_1_1_1_1, 5), array(mean_1_1_m1_m1, 5), array(mean_1_1_m1_1, 5))
print(residuals)

plot(x, residuals)
dev.off()

jpeg("qqnorm.jpg")
qqnorm( (residuals - mean(residuals))/sd(residuals) )
qqline( (residuals - mean(residuals))/sd(residuals) )
dev.off()


#Finding coefficients
s_q0 <- sum(q0 * result) / (2**4)

qa <- sum(a * result) / (2**4)
qb <- sum(b * result) / (2**4)
qc <- sum(c * result) / (2**4)
qd <- sum(d * result) / (2**4)

qab <- sum(a * b * result) / (2**4)
qac <- sum(a * c * result) / (2**4)
qad <- sum(a * d * result) / (2**4)
qbc <- sum(b * c * result) / (2**4)
qbd <- sum(b * d * result) / (2**4)
qcd <- sum(c * d * result) / (2**4)

qabc <- sum(a * b * c * result) / (2**4)
qabd <- sum(a * b * d * result) / (2**4)
qacd <- sum(a * c * d * result) / (2**4)
qbcd <- sum(b * c * d * result) / (2**4)

qabcd <- sum(a * b * c * d * result) / (2**4)

#Sum of squares
SS0 = (2**4) * 5 * (s_q0**2)
SSY = sum(l_5000_inf_t1**2, l_5000_inf_t2**2, l_5000_20_t1**2, l_5000_20_t2**2, e_5000_inf_t1**2, e_5000_inf_t2**2, e_5000_20_t1**2, e_5000_20_t2**2, l_50_inf_t1**2, l_50_inf_t2**2, l_50_20_t1**2, l_50_20_t2**2, e_50_inf_t1**2, e_50_inf_t2**2, e_50_20_t1**2, e_50_20_t2**2)

SSA = (2**4) * 5 * (qa**2)
SSB = (2**4) * 5 * (qb**2)
SSC = (2**4) * 5 * (qc**2)
SSD = (2**4) * 5 * (qd**2)

SSAB = (2**4) * 5 * (qab**2)
SSAC = (2**4) * 5 * (qac**2)
SSAD = (2**4) * 5 * (qad**2)
SSBC = (2**4) * 5 * (qbc**2)
SSBD = (2**4) * 5 * (qbd**2)
SSCD = (2**4) * 5 * (qcd**2)

SSABC = (2**4) * 5 * (qabc**2)
SSABD = (2**4) * 5 * (qabd**2)
SSACD = (2**4) * 5 * (qacd**2)
SSBCD = (2**4) * 5 * (qbcd**2)

SSABCD = (2**4) * 5 * (qabcd**2)

#Total Variation
#SST = sum(SSA, SSB, SSC, SSD, SSAB, SSAC, SSAD, SSBC, SSBD, SSCD, SSABC, SSABD, SSACD, SSBCD, SSABCD, SSE)
SST = SSY - SS0
SSE = SST - (SSA + SSB + SSC + SSD + SSAB + SSAC + SSAD + SSBC + SSBD + SSCD + SSABC + SSABD + SSACD + SSBCD + SSABCD)
SSR = SST - SSE

#Percentual values for variations
sink("variation.dat")

cat("SSE/SST", SSE/SST, "\n")
cat("SSA/SST", SSA/SST, "\n")
cat("SSB/SST", SSB/SST, "\n")
cat("SSC/SST", SSC/SST, "\n")
cat("SSD/SST", SSD/SST, "\n")

cat("SSAB/SST", SSAB/SST, "\n")
cat("SSAC/SST", SSAC/SST, "\n")
cat("SSAD/SST", SSAD/SST, "\n")
cat("SSBC/SST", SSBC/SST, "\n")
cat("SSBD/SST", SSBD/SST, "\n")
cat("SSCD/SST", SSCD/SST, "\n")

cat("SSABC/SST", SSABC/SST, "\n")
cat("SSABD/SST", SSABD/SST, "\n")
cat("SSACD/SST", SSACD/SST, "\n")
cat("SSBCD/SST", SSBCD/SST, "\n")

cat("SSABCCD/SST", SSABCD/SST, "\n")
cat("SSR/SST", SSR/SST, "\n")

cat("F-value",  (SSR/(63)) / (SSE/((2**4)*(4))), "\n" )

#Confidence interval for each coefficient with confidence level = 95%
sink("coefficients_095.dat")
se = sqrt( SSE / ((2**4)*(5-1)) )
sqi = se / sqrt((2**4)*5)

dg = (2**4)*(5-1)#error degrees of freedom
alpha = 0.05
t = qt(1 - alpha/2, df=dg)

cat("q0 q0+ts q0-ts", s_q0, s_q0 + t*sqi, s_q0 - t*sqi, "\n")
cat("qa qa+ts qa-ts", qa, qa + t*sqi, qa - t*sqi, "\n")
cat("qb qb+ts qb-ts", qb, qb + t*sqi, qb - t*sqi, "\n")
cat("qc qc+ts qc-ts", qc, qc + t*sqi, qc - t*sqi, "\n")
cat("qd qd+ts qd-ts", qd, qd + t*sqi, qd - t*sqi, "\n")

cat("qab qab+ts qab-ts", qab, qab + t*sqi, qab - t*sqi, "\n")
cat("qac qac+ts qac-ts", qac, qac + t*sqi, qac - t*sqi, "\n")
cat("qad qad+ts qad-ts", qad, qad + t*sqi, qad - t*sqi, "\n")
cat("qbc qbc+ts qbc-ts", qbc, qbc + t*sqi, qbc - t*sqi, "\n")
cat("qbd qbd+ts qbd-ts", qbd, qbd + t*sqi, qbd - t*sqi, "\n")
cat("qcd qcd+ts qcd-ts", qcd, qcd + t*sqi, qcd - t*sqi, "\n")

cat("qabc qabc+ts qabc-ts", qabc, qabc + t*sqi, qabc - t*sqi, "\n")
cat("qabd qabd+ts qabd-ts", qabd, qabd + t*sqi, qabd - t*sqi, "\n")
cat("qacd qacd+ts qacd-ts", qacd, qacd + t*sqi, qacd - t*sqi, "\n")
cat("qbcd qbcd+ts qbcd-ts", qbcd, qbcd + t*sqi, qbcd - t*sqi, "\n")

cat("qabcd qabcd+ts qabcd-ts", qabcd, qabcd + t*sqi, qabcd - t*sqi, "\n")

#Confidence interval for each coefficient with confidence level = 90%
sink("coefficients_090.dat")
se = sqrt( SSE / ((2**4)*(5-1)) )
sqi = se / sqrt((2**4)*5)

dg = (2**4)*(5-1)#error degrees of freedom
alpha = 0.1
t = qt(1 - alpha/2, df=dg)

cat("q0 q0+ts q0-ts", s_q0, s_q0 + t*sqi, s_q0 - t*sqi, "\n")
cat("qa qa+ts qa-ts", qa, qa + t*sqi, qa - t*sqi, "\n")
cat("qb qb+ts qb-ts", qb, qb + t*sqi, qb - t*sqi, "\n")
cat("qc qc+ts qc-ts", qc, qc + t*sqi, qc - t*sqi, "\n")
cat("qd qd+ts qd-ts", qd, qd + t*sqi, qd - t*sqi, "\n")

cat("qab qab+ts qab-ts", qab, qab + t*sqi, qab - t*sqi, "\n")
cat("qac qac+ts qac-ts", qac, qac + t*sqi, qac - t*sqi, "\n")
cat("qad qad+ts qad-ts", qad, qad + t*sqi, qad - t*sqi, "\n")
cat("qbc qbc+ts qbc-ts", qbc, qbc + t*sqi, qbc - t*sqi, "\n")
cat("qbd qbd+ts qbd-ts", qbd, qbd + t*sqi, qbd - t*sqi, "\n")
cat("qcd qcd+ts qcd-ts", qcd, qcd + t*sqi, qcd - t*sqi, "\n")

cat("qabc qabc+ts qabc-ts", qabc, qabc + t*sqi, qabc - t*sqi, "\n")
cat("qabd qabd+ts qabd-ts", qabd, qabd + t*sqi, qabd - t*sqi, "\n")
cat("qacd qacd+ts qacd-ts", qacd, qacd + t*sqi, qacd - t*sqi, "\n")
cat("qbcd qbcd+ts qbcd-ts", qbcd, qbcd + t*sqi, qbcd - t*sqi, "\n")

cat("qabcd qabcd+ts qabcd-ts", qabcd, qabcd + t*sqi, qabcd - t*sqi, "\n")

#Kruskal-Wallis for each factor

#Max Utility
sink("mann-whitney.dat")

cat("Max Utility: 50 x 5000")
ut_5000 <- c(l_5000_inf_t1, l_5000_inf_t2, l_5000_20_t1, l_5000_20_t2, e_5000_inf_t1, e_5000_inf_t2, e_5000_20_t1, e_5000_20_t2)
ut_50 <- c(l_50_inf_t1, l_50_inf_t2, l_50_20_t1, l_50_20_t2, e_50_inf_t1, e_50_inf_t2, e_50_20_t1, e_50_20_t2)
wilcox.test(ut_50, ut_5000, alternative="g")
wilcox.test(ut_50, ut_5000, alternative="l")

cat("Utility Type: Linear x Expo")
ut_e <- c(e_5000_inf_t1, e_5000_inf_t2, e_5000_20_t1, e_5000_20_t2, e_50_inf_t1, e_50_inf_t2, e_50_20_t1, e_50_20_t2)
ut_l <- c(l_5000_inf_t1, l_5000_inf_t2, l_5000_20_t1, l_5000_20_t2, l_50_inf_t1, l_50_inf_t2, l_50_20_t1, l_50_20_t2)
wilcox.test(ut_l, ut_e, alternative="g")
wilcox.test(ut_l, ut_e, alternative="l")

cat("N_MAX: 20 x 430")
ut_20 <- c(e_5000_20_t1, e_5000_20_t2, e_50_20_t1, e_50_20_t2, l_5000_20_t1, l_5000_20_t2, l_50_20_t1, l_50_20_t2)
ut_430 <- c(e_5000_inf_t1, e_5000_inf_t2, e_50_inf_t1, e_50_inf_t2, l_5000_inf_t1, l_5000_inf_t2, l_50_inf_t1, l_50_inf_t2)
wilcox.test(ut_20, ut_430, alternative="g")
wilcox.test(ut_20, ut_430, alternative="l")


cat("App: App1 x App2")
ut_1 <- c(e_5000_20_t1, e_50_20_t1, l_5000_20_t1, l_50_20_t1, e_5000_inf_t1, e_50_inf_t1, l_5000_inf_t1, l_50_inf_t1)
ut_2 <- c(e_5000_20_t2, e_50_20_t2, l_5000_20_t2, l_50_20_t2, e_5000_inf_t2, e_50_inf_t2, l_5000_inf_t2, l_50_inf_t2)
wilcox.test(ut_1, ut_2, alternative="g")
wilcox.test(ut_1, ut_2, alternative="l")


