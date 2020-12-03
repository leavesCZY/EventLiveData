package github.leavesc.eventlivedatasamples

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import github.leavesc.eventlivedata.EventLiveData
import kotlinx.android.synthetic.main.activity_alive.*
import kotlin.concurrent.thread
import kotlin.random.Random

/**
 * @Author: leavesC
 * @Date: 2020/7/11 17:55
 * @Desc: 扩大 Observer 的生命周期
 * GitHub：https://github.com/leavesC
 */
class AliveActivity : AppCompatActivity() {

    companion object {

        val mutableLiveData = MutableLiveData<String>()

        val eventLiveData = EventLiveData<String>()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alive)
        mutableLiveData.observe(this, Observer {
            showToast("MutableLiveData observe 收到消息了: $it")
            tv_mutableObserver.text = it
        })
        eventLiveData.observe(this, Observer {
            showToast("EventLiveData observe 收到消息了: $it")
            tv_eventObserveAlive.text = it
        }, false)
        eventLiveData.observeSticky(this, Observer {
            showToast("EventLiveData observeSticky 收到消息了: $it")
            tv_eventLiveAliveSticky.text = it
        }, false)
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
        btn_setValueOtherPage.setOnClickListener {
            startActivity(Intent(this, Alive2Activity::class.java))
        }
    }

    private fun showToast(log: String) {
        Toast.makeText(this, log, Toast.LENGTH_SHORT).show()
    }

}