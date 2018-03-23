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
    return false;
  }
}
