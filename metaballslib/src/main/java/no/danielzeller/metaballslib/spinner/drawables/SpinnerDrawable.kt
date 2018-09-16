package no.danielzeller.metaballslib.spinner.drawables

import android.view.View

interface SpinnerDrawable {
    fun stopAndHide(spinner: View)

    fun startAnimations()

    fun stopAllAnimations()
}
