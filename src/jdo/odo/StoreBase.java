package jdo.odo;

import java.sql.Timestamp;

import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 *
 * <p>Title: 数据存储基础类</p>
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
public class StoreBase extends TDataStore {
	/**
     * 就诊号
     */
    private String mrNo;//yanjing 20131205
    /**
     * 就诊号
     */
    private String caseNo;
    /**
     * 数据库工具
     */
    private TJDODBTool dbTool = new TJDODBTool();
    /**
     * 构造器
     */
    public StoreBase(){
    }
    //==========yanjing 20131205 添加病案号
    /**
     * 得到病案号
     * @return String
     */
    public String getMrNo(){
        return this.mrNo;
    }
    /**
     * 设置病案号
     * @param caseNo String
     */
    public void setMrNo(String mrNo){	
        this.mrNo = mrNo;
    }
  //==========yanjing 20131205 添加病案号
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
        this.caseNo = caseNo;
    }
    /**
     * 得到SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "";
    }
    /**
     * 得到数据库对象
     * @return TJDODBTool
     */
    public TJDODBTool getDbTool(){
    	return this.dbTool;
    }
    /**
     * 查询
     * @return boolean
     */
    public boolean onQuery(){
        if(!setSQL(getQuerySQL()))
            return false;
        if(retrieve()==-1)
            return false;
        return true;
    }
    /**
     * 检测保存
     * @return boolean
     */
    public boolean checkSave()
    {
        return true;
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

    /**
     * 插入数据
     * @param row int
     * @return int
     */
    public int insertRow(int row){
        int newRow = super.insertRow(row);
        if(newRow==-1)
            return -1;
        setItem(newRow,"CASE_NO",getCaseNo());
        setItem(newRow,"MR_NO",getMrNo());//YANJING 20131205
        setActive(newRow,false);
        return newRow;
    }
   
    public boolean deleteRow(int row){
    	super.deleteRow(row);
    	return true;
    }
}
