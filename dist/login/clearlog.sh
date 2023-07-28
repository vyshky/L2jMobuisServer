#!/bin/bash

log=./log
if [ ! -d log ]; then
 mkdir log
elif [ -d log ]; then
 rm -rf log
 mkdir log
fi