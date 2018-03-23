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

    //If we have recieved the full header, let's parse it
    if(readQueue.size() == PACKET_DATA_OFFSET)
      {
	printf("Need to parse the header");
	return true;
      }
  }
    
}
