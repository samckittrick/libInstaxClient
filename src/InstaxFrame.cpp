
/**
Instax Client Library
By: Scott McKittrick

InstaxFrame - Class for building and managing data packets sent between client and printer

*/

#include "InstaxFrame.hpp"

namespace InstaxClient
{
  /*
    The frame is built by inserting one byte at a time. 
    Each call to this function takes a byte. It will return true when a full packet is recieved
    It will throw an exception when something is wrong with the frame.
  */
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

  /*
  The Instax Printer uses a modified TCP Checksum
  Essentially it is a normal TCP checksum but instead of a 16 bit value, they use a 32 bit value
  With the original 16 bit value as the upper two bytes and two static numbers as the lower two.
  */
  const bool InstaxFrame::verifyChecksum()
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
  uint32_t InstaxFrame::generateChecksum(const std::vector<uint8_t>& packet)
  {
    uint16_t calcSum = 0;
    for(int i = 0; i < packet.size(); i++)
      {
	calcSum += packet[i];
      }

    uint32_t result = ~calcSum << 16;

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

  const uint8_t InstaxFrame::getSID()
  {
    return SID;
  }

  const uint16_t InstaxFrame::getLength()
  {
    return length;
  }

  const uint32_t InstaxFrame::getUID()
  {
    return UID;
  }

  const uint16_t InstaxFrame::getPassword()
  {
    return password;
  }

  //Copys the packet data into the provided vector
  //Returns the length of the copied data
  const int InstaxFrame::getPacketData(std::vector<uint8_t> * dest)
  {
    //Check that there is data to copy
    int dataLen = length - PACKET_DATA_OFFSET - PACKET_CHECKSUM_LENGTH;
    if(dataLen == 0)
      //there is no data in the packet
      return 0;

    std::vector<uint8_t>::iterator dataStart = readQueue.begin() + PACKET_DATA_OFFSET;
    std::vector<uint8_t>::iterator dataEnd = readQueue.end() - PACKET_CHECKSUM_LENGTH;

    dest->assign(dataStart, dataEnd);
    return dataLen;
  }

      /*
      Build a frame ready to be sent over the network.
      Takes a pointer to the vector the packet should be added
      Takes packet parameters and vector of data
    */
  void InstaxFrame::buildOutgoingPacket(std::vector<uint8_t> * packet,
                                    const uint8_t startByte,
                                    const uint8_t SID,
                                    const uint32_t UID,
                                    const uint16_t password,
                                    const std::vector<uint8_t>& data)
  {
    int packetLength = data.size() + PACKET_DATA_OFFSET + PACKET_CHECKSUM_LENGTH;

    //ToDo Handle packets that are too long for the 2 byte packet length value. Throw an exception
    
    packet->push_back(startByte);
    
    packet->push_back(SID);
    
    packet->push_back((packetLength >> 8) & 0x00FF);
    packet->push_back(packetLength & 0x00FF);

    packet->push_back((UID >> 24) & 0xFF);
    packet->push_back((UID >> 16) & 0xFF);
    packet->push_back((UID >> 8) & 0xFF);
    packet->push_back(UID & 0xFF);

    packet->push_back((password >> 8) & 0xFF);
    packet->push_back(password & 0xFF);

    packet->push_back(0x00);
    packet->push_back(0x00);
    
    packet->insert(packet->end(), data.begin(), data.end());

    uint32_t cksum = generateChecksum(*packet);
    packet->push_back((cksum >> 24) & 0xFF);
    packet->push_back((cksum >> 16) & 0xFF);
    packet->push_back((cksum >> 8) & 0xFF);
    packet->push_back(cksum & 0xFF);
  }
    
}
