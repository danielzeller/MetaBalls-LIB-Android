package no.danielzeller.metaballslib.spinner.drawables

import android.view.View
import no.danielzeller.metaballslib.spinner.SpinneHiddenListener

interface SpinnerDrawable {
    fun stopAndHide(spinner: View, spinnerHiddenListener: SpinneHiddenListener?)

    fun startAnimations()

    fun stopAllAnimations()
}
