#!/bin/sh

SERVICE_URL=$1
MESSAGE=$2

curl -X POST $SERVICE_URL/event -H "Content-Type:application/json" -d "$MESSAGE"