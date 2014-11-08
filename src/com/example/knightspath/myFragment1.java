package com.example.knightspath;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.TextView;

import ucf.knightspathlib.*;
import ucf.knightspathlib.GoogleMapsWrapper.*;

public class myFragment1 extends Fragment{

	
	public static Fragment newInstance(Context context)
	{
		myFragment1 f = new myFragment1();
		
		return f;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.activity_help, container,false);
	}
}
