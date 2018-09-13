package no.danielzeller.metaballslib

import android.graphics.drawable.Drawable

interface MetaBallAdapter {

    fun menuItemBackgroundColor(index: Int): Int
    fun menuItemIcon(index: Int): Drawable?
    fun menuItemIconTint(index: Int): Int
    fun itemsCount(): Int
}

class EmptyAdapter : MetaBallAdapter {
    override fun menuItemIconTint(index: Int): Int {
        return 0
    }

    override fun menuItemBackgroundColor(index: Int): Int {
        return 0
    }

    override fun menuItemIcon(index: Int): Drawable? {
        return null
    }

    override fun itemsCount(): Int {
        return 0
    }
}