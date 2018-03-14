package jdo.odo;

import java.sql.Timestamp;
import java.util.Vector;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;

/**
*
* <p>Title: 诊断存储对象</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author ehui 2009.2.11
* @version 1.0
*/
public class CommonDiag extends StoreBase{

    /**
     * 科室医师
     */
    private String deptOrDr;
    /**
     * 代码
     */
    private String deptOrdrCode;
    /**
     * 诊断类型（中西医）
     */
    private String icdType;
    
    TDataStore ICD = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
    
    public CommonDiag(String deptOrDr,String deptOrdrCode){
    	this.setDeptOrDr(deptOrDr);
    	this.setDeptOrdrCode(deptOrdrCode);
    	this.onQuery();
    }
    /**
     * 设置科室、医师类别
     * @param deptOrDr
     */
    public void setDeptOrDr(String deptOrDr){
    	this.deptOrDr=deptOrDr;
    }
    /**
     * 得到科室、医师类别
     * @return deptOrDr String
     */
    public String getDeptOrDr(){
    	return this.deptOrDr;
    }
    /**
     * 设置代码
     * @param deptOrdrCode
     */
    public void setDeptOrdrCode(String deptOrdrCode){
    	this.deptOrdrCode=deptOrdrCode;
    }
    /**
     * 得到代码
     * @return
     */
    public String getDeptOrdrCode(){
    	return this.deptOrdrCode;
    }
    /**
     * 设置诊断类型
     * @param icdType 诊断类型
     */
    public void setIcdType(String icdType){
    	this.icdType=icdType;
    }
    /**
     * 得到诊断类型
     */
    public String getIcdType(){
    	return this.icdType;
    }
    /**
     * 得到SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT * FROM OPD_COMDIAG WHERE DEPT_OR_DR = '" + this.getDeptOrDr() + "' AND DEPTORDR_CODE='" +this.getDeptOrdrCode()+	"' ORDER BY SEQ";
    }
    /**
     * 插入数据
     * @param row int
     * @return int
     */
    public int insertRow(int row){
        int newRow = super.insertRow(row);
        if(newRow==-1)
            return -1;
        //设置门急住别
        setItem(newRow,"DEPT_OR_DR",getDeptOrDr());
        setItem(newRow,"DEPTORDR_CODE",getDeptOrdrCode());
        setItem(newRow,"ICD_TYPE",getIcdType());
        setItem(newRow,"SEQ",getMaxSeq());
        setActive(newRow, false);
//        setItem(newRow,"ORDER_DATE",super.getDBTime());
        return newRow;
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
        return super.setOperator(optUser,optDate,optTerm);
    }
    /**
     * 返回最大SEQ_NO
     * @return int
     */
    public int getMaxSeq(){
    	
    	if(this.rowCount()<=1){
    		return 1;
    	}
    	return this.getItemInt(this.rowCount()-2, "SEQ")+1;
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
		if(rowNow<0){
			return "";
		}
			
    	if("ICD_CHN_DESC".equalsIgnoreCase(column)){
    		Operator d;
    		if("en".equalsIgnoreCase(Operator.getLanguage())){
    			return icdCode+"  "+ icdParm.getValue("ICD_ENG_DESC",rowNow);
    		}else{
    			return icdCode+"  "+ icdParm.getValue("ICD_CHN_DESC",rowNow);
    		}
    			
    			
    	}
    	return "";
    }
//    public setOtherColumnValue(TParm parm,int row,String column , Objecdt value)
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


}
