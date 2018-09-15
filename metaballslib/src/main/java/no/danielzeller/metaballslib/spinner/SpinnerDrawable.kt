package no.danielzeller.metaballslib.spinner

import android.view.View

interface SpinnerDrawable {
    fun stopAndHide(spinner: View)

    fun startAnimations()

    fun stopAllAnimations()
}
