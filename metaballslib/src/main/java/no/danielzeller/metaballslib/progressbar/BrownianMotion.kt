package no.danielzeller.metaballslib.progressbar

import android.os.SystemClock
import java.util.*

class BrownianMotion {

    var positionFrequency = 0.25f
    var positionAmplitude = 0.5f
    private var positionScale = Vector3(1000f, 1000f, 1000f)
    var positionFractalLevel = 3
    private val fbmNorm = 1 / 0.75f

    private val time: FloatArray = FloatArray(6)
    val position = Vector3(0f, 0f, 0f)
    private val frameRate = FrameRateCounter()

    init {
        rehash()
        frameRate.timeStep()
    }

    constructor(positionScale: Vector3) {
        this.positionScale = positionScale
    }

    fun rehash() {
        for (i in 0..5) {
            time[i] = MathHelper.randomRange(-10000.0f, 0.0f)
        }
    }


    fun update() {

        val deltaTime = frameRate.timeStep()
        for (i in 0..2) {
            time[i] += positionFrequency * deltaTime
        }
        var n = Vector3(
                Perlin.fbm(time[0], positionFractalLevel),
                Perlin.fbm(time[1], positionFractalLevel),
                Perlin.fbm(time[2], positionFractalLevel))

        n = Vector3.scale(n, positionScale)
        n.x *= positionAmplitude * fbmNorm
        n.y *= positionAmplitude * fbmNorm
        n.z *= positionAmplitude * fbmNorm

        position.x = n.x
        position.y = n.y
        position.z = n.z
    }

    object Perlin {

        internal var perm = intArrayOf(151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180, 151)

        fun noise(x: Float): Float {
            var x = x
            val x1 = Math.floor(x.toDouble()).toInt() and 0xff
            x -= Math.floor(x.toDouble()).toFloat()
            val u = fade(x)
            return lerp(u, grad(perm[x1], x), grad(perm[x1 + 1], x - 1)) * 2
        }

        fun fbm(x: Float, octave: Int): Float {
            var x = x
            var f = 0.0f
            var w = 0.5f
            for (i in 0 until octave) {
                f += w * noise(x)
                x *= 2.0f
                w *= 0.5f
            }
            return f
        }

        internal fun fade(t: Float): Float {
            return t * t * t * (t * (t * 6 - 15) + 10)
        }

        internal fun lerp(t: Float, a: Float, b: Float): Float {
            return a + t * (b - a)
        }

        internal fun grad(hash: Int, x: Float): Float {
            return if (hash and 1 == 0) x else -x
        }
    }
}

class Vector3(var x: Float, var y: Float, var z: Float) {
    companion object {
        fun scale(inVector: Vector3, scaleVector: Vector3): Vector3 {
            inVector.x *= scaleVector.x
            inVector.y *= scaleVector.y
            inVector.z *= scaleVector.z
            return inVector
        }
    }
}

object MathHelper {
    var rand = Random()
    fun randomRange(min: Float, max: Float): Float {

        val randomNum = rand.nextInt(max.toInt() - min.toInt() + 1) + min.toInt()

        return randomNum.toFloat()
    }
}

class FrameRateCounter {
    private var mLastTime: Long = 0

    fun timeStep(): Float {
        val time = SystemClock.uptimeMillis()
        val timeDelta = time - mLastTime
        val timeDeltaSeconds = if (mLastTime > 0.0f) timeDelta / 1000.0f else 0.0f
        mLastTime = time
        return Math.min(0.021f, timeDeltaSeconds)
    }
}

