package org.ubc.de2vtt.comm;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;

public class SocketConnect extends AsyncTask<String, Integer, Socket> {

	// The main parcel of work for this thread.  Opens a socket
	// to connect to the specified IP.
	@Override
	protected Socket doInBackground(String... params) {
		Socket socket = null;
		String ip = params[0];
		Integer port = Integer.decode(params[1]);

		try {
			socket = new Socket(ip, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return socket;
	}

	// After executing the doInBackground method, this is 
	// automatically called, in the UI (main) thread to store
	// the socket in this app's persistent storage
//	protected void onPostExecute(Socket s) {
//		MyApplication myApp = (MyApplication) MainActivity.getApplication();
//		myApp.sock = s;
//	}
}