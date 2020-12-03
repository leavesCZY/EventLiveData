package github.leavesc.eventlivedatasamples

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @Author: leavesC
 * @Date: 2020/7/11 14:35
 * @Desc: 扩大 Observer 的生命周期
 * GitHub：https://github.com/leavesC
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_sticky.setOnClickListener {
            startActivity(Intent(this, StickyActivity::class.java))
        }
        btn_alive.setOnClickListener {
            startActivity(Intent(this, AliveActivity::class.java))
        }
    }

}