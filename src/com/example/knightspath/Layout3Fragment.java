package com.example.knightspath;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.*;
//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.*;


public class Layout3Fragment extends Fragment {

	
	private static final String ARG_SECTION_NUMBER = "section_number";
	
	public static Layout3Fragment newInstance(int sectionNumber)
	{
		Layout3Fragment fragment = new Layout3Fragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}
	
	public Layout3Fragment()
	{
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_layout3, container, false);
		
		return rootView;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
	}
	
}