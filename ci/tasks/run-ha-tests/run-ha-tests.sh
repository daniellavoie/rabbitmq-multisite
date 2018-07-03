#!/bin/bash

echo "REGION_1_CF_API : $REGION_1_CF_API"
echo "REGION_1_CF_USERNAME : $REGION_1_CF_USERNAME"
echo "REGION_1_CF_ORG : $REGION_1_CF_ORG"
echo "REGION_1_CF_SPACE : $REGION_1_CF_SPACE"
echo "REGION_1_CONSUMER_URL : $REGION_1_CONSUMER_URL"
echo "REGION_2_CONSUMER_URL : $REGION_2_CONSUMER_URL"
echo "REGION_1_PRODUCER_URL : $REGION_1_PRODUCER_URL"
echo "REGION_2_PRODUCER_URL : $REGION_2_PRODUCER_URL"

echo "Configuring the region 1 broker with federation."
curl -X DELETE "${REGION_1_BROKER_HTTP_URI}parameters/federation-upstream/${REGION_1_BROKER_VHOST}/event-upstream" -k
curl -X DELETE "${REGION_1_BROKER_HTTP_URI}policies/${REGION_1_BROKER_VHOST}/event-upstream" -k
curl -X PUT ${REGION_1_BROKER_HTTP_URI}parameters/federation-upstream/${REGION_1_BROKER_VHOST}/event-upstream -d "{\"value\":{\"uri\":\"$REGION_2_BROKER_URI\",\"expires\":3600000}}" -k
curl -X PUT ${REGION_1_BROKER_HTTP_URI}policies/${REGION_1_BROKER_VHOST}/event-upstream -d "{\"pattern\":\"^event\", \"definition\":{\"federation-upstream-set\":\"all\"}, \"apply-to\":\"exchanges\"}" -k

echo "Configuring the region 2 broker with federation."
curl -X DELETE "${REGION_2_BROKER_HTTP_URI}parameters/federation-upstream/${REGION_2_BROKER_VHOST}/event-upstream" -k
curl -X DELETE "${REGION_2_BROKER_HTTP_URI}policies/${REGION_2_BROKER_VHOST}/event-upstream" -k
curl -X PUT ${REGION_2_BROKER_HTTP_URI}parameters/federation-upstream/${REGION_2_BROKER_VHOST}/event-upstream -d "{\"value\":{\"uri\":\"$REGION_1_BROKER_URI\",\"expires\":3600000}}" -k
curl -X PUT ${REGION_2_BROKER_HTTP_URI}policies/${REGION_2_BROKER_VHOST}/event-upstream -d "{\"pattern\":\"^event\", \"definition\":{\"federation-upstream-set\":\"all\"}, \"apply-to\":\"exchanges\"}" -k

echo "Starting the consumer app from region 1."
cf login -a $REGION_1_CF_API -u $REGION_1_CF_USERNAME -p $REGION_1_CF_PASSWORD -o $REGION_1_CF_ORG -s $REGION_1_CF_SPACE --skip-ssl-validation && \
  cf start consumer

echo "Purging the transaction database with service from consumer app from region 1."
curl -X DELETE $REGION_1_CONSUMER_URL/transaction -k

echo "Purging the event database with service from consumer app from region 1."
curl -X DELETE $REGION_1_CONSUMER_URL/event -k

echo "Purging the event database with service from consumer app from region 2."
curl -X DELETE $REGION_2_CONSUMER_URL/event -k

echo "Generating 1000 events for each producer app."
for i in {1..1000}
do
  curl -X POST $REGION_1_PRODUCER_URL/event?eventNumber=$i -H "Content-Type:application/json" -d "event $i" -k
done

for i in {1001..2000}
do  
  curl -X POST $REGION_2_PRODUCER_URL/event?eventNumber=$i -H "Content-Type:application/json" -d "event $i" -k
done

echo "Validating that the transaction database has 2000 events registered."
TRANSACTION_COUNT=`curl -X GET $REGION_1_CONSUMER_URL/transaction -H "Content-Type:application/json" -k`
echo "Transaction count : $TRANSACTION_COUNT"
if [ "$TRANSACTION_COUNT" != "2000" ]
then
  echo "Transaction count does not match."
  exit 1
fi

echo "Stopping the consumer app from region 1."
cf stop consumer

TIMESTAMP=`date -u +"%Y-%m-%dT%H:%M:%SZ"`

echo "Generating 1000 events for each producer app."
for i in {2001..3000}
do
  curl -X POST $REGION_1_PRODUCER_URL/event?eventNumber=$i -H "Content-Type:application/json" -d "event $i - to recover" -k
done

for i in {3001..4000}
do  
  curl -X POST $REGION_2_PRODUCER_URL/event?eventNumber=$i -H "Content-Type:application/json" -d "event $i" -k
done

echo "Calling the event recovery from the consumer app from region 2 with the timestamp matching the last generated events."
curl -X GET "$REGION_2_CONSUMER_URL/event?source=region-1&fromEventNumber=2001" -H "Content-Type:application/json" -k

echo "Validating the transaction database has a total of 4000 events registered"
TRANSACTION_COUNT=`curl -X GET $REGION_2_CONSUMER_URL/transaction -H "Content-Type:application/json" -k`
echo "Transaction count : $TRANSACTION_COUNT"
if [ "$TRANSACTION_COUNT" != "4000" ]
then
  echo "Transaction count does not match."
  exit 1
fi