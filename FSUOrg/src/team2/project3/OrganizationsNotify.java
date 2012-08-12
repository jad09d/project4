package team2.project3;

import java.util.HashMap;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobdb.android.GetRowData;
import com.mobdb.android.MobDB;
import com.mobdb.android.MobDBResponseListener;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/* The organization's view of the third tab called notify. 
 *  Here, an organization can mass text their members notifications
 *  The user's view of the third tab is the follow tab to follow an
 *  organization to receive these messages.
 */ 
public class OrganizationsNotify extends Activity{
	
	// MobDB constants
	String APP_KEY = "dhTii1-5T3-WoBi0DpadReeum39O1iPixXx-cutCsWBLUcBJWoW";
	String USER_TABLE_NAME = "orgpop";
	
	Button mSendMessage;
	String mMessage;
	EditText mWriteMessage;
	String[] mPhones;
	
	String delims = "[,]";
	String numbers;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.orgnotify);
	    
	    mSendMessage = (Button) findViewById(R.id.button1);
	    mWriteMessage = (EditText) findViewById(R.id.editText1);
	      
	    final String un = getIntent().getExtras().getString("un");
	    final String pw = getIntent().getExtras().getString("pw");
	    
	    GetRowData getRowData = new GetRowData(USER_TABLE_NAME);
	    getRowData.whereEqualsTo("login", un);

	        // db call to retrieve the phone numbers
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
    	    					 if(array.getJSONObject(i).getString("password").equals(pw)) {
    	    						numbers = array.getJSONObject(i).getString("numbers");
    	    					 }
	    					  }
	    					}
	    			} catch (JSONException e) {
	    				e.printStackTrace();
	    			}	    			
					
	    			// split the numbers string which is comma separated into the phone numbers array
	    			if(!numbers.equals("\"")) {
	    				mPhones = numbers.split(delims);
	    			}
	    		    else {
	    		    	//if there aren't any phone numbers, a message displays explaining you cannot send
	    		    	//messages if no one is in your group
	    		    	TextView notice = (TextView) findViewById(R.id.textView1);
	    		    	notice.setText("There is no one in your group to notify right now. Go find followers!");
	    		    	mWriteMessage.setVisibility(View.GONE);
	    		    	mSendMessage.setVisibility(View.GONE);
	    		    }

	    		}

	    		public void mobDBFileResponse(String fileName, byte[] fileData) {}           
	    		public void mobDBErrorResponse(Integer errValue, String errMsg) {}
	    		
	      });
    	
	    	    
	    mSendMessage.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {				
				
				mMessage = mWriteMessage.getText().toString(); 
				for(int i=0; i < mPhones.length; i++) {
					if(mPhones[i] != null) {
				
				  try {
					   //sends message as an SMS message
						SmsManager smsManager = SmsManager.getDefault();
						smsManager.sendTextMessage(mPhones[i], null, mMessage, null, null);
						Toast.makeText(getApplicationContext(), "Message Sent!", Toast.LENGTH_LONG).show();
			      } catch (Exception e) {
						Toast.makeText(getApplicationContext(), "Message was not sent... Try again", Toast.LENGTH_LONG).show();
						e.printStackTrace();
				  }
					}
				}
			}
	    	
	    });
	
	}

}
