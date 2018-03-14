package jdo.odo;

import java.sql.Timestamp;

/**
*
* <p>Title: 病患存储对象</p>
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
public class PatInfo extends StoreBase{


	/**
     * 病案号
     */
    private String mrNo;

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
     * 得到SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT * FROM SYS_PATINFO WHERE MR_NO='" + getMrNo() + "' ";
    }
    /**
     * 插入数据
     * @param row int
     * @return int
     */
    public int insertRow(int row){
//        int newRow = super.insertRow(row);
//        if(newRow==-1)
//            return -1;
        //设置门急住别
//        setItem(0,"CTZ1_CODE",this.getCtz1Code());
//        setItem(0,"CTZ2_CODE",this.getCtz2Code());
//        setItem(0,"CTZ3_CODE",this.getCtz3Code());
//        setItem(0,"ADM_STATUS",this.getAdmStatus());
//        setItem(0,"REPORT_STATUS",this.getReportStatus());
//        setItem(0,"SEE_DR_FLG",this.getSeeDrFlg());
        return 0;
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



}
