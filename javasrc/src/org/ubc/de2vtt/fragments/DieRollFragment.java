package org.ubc.de2vtt.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.rolldie.ShakeListener;
import org.ubc.de2vtt.rolldie.ShakeListener.OnShakeListener;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class DieRollFragment extends WINGFragment {
	private static final String TAG = DieRollFragment.class.getSimpleName();	
	
	private Activity mActivity;
	protected View mParentView;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeListener mShakeListener;
	private Spinner mDieTypeSelector;
	private Button mRollButton;
	private TextView mRollValue;
	
	private int mDieValues[] = {4, 6, 8, 10, 12, 20};
	private int mDiePictureIds[] = {
		R.drawable.d4,	R.drawable.d6,
		R.drawable.d8,	R.drawable.d10,
		R.drawable.d12, R.drawable.d20
	};
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_dieroll,  container, false);
		mActivity = this.getActivity();
	
		mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        mRollButton = (Button)mParentView.findViewById(R.id.btnRoll);
        mRollButton.setVisibility(View.GONE);
        
        mShakeListener = new ShakeListener();

        mDieTypeSelector = (Spinner)mParentView.findViewById(R.id.dieSpinner);
        mRollValue = (TextView)mParentView.findViewById(R.id.dieValue);
        mRollValue.setBackgroundResource(mDiePictureIds[0]);
     	
		setupSpinner();
	    setupOnEventListeners();
	  		
		return mParentView;
	}
	
	@Override
    public void onResume() {
        super.onResume();
        
        if (!mSensorManager.registerListener(mShakeListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)) {
        	mRollButton.setVisibility(View.VISIBLE);
        }
    }
 
    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeListener);
        super.onPause();
    }
    
	private void setupOnEventListeners() {		
		mShakeListener.setOnShakeListener(new OnShakeListener() {

			@Override
			public void onShake(int count) {
				onShakeEvent();
			}
		}); 
		
		mDieTypeSelector.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Log.v(TAG, "item selected :" + arg2);
				mRollValue.setText("1");
				
				mRollValue.setBackgroundResource(mDiePictureIds[arg2]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		mRollButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onShakeEvent();
			}
        });
	}
	
	private void setupSpinner() {
		// Obtain list of currently connected phone IDs (names), as well as title for main screen. TBD - add more 
		List<String> dieTypes = new ArrayList<String>();
		
		for (int i = 0; i < mDieValues.length; i++){
			dieTypes.add("D" + mDieValues[i]);
		}
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<String> adapter = new ArrayAdapter<String>
				(mActivity, android.R.layout.simple_spinner_item, dieTypes);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mDieTypeSelector.setAdapter(adapter);
	}

	private void onShakeEvent() {
		Log.v(TAG, "SHAKE!!");
		Random randomGen = new Random();
		
		int pos = (int) mDieTypeSelector.getSelectedItemId();
		Log.v(TAG, "item selected :" + pos);
		
		int randomInt = 1 + randomGen.nextInt(mDieValues[pos]);
    	mRollValue.setText("" + randomInt);
	}

	@Override
	public boolean passReceived(Received r) {
		// Don't need anything as this fragment is not expecting any messages
		return false;
	}
}
