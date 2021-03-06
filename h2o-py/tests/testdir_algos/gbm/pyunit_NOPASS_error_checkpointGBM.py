import sys, shutil
sys.path.insert(1, "../../../")
import h2o, tests

def cars_checkpoint():

    cars = h2o.upload_file(tests.locate("smalldata/junit/cars_20mpg.csv"))
    predictors = ["displacement","power","weight","acceleration","year"]
    response_col = "economy"
    distribution = "gaussian"

    # build first model
    model1 = h2o.gbm(x=cars[predictors],y=cars[response_col],ntrees=10,max_depth=2, min_rows=10,
                     distribution=distribution)

    # continue building the model
    model2 = h2o.gbm(x=cars[predictors],y=cars[response_col],ntrees=11,max_depth=3, min_rows=9,r2_stopping=0.8,
                     distribution=distribution,checkpoint=model1._id)

    #   erroneous, not MODIFIABLE_BY_CHECKPOINT_FIELDS
    # PUBDEV-1833
    #   learn_rate
    try:
        model = h2o.gbm(y=cars[response_col], x=cars[predictors],learn_rate=0.00001,distribution=distribution,
                        checkpoint=model1._id)
        assert False, "Expected model-build to fail because learn_rate not modifiable by checkpoint"
    except EnvironmentError:
        assert True

    #   nbins_cats
    try:
        model = h2o.gbm(y=cars[response_col], x=cars[predictors],nbins_cats=99,distribution=distribution,
                        checkpoint=model1._id)
        assert False, "Expected model-build to fail because nbins_cats not modifiable by checkpoint"
    except EnvironmentError:
        assert True

    #   balance_classes
    try:
        model = h2o.gbm(y=cars[response_col], x=cars[predictors],balance_classes=True,distribution=distribution,
                        checkpoint=model1._id)
        assert False, "Expected model-build to fail because balance_classes not modifiable by checkpoint"
    except EnvironmentError:
        assert True

    #   nbins
    try:
        model = h2o.gbm(y=cars[response_col], x=cars[predictors],nbins=99,distribution=distribution,
                        checkpoint=model1._id)
        assert False, "Expected model-build to fail because nbins not modifiable by checkpoint"
    except EnvironmentError:
        assert True

    #   nfolds
    try:
        model = h2o.gbm(y=cars[response_col], x=cars[predictors],nfolds=3,distribution=distribution,
                        checkpoint=model1._id)
        assert False, "Expected model-build to fail because nfolds not modifiable by checkpoint"
    except EnvironmentError:
        assert True


if __name__ == "__main__":
    tests.run_test(sys.argv, cars_checkpoint)
