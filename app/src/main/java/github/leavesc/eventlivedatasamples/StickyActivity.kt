package github.leavesc.eventlivedatasamples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
        mutableLiveData.observe(this, Observer {
            tv_mutableObserver.text = it
        })
        eventLiveData.observe(this, Observer {
            tv_eventObserve.text = it
        })
        eventLiveData.observeSticky(this, Observer {
            tv_eventObserveSticky.text = it
        })
        btn_setValue.setOnClickListener {
            val newValue = Random.nextInt(1, 300).toString()
            mutableLiveData.value = newValue
            eventLiveData.setValue(newValue)
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