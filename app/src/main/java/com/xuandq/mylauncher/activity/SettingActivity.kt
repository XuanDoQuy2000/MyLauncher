package com.xuandq.mylauncher.activity

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.xuandq.mylauncher.R

class SettingActivity : AppCompatActivity() {

    companion object{
        const val TAG = "SettingActivity"
        val REQUEST_PERMISSIONS = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        const val REQUEST_CODE = 1101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        if (allPermissionGranted()){

        }else{
            ActivityCompat.requestPermissions(
                this,
                REQUEST_PERMISSIONS,
                REQUEST_CODE)
        }

    }

    private fun allPermissionGranted() : Boolean = REQUEST_PERMISSIONS.all {
        ContextCompat
            .checkSelfPermission(
            applicationContext,
            it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (allPermissionGranted()){

        }else {
            finish()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}