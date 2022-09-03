package com.mahmoudrh.roomxml.application

import android.app.Application
import android.content.Context
import com.mahmoudrh.roomxml.BuildConfig
import com.mahmoudrh.roomxml.R
import dagger.hilt.android.HiltAndroidApp
import org.acra.config.dialog
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra

@HiltAndroidApp
class NotesApplication: Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON

            dialog {
                text = getString(R.string.dialog_text)
                title = getString(R.string.dialog_title)
                positiveButtonText = getString(R.string.dialog_positive)
                negativeButtonText = getString(R.string.dialog_negative)
                commentPrompt = getString(R.string.dialog_comment)
                resIcon = R.drawable.ic_dialog_icon
                resTheme = R.style.Theme_DNote
            }

            mailSender {
                mailTo = "dnote.developer@gmail.com"
                subject = "D-Note Crash Report"
                reportFileName = "Crash.txt"
            }
        }
    }
}