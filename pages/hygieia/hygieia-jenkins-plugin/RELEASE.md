# Steps to release

This outlines the maintainers steps to release the Jenkins Hygieia plugin.  Follow
the Jenkins documentation for [making a new release][plugin-release].

- [ ] Configure your credentials in `~/.m2/settings.xml`. (outlined in [making a
      new release][plugin-release] doc)
- [ ] Create a new issue to track the release and give it the label `maintainer
      communication`.
- [ ] Create a release branch. `git checkout origin/master -b prepare_release`
- [ ] Update the release notes in `CHANGELOG.md`.
- [ ] Open a pull request from `prepare_release` branch to `master` branch.
      Merge it.
- [ ] Fetch the latest `master`.
- [ ] Clean the workspace `git clean -xfd`.
- [ ] Execute the release plugin.

    ```
    mvn org.apache.maven.plugins:maven-release-plugin:2.5:prepare org.apache.maven.plugins:maven-release-plugin:2.5:perform
    ```

- [ ] Wait for the plugin to be released into the Jenkins Update Center.
- [ ] Successfully perform an upgrade from the last stable plugin release to the
      current release.

I pin which version of the release plugin to use because of the working around
common issues section of the [release document][plugin-release].


[plugin-release]: https://wiki.jenkins-ci.org/display/JENKINS/Hosting+Plugins
