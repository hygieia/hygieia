package com.capitalone.dashboard.collector

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "stash")
class StashSettings {
  var cron: String = "0/1 * * * * *"
  var host: String = null
  var key: String = null
}
