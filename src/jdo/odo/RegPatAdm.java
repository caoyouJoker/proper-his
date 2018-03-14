package jdo.odo;

import java.sql.Timestamp;

/**
*
* <p>Title: 挂号存储对象</p>
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
public class RegPatAdm extends StoreBase{

	/**
     * 病案号
     */
    private String mrNo;
    /**
     * 就诊号
     */
    private String caseNo;
    /**
     * 身份1
     */
    private String ctz1Code;
    /**
     * 身份2
     */
    private String ctz2Code;
    /**
     * 身份3
     */
    private String ctz3Code;
    /**
     * 就诊状态
     */
    private String admStatus;
    /**
     * 报告状态
     */
    private String reportStatus;
    /**
     * 是否看诊
     */
    private String seeDrFlg;
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
     * 设置身份1
     * @param ctz1Code String
     */
    public void setCtz1Code(String ctz1Code)
    {
        this.ctz1Code = ctz1Code;
    }
    /**
     * 得到身份1
     * @return ctz1Code String
     */
    public String getCtz1Code()
    {
        return ctz1Code;
    }
    /**
     * 设置身份2
     * @param ctz2Code String
     */
    public void setCtz2Code(String ctz2Code)
    {
        this.ctz2Code = ctz2Code;
    }
    /**
     * 得到身份2
     * @return ctz2Code String
     */
    public String getCtz2Code()
    {
        return ctz2Code;
    }
    /**
     * 设置身份3
     * @param ctz3Code String
     */
    public void setCtz3Code(String ctz3Code)
    {
        this.ctz3Code = ctz3Code;
    }
    /**
     * 得到身份3
     * @return ctz3Code String
     */
    public String getCtz3Code()
    {
        return ctz3Code;
    }
    /**
     * 设置就诊状态
     * @param admStatus String
     */
    public void setAdmStatus(String admStatus){
    	this.admStatus=admStatus;
    }
    /**
     * 得到就诊状态
     * @return admStatus String
     */
    public String getAdmStatus(){
    	return this.admStatus;
    }
    /**
     * 设置报告状态
     * @param reportStatus String
     */
    public void setReportStatus(String reportStatus){
    	this.reportStatus=reportStatus;
    }
    /**
     * 得到报告状态
     * @return reportStatus String
     */
    public String getReportStatus(){
    	return this.reportStatus;
    }
    /**
     * 设置是否看诊
     * @param seeDrFlg String
     */
    public void setSeeDrFlg(String seeDrFlg){
    	this.seeDrFlg=seeDrFlg;
    }
    /**
     * 得到是否看诊
     * @return seeDrFlg String
     */
    public String getSeeDrFlg (){
    	return this.seeDrFlg;
    }
    /**
     * 得到SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT * FROM REG_PATADM WHERE CASE_NO='" + getCaseNo() + "' ORDER BY CASE_NO";
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
