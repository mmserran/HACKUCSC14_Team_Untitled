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
      socket.recv (&message);
      std::string messageString = std::string(static_cast<char*>(message.data())) + '\0';
      std::cout << "received data!"<<std::endl;
      std::cout << messageString << std::endl;
      
      
      
      
      zmq::message_t reply (50);
      memcpy ((void *) reply.data (), "HWORLD DUDE", 40);
      std::cout << "Sending REPLY" << std::endl;
      socket.send (reply);
      //Wait for new connection
      //spin new connection in its own thread
   }
   return 0;
}