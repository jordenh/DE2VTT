package org.ubc.de2vtt.bulletin;

import java.util.ArrayList;
import java.util.List;

public class BulletinManager {
	private List<Bulletin> bulletins;
	
	private static BulletinManager sharedInstance;
	
	public static BulletinManager getSharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new BulletinManager();
		}
		return sharedInstance;
	}
	
	protected BulletinManager() {
		bulletins = new ArrayList<Bulletin>();
	}
	
	public List<Bulletin> getList() {
		return bulletins;
	}
	
	public Bulletin getAtIndex(int i) {
		return bulletins.get(i);
	}
	
	public int count() {
		return bulletins.size();
	}
	
	public void removeAtIndex(int index) {
		bulletins.remove(index);
	}
	
	public void remove(Bulletin b) {
		bulletins.remove(b);
	}
	
	public void add(Bulletin b) {
		bulletins.add(b);
	}
}
