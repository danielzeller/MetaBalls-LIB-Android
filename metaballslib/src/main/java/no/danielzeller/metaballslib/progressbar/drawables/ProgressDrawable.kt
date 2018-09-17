package no.danielzeller.metaballslib.progressbar.drawables

import android.graphics.drawable.Drawable
import android.view.View
import no.danielzeller.metaballslib.progressbar.SpinneHiddenListener

abstract class ProgressDrawable : Drawable() {

    abstract fun stopAndHide(spinner: View, spinnerHiddenListener: SpinneHiddenListener?)

    abstract fun startAnimations()

    abstract fun stopAllAnimations()
    abstract fun setDrop(isDrop: Boolean)
    var isDropDrawable = false
    var rotate = false
}
