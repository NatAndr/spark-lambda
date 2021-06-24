package com.example.myproject.consumer

import java.util.Properties
import pureconfig.generic.auto._

object JdbcUtils {
  def getConnectionProperties: Properties = {
    val config: AppConfig = pureconfig.loadConfigOrThrow[AppConfig]

    val connectionProperties = new Properties()

    connectionProperties.put("user", config.postgresUser)
    connectionProperties.put("password", config.postgresPassword)
    connectionProperties.put("driver", "org.postgresql.Driver")
    connectionProperties.put("dbtable", config.postgresStreamTable)

    connectionProperties
  }

}
