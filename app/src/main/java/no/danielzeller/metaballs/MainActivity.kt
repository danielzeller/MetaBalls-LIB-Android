package no.danielzeller.metaballs

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_viewpager.*
import kotlinx.android.synthetic.main.bullet_text.view.*
import kotlinx.android.synthetic.main.demo_card_bottom.view.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import no.danielzeller.metaballslib.menu.MetaBallMenuBase
import no.danielzeller.metaballslib.progressbar.MBProgressBar
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    val LOG_TAG = "META BALL DEMO"

    private val menuIcons = intArrayOf(R.drawable.ic_facebook, R.drawable.ic_instagram,
            R.drawable.ic_twitter, R.drawable.ic_linkedin, R.drawable.ic_dribbble,
            R.drawable.ic_google, R.drawable.ic_vimeo, R.drawable.ic_behance)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupMetaBallMenuDemo()
        setupPageIndicatorDemo()
    }

    private fun setupMetaBallMenuDemo() {
        val card1MetaBallMenuAdapter = createMetaBallMenuAdapter(3, R.array.card_menu_single_colors)
        val card2MetaBallMenuAdapter = createMetaBallMenuAdapter(8, R.array.card2_menu_colors)
        val card3MetaBallMenuAdapter = createMetaBallMenuAdapter(3, R.array.card_menu_single_colors)
        val card4And5MetaBallMenuAdapter = createMetaBallMenuAdapter(4, R.array.card_menu_single_colors)

        setupCardView(R.id.demo_card1, card1MetaBallMenuAdapter, R.color.pastel_yellow, R.array.demo_card_1_description, R.string.demo_card_link1_and5)
        setupCardView(R.id.demo_card2, card2MetaBallMenuAdapter, R.color.pastel_green, R.array.demo_card_2_description, R.string.demo_card_link2)
        setupCardView(R.id.demo_card3, card3MetaBallMenuAdapter, R.color.pastel_pink, R.array.demo_card_3_description, R.string.demo_card_link3)
        setupCardView(R.id.demo_card4, card4And5MetaBallMenuAdapter, R.color.pastel_blue, R.array.demo_card_4_description, R.string.demo_card_link4)
        setupCardView(R.id.demo_card5, card4And5MetaBallMenuAdapter, R.color.pastel_beach, R.array.demo_card_5_description, R.string.demo_card_link1_and5)
    }

    private fun setupPageIndicatorDemo() {
        bottomViewPager.adapter = ImagePagerAdapter()
        metaBallPageIndicator.attachToViewPager(bottomViewPager)
        metaBallPageIndicatorSmall.attachToViewPager(bottomViewPager)
        metaBallPageIndicator.onDotClicked = { pageIndex ->
            bottomViewPager.setCurrentItem(pageIndex, true)
        }
    }


    /**
     *
     * Create some simple adapters with colors for each menu item and icons
     */
    private fun createMetaBallMenuAdapter(count: Int, colorArrayId: Int): MetaBallMenuAdapter {
        val menuItems = ArrayList<MenuItem>()
        val colors = resources.getIntArray(colorArrayId)
        for (i in 0 until count) {
            menuItems.add(MenuItem(colors[i], resources.getDrawable(menuIcons[i], null), Color.WHITE))
        }
        return MetaBallMenuAdapter(menuItems)
    }


    /**
     *
     * Setup the card view with description and MetaBall menu
     */
    private fun setupCardView(cardId: Int, adapter: MetaBallMenuAdapter, color: Int, stringArrayId: Int, demo_card_link: Int) {

        val cardView = findViewById<View>(cardId)

        //Setup the metaball menu with an adapter and click listener. Showing a spinner when a menu item is clicked.
        val metaBallMenu = cardView.findViewById<MetaBallMenuBase>(R.id.metaBallMenu)
        metaBallMenu.adapter = adapter
        metaBallMenu.onItemSelectedListener = { index ->
            Log.i(LOG_TAG, "Clicked menu item: $index")
            if (cardView.findViewById<View>(R.id.mbProgressBar).visibility != View.VISIBLE) {
                showSpinnerAndHideDelayed(cardView)
            }
            metaBallMenu.toggleMenu()
        }

        // Setup the rest of the View with description and colors
        cardView.unsplashIcon.background.setTint(ContextCompat.getColor(this, color))

        val descriptionText = resources.getStringArray(stringArrayId)
        cardView.headingTextView.text = descriptionText[0]

        cardView.captionTextView.setText(demo_card_link)
        cardView.captionTextView.movementMethod = LinkMovementMethod.getInstance()


        for (i in 1 until descriptionText.size) {
            val bulletView = LayoutInflater.from(this).inflate(R.layout.bullet_text, null, false)
            bulletView.bulletText.text = descriptionText[i]
            cardView.bulletListContainer.addView(bulletView, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        }
    }


    /**
     *
     * Shows a MetaBall Progressbar and hides it delayed
     */
    private fun showSpinnerAndHideDelayed(cardView: View) {
        val progressBar = cardView.findViewById<MBProgressBar>(R.id.mbProgressBar)
        progressBar.visibility = View.VISIBLE
        val progressBarWeakRef = WeakReference<MBProgressBar>(progressBar)

        val job = launch(UI) {
            delay(4800, TimeUnit.MILLISECONDS)
            progressBarWeakRef.get()?.stopAnimated()
        }

        //The View holds on to the coroutine so that we can cancel it in onPause :)
        cardView.setTag(R.string.job_coroutine_key_id, job)
    }

    override fun onPause() {
        super.onPause()

        for (i in 0 until scrollViewRoot.childCount) {
            val cardView = scrollViewRoot.getChildAt(i)
            val job = cardView.getTag(R.string.job_coroutine_key_id)
            if (job != null && job is Job) {
                job.cancel()
                cardView.findViewById<MBProgressBar>(R.id.mbProgressBar).visibility = View.GONE
            }
        }
    }

    inner class ImagePagerAdapter : PagerAdapter() {

        private val images = intArrayOf(R.drawable.img_pastel_yellow, R.drawable.img_pastel_green, R.drawable.img_pastel_pink, R.drawable.img_pastel_blue, R.drawable.img_pastel_beach)

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = ImageView(container.context)
            imageView.setImageResource(images[position])
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            container.addView(imageView)
            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as View)
        }

        override fun getCount(): Int {
            return images.size
        }
    }
}
