package com.madebyaron.betterhealth.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.madebyaron.betterhealth.R
import com.madebyaron.betterhealth.view.home.HomeFragment
import com.madebyaron.betterhealth.view.list.ListFragment
import com.madebyaron.betterhealth.view.steps.StepsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

	lateinit var currentScreen: Fragment

	lateinit var homeScreen: Fragment
	lateinit var listScreen: Fragment
	lateinit var stepsScreen: Fragment

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setSupportActionBar(toolbar)

		supportActionBar?.setDisplayShowTitleEnabled(false)
		toolbar_title.text = getString(R.string.app_name)

		homeScreen = HomeFragment()
		listScreen = ListFragment()
		stepsScreen = StepsFragment()

		navigateToScreen(homeScreen)
		bottom_navigation.setOnNavigationItemSelectedListener {
			when (it.itemId) {
				R.id.home_screen -> navigateToScreen(homeScreen)
				R.id.list_screen -> navigateToScreen(listScreen)
				R.id.steps_screen -> navigateToScreen(stepsScreen)
			}
			true
		}
	}

	override fun onBackPressed() {
		if (currentScreen == homeScreen) {
			finish()
		} else {
            bottom_navigation.selectedItemId = R.id.home_screen
			navigateToScreen(homeScreen)
		}
	}

	private fun navigateToScreen(screen: Fragment) {
		currentScreen = screen
		supportFragmentManager.beginTransaction()
			.replace(R.id.fragment_container, screen)
			.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
			.commit()
	}
}
