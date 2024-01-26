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
import com.example.pomodorotimer.databinding.OptionsBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Options : Fragment() {

    private var _binding: OptionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var vibration = true
    var alarmsound = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = OptionsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            vibration = isChecked
        }
        binding.switchAlarmsound.setOnCheckedChangeListener { _, isChecked ->
            alarmsound = isChecked
        }



    }

}