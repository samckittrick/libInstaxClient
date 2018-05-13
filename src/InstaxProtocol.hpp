/**

Instax Client Library
By: Scott McKittrick

InstaxProtocol - Class for higher level functions for interacting with printer.

*/

#ifndef INSTAX_PROTOCOL_HPP
#define INSTAX_PROTOCOL_HPP

#include <stdint.h>

namespace InstaxClient
{
  class InstaxProtocol
  {
  private:
    //SID values for sending to the printer

    /* Establishes versions of firmware and such to make sure proper protocol is used. */
    static const int SID_GET_VERSION = 0xC0;
    /* Gets the name of the printer. Checks if it's SP-1 or 2? */
    static const int SID_GET_NAME = 0xC2;
    /* Retrieves shading data from the printer */
    static const int SID_SHADING_DATA = 0x40;
    /* Checks the versions for all functions in the function map. */
    static const int SID_FUNCTION_VERSION = 0xC4;
    /*	Checks the state of the printer, i.e. battery state, door state, and the numbe of photos left. */
    static const int SID_GET_INSTAXPARAMETER = 0xC1;
    /* Lock the instax. Not sure what it does. */
    static const int SID_LOCK_INSTAX = 0xB3;
    /* Prepare to send image to the printer. Tells printer to clear out it's buffer. */
    static const int SID_PREP_PRINTER = 0x50;
    /* Send immediately before sending image data. */
    static const int SID_TRANSFER_START = 0x51;
    /* Image data packet. */
    static const int SID_SEND_IMAGE = 0x52;
    /* Marks the end of the image transfer */
    static const int SID_TRANSFER_END = 0x53;
    /* Command the printer to begin printing */
    static const int SID_START_PRINT = 0xB0;
    /* Check the progress of the print. */
    static const int SID_CHECK_PROGRESS = 0xC3;

    // Response codes from the printer
    static const int RSP_RET_OK = 0x00;
    static const int RSP_RET_HOLD = 0x7F;
    static const int RSP_E_PRINTING = 0xA3;
    static const int RSP_E_EJECTING = 0xA4;

    uint16_t password;
    uint32_t UID;
    InstaxPrinterConnection* conn;

  }
}
