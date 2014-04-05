#Carl Eadler
#Ucsc hackathon 2014
#wheres my bus?

default: compile

compile:
	g++ -Wall -Wextra -std=c++11 -o Server main.cpp -lzmq

clean:
	rm ./Server

run: compile
	./Server

all: clean compile run

gitadd: main.cpp Makefile
	git add main.cpp
	git add Makefile
	git commit