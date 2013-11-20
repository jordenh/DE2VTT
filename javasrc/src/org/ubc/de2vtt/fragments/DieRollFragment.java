package org.ubc.de2vtt.fragments;

import java.util.Random;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.rolldie.ShakeListener;
import org.ubc.de2vtt.rolldie.ShakeListener.OnShakeListener;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DieRollFragment extends Fragment {
	private static final String TAG = DieRollFragment.class.getSimpleName();	
	
	private Activity mActivity;
	protected View mParentView;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeListener mShakeListener;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_dieroll,  container, false);
		mActivity = this.getActivity();
	
		mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        if (mAccelerometer == null) {
        	Log.v(TAG, "Can't find accelerometer");
        }
        
        mShakeListener = new ShakeListener();
		
	    setupOnShakeListeners();
	  		
		return mParentView;
	}
	
	@Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
 
    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeListener);
        super.onPause();
    }
    
	private void setupOnShakeListeners() {		
		mShakeListener.setOnShakeListener(new OnShakeListener() {

			@Override
			public void onShake(int count) {
				Log.v(TAG, "SHAKE!!");
				Random randomGen = new Random();
				
				int randomInt = randomGen.nextInt(10);
				
				TextView dieValue = (TextView)mActivity.findViewById(R.id.dieValue);
	        	dieValue.setText("" + randomInt);
			}
		}); 
	}
}
