setwd(normalizePath(dirname(R.utils::commandArgs(asValues=TRUE)$"f")))
source('../../h2o-runit.R')

test.GBM.ecology <- function() {
  Log.info("==============================")
  Log.info("Gaussian Behavior")
  Log.info("==============================")
  eco.hex <- h2o.importFile(path=locate("smalldata/gbm_test/ecology_model.csv"))
  # 0/1 response: expect gaussian
  eco.model <- h2o.gbm(x = 3:13, y = "Angaus", training_frame = eco.hex, distribution="gaussian")
  expect_true(class(eco.model) == "H2ORegressionModel")
  # character response: expect error
  expect_error(eco.model <- h2o.gbm(x = 1:8, y = "Method", training_frame = eco.hex, distribution="gaussian"))

  Log.info("==============================")
  Log.info("Bernoulli Behavior")
  Log.info("==============================")
  # 0/1 response: expect bernoulli
  eco.hex[, "Angaus"] = as.factor(eco.hex[, "Angaus"])
  eco.model <- h2o.gbm(x = 3:13, y = "Angaus", training_frame = eco.hex, distribution="bernoulli")
  expect_true(class(eco.model) == "H2OBinomialModel")
  # 2 level character response: expect bernoulli
  tree.hex <- h2o.importFile(path=locate("smalldata/junit/test_tree_minmax.csv"))
  tree.model <- h2o.gbm(x = 1:3, y = "response", training_frame = tree.hex, distribution="bernoulli", min_rows=1)
  expect_true(class(tree.model) == "H2OBinomialModel")
  # more than two integers for response: expect error
  cars.hex <- h2o.importFile(path=locate("smalldata/junit/cars.csv"))
  expect_error(cars.mod <- h2o.gbm(x = 4:7, y = "cylinders", training_frame = cars.hex, distribution="bernoulli"))
  # more than two character levels for response: expect error
  expect_error(eco.model <- h2o.gbm(x = 1:8, y = "Method", training_frame = eco.hex, distribution="bernoulli"))

  Log.info("==============================")
  Log.info("Multinomial Behavior")
  Log.info("==============================")
  # more than two integers for response: expect multinomial
  cars.hex[, "cylinders"] = as.factor(cars.hex[, "cylinders"])
  cars.mod <- h2o.gbm(x = 4:7, y = "cylinders", training_frame = cars.hex, distribution="multinomial")
  expect_true(class(cars.mod) == "H2OMultinomialModel")
  # more than two character levels for response: expect multinomial
  eco.model <- h2o.gbm(x = 1:8, y = "Method", training_frame = eco.hex, distribution="multinomial")
  expect_true(class(eco.model) == "H2OMultinomialModel")

  
}

doTest("GBM: Ecology Data", test.GBM.ecology)

