package com.woodscrewbot

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(48, 48, 48, 48)

        val title = TextView(this)
        title.text = "WOOD SCREW SORT BOT"
        title.textSize = 26f
        title.gravity = Gravity.CENTER

        val status = TextView(this)
        status.text = "Test build works. The bot comes next."
        status.textSize = 16f
        status.gravity = Gravity.CENTER

        layout.addView(title)
        layout.addView(status)
        setContentView(layout)
    }
}
