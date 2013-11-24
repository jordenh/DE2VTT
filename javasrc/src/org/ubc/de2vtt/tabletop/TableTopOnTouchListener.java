package org.ubc.de2vtt.tabletop;

import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


public class TableTopOnTouchListener implements View.OnTouchListener {
	private int prev_x,  prev_y;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		final int dx, dy;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			prev_x = (int) event.getRawX();
			prev_y = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final int x = (int) event.getRawX();
			final int y = (int) event.getRawY();

			dx = x - prev_x;
			dy = y - prev_y;
			
			prev_x = x;
			prev_y = y;

			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
			params.leftMargin += dx;
			params.topMargin += dy;
			v.setLayoutParams(params);  
			break;
		default:
			break;
		}
		return true;
	}
}
