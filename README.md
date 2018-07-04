# RabbitMQ - Active / Active Replication

This project provides a [Concourse](https://concourse-ci.org) pipeline to deploy a sample architecture showcasing
transaction recovery in a disaster scenario involving losing a site.

## Pipeline

![Pipeline](doc/img/pipeline.png "Pipeline")

* <strong>deploys-apps:</strong> Deploys all the apps in the involved architecture.
* <strong>run-ha-tests:</strong> Generates events, then simulates a DR by stopping a consumer app from a site. Triggers the recovery procedure then validates that the transaction count is expected.

## Architecture Overview

![RabbitMQ Active Active Replication](doc/img/rabbitmq-active-activerreplication-1.png "RabbitMQ Active Active Replication")

![Disaster](doc/img/rabbitmq-active-activerreplication-2.png "Disaster")

## Test scenario

* Deploy the producer and consumer apps in `region 1` and `region 2` spaces.
* Purge the transaction database with service from consumer app from `space 1`.
* Purge the event-store databases from each spaces.
* Setup the RabbitMQ exchange replication with federation.
* Generate 1000 events for each producer app.
* Validate that the transaction database has 2000 events registered.
* Stop the consumer app from `region 1`.
* Generate 1000 events for each producer app.
* Call the event recovery from the consumer app from `region 2` with 
  the event number higher than the last processed by the `region 1`.
* Validate the transaction database has a total of 4000 events registered

## Prerequisites

* `On Demand RabbitMQ` or `Standard RabbitMQ` with federation plugin installed
* 3 spaces
* An On Demand or Standard `RabbitMQ` service instance named `broker` in `space 1` and `space 2`.
* A `MySQL` service instance named `event-store-db` in `space 1` and `space 2`.
* A `MySQL` service instance named `transaction-db` in `space 3`.
* A service key for each `broker` service instance in `space 1` and `space 2`.
* A service key for the `transaction-db` from `space 3`.

## Properties for the pipeline

```
region-1-cf-api:             # https://api.run.pivotal.io
region-1-cf-org:             # my-org
region-1-cf-space:           # region-1
region-1-cf-username:        # my-user
region-1-cf-password:        # *******
region-2-cf-api:             # https://api.run.pivotal.io
region-2-cf-org:             # my-org
region-2-cf-space:           # region-2
region-2-username:           # my-user
region-2-password:           # *******
region-3-cf-api:             # https://api.run.pcfbeta.io
region-3-cf-org:             # my-org
region-3-cf-space:           # mainframe
region-3-username:           # my-user
region-3-password:           # *******
transaction-datasource-jdbc: # jdbc:mysql://HOSTNAME:PORT/DB_NAME?user=USERNAME&password=PASSWORD

github-uri:                  # https://github.com/daniellavoie/rabbitmq-multisite.git

region-1-consumer-url:       # https://rabbitmq-multisite-consumer-region-1.apps.TLD-DOMAIN
region-2-consumer-url:       # https://rabbitmq-multisite-consumer-region-2.apps.TLD-DOMAIN
region-1-producer-url:       # https://rabbitmq-multisite-producer-region-1.apps.TLD-DOMAIN
region-2-producer-url:       # https://rabbitmq-multisite-producer-region-2.apps.TLD-DOMAIN

region-1-broker-vhost:       # d36cf216-e483-4ba4-a45e-134046338598
region-2-broker-vbost:       # 11f683ac-3104-42f5-8bd1-9933b8b03db8
region-1-broker-uri:         # amqp://65d6c23d-4a3f-4eec-921b-dfaada057d93:************@X.X.X.X/d36cf216-e483-4ba4-a45e-134046338598
region-2-broker-uri:         # amqp://ebd1fb9d-2556-4c1f-a009-36566ff61d3a:************@X.X.X.X/11f683ac-3104-42f5-8bd1-9933b8b03db8
region-1-broker-http-uri:    # https://65d6c23d-4a3f-4eec-921b-dfaada057d93:************@rmq-d36cf216-e483-4ba4-a45e-134046338598.sys.TLD-DOMAIN/api/
region-2-broker-http-uri:    # https://ebd1fb9d-2556-4c1f-a009-36566ff61d3a:************@rmq-11f683ac-3104-42f5-8bd1-9933b8b03db8.sys.TLD-DOMAIN/api/
```

## Deploy the pipeline with Concourse

```
$ fly -t TARGET set-pipeline -p rabbitmq-multisite -c ci/pipeline.yml -l PATH_TO_PIPELINE_PROPERTIES
```