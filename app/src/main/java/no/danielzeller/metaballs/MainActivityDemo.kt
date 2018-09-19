package no.danielzeller.metaballs

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.content.res.AppCompatResources
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.bullet_text.view.*
import kotlinx.android.synthetic.main.demo_card1.view.*
import kotlinx.android.synthetic.main.demo_card_bottom.view.*
import no.danielzeller.metaballslib.menu.MetaBallMenuBase

class MainActivityDemo : AppCompatActivity() {

    private val LOG_TAG = "METABALL_DEMO"
    val menuIcons = intArrayOf(R.drawable.facebook_animation, R.drawable.instagram_animation,
            R.drawable.twitter_animation, R.drawable.linkedin_animation, R.drawable.dribble_animation,
            R.drawable.google_animation, R.drawable.vimeo_animation, R.drawable.behance_animation)

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_demo)


        setupCardView(R.id.demo_card1, createAdapter(3, R.array.card_menu_single_colors), R.color.pastel_yellow, R.array.demo_card_1,R.string.demo_card_link)
        setupCardView(R.id.demo_card2, createAdapter(8, R.array.card2_menu_colors), R.color.pastel_green, R.array.demo_card_2, R.string.demo_card_link2)
        setupCardView(R.id.demo_card3, createAdapter(3, R.array.card3_menu_colors), R.color.pastel_pink, R.array.demo_card_3, R.string.demo_card_link3)
        setupCardView(R.id.demo_card4, createAdapter(4, R.array.card_menu_single_colors), R.color.pastel_blue, R.array.demo_card_4, R.string.demo_card_link4)
        setupCardView(R.id.demo_card5, createAdapter(4, R.array.card_menu_single_colors), R.color.pastel_beach, R.array.demo_card_1, R.string.demo_card_link)
    }

    private fun createAdapter(count: Int, colorArrayId: Int): MetaBallMenuAdapter {
        val menuItems = ArrayList<MenuItem>()
        val colors = resources.getIntArray(colorArrayId)
        for (i in 0 until count) {
            menuItems.add(MenuItem(colors[i], resources.getDrawable(menuIcons[i], null), Color.WHITE))
        }
        return MetaBallMenuAdapter(menuItems)
    }

    private fun setupCardView(cardId: Int, adapter: MetaBallMenuAdapter, color: Int, stringArrayId: Int, demo_card_link: Int) {
        val cardView = findViewById<View>(cardId)
        cardView.unsplashIcon.background = AppCompatResources.getDrawable(this, R.drawable.ic_oval)!!.mutate()
        cardView.unsplashIcon.background.setTint(ContextCompat.getColor(this, color))

        val strings = resources.getStringArray(stringArrayId)
        cardView.headingTextView.text = strings[0]

        cardView.captionTextView.setText(demo_card_link)
        cardView.captionTextView.setMovementMethod(LinkMovementMethod.getInstance());


        for (i in 1 until strings.size) {
            val bulletView = LayoutInflater.from(this).inflate(R.layout.bullet_text, null, false)
            bulletView.bulletText.text = strings[i]
            cardView.bulletListContainer.addView(bulletView, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }

        val metaBallMenu = cardView.findViewById<MetaBallMenuBase>(R.id.metaBallMenu)
        metaBallMenu.adapter = adapter
        metaBallMenu.onItemSelectedListener = { index ->
            Log.i(LOG_TAG, "Clicked menu item: " + index)
            showSpinner(cardView)
            metaBallMenu.toggleMenu()
        }
    }


    private fun showSpinner(cardView: View) {
        cardView.progressBar.visibility = View.VISIBLE

        //Pretending we do some work here and hiding the progressbar after a sek delay
        handler.postDelayed(hideDelayedRunnable(cardView), 4800)

    }

    fun hideDelayedRunnable(cardView: View): Runnable {
        return object : Runnable {
            override fun run() {
                cardView.progressBar.stopAnimated()
            }
        }
    }
}
