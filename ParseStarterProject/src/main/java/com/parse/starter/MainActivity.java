/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseAnalytics;

import java.util.Locale;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

  SectionsPagerAdapter mSectionPagerAdapter;

  ViewPager mViewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final ActionBar actionBar = getSupportActionBar();
    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue)));
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    mSectionPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mSectionPagerAdapter);

    mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
      public void onPageSelected(int position){
        actionBar.setSelectedNavigationItem(position);
      }
    });

    for(int i = 0; i <mSectionPagerAdapter.getCount(); i++){
      actionBar.addTab(actionBar.newTab().setText(mSectionPagerAdapter.getPageTitle(i)).setTabListener(this));
      actionBar.setSplitBackgroundDrawable(getResources().getDrawable(android.R.color.white));
      actionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.color.blue));
    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
    mViewPager.setCurrentItem(tab.getPosition());

  }

  @Override
  public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

  }

  @Override
  public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

  }

  public class SectionsPagerAdapter extends FragmentPagerAdapter{

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      if(position == 0){
        return new BrowseFragment();
      }else{
        return new SearchFragment();
      }
    }

    @Override
    public int getCount() {
      return 2;
    }

    public CharSequence getPageTitle(int position){
      Locale l = Locale.getDefault();
      switch (position){
        case 0:
          return "Browse".toUpperCase(l);
        case 1:
          return "Search".toUpperCase(l);
      }
      return null;

    }

  }
}
