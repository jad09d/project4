package team2.project3;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/* tab activity for the profile/events/follow tabs the users would view */
@SuppressWarnings("deprecation")
public class OrgTab extends Activity {

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
	    
	    // setting up profile tab
	    mTabIntent = new Intent(OrgTab.this, OrgProfile.class);
	    mTabIntent.putExtras(getIntent().getExtras());
	    mSpec = mTabHost.newTabSpec("Profile");
	    mSpec.setIndicator("Profile");
	    mSpec.setContent(mTabIntent);
	    mTabHost.addTab(mSpec);
	    
	    // setting up events tab
	    mTabIntent = new Intent(OrgTab.this, OrgEvents.class);
	    mTabIntent.putExtras(getIntent().getExtras());
	    mSpec = mTabHost.newTabSpec("Events");
	    mSpec.setIndicator("Events");
	    mSpec.setContent(mTabIntent);
	    mTabHost.addTab(mSpec);
	    
	    // setting up follow tab
	    mTabIntent = new Intent(OrgTab.this, OrgFollow.class);
	    mTabIntent.putExtras(getIntent().getExtras());
	    mSpec = mTabHost.newTabSpec("Follow");
	    mSpec.setIndicator("Follow");
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
