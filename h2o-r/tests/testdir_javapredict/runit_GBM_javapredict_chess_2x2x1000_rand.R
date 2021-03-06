#----------------------------------------------------------------------
# Purpose:  This test exercises the GBM model downloaded as java code
#           for the iris data set while randomly setting the parameters.
#
# Notes:    Assumes unix environment.
#           curl, javac, java must be installed.
#           java must be at least 1.6.
#----------------------------------------------------------------------
setwd(normalizePath(dirname(R.utils::commandArgs(asValues=TRUE)$"f")))
source("../h2o-runit.R")

test.gbm.javapredict.chess <-
function() {
    #----------------------------------------------------------------------
    # Parameters for the test.
    #----------------------------------------------------------------------
    training_file <- locate("smalldata/chess/chess_2x2x1000/train.csv")
    test_file <- locate("smalldata/chess/chess_2x2x1000/test.csv")
    training_frame <- h2o.importFile(training_file)
    test_frame <- h2o.importFile(test_file)

    params                 <- list()
    params$ntrees          <- sample(100, 1)
    params$max_depth       <- sample(10, 1)
    params$min_rows        <- sample(5, 1)
    params$learn_rate      <- sample(c(0.001, 0.002, 0.01, 0.02, 0.1, 0.2), 1)
    params$x               <- c("x", "y")
    params$y               <- "color"
    params$training_frame  <- training_frame

    #----------------------------------------------------------------------
    # Run the test
    #----------------------------------------------------------------------
    doJavapredictTest("gbm",normalizePath(paste0(getwd(),"/..")),test_file,test_frame,params)
}

doTest("GBM test", test.gbm.javapredict.chess)
