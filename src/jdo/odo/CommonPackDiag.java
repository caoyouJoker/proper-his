package jdo.odo;

import java.sql.Timestamp;
import java.util.Vector;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import com.javahis.util.StringUtil;
/**
 * 
 * <p>Title: 门诊医生工作站常用组套诊断</p>
 * 
 * <p>Description:门诊医生工作站常用组套诊断</p>
 * 
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 * 
 * <p>Company:Javahis</p>
 * 
 * @author ehui 20090429
 * @version 1.0
 */
public class CommonPackDiag extends StoreBase {
	//诊断内存表
	TDataStore ICD = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
	/**
	 * 科室、医师分类
	 */
	private String deptOrDr;
	/**
	 * 科室或医师代码
	 */
	private String deptOrDrCode;
	/**
	 * 模板代码
	 */
	private String packCode;
	/**
	 * 得到科室、医师分类
	 * @return deptOrDr String
	 */
	public String getDeptOrDr() {
		return deptOrDr;
	}
	/**
	 * 设置科室、医师分类
	 * @parm deptOrDr String
	 */
	public void setDeptOrDr(String deptOrDr) {
		this.deptOrDr=deptOrDr;
	}
	/**
	 * 得到科室或医师代码
	 * @return deptOrDrCode String
	 */
	public String getDeptOrDrCode() {
		return deptOrDrCode;
	}
	/**
	 * 设置科室或医师代码
	 * @parm deptOrDrCode String
	 */
	public void setDeptOrDrCode(String deptOrDrCode) {
		this.deptOrDrCode=deptOrDrCode;
	}
	/**
	 * 得到模板代码
	 * @return packCode String
	 */
	public String getPackCode() {
		return packCode;
	}
	/**
	 * 设置模板代码
	 * @parm packCode String
	 */
	public void setPackCode(String packCode) {
		this.packCode=packCode;
	}
	/**
	 * Constructor
	 * @param deptOrDr
	 * @param deptOrDrCode
	 * @param packCode
	 */
	public CommonPackDiag(String deptOrDr,String deptOrDrCode,String packCode){
		this.setDeptOrDr(deptOrDr);
		this.setDeptOrDrCode(deptOrDrCode);
		this.setPackCode(packCode);

	}
    /**
     * 得到SQL
     * @return String
     */
    protected String getQuerySQL(){
        return OpdComPackQuoteTool.INIT_DIAG_SQL+" WHERE PACK_CODE='" +packCode+"'";
    }
    /**
     * 插入数据
     * @param row int
     * @return int
     */
    public int insertRow(int row){
        int newRow = super.insertRow(row);
        //设置门急住别
        setItem(newRow,"DEPT_OR_DR",getDeptOrDr());
        setItem(newRow,"DEPTORDR_CODE",getDeptOrDrCode());
        setItem(newRow,"PACK_CODE",getPackCode());
//        setItem(newRow,"ORDER_DATE",super.getDBTime());
        return newRow;
    }
    /**
     * 得到其他列数据
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TParm parm,int row,String column)
    {
    	TParm icdParm=ICD.getBuffer(ICD.isFilter()?ICD.FILTER:ICD.PRIMARY);
    	Vector code=(Vector)icdParm.getData("ICD_CODE");
    	String icdCode=parm.getValue("ICD_CODE",row);
		int rowNow=code.indexOf(icdCode);
		if(rowNow<0)
			return "";
    	if("ICD_CHN_DESC".equalsIgnoreCase(column)){
    			return icdParm.getValue("ICD_CHN_DESC",rowNow);
    	}
    	if("ICD_ENG_DESC".equalsIgnoreCase(column)){
    		return	icdParm.getValue("ICD_ENG_DESC",rowNow);
    	}	
    	return "";
    }
    /**
     * 设置操作用户
     * @param optUser String 操作用户
     * @param optDate Timestamp 操作时间
     * @param optTerm String 操作IP
     * @return boolean true 成功 false 失败
     */
    public boolean setOperator(String optUser,Timestamp optDate,String optTerm)
    {
        String storeName = isFilter()?FILTER:PRIMARY;
        int rows[] = getNewRows(storeName);
        return super.setOperator(optUser,optDate,optTerm);
    }
    /**
     * 返回是否有给入的诊断
     * @param code 给进要开立的诊断
     * @return boolean true:存在该诊断;false:不存在该诊断
     */
    public boolean isHaveSameDiag(String code){
    	TParm parm=this.getBuffer(this.isFilter()?this.FILTER:this.PRIMARY);
    	Vector mainFlg=(Vector)parm.getData("ICD_CODE");
    	return mainFlg.indexOf(code)>-1;
    }
    /**
     * 判断是否已经有主诊断
     * @return boolean ：true,有；false,没有
     */
    public int isHavaMainDiag(){
    	TParm parm=this.getBuffer(this.isFilter()?this.FILTER:this.PRIMARY);
    	Vector mainFlg=(Vector)parm.getData("MAIN_DIAG_FLG");
    	return mainFlg.indexOf("Y");
    }
    /**
     * 用门诊病历初始化
     * @param odo ODO
     * @return boolean true:初始化成功,false:初始化失败
     */
    public boolean initOdo(ODO odo){
    	if(!this.onQuery())
    		return false;
    	if(odo==null||odo.getDiagrec()==null||odo.getDiagrec().rowCount()<1)
    		return false;
    	Diagrec diag=odo.getDiagrec();
    	TParm diagParm=diag.getBuffer(diag.FILTER);
    	int count=diagParm.getCount();
    	for(int i=0;i<count;i++){
    		int row=this.insertRow();
    		if(row<0)
    			return false;
        	this.setItem(row, "ICD_TYPE", diagParm.getValue("ICD_TYPE",i));
        	this.setItem(row, "ICD_CODE", diagParm.getValue("ICD_CODE",i));
        	this.setItem(row, "MAIN_DIAG_FLG", diagParm.getValue("MAIN_DIAG_FLG",i));
        	this.setItem(row, "DIAG_NOTE", diagParm.getValue("DIAG_NOTE",i));
    	}
    	return true;
    }
    /**
     * 根据模板号删除模板
     * @param packCode
     * @return
     */
    public boolean deletePack(String packCode){
    	if(StringUtil.isNullString(packCode)){
    		return false;
    	}
    	this.setFilter("PACK_CODE='" +packCode+
    			"'");
    	this.filter();
    	for(int i=this.rowCount()-1;i>-1;i--){
    		this.deleteRow(i);
    	}
    	return true;
    }
    /**
     * 返回是否有主诊断
     * @return boolean true:有主诊断,false:没有主诊断
     */
    public boolean haveMainDiag(int[] i){
    	TParm parm=this.getBuffer(this.isFilter()?this.FILTER:this.PRIMARY);
    	Vector mainFlg=(Vector)parm.getData("MAIN_DIAG_FLG");
    	i[0]=mainFlg.indexOf("Y");
    	return i[0]>-1;
    }
}
