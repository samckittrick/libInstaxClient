#include "gtest/gtest.h"
#include "InstaxFrame.hpp"
#include <vector>

TEST(InstaxFrameTest, parseHeaderTest)
{
  std::vector<uint8_t> incoming = { 0x2A, 0xC2, 0x00, 0x18, 0x49, 0xB6, 0xAB, 0xC9,
				    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x4A,
				    0x53, 0x50, 0x2D, 0x31, 0xFB, 0x3D, 0x0D, 0x0A };

  InstaxClient::InstaxFrame f;

  bool result = f.recvPacket(incoming[0]);
  //WE haven't recieved the packet yet, so return false
  ASSERT_FALSE(result);

  for(int i = 1; i < incoming.size(); i++)
    {
      result = f.recvPacket(incoming[i]);
    }

  //We should have recieved the whole packet. Should return true
  ASSERT_TRUE(result);

  ASSERT_EQ(f.getSID(), 0xC2);
  ASSERT_EQ(f.getLength(), 24);
  ASSERT_EQ(f.getUID(), 0x49B6ABC9);
  ASSERT_EQ(f.getPassword(), 0x0000);
}

TEST(InstaxFrameTest, checksumFailTest)
{
  //Base valid packet
   std::vector<uint8_t> incoming = { 0x2A, 0xC2, 0x00, 0x18, 0x49, 0xB6, 0xAB, 0xC9,
				     0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x4A,
				     0x53, 0x50, 0x2D, 0x31, 0x00, 0x00, 0x0D, 0x0A };
   
   InstaxClient::InstaxFrame f;

   bool result = f.recvPacket(incoming[0]);
   //WE haven't recieved the packet yet, so return false
   ASSERT_FALSE(result);

   for(int i = 1; i < incoming.size() - 1; i++)
     {
       result = f.recvPacket(incoming[i]);
     }

   ASSERT_THROW(f.recvPacket(incoming[incoming.size() - 1]), InstaxClient::InvalidFrameException);
}
  

