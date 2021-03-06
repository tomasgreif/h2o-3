import sys
sys.path.insert(1,"../../../")
import h2o, tests

def gbm_mean_residual_deviance():

    cars =  h2o.import_file(path=tests.locate("smalldata/junit/cars_20mpg.csv"))
    s = cars[0].runif()
    train = cars[s > 0.2]
    valid = cars[s <= 0.2]
    predictors = ["displacement","power","weight","acceleration","year"]
    response_col = "economy"
    gbm = h2o.gbm(x=train[predictors],
                  y=train[response_col],
                  validation_x=valid[predictors],
                  validation_y=valid[response_col],
                  nfolds=3)
    gbm_mrd = gbm.mean_residual_deviance(train=True,valid=True,xval=True)
    assert isinstance(gbm_mrd['train'],float), "Expected training mean residual deviance to be a float, but got " \
                                              "{0}".format(type(gbm_mrd['train']))
    assert isinstance(gbm_mrd['valid'],float), "Expected validation mean residual deviance to be a float, but got " \
                                              "{0}".format(type(gbm_mrd['valid']))
    assert isinstance(gbm_mrd['xval'],float), "Expected cross-validation mean residual deviance to be a float, but got " \
                                             "{0}".format(type(gbm_mrd['xval']))

if __name__ == '__main__':
    tests.run_test(sys.argv, gbm_mean_residual_deviance)
