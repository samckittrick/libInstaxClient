cmake_minimum_required (VERSION 2.6)

file(GLOB libInstaxClient_src
  "*.hpp"
  "*.cpp"
  )

add_library(libInstaxClient-Static STATIC InstaxFrame.hpp
  InstaxFrame.cpp
  InstaxStatus.hpp
  )

