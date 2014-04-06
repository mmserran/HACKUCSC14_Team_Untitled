#Carl Eadler
#Ucsc hackathon 2014
#wheres my bus?


libpath = /home/ec2-user/SFML-2.1/lib
libs    =  -lsfml-system -lsfml-network 
incpath = /home/ec2-user/SFML-2.1/include
output = ./Server

default: compile

compile: ./main.o
	g++ -g -Wall -Wextra -std=c++11 -o Server main.o -L$(libpath) $(libs)
 
%.o: %.cpp
	g++ -g -c -std=c++11 $< -I$(incpath)


#Old target
compile2:
	g++ -g -Wall -Wextra -std=c++11 -o Server main.cpp -lzmq

clean:
	rm main.o
	rm $(output)

run: $(output)
	$(output)

all: clean compile run

gitadd: main.cpp Makefile
	git add main.cpp
	git add zmq.hpp
	git add Makefile
	git commit