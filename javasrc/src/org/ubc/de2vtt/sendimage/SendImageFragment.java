package org.ubc.de2vtt.sendimage;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Message;
import org.ubc.de2vtt.comm.Messenger;
import org.ubc.de2vtt.comm.ReceiveTask;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.comm.Receiver;
import org.ubc.de2vtt.sendables.SendableBitmap;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class SendImageFragment extends Fragment {
	private static final String TAG = SendImageFragment.class.getSimpleName();	
	
	protected View mParentView;
	private Activity mActivity;
	private Receiver receiver;
	
	private static final int REQUEST_CODE = 1;
    private Bitmap bitmap;
    private ImageView imageView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_sendimage,  container, false);
	
		setupOnClickListeners();
		
		ImageView imageView = (ImageView) mParentView.findViewById(R.id.imgView);
		imageView.buildDrawingCache();
		bitmap = imageView.getDrawingCache();
		
		mActivity = this.getActivity();
		
		return mParentView;
	}

	private void setupOnClickListeners() {
		Button pickBtn = (Button) mParentView.findViewById(R.id.btnPickImage);
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
	
	public void pickImage(View View) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);

		startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQUEST_CODE);
    }
	
	public void sendToken() {
		sendImage(Command.SEND_TOKEN, 30, 30);
	}
	
	public void sendMap() {
		sendImage(Command.SEND_MAP, 340, 260);
	}
	
	public void sendImage(Command cmd, int x, int y) {
		if (cmd == Command.SEND_MAP || cmd == Command.SEND_TOKEN) {
			if (bitmap != null) {
				Bitmap scaled = Bitmap.createScaledBitmap(bitmap, x, y, false);
				SendableBitmap bmp = new SendableBitmap(scaled);
				Message msg = new Message(cmd, bmp);
				Messenger messenger = Messenger.GetSharedInstance();
				
				messenger.sendMessage(msg);
			} else {
				Log.v(TAG, "Attempt to send null bitmap.");
			}
		} else {
			Log.v(TAG, "Attempt to send image with invalid command.");
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
			cursor.moveToFirst();
			
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			ImageView imageView = (ImageView) mParentView.findViewById(R.id.imgView);
			bitmap = BitmapFactory.decodeFile(picturePath);
			//imageView.setImageBitmap(bitmap);
			
			receiver = new Receiver(new TCPReadTimerTask());
        }
    }
	
	public class TCPReadTimerTask extends ReceiveTask {
		@Override
		protected void performAction(Received rcv) {
			bitmap = rcv.DataToBitmap();
			
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ImageView imageView = (ImageView) mParentView.findViewById(R.id.imgView);
					imageView.setImageBitmap(bitmap);
				}		
			});
			
			receiver.cancel();
		}
	}
}


