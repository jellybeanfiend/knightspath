package com.mariomassad.knightucf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ScheduleFragment extends Fragment {

	int count = 0;
	public ScheduleDbHelper mDbHelper;
	public SimpleCursorAdapter adapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.activity_schedule_fragment, container, false);
		
		ListView list = (ListView)rootView.findViewById(android.R.id.list);
		setUpAdapter(list);
		
		return rootView;
	}
	
	// Sets up a SimpleCursorAdapter to populate the listview with database data
	public void setUpAdapter(ListView list){
		String[] fromColumns = {"_id", "description", "location", "start_time", "end_time", "days"};
		int[] toViews = {R.id.idListItem, R.id.descriptionListItem, R.id.locationListItem, R.id.startTimeListItem, R.id.endTimeListItem, R.id.sun};
		mDbHelper = ((MainActivity)getActivity()).getSQLiteDatabase();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM destinations", null);
		
		// Maps data by column with views
		adapter = new SimpleCursorAdapter(getActivity(),
				R.layout.event_layout, cursor, fromColumns, toViews, 0);
		// Handles the "days" data, which uses a binary string and checks the togglebuttons
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            	String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            	int[] ids = {R.id.sun, R.id.mon, R.id.tue, R.id.wed, R.id.thu, R.id.fri, R.id.sat};
                //Let the adapter handle the binding if the column is not TYPE
                
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
            	
            	
            	if (columnIndex == 5) {
                	
                	String dayList = cursor.getString(5);
                	
                	for (int i =0; i < 7; i++){
                		ToggleButton tb = (ToggleButton)((View)view.getParent()).findViewById(ids[i]);
                		if(tb != null)
                		tb.setChecked(false);
                	}
                	
                    for (int i = 0; i < 7; i++) {
                    	ToggleButton tb = (ToggleButton)((View)view.getParent()).findViewById(ids[i]);
                    	if(tb != null)
                    	tb.setClickable(false);
                    	if(tb != null && dayList.contains(days[i]))
                    		tb.setChecked(true);
                    }
                    return true;
                }
                return false;
            }
        });
		list.setAdapter(adapter);
	}

	public class CustomArrayAdapter extends ArrayAdapter<Event> {

	    Context context;

	    public CustomArrayAdapter(Context context, int textViewResourceId, List<Event> objects) {
	        super(context, textViewResourceId, objects);
	        // TODO Auto-generated constructor stub
	        this.context = context;
	    }

	    /*private view holder class*/
	    private class ViewHolder {
	        
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder = null;
	        Event rowItem = getItem(position);

	        LayoutInflater mInflater = (LayoutInflater) context
	                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.event_layout, null);
	            holder = new ViewHolder();
	            
	            convertView.setTag(holder);
	        } else
	            holder = (ViewHolder) convertView.getTag();

	        
	        //holder.imageView.setImageResource(rowItem.getImageId());

	        return convertView;
	    }

	}
}

