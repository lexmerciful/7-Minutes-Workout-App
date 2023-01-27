package com.lex.a7minutesworkout

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.lex.a7minutesworkout.databinding.ActivityFinishBinding

class FinishActivity : AppCompatActivity() {

    private var binding: ActivityFinishBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //Change status bar color
        val window = this.window
        window.addFlags((WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS))
        window.statusBarColor = this.resources.getColor(R.color.colorAccent)

        setSupportActionBar(binding?.toolbarFinishActivity)

        binding?.btnFinish?.setOnClickListener {
            finish()
        }
    }
}