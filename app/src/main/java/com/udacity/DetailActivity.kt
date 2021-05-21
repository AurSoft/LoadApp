package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.cancelAll()
        updateDetails(intent)
        button_ok.setOnClickListener { finish() }
    }

    private fun updateDetails(intent: Intent) {
        val fileName = intent.getStringExtra(FILENAME)
        if (fileName.isNullOrBlank()) {
            text_filename.visibility = View.INVISIBLE
            text_filename_value.visibility = View.INVISIBLE
            text_path.visibility = View.GONE
            text_path_value.visibility = View.GONE
        } else {
            text_filename_value.text = fileName
            text_path_value.text = intent.getStringExtra(DIRECTORY)
        }
        val status = intent.getStringExtra(DOWNLOAD_STATUS)
        text_status_value.text = status
        if (status == getString(R.string.status_ok)){
            image_status.setImageResource(R.drawable.ic_baseline_check_circle_24)
        } else {
            image_status.setImageResource(R.drawable.ic_baseline_highlight_off_24)
        }
    }

}
