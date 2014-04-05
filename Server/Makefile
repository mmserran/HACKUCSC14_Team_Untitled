#Carl Eadler
#Ucsc hackathon 2014
#wheres my bus?

default: compile

compile:
	g++ -Wall -Wextra -std=c++11 -o Server main.cpp

clean:
	rm ./Server

run: compile
	./Server

all: clean compile run
