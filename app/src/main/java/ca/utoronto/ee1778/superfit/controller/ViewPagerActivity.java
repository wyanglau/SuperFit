/**************************************************************************************************
  Filename:       ViewPagerActivity.java

  Copyright (c) 2013 - 2014 Texas Instruments Incorporated

  All rights reserved not granted herein.
  Limited License. 

  Texas Instruments Incorporated grants a world-wide, royalty-free,
  non-exclusive license under copyrights and patents it now or hereafter
  owns or controls to make, have made, use, import, offer to sell and sell ("Utilize")
  this software subject to the terms herein.  With respect to the foregoing patent
  license, such license is granted  solely to the extent that any such patent is necessary
  to Utilize the software alone.  The patent license shall not apply to any combinations which
  include this software, other than combinations with devices manufactured by or for TI ('TI Devices').
  No hardware patent is licensed hereunder.

  Redistributions must preserve existing copyright notices and reproduce this license (including the
  above copyright notice and the disclaimer and (if applicable) source code license limitations below)
  in the documentation and/or other materials provided with the distribution

  Redistribution and use in binary form, without modification, are permitted provided that the following
  conditions are met:

    * No reverse engineering, decompilation, or disassembly of this software is permitted with respect to any
      software provided in binary form.
    * any redistribution and use are licensed by TI for use only with TI Devices.
    * Nothing shall obligate TI to provide you with source code for the software licensed and provided to you in object code.

  If software source code is provided to you, modification and redistribution of the source code are permitted
  provided that the following conditions are met:

    * any redistribution and use of the source code, including any resulting derivative works, are licensed by
      TI for use only with TI Devices.
    * any redistribution and use of any object code compiled from the source code and any resulting derivative
      works, are licensed by TI for use only with TI Devices.

  Neither the name of Texas Instruments Incorporated nor the names of its suppliers may be used to endorse or
  promote products derived from this software without specific prior written permission.

  DISCLAIMER.

  THIS SOFTWARE IS PROVIDED BY TI AND TI'S LICENSORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL TI AND TI'S LICENSORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
  POSSIBILITY OF SUCH DAMAGE.


 **************************************************************************************************/
package ca.utoronto.ee1778.superfit.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerActivity extends FragmentActivity {
  // Constants
  // private static final String TAG = "ViewPagerActivity";

  // GUI
  protected static ViewPagerActivity mThis = null;
  protected SectionsPagerAdapter mSectionsPagerAdapter;
  private ViewPager mViewPager;
  protected int mResourceFragmentPager;
  protected int mResourceIdPager;
  private int mCurrentTab = 0;
  protected Menu optionsMenu;
  private MenuItem refreshItem;
  protected boolean mBusy;

  protected ViewPagerActivity() {
    // Log.d(TAG, "construct");
    mThis = this;
    mBusy = false;
    refreshItem = null;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(mResourceFragmentPager);
    mViewPager = (ViewPager) findViewById(mResourceIdPager);
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    mViewPager.setAdapter(mSectionsPagerAdapter);
  }


  @Override
  public void onDestroy() {
    super.onDestroy();
    mSectionsPagerAdapter = null;
  }

  @Override
  public void onBackPressed() {
    if (mCurrentTab != 0)
      getActionBar().setSelectedNavigationItem(0);
    else
      super.onBackPressed();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Log.d(TAG, "onOptionsItemSelected");
    // Handle presses on the action bar items
    switch (item.getItemId()) {
    // Respond to the action bar's Up/Home button
    case android.R.id.home:
      onBackPressed();
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  protected void showBusyIndicator(final boolean busy) {
  	mBusy = busy;
  }
  
  protected void refreshBusyIndicator() {
  	if (refreshItem == null) {
  		runOnUiThread(new Runnable() {

  			@Override
  			public void run() {
  				showBusyIndicator(mBusy);
  			}
  		});
  	}
  }
  

  public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragmentList;
    private List<String> mTitles;

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
      mFragmentList = new ArrayList<Fragment>();
      mTitles = new ArrayList<String>();
    }

    public void addSection(Fragment fragment, String title) {
      //final ActionBar actionBar = getActionBar();
      mFragmentList.add(fragment);
      mTitles.add(title);
      //actionBar.addTab(actionBar.newTab().setText(title).setTabListener(tabListener));
      notifyDataSetChanged();
      // Log.d(TAG, "Tab: " + title);
    }

    @Override
    public Fragment getItem(int position) {
      return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
      return mTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      if (position < getCount()) {
        return mTitles.get(position);
      } else {
        return null;
      }
    }
  }
}
