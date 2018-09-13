package no.danielzeller.metaballslib

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent

class DecreasedTouchImageView(context: Context, val touchAreaSize: Float) : android.support.v7.widget.AppCompatImageView(context) {

    val tempRect = Rect()

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN && !getDecreasedHitRect().contains(ev.x.toInt(), ev.y.toInt())) {
            return false
        } else {
            return super.onTouchEvent(ev)
        }
    }

    fun getDecreasedHitRect(): Rect {
        val widtOffset = (width - touchAreaSize) / 2f
        val heightOffset = (height - touchAreaSize) / 2f

        tempRect.set((widtOffset).toInt(), (heightOffset).toInt(), (width - widtOffset).toInt(), (height - heightOffset).toInt())

        return tempRect;
    }
}
