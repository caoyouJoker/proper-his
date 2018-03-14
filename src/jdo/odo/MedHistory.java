package jdo.odo;

import java.sql.Timestamp;
import java.util.Vector;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;

/**
*
* <p>Title: 既往史存储对象</p>
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
public class MedHistory extends StoreBase{

	/**
     * 病案号
     */
    private String mrNo;
    /**
     * 就诊号
     */
    private String caseNo;
    /**
     * 门急住别
     */
    private String admType;
    /**
     * 医师
     */
    private String drCode;
    /**
     * 部门
     */
    private String deptCode;

    TDataStore ICD = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
    /**
     * 得到病案号
     * @return String
     */
    public String getMrNo()
    {
        return this.mrNo;
    }
    /**
     * 设置病案号
     * @param mrNo String
     */
    public void setMrNo(String mrNo)
    {
        this.mrNo = mrNo;
    }
    /**
     * 得到就诊号
     * @return String
     */
    public String getCaseNo(){
    	return this.caseNo;
    }
    /**
     * 设置就诊号
     * @param caseNo String
     */
    public void setCaseNo(String caseNo){
    	this.caseNo=caseNo;
    }
    /**
     * 设置门急住别
     * @param admType String
     */
    public void setAdmType(String admType)
    {
        this.admType = admType;
    }
    /**
     * 得到门急住别
     * @return String
     */
    public String getAdmType()
    {
        return admType;
    }
    /**
     * 设置医师
     * @param drCode String
     */
    public void setDrCode(String drCode)
    {
        this.drCode = drCode;
    }
    /**
     * 得到医师
     * @return String
     */
    public String getDrCode()
    {
        return drCode;
    }
    /**
     * 设置部门
     * @param deptCode String
     */
    public void setDeptCode(String deptCode)
    {
        this.deptCode = deptCode;
    }
    /**
     * 得到部门
     * @return String
     */
    public String getDeptCode()
    {
        return deptCode;
    }
    /**
     * 得到SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT * FROM OPD_MEDHISTORY WHERE MR_NO = '" + getMrNo()+ "' ORDER BY SEQ_NO";
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
        setItem(newRow,"ADM_TYPE",getAdmType());
        setItem(newRow,"DR_CODE",getDrCode());
        setItem(newRow,"DEPT_CODE",getDeptCode());
        setItem(newRow,"CASE_NO",getCaseNo());
        setItem(newRow,"MR_NO",getMrNo());
        setItem(newRow,"ADM_DATE",StringTool.getString(super.getDBTime(),"yyyyMMddHHmmss"));

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
        int rows[] = getNewRows(storeName);
      	StringTool s;
        for(int i = 0;i < rows.length;i++)

            setItem(rows[i],"ADM_DATE",StringTool.getString(optDate,"yyyyMMddHHmmss") ,storeName);
        return super.setOperator(optUser,optDate,optTerm);
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

    	if ("ADM_DATE_FORMAT".equalsIgnoreCase(column)) {
//			System.out.println("in method getOtherColumnValue"+parm.getValue("ADM_DATE",row));

			Timestamp time = StringTool.getTimestamp(parm.getValue("ADM_DATE",row),
					"yyyyMMddHHmmss");
			return StringTool.getString(time, "yyyy/MM/dd HH:mm:ss");
		}
    	TParm icdParm=ICD.getBuffer(ICD.isFilter()?ICD.FILTER:ICD.PRIMARY);
    	Vector code=(Vector)icdParm.getData("ICD_CODE");
    	String icdCode=parm.getValue("ICD_CODE",row);
		int rowNow=code.indexOf(icdCode);
		if(rowNow<0)
			return "";

    	if("ICD_DESC".equalsIgnoreCase(column)){
    		if("en".equalsIgnoreCase(Operator.getLanguage())){
    			return icdParm.getValue("ICD_ENG_DESC",rowNow);
    		}else{
    			return icdParm.getValue("ICD_CHN_DESC",rowNow);
    		}

    	}
    	return "";
    }
    /**
     * 根据给入ICD_CODE，判断对象中是否已存在此条诊断。如有返回true,否则返回false
     * @param icdCode
     * @return
     */
    public boolean isSameICD(String icdCode){
    	int count=this.rowCount();
    	for(int i=0;i<count;i++){
    		if(icdCode.equalsIgnoreCase(this.getItemString(i, "ICD_CODE"))){
    			return true;
    		}
    	}
    	return false;
    }
    /**
     * 获取最大的SEQ_NO
     * @param mrNo String
     * @return int
     */
    public int getMaxSEQ(String mrNo){
        TParm result = this.getBuffer(this.PRIMARY);
        int max = 0;
        for(int i=0;i<result.getCount("ICD_CODE");i++){
            if(result.getInt("SEQ_NO",i)>max){
                max = result.getInt("SEQ_NO",i);
            }
        }
        return max+1;
    }
}
