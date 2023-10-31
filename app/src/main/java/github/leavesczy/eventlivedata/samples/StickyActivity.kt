package github.leavesczy.eventlivedata.samples

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import github.leavesczy.eventlivedata.EventLiveData
import kotlin.concurrent.thread
import kotlin.random.Random

/**
 * @Author: leavesCZY
 * @Date: 2020/7/11 14:35
 * @Desc: 解决黏性事件问题
 * @GitHub：https://github.com/leavesCZY
 */
class StickyActivity : AppCompatActivity() {

    companion object {

        val mutableLiveData = MutableLiveData<String>()

        val eventLiveData = EventLiveData<String>()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sticky)
        val tvMutableObserver = findViewById<TextView>(R.id.tvMutableObserver)
        val tvEventObserve = findViewById<TextView>(R.id.tvEventObserve)
        val tvEventObserveSticky = findViewById<TextView>(R.id.tvEventObserveSticky)
        val btnSetValue = findViewById<Button>(R.id.btnSetValue)
        val btnPostValue = findViewById<Button>(R.id.btnPostValue)
        mutableLiveData.observe(this) {
            tvMutableObserver.text = it
        }
        eventLiveData.observe(this) {
            tvEventObserve.text = it
        }
        eventLiveData.observeSticky(this) {
            tvEventObserveSticky.text = it
        }
        btnSetValue.setOnClickListener {
            val newValue = Random.nextInt(1, 300).toString()
            mutableLiveData.value = newValue
            eventLiveData.value = newValue
        }
        btnPostValue.setOnClickListener {
            thread {
                val newValue = Random.nextInt(1, 300).toString()
                mutableLiveData.postValue(newValue)
                eventLiveData.postValue(newValue)
            }
        }
    }

}