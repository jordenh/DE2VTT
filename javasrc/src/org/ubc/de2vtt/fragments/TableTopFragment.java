package org.ubc.de2vtt.fragments;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.tabletop.TableTopOnTouchListener;
import org.ubc.de2vtt.token.Token;
import org.ubc.de2vtt.token.TokenManager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class TableTopFragment extends WINGFragment {
	protected View mParentView;
	private Activity mActivity;
	private RelativeLayout mLayout;
	private ImageView mMapView;
	private TokenManager tokMan = TokenManager.getSharedInstance();
	private static Bitmap mBitmap;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_tabletop,  container, false);
		mActivity = this.getActivity();

		mLayout = (RelativeLayout) mParentView.findViewById(R.id.tabletop);
		
		if (mBitmap != null) {
			mMapView = (ImageView) mParentView.findViewById(R.id.MapView);
			mMapView.setImageBitmap(mBitmap);
			mMapView.setScaleType(ScaleType.FIT_XY);
		}

		final ViewTreeObserver vto = mParentView.findViewById(R.id.tabletop).getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				final int fragmentWidth = mParentView.findViewById(R.id.tabletop).getWidth();
				if (fragmentWidth != 0){
					for (Token tok : tokMan.getList()) {
						final ImageView tokenImageView = new ImageView(mActivity);
						
						Matrix matrix = new Matrix();
						matrix.postRotate(90);
						
						Bitmap bmp = tok.getBitmap();
						Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, bmp.getHeight(), bmp.getWidth(), true);
						Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
						tokenImageView.setImageBitmap(rotatedBitmap);
						
						tok.setImageView(tokenImageView);

						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(40, 40);
						params.leftMargin = fragmentWidth - tok.getY() - params.width;
						params.topMargin = tok.getX();
						tokenImageView.setLayoutParams(params);
						mLayout.addView(tokenImageView);
						
						tokenImageView.setOnTouchListener(new TableTopOnTouchListener());
					}
					
					getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}

			}

		});
		
		//setAcceptedCommands(new Command[0]);

		return mParentView;
	}
			
	public static void setMap(Bitmap map) {
		MapSetter m = new MapSetter();
		m.execute(map);
	}
	
	private static class MapSetter extends AsyncTask<Bitmap, Void, Void> {
		
		@Override
		protected Void doInBackground(Bitmap... params) {
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(params[0], 260, 340, true);
			
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			
			Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, 
					260, 340, matrix, true);
			//scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, 340, 260, true);
			
			mBitmap = rotatedBitmap;
			return null;
		}
		
	}

	@Override
	public boolean passReceived(Received r) {
		// TODO Move token
		
		// update map
		mMapView = (ImageView) mParentView.findViewById(R.id.MapView);
		mMapView.setImageBitmap(mBitmap);
		mMapView.setScaleType(ScaleType.FIT_XY);
		return true;
	}
}