package com.javahis.ui.opd;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import javax.swing.SwingUtilities;

import jdo.odo.ODO;
import jdo.odo.OpdOrder;
import jdo.odo.OpdRxSheetTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWord;
import com.dongyang.util.StringTool;
import com.javahis.ui.emr.EMRTool;

/**
 *
 * <p>Title: 门诊医生工作站补打处方签</p>
 *
 * <p>Description:门诊医生工作站补打处方签控制类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui 20090526
 * @version 1.0
 */
public class OdoCaseSheetControl extends TControl {
    TParm parm;
    String rxNo = "090514000002";
    String caseNo = "090512000006";
    String mrNo = "000000000174";
    String deptCode = "20201";
    String icdCode = "";
    String icdDesc = "";
    Timestamp admDate;
    String rxType = "";
    String filter = "";
    boolean isFirstPrint = false;
    String patName = "";
    ODO odo;
    TTable table;
    TWord word;
    //记录保存的EMR名称
    private String EMRName = "";
    //记录CLASS_CODE
    private String classCode = "";
    private String subClassCode = "";
    public void onInit() {
        super.onInit();
        initParameter();
        if (caseNo == null || caseNo.trim().length() < 1) {
            this.messageBox("E0024");
            return;
        }
        initForm();
    }

    /**
     * 初始化参数
     */
    public void initParameter() {
        parm = (TParm)this.getParameter();
        if (parm == null)
            return;
        patName = parm.getValue("PAT_NAME");
        caseNo = parm.getValue("CASE_NO");
        mrNo = parm.getValue("MR_NO");
        deptCode = parm.getValue("DEPT_CODE");
        admDate = (Timestamp) parm.getData("ADM_DATE");
        icdCode = parm.getValue("ICD_CODE");
        icdDesc = parm.getValue("ICD_DESC");
        isFirstPrint = false;
        odo = (ODO) parm.getData("ODO");
        OpdOrder order = (OpdOrder) parm.getData("OPD_ORDER");
        filter = order.getFilter();
        order.setFilter("");
        order.filter();
        table = (TTable)this.getComponent("TABLE_RX");
        word = (TWord)this.getComponent("WORD");
    }

    /**
     * 初始化基础信息值
     */
    public void initForm() {
        this.setValue("NAME", patName);
        this.setValue("MR_NO", mrNo);
        this.setValue("ADM_DATE", admDate);
        this.setValue("ICD_CODE", icdCode);
        this.setValue("ICD_DESC", icdDesc);
        this.setValue("DEPT_CODE", deptCode);
        if ("en".equals(this.getLanguage()))
            this.setValue("DR_NAME", Operator.getEngName());
        else
            this.setValue("DR_NAME", Operator.getName());

    }

    /**
     * TABLE点击事件
     */
    public void onTableClick() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        //TParm parm = table.getParmValue();
        rxNo = table.getParmValue().getValue("RX_NO", row);
        if (rxNo == null || rxNo.trim().length() < 1) {
            this.messageBox("E0034");
            return;
        }
        TParm inParam = new TParm();
        String filterString = odo.getOpdOrder().getFilter();
        odo.getOpdOrder().setFilter("RX_NO='" + rxNo + "'");
        odo.getOpdOrder().filter();
        //System.out.println("=== OpdRxSheetTool begin date 1==="+new Date());
        inParam = OpdRxSheetTool.getInstance().getOrderPrintParm(deptCode,
                rxType, odo, rxNo, odo.getOpdOrder().getItemString(0, "PSY_FLG"));
        if (inParam == null || inParam.getErrCode() != 0) {
            this.messageBox("E0116");
            return;
        }
    	TParm data2 = new TParm();
		DecimalFormat df = new DecimalFormat("############0.00");
    	double pageAmt = 0.00 ;
        //System.out.println("=== OpdRxSheetTool stop date 1==="+new Date());
        int rxTypeInt = StringTool.getInt(rxType);
        String billType ="";
      //modify by pangben 20130418 修改折扣显示金额
		String sql = "SELECT  A.MED_APPLY_NO,A.ORDER_CODE,CASE WHEN A.BILL_FLG='Y' THEN '√' ELSE '' END AS BILL_FLG,  C.DEPT_CHN_DESC,F.AR_AMT, "
			    +" A.ORDER_DESC||CASE WHEN A.DR_NOTE IS NOT NULL THEN '/'||A.DR_NOTE ELSE '' END AS ORDER_DESC, A.MEDI_QTY,"
			    +" CASE WHEN A.URGENT_FLG='Y' THEN '√' ELSE '' END AS URGENT_FLG,B.DESCRIPTION,A.DISCOUNT_RATE,"
			    +" CASE WHEN A.DAY_OPE_CODE='1' THEN '介入手术' ELSE '' END AS DAY_OPE_CODE "
		//modify end
			    + " FROM  OPD_ORDER A,SYS_FEE B,SYS_DEPT C, (SELECT   RX_NO, ORDERSET_GROUP_NO, SUM (AR_AMT) AS AR_AMT, CASE_NO "
                + "FROM OPD_ORDER "
                + "WHERE CASE_NO = '"+caseNo+"' "
                + "AND RX_NO IN ("+inParam.getValue("RX_NO")+") "
                + "AND SETMAIN_FLG = 'N' "
                + "AND ORDERSET_GROUP_NO IN ( "
                + "  SELECT A.ORDERSET_GROUP_NO "
                + "    FROM OPD_ORDER A, SYS_FEE B, SYS_DEPT C "
                + "    WHERE A.CASE_NO = '"+caseNo+"' "
                + "      AND RX_NO IN ("+inParam.getValue("RX_NO")+") "
                + "       AND A.ORDER_CODE = B.ORDER_CODE "
                + "       AND C.DEPT_CODE = A.EXEC_DEPT_CODE "
                + "       AND A.CAT1_TYPE = 'LIS') "
                + " GROUP BY RX_NO, ORDERSET_GROUP_NO, CASE_NO) F"
				+ " WHERE A.CASE_NO='"+ caseNo+ "' AND A.RX_NO IN ("+ inParam.getValue("RX_NO")
				+ ") AND A.SETMAIN_FLG='Y' AND A.ORDER_CODE=B.ORDER_CODE "
                + "  AND A.CASE_NO = F.CASE_NO AND A.RX_NO = F.RX_NO AND A.ORDERSET_GROUP_NO = F.ORDERSET_GROUP_NO AND C.DEPT_CODE=A.EXEC_DEPT_CODE AND A.CAT1_TYPE='LIS'"
                + " ORDER BY A.SEQ_NO";
        switch (rxTypeInt) {
        case 1: //西药
        	
        	//add by huangtt 20170401 添加收费状态 
			inParam.setData("BILL_TYPE", "TEXT", OpdRxSheetTool.getInstance().getBillType(caseNo, inParam.getValue("RX_NO"), ""));
			
        	
        	//System.out.println("=== 显示 begin date 1==="+new Date());
        	//=============modify by lim begin 
        	String westsql = "  SELECT   CASE WHEN   OPD_ORDER.BILL_FLG='Y' THEN '√' ELSE '' END||'  '||OPD_ORDER.LINK_NO aa , "+
				           " CASE WHEN SYS_FEE.IS_REMARK = 'Y' THEN OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.ORDER_DESC  END bb , "+
				           " OPD_ORDER.SPECIFICATION cc, "+
				           " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '皮试' ELSE SYS_PHAROUTE.ROUTE_CHN_DESC  END dd,"+
				           " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '' ELSE RTRIM(RTRIM(TO_CHAR(OPD_ORDER.MEDI_QTY,'fm9999999990.000'),'0'),'.')||''||A.UNIT_CHN_DESC  END ee,"+
				           
				           " RPAD(SYS_PHAFREQ.FREQ_CHN_DESC||' ', 12, '　') || OPD_ORDER.TAKE_DAYS ff,"+
				           " CASE WHEN OPD_ORDER.DISPENSE_QTY<1 THEN TO_CHAR(OPD_ORDER.DISPENSE_QTY,'fm9999999990.0') ELSE "+
				           " TO_CHAR(OPD_ORDER.DISPENSE_QTY) END||''|| B.UNIT_CHN_DESC er,"+
				           //modify by wanglong 20121226
				           " CASE WHEN OPD_ORDER.RELEASE_FLG = 'Y' THEN '自备  '|| OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.DR_NOTE END gg ,OPD_ORDER.DOSAGE_QTY,OPD_ORDER.OWN_PRICE,OPD_ORDER.DISCOUNT_RATE "+
				           //modify end
				         " FROM   OPD_ORDER, SYS_PHAFREQ, SYS_PHAROUTE,SYS_UNIT A, SYS_UNIT B,SYS_FEE "+
				         " WHERE       CASE_NO = '"+this.caseNo+"'"+
				         "  AND RX_NO = '"+inParam.getValue("RX_NO")+"'"+
				         " and SYS_PHAROUTE.ROUTE_CODE(+) = OPD_ORDER.ROUTE_CODE "+
				         "  AND SYS_PHAFREQ.FREQ_CODE(+) = OPD_ORDER.FREQ_CODE "+
				         "  AND A.UNIT_CODE(+) =  OPD_ORDER.MEDI_UNIT "+
				         "  AND B.UNIT_CODE(+) =  OPD_ORDER.DISPENSE_UNIT "+
				         "  AND OPD_ORDER.ORDER_CODE = SYS_FEE.ORDER_CODE "+
				         " ORDER BY   LINK_NO, LINKMAIN_FLG DESC, SEQ_NO" ;
        	TParm westResult = new TParm(TJDODBTool.getInstance().select(westsql));
    		if(westResult.getErrCode()<0){
    			this.messageBox("E0001"); 
    			return ;
    		}
    		if(westResult.getCount()<0){
    			this.messageBox("没有处方签数据.") ;
    			return ; 
    		}  
    		
    		TParm westParm = new TParm() ;
    		double pageAmt2 = 0 ; 
    		DecimalFormat df2 = new DecimalFormat("############0.00");     		
    		for (int i = 0; i < westResult.getCount(); i++) {
    			westParm.addData("AA", westResult.getData("AA", i)) ;
    			westParm.addData("BB", westResult.getData("BB", i)) ;        	
    			westParm.addData("CC", westResult.getData("CC", i)) ;
    			westParm.addData("DD", westResult.getData("DD", i)) ;
    			westParm.addData("EE", westResult.getData("EE", i)) ;
    			westParm.addData("FF", westResult.getData("FF", i)) ; 
    			westParm.addData("ER", westResult.getData("ER", i)) ; 
    			westParm.addData("GG", westResult.getData("GG", i)) ; 
    			
				pageAmt2 += (StringTool.round(westResult.getDouble(
						"DOSAGE_QTY", i)
						* westResult.getDouble("OWN_PRICE", i)
						//add by wanglong 20121226
						* westResult.getDouble("DISCOUNT_RATE", i), 2));// ===zhangp 20120809
                        //add end
				if((i!=0 && (i+1)%5 == 0) || i == westResult.getCount()-1){
	    			westParm.addData("AA", "") ;
	    			westParm.addData("BB", "") ;        	
	    			westParm.addData("CC", "") ;
	    			westParm.addData("DD", "") ;
	    			westParm.addData("EE", "") ;
	    			westParm.addData("FF", "处方金额(￥):") ; 
	    			westParm.addData("ER", df2.format(pageAmt2)) ; 
	    			westParm.addData("GG", "") ;	
	    			pageAmt2=0 ;
				}
			}
    		westParm.setCount(westParm.getCount("AA")) ;
    		westParm.addData("SYSTEM", "COLUMNS", "AA"); 
    		westParm.addData("SYSTEM", "COLUMNS", "BB");        
    		westParm.addData("SYSTEM", "COLUMNS", "CC");
    		westParm.addData("SYSTEM", "COLUMNS", "DD");
    		westParm.addData("SYSTEM", "COLUMNS", "EE");
    		westParm.addData("SYSTEM", "COLUMNS", "FF");
    		westParm.addData("SYSTEM", "COLUMNS", "ER");
    		westParm.addData("SYSTEM", "COLUMNS", "GG");

    		inParam.setData("ORDER_TABLE", westParm.getData()) ; 	
        	//=============modify by lim end 
            word.setWordParameter(inParam);
            word.setPreview(true);
            word.setFileName("%ROOT%\\config\\prt\\OPD\\OpdOrderSheet.jhw");
            EMRName = "西药处方签_" + rxNo;
            this.classCode = "EMR030001";
            this.subClassCode = "EMR03000101";
            //System.out.println("=== 显示 end date 2==="+new Date());
            break;
        case 2:
        	//add by huangtt 20170401 添加收费状态
			inParam.setData("BILL_TYPE", "TEXT", OpdRxSheetTool.getInstance().getBillType(caseNo, rxNo, ""));
		
            word.setWordParameter(inParam);
            word.setPreview(true);
            word.setFileName("%ROOT%\\config\\prt\\OPD\\OpdDrugSheet.jhw");
            EMRName = "管制药品处方签_" + rxNo;
            this.classCode = "EMR030001";
            this.subClassCode = "EMR030001";
            break;
        case 3:
        	//add by huangtt 20170401 添加收费状态
			inParam.setData("BILL_TYPE", "TEXT", OpdRxSheetTool.getInstance().getBillType(caseNo, rxNo, ""));
		
            word.setWordParameter(inParam);
            word.setPreview(true);
            word.setFileName("%ROOT%\\config\\prt\\OPD\\OpdChnOrderSheet.jhw");
            EMRName = "中药处方签_" + rxNo;
            this.classCode = "EMR030002";
            this.subClassCode = "EMR03000201";
            break;
        case 4:
            //modify by liming 2012/02/23 begin
    		TParm dataParm = new TParm() ;
    		if( inParam.getValue("RX_NO") != null &&  inParam.getValue("RX_NO").trim().length() > 0){
                    sql = // modify by wanglong 20140402
                            "SELECT A.ORDER_CODE,A.DOSAGE_QTY,B.OWN_PRICE,CASE WHEN A.BILL_FLG='Y' THEN '√' ELSE '' END AS BILL_FLG,D.DEPT_CHN_DESC, "
                                    + "       A.ORDER_DESC||CASE WHEN A.SPECIFICATION IS NOT NULL THEN '/'||A.SPECIFICATION ELSE '' END AS ORDER_DESC,"
                                    + "       A.MEDI_QTY,CASE WHEN A.URGENT_FLG='Y' THEN '√' ELSE '' END AS URGENT_FLG,C.DESCRIPTION,"
                                    + "       CASE WHEN A.DISCOUNT_RATE=0 THEN 1 ELSE A.DISCOUNT_RATE END AS DISCOUNT_RATE,B.AR_AMT "
                                    + "  FROM OPD_ORDER A, ( "
                                    + "          SELECT MIN(A.SEQ_NO) AS SEQ_NO,A.CASE_NO,A.RX_NO,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,SUM(A.OWN_PRICE) OWN_PRICE,SUM(A.AR_AMT) AR_AMT "
                                    + "          FROM OPD_ORDER A "
                                    + "         WHERE A.CASE_NO = '@' "
                                    + "           AND A.RX_NO IN ('#') "
                                    + "           AND A.ORDERSET_CODE IS NOT NULL "
                                    + "        GROUP BY A.CASE_NO, A.RX_NO, A.ORDERSET_GROUP_NO, A.ORDERSET_CODE "
                                    + "        UNION "
                                    + "        SELECT A.SEQ_NO, A.CASE_NO, A.RX_NO, A.ORDERSET_GROUP_NO, A.ORDER_CODE AS ORDERSET_CODE, A.OWN_PRICE, A.AR_AMT "
                                    + "          FROM OPD_ORDER A "
                                    + "         WHERE A.CASE_NO = '@' "
                                    + "           AND A.RX_NO IN ('#') "
                                    + "           AND A.ORDERSET_CODE IS NULL) B,SYS_FEE C,SYS_DEPT D "
                                    + " WHERE A.CASE_NO = B.CASE_NO "
                                    + "   AND A.RX_NO = B.RX_NO "
                                    + "   AND A.SEQ_NO = B.SEQ_NO "
                                    + "   AND A.ORDER_CODE = B.ORDERSET_CODE "
                                    + "   AND A.ORDER_CODE = C.ORDER_CODE "
                                    + "   AND A.EXEC_DEPT_CODE = D.DEPT_CODE "
                                    + "ORDER BY A.SEQ_NO";
                    sql = sql.replaceAll("@", this.caseNo);
                    sql = sql.replaceAll("#", inParam.getValue("RX_NO"));
                    dataParm = new TParm(TJDODBTool.getInstance().select(sql));
    		}
    		if(dataParm.getErrCode()<0){
    			this.messageBox("E0001"); 
    			return ;
    		}
    		if(dataParm.getCount()<0){
    			this.messageBox("没有处置通知单数据.") ;
    			return ; 
    		}
    		
			 billType = "已收费";
			for (int i = 0; i < dataParm.getCount(); i++) {
				if (dataParm.getValue("BILL_FLG", i).length() == 0) {
					billType = "未收费";
					break;
				}
			}
	        inParam.setData("BILL_TYPE", "TEXT", billType); //add by huangtt 20170401处方签显示已收费未收费状态

    		TParm myParm = new TParm() ;
    		boolean flg1 = false ;
    		int blankRow1 = 0 ;
    		double pageAmt1 = 0 ; 
    		DecimalFormat df1 = new DecimalFormat("############0.00"); 

    		for (int i = 0; i < dataParm.getCount(); i++) {
    			String orderDesc = dataParm.getValue("ORDER_DESC", i) ;
    			if(orderDesc.length()<=29){
    				StringBuilder temp = new StringBuilder() ;
    				for (int j = 1; j <= 58 - orderDesc.length(); j++) {
						temp.append(" ") ;
					}
    				orderDesc = dataParm.getValue("ORDER_DESC", i)+temp.toString() ; 
    			} 
    			myParm.addData("BILL_FLG", dataParm.getData("BILL_FLG", i)) ;
    			myParm.addData("DEPT_CHN_DESC", dataParm.getData("DEPT_CHN_DESC", i)) ;        	
    			myParm.addData("ORDER_DESC", orderDesc) ;
    			myParm.addData("MEDI_QTY", dataParm.getData("MEDI_QTY", i)) ;
    			myParm.addData("URGENT_FLG", dataParm.getData("URGENT_FLG", i)) ;
    			myParm.addData("DESCRIPTION", dataParm.getData("DESCRIPTION", i)) ; 
            	//累计
    		    pageAmt1 += dataParm.getDouble("AR_AMT", i);//modify by wanglong 20140415
				pageAmt1 = StringTool.round(pageAmt1, 2);//add by wanglong 20121226
            	//TODO:“###########”处需要被每页计算的金额替代.用获得的数量*getEveryAmt(ORDERCODE)计算出每条记录的金额。
            	int num = i+blankRow1+1 ;//行数（i）+ 空白行(blankRow)+1
            	if(!flg1){//第一页
            		if(i == 4 || i == (dataParm.getCount() - 1)){
            			myParm.addData("BILL_FLG", "") ;
            			myParm.addData("DEPT_CHN_DESC", "") ;
            			myParm.addData("ORDER_DESC", "") ;
            			myParm.addData("MEDI_QTY", "") ;
            			myParm.addData("URGENT_FLG","处方金额:") ;
            			myParm.addData("DESCRIPTION",df1.format(pageAmt1)) ;
        	        	flg1 = true ;
        	        	blankRow1 ++ ;
        	        	pageAmt1 = 0 ;
            		}

            	}else{//其他页.//第5行显示金额
            		if(i == dataParm.getCount() - 1 ){
            			myParm.addData("BILL_FLG", "") ;
            			myParm.addData("DEPT_CHN_DESC", "") ;
            			myParm.addData("ORDER_DESC", "") ;
            			myParm.addData("MEDI_QTY", "") ;
            			myParm.addData("URGENT_FLG","处方金额:") ;
            			myParm.addData("DESCRIPTION",df1.format(pageAmt1)) ;
        	        	blankRow1 ++ ;
        	        	pageAmt1 = 0 ;
            		}else if(i != dataParm.getCount() - 1 && ((num % 6) + 1) == 6){
            			myParm.addData("BILL_FLG", "") ;
            			myParm.addData("DEPT_CHN_DESC", "") ;
            			myParm.addData("ORDER_DESC", "") ;
            			myParm.addData("MEDI_QTY", "") ;
            			myParm.addData("URGENT_FLG","处方金额:") ;
            			myParm.addData("DESCRIPTION",df1.format(pageAmt1)) ;
        	        	blankRow1 ++ ;
        	        	pageAmt1 = 0 ;
            		}
            	}    			
    		}
    		myParm.setCount(myParm.getCount("ORDER_DESC")) ;
    		myParm.addData("SYSTEM", "COLUMNS", "BILL_FLG"); 
    		myParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");        
    		myParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
    		myParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
    		myParm.addData("SYSTEM", "COLUMNS", "URGENT_FLG");
    		myParm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");	
    		inParam.setData("ORDER_TABLE", myParm.getData()) ; 	
    		//modify by liming 2012/02/23 end         	
            word.setWordParameter(inParam);
            word.setPreview(true);
            word.setFileName("%ROOT%\\config\\prt\\OPD\\OpdNewHandleSheet.jhw");
            EMRName = "处置通知单_" + inParam.getValue("RX_NO");
            this.classCode = "EMR040002";
            this.subClassCode = "EMR04000203";
            break;
        case 5:
    		//modify by wanglong 20121226
    		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    		if(result.getErrCode()<0){
    			return ;
    		}
    		if(result.getCount()<0 ){
    			return ;
    		}
    		
    		billType = "已收费";
			for (int i = 0; i < result.getCount(); i++) {
				if (result.getValue("BILL_FLG", i).length() == 0) {
					billType = "未收费";
					break;
				}
			}
	        inParam.setData("BILL_TYPE", "TEXT", billType); //add by huangtt 20170401处方签显示已收费未收费状态

    		
    		boolean flg = false ;
    		int blankRow = 0 ;
    		if(result.getCount() > 0){
    	        int pageCount = 11 ;
    	    	data2.addData("BILL_FLG", "") ;
    	    	data2.addData("DEPT_CHN_DESC","检验通知单") ;
    	    	data2.addData("ORDER_DESC", "") ;
    	    	data2.addData("MEDI_QTY", "") ;
    	    	data2.addData("URGENT_FLG", "") ;
    	    	data2.addData("DESCRIPTION", "") ;
    	    	data2.addData("MED_APPLY_NO", "") ;
    	        for (int i = 0; i < result.getCount(); i++) {
    	        	data2.addData("BILL_FLG", result.getData("BILL_FLG", i)) ;
    	        	data2.addData("DEPT_CHN_DESC", result.getData("DEPT_CHN_DESC", i)) ;
    	        	data2.addData("ORDER_DESC", result.getData("ORDER_DESC", i)) ;
    	        	data2.addData("MEDI_QTY", result.getData("MEDI_QTY", i)) ;
    	        	data2.addData("URGENT_FLG", result.getData("URGENT_FLG", i)) ;
    	        	data2.addData("DESCRIPTION", result.getData("DESCRIPTION", i)) ;
    	        	//data2.addData("MED_APPLY_NO", result.getData("MED_APPLY_NO", i)+"   "+result.getData("DAY_OPE_CODE",i)) ;
    	        	data2.addData("MED_APPLY_NO", result.getData("MED_APPLY_NO", i)) ;
    	        	//累计
//					pageAmt += (result.getDouble("MEDI_QTY", i)
//							* this.getEveryAmt(result.getValue("ORDER_CODE",i))
//							* result.getDouble("DISCOUNT_RATE", i));//modify by wanglong 20121226
					pageAmt += StringTool.round(result.getDouble("AR_AMT", i), 2);//add by wanglong 20121226
    	        	//TODO:“###########”处需要被每页计算的金额替代.用获得的数量*getEveryAmt(ORDERCODE)计算出每条记录的金额。
    	        	int num = i+blankRow+1+1 ;//行数（i）+ 空白行(blankRow)+第一行检验通知单(1)+1
    	        	if(!flg){//第一页
    	        		if(i == 8 || i == (result.getCount() - 1)){
    	    	        	data2.addData("BILL_FLG", "") ;
    	    	        	data2.addData("DEPT_CHN_DESC", "") ;
    	    	        	data2.addData("ORDER_DESC", "") ;
    	    	        	data2.addData("MEDI_QTY", "") ;
    	    	        	data2.addData("URGENT_FLG","") ;
    	    	        	data2.addData("DESCRIPTION","处方金额:") ;
    	    	        	data2.addData("MED_APPLY_NO", df.format(pageAmt)) ;
    	    	        	flg = true ;
    	    	        	blankRow ++ ;
    	    	        	pageAmt = 0 ;
    	        		}

    	        	}else{//其他页.//第11行显示金额
    	        		if(i == result.getCount() - 1 ){
    	    	        	data2.addData("BILL_FLG", "") ;
    	    	        	data2.addData("DEPT_CHN_DESC", "") ;
    	    	        	data2.addData("ORDER_DESC", "") ;
    	    	        	data2.addData("MEDI_QTY", "") ;
    	    	        	data2.addData("URGENT_FLG","") ;
    	    	        	data2.addData("DESCRIPTION","处方金额:") ;
    	    	        	data2.addData("MED_APPLY_NO", df.format(pageAmt)) ;
    	    	        	blankRow ++ ;
    	    	        	pageAmt = 0 ;
    	        		}else if(i != result.getCount() - 1 && ((num % 11) + 1) == 11){
    	    	        	data2.addData("BILL_FLG", "") ;
    	    	        	data2.addData("DEPT_CHN_DESC", "") ;
    	    	        	data2.addData("ORDER_DESC", "") ;
    	    	        	data2.addData("MEDI_QTY", "") ;
    	    	        	data2.addData("URGENT_FLG","") ;
    	    	        	data2.addData("DESCRIPTION","处方金额:") ;
    	    	        	data2.addData("MED_APPLY_NO", df.format(pageAmt)) ;
    	    	        	blankRow ++ ;
    	    	        	pageAmt = 0 ;
    	        		}
    	        	}
    			}
    	        int resultLen1 = result.getCount()+1+blankRow ;
    	        int len = (resultLen1<=pageCount)? (pageCount - resultLen1):((resultLen1%pageCount == 0) ? 0 : (((resultLen1/pageCount)+1)*pageCount-resultLen1)) ;

    	        for (int i = 1; i <=len; i++) {
    	        	data2.addData("BILL_FLG", "") ;
    	        	data2.addData("DEPT_CHN_DESC", "") ;
    	        	data2.addData("ORDER_DESC", "") ;
    	        	data2.addData("MEDI_QTY", "") ;
    	        	data2.addData("URGENT_FLG", "") ;
    	        	data2.addData("DESCRIPTION", "") ;
    	        	data2.addData("MED_APPLY_NO", "") ;
    			}
    		}
            
            data2.setCount(data2.getCount("ORDER_DESC")) ;
            data2.addData("SYSTEM", "COLUMNS", "BILL_FLG");
            data2.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
            data2.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            data2.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
            data2.addData("SYSTEM", "COLUMNS", "URGENT_FLG");
            data2.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
            data2.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");

            inParam.setData("ORDER_TABLE", data2.getData()) ;
          //modify by lim 2012/02/23 begin
            word.setWordParameter(inParam);
            word.setPreview(true);
            word.setFileName("%ROOT%\\config\\prt\\OPD\\OpdNewExaSheet.jhw");
            EMRName = "检验检查通知单_" + inParam.getValue("RX_NO");
            this.classCode = "EMR040001";
            this.subClassCode = "EMR04000141";
            break;
        case 6:
        	//modify by pangben 20130417
        	String sql2 = sql.replace("'LIS'", "'RIS'");
    		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
    		if(result2.getErrCode()<0){
    			return ;
    		}
    		if(result2.getCount()<0 ){
    			return ;
    		}
    		
    		billType = "已收费";
			for (int i = 0; i < result2.getCount(); i++) {
				if (result2.getValue("BILL_FLG", i).length() == 0) {
					billType = "未收费";
					break;
				}
			}
	        inParam.setData("BILL_TYPE", "TEXT", billType); //add by huangtt 20170401处方签显示已收费未收费状态

    		
    		 if(result2.getCount()>0){
             	data2.addData("BILL_FLG", "") ;
             	data2.addData("DEPT_CHN_DESC","检查通知单") ;
             	data2.addData("ORDER_DESC", "") ;
             	data2.addData("MEDI_QTY", "") ;
             	data2.addData("URGENT_FLG", "") ;
             	data2.addData("DESCRIPTION", "") ;
             	data2.addData("MED_APPLY_NO", "") ;
             }
         	blankRow = 0 ;
         	flg = false ;
             for(int i = 0; i < result2.getCount(); i++){
             	data2.addData("BILL_FLG", result2.getData("BILL_FLG", i)) ;
             	data2.addData("DEPT_CHN_DESC", result2.getData("DEPT_CHN_DESC", i)) ;
             	data2.addData("ORDER_DESC", result2.getData("ORDER_DESC", i)) ;
             	data2.addData("MEDI_QTY", result2.getData("MEDI_QTY", i)) ;
             	data2.addData("URGENT_FLG", result2.getData("URGENT_FLG", i)) ;
             	data2.addData("DESCRIPTION", result2.getData("DESCRIPTION", i)) ;
             	//data2.addData("MED_APPLY_NO", result2.getData("MED_APPLY_NO", i)+"   "+result2.getData("DAY_OPE_CODE",i)) ;
             	data2.addData("MED_APPLY_NO", result2.getData("MED_APPLY_NO", i)) ;
// 				pageAmt += (result2.getDouble("MEDI_QTY", i)
// 						* this.getEveryAmt(result2.getValue("ORDER_CODE", i)) 
// 						* result2.getDouble("DISCOUNT_RATE", i));//modify by wanglong 20121226
 				pageAmt += StringTool.round(result2.getDouble("AR_AMT", i), 2);//add by wanglong 20121226
             	int num = i+blankRow+1+1 ;//行数（i）+ 空白行(blankRow)+第一行检验通知单(1)+1
             	if(!flg){//第一页
             		if(i == 6 || i == (result2.getCount() - 1)){
         	        	data2.addData("BILL_FLG", "") ;
         	        	data2.addData("DEPT_CHN_DESC", "") ;
         	        	data2.addData("ORDER_DESC", "") ;
         	        	data2.addData("MEDI_QTY", "") ;
         	        	data2.addData("URGENT_FLG","") ;
         	        	data2.addData("DESCRIPTION","处方金额:") ;
         	        	data2.addData("MED_APPLY_NO", df.format(pageAmt)) ;
         	        	flg = true ;
         	        	blankRow ++ ;
         	        	pageAmt = 0 ;
             		}

             	}else{//其他页.//第9行显示金额
             		if(i == result2.getCount() - 1 ){
         	        	data2.addData("BILL_FLG", "") ;
         	        	data2.addData("DEPT_CHN_DESC", "") ;
         	        	data2.addData("ORDER_DESC", "") ;
         	        	data2.addData("MEDI_QTY", "") ;
         	        	data2.addData("URGENT_FLG","") ;
         	        	data2.addData("DESCRIPTION","处方金额:") ;
         	        	data2.addData("MED_APPLY_NO", df.format(pageAmt)) ;
         	        	blankRow ++ ;
         	        	pageAmt = 0 ;
             		}else if(i != result2.getCount() - 1 && ((num % 9) + 1) == 9){
         	        	data2.addData("BILL_FLG", "") ;
         	        	data2.addData("DEPT_CHN_DESC", "") ;
         	        	data2.addData("ORDER_DESC", "") ;
         	        	data2.addData("MEDI_QTY", "") ;
         	        	data2.addData("URGENT_FLG","") ;
         	        	data2.addData("DESCRIPTION","处方金额:") ;
         	        	data2.addData("MED_APPLY_NO", df.format(pageAmt)) ;
         	        	blankRow ++ ;
         	        	pageAmt = 0 ;
             		}
             	}
             }
             data2.setCount(data2.getCount("ORDER_DESC")) ;
             data2.addData("SYSTEM", "COLUMNS", "BILL_FLG");
             data2.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
             data2.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
             data2.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
             data2.addData("SYSTEM", "COLUMNS", "URGENT_FLG");
             data2.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
             data2.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");

             inParam.setData("ORDER_TABLE", data2.getData()) ;
           //modify by lim 2012/02/23 begin
             word.setWordParameter(inParam);
             word.setPreview(true);
             word.setFileName("%ROOT%\\config\\prt\\OPD\\OpdNewExaSheet.jhw");
             EMRName = "检验检查通知单_" + inParam.getValue("RX_NO");
             this.classCode = "EMR040001";
             this.subClassCode = "EMR04000141";
             break;
        }
        odo.getOpdOrder().setFilter(filterString);
        odo.getOpdOrder().filter();
    }
//	
//	private double getEveryAmt(String opdOrderCode){
//		String sql = "SELECT SUM(O.DOSAGE_QTY * F.OWN_PRICE) AS AMT FROM SYS_ORDERSETDETAIL O,SYS_FEE F WHERE O.ORDER_CODE = F.ORDER_CODE AND O.ORDERSET_CODE = '"+opdOrderCode+"'" ;
//		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql));
//		if(result2.getErrCode() < 0){
//			messageBox("单页处方金额计算错误.") ;
//			return 0 ;
//		}
//		if(result2.getCount()<=0){
//			return 0 ;
//		}
//		return result2.getDouble("AMT", 0);
//	}
//	

    /**
     * 处方类型点选事件
     * @param rxType
     */
    public void onTypeChange(String rxType) {
        TParm tableParm = getParmRxType(rxType);
        if (tableParm == null || tableParm.getErrCode() != 0) {
            this.messageBox("E0116");
            return;
        }
        int count = tableParm.getCount("RX_NO");
        //判断目前语种是否是英文
        boolean isEN = false;
        if ("en".equals(this.getLanguage()))
            isEN = true;
        for (int i = 0; i < count; i++) {
            if (isEN)
                tableParm.addData("RX_DESC", "【" + (i + 1) + "】 Rx");
            else
                tableParm.addData("RX_DESC", "第" + (i + 1) + "张处方");
        }
        this.rxType = rxType;
        table.setParmValue(tableParm);

    }

    public void onPrint() {
        if (table == null || table.getParmValue().getCount() <= 0) {
            this.messageBox_("没有数据");
            return;
        }
			//int printNum = OpdRxSheetTool.getInstance().getPrintNum(caseNo, rxNo);
			//$$=========Modified by lx 2012/07/02 改成直接打印 START========$$//
			boolean flg=word.getWordText().getPM().getPageManager().print();
			//$$=========Modified by lx 2012/07/02 改成直接打印 END========$$//
	
	        /**boolean flg=word.getWordText().getPM().getPageManager().printDialog(printNum > 0 ?
	                printNum : 1);**/
	        
	        
	        //System.out.println("======flg========"+flg);
	        if(flg){
	        	//保存EMR (保存，写文件)
	        	this.saveEMR(this.EMRName, this.classCode,this.subClassCode);
	        }
        
    }

    /**
     * 取得根据给入rxType得到处方签数据
     * @param rxType
     * @return
     */
    private TParm getParmRxType(String rxType) {
        TParm result = new TParm();
        if (rxType == null || rxType.trim().length() < 1) {
            return result;
        }
        int index =Integer.parseInt(rxType);
        String sql = "";
        switch (index){
        case 1:
        case 2:
        case 3:
        	 sql = "SELECT   SUM (A.AR_AMT) AR_AMT,A.RX_NO,B.DEPT_ABS_DESC DEPT,B.DEPT_ENG_DESC" +
             "  FROM   OPD_ORDER A, SYS_DEPT B" +
             " WHERE   A.CASE_NO = '" + caseNo +
             "' AND A.EXEC_DEPT_CODE = B.DEPT_CODE AND RX_TYPE='" + rxType +
             "'" +
             //zhangyong20110308
             " AND RELEASE_FLG <> 'Y' " +
             " GROUP BY A.RX_NO,B.DEPT_ABS_DESC,B.DEPT_ENG_DESC" +
             " ORDER BY A.RX_NO";
        	 break;
        case 4:
        	 sql = "SELECT   SUM (A.AR_AMT) AR_AMT,A.RX_NO,'' DEPT" +
             "  FROM   OPD_ORDER A" +
             " WHERE   A.CASE_NO = '" + caseNo + "' AND RX_TYPE='" +
             rxType + "'" +
             " GROUP BY A.RX_NO" +
             " ORDER BY A.RX_NO";
        	 break;
        case 5:
        	 sql = "SELECT   SUM (A.AR_AMT) AR_AMT,A.RX_NO,'' DEPT" +
             "  FROM   OPD_ORDER A" +
             " WHERE   A.CASE_NO = '" + caseNo + "' AND RX_TYPE='" +
             rxType + "' AND CAT1_TYPE='LIS'" + 
             " GROUP BY A.RX_NO" +
             " ORDER BY A.RX_NO";
        	 break;
        case 6:
        	 sql = "SELECT   SUM (A.AR_AMT) AR_AMT,A.RX_NO,'' DEPT" +//modify by wanglong 20140411
             "  FROM   OPD_ORDER A" +
             " WHERE   A.CASE_NO = '" + caseNo + "' AND RX_TYPE='" +(index-1) + "' AND CAT1_TYPE <>'LIS'" + 
             " GROUP BY A.RX_NO" +
             " ORDER BY A.RX_NO";
        	 break;
        }
 //        System.out.println("sql================="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 关闭事件，将odo对象的过滤回置
     */
    public boolean onClosing() {
    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
        odo.getOpdOrder().setFilter(filter);
        odo.getOpdOrder().filter();
			}});
        return true;
    }
    /**
     * 上传EMR
     * @param obj Object
     */
    private void saveEMR(String fileName, String classCode,String subClassCode) {
        EMRTool emrTool = new EMRTool(odo.getCaseNo(), odo.getMrNo(), this);
        emrTool.saveEMR(this.word, fileName,classCode,subClassCode);
    }
}
