package com.example.myproject.consumer

case class AppConfig(
                      bootstrapServers: String,
                      topic: String,
                      checkpointLocation: String,
                      analyticsConsumerAppName: String,
                      structuredConsumerAppName: String,
                      postgresUser: String,
                      postgresDatabase: String,
                      postgresPassword: String,
                      postgresStreamTableTopTags: String,
                      postgresStreamSchema: String
                    )
