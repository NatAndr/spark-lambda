#!/bin/bash

echo "STARTING APP"


${SPARK_HOME}/bin/spark-submit \
        --class "com.example.myproject.consumer.StreamingJob" \
        --jars ${SPARK_HOME}/jars/spark-streaming-kafka-0-10_2.12-${SPARK_VERSION}.jar \
        ./build/consumer.jar


java -jar ./build/provider.jar
