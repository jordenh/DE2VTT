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
			private int fragWidth;
			
			@Override
			public void onGlobalLayout() {
				final int fragmentWidth = mParentView.findViewById(R.id.tabletop).getWidth();
				fragWidth = fragmentWidth;
				if (fragmentWidth != 0){
					setupTokenViews(fragmentWidth);
				}

			}

			private void setupTokenViews(final int fragmentWidth) {
				for (Token tok : tokMan.getList()) {
					new TokenViewSetter().execute(tok);
				}
				
				getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
			
			class TokenViewSetter extends AsyncTask<Token, Void, Bitmap> {
				private Token tok;
						
				@Override
				protected Bitmap doInBackground(Token... params) {
					tok = params[0];
					Bitmap bmp = tok.getBitmap();
					
					Matrix matrix = new Matrix();
					matrix.postRotate(90);
					Bitmap rotatedBitmap = Bitmap.createBitmap(bmp , 0, 0, 
							bmp.getWidth(), bmp.getHeight(), matrix, true);
					return rotatedBitmap;
				}
				
				@Override
				protected void onPostExecute(Bitmap b) {
					final ImageView tokenImageView = new ImageView(mActivity);

					tokenImageView.setImageBitmap(b);
					
					tok.setImageView(tokenImageView);

					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(40, 40);
					params.leftMargin = fragWidth - tok.getY() - params.width;
					params.topMargin = tok.getX();
					tokenImageView.setLayoutParams(params);
					mLayout.addView(tokenImageView);
					
					tokenImageView.setOnTouchListener(new TableTopOnTouchListener());
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
			Bitmap b = params[0];
			
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			float sx = (float) (340.0 / (float)b.getWidth());
			float sy = (float) (240.0 / (float)b.getHeight());
			matrix.preScale(sy, sx);
			
			Bitmap rotatedBitmap = Bitmap.createBitmap(b , 0, 0, 
					340, 240, matrix, true);
			
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