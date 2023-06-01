package com.edasinar.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageInfo(val email: String, val teacher: String, val messageLabel: String,
                       val messageBody: String, val fileDownloadUrl: String) : Parcelable
