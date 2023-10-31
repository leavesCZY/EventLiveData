package github.leavesczy.eventlivedata.samples

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * @Author: leavesCZY
 * @Date: 2020/7/11 14:35
 * @Desc: 扩大 Observer 的生命周期
 * @GitHub：https://github.com/leavesCZY
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnSticky = findViewById<TextView>(R.id.btnSticky)
        val btnAlive = findViewById<TextView>(R.id.btnAlive)
        btnSticky.setOnClickListener {
            startActivity(Intent(this, StickyActivity::class.java))
        }
        btnAlive.setOnClickListener {
            startActivity(Intent(this, AliveActivity::class.java))
        }
    }

}