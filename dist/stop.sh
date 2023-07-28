#!/bin/bash

# killall LoginServerTask
kill $(ps aux | grep LoginServerTask | awk '{print $2}')
echo "Login server is stop"

# killall GameServerTask.
kill $(ps aux | grep GameServerTask. | awk '{print $2}')
echo "Game server is stop"

kill $(ps aux | grep java | grep GameServer.jar | awk '{print $2}')
kill $(ps aux | grep java | grep LoginServer.jar | awk '{print $2}')

echo "Done"