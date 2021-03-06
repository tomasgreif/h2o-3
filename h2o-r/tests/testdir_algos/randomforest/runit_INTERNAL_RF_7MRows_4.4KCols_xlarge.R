setwd(normalizePath(dirname(R.utils::commandArgs(asValues=TRUE)$"f")))
source('../../h2o-runit.R')

rtest <- function() {

hdfs_name_node = H2O.INTERNAL.HDFS.NAME.NODE
hdfs_data_file = "/datasets/bigdata/7MRows_4400KCols.csv"
#----------------------------------------------------------------------
# Parameters for the test.
#----------------------------------------------------------------------

url <- sprintf("hdfs://%s%s", hdfs_name_node, hdfs_data_file)
parse_time <- system.time(data.hex <- h2o.importFile(url))
print("Time it took to parse")
print(parse_time)

# Start modeling   
# Random Forest 
response="C1" #1:1000 imbalance
predictors=c(4:ncol(data.hex))

rf_time <- system.time(mdl.rf <- h2o.randomForest(x=predictors, y=response, training_frame=data.hex, ntrees=10, max_depth=5))
mdl.rf
print("Time it took to build RF")
print(rf_time)

}

doTest("Test",rtest)

