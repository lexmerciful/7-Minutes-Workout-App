package com.lex.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.lex.a7minutesworkout.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private var binding: ActivityHistoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //Setting Toolbar and it button Navigation
        setSupportActionBar(binding?.toolbarHistory)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarHistory?.setNavigationOnClickListener {
            onBackPressed()
        }

        lifecycleScope.launch {
            val historyDao = (application as HistoryApp).db.historyDao()
            historyDao.fetchAllHistory().collect{
                val list = ArrayList(it)
                setupListOfCompletedToHistory(list,historyDao)
            }
        }
    }

    private fun setupListOfCompletedToHistory(historyList:ArrayList<HistoryEntity>, historyDao: HistoryDao){
        if (historyList.isNotEmpty()){
            val adapter = HistoryAdapter(historyList)

            binding?.rvHistoryItem?.layoutManager = LinearLayoutManager(this)
            binding?.rvHistoryItem?.adapter = adapter

            binding?.rvHistoryItem?.visibility = View.VISIBLE
            binding?.tvNoRecordAvailable?.visibility = View.GONE
        }else{
            binding?.rvHistoryItem?.visibility = View.INVISIBLE
            binding?.txtExerciseCompleted?.visibility = View.INVISIBLE
            binding?.tvNoRecordAvailable?.visibility = View.VISIBLE
        }
    }
}