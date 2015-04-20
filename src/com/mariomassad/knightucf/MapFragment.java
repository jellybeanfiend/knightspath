package com.mariomassad.knightucf;



import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.ucf.poop.UCFMapBeta.GoogleApis.Coordinates;
import edu.ucf.poop.UCFMapBeta.GoogleApis.Directions;
import edu.ucf.poop.UCFMapBeta.GoogleApis.Maps;
import edu.ucf.poop.UCFMapBeta.GoogleApis.Places;
import edu.ucf.poop.UCFMapBeta.GoogleApis.PolylinePlacer;
import edu.ucf.poop.UCFMapBeta.GoogleApis.Settings;
import edu.ucf.poop.UCFMapBeta.GoogleApis.StaticUCFCoordinates;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class MapFragment extends Fragment implements Directions.GoogleDirectionsNewPolylineCallback{

	public GoogleMap googleMap;
	public ArrayList<String> path;
	
	//private static Coordinates lastValidCoord = StaticUCFCoordinates.STUDENT_UNION;
	ScheduleDbHelper mDbHelper;
	
	public void onNewPolyline()
	{
	    PolylineOptions p = Directions.getLastPolyline();
		getActivity().runOnUiThread(new PolylinePlacer(googleMap, p, Directions.getLastBounds()));
	}
	
	public void navToLocation(Coordinates coord)
	{
		Activity a = getActivity();
		if(a == null)
			return;
		Coordinates loc = this.getCurrentLocation();
		if(loc != null)
		{
			if(loc.latitude == 200)
				Toast.makeText(getActivity().getApplicationContext(), "GPS Not Enabled", 1000).show();
			Directions.requestDrawDirections(loc, coord, googleMap);
			
		}
		else
			Toast.makeText(getActivity().getApplicationContext(), "GPS Not Enabled", 1000).show();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		
		
		View rootView = inflater.inflate(R.layout.activity_map_fragment, container, false);
		Settings.calcPadding(getActivity());
		
		
	
		setRetainInstance(true);
	

		Log.e("error", "THIS IS MAP CREATE");
		//googleMap = ((SupportMapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		try
		{
			googleMap = ((SupportMapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
//			if(googleMap == null)
//			{
//				googleMap = ((SupportMapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
//				Log.e("error", "returnign a map");
//			}
			//googleMap.setMyLocationEnabled(true);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		Directions.assignCallbackObj(this);
		

		googleMap.setMyLocationEnabled(true);
		Maps.goTo(googleMap, StaticUCFCoordinates.STUDENT_UNION);
		
	    
		return rootView;
	}
	
	private void setUpMapIfNeeded()
	{
		if(googleMap == null)
		{
			googleMap = ((SupportMapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		}
		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		setUpMapIfNeeded();
		
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		googleMap = ((SupportMapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		googleMap = null;
		
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		googleMap = null;
		
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		googleMap = null;
	}

	public Coordinates getCurrentLocation()
	{
		if(googleMap != null)
		{
			
			return new Coordinates(googleMap.getMyLocation());
			
			
		}
		else
			return null;
	}
	
	public ArrayList<String> getDestinations(Coordinates start){
		ArrayList<String> coordinates = new ArrayList<String>(); 
		coordinates.add(start.toString());
		mDbHelper = ((MainActivity)getActivity()).getSQLiteDatabase();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM destinations", null);
		if(cursor.moveToFirst()){
			do{
				String coords = cursor.getString(6);
				coordinates.add(coords);
			}while(cursor.moveToNext());
		}
		return coordinates;
	}
}
