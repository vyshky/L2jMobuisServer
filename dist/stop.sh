#!/bin/bash

killall LoginServerTask
echo "Login server is stop"

killall GameServerTask.
echo "Game server is stop"

killall java
echo "Done"