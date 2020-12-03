package github.leavesc.eventlivedatasamples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_alive2.*
import kotlin.concurrent.thread
import kotlin.random.Random

/**
 * @Author: leavesC
 * @Date: 2020/7/11 17:55
 * @Desc:
 * GitHub：https://github.com/leavesC
 */
class Alive2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alive2)
        btn_setValue.setOnClickListener {
            val newValue = Random.nextInt(1, 300).toString()
            AliveActivity.mutableLiveData.value = newValue
            AliveActivity.eventLiveData.setValue(newValue)
        }
        btn_postValue.setOnClickListener {
            thread {
                val newValue = Random.nextInt(1, 300).toString()
                AliveActivity.mutableLiveData.postValue(newValue)
                AliveActivity.eventLiveData.postValue(newValue)
            }
        }
    }

}