#!/bin/bash

BASE_HOME="$( dirname "$( cd "$( dirname "$0"  )" && pwd ) " )"

if [ $# -ne 1 ] || [ "$1" != "agent" -a "$1" != "master" ] ;then
    echo "Number of parameters or configuration errors !!!"
    exit 1
fi

${BASE_HOME}/bin/stop.sh $1

${BASE_HOME}/bin/startup.sh $1
