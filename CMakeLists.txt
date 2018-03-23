cmake_minimum_required (VERSION 2.6)

project(libInstaxClient)

#Download and build the google test project
#Example taken from https://github.com/Crascit/DownloadProject/blob/master/CMakeLists.txt
include(DownloadProject.cmake)
download_project(PROJ googletest
  GIT_REPOSITORY https://github.com/google/googletest.git
  GIT_TAG master
  ${UPDATE_DISCONNECTED_IF_AVAILABLE}
  )

# Prevent GoogleTest from overriding our compiler/linker options
# when building with Visual Studio
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)

add_subdirectory(${googletest_SOURCE_DIR} ${googletest_BINARY_DIR})

# When using CMake 2.8.11 or later, header path dependencies
# are automatically added to the gtest and gmock targets.
# For earlier CMake versions, we have to explicitly add the
# required directories to the header search path ourselves.
if (CMAKE_VERSION VERSION_LESS 2.8.11)
  include_directories("${gtest_SOURCE_DIR}/include"
    "${gmock_SOURCE_DIR}/include")
endif()

#End coogle test stuff

include_directories("${PROJECT_SOURCE_DIR}/src")
add_subdirectory("src")
