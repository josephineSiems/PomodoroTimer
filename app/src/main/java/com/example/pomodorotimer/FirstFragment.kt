package com.example.pomodorotimer

import android.content.Context
import android.media.MediaPlayer
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

    //workTime and breakTime in milliseconds
    var workMilli = 0L
    var breakMilli = 0L

    var workMinutes = 0
    var breakMinutes = 0

    var secCounter = 60

    var alarmState = true
    var vibrationState = true

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
        breakMilli = breakTime*60000L

        workMinutes = workTime
        breakMinutes = breakTime

        setTimer(workMinutes, 0)


        val resID = resources.getIdentifier("chime", "raw", activity?.packageName)
        val mediaPlayer = MediaPlayer.create(view.context, resID)


        try {
            mediaPlayer.setLooping(true)
        }catch (e:Exception){
            Log.d("NoteThing","error: " + e)
            }


        val playMusicTime = object: CountDownTimer(2000, 1000){
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                mediaPlayer.pause()
            }
        }


        val timerBreak = object: CountDownTimer(breakMilli, 1000){
            override fun onTick(millisUntilFinished: Long) {
                if(breakMinutes == breakTime) {
                    breakMinutes = breakMinutes - 1
                }
                secCounter = secCounter-1
                if (secCounter < 1){
                    secCounter = 59
                    if (breakMinutes > 0) {
                        breakMinutes = breakMinutes - 1
                    }

                }
                setTimer(breakMinutes, secCounter)
            }
            override fun onFinish() {
                Toast.makeText(context, "Time to Start Working!", Toast.LENGTH_LONG).show()
                binding.buttonStart.performClick()
                if (alarmState) {
                    playJingle(mediaPlayer, playMusicTime)
                }
                if (vibrationState) {
                    vibratePhone()
                }
                breakMinutes = breakTime
                secCounter = 60
            }
        }

        val timerWork = object: CountDownTimer(workMilli, 1000){
            override fun onTick(millisUntilFinished: Long) {
                if(workMinutes == workTime) {
                    workMinutes = workMinutes - 1
                }
                secCounter = secCounter-1
                if (secCounter < 1){
                    secCounter = 59
                    if(workMinutes > 0) {
                        workMinutes = workMinutes - 1
                    }

                }
                setTimer(workMinutes, secCounter)
            }
            override fun onFinish() {
                Toast.makeText(context, "Time for a break :)", Toast.LENGTH_LONG).show()
                timerBreak.start()
                if (alarmState) {
                    playJingle(mediaPlayer, playMusicTime)
                }
                if (vibrationState) {
                    vibratePhone()
                }
                workMinutes = workTime
                secCounter = 60
            }
        }


        binding.buttonStart.setOnClickListener {
            timerWork.start()
            Toast.makeText(this.context, "Time to Start Working!", Toast.LENGTH_LONG).show()
        }

        binding.buttonStop.setOnClickListener {
            try {
                timerWork.cancel()
                timerBreak.cancel()
                Toast.makeText(this.context, "Timer Stopped", Toast.LENGTH_LONG).show()
            }catch (e:Exception){
                Log.d("NoteThing", "No Timer: " + e)
                Toast.makeText(this.context, "No Timer active", Toast.LENGTH_LONG).show()
            }
        }

        try {
            binding.buttonChange.setOnClickListener {
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            }
        }catch (e:Exception){
            Log.d("NoteThing", "Exception: " + e)
        }

        binding.buttonRestart.setOnClickListener {
            try {
                timerWork.cancel()
                timerBreak.cancel()
            }catch (e:Exception){
                Log.d("NoteThing", "No Timer: " + e)
            }
            timerWork.start()
        }
    }


    fun setTimer(minute: Int, second: Int){
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

    }

    fun playJingle(mediaPlayer:MediaPlayer, playMusic: CountDownTimer){
        //mediaPlayer.prepare()
        mediaPlayer.start()
        playMusic.start()
        Log.d("NoteThing", "vibration: " + vibrationState + " alarm: " + alarmState)
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
            vibrationState = sharedPref.getBoolean("vibration", true)
            alarmState = sharedPref.getBoolean("alarm", true)
            Log.d("NoteThing", "vibration: " + vibrationState + " alarm: " + alarmState)

        }catch (e:Exception) {
            Log.d("NoteThing", ("No Counter:" + e))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}