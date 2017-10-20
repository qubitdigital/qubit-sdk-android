
# Procedure of releasing new version

* Update version number in qubit-sdk/build.gradle file

```
version = '1.0.0'
```

* Update version number in README.md

```
dependencies   {
    compile  ‘com.qubit:qubit-sdk-android:1.0.0’
}
```

* Commit and push to github repository

* Create git tag: v\<version number\>, for example: v1.0.0

* Publish artifact to Bintray Maven repository

```
> ./gradlew clean bintrayUpload -Pbintray.user=... -Pbintray.apiKey=...
```

