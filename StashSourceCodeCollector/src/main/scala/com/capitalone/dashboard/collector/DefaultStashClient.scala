package com.capitalone.dashboard.collector

import com.capitalone.dashboard.model.{Commit, StashRepo}
import org.springframework.stereotype.Component

@Component
class DefaultStashClient extends StashClient {
  override def getCommits(repo: StashRepo): List[Commit] = ???
}
