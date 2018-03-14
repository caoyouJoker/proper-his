package com.javahis.ui.ami;

import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EMicroField;
import com.dongyang.ui.TWord;

public class AMIRecord {
	private EMicroField name ;
	private TWord word;
	
	
	
	
	
	public EMicroField getName(){
		name = (EMicroField)word.findObject("NAME", EComponent.MICRO_FIELD_TYPE);
		return name;
	}





	public static Object getInstance() {
		// TODO Auto-generated method stub
		return null;
	}
}
