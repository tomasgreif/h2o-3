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

test.drf.javapredict.iris <-
function() {
    #----------------------------------------------------------------------
    # Parameters for the test.
    #----------------------------------------------------------------------
    training_file <- locate("smalldata/iris/iris_train.csv")
    test_file <- locate("smalldata/iris/iris_test.csv")
    training_frame <- h2o.importFile(training_file)
    test_frame <- h2o.importFile(test_file)

    params                 <- list()
    params$ntrees          <- sample( 1000, 1)
    params$max_depth       <- sample( 999, 1)
    params$min_rows        <- sample( 10, 1)
    params$x               <- c("sepal_len","sepal_wid","petal_len","petal_wid");
    params$y               <- "species"
    params$training_frame  <- training_frame

    #----------------------------------------------------------------------
    # Run the test
    #----------------------------------------------------------------------
    doJavapredictTest("randomForest",normalizePath(paste0(getwd(),"/..")),test_file,test_frame,params)
}

doTest("RF test", test.drf.javapredict.iris)
