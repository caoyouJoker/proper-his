package jdo.opd.ws;

import java.util.ArrayList;
import java.util.List;

public class OpdOrderList {

	private ArrayList<OpdOrder> opdOrderList= new ArrayList<OpdOrder>();
	private String rxNo;
	public String getRxNo() {
		return rxNo;
	}

	public void setRxNo(String rxNo) {
		this.rxNo = rxNo;
	}

	public ArrayList<OpdOrder> getOpdOrderList() {
		return opdOrderList;
	}

	public void setOpdOrderList(ArrayList<OpdOrder> opdOrderList) {
		this.opdOrderList = opdOrderList;
	}
	
}
