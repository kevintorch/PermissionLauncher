package com.kevintorch.permission_launcher

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PermissionLauncher {
    private var activity: AppCompatActivity? = null
    private val callback: ActivityResultCallback<Boolean>
    private val launcher: ActivityResultLauncher<String>
    private var rationaleMessage: CharSequence? = null

    private var enableRationale = true

    constructor(activity: AppCompatActivity, callback: ActivityResultCallback<Boolean>) {
        this.activity = activity
        this.callback = callback
        launcher = activity.registerForActivityResult(RequestPermission(), callback)
    }

    constructor(fragment: Fragment, callback: ActivityResultCallback<Boolean>) {
        this.activity = fragment.activity as AppCompatActivity?
        this.callback = callback
        launcher = fragment.registerForActivityResult(RequestPermission(), callback)
    }

    constructor(activity: AppCompatActivity, onGranted: Runnable) {
        this.activity = activity
        this.callback = ActivityResultCallback { granted: Boolean ->
            if (granted) onGranted.run()
        }
        launcher = activity.registerForActivityResult(RequestPermission(), this.callback)
    }

    constructor(fragment: Fragment, onGranted: Runnable) {
        if (fragment.activity != null) {
            activity = fragment.activity as? AppCompatActivity
        } else {
            fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onCreate(owner: LifecycleOwner) {
                    activity = fragment.activity as? AppCompatActivity
                }
            })
        }
        this.callback = ActivityResultCallback { granted -> if (granted) onGranted.run() }
        launcher = fragment.registerForActivityResult(RequestPermission(), this.callback)
    }

    @JvmOverloads
    fun setEnableRationale(enableRationale: Boolean, rationaleMessage: CharSequence? = null) {
        this.enableRationale = enableRationale
        this.rationaleMessage = rationaleMessage
    }

    fun launch(permission: String) {
        if (activity == null) {
            throw IllegalArgumentException("Activity is null")
        }
        if (isPermissionsGranted(activity!!, permission)) {
            callback.onActivityResult(true)
            return
        }
        if (enableRationale && ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                permission
            )
        ) {
            // Display a dialog with rationale.
            RationaleAlertDialog(activity!!, permission).show()
        } else {
            launcher.launch(permission)
        }
    }

    private fun isPermissionsGranted(activity: AppCompatActivity, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    private inner class RationaleAlertDialog(context: Context, permission: String) :
        MaterialAlertDialogBuilder(context) {
        init {
            setTitle("Permission Required")
            setMessage(rationaleMessage ?: defaultRationaleMessage(permission))
            setPositiveButton("Allow") { dialog: DialogInterface?, which: Int ->
                launch(permission)
            }
            setNegativeButton("No Thanks", null)
        }
    }

    private fun defaultRationaleMessage(permission: String): String {
        return """ 
            "${permissionName(permission)}" permission is required to work properly.
            """.trimIndent()
    }

    private fun permissionName(permission: String): String {
        val name = permission.replace("android.permission.", "")
        val words = name.split("_")
        return words.joinToString(" ") { it.lowercase().replaceFirstChar { it.uppercase() } }
    }
}
