package team2.project3;

import java.util.HashMap;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobdb.android.GetRowData;
import com.mobdb.android.MobDB;
import com.mobdb.android.MobDBResponseListener;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/* the user's view of the event tab. only displays a list
 *  of events with more information when clicked
 */
public class OrgEvents extends ListActivity {

	//mobDB constants
	String APP_KEY = "dhTii1-5T3-WoBi0DpadReeum39O1iPixXx-cutCsWBLUcBJWoW";
	String USER_TABLE_NAME = "orgpop";
	
	String un;
	String pw;
	ListView mListView;

	String[] mEvents;
	String[] mEventInfo;
	
	int nullNum;
	String[] list;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    // obtain list  view and add the on item click listener.
	    mListView = getListView();
	    
	    mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int pos, long arg3) {
				// dialog pop up giving the user more information on the event clicked
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrgEvents.this);
		        final TextView info = new TextView(OrgEvents.this);
		        
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
	    
	    un = getIntent().getExtras().getString("un");
		pw = getIntent().getExtras().getString("pw");
		
		mEvents = new String[5];
		mEventInfo = new String[5];
		nullNum = 0;
	    
		GetRowData getRowData = new GetRowData(USER_TABLE_NAME);
		getRowData.whereEqualsTo("login", un);

		// db call to obtain all events and event info. very similar to call in the organization's view of the events tab
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

								mEventInfo[0] = array.getJSONObject(i).getString("mEventInfo1");
								mEventInfo[1] = array.getJSONObject(i).getString("mEventInfo2");
								mEventInfo[2] = array.getJSONObject(i).getString("mEventInfo3");
								mEventInfo[3] = array.getJSONObject(i).getString("eventinfo4");
								mEventInfo[4] = array.getJSONObject(i).getString("eventinfo5");
								
								for(i = 0; i < 5; i++) {
									if(mEvents[i].equals("\""))
										nullNum++;
								}

																
							}
						}
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				// making new list of valid events to set the adapter
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
