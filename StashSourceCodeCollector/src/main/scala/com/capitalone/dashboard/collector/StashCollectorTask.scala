package com.capitalone.dashboard.collector

import com.capitalone.dashboard.model
import com.capitalone.dashboard.model._
import com.capitalone.dashboard.repository.{BaseCollectorRepository, CommitRepository, ComponentRepository, StashRepoRepository}
import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.{Log, LogFactory}
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component

@Component
class StashCollectorTask @Autowired()(taskScheduler: TaskScheduler,
                                      collectorRepository: BaseCollectorRepository[Collector],
                                      stashRepoRepository: StashRepoRepository,
                                      commitRepository: CommitRepository,
                                      stashClient: StashClient,
                                      stashSettings: StashSettings,
                                      dbComponentRepository: ComponentRepository) extends CollectorTask[Collector](taskScheduler: TaskScheduler, "Stash") {

  private val LOG: Log = LogFactory.getLog(classOf[StashCollectorTask])

  override def getCollector: Collector = {
    val protoType = new Collector
    protoType.setName("Stash")
    protoType.setCollectorType(CollectorType.SCM)
    protoType.setOnline(true)
    protoType.setEnabled(true)
    protoType
  }

  override def getCollectorRepository: BaseCollectorRepository[Collector] = collectorRepository

  override def collect(collector: Collector): Unit = {
    logBanner("Starting....")

    val start = System.currentTimeMillis

    clean(collector)

    Option(stashRepoRepository.findStashRepo(collector.getId)) match {
      case Some(repositories) => repositories.foreach { repo =>
        repo.setLastUpdateTime(DateTime.now())
        stashRepoRepository.save(repo)

        val newCommits = stashClient.getCommits(repo).filter(isNewCommit(repo, _))
        newCommits.foreach { commit =>
          commit.setCollectorItemId(repo.getId)
          commitRepository.save(commit)
        }

        log("Repo Count", start, repositories.length)
        log("New Commits", start, newCommits.length)
      }
      case None =>
    }

    log("Finished", start)
  }

  override def getCron: String = stashSettings.cron

  private def clean(collector: Collector): Unit = {
    import scala.collection.JavaConversions._

    def getStashComponentIds(comp: model.Component) = {
      Option(comp.getCollectorItems.get(CollectorType.SCM)) match {
        case Some(items) => items.filter(_.getCollectorId == collector.getId).toSet
        case None => Set.empty[CollectorItem]
      }
    }

    def stashRepositories = {
      val uniqueIds = dbComponentRepository.findAll.foldLeft(Set[CollectorItem]())({ (acc, component) =>
        acc ++ getStashComponentIds(component)
      })

      stashRepoRepository.findByCollectorIdIn(Set(collector.getId)).map({ repo =>
        repo.setEnabled(uniqueIds.contains(repo.getId))
        repo
      })
    }

    stashRepoRepository.save(stashRepositories)
  }

  private def isNewCommit(repo: StashRepo, commit: Commit) = {
    Option({
      commitRepository.findByCollectorItemIdAndScmRevisionNumber(repo.getId, commit.getScmRevisionNumber)
    }).isEmpty
  }

  private def log(marker: String, start: Long) {
    log(marker, start, null)
  }

  // TODO get rid of leftPad
  private def log(text: String, start: Long, count: Integer) {
    val end: Long = System.currentTimeMillis
    val elapsed: String = ((end - start) / 1000) + "s"
    var token2: String = ""
    var token3: String = null
    if (count == null) {
      token3 = StringUtils.leftPad(elapsed, 30 - text.length)
    }
    else {
      val countStr: String = count.toString
      token2 = StringUtils.leftPad(countStr, 20 - text.length)
      token3 = StringUtils.leftPad(elapsed, 10)
    }
    LOG.info(text + token2 + token3)
  }

  private def logBanner(title: String): Unit = {
    LOG.info("------------------------------")
    LOG.info(title)
    LOG.info("------------------------------")
  }
}
