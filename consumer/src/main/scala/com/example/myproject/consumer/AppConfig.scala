package com.example.myproject.consumer

case class AppConfig(
                      bootstrapServers: String,
                      topic: String,
                      checkpointLocation: String,
                      batchConsumerAppName: String,
                      streamingConsumerAppName: String,
                      postgresUser: String,
                      postgresDatabase: String,
                      postgresPassword: String,
                      postgresStreamTable: String,
                      postgresBatchTable: String,
                      topCount: Int
                    )
