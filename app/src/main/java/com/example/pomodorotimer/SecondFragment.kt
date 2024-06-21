package com.example.pomodorotimer

import android.R
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pomodorotimer.databinding.FragmentSecondBinding
import kotlin.math.round


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    var vibration = false
    var alarm = false
    var workTime = 1
    var breakTime = 1
    var workSeconds = 0
    var breakSeconds = 0
    var alarmsound = ""
    var volume = 5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        load()

       binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            vibration = isChecked
        }

        binding.switchAlarmsound.setOnCheckedChangeListener { _, isChecked ->
            alarm = isChecked
        }

        //work time
        binding.picker1.maxValue = 60
        binding.picker1.minValue = 0
        binding.picker2.maxValue = 59
        binding.picker2.minValue = 0

        //break time
        binding.picker3.maxValue = 60
        binding.picker3.minValue = 0
        binding.picker4.maxValue = 59
        binding.picker4.minValue = 0

        try {
            binding.picker1.value = workTime
            binding.picker2.value = workSeconds
            binding.picker3.value = breakTime
            binding.picker4.value = breakSeconds


            binding.switchVibration.isChecked = vibration
            binding.switchAlarmsound.isChecked= alarm
        }catch (e: Exception){
            Log.d("NoteThing", "Exception: " + e)
        }

        binding.textviewVolumeNumber.text = volume.toString()

        binding.buttonAlarmsoundUpload.setOnClickListener {
            chooseAlarm()
        }

        binding.buttonVolumePlus.setOnClickListener {
            if (volume >= 10){
                volume = 10
            }else{
                volume ++
            }
            binding.textviewVolumeNumber.text = volume.toString()
            Log.d("NoteThing", "Volume: " + volume)
        }

        binding.buttonVolumeMinus.setOnClickListener {
            if (volume <= 1){
                volume = 1
            }else{
                volume -= 1
            }
            binding.textviewVolumeNumber.text = volume.toString()
            Log.d("NoteThing", "Volume: " + volume)
        }

        binding.buttonSave.setOnClickListener {
            val pickerWork = binding.picker1.value
            val workSeconds = binding.picker2.value

            val pickerBreak = binding.picker3.value
            val breakSeconds = binding.picker4.value

            Log.d("NoteThing", "vibration: " + vibration + " alarm: " + alarm)

            save(pickerWork, pickerBreak, vibration, alarm, alarmsound, volume, workSeconds, breakSeconds)
            //findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    fun save(workTime: Int, breakTime: Int, vibration: Boolean, alarm: Boolean, alarmsound: String, volume: Int, worksec: Int, breaksec: Int ){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val editor = sharedPref.edit()
        try {
            editor.putInt("workTime", workTime)
            editor.putInt("breakTime", breakTime)
            editor.putInt("workSeconds", worksec)
            editor.putInt("breakSeconds", breaksec)
            editor.putBoolean("vibration", vibration)
            editor.putBoolean("alarm", alarm)
            editor.putString("alarmsound", alarmsound)
            editor.putInt("volume", volume)
            editor.apply()
            Log.d("NoteThing", "vibration: " + vibration + " alarm: " + alarm + "alarmsound: " + alarmsound + " volume: " + volume )
        } catch (e: Exception) {
            Log.d("NoteThing", ("No Counter in Save:" + e))
        }


    }

    fun load(){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

        try {
            workTime = sharedPref.getInt("workTime", 1)
            breakTime = sharedPref.getInt("breakTime", 1)
            workSeconds = sharedPref.getInt("workSeconds", 1)
            breakSeconds = sharedPref.getInt("breakSeconds", 1)
            vibration = sharedPref.getBoolean("vibration", true)
            alarm = sharedPref.getBoolean("alarm", true)
            alarmsound = sharedPref.getString("alarmsound", "").toString()
            volume = sharedPref.getInt("volume", 5)


        }catch (e:Exception) {
            Log.d("NoteThing", ("No Counter:" + e))
        }
    }

    fun chooseAlarm(){
        val intent: Intent
        intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "audio/mpeg"
        startActivityForResult(intent,1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                val audioFileUri = data.data
                alarmsound = audioFileUri.toString()
                Log.d("Pomo", audioFileUri.toString())
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}