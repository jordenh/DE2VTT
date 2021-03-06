package org.ubc.de2vtt.token;

import org.ubc.de2vtt.MainActivity;
import org.ubc.de2vtt.R;
import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Message;
import org.ubc.de2vtt.comm.Messenger;
import org.ubc.de2vtt.comm.sendables.SendableRemoveToken;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TokenActivity extends Activity {
	private Token mToken;
	private TokenManager tokMan = TokenManager.getSharedInstance();
	private Messenger mMessenger = Messenger.GetSharedInstance();
	
	private ImageView mTokenImage;
	private TextView mTokenID;
	private EditText mTokenName;
	private TextView mTokenXY;
	
	boolean editing = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_token);
		
		Intent current = getIntent();
        Bundle b = current.getExtras();
        if (b != null) {
        	int tokenId = b.getInt("token_id");
        	mToken = tokMan.getLocal(tokenId);
        	if (mToken == null) {
        		mToken = tokMan.getRemote(tokenId);
        	}
        	
        	mTokenImage = (ImageView) findViewById(R.id.tokenImage);
        	mTokenImage.setImageBitmap(mToken.getBitmap());
        	
        	mTokenID = (TextView)findViewById(R.id.tokenID);
        	mTokenID.setText("" + mToken.getId());
        	
        	mTokenName = (EditText)findViewById(R.id.tokenName);
        	mTokenName.setText(mToken.getName());
        	mTokenName.setEnabled(false);
        	
        	mTokenXY = (TextView)findViewById(R.id.tokenXY);
        	mTokenXY.setText("(" + mToken.getX() + ", " + mToken.getY() + ")");
        } 
		
		setupOnClickListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.token, menu);
		return true;
	}
	
	private void setupOnClickListeners() {
		Button editSaveBtn = (Button)findViewById(R.id.btnEditSave);
		Button cancelBtn = (Button)findViewById(R.id.btnCancel);
		Button tableTopBtn = (Button)findViewById(R.id.btnViewTableTop);
		Button deleteBtn = (Button)findViewById(R.id.btnDelete);
		
		// Listener to listen for short clicks on the buttons within the grid
		// this should take the user to the tabletop view of their token
		OnClickListener editSaveBtnListener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Button editSaveButton = (Button)findViewById(R.id.btnEditSave);
				
				if (editing){
					mTokenName.setEnabled(false);
					mToken.setName(mTokenName.getText().toString());
					editSaveButton.setText(R.string.button_edit);
					editing = false;
				} else {
					mTokenName.setEnabled(true);
					editSaveButton.setText(R.string.button_save);
					editing = true;
				}
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
			
		OnClickListener deleteTokenListener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//delete token from DE2 and android
				Message msg = new Message(Command.REMOVE_TOKEN, new SendableRemoveToken(mToken.getId()));
				mMessenger.send(msg);
				tokMan.remove(mToken);
				
				Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
				myIntent.putExtra("fragment_sel", 1);
				startActivity(myIntent);
			}};
		
		editSaveBtn.setOnClickListener(editSaveBtnListener);
		cancelBtn.setOnClickListener(cancelBtnListener);
		tableTopBtn.setOnClickListener(tableTopBtnListener);
		deleteBtn.setOnClickListener(deleteTokenListener);
	}

}
