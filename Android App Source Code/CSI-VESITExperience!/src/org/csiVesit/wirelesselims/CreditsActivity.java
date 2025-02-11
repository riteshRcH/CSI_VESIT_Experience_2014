package org.csiVesit.wirelesselims;

import org.csiVesit.csiVesitExperience.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CreditsActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.we_activity_credits);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		getWindow().setBackgroundDrawableResource(R.drawable.creditsbglytwt);
		
		if(customTitleSupported)
		{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
			TextView myTitleText = (TextView) findViewById(R.id.myTitle);
			myTitleText.setText("Wireless Elims");
			//myTitleText.setTextSize(20);
			//myTitleText.setBackgroundColor(Color.rgb(220, 208, 255));
			//myTitleText.setTextColor(Color.rgb(0,0,139));
		}
		
		Button btnExit = (Button)findViewById(R.id.btnExit);
		btnExit.setOnClickListener(new View.OnClickListener()
		{	
			@Override
			public void onClick(View btn)
			{
				// TODO Auto-generated method stub
				//Toast.makeText(getBaseContext(), "Hope you enjoyed our very 1st Wireless Elims :)\nCyaa Soon Right Here!", Toast.LENGTH_LONG).show();
				//moveTaskToBack(true);
				finish();
				//android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
		
	}

	

}
