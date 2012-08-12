package team2.project3;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobdb.android.GetFile;
import com.mobdb.android.GetRowData;
import com.mobdb.android.MobDB;
import com.mobdb.android.MobDBResponseListener;
import com.mobdb.android.UpdateRowData;

/* The organization's view of the profile tab
 *  This view is identical to the user's view of
 *  the profile with the exception of the edit button
 */
public class OrganizationsProfile extends Activity{

	// MobDB constants
	final String APP_KEY = "dhTii1-5T3-WoBi0DpadReeum39O1iPixXx-cutCsWBLUcBJWoW";
	final String USER_TABLE_NAME = "orgpop";
	final String USER_LOGIN = "login";
	final String USER_PASSWORD = "password";
	// End MobDB constants
	
	// Profile UI variables
	TextView mOrgName;
	TextView mOrgDescription;
    ImageView mOrgPic;
    Button mEditProf;
    
    // Edit dialog variables
    ImageView mPicPrev;
    String filepath;
    Bitmap selectedImage;
     
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
	    selectedImage = null;
	    
	    un = getIntent().getExtras().getString("un");
	    pw = getIntent().getExtras().getString("pw");
	    
	    GetRowData getRowData = new GetRowData(USER_TABLE_NAME);
	    getRowData.whereEqualsTo(USER_LOGIN, un);

	        // db call to obtain organization's name, description and image
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
    	    						
    	    						// check if the organization has a pic, if so obtain the file id
    	    						if(array.getJSONObject(i).getInt("haspic") == 1) {
    	    						String fileId = array.getJSONObject(i).getString("pik1");
    	    						GetFile getFile = new GetFile(fileId);
    	    						
    	    						// db call to obtain the image file
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
	    	
	    // to edit a profile
	    mEditProf.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				GetRowData getRowData2 = new GetRowData(USER_TABLE_NAME);
				getRowData2.whereEqualsTo(USER_LOGIN, un);

				MobDB.getInstance().execute(APP_KEY, getRowData2, null, false, new MobDBResponseListener() {

					public void mobDBSuccessResponse() {
						Toast toast = Toast.makeText(getApplicationContext(), "Get Success!", Toast.LENGTH_SHORT);
						toast.show();

						LayoutInflater factory = LayoutInflater.from(OrganizationsProfile.this); 
						final View editView = factory.inflate(R.layout.register, null);
                        
						// set up the dialog for editing the profile
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrganizationsProfile.this);
								
						final EditText orgName = (EditText) editView.findViewById(R.id.editText1);
						final EditText orgDescript = (EditText) editView.findViewById(R.id.editText2);
						mPicPrev = (ImageView) editView.findViewById(R.id.imageView1);
						final Button uploadPic = (Button) editView.findViewById(R.id.button1); 
						final Button resetPic =  (Button) editView.findViewById(R.id.button2); 
							
						uploadPic.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Intent picIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								startActivityForResult(picIntent, 0); 
								    
							}									
						});
								
						resetPic.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {	
								mPicPrev.setImageResource(R.drawable.no_image);
								selectedImage = null;
							}
									
						});
						 
						alertDialogBuilder.setTitle("Edit Profile");
						 
						alertDialogBuilder
							.setMessage("Edit your organization's profile!")
							.setView(editView)
							.setCancelable(true)
							.setPositiveButton("Save",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										UpdateRowData updateRowData = new UpdateRowData(USER_TABLE_NAME);
										updateRowData.whereEqualsTo(USER_LOGIN, un);
										updateRowData.setValue("name", orgName.getText().toString());
										updateRowData.setValue("descript", orgDescript.getText().toString());
									    if(selectedImage != null) {
										  ByteArrayOutputStream baos = new ByteArrayOutputStream();
										  selectedImage.compress(Bitmap.CompressFormat.PNG, 100, baos);									
										  updateRowData.setValue("pik1", filepath.substring(filepath.lastIndexOf('/')+1), baos.toByteArray());
										  updateRowData.setValue("haspic", 1);
										}
										else {
										  mOrgPic.setImageResource(R.drawable.no_image);
										  updateRowData.setValue("haspic", 0);
										}
										
										// db call to update the edited info. images still not cooperating
										MobDB.getInstance().execute(APP_KEY, updateRowData, null, false, new MobDBResponseListener() {
											public void mobDBSuccessResponse() {
												Toast toast = Toast.makeText(getApplicationContext(), "Insert Success!", Toast.LENGTH_SHORT);
												toast.show();
												
												mOrgName.setText(orgName.getText().toString());
												mOrgDescription.setText(orgDescript.getText().toString());
											    mOrgPic.setImageBitmap(selectedImage);
														
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
				
					public void mobDBResponse(Vector<HashMap<String, Object[]>> result) {}           

					public void mobDBResponse(String jsonStr) { }

					public void mobDBFileResponse(String fileName, byte[] fileData) {}           
					public void mobDBErrorResponse(Integer errValue, String errMsg) {}
				});
			}
		});

	}
	
	// called after image is selected from gallery
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

	    switch(requestCode) { 
	    case 0:
	        if(resultCode == RESULT_OK){  
	            Uri selected = imageReturnedIntent.getData();
	            String[] filePathColumn = {MediaStore.Images.Media.DATA};

	            Cursor cursor = getContentResolver().query(selected, filePathColumn, null, null, null);
	            cursor.moveToFirst();

	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            filepath = cursor.getString(columnIndex);
	            cursor.close();

	            selectedImage = BitmapFactory.decodeFile(filepath);
	            mPicPrev.setImageBitmap(selectedImage);

	        }
	    }
	}
}
