package github.leavesc.eventlivedata

import android.os.Build
import android.os.Handler
import android.os.Looper
import java.lang.reflect.InvocationTargetException

/**
 * @Author: leavesC
 * @Date: 2020/7/10 23:25
 * @Desc:
 * GitHub：https://github.com/leavesC
 */
internal object EventTaskExecutor {

    private fun createAsync(looper: Looper): Handler {
        if (Build.VERSION.SDK_INT >= 28) {
            return Handler.createAsync(looper)
        }
        if (Build.VERSION.SDK_INT >= 16) {
            try {
                return Handler::class.java.getDeclaredConstructor(
                    Looper::class.java, Handler.Callback::class.java,
                    Boolean::class.javaPrimitiveType
                ).newInstance(looper, null, true)
            } catch (ignored: IllegalAccessException) {
            } catch (ignored: InstantiationException) {
            } catch (ignored: NoSuchMethodException) {
            } catch (e: InvocationTargetException) {
                return Handler(looper)
            }
        }
        return Handler(looper)
    }

    private val handler by lazy {
        createAsync(Looper.getMainLooper())
    }

    val isMainThread: Boolean
        get() = Looper.getMainLooper().thread === Thread.currentThread()

    fun postToMainThread(runnable: Runnable) {
        handler.post(runnable)
    }

}