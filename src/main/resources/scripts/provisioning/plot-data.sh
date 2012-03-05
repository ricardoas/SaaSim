#!/bin/bash

args <- commandArgs(TRUE)

name=args[1]

data <- read.table(name)

library("Cairo")

CairoJPEG(filename=paste(name, ".jpeg", sep=""))

attach(data)

plot(V3, ylim=range(data[,3:6]))
points(V4, ylim=range(data[,3:6]),col="green")
points(V5, ylim=range(data[,3:6]),col="orange")
points(V6, ylim=range(data[,3:6]),col="red")

dev.off()

