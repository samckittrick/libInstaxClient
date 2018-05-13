#include <iostream>
#include "InstaxPrinterConnection.hpp"

int main()
{
  std::cout << " Starting connection\n";

  InstaxClient::InstaxPrinterConnection conn;

  conn.connect();

  return  0;
}
