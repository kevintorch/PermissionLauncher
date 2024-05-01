package com.kevintorch.permissionlauncher

import android.Manifest
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kevintorch.permission_launcher.PermissionLauncher

class MainActivity : AppCompatActivity() {
    private val permissionLauncher = PermissionLauncher(this, ::onPermissionGranted)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun onPermissionGranted() {
        // Do Something if Permission Granted.
    }
}