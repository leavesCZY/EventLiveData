package github.leavesczy.eventlivedata.samples

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread
import kotlin.random.Random

/**
 * @Author: leavesCZY
 * @Date: 2020/7/11 17:55
 * @Desc:
 * @GitHub：https://github.com/leavesCZY
 */
class Alive2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alive2)
        val btnSetValue = findViewById<Button>(R.id.btnSetValue)
        val btnPostValue = findViewById<Button>(R.id.btnPostValue)
        btnSetValue.setOnClickListener {
            val newValue = Random.nextInt(1, 300).toString()
            AliveActivity.mutableLiveData.value = newValue
            AliveActivity.eventLiveData.value = newValue
        }
        btnPostValue.setOnClickListener {
            thread {
                val newValue = Random.nextInt(1, 300).toString()
                AliveActivity.mutableLiveData.postValue(newValue)
                AliveActivity.eventLiveData.postValue(newValue)
            }
        }
    }

}