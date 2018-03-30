cmake_minimum_required (VERSION 2.8)

add_library(libInstaxClient-Static STATIC InstaxFrame.hpp
  InstaxFrame.cpp
  InstaxStatus.hpp
  InstaxPrinterParameters.hpp
  )

