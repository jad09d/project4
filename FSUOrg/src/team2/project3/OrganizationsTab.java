package team2.project3;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/* tab activity for the profile/events/notify tabs the organizations would view */
@SuppressWarnings("deprecation")
public class OrganizationsTab extends Activity {
	
	final String APP_KEY = "dhTii1-5T3-WoBi0DpadReeum39O1iPixXx-cutCsWBLUcBJWoW";

	TabHost mTabHost;
	TabSpec mSpec;
	Intent mTabIntent;
    LocalActivityManager mLAM;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tab);
	       
	    mTabHost = (TabHost) findViewById(android.R.id.tabhost);
	    
	    // setting up tab host
	    mLAM = new LocalActivityManager(this, false);
	    mLAM.dispatchCreate(savedInstanceState);
	    mTabHost.setup(mLAM);
	    
	    // set up profile tab
	    mTabIntent = new Intent(OrganizationsTab.this, OrganizationsProfile.class);
	    mTabIntent.putExtras(getIntent().getExtras());
	    mSpec = mTabHost.newTabSpec("Profile");
	    mSpec.setIndicator("Profile");
	    mSpec.setContent(mTabIntent);
	    mTabHost.addTab(mSpec);
	    
	    // set up events tab
	    mTabIntent = new Intent(OrganizationsTab.this, OrganizationsEvents.class);
	    mTabIntent.putExtras(getIntent().getExtras());
	    mSpec = mTabHost.newTabSpec("Events");
	    mSpec.setIndicator("Events");
	    mSpec.setContent(mTabIntent);
	    mTabHost.addTab(mSpec);
	    
	    // set up notify tab
	    mTabIntent = new Intent(OrganizationsTab.this, OrganizationsNotify.class);
	    mTabIntent.putExtras(getIntent().getExtras());
	    mSpec = mTabHost.newTabSpec("Notify");
	    mSpec.setIndicator("Notify");
	    mSpec.setContent(mTabIntent);
	    mTabHost.addTab(mSpec);
	    
	}
	
	@Override
	public void onResume() {
		super.onResume();
        mLAM.dispatchResume(); 
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mLAM.dispatchPause(isFinishing());
	}
}
