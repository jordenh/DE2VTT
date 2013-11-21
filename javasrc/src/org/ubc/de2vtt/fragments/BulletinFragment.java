package org.ubc.de2vtt.fragments;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.bulletin.BulletinAdapter;
import org.ubc.de2vtt.bulletin.BulletinManager;
import org.ubc.de2vtt.comm.Received;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class BulletinFragment extends WINGFragment {
	private View parentView;
	private ListView listView;
	private BulletinAdapter adapter;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.fragment_bulletins, container, false);
		
		setupListView(container);
		
		return parentView;
	}

	private void setupListView(ViewGroup container) {
		listView = new ListView(container.getContext());
		listView = (ListView) parentView.findViewById(R.id.lvBulletins);
		
		setListViewAdapter();
		setListViewOnLongClickListener();
	}

	private void setListViewAdapter() {
		BulletinManager bm = BulletinManager.getSharedInstance();
		adapter = new BulletinAdapter(getActivity(), R.layout.bulletin_row,
				bm.getList());
		listView.setAdapter(adapter);
	}
	
	private void setListViewOnLongClickListener() {
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent,
					View view,
					int position,
					long id) {
				BulletinManager bm = BulletinManager.getSharedInstance();
				bm.removeAtIndex(position);
				return true;
			}
		});
	}
	
	@Override
	public boolean passReceived(Received r) {
		adapter.notifyDataSetChanged();
		return true;
	}
}
