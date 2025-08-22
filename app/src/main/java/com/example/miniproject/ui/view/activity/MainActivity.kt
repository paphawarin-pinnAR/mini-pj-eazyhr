package com.example.miniproject.ui.view.activity

import HrController
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.miniproject.R
import com.example.miniproject.ui.view.fragment.ActivitiesLogFragment
import com.example.miniproject.ui.view.fragment.HomeFragment
import com.example.miniproject.ui.view.fragment.RequestFragment
import com.example.miniproject.utils.DateTimeUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import java.time.LocalDate
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private lateinit var drawer_layout : DrawerLayout
    private lateinit var toolbar : MaterialToolbar
    private lateinit var navigationMenu : NavigationView
    lateinit var date : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        //Load 1st fragment in the MainActivity
        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, HomeFragment())
                .commit()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupToolbarListener()
        setupNavigationItemSelectedListener()
        setTextDate()
    }

   private fun initViews(){
        drawer_layout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navigationMenu = findViewById(R.id.nav_menu)
        date = findViewById(R.id.txt_date)
    }

    private fun setupNavigationItemSelectedListener(){
        navigationMenu.setNavigationItemSelectedListener { menuItem -> menuItem.isChecked = true
            drawer_layout.closeDrawer(GravityCompat.START)

            //check the current fragment
            val currentFragment = supportFragmentManager.findFragmentById(R.id.frame)

            when (menuItem.itemId) {
                R.id.menu_home -> {
                    if (currentFragment !is HomeFragment){
                        replaceFragment(HomeFragment())
                    }
                }
                R.id.menu_request -> {
                    if (currentFragment !is RequestFragment){
                        replaceFragment(RequestFragment())
                    }
                }
                R.id.menu_activities_log -> {
                    if (currentFragment !is ActivitiesLogFragment){
                        replaceFragment(ActivitiesLogFragment())
                    }
                }
            }

            true
        }
    }

    private fun setupToolbarListener(){
        toolbar.setNavigationOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)
            .commit()
    }

    private fun setTextDate() {
        val currentDate = LocalDate.now()
        date.text = DateTimeUtils.formatDate(currentDate)
    }
}
