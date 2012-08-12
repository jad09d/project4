package team2.project3;

import java.util.HashMap;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobdb.android.GetFile;
import com.mobdb.android.GetRowData;
import com.mobdb.android.MobDB;
import com.mobdb.android.MobDBResponseListener;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class OrgList extends ListActivity {
	
	//MobDB constants
	final String APP_KEY = "dhTii1-5T3-WoBi0DpadReeum39O1iPixXx-cutCsWBLUcBJWoW";
	final String USER_TABLE_NAME = "orgpop";
	final String USER_LOGIN = "login";
	final String USER_PASSWORD = "password";
	// End MobDB constants
	
	String[] mUsernames;
	String[] mPasswords;
	String[] mNumbers;
	String[] mNames;
	Bitmap[] mPics;	
	ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    mListView = getListView();

		GetRowData getRowData = new GetRowData(USER_TABLE_NAME);

		// db call to get usernames/passwords and names/images of all organizations
		MobDB.getInstance().execute(APP_KEY, getRowData, null, false, new MobDBResponseListener() {

			public void mobDBSuccessResponse() { }

			public void mobDBErrorResponse(Integer errValue, String errMsg) { }

			public void mobDBResponse(String jsonObj) {
			try {	
				JSONObject response = new JSONObject(jsonObj);
				int status = response.getInt("status");
				JSONArray array = response.getJSONArray("row");
				if(status == 101 && response.getJSONArray("row").length() != 0) {

					// set up the arrays for the organizations array length
					mNames = new String[array.length()];
					mPics = new Bitmap[array.length()];
					mPasswords = new String[array.length()];
					mUsernames = new String[array.length()];
					mNumbers = new String[array.length()];
					
					// for each organization, grab their info
					for(int i = 0; i < array.length(); i++) {
						
						final int pos = i;
						mUsernames[i] = array.getJSONObject(i).getString(USER_LOGIN);
						mPasswords[i] = array.getJSONObject(i).getString(USER_PASSWORD);
						mNames[i] = array.getJSONObject(i).getString("name");
						mNumbers[i] = array.getJSONObject(i).getString("numbers");
						
						// check if the org has a pic
						if(array.getJSONObject(i).getInt("haspic") == 1) {
    						String fileId = array.getJSONObject(i).getString("pik1");
    						GetFile getFile = new GetFile(fileId);
    						
    						// db call to obtain image file
    						MobDB.getInstance().execute(APP_KEY, getFile, null, false, new MobDBResponseListener() {

								public void mobDBSuccessResponse() { }

								public void mobDBErrorResponse(Integer errValue, String errMsg) { }

								public void mobDBResponse(String jsonObj) { }

								public void mobDBFileResponse(String fileName, byte[] fileData) { 
									mPics[pos] = BitmapFactory.decodeByteArray(fileData, 0, fileData.length);
								}

								public void mobDBResponse(Vector<HashMap<String, Object[]>> result) { }
    						 
    						});						
					}
				  }
				}
												
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			 setListView();
			
			}

			public void mobDBFileResponse(String fileName, byte[] fileData) { }

			public void mobDBResponse(Vector<HashMap<String, Object[]>> result) { }
			
		});
	    
	}

	// function to set up the list adapter and onclick to each organizations tab view
    private void setListView() {
	    mListView.setAdapter(new MyCustomAdapter(OrgList.this, R.layout.orglistrow, mNames));
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int pos, long arg3) {
                  Intent myIntent = new Intent(OrgList.this, OrgTab.class);
                  Bundle extras = new Bundle();
                  extras.putString("un", mUsernames[pos]);
                  extras.putString("pw", mPasswords[pos]);
                  extras.putString("numbers", mNumbers[pos]);
                  myIntent.putExtras(extras);
                  startActivity(myIntent);                  
			}
						
		});
    	
    }
	
    // custom adapter to display image and name of each organization
	public class MyCustomAdapter extends ArrayAdapter<String> {

		public MyCustomAdapter(Context context, int textViewResourceId, String[] objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater=getLayoutInflater();
			View row=inflater.inflate(R.layout.orglistrow, parent, false);
			TextView label=(TextView)row.findViewById(R.id.orgName);
			label.setText(mNames[position]);
			ImageView icon=(ImageView)row.findViewById(R.id.orgPic);
			icon.setImageBitmap(mPics[position]);
			
			return row;
		}
	}

}
