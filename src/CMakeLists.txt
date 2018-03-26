cmake_minimum_required (VERSION 2.6)

add_library(libInstaxClient-Static STATIC InstaxFrame.hpp
  InstaxFrame.cpp
  InstaxStatus.hpp
  InstaxPrinterParameters.hpp
  )

