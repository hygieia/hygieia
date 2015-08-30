package com.capitalone.dashboard.collector

import com.capitalone.dashboard.model.{Commit, StashRepo}

trait StashClient {
  def getCommits(repo: StashRepo): List[Commit]
}
