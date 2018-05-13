/*

Instax Client Library 
By: Scott McKittrick

*/

#ifndef INSTAX_PRINTER_PARAMETER_HPP
#define INSTAX_PRINTER_PARAMETER_HPP

#include "InstaxStatus.hpp"

namespace InstaxClient
{
  struct InstaxPrinterParam {
    //Printer Type
    INSTAX_TYPE myType;

    //Image Parameters
    int IMAGE_HEIGHT;
    int IMAGE_WIDTH;
    int MAX_IMAGE_SIZE;
    int IMAGE_FORMAT_JPG;
    
    //Firmware information
    int BOOT_VER;
    int FIRMWARE_VER;
    int FPGA_VER;
    int funtionVersions[10];
  };

  //Specify printer parameters for SP1 Printers
  const struct InstaxPrinterParam SP1_PARAM = {
    INSTAX_TYPE::SP1,
    640,
    480,
    153600,
    2,
    0x0301,
    0x0322,
    0x0213,
    { 0x01, 0x01, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}
  }
    
}
#endif
