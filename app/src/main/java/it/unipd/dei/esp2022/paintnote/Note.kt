package it.unipd.dei.esp2022.paintnote

import android.graphics.Bitmap

data class Note(var id: Int, var title: String, var bitmap: Bitmap? = null, var isBackgroundWhite: Boolean? = null)