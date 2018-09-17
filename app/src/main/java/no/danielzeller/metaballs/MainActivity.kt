package no.danielzeller.metaballs

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.view.*
import no.danielzeller.metaballslib.MetaBallPageIndicator
import no.danielzeller.metaballslib.menu.DirectionalMenu
import no.danielzeller.metaballslib.progressbar.MBProgressBar
import no.danielzeller.metaballslib.progressbar.MBProgressBarType

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
        handler.postDelayed(testSpinners(true, true, MBProgressBarType.CIRCULAR, spinnerTest), 3000)
        handler.postDelayed(testSpinners(true, true, MBProgressBarType.EIGHT, spinnerTest), 6000)
        handler.postDelayed(testSpinners(true, true, MBProgressBarType.LONG_PATH, spinnerTest), 9000)
        handler.postDelayed(testSpinners(true, true, MBProgressBarType.SQUARE, spinnerTest), 12000)
        handler.postDelayed(testSpinners(true, true, MBProgressBarType.BLOBS, spinnerTest), 15000)
        handler.postDelayed(testSpinners(true, true, MBProgressBarType.DOTS, spinnerTest), 18000)


        handler.postDelayed(testSpinners(false, false, MBProgressBarType.CIRCULAR, spinnerTest), 21000)
        handler.postDelayed(testSpinners(false, false, MBProgressBarType.EIGHT, spinnerTest), 24000)
        handler.postDelayed(testSpinners(false, false, MBProgressBarType.BLOBS, spinnerTest), 27000)
        handler.postDelayed(testSpinners(false, false, MBProgressBarType.DOTS, spinnerTest), 30000)
        handler.postDelayed(testSpinners(false, false, MBProgressBarType.LONG_PATH, spinnerTest), 33000)
        handler.postDelayed(testSpinners(false, false, MBProgressBarType.SQUARE, spinnerTest), 36000)

        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = SectionsPagerAdapter(supportFragmentManager)
        findViewById<MetaBallPageIndicator>(R.id.pageIndicator).attachToViewPager(viewPager)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }

    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
            rootView.section_label.text = "HORSE" + arguments?.getInt(ARG_SECTION_NUMBER)
            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    fun testSpinners(isDrop: Boolean, isRotate: Boolean, type: MBProgressBarType, spinner: MBProgressBar): Runnable {
//        return Runnable {
//            spinner.visibility = View.GONE
//            spinner.isDropDrawable = isDrop
//            spinner.isRotate = isRotate
//            spinner.mbProgressBarType = type
//            Handler().postDelayed({
//                spinner.visibility = View.VISIBLE
//            },10)
//
//        }
        return Runnable {
            spinner.stopAnimated({
                spinner.isDropDrawable = isDrop
                spinner.isRotate = isRotate
                spinner.mbProgressBarType = type
                spinner.visibility = View.VISIBLE
                spinner.colorArray = resources.getIntArray(R.array.default_spinner_colors)
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
