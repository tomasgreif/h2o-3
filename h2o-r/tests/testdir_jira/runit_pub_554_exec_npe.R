setwd(normalizePath(dirname(R.utils::commandArgs(asValues=TRUE)$"f")))
source('../h2o-runit.R')

test.ifce<- function() {

  r.hex <- as.h2o(iris)
  r.hex[3,-2] + 5
  ifelse(1, r.hex, (r.hex + 1))[1,1]
  r.hex[2+4,-4] + 5

  
}

doTest("test ifce", test.ifce)
