## EventLiveData [![Maven Central](https://img.shields.io/maven-central/v/io.github.leavesczy/event-livedata.svg)](https://central.sonatype.com/artifact/io.github.leavesczy/event-livedata)

Jetpack LiveData 功能扩展，可自由选择是否接收黏性事件，也可自由选择是否扩大 observe 时的生命周期范围

导入依赖：

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

使用：

```kotlin
val eventLiveData = EventLiveData<String>()

//不接收黏性消息
//在 onStart 之后和 onStop 之前均能收到 Observer 回调，和 LiveData 一致
eventLiveData.observe(lifecycleOwner) {

}
//不接收黏性消息
//在 onCreate 之后和 onDestroy 之前均能收到 Observer 回调
eventLiveData.observe(lifecycleOwner, false) {

}
//不接收黏性消息
eventLiveData.observeForever {

}

//接收黏性消息
//在 onStart 之后和 onStop 之前均能收到 Observer 回调，和 LiveData 一致
eventLiveData.observeSticky(lifecycleOwner) {

}
//接收黏性消息
//在 onCreate 之后和 onDestroy 之前均能收到 Observer 回调
eventLiveData.observeSticky(lifecycleOwner, false) {

}
//接收黏性消息
eventLiveData.observeForeverSticky {

}
```

关联的文章：

- [Jetpack LiveData 的设计理念及改进](https://juejin.cn/post/6903096576734920717/)