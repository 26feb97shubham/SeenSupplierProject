package com.dev.ecomercesupplier.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.fragment.UploadImageVideoFragment
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.about_us_more_info_frag_toolbar.view.*
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    var doubleClick:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Utility.setLanguage(this, SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
    }
    companion object{
        var type:String=""
    }
    private fun setUpViews() {
        if(intent != null){
           type=intent.getStringExtra("type").toString()
        }
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END)
        menuImg.setOnClickListener {
            opencloseDrawer()
        }

        other_frag_menu.setOnClickListener {
            opencloseDrawer()
        }

        frag_profile_menu.setOnClickListener {
            opencloseDrawer()
        }

        about_us_fragment_toolbar.frag_about_us_menu.setOnClickListener {
            opencloseDrawer()
        }
    }

    private fun opencloseDrawer() {
        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END)
        }
        else{
            drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END)
            return
        }
        when(findNavController(R.id.nav_home_host_fragment).currentDestination?.id){
            R.id.homeFragment -> exitApp()
            R.id.revenueFragment -> {
                itemHome1.setImageResource(R.drawable.selected_home)
                findNavController(R.id.nav_home_host_fragment).navigate(R.id.homeFragment)
            }

            R.id.profileFragment -> {
                itemHome1.setImageResource(R.drawable.selected_home)
                findNavController(R.id.nav_home_host_fragment).navigate(R.id.homeFragment)
            }
            /*R.id.uploadImageVideoFragment -> {

                if(UploadImageVideoFragment.uploadCount==UploadImageVideoFragment.pathList.size){
                    findNavController(R.id.nav_home_host_fragment).popBackStack()
                }
            }*/
            else-> findNavController(R.id.nav_home_host_fragment).popBackStack()
        }
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
        }
        else{

            doubleClick=true
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                toast.show()
                doubleClick=false
            }, 300)
        }
    }
}