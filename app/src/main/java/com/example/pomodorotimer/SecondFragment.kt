package com.example.pomodorotimer

import android.content.Context
import android.os.Bundle
import android.os.CombinedVibration
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pomodorotimer.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var vibration = false
    var alarmsound = false
    var workTime = 1
    var breakTime = 1
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
            alarmsound = isChecked
        }

        binding.picker1.maxValue = 100
        binding.picker1.minValue = 1
        binding.picker2.maxValue = 100
        binding.picker2.minValue = 1

        try {
            binding.picker1.value = workTime
            binding.picker2.value = breakTime

            binding.switchVibration.isChecked = vibration
            binding.switchAlarmsound.isChecked= alarmsound
        }catch (e: Exception){
            Log.d("NoteThing", "Exception: " + e)
        }


        binding.buttonSave.setOnClickListener {
            val pickerWork = binding.picker1.value
            val pickerBreak = binding.picker2.value

            Log.d("NoteThing", "vibration: " + vibration + " alarm: " + alarmsound)

            save(pickerWork, pickerBreak, vibration, alarmsound)
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    fun save(workTime: Int, breakTime: Int, vibration: Boolean, alarm: Boolean ){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val editor = sharedPref.edit()
        try {
            editor.putInt("workTime", workTime)
            editor.putInt("breakTime", breakTime)
            editor.putBoolean("vibration", vibration)
            editor.putBoolean("alarm", alarm)
            editor.apply()
        } catch (e: Exception) {
            Log.d("NoteThing", ("No Counter in Save:" + e))
        }


    }

    fun load(){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

        try {
            workTime = sharedPref.getInt("workTime", 1)
            breakTime = sharedPref.getInt("breakTime", 1)
            vibration = sharedPref.getBoolean("vibration", true)
            alarmsound = sharedPref.getBoolean("alarm", true)


        }catch (e:Exception) {
            Log.d("NoteThing", ("No Counter:" + e))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}