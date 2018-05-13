/*

Instax Client Library
By: Scott McKittrick

InstaxPrinterConnection - Class representing the printer connection.

*/

#ifndef INSTAX_PRINTER_CONNECTION_HPP
#define INSTAX_PRINTER_CONNECTION_HPP

#include <string>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <unistd.h>
#include <exception>

namespace InstaxClient
{
  struct PrinterConnectionFailedException : public std::exception {
    std::string message;

    PrinterConnectionFailedException(std::string m) {
      message = m;
    }
    
    const char * what() {
      return message.c_str();
    }
  };
    
  class InstaxPrinterConnection
  {
  private:
    static constexpr const char* ipAddr = "192.168.0.4";
    static constexpr const char* port = "8080";

    int fd;

  public:
    void connect();
  };
}

#endif
