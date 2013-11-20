package org.ubc.de2vtt.users;

import java.nio.charset.Charset;

import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.exceptions.IncorrectCommandDatumExpression;

public class User {
        private int ID;
        private String Alias;
        
        public User(Received rcv) {
        	if (rcv.getCommand() != Command.UPDATE_ALIAS) {
                throw new IncorrectCommandDatumExpression();
		    }
		    
		    byte[] data = rcv.getData();
		    if(data.length == 0) {
		    	ID = -1;
		    	Alias = "ERR";
		    } else {
		    	ID = data[0];
		    	Alias = new String(data, 1, data.length - 1, Charset.forName("US-ASCII"));
		    }
        }
        
        public User(int ID, String Alias) {
        	this.ID = ID;
        	this.Alias = Alias;
        }
        
        public String getAlias() {
                return Alias;
        }
        
        public int getID() {
               return ID;
        }
        
        public void setAlias(String newAlias) {
            Alias = newAlias;
	    }
	    
	    public void setID(int newID) {
	        ID = newID;
	    }
}