setwd(normalizePath(dirname(R.utils::commandArgs(asValues=TRUE)$"f")))
source('../h2o-runit.R')

test.rdocsd.golden <- function() {

    prosPath <- system.file("extdata", "prostate.csv", package="h2o")
    prostate.hex <- h2o.uploadFile(path = prosPath)
    sd(prostate.hex$AGE)


}

doTest("R Doc SD", test.rdocsd.golden)

