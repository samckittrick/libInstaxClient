
/**
Instax Client Library
By: Scott McKittrick

InstaxFrame - Class for building and managing data packets sent between client and printer

*/

#include "InstaxFrame.hpp"

namespace InstaxClient
{
  bool InstaxFrame::recvPacket(uint8_t byte)
  {
    readQueue.push_back(byte);

    //If we haven't recieved the full header yet
    if(readQueue.size() < PACKET_DATA_OFFSET)
      return false;

    //If we have recieved the full header, let's parse it
    if(readQueue.size() == PACKET_DATA_OFFSET)
      {
	parseHeader();
	return false;
      }

    if(readQueue.size() == length)
      {

	//Verify the checksum
	if(verifyChecksum())
	  {
	    return true;
	  }
	else
	  {
	    throw InvalidFrameException("Invalid frame checksum");
	  }  
      }

    //if we get here, we must be waiting on more data bytes
    return false;
  }

  //The Instax Printer uses a modified TCP Checksum
  //Essentially it is a normal TCP checksum but instead of a 16 bit value, they use a 32 bit value
  // With the original 16 bit value as the upper two bytes and two static numbers as the lower two.
  bool InstaxFrame::verifyChecksum()
  {
    uint16_t cksum = 0;
    int checksumOffset = readQueue.size() - PACKET_CHECKSUM_LENGTH; 
    //Start by checking the magic numbers
    if(readQueue[checksumOffset + 2] != PACKET_CHECKSUM_MAGIC1)
      {
	return false;
      }

    if(readQueue[checksumOffset + 3] != PACKET_CHECKSUM_MAGIC2)
      {
	return false;
      }
    
    //Start by adding all data bytes together 
    for(int i = 0; i < checksumOffset; i++)
      {
	cksum += readQueue[i];
      }

    //Now we put in the recieved checksum and the result should equal 0xFFFF
    cksum += (readQueue[checksumOffset] << 8) | readQueue[checksumOffset + 1];
    return cksum == 0xFFFF;
    
  }

  //takes a data array and calculates the checksum
  uint32_t InstaxFrame::generateChecksum(std::vector<uint8_t> packet)
  {
    uint16_t calcSum = 0;
    for(int i = 0; i < packet.size(); i++)
      {
	calcSum += packet[i];
      }

    uint32_t result = calcSum << 16;

    //Add in the magic numbers
    result |= PACKET_CHECKSUM_MAGIC1 << 8;
    return result | PACKET_CHECKSUM_MAGIC2;
  }

  void InstaxFrame::parseHeader()
  {
    //Parse out Values
    SID = readQueue[PACKET_SID_OFFSET];

    length = readQueue[PACKET_LENGTH_OFFSET] << 8 | readQueue[PACKET_LENGTH_OFFSET + 1];

    UID = readQueue[PACKET_UID_OFFSET] << 24;
    UID |= readQueue[PACKET_UID_OFFSET + 1] << 16;
    UID |= readQueue[PACKET_UID_OFFSET + 2] << 8;
    UID |= readQueue[PACKET_UID_OFFSET + 3];

    password = readQueue[PACKET_PASSWORD_OFFSET] << 8 | readQueue[PACKET_PASSWORD_OFFSET +1];
  }

  uint8_t InstaxFrame::getSID()
  {
    return SID;
  }

  uint16_t InstaxFrame::getLength()
  {
    return length;
  }

  uint32_t InstaxFrame::getUID()
  {
    return UID;
  }

  uint16_t InstaxFrame::getPassword()
  {
    return password;
  }
    
}
