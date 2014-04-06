#include <iostream>
#include <unordered_map>
#include <queue>
#include "SFML/Network.hpp"



const long MAX_TIME = 30;//minutes
const unsigned MAX_PINGS = 10;//minutes

std::string errorMsg = "Error";



class fourlet{
   public:
   double lati;
   double longi;
   long uptime;
   char dir;
   fourlet(double a, double b, long c, char d);
};

fourlet::fourlet(double a, double b, long c, char d):lati(a), longi(b), uptime(c), dir(d){};
std::unordered_map<std::string, std::queue<fourlet>> queues;


void addToDatabase(double lati, double longi, std::string route, long uptime, char direction)
{
   fourlet node(lati, longi, uptime, direction);
   queues[route].push(node);
   while((queues[route].size() > MAX_PINGS) /* || time.now() > queues[route].first().uptime + MAX_TIME */)
   {
      queues[route].pop();
   }
}





/**********MAIN**********/
int main()
{
   std::cout << "hi" << std::endl;
   sf::TcpListener listener;
   if (listener.listen(5867) != sf::Socket::Done)
   {
      std::cout << "Could not listen on port 5867. "<<std::endl;
      return 1;
   }
   //Say hi
   //zmq::context_t context (1);
   //zmq::socket_t socket (context, ZMQ_DEALER);
   //socket.bind ("tcp://eth0:5867");
   
   //int opt = 1;
   //socket.setsockopt(ZMQ_RCVHWM, &opt, sizeof(opt));
   
   while(true)
   {
      std::cout << "Entering loop thing";
      std::cout.flush();
      sf::TcpSocket* client = new sf::TcpSocket;
      std::cout << "Waiting on new client...";
      std::cout.flush();
      if (listener.accept(*client) != sf::Socket::Done)
      {
         std::cout << "Got bad client!";
         std::cout.flush();
         //some kind of error
         delete client;
         continue;
      }
      std::cout << "Got good client!";
      std::cout.flush();
      sf::Packet dataPack;
      
      std::size_t maxData = sizeof(char)*100;
      void* dataA = malloc(maxData);
      std::size_t received;
      
      
      
      while(client->receive(dataA, maxData, received) != sf::Socket::Disconnected){
         std::cout << "Received Data!";
         std::cout.flush();
         //zmq::message_t message;
         //socket.recv (&message);
         std::string messageString = std::string(static_cast<const char*>(dataA)) + '\0';
         std::cout << "received data!"<<std::endl;
         std::cout << messageString << std::endl;
         
         if(messageString[0] == 'G' || messageString[0] == 'S')
         {
            bool requestingPings = (messageString[0] == 'G');
            
            std::string rest = messageString.substr(1);
            std::string data[5];
            
            bool goodString = true;
            for(int i = 0; i < 5; i++)
            {
               size_t delimLocation = rest.find('*');
               if(delimLocation == std::string::npos) //no delim found
               {
                  goodString = false;
               }
               else
               {
                  data[i] = rest.substr(0, delimLocation); //sets data[i] from begin
                  // to next '*' character
                  rest = rest.substr(delimLocation+1); //sets rest to everything after
                  //the first '*'
                  std::cout << "Part " << i << ": "<<data[i] << std::endl;
               }
            }
            
            double lati;
            double longi;
            std::string route;
            long upTime;
            char direction;
            
            if(goodString)
            {
               try
               {
                  lati = std::stod(data[0]);
                  longi = std::stod(data[1]);
                  route = data[2];
                  upTime = std::stol(data[3]);
                  direction = data[4][0];
               }
               catch(...)
               {
                  std::cout << "Improperly formatted data. Throwing away string!"<<std::endl;
                  goodString = false;
               }
               
               //std::cout << "Lati"     << ": " << lati      << std::endl;
               //std::cout << "Longi "   << ": " << longi     << std::endl;
               //std::cout << "Route"    << ": " << route     << std::endl;
               //std::cout << "Time"     << ": " << upTime    << std::endl;
               //std::cout << "Direction"<< ": " << direction << std::endl;
            }
            if(goodString)
            {
               if(!requestingPings)
               {
                  addToDatabase(lati, longi, route, upTime, direction); //should thread and free this to make connections
                  
                  
                  std::cout << "Sending REPLY3" << std::endl;
                  client->send (errorMsg.c_str(), errorMsg.size());
               }
               else
               {
                  //send many pings back
               }
            }
            if(!goodString)
            {
               //Bad in string
               std::cout << "Error_Reply2" << std::endl;
               client->send (errorMsg.c_str(), errorMsg.size());
            }
         }
         else //Not G or S
         {
            //Bad in string
            std::cout << "Error_Reply1" << std::endl;
            client->send (errorMsg.c_str(), errorMsg.size());
         }
         //Wait for new connection
         //spin new connection in its own thread
      }
      delete client;
   }
   return 0;
}