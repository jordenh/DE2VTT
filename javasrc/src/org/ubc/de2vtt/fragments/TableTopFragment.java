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
		
		for (Token tok : tokMan.getList()) {
			final ImageView tokenImageView = new ImageView(mActivity);
			tokenImageView.setImageBitmap(tok.getBitmap());
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(40, 40);
			params.topMargin = tok.getX();
			params.rightMargin = tok.getY();
			mLayout.addView(tokenImageView, params);
			
			tokenImageView.setOnTouchListener(new TableTopOnTouchListener());
		}

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