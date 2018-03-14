package jdo.odo;

import java.sql.Timestamp;

/**
 *
 * <p>Title: 主诉客诉存储对象</p>
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
public class Subjrec extends StoreBase{
    /**
     * 门急住别
     */
    private String admType;
    /**
     * 医师
     */
    private String drCode;
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
     * 得到SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT * FROM OPD_SUBJREC WHERE CASE_NO = '" + getCaseNo() + "'";
    }
    /**
     * 插入数据
     * @param row int
     * @return int
     */
    public int insertRow(int row){
        int newRow = super.insertRow(row);
        if(newRow==-1){
//        	//System.out.println("insert subjuect failed");
        	return -1;
        }
            
        //设置门急住别
        
        setItem(newRow,"ADM_TYPE",getAdmType());
        setItem(newRow,"DR_CODE",getDrCode());
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
}
