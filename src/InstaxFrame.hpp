/** 

Instax Client Library
By: Scott McKittrick

InstaxFrame - Class for building and managing data packets sent between client and printer

*/

#ifndef INSTAX_FRAME_HPP
#define INSTAX_FRAME_HPP

#include <vector>
#include <stdint.h>

namespace InstaxClient
{
  class InstaxFrame
  {
  private:
    //Offsets and lengths for fields in the packet

    //Offset for the SID (command) field
    static const int PACKET_SID_OFFSET = 1;
    //Offset for the packet length field
    static const int PACKET_LENGTH_OFFSET = 2;
    //Offset for the Device ID field
    static const int PACKET_UID_OFFSET = 4;
    //Offset for the password field
    static const int PACKET_PASSWORD_OFFSET = 8;
    //Offset for the Body of the packet - Note header length is data offset - 0
    static const int PACKET_DATA_OFFSET = 12;
    //Offset for string data in packet - There are always a few fields in the data structure that are reserved for status. A string only packet will have the string agfter these fields
    static const int PACKET_STRING_DATA_OFFSET = 16;
    //The length of the checksum field - the offset varies, but it is always the last <checksum length> bytes of the packet
    static const int PACKET_CHECKSUM_LENGTH = 4;
    
    //These two numbers are always part of the checksum. they are always static
    static const int PACKET_CHECKSUM_MAGIC1 = 13;
    static const int PACKET_CHECKSUM_MAGIC2 = 10;
    
    //Every packet begins with a byte indicating whether it was sent by the printer or the client
    static const uint8_t PACKET_STARTBYTE_CLIENT = 0x24;
    static const uint8_t PACKET_STARTBYTE_PRINTER = 0x2A;
    
    //The default password values. The library doesn't have a provision for changing passwords yet.
    static const uint16_t CLIENT_DEFAULT_PASSWORD = 0xFFFF;
    static const uint16_t PRINTER_DEFAULT_PASSWORD = 0x0000;
    
    std::vector<uint8_t> readQueue;
    uint8_t SID;
    uint16_t length;
    uint32_t UID;
    uint16_t password;

    //void parseHeader();

  public:
    //InstaxFrame();
    bool recvPacket(uint8_t byte);
  
  };
}
    
#endif
