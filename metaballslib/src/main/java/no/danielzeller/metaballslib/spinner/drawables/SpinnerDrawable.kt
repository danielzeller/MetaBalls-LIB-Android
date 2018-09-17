package no.danielzeller.metaballslib.spinner.drawables

import android.graphics.drawable.Drawable
import android.view.View
import no.danielzeller.metaballslib.spinner.SpinneHiddenListener

abstract class SpinnerDrawable : Drawable() {

    abstract fun stopAndHide(spinner: View, spinnerHiddenListener: SpinneHiddenListener?)

    abstract fun startAnimations()

    abstract fun stopAllAnimations()
    abstract fun setDrop(isDrop: Boolean)
    var isDropDrawable = false
    var rotate = false
}
