package no.danielzeller.metaballs

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
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

class MainActivityDemo : AppCompatActivity() {

    private val LOG_TAG = "METABALL_DEMO"

    private val menuIcons = intArrayOf(R.drawable.facebook_animation, R.drawable.instagram_animation,
            R.drawable.twitter_animation, R.drawable.linkedin_animation, R.drawable.dribble_animation,
            R.drawable.google_animation, R.drawable.vimeo_animation, R.drawable.behance_animation)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_demo)

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
            Log.i(LOG_TAG, "Clicked menu item: " + index)
            if (cardView.findViewById<View>(R.id.mpProgressBar).visibility != View.VISIBLE) {
                showSpinnerAndHideDelayed(cardView)
            }
            metaBallMenu.toggleMenu()
        }

        // Setup the rest of the View with description and colors
        cardView.unsplashIcon.background.setTint(ContextCompat.getColor(this, color))

        val descriptionText = resources.getStringArray(stringArrayId)
        cardView.headingTextView.text = descriptionText[0]

        cardView.captionTextView.setText(demo_card_link)
        cardView.captionTextView.setMovementMethod(LinkMovementMethod.getInstance());


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
        val progressBar = cardView.findViewById<MBProgressBar>(R.id.mpProgressBar)
        progressBar.visibility = View.VISIBLE
        val progressBarWeakRef = WeakReference<MBProgressBar>(progressBar)

        val job = launch(UI) {
            delay(4800, TimeUnit.MILLISECONDS)
            progressBarWeakRef.get()?.stopAnimated()
        }
        runningJobs.add(Pair(job, progressBarWeakRef))
    }

    //Cancel the jobs
    private var runningJobs = ArrayList<Pair<Job, WeakReference<MBProgressBar>>>()

    override fun onPause() {
        super.onPause()
        for (job in runningJobs) {
            job.first.cancel()
            job.second.get()?.visibility = View.GONE
        }
    }

}
