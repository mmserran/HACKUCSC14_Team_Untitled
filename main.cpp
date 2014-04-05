#include <zmq.h>
#include <iostream>

int main(){
   
   std::cout << "heya."<< std::endl;
   while(true)
   {
     std::cout << "Trying to bind..." << std::endl;
     zmq_bind(zmq_socket(zmq_ctx_new(), ZMQ_REP), "tcp://eth0:5837");
   }
   return 0;
}