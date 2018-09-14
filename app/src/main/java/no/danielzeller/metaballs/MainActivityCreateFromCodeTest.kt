package no.danielzeller.metaballs

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.Toast
import no.danielzeller.metaballslib.menu.DirectionalMenu
import no.danielzeller.metaballslib.menu.MetaBallMenuBase
import no.danielzeller.metaballslib.menu.PositionGravity

class MainActivityCreateFromCodeTest : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = FrameLayout(this)
        setContentView(root)
        val directionalMenu = DirectionalMenu(this)
        root.addView(directionalMenu, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))

        Handler().postDelayed({ testSetters(directionalMenu) }, 1000)
        Handler().postDelayed({ directionalMenu.positionGravity = PositionGravity.TOP_LEFT }, 2000)
        Handler().postDelayed({ initializeAdaper(createLargeMenuItemList(), directionalMenu) }, 3000)

    }

    private fun testSetters(directionalMenu: DirectionalMenu) {
        directionalMenu.marginBetweenMenuItems = (50f * resources.displayMetrics.density).toInt()

        directionalMenu.mainButtonIconColor = Color.BLUE
        directionalMenu.mainButtonIcon = resources.getDrawable(R.drawable.ic_share_black_24dp, null)
        directionalMenu.mainButtonColor = Color.RED

        directionalMenu.delayBetweenItemsAnimation = 20
        directionalMenu.closeAnimationDuration = 800
        directionalMenu.openAnimationDuration = 800

        directionalMenu.openInterpolatorAnimator = AnimationUtils.loadInterpolator(this, R.anim.overshoot_interpolator)
        directionalMenu.closeInterpolatorAnimator = AnimationUtils.loadInterpolator(this, R.anim.overshoot_interpolator)
    }

    private fun createLargeMenuItemList(): ArrayList<MenuItem> {
        val menuItem1 = ArrayList<MenuItem>()
        menuItem1.add(MenuItem(resources.getColor(R.color.colorPrimary), resources.getDrawable(R.drawable.facebook_animation, null), Color.WHITE))
        menuItem1.add(MenuItem(resources.getColor(R.color.colorAccent), resources.getDrawable(R.drawable.instagram_animation, null), Color.WHITE))
        menuItem1.add(MenuItem(resources.getColor(R.color.colorPrimary), resources.getDrawable(R.drawable.twitter_animation, null), Color.WHITE))
        menuItem1.add(MenuItem(resources.getColor(R.color.colorAccent), resources.getDrawable(R.drawable.linkedin_animation, null), Color.WHITE))
        menuItem1.add(MenuItem(resources.getColor(R.color.colorPrimary), resources.getDrawable(R.drawable.dribble_animation, null), Color.RED))
        menuItem1.add(MenuItem(resources.getColor(R.color.colorAccent), resources.getDrawable(R.drawable.google_animation, null), Color.WHITE))
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

    private fun initializeAdaper(menuItem1: ArrayList<MenuItem>, circularTopRight: MetaBallMenuBase) {
        circularTopRight.adapter = MetaBallMenuAdapter(menuItem1)
        circularTopRight.onItemSelectedListener = { index ->
            Toast.makeText(baseContext, "Clicked: " + index, Toast.LENGTH_LONG).show()
            circularTopRight.toggleMenu()
        }
    }
}
