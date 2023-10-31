package github.leavesczy.eventlivedata.samples

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import github.leavesczy.eventlivedata.EventLiveData
import kotlin.concurrent.thread
import kotlin.random.Random

/**
 * @Author: leavesCZY
 * @Date: 2020/7/11 17:55
 * @Desc: 扩大 Observer 的生命周期
 * @GitHub：https://github.com/leavesCZY
 */
class AliveActivity : AppCompatActivity() {

    companion object {

        val mutableLiveData = MutableLiveData<String>()

        val eventLiveData = EventLiveData<String>()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alive)
        val tvMutableObserver = findViewById<TextView>(R.id.tvMutableObserver)
        val tvEventObserveAlive = findViewById<TextView>(R.id.tvEventObserveAlive)
        val tvEventLiveALiveSticky = findViewById<TextView>(R.id.tvEventLiveAliveSticky)
        val btnSetValue = findViewById<Button>(R.id.btnSetValue)
        val btnPostValue = findViewById<Button>(R.id.btnPostValue)
        val btnSetValueOtherPage = findViewById<Button>(R.id.btnSetValueOtherPage)
        mutableLiveData.observe(this) {
            showToast("MutableLiveData observe 收到消息了: $it")
            tvMutableObserver.text = it
        }
        eventLiveData.observe(this, {
            showToast("EventLiveData observe 收到消息了: $it")
            tvEventObserveAlive.text = it
        }, false)
        eventLiveData.observeSticky(this, {
            showToast("EventLiveData observeSticky 收到消息了: $it")
            tvEventLiveALiveSticky.text = it
        }, false)
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
        btnSetValueOtherPage.setOnClickListener {
            startActivity(Intent(this, Alive2Activity::class.java))
        }
    }

    private fun showToast(log: String) {
        Toast.makeText(this, log, Toast.LENGTH_SHORT).show()
    }

}