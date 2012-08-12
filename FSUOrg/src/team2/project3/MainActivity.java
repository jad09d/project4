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
import android.widget.Toast;

import com.mobdb.android.GetRowData;
import com.mobdb.android.InsertRowData;
import com.mobdb.android.MobDB;
import com.mobdb.android.MobDBResponseListener;

/* The main menu activity viewed by both
 * organizations and users. organizations
 * can login or register while users can 
 * browse the various organizations
 */

public class MainActivity extends Activity {

	// MobDB constants
	final String APP_KEY = "dhTii1-5T3-WoBi0DpadReeum39O1iPixXx-cutCsWBLUcBJWoW";
	final String USER_TABLE_NAME = "orgpop";
	final String USER_LOGIN = "login";
	final String USER_PASSWORD = "password";
	// End MobDB constants

	// Main activity's UI views
	EditText mLoginName, mPassword;
	Button mLogin, mRegister, mBrowse;
	
	// dialog's image view
	ImageView mOrgPic;
	
	Bitmap selectedImage;	
	String filepath;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);

		mLoginName = (EditText) findViewById(R.id.organization);
		mPassword = (EditText) findViewById(R.id.password_et);
		mLogin = (Button) findViewById(R.id.login);
		mRegister = (Button) findViewById(R.id.register);
		mBrowse = (Button) findViewById(R.id.browse);
				
		// Login database call
		mLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				final String un = mLoginName.getText().toString();
				final String pw = mPassword.getText().toString();

				GetRowData getRowData = new GetRowData(USER_TABLE_NAME);
				getRowData.whereEqualsTo(USER_LOGIN, un);

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
									if(array.getJSONObject(i).getString(USER_PASSWORD).equals(pw)){
										clear();
										Intent intent = new Intent(getApplicationContext(), OrganizationsTab.class);
										Bundle extras = new Bundle();
										extras.putString("un", un);
										extras.putString("pw", pw);
										intent.putExtras(extras);
										startActivity(intent);
									}
								}	
							}
							else{
								mLoginName.setError("Username does not exist");
								mPassword.setError("Password does not exist");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					public void mobDBFileResponse(String fileName, byte[] fileData) {}           
					public void mobDBErrorResponse(Integer errValue, String errMsg) {}
				});
			}
		});

		// Register database call
		mRegister.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//create entry in mobDB with this information
				final String un2 = mLoginName.getText().toString();
				final String pw2 = mPassword.getText().toString();

				GetRowData getRowData2 = new GetRowData(USER_TABLE_NAME);
				getRowData2.whereEqualsTo(USER_LOGIN, un2);

				
				MobDB.getInstance().execute(APP_KEY, getRowData2, null, false, new MobDBResponseListener() {

					public void mobDBSuccessResponse() {
						Toast toast = Toast.makeText(getApplicationContext(), "Get Success!", Toast.LENGTH_SHORT);
						toast.show();
					}           
					public void mobDBResponse(Vector<HashMap<String, Object[]>> result) {}           

					public void mobDBResponse(String jsonStr) {
						try {
							JSONObject response = new JSONObject(jsonStr);
							int status = response.getInt("status");

							if(status == 101 && response.getJSONArray("row").length() == 0) {
								
								LayoutInflater factory = LayoutInflater.from(MainActivity.this); 
						        final View registerView = factory.inflate(R.layout.register, null);

						        // dialog to fill out profile info when registering
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
								
								final EditText orgName = (EditText) registerView.findViewById(R.id.editText1);
								final EditText orgDescript = (EditText) registerView.findViewById(R.id.editText2);
								mOrgPic = (ImageView) registerView.findViewById(R.id.imageView1);
								final Button uploadPic = (Button) registerView.findViewById(R.id.button1); 
								final Button resetPic =  (Button) registerView.findViewById(R.id.button2); 
								
								uploadPic.setOnClickListener(new OnClickListener() {

									public void onClick(View v) {
										Intent picIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
									    startActivityForResult(picIntent, 0); 
									    
									}									
								});
								
								resetPic.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {	
										mOrgPic.setImageResource(R.drawable.no_image);		
									}
									
								});
						 
								alertDialogBuilder.setTitle("Register");
						 
								alertDialogBuilder
										.setMessage("Enter a name and description for your organization!")
										.setView(registerView)
										.setCancelable(true)
										.setPositiveButton("Save",new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,int id) {
												InsertRowData insertRowData = new InsertRowData(USER_TABLE_NAME);
												insertRowData.setValue(USER_LOGIN, un2);
												insertRowData.setValue(USER_PASSWORD, pw2);
												insertRowData.setValue("name", orgName.getText().toString());
												insertRowData.setValue("descript", orgDescript.getText().toString());
												insertRowData.setValue("numbers", "\"");
												insertRowData.setValue("event1", "\"");
												insertRowData.setValue("eventinfo1", "\"");
												insertRowData.setValue("event2", "\"");
												insertRowData.setValue("eventinfo2", "\"");
												insertRowData.setValue("event3", "\"");
												insertRowData.setValue("eventinfo3", "\"");
												insertRowData.setValue("event4", "\"");
												insertRowData.setValue("eventinfo4", "\"");
												insertRowData.setValue("event5", "\"");
												insertRowData.setValue("eventinfo5", "\"");

												// images not cooperating
												if(selectedImage != null) {
												  ByteArrayOutputStream baos = new ByteArrayOutputStream();
												  selectedImage.compress(Bitmap.CompressFormat.PNG, 100, baos);									
												  insertRowData.setValue("pik1", filepath.substring(filepath.lastIndexOf('/')+1), baos.toByteArray());
												  insertRowData.setValue("haspic", 1);
												}
												else {
												  insertRowData.setValue("haspic", 0);
												}
												
												// db call to store the information and continue as if user logged in
												MobDB.getInstance().execute(APP_KEY, insertRowData, null, false, new MobDBResponseListener() {

													public void mobDBSuccessResponse() {
														Toast toast = Toast.makeText(getApplicationContext(), "Insert Success!", Toast.LENGTH_SHORT);
														toast.show();
																												
														Intent intent = new Intent(getApplicationContext(), OrganizationsTab.class);
														Bundle extras = new Bundle();
														extras.putString("un", un2);
														extras.putString("pw", pw2);
														intent.putExtras(extras);
														startActivity(intent);
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
							else {
								// displays error when password/username is taken
								mPassword.setError("Password already exists");
								mLoginName.setError("Username already exists");
							}
						}
						catch (JSONException e) {
							e.printStackTrace();
						}
					}

					public void mobDBFileResponse(String fileName, byte[] fileData) {}           
					public void mobDBErrorResponse(Integer errValue, String errMsg) {}
				});
			}
		});
		
		mBrowse.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// start list activity of organizations
				Intent intent = new Intent(getApplicationContext(), OrgList.class);
				startActivity(intent);
			}
		});
		
	}

	public void clear(){

		//clears the current text
		EditText mLoginName, mPassword;
		mLoginName = (EditText) findViewById(R.id.organization);
		mPassword = (EditText) findViewById(R.id.password_et);

		mLoginName.setText("");
		mPassword.setText("");
	}
	
	// called when image is selected from gallery
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

	    switch(requestCode) { 
	    case 0:
	        if(resultCode == RESULT_OK){  
	            Uri selected = imageReturnedIntent.getData();
	            String[] filePathCol = { MediaStore.Images.Media.DATA };

	            Cursor cursor = getContentResolver().query(selected, filePathCol, null, null, null);
	            cursor.moveToFirst();

	            int columnIndex = cursor.getColumnIndex(filePathCol[0]);
	            filepath = cursor.getString(columnIndex);
	            cursor.close();

	            // sets image 
	            selectedImage = BitmapFactory.decodeFile(filepath);
	            mOrgPic.setImageBitmap(selectedImage);

	        }
	    }
	}
}