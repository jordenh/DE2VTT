package org.ubc.de2vtt.token;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.ubc.de2vtt.SharedPreferencesManager;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.SparseArray;

public class TokenManager {
	private static final String TOKENS_KEY = "tokens";
	private static final int[] COLORS = {Color.CYAN, Color.MAGENTA, Color.WHITE, Color.GREEN, Color.YELLOW};
	
	static TokenManager sharedInstance;
	private SparseArray<Token> localTokenList;
	private SparseArray<Token> remoteTokenList;
	private Queue<Bitmap> sendBmps;
	
	public static TokenManager getSharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new TokenManager();
		}
		return sharedInstance;
	}
	
	protected TokenManager() {
		localTokenList = new SparseArray<Token>();
		remoteTokenList = new SparseArray<Token>();
		sendBmps = new LinkedList<Bitmap>();
	}
	
	public void add(Token tok) {
		if (tok.isLocal()) {
			if (!sendBmps.isEmpty()) {
				tok.setBmp(sendBmps.remove());
			}
			localTokenList.append(tok.getId(), tok);
		} else {
			// TODO: set token's bitmap
			int playerID = tok.getPlayerID();
			int[] color = new int[1];
			color[0] = COLORS[playerID % COLORS.length];
			Bitmap bmp = Bitmap.createBitmap(color, 1, 1, Bitmap.Config.RGB_565);
			tok.setBmp(bmp);
			remoteTokenList.append(tok.getId(), tok);
		}
	}
	
	public void remove(Token tok) {
		localTokenList.remove(tok.getId());
	}
	
	public void resetTokenManager() {
		localTokenList = new SparseArray<Token>();
		remoteTokenList = new SparseArray<Token>();
		sendBmps = new LinkedList<Bitmap>();
	}
	
	public void save() {
		new TokenSave().execute();
	}
	
	public void queueBitmap(Bitmap bmp) {
		sendBmps.add(bmp);
	}
	
	private class TokenSave extends AsyncTask<Void, Integer, Void>  {

		@Override
		protected Void doInBackground(Void... params) {
			SharedPreferencesManager man = SharedPreferencesManager.getSharedInstance();
			Set<String> s = new HashSet<String>();
			int key = 0;
			
			for (int i = 0; i < localTokenList.size(); i++) {
				key = localTokenList.keyAt(i);
				Token t = localTokenList.get(key);
				s.add(t.encode());
			}
			
			man.putStringSet(TOKENS_KEY, s);
			return null;
		}
	}
	
	public void load() {
		new TokenLoad().execute();
	}
	
	// TODO: token ids need to be assigned by the DE2 each session
	private class TokenLoad extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			SharedPreferencesManager man = SharedPreferencesManager.getSharedInstance();
			Set<String> s = man.getStringSet(TOKENS_KEY);
			String[] tokens = (String[]) s.toArray();
			for (int i = 0; i < tokens.length; i++) {
				Token t = new Token(tokens[i]);
				localTokenList.append(t.getId(), t);
			}
			return null;
		}
	}
	
	// NOTE: token received from DE2 can be used to move a token, no need for a move object
	public void move(Token tok) {
		if (tok.isLocal()) {
			Token toMove = localTokenList.get(tok.getId());
			toMove.move(tok.getX(), tok.getY());
		} else {
			remoteTokenList.remove(tok.getId());
			remoteTokenList.append(tok.getId(), tok);
		}
	}
	
	public int sizeLocal() {
		return localTokenList.size();
	}
	
	public int sizeAll() {
		return localTokenList.size() + remoteTokenList.size();
	}
	
	public int getLocalKey(int i) {
		return localTokenList.keyAt(i);
	}
	
	public Token getLocal(int i) {
		return localTokenList.get(i);
	}
	
	public int getRemoteKey(int i) {
		return remoteTokenList.keyAt(i);
	}
	
	public Token getRemote(int i) {
		return remoteTokenList.get(i);
	}
	
	public List<Token> getList() {
		List<Token> l = new ArrayList<Token>();
		
		addElementsToList(localTokenList, l);
		addElementsToList(remoteTokenList, l);
		
		return l;
	}
	
	private void addElementsToList(SparseArray<Token> a, List<Token> l) {
		for (int i = 0; i < a.size(); i++) {
			int key = a.keyAt(i);
			Token t = a.get(key);
			l.add(t);
		}
	}
}
