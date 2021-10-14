package github.leavesc.eventlivedatasamples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import github.leavesc.eventlivedata.EventLiveData
import kotlinx.android.synthetic.main.activity_sticky.*
import kotlin.concurrent.thread
import kotlin.random.Random

/**
 * @Author: leavesC
 * @Date: 2020/7/11 14:35
 * @Desc: 解决黏性事件问题
 * GitHub：https://github.com/leavesC
 */
class StickyActivity : AppCompatActivity() {

    companion object {

        val mutableLiveData = MutableLiveData<String>()

        val eventLiveData = EventLiveData<String>()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticky)
        mutableLiveData.observe(this, {
            tv_mutableObserver.text = it
        })
        eventLiveData.observe(this, {
            tv_eventObserve.text = it
        })
        eventLiveData.observeSticky(this, {
            tv_eventObserveSticky.text = it
        })
        btn_setValue.setOnClickListener {
            val newValue = Random.nextInt(1, 300).toString()
            mutableLiveData.value = newValue
            eventLiveData.value = newValue
        }
        btn_postValue.setOnClickListener {
            thread {
                val newValue = Random.nextInt(1, 300).toString()
                mutableLiveData.postValue(newValue)
                eventLiveData.postValue(newValue)
            }
        }
    }

}