package com.android.everglam.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.android.everglam.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun Activity.showOptionAlert(title: String, message: String, pbName: String, listener: () -> Unit){
    val alertDialog = AlertDialog.Builder(this)
    alertDialog.setTitle(title)
    alertDialog.setMessage(message)
    alertDialog.setCancelable(false)

    alertDialog.setNegativeButton("Cancel") { dialogInterface, _ ->
        dialogInterface.dismiss()
    }.setPositiveButton(pbName) { dialogInterface, _ ->
        listener.invoke()
        dialogInterface.dismiss()
    }
    alertDialog.show()
}

fun <T> Activity.goToWithBundle(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    val intent = Intent(this, it)
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
}

fun <T> Activity.goTo(it: Class<T>) {
    startActivity(Intent(this, it))
}

fun Activity.showShortSnack(mView: View, s: String) {

    val snackBarView = Snackbar.make(mView, s , Snackbar.LENGTH_LONG)
    val view = snackBarView.view
    val params = view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    view.layoutParams = params
    view.background = ContextCompat.getDrawable(this,R.color.app_dark_color_2) // for custom background
    snackBarView.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
    snackBarView.show()
}
