package no.danielzeller.compbat

import android.content.Context
import android.graphics.Canvas

/**
 * Created by danielzeller on 26/03/2018.
 */
open class SnapshotTexture {
    var surfaceTextureID: Int = 0

    open fun getTextureID():Int{
        return surfaceTextureID
    }
    open fun createSurface(width: Int, height: Int, context: Context) {}

    open fun updateTexture() {
    }

    open fun isLoaded(): Boolean {
        return true
    }

    open fun releaseSurface() {
    }

    open fun beginDraw(): Canvas? {
        return null
    }
}