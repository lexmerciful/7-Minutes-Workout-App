package com.lex.a7minutesworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.lex.a7minutesworkout.databinding.ActivityExerciseBinding
import com.lex.a7minutesworkout.databinding.DialogCustomDialogBackConfirmBinding
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding : ActivityExerciseBinding? = null

    //Rest Timer Variables
    private var restTimer : CountDownTimer? = null
    private var restTimerDuration : Long = 1000
    private var restProgress = 0

    //Exercise Timer Variables
    private var exerciseTimer : CountDownTimer? = null
    private var exerciseTimerDuration : Long = 1000
    private var exerciseProgress = 0

    //Exercise Array List Variables
    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    //Text-To-Speech Variable
    private var tts : TextToSpeech? = null

    //Media Player Variable
    private var player : MediaPlayer? = null

    //Adapter Variable
    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //Setting Toolbar and it button Navigation
        setSupportActionBar(binding?.toolbarExercise)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }

        //Getting the ExerciseModel Constant Function
        exerciseList = Contants.defaultExerciseList()

        //Text-To-Speech
        tts = TextToSpeech(this,this)

        setupRestView()
        setupExerciseStatusRecyclerView()
    }

    private fun customDialogForBackButton(){
        val customDialogBack = Dialog(this)
        val dialogBinding = DialogCustomDialogBackConfirmBinding.inflate(layoutInflater)
        customDialogBack.setContentView(dialogBinding.root)
        customDialogBack.setCancelable(false)

        dialogBinding.btnYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialogBack.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            customDialogBack.dismiss()
        }

        customDialogBack.show()
    }

    override fun onBackPressed() {
        customDialogForBackButton()
        //super.onBackPressed()
    }

    private fun setupExerciseStatusRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    private fun setRestProgressBar() {
        val timeSet = (restTimerDuration/1000)
        binding?.progressBarRest?.progress = restProgress
        restTimer = object : CountDownTimer(restTimerDuration, 1000){
            override fun onTick(milisUntilFinish: Long) {
                restProgress++
                binding?.progressBarRest?.progress = (timeSet - restProgress).toInt()
                binding?.tvTimer?.text = (timeSet - restProgress).toString()
            }
            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].isSelected = true
                exerciseAdapter!!.notifyDataSetChanged()

                setupExerciseView()
            }
        }.start()
    }

    private fun setExerciseProgressBar() {
        val timeSet = (exerciseTimerDuration/1000)
        binding?.progressBarExercise?.progress = exerciseProgress
        speakText("${exerciseList!![currentExercisePosition].name} Begins")

        exerciseTimer = object : CountDownTimer(exerciseTimerDuration, 1000){
            override fun onTick(milisUntilFinish: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = (timeSet - exerciseProgress).toInt()
                binding?.tvTimerExercise?.text = (timeSet - exerciseProgress).toString()
            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {
                if (currentExercisePosition < exerciseList!!.size - 1){
                    Toast.makeText(this@ExerciseActivity, "Exercise Completed", Toast.LENGTH_SHORT).show()
                    speakText("${exerciseList!![currentExercisePosition].name} Completed")

                    exerciseList!![currentExercisePosition].isCompleted = true
                    exerciseList!![currentExercisePosition].isSelected = false
                    exerciseAdapter!!.notifyDataSetChanged()

                    setupRestView()
                }
                else{
                    finish()
                    //Get HistoryDao with instance of Database
                    val historyDao = (application as HistoryApp).db.historyDao()

                    addCompletedToHistory(historyDao)
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }
    
    private fun setupRestView(){
        //Setup Rest Time Media Sound
        try {
            val soundURI = Uri.parse(
                "android.resource://com.lex.a7minutesworkout/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, soundURI)
            player?.isLooping = false
            player?.start()
        }catch (e: Exception){
            e.printStackTrace()
        }

        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvGetReady?.visibility = View.VISIBLE
        binding?.tvUpcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExercise?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivExerciseImage?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE

        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        binding?.tvUpcomingExercise?.text = exerciseList!![currentExercisePosition + 1].name
        speakText("Rest for 10 seconds. Your upcoming exercise is ${exerciseList!![currentExercisePosition + 1].name}")
        setRestProgressBar()
    }

    private fun setupExerciseView(){
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvGetReady?.visibility = View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility = View.INVISIBLE
        binding?.tvUpcomingExercise?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivExerciseImage?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE

        if (exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        binding?.ivExerciseImage?.setImageResource(exerciseList!![currentExercisePosition].image)
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].name

        setExerciseProgressBar()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addCompletedToHistory(historyDao: HistoryDao){
        val itemPosition = ""
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        val formatted = current.format(formatter)
        val itemDate = formatted

        if (itemDate != null){
            lifecycleScope.launch {
                historyDao.insert(HistoryEntity(date = itemDate))
            }
        }else{
            Log.e("SAVED: ", "Failed!!!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        if (exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        if (tts != null){
            tts?.stop()
            tts?.shutdown()
        }

        if (player != null){
            player?.stop()
        }

        binding = null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.ENGLISH)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "Language specified not supported")
            }else{
                Log.e("TTS", "Initialization Failed!")
            }
        }
    }

    private fun speakText(text: String){
        tts?.speak(text,TextToSpeech.QUEUE_ADD,null,"")
    }
}