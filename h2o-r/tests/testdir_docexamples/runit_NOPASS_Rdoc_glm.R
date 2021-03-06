setwd(normalizePath(dirname(R.utils::commandArgs(asValues=TRUE)$"f")))
source('../h2o-runit.R')

test.rdocglm.golden <- function() {
	
    # Run GLM of CAPSULE ~ AGE + RACE + PSA + DCAPS
    prostatePath = system.file("extdata", "prostate.csv", package = "h2o")
    prostate.hex = h2o.importFile(path = prostatePath, destination_frame = "prostate.hex")
    h2o.glm(y = "CAPSULE", x = c("AGE","RACE","PSA","DCAPS"), training_frame = prostate.hex,
             family = "binomial", nfolds = 0, alpha = 0.5, lambda_search = FALSE)

    # Run GLM of VOL ~ CAPSULE + AGE + RACE + PSA + GLEASON
    myX = setdiff(colnames(prostate.hex), c("ID", "DPROS", "DCAPS", "VOL"))
    h2o.glm(y = "VOL", x = myX, training_frame = prostate.hex, family = "gaussian",
             nfolds = 0, alpha = 0.1, lambda_search = FALSE)

    
}

doTest("R Doc GLM example", test.rdocglm.golden)

