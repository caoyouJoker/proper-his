package action.ins;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.ins.INSADMConfirmTool;
import jdo.ins.INSIbsOrderTool;
import jdo.ins.INSIbsTool;
import jdo.ins.INSIbsUpLoadTool;
import jdo.ins.INSIpdHistoryTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;

/**
 *
 * <p>
 * Title:住院费用分割
 * </p>
 *
 * <p>
 * Description:住院费用分割
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 *
 * <p>
 * Company:ProperSoft
 * </p>
 *
 * @author pangb 2012-2-07
 * @version 2.0
 */
public class INSBalanceAction extends TAction {
    // 金额名称
    private String[] nameAmt = {"PHA_AMT", "PHA_OWN_AMT", "EXM_AMT",
                               "EXM_OWN_AMT", "TREAT_AMT", "TREAT_OWN_AMT",
                               "OP_AMT",
                               "OP_OWN_AMT", "BED_AMT", "BED_OWN_AMT",
                               "MATERIAL_AMT",
                               "MATERIAL_OWN_AMT", "OTHER_AMT", "OTHER_OWN_AMT",
                               "BLOOD_AMT",
                               "BLOOD_OWN_AMT", "TOTAL_AMT", "OWN_AMT",
                               "BLOODALL_AMT",
                               "BLOODALL_OWN_AMT", "PHA_NHI_AMT", "EXM_NHI_AMT",
                               "TREAT_NHI_AMT",
                               "OP_NHI_AMT", "BED_NHI_AMT", "MATERIAL_NHI_AMT",
                               "OTHER_NHI_AMT",
                               "BLOODALL_NHI_AMT", "BLOOD_NHI_AMT"};
    private TParm amtParm = new TParm(); // 保存金额的数据
    // private TParm ibsOrddParm = new TParm();// 住院费用明细 数据
    private int errorIndex; // 累计错误个数
    private int succIndex; // 累计成功个数
    private int flgIndex =1;// 是否上传
    private int count = 1; // 添加 INS_IBS_ORDER 累计个数
    // 校验添加INS_IBS数据是否为空
    private String[] nameIbs = {"PAT_NAME", "IDNO", "BIRTH_DATE", "ADM_SEQ",
                               "CONFIRM_SRC", "HOSP_NHI_NO", "INSBRANCH_CODE",
                               "CTZ1_CODE",
                               "ADM_CATEGORY", "DIAG_CODE", "DIAG_DESC",
                               "OWN_RATE",
                               "DECREASE_RATE", "REALOWN_RATE", "INSOWN_RATE",
                               "STATION_DESC",
                               "BED_NO", "DEPT_DESC", "DEPT_CODE",
                               "BASEMED_BALANCE",
                               "INS_BALANCE", "OWN_AMT", "CONFIRM_NO",
                               "CHEMICAL_DESC", "ADM_PRJ",
                               "SPEDRS_CODE", "STATUS", "RECEIPT_USER",
                               "NHI_NUM", "INS_UNIT",
                               "HOSP_CLS_CODE", "INP_TIME", "HOMEBED_TIME",
                               "TRANHOSP_DESC",
                               "TRAN_CLASS", "HOMEDIAG_DESC", "SEX_CODE",
                               "SOURCE_CODE",
                               "UNIT_CODE", " UNIT_DESC", "PAT_AGE"};
    // 校验添加INS_IBS_ORDER数据是否为空
    private String[] nameIbsOrder = {"ADM_SEQ", "INSBRANCH_CODE",
                                    "HOSP_NHI_NO", "ORDER_CODE",
                                    "NHI_ORDER_CODE", "ORDER_DESC",
                                    "OWN_RATE", "DOSE_CODE", "STANDARD",
                                    "OP_FLG", "ADDPAY_FLG",
                                    "NHI_ORD_CLASS_CODE", "PHAADD_FLG",
                                    "CARRY_FLG","EXE_DEPT_CODE","DOSAGE_UNIT"};
    // 校验添加INS_IBS_UPLOAD数据是否为空
    private String[] nameIbsUpLoad = {"NHI_ORDER_CODE", "ORDER_CODE",
                                     "ORDER_DESC", "OWN_RATE", "DOSE_CODE",
                                     "STANDARD", "OP_FLG",
                                     "CARRY_FLG", "PHAADD_FLG", "ADDPAY_FLG",
                                     "HYGIENE_TRADE_CODE",
                                     "NHI_ORD_CLASS_CODE"};
    // INS_IBS_UPLOAD表修改界面数据时操作校验为空
    private String[] nameIbsUpLoadOne = {"ORDER_CODE", "ORDER_DESC",
                                        "DOSE_CODE", "STANDARD", "PHAADD_FLG",
                                        "CARRY_FLG","ADDPAY_FLG",
                                        "NHI_ORDER_CODE", "NHI_ORD_CLASS_CODE",
                                        "NHI_FEE_DESC", "HYGIENE_TRADE_CODE"};
    // INS_IBS_UPLOAD_ADVANCE表修改界面数据时操作校验为空
    private String[] nameIbsUpLoadAdvance = {"ORDER_CODE", "ORDER_DESC",
                                        "ADDPAY_FLG","NHI_ORDER_CODE", 
                                        "NHI_ORD_CLASS_CODE",
                                        "HYGIENE_TRADE_CODE"};
    /**
     * 转病患基本资料 和 转申报操作
     * @param tempParm TParm
     * @return TParm
     */
    public TParm onExe(TParm tempParm) {
        // 费用分割 查询病患基本信息操作
        TParm confirmParm = INSADMConfirmTool.getInstance().queryConfirmInfo(
                tempParm);
        if (confirmParm.getErrCode() < 0) {
            return confirmParm;
        }
        TParm result = new TParm();
        if (tempParm.getValue("TYPE").equals("M")) { // TYPE : M,转病患信息
            result = onQueryInfo(confirmParm, tempParm);
        } else if (tempParm.getValue("TYPE").equals("H")) { // TYPE :  H,转申报
            result = onApply(confirmParm, tempParm);
        }
        if (result.getErrCode() < 0) {
            return result;
        }
        result.setData("ERROR_INDEX", errorIndex); // 累计错误个数
        result.setData("SUCCESS_INDEX", succIndex); // 累计成功个数
        return result;
    }

    /**
     * 转病患信息 累计金额操作
     * @param confirmParm TParm
     * @param tempParm TParm
     * @return TParm
     */
    private TParm onQueryInfo(TParm confirmParm, TParm tempParm) {
        TParm tParm = null; // 从IBS_OrdD读取数据 读取住院批价资料
        TParm insParm = null; //查询医保码数据
        TParm ibsOrddParm = null; // 查询医嘱费用
        TParm orderParm = new TParm(); // 查询医嘱费用 执行后 重新整理的数据
        TParm result = new TParm(); // 执行结果
        for (int i = 0; i < confirmParm.getCount(); i++) {
        	// 查询结算表中是否存在诗句
        	String sql = "SELECT REGION_CODE FROM INS_IBS WHERE YEAR_MON='" + tempParm.getValue("YEAR_MON")
                    + "' AND CASE_NO='" + tempParm.getValue("CASE_NO") + "'";
            TParm checkParm = new TParm(TJDODBTool.getInstance().select(sql));
            if (checkParm.getErrCode() < 0) {
                return checkParm;
            }         
            amtParm = new TParm();
            TParm confirmTempParm = confirmParm.getRow(i);
//            System.out.println("confirmTempParm:"+confirmTempParm);
            TParm ibsOrdParm = new TParm();
            ibsOrdParm.setData("START_DATE", tempParm.getValue("START_DATE")
                               + "000000"); // 开始时间
            ibsOrdParm.setData("END_DATE", tempParm.getValue("END_DATE")
                               + "235959"); // 结束时间
            ibsOrdParm.setData("CASE_NO", tempParm.getValue("CASE_NO")); // 就诊号
            // 从IBS_OrdD读取数据 读取住院批价资料
            tParm = INSIbsTool.getInstance().queryIbsOrdd(ibsOrdParm);
            if (tParm.getErrCode() < 0) {
                return tParm;
            }

            for (int j = 0; j < tParm.getCount(); j++) {
                ibsOrddParm = tParm.getRow(i);
                if (null != tempParm.getValue("REGION_CODE")
                    && tempParm.getValue("REGION_CODE").length() > 0) {
                    ibsOrddParm.setData("REGION_CODE", tempParm
                                        .getValue("REGION_CODE"));
                }
                // 查询医保码数据
                insParm=INSIbsTool.getInstance().queryInsIbsOrderByInsRule(ibsOrddParm);
                if (insParm.getErrCode()<0 || insParm.getCount()!=1) {
                	if (insParm.getCount()!=1) {
                		TParm errResult=new TParm();
                    	errResult.setErr(-1,ibsOrddParm.getValue("ORDER_CODE")+"  "+
                    			            ibsOrddParm.getValue("ORDER_DESC")+"医保码有问题");
                    	return errResult;
					}
                	return insParm;
        		}
                // 重新整理数据
                getOrderParm(confirmTempParm, ibsOrddParm, tempParm, orderParm,insParm, 1);
            }
            orderParm.setCount(tParm.getCount());
            for (int z = 0; z < orderParm.getCount(); z++) {
                TParm exeParm = orderParm.getRow(z);

                // 计算money
                double amt = exeParm.getDouble("TOT_AMT"); // 发生
                double own_amt = exeParm.getDouble("OWN_AMT"); // 自费
                double nhi_amt = exeParm.getDouble("TOTAL_NHI_AMT"); // 医保金额
                int Nhi_ord_class_code = exeParm.getInt("NHI_ORD_CLASS_CODE");
                // 计算各 Order 属于哪一项目的钱(转文件使用)
                Accnt_OrderRange_Amt(Nhi_ord_class_code, amt, own_amt, nhi_amt);
            }
            // 重新赋值
            setInsIbsParm(confirmTempParm, tempParm);
            // 金额赋值
            for (int j = 0; j < nameAmt.length; j++) {
                confirmTempParm.setData(nameAmt[j], amtParm
                                        .getDouble(nameAmt[j]));
            }
            //=====pangben 2012-5-30 转归数据添加
            TParm  statusParm = new TParm(TJDODBTool.getInstance().select("SELECT CODE1_STATUS FROM MRO_RECORD WHERE CASE_NO='" + tempParm.getValue("CASE_NO") + "'"));
            if (statusParm.getErrCode()<0) {
            	return statusParm;
			}
            confirmTempParm.setData("SOURCE_CODE",statusParm.getValue("CODE1_STATUS",0));
            // 校验添加INS_IBS数据是否为空
            for (int j = 0; j < nameIbs.length; j++) {
                if (confirmTempParm.getValue(nameIbs[j]).equals("null")
                    || confirmTempParm.getValue(nameIbs[j]).equals("")) {
                    confirmTempParm.setData(nameIbs[j], "");
                }
            }
                        
            if (checkParm.getCount() <= 0) { // 不存在数据
                result = INSIbsTool.getInstance().insertInsIbs(confirmTempParm);
                if (result.getErrCode() < 0) {
                    errorIndex++; // 累计错误
                } else {
                    succIndex++; // 累计成功
                }
            } else {
                result = INSIbsTool.getInstance().updateINSIbs(confirmTempParm);
                if (result.getErrCode() < 0) {
                    errorIndex++; // 累计错误
                } else {
                    succIndex++; // 累计成功
                }
            }
            result = INSADMConfirmTool.getInstance().updatedDsDiag(
                    confirmTempParm);
            if (result.getErrCode() < 0) {
                //System.out.println("修改INSADMConfirm失败");
            }
        }
        TParm parm = new TParm();
        parm.setData("YEAR_MON", tempParm.getValue("YEAR_MON")); // 期号
        parm.setData("CASE_NO", tempParm.getValue("CASE_NO"));
        result = INSIbsTool.getInstance().queryIbsSum(parm); // 查询数据给界面赋值
        return result;
    }

    /**
     * 转申报操作
     * @param confirmParm TParm
     * @param tempParm TParm
     * @return TParm
     */
    private TParm onApply(TParm confirmParm, TParm tempParm) {
        // 删除操作
        TParm parm = new TParm();
        parm.setData("CASE_NO", tempParm.getValue("CASE_NO")); // 就诊号
        parm.setData("YEAR_MON", tempParm.getValue("YEAR_MON")); // 期号
        parm.setData("START_DATE", tempParm.getValue("START_DATE")); // 开始
        parm.setData("END_DATE", tempParm.getValue("END_DATE")); // 结束时间
        TParm result = null;
        if (confirmParm.getCount("CASE_NO") > 0) {
            result = INSIbsOrderTool.getInstance().deleteINSIbsOrder(parm);
        }
        if (result.getErrCode() < 0) {
            return result;
        }
        TParm tParm = null; // 从IBS_OrdD读取数据 读取住院批价资料
        TParm ibsOrddParm = null; // 查询医嘱费用
        TParm orderParm = new TParm(); // 查询医嘱费用 执行后 重新整理的数据
        boolean flg = false; // 管控 放到一个事物操作
        for (int i = 0; i < confirmParm.getCount(); i++) {
        	//int index=0;
            TParm confirmTempParm = confirmParm.getRow(i);
            // 从IBS_OrdD读取数据 读取住院批价资料
            tParm = INSIbsTool.getInstance().queryIbsOrdd(parm);
            if (tParm.getErrCode() < 0) {
                return tParm;
            }
            count = 1; // 重新累计数据 添加 INS_IBS_ORDER
            for (int j = 0; j < tParm.getCount(); j++) {
                ibsOrddParm = tParm.getRow(j);
                if(!getInsertIbsOrder(tempParm, confirmTempParm, ibsOrddParm, orderParm)){
                	TParm errResult=new TParm();
                	errResult.setErr(-1,ibsOrddParm.getValue("ORDER_CODE")+"  "+
                			            ibsOrddParm.getValue("ORDER_DESC")+"医保码有问题");
                	return errResult;
                }
                //index++;
            }
            orderParm.setCount(tParm.getCount());
            //System.out.println("添加INS_IBS_ORDER 数据"+orderParm);
            flg = false;
            // 添加INS_IBS_ORDER 数据
            for (int j = 0; j < orderParm.getCount(); j++) {
                TParm ibsOrder = orderParm.getRow(j);
                //System.out.println("ibsOrder::::"+ibsOrder);
                // 校验为空
                for (int k = 0; k < nameIbsOrder.length; k++) {
                    if (null == ibsOrder.getValue(nameIbsOrder[k]) ||
                        ibsOrder.getValue(nameIbsOrder[k]).equals("null")
                        || ibsOrder.getValue(nameIbsOrder[k]).equals("")) {
                        ibsOrder.setData(nameIbsOrder[k], "");
                    }
                }
                result = INSIbsOrderTool.getInstance().insertINSIbsOrder(
                        ibsOrder);
                if (result.getErrCode() < 0) {
                    flg = true;
                    errorIndex++;
                    break;
                }
            }
            if (flg) { // 管控 放到一个事物操作
                break;
            }
            // 明细上传数据
            TParm insIbsUnionParm = INSIbsOrderTool.getInstance()
                                    .queryInsIbsDUnion(parm);
            if (insIbsUnionParm.getErrCode() < 0) {
                errorIndex++;
                break;
            }
            //System.out.println("明细上传数据insIbsUnionParm:::"+insIbsUnionParm);
            // 添加 INS_IBS_UPLOAD 表操作
            result = onApplyInsertIbsUpLoad(confirmTempParm, insIbsUnionParm);
            if (result.getErrCode() < 0) {
                errorIndex++;
                break;
            }
            succIndex++;
        }
        //处理单病种床位费
        if(tempParm.getValue("SINGLE_TYPE").equals("SINGLE")){
            //获得原床位费数据
        	TParm OriginalBedParm = getOriginalBed(confirmParm);       	
//            System.out.println("OriginalBedParm:=============="+OriginalBedParm);
        if(OriginalBedParm.getCount()>0){       	
            //获得最大SEQ_NO       	
        	TParm ParmMax  = getMaxSeqNo(confirmParm);         	
//        	System.out.println("ParmMax:=============="+ParmMax);
            //获得三级医院床位费和特需床位费       	
    	    TParm LaterBedParm  = getLaterBed();
//    	    System.out.println("LaterBedParm:=============="+LaterBedParm);
    	    //整理数据添加 INS_IBS_UPLOAD 表操作
    	    result =  InsertIbsUpLoadSingle(OriginalBedParm,ParmMax,LaterBedParm,tempParm);    
        }              
    }       
        return new TParm();
    }
    /**
     * 获得原床位费数据
     * @param confirmParm
     * @return
     */
    public TParm getOriginalBed(TParm confirmParm) { 
    	String sql = " SELECT A.ADM_SEQ,A.ORDER_CODE,A.CHARGE_DATE," +
    	" A.QTY,A.TOTAL_AMT,A.NHI_ORD_CLASS_CODE" +
    	" FROM INS_IBS_UPLOAD A" +
    	" WHERE A.ADM_SEQ = '" + confirmParm.getData("ADM_SEQ", 0) +"' " +
    	" AND A.NHI_ORD_CLASS_CODE = '05'" +
    	" ORDER BY A.CHARGE_DATE DESC ";
        TParm Parm  = new TParm(TJDODBTool.getInstance().select(sql));	
        return Parm;
    }
    
    /**
     * 获得最大SEQ_NO
     * @param confirmParm
     * @return
     */
    public TParm getMaxSeqNo(TParm confirmParm) { 
    	String sql = " SELECT MAX(SEQ_NO) AS SEQ_NO_MAX FROM INS_IBS_UPLOAD" + 
    	" WHERE ADM_SEQ = '" + confirmParm.getData("ADM_SEQ", 0) +"'";
    	TParm Parm  = new TParm(TJDODBTool.getInstance().select(sql));
    	return Parm;
    }
    /**
     * 获得三级医院床位费和特需床位费
     * @param confirmParm
     * @return
     */
    public TParm getLaterBed() { 
    	String sql = " SELECT A.REGION_CODE,A.ORDER_CODE,A.ORDER_DESC," +
    	" '' AS DOSE_CODE, '' AS STANDARD,A.NHI_CODE_I,A.OWN_PRICE," +
    	" A.ADDPAY_AMT,'' AS REFUSE_AMT,'' AS REFUSE_REASON_CODE," +
        " '' AS REFUSE_REASON_NOTE,'' AS OP_FLG,'' AS CARRY_FLG,'' AS PHAADD_FLG," +
    	" 'Y' AS PHAADD_FLG, '' AS HYGIENE_TRADE_CODE,''AS INVNO,'' AS UP_DATE," +
    	" '0' AS INS_FLG,'0' AS UP_FLG FROM SYS_FEE A" +
    	" WHERE A.ORDER_CODE IN ('C0000061','C0000062')";
	    TParm Parm  = new TParm(TJDODBTool.getInstance().select(sql));
	    return Parm;
    }
    /**
     * 整理数据添加 INS_IBS_UPLOAD 表操作
     * @param OriginalBedParm,ParmMax,LaterBedParm
     * @return
     */
    public TParm InsertIbsUpLoadSingle(TParm OriginalBedParm,
    		TParm ParmMax,TParm LaterBedParm,TParm tempParm) {
    	TParm result = new TParm();
    	int qty=0;//总数
    	double totalAmt = 0.00;//总金额
    	double bedAmt  = 0.00; //三级医院床位费总金额
    	double txbedAmt  = 0.00; //特需床位费总金额	
    	//整理数据   	
   	     for (int m = 0; m < OriginalBedParm.getCount(); m++) {
   	    	qty += OriginalBedParm.getInt("QTY",m);
   	    	totalAmt +=OriginalBedParm.getDouble("TOTAL_AMT", m);	    	
	        }
   	     //计算三级医院床位费和特需床位费总金额
   	      for (int n = 0; n < LaterBedParm.getCount(); n++) {
   	    	 if(LaterBedParm.getData("ORDER_CODE", n).equals("C0000061")){
    			 bedAmt = LaterBedParm.getDouble("OWN_PRICE", n)*qty;
    			 txbedAmt =  totalAmt-bedAmt;
   	      }
   	   }
//   	    	System.out.println("bedAmt:=============="+bedAmt);
//   	    	System.out.println("txbedAmt:=============="+txbedAmt);
   	    
   	      int seqnomax = ParmMax.getInt("SEQ_NO_MAX", 0); 
    	  for (int i = 0; i < LaterBedParm.getCount(); i++) {
    		 LaterBedParm.setData("ADM_SEQ",i,OriginalBedParm.getData("ADM_SEQ", 0));
    		 LaterBedParm.setData("SEQ_NO",i,seqnomax + i + 1);
    		 LaterBedParm.setData("CHARGE_DATE",i, SystemTool.getInstance()
                              .getDateReplace(OriginalBedParm.getValue("CHARGE_DATE",0), true));
    		 //三级医院床位费
    		 if(LaterBedParm.getData("ORDER_CODE", i).equals("C0000061")){
    			 LaterBedParm.setData("OWN_RATE",i,0); 
    			 LaterBedParm.setData("QTY",i,qty);
    			 LaterBedParm.setData("TOTAL_AMT",i,bedAmt);
    			 LaterBedParm.setData("TOTAL_NHI_AMT",i,bedAmt);
    			 LaterBedParm.setData("OWN_AMT",i,0);    			 
    		 }      		 
    		 //特需床位费
    		 else if(LaterBedParm.getData("ORDER_CODE", i).equals("C0000062")){
    			 LaterBedParm.setData("OWN_RATE",i,1);
    			 LaterBedParm.setData("QTY",i,txbedAmt/LaterBedParm.getDouble("OWN_PRICE", i));
    			 LaterBedParm.setData("TOTAL_AMT",i,txbedAmt);
    			 LaterBedParm.setData("TOTAL_NHI_AMT",i,0);
    			 LaterBedParm.setData("OWN_AMT",i,txbedAmt);
    		 }   	
    		 LaterBedParm.setData("NHI_ORD_CLASS_CODE",i,
    				 OriginalBedParm.getData("NHI_ORD_CLASS_CODE", 0));
    		 LaterBedParm.setData("OPT_USER",i,tempParm.getData("OPT_USER"));
    		 LaterBedParm.setData("OPT_TERM",i,tempParm.getData("OPT_TERM"));
//    		 System.out.println("LaterBedParm:=============="+LaterBedParm);
    		// 添加 INS_IBS_UPLOAD 表操作 
    		 String sql= " INSERT INTO INS_IBS_UPLOAD("+
    		 " ADM_SEQ, SEQ_NO, REGION_CODE, "+
    		 " CHARGE_DATE, NHI_ORDER_CODE, ORDER_CODE,"+ 
    		 " ORDER_DESC, OWN_RATE, DOSE_CODE, "+
    		 " STANDARD, PRICE, QTY,"+
    		 " TOTAL_AMT, TOTAL_NHI_AMT, OWN_AMT, "+
    		 " ADDPAY_AMT, OP_FLG, CARRY_FLG, "+
    		 " PHAADD_FLG, ADDPAY_FLG, NHI_ORD_CLASS_CODE,"+
    		 " HYGIENE_TRADE_CODE,OPT_USER,OPT_DATE,OPT_TERM) " +
    		 " VALUES('"+ LaterBedParm.getValue("ADM_SEQ",i)+ "'," +
    		 " "+ LaterBedParm.getValue("SEQ_NO",i)+ "," +
    		 " '"+ LaterBedParm.getValue("REGION_CODE",i)+ "'," +
    		 " to_date('"+ LaterBedParm.getValue("CHARGE_DATE",i)+"','yyyyMMddHH24MISS')," +
    		 " '"+ LaterBedParm.getValue("NHI_CODE_I",i)+ "'," +
    		 " '"+ LaterBedParm.getValue("ORDER_CODE",i)+ "'," +
    		 " '"+ LaterBedParm.getValue("ORDER_DESC",i)+ "'," +
    		 " "+ LaterBedParm.getInt("OWN_RATE",i)+ "," +
    		 " '"+ LaterBedParm.getValue("DOSE_CODE",i)+ "'," +
    		 " '"+ LaterBedParm.getValue("STANDARD",i)+ "'," +
    		 " "+ LaterBedParm.getDouble("OWN_PRICE",i)+ "," +
    		 " "+ LaterBedParm.getInt("QTY",i)+ "," +
    		 " "+ LaterBedParm.getDouble("TOTAL_AMT",i)+ "," +
    		 " "+ LaterBedParm.getDouble("TOTAL_NHI_AMT",i)+ "," +
    		 " "+ LaterBedParm.getDouble("OWN_AMT",i)+ "," +
    		 " "+ LaterBedParm.getDouble("ADDPAY_AMT",i)+ "," +
    		 " '"+ LaterBedParm.getValue("OP_FLG",i)+ "'," +
    		 " '"+ LaterBedParm.getValue("CARRY_FLG",i)+ "'," +
    		 " '"+ LaterBedParm.getValue("PHAADD_FLG",i)+ "'," +
    		 " '"+ LaterBedParm.getValue("ADDPAY_FLG",i)+ "'," +
    		 " '"+ LaterBedParm.getValue("NHI_ORD_CLASS_CODE",i)+ "'," +
    		 " '"+ LaterBedParm.getValue("HYGIENE_TRADE_CODE",i)+ "'," +
    		 " '"+ LaterBedParm.getValue("OPT_USER",i)+ "',SYSDATE," +
    		 " '"+ LaterBedParm.getValue("OPT_TERM",i)+ "')";
//    		 System.out.println("sql:=============="+sql);
    		result = new TParm(TJDODBTool.getInstance().update(sql));
    	 }
    	 if (result.getErrCode() < 0) {	        		        	
				return result;
	        }else{
	        	//删除原床位费
	        	 for (int  n= 0; n < OriginalBedParm.getCount(); n++) {
	        	String sql = " DELETE FROM INS_IBS_UPLOAD"+ 
	            " WHERE ADM_SEQ ='"+ OriginalBedParm.getValue("ADM_SEQ",0)+ "'"+ 
	        	" AND ORDER_CODE ='"+ OriginalBedParm.getValue("ORDER_CODE",n)+ "'";
	        	result = new TParm(TJDODBTool.getInstance().update(sql)); 
	        	}
	        }
    	 return result;
    }
    /**
     * 整理数据
     * @param tempParm
     * @param confirmTempParm
     * @param ibsOrddParm
     * @param orderParm
     * @return
     */
    private boolean getInsertIbsOrder(TParm tempParm,TParm confirmTempParm,TParm ibsOrddParm,TParm orderParm){
		if (null != tempParm.getValue("REGION_CODE")
				&& tempParm.getValue("REGION_CODE").length() > 0) {
			ibsOrddParm
					.setData("REGION_CODE", tempParm.getValue("REGION_CODE"));
		}
		TParm insParm=INSIbsTool.getInstance().queryInsIbsOrderByInsRule(ibsOrddParm);
        if (insParm.getErrCode()<0 || insParm.getCount()!=1) {
        	return false;
		}
		// 重新整理数据
		getOrderParm(confirmTempParm, ibsOrddParm,
				tempParm, orderParm,insParm, 2);
		return true;
    }
    /**
     * 添加 INS_IBS_UPLOAD 表操作 不可以放到一个事物里面操作 需要查询之前添加的数据
     * @param confirmTempParm TParm 查询 ADM_CONFIRM 表
     * @param insIbsUnionParm TParm 查询INS_IBS_UPLOAD
     * @return TParm
     */
    private TParm onApplyInsertIbsUpLoad(TParm confirmTempParm,
                                         TParm insIbsUnionParm) {

        TParm result = new TParm();
        // 删除老数据
        if (insIbsUnionParm.getCount() > 0) {
            result = INSIbsUpLoadTool.getInstance().deleteINSIbsUpload(
                    confirmTempParm);
        }
        if (result.getErrCode() < 0) {
            return result;
        }
        // 执行添加操作
        for (int j = 0; j < insIbsUnionParm.getCount(); j++) {
            TParm tempParm = insIbsUnionParm.getRow(j);
            tempParm.setData("SEQ_NO", j + 1);
            tempParm.setData("CHARGE_DATE", SystemTool.getInstance()
                             .getDateReplace(tempParm.getValue("CHARGE_DATE"), true)); // 明细录入时间
            tempParm.setData("ADDPAY_FLG", "Y"); // 增负
            for (int i = 0; i < nameIbsUpLoad.length; i++) { //校验是否为空
                if (tempParm.getValue(nameIbsUpLoad[i]).equals("null")
                    || tempParm.getValue(nameIbsUpLoad[i]).equals("")) {
                    tempParm.setData(nameIbsUpLoad[i], "");
                }
            }
            result = INSIbsUpLoadTool.getInstance()
                     .insertINSIbsUpload(tempParm);
            if (result.getErrCode() < 0) {
                return result;
            }
        }

        return result;
    }

    /**
     * 计算各 Order 属于哪一项目的钱(转文件使用)
     * @param Nhi_ord_class_code int INS_RULE 表中统计代码
     * @param amt double
     * @param own_amt double
     * @param nhi_amt double
     */
    private void Accnt_OrderRange_Amt(int Nhi_ord_class_code, double amt,
                                      double own_amt, double nhi_amt) {
        switch (Nhi_ord_class_code) {
        case 1: // 药品费
            amtParm.setData("PHA_AMT", amtParm.getDouble("PHA_AMT") + amt);
            amtParm.setData("PHA_OWN_AMT", amtParm.getDouble("PHA_OWN_AMT")
                            + own_amt);
            amtParm.setData("PHA_NHI_AMT", amtParm.getDouble("PHA_NHI_AMT")
                            + nhi_amt);
            break;
        case 2: // 检查费
            amtParm.setData("EXM_AMT", amtParm.getDouble("EXM_AMT") + amt);
            amtParm.setData("EXM_OWN_AMT", amtParm.getDouble("EXM_OWN_AMT")
                            + own_amt);
            amtParm.setData("EXM_NHI_AMT", amtParm.getDouble("EXM_NHI_AMT")
                            + nhi_amt);
            break;
        case 3: // 治疗费
            amtParm.setData("TREAT_AMT", amtParm.getDouble("TREAT_AMT") + amt);
            amtParm.setData("TREAT_OWN_AMT", amtParm.getDouble("TREAT_OWN_AMT")
                            + own_amt);
            amtParm.setData("TREAT_NHI_AMT", amtParm.getDouble("TREAT_NHI_AMT")
                            + nhi_amt);
            break;
        case 4: // 手术费
            amtParm.setData("OP_AMT", amtParm.getDouble("OP_AMT") + amt);
            amtParm.setData("OP_OWN_AMT", amtParm.getDouble("OP_OWN_AMT")
                            + own_amt);
            amtParm.setData("OP_NHI_AMT", amtParm.getDouble("OP_NHI_AMT")
                            + nhi_amt);
            break;
        case 5: // 床位费
            amtParm.setData("BED_AMT", amtParm.getDouble("BED_AMT") + amt);
            amtParm.setData("BED_OWN_AMT", amtParm.getDouble("BED_OWN_AMT")
                            + own_amt);
            amtParm.setData("BED_NHI_AMT", amtParm.getDouble("BED_NHI_AMT")
                            + nhi_amt);
            break;
        case 6: // 医用材料
            amtParm.setData("MATERIAL_AMT", amtParm.getDouble("MATERIAL_AMT")
                            + amt);
            amtParm.setData("MATERIAL_OWN_AMT", amtParm
                            .getDouble("MATERIAL_OWN_AMT")
                            + own_amt);
            amtParm.setData("MATERIAL_NHI_AMT", amtParm
                            .getDouble("MATERIAL_NHI_AMT")
                            + nhi_amt);
            break;
        case 7: // 其它
            amtParm.setData("OTHER_AMT", amtParm.getDouble("OTHER_AMT") + amt);
            amtParm.setData("OTHER_OWN_AMT", amtParm.getDouble("OTHER_OWN_AMT")
                            + own_amt);
            amtParm.setData("OTHER_NHI_AMT", amtParm.getDouble("OTHER_NHI_AMT")
                            + nhi_amt);
            break;
        case 8: // 输全血
            amtParm.setData("BLOODALL_AMT", amtParm.getDouble("BLOODALL_AMT")
                            + amt);
            amtParm.setData("BLOODALL_OWN_AMT", amtParm
                            .getDouble("BLOODALL_OWN_AMT")
                            + own_amt);
            amtParm.setData("BLOODALL_NHI_AMT", amtParm
                            .getDouble("BLOODALL_NHI_AMT")
                            + nhi_amt);
            break;
        case 9: // 成分输血
            amtParm.setData("BLOOD_AMT", amtParm.getDouble("BLOOD_AMT") + amt);
            amtParm.setData("BLOOD_OWN_AMT", amtParm.getDouble("BLOOD_OWN_AMT")
                            + own_amt);
            amtParm.setData("BLOOD_NHI_AMT", amtParm.getDouble("BLOOD_NHI_AMT")
                            + nhi_amt);
            break;
        }
        amtParm.setData("TOTAL_AMT", amtParm.getDouble("TOTAL_AMT") + amt); // 总金额
        amtParm.setData("OWN_AMT", amtParm.getDouble("OWN_AMT") + own_amt); // 自费总金额
    }

    /**
     * 重新整理数据 统计金额使用
     * @param confirmTempParm TParm
     * @param ibsParm TParm
     * @param sysFeeParm TParm
     * @param tempParm TParm
     * @param orderParm TParm
     * @param type int int type 1:转病患信息查询 2：转申报操作 INS_IBS_ORDER 数据
     * @return TParm
     */
    private TParm getOrderParm(TParm confirmTempParm, TParm ibsParm, TParm tempParm,
                               TParm orderParm,TParm insParm, int type) {

        if (type == 2) {
            orderParm.addData("YEAR_MON", tempParm.getValue("YEAR_MON")); // 期号
            orderParm.addData("CASE_NO", tempParm.getValue("CASE_NO")); // 就诊号
            orderParm.addData("INSBRANCH_CODE", confirmTempParm
                              .getValue("INSBRANCH_CODE")); // 分中心
            orderParm.addData("BILL_DATE", SystemTool.getInstance()
                              .getDateReplace(ibsParm.getValue("BILL_DATE1"), true)); // 明细帐日期时间
            orderParm.addData("HOSP_NHI_NO", confirmTempParm
                              .getValue("NHIHOSP_NO")); // 医保区域代码
            orderParm.addData("DOSE_DESC", ibsParm.getValue("DOSE_DESC")); // 剂型名称
            orderParm.addData("OPT_USER", tempParm.getValue("OPT_USER")); // ID
            orderParm.addData("OPT_TERM", tempParm.getValue("OPT_TERM")); // IP

            orderParm.addData("SEQ_NO", count); // 顺序号
            orderParm.addData("REGION_CODE", tempParm.getValue("REGION_CODE")); // 区域代码
            orderParm.addData("ADM_SEQ", confirmTempParm.getValue("ADM_SEQ")); // 就诊顺序号
            orderParm.addData("PRICE", ibsParm.getDouble("OWN_PRICE")); // 单价
            orderParm.addData("QTY", ibsParm.getDouble("DOSAGE_QTY")); // 个数
            orderParm.addData("ADDPAY_AMT", ibsParm.getDouble("ADDPAY_AMT")); // 累计金额
            // 医保金额 =医保单价*个数
            orderParm.addData("TOTAL_NHI_AMT", 0.00);
            orderParm.addData("ADDPAY_FLG", "N"); // 累计增负注记ACCRUAL_FLG?????? 原程序
            // 查询 IBS_ORDD
            orderParm.addData("PHAADD_FLG", "N"); // 增负药品注记??????? 原程序 查询
            //======pangben 2012-6-11 start 修改出院带药程序
            orderParm.addData("CARRY_FLG", null==ibsParm.getValue("DS_FLG")||ibsParm.getValue("DS_FLG").trim().length()<=0||
            		ibsParm.getValue("DS_FLG").trim().equals("N")?"N":"Y"); // 出院带药注记 
            // IBS_ORDD
            // 原程序
            // 查询
            // IBS_ORDM
            orderParm.addData("ORDER_CODE", ibsParm.getValue("ORDER_CODE")); // 医嘱名称
            //修改:医保升级 以后，在医保升级之前已经收费的医嘱医保码使用旧的,新开立的收费医嘱使用新的
            //======pangben 2012-9-7
            //判断此收费时间是否在医保字典数据开始时间之前
			orderParm.addData("NHI_ORDER_CODE", ibsParm
                    .getValue("INS_CODE")); // 医保医嘱代码
            orderParm.addData("OP_FLG",
            		insParm.getValue("TJDM",0).equals("04") ? "Y" : "N"); // 手术费用注记
            orderParm.addData("OWN_RATE", insParm.getDouble("ZFBL1",0)); // 自负比例
            orderParm.addData("DOSAGE_UNIT", ibsParm.getValue("DOSAGE_UNIT"));//发药单位
            orderParm.addData("EXE_DEPT_CODE", ibsParm.getValue("EXE_DEPT_CODE"));//执行科室
            orderParm.addData("HYGIENE_TRADE_CODE", insParm.getValue("PZWH",0)); // 批准文号
            orderParm.addData("ORDER_DESC", ibsParm.getValue("ORDER_DESC")); // 名称
            orderParm.addData("STANDARD", null!=ibsParm.getValue("SPECIFICATION") && ibsParm.getValue("SPECIFICATION").length()>=20?//======pangben 20120801 修改保存长度
            		ibsParm.getValue("SPECIFICATION").substring(0,20):ibsParm.getValue("SPECIFICATION")); // 规格 
            orderParm.addData("DOSE_CODE", ibsParm.getValue("DOSE_CODE")); // 剂型
            count++;
        }
        orderParm.addData("TOTAL_AMT", ibsParm.getDouble("TOT_AMT")); // 发生金额
        orderParm.addData("OWN_AMT", ibsParm.getDouble("OWN_AMT")); // 自费金额
        orderParm.addData("NHI_ORD_CLASS_CODE", insParm.getValue("TJDM",0)); // 统计代码

        // vRow.add(20, String.valueOf(vIBSOrdMD.get(18)).trim()); //批准文号
        return orderParm;
    }

    /**
     * 添加或修改INS_IBS表数据
     * @param parm TParm
     * @param tempParm TParm
     */
    private void setInsIbsParm(TParm parm, TParm tempParm) {
        parm.setData("YEAR_MON", tempParm.getValue("YEAR_MON")); // 期号
        parm.setData("BIRTH_DATE", SystemTool.getInstance().getDateReplace(
                parm.getValue("BIRTH_DATE"), true)); // 出生日期
        parm.setData("HOSP_NHI_NO", parm.getValue("NHIHOSP_NO")); // 医保区域代码
        parm.setData("IN_DATE", SystemTool.getInstance().getDateReplace(
                parm.getValue("IN_DATE"), true)); // 入院日期
        parm.setData("DS_DATE", SystemTool.getInstance().getDateReplace(
                parm.getValue("DS_DATE"), true)); // 出院日期
        // 基本医疗剩余额----距基本医疗保险最高支付限额剩余额????
        parm
                .setData("BASEMED_BALANCE", parm
                         .getDouble("INSBASE_LIMIT_BALANCE"));
        // 医疗救助剩余额--距医疗救助最高支付限额剩余额
        parm.setData("INS_BALANCE", parm.getDouble("INS_LIMIT_BALANCE"));
        parm.setData("HOSP_CLS_CODE", parm.getValue("HOSP_CLASS_CODE")); // 医院等级代码
        parm.setData("STATUS", "N"); // 状态 N,未上传，S上传成功
        parm.setData("OPT_USER", tempParm.getValue("OPT_USER")); // ID
        parm.setData("OPT_TERM", tempParm.getValue("OPT_TERM")); // IP
        parm.setData("REGION_CODE", tempParm.getValue("REGION_CODE"));
        String sql =
                " SELECT  A.ICD_CODE ,B.ICD_CHN_DESC,C.DS_DATE  " +
                " FROM ADM_INPDIAG A ,SYS_DIAGNOSIS B,ADM_INP C " +
                " WHERE A.CASE_NO=C.CASE_NO " +
                " AND A.ICD_CODE=B.ICD_CODE(+)" +
                " AND A.CASE_NO ='" + parm.getValue("CASE_NO") +"' " +
                " AND A.MAINDIAG_FLG ='Y' " +
                " AND A.IO_TYPE = 'O' " ;
//                " AND C.CONFIRM_NO='" + parm.getValue("CONFIRM_NO") + "'";
        TParm tempMroParm = new TParm(TJDODBTool.getInstance().select(sql));
//        System.out.println("tempMroParmP:"+tempMroParm);
        if (tempMroParm.getErrCode() < 0) {
            return;
        }
        parm.setData("DIAG_CODE", tempMroParm.getValue("ICD_CODE", 0)); //出院诊断
        parm.setData("DIAG_DESC", tempMroParm.getValue("ICD_CHN_DESC", 0));
        parm.setData("DSDIAG_DESC", tempMroParm.getValue("ICD_CHN_DESC", 0));
        parm.setData("DSDIAG_CODE", tempMroParm.getValue("ICD_CODE", 0));
        //获得次诊断 
        parm.setData("DIAG_DESC2", getDiagDesc(parm.getValue("CASE_NO")));//for INS_IBS表
        parm.setData("DSDIAG_DESC2", getDiagDesc(parm.getValue("CASE_NO")));//for INS_ADM_CONFIRM表 
        //获得其它出院诊断
        parm.setData("OTHER_DIAGE_CODE", getOtherDiagCode(parm.getValue("CASE_NO")));//for INS_IBS表 
        //获得人群类别(1城职、2城居、3异地) 
        parm.setData("INS_CROWD_TYPE", tempParm.getValue("INS_CROWD_TYPE_YD"));//for INS_IBS表
        //获得区域(Y本地、N异地)
        parm.setData("LOCAL_FLG", tempParm.getValue("LOCAL_FLG"));//for INS_IBS表        
    }
    /**
	 * 获得次诊断
	 * 
	 * @param caseNo
	 *            String
	 * @return String
	 */
	private String getDiagDesc(String caseNo) {
		String sql = "SELECT ICD_CODE,ICD_DESC AS ICD_CHN_DESC FROM MRO_RECORD_DIAG  WHERE CASE_NO='"
				+ caseNo + "' AND IO_TYPE='O' AND MAIN_FLG='N'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			return "";
		}
		String diagDesc = "";
		for (int i = 0; i < result.getCount(); i++) {
			diagDesc += result.getValue("ICD_CHN_DESC", i) + ",";
		}
		if (diagDesc.length() > 0) {
			diagDesc = diagDesc.substring(0, diagDesc.lastIndexOf(","));
		}
		return diagDesc;
	}
	/**
	 * 其它出院诊断
	 * @param caseNo
	 * @return String
	 */
	private String getOtherDiagCode(String caseNo) {
		String sql =  " SELECT A.ICD_CODE,A.ICD_DESC,A.IO_TYPE,A.MAIN_FLG"+
		              " FROM MRO_RECORD_DIAG A"+ 	  
		              " WHERE A.CASE_NO = '"+ caseNo + "'"+ 
		              " AND A.IO_TYPE IN ('O','Q','W')"+
		              " ORDER BY A.IO_TYPE,A.MAIN_FLG DESC";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			return "";
		}
		String diagDesc = "";
		for (int i = 0; i < result.getCount(); i++) {
			diagDesc += result.getValue("ICD_CODE", i) + "@";
		}
		if (diagDesc.length() > 0) {
			diagDesc = diagDesc.substring(0, diagDesc.lastIndexOf("@"));
		}
		return diagDesc;
	}
    /**
     * 费用分割 费用分割按钮
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveInsUpLoad(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        // 添加数据
        for (int i = 0; i < parm.getCount("SEQ_NO"); i++) {
            TParm tempParm = parm.getRow(i);
            tempParm.setData("OPT_USER", parm.getValue("OPT_USER"));
            tempParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
//                        System.out.println("费用分割后修改数据>>>"+tempParm);
            result = INSIbsUpLoadTool.getInstance().updateSplit(
                    tempParm, connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        //单病种分割操作执行修改 INS_IBS 结算表中床位费用自需金额和医用材料费特需金额数据
        //TYPE=SINGLE 单病种操作
        if (null != parm.getValue("TYPE") &&
            parm.getValue("TYPE").equals("SINGLE")) {
            TParm tempParm = new TParm();
            tempParm.setData("CASE_NO", parm.getValue("CASE_NO"));
            tempParm.setData("NHI_ORDER_CODE", "006409"); //床位费用自需金额
            TParm bedParm = INSIbsUpLoadTool.getInstance().queryBedAndMaterial(
                    tempParm);
            if (bedParm.getErrCode() < 0) {
                connection.close();
                return bedParm;
            }
            tempParm.setData("NHI_ORDER_CODE", "006410"); //医用材料费特需金额
            TParm materialParm = INSIbsUpLoadTool.getInstance().
                                 queryBedAndMaterial(tempParm);
            if (materialParm.getErrCode() < 0) {
                connection.close();
                return materialParm;
            }
            tempParm.setData("YEAR_MON", parm.getValue("YEAR_MON")); //期号
            tempParm.setData("BED_SINGLE_AMT", bedParm.getDouble("OWN_AMT", 0)); //床位费用自需金额
            tempParm.setData("MATERIAL_SINGLE_AMT",
                             materialParm.getDouble("OWN_AMT", 0)); //医用材料费特需金额
            //修改床位费特需金额和医用材料费特需金额
            result = INSIbsTool.getInstance().updateIbsBedFee(tempParm);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 累计增负 操作
     * @param parm TParm
     * @return TParm
     */
    public TParm onAdd(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        //parm.setData("UPLOAD_FLG", "Y");// 上传注记
        // 费用分割结算后更新 INS_IBS
//        result = INSIbsTool.getInstance().updateSplitByIns(parm, connection);
//        if (result.getErrCode() < 0) {
//            connection.close();
//            return result;
//        }
        // 查询是否存在累计增负数据
        result = INSIbsUpLoadTool.getInstance().queryIbsUploadAdd(parm);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        // 删除操作
        if (result.getCount() > 0) {
            result = INSIbsUpLoadTool.getInstance().deleteAddIbsUpload(parm,
                    connection);
        }
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        // 累计增负添加数据
        result = INSIbsUpLoadTool.getInstance().insertINSIbsUploadOne(parm,
                connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 修改保存费用分割后明细数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updateUpLoad(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        for (int i = 0; i < parm.getCount(); i++) {
            TParm temp = parm.getRow(i);
            if (null != temp.getValue("FLG")
                && temp.getValue("FLG").length() > 0
                && temp.getValue("SEQ_NO").length() > 0) { // 除去合计数据
                temp.setData("OPT_USER", parm.getValue("OPT_USER"));
                temp.setData("OPT_TERM", parm.getValue("OPT_TERM"));
                temp.setData("REGION_CODE", parm.getValue("REGION_CODE"));
                temp.setData("CHARGE_DATE", SystemTool.getInstance()
                             .getDateReplace(temp.getValue("CHARGE_DATE"), true)); // 明细账日期
                //校验是否为空值
                for (int j = 0; j < nameIbsUpLoadOne.length; j++) {
                    if (null == temp.getValue(nameIbsUpLoadOne[j]) ||
                        temp.getValue(nameIbsUpLoadOne[j]).equals("null")
                        || temp.getValue(nameIbsUpLoadOne[j]).equals("")) {
                        temp.setData(nameIbsUpLoadOne[j], "");
                    }
                }
                if (temp.getBoolean("FLG")) { // 添加操作
                    result = INSIbsUpLoadTool.getInstance()
                             .insertINSIbsUploadOne(temp, connection);
                } else {
                    // 修改操作
                    result = INSIbsUpLoadTool.getInstance()
                             .updateINSIbsUploadOne(temp, connection);
                }
            }
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 转病患基本资料 和 转申报操作(新)
     * @param tempParm TParm
     * @return TParm
     */
    public TParm onExeNew(TParm tempParm) {
        // 费用分割 查询病患基本信息操作
        TParm confirmParm = INSADMConfirmTool.getInstance().queryConfirmInfo(
                tempParm);
        if (confirmParm.getErrCode() < 0) {
            return confirmParm;
        }
        TParm result = new TParm();
        if (tempParm.getValue("TYPE").equals("M")) { // TYPE : M,转病患信息
            result = onQueryInfo(confirmParm, tempParm);
        } else if (tempParm.getValue("TYPE").equals("H")) { // TYPE :  H,转申报
            result = onApplyNew(confirmParm, tempParm);
        }
        if (result.getErrCode() < 0) {
            return result;
        }
        result.setData("ERROR_INDEX", errorIndex); // 累计错误个数
        result.setData("SUCCESS_INDEX", succIndex); // 累计成功个数
        result.setData("MES", flgIndex); // 是否上传
        return result;
    }

    /**
     * 转日明细操作
     * @param confirmParm TParm
     * @param tempParm TParm
     * @return TParm
     */
    private TParm onApplyNew(TParm confirmParm, TParm tempParm) {
//    	 System.out.println("onApplyNew===="+confirmParm);
        // 删除操作
        TParm parm = new TParm();
        parm.setData("CASE_NO", tempParm.getValue("CASE_NO")); // 就诊号
        parm.setData("YEAR_MON", tempParm.getValue("YEAR_MON")); // 期号
        parm.setData("START_DATE", tempParm.getValue("UPLOAD_DATE")); //上传时间
        parm.setData("END_DATE", tempParm.getValue("UPLOAD_DATE")); // 上传时间
        TParm result = null;
        if (confirmParm.getCount("CASE_NO") > 0) {
        	String flg = " SELECT UP_FLG FROM INS_IBS_UPLOAD"+ 
            " WHERE ADM_SEQ ='"+ confirmParm.getValue("ADM_SEQ",0) + "'"+       
        	" AND UP_FLG = '2'" +
        	" AND CHARGE_DATE BETWEEN"+  
        	" TO_DATE('"+ parm.getValue("START_DATE")+"000000"+"','YYYYMMDDHH24MISS')"+  
        	" AND  TO_DATE('"+ parm.getValue("END_DATE")+"235959"+"','YYYYMMDDHH24MISS')";
        	result = new TParm(TJDODBTool.getInstance().select(flg));
//        	 System.out.println("onApplyNew====1"+result);
        	if(result.getCount()>0){
        		  flgIndex =2;
        		  return result;
        	}
        	else{        		
        	String sql = " DELETE FROM INS_IBS_ORDER"+ 
            " WHERE YEAR_MON='"+ parm.getValue("YEAR_MON") + "'"+  
        	" AND CASE_NO='"+ parm.getValue("CASE_NO") + "'" +
        	" AND BILL_DATE BETWEEN"+  
        	" TO_DATE('"+ parm.getValue("START_DATE")+"000000"+"','YYYYMMDDHH24MISS')"+  
        	" AND  TO_DATE('"+ parm.getValue("END_DATE")+"235959"+"','YYYYMMDDHH24MISS')";
        	result = new TParm(TJDODBTool.getInstance().update(sql)); 
        	
        	}
        }
        if (result.getErrCode() < 0) {
            return result;
        }
//        System.out.println("onApplyNew====AAAAAA"+confirmParm);
        TParm tParm = null; // 从IBS_OrdD读取数据 读取住院批价资料
        TParm ibsOrddParm = null; // 查询医嘱费用
        TParm orderParm = new TParm(); // 查询医嘱费用 执行后 重新整理的数据
        boolean flg = false; // 管控 放到一个事物操作
        for (int i = 0; i < confirmParm.getCount(); i++) {
        	//int index=0;
            TParm confirmTempParm = confirmParm.getRow(i);
            // 从IBS_OrdD读取数据 读取住院批价资料
            String T =" SELECT B.ORDER_CODE,TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BILL_DATE1," +
            		  " TO_CHAR(B.BILL_DATE,'YYYYMMDD') BILL_DATE2,B.BILL_DATE AS BILL_D " +
            		  " ,B.ORDER_SEQ,B.ORDER_NO,B.DS_FLG,B.OWN_PRICE,B.OWN_AMT,B.TOT_AMT ,C.CASE_NO," +
            		  " B.DOSAGE_QTY,B.OWN_RATE,B.DOSE_CODE,B.NHI_PRICE,E.SPECIFICATION," +
            		  " G.DOSE_CHN_DESC AS DOSE_DESC,B.TAKE_DAYS,B.DOSAGE_UNIT,B.EXE_DEPT_CODE ," +
            		  " E.ORDER_DESC,E.ADDPAY_AMT,E.NHI_CODE_I AS INS_CODE" + 
            		  " FROM  IBS_ORDD B,ADM_INP C,PHA_DOSE G,SYS_FEE_HISTORY E" + 
            		  " WHERE  B.CASE_NO=C.CASE_NO" + 
            		  " AND B.ORDER_CODE=E.ORDER_CODE " +
            		  " AND B.DOSE_CODE=G.DOSE_CODE(+)" + 
            		  " AND B.CASE_NO='"+ parm.getValue("CASE_NO") + "'" +
            		  " AND B.BILL_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+"000000"+"','YYYYMMDDHH24MISS')"+  
        	          " AND  TO_DATE('"+ parm.getValue("END_DATE")+"235959"+"','YYYYMMDDHH24MISS')"+
            		  " AND B.BILL_DATE BETWEEN TO_DATE(E.START_DATE,'YYYYMMDDHH24MISS')" +  
            		  " AND TO_DATE(E.END_DATE,'YYYYMMDDHH24MISS') ORDER BY B.ORDER_CODE";
//            System.out.println("onApplyNew====2RRR"+T);
             tParm = new TParm(TJDODBTool.getInstance().select(T));
//             System.out.println("onApplyNew====2YYYY"+tParm);
            if (tParm.getErrCode() < 0) {
                return tParm;
            } 
            //序号判断
            String sql = " SELECT  NVL(MAX(SEQ_NO)+1,1) AS SEQ_NO "+ 
                         " FROM INS_IBS_ORDER"+ 
                         " WHERE CASE_NO='"+ parm.getValue("CASE_NO") + "'" +
                         " AND YEAR_MON ='"+ parm.getValue("YEAR_MON") + "'";
//            System.out.println("onApplyNew====2sql"+sql);
            TParm seqnoparm = new TParm(TJDODBTool.getInstance().select(sql)); 
//            System.out.println("onApplyNew====2seqnoparm"+seqnoparm.getInt("SEQ_NO", 0));
          // 重新累计数据 添加 INS_IBS_ORDER
            count = seqnoparm.getInt("SEQ_NO", 0);	
            for (int j = 0; j < tParm.getCount(); j++) {
                ibsOrddParm = tParm.getRow(j);
                if(!getInsertIbsOrder(tempParm, confirmTempParm, ibsOrddParm, orderParm)){
                	TParm errResult=new TParm();
                	errResult.setErr(-1,ibsOrddParm.getValue("ORDER_CODE")+"  "+
                			            ibsOrddParm.getValue("ORDER_DESC")+"医保码有问题");
                	return errResult;
                }
                //index++;
            }
            orderParm.setCount(tParm.getCount());
//           System.out.println("添加INS_IBS_ORDER 数据"+orderParm);
            flg = false;
            // 添加INS_IBS_ORDER 数据
            for (int j = 0; j < orderParm.getCount(); j++) {
                TParm ibsOrder = orderParm.getRow(j);
//                System.out.println("ibsOrder::::"+ibsOrder);
                // 校验为空
                for (int k = 0; k < nameIbsOrder.length; k++) {
                    if (null == ibsOrder.getValue(nameIbsOrder[k]) ||
                        ibsOrder.getValue(nameIbsOrder[k]).equals("null")
                        || ibsOrder.getValue(nameIbsOrder[k]).equals("")) {
                        ibsOrder.setData(nameIbsOrder[k], "");
                    }
                }
                result = INSIbsOrderTool.getInstance().insertINSIbsOrder(
                        ibsOrder);
                if (result.getErrCode() < 0) {
                    flg = true;
                    errorIndex++;
                    break;
                }
            }
            if (flg) { // 管控 放到一个事物操作
                break;
            }
            // 明细上传数据
            String sqlUnion =" SELECT B.* FROM (  SELECT A.REGION_CODE," +
            	" A.YEAR_MON,A.ADM_SEQ,A.HOSP_NHI_NO," +
            	" A.ORDER_CODE,A.ORDER_DESC,A.DOSE_DESC,A.STANDARD,A.PRICE,SUM(A.QTY) AS QTY," +
            	" SUM(A.TOTAL_AMT) AS TOTAL_AMT,SUM(A.TOTAL_NHI_AMT) AS TOTAL_NHI_AMT," +
            	" SUM(A.OWN_AMT) AS OWN_AMT,SUM(A.ADDPAY_AMT) AS ADDPAY_AMT," +
            	" A.OP_FLG,A.ADDPAY_FLG,A.NHI_ORD_CLASS_CODE,A.PHAADD_FLG," +
            	" A.CARRY_FLG,A.NHI_ORDER_CODE, '0'," +
            	" C.NHI_CODE_I,C.OWN_PRICE ,A.OWN_RATE,A.DOSE_CODE , " +
            	" MAX(A.BILL_DATE) AS CHARGE_DATE,A.HYGIENE_TRADE_CODE," +
            	" A.OPT_USER,MAX(A.OPT_DATE) AS OPT_DATE,A.OPT_TERM,A.SEQ_NO" + 
            	" FROM INS_IBS_ORDER A ,SYS_FEE C " +
            	" WHERE CASE_NO='"+ parm.getValue("CASE_NO") + "'" +
            	" AND YEAR_MON ='"+ parm.getValue("YEAR_MON") + "'" +
            	" AND A.BILL_DATE BETWEEN" + 
            	" TO_DATE('"+ parm.getValue("START_DATE")+"000000"+"','YYYYMMDDHH24MISS')" + 
            	" AND TO_DATE('"+ parm.getValue("END_DATE")+"235959"+"','YYYYMMDDHH24MISS')" +
            	" AND A.TOTAL_AMT <> 0" + 
            	" AND A.ORDER_CODE=C.ORDER_CODE" +
            	" GROUP BY A.REGION_CODE,A.YEAR_MON,A.ADM_SEQ,A.HOSP_NHI_NO," +
            	" A.ORDER_CODE,A.ORDER_DESC,A.DOSE_DESC,A.STANDARD," +
            	" A.PRICE,A.OP_FLG,A.ADDPAY_FLG,A.NHI_ORD_CLASS_CODE,A.PHAADD_FLG,A.CARRY_FLG," + 
            	" A.NHI_ORDER_CODE,A.OPT_TERM,A.OPT_USER," +
            	" C.NHI_CODE_I,C.OWN_PRICE ,A.OWN_RATE,A.DOSE_CODE,A.HYGIENE_TRADE_CODE,A.SEQ_NO ) B" +
            	" WHERE B.TOTAL_AMT!=0";
//            System.out.println("sqlUnion::::"+sqlUnion);
            TParm insIbsUnionParm = new TParm(TJDODBTool.getInstance().select(sqlUnion)); 
            if (insIbsUnionParm.getErrCode() < 0) {
                errorIndex++;
                break;
            }
//            System.out.println("明细上传数据insIbsUnionParm:::"+insIbsUnionParm);
            // 添加 INS_IBS_UPLOAD 表操作
            result = onApplyInsertIbsUpLoadNew(confirmTempParm, insIbsUnionParm,parm);
            if (result.getErrCode() < 0) {
                errorIndex++;
                break;
            }
            succIndex++;
        }
        return new TParm();
    } 
    /**
     * 添加 INS_IBS_UPLOAD 表操作 (新)
     * @param confirmTempParm TParm 查询 ADM_CONFIRM 表
     * @param insIbsUnionParm TParm 查询INS_IBS_UPLOAD
     * @return TParm
     */
    private TParm onApplyInsertIbsUpLoadNew(TParm confirmTempParm,
                                         TParm insIbsUnionParm,TParm parm) {

        TParm result = new TParm();
        // 删除老数据
        if (insIbsUnionParm.getCount() > 0) {
        	String sql =" DELETE FROM INS_IBS_UPLOAD"+ 
            " WHERE ADM_SEQ ='"+ confirmTempParm.getValue("ADM_SEQ") + "'"+  
        	" AND CHARGE_DATE BETWEEN"+  
        	" TO_DATE('"+ parm.getValue("START_DATE")+"000000"+"','YYYYMMDDHH24MISS')"+  
        	" AND  TO_DATE('"+ parm.getValue("END_DATE")+"235959"+"','YYYYMMDDHH24MISS')";
        	result = new TParm(TJDODBTool.getInstance().update(sql));
        }
        if (result.getErrCode() < 0) {
            return result;
        }
        // 执行添加操作
        for (int j = 0; j < insIbsUnionParm.getCount(); j++) {
            TParm tempParm = insIbsUnionParm.getRow(j);
            tempParm.setData("SEQ_NO", tempParm.getValue("SEQ_NO"));
            tempParm.setData("CHARGE_DATE", SystemTool.getInstance()
                             .getDateReplace(tempParm.getValue("CHARGE_DATE"), true)); // 明细录入时间
            tempParm.setData("ADDPAY_FLG", "Y"); // 增负
            for (int i = 0; i < nameIbsUpLoad.length; i++) { //校验是否为空
                if (tempParm.getValue(nameIbsUpLoad[i]).equals("null")
                    || tempParm.getValue(nameIbsUpLoad[i]).equals("")) {
                    tempParm.setData(nameIbsUpLoad[i], "");
                }
            }
            result = INSIbsUpLoadTool.getInstance()
                     .insertINSIbsUpload(tempParm);
            if (result.getErrCode() < 0) {
                return result;
            }
        }

        return result;
    } 
  
    /**
     * 明细汇总
     * @param tempParm TParm
     * @return TParm
     */
    public TParm onExeAdvance(TParm tempParm) {
        // 删除操作
        TParm parm = new TParm();
        parm.setData("CASE_NO", tempParm.getValue("CASE_NO")); // 就诊号
        parm.setData("YEAR_MON", tempParm.getValue("YEAR_MON")); // 期号
        parm.setData("START_DATE", tempParm.getValue("START_DATE")); // 开始
        parm.setData("END_DATE", tempParm.getValue("END_DATE")); // 结束时间
        TParm result = null;
        result = INSIbsOrderTool.getInstance().deleteINSIbsOrder(parm);       
        if (result.getErrCode() < 0) {
            return result;
        }
        TParm tParm = null; // 从IBS_OrdD读取数据 读取住院批价资料
        TParm ibsOrddParm = null; // 查询医嘱费用
        TParm orderParm = new TParm(); // 查询医嘱费用 执行后 重新整理的数据      
        TParm confirmTempParm = new TParm();       
        confirmTempParm.setData("INSBRANCH_CODE","07"); // 分中心
        confirmTempParm.setData("NHIHOSP_NO",tempParm.getValue("NHIHOSP_NO"));// 医保区域代码
        confirmTempParm.setData("ADM_SEQ",""); // 就诊顺序号 
        confirmTempParm.setData("CONFIRM_NO",tempParm.getValue("CONFIRM_NO")); //住院垫付顺序号
            // 从IBS_OrdD读取数据 读取住院批价资料
            tParm = INSIbsTool.getInstance().queryIbsOrdd(parm);
            if (tParm.getErrCode() < 0) {
                return tParm;
            }
           
            count = 1; // 重新累计数据 添加 INS_IBS_ORDER
            for (int j = 0; j < tParm.getCount(); j++) {
                ibsOrddParm = tParm.getRow(j);
                if(!getInsertIbsOrder(tempParm, confirmTempParm, ibsOrddParm, orderParm)){
                	TParm errResult=new TParm();
                	errResult.setErr(-1,ibsOrddParm.getValue("ORDER_CODE")+"  "+
                			            ibsOrddParm.getValue("ORDER_DESC")+"医保码有问题");
                	return errResult;
                }
            }
            orderParm.setCount(tParm.getCount());
//           System.out.println("添加INS_IBS_ORDER 数据"+orderParm);
            // 添加INS_IBS_ORDER 数据
            for (int j = 0; j < orderParm.getCount(); j++) {
                TParm ibsOrder = orderParm.getRow(j);
//                System.out.println("ibsOrder::::"+ibsOrder);
                // 校验为空
                for (int k = 0; k < nameIbsOrder.length; k++) {
                    if (null == ibsOrder.getValue(nameIbsOrder[k]) ||
                        ibsOrder.getValue(nameIbsOrder[k]).equals("null")
                        || ibsOrder.getValue(nameIbsOrder[k]).equals("")) {
                        ibsOrder.setData(nameIbsOrder[k], "");
                    }
                }
                result = INSIbsOrderTool.getInstance().insertINSIbsOrder(
                        ibsOrder);
                if (result.getErrCode() < 0) {               
                    break;
                }
            }
            // 明细上传数据
            TParm insIbsUnionParm = INSIbsOrderTool.getInstance()
                                    .queryInsIbsDUnion(parm);
            if (insIbsUnionParm.getErrCode() < 0) {
                return result;
            }
//            System.out.println("明细上传数据insIbsUnionParm:::"+insIbsUnionParm);
            // 添加 INS_IBS_UPLOAD_ADVANCE 表操作
            result = onApplyInsertIbsUpLoadAdvance(
            		confirmTempParm, insIbsUnionParm,tempParm);
            if (result.getErrCode() < 0) {
            
                return result;
            }
        return result;
    }   
    /**
     * 添加 INS_IBS_UPLOAD_ADVANCE（住院垫付使用）
     * @return TParm
     */
    private TParm onApplyInsertIbsUpLoadAdvance(
    		TParm confirmTempParm,TParm insIbsUnionParm,TParm sysParm) {

        TParm result = new TParm();
        // 删除老数据
        if (insIbsUnionParm.getCount() > 0) {
        	String sql =" DELETE FROM INS_IBS_UPLOAD_ADVANCE"+ 
            " WHERE CONFIRM_NO ='"+ confirmTempParm.getValue("CONFIRM_NO") + "'";
        	result = new TParm(TJDODBTool.getInstance().update(sql));
        }
        if (result.getErrCode() < 0) {
            return result;
        }
        // 执行添加操作  
        for (int j = 0; j < insIbsUnionParm.getCount(); j++) {
        	 TParm data = new TParm();
            TParm tempParm = insIbsUnionParm.getRow(j);
            data.setData("CONFIRM_NO", confirmTempParm.getValue("CONFIRM_NO"));//垫付住院顺序号
            data.setData("SEQ_NO", j + 1);//序号
            data.setData("REGION_CODE", tempParm.getValue("REGION_CODE"));//区域
            data.setData("CHARGE_DATE", SystemTool.getInstance()
                             .getDateReplace(tempParm.getValue("CHARGE_DATE"), true)); // 明细录入时间
            data.setData("ORDER_CODE", tempParm.getValue("ORDER_CODE"));//医嘱编码
            data.setData("ORDER_DESC", tempParm.getValue("ORDER_DESC"));//医嘱名称
            data.setData("NHI_ORDER_CODE", tempParm.getValue("NHI_ORDER_CODE"));//三目医保编码
            data.setData("PRICE", tempParm.getDouble("PRICE"));//单价
            data.setData("QTY", tempParm.getInt("QTY"));//数量
            data.setData("TOTAL_AMT", tempParm.getDouble("TOTAL_AMT"));//总金额           
            //获得累计增付标志
            String SQL =" SELECT LJZFBZ FROM INS_RULE"+
                        " WHERE SFXMBM = '"+ tempParm.getValue("NHI_ORDER_CODE") + "'";
            TParm LJZF = new TParm(TJDODBTool.getInstance().select(SQL));
            if (LJZF.getCount()>0) 
               data.setData("ADDPAY_FLG", LJZF.getValue("LJZFBZ",0).equals("1")? "Y" : "N");
            else           	
               data.setData("ADDPAY_FLG", "N");
            data.setData("NHI_ORD_CLASS_CODE", tempParm.getValue("NHI_ORD_CLASS_CODE"));//统计代码
            data.setData("HYGIENE_TRADE_CODE", tempParm.getValue("HYGIENE_TRADE_CODE"));//批准文号  
//        	System.out.println("data============"+data);
            
         // 添加 INS_IBS_UPLOAD_ADVANCE 表操作 
   		 String sql= " INSERT INTO INS_IBS_UPLOAD_ADVANCE("+
   		 " CONFIRM_NO,SEQ_NO, REGION_CODE,CHARGE_DATE,ORDER_CODE,"+ 
   		 " ORDER_DESC,NHI_ORDER_CODE,PRICE,QTY,"+
   		 " TOTAL_AMT,ADDPAY_FLG,NHI_ORD_CLASS_CODE,"+
   		 " HYGIENE_TRADE_CODE,OPT_USER,OPT_DATE,OPT_TERM) " +
   		 " VALUES('"+ data.getValue("CONFIRM_NO")+ "'," +
   		 " "+ data.getValue("SEQ_NO")+ "," +
   		 " '"+ data.getValue("REGION_CODE")+ "'," +
   		 " to_date('"+ data.getValue("CHARGE_DATE")+"','yyyyMMddHH24MISS')," +
   		 " '"+ data.getValue("ORDER_CODE")+ "'," +
   		 " '"+ data.getValue("ORDER_DESC")+ "'," +
   		 " '"+ data.getValue("NHI_ORDER_CODE")+ "'," +
   		 " "+ data.getDouble("PRICE")+ "," +
   		 " "+ data.getInt("QTY")+ "," +
   		 " "+ data.getDouble("TOTAL_AMT")+ "," +  
   		 " '"+ data.getValue("ADDPAY_FLG")+ "'," +
   		 " '"+ data.getValue("NHI_ORD_CLASS_CODE")+ "'," +
   		 " '"+ data.getValue("HYGIENE_TRADE_CODE")+ "'," +
   		 " '"+ sysParm.getValue("OPT_USER")+ "',SYSDATE," +
   		 " '"+ sysParm.getValue("OPT_TERM")+ "')";
//   		 System.out.println("sql:=============="+sql);
   		result = new TParm(TJDODBTool.getInstance().update(sql));  
        if (result.getErrCode() < 0) {
               return result;
            }
        }
        return result;
    }
    /**
     * 修改保存费用汇总后明细数据（住院垫付使用）
     * @param parm TParm
     * @return TParm
     */
    public TParm updateUpLoadAdvance(TParm parm) {
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount(); i++) {
            TParm temp = parm.getRow(i);
            if (null != temp.getValue("FLG")
                && temp.getValue("FLG").length() > 0
                && temp.getValue("SEQ_NO").length() > 0) { // 除去合计数据
                temp.setData("OPT_USER", parm.getValue("OPT_USER"));
                temp.setData("OPT_TERM", parm.getValue("OPT_TERM"));
                temp.setData("REGION_CODE", parm.getValue("REGION_CODE"));
                temp.setData("CHARGE_DATE", SystemTool.getInstance()
                             .getDateReplace(temp.getValue("CHARGE_DATE"), true)); // 明细账日期
                //校验是否为空值
                for (int j = 0; j < nameIbsUpLoadAdvance.length; j++) {
                    if (null == temp.getValue(nameIbsUpLoadAdvance[j]) ||
                        temp.getValue(nameIbsUpLoadAdvance[j]).equals("null")
                        || temp.getValue(nameIbsUpLoadAdvance[j]).equals("")) {
                        temp.setData(nameIbsUpLoadAdvance[j], "");
                    }
                }
                if (temp.getBoolean("FLG")) { // 添加操作
                	 String sql= " INSERT INTO INS_IBS_UPLOAD_ADVANCE("+
               		 " CONFIRM_NO,SEQ_NO, REGION_CODE,CHARGE_DATE,ORDER_CODE,"+ 
               		 " ORDER_DESC,NHI_ORDER_CODE,PRICE,QTY,"+
               		 " TOTAL_AMT,ADDPAY_FLG,NHI_ORD_CLASS_CODE,"+
               		 " HYGIENE_TRADE_CODE,OPT_USER,OPT_DATE,OPT_TERM) " +
               		 " VALUES('"+ temp.getValue("CONFIRM_NO")+ "'," +
               		 " "+ temp.getValue("SEQ_NO")+ "," +
               		 " '"+ temp.getValue("REGION_CODE")+ "'," +
               		 " to_date('"+ temp.getValue("CHARGE_DATE")+"','yyyyMMddHH24MISS')," +
               		 " '"+ temp.getValue("ORDER_CODE")+ "'," +
               		 " '"+ temp.getValue("ORDER_DESC")+ "'," +
               		 " '"+ temp.getValue("NHI_ORDER_CODE")+ "'," +
               		 " "+ temp.getDouble("PRICE")+ "," +
               		 " "+ temp.getInt("QTY")+ "," +
               		 " "+ temp.getDouble("TOTAL_AMT")+ "," +  
               		 " '"+ temp.getValue("ADDPAY_FLG")+ "'," +
               		 " '"+ temp.getValue("NHI_ORD_CLASS_CODE")+ "'," +
               		 " '"+ temp.getValue("HYGIENE_TRADE_CODE")+ "'," +
               		 " '"+ temp.getValue("OPT_USER")+ "',SYSDATE," +
               		 " '"+ temp.getValue("OPT_TERM")+ "')";
//               		 System.out.println("sql:=============="+sql);
               		result = new TParm(TJDODBTool.getInstance().update(sql));
                } else {
                    // 修改操作
                	 String sql= " UPDATE INS_IBS_UPLOAD_ADVANCE SET"+
                	" CHARGE_DATE=TO_DATE('"+ temp.getValue("CHARGE_DATE")+"','YYYYMMDDHH24MISS'),"+ 
                	" NHI_ORDER_CODE='"+ temp.getValue("NHI_ORDER_CODE")+ "',"+
                	" ORDER_CODE='"+ temp.getValue("ORDER_CODE")+ "',"+ 	       
                	" ORDER_DESC='"+ temp.getValue("ORDER_DESC")+ "',"+
                	" PRICE="+ temp.getDouble("PRICE")+ ","+
                	" QTY="+ temp.getInt("QTY")+ ","+
                	" TOTAL_AMT="+ temp.getDouble("TOTAL_AMT")+ ","+ 
                	" ADDPAY_FLG='"+ temp.getValue("ADDPAY_FLG")+ "',"+ 
                	" HYGIENE_TRADE_CODE='"+ temp.getValue("HYGIENE_TRADE_CODE")+ "',"+ 
                	" NHI_ORD_CLASS_CODE='"+ temp.getValue("NHI_ORD_CLASS_CODE")+ "',"+
                	" OPT_USER='"+ temp.getValue("OPT_USER")+ "',"+
                	" OPT_DATE=SYSDATE,"+ 
                	" OPT_TERM='"+ temp.getValue("OPT_TERM")+ "'"+
                	" WHERE CONFIRM_NO='"+ temp.getValue("CONFIRM_NO")+ "'"+
                	" AND SEQ_NO="+ temp.getValue("SEQ_NO")+ "";
//                	 System.out.println("sql:=============="+sql); 
                	result = new TParm(TJDODBTool.getInstance().update(sql));
                }
            }
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }
    
    
}
