package jdo.odo;

import java.sql.Timestamp;
import java.util.Vector;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

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
 * @author lzk 2009.2.11
 * @version 1.0
 */
public class Diagrec extends StoreBase{
    /**
     * 门急住别
     */
    private String admType;
    /**
     * 医师
     */
    private String drCode;
    /**
     * 诊断类型（中西医）
     */
    private String icdType;
    //取得主诊断注记
    private String GET_MAIN_FLG="SELECT * FROM SYS_DIAGNOSIS WHERE ICD_CODE='#'";
    TDataStore ICD = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
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
     * 得到SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT * FROM OPD_DIAGREC WHERE CASE_NO = '" + getCaseNo() + "' ORDER BY MAIN_DIAG_FLG DESC,ICD_CODE";
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
        setItem(newRow,"ICD_TYPE",getIcdType());
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
        int rows[] = getNewRows(storeName);
        for(int i = 0;i < rows.length;i++)
            setItem(rows[i],"ORDER_DATE",optDate,storeName);
        return super.setOperator(optUser,optDate,optTerm);
    }
//    /**
//     * 列的值改变
//     */
//    public boolean setItem(int row,String column,Object value){
//    	super.setItem(row, column, value);
////    	if(itemNow)
////    		return true;
//    	if("ICD_CODE".equalsIgnoreCase(column)){
//
//    		//System.out.println("ICD_CHN_DESC"+parm.getValue("ICD_CHN_DESC",0));
//    		this.setItem(row, "ICD_DESC", parm.getValue("ICD_CHN_DESC",0));
//    		return true;
//    	}
//    	if("MAIN_DIAG_FLG".equalsIgnoreCase(column)){
//
//    	}
//
//    	return true;
//    }
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
    	if(code==null||code.size()<=0){
    		System.out.println("getOtherColumnValue.code is null");
    		return "";
    	}
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
    	if("ICD_ENG_DESC".equalsIgnoreCase(column)){
    		return	icdParm.getValue("ICD_ENG_DESC",rowNow);
    	}
    	return "";
    }
    /**
     * 根据给入的诊断代码返回值
     * @param icdCode String
     * @param LagType String 语言种类
     * @return String
     */
    public String getIcdDesc(String icdCode,String LagType){
        TParm icdParm=ICD.getBuffer(ICD.isFilter()?ICD.FILTER:ICD.PRIMARY);
        Vector code=(Vector)icdParm.getData("ICD_CODE");
        int rowNow=code.indexOf(icdCode);
        if("en".equals(LagType))
            return icdParm.getValue("ICD_ENG_DESC",rowNow);
        else
            return icdParm.getValue("ICD_CHN_DESC",rowNow);
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
     * 返回code值+诊断内容
     * @return String
     */
    public String mainIcd(){
    	String result="";
    	int[] mainDiag=new int[]{0};
    	if(this.haveMainDiag(mainDiag)){
    		String code=this.getItemString(mainDiag[0], "ICD_CODE");
    		String desc=ICD.getItemString(ICD.find(code), "ICD_CHN_DESC");
    		result=code+"	"+desc;
    	}
    	return result;
    }
    /**
     * 取得主诊断行数据
     * @return
     */
    public int getMainDiag(){
    	int count=this.rowCount();
    	int mainRow=-1;
    	for(int i=0;i<count;i++){
    		StringTool d;
    		boolean isMain=StringTool.getBoolean(this.getItemString(i, "MAIN_DIAG_FLG"));
    		if(isMain){
    			return i;
    		}
    	}
    	return -1;
    }
    /**
     * 判断给入行号的诊断是否传染病
     * @param row
     * @return
     */
    public boolean isContagion(int row){
    	String sql="SELECT CHLR_FLG FROM SYS_DIAGNOSIS WHERE ICD_CODE='#'";
    	if(row<0){
    		return false;
    	}
    	String icdCode=this.getItemString(row, "ICD_CODE");
    	if(icdCode==null){
    		return false;
    	}
    	if(icdCode.trim().length()<1){
    		return false;
    	}
    	sql=sql.replaceFirst("#", icdCode);
    	//System.out.println("sql="+sql);
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	//System.out.println("result="+result);
    	if(result.getErrCode()!=0){
    		return false;
    	}
    	//System.out.println("chlr_flg="+result.getBoolean("CHLR_FLG",0));
    	return result.getBoolean("CHLR_FLG",0);
    }
    /**
     * 保存时将诊断中的传染病诊断变成TParm集合
     * @return
     */
    public TParm getContagionParm(){
    	TParm result=new TParm();
    	int count=this.rowCount();
    	if(count<1){
    		return result;
    	}

    	return result;
    }
    /**
     * 根据给入ICD_CODE得到主诊断注记
     * @param icdCode
     * @return
     */
    public boolean isMainFlg(String icdCode){
    	if(StringUtil.isNullString(icdCode)){
    		System.out.println("isMainFlg para is null");
    		return false;
    	}
    	String sql=GET_MAIN_FLG.replaceFirst("#", icdCode);
//    	System.out.println("sql="+sql);
    	TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getErrCode()!=0){
    		System.out.println("isMainFlg.err"+parm.getErrText());
    		return false;
    	}
//    	System.out.println("parm=="+parm);
    	return parm.getBoolean("MAIN_DIAG_FLG",0);
    }
    /**
     * 取得症候注记
     * @param icdCode
     * @return
     */
    public boolean isSyndromFlg(String icdCode){
    	if(StringUtil.isNullString(icdCode)){
    		return true;
    	}
    	TParm parm=new TParm(TJDODBTool.getInstance().select(GET_MAIN_FLG.replaceFirst("#", icdCode)));
    	if(parm.getErrCode()!=0){
    		System.out.println("isMainFlg.err"+parm.getErrText());
    		return true;
    	}
    	return parm.getBoolean("SYNDROME_FLG",0);
    }
}
