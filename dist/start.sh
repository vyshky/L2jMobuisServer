#!/bin/bash

#Start LoginServer
cd ./login
./LoginServer.sh
echo "Login server started"

#Start GameServer
cd ../game
./GameServer.sh
echo "Game server started"