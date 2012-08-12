package team2.project3;

import java.util.HashMap;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobdb.android.GetRowData;
import com.mobdb.android.MobDB;
import com.mobdb.android.MobDBResponseListener;
import com.mobdb.android.UpdateRowData;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*  the organization's view of the events tab. 
 *  similar to the user's view of events except
 *  organizations have a button to add events onto
 *  the list, with a max of five events per org
 */

public class OrganizationsEvents extends Activity {
	
	// MobDB constants
	String APP_KEY = "dhTii1-5T3-WoBi0DpadReeum39O1iPixXx-cutCsWBLUcBJWoW";
	String USER_TABLE_NAME = "orgpop";
	// MobDB constants
	
	// event's UI views
	Button mAddEvent;
	ListView mListView;
	
	String[] mEvents;
	String[] mEventInfo;
	
	String[] list;
	boolean full = true;
	int nullNum;
    
	String un;
	String pw;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.orgevents);
	    
	    // number of null events
	    nullNum = 0;
	    
	    un = getIntent().getExtras().getString("un");
		pw = getIntent().getExtras().getString("pw");

		// parallel arrays
		// stores the events (max 5)  
	    mEvents = new String[5];
	    // stores the event information
	    mEventInfo = new String[5];
	    
	    mListView = (ListView) findViewById(R.id.listView1);
	    mAddEvent = (Button) findViewById(R.id.button1);
	    
	    mListView.setOnItemClickListener(new OnItemClickListener() {

	    	// alert dialog pops up for every event displaying the event information
			public void onItemClick(AdapterView<?> parent, View v, int pos, long arg3) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrganizationsEvents.this);
		        final TextView info = new TextView(OrganizationsEvents.this);
		        
				alertDialogBuilder.setTitle(mEvents[pos]);
		 
				alertDialogBuilder
						.setMessage(mEventInfo[pos])
						.setView(info)
						.setCancelable(true)
						.setNegativeButton("Back",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								dialog.cancel();
							}
						});
		 
				AlertDialog alertDialog = alertDialogBuilder.create();
		 
				alertDialog.show();
				
			}
						
		});
	    	    
	    mAddEvent.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				//dialog pop up for creating an event
				LayoutInflater factory = LayoutInflater.from(OrganizationsEvents.this); 
		        final View eventView = factory.inflate(R.layout.event_entry, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrganizationsEvents.this);
				final EditText eventName = (EditText) eventView.findViewById(R.id.editText1); 
				final EditText eventInfo = (EditText) eventView.findViewById(R.id.editText2);
		 
				alertDialogBuilder.setTitle("Event");
		 
				alertDialogBuilder
						.setMessage("Enter a name and other info for your event!")
						.setView(eventView)
						.setCancelable(true)
						.setPositiveButton("Save",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								UpdateRowData updateRowData = new UpdateRowData(USER_TABLE_NAME);
								int i;
								switch(nullNum) {
								case 0 : i = 6;break;
								case 1 : i = 5;break;
								case 2 : i = 4;break;
								case 3 : i = 3;break;
								case 4 : i = 2;break;
								case 5 : i = 1;break;
								default: i = -1;
								}
								
								updateRowData.setValue("event"+i, eventName.getText().toString());
								updateRowData.setValue("eventinfo"+i, eventInfo.getText().toString());
								updateRowData.whereEqualsTo("login", un);

								// db call to add the event
								MobDB.getInstance().execute(APP_KEY, updateRowData, null, false, new MobDBResponseListener() {

									public void mobDBSuccessResponse() {
										Toast toast = Toast.makeText(getApplicationContext(), "Event added!", Toast.LENGTH_SHORT);
										toast.show();

										// function call to refresh the list view
    									repopulateList();

									}

									public void mobDBResponse(Vector<HashMap<String, Object[]>> result) {}

									public void mobDBResponse(String jsonStr) {}

									public void mobDBFileResponse(String fileName, byte[] fileData) {}

									public void mobDBErrorResponse(Integer errValue, String errMsg) {}
								});
						    }
					     })
						.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								dialog.cancel();
							}
						});
		 
				AlertDialog alertDialog = alertDialogBuilder.create();
		 
				alertDialog.show();
				
			}
	    	
	    });

		GetRowData getRowData = new GetRowData(USER_TABLE_NAME);
		getRowData.whereEqualsTo("login", un);

		// db call to retrieve the events and event information
		MobDB.getInstance().execute(APP_KEY, getRowData, null, false, new MobDBResponseListener() {

			public void mobDBSuccessResponse() {}           
			public void mobDBResponse(Vector<HashMap<String, Object[]>> result) {}           

			public void mobDBResponse(String jsonStr) {
				try {
					JSONObject response = new JSONObject(jsonStr);
					int status = response.getInt("status");
					JSONArray array = response.getJSONArray("row");
					if(status == 101 && response.getJSONArray("row").length() != 0) {

						for(int i = 0; i < array.length(); i++){
							// row -> column -> password
							if(array.getJSONObject(i).getString("password").equals(pw)){
								//store all events in the arrays
								mEvents[0] = array.getJSONObject(i).getString("event1");
								mEvents[1] = array.getJSONObject(i).getString("event2");
								mEvents[2] = array.getJSONObject(i).getString("event3");
								mEvents[3] = array.getJSONObject(i).getString("event4");
								mEvents[4] = array.getJSONObject(i).getString("event5");

								mEventInfo[0] = array.getJSONObject(i).getString("eventinfo1");
								mEventInfo[1] = array.getJSONObject(i).getString("eventinfo2");
								mEventInfo[2] = array.getJSONObject(i).getString("eventinfo3");
								mEventInfo[3] = array.getJSONObject(i).getString("eventinfo4");
								mEventInfo[4] = array.getJSONObject(i).getString("eventinfo5");
								
								//figure out how many events there are
								//set the boolean 'full' to false when
								//there's empty event slots and increase
								//the 'nullNum' count
								for(i = 0; i < 5; i++) {
									if(mEvents[i].equals("\"")) {
										full = false;
										nullNum++;
									}
								}
								//else, set the boolean to true and disable
								//the add button so that you cannot add more
								if(full == true)
									mAddEvent.setEnabled(false);
																
							}
						}
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				//set up a new list for the valid events to set adapter
				if(nullNum != 5) {
					int listNum = 0;
					list = new String[5 - nullNum];
					for(int i = 0; i < 5; i++) {
						if(!mEvents[i].equals("\"")) {
						   	list[listNum] = mEvents[i];
						   	listNum++;
						}
					}
					
					//set up the adapter
				    mListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list));
				}
			}

			public void mobDBFileResponse(String fileName, byte[] fileData) {}           
			public void mobDBErrorResponse(Integer errValue, String errMsg) {}
		});
	}
	
	protected void repopulateList() {
		//recalling the db to obtain the events after a change
		//refreshes the list to show the added/removed events
		//code similar to above, possible combine into 1 function 
		
	    nullNum = 0;
		un = getIntent().getExtras().getString("un");
		pw = getIntent().getExtras().getString("pw");

		GetRowData getRowData = new GetRowData(USER_TABLE_NAME);
		getRowData.whereEqualsTo("login", un);

		MobDB.getInstance().execute(APP_KEY, getRowData, null, false, new MobDBResponseListener() {

			public void mobDBSuccessResponse() {}           
			public void mobDBResponse(Vector<HashMap<String, Object[]>> result) {}           

			public void mobDBResponse(String jsonStr) {
				try {
					JSONObject response = new JSONObject(jsonStr);
					int status = response.getInt("status");
					JSONArray array = response.getJSONArray("row");
					if(status == 101 && response.getJSONArray("row").length() != 0) {

						for(int i = 0; i < array.length(); i++){
							// row -> column -> password
							if(array.getJSONObject(i).getString("password").equals(pw)){
								mEvents[0] = array.getJSONObject(i).getString("event1");
								mEvents[1] = array.getJSONObject(i).getString("event2");
								mEvents[2] = array.getJSONObject(i).getString("event3");
								mEvents[3] = array.getJSONObject(i).getString("event4");
								mEvents[4] = array.getJSONObject(i).getString("event5");

								mEventInfo[0] = array.getJSONObject(i).getString("eventinfo1");
								mEventInfo[1] = array.getJSONObject(i).getString("eventinfo2");
								mEventInfo[2] = array.getJSONObject(i).getString("eventinfo3");
								mEventInfo[3] = array.getJSONObject(i).getString("eventinfo4");
								mEventInfo[4] = array.getJSONObject(i).getString("eventinfo5");
								
								full = true;
								for(i = 0; i < 5; i++) {
									if(mEvents[i].equals("\"")) {
										full = false;
										nullNum++;
									}	
								}
								if(full == true)
									mAddEvent.setEnabled(false);
																
							}
						}
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				if(nullNum != 5) {
					int listNum = 0;
					list = new String[5 - nullNum];
					for(int i = 0; i < 5; i++) {
						if(!mEvents[i].equals("\"")) {
						   	list[listNum] = mEvents[i];
						   	listNum++;
						}
					}


					mListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list));
				    
				}
			}

			public void mobDBFileResponse(String fileName, byte[] fileData) {}           
			public void mobDBErrorResponse(Integer errValue, String errMsg) {}
		});
	
	}
}
