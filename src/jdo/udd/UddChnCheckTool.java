package jdo.udd;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jdo.ind.INDTool;
import jdo.odo.OpdRxSheetTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

/**
 *
 * <p>
 * Title: 住院药房退药登记TOOL
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author ehui 20090307
 * @version 1.0
 */
public class UddChnCheckTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static UddChnCheckTool instanceObject;
    /**
     * 得到实例
     * @return UddChnCheckTool
     */
    public static UddChnCheckTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new UddChnCheckTool();
        }
        return instanceObject;
    }

    /**
     * 构造器
     */
    public UddChnCheckTool() {
        setModuleName("udd\\UddChnMedCheckModule.x");
        onInit();
    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateOCheck(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("onUpdateOCheck", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMCheck(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("onUpdateMCheck", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * 取消审核更新ODI_ORDER
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateOUnCheck(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("onUpdateOUnCheck", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 取消审核更新ODI_DSPNM
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMUnCheck(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("onUpdateMUnCheck", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;

    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMDispense(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("onUpdateMDispense", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;

    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMDosage(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("onUpdateMDosage", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMedODosage(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedODosage");
        result = update("onUpdateMedODosage", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;

    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMedMDosage(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedMDosage"+parm);
        result = update("onUpdateMedMDosage", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
    * 更新长期静脉配液信息 ODI_DSPND
    * 20150401 wangjingchun add
    * @param parm
    * @param connection
    * @return
    */
   public TParm onUpdateIvaDMes(TParm parm, TConnection connection) {
       TParm result = new TParm();
       //System.out.println("onUpdateMedMDosage"+parm);
       result = update("onUpdateIvaDMes", parm, connection);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }
   
   /**
   * 更新临时，首日量静脉配液信息 ODI_DSPND
   * 20150401 wangjingchun add
   * @param parm
   * @param connection
   * @return
   */
  public TParm onUpdateIvaDSTFMes(TParm parm, TConnection connection) {
      TParm result = new TParm();
      //System.out.println("onUpdateMedMDosage"+parm);
      result = update("onUpdateIvaDSTFMes", parm, connection);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText()
              + result.getErrName());
          return result;
      }
      return result;
  }
   
   /**
    * 更新静脉配液信息 ODI_DSPNM
    * 20150401 wangjingchun add
    * @param parm
    * @param connection
    * @return
    */
   public TParm onUpdateIvaMMes(TParm parm, TConnection connection) {
       TParm result = new TParm();
       //System.out.println("onUpdateMedMDosage"+parm);
       result = update("onUpdateIvaMMes", parm, connection);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }
   
   public TParm onCancelIvaDSTFMes(TParm parm, TConnection connection){
	   TParm result = new TParm();
       //System.out.println("onUpdateMedMDosage"+parm);
       result = update("onCancelIvaDSTFMes", parm, connection);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateUnChargeMedMDosage(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedMDosage"+parm);
        result = update("onUpdateUnChargeMedMDosage", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新皮试药品批号
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdatePhaAnti(TParm parm,String PHA_SEQ,int SEQ_NO, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedMDosage"+parm);
        parm.setData("PHA_SEQ",PHA_SEQ);
        parm.setData("SEQ_NO",SEQ_NO);
        result = update("updatePhaAnti", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    } 
    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateCnclMedMDosage(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedMDosage"+parm);
        result = update("onUpdateCnclMedMDosage", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateUnChargeCnclMedMDosage(TParm parm,
                                                TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedMDosage"+parm);
        result = update("onUpdateUnChargeCnclMedMDosage", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateUnChargeCnclMedMDipense(TParm parm,
                                                 TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedMDosage"+parm);
        result = update("onUpdateUnChargeCnclMedMDispense", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMedMDispense(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedMDispense");
        result = update("onUpdateMedMDispense", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateUnChargeMedMDispense(TParm parm,
                                              TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedMDispense");
        result = update("onUpdateUnChargeMedMDispense", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMedDDispense(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedDDispense");
        result = update("onUpdateMedDDispense", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateUnChargeMedDDispense(TParm parm,
                                              TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedDDispense");
        result = update("onUpdateUnChargeMedDDispense", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateMedDDosage(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedDDosage");
        result = update("onUpdateMedDDosage", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateUnChargeMedDDosage(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedDDosage");
        result = update("onUpdateUnChargeMedDDosage", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateCnclMedDDosage(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedDDosage");
        result = update("onUpdateCnclMedDDosage", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateCnclMedDDispense(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedDDosage");
        result = update("onUpdateUnChargeCnclMedDDispense", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateCnclMedMDispense(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedDDosage");
        result = update("onUpdateUnChargeCnclMedMDispense", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateUnChargeCnclMedDDosage(TParm parm,
                                                TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedDDosage");
        result = update("onUpdateUnChargeCnclMedDDosage", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 长期摆药更新Order表
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUpdateO(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedDDosage");
        result = update("onUpdateMedDDosage", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 长期摆药更新Order表
     * @param parm
     * @param connection
     * @return
     */
    public TParm onInsertM(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedDDosage");
        result = update("onUpdateMedDDosage", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据给定的主键查询某一条医嘱是否已经配药
     * @param parm
     * @return
     */
    public boolean isDosage(TParm parm) {
        String caseNo = parm.getValue("CASE_NO");
        String orderNo = parm.getValue("ORDER_NO");
        int orderSeq = parm.getInt("ORDER_SEQ");
        boolean isDosage = TypeTool.getBoolean(TConfig.getSystemValue(
            "IS_DOSAGE"));
        String sql = "SELECT COUNT(CASE_NO) COUNTDOSAGE FROM ODI_DSPNM WHERE CASE_NO='#' AND ORDER_NO='#' AND ORDER_SEQ=# ";
        if (isDosage) {
            sql += "AND (PHA_DOSAGE_CODE IS NOT NULL OR PHA_DOSAGE_CODE<>'')";
        }
        else {
            sql +=
                "AND (PHA_DISPENSE_CODE IS NOT NULL OR PHA_DISPENSE_CODE<>'')";
        }
        sql = sql.replaceFirst("#", caseNo).replaceFirst("#", orderNo).
            replaceFirst("#", orderSeq + "");
//    	System.out.println("isDosage.sql="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            return false;
        }
        return result.getInt("COUNTDOSAGE", 0) < 1;
    }

    /**
     * 长期摆药更新Order表
     * @param parm
     * @param connection
     * @return
     */
    public TParm onInsertD(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedDDosage");
        result = update("onUpdateMedDDosage", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 将按病患分组
     * @param parm
     * @return
     */
    public Map groupByPatParm(TParm parm) {
        Map result = new HashMap();
        if (parm == null) {
            return null;
        }
        int count = parm.getCount();
        if (count < 1) {
            return null;
        }
        TParm temp = new TParm();
        String[] names = parm.getNames();
        if (names == null) {
            return null;
        }
        if (names.length < 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (String name : names) {
            sb.append(name).append(";");
        }
        try {
            sb.replace(sb.lastIndexOf(";"), sb.length(), "");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //luhai modify 2012-04-24 begin 
//        for (int i = 0; i < count; i++) {
//            //zhangyong20100820 ORDER_NO==>CASE_NO
//            String orderNo = parm.getValue("CASE_NO", i);
//            if (result.get(orderNo) == null) {
//                temp = new TParm();
//                result.put(orderNo, temp);
//            }
//            temp.addRowData(parm, i, sb.toString());
//        }
        TParm tranParm=new TParm();
        for (int i = 0; i < count; i++) {
			// zhangyong20100820 ORDER_NO==>CASE_NO
			String orderNo = parm.getValue("CASE_NO", i);
			if (result.get(orderNo) == null) {
				temp = new TParm();
				temp.addRowData(parm, i, sb.toString());
				result.put(orderNo, temp);
			}else{
				tranParm=(TParm)result.get(orderNo);
				tranParm.addRowData(parm, i, sb.toString());
				result.put(orderNo, tranParm);
			}
		}
      //luhai modify 2012-04-24 end
        return result;
    }

    /**
     * 将药品按药房和ORDER_CODE GROUP BY
     * @param parm
     * @return
     */
    public TParm groupByStockParm(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            return result;
        }
        int count = parm.getCount();
        if (count < 1) {
            return result;
        }
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        for (int i = 0; i < count; i++) {
            String orderCode = parm.getValue("ORDER_CODE", i);
            String orgCode = parm.getValue("EXEC_DEPT_CODE", i);
            boolean drugFlg=INDTool.getInstance().isDrug(orderCode);
            if (parm.getValue("TAKEMED_ORG", i).equals("1") && !drugFlg) {//wanglong add 20150423
                orgCode = parm.getValue("STATION_CODE", i);
            }
            String key = orderCode + orgCode;
            Vector vctKey = (Vector) result.getData("KEY");
            if (vctKey == null) {
                vctKey = new Vector();
            }
//    		System.out.println("vctKey="+vctKey);

            int index = vctKey.indexOf(key);
            if (index < 0) {
                result.addData("ORDER_CODE", orderCode);
                result.addData("ORG_CODE", orgCode);
                result.addData("DOSAGE_QTY", parm.getData("DOSAGE_QTY", i));
                result.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
                result.addData("MR_NO", parm.getValue("MR_NO", i));
                result.addData("ORDER_DESC",
                               parm.getValue("ORDER_DESC", i) + " (" +
                               parm.getValue("SPECIFICATION", i) + ") " +
                               parm.getValue("GOODS_DESC", i));
                result.addData("OPT_USER", parm.getValue("OPT_USER", i));
                result.addData("OPT_TERM", parm.getValue("OPT_TERM", i));
                result.addData("OPT_DATE", now);
                result.addData("DOSAGE_UNIT", parm.getValue("DOSAGE_UNIT", i));
                result.addData("OWN_PRICE", parm.getDouble("OWN_PRICE", i));
                result.addData("KEY", orderCode + orgCode);
                result.addData("BATCH_NO", parm.getValue("BATCH_NO", i));//====pangben 2015-4-20 皮试药品显示批号
                result.addData("TAKEMED_ORG", parm.getValue("TAKEMED_ORG", i));//wanglong add 20150423
            }
            else {
                double oldDosage = result.getDouble("DOSAGE_QTY", index);
                double newDosage = parm.getDouble("DOSAGE_QTY", i);
                result.setData("DOSAGE_QTY", index, oldDosage + newDosage);
                double oldPrice = result.getDouble("OWN_PRICE", index);
                double newPrice = parm.getDouble("OWN_PRICE", i);
                result.setData("OWN_PRICE", index, oldPrice + newPrice);
            }
        }
        result.setCount(result.getCount("KEY"));
        return result;
    }

    /**
     * 取得处方签数据
     * @param rxParm
     * @param realDeptCode
     * @param rxType
     * @param odo
     * @return
     */
    public TParm getOrderPrintParm(String realDeptCode, String rxType,
                                   TParm odo, String rxNo) {

        TParm inParam = new TParm();
        String text = "TEXT";

        String sql = "SELECT A.* ,B.PAT_NAME,B.SEX_CODE,B.BIRTH_DATE FROM ADM_INP A,SYS_PATINFO B WHERE A.CASE_NO='" +
            odo.getValue("CASE_NO") + "' AND A.MR_NO=B.MR_NO";
        TParm admParm = new TParm(TJDODBTool.getInstance().select(sql));
        String ctz1Code = admParm.getValue("CTZ1_CODE", 0);

        String patName = admParm.getValue("PAT_NAME", 0);

        inParam.setData("ADDRESS", text,
                        "OpdChnOrderSheet From " + Operator.getID() +
                        " To LPT1");
        inParam.setData("PRINT_TIME", text,
                        StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                             "yyyy/MM/dd HH:mm:ss"));
        inParam.setData("HOSP_NAME", text,
                        OpdRxSheetTool.getInstance().getHospFullName());
        inParam.setData("SHEET_NAME", text, "");
        inParam.setData("ORG_CODE", text, odo.getValue("ORG_CODE"));
        inParam.setData("PAY_TYPE", text,
                        OpdRxSheetTool.getInstance().getPayTypeName(ctz1Code));
        inParam.setData("MR_NO", text, "病案号:" + odo.getValue("MR_NO"));
        inParam.setData("PAT_NAME", text, "姓名:" + patName);
        String sex = "1".equalsIgnoreCase(admParm.getValue("SEX_CODE", 0)) ?
            "男" : "女";
        inParam.setData("SEX_CODE", text, "性别:" + sex);
        inParam.setData("BIRTHDAY", text,
                        "出生日期:" +
                        StringTool.getString(
                        admParm.getTimestamp("BIRTH_DATE", 0), "yyyy-MM-dd"));
        String age = OdoUtil.showAge(admParm.getTimestamp("BIRTH_DATE", 0),
                                     SystemTool.getInstance().getDate());
        inParam.setData("AGE", text, "年龄:" + age);
        inParam.setData("DEPT_CODE", text,
                        "科别:" +
                        OpdRxSheetTool.getInstance().getDeptName(realDeptCode));
        inParam.setData("CLINIC_ROOM", text, "");
        inParam.setData("DR_CODE", text, "医生:" + odo.getValue("DR_NAME"));
        inParam.setData("ADM_DATE", text, "就诊时间:" + odo.getValue("ORDER_DATE"));
        inParam.setData("SESSION_CODE", text, "");
        inParam.setData("BAR_CODE", text, odo.getValue("MR_NO"));
        inParam.setData("ORDER_TYPE", text, "中医");
        inParam.setData("RX_NO", rxNo);
        inParam.setData("CASE_NO", odo.getValue("CASE_NO"));
        inParam.setData("RX_TYPE", rxType);
        sql = "SELECT   ORDER_DESC,MEDI_QTY,TOT_AMT,D.FREQ_TIMES||'次/'||D.CYCLE||'天' FREQ_CODE,A. TAKE_DAYS,"
            +
            "         B.CHN_DESC DCTAGENT_CODE,'' DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_AMT"
            +
            "  FROM   ODI_DSPNM A,SYS_DICTIONARY B, SYS_DICTIONARY C,SYS_PHAFREQ D"
            + "  WHERE   RX_NO = '"
            + rxNo
            + "'"
            + "         AND B.GROUP_ID='PHA_DCTAGENT'"
            + "         AND A.DCTAGENT_CODE=B.ID"
            + "         AND C.GROUP_ID='PHA_DCTEXCEP'"
            + "         AND A.DCTEXCEP_CODE IS NULL"
            + "         AND A.FREQ_CODE=D.FREQ_CODE" +
            " 	UNION" +
            "	SELECT   DISTINCT ORDER_DESC,MEDI_QTY,TOT_AMT,D.FREQ_TIMES || '次/' || D.CYCLE || '天' FREQ_CODE,A.TAKE_DAYS," +
            "                  B.CHN_DESC DCTAGENT_CODE,C.CHN_DESC DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_AMT" +
            "   FROM   ODI_DSPNM A,SYS_DICTIONARY B,SYS_DICTIONARY C,SYS_PHAFREQ D" +
            "   WHERE       RX_NO = '" + rxNo + "'" +
            "         AND B.GROUP_ID = 'PHA_DCTAGENT'" +
            "         AND A.DCTAGENT_CODE = B.ID" +
            "         AND C.GROUP_ID = 'PHA_DCTEXCEP'" +
            "         AND A.DCTEXCEP_CODE = C.ID" +
            "         AND A.FREQ_CODE = D.FREQ_CODE";
//		System.out.println("sql==============" + sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getErrCode() != 0) {
            //System.out.println("没有数据");
            return parm;
        }

//		 System.out.println("tableparm=========="+parm);
        int count = parm.getCount();
        text = "TEXT";
        double totAmtDouble = 0.0;
//		Systme.out.printl
        for (int i = 0; i < count; i++) {
            String row = (i / 4 + 1) + "";
//			System.out.println("row==========="+row);
            String column = (i % 4 + 1) + "";
//			System.out.println("column========"+column);
            totAmtDouble += parm.getDouble("TOT_AMT", i);
            inParam.setData("ORDER_DESC" + row + column, text, parm.getValue(
                "ORDER_DESC", i));
            inParam.setData("MEDI_QTY" + row + column, text, parm.getValue(
                "MEDI_QTY", i)
                            + "克");
            inParam.setData("DCTAGENT" + row + column, text, parm.getValue(
                "DCTEXCEP_CODE", i));
        }
        inParam.setData("TAKE_DAYS", text, "付数："
                        + parm.getValue("TAKE_DAYS", 0) + "付");
        inParam.setData("FREQ_CODE", text, "频次："
                        + parm.getValue("FREQ_CODE", 0));
        inParam.setData("ROUTE_CODE", text, "方法："
                        + parm.getValue("DCTAGESNT_CODE", 0));
        inParam.setData("DCT_TAKE_QTY", text, "每次服用量："
                        + parm.getValue("DCT_TAKE_QTY", 0) + "ml");
        inParam.setData("PACKAGE_TOT", text, "每付总克数："
                        + parm.getValue("PACKAGE_AMT", 0) + "克");
//		System.out.println("inPararm="+inParam);
        return inParam;
    }
    /**
     * 更新住院发药详细信息
     * luhai 2012-1-28 add 
     * @param parm
     * @param connection
     * @return
     */
    public TParm updateUDDDispenseDetail(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("onUpdateMedDDosage");
        result = update("onUpdateMedDispenseDetail", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新住院主表BAR_CODE
     * @param parm
     * @param connection
     * @return
     */
    public TParm updateUddMBarCode(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("updateUddMBarCode", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
   /**
    * 更新住院细表BAR_CODE
    * @param parm
    * @param connection
    * @return
    */
    public TParm updateUddDBarCode(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("updateUddDBarCode", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 插入一条新的长期处置向ODI_DSPND  
     * @param parm TParm
     * @return TParm
     */
    public TParm insertOdiDspnd(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //执行module上的insert update delete用update
        result = update("insertOdiDspnd", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 插入一条新的长期处置向ODI_DSPNM 
     * @param parm TParm
     * @return TParm
     */
    public TParm insertOdiDspnm(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //执行module上的insert update delete用update
        result = update("insertOdiDspnM", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新ODI_ORDER
     * @param parm TParm
     * @return TParm
     */
    public TParm updateOdiOrder(TParm parm, TConnection connection) {

        TParm result = new TParm();
        //执行module上的insert update delete用update
        result = update("updateOdiOrder", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新国药传送注记方法
     * @param parm
     * @return
     */
    public TParm updateGYWSSendFlg(TParm parm,String flg){
    	TParm result=new TParm();
    	if(parm==null)
    		return result;
    	int count=parm.getCount("CASE_NO");
    	for(int i=0;i<count;i++){
    		String caseNo=parm.getValue("CASE_NO", i);
    		String orderNo=parm.getValue("ORDER_NO", i);
    		String orderSeq=parm.getValue("ORDER_SEQ", i);
    		String startDttm=parm.getValue("START_DTTM", i);
            String sql="UPDATE ODI_DSPNM SET GYWSSEND_FLG='"+flg+"' WHERE CASE_NO='"+caseNo+"' AND ORDER_NO='"
            +orderNo+"' AND ORDER_SEQ='"+orderSeq+"' AND START_DTTM='"+startDttm+"'";
//            System.out.println("=========新国药传送注记============="+sql);
            result=new TParm(TJDODBTool.getInstance().update(sql));
            if(result.getErrCode()<0){
            	 err("ERR:" + result.getErrCode() + result.getErrText()
                         + result.getErrName());
            	return result;
            }
    	}
    	return result;  	
    }
}
