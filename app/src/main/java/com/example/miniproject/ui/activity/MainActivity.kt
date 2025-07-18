package com.example.miniproject.ui.activity

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.miniproject.R
import com.example.miniproject.ui.fragment.ActivitiesLogFragment
import com.example.miniproject.ui.fragment.HomeFragment
import com.example.miniproject.ui.fragment.RequestFragment
import com.example.miniproject.utils.AppFormatters
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var drawer_layout : DrawerLayout
    lateinit var toolbar : MaterialToolbar
    lateinit var navigationMenu : NavigationView
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

        drawer_layout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navigationMenu = findViewById(R.id.nav_menu)
        date = findViewById(R.id.txt_date)

        toolbar.setNavigationOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }

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

        setTextDate()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


     fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)
            .commit()
    }

     fun setTextDate() {
         val currentDate = LocalDateTime.now()
         date.text = currentDate.format(AppFormatters.displayDate)
    }
}
