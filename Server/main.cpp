#include <iostream>
#include <unordered_map>
#include <list>
//#include <cstring>
#include "SFML/Network.hpp"



const long MAX_TIME = 30;//minutes
const unsigned MAX_PINGS = 10;//minutes

std::string errorMsg = "Error";
std::string receiMsg = "Thanks for your data!";

sf::Mutex mutex;

std::string last_ping = "";



/*class fivelet{
   public:
   double lati;
   double longi;
   int route;
   long uptime;
   char dir;
   fivelet(double a, double b, int c, long d, char e);
   ~fivelet();
   };
   
   fivelet::fivelet(double a, double b, int c, long d, char e)
   :lati(a), longi(b), route(c), uptime(d), dir(e)
   {
   std::cout << "Making new Fivelet!" << std::endl;
   std::cout << a << std::endl;
   std::cout << b << std::endl;
   std::cout << c << std::endl;
   std::cout << d << std::endl;
   std::cout << e << std::endl;
   std::cout.flush();
   };
   
   fivelet::~fivelet()
   {
   //empty deconstructor
   
   std::cout << "Deleting old Fivelet!" << std::endl;
   std::cout << lati   << std::endl;
   std::cout << longi  << std::endl;
   std::cout << route << std::endl;
   std::cout << uptime << std::endl;
   std::cout << dir    << std::endl;
   std::cout.flush();
   };
*/

std::vector<std::vector<std::string>> queues(20);


void addToDatabase(int route, std::string dataSet)
{
   mutex.lock();
   queues[route].push_back(dataSet);
   while((queues[route].size() > MAX_PINGS) /* || time.now() > queues[route].first().uptime + MAX_TIME */)
   {
      queues[route].erase(queues[route].begin());
   }
   mutex.unlock();
}

/*void addToDatabase(double lati, double longi, int route, long uptime, char direction)
   {
   mutex.lock();
   fivelet node(lati, longi, route, uptime, direction);
   queues[route].push_back(node);
   while((queues[route].size() > MAX_PINGS) /* || time.now() > queues[route].first().uptime + MAX_TIME )
   {
   queues[route].erase(queues[route].begin());
   }
   mutex.unlock();
}*/




std::vector<std::string> retrieveFromDatabase(int route)
{
   mutex.lock();
   std::vector<std::string> returnlist(0);
   try
   {
      if(route == 0)//all routes
      {
         //Run retrieveFromDataBase recursively for each route
         //combine vectors
         //AB.reserve( A.size() + B.size() ); // preallocate memory
         //AB.insert( AB.end(), A.begin(), A.end() );
         //AB.insert( AB.end(), B.begin(), B.end() );
      }
      else
      {
         while((queues[route].size() > MAX_PINGS) /* || time.now() > queues[route].first().uptime + MAX_TIME */)
         {
            queues[route].erase(queues[route].begin());
         }
         if(!queues[route].empty())
         {
            //auto q_iter = queues[route].begin();
            
            for(auto q_iter = queues[route].begin(); q_iter !=  queues[route].end(); q_iter++)
            {
               std::cout << "Adding item to return vector" << std::endl;
               returnlist.push_back(*q_iter);
            }
         }
      }
   }
   catch(...)
   {
      //do nothing
   }
   mutex.unlock();
   return returnlist;
}

/*std::vector<fivelet> retrieveFromDatabase(int route)
   {
   mutex.lock();
   if(route == 0)//all routes
   {
   //Run retrieveFromDataBase recursively for each route
   //combine vectors
   //AB.reserve( A.size() + B.size() ); // preallocate memory
   //AB.insert( AB.end(), A.begin(), A.end() );
   //AB.insert( AB.end(), B.begin(), B.end() );
   }
   else
   {
   while((queues[route].size() > MAX_PINGS) /* || time.now() > queues[route].first().uptime + MAX_TIME )
   {
   queues[route].erase(queues[route].begin());
   }
   if(!queues[route].empty())
   {
   std::vector<fivelet> returnlist;
   auto q_iter = queues[route].begin();
   
   for(auto q_iter: queues[route])
   {
   std::cout << "Adding item to return vector" << std::endl;
   returnlist.push_back(q_iter);
   }
   return returnlist;
   }
   }
   mutex.unlock();
}*/



void clientCommThread(sf::TcpSocket* newClient)
{
   newClient->setBlocking(true);
   std::cout << "SPAWNING THREAD!";
   std::cout.flush();
   std::size_t maxData = sizeof(char)*100;
   void* dataA = malloc(maxData);
   std::size_t received;
   
   
   
   while(newClient->receive(dataA, maxData, received) != sf::Socket::Disconnected)
   {
      std::cout << "Received Data! " << received << " # of bytes!" << std::endl;
      std::cout.flush();
      
      std::string messageString = std::string(static_cast<const char*>(dataA)) + '\0';
      std::cout << "Accepted String: " << messageString << std::endl;
      
      bool goodString = true;
      
      if(messageString[0] == 'G' || messageString[0] == 'S')
      {
         bool requestingPings = (messageString[0] == 'G');
         
         std::string rest = messageString.substr(1);
         std::string dataSet = rest;
         if(!requestingPings)
         {
            std::string data[5];
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
            std::cout << "String has enough parts." <<std::endl;
            
            double lati;
            double longi;
            int route;
            long upTime;
            char direction;
            
            if(goodString)
            {
               try
               {
                  lati = std::stod(data[0]);
                  longi = std::stod(data[1]);
                  route = std::stoi(data[2]);
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
            
            std::cout << "String has been parsed." <<std::endl;
            if(goodString)
            {
               
               std::cout << "String was good." <<std::endl;
               
               addToDatabase(route, dataSet);
               std::cout << "Added to database." <<std::endl;
               mutex.lock();
               last_ping = dataSet;
               mutex.unlock();
               
               std::cout << "Sending GOOD REPLY" << std::endl;
               std::cout.flush();
               newClient->send (receiMsg.c_str(), receiMsg.size());
            }
            if(!goodString)
            {
               //Bad in string
               std::cout << "Error_Reply2" << std::endl;
               newClient->send (errorMsg.c_str(), errorMsg.size());
            }
         }
         else //"REQUESTING PINGS
         {
            int route;
            try
            {
               route = std::stoi(rest);
            }
            catch(...)
            {
               //bad string
               std::cout << "Error_Reply3: String:" << messageString << std::endl;
               std::cout.flush();
               newClient->send (errorMsg.c_str(), errorMsg.size());
               continue;
            }
            std::cout << "Client is requesting pings." <<std::endl;
            std::vector<std::string> pingData = retrieveFromDatabase(route);
            //send many pings back
            std::cout << "Sending GOOD REPLY" << std::endl;
            std::cout.flush();
            
            if(pingData.empty())
            {
               std::cout << "Empty." << std::endl;
               newClient->send (errorMsg.c_str(), errorMsg.size());
            }
            else
            {
               std::string send_str = "@";
               send_str += pingData[0].data();
               for(int i = 1; i <  pingData.size(); i++)
               {
                  send_str += '$';
                  send_str += pingData[i].data();
               }
               send_str += "@";
               std::cout << "Sending: \"" << send_str << "\"" << std::endl;
               std::cout.flush();
               newClient->send (send_str.c_str(), send_str.size());
            }
         }
         
         
         std::cout << "Reply sent successfully." << std::endl;
      }
      else //Not G or S
      {
         //Bad in string
         std::cout << "Error_Reply1: String:" << messageString << std::endl;
         std::cout.flush();
         newClient->send (errorMsg.c_str(), errorMsg.size());
      }
      //Wait for new connection
      //spin new connection in its own thread
      std::cout << "Trying to loop." << std::endl;
      std::cout.flush();
   }
   std::cout << "Attempting to delete new client!"<<std::endl;
   newClient->disconnect();
   std::cout.flush();
   //delete newClient;
   std::cout << "EXITING THREAD!";
   std::cout.flush();
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
   std::vector<sf::Thread*> threads;
   
   
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
      sf::Thread* thread_ptr = new sf::Thread(&clientCommThread, client);
      threads.push_back(thread_ptr);
      thread_ptr->launch();
      //clientCommThread(client);
      std::cout.flush();
   }
   return 0;
   }      