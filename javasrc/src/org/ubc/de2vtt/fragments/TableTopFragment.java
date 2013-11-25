package org.ubc.de2vtt.fragments;

import java.util.HashMap;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.tabletop.TableTopOnTouchListener;
import org.ubc.de2vtt.token.Token;
import org.ubc.de2vtt.token.TokenManager;
import org.ubc.de2vtt.users.DMManager;

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
import android.util.Log;

public class TableTopFragment extends WINGFragment {
	private static final String TAG = TableTopFragment.class.getSimpleName();
	
	protected View mParentView;
	private Activity mActivity;
	private RelativeLayout mLayout;
	private ImageView mMapView;
	private TokenManager tokMan = TokenManager.getSharedInstance();
	private DMManager dmMan = DMManager.getSharedInstance();
	private static Bitmap mBitmap;
	private HashMap<Integer, ImageView> tokenViews;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_tabletop, container,
				false);
		mActivity = this.getActivity();
		tokenViews = new HashMap<Integer, ImageView>();

		mLayout = (RelativeLayout) mParentView.findViewById(R.id.tabletop);

		if (mBitmap != null) {
			mMapView = (ImageView) mParentView.findViewById(R.id.MapView);
			mMapView.setImageBitmap(mBitmap);
			mMapView.setScaleType(ScaleType.FIT_XY);
		}

		final ViewTreeObserver vto = mParentView.findViewById(R.id.tabletop)
				.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			private int fragWidth;
			private int fragHeight;

			@Override
			public void onGlobalLayout() {
				final int fragmentWidth = mParentView.findViewById(
						R.id.tabletop).getWidth();
				final int fragmentHeight = mParentView.findViewById(
						R.id.tabletop).getHeight();

				fragWidth = fragmentWidth;
				fragHeight = fragmentHeight;

				if (fragmentWidth != 0) {
					setupTokenViews();
				}

			}

			private void setupTokenViews() {
				for (Token tok : tokMan.getList()) {
					new TokenViewSetter(fragWidth, fragHeight).execute(tok);
				}

				getView().getViewTreeObserver().removeGlobalOnLayoutListener(
						this);
			}
		});

		// setAcceptedCommands(new Command[0]);

		return mParentView;
	}

	class TokenViewSetter extends AsyncTask<Token, Void, Bitmap> {
		private Token tok;
		private int fragWidth, fragHeight;

		public TokenViewSetter(int fragWidth, int fragHeight) {
			this.fragHeight = fragHeight;
			this.fragWidth = fragWidth;
		}

		@Override
		protected Bitmap doInBackground(Token... params) {
			tok = params[0];
			Integer tokId = Integer.valueOf(tok.getId());
			
			if (tokenViews.containsKey(tokId)) {
				Bitmap bmp = tok.getBitmap();
	
				Matrix matrix = new Matrix();
				matrix.postRotate(90);
				Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0,
						bmp.getWidth(), bmp.getHeight(), matrix, true);
				return rotatedBitmap;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap b) {
			ImageView tokenImageView;
			Integer tokId = Integer.valueOf(tok.getId());
			
			if (tokenViews.containsKey(tokId)) {
				tokenImageView = tokenViews.get(tokId);
			} else {
				tokenImageView = new ImageView(mActivity);
				tokenImageView.setImageBitmap(b);
				
				tokenViews.put(tokId, tokenImageView);
			}

			tok.setImageView(tokenImageView);

			final int tokenWidth = fragWidth / 12;
			final int tokenHeight = fragHeight / 17;

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					tokenWidth, tokenHeight);
			params.leftMargin = fragWidth - (int) (tok.getY() * fragWidth)
					- params.width;
			params.topMargin = (int) (tok.getX() * fragHeight);
			tokenImageView.setLayoutParams(params);
			mLayout.addView(tokenImageView);

			if ((tok.isLocal()) || (dmMan.isUserDM())) {
				tokenImageView.setOnTouchListener(new TableTopOnTouchListener(
						tok, fragWidth, fragHeight));
			}
		}
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
			float sx = (float) (340.0 / (float) b.getWidth());
			float sy = (float) (240.0 / (float) b.getHeight());
			matrix.preScale(sy, sx);

			Bitmap rotatedBitmap = Bitmap.createBitmap(b, 0, 0, 340, 240,
					matrix, true);

			mBitmap = rotatedBitmap;
			return null;
		}
	}

	@Override
	public boolean passReceived(Received r) {
		Log.v(TAG, "passReceived");
		
		if (r.getCommand() == Command.OUTPUT_TOKEN_INFO
				| r.getCommand() == Command.MOVE_TOKEN) {
			final int fragmentWidth = mParentView.findViewById(R.id.tabletop)
					.getWidth();
			final int fragmentHeight = mParentView.findViewById(R.id.tabletop)
					.getHeight();

			TokenViewSetter tvs = new TokenViewSetter(fragmentWidth,
					fragmentHeight);

			Token t = new Token(r);
			TokenManager tm = TokenManager.getSharedInstance();
			tm.move(t);
			tvs.execute(t);
		}

		// update map
		mMapView = (ImageView) mParentView.findViewById(R.id.MapView);
		mMapView.setImageBitmap(mBitmap);
		mMapView.setScaleType(ScaleType.FIT_XY);
		return true;
	}
}