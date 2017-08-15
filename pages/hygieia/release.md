----------------------
gradle.properties
-----------------------
```
signing.keyId=<whatever>
signing.password=<whatever>
signing.secretKeyRingFile=secring.gpg

ossrhUsername=<whatever>
ossrhPassword=<secret>
```

--------------------------------------

Release:

``` ./gradlew uploadArchives -Prelease ```

Snapshot:

``` ./gradlew uploadArchives -Psnapshot ```

