/*

Instax Client Library
By: Scott McKittrick

*/

#include "InstaxPrinterConnection.hpp"

namespace InstaxClient
{
  
  void InstaxPrinterConnection::connect()
  {
    int status;
    struct addrinfo hints;
    struct addrinfo *servinfo, *p;

    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;

    if((status = getaddrinfo(ipAddr, port, &hints, &servinfo)) != 0) {
      fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(status));
      throw PrinterConnectionFailedException("Failed to get address info");
    }

    for(p = servinfo; p != NULL; p = p->ai_next)
      {
	if((fd = socket(p->ai_family, p->ai_socktype, p->ai_protocol)) == -1)
	  {
	    perror("Failed to get this socket. Trying next...");
	    continue;
	  }

	if(::connect(fd, p->ai_addr, p->ai_addrlen) == -1 )
	  {
	    ::close(fd);
	    perror("Failed to connec to this socket. Trying next...");
   
	    continue;
	  }
	break;
      }

    if(p == NULL)
      {
	fprintf(stderr, "Failed to connect");
	throw PrinterConnectionFailedException("Failed to connect to printer");
      }
  }
}
