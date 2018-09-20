package no.danielzeller.metaballslib.menu

import android.graphics.drawable.Drawable

interface MetaBallAdapter {

    fun menuItemBackgroundColor(index: Int): Int
    fun menuItemIcon(index: Int): Drawable?
    fun menuItemIconTint(index: Int): Int
    fun itemsCount(): Int
}

class EmptyAdapter : MetaBallAdapter {

    /**
     *
     * Returns the icon tint for the menu item
     */
    override fun menuItemIconTint(index: Int): Int {
        return 0
    }

    /**
     *
     * Returns the background tint for the menu item
     */
    override fun menuItemBackgroundColor(index: Int): Int {
        return 0
    }

    /**
     *
     * Returns the icon drawable for the menu item. Can be null.
     */
    override fun menuItemIcon(index: Int): Drawable? {
        return null
    }

    /**
     *
     * Menu items count
     */
    override fun itemsCount(): Int {
        return 0
    }
}