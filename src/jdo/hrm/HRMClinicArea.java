package jdo.hrm;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
/**
*
* <p>Title: 健康检查诊区对象</p>
*
* <p>Description: 健康检查诊区对象</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: javahis</p>
*
* @author ehui 20090922
* @version 1.0
*/
public class HRMClinicArea extends TDataStore {
	//初始化数据
	private static final String INIT_SQL="SELECT * FROM REG_CLINICAREA ORDER BY SEQ";
	private static final String COMBO_SQL="SELECT CLINICAREA_CODE ID,CLINIC_DESC NAME,PY1,PY2 FROM REG_CLINICAREA ORDER BY SEQ";
	private static TJDODBTool tool;
	private StringUtil util;
	private String id=Operator.getID();
	private String ip=Operator.getIP();
	private String region=Operator.getRegion();
	/**
	 * 查询
	 * @return
	 */
	public boolean onQuery(){
		this.setSQL(INIT_SQL);
		return (this.retrieve()<0);
	}
	/**
	 * 保存
	 * @return
	 */
	public TParm onSave(){
		TParm result=new TParm();
		if(!this.isModified()){
			result.setErrCode(0);
			return result;
		}
		String sql[]=this.getUpdateSQL();
		result=new TParm(tool.getInstance().update(sql));
		if(result.getErrCode()==0){
			this.resetModify();
		}
		return result;
	}
	/**
	 * 新增一行数据
	 */
	public int newRow(int row){
		if(row==-1){
			row=this.insertRow();
			this.setItem(row, "OPT_USER", id);
			this.setItem(row, "OPT_TERM", ip);
			this.setItem(row, "OPT_DATE", this.getDBTime());
			this.setItem(row, "REGION_CODE", this.region);
			this.setItem(row, "SEQ", StringTool.getInt(this.getItemData(row, "#ID#")+"")+1);
			this.setActive(row, false);
			return row;
		}else{
			return -1;
		}
	}
	/**
	 * 提供COMBO数据
	 * @return
	 */
	public static TParm getComboParm(){
		TParm result=new TParm(tool.getInstance().select(COMBO_SQL));
		return result;
	}
}
