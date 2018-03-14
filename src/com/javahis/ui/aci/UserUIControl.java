package com.javahis.ui.aci;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextField;

public class UserUIControl extends TControl { 
	  
	  TTextField txtUserId;
	
	  public void onInit() {
		  
		  txtUserId=(TTextField)this.getComponent("UserId");
	  }
	  
	  public void onTest(){
		  this.messageBox("come in!!!!1");
		  
	  }

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
