package com.javahis.util;

import jdo.opd.OPDSysParmTool;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;

import org.apache.commons.lang.time.DurationFormatUtils;

import com.dongyang.jdo.TDataStore;
import java.text.DecimalFormat;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OdiUtil {
    private static OdiUtil instanceObject;
    public OdiUtil(){
   }
   public static synchronized OdiUtil getInstance(){
       if (instanceObject == null) {
           instanceObject = new OdiUtil();
       }
       return instanceObject;
   }
   /**
    * 根据病患生日和传入的截至日期，计算病人年龄并根据是否为儿童以不同的形式显示年龄，如是儿童则显示X岁X月X日，如成人则显示x岁
    * @param odo
    * @return String 界面显示的年龄
    */
   @SuppressWarnings("deprecation")
   public static String showAge(Timestamp birth, Timestamp sysdate) {
       String age = "";
       String[] res;
      // res = StringTool.CountAgeByTimestamp(birth, sysdate);
     //modified by lx  计算精确年龄
       String times=DurationFormatUtils.formatPeriod(birth.getTime(), sysdate.getTime(),
		"y-M-d-H-m-s");
       res=times.split("-");
       
//       for (String temp : res) {
//           //System.out.println("temp" + temp);
//       }
       //add by wukai on 20160805   添加对日期的判断  不跨月和年的年龄要 日要减一
       boolean b ;
       if(sysdate.getMonth() == birth.getMonth() && sysdate.getYear() == birth.getYear() && sysdate.getDay() != birth.getDay()) {
    	   b = true;
       } else {
    	   b = false;
       }
       if (OPDSysParmTool.getInstance().isChild(birth, sysdate)) {//wanglong modify 20150119
    	   if(b){
    		   age = res[0] + "岁" + res[1] + "月" + (Integer.valueOf(res[2])-1) + "日";
    	   }
    	   else{
    	   	   age = res[0] + "岁" + res[1] + "月" + res[2] + "日";
    	   }
       }
       else {
           age = (Integer.parseInt(res[0])==0?1:res[0]) + "岁";
       }
//       System.out.println("age" + age);
       return age;
   }
   /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

   /**
    * 检查证照代码
    * @param userId String
    * @param lcsClassCode String
    * @return boolean
    */
   public boolean checkLcsClassCode(String userId,String lcsClassCode){
       boolean falg = false;
       //拿到使用者的证照列表
       TParm parm = new TParm(this.getDBTool().select("SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"+userId+"' AND SYSDATE BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
       int rowCount = parm.getCount("LCS_CLASS_CODE");
       for(int i=0;i<rowCount;i++){
           if(lcsClassCode.equals(parm.getValue("LCS_CLASS_CODE",i))){
               return true;
           }
       }
       return falg;
   }
   String code="";
   String icdCode="";
   /**
    * 拿到SYS_FEE简称
    * @param orderCode String
    * @return String
    */
   public synchronized String getSysOrderDesc(String orderCode) {
       TDataStore d = com.dongyang.manager.TIOM_Database.getLocalTable(
           "SYS_FEE");
       this.code = orderCode;
       d.filterObject(this, "filter");
       if (d.rowCount() == 0)
           return "";
       return ""+d.getItemData(0, "ORDER_DESC");
   }
   /**
    * 过滤方法
    * @param parm TParm
    * @param row int
    * @return boolean
    */
   public boolean filter(TParm parm, int row) {
       return parm.getValue("ORDER_CODE", row).equals(this.code);
   }
   /**
    * 过滤诊断
    * @param parm TParm
    * @param row int
    * @return boolean
    */
   public boolean filterDiag(TParm parm,int row){
       return parm.getValue("ICD_CODE", row).equals(this.icdCode);
   }
   /**
    * 拿到SYS_FEE简称
    * @param orderCode String
    * @return String
    */
   public synchronized TParm getSysFeeOrder(String orderCode) {
       TParm actionParm = new TParm(TJDODBTool.getInstance().select("SELECT * FROM SYS_FEE WHERE ORDER_CODE='" + orderCode + "'"));
       return actionParm.getRow(0);
   }
   
   public synchronized TParm getSysFeeOrderr(String orderCode,String deptOrDr,String deptordrCode,String rxType,String seqNo) {
	   String sql = "SELECT * FROM SYS_FEE A,OPD_COMORDER B "+
					"WHERE A.ORDER_CODE = B.ORDER_CODE "+
					"AND B.ORDER_CODE='" + orderCode + "' "+
					"AND B.DEPT_OR_DR = '"+ deptOrDr + "' "+
					"AND B.DEPTORDR_CODE = '"+ deptordrCode + "' "+
					"AND B.RX_TYPE = '"+ rxType + "' "+
					"AND B.SEQ_NO = '"+ seqNo+ "' ";
	   
       TParm actionParm = new TParm(TJDODBTool.getInstance().select(sql));
       return actionParm.getRow(0);
   }
   /**
    * 拿到SYS_FEE和临床路径药品简称
    * @param orderCode String
    * @return String
    * @author yuml
    */
   public synchronized TParm getSysFeeAndclp(String orderCode,String cr,String so) {
       TParm actionParm = new TParm(TJDODBTool.getInstance().select("SELECT A.*,B.NOTE AS DR_NOTE,B.DOSE AS MEDI_QTY,B.FREQ_CODE ,B.ROUT_CODE AS ROUTE_CODE FROM SYS_FEE A, CLP_PACK B  " +
        			" where A.ORDER_CODE = B.ORDER_CODE  AND A.ORDER_CODE = '"+orderCode+"'"+
        	" AND B.CLNCPATH_CODE='"+cr+"'"+" AND B.SCHD_CODE='"+so+"'"));
       return actionParm.getRow(0);
   }
   /**
    * 拿到SYS_FEE和临床路径药品简称
    * @param orderCode String
    * @param cr String
    * @param so String
    * @param cc String
    * @param osn String
    * @return String
    * @author xiongwg20150429
    */
   public synchronized TParm getSysFeeAndclp(String orderCode,String cr,String so,String cc,String osn,String orderType) {
		TParm actionParm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT A.*,B.NOTE AS DR_NOTE,B.DOSE AS MEDI_QTY,B.FREQ_CODE ,B.ROUT_CODE AS ROUTE_CODE FROM SYS_FEE A, CLP_PACK B  "
										+ " WHERE A.ORDER_CODE = B.ORDER_CODE  AND A.ORDER_CODE = '"
										+ orderCode
										+ "'"
										+ " AND B.CLNCPATH_CODE='"
										+ cr
										+ "'"
										+ " AND B.SCHD_CODE='"
										+ so
										+ "'"
										+ " AND B.CHKTYPE_CODE='"
										+ cc
										+ "'"
										+ " AND B.ORDER_TYPE='"
										+ orderType
										+ "'"
										+ " AND B.ORDER_SEQ_NO='" + osn + "'"));
       return actionParm.getRow(0);
   }
   /**
    * 拿到诊断对象
    * @param diagCode String
    * @return TParm
    */
   public synchronized TParm getDiagNosis(String diagCode){
       TDataStore d = com.dongyang.manager.TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
       this.icdCode = diagCode;
       d.filterObject(this,"filterDiag");
       if (d.rowCount() == 0)
           return null;
       return d.getRowParm(0);
   }
   /**
    * 得到待煎包总数
    * @param rfNum int
    * @param freqCode String
    * @return int
    */
   public int getPACKAGE_AMT(int rfNum,String freqCode){
       int count = 0;
       TParm parm = new TParm(this.getDBTool().select("SELECT FREQ_UNIT_48 FROM SYS_PHAFREQ WHERE FREQ_CODE = '"+freqCode+"'"));
       String freqUnit = parm.getValue("FREQ_UNIT_48",0);
       int strCount = freqUnit.length();
       for(int i=0;i<strCount;i++){
           if(String.valueOf(freqUnit.charAt(i)).equals("Y")){
               count++;
           }
       }
       return rfNum*count;
   }
   /**
     * 拿到字典信息
     * @param groupId String
     * @param id String
     * @return String
     */
    public String getDictionary(String groupId,String id){
        String result="";
        TParm parm = new TParm(this.getDBTool().select("SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"+groupId+"' AND ID='"+id+"'"));
        result = parm.getValue("CHN_DESC",0);
        return result;
    }
    /**
     * 拿到科室
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode){
        TParm parm = new TParm(this.getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'"));
        return parm.getValue("DEPT_CHN_DESC",0);
    }
    /**
     * 拿到病区
     * @param stationCode String
     * @return String
     */
    public String getStationDesc(String stationCode){
        TParm parm = new TParm(this.getDBTool().select("SELECT STATION_CODE,STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"+stationCode+"'"));
        return parm.getValue("STATION_DESC",0);
    }
    /**
     * 拿到病区
     * @param stationCode String
     * @return String
     */
    public String getStationDescEnd(String stationCode){
        TParm parm = new TParm(this.getDBTool().select("SELECT STATION_CODE,ENG_DESC FROM SYS_STATION WHERE STATION_CODE='"+stationCode+"'"));
        return parm.getValue("ENG_DESC",0);
    }

    public static void main(String[] args) {
        DecimalFormat df = new DecimalFormat("#############0.00");
//        System.out.println(""+df.format(2920.995));
//        System.out.println(""+df.format(72.613));
        StringBuffer ori = new StringBuffer("2920.9950000000003");
        StringBuffer falg = new StringBuffer();
        int index = ori.indexOf(".");
//        System.out.println("index"+index);
        if (index > 0){
            String str[] = ori.toString().split("\\.");
            if (str[1].length() <= 2) {
                System.out.println("" + ori);
                return;
            }
           ori.deleteCharAt(index);
//           System.out.println("ori" + ori);
           ori.delete(index + 2, ori.length());
//           System.out.println("ori=" + ori);
           falg.append(ori.toString().substring(0, index) + "." +
                       ori.toString().substring(index, ori.toString().length()));
//           System.out.println("falg"+falg);
        }
    }

}
