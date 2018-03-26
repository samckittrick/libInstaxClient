/*

Instax Client Library
By: Scott McKittrick

InstaxPrinterParameter - Class for managing and providing parameters for a given printer.

*/

#ifndef INSTAX_PRINTER_PARAMETER_HPP
#define INSTAX_PRINTER_PARAMETER_HPP

#include "InstaxStatus.hpp"
#include <exception>
#include <string>

namespace InstaxClient
{ 
  class InstaxPrinterParameter
  {
  public:
    //Maximum image sizes
    static const int SP1_HEIGHT = 640;
    static const int SP1_WIDTH = 480;
    static const int SP1_MAX_SIZE = 153600;

    static const int IMAGE_FORMAT_JPG = 2;

    InstaxPrinterParameter(INSTAX_TYPE);
    int getHeight();
    //int getWidth();
    //int getMaxSize();

  private:
    INSTAX_TYPE myType;

  };

  InstaxPrinterParameter::InstaxPrinterParameter(INSTAX_TYPE t)
  {
    myType = t;
  }

  int InstaxPrinterParameter::getHeight()
  {
    switch(myType)
      {
      case INSTAX_TYPE::SP1:
	return SP1_HEIGHT;
      }
  }

  int InstaxPrinterParameter::getWidth()
  {
    switch(myType)
      {
      case INSTAX_TYPE::SP1:
	return SP1_WIDTH;
      }
  }

  int InstaxPrinterParameter::getMaxSize()
  {
    switch(myType)
      {
      case INSTAX_TYPE::SP1:
        return SP1_MAX_SIZE;
      }
  }

}

#endif //INSTAX_PRINTER_PARAMETER_HPP
