package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.bms.BMSApplyMTool;
import jdo.sys.Pat;
import com.javahis.util.StringUtil;
import java.sql.Timestamp;
import jdo.sys.SystemTool;

import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import jdo.sys.Operator;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.data.TNull;

/**
 * <p>
 * Title: 病患检查记录
 * </p>
 *
 * <p>
 * Description: 病患检查记录
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSPatCheckInfoControl
    extends TControl {

    public BMSPatCheckInfoControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm parm = (TParm) obj;
            this.setValue("APPLY_NO", parm.getValue("APPLY_NO"));
            this.onApplyNoAction();
        }
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if ("".equals(this.getValueString("APPLY_NO"))) {
            this.messageBox("不存在申请单号");
            return;
        }
        
        //add by yangjj 
        //判断应做抗体筛检但未做的给出提示
        TCheckBox SIFT_FLAG = (TCheckBox) this.getComponent("SIFT_FLAG");
        if(SIFT_FLAG.isSelected() && "".equals(this.getValueString("SIFT_RESU"))){
        	this.messageBox("未执行抗体筛检");
        	//return;
        }
        
        //未做正反定型的给出提出
        if("".equals(this.getValueString("TEST_BLD_A")) || "".equals(this.getValueString("TEST_BLD_B"))){
        	this.messageBox("需执行正反定型血型鉴定");
        	//return ;
        }
        
        //判断正反定型结果是否一致
        if(!this.getValueString("TEST_BLD_A").equals(this.getValueString("TEST_BLD_B"))){
        	this.messageBox("受血方正反定型血型鉴定结果不一致");
        	//return ;
        }
        
        //add by yangjj 2015417不选择RH血型不允许保存
        if(((!this.getRadioButton("RH_A").isSelected()) && (!this.getRadioButton("RH_B").isSelected())) || this.getRadioButton("RH_C").isSelected()){
        	this.messageBox("请选择RH血型！");
        	return ;
        }
        
        
        
        
        TParm parm = new TParm();
        // 判断是否更新病患血型
        Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
        String blood_type = pat.getBloodType();
        if (!blood_type.equals(this.getValueString("TEST_BLD_B"))) {
            if (this.messageBox("提示", "血型代码与病患血型不合，是否保存", 2) == 0) {
//                parm.setData("BLOOD_TYPE", this.getValueString("TEST_BLD_B"));
//                parm.setData("BLOOD_TYPE_OLD", pat.getBloodType());
//                parm.setData("RH_FLG",
//                             this.getRadioButton("RH_A").isSelected() ? "+" :
//                             "-");
//                parm.setData("MR_NO", this.getValueString("MR_NO"));
            }
            else {
                return;
            }
        }

        parm.setData("BLOOD_TYPE", this.getValueString("TEST_BLD_B"));
        parm.setData("BLOOD_TYPE_OLD", pat.getBloodType());
        
        String rh = "";
        if(this.getRadioButton("RH_A").isSelected()){
        	rh = "+";
        }else if(this.getRadioButton("RH_B").isSelected()){
        	rh = "-";
        }else{
        	this.messageBox("请选择RH血型！");
        	return;
        }
        parm.setData("RH_FLG",rh);
        parm.setData("MR_NO", this.getValueString("MR_NO"));
        
        // 更新备血单信息
        Timestamp date = SystemTool.getInstance().getDate();
        parm.setData("APPLY_NO", this.getValue("APPLY_NO"));
        parm.setData("ANTI_A", this.getValue("ANTI_A"));
        parm.setData("ANTI_A_DATE", date);
        parm.setData("ANTI_B", this.getValue("ANTI_B"));
        parm.setData("ANTI_B_DATE", date);
        parm.setData("ANTI_D", this.getValue("ANTI_D"));
        parm.setData("ANTI_D_DATE", date);
        parm.setData("A1_CELL", this.getValue("A1_CELL"));
        parm.setData("A1_CELL_DATE", date);
        parm.setData("B_CELL", this.getValue("B_CELL"));
        parm.setData("B_CELL_DATE", date);
        parm.setData("SI", this.getValue("SI"));
        parm.setData("SII", this.getValue("SII"));
        parm.setData("SIII", this.getValue("SIII"));
        parm.setData("SI_III_DATE", date);
        parm.setData("TEST_BLD", this.getValue("TEST_BLD_A"));
        parm.setData("SIFT_RESU", this.getValue("SIFT_RESU"));
        if(SIFT_FLAG.isSelected()){
        	parm.setData("SIFT_FLG", "Y");
        }else{
        	parm.setData("SIFT_FLG", "N");
        }
        //parm.setData("SIFT_FLG", "Y");
        parm.setData("SIFT_DATE", date);
        parm.setData("HBSAG", this.getValue("HBSAG"));
        parm.setData("HBSAG_DATE", date);
        parm.setData("HCVAB", this.getValue("HCVAB"));
        parm.setData("HCVAB_DATE", date);
        parm.setData("HIV", this.getValue("HIV"));
        parm.setData("HIV_DATE", date);
        parm.setData("HTIV", this.getValue("HTIV"));
        parm.setData("HTIV_DATE", date);
        
        //add by yangjj 20150519增加梅毒属性
        parm.setData("SY",this.getValue("SY"));
        
        parm.setData("TEST_USER", Operator.getID());
        parm.setData("TEST_DATE", date);
        parm.setData("PRT_FLG", "Y");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());

        TParm result = TIOM_AppServer.executeAction(
            "action.bms.BMSBloodAction", "onUpdatePatCheckInfo", parm);
        // 主项保存判断
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        if ("".equals(this.getValueString("APPLY_NO"))) {
            this.messageBox("不存在申请单号");
            return;
        }
        TParm parm = new TParm();
        // 更新备血单信息
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        parm.setData("APPLY_NO", this.getValue("APPLY_NO"));
        parm.setData("ANTI_A", "");
        parm.setData("ANTI_A_DATE", tnull);
        parm.setData("ANTI_B", "");
        parm.setData("ANTI_B_DATE", tnull);
        parm.setData("ANTI_D", "");
        parm.setData("ANTI_D_DATE", tnull);
        parm.setData("A1_CELL", "");
        parm.setData("A1_CELL_DATE", tnull);
        parm.setData("B_CELL", "");
        parm.setData("B_CELL_DATE", tnull);
        parm.setData("SI", "");
        parm.setData("SII", "");
        parm.setData("SIII", "");
        parm.setData("SI_III_DATE", tnull);
        parm.setData("TEST_BLD", "");
        parm.setData("SIFT_RESU", "");
        parm.setData("SIFT_FLG", "");
        parm.setData("SIFT_DATE", tnull);
        parm.setData("HBSAG", "");
        parm.setData("HBSAG_DATE", tnull);
        parm.setData("HCVAB", "");
        parm.setData("HCVAB_DATE", tnull);
        parm.setData("HIV", "");
        parm.setData("HIV_DATE", tnull);
        parm.setData("HTIV", "");
        parm.setData("HTIV_DATE", tnull);
        
        //add by yangjj 20150519增加梅毒属性
        parm.setData("SY","");
        
        parm.setData("TEST_USER", "");
        parm.setData("TEST_DATE", tnull);
        parm.setData("PRT_FLG", "N");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());

        TParm result = TIOM_AppServer.executeAction(
            "action.bms.BMSBloodAction", "onUpdatePatCheckInfo", parm);
        // 主项保存判断
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("删除失败");
            return;
        }
        this.messageBox("删除成功");
        onClear();

    }

    /**
     * 清空方法
     */
    public void onClear() {
    	this.getRadioButton("RH_A").setSelected(false);
        this.getRadioButton("RH_B").setSelected(false);
        this.getRadioButton("RH_C").setSelected(true);
    	String clearStr = "BAR_CODE;APPLY_NO;PRE_DATE;USE_DATE;END_DAYS;MR_NO;IPD_NO;"
            + "PAT_NAME;SEX;AGE;ID_NO;TEST_BLD;BLOOD_TEXT;ANTI_A;ANTI_B;"
            + "ANTI_D;A1_CELL;B_CELL;SI;SII;SIII;TEST_BLD_A;TEST_BLD_B;"
            + "SIFT_RESU;HBSAG;HCVAB;HIV;HTIV;SY";
        this.clearValue(clearStr);
        ((TCheckBox)this.getComponent("SIFT_FLAG")).setSelected(false);
    }

    /**
     * 历史记录查询
     */
    public void onHistoryQuery() {
        if ("".equals(this.getValueString("APPLY_NO"))) {
            this.messageBox("请输入备血单号");
            return;
        }
        if ("".equals(this.getValueString("MR_NO"))) {
            this.messageBox("请输入病案号");
            return;
        }

        TParm parm = new TParm();
        parm.setData("MR_NO", getValueString("MR_NO"));
        TParm result = BMSApplyMTool.getInstance().onApplyQuery(parm);
        if (result.getCount() <= 0) {
            this.messageBox("病人无上次检验资料");
            return;
        }
        int row = 0;
        for (int i = 0; i < result.getCount(); i++) {
            if (getValueString("APPLY_NO").equals(result.getValue("APPLY_NO", i))) {
                row = i - 1;
                break;
            }
        }
        if (row < 0) {
            this.messageBox("病人无上次检验资料");
            return;
        }
        TParm resultParm = result.getRow(row);
        this.setValue("APPLY_NO",resultParm.getValue("APPLY_NO"));
        this.onApplyNoAction();
    }

    /**
     * 备血单号查询
     */
    public void onBloodNoQuery() {
        Object result = openDialog("%ROOT%\\config\\bms\\BMSPatApplyNo.x");
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                this.messageBox("没有传回数据");
                return;
            }
            String apply_no = addParm.getValue("APPLY_NO");
            this.setValue("APPLY_NO", apply_no);
            this.onApplyNoAction();
        }
    }

    /**
     * 备血单号查询(回车事件)
     */
    public void onApplyNoAction() {
        //modify by yangjj 20150410 扫描血管标本号
    	
    	String barCode = this.getValueString("BAR_CODE");
    	
    	String apply_no = this.getValueString("APPLY_NO");
        
        TParm parm = new TParm();
        if(!"".equals(barCode)){
            
            String sql = getApplyNoByBarCodeSql(barCode);
        	TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql)) ;
        	if (sqlParm.getCount("APPLY_NO") == 0 || sqlParm.getCount() <= 0) {
                this.messageBox("标本条码不存在");
                this.setValue("BAR_CODE", "");
                onClear();
                return;
            }

        	apply_no = sqlParm.getValue("APPLY_NO", 0);
        }   
        
        
        	parm.setData("APPLY_NO", apply_no);
            TParm result = BMSApplyMTool.getInstance().onApplyQuery(parm);
            if (result.getCount("APPLY_NO") == 0 || result.getCount() <= 0) {
                this.messageBox("备血申请单不存在");
                this.setValue("BAR_CODE", "");
                onClear();
                return;
            }
            this.setValue("APPLY_NO", apply_no);
            this.setValue("PRE_DATE", result.getData("PRE_DATE", 0));
            this.setValue("USE_DATE", result.getData("USE_DATE", 0));
            this.setValue("END_DAYS", result.getData("END_DAYS", 0));
            this.setValue("MR_NO", result.getData("MR_NO", 0));
            this.setValue("IPD_NO", result.getData("IPD_NO", 0));
            // 查询病患信息
            Pat pat = Pat.onQueryByMrNo(result.getValue("MR_NO", 0));
            this.setValue("PAT_NAME", pat.getName());
            this.setValue("SEX", pat.getSexString());
            Timestamp date = SystemTool.getInstance().getDate();
            this.setValue("AGE",
                          StringUtil.getInstance().showAge(pat.getBirthday(),
                date));
            this.setValue("ID_NO", pat.getIdNo());
            this.setValue("TEST_BLD", result.getData("TEST_BLD", 0));
            this.setValue("ANTI_A", result.getData("ANTI_A", 0));
            this.setValue("ANTI_B", result.getData("ANTI_B", 0));
            this.setValue("ANTI_D", result.getData("ANTI_D", 0));
            this.setValue("A1_CELL", result.getData("A1_CELL", 0));
            this.setValue("B_CELL", result.getData("B_CELL", 0));
            this.setValue("SI", result.getData("SI", 0));
            this.setValue("SII", result.getData("SII", 0));
            this.setValue("SIII", result.getData("SIII", 0));
            this.setValue("TEST_BLD_A", result.getData("TEST_BLD", 0));
            this.setValue("TEST_BLD_B", pat.getBloodType());
            this.setValue("SIFT_RESU", result.getData("SIFT_RESU", 0));
            this.setValue("HBSAG", result.getData("HBSAG", 0));
            this.setValue("HCVAB", result.getData("HCVAB", 0));
            this.setValue("HIV", result.getData("HIV", 0));
            this.setValue("HTIV", result.getData("HTIV", 0));
            
            //add by yangjj 20150519增加梅毒属性
            this.setValue("SY", result.getData("SY", 0));
            
            if ("-".equals(pat.getBloodRHType())) {
                getRadioButton("RH_B").setSelected(true);
            }
            else if("+".equals(pat.getBloodRHType())) {
                getRadioButton("RH_A").setSelected(true);
            }else{
            	getRadioButton("RH_C").setSelected(true);
            }
            this.setValue("BLOOD_TEXT", pat.getBloodType());
            
            String siftFlag = result.getData("SIFT_FLG", 0)+"";
            if("Y".equals(siftFlag)){
            	TCheckBox SIFT_FLAG = (TCheckBox) this.getComponent("SIFT_FLAG");
            	SIFT_FLAG.setSelected(true);
            }
        
    }


    /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }
    
    public String getApplyNoByBarCodeSql(String barCode){
    	String sql = "";
    	sql += " SELECT APPLY_NO FROM ODI_ORDER WHERE MED_APPLY_NO = '"+barCode+"'";
    	return sql ;
    }

}
