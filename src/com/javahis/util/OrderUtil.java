package com.javahis.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Calendar;
import jdo.odi.OdiMainTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;

/**
 * 
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 * 
 *
 * @author WangM
 * 
 * @version 1.0
 */
public class OrderUtil {
    private static OrderUtil instanceObject;
    /**
     * 取得SYS_ORDER_CAT1數據
     */
    private static final String GET_DEAL_SYSTEM_SQL =
            "SELECT * FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE='#'";
    public OrderUtil() {
    }

    public static synchronized OrderUtil getInstance() {
        if (instanceObject == null) {
            instanceObject = new OrderUtil();
        }
        return instanceObject;
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
    public boolean checkLcsClassCode(String userId, String lcsClassCode) {
        boolean falg = false;
        //拿到使用者的证照列表
        TParm parm = new TParm(this.getDBTool().select(
                "SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '" +
                userId + "' AND TRUNC(SYSDATE) BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
        int rowCount = parm.getCount("LCS_CLASS_CODE");
        for (int i = 0; i < rowCount; i++) {
            if (lcsClassCode.equals(parm.getValue("LCS_CLASS_CODE", i))) {
                return true;
            }
        }
        return falg;
    }
    /**
     * 检查证照代码
     * @param userId String
     * @param lcsClassCode String
     * @return boolean
     */
    public boolean checkLcsClassCode(TParm parm,String userId, String lcsClassCode){
    	boolean falg = false; 
    	int rowCount = parm.getCount("LCS_CLASS_CODE");
    	for (int i = 0; i < rowCount; i++) {
             if (lcsClassCode.equals(parm.getValue("LCS_CLASS_CODE", i))) {
                 return true;
             }
         }
         return falg;
    }
    /**
     * 抗生素天数
     * @param ctrlDrugClass String
     * @return boolean
     */
    public int checkAntibioticDay(String antibioticNo) {
//      System.out.println("ctrlDrugClass"+antibioticNo);
        int day = -1;
        TParm action = new TParm(this.getDBTool().select(
                "SELECT TAKE_DAYS FROM SYS_ANTIBIOTIC WHERE ANTIBIOTIC_CODE='" +
                antibioticNo + "'"));
//      System.out.println("ctrlDrugClassaction"+action);
        if (action.getInt("TAKE_DAYS", 0) != 0) {
            day = action.getInt("TAKE_DAYS", 0);
        }
        return day;
    }

    /**
     * 判断临时频次
     * @param freqCode String
     * @return boolean
     */
    public boolean isSTFreq(String freqCode) {
        boolean falg = false;
        TParm action = new TParm(this.getDBTool().select(
                "SELECT STAT_FLG FROM SYS_PHAFREQ WHERE FREQ_CODE = '" +
                freqCode + "'"));
        if (action.getCount() > 0) {
            falg = action.getBoolean("STAT_FLG", 0);
        }
        return falg;
    }

    /**
     * 拿到转换率
     * @param OrderCode String
     * @return TParm
     */
    public TParm getPhaTransUnit(String orderCode) {
        TParm result = new TParm();
        result = new TParm(this.getDBTool().select("SELECT DOSAGE_QTY,DOSAGE_UNIT,MEDI_QTY,MEDI_UNIT FROM PHA_TRANSUNIT WHERE ORDER_CODE='" +
                orderCode + "'"));
//      System.out.println("转换率:"+result);
        return result;
    }

    /**
     * 拿到药品单价
     * @param effDate String
     * @param orderCode String
     * @return double
     */
    public double getOrderOwnPrice(String effDate, String orderCode) {
        double ownPrice = 0.0;
        TParm result = new TParm();
        result = new TParm(this.getDBTool().select(
                "SELECT OWN_PRICE FROM SYS_FEE_HISTORY WHERE ORDER_CODE='" +
                orderCode + "' AND TO_DATE('" + effDate + "','YYYYMMDDHH24MISS') BETWEEN TO_DATE(START_DATE,'YYYYMMDDHH24MISS') AND TO_DATE(END_DATE,'YYYYMMDDHH24MISS')"));
//      System.out.println("价格返回值:"+result);
        ownPrice = result.getDouble("OWN_PRICE", 0);
        return ownPrice;
    }

    /**
     * 得到集合医嘱细项价格
     * @param parm TParm
     * @param type String
     * @param exDeptCode String
     * @return TParm
     */
    public double getOrderSetList(String orderCode) {
        double ownPrice = 0.0;
        TParm result = new TParm();
        result = new TParm(this.getDBTool().select("SELECT OWN_PRICE " +
                " FROM SYS_ORDERSETDETAIL A,SYS_FEE B WHERE A.ORDERSET_CODE='" +
                orderCode + "' AND A.ORDER_CODE=B.ORDER_CODE"));
        int rowCount = result.getCount("OWN_PRICE");
//       System.out.println("个数:"+rowCount);
//       System.out.println("返回值:"+result);
        for (int i = 0; i < rowCount; i++) {
            ownPrice += result.getDouble("OWN_PRICE", i);
        }
        return ownPrice;
    }

    /**
     * 得到折扣比率
     * @param ctzCode String
     * @return double
     */
    public double getOwnRate(String ctzCode, String orderCode) {
        double ownRate = 1;
        TParm action = new TParm(this.getDBTool().select(
                "SELECT CHARGE_HOSP_CODE FROM SYS_FEE WHERE ORDER_CODE='" +
                orderCode + "'"));
        TParm actionParm = new TParm(this.getDBTool().select(
                "SELECT DISCOUNT_RATE FROM SYS_CHARGE_DETAIL WHERE CTZ_CODE='" +
                ctzCode + "' AND CHARGE_HOSP_CODE='" +
                action.getValue("CHARGE_HOSP_CODE", 0) + "'"));
        if (actionParm.getDouble("DISCOUNT_RATE", 0) == 0)
            return ownRate;
        else
            ownRate = actionParm.getDouble("DISCOUNT_RATE", 0);
        return ownRate;
    }

    /**
     * 是否是医保病人
     * @param caseNo String
     * @return boolean
     */
    public boolean isNhiPat(String caseNo) {
        boolean falg = false;
        TParm result = new TParm();
        result = new TParm(this.getDBTool().select(
                "SELECT CTZ1_CODE FROM ADM_INP WHERE CASE_NO='" + caseNo + "'"));
        String ctzCode = result.getValue("CTZ1_CODE", 0);
        result = new TParm(this.getDBTool().select(
                "SELECT NHI_CTZ_FLG FROM SYS_CTZ WHERE CTZ_CODE='" + ctzCode +
                "' AND MAIN_CTZ_FLG='Y'"));
        if (result.getBoolean("NHI_CTZ_FLG", 0))
            falg = true;
        return falg;
    }

    /**
     * 判断是否是抗生素
     * @param parm TParm
     * @return boolean
     */
    public boolean checkKssPhaQty(TParm parm) {
        //是否是管制药品
//    	System.out.println("ANTIBIOTIC_CODE"+parm.getValue("ANTIBIOTIC_CODE"));
        if (parm.getValue("ANTIBIOTIC_CODE").length() != 0) {
            double mediQty = getPhaTransUnit(parm.
                                             getValue("ORDER_CODE")).getDouble(
                    "MEDI_QTY", 0);
//            System.out.println("得到转换率数量" + mediQty);
            double onlyMediQty = parm.getDouble("MEDI_QTY");
//            System.out.println("当前的值:" + onlyMediQty);
            
            if (onlyMediQty > mediQty) {
                    //if (messageBox("提示信息", "已超过此抗生素类药品的用量,是否继续开立?", this.YES_NO_OPTION) != 0)
                return false;
            }
        }
        return true;
    }

    /**
     * 判断给定医嘱的抗生素等级对应的默认量是否<=传入的量
     * @param parm TParm
     * @return boolean true:可以继续开立,false:超过默认量
     */
    public static boolean checkKssPhaQty(String ctrlDrugCode, String orderCode,
                                         double mediQty) {
        if (StringUtil.isNullString(ctrlDrugCode)) {
            return true;
        }
        double sysQty = OrderUtil.getInstance().getPhaTransUnit(orderCode).
                        getDouble("MEDI_QTY", 0);
//        System.out.println("得到转换率数量" + mediQty);
//        System.out.println("当前的值:" + sysQty);
        //是否是管制药品
        return sysQty <= mediQty;
    }

    /**
     * 返回给入的ORDER_CODE的抗生素代码
     * @param orderCode
     * @return
     */
    public String getAntiBioCode(String orderCode) {
        String antiBio = "";
        if (StringUtil.isNullString(orderCode))
            return antiBio;
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = OdiMainTool.getInstance().queryPhaBase(parm);
//    	System.out.println("RESULT=============="+result);
        return result.getValue("ANTIBIOTIC_CODE", 0);
    }

    /**
     * 返回申请列表
     * @param ds TDataStore
     * @param type String
     * @return TParm
     */
    public TParm getOrderPasEMR(TDataStore ds, String type) {
//        System.out.println("返回申请列表");
//        ds.showDebug();
        TParm result = new TParm();
        String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
        int rowCount = 0;
        if ("ODI".equals(type)) {
            int newRow[] = ds.getNewRows(buff);
            for (int i : newRow) {
                if (!ds.isActive(i, buff))
                    continue;
                TParm temp = ds.getRowParm(i, buff);
                if (temp.getValue("MR_CODE").length() == 0)
                    continue;
                if (temp.getValue("ORDERSET_CODE").length() != 0 &&
                    !temp.getBoolean("SETMAIN_FLG"))
                    continue;
                result.addData("ORDER_DATE", temp.getData("EFF_DATE"));
                result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                result.addData("MR_CODE", temp.getData("MR_CODE"));
                result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                rowCount++;
            }
        }
        if ("ODO".equals(type)) {
            int newRow[] = ds.getNewRows(buff);
            for (int i : newRow) {
                if (!ds.isActive(i, buff))
                    continue;
                TParm temp = ds.getRowParm(i, buff);
                if (temp.getValue("MR_CODE").length() == 0)
                    continue;
                if (temp.getValue("ORDERSET_CODE").length() != 0 &&
                    !temp.getBoolean("SETMAIN_FLG"))
                    continue;
                result.addData("ORDER_DATE", temp.getData("ORDER_DATE"));
                result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                result.addData("MR_CODE", temp.getData("MR_CODE"));
                result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                rowCount++;
            }
        }
        if ("HRM".equals(type)) {
            int newRow[] = ds.getNewRows(buff);
            for (int i : newRow) {
                if (!ds.isActive(i, buff))
                    continue;
                TParm temp = ds.getRowParm(i, buff);
                if (temp.getValue("MR_CODE").length() == 0)
                    continue;
                if (temp.getValue("ORDERSET_CODE").length() != 0 &&
                    !temp.getBoolean("SETMAIN_FLG"))
                    continue;
                result.addData("ORDER_DATE", temp.getData("ORDER_DATE"));
                result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                result.addData("MR_CODE", temp.getData("MR_CODE"));
                result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                rowCount++;
            }
        }
        if ("ODI_PHA".equals(type)) {//====pangben 2013-7-30 药品抗菌药物获得数据，弹出申请单，住院医生站界面操作
       	 int newRow[] = ds.getNewRows(buff);
         for (int i : newRow) {
             if (!ds.isActive(i, buff))
                  continue;
                  TParm temp = ds.getRowParm(i, buff);
                  String linkNo = temp.getData("LINK_NO").toString();
                if(temp.getData("LINKMAIN_FLG").toString().equals("Y")){//连嘱的主项，for循环校验所有的连嘱写入pha_anti表
            	boolean antiFlg = false;//false:该主项对应的细项中不含有抗菌药物
            	for(int j : newRow){ 
            		for(int m : newRow){//循环校验细项是否含有抗菌药物(包括连组号相同的主项)
            			TParm meTemp = ds.getRowParm(m, buff);
            			String mLinkNo = meTemp.getData("LINK_NO").toString();
            			if(linkNo.equals(mLinkNo)&&(!"".equals(meTemp.getData("ANTIBIOTIC_CODE").toString())
            					&&!meTemp.getData("ANTIBIOTIC_CODE").toString().equals(null))){
            				antiFlg = true;//细项中含有抗菌药物标记
            			}               			
            		}
            		if(antiFlg){//细项中含有抗菌药物标记，将该主项及细项均写入pha_anti表
            			TParm reTemp = ds.getRowParm(j, buff);
                		String LlinkNo = reTemp.getData("LINK_NO").toString();
                		 String dcDate = reTemp.getValue("DC_DATE");
                		 String effDate = reTemp.getValue("EFF_DATE");
//                		System.out.println("000+++9999 is AA is :"+dcDate);
                		if(LlinkNo.equals(linkNo)){
                			long takeDays = 0;
                			String mrCode = "";
                			String antiCode = "";
                			TParm orderParm = new TParm(this.getDBTool().select(
                                    "SELECT A.ANTIBIOTIC_CODE,A.ORDER_CODE,B.MR_CODE,A.SPECIFICATION,B.TAKE_DAYS FROM PHA_BASE A,SYS_ANTIBIOTIC B " +
                                    "WHERE A.ANTIBIOTIC_CODE=B.ANTIBIOTIC_CODE AND A.ORDER_CODE='" +
                                    reTemp.getData("ORDER_CODE") + "' AND A.ANTIBIOTIC_CODE IS NOT NULL "));
                            if (orderParm.getValue("MR_CODE",0).length() != 0){
                            	mrCode = orderParm.getValue("MR_CODE", 0);
                            }
                            //查询药嘱是否为特殊类抗菌药物，20131023 yanjing
                            if("".equals(dcDate)||dcDate.equals(null)){
                            	takeDays = orderParm.getInt("TAKE_DAYS",0);
                			}else{
                				takeDays = this.diffDays(dcDate, effDate);
                			}
                			
                            if(orderParm.getCount()>0){
                            	antiCode = orderParm.getValue("ANTIBIOTIC_CODE", 0);
                            }
                			result.addData("TAKE_DAYS", takeDays);
                			result.addData("ANTIBIOTIC_CODE", antiCode);//抗菌素代码，非抗菌药物为空
                            result.addData("ORDER_DATE", reTemp.getData("ORDER_DATE"));
                            result.addData("LINKMAIN_FLG", reTemp.getData("LINKMAIN_FLG"));
                            result.addData("LINK_NO", reTemp.getData("LINK_NO"));
                            result.addData("ORDER_CODE", reTemp.getData("ORDER_CODE"));
                            result.addData("ORDER_DESC", reTemp.getData("ORDER_DESC"));
                            result.addData("MR_CODE", mrCode);
                            result.addData("OPTITEM_CODE", reTemp.getData("OPTITEM_CODE"));
                            result.addData("REQUEST_NO", reTemp.getData("REQUEST_NO"));
                            result.addData("SPECIFICATION", reTemp.getData("SPECIFICATION"));//规格
                            result.addData("MEDI_UNIT", reTemp.getData("MEDI_UNIT"));//开药单位
                            result.addData("MEDI_QTY", reTemp.getData("MEDI_QTY"));//开药量
                            result.addData("FREQ_CODE", reTemp.getData("FREQ_CODE"));//频次
                            result.addData("ROUTE_CODE", reTemp.getData("ROUTE_CODE"));//用法
                            result.addData("DC_DATE", reTemp.getData("DC_DATE"));//停用时间
                            rowCount++;
                		}
            		}
            	}
            }else if((linkNo.equals(null)||"".equals(linkNo))){//将非连嘱的抗菌药物写入pha_anti表中
            	TParm orderParm = new TParm(this.getDBTool().select(
                        "SELECT A.ANTIBIOTIC_CODE,A.ORDER_CODE,B.MR_CODE,A.SPECIFICATION,B.TAKE_DAYS FROM PHA_BASE A,SYS_ANTIBIOTIC B " +
                        "WHERE A.ANTIBIOTIC_CODE=B.ANTIBIOTIC_CODE AND A.ORDER_CODE='" +
                        temp.getData("ORDER_CODE") + "' AND A.ANTIBIOTIC_CODE IS NOT NULL "));
                if (orderParm.getCount() <= 0)
                    continue;
                if (orderParm.getValue("MR_CODE",0).length() == 0)
                    continue;
                //查询药嘱是否为特殊类抗菌药物，20131023 yanjing
                String dcDate = temp.getValue("DC_DATE");
       		    String effDate = temp.getValue("EFF_DATE");
       		    String antiCode = "";
       		    long takeDays = 0;
                if("".equals(dcDate)||dcDate.equals(null)){
                	if(orderParm.getCount()>0){
                    	takeDays = orderParm.getInt("TAKE_DAYS",0);
                    	antiCode = orderParm.getValue("ANTIBIOTIC_CODE", 0);
                    }
                }else{
                	   takeDays = this.diffDays(dcDate, effDate);
                     if(orderParm.getCount()>0){
//                     	takeDays = orderParm.getInt("TAKE_DAYS",0);
                     	antiCode = orderParm.getValue("ANTIBIOTIC_CODE", 0);
                     }
                }
                result.addData("ANTI_FLG", "Y");//抗菌药物标记
                result.addData("ANTIBIOTIC_CODE", antiCode);//抗菌素代码，非抗菌药物为空
                result.addData("TAKE_DAYS", takeDays);
                result.addData("ORDER_DATE", temp.getData("ORDER_DATE"));
                result.addData("LINKMAIN_FLG", temp.getData("LINKMAIN_FLG"));
                result.addData("LINK_NO", temp.getData("LINK_NO"));
                result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                result.addData("MR_CODE", orderParm.getData("MR_CODE",0));
                result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                result.addData("SPECIFICATION", orderParm.getData("SPECIFICATION",0));//规格
                result.addData("MEDI_UNIT", temp.getData("MEDI_UNIT"));//开药单位
                result.addData("MEDI_QTY", temp.getData("MEDI_QTY"));//开药量
                result.addData("FREQ_CODE", temp.getData("FREQ_CODE"));//频次
                result.addData("ROUTE_CODE", temp.getData("ROUTE_CODE"));//用法
                result.addData("DC_DATE", temp.getData("DC_DATE"));//停用时间
                rowCount++;
            	
            }
            }
	}
        result.setData("ACTION", "COUNT", rowCount);
        return result;
    }
    /**
     * 累计抗菌药品数量
     * @param caseNo
     * @return
     * =======pangben 2013-9-10
     */
   /* private TParm sumPhaAntibiotic(String caseNo,Timestamp sysDate1){
    	String sysDate = StringTool.getString(sysDate1, "yyyyMMddHHmmss");
		String sql="SELECT COUNT(CASE_NO) AS COUNT FROM ODI_ORDER A,PHA_BASE B  WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CAT1_TYPE='PHA' AND CASE_NO='"
			+caseNo+"' AND B.ANTIBIOTIC_CODE IS NOT NULL  AND A.RX_KIND = 'UD' AND A.DC_DATE>TO_DATE('"+ sysDate +"','YYYYMMDDHH24MISS')";
		TParm orderParm = new TParm(this.getDBTool().select(sql));
		return orderParm;
		
    }*/
    /**
     * 计算pha_anti表中抗菌药物的个数
     */
    private int sumAntibioticPha(TParm parm){
//		TParm orderParm = new TParm();
    	int count = 0;
		if(parm.getInt("ACTION", "COUNT")>0){
			for(int i = 0;i<parm.getInt("ACTION", "COUNT");i++){
				String sql = "SELECT ANTIBIOTIC_CODE FROM PHA_BASE WHERE ORDER_CODE = '"+parm.getValue("ORDER_CODE", i)+"' ";
				TParm antiParm = new TParm(this.getDBTool().select(sql));
				if(!"".equals(antiParm.getValue("ANTIBIOTIC_CODE", 0))&&
						!antiParm.getValue("ANTIBIOTIC_CODE", 0).equals(null)){//kangjun
					count++;
				}
			}
			
		}
		return count;
    }
    /**
     * MODIFY caoyong 20140224
     */
    private TParm sumPhaAntibiotic(String caseNo,Timestamp sysDate1){
	String sysDate = StringTool.getString(sysDate1, "yyyyMMddHHmmss");
	String sql="SELECT DISTINCT A.ORDER_CODE FROM ODI_ORDER A,PHA_BASE B  WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CAT1_TYPE='PHA' AND CASE_NO='"
		+caseNo+"' AND B.ANTIBIOTIC_CODE IS NOT NULL  AND A.RX_KIND = 'UD' AND A.DC_DATE>TO_DATE('"+ sysDate +"','YYYYMMDDHH24MISS')";
	TParm orderParm = new TParm(this.getDBTool().select(sql));
	return orderParm;
	
}
    
//    /**
//     * 特殊类抗菌素校验
//     * yanjing 20131021
//     * return result
//     */
//    public TParm checkPhaAnti(String caseNo){
//    	TParm result = new TParm();
//    	int takeDays = 0;
//        String anti_sql = "SELECT C.TAKE_DAYS FROM ODI_ORDER A,PHA_BASE B,SYS_ANTIBIOTIC C WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CAT1_TYPE='PHA' AND CASE_NO='"
//			+caseNo+"' AND B.ANTIBIOTIC_CODE = '03' AND C.ANTIBIOTIC_CODE = B.ANTIBIOTIC_CODE" ;
////        		"AND B.ORDER_CODE = '"+temp.getData("ORDER_CODE")+"' ";
//        System.out.println("查询sql：：："+anti_sql);
//        TParm antiParm = new TParm(this.getDBTool().select(anti_sql));
//        boolean flg = false;
//        if(antiParm.getCount()>0){
////        	takeDays = antiParm.getInt("TAKE_DAYS");
//        	for(int i = 0; i<antiParm.getCount();i++){
//        		takeDays = antiParm.getInt("TAKE_DAYS",i);
//        		if(takeDays == 1){
//        			result.setErr(-1,"含有特殊类抗菌药物，填写申请单");
//    				flg = true;
//        		}
//        	}
//        }
//        result.addData("TAKE_DAYS", takeDays);
//        return result;
//		
//    }
    /**
     * 返回全部医嘱申请列表
     * @param ds TDataStore
     * @param type String
     * @return TParm
     */
    public TParm getOrderPasEMRAll(TDataStore ds, String type) {
    	//System.out.println("========getOrderPasEMRAll==========");
        TParm result = new TParm();
        String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
        int rowCount = ds.rowCountFilter();
        int rowCounts = 0;
        if ("ODI".equals(type)) {
            for (int i = rowCount-1; i >= 0; i--) {
                if (!ds.isActive(i, buff))
                    continue;
                TParm temp = ds.getRowParm(i, buff);
                if (temp.getValue("MR_CODE").length() == 0)
                    continue;
//                if(temp.getValue("ORDERSET_CODE").length()!=0&&!temp.getBoolean("SETMAIN_FLG"))
//                    continue;
//                if(temp.getValue("MED_APPLY_NO")==null||temp.getValue("MED_APPLY_NO").length()==0)
//                    continue;
                if (temp.getBoolean("HIDE_FLG"))
                    continue;
              //============xueyf modify 20120306 start
                //查询有没有FILE_NO
               /**System.out.println(
                        "SELECT FILE_NO FROM ODI_ORDER WHERE CASE_NO='" +
                        temp.getData("CASE_NO") + "' AND CAT1_TYPE='RIS' AND ORDER_NO='" +
                        temp.getData("ORDER_NO") + "' AND ORDER_SEQ='" +
                        temp.getData("ORDER_SEQ") + "'");**/
                //System.out.println("======temp========"+temp);
                
                TParm fileNoParm = new TParm(this.getDBTool().select(
                        "SELECT FILE_NO FROM ODI_ORDER WHERE CASE_NO='" +
                        temp.getData("CASE_NO") + "' AND CAT1_TYPE='RIS' AND ORDER_NO='" + //============xueyf modify 20120306 检验医嘱不显示
                        temp.getData("ORDER_NO") + "' AND ORDER_SEQ='" +
                        temp.getData("ORDER_SEQ") + "'"));
                //System.out.println("========fileNoParm=========="+fileNoParm);
                if (fileNoParm.getCount() <= 0)
                    continue;
              //============xueyf modify 20620301 stop
                result.addData("ORDER_DATE", temp.getData("EFF_DATE"));
                result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                result.addData("ORDER_ENG_DESC", temp.getData("ORDER_ENG_DESC"));
                result.addData("MR_CODE", temp.getData("MR_CODE"));
                result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                result.addData("URGENT_FLG", temp.getData("URGENT_FLG")); //============pangben modify 20110706 添加显示急诊标记
                result.addData("MED_APPLY_NO", temp.getData("MED_APPLY_NO"));
            
                if (fileNoParm.getCount() <= 0) {
                    result.addData("FILE_NO", "0");
                } else {
                    result.addData("FILE_NO", fileNoParm.getValue("FILE_NO", 0));
                }
                result.addData("ORDER_NO", temp.getData("ORDER_NO"));
                result.addData("ORDER_SEQ", temp.getData("ORDER_SEQ"));
                rowCounts++;
            }
        }
        if ("ODO".equals(type)) {
        	 for (int i = rowCount-1; i >= 0; i--){
                if (!ds.isActive(i, buff))
                    continue;
                TParm temp = ds.getRowParm(i, buff);
                if (temp.getValue("MR_CODE").length() == 0)
                    continue;
//                if(temp.getValue("ORDERSET_CODE").length()!=0&&!temp.getBoolean("SETMAIN_FLG"))
//                    continue;
//                if(temp.getValue("MED_APPLY_NO")==null||temp.getValue("MED_APPLY_NO").length()==0)
//                    continue;
                if (temp.getBoolean("HIDE_FLG"))
                    continue;
                TParm countParm = new TParm(this.getDBTool().select(
                        "SELECT MED_APPLY_NO FROM OPD_ORDER WHERE CASE_NO='" +
                        temp.getValue("CASE_NO") + "' AND RX_NO='" +
                        temp.getValue("RX_NO") + "' AND CAT1_TYPE='RIS' AND SEQ_NO='" +
                        temp.getValue("SEQ_NO") + "'  "));
                /**System.out.println("ODO-------"+"SELECT MED_APPLY_NO FROM OPD_ORDER WHERE CASE_NO='" +
                        temp.getValue("CASE_NO") + "' AND RX_NO='" +
                        temp.getValue("RX_NO") + "' AND ORDER_CAT1_CODE='RIS' AND SEQ_NO='" +
                        temp.getValue("SEQ_NO") + "'  ");**/
                if (countParm.getCount() <= 0)
                    continue;
                result.addData("ORDER_DATE", temp.getData("ORDER_DATE"));
                result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                result.addData("ORDER_ENG_DESC", temp.getData("TRADE_ENG_DESC"));
                result.addData("MR_CODE", temp.getData("MR_CODE"));
                result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                result.addData("URGENT_FLG", temp.getData("URGENT_FLG")); //============pangben modify 20110706 添加显示急诊标记
                result.addData("MED_APPLY_NO", temp.getData("MED_APPLY_NO"));
                result.addData("DAY_OPE_CODE", temp.getData("DAY_OPE_CODE").equals("1") ? "介入手术" : "");//add by huangjw 20161230
                //查询有没有FILE_NO
                /**System.out.println("SQL===" +
                        "SELECT FILE_NO FROM OPD_ORDER WHERE CASE_NO='" +
                                   temp.getData("CASE_NO") + "' AND RX_NO='" +
                                   temp.getData("ORDER_NO") + "' AND SEQ_NO='" +
                                   temp.getData("SEQ_NO") + "'");**/
                TParm fileNoParm = new TParm(this.getDBTool().select(
                        "SELECT FILE_NO FROM OPD_ORDER WHERE CASE_NO='" +
                        temp.getData("CASE_NO") + "' AND RX_NO='" +
                        temp.getData("RX_NO") + "' AND CAT1_TYPE='RIS' AND SEQ_NO='" +
                        temp.getData("SEQ_NO") + "'"));
                /**System.out.println(
                        "SELECT FILE_NO FROM OPD_ORDER WHERE CASE_NO='" +
                        temp.getData("CASE_NO") + "' AND RX_NO='" +
                        temp.getData("ORDER_NO") + "' AND SEQ_NO='" +
                        temp.getData("SEQ_NO") + "'");
                System.out.println("fileNoParmfileNoParmfileNoParm" +
                                   fileNoParm);**/
                if (fileNoParm.getCount() <= 0) {
                    result.addData("FILE_NO", "0");
                } else {
                    result.addData("FILE_NO", fileNoParm.getValue("FILE_NO", 0));
                }
                result.addData("ORDER_NO", temp.getData("RX_NO"));
                result.addData("ORDER_SEQ", temp.getData("SEQ_NO"));
                rowCounts++;
            }
        }
        if ("EMG".equals(type)) {
            for (int i = 0; i < rowCount; i++) {
                if (!ds.isActive(i, buff))
                    continue;
                TParm temp = ds.getRowParm(i, buff);
                if (temp.getValue("MR_CODE").length() == 0)
                    continue;
//                if(temp.getValue("ORDERSET_CODE").length()!=0&&!temp.getBoolean("SETMAIN_FLG"))
//                    continue;
//                if(temp.getValue("MED_APPLY_NO")==null||temp.getValue("MED_APPLY_NO").length()==0)
//                    continue;
                if (temp.getBoolean("HIDE_FLG"))
                    continue;
                TParm countParm = new TParm(this.getDBTool().select(
                        "SELECT MED_APPLY_NO FROM OPD_ORDER WHERE CASE_NO='" +
                        temp.getValue("CASE_NO") + "' AND RX_NO='" +
                        temp.getValue("RX_NO") + "' AND CAT1_TYPE='RIS' AND SEQ_NO='" +
                        temp.getValue("SEQ_NO") + "'"));
                if (countParm.getCount() <= 0)
                    continue;
                result.addData("ORDER_DATE", temp.getData("ORDER_DATE"));
                result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                result.addData("ORDER_ENG_DESC", temp.getData("TRADE_ENG_DESC"));
                result.addData("MR_CODE", temp.getData("MR_CODE"));
                result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                result.addData("URGENT_FLG", temp.getData("URGENT_FLG")); //============pangben modify 20110706 添加显示急诊标记
                result.addData("MED_APPLY_NO", temp.getData("MED_APPLY_NO"));             
                result.addData("DAY_OPE_CODE", temp.getData("DAY_OPE_CODE").equals("1") ? "介入手术" : "");//add by huangjw 20161230
                //查询有没有FILE_NO
                TParm fileNoParm = new TParm(this.getDBTool().select(
                        "SELECT FILE_NO FROM OPD_ORDER WHERE CASE_NO='" +
                        temp.getData("CASE_NO") + "' AND RX_NO='" +
                        temp.getData("RX_NO") + "' AND SEQ_NO='" +
                        temp.getData("SEQ_NO") + "'"));
                if (fileNoParm.getCount() <= 0) {
                    result.addData("FILE_NO", "0");
                } else {
                    result.addData("FILE_NO", fileNoParm.getValue("FILE_NO", 0));
                }
                result.addData("ORDER_NO", temp.getData("RX_NO"));
                result.addData("ORDER_SEQ", temp.getData("SEQ_NO"));
                rowCounts++;
            }
        }
        if ("HRM".equals(type)) {
            for (int i = 0; i < rowCount; i++) {
                if (!ds.isActive(i, buff))
                    continue;
                TParm temp = ds.getRowParm(i, buff);
                if (temp.getValue("MR_CODE").length() == 0)
                    continue;
//                if(temp.getValue("ORDERSET_CODE").length()!=0&&!temp.getBoolean("SETMAIN_FLG"))
//                    continue;
//                if(temp.getValue("MED_APPLY_NO")==null||temp.getValue("MED_APPLY_NO").length()==0)
//                    continue;
                if (temp.getBoolean("HIDE_FLG"))
                    continue;
                TParm countParm = new TParm(this.getDBTool().select(
                        "SELECT MED_APPLY_NO FROM HRM_ORDER WHERE CASE_NO='" +
                        temp.getValue("CASE_NO") + "' AND CAT1_TYPE='RIS' AND SEQ_NO='" +
                        temp.getValue("SEQ_NO") + "'"));
                if (countParm.getCount() <= 0)
                    continue;
                result.addData("ORDER_DATE", temp.getData("ORDER_DATE"));
                result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                result.addData("ORDER_ENG_DESC", temp.getData("TRADE_ENG_DESC"));
                result.addData("MR_CODE", temp.getData("MR_CODE"));
                result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                result.addData("URGENT_FLG", temp.getData("URGENT_FLG")); //============pangben modify 20110706 添加显示急诊标记
                result.addData("MED_APPLY_NO", temp.getData("MED_APPLY_NO"));
               
                //查询有没有FILE_NO
                TParm fileNoParm = new TParm(this.getDBTool().select(
                        "SELECT FILE_NO FROM HRM_ORDER WHERE CASE_NO='" +
                        temp.getData("CASE_NO") + "' AND SEQ_NO='" +
                        temp.getData("SEQ_NO") + "'"));
                if (fileNoParm.getCount() <= 0) {
                    result.addData("FILE_NO", "0");
                } else {
                    result.addData("FILE_NO", fileNoParm.getValue("FILE_NO", 0));
                }
                result.addData("ORDER_NO", temp.getData("CASE_NO"));
                result.addData("ORDER_SEQ", temp.getData("SEQ_NO"));
                rowCounts++;
            }
        }
        if ("ODI_PHA".equals(type)) {//====pangben 2013-7-30 药品抗菌药物获得数据，弹出申请单，住院医生站界面操作
        	  for (int i = rowCount-1; i >= 0; i--) {
                  if (!ds.isActive(i, buff))
                      continue;
                  TParm temp = ds.getRowParm(i, buff);
                  if (temp.getBoolean("HIDE_FLG"))
                      continue;
                  TParm fileNoParm = new TParm(this.getDBTool().select(
                          "SELECT A.FILE_NO,C.MR_CODE FROM ODI_ORDER A ,PHA_BASE B,SYS_ANTIBIOTIC C  WHERE A.ORDER_CODE=B.ORDER_CODE AND " +
                          "B.ANTIBIOTIC_CODE=C.ANTIBIOTIC_CODE AND A.CASE_NO='" +
                          temp.getData("CASE_NO") + "' AND A.ORDER_NO='" +
                          temp.getData("ORDER_NO") + "' AND A.ORDER_SEQ='" +
                          temp.getData("ORDER_SEQ") + "' AND A.CAT1_TYPE='PHA' AND B.ANTIBIOTIC_CODE IS NOT NULL "));
                  if (fileNoParm.getCount() <= 0)
                      continue;
                  if (fileNoParm.getValue("MR_CODE",0).length() == 0)
                      continue;
                //============xueyf modify 20620301 stop
                  result.addData("ORDER_DATE", temp.getData("EFF_DATE"));
                  result.addData("ORDER_CODE", temp.getData("ORDER_CODE"));
                  result.addData("ORDER_DESC", temp.getData("ORDER_DESC"));
                  result.addData("ORDER_ENG_DESC", temp.getData("ORDER_ENG_DESC"));
                  result.addData("MR_CODE", fileNoParm.getData("MR_CODE",0));
                  result.addData("OPTITEM_CODE", temp.getData("OPTITEM_CODE"));
                  result.addData("REQUEST_NO", temp.getData("REQUEST_NO"));
                  result.addData("URGENT_FLG", temp.getData("URGENT_FLG")); //============pangben modify 20110706 添加显示急诊标记
                  result.addData("MED_APPLY_NO", temp.getData("MED_APPLY_NO"));
              
                  if (fileNoParm.getCount() <= 0) {
                      result.addData("FILE_NO", "0");
                  } else {
                      result.addData("FILE_NO", fileNoParm.getValue("FILE_NO", 0));
                  }
                  result.addData("ORDER_NO", temp.getData("ORDER_NO"));
                  result.addData("ORDER_SEQ", temp.getData("ORDER_SEQ"));
                  rowCounts++;
              }
		}
        result.setData("ACTION", "COUNT", rowCounts);
        //System.out.println("申请单===" + result);
        return result;
    }

    /**
     * 查询检体名称
     * @param iptCode String
     * @return String
     */
    public String queryOptItem(String iptCode) {
        TParm parm = new TParm(this.getDBTool().select(
                "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_OPTITEM' AND ID = '" +
                iptCode + "'"));
        return parm.getValue("CHN_DESC", 0);
    }

    /**
     * 拿到诊断类别
     * @param mainDiag String
     * @return String
     */
    public String queryDiagType(String mainDiag) {
        TParm parm = new TParm(this.getDBTool().select(
                "SELECT ICD_TYPE FROM SYS_DIAGNOSIS WHERE ICD_CODE='" +
                mainDiag + "'"));
        return parm.getValue("ICD_TYPE", 0);
    }

    /**
     * 判断医嘱是否已经审核
     * @param parm TParm
     * @return boolean
     */
    public boolean checkOrderNSCheck(TParm parm, boolean ncCheckFlg) {
        boolean falg = false;
        if (ncCheckFlg) {
            TParm result = new TParm(this.getDBTool().select(
                    "SELECT NS_CHECK_DATE FROM ODI_ORDER WHERE CASE_NO='" +
                    parm.getValue("CASE_NO") + "' AND ORDER_NO='" +
                    parm.getValue("ORDER_NO") + "' AND ORDER_SEQ='" +
                    parm.getValue("ORDER_SEQ") + "'"));
            if (result.getErrCode() < 0)
                falg = true;
            if (result.getTimestamp("NS_CHECK_DATE", 0) != null)
                falg = true;
        } else {
            String startDttm = StringTool.getString(parm.getTimestamp(
                    "START_DTTM"), "yyyyMMddHHmmss");
//            System.out.println("startDttm"+startDttm);
            TParm resultExe = new TParm(this.getDBTool().select(
                    "SELECT NS_EXEC_DATE FROM ODI_DSPNM WHERE CASE_NO='" +
                    parm.getValue("CASE_NO") + "' AND ORDER_NO='" +
                    parm.getValue("ORDER_NO") + "' AND ORDER_SEQ='" +
                    parm.getValue("ORDER_SEQ") + "' AND START_DTTM='" +
                    startDttm + "'"));
            if (resultExe.getErrCode() < 0)
                falg = true;
            if (resultExe.getTimestamp("NS_EXEC_DATE", 0) != null)
                falg = true;
        }
        return falg;
    }

    /**
     * 拿到单位
     * @param unitCode String
     * @return String
     */
    public String getUnit(String unitCode) {
        TParm parm = new TParm(this.getDBTool().select(
                "SELECT UNIT_CODE,UNIT_CHN_DESC FROM SYS_UNIT WHERE UNIT_CODE='" +
                unitCode + "'"));
        return parm.getValue("UNIT_CHN_DESC", 0);
    }

    /**
     * 得到频次
     * @param freqCode String
     * @return String
     */
    public String getFreq(String freqCode) {
        TParm parm = new TParm(this.getDBTool().select(
                "SELECT CYCLE,FREQ_TIMES FROM SYS_PHAFREQ WHERE FREQ_CODE='" +
                freqCode + "'"));
        return parm.getInt("FREQ_TIMES", 0) + "/" + parm.getInt("CYCLE", 0);
    }

    /**
     * 拿到用法
     * @param routeCode String
     * @return String
     */
    public String getRoute(String routeCode) {
        TParm parm = new TParm(this.getDBTool().select(
                "SELECT ROUTE_CODE,ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE='" +
                routeCode + "'"));
        return parm.getValue("ROUTE_CHN_DESC", 0);
    }

    /**
     * 得到合理用药药品
     * @param caseNo String
     * @return List
     */
    public List getPassOrder(String caseNo) {
        List result = new ArrayList();
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String starDate = StringTool.getString(sysDate, "yyyyMMdd") + "000000";
        String sql = "SELECT * FROM ODI_ORDER WHERE CASE_NO='" + caseNo + "' AND RX_KIND IN ('ST','DS','IG') AND CAT1_TYPE='PHA' AND  DC_DATE IS NULL AND ORDER_DATE BETWEEN TO_DATE('" +
                     starDate + "','YYYYMMDDHH24MISS') AND SYSDATE " +
                     "UNION " +
                     " SELECT * FROM ODI_ORDER WHERE CASE_NO='" + caseNo +
                "' AND RX_KIND='UD' AND CAT1_TYPE='PHA' AND  DC_DATE IS NULL";
        TParm parm = new TParm(this.getDBTool().select(sql));
        if (parm.getErrCode() != 0) {
            return result;
        }
        int countP = parm.getCount();
        String[] orderInfo;
        for (int i = 0; i < countP; i++) {
            TParm temp = parm.getRow(i);
            if (StringUtil.isNullString(temp.getValue("ORDER_DESC")))
                continue;
            orderInfo = this.getOrderInfo(temp);
            result.add(orderInfo);
        }
        return result;
    }

    /**
     * 得到合理用药信息
     * @param parm TParm
     * @return String[]
     */
    public String[] getOrderInfo(TParm parm) {
        String[] result = new String[12];
        //药品唯一码：RX_NO+FILL0(SEQ,3)+ORDER_CODE
        result[0] = parm.getValue("RX_NO") +
                    StringTool.fill0(parm.getValue("SEQ_NO") + "", 3) +
                    parm.getValue("ORDER_CODE");
        result[1] = parm.getValue("ORDER_CODE");
        result[2] = parm.getValue("ORDER_DESC");
        result[3] = parm.getValue("MEDI_QTY");
        result[4] = getUnit(parm.getValue("MEDI_UNIT"));
        result[5] = getFreq(parm.getValue("FREQ_CODE"));
        String date = StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                           "yyyy-MM-dd");
        result[6] = date;
        result[7] = date;
        result[8] = getRoute(parm.getValue("ROUTE_CODE"));
        result[9] = "1";
        String type = "UD".equals(parm.getValue("RX_KIND")) ? "0" : "1";
        result[10] = type;
        result[11] = Operator.getName();
        return result;
    }

    /**
     * 根據給入orderCat1Code得到deal_system
     * @param orderCat1Code
     * @return
     */
	public String getDealSystem(String orderCat1Code) {
		if (StringUtil.isNullString(orderCat1Code)) {
			return "";
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(
				GET_DEAL_SYSTEM_SQL.replace("#", orderCat1Code)));
		return result.getValue("DEAL_SYSTEM", 0);
	}
	/**
	 * 计算当前日期和最大日期相隔的天数
	 * @throws ParseException 
	 * 
	 */
	public long diffDays(String SDate, String MDate) {
		 long quot = 0;
		try{
//		 long quot = 0;
			SDate = SDate.substring(0, 10);
			MDate = MDate.substring(0, 10);
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		Date sysDate = ft.parse( SDate );
		   Date maxDate = ft.parse( MDate );
		   quot = sysDate.getTime() - maxDate.getTime();
		   quot = quot / 1000 / 60 / 60 / 24;
//		   return quot;
		   }catch (Exception e) {
				e.printStackTrace();
			}
		   return quot;
	}
	/**
	 * 抗菌素时间控制
	 * 
	 */
	public TParm getAntiTimeControl(String caseNo){
        TParm phaEmrParm = new TParm();//返回结果
        TParm result = new TParm();//查询结果
        boolean flg = false;//报错或者弹出申请单校验
		//查询医嘱表中存在最大结束时间和最小的开始时间
		String odiOrderSql="SELECT MAX(DC_DATE) AS DC_DATE,MIN(ORDER_DATE) AS ORDER_DATE FROM ODI_ORDER WHERE CASE_NO='"+caseNo+
		"' AND ANTIBIOTIC_CODE IS NOT NULL AND RX_KIND ='UD'";
		TParm odiOrderParm=new TParm(TJDODBTool.getInstance().select(odiOrderSql));
		 long diffDays = 100;//当pha_anti表中不存在该病人信息时，和大于31天的流程相同
		if (odiOrderParm.getCount("DC_DATE")<=0) {//没有使用抗菌药品
			phaEmrParm.setData("NODE_FLG", "N");
		}else{
		    String maxDate = odiOrderParm.getValue("DC_DATE",0).toString();//最大结束日期
			if (null != maxDate && maxDate.length() > 0) {
				String sysDate = SystemTool.getInstance().getDate().toString();// 获取系统当前时间
				diffDays = this.diffDays(sysDate, maxDate);// 比较最大结束时间与当前时间比较
				if (diffDays < 31) {// 可能出现14天使用的情况
					String minDate = odiOrderParm.getValue("ORDER_DATE", 0)
							.toString();// 最小开始日期
					if (null != minDate && minDate.length() > 0) {
						diffDays = this.diffDays(sysDate, minDate);// 比较最大结束时间与当前时间比较
						if (diffDays >= 14) {// 超过使用14天的情况
							String sql = "SELECT SUM(DAYS) SUMDAY FROM (SELECT TRUNC(DC_DATE-ORDER_DATE) DAYS FROM ODI_ORDER WHERE CASE_NO='"
									+ caseNo
									+ "' AND ANTIBIOTIC_CODE IS NOT NULL AND RX_KIND ='UD')";// pangben
																								// 2013-12-23查询ODI_ORDER表里停用时间检验14天问题
							odiOrderParm = new TParm(TJDODBTool.getInstance()
									.select(sql));
							if (odiOrderParm.getInt("SUMDAY", 0) >= 14) {
								result.setErr(-1, "抗菌药品数量超过14天，填写申请单");
								flg = true;
								phaEmrParm.setData("PHA_USE_FLG", "N");// 医生没有使用状态
							}
						}
					}
					phaEmrParm.setData("NODE_FLG", "N");
				} else {
					phaEmrParm.setData("NODE_FLG", "Y");// 31天节点数据添加
				}
			} else {
				phaEmrParm.setData("NODE_FLG", "N");
			}
		}
	    phaEmrParm.setData("FLG", flg);
//	    phaEmrParm.addParm(result);
	    return phaEmrParm;
	}
	/**
	 * 抗菌素药品创建
	 * @return
	 */
	public TParm getPhaAntiCreate(TParm phaEmrParm,String caseNo,String mrNo,int index,TControl control) {
		Timestamp sysDate = SystemTool.getInstance().getDate();		
		TParm phaParm = sumPhaAntibiotic(caseNo,sysDate);
//		int count=this.getAntCount(phaEmrParm,phaParm);// 累计数据量 modify caoyong 20140224
		int newPhaEmrParm = sumAntibioticPha(phaEmrParm);
//		int count = phaParm.getInt("COUNT", 0)
//				+ phaEmrParm.getInt("ACTION", "COUNT");// 累计数据量
		int count=0;
		if (phaParm.getCount("ORDER_CODE")>0) {
			count = phaParm.getCount("ORDER_CODE")//pha_anti表中加入非抗菌药物时数量重整
		            + newPhaEmrParm;// 累计数据量
		}else{
			count=newPhaEmrParm;
		}
		//int count = phaParm.getInt("COUNT", 0)
			//	+ phaEmrParm.getInt("ACTION", "COUNT");// 累计数据量
		String pagIndex = String.valueOf(index);
		phaEmrParm.addData("INDEX", pagIndex);
//		phaEmrParm.addData("INDEX", index);
		phaEmrParm.setData("CASE_NO", caseNo);
		phaEmrParm.setData("MR_NO", mrNo);
		phaEmrParm.setData("OPT_USER", Operator.getID());
		phaEmrParm.setData("OPT_TERM", Operator.getIP());
//		System.out.println("phaEmrParm=====yeqina is ::"+phaEmrParm);
		boolean flg = false;//报错或者弹出申请单校验
		TParm result=new TParm();
		switch (index) {
		case 0://临时
			for (int i = 0; i < phaEmrParm.getCount("ORDER_CODE"); i++) {
				phaEmrParm.setData("OVERRIDE_FLG", i, "N");//越级审批标记
				phaEmrParm.setData("CHECK_FLG", i, "N");//是否审批标记
				if (null!=phaEmrParm.getValue("ROUTE_CODE",i)&&!phaEmrParm.getValue("ROUTE_CODE",i).equals("PS")) {
					//====查询该药品是否是皮试用药
					String  psPhaSql = "SELECT SKINTEST_FLG FROM PHA_BASE WHERE ORDER_CODE = '"+phaEmrParm.getValue("ORDER_CODE",i)+"' ";
					TParm psPharesult = new TParm(TJDODBTool.getInstance().select(psPhaSql));//查询长期医嘱是否开立	
					if ("Y".equals(psPharesult.getValue("SKINTEST_FLG", 0))) {
						//=====查询该人该药品是否做过皮试
						String psSql = "SELECT COUNT(A.CASE_NO) AS SUM  FROM ODI_ORDER A WHERE " +
								" A.CASE_NO = '"+caseNo+"' AND a.RX_KIND='ST' " +
								"AND A.ORDER_CODE='"+phaEmrParm.getValue("ORDER_CODE",i)+"'  ";
						TParm ps_result = new TParm(TJDODBTool.getInstance().select(psSql));
						if(ps_result.getInt("SUM",0)<=0){
							//pangben 20150602选择确认按钮将数据保存到PHA_ANTI表
							 if(control.messageBox("提示",phaEmrParm.getValue("ORDER_DESC", i)+"皮试的用法不正确，是否继续保存？", 2) != 0){
								 result.setErr(-1, "皮试的用法不正确");
								 return result;
		                     }
						}
					}
					
					String sql="SELECT A.CASE_NO,A.ORDER_DATE,B.ANTIBIOTIC_CODE FROM ODI_ORDER A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='"+caseNo+
					"' AND A.RX_KIND='UD' AND A.ORDER_CODE='"+phaEmrParm.getValue("ORDER_CODE",i)+"'";
					TParm ope_result = new TParm(TJDODBTool.getInstance().select(sql));//查询长期医嘱是否开立	
					if (ope_result.getCount()>0) {
						if (null==ope_result.getValue("ANTIBIOTIC_CODE",0)||
								ope_result.getValue("ANTIBIOTIC_CODE",0).length()<=0) {
							continue;
						}
						sql = "SELECT A.CASE_NO,B.ORDER_DESC,B.ANTIBIOTIC_CODE FROM ODI_ORDER A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='"
							+ caseNo
							+ "' AND A.RX_KIND='ST' AND A.ORDER_CODE='"
							+ phaEmrParm.getValue("ORDER_CODE",i)
							+ "' AND A.ROUTE_CODE<>'PS' AND A.ORDER_DATE>TO_DATE('"
							+ SystemTool.getInstance().getDateReplace(ope_result.getValue("ORDER_DATE",0), true)+"','YYYYMMDDHH24MISS')";
					}else{
						
						sql="SELECT A.CASE_NO,A.ORDER_DESC,B.ANTIBIOTIC_CODE FROM ODI_ORDER A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='"+caseNo+
						"' AND A.RX_KIND='ST' AND A.ORDER_CODE='"+phaEmrParm.getValue("ORDER_CODE",i)+"' AND A.ROUTE_CODE<>'PS' ";
					}
					ope_result = new TParm(TJDODBTool.getInstance().select(sql));//校验临时医嘱是否已经开立
					//如果长期医嘱已经开立临时医嘱允许开立一次
					if (ope_result.getCount()>0) {
						if (null==ope_result.getValue("ANTIBIOTIC_CODE",0)||
								ope_result.getValue("ANTIBIOTIC_CODE",0).length()<=0) {
							continue;
						}
						result.setErr(-1,ope_result.getValue("ORDER_DESC",0)+"药品已经开立，不可以操作");
						return result;
					}
				}
			}
			TParm result1 = TIOM_AppServer.executeAction(
					"action.pha.PHAAntiAction", "onSavePhaAntiOne", phaEmrParm);
			if(result1.getErrCode() < 0){
				result.setErr(-1,result1.getErrText());
				return result;
			}
			break;
		case 1://长期
			if (count >= 3) {// 数量超过三个处理方法
				result.setErr(-1,"抗菌药品数量超过三个，填写申请单");
				flg = true;
				phaEmrParm.setData("PHA_USE_FLG", "N");// 医生没有使用状态
			}else{
				TParm parm = new TParm();//接受时间控制方法传过来的值
				parm = this.getAntiTimeControl(caseNo);
				phaEmrParm.setData("FLG", parm.getBoolean("FLG"));
				phaEmrParm.setData("PHA_USE_FLG", parm.getValue("PHA_USE_FLG"));
				phaEmrParm.setData("NODE_FLG", parm.getValue("NODE_FLG"));
				flg = phaEmrParm.getBoolean("FLG");
				if(flg){
					result.setErr(-1,"抗菌药品数量超过14天，填写申请单");
				}else{
					TParm result2 = TIOM_AppServer.executeAction(
							"action.pha.PHAAntiAction", "onSavePhaAntiOne", phaEmrParm);
					if (result2.getErrCode() < 0) {
						result.setErr(-1,"保存抗菌药品失败");
						return result;
					}
				}
			}	
			break;
		default:
			//======donglt 2016-3-2 添加出院带药保存抗菌药品操作
			for (int i = 0; i < phaEmrParm.getCount("ORDER_CODE"); i++) {
				phaEmrParm.setData("OVERRIDE_FLG", i, "N");//越级审批标记
				phaEmrParm.setData("CHECK_FLG", i, "N");//是否审批标记
			}
			TParm result3 = TIOM_AppServer.executeAction(
					"action.pha.PHAAntiAction", "onSavePhaAntiOne", phaEmrParm);
			if (result3.getErrCode() < 0) {
				return result;
			}
			break;
			
		}
//		TParm result1 = TIOM_AppServer.executeAction(
//				"action.pha.PHAAntiAction", "onSavePhaAntiOne", phaEmrParm);
//		if (result1.getErrCode() < 0) {
//			result.setErr(-1,"保存抗菌药品失败");
//			return result;
//		}
		if (flg) {
			result.setData("MESSAGE","Y");
			//====pangben 2014-2-25 抗菌药品提示三条医嘱或14天条件也添加PHA_ANTI表逻辑
			TParm result3 = TIOM_AppServer.executeAction(
					"action.pha.PHAAntiAction", "onSavePhaAntiOne", phaEmrParm);
			if (result3.getErrCode() < 0) {
				result.setErr(-1,"保存抗菌药品失败");
			}
			return result;
		}
		return result;
	}
	/**
	 * add caoyong 20140225
	 * 联合
	 * 联合医嘱统计抗菌药数量
	 */
	public int getAntCount(TParm phaEmrParm,TParm phaParm ){
		
		List list =new ArrayList();
		List list1 =new ArrayList();
		//TParm result= new TParm();
		int cout=0;
		
		for(int i=0;i<phaParm.getCount();i++){
			list.add(phaParm.getValue("ORDER_CODE",i));
		}
		for(int j=0;j<phaEmrParm.getCount();j++){
			if(!list.contains(phaEmrParm.getValue("ORDER_CODE",j)))
				list.add(phaEmrParm.getValue("ORDER_CODE",j));
		}
		String phant="";
		Iterator iter=list.iterator();
		while(iter.hasNext()){
			TParm result=getAnt((String)iter.next());
			if(result.getCount()<=0){
				cout++;
			}else{
				phant=result.getValue("PHA_ANTIBIOTIC_NO",0);
				if(!list1.contains(phant))
				 list1.add(phant);
			}
		}
		return cout+list1.size();
	}
	/**
	 * add caoyong 20140225
	 * 联合医嘱统计抗菌药数量
	 */
  public TParm getAnt(String orderCode){
	  String sql="";
	  TParm result=new TParm();
	  if(!"".equals(orderCode)&&orderCode!=null){
	    sql="SELECT PHA_ANTIBIOTIC_NO FROM  PHA_ANTIBIOTIC_ORDER  WHERE ORDER_CODE='"+orderCode+"' ";
	  }
	    result = new TParm(TJDODBTool.getInstance().select(sql));
	   return result;
  }
  
 
	
}
