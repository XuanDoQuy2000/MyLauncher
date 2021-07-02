package com.xuandq.mylauncher.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xuandq.mylauncher.R
import com.xuandq.mylauncher.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_setting.*


class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item_2.setOnClickListener {
            val intent = Intent(Settings.ACTION_HOME_SETTINGS)
            startActivity(intent)
        }

        item_3.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        }

        item_4.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_layoutSettingFragment)
        }
    }


}