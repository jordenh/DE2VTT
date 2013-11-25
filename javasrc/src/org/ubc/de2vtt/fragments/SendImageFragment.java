package org.ubc.de2vtt.fragments;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Message;
import org.ubc.de2vtt.comm.Messenger;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.comm.sendables.SendableBitmap;
import org.ubc.de2vtt.token.TokenManager;
import org.ubc.de2vtt.users.DMManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class SendImageFragment extends WINGFragment {
	private static final String TAG = SendImageFragment.class.getSimpleName();        

	private static final int TOKEN_X = 20;
	private static final int TOKEN_Y = 20;
	private static final int MAP_X = 340;
	private static final int MAP_Y = 240;

	protected View mParentView;

	private static final int REQUEST_CODE = 1;
	private Bitmap bitmap;
	private Uri selectedImage;
	private ProgressDialog progress;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_sendimage,  container, false);

		setupOnClickListeners();

		if (bitmap != null) {
			ImageView imageView = (ImageView) mParentView.findViewById(R.id.imgView);
			imageView.setImageBitmap(bitmap);
			imageView.setScaleType(ScaleType.FIT_XY);
		}

		DMManager dmm = DMManager.getSharedInstance();
		if (!dmm.isUserDM()) {
			Button sendManBtn = (Button) mParentView.findViewById(R.id.btnSendMap);
			sendManBtn.setVisibility(View.GONE);
		}
		
		updateButtonState();

		return mParentView;
	}

	private void setupOnClickListeners() {                
		Button pickBtn = (Button) mParentView.findViewById(R.id.btnSelectImage);
		pickBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pickImage(v);
			}
		});

		Button sendTokBtn = (Button) mParentView.findViewById(R.id.btnSendToken);
		sendTokBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendToken();
			}
		});

		Button sendMapBtn = (Button) mParentView.findViewById(R.id.btnSendMap);
		sendMapBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendMap();
			}
		});
	}

	private void updateButtonState() {
		boolean canSend = Messenger.readyToSend();

		Button sendMapBtn = (Button) mParentView.findViewById(R.id.btnSendMap);
		sendMapBtn.setEnabled(canSend);

		Button sendTokBtn = (Button) mParentView.findViewById(R.id.btnSendToken);
		sendTokBtn.setEnabled(canSend);

		if (bitmap == null) {
			sendMapBtn.setEnabled(false);
			sendTokBtn.setEnabled(false);
		}
	}

	public void pickImage(View View) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);

		startActivityForResult(Intent.createChooser(intent,"Select Image"), REQUEST_CODE);
	}

	public void sendToken() {
		sendImage(Command.SEND_TOKEN, TOKEN_X, TOKEN_Y);
	}

	public void sendMap() {
		sendImage(Command.SEND_MAP, MAP_X, MAP_Y);
	}

	public void sendImage(Command cmd, int x, int y) {
		if (cmd == Command.SEND_MAP) {
			if (bitmap != null) {
				Bitmap scaled = Bitmap.createScaledBitmap(bitmap, x, y, false);
				SendableBitmap bmp = new SendableBitmap(scaled.copy(Bitmap.Config.ARGB_8888, false));
				Message msg = new Message(cmd, bmp);
				Messenger messenger = Messenger.GetSharedInstance();
				
				messenger.send(msg);
				updateButtonState();
			} else {
				Log.v(TAG, "Attempt to send null bitmap.");
			}
		} else if(cmd == Command.SEND_TOKEN) {
			if (bitmap != null) {
				Bitmap scaled = Bitmap.createScaledBitmap(bitmap, x, y, false);
				SendableBitmap bmp = new SendableBitmap(scaled.copy(Bitmap.Config.ARGB_8888, false));
				Message msg = new Message(cmd, bmp);
				Messenger messenger = Messenger.GetSharedInstance();

				TokenManager m = TokenManager.getSharedInstance();
				m.queueBitmap(scaled);

				messenger.send(msg);
				updateButtonState();
			} else {
				Log.v(TAG, "Attempt to send null bitmap.");
			}
		} else {
			Log.v(TAG, "Attempt to send image with invalid command.");
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v(TAG, "onActivityResult entered.");
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && null != data) {
			progress = new ProgressDialog(getActivity());
			progress.setTitle("Loading");
			progress.setMessage("Loading your image...");
			progress.show();
			
			selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			
			ImageView imageView = (ImageView) mParentView.findViewById(R.id.imgView);
			imageView.setImageResource(R.drawable.black);
			
			ThumbnailSetter ts = new ThumbnailSetter();
			ts.execute(picturePath);
		}
		Log.v(TAG, "onActivityResult finished.");
	}
	
	private class ThumbnailSetter extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... params) {
			String picturePath = params[0];
			
			Bitmap bmp = BitmapFactory.decodeFile(picturePath);
			return Bitmap.createScaledBitmap(bmp, 500, 500, false);
		}
		
		@Override
		protected void onPostExecute(Bitmap b) {
			Log.v(TAG, "Setting image bmp");
			bitmap = b;
			ImageView imageView = (ImageView) mParentView.findViewById(R.id.imgView);
			imageView.setImageBitmap(b);

			progress.dismiss();
			updateButtonState();
		}
	}

	@Override
	public boolean passReceived(Received r) {
		Log.e(TAG, "Received message from Mailbox via MainActivity");
		return false;
	}
}
