################################################################################
##
## Verifying that Python can support user-specified handling of the first line
## of the file as either headers (1), to-be-guessed (0), or data (-1).
##
################################################################################
import sys, urllib
sys.path.insert(1, "../../")
import h2o, tests


def header():

    path = "smalldata/jira/hexdev_29.csv"

    fhex_header_true = h2o.import_file(tests.locate(path), header=1)

    fhex_header_unknown = h2o.import_file(tests.locate(path), header=0)

    fhex_header_false = h2o.import_file(tests.locate(path), header=-1)

    fhex_header_unspecified = h2o.import_file(tests.locate(path))

    try:
        h2o.import_file(tests.locate(path), header=2)
        assert False
    except ValueError:
        pass

    assert fhex_header_true._nrows == fhex_header_false._nrows - 1
    assert fhex_header_unknown._nrows == fhex_header_false._nrows or fhex_header_unknown._nrows == fhex_header_true._nrows
    assert fhex_header_unspecified._nrows == fhex_header_false._nrows or fhex_header_unspecified._nrows == fhex_header_true._nrows

if __name__ == "__main__":
    tests.run_test(sys.argv, header)
