package com.udacity

import android.Manifest
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadManager: DownloadManager
    private val extras = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        custom_button.setOnClickListener {
            Log.d("RadioGroup", radioGroup.checkedRadioButtonId.toString())
            if (checkStoragePermission()) {
                custom_button.enableAnimation = radioGroup.checkedRadioButtonId != -1
                when (radioGroup.checkedRadioButtonId) {
                    -1 -> {
                        Toast.makeText(this, getString(R.string.toast_msg), Toast.LENGTH_SHORT).show()
                    }
                    R.id.radio_glide -> download(GLIDE_URL)
                    R.id.radio_app -> download(C3_URL)
                    R.id.radio_retrofit -> download(RETROFIT_URL)
                }
            } else {
                Toast.makeText(this, getString(R.string.toast_msg_no_permission), Toast.LENGTH_SHORT).show()
            }
        }
        notificationManager = getSystemService(NotificationManager::class.java)
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        notificationManager.createChannel(
                getString(R.string.notification_channel),
                getString(R.string.notification_channel_name),
                getString(R.string.notification_channel_desc))
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            id?.let {
                var messageBody = ""
                when(getDownloadStatus(downloadManager, it)) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        messageBody = getString(R.string.msg_body_ok)
                        extras[DOWNLOAD_STATUS] = getString(R.string.status_ok)
                        extras[FILENAME] = getFileName(downloadManager, it)
                    }
                    DownloadManager.STATUS_FAILED -> {
                        messageBody = getString(R.string.msg_body_failure)
                        extras[DOWNLOAD_STATUS] = getString(R.string.status_failure)
                        extras[FILENAME] = ""
                    }
                }

                if (messageBody.isNotBlank()) notificationManager.sendDownloadNotification(messageBody, this@MainActivity, extras)
            }

        }
    }

    private fun getDownloadStatus(downloadManager: DownloadManager, id: Long): Int {
        val query = DownloadManager.Query()
        query.setFilterById(id)
        val c = downloadManager.query(query)
        if (c.moveToFirst()) {
            val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            c.close()
            return status
        }
        return -1
    }

    private fun getFileName(downloadManager: DownloadManager, id: Long): String {
        val query = DownloadManager.Query()
        query.setFilterById(id)
        val c = downloadManager.query(query)
        if (c.moveToFirst()) {
            val uri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
            c.close()
            val status = uri.split("/").last()
            return status
        }
        return ""
    }

    private fun download(urlString: String) {
        Log.d("download", urlString)
        val fileName = when(urlString) {
            GLIDE_URL -> getString(R.string.glide_filename)
            C3_URL -> getString(R.string.c3_filename)
            RETROFIT_URL -> getString(R.string.retrofit_filename)
            else -> "nothing"
        }
        if(fileName != "nothing") {
            extras[DIRECTORY] = Environment.DIRECTORY_DOWNLOADS
            val request = DownloadManager.Request(Uri.parse(urlString))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            downloadID =
                    downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        }
    }

    private fun checkStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT <= 28) {
            return if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                false
            }
        }

        return true //no need to check
    }

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val C3_URL =
                "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/refs/heads/master.zi"
    }

}
