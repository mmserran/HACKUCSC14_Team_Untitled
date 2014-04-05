#include <iostream>
#include "zmq.hpp"
#include <cassert>
int main()
{
   std::cout << "hi" << std::endl;
   //Say hi
   zmq::context_t context (1);
   zmq::socket_t socket (context, ZMQ_REP);
   socket.bind ("tcp://eth0:5867");
   
   while(true)
   {
      zmq::message_t message;
      socket.recv (&request);
      //Wait for new connection
      //spin new connection in its own thread
   }
   return 0;
}