package com.csd.mod_achievement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import com.csd.lib_common.constant.ROUTE_ACHIEVEMENT_ACTIVITY_MAIN
import com.example.mod_achievement.R
import com.therouter.router.Route
import kotlin.io.path.Path

@Route(path=ROUTE_ACHIEVEMENT_ACTIVITY_MAIN)
class MainActivity2 : AppCompatActivity() {
    private lateinit var fragmentContainer: FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.achievement_activity_main)
        val button : Button = findViewById(R.id.button1)
        fragmentContainer = findViewById<FrameLayout>(R.id.myFragment)
        button.setOnClickListener{
            val fragment = AchievementMain()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.myFragment, fragment)
            transaction.commit()
        }

    }


}