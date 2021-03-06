#' Do not echo any loading of this file
.origEchoValue <- getOption("echo")
options(echo=FALSE)

#'#####################################################
#'
#'
#' h2o runit setup procedure and testing infrastructure
#'
#'
#'#####################################################

#'
#'
#' ----------------- Global variables -----------------
#'
#'
H2O.IP                      <<- "127.0.0.1"
H2O.PORT                    <<- 54321
ON.JENKINS.HADOOP           <<- FALSE
SEED                        <<- NULL
PROJECT.ROOT                <<- "h2o-3"
H2O.INTERNAL.HDFS.NAME.NODE <<- "172.16.2.176"

#'
#'
#' ----------------- Main setup procedure -----------------
#'
#'
h2oRunitSetup <-
function() {
    # source h2o-r/h2o-package/R
    root.path <- locate("h2o-package/R/", "h2o-r")
    src(root.path)   # overrides package load

    # source h2o-r/tests/Utils
    utils.path <- locate("tests/Utils/", "h2o-r")
    src.utils(utils.path)

    parseArgs(commandArgs(trailingOnly=TRUE)) # provided by --args

    Log.info("Load default packages. Additional required packages must be loaded explicitly.\n")
    default.packages()

    Log.info(paste0("Connect to h2o on IP: ",H2O.IP,", PORT: ",H2O.PORT,"\n"))
    h2o.init(ip = H2O.IP, port = H2O.PORT, startH2O = FALSE)

    setupRandomSeed()
    Log.info(paste0("[SEED] : ",SEED))

    sandbox()

    h2o.logAndEcho("------------------------------------------------------------")
    h2o.logAndEcho("")
    h2o.logAndEcho(paste("STARTING TEST: ", R.utils::commandArgs(asValues=TRUE)$"f"))
    h2o.logAndEcho("")
    h2o.logAndEcho("------------------------------------------------------------")
}

#'
#'
#' ----------------- Testing infrastructure -----------------
#'
#'

#' Locate a file given the pattern <bucket>/<path/to/file>
#' e.g. locate( "smalldata/iris/iris22.csv") returns the absolute path to iris22.csv
locate<-
function(pathStub, root.parent = NULL) {
  if (ON.JENKINS.HADOOP) {
    # HACK: jenkins jobs create symbolic links to smalldata and bigdata on the machine that starts the test. However,
    # in a h2o-hadoop cluster scenario, the other machines don't have this link. We need to reference the actual path,
    # which is /home/0xdiag/ on ALL jenkins machines. If ON.JENKINS.HADOOP is set by the run.py, pathStub MUST be
    # relative to /home/0xdiag/
    return(paste0("/home/0xdiag/",pathStub))
  } else {
    pathStub <- clean(pathStub)
    bucket <- pathStub[1]
    offset <- pathStub[-1]
    cur.dir <- getwd()
  }
  #recursively ascend until `bucket` is found
  bucket.abspath <- path.compute(cur.dir, bucket, root.parent)
  if (length(offset) != 0) return(paste(c(bucket.abspath, offset), collapse = "/", sep = "/"))
  else return(bucket.abspath)
}

#' Clean a path up: change \ -> /; remove starting './'; split
clean<-
function(p) {
  if (.Platform$file.sep == "/") {
    p <- gsub("[\\]", .Platform$file.sep, p)
    p <- unlist(strsplit(p, .Platform$file.sep))
  } else {
    p <- gsub("/", "\\\\", p)  # is this right?
    p <- unlist(strsplit(p, "\\\\"))
  }
  p
}

#' Compute a path distance.
#'
#' We are looking for a directory `root`. Recursively ascend the directory structure until the root is found.
#' If not found, produce an error.
#'
#' @param cur.dir: the current directory
#' @param root: the directory that is being searched for
#' @param root.parent: if not null, then the `root` must have `root.parent` as immediate parent
#' @return: Return the absolute path to the root.
path.compute<-
function(cur.dir, root, root.parent = NULL) {
  parent.dir  <- dirname(cur.dir)
  parent.name <- basename(parent.dir)

  # root.parent is null
  if (is.null(root.parent)) {

    # first check if cur.dir is root
    if (basename(cur.dir) == root) return(normalizePath(cur.dir))

    # next check if root is in cur.dir somewhere
    if (root %in% dir(cur.dir)) return(normalizePath(paste(cur.dir, .Platform$file.sep, root, sep = "")))

    # the root is the parent
    if (parent.name == root) return(normalizePath(paste(parent.dir, .Platform$file.sep, root, sep = "")))

    # the root is h2o-dev, check the children here (and fail if `root` not found)
    if (parent.name == PROJECT.ROOT) {
      if (root %in% dir(parent.dir)) return(normalizePath(paste(parent.dir, .Platform$file.sep, root, sep = "")))
      else stop(paste("Could not find the dataset bucket: ", root, sep = "" ))
    }
  # root.parent is not null
  } else {

    # first check if cur.dir is root
    if (basename(cur.dir) == root && parent.name == root.parent) return(normalizePath(cur.dir))

    # next check if root is in cur.dir somewhere (if so, then cur.dir is the parent!)
    if (root %in% dir(cur.dir) && root.parent == basename(cur.dir)) {
      return(normalizePath(paste(cur.dir, .Platform$file.sep, root, sep = ""))) }

    # the root is the parent
    if (parent.name == root && basename(dirname(parent.dir)) == root.parent) {
      return(path.compute(parent.dir, root, root.parent)) }

    # fail if reach h2o-dev
    if (parent.name == PROJECT.ROOT) stop("Reached the root h2o-dev. Didn't find the bucket with the root.parent")
  }
  return(path.compute(parent.dir, root, root.parent))
}

#' Source the files in h2o-r/h2o-package/R/
src <-
function(ROOT.PATH) {
  to_src <- c("/classes.R", "/connection.R", "/constants.R", "/logging.R", "/communication.R", "/kvstore.R", "/frame.R",
              "/astfun.R", "/import.R", "/parse.R", "/export.R", "/models.R", "/edicts.R", "/gbm.R","/glm.R", "/glrm.R",
              "/kmeans.R", "/deeplearning.R", "/randomforest.R", "/naivebayes.R", "/pca.R", "/svd.R", "/locate.R",
              "/grid.R")
  invisible(lapply(to_src,function(x){source(paste(ROOT.PATH, x, sep = ""))}))
}

#' Source the utilities in h2o-r/tests/Utils/
src.utils<-
function(ROOT.PATH) {
  to_src <- c("/utilsR.R", "/pcaR.R", "/deeplearningR.R", "/glmR.R", "/glrmR.R", "/gbmR.R", "/kmeansR.R",
              "/naivebayesR.R", "/gridR.R", "/shared_javapredict.R")
  invisible(lapply(to_src,function(x){source(paste(ROOT.PATH, x, sep = ""))}))
}

#' Load a handful of packages automatically. Runit tests that require additional packages must be loaded explicitly
default.packages <-
function() {
  to_require <- c("jsonlite", "RCurl", "RUnit", "R.utils", "testthat", "ade4", "glmnet", "gbm", "ROCR", "e1071",
                  "tools", "statmod", "fpc", "cluster")
  if (Sys.info()['sysname'] == "Windows") {
    options(RCurlOptions = list(cainfo = system.file("CurlSSL", "cacert.pem", package = "RCurl"))) }
  invisible(lapply(to_require,function(x){require(x,character.only=TRUE,quietly=TRUE,warn.conflicts=FALSE)}))
}

setupRandomSeed<-
function() {
    maxInt <- .Machine$integer.max
    SEED <<- sample(maxInt, 1)
    set.seed(SEED)
}

h2oRunitSetup()

options(echo=.origEchoValue)
options(scipen=999)
rm(list=c(".origEchoValue"))
