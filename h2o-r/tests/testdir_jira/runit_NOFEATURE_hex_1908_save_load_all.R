##
# Test: Saving and Loading GLM Model (HEX-1775)
# Description: Build GLM model, save model in R, copy model and load in R
##

setwd(normalizePath(dirname(R.utils::commandArgs(asValues=TRUE)$"f")))
# setwd("/Users/tomk/0xdata/ws/h2o/R/tests/testdir_jira")

source('../h2o-runit.R')

test.hex_1908 <- function() {
  temp_dir = tempdir()
  temp_subdir1 = paste(temp_dir, "tmp", sep = .Platform$file.sep)
  temp_subdir2 = paste(temp_dir, "tmp2", sep = .Platform$file.sep)
  dir.create(temp_subdir1)
  
  # Test saving and loading of GLM model
  Log.info("Importing airlines.csv...")
  airlines.hex = h2o.importFile(normalizePath(locate('smalldata/airlines/AirlinesTrain.csv.zip')))
  
  # Set x and y variables
  myY = "IsDepDelayed"
  myX = c("fYear", "UniqueCarrier", "Origin", "Dest", "Distance")
  
  # Build GLM and GBM each with cross validation models
  Log.info("Build GLM model")
  airlines.glm = h2o.glm(y = myY, x = myX, training_frame = airlines.hex, family = "binomial", nfolds = 0, alpha = 0.5)
  Log.info("Build GLM model with nfold = 5")
  airlines_xval.glm = h2o.glm(y = myY, x = myX, training_frame = airlines.hex, family = "binomial", nfolds = 5, alpha = 0.5)
  Log.info("Build GBM model with nfold = 3")
  airlines_xval.gbm = h2o.gbm(y = myY, x = myX, training_frame = airlines.hex, nfolds = 3, loss = "multinomial")
  
  # Predict on models and save results in R
  Log.info("Scoring on models and saving predictions to R")
  
  pred_df <- function(object, newdata) {
    h2o_pred = predict(object, newdata)
    as.data.frame(h2o_pred)
  }
      
  glm.pred = pred_df(object = airlines.glm , newdata = airlines.hex)
  glm_xval.pred = pred_df(object = airlines_xval.glm, newdata = airlines.hex)
  gbm_xval.pred = pred_df(object = airlines_xval.gbm, newdata = airlines.hex)
  
  # Save models to disk
  Log.info("Saving models to disk")
  model_paths = h2o.saveAll(object = conn, dir = temp_subdir1, save_cv = TRUE, force = TRUE)
  
  # All keys removed to test that cross validation models are actually being loaded
  h2o.removeAll(object = conn)

  # Proving we can move files from one directory to another and not affect the load of the model
  Log.info(paste("Moving models from", temp_subdir1, "to", temp_subdir2))
  file.rename(temp_subdir1, temp_subdir2)

  # Check to make sure predictions made on loaded model is the same as glm.pred
  airlines.hex = h2o.importFile(normalizePath(locate('smalldata/airlines/AirlinesTrain.csv.zip')))
  
  # Load model back into H2O
  Log.info(paste("Model saved in", temp_subdir2))
  reloaded_models = h2o.loadAll(object = conn, dir = temp_subdir2)
  
  Log.info("Running Predictions for Loaded Models")
  new_keys = lapply(reloaded_models, function(model) model@key)
  
  gbm_xval2 = reloaded_models[[1]]
  if(length(reloaded_models[[2]]@xval) == 0 ) {
    glm2 = reloaded_models[[2]]
    glm_xval2 = reloaded_models[[3]]
  } else {
    glm2 = reloaded_models[[3]]
    glm_xval2 = reloaded_models[[2]]
  }
  
  glm.pred2 = pred_df(object = glm2, newdata = airlines.hex)
  glm_xval.pred2 = pred_df(object = glm_xval2, newdata = airlines.hex)
  gbm_xval.pred2 = pred_df(object = gbm_xval2, newdata = airlines.hex)
  
## Check to make sure scores are the same, and the number of cross validations models are the same
  expect_equal(length(glm2@xval), length(airlines.glm@xval))
  expect_equal(length(glm_xval2@xval), length(airlines_xval.glm@xval))
  expect_equal(length(gbm_xval2@xval), length(airlines_xval.gbm@xval))
  expect_equal(nrow(glm.pred), 24421)
  expect_equal(glm.pred, glm.pred2)
  expect_equal(nrow(glm_xval.pred), 24421)
  expect_equal(glm_xval.pred, glm_xval.pred2)
  expect_equal(nrow(gbm_xval.pred), 24421)
  expect_equal(gbm_xval.pred, gbm_xval.pred2)
  expect_equal(glm.pred, glm_xval.pred)
  
  h2o.removeAll(conn)
  
}

doTest("HEX-1908 Test: Save and Load All Models", test.hex_1908)
