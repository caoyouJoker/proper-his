package action.ins;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

//import org.apache.ws.xnh.XNHService;

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
 * Title:新农合联网结算
 * </p>
 * Description:新农合联网结算
 * Copyright: Copyright (c) 2017
 * </p>
 * @version 2.0
 */
public class XNHINSBalanceAction extends TAction {
	 private int count = 1; // 添加 INS_IBS_ORDER 累计个数
	 // 校验添加INS_IBS_ORDER数据是否为空
    private String[] nameIbsOrder = {"ADM_SEQ", "INSBRANCH_CODE",
                                    "HOSP_NHI_NO", "ORDER_CODE",
                                    "NHI_ORDER_CODE", "ORDER_DESC",
                                    "OWN_RATE", "DOSE_CODE", "STANDARD",
                                    "OP_FLG", "ADDPAY_FLG",
                                    "NHI_ORD_CLASS_CODE", "PHAADD_FLG",
                                    "CARRY_FLG","EXE_DEPT_CODE","DOSAGE_UNIT"};
    // INS_XNH_UPLOAD表修改界面数据时操作校验为空
    private String[] nameIbsUpLoadAdvance = {"ORDER_CODE", "ORDER_DESC",
                                        "NHI_ORDER_CODE", 
                                        "NHI_ORD_CLASS_CODE"};
    
    /**
     * 转明细
     * @param tempParm TParm
     * @return TParm
     */
    public TParm onExeXnh(TParm tempParm) {
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
        confirmTempParm.setData("CONFIRM_NO","");
        confirmTempParm.setData("CASE_NO",tempParm.getValue("CASE_NO"));
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
            // 添加 INS_XNH_UPLOAD 表操作
            result = onApplyInsertXnhUpLoad(
            		confirmTempParm, insIbsUnionParm,tempParm);
            if (result.getErrCode() < 0) {
            
                return result;
            }
        return result;
    }  
    /**
     * 添加 INS_XNH_UPLOAD
     * @return TParm
     */
    private TParm onApplyInsertXnhUpLoad(
    		TParm confirmTempParm,TParm insIbsUnionParm,TParm sysParm) {

        TParm result = new TParm();
        String listcode = "";
        String listdesc = "";
        // 删除老数据
        if (insIbsUnionParm.getCount() > 0) {
        	String sql =" DELETE FROM INS_XNH_UPLOAD"+ 
            " WHERE CASE_NO ='"+ confirmTempParm.getValue("CASE_NO") + "'";
        	result = new TParm(TJDODBTool.getInstance().update(sql));
        }
        if (result.getErrCode() < 0) {
            return result;
        }
        // 执行添加操作  
        for (int j = 0; j < insIbsUnionParm.getCount(); j++) {
        	 TParm data = new TParm();
            TParm tempParm = insIbsUnionParm.getRow(j);           
            int seqNo = j + 1;           
            String orderNo = confirmTempParm.getValue("CASE_NO")+ seqNo;
//        	System.out.println("orderNo============"+orderNo);
            data.setData("ORDER_NO", orderNo);//住院处方流水号
            data.setData("SEQ_NO", seqNo);//序号
            //获得新农合费用类别
            String sql = " SELECT HEXP_CODE FROM IBS_ORDD"+
                         " WHERE CASE_NO = '"+ confirmTempParm.getValue("CASE_NO") + "'"+
                         " AND ORDER_CODE = '"+ tempParm.getValue("ORDER_CODE") + "'";
            TParm datahexp  = new TParm(TJDODBTool.getInstance().select(sql));
//            System.out.println("datahexp============"+datahexp);
            String hexpCode = datahexp.getValue("HEXP_CODE", 0);
//            System.out.println("hexpCode============"+hexpCode);
            String sql1 = " SELECT XNH_CHARGE_CODE FROM SYS_CHARGE_HOSP"+
                          " WHERE CHARGE_HOSP_CODE = '"+ hexpCode + "'";
//            System.out.println("sql1============"+sql1);
            TParm xnhParm  = new TParm(TJDODBTool.getInstance().select(sql1)); 
//            System.out.println("xnhParm============"+xnhParm);
            String xnhCode = xnhParm.getValue("XNH_CHARGE_CODE", 0);      
            String sql2 = " SELECT ID,CHN_DESC FROM SYS_DICTIONARY "+
                          " WHERE GROUP_ID = 'XNH_CHARGE'"+
                          " AND ID = '"+ xnhCode + "'";
            TParm classParm  = new TParm(TJDODBTool.getInstance().select(sql2));
//            System.out.println("classParm============"+classParm);
            data.setData("CLASS_CODE", classParm.getValue("ID",0));//费用类别代码           
            data.setData("CLASS_DESC", classParm.getValue("CHN_DESC",0));//费用类别名称
            data.setData("NHI_ORDER_CODE", tempParm.getValue("NHI_ORDER_CODE"));//三目医保编码
            data.setData("ORDER_CODE", tempParm.getValue("ORDER_CODE"));//HIS系统项目代码
            data.setData("ORDER_DESC", tempParm.getValue("ORDER_DESC"));//HIS系统项目名称
            data.setData("DOSE_DESC", tempParm.getValue("DOSE_DESC"));//剂型
            data.setData("STANDARD", tempParm.getValue("STANDARD"));//规格
            data.setData("UNIN_DESC", "");//单位
            data.setData("PRICE", tempParm.getDouble("PRICE"));//单价
            data.setData("TOT_AMT", tempParm.getDouble("TOTAL_AMT"));//总金额
            data.setData("DR_DESC", "");//医生姓名             
            data.setData("CHARGE_DATE", SystemTool.getInstance()
                             .getDateReplace(tempParm.getValue("CHARGE_DATE"), true)); // 开单日期
            data.setData("PAY_QTY", 0);//付数
            data.setData("QTY", tempParm.getInt("QTY"));//数量
            data.setData("XNH_ORDER_CODE", tempParm.getValue("NHI_ORDER_CODE"));//农合项目编码    
            data.setData("XNH_ORDER_DESC", tempParm.getValue("ORDER_DESC"));//农合项目名称     
            
            String sql3 = " SELECT INSPAY_TYPE FROM SYS_FEE"+
                         " WHERE ORDER_CODE =  '" + tempParm.getValue("ORDER_CODE") + "'";
            TParm Parm = new TParm(TJDODBTool.getInstance().select(sql3));           
            if(Parm.getValue("INSPAY_TYPE",0).equals("A")||
               Parm.getValue("INSPAY_TYPE",0).equals("B")){
            data.setData("INS_AMT", tempParm.getDouble("TOTAL_AMT"));//可报销金额 
            listcode = "1";
            listdesc = "就医地目录内";
            }else{
            data.setData("INS_AMT", 0);//可报销金额 
            listcode = "2";
            listdesc = "就医地目录外";
            }
           // CREATE_DATE 创建日期(当前时间)
           // UPDATE_DATE 更新日期(当前时间)
            data.setData("CASE_NO", confirmTempParm.getValue("CASE_NO"));//住院登记流水号
            //data.setData("HOSP_CODE", XNHService.HOSPCODE);//就医机构代码
            //data.setData("HOSP_DESC", XNHService.HOSPNAME);//就医机构名称
            data.setData("IMPORT_FLG_CODE", "");//国产进口标识代码
            data.setData("IMPORT_FLG_DESC", "");//国产进口标识名称
            data.setData("DEDUCTION_AMT", 0);//扣减金额
            data.setData("DEDUCTION_REASON", "");//扣减原因
            data.setData("LIST_CODE", listcode);//目录属性
            data.setData("LIST_DESC", listdesc);//目录属性名称
            data.setData("BUY_SUBJECT_CODE", "");//集中采购项目编码                     
//        	System.out.println("data============"+data);           
         // 添加 INS_XNH_UPLOAD 表操作 
   		 String sql4= " INSERT INTO INS_XNH_UPLOAD("+
   		 " ORDER_NO,SEQ_NO,CLASS_CODE,CLASS_DESC," +
   		 " NHI_ORDER_CODE,ORDER_CODE,ORDER_DESC," +
   		 " DOSE_DESC,STANDARD,UNIN_DESC,PRICE,TOT_AMT,"+
   		 " DR_DESC,CHARGE_DATE,PAY_QTY,QTY,XNH_ORDER_CODE,"+
   		 " XNH_ORDER_DESC,INS_AMT,CREATE_DATE,UPDATE_DATE," +
   		 " CASE_NO,HOSP_CODE,HOSP_DESC,IMPORT_FLG_CODE," +
   		 " IMPORT_FLG_DESC,DEDUCTION_AMT,DEDUCTION_REASON," +
   		 " LIST_CODE,LIST_DESC,BUY_SUBJECT_CODE," +
   		 " OPT_USER,OPT_DATE,OPT_TERM) " +
   		 " VALUES('"+ data.getValue("ORDER_NO")+ "'," +
   		 " "+ data.getValue("SEQ_NO")+ "," +
   		 " '"+ data.getValue("CLASS_CODE")+ "'," +
   		 " '"+ data.getValue("CLASS_DESC")+ "'," +
   		 " '"+ data.getValue("NHI_ORDER_CODE")+ "'," +
   		 " '"+ data.getValue("ORDER_CODE")+ "'," +
   		 " '"+ data.getValue("ORDER_DESC")+ "'," +
   		 " '"+ data.getValue("DOSE_DESC")+ "'," +
   		 " '"+ data.getValue("STANDARD")+ "'," +
   		 " '"+ data.getValue("UNIN_DESC")+ "'," +
   		 " "+ data.getDouble("PRICE")+ "," +
   		 " "+ data.getDouble("TOT_AMT")+ "," + 
   		 " '"+ data.getValue("DR_DESC")+ "'," +
   		 " to_date('"+ data.getValue("CHARGE_DATE")+"','yyyyMMddHH24MISS')," +  		
   		 " "+ data.getInt("PAY_QTY")+ "," +
   		 " "+ data.getInt("QTY")+ "," +
   		 " '"+ data.getValue("XNH_ORDER_CODE")+ "'," +
   		 " '"+ data.getValue("XNH_ORDER_DESC")+ "'," +
   		 " "+ data.getDouble("INS_AMT")+ ",SYSDATE,SYSDATE," + 
   		 " '"+ data.getValue("CASE_NO")+ "'," +
   		 " '"+ data.getValue("HOSP_CODE")+ "'," +
   		 " '"+ data.getValue("HOSP_DESC")+ "'," +
   		 " '"+ data.getValue("IMPORT_FLG_CODE")+ "'," +
   		 " '"+ data.getValue("IMPORT_FLG_DESC")+ "'," +
   		 " "+ data.getDouble("DEDUCTION_AMT")+ "," + 
   		 " '"+ data.getValue("DEDUCTION_REASON")+ "'," +
   		 " '"+ data.getValue("LIST_CODE")+ "'," +
   		 " '"+ data.getValue("LIST_DESC")+ "'," +
   		 " '"+ data.getValue("BUY_SUBJECT_CODE")+ "'," +   		    		
   		 " '"+ sysParm.getValue("OPT_USER")+ "',SYSDATE," +
   		 " '"+ sysParm.getValue("OPT_TERM")+ "')";
//   		 System.out.println("sql:=============="+sql);
   		result = new TParm(TJDODBTool.getInstance().update(sql4));  
        if (result.getErrCode() < 0) {
               return result;
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
     * 修改保存费用汇总后明细数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updateXnhUpLoad(TParm parm) {
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
                	String orderNo = temp.getValue("CASE_NO")+temp.getValue("SEQ_NO");
                	 String sql= " INSERT INTO INS_XNH_UPLOAD("+
               		 " ORDER_NO,CASE_NO,SEQ_NO,CHARGE_DATE,ORDER_CODE,"+ 
               		 " ORDER_DESC,NHI_ORDER_CODE,PRICE,QTY,"+
               		 " TOT_AMT,CLASS_CODE,PAY_QTY,INS_AMT,DEDUCTION_AMT," +
               		 " CREATE_DATE,UPDATE_DATE,HOSP_CODE,HOSP_DESC," +
               		 " OPT_USER,OPT_DATE,OPT_TERM) " +
               		 " VALUES('"+ orderNo+ "'," +
               		 " '"+ temp.getValue("CASE_NO")+ "',"+
               		 " "+ temp.getValue("SEQ_NO")+ "," +
               		 " to_date('"+ temp.getValue("CHARGE_DATE")+"','yyyyMMddHH24MISS')," +
               		 " '"+ temp.getValue("ORDER_CODE")+ "'," +
               		 " '"+ temp.getValue("ORDER_DESC")+ "'," +
               		 " '"+ temp.getValue("NHI_ORDER_CODE")+ "'," +
               		 " "+ temp.getDouble("PRICE")+ "," +
               		 " "+ temp.getInt("QTY")+ "," +
               		 " "+ temp.getDouble("TOT_AMT")+ "," +  
               		 " '"+ temp.getValue("CLASS_CODE")+ "',0,0,0," +
               		 " SYSDATE,SYSDATE,'40','市、地区'," +
               		 " '"+ temp.getValue("OPT_USER")+ "',SYSDATE," +
               		 " '"+ temp.getValue("OPT_TERM")+ "')";
//               		 System.out.println("sql:=============="+sql);
               		result = new TParm(TJDODBTool.getInstance().update(sql));
                } else {
                    // 修改操作
                	 String sql= " UPDATE INS_XNH_UPLOAD SET"+
                	" CHARGE_DATE=TO_DATE('"+ temp.getValue("CHARGE_DATE")+"','YYYYMMDDHH24MISS'),"+ 
                	" NHI_ORDER_CODE='"+ temp.getValue("NHI_ORDER_CODE")+ "',"+
                	" ORDER_CODE='"+ temp.getValue("ORDER_CODE")+ "',"+ 	       
                	" ORDER_DESC='"+ temp.getValue("ORDER_DESC")+ "',"+
                	" PRICE="+ temp.getDouble("PRICE")+ ","+
                	" QTY="+ temp.getInt("QTY")+ ","+
                	" TOT_AMT="+ temp.getDouble("TOT_AMT")+ ","+ 
                	" CLASS_CODE='"+ temp.getValue("CLASS_CODE")+ "',"+
                	" OPT_USER='"+ temp.getValue("OPT_USER")+ "',"+
                	" OPT_DATE=SYSDATE,"+ 
                	" OPT_TERM='"+ temp.getValue("OPT_TERM")+ "'"+
                	" WHERE CASE_NO='"+ temp.getValue("CASE_NO")+ "'"+
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
