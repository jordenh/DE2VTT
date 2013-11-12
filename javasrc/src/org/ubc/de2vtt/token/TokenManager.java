package org.ubc.de2vtt.token;

import java.util.HashSet;
import java.util.Set;

import org.ubc.de2vtt.SharedPreferencesManager;

import android.os.AsyncTask;
import android.util.SparseArray;

public class TokenManager {
	private static final String TOKENS_KEY = "tokens";
	
	static TokenManager sharedInstance;
	private SparseArray<Token> tokenList;
	
	public static TokenManager getSharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new TokenManager();
		}
		return sharedInstance;
	}
	
	protected TokenManager() {
		tokenList = new SparseArray<Token>();
	}
	
	public void add(Token tok) {
		tokenList.append(tok.getId(), tok);
	}
	
	public void remove(Token tok) {
		tokenList.remove(tok.getId());
	}
	
	public void save() {
		new TokenSave().execute();
	}
	
	private class TokenSave extends AsyncTask<Void, Integer, Void>  {

		@Override
		protected Void doInBackground(Void... params) {
			SharedPreferencesManager man = SharedPreferencesManager.getSharedInstance();
			Set<String> s = new HashSet<String>();
			int key = 0;
			
			for (int i = 0; i < tokenList.size(); i++) {
				key = tokenList.keyAt(i);
				Token t = tokenList.get(key);
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
				tokenList.append(t.getId(), t);
			}
			return null;
		}
	}
	
	// NOTE: token received from DE2 can be used to move a token, no need for a move object
	public void move(Token tok) {
		Token toMove = tokenList.get(tok.getId());
		toMove.move(tok.getX(), tok.getY());
	}
	
	public int size() {
		return tokenList.size();
	}
	
	public int getKey(int i) {
		return tokenList.keyAt(i);
	}
	
	public Token get(int i) {
		return tokenList.get(i);
	}
}
