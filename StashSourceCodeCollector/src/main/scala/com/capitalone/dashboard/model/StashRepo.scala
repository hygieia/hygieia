package com.capitalone.dashboard.model

import com.capitalone.dashboard.model.StashRepo._
import org.joda.time.DateTime

class StashRepo extends CollectorItem {
  def getUserId: String = getOptions.get(USER_ID).asInstanceOf[String]

  def setUserId(userId: String): Unit = getOptions.put(USER_ID, userId)

  def getPassword: String = getOptions.get(PASSWORD).asInstanceOf[String]

  def setPassword(password: String): Unit = getOptions.put(PASSWORD, password)

  def getRepoUrl: String = getOptions.get(REPO_URL).asInstanceOf[String]

  def setRepoUrl(instanceUrl: String): Unit = getOptions.put(REPO_URL, instanceUrl)

  def getBranch: String = getOptions.get(BRANCH).asInstanceOf[String]

  def setBranch(branch: String): Unit = getOptions.put(BRANCH, branch)

  def getLastUpdateTime: DateTime = getOptions.get(LAST_UPDATE_TIME).asInstanceOf[DateTime]

  def setLastUpdateTime(date: DateTime): Unit = getOptions.put(LAST_UPDATE_TIME, date)
}

object StashRepo {
  private val REPO_URL: String = "repoUrl"
  private val BRANCH: String = "branch"
  private val USER_ID: String = "userID"
  private val PASSWORD: String = "password"
  private val LAST_UPDATE_TIME: String = "lastUpdate"
}
