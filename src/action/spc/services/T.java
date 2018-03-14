package action.spc.services;

import java.sql.Timestamp;

import com.dongyang.util.StringTool;

public class T {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dOrderDate = "2012-10-31 08:00:59.0" ;
		Timestamp s = StringTool.getTimestamp(dOrderDate,"yyyyMMddHHmmss");
		String nowStr = StringTool.getString(s, "yyyyMMdd");
 
	System.out.println("nowStr-----------:"+nowStr);
	
	}

}
