
enable_testing()

set(CTEST_TEST_TIMEOUT 20)

add_test(MuscleVersion muscle --version)

macro (do_test cxa result)
  set (CXA_FILE "${CMAKE_INSTALL_PREFIX}/share/muscle/examples/cxa/${cxa}")
  add_test (MuscleTest-${cxa} muscle --cxa_file ${CXA_FILE} --main --allkernels --autoquit)
  set_tests_properties (MuscleTest-${cxa} PROPERTIES PASS_REGULAR_EXPRESSION ${result})
  set_tests_properties (MuscleTest-${cxa} PROPERTIES TIMEOUT 30)
endmacro (do_test)

macro (do_test2 cxa result)
  set (CXA_FILE "${CMAKE_INSTALL_PREFIX}/share/muscle/examples/cxa/${cxa}")
  add_test (Muscle2Test-${cxa} muscle2 --cxa ${CXA_FILE} --main --allkernels)
  set_tests_properties (Muscle2Test-${cxa} PROPERTIES PASS_REGULAR_EXPRESSION ${result})
  set_tests_properties (Muscle2Test-${cxa} PROPERTIES TIMEOUT 30)
endmacro (do_test2)


do_test("SimpleExample.cxa.rb" ] "got: 2.0")
do_test("Triangle.cxa.rb"  "Got from Three: 3.0")
do_test("PingPong.cxa.rb" "All submodel instances have finished")
do_test("SimpleSubmodelExample.cxa.rb"  "(1.0\t2.0\t)")
if (BUILD_CPP)
    do_test("NativeExample2.cxa.rb"  "got: 2.0")
    do_test("TransmutableExample2.cxa.rb"  "t: 2")
    do_test("LHC2.cxa.rb"  "LHC:Final proton energy: 12")
endif (BUILD_CPP)

do_test2("SimpleExample.cxa.rb" ] "got: 2.0")
do_test2("Triangle.cxa.rb"  "Got from Three: 3.0")
do_test2("PingPong.cxa.rb" "All submodel instances have finished")
do_test2("SimpleSubmodelExample.cxa.rb"  "(1.0\t2.0\t)")
if (BUILD_CPP)
    do_test2("NativeExample2.cxa.rb"  "got: 2.0")
    do_test2("TransmutableExample2.cxa.rb"  "t: 2")
    do_test2("LHC2.cxa.rb"  "LHC:Final proton energy: 12")
endif (BUILD_CPP)



#legacy examples
#do_test("NativeExample.cxa.rb" "r w" "got: 2.0")

