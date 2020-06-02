#!/usr/bin/env bash

if [ -z "$ADVERTISED_LISTENER" ]; then
  export ADVERTISED_LISTENER=$(hostname -I | cut -d " " -f 1)
fi

if [[ $ADVERTISED_LISTENER =~ ^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "ADVERTISED_LISTENER: $ADVERTISED_LISTENER"
  docker-compose -f docker-compose.yml up -d
else
  echo "ERROR: Could not get the ADVERTISED_LISTENER IP on your system."
  echo "       This should be the IP of your local machine."
  echo "       Please run this before this script:"
  echo "       export ADVERTISED_LISTENER=[your IP here]"
  exit 1
fi
