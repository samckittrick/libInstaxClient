cmake_minimum_required (VERSION 2.8)

project(libInstaxClient)

macro(use_cxx11)
  if (CMAKE_VERSION VERSION_LESS "3.1")
    if (CMAKE_CXX_COMPILER_ID STREQUAL "GNU")
      MESSAGE( STATUS "CMake too old to use CMAKE_CXX_STANDARD. This macro is used instead and only supports GCC") 
      set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
    endif ()
  else ()
    set (CMAKE_CXX_STANDARD 11)
  endif ()
endmacro(use_cxx11)

use_cxx11()

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
