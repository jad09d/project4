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

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/* The user's view of the organization's profile  
 */
public class OrgProfile extends Activity {
	
	//mobDB constants
	final String APP_KEY = "dhTii1-5T3-WoBi0DpadReeum39O1iPixXx-cutCsWBLUcBJWoW";
	final String USER_TABLE_NAME = "orgpop";
	final String USER_LOGIN = "login";
	final String USER_PASSWORD = "password";
	
    TextView mOrgName;
	TextView mOrgDescription;
	ImageView mOrgPic;
	Button mEditProf;
	
	String un;
	String pw;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.profile);
	    
	    mOrgName = (TextView) findViewById(R.id.textView1);
	    mOrgDescription = (TextView) findViewById(R.id.textView2);
	    mOrgPic = (ImageView) findViewById(R.id.imageView1);
	    mEditProf = (Button) findViewById(R.id.button1);
	    // remove the edit profile button when a user is viewing it
	    mEditProf.setVisibility(View.GONE);
	    
	    un = getIntent().getExtras().getString("un");
	    pw = getIntent().getExtras().getString("pw");

	    GetRowData getRowData = new GetRowData(USER_TABLE_NAME);
	    getRowData.whereEqualsTo(USER_LOGIN, un);

	        // db call to obtain org's info including name/description/image
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
    	    					 if(array.getJSONObject(i).getString(USER_PASSWORD).equals(pw)) {
    	    						mOrgName.setText(array.getJSONObject(i).getString("name"));
    	    						mOrgDescription.setText(array.getJSONObject(i).getString("descript"));
    	    						
    	    						if(array.getJSONObject(i).getInt("haspic") == 1) {
    	    						String fileId = array.getJSONObject(i).getString("pik1");
    	    						GetFile getFile = new GetFile(fileId);
    	    						
    	    						// db call to retrieve image
    	    						MobDB.getInstance().execute(APP_KEY, getFile, null, false, new MobDBResponseListener() {

										public void mobDBSuccessResponse() { }

										public void mobDBErrorResponse(Integer errValue, String errMsg) { }

										public void mobDBResponse(String jsonObj) { }

										public void mobDBFileResponse(String fileName, byte[] fileData) { 
											Bitmap bitmap = BitmapFactory.decodeByteArray(fileData, 0, fileData.length);
											mOrgPic.setImageBitmap(bitmap);
										}

										public void mobDBResponse(Vector<HashMap<String, Object[]>> result) { }
    	    						 
    	    						});
    	    						}
    	    						
    	    				     }
	    					  }
	    		        	}	 
	    			} catch (JSONException e) {
	    				e.printStackTrace();
	    			}
	    		}

	    		public void mobDBFileResponse(String fileName, byte[] fileData) {}           
	    		public void mobDBErrorResponse(Integer errValue, String errMsg) {}
	    		
	      });
	}

}
