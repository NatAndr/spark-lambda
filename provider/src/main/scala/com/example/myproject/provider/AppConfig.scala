package com.example.myproject.provider

case class AppConfig(
                      bootstrapServers: String,
                      topic: String,
                      partitions: Int,
                      replication: Int,
                      appName: String
                    )
