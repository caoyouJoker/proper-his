package jdo.odo;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;

/**
 * 
 * <p>Title: 门诊医生工作站常用组套主表</p>
 * 
 * <p>Description:门诊医生工作站常用组套主表</p>
 * 
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 * 
 * <p>Company:Javahis</p>
 * 
 * @author ehui 20090429
 * @version 1.0
 */
public class CommonPackMain extends StoreBase {
	/**
	 * 科室、医师分类
	 */
	private String deptOrDr;
	/**
	 * 科室或医师代码
	 */
	private String deptOrDrCode;
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
	 * Contructor
	 * @param deptOrdr String
	 * @param deptOrDrCode String
	 */
	public CommonPackMain(String deptOrdr,String deptOrDrCode){
		this.setDeptOrDr(deptOrdr);
		this.setDeptOrDrCode(deptOrDrCode);

	}
    /**
     * 得到SQL
     * @return String
     */
    protected String getQuerySQL(){
    	TParm parm=new TParm();
    	parm.setData("DEPT_OR_DR",this.getDeptOrDr());
    	parm.setData("DEPTORDR_CODE",this.getDeptOrDrCode());
        return OpdComPackQuoteTool.getInstance().initQuote(parm);
    }
    /**
     * 用门诊病历初始化
     * @param odo ODO
     * @return boolean true:初始化成功,false:初始化失败
     */
    public boolean initOdo(ODO odo){
    	if(!this.onQuery())
    		return false;
    	int row=this.insertRow();
    	if(row<0)
    		return false;
    	if(odo==null)
    		return false;
    	this.setItem(row, "SUBJ_TEXT", odo.getSubjrec().getItemData(0, "SUBJ_TEXT"));
    	this.setItem(row, "OBJ_TEXT", odo.getSubjrec().getItemData(0, "OBJ_TEXT"));
    	this.setItem(row, "PHYSEXAM_REC", odo.getSubjrec().getItemData(0, "PHYSEXAM_REC"));
    	return true;
    }
    /**
     * 插入数据
     * @param row int
     * @return int
     */
    public int insertRow(int row){
        int newRow = super.insertRow(row);
//        if(newRow==-1)
//            return -1;
        //设置门急住别
        setItem(newRow,"DEPT_OR_DR",getDeptOrDr());
        setItem(newRow,"DEPTORDR_CODE",getDeptOrDrCode());
        setItem(newRow,"PACK_CODE",getNo());
        setActive(newRow, false);
//        setItem(newRow,"ORDER_DATE",super.getDBTime());
        return newRow;
    }
    /**
     * 通过区号原则取得新的PACK_CODE
     * @return packCode String
     */
    public String getNo(){
		String packCode = SystemTool.getInstance().getNo("ALL", "ODO", "PACK",
		"PACK_CODE");
		return packCode;
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
        int rows[] = getModifiedRows(storeName);
        for(int i = 0;i < rows.length;i++)
        {
            setItem(rows[i],"OPT_USER",optUser,storeName);
            setItem(rows[i],"OPT_DATE",optDate,storeName);
            setItem(rows[i],"OPT_TERM",optTerm,storeName);
        }
        return true;
    }
}
