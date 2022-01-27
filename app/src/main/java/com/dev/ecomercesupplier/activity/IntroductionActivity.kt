package com.dev.ecomercesupplier.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.fragment.IntroFragment
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_introduction.*

class IntroductionActivity : AppCompatActivity() {
    var doubleClick:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)
        setUpViews()
    }


    private fun setUpViews() {
        SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.isFirstTime, true)
        val pagerAdapter = ScreenSlidePagerAdapter(this)

        viewPager2.adapter = pagerAdapter
        TabLayoutMediator(tabLayout, viewPager2){ tab, position ->

        }.attach()

    }

    private inner class ScreenSlidePagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int{
            return 3
        }

        override fun createFragment(position: Int): Fragment {

            val fragment= IntroFragment(position)
            return fragment
        }
    }
    override fun onBackPressed() {
        exitApp()
    }
    private fun exitApp() {
        val toast = Toast.makeText(
                this,
                getString(R.string.please_click_back_again_to_exist),
                Toast.LENGTH_SHORT
        )

        if(doubleClick){
            finishAffinity()
            doubleClick=false
        } else{
            doubleClick=true
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                toast.show()
                doubleClick=false
            }, 300)
        }
    }
}