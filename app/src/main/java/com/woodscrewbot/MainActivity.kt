package com.woodscrewbot

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var gameText: TextView
    private lateinit var statusText: TextView
    private var paused = false

    private fun prefs() = getSharedPreferences("bot", Context.MODE_PRIVATE)

    private fun dp(x: Int): Int = (x * resources.displayMetrics.density).toInt()

    private fun makeText(text: String, size: Float, color: String, bold: Boolean): TextView {
        val t = TextView(this)
        t.text = text
        t.textSize = size
        t.setTextColor(Color.parseColor(color))
        if (bold) t.setTypeface(null, Typeface.BOLD)
        t.gravity = Gravity.CENTER
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(0, dp(8), 0, 0)
        t.layoutParams = lp
        return t
    }

    private fun makeButton(label: String, color: String, onClick: () -> Unit): Button {
        val b = Button(this)
        b.text = label
        b.textSize = 20f
        b.setTextColor(Color.WHITE)
        val bg = GradientDrawable()
        bg.setColor(Color.parseColor(color))
        bg.cornerRadius = dp(18).toFloat()
        b.background = bg
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(64)
        )
        lp.setMargins(0, dp(12), 0, 0)
        b.layoutParams = lp
        b.setOnClickListener { onClick() }
        return b
    }

    private fun refreshGame() {
        val label = prefs().getString("gameLabel", null)
        gameText.text = if (label == null) "Game: not chosen yet" else "Game: " + label
    }

    private fun chooseGame() {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = pm.queryIntentActivities(intent, 0)
            .map { Pair(it.loadLabel(pm).toString(), it.activityInfo.packageName) }
            .filter { it.second != packageName }
            .sortedBy { it.first.lowercase() }
        val names = apps.map { it.first }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Choose your game")
            .setItems(names) { _, i ->
                prefs().edit()
                    .putString("gameLabel", apps[i].first)
                    .putString("gamePackage", apps[i].second)
                    .apply()
                refreshGame()
                statusText.text = "Status: game chosen"
            }
            .show()
    }

    private fun onPlay() {
        val pkg = prefs().getString("gamePackage", null)
        if (pkg == null) {
            statusText.text = "Status: choose your game first"
            return
        }
        val launch = packageManager.getLaunchIntentForPackage(pkg)
        if (launch == null) {
            statusText.text = "Status: cannot open that game"
            return
        }
        paused = false
        startActivity(launch)
        statusText.text = "Status: game opened. Tapping is not built yet."
    }

    private fun onPause() {
        paused = !paused
        statusText.text = if (paused) "Status: paused" else "Status: resumed"
    }

    private fun onStop() {
        paused = false
        statusText.text = "Status: stopped"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(Color.parseColor("#1E1410"))
        root.setPadding(dp(24), dp(48), dp(24), dp(24))

        root.addView(makeText("WOOD SCREW SORT BOT", 26f, "#F5C77E", true))
        root.addView(makeText("Auto-player for your puzzle game", 14f, "#BBAA99", false))

        gameText = makeText("", 16f, "#FFFFFF", false)
        root.addView(gameText)
        root.addView(makeButton("CHOOSE GAME", "#6B4A2F") { chooseGame() })

        root.addView(makeText("Current level: 0", 16f, "#FFFFFF", false))
        root.addView(makeText("Completed levels: 0", 16f, "#FFFFFF", false))
        root.addView(makeText("Total moves: 0", 16f, "#FFFFFF", false))

        statusText = makeText("Status: ready", 16f, "#9BE29B", true)
        root.addView(statusText)

        root.addView(makeButton("PLAY", "#2E9E4F") { onPlay() })
        root.addView(makeButton("PAUSE", "#D98A1F") { onPause() })
        root.addView(makeButton("STOP", "#C93A3A") { onStop() })

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)
        refreshGame()
    }
}
