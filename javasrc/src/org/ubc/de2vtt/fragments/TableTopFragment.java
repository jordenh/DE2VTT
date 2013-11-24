package org.ubc.de2vtt.fragments;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.tabletop.TableTopOnTouchListener;
import org.ubc.de2vtt.token.Token;
import org.ubc.de2vtt.token.TokenManager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
					for (int i = 0; i < tokMan.size(); i++) {
						int id = tokMan.getKey(i);
						Token tok = tokMan.get(id);

						final ImageView tokenImageView = new ImageView(mActivity);
						tokenImageView.setImageBitmap(tok.getBitmap());
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
		Matrix matrix = new Matrix();
		matrix.postRotate(90);

		Bitmap scaledBitmap = Bitmap.createScaledBitmap(map, map.getHeight(), map.getWidth(), true);
		Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
		
		mBitmap = rotatedBitmap;
	}

	@Override
	public boolean passReceived(Received r) {
		// TODO Move token
		//mAdapter.notifyDataSetChanged(); // hopefully this will move things
		return true;
	}
}