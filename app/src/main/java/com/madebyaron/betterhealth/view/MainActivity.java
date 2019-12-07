package com.madebyaron.betterhealth.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.madebyaron.betterhealth.R;
import com.madebyaron.betterhealth.databinding.ActivityMainBinding;
import com.madebyaron.betterhealth.view.home.HomeFragment;
import com.madebyaron.betterhealth.view.list.ListFragment;
import com.madebyaron.betterhealth.view.steps.StepsFragment;

public class MainActivity extends AppCompatActivity {

	private ActivityMainBinding binding;

	private Fragment currentScreen;

	private Fragment homeScreen;
	private Fragment listScreen;
	private Fragment stepsScreen;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

		setSupportActionBar(binding.toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);
		binding.toolbarTitle.setText(R.string.app_name);

		homeScreen = new HomeFragment();
		listScreen = new ListFragment();
		stepsScreen = new StepsFragment();

		navigateToScreen(homeScreen);
		binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
			switch (item.getItemId()) {
				case R.id.home_screen:
					navigateToScreen(homeScreen);
					break;
				case R.id.list_screen:
					navigateToScreen(listScreen);
					break;
				case R.id.steps_screen:
					navigateToScreen(stepsScreen);
					break;
			}

			return true;
		});
	}

	@Override
	public void onBackPressed() {
		if (currentScreen == homeScreen) {
			finish();
		} else {
			binding.bottomNavigation.setSelectedItemId(R.id.home_screen);
			navigateToScreen(homeScreen);
		}
	}

	private void navigateToScreen(Fragment screen) {
		currentScreen = screen;
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragment_container, screen)
			.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
			.commit();
	}
}
