package jdo.hrm;

import java.sql.Timestamp;

import jdo.reg.SessionTool;
import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
/**
 * <p> Title: 健康检查日班表 </p>
 *
 * <p> Description: 健康检查日班表 </p>
 *
 * <p> Copyright: javahis 20090922 </p>
 *
 * <p> Company:JavaHis </p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMSchdayDr extends TDataStore {
	private static final String INIT_SQL="SELECT * FROM HRM_SCHDAY ";
	private static final String GET_FILE_SQL="SELECT FILE_NO FROM HRM_SCHDAY WHERE REGION_CODE='#' AND ADM_DATE=TO_DATE('#','YYYYMMDD') AND SESSION_CODE='#' AND CLINIC_AREA='#' AND DEPT_CODE='#' AND DR_CODE='#'";
	private static final String GET_ATTRI="SELECT DEPT_ATTRIBUTE FROM HRM_SCHDAY WHERE REGION_CODE='#' AND CLINIC_AREA='#' AND DEPT_CODE='#' AND DR_CODE='#'";
	private HRMClinicRoom room;
	private static StringUtil util;
	private String id=Operator.getID();
	private String ip=Operator.getIP();
	/**
	 * 初始化
	 * @param where
	 * @return
	 */
	public boolean onQuery(String where){
		if(util.isNullString(where)){
			return false;
		}
		String sql=INIT_SQL+where+"ORDER BY DEPT_CODE,DR_CODE";
		//// System.out.println("where.sql"+sql);
		this.setSQL(sql);
		if(this.retrieve()!=0){
			return false;
		}
		return true;
	}
	/**
	 * 根据当前日期，当前登录诊区，新增多条记录
	 * @return
	 */
	public boolean onQuery(){
		Timestamp now=this.getDBTime();
		String tomStr=StringTool.getString(now,"yyyyMMdd");
		/**
		 * REGION_CODE    VARCHAR2(20 BYTE)              NOT NULL,
  ADM_DATE       DATE,
  SESSION_CODE   VARCHAR2(20 BYTE)              NOT NULL,
  CLINIC_AREA    VARCHAR2(20 BYTE)              NOT NULL,
  CLINICROOM_NO  VARCHAR2(20 BYTE)              NOT NULL,
  DEPT_CODE      VARCHAR2(20 BYTE),
  DR_CODE        VARCHAR2(20 BYTE),
  FILE_NO        VARCHAR2(20 BYTE),
  OPT_USER       VARCHAR2(20 BYTE)              NOT NULL,
  OPT_DATE       DATE                           NOT NULL,
  OPT_TERM
		 */
		String regionCode=Operator.getRegion();
		this.setSQL(INIT_SQL);
		this.retrieve();
//		this.setFilter("REGION_CODE='" +regionCode+"' AND ADM_DATE='"+tomStr+"'");
//		this.filter();
		TParm clinicParm=getClinicRoom();
		if(clinicParm==null){
			// System.out.println("is null");
			return false;
		}
		int count=clinicParm.getCount();
		if(count<=0){
			// System.out.println("count <0");
			return false;
		}
		for(int i=0;i<count;i++){
			String clinicArea=clinicParm.getValue("CLINICAREA_CODE",i);
			String clinicRoom=clinicParm.getValue("CLINICROOM_NO",i);
			String finder="CLINIC_AREA='" +clinicArea+
					"' AND CLINICROOM_NO='" +clinicRoom+
					"'";

			if(this.find(finder)<0){
				int row=this.insertRow();
				this.setItem(row, "REGION_CODE", regionCode);
//				this.setItem(row, "ADM_DATE", tomStr);
//				this.setItem(row, "SESSION_CODE", SessionTool.getInstance().getDrSessionNow("O"));
				this.setItem(row, "CLINIC_AREA", clinicArea);
				this.setItem(row, "CLINICROOM_NO", clinicRoom);
				this.setItem(row, "OPT_USER", Operator.getID());
				this.setItem(row, "OPT_DATE", this.getDBTime());
				this.setItem(row, "OPT_TERM", Operator.getIP());
				this.setActive(row,false);
			}
		}
		return true;
	}
	/**
	 * 取得诊间信息
	 * @return
	 */
	public TParm getClinicRoom(){
		TParm result=new TParm();
		if(room==null){
			room=new HRMClinicRoom();
		}
		result=room.getClinicRoomData();
		if(result==null){
			return null;
		}
		return result;
	}
	/**
	 * 新增
	 * @param parm
	 * @return
	 */
	public TParm onNew(TParm parm){
		TParm result=new TParm();
		if(parm==null){
			return result;
		}
		//// System.out.println("parm="+parm);
		String[] names=parm.getNames();
		int row=this.insertRow();
		for(int i=0;i<names.length;i++){
			this.setItem(row, names[i], parm.getData(names[i]));
		}
		this.setItem(row, "OPT_USER", id);
		this.setItem(row, "OPT_TERM", ip);
		this.setItem(row, "OPT_DATE", this.getDBTime());
		//// System.out.println("before onNew");
//		this.showDebug();
		String[] sql=this.getUpdateSQL();
		for(String temp:sql){
			//// System.out.println("sql="+temp);
		}
//		this.update();
		result=new TParm(TJDODBTool.getInstance().update(sql));

		return result;
	}
	/**
	 * 给据给入参数取得FILE_NO
	 * @param regionCode
	 * @param admDate
	 * @param sessionCode
	 * @param areaCode
	 * @param roomCode
	 * @return
	 */
	public static String getFileNo(String regionCode,String admDate,String sessionCode,String areaCode,String deptCode,String drCode){
		String fileNo="";
		if(util.isNullString(regionCode)||util.isNullString(admDate)||util.isNullString(sessionCode)||util.isNullString(areaCode)||util.isNullString(deptCode)||util.isNullString(drCode)){
			//// System.out.println("getFileNo is null");
			return fileNo;
		}
		String sql=GET_FILE_SQL.replaceFirst("#", regionCode).replaceFirst("#", admDate).replaceFirst("#", sessionCode).replaceFirst("#", areaCode).replaceFirst("#", deptCode).replaceFirst("#", drCode);
		//// System.out.println("sql="+sql);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		//// System.out.println("result-="+result);
		if(result.getErrCode()!=0){
			return fileNo;
		}
		fileNo=result.getValue("FILE_NO",0);
		return fileNo;
	}
	/**
	 * 得到其他列数据
	 * @param parm TParm
	 * @param row int
	 * @param column String
	 * @return Object
	 */
	public Object getOtherColumnValue(TParm parm, int row, String column) {
		if("ADM_DATE_TIME".equalsIgnoreCase(column)){
			Timestamp admDateTime=StringTool.getTimestamp(this.getItemString(row, "ADM_DATE"),"yyyyMMdd");
			return admDateTime;
		}
		return "";
	}
    /**
     * 设置其他项目
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
    public boolean setOtherColumnValue(TParm parm,int row,String column,Object value)
    {
    	if("ADM_DATE_TIME".equalsIgnoreCase(column)){
    		String admDate=StringTool.getString((Timestamp)value,"yyyyMMdd");
    		this.setItem(row, "ADM_DATE", admDate);
    	}
    	return true;
    }
	/**
	 * 取得当前登录人员的科别属性
	 * @return
	 */
	public static String getDeptAttribute(){
		String deptAtt="";
//		String admDate=StringTool.getString(TJDODBTool.getInstance().getDBTime(),"yyyyMMdd");
//		String sessionCode = SessionTool.getInstance().getDrSessionNow("O");
		String sql=GET_ATTRI.replaceFirst("#", Operator.getRegion()).replaceFirst("#", Operator.getStation()).replaceFirst("#", Operator.getDept()).replaceFirst("#", Operator.getID());
		// System.out.println("attri.sql="+sql);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		deptAtt=result.getValue("DEPT_ATTRIBUTE",0);
		return deptAtt;
	}
}
