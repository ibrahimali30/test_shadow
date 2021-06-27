package com.example.testshadow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class MainActivity : AppCompatActivity() {
    lateinit var button: ASButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        button = findViewById(R.id.button)
//        button.setOnClickListener {
//            test()
//        }
//
//        initTest()


    }

    private fun initTest() {
        test()
        delay(3){ test() }
        delay(5){ test(15.0f) }
        delay(7){ test(25.0f) }
    }

    private fun delay(time: Int = 1, function: () -> Unit) {
        Handler().postDelayed({

        },time*1000L)
    }

    private fun test(value: Float = 28.0f) {
        button.value = value
        button.updatePaintShadow(
            28.0f,
            0.0f,
            1.0f,
            -2302496
        )
    }
}