cmake_minimum_required (VERSION 2.6)

project(libInstaxClient)

include_directories("${PROJECT_SOURCE_DIR}/src")
add_subdirectory("src")


enable_testing()
add_subdirectory(test)
#Add check target to output detailed test results
add_custom_target(check COMMAND ${CMAKE_CTEST_COMMAND} --verbose)

MESSAGE( STATUS "CMake Build Complete.\n
Use \"make\" to build
Use \"make test\" to test
Use \"make check\" to show detailed test results")
