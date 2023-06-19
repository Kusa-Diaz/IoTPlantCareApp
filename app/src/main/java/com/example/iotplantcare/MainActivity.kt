package com.example.iotplantcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imgLogo = findViewById<ImageView>(R.id.imgAppLogo)

        imgLogo.setOnClickListener{
            val intent = Intent(this, PlantRecycler::class.java)
            startActivity(intent)
        }
    }
}