package com.example.knightspath;

import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.*;
import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.*;


public class Layout2Fragment extends Fragment {

	
	private static final String ARG_SECTION_NUMBER = "section_number";
	private GoogleMap googleMap;
	static final LatLng TutorialsPoint = new LatLng(21, 57);
	
	public static Layout2Fragment newInstance(int sectionNumber)
	{
		Layout2Fragment fragment = new Layout2Fragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}
	
	public Layout2Fragment()
	{
		
	}

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_layout2, container, false);
		
		
		try{
			if(googleMap == null)
			{
				googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
				//googleMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map).getMap();
			}
			googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	
			//Marker TP = googleMap.addMarker(new MarkerOptions().position(TutorialsPoint).title("TutorialsPoint"));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return rootView;
	}
	
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		ft.remove(fragment);
		ft.commit();
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
	}
	
}