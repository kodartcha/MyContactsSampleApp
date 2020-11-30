package com.gabriel_codarcea.mycontacts.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.gabriel_codarcea.mycontacts.R
import com.gabriel_codarcea.mycontacts.data.model.DownloadStatus
import com.gabriel_codarcea.mycontacts.databinding.ActivityLauncherBinding
import com.gabriel_codarcea.mycontacts.view_model.LauncherViewModel
import kotlinx.android.synthetic.main.activity_launcher.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LauncherActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_PERMISSION = 1234
    }

    private val viewModel by viewModel<LauncherViewModel>()

    private lateinit var binding: ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        initBindings()
        initObservers()

        supportActionBar?.hide()

        requestPermission()
    }

    private fun initBindings() {
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_launcher
        )
    }

    private fun initObservers() {
        viewModel.downloadContactsStatus.observe(this, { downloadStatus ->
            when (downloadStatus.state) {
                DownloadStatus.FINISHED -> navigateToContactsActivity()
                DownloadStatus.FAILED -> showErrorDialog()
            }
        })
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            REQUEST_CODE_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSION &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            checkAndDownloadContacts()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun checkAndDownloadContacts() {
        viewModel.downloadContactsStatus.value?.let {
            when (it.state) {
                DownloadStatus.EMPTY -> {
                    showLoading()
                    viewModel.downloadContacts()
                }
                DownloadStatus.FINISHED -> {
                    navigateToContactsActivity()
                }
            }
        }
    }

    private fun navigateToContactsActivity() {
        hideLoading()
        val contactsActivityIntent = Intent(
            this@LauncherActivity,
            ContactsActivity::class.java
        )
        startActivity(contactsActivityIntent)
        finish()
    }

    private fun showLoading() {
        progress_circular.visibility = View.VISIBLE
        loading_text.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progress_circular.visibility = View.INVISIBLE
        loading_text.visibility = View.INVISIBLE
    }

    private fun showErrorDialog() {
        hideLoading()
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(resources.getString(R.string.error_dialog_title))
        alertDialogBuilder.setMessage(resources.getString(R.string.error_dialog_message))
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(resources.getString(R.string.error_dialog_btn_retry)) { dialog, _ ->
            dialog.dismiss()
            showLoading()
            viewModel.downloadContacts()
        }
        alertDialogBuilder.setNegativeButton(resources.getString(R.string.error_dialog_btn_exit)) { dialog, _ ->
            dialog.dismiss()
            finish()
        }
        alertDialogBuilder.show()
    }
}
