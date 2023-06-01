package com.edasinar.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TeacherMessageInfo(val email: String, val messageLabel: String,
                              val messageBody: String, val fileDownloadUrl: String) : Parcelable
