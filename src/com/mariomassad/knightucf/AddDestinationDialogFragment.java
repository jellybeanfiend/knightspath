package com.mariomassad.knightucf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import edu.ucf.poop.UCFMapBeta.GoogleApis.Destination;
import edu.ucf.poop.UCFMapBeta.GoogleApis.Places;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AddDestinationDialogFragment extends DialogFragment implements Places.GooglePlacesNewSearchResultsCallback{
	
	public Button start_picker;
	public Button end_picker;
	public String desc;
	public String location;
	public String daysSelected;
	static Button currentPicker;
	public String coordinates;
	private LinearLayout repeatDays;
	private String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
	private View view;
	private ScheduleDbHelper mDbHelper;
	private ContentValues values;
	private HashMap<String,String> ucfLocations;
	private ArrayList<String> ucfLocationNames;
	private SQLiteDatabase db;
	private OnDestinationAddedListener mListener;
	private AutoCompleteTextView loc;
	private Destination[] results;
	private String[] options;
	private FragmentManager fm;
	private ProgressDialog progress;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        fm = getFragmentManager();
        Places.assignCallbackObj(this);
        view = inflater.inflate(R.layout.add_destination, null);
        ucfLocations = ((MainActivity)getActivity()).getUcfLocations();
        loc = (AutoCompleteTextView) view.findViewById(R.id.location);
        progress = new ProgressDialog(getActivity());
    	progress.setTitle("Searching");
    	
    	
        ucfLocationNames = ((MainActivity)getActivity()).getUcfNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, ucfLocationNames);
        loc.setAdapter(adapter);
        builder.setView(view).setTitle(R.string.add_destination_title)
               .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   setInput();
                	   mDbHelper = ((MainActivity)getActivity()).getSQLiteDatabase();
                       db = mDbHelper.getWritableDatabase();
                       values = new ContentValues();
                       values.put("description", desc);
                       values.put("start_time", convertTime(start_picker.getText().toString()));
                       values.put("end_time", convertTime(end_picker.getText().toString()));
                       values.put("days", daysSelected);
               		
	               		if(ucfLocations.containsKey(location) || location.equals("")){
	               			values.put("location", location);
	               			values.put("coordinates", ucfLocations.get(location));
	               			db.insert("destinations", null, values);
	               	        mListener.OnDestinationAddedListener();
	               		}
	               		else{
	               			progress.setMessage("Finding options that match "+location);
	               			progress.show();
	               			Places.startSearch(location);
	               		}
                	   
                       // values inserted into db in onNewSearchResults(), 
                       // which is a callback for getting coordinates for a location
                   }
               })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        
        	start_picker = (Button)view.findViewById(R.id.start_picker);
        	start_picker.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                	showTimePickerDialog(v);
                  }
            });
        	end_picker = (Button)view.findViewById(R.id.end_picker);
        	end_picker.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                	showTimePickerDialog(v);
                  }
            });
        	repeatDays = (LinearLayout) view.findViewById(R.id.repeat_days);
        	
            for (int i = 0; i < 7; i++) {
            	final View btn = inflater.inflate(R.layout.day_button, null);
            	final ToggleButton tb = (ToggleButton) btn.findViewById(R.id.btn);
            	tb.setText(days[i]);
            	tb.setTextOn(days[i]);
            	tb.setTextOff(days[i]);
            	tb.setPadding(0, 0, 0, 0);
            	tb.setWidth(0);
            	repeatDays.addView(tb);
            }
        // Create the AlertDialog object and return it
        return builder.create();
    }
	
    public interface OnDestinationAddedListener {
        public void OnDestinationAddedListener();
    }
	
	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		
		
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
			DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			String pm;
			int hour = hourOfDay;
			
			if(hour > 12)
			{
				hour -= 12;
				pm = "PM";
			}
			else
				pm = "AM";
			
			currentPicker.setText(hour + ":" + ( minute < 10 ? "0" : "") + minute+ " " + pm);	
		}
	}
	
	public int convertTime(String time) {

		if (time.length() == 0)
			return 0;

		boolean isPM = false;

		// isPM = date.
		String[] myS = time.split("[:\\s]");
		if (myS[2].equals("PM")) {
			isPM = true;
		}

		int hour = Integer.parseInt(myS[0]);
		int min = Integer.parseInt(myS[1]);
		int total = hour * 60 + min;

		if (isPM) {
			total += 60 * 12;
		}

		return total;
	}
	
	public void setInput(){
		desc = ((EditText)view.findViewById(R.id.description)).getText().toString();
		location = ((EditText)view.findViewById(R.id.location)).getText().toString().toLowerCase();
		daysSelected = "";
		int childcount = repeatDays.getChildCount();
		for (int i=0; i < childcount; i++){
		      ToggleButton tb = (ToggleButton)repeatDays.getChildAt(i);
		      if(tb.isChecked()){
		    	  daysSelected += days[i]+",";
		      }
		}
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
        	mListener = (OnDestinationAddedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
	
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "timePicker");
	    currentPicker = (Button)view.findViewById(v.getId());
	}
	
	public void onNewSearchResults() {
		Log.i("knightsucf", "callbacktime");
		results = Places.getLastResultList();
		if(results != null && results.length > 0){
			options = new String[results.length];
			for(int i = 0; i < results.length; i++){
				Log.i("knightsucf",results[i].getTitle());
				options[i] = results[i].getTitle()+"\n"+results[i].getDescription();
			}
			SelectPlaceDialog frag = new SelectPlaceDialog();
			frag.show(fm, "fragment_main");
		}

		progress.dismiss();
		//progress.dismiss();
	}
	
	public class SelectPlaceDialog extends DialogFragment {
	    @Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	
		    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setTitle("Choose Place")
		           .setItems(options, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		            	   Log.i("knightsucf", "INDEX:"+which);
		            	   values.put("coordinates", results[which].getLocation().toString());
		            	   values.put("location", results[which].getTitle());
		           		   db.insert("destinations", null, values);
		                   mListener.OnDestinationAddedListener();
		           }
		    });
		    return builder.create();
		}
	}
	

}

