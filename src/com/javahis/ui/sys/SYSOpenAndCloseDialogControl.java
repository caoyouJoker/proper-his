package com.javahis.ui.sys;

import com.dongyang.control.TControl;

public class SYSOpenAndCloseDialogControl extends TControl{
	
	public void onInit(){
		
		class CloseTread extends Thread {
    		
    		TControl tc;
    		
    		public CloseTread(TControl tc){
    			this.tc = tc;
    		}
    		
    		public void run() {
    			tc.closeWindow();
    		}
    		
    	}
    	
    	CloseTread closeTread = new CloseTread(this);
    	
    	closeTread.start();
		
	}

}
