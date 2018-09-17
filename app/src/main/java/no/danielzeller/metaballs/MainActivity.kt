package no.danielzeller.metaballs

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import no.danielzeller.metaballslib.menu.DirectionalMenu
import no.danielzeller.metaballslib.progressbar.MBProgressBar
import no.danielzeller.metaballslib.progressbar.MBProgressBarType
import no.danielzeller.metaballslib.progressbar.SpinneHiddenListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        initializeAdaper(createSmallMenuItemList(), R.id.circularMenuTopRight)
        initializeAdaper(createSmallMenuItemList(), R.id.circularMenuTopLeft)
        initializeAdaper(createSmallMenuItemList(), R.id.circularMenuBottomRight)
        initializeAdaper(createSmallMenuItemList(), R.id.circularMenuBottomLeft)
        initializeAdaper(createSmallMenuItemList(), R.id.circularMenuCenter)
        val spinnerTest = findViewById<MBProgressBar>(R.id.dotsSpinner)
        spinnerTest.mbProgressBarType = MBProgressBarType.LONG_PATH
        spinnerTest.isRotate = true
        val handler = Handler()
        handler.postDelayed(testSpinners(true, true,  MBProgressBarType.CIRCULAR,   spinnerTest), 3000)
        handler.postDelayed(testSpinners(true, true,  MBProgressBarType.EIGHT,   spinnerTest), 6000)
        handler.postDelayed(testSpinners(true, true,  MBProgressBarType.LONG_PATH,   spinnerTest), 9000)
        handler.postDelayed(testSpinners(true, true,  MBProgressBarType.SQUARE,   spinnerTest), 12000)
        handler.postDelayed(testSpinners(true, true,  MBProgressBarType.BLOBS,   spinnerTest), 15000)
        handler.postDelayed(testSpinners(true, true,  MBProgressBarType.DOTS,   spinnerTest), 18000)


        handler.postDelayed(testSpinners(false, false,  MBProgressBarType.CIRCULAR,   spinnerTest), 21000)
        handler.postDelayed(testSpinners(false, false,  MBProgressBarType.EIGHT,   spinnerTest), 24000)
        handler.postDelayed(testSpinners(false, false,  MBProgressBarType.BLOBS,   spinnerTest), 27000)
        handler.postDelayed(testSpinners(false, false,  MBProgressBarType.DOTS,   spinnerTest), 30000)
        handler.postDelayed(testSpinners(false, false,  MBProgressBarType.LONG_PATH,   spinnerTest), 33000)
        handler.postDelayed(testSpinners(false, false,  MBProgressBarType.SQUARE,   spinnerTest), 36000)
    }

    fun testSpinners(isDrop: Boolean, isRotate: Boolean, type: MBProgressBarType,   spinner: MBProgressBar): Runnable {
        return Runnable {
            spinner.stopAnimated(object : SpinneHiddenListener {
                override fun onSpinnHidden(spinne: View) {
                    spinner.isDropDrawable = isDrop
                    spinner.isRotate = isRotate
                    spinner.mbProgressBarType = type
                    spinner.visibility = View.VISIBLE
                }
            })

        }
    }

    private fun createLargeMenuItemList(): ArrayList<MenuItem> {
        val menuItem1 = ArrayList<MenuItem>()
        menuItem1.add(MenuItem(resources.getColor(R.color.colorPrimary), resources.getDrawable(R.drawable.facebook_animation, null), Color.WHITE))
        menuItem1.add(MenuItem(resources.getColor(R.color.colorPrimary), resources.getDrawable(R.drawable.instagram_animation, null), Color.WHITE))
        menuItem1.add(MenuItem(resources.getColor(R.color.colorPrimary), resources.getDrawable(R.drawable.twitter_animation, null), Color.WHITE))
        menuItem1.add(MenuItem(resources.getColor(R.color.colorPrimary), resources.getDrawable(R.drawable.linkedin_animation, null), Color.WHITE))
        menuItem1.add(MenuItem(resources.getColor(R.color.colorPrimary), resources.getDrawable(R.drawable.dribble_animation, null), Color.WHITE))
        menuItem1.add(MenuItem(resources.getColor(R.color.colorPrimary), resources.getDrawable(R.drawable.google_animation, null), Color.WHITE))
        menuItem1.add(MenuItem(resources.getColor(R.color.colorPrimary), resources.getDrawable(R.drawable.vimeo_animation, null), Color.WHITE))
        menuItem1.add(MenuItem(resources.getColor(R.color.colorPrimary), resources.getDrawable(R.drawable.behance_animation, null), Color.WHITE))
        return menuItem1
    }

    private fun createSmallMenuItemList(): ArrayList<MenuItem> {
        val menuItem2 = ArrayList<MenuItem>()
        menuItem2.add(MenuItem(resources.getColor(R.color.colorAccent), resources.getDrawable(R.drawable.facebook_animation, null), Color.WHITE))
        menuItem2.add(MenuItem(resources.getColor(R.color.colorPrimary), resources.getDrawable(R.drawable.instagram_animation, null), Color.WHITE))
        menuItem2.add(MenuItem(resources.getColor(R.color.colorAccent), resources.getDrawable(R.drawable.twitter_animation, null), Color.WHITE))
        menuItem2.add(MenuItem(resources.getColor(R.color.colorPrimary), resources.getDrawable(R.drawable.linkedin_animation, null), Color.WHITE))
        return menuItem2
    }

    private fun initializeAdaper(menuItem1: ArrayList<MenuItem>, viewId: Int) {
        val circularTopRight = findViewById<DirectionalMenu>(viewId)
        circularTopRight.adapter = MetaBallMenuAdapter(menuItem1)
        circularTopRight.onItemSelectedListener = { index ->
            circularTopRight.toggleMenu()
        }
    }
}
