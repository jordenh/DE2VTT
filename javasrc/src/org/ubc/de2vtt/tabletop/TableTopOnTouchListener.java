package org.ubc.de2vtt.tabletop;

import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Message;
import org.ubc.de2vtt.comm.Messenger;
import org.ubc.de2vtt.comm.sendables.SendableMove;
import org.ubc.de2vtt.token.Token;

import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


public class TableTopOnTouchListener implements View.OnTouchListener {
	private Token mTok;
    private int prev_x, prev_y; 
    private int mFragmentHeight, mFragmentWidth;
    
    public TableTopOnTouchListener (Token tok, int fragmentWidth, int fragmentHeight) {
    	mTok = tok;
    	mFragmentHeight = fragmentHeight;
    	mFragmentWidth = fragmentWidth;
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int x, y, dx, dy;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
        
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            prev_x = (int) event.getRawX();
            prev_y = (int) event.getRawY();
            break;
        case MotionEvent.ACTION_MOVE:
            x = (int) event.getRawX();
            y = (int) event.getRawY();

            dx = x - prev_x;
            dy = y - prev_y;
            
            if ((params.topMargin + dy + params.height) < mFragmentHeight && (params.leftMargin + dx + params.width) < mFragmentWidth
            		&& (params.topMargin + dy)  > 0 && (params.leftMargin + dx)  > 0) {
            	prev_x = x;
                prev_y = y;
            	
            	params.leftMargin += dx;
            	params.topMargin += dy;
            	v.setLayoutParams(params);
            }
            break;
        case MotionEvent.ACTION_UP:
        	float y_ratio = ((float) (mFragmentWidth - params.leftMargin - params.width))/((float) mFragmentWidth);
        	float x_ratio = ((float) params.topMargin)/((float) mFragmentHeight);
            
        	mTok.move(x_ratio,y_ratio);
        	SendableMove mv = new SendableMove(mTok.getId(), (int) (mTok.getX() * Token.SCREEN_WIDTH),
    				(int) (mTok.getY() * Token.SCREEN_HEIGHT));
    		Messenger m = Messenger.GetSharedInstance();
    		Message msg = new Message(Command.MOVE_TOKEN, mv);
    		m.send(msg);
        	break;
        default:
        	break;
        }
        return true;
    }
}