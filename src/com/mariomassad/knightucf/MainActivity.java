package com.mariomassad.knightucf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


import java.util.Locale;

import edu.ucf.poop.UCFMapBeta.GoogleApis.Coordinates;
import edu.ucf.poop.UCFMapBeta.GoogleApis.Directions;
import edu.ucf.poop.UCFMapBeta.GoogleApis.Settings;
import edu.ucf.poop.UCFMapBeta.GoogleApis.StaticUCFCoordinates;


import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
//import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements
		ActionBar.TabListener, AddDestinationDialogFragment.OnDestinationAddedListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	public ScheduleDbHelper mDbHelper;
	public HashMap<String,String> ucfLocations;
	public ArrayList<String> ucfNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Settings.calcPadding(this);
		Log.e("error", "THIS IS MAIN CREATE");
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		ucfNames = new ArrayList<String>();
		mDbHelper = new ScheduleDbHelper(this);
		ucfLocations = new HashMap<String,String>();
        try{
			InputStream test = this.getAssets().open("coordinateslist.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(test));
			String line;
	        while ((line = reader.readLine()) != null) {
	        	String coord = reader.readLine();
	        	Log.i("knightsucf", line + " " + coord);
	        	ucfLocations.put(line.toLowerCase(), coord);
	        	ucfNames.add(line);
	        }
		}catch(Exception e){
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	public void deleteEvent(View v){
		View viewer = (View)v.getParent();
		TextView vw = (TextView)viewer.findViewById(R.id.idListItem);
		String id = vw.getText().toString();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		db.delete("destinations", "_id =" + id, null );
		String tag = "android:switcher:" + mViewPager.getId() + ":"
                + 2;
		ScheduleFragment schedFrag = (ScheduleFragment)
                getSupportFragmentManager().findFragmentByTag(tag);
		if(schedFrag != null){
			schedFrag.adapter.getCursor().requery();
			schedFrag.adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			
			Intent intent = new Intent(this, AboutUs.class);
        	//setContentView(R.layout.activity_about_page);
        	startActivity(intent);
  
			return true;
		}
		else if(id == R.id.help_settings)
		{
			Intent intent = new Intent(this, HelpActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public ScheduleDbHelper getSQLiteDatabase(){
		return mDbHelper;
	}
	
	public HashMap getUcfLocations()
	{
		return ucfLocations;
	}
	
	public void showDialog(View button){
		AddDestinationDialogFragment frag = new AddDestinationDialogFragment();
		frag.show(getFragmentManager(), "fragment_main");
	}
	
	public ArrayList<String> getUcfNames()
	{
		return ucfNames;
	}
	public void showDestinations(View button){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] projection = {
				"description", "location", "start_time", "end_time", "days"
		};
		Cursor cursor = db.rawQuery("SELECT * FROM destinations", null);
		String txt = "HERE:\n";
		if(cursor.moveToFirst()){
			do{
				txt += cursor.getString(1);
				txt += cursor.getString(2);
				txt += cursor.getString(3);
				txt += cursor.getString(4);
				txt += cursor.getString(5);
				txt += "\n";
			}while(cursor.moveToNext());
		}
		
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		private Fragment mapFragment;
		private Fragment scheduleFragment;
		private Fragment homeFragment;
		
		public Fragment gethomeFragment()
		{
			if(homeFragment == null)
				homeFragment = new PlaceholderFragment();
			
			return homeFragment;
		}
		public Fragment getmapFragment()
		{
			if(mapFragment == null)
				mapFragment = new MapFragment();
			
			return mapFragment;
		}
		public Fragment getscheduleFragment()
		{
			if(scheduleFragment == null)
				scheduleFragment = new ScheduleFragment();
			
			return scheduleFragment;
		}
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch(position)
        	{
        	case 0:
        		return gethomeFragment();
 
        	case 1:
        		return getmapFragment();
        	
        	case 2:
        		return getscheduleFragment();
        	default:
        		return gethomeFragment();
        	}
			//return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	
	public void goToDestination(View v)
	{
		
		
		TextView tv = (TextView)(v.findViewById(R.id.coordinatesList));
		
		Coordinates coord = Coordinates.parseFromString(tv.getText().toString());
		
		MapFragment mapFrag = (MapFragment)mSectionsPagerAdapter.getmapFragment();
		
		Log.e("info", mapFrag.toString());
		
		//mViewPager.setCurrentItem(1);
		mViewPager.setCurrentItem(1);
		mapFrag.navToLocation(coord);
		
		Log.e("error", "view pager is " + mViewPager.toString());
		//mViewPager.setCurrentItem(1);
		
		
		
	}
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		
		public SimpleCursorAdapter adapter;
		public ScheduleDbHelper mDbHelper;
		
		private String destLocation;

		private Coordinates destCoordinates;
		
		
		public PlaceholderFragment() {
		}
		
		
		
		
		public void setUpAdapter(ListView list){
			String[] fromColumns = {"_id", "description", "location", "start_time", "end_time", "coordinates"};
			int[] toViews = {R.id.idListItem2, R.id.descriptionListItem2, R.id.locationListItem2, R.id.startTimeListItem2, R.id.endTimeListItem2, R.id.coordinatesList};
			mDbHelper = ((MainActivity)getActivity()).getSQLiteDatabase();
			SQLiteDatabase db = mDbHelper.getReadableDatabase();
			
			int day = getDay();
			Cursor cursor;
			String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
			cursor = db.rawQuery("SELECT * FROM destinations WHERE days LIKE \"%" + days[day-1] + "%\" ORDER BY start_time", null);
			
//			String coordinates = cursor.getString(5);
//			destCoordinates = Coordinates.parseFromString(coordinates);
			
			adapter = new SimpleCursorAdapter(getActivity(),
					R.layout.event_home_layout, cursor, fromColumns, toViews, 0);
			
			
			
					adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
				
				public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
	            	
					
					
					
	                if (columnIndex == 3 || columnIndex == 4) {
	                	
	                	
	                	int time = cursor.getInt(columnIndex);
	                	
	                	int hour = time/60;
	                	int min = time%60;
	                	
	                	String tOd = "AM";
	                	if(hour >= 12)
	                		{
	                			tOd = "PM";
	                		}
	                	if(hour == 0)
	                		hour = 12;
	                	
	                	if(hour > 12)
	                		hour -= 12;
	                	
	                	String answer = String.format("%d:%02d %s", hour, min, tOd);
	                	
	                	Log.v("error", view.toString());
	                	((TextView)view).setText(answer);
	                	
	                    return true;
	                }
	               
	                return false;
	            }
				
				
				
				
				
			});
			
			
			list.setAdapter(adapter);
		}
		
		public int getDay()
		{
			Calendar calendar = Calendar.getInstance();
			int day = calendar.get(Calendar.DAY_OF_WEEK);
			
			return day;
		}
		
		void goUCF()
		{
			
			
		}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			
			ListView list = (ListView)rootView.findViewById(android.R.id.list);
			setUpAdapter(list);
			
			return rootView;
		}
	}////end of placeholder fragment
	
	// AddDestinationDialogFragment notified MainActivity that an event has been added,
	// therefore we will update the listview in ScheduleFragment
	public void OnDestinationAddedListener(){
		String tag = "android:switcher:" + mViewPager.getId() + ":"
                + 2;
		ScheduleFragment schedFrag = (ScheduleFragment)
                getSupportFragmentManager().findFragmentByTag(tag);
		if(schedFrag != null){
			schedFrag.adapter.getCursor().requery();
			schedFrag.adapter.notifyDataSetChanged();
		}
	}
	


}