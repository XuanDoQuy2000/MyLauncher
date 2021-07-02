package com.xuandq.mylauncher.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xuandq.mylauncher.R
import com.xuandq.mylauncher.utils.AppSetting
import kotlinx.android.synthetic.main.fragment_layout_setting.*


class LayoutSettingFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_layout_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        AppSetting.init(requireContext())

        btn_layout_1.setOnClickListener {
            AppSetting.resetLayout(requireContext())
            AppSetting.setLayout(1)

        }

        btn_layout_2.setOnClickListener {
            AppSetting.resetLayout(requireContext())
            AppSetting.setLayout(2)
        }

        btn_layout_reset.setOnClickListener {
            AppSetting.resetLayout(requireContext())
        }
    }

}