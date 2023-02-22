package com.alltrack.examples

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View

import com.alltrack.sdk.Alltrack

class ServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        val intent = intent
        val data = intent.data
        Alltrack.appWillOpenUrl(data, applicationContext)
    }

    fun onServiceClick(v: View) {
        val intent = Intent(this, ServiceExample::class.java)
        startService(intent)
    }

    fun onReturnClick(v: View) {
        finish()
    }
}