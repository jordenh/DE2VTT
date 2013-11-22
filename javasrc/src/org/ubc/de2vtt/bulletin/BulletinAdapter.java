package org.ubc.de2vtt.bulletin;

import java.util.List;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.users.UserManager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BulletinAdapter extends ArrayAdapter<Bulletin> {
	private List<Bulletin> bulletins;
	private Context context;
	private int textViewResourceId;
	
	public BulletinAdapter(Context context, int textViewResourceId,
			List<Bulletin> objects) {
		super(context, textViewResourceId, objects);
		bulletins = objects;
		this.context = context;
		this.textViewResourceId = textViewResourceId;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		BulletinHolder holder;
		
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		row = inflater.inflate(textViewResourceId, parent, false);
		
		holder = new BulletinHolder();
		
		holder.name = (TextView)row.findViewById(R.id.tvName);
		holder.text = (TextView)row.findViewById(R.id.tvBulletinText);
		
		Bulletin b = bulletins.get(position);
		
		UserManager um = UserManager.getSharedInstance();
		String name = um.getAliasWithID(b.getSenderID());
		
		holder.name.setText(name);
		holder.text.setText(b.getText());
		
		return row;
	}
	
	static class BulletinHolder {
		TextView name;
		TextView text;
	}
}
