package org.ubc.de2vtt.movetoken;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.R.layout;
import org.ubc.de2vtt.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TokenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_token);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.token, menu);
		return true;
	}

}
