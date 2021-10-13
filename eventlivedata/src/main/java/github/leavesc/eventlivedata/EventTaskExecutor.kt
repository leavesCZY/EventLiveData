package github.leavesc.eventlivedata

import android.annotation.SuppressLint
import androidx.arch.core.executor.ArchTaskExecutor

/**
 * @Author: leavesC
 * @Date: 2020/7/10 23:25
 * @Desc:
 * GitHub：https://github.com/leavesC
 */
internal object EventTaskExecutor {

    val isMainThread: Boolean
        @SuppressLint("RestrictedApi")
        get() = ArchTaskExecutor.getInstance().isMainThread

    @SuppressLint("RestrictedApi")
    fun postToMainThread(runnable: Runnable) {
        ArchTaskExecutor.getInstance().postToMainThread(runnable)
    }

}