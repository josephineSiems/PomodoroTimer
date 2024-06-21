package com.example.pomodorotimer

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.pomodorotimer.databinding.FragmentFirstBinding
import kotlin.concurrent.timer
import kotlin.math.round

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //workTime and breakTime in minutes
    var workTime = 0
    var breakTime = 0
    var alarmSound = ""
    var volume = 5

    //workTime and breakTime in milliseconds
    var workMilli = 0L
    var breakMilli = 0L

    var workMinutes = 0
    var breakMinutes = 0


    var workSeconds = 0
    var workSecs = 0
    var breakSeconds = 0
    var breakSecs = 0

    var secCounter = 60

    var alarmState = true
    var vibrationState = true
    var first = true

    lateinit var mediaPlayer: MediaPlayer
    lateinit var timerWork: CountDownTimer
    lateinit var timerBreak: CountDownTimer
    lateinit var playMusicTime: CountDownTimer

    var runningTimer = false
    var timertype = "work"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        load()
        workMilli = workTime*60000L
        workMilli += workSeconds*1000
        breakMilli = breakTime*60000L
        breakMilli += breakSeconds*1000

        workSecs = workSeconds +1
        breakSecs = breakSeconds +1

        workMinutes = workTime
        breakMinutes = breakTime

        setTimerText(workMinutes, workSeconds)
        setMediaPlayer()
        setMusicTimer()
        setBreakTimer()
        setWorkTimer()


        binding.buttonStart.setOnClickListener {
            if (!runningTimer && first) {
                timerWork.start()
                Toast.makeText(this.context, "Time to Start Working!", Toast.LENGTH_LONG).show()
                binding.textviewTimeType.text = "Work Time"
                binding.buttonStart.text = "Stop"
                runningTimer = true
                first = false
            }else if(!runningTimer){
                if(timertype == "work"){
                    timerWork.start()
                    runningTimer = true
                }else{
                    timerBreak.start()
                    runningTimer = true
                }
                Toast.makeText(this.context, "Timer Resumed", Toast.LENGTH_LONG).show()
            }
            else {
                try {
                    timerWork.cancel()
                    timerBreak.cancel()
                    Toast.makeText(this.context, "Timer Stopped", Toast.LENGTH_LONG).show()
                    binding.buttonStart.text = "Start"
                    runningTimer = false
                } catch (e: Exception) {
                    Log.d("NoteThing", "No Timer: " + e)
                    Toast.makeText(this.context, "No Timer active", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.buttonRestart.setOnClickListener {
            try {
                if (runningTimer) {
                    timerWork.cancel()
                    timerBreak.cancel()
                }

                if (timertype == "work") {
                    workMinutes = workTime
                    workSecs = workSeconds + 1
                    secCounter = 60
                } else if (timertype == "break") {
                    breakMinutes = breakTime
                    breakSecs = breakSeconds + 1
                    secCounter = 60
                }


                timerWork.start()
                binding.textviewTimeType.text = "Work Time"
                binding.buttonStart.text = "Stop"
                runningTimer = true
                Toast.makeText(this.context, "Timer Restarted", Toast.LENGTH_LONG).show()

            }catch (e:Exception){
                Log.d("NoteThing", "No Timer: " + e)
            }

        }

        binding.buttonChange.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    fun setTimerText(minute: Int, second: Int){
        var minZero = ""
        var secZero = ""
        if(minute < 10){
            minZero = "0"
        }
        if(second < 10){
            secZero = "0"
        }
        var time = minZero + minute.toString() + ":" + secZero + second.toString()
        binding.textviewTime.setText(time)

        //secCounter = workSeconds

    }

    fun setMediaPlayer(){
        if (alarmSound != "") {
            try {
                var alarmPath = Uri.parse(alarmSound)
                Log.d("NoteThing", "Song1: " + alarmPath)
                mediaPlayer = MediaPlayer.create(context, alarmPath)

            } catch (e: Exception){
                Log.d("NoteThing", "Exception: " + e )
                var alarmPath = resources.getIdentifier("chime", "raw", activity?.packageName)
                mediaPlayer = MediaPlayer.create(context, alarmPath)
            }
        }else{
            var alarmPath = resources.getIdentifier("chime", "raw", activity?.packageName)
            mediaPlayer = MediaPlayer.create(context, alarmPath)
            Log.d("NoteThing", "Song2: " + alarmPath)
        }

        try {
            mediaPlayer.setLooping(true)
            var volumePercent = ("0." + volume.toString()).toFloat()
            mediaPlayer.setVolume(volumePercent,volumePercent)
            Log.d("NoteThing", "Volume: " + volume)
        }catch (e:Exception){
            Log.d("NoteThing","error: " + e)
        }
    }

    fun setMusicTimer() {
        playMusicTime = object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                mediaPlayer.pause()
            }
        }
    }

    fun setBreakTimer(){
        timerBreak = object: CountDownTimer(breakMilli, 1000){
            override fun onTick(millisUntilFinished: Long) {
                Log.d("NoteThing", "break2")
                if(breakMinutes == breakTime && breakSecs == 0) {
                    breakMinutes--
                }
                if(breakSecs != 0){
                    breakSecs--
                    setTimerText(breakMinutes, breakSecs)
                }

                if (breakSecs == 0 && breakMinutes == 0){
                    setTimerText(0, 0)
                }
                else if (breakSecs == 0){
                    secCounter--
                    Log.d("NoteThing", "secCounter1: " + secCounter)
                    if (secCounter < 1){
                        secCounter = 59
                        if (breakMinutes > 0) {
                            breakMinutes--
                        }
                    }
                    setTimerText(breakMinutes, secCounter)
                    Log.d("NoteThing", "secCounter please: " + secCounter)
                }
                Log.d("NoteThing", "secCounter2: " + secCounter + " breakSec: " + breakSecs)
            }

            override fun onFinish() {
                binding.textviewTimeType.text = "Work Time"
                Toast.makeText(context, "Time to Start Working!", Toast.LENGTH_LONG).show()
                timertype = "work"

                timerWork.start()
                if (alarmState) {
                    playJingle(mediaPlayer, playMusicTime)
                }
                if (vibrationState) {
                    vibratePhone()
                }
                breakMinutes = breakTime
                breakSecs = breakSeconds+1
                secCounter = 60
            }
        }
    }

    fun setWorkTimer(){
        timerWork = object: CountDownTimer(workMilli, 1000){
            override fun onTick(millisUntilFinished: Long) {
                Log.d("NoteThing", "secCounter0: " + secCounter)
                if(workMinutes == workTime && workSecs == 0) {
                    workMinutes--
                }

                if(workSecs != 0){
                    workSecs--
                    setTimerText(workMinutes, workSecs)
                }

                if (workSecs == 0 && workMinutes == 0){
                    setTimerText(0, 0)
                }
                else if(workSecs == 0 ){
                    secCounter--
                    Log.d("NoteThing", "secCounter1: " + secCounter)
                    if (secCounter < 1){
                        secCounter = 59
                        if(workMinutes > 0) {
                            workMinutes--
                        }
                    }
                    setTimerText(workMinutes, secCounter)
                    Log.d("NoteThing", "secCounter please: " + secCounter)
                }
                Log.d("NoteThing", "secCounter2: " + secCounter + " workSec: " + workSecs)

            }
            override fun onFinish() {
                Log.d("NoteThing", "break")
                binding.textviewTimeType.text = "Break Time"
                Toast.makeText(context, "Time for a break :)", Toast.LENGTH_LONG).show()
                timertype = "break"

                timerBreak.start()
                if (alarmState) {
                    playJingle(mediaPlayer, playMusicTime)
                }
                if (vibrationState) {
                    vibratePhone()
                }
                workMinutes = workTime
                workSecs = workSeconds+1
                secCounter = 60
            }

            fun restart() {
                //

            }
        }
    }



    fun playJingle(mediaPlayer:MediaPlayer, playMusic: CountDownTimer){
        //mediaPlayer.prepare()
        mediaPlayer.start()
        playMusic.start()
    }

    fun vibratePhone() {
        try {
            val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        }catch (e:Exception) {
            Log.d("NoteThing", "Error: " + e)
        }
    }


    fun load(){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

        try {
            workTime = sharedPref.getInt("workTime", 0)
            breakTime = sharedPref.getInt("breakTime", 0)
            workSeconds = sharedPref.getInt("workSeconds", 0)
            breakSeconds = sharedPref.getInt("breakSeconds", 0)
            vibrationState = sharedPref.getBoolean("vibration", true)
            alarmState = sharedPref.getBoolean("alarm", true)
            alarmSound = sharedPref.getString("alarmsound", "").toString()
            volume = sharedPref.getInt("volume", 5)

            Log.d("NoteThing", "vibration: " + vibrationState + " alarm: " + alarmState + "alarmsound: " + alarmSound )

        }catch (e:Exception) {
            Log.d("NoteThing", ("No Counter:" + e))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}