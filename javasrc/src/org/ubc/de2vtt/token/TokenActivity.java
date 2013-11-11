package org.ubc.de2vtt.token;

import org.ubc.de2vtt.MainActivity;
import org.ubc.de2vtt.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TokenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_token);
		
		setupOnClickListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.token, menu);
		return true;
	}
	
	private void setupOnClickListeners() {
		
		Button saveBtn = (Button)findViewById(R.id.btnSave);
		Button cancelBtn = (Button)findViewById(R.id.btnCancel);
		Button tableTopBtn = (Button)findViewById(R.id.btnViewTableTop);
		
		// Listener to listen for short clicks on the buttons within the grid
		// this should take the user to the tabletop view of their token
		OnClickListener saveBtnListener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
				myIntent.putExtra("fragment_sel", 1);
				startActivity(myIntent);
			}};
			
		// Listener to listen for long clicks on the buttons within the grid
		// this should take the user to the token settings activity
		OnClickListener cancelBtnListener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
				myIntent.putExtra("fragment_sel", 1);
				startActivity(myIntent);
			}};
			
		OnClickListener tableTopBtnListener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
				myIntent.putExtra("fragment_sel", 0);
				startActivity(myIntent);
			}};
			
		saveBtn.setOnClickListener(saveBtnListener);
		cancelBtn.setOnClickListener(cancelBtnListener);
		tableTopBtn.setOnClickListener(tableTopBtnListener);
	}

}
