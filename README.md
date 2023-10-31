## EventLiveData [![Maven Central](https://img.shields.io/maven-central/v/io.github.leavesczy/event-livedata.svg)](https://central.sonatype.com/artifact/io.github.leavesczy/event-livedata)

对 Jetpack LiveData 进行功能扩展，解决黏性事件及通知延迟问题

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

dependencies {
    implementation("io.github.leavesczy:event-livedata:latestVersion")
}
```

关联的文章：

- [Jetpack LiveData 的设计理念及改进](https://juejin.cn/post/6903096576734920717/)
