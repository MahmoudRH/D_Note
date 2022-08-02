package com.mahmoudrh.roomxml.application

import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors
import com.mahmoudrh.roomxml.BuildConfig
import com.mahmoudrh.roomxml.R
import com.mahmoudrh.roomxml.data.NotesDB
import com.mahmoudrh.roomxml.data.NotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.acra.config.dialog
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra

class NotesApplication: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { NotesDB.getInstance(this,applicationScope) }
    val repository by lazy { NotesRepository(database.NoteDAO()) }

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
                resTheme = R.style.Theme_Material3_Light_Dialog
            }

            mailSender {
                mailTo = "dnote.developer@gmail.com"
                subject = "D-Note Crash Report"
                reportFileName = "Crash.txt"
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}