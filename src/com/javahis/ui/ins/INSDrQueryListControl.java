package com.javahis.ui.ins;

import jdo.ins.InsManager;
import com.dongyang.ui.TTable;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: 医保门特处方查询控制类</p>
 *
 * <p>Description: 医保门特处方查询控制类</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2012.02.17
 * @version 1.0
 */
public class INSDrQueryListControl extends TControl {
    String invNo = "";
    private TTable table1;
    private TTable table2;
    private int ins_type=0;

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        TParm parm = (TParm) getParameter();
        if (null == parm) {
            return;
        }
        String caseNo = parm.getValue("CASE_NO");
        TParm initParm = new TParm();
        ins_type=parm.getInt("INS_TYPE");
//        initParm.setData("OWN_NO","8000326865");
//        initParm.setData("DISEASE_CODE","4");
        initParm.setData("CASE_NO", caseNo);
        initPage(initParm);
    }

    /**
     * 初始化界面信息
     * @param parm TParm
     */
    public void initPage(TParm parm) {
        String selConfirmNo =
                " SELECT OWN_NO, DISEASE_CODE " +
                "   FROM REG_PATADM A, INS_MZ_CONFIRM B " +
                "   WHERE A.CASE_NO = '"+parm.getValue("CASE_NO")+"' " +
                "     AND A.REGION_CODE = B.REGION_CODE " +
                "     AND A.CONFIRM_NO = B.CONFIRM_NO ";
        TParm singleParm = new TParm(TJDODBTool.getInstance().select(
                selConfirmNo));
        if (singleParm.getErrCode() < 0) {
            this.messageBox(singleParm.getErrName() + singleParm.getErrText());
            return;
        }
        if(singleParm.getCount()<=0){
            this.messageBox("无门诊特处方数据");
            return;
        }
        String ownNo = singleParm.getValue("OWN_NO", 0);
        String diseaseCode = singleParm.getValue("DISEASE_CODE", 0);
        parm.setData("OWN_NO", ownNo);
        parm.setData("DISEASE_CODE", diseaseCode);
        //System.out.println("-------初始化-----" + parm);
        this.table1 = (TTable)this.getComponent("Table1");
        this.table2 = (TTable)this.getComponent("Table2");
        TParm result1=null;
        if (ins_type==3) {
        	result1 = this.DataDown_cmtd_I(parm);
		}else{
			 result1 = this.DataDown_mtd_I(parm);
		}
       
        if (result1.getErrCode() < 0) {
            this.messageBox(result1.getErrText()+result1.getErrName());
            return ;
        }
        TParm result2=null;
        if (ins_type==3) {
        	result2 = this.DataDown_cmtd_J(parm);
		}else{
			result2 = this.DataDown_mtd_J(parm);
		}
        
        if (result2.getErrCode() < 0) {
            this.messageBox(result2.getErrText()+result2.getErrName());
            return ;
        }
        result2.setCount(result2.getCount("CLASS1_CODE"));
        result1.setCount(result1.getCount("NHI_CODE"));
        table1.setParmValue(result1);
        table2.setParmValue(result2);

    }

    /**
     * 门特药品费限定台帐下载
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_mtd_I(TParm parm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_mtd");
        confInfoParm.setData("PLOT_TYPE", "I");
        //个人编码
        confInfoParm.addData("OWN_NO", parm.getValue("OWN_NO"));
        //门特病种编码
        confInfoParm.addData("DISEASE_CODE", parm.getValue("DISEASE_CODE"));
        confInfoParm.addData("PARM_COUNT", 2);
        result = InsManager.getInstance().safe(confInfoParm,"");
        //System.out.println("门特药品费限定台帐下载" + result);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }
    /**
     * 门特药品费限定台帐下载
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_cmtd_I(TParm parm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_cmtd");
        confInfoParm.setData("PLOT_TYPE", "I");
        //个人编码
        confInfoParm.addData("OWN_NO", parm.getValue("OWN_NO"));
        //门特病种编码
        confInfoParm.addData("DISEASE_CODE", parm.getValue("DISEASE_CODE"));
        confInfoParm.addData("PARM_COUNT", 2);
        result = InsManager.getInstance().safe(confInfoParm,"");
        //System.out.println("门特药品费限定台帐下载" + result);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }
    /**
     * 门特大类限定台帐下载
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_mtd_J(TParm parm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_mtd");
        confInfoParm.setData("PLOT_TYPE", "J");
        //个人编码
        confInfoParm.addData("OWN_NO", parm.getValue("OWN_NO"));
        //门特病种编码
        confInfoParm.addData("DISEASE_CODE", parm.getValue("DISEASE_CODE"));
        confInfoParm.addData("PARM_COUNT", 2);
        result = InsManager.getInstance().safe(confInfoParm,"");
        //System.out.println("门特大类限定台帐下载" + result);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }
    /**
     * 门特大类限定台帐下载
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_cmtd_J(TParm parm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_cmtd");
        confInfoParm.setData("PLOT_TYPE", "J");
        //个人编码
        confInfoParm.addData("OWN_NO", parm.getValue("OWN_NO"));
        //门特病种编码
        confInfoParm.addData("DISEASE_CODE", parm.getValue("DISEASE_CODE"));
        confInfoParm.addData("PARM_COUNT", 2);
        result = InsManager.getInstance().safe(confInfoParm,"");
        //System.out.println("门特大类限定台帐下载" + result);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }
}
