#!/bin/sh

PRODUCER_APP_BIN=bin-producer-app
CONSUMER_APP_BIN=bin-consumer-app

echo "REGION_1_CF_API : $REGION_1_CF_API"
echo "REGION_1_CF_ORG : $REGION_1_CF_ORG"
echo "REGION_1_CF_SPACE : $REGION_1_CF_SPACE"
echo "REGION_1_CF_USERNAME : $REGION_1_CF_USERNAME"

echo "REGION_2_CF_API : $REGION_2_CF_API"
echo "REGION_2_CF_ORG : $REGION_2_CF_ORG"
echo "REGION_2_CF_SPACE : $REGION_2_CF_SPACE"
echo "REGION_2_CF_USERNAME : $REGION_2_CF_USERNAME"

echo "REGION_3_CF_API : $REGION_3_CF_API"
echo "REGION_3_CF_ORG : $REGION_3_CF_ORG"
echo "REGION_3_CF_SPACE : $REGION_3_CF_SPACE"
echo "REGION_3_CF_USERNAME : $REGION_3_CF_USERNAME"

echo "TRANSACTION_DATASOURCE_JDBC : $TRANSACTION_DATASOURCE_JDBC"

cf login -a $REGION_1_CF_API -u $REGION_1_CF_USERNAME -p $REGION_1_CF_PASSWORD --skip-ssl-validation && \
  cf target -o $REGION_1_CF_ORG -s REGION_1_CF_SPACE && \
  cf push --no-start -p $PRODUCER_APP_BIN -n rabbitmq-multisite-producer-region-1  producer && \
  cf set-env producer PRODUCER_SOURCE region-1 && \
  cf bind-service producer broker && \
  cf start producer && \
  cf push -no-start -p $PRODUCER_APP_BIN -n rabbitmq-multisite-consumer-region-1 consumer && \
  cf set-env consumer CONSUMER_SOURCE region-1 && \
  cf set-env consumer TRANSACTION_DATASOURCE_JDBC $TRANSACTION_DATASOURCE_JDBC && \
  cf bind-service consumer broker && \
  cf bind-service consumer event-store-db && \
  cf start consumer && \
  cf login -a $REGION_2_CF_API -u $REGION_2_CF_USERNAME -p $REGION_2_CF_PASSWORD --skip-ssl-validation && \
  cf target -o $REGION_2_CF_ORG -s REGION_2_CF_SPACE && \
  cf push --no-start -p $PRODUCER_APP_BIN -n rabbitmq-multisite-producer-region-2 producer && \
  cf set-env producer PRODUCER_SOURCE region-2 && \
  cf bind-service producer broker && \
  cf start producer && \
  cf push -no-start -p $PRODUCER_APP_BIN -n rabbitmq-multisite-consumer-region-2 consumer && \
  cf set-env consumer CONSUMER_SOURCE region-2 && \
  cf set-env consumer TRANSACTION_DATASOURCE_JDBC $TRANSACTION_DATASOURCE_JDBC && \
  cf bind-service consumer broker && \
  cf bind-service consumer event-store-db && \
  cf start consumer