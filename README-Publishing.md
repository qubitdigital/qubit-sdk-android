
# Procedure of releasing new version

* Update version number in qubit-sdk/build.gradle file

```
version = '0.3.0'
```

* Update version number in README.md

```
dependencies   {
    compile  ‘com.qubit:qubit-sdk-android:0.3.0’
}
```

* Commit and push to github repository

* Create git tag: version-\<version number\>, for example: version-0.3.0

* Publish artifact to Bintray Maven repository

```
> ./gradlew clean bintrayUpload -Pbintray.user=... -Pbintray.password=... -Pbintray.gpg.password=...
```

