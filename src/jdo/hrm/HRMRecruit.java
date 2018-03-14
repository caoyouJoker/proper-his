/**
 * 
 */
package jdo.hrm;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;

/**
 * <p> Title: 受试者招募表对象 </p>
 *
 * <p> Description: HRM_RECRUIT对象 </p>
 *
 * <p> Copyright: Copyright (c) 2016 </p>
 *
 * <p> Company:BlueCore </p>
 *
 * @author guangl 20160614
 * @version 1.0
 */
public class HRMRecruit extends TDataStore {
	
	private static final String INIT =
		" SELECT * FROM HRM_RECRUIT ORDER BY CONTRACT_CODE,SEQ ";
	private String id=Operator.getID();
	private String ip=Operator.getIP();
	
	
	
	public HRMRecruit() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void onQuery(){
		this.setSQL(INIT);
		this.retrieve();
	}
	
	public void onUpdate(TParm parm){
		int row=parm.getInt("ROW");
		this.setItem(row, "PAT_NAME", parm.getValue("PAT_NAME"));
	}
	
	public void onNew(int insertrow){
		this.setItem(insertrow, "OPT_USER", id);
		this.setItem(insertrow, "OPT_DATE", SystemTool.getInstance().getDate());
		this.setItem(insertrow, "OPT_TERM", ip);
	}
}
