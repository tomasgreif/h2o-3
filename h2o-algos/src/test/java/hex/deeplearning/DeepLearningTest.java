package hex.deeplearning;


import hex.*;

import static hex.Distribution.Family.*;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import water.*;
import water.exceptions.H2OModelBuilderIllegalArgumentException;
import water.fvec.Frame;
import water.fvec.Vec;
import water.util.Log;
import water.util.MathUtils;

import java.util.Arrays;

public class DeepLearningTest extends TestUtil {
  @BeforeClass public static void stall() { stall_till_cloudsize(1); }

  abstract static class PrepData { abstract int prep(Frame fr); }

  static String[] s(String...arr)  { return arr; }
  static long[]   a(long ...arr)   { return arr; }
  static long[][] a(long[] ...arr) { return arr; }

  @Test public void testClassIris1() throws Throwable {

    // iris ntree=1
    basicDLTest_Classification(
        "./smalldata/iris/iris.csv", "iris.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            return fr.numCols() - 1;
          }
        },
        1,
        ard(ard(27, 16, 7),
            ard(0, 4, 46),
            ard(0, 3, 47)),
        s("Iris-setosa", "Iris-versicolor", "Iris-virginica"),
        DeepLearningParameters.Activation.Rectifier);

  }

  @Test public void testClassIris5() throws Throwable {
    // iris ntree=50
    basicDLTest_Classification(
        "./smalldata/iris/iris.csv", "iris5.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            return fr.numCols() - 1;
          }
        },
        5,
        ard(ard(50, 0, 0),
            ard(0, 39, 11),
            ard(0, 8, 42)),
        s("Iris-setosa", "Iris-versicolor", "Iris-virginica"),
        DeepLearningParameters.Activation.Rectifier);
  }

  @Test public void testClassCars1() throws Throwable {
    // cars ntree=1
    basicDLTest_Classification(
        "./smalldata/junit/cars.csv", "cars.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            fr.remove("name").remove();
            return fr.find("cylinders");
          }
        },
        1,
        ard(ard(0, 4, 0, 0, 0),
            ard(0, 193, 5, 9, 0),
            ard(0, 2, 1, 0, 0),
            ard(0, 65, 3, 16, 0),
            ard(0, 11, 0, 7, 90)),
        s("3", "4", "5", "6", "8"),
        DeepLearningParameters.Activation.Rectifier);
  }

  @Test public void testClassCars5() throws Throwable {
    basicDLTest_Classification(
        "./smalldata/junit/cars.csv", "cars5.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            fr.remove("name").remove();
            return fr.find("cylinders");
          }
        },
        5,
        ard(ard(0, 4, 0, 0, 0),
            ard(0, 206, 0, 1, 0),
            ard(0, 2, 0, 1, 0),
            ard(0, 14, 0, 69, 1),
            ard(0, 0, 0, 6, 102)),
        s("3", "4", "5", "6", "8"),
        DeepLearningParameters.Activation.Rectifier);
  }

  @Test public void testConstantCols() throws Throwable {
    try {
      basicDLTest_Classification(
          "./smalldata/poker/poker100", "poker.hex",
          new PrepData() {
            @Override
            int prep(Frame fr) {
              for (int i = 0; i < 7; i++) {
                fr.remove(3).remove();
              }
              return 3;
            }
          },
          1,
          null,
          null,
          DeepLearningParameters.Activation.Rectifier);
      Assert.fail();
    } catch( H2OModelBuilderIllegalArgumentException iae ) {
    /*pass*/
    }
  }

  @Test public void testBadData() throws Throwable {
    basicDLTest_Classification(
        "./smalldata/junit/drf_infinities.csv", "infinitys.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            return fr.find("DateofBirth");
          }
        },
        1,
        ard(ard(0, 17),
            ard(0, 17)),
        s("0", "1"),
        DeepLearningParameters.Activation.Rectifier);
  }

  //@Test
  public void testCreditSample1() throws Throwable {
    basicDLTest_Classification(
        "./smalldata/kaggle/creditsample-training.csv.gz", "credit.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            fr.remove("MonthlyIncome").remove();
            return fr.find("SeriousDlqin2yrs");
          }
        },
        1,
        ard(ard(46294, 202),
            ard(3187, 107)),
        s("0", "1"),
        DeepLearningParameters.Activation.Rectifier);

  }

  @Test public void testCreditProstate1() throws Throwable {
    basicDLTest_Classification(
        "./smalldata/logreg/prostate.csv", "prostate.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            fr.remove("ID").remove();
            return fr.find("CAPSULE");
          }
        },
        1,
        ard(ard(97, 130),
            ard(28, 125)),
        s("0", "1"),
        DeepLearningParameters.Activation.Rectifier);
  }

  @Test public void testCreditProstateReLUDropout() throws Throwable {
    basicDLTest_Classification(
        "./smalldata/logreg/prostate.csv", "prostateReLUDropout.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            fr.remove("ID").remove();
            return fr.find("CAPSULE");
          }
        },
        1,
        ard(ard(4, 223),
            ard(0, 153)),
        s("0", "1"),
        DeepLearningParameters.Activation.RectifierWithDropout);

  }

  @Test public void testCreditProstateTanh() throws Throwable {
    basicDLTest_Classification(
        "./smalldata/logreg/prostate.csv", "prostateTanh.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            fr.remove("ID").remove();
            return fr.find("CAPSULE");
          }
        },
        1,
        ard(ard(141, 86),
            ard(25, 128)),
        s("0", "1"),
        DeepLearningParameters.Activation.Tanh);
  }

  @Test public void testCreditProstateTanhDropout() throws Throwable {
    basicDLTest_Classification(
        "./smalldata/logreg/prostate.csv", "prostateTanhDropout.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            fr.remove("ID").remove();
            return fr.find("CAPSULE");
          }
        },
        1,
        ard(ard(110, 117),
            ard(23, 130)),
        s("0", "1"),
        DeepLearningParameters.Activation.TanhWithDropout);
  }

  @Test public void testCreditProstateMaxout() throws Throwable {
    basicDLTest_Classification(
        "./smalldata/logreg/prostate.csv", "prostateMaxout.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            fr.remove("ID").remove();
            return fr.find("CAPSULE");
          }
        },
        100,
        ard(ard(189, 38),
            ard(30, 123)),
        s("0", "1"),
        DeepLearningParameters.Activation.Maxout);
  }

  @Test public void testCreditProstateMaxoutDropout() throws Throwable {
    basicDLTest_Classification(
        "./smalldata/logreg/prostate.csv", "prostateMaxoutDropout.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            fr.remove("ID").remove();
            return fr.find("CAPSULE");
          }
        },
        100,
        ard(ard(183, 44),
            ard(40, 113)),
        s("0", "1"),
        DeepLearningParameters.Activation.MaxoutWithDropout);
  }

  @Test public void testCreditProstateRegression1() throws Throwable {
    basicDLTest_Regression(
        "./smalldata/logreg/prostate.csv", "prostateRegression.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            fr.remove("ID").remove();
            return fr.find("AGE");
          }
        },
        1,
        46.2696942006363,
        DeepLearningParameters.Activation.Rectifier);

  }

  @Test public void testCreditProstateRegressionTanh() throws Throwable {
    basicDLTest_Regression(
        "./smalldata/logreg/prostate.csv", "prostateRegressionTanh.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            fr.remove("ID").remove();
            return fr.find("AGE");
          }
        },
        1,
        43.458837416075966,
        DeepLearningParameters.Activation.Tanh);

  }

  @Test public void testCreditProstateRegressionMaxout() throws Throwable {
    basicDLTest_Regression(
        "./smalldata/logreg/prostate.csv", "prostateRegressionMaxout.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            fr.remove("ID").remove();
            return fr.find("AGE");
          }
        },
        100,
        32.79983057944052,
        DeepLearningParameters.Activation.Maxout);

  }

  @Test public void testCreditProstateRegression5() throws Throwable {
    basicDLTest_Regression(
        "./smalldata/logreg/prostate.csv", "prostateRegression5.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            fr.remove("ID").remove();
            return fr.find("AGE");
          }
        },
        5,
        41.85026474777778,
        DeepLearningParameters.Activation.Rectifier);

  }

  @Test public void testCreditProstateRegression50() throws Throwable {
    basicDLTest_Regression(
        "./smalldata/logreg/prostate.csv", "prostateRegression50.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            fr.remove("ID").remove();
            return fr.find("AGE");
          }
        },
        50,
        37.894819598316246,
        DeepLearningParameters.Activation.Rectifier);

  }
  @Test public void testAlphabet() throws Throwable {
    basicDLTest_Classification(
        "./smalldata/gbm_test/alphabet_cattest.csv", "alphabetClassification.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            return fr.find("y");
          }
        },
        10,
        ard(ard(2080, 0),
            ard(0, 2080)),
        s("0", "1"),
        DeepLearningParameters.Activation.Rectifier);
  }
  @Test public void testAlphabetRegression() throws Throwable {
    basicDLTest_Regression(
        "./smalldata/gbm_test/alphabet_cattest.csv", "alphabetRegression.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            return fr.find("y");
          }
        },
        10,
        1.6201002863836856E-4,
        DeepLearningParameters.Activation.Rectifier);
  }

  @Ignore  //1-vs-5 node discrepancy (parsing into different number of chunks?)
  @Test public void testAirlines() throws Throwable {
    basicDLTest_Classification(
        "./smalldata/airlines/allyears2k_headers.zip", "airlines.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            for (String s : new String[]{
                "DepTime", "ArrTime", "ActualElapsedTime",
                "AirTime", "ArrDelay", "DepDelay", "Cancelled",
                "CancellationCode", "CarrierDelay", "WeatherDelay",
                "NASDelay", "SecurityDelay", "LateAircraftDelay", "IsArrDelayed", "TailNum"
            }) {
              fr.remove(s).remove();
            }
            return fr.find("IsDepDelayed");
          }
        },
        7,
        ard(ard(9251, 11636),
            ard(3053, 200038)),
        s("NO", "YES"),
        DeepLearningParameters.Activation.Rectifier);
  }

  @Ignore //PUBDEV-1001
  @Test public void testCzechboard() throws Throwable {
    basicDLTest_Classification(
        "./smalldata/gbm_test/czechboard_300x300.csv", "czechboard_300x300.hex",
        new PrepData() {
          @Override
          int prep(Frame fr) {
            Vec resp = fr.remove("C2");
            fr.add("C2", resp.toCategorical());
            resp.remove();
            return fr.find("C3");
          }
        },
        1,
        ard(ard(7, 44993),
            ard(2, 44998)),
        s("0", "1"),
        DeepLearningParameters.Activation.Tanh);
  }


  // Put response as the last vector in the frame and return possible frames to clean up later
  static Vec unifyFrame(DeepLearningParameters drf, Frame fr, PrepData prep, boolean classification) {
    int idx = prep.prep(fr);
    if( idx < 0 ) { idx = ~idx; }
    String rname = fr._names[idx];
    drf._response_column = fr.names()[idx];

    Vec resp = fr.vecs()[idx];
    Vec ret = null;
    if (classification) {
      ret = fr.remove(idx);
      fr.add(rname,resp.toCategorical());
    } else {
      fr.remove(idx);
      fr.add(rname,resp);
    }
    return ret;
  }

  public void basicDLTest_Classification(String fnametrain, String hexnametrain, PrepData prep, int epochs, double[][] expCM, String[] expRespDom, DeepLearningParameters.Activation activation) throws Throwable { basicDL(fnametrain, hexnametrain, null, prep, epochs, expCM, expRespDom, -1, new int[]{10, 10}, 1e-5, true, activation); }
  public void basicDLTest_Regression(String fnametrain, String hexnametrain, PrepData prep, int epochs, double expMSE, DeepLearningParameters.Activation activation) throws Throwable { basicDL(fnametrain, hexnametrain, null, prep, epochs, null, null, expMSE, new int[]{10, 10}, 1e-5, false, activation); }

  public void basicDL(String fnametrain, String hexnametrain, String fnametest, PrepData prep, int epochs, double[][] expCM, String[] expRespDom, double expMSE, int[] hidden, double l1, boolean classification, DeepLearningParameters.Activation activation) throws Throwable {
    Scope.enter();
    DeepLearningParameters dl = new DeepLearningParameters();
    Frame frTest = null, pred = null;
    Frame frTrain = null;
    Frame test = null, res = null;
    DeepLearningModel model = null;
    try {
      frTrain = parse_test_file(fnametrain);
      Vec removeme = unifyFrame(dl, frTrain, prep, classification);
      if (removeme != null) Scope.track(removeme._key);
      DKV.put(frTrain._key, frTrain);
      // Configure DL
      dl._train = frTrain._key;
      dl._response_column = ((Frame)DKV.getGet(dl._train)).lastVecName();
      dl._seed = (1L<<32)|2;
      dl._model_id = Key.make("DL_model_" + hexnametrain);
      dl._reproducible = true;
      dl._epochs = epochs;
      dl._activation = activation;
      dl._export_weights_and_biases = true;
      dl._hidden = hidden;
      dl._l1 = l1;
      dl._elastic_averaging = false;

      // Invoke DL and block till the end
      DeepLearning job = null;
      try {
        job = new DeepLearning(dl);
        // Get the model
        model = job.trainModel().get();
        Log.info(model._output);
      } finally {
        if (job != null) job.remove();
      }
      assertTrue(job._state == Job.JobState.DONE); //HEX-1817

      hex.ModelMetrics mm;
      if (fnametest != null) {
        frTest = parse_test_file(fnametest);
        pred = model.score(frTest);
        mm = hex.ModelMetrics.getFromDKV(model, frTest);
        // Check test set CM
      } else {
        pred = model.score(frTrain);
        mm = hex.ModelMetrics.getFromDKV(model, frTrain);
      }

      test = parse_test_file(fnametrain);
      res = model.score(test);

      if (classification) {
        assertTrue("Expected: " + Arrays.deepToString(expCM) + ", Got: " + Arrays.deepToString(mm.cm()._cm),
            Arrays.deepEquals(mm.cm()._cm, expCM));

        String[] cmDom = model._output._domains[model._output._domains.length - 1];
        Assert.assertArrayEquals("CM domain differs!", expRespDom, cmDom);
        Log.info("\nTraining CM:\n" + mm.cm().toASCII());
        Log.info("\nTraining CM:\n" + hex.ModelMetrics.getFromDKV(model, test).cm().toASCII());
      } else {
        assertTrue("Expected: " + expMSE + ", Got: " + mm.mse(), MathUtils.compare(expMSE, mm.mse(), 1e-8, 1e-8));
        Log.info("\nOOB Training MSE: " + mm.mse());
        Log.info("\nTraining MSE: " + hex.ModelMetrics.getFromDKV(model, test).mse());
      }

      hex.ModelMetrics.getFromDKV(model, test);

      // Build a POJO, validate same results
      assertTrue(model.testJavaScoring(test, res, 1e-5));

    } finally {
      if (frTrain!=null) frTrain.remove();
      if (frTest!=null) frTest.remove();
      if( model != null ) model.delete(); // Remove the model
      if( pred != null ) pred.delete();
      if( test != null ) test.delete();
      if( res != null ) res.delete();
      Scope.exit();
    }
  }

  @Test public void elasticAveragingTrivial() {
    DeepLearningParameters dl;
    Frame frTrain = null;
    int N = 2;
    DeepLearningModel [] models = new DeepLearningModel[N];
    dl = new DeepLearningParameters();
    Scope.enter();
    try {
      for (int i = 0; i < N; ++i) {
        frTrain = parse_test_file("./smalldata/covtype/covtype.20k.data");
        Vec resp = frTrain.lastVec().toCategorical();
        frTrain.remove(frTrain.vecs().length - 1).remove();
        frTrain.add("Response", resp);
        DKV.put(frTrain);
        dl._train = frTrain._key;
        dl._response_column = ((Frame) DKV.getGet(dl._train)).lastVecName();
        dl._export_weights_and_biases = true;
        dl._hidden = new int[]{17, 11};
        dl._quiet_mode = false;

        // make it reproducible
        dl._seed = 1234;
        dl._reproducible = true;

        // only do one M/R iteration, and there's no elastic average yet - so the two paths below should be identical
        dl._epochs = 1;
        dl._train_samples_per_iteration = -1;

        if (i == 0) {
          // no elastic averaging
          dl._elastic_averaging = false;
          dl._elastic_averaging_moving_rate = 0.5; //ignored
          dl._elastic_averaging_regularization = 0.9; //ignored
        } else {
          // no-op elastic averaging
          dl._elastic_averaging = true; //go different path, but don't really do anything because of epochs=1 and train_samples_per_iteration=-1
          dl._elastic_averaging_moving_rate = 0.5; //doesn't matter, it's not used since we only do one M/R iteration and there's no time average
          dl._elastic_averaging_regularization = 0.1; //doesn't matter, since elastic average isn't yet available in first iteration
        }

        // Invoke DL and block till the end
        DeepLearning job = null;
        try {
          job = new DeepLearning(dl);
          // Get the model
          models[i] = job.trainModel().get();
        } finally {
          if (job != null) job.remove();
        }
        if (frTrain != null) frTrain.remove();
      }
      for (int i = 0; i < N; ++i) {
        Log.info(models[i]._output._training_metrics.cm().table().toString());
        Assert.assertEquals(models[i]._output._training_metrics._MSE, models[0]._output._training_metrics._MSE, 1e-6);
      }

    }finally{
      for (int i=0; i<N; ++i)
        if (models[i] != null)
          models[i].delete();
      Scope.exit();
    }
  }

  @Test public void elasticAveraging() {
    DeepLearningParameters dl;
    Frame frTrain;
    int N = 2;
    DeepLearningModel [] models = new DeepLearningModel[N];
    dl = new DeepLearningParameters();
    Scope.enter();
    boolean covtype = true;
    if (covtype) {
      frTrain = parse_test_file("./smalldata/covtype/covtype.20k.data");
      Vec resp = frTrain.lastVec().toCategorical();
      frTrain.remove(frTrain.vecs().length - 1).remove();
      frTrain.add("Response", resp);
    } else {
      frTrain = parse_test_file("./bigdata/server/HIGGS.csv");
      Vec resp = frTrain.vecs()[0].toCategorical();
      frTrain.remove(0).remove();
      frTrain.prepend("Response", resp);
    }
    DKV.put(frTrain);
    try {
      for (int i = 0; i < N; ++i) {
        dl._train = frTrain._key;
        String[] n = ((Frame) DKV.getGet(dl._train)).names();
        if (covtype) {
          dl._response_column = n[n.length-1];
          dl._ignored_columns = null;
        } else {
          dl._response_column = n[0];
          dl._ignored_columns = new String[]{n[22], n[23], n[24], n[25], n[26], n[27], n[28]};
        }
        dl._export_weights_and_biases = true;
        dl._hidden = new int[]{64, 64};
        dl._quiet_mode = false;
        dl._max_w2 = 10;
        dl._l1 = 1e-5;
        dl._replicate_training_data = false; //every node only has a piece of the data
        dl._force_load_balance = true; //use multi-node

        dl._epochs = 1;
        dl._train_samples_per_iteration = frTrain.numRows()/100; //100 M/R steps

        dl._elastic_averaging = i==1;
        dl._elastic_averaging_moving_rate = 0.999;
        dl._elastic_averaging_regularization = 1e-3;

        // Invoke DL and block till the end
        DeepLearning job = null;
        try {
          job = new DeepLearning(dl);
          // Get the model
          models[i] = job.trainModel().get();
        } finally {
          if (job != null) job.remove();
        }
      }
      for (int i = 0; i < N; ++i) {
        if (models[i] != null)
          Log.info(models[i]._output._training_metrics.cm().table().toString());
      }
      if (models[0] != null)
        Log.info("Without elastic averaging: error=" + models[0]._output._training_metrics.cm().err());
      if (models[1] != null)
        Log.info("With elastic averaging:    error=" + models[1]._output._training_metrics.cm().err());
//      if (models[0] != null && models[1] != null)
//        Assert.assertTrue(models[1]._output._training_metrics.cm().err() < models[0]._output._training_metrics.cm().err());

    }finally{
      if (frTrain != null) frTrain.remove();
      for (int i=0; i<N; ++i)
        if (models[i] != null)
          models[i].delete();
      Scope.exit();
    }
  }

  @Test
  public void testNoRowWeights() {
    Frame tfr = null, vfr = null, pred = null, fr2 = null;

    Scope.enter();
    try {
      tfr = parse_test_file("smalldata/junit/no_weights.csv");
      DKV.put(tfr);
      DeepLearningParameters parms = new DeepLearningParameters();
      parms._train = tfr._key;
      parms._response_column = "response";
      parms._reproducible = true;
      parms._seed = 0xdecaf;
      parms._l1 = 0.1;
      parms._epochs = 1;
      parms._hidden = new int[]{1};
      parms._classification_stop = -1;

      // Build a first model; all remaining models should be equal
      DeepLearning job = new DeepLearning(parms);
      DeepLearningModel dl = job.trainModel().get();

      pred = dl.score(parms.train());
      hex.ModelMetricsBinomial mm = hex.ModelMetricsBinomial.getFromDKV(dl, parms.train());
      assertEquals(0.7592592592592592, mm.auc()._auc, 1e-8);

      double mse = dl._output._training_metrics.mse();
      assertEquals(0.31481334186707816, mse, 1e-8);

      assertTrue(dl.testJavaScoring(tfr, fr2=dl.score(tfr), 1e-5));
      job.remove();
      dl.delete();
    } finally {
      if (tfr != null) tfr.remove();
      if (vfr != null) vfr.remove();
      if (pred != null) pred.remove();
      if (fr2 != null) fr2.remove();
    }
    Scope.exit();
  }

  @Test
  public void testRowWeightsOne() {
    Frame tfr = null, vfr = null, pred = null, fr2 = null;

    Scope.enter();
    try {
      tfr = parse_test_file("smalldata/junit/weights_all_ones.csv");
      DKV.put(tfr);
      DeepLearningParameters parms = new DeepLearningParameters();
      parms._train = tfr._key;
      parms._response_column = "response";
      parms._weights_column = "weight";
      parms._reproducible = true;
      parms._seed = 0xdecaf;
      parms._classification_stop = -1;
      parms._l1 = 0.1;
      parms._hidden = new int[]{1};
      parms._epochs = 1;

      // Build a first model; all remaining models should be equal
      DeepLearning job = new DeepLearning(parms);
      DeepLearningModel dl = job.trainModel().get();

      pred = dl.score(parms.train());
      hex.ModelMetricsBinomial mm = hex.ModelMetricsBinomial.getFromDKV(dl, parms.train());
      assertEquals(0.7222222222222222, mm.auc()._auc, 1e-8);

      double mse = dl._output._training_metrics.mse();
      assertEquals(0.31599425403539766, mse, 1e-8); //Note: better results than non-shuffled

//      assertTrue(dl.testJavaScoring(tfr, fr2=dl.score(tfr, 1e-5)); //PUBDEV-1900
      job.remove();
      dl.delete();
    } finally {
      if (tfr != null) tfr.remove();
      if (vfr != null) vfr.remove();
      if (pred != null) pred.remove();
      if (fr2 != null) fr2.remove();
    }
    Scope.exit();
  }

  @Test
  public void testNoRowWeightsShuffled() {
    Frame tfr = null, vfr = null, pred = null, fr2 = null;

    Scope.enter();
    try {
      tfr = parse_test_file("smalldata/junit/no_weights_shuffled.csv");
      DKV.put(tfr);
      DeepLearningParameters parms = new DeepLearningParameters();
      parms._train = tfr._key;
      parms._response_column = "response";
      parms._reproducible = true;
      parms._seed = 0xdecaf;
      parms._l1 = 0.1;
      parms._epochs = 1;
      parms._hidden = new int[]{1};
      parms._classification_stop = -1;

      // Build a first model; all remaining models should be equal
      DeepLearning job = new DeepLearning(parms);
      DeepLearningModel dl = job.trainModel().get();

      pred = dl.score(parms.train());
      hex.ModelMetricsBinomial mm = hex.ModelMetricsBinomial.getFromDKV(dl, parms.train());
      assertEquals(0.7222222222222222, mm.auc()._auc, 1e-8);

      double mse = dl._output._training_metrics.mse();
      assertEquals(0.3164307133994674, mse, 1e-8);

      assertTrue(dl.testJavaScoring(tfr, fr2=dl.score(tfr), 1e-5));
      job.remove();
      dl.delete();
    } finally {
      if (tfr != null) tfr.remove();
      if (vfr != null) vfr.remove();
      if (pred != null) pred.remove();
      if (fr2 != null) fr2.remove();
    }
    Scope.exit();
  }

  @Test
  public void testRowWeights() {
    Frame tfr = null, vfr = null, pred = null, fr2=null;

    Scope.enter();
    try {
      tfr = parse_test_file("smalldata/junit/weights.csv");
      DKV.put(tfr);
      DeepLearningParameters parms = new DeepLearningParameters();
      parms._train = tfr._key;
      parms._response_column = "response";
      parms._weights_column = "weight";
      parms._reproducible = true;
      parms._seed = 0xdecaf;
      parms._classification_stop = -1;
      parms._l1 = 0.1;
      parms._hidden = new int[]{1};
      parms._epochs = 10;

      // Build a first model; all remaining models should be equal
      DeepLearning job = new DeepLearning(parms);
      DeepLearningModel dl = job.trainModel().get();

      pred = dl.score(parms.train());
      hex.ModelMetricsBinomial mm = hex.ModelMetricsBinomial.getFromDKV(dl, parms.train());
      assertEquals(0.7777777777777778, mm.auc()._auc, 1e-8);

      double mse = dl._output._training_metrics.mse();
      assertEquals(0.32223485418125575, mse, 1e-8);

//      Assert.assertTrue(dl.testJavaScoring(tfr,fr2=dl.score(tfr),1e-5)); //PUBDEV-1900
      job.remove();
      dl.delete();
    } finally {
      if (tfr != null) tfr.remove();
      if (vfr != null) vfr.remove();
      if (pred != null) pred.remove();
      if (fr2 != null) fr2.remove();
    }
    Scope.exit();
  }

  @Ignore
  @Test public void testWhatever() {
    DeepLearningParameters dl;
    Frame frTrain = null;
    DeepLearningModel model = null;
    while(true) {
      dl = new DeepLearningParameters();
      Scope.enter();
      try {
        frTrain = parse_test_file("./smalldata/covtype/covtype.20k.data");
        Vec resp = frTrain.lastVec().toCategorical();
        frTrain.remove(frTrain.vecs().length-1);
        frTrain.add("Response", resp);
        // Configure DL
        dl._train = frTrain._key;
        dl._response_column = ((Frame) DKV.getGet(dl._train)).lastVecName();
        dl._seed = 1234;
        dl._reproducible = true;
        dl._epochs = 0.0001;
        dl._export_weights_and_biases = true;
        dl._hidden = new int[]{188, 191};
        dl._elastic_averaging = false;

        // Invoke DL and block till the end
        DeepLearning job = null;
        try {
          job = new DeepLearning(dl);
          // Get the model
          model = job.trainModel().get();
          Log.info(model._output);
        } finally {
          if (job != null) job.remove();
        }
        assertTrue(job._state == Job.JobState.DONE); //HEX-1817


      } finally {
        if (frTrain != null) frTrain.remove();
        if (model != null) model.delete();
        Scope.exit();
      }
    }
  }

  // just a simple sanity check - not a golden test
  @Test
  public void testLossFunctions() {
    Frame tfr = null, vfr = null, fr2 = null;
    DeepLearningModel dl = null;

    for (DeepLearningParameters.Loss loss: new DeepLearningParameters.Loss[]{
        DeepLearningParameters.Loss.Automatic,
        DeepLearningParameters.Loss.Quadratic,
        DeepLearningParameters.Loss.Huber,
        DeepLearningParameters.Loss.Absolute,
    }) {
      Scope.enter();
      try {
        tfr = parse_test_file("smalldata/glm_test/cancar_logIn.csv");
        for (String s : new String[]{
            "Merit", "Class"
        }) {
          Scope.track(tfr.replace(tfr.find(s), tfr.vec(s).toCategorical())._key);
        }
        DKV.put(tfr);
        DeepLearningParameters parms = new DeepLearningParameters();
        parms._train = tfr._key;
        parms._epochs = 1;
        parms._reproducible = true;
        parms._hidden = new int[]{50,50};
        parms._response_column = "Cost";
        parms._seed = 0xdecaf;
        parms._loss = loss;

        // Build a first model; all remaining models should be equal
        DeepLearning job = new DeepLearning(parms);
        dl = job.trainModel().get();

        ModelMetricsRegression mm = (ModelMetricsRegression)dl._output._training_metrics;

        if (loss == DeepLearningParameters.Loss.Automatic || loss == DeepLearningParameters.Loss.Quadratic)
          Assert.assertEquals(mm._mean_residual_deviance, mm._MSE, 1e-6);
        else
          assertTrue(mm._mean_residual_deviance != mm._MSE);

        assertTrue(dl.testJavaScoring(tfr, fr2=dl.score(tfr), 1e-5));

        job.remove();
      } finally {
        if (tfr != null) tfr.remove();
        if (vfr != null) vfr.remove();
        if (dl != null) dl.delete();
        if (fr2 != null) fr2.remove();
        Scope.exit();
      }
    }
  }

  @Test
  public void testDistributions() {
    Frame tfr = null, vfr = null, fr2 = null;
    DeepLearningModel dl = null;

    for (Distribution.Family dist : new Distribution.Family[] {
        AUTO,
        gaussian,
        poisson,
        gamma,
        tweedie,
    }) {
      Scope.enter();
      try {
        tfr = parse_test_file("smalldata/glm_test/cancar_logIn.csv");
        for (String s : new String[]{
            "Merit", "Class"
        }) {
          Scope.track(tfr.replace(tfr.find(s), tfr.vec(s).toCategorical())._key);
        }
        DKV.put(tfr);
        DeepLearningParameters parms = new DeepLearningParameters();
        parms._train = tfr._key;
        parms._epochs = 1;
        parms._reproducible = true;
        parms._hidden = new int[]{50,50};
        parms._response_column = "Cost";
        parms._seed = 0xdecaf;
        parms._distribution = dist;

        // Build a first model; all remaining models should be equal
        DeepLearning job = new DeepLearning(parms);
        dl = job.trainModel().get();

        ModelMetricsRegression mm = (ModelMetricsRegression)dl._output._training_metrics;

        if (dist == gaussian || dist == AUTO)
          Assert.assertEquals(mm._mean_residual_deviance, mm._MSE, 1e-6);
        else
          assertTrue(mm._mean_residual_deviance != mm._MSE);

        assertTrue(dl.testJavaScoring(tfr, fr2=dl.score(tfr), 1e-5));

        job.remove();
      } finally {
        if (tfr != null) tfr.remove();
        if (vfr != null) vfr.remove();
        if (dl != null) dl.delete();
        if (fr2 != null) fr2.delete();
        Scope.exit();
      }
    }
  }

  @Test
  public void testAutoEncoder() {
    Frame tfr = null, vfr = null, fr2 = null;
    DeepLearningModel dl = null;

    Scope.enter();
    try {
      tfr = parse_test_file("smalldata/glm_test/cancar_logIn.csv");
      for (String s : new String[]{
          "Merit", "Class"
      }) {
        Scope.track(tfr.replace(tfr.find(s), tfr.vec(s).toCategorical())._key);
      }
      DKV.put(tfr);
      DeepLearningParameters parms = new DeepLearningParameters();
      parms._train = tfr._key;
      parms._epochs = 100;
      parms._reproducible = true;
      parms._hidden = new int[]{5,5,5};
      parms._response_column = "Cost";
      parms._seed = 0xdecaf;
      parms._autoencoder = true;
      parms._input_dropout_ratio = 0.1;
      parms._activation = DeepLearningParameters.Activation.Tanh;

      // Build a first model; all remaining models should be equal
      DeepLearning job = new DeepLearning(parms);
      dl = job.trainModel().get();

      ModelMetricsAutoEncoder mm = (ModelMetricsAutoEncoder)dl._output._training_metrics;
      Assert.assertEquals(0.0712931422088762, mm._MSE, 1e-2);

      assertTrue(dl.testJavaScoring(tfr, fr2=dl.score(tfr), 1e-5));

      job.remove();
    } finally {
      if (tfr != null) tfr.remove();
      if (vfr != null) vfr.remove();
      if (dl != null) dl.delete();
      if (fr2 != null) fr2.delete();
      Scope.exit();
    }
  }

  @Test
  public void testNumericalExplosion() {
    for (boolean ae : new boolean[]{
        true,
        false
    }) {
      Frame tfr = null;
      DeepLearningModel dl = null;

      try {
        tfr = parse_test_file("./smalldata/junit/two_spiral.csv");
        for (String s : new String[]{
            "Class"
        }) {
          Vec resp = tfr.vec(s).toCategorical();
          tfr.remove(s).remove();
          tfr.add(s, resp);
          DKV.put(tfr);
        }
        DeepLearningParameters parms = new DeepLearningParameters();
        parms._train = tfr._key;
        parms._epochs = 100;
        parms._response_column = "Class";
        parms._autoencoder = ae;
        parms._reproducible = true;
        parms._train_samples_per_iteration = 10;
        parms._hidden = new int[]{10,10,10,10,10,10,10,10,10,10,10,10};
        parms._initial_weight_distribution = DeepLearningParameters.InitialWeightDistribution.Uniform;
        parms._initial_weight_scale = 1e20;
        parms._seed = 0xdecaf;
        parms._max_w2 = 1e20f;
        parms._model_id = Key.make();

        // Build a first model; all remaining models should be equal
        DeepLearning job = new DeepLearning(parms);
        try {
          dl = job.trainModel().get();
          Assert.fail("Should toss exception instead of reaching here");
        } catch( RuntimeException de ) {
          // catch anything - might be a NPE during cleanup
//          assertTrue(de.getMessage().contains("Trying to predict with an unstable model."));
        } finally {
          job.remove();
        }
        dl = DKV.getGet(parms._model_id);
        assertTrue(dl.model_info().isUnstable());
        assertTrue(dl._output._status == Job.JobState.FAILED);
      } finally {
        if (tfr != null) tfr.delete();
        if (dl != null) dl.delete();
      }
    }
  }

  @Test
  public void testEarlyStopping() {
    Frame tfr = null;
    DeepLearningModel dl = null;

    try {
      tfr = parse_test_file("./smalldata/junit/two_spiral.csv");
      for (String s : new String[]{
              "Class"
      }) {
        Vec resp = tfr.vec(s).toCategorical();
        tfr.remove(s).remove();
        tfr.add(s, resp);
        DKV.put(tfr);
      }
      DeepLearningParameters parms = new DeepLearningParameters();
      parms._train = tfr._key;
      parms._epochs = 100;
      parms._response_column = "Class";
      parms._reproducible = true;
      parms._classification_stop = 0.7;
      parms._score_duty_cycle = 1;
      parms._score_interval = 0;
      parms._hidden = new int[]{100,100};
      parms._seed = 0xdecaf;
      parms._model_id = Key.make();

      // Build a first model; all remaining models should be equal
      DeepLearning job = new DeepLearning(parms);
      try {
        dl = job.trainModel().get();
      } finally {
        job.remove();
      }
      dl = DKV.getGet(parms._model_id);
      assertTrue(dl.stopped_early);
      assertTrue(dl.epoch_counter < 100);
    } finally {
      if (tfr != null) tfr.delete();
      if (dl != null) dl.delete();
    }
  }
}

