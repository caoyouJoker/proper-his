package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import jdo.spc.IndSysParmTool;
import jdo.spc.SPCCabinetTool;
import jdo.spc.SPCInStoreReginTool;
import jdo.spc.SPCInStoreTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>Title: 病区普药入智能柜</p>
 *
 * <p>Description:TODO </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class SPCInStoreRegionControl extends TControl {
	// 未取药
	TTable table ;

	/**
	 * 出库单号
	 */
	String dispenseNo ;
	
	/**
	 * 周转箱
	 */
	String boxEslId ;
	
	/**
	 * 未出库，已出库
	 */
	String radioN ,radioY;

	String ip;
	
	/**
	 * 返回结果值
	 */
	TParm result ;
	
	  // 返回结果集
    private TParm resultParm;
    
	/**
	 * 智能柜本机物理IP
	 */
	String guardIp;
	
	String userId ;

	/**初始化方法*/
	public void onInit() {
		super.onInit();
		table = (TTable) getComponent("TABLE");
		onInitData() ;
		
	}
	
	/**
	 * 初始化界面
	 */
	public void onInitData(){
		 
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			ip=addr.getHostAddress();//获得本机IP
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		TParm parm = new TParm();
		parm.setData("IP",ip);
		TParm result = SPCCabinetTool.getInstance().onQuery(parm);
		// add by wangbin 2014/11/24 非智能柜设备访问该页面时IP控件可编辑 START
		if (result.getCount() > 0) {
			guardIp = result.getValue("GUARD_IP", 0);
			setValue("CABINET_IP", result.getValue("CABINET_IP", 0));
			setValue("CABINET_ID", result.getValue("CABINET_ID", 0));
			setValue("CABINET_DESC", result.getValue("CABINET_DESC", 0));
			setValue("ORG_CHN_DESC", result.getValue("ORG_CHN_DESC", 0));
			setValue("OPT_USER", Operator.getName());
		} else {
			getTextField("CABINET_IP").setEnabled(true);
		}
		// add by wangbin 2014/11/24 非智能柜设备访问该页面时IP控件可编辑 END
		
	}
    
	
    /**-----------------------------------------------------*/

	/**出库单回车事件*/
	public void onDispenseNo() {
		 
    	dispenseNo = getValueString("DISPENSE_NO");
		onQuery();
    	//跳转到周转箱
    	this.getTextField("BOX_ESL_ID").grabFocus();
	}
	
	/**周转箱回车事件*/
	public void onBoxEslId() {
		boxEslId = getValueString("BOX_ESL_ID");
		onQuery() ;
		
	}
	
	/**
	 * 单选按钮 出库与未出库事件
	 */
	public void inOut(){
		onQuery();
	}
    
    /**查询*/
    public void onQuery() {
    	
    	table = (TTable) getComponent("TABLE");
    	table.acceptText() ;
    	
    	dispenseNo = getValueString("DISPENSE_NO");
    	boxEslId = getValueString("BOX_ESL_ID");
    	
    	TParm inParm = new TParm() ;
    	inParm.setData("DISPENSE_NO",dispenseNo);
    	inParm.setData("BOX_ESL_ID",boxEslId);
    	
    	if(this.getRadioButton("RADIO_N").isSelected()){
    		inParm.setData("IS_STORE","N");
    		result = SPCInStoreReginTool.getInstance().onQuery(inParm);
    		resultParm = result ;
    	}else if(this.getRadioButton("RADIO_Y").isSelected()){
    		//inParm.setData("UPDATE_FLG","3");
    		inParm.setData("IS_STORE","Y");
    		result = SPCInStoreReginTool.getInstance().onQuery(inParm);
    	}
    	if(result.getCount() < 0 ){
    		this.messageBox("没有查询到入库数据");
    		setValue("DISPENSE_NO", "");
    		table.setParmValue(new TParm());
    		return ;
    	}
    	
    	setValue("OUT_CHN_DESC", result.getValue("ORG_CHN_DESC",0));
		setValue("REQTYPE_CODE", getReqtype(result.getValue("REQTYPE_CODE",0)));
    	table.setParmValue(result);
    }
    
    /**
     * 入库更新D表为入库状态，M表UPDATE_FLG=3 IND_STOCK更新库存数
     */
    public boolean onSave(){
    	// add by wangbin 2141124 取不到智能柜编号时提示 START
    	if (StringUtils.isEmpty(getValueString("CABINET_ID"))) {
    		this.messageBox("智能柜编号不能为空");
    		return false;
    	}
    	// add by wangbin 2141124 取不到智能柜编号时提示 END
    	
    	dispenseNo = getValueString("DISPENSE_NO");
    	if(dispenseNo == null || dispenseNo.equals("")){
    		this.messageBox("出库单号不能为空");
    		return false;
    	}
    	table.acceptText() ;
    	 
    	if(table.getRowCount()  <=0 ){
    		this.messageBox("没有入库数据");
    		return false;
    	}
    	
    	// 密码判断
		if (!checkPW()) {
			return false;
		}
		
    	
    	TParm inParm = new TParm();
    	inParm.setData("DISPENSE_NO",dispenseNo);
    	
    	//根据出库单号查询IND_DISPENSEM表信息
    	TParm mParm = SPCInStoreReginTool.getInstance().onQueryDispenseM(inParm);
    	
    	if(mParm.getCount() <  0){
    		this.messageBox("没有入库数据");
    		return false;
    	}
    	
    	
    	//保存
        TParm parm  = onSave(mParm, resultParm, table);
        
        //组装IND_CBNSTOCK需要的参数
        TParm cbnParm = new TParm();
        int count = 0;
        Timestamp date = SystemTool.getInstance().getDate();
        for (int i = 0; i < table_d.getRowCount(); i++) {
        	cbnParm.setData("CABINET_ID", count, getValue("CABINET_ID"));
        	cbnParm.setData("ORDER_CODE", count,
                          resultParm.getValue("ORDER_CODE", i));
        	cbnParm.setData("BATCH_SEQ", count, resultParm.getValue(
                     "BATCH_SEQ",i));
        	cbnParm.setData("BATCH_NO", count,
                     table_d.getItemString(i, "BATCH_NO"));  
        	cbnParm.setData("VALID_DATE", count, table_d.getItemData(i,
             "VALID_DATE"));
        	cbnParm.setData("VERIFYIN_PRICE", count, resultParm.getDouble( 
             "STOCK_PRICE",i));
        	
        	String unitCode = resultParm.getValue("UNIT_CODE",i);
        	String dosageUnit = resultParm.getValue("DOSAGE_UNIT",i);
        	String stockUnit = resultParm.getValue("STOCK_UNIT",i);
        	double outQty = table_d.getItemDouble(i,"OUT_QTY" );
      	    double stockQty = resultParm.getDouble("STOCK_QTY",i);
      	    double dosageQty = resultParm.getDouble("DOSAGE_QTY",i);
      	    if(unitCode != null && unitCode.equals(stockUnit)){
      	    	outQty = outQty*stockQty*dosageQty;
      	    } 
      	   
        	cbnParm.setData("OUT_QTY", count, outQty);
        	cbnParm.setData("STOCK_QTY",count,resultParm.getDouble("STOCK_QTY",i));
        	cbnParm.setData("DOSAGE_QTY",count,resultParm.getDouble("DOSAGE_QTY",i));
        	
        	cbnParm.setData("SEQ_NO",count,resultParm.getInt("SEQ_NO",i));
        	
        	//表ind_cabstock 里的stock_unit
        	cbnParm.setData("UNIT_CODE", count, dosageUnit);
            cbnParm.setData("OPT_USER", count, userId);
            cbnParm.setData("OPT_DATE", count, date);
            cbnParm.setData("OPT_TERM", count, Operator.getIP());
          
            count++;
        }
        
        if (cbnParm != null) {
        	cbnParm.setCount(count);
            parm.setData("OUT_IN", cbnParm.getData());
        }
        
        // 执行数据新增
        TParm result = TIOM_AppServer.executeAction("action.spc.SPCInStoreRegionAction",
                                            "onInsertIn", parm);
    	if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return false;
        }
    	         
        this.messageBox("入库成功");
        onClear() ;
        return true;
    
    }
    
    /**
	 * 调用密码验证
	 * 
	 * @return boolean
	 */
	public boolean checkPW() {
		String inwCheck = "station";
		TParm parm = (TParm) this.openDialog(
				"%ROOT%\\config\\spc\\passWordCheck.x", inwCheck);
		if (parm == null) {
			return false;
		}
		String value = parm.getValue("OK");
		userId = parm.getValue("USER_ID");
		return value.equals("OK");
	}
    
    
   
    
    /**清空方法*/
    public void onClear() {
        table.removeRowAll();
               
        this.setValue("DISPENSE_NO", "");
		this.setValue("BOX_ESL_ID", "");
        this.setValue("OUT_CHN_DESC", "");
		this.setValue("REQTYPE_CODE", "");
		
         
    }
    
    /**
	 * 得到单号类型　
	 * @param reqtype
	 * @return
	 */
	private String getReqtype(String reqtype){
		if(StringUtil.isNullString(reqtype)){
			return "";
		}
		if(reqtype.equals("DEP")){
			return "部门请领";
		}else if(reqtype.equals("TEC")){
			return "备药生成";
		}else if(reqtype.equals("EXM")){
			return "补充计费";
		}else if(reqtype.equals("GIF")){
			return "药房调拨";
		}else if(reqtype.equals("RET")){
			return "退库";
		}else if(reqtype.equals("WAS")){
			return "损耗";
		}else if(reqtype.equals("THO")){
			return "其它出库";
		}else if(reqtype.equals("COS")){
			return "卫耗材领用";
		}else if(reqtype.equals("THI")){
			return "其它入库";
		}else if(reqtype.equals("EXM")){
			return "科室备药";
			
		}
		return "";
	}
	

    
	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
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
    
    
    /**---------------入库公用方法-------------------------------------------**/
    
    // 细项表格
    private TTable table_d;
    
	// 单据类型
	private String request_type;

	// 申请单号
	private String request_no;

	// 使用单位
	private String u_type;

	// 出库部门
	private String out_org_code;

	// 入库部门
	private String in_org_code;

	// 是否出库
	private boolean out_flg;

	// 是否入库
	private boolean in_flg;

	// 入库单号
	private String dispense_no;
	
	int  seq = 1;
    
	/**
	 * 得到主表与子表的所有参数
	 * @param parm    返回的参数
	 * @param mParm   主表对应数据
	 * @param resultParm   子表对应的数据
	 * @param updateFlg    更新状态
	 * @param table        子表UI table名称
	 * @return
	 */
	public TParm onSave(TParm mParm,TParm resultParm,TTable table){
		/**
		 * 初始化数据
		 */
		initParm(mParm,table) ;
		
		TParm parm = new TParm();
		
		 // 2.出库批号判断
        String batchvalid = getBatchValid(request_type);
		parm = getDispenseOutIn(out_org_code, in_org_code, batchvalid, out_flg, in_flg, parm, mParm, resultParm);
		return parm;
	}
	
	 /**
     * 入库作业
     *
     * @param out_org_code
     * @param in_org_code
     * @param batchvalid
     */
    private TParm getDispenseOutIn(String out_org_code, String in_org_code,
                                  String batchvalid, boolean out_flg,
                                  boolean in_flg ,TParm parm,TParm mParm ,TParm resultParm) {
    	
    	 parm = getDispenseMParm(parm, mParm,"3");
    	// 细项信息(OUT_D)
         if (!CheckDataD()) {
             return new TParm();
         }
         parm = getDispenseDParm(parm,resultParm);
         if (!"".equals(in_org_code)) {
             // 入库部门(IN_ORG)
             parm.setData("IN_ORG", in_org_code);
             // 是否入库(IN_FLG)
             parm.setData("IN_FLG", in_flg);
             parm.setData("OUT_ORG",out_org_code);
         }
        
         
         return parm;
    	 
    	
    }
    /**
     * 获得主项信息
     *
     * @param parm
     * @return
     */
    private TParm getDispenseMParm(TParm parm,TParm mParm, String updateFlg) {
        // 药库参数信息
        TParm sysParm = getSysParm();
        // 是否回写购入价格
        String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
        parm.setData("REUPRICE_FLG", reuprice_flg);

        TParm parmM = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        
        // 入库单号
        String  dispense_no = "";
        
        dispense_no = mParm.getValue("DISPENSE_NO",0);
       
        parmM.setData("DISPENSE_NO", dispense_no);
        parmM.setData("REQTYPE_CODE", mParm.getValue("REQTYPE_CODE",0));
        parmM.setData("REQUEST_NO", mParm.getValue("REQUEST_NO",0));
        parmM.setData("REQUEST_DATE", mParm.getValue("REQUEST_DATE",0));
        parmM.setData("APP_ORG_CODE", mParm.getValue("APP_ORG_CODE",0));
        parmM.setData("TO_ORG_CODE", mParm.getValue("TO_ORG_CODE",0));
        parmM.setData("URGENT_FLG", mParm.getValue("URGENT_FLG",0));
        parmM.setData("DESCRIPTION", mParm.getValue("DESCRIPTION",0));
        parmM.setData("DISPENSE_DATE", mParm.getValue("WAREHOUSING_DATE",0));
        parmM.setData("DISPENSE_USER", Operator.getID());
       
        if (!"1".equals(updateFlg)) {
            parmM.setData("WAREHOUSING_DATE", SystemTool.getInstance().getDate());
            parmM.setData("WAREHOUSING_USER", Operator.getID());
        }
        else {
            parmM.setData("WAREHOUSING_DATE", tnull);
            parmM.setData("WAREHOUSING_USER", "");
        }
        
        parmM.setData("DRUG_CATEGORY",mParm.getValue("DRUG_CATEGORY",0));
        
        //申请方式--全部:APP_ALL,人工:APP_ARTIFICIAL,请领建议:APP_PLE,自动拔补:APP_AUTO
        parmM.setData("APPLY_TYPE",mParm.getValue("REQTYPE_CODE",0));
        
        parmM.setData("REASON_CHN_DESC", mParm.getValue("REASON_CHN_DESC",0));
        parmM.setData("UNIT_TYPE", u_type);
        parmM.setData("UPDATE_FLG", updateFlg);
        parmM.setData("OPT_USER", userId);
        parmM.setData("OPT_DATE", date);
        parmM.setData("OPT_TERM", Operator.getIP());
        //luhai 2012-01-13 add region_code begin
        parmM.setData("REGION_CODE", Operator.getRegion());
        //luhai 2012-01-13 add region_code end
        if (parmM != null) {
            parm.setData("OUT_M", parmM.getData());
        }
        return parm;
    }
    
    /**
     * 获得明细信息
     *
     * @param parm
     * @return
     */
    private TParm getDispenseDParm(TParm parm,TParm resultParm) {
        TParm parmD = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        int count = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            
            parmD.setData("DISPENSE_NO", count, getValueString("DISPENSE_NO"));
            if (!"THI".equals(this.getValueString("REQTYPE_CODE"))) {
                parmD.setData("SEQ_NO", count, resultParm.getInt("SEQ_NO", i));
            }
            else {
                parmD.setData("SEQ_NO", count,table_d.getParmValue().getValue("BATCH_SEQ",i));
            }
            parmD.setData("REQUEST_SEQ", count, resultParm.getInt(
                "REQUEST_SEQ", i));
            parmD.setData("ORDER_CODE", count,
                          resultParm.getValue("ORDER_CODE", i));
            parmD.setData("QTY", count, table_d.getItemDouble(i, "QTY"));
            parmD.setData("UNIT_CODE", count, table_d.getItemString(i,
                "UNIT_CODE"));
            parmD.setData("RETAIL_PRICE", count,resultParm.getDouble(
                "RETAIL_PRICE",i));
            parmD.setData("STOCK_PRICE", count, table_d.getParmValue().getDouble(
                "STOCK_PRICE",i));
            parmD.setData("ACTUAL_QTY", count, table_d.getItemDouble(i,
                "OUT_QTY"));
            parmD.setData("PHA_TYPE", count, table_d.getParmValue().getValue(
                "PHA_TYPE",i));
            parmD.setData("BATCH_SEQ", count, table_d.getParmValue().getValue(
                "BATCH_SEQ",i));
            parmD.setData("BATCH_NO", count,
                          table_d.getItemString(i, "BATCH_NO"));
            parmD.setData("VALID_DATE", count, table_d.getItemData(i,
                "VALID_DATE"));
            parmD.setData("DOSAGE_QTY", count, table_d.getParmValue().getDouble(
                "DOSAGE_QTY", i));
            parmD.setData("STOCK_QTY", count, table_d.getParmValue().getDouble(
                "STOCK_QTY", i));
            parmD.setData("REGION_CODE", count, Operator.getRegion());
            parmD.setData("OPT_USER", count, userId);
            parmD.setData("OPT_DATE", count, date);
            parmD.setData("OPT_TERM", count, Operator.getIP());
            
            parmD.setData("SUP_CODE", count,resultParm.getValue("SUP_CODE",i));//liuzhen
            parmD.setData("PUTAWAY_USER",count,userId);
            parmD.setData("IS_PUTAWAY",count,"Y");
            parmD.setData("PUTAWAY_DATE",count,SystemTool.getInstance().getDate());
        	
            //验收价格
            parmD.setData("VERIFYIN_PRICE",count,resultParm.getDouble("VERIFYIN_PRICE",i));
            //整盒价格
            parmD.setData("INVENT_PRICE",count,resultParm.getDouble("INVENT_PRICE",i));
            count++;
        }
        if (parmD != null) {
            parm.setData("OUT_D", parmD.getData());
        }
        return parm;
    }
    

	public void initParm(TParm mparm,TTable table) {
		table_d = table ;
		request_type = mparm.getValue("REQTYPE_CODE",0);
		if ("TEC".equals(request_type) || "EXM".equals(request_type)
				|| "COS".equals(request_type)) {
			u_type = "1";
		} else if ("DEP".equals(request_type)) {
			u_type = IndSysParmTool.getInstance().onQuery().getValue(
					"UNIT_TYPE", 0);
		}

		if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
			out_org_code =mparm.getValue("TO_ORG_CODE",0);
			out_flg = true;
			in_org_code = mparm.getValue("APP_ORG_CODE",0);
			in_flg = true;
		} else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
			out_org_code = mparm.getValue("APP_ORG_CODE",0);
			out_flg = true;
			in_org_code = mparm.getValue("TO_ORG_CODE",0);
			in_flg = true;
		} else if ("EXM".equals(request_type) || "COS".equals(request_type)) {
			out_org_code = mparm.getValue("TO_ORG_CODE",0);
			out_flg = true;
			in_org_code = mparm.getValue("APP_ORG_CODE",0);
			in_flg = false;
		} else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
			out_org_code = mparm.getValue("APP_ORG_CODE",0);
			out_flg = true;
			in_org_code = mparm.getValue("TO_ORG_CODE",0);
			in_flg = false;
		} else if ("THI".equals(request_type)) {
			out_org_code = mparm.getValue("TO_ORG_CODE",0);
			out_flg = false;
			in_org_code = mparm.getValue("APP_ORG_CODE",0);
			in_flg = true;
		}
	}
	
	 /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckDataD() {
        // 判断细项是否有被选中项
        for (int i = 0; i < table_d.getRowCount(); i++) {
            // 判断数据正确性
            double qty = table_d.getItemDouble(i, "OUT_QTY");
            if (qty <= 0) {
                this.messageBox("入库数数量不能小于或等于0");
                return false;
            }
            double price = table.getParmValue().getDouble("STOCK_PRICE",i);
            if (price <= 0) {
                this.messageBox("成本价不能小于或等于0");
                return false;
            }
        }
        return true;
    }
    
    /**
     * 出库批号判断: 1,不指定批号和效期;2,指定批号和效期
     *
     * @return
     */
    private String getBatchValid(String type) {
        if ("DEP".equals(type) || "TEC".equals(type) || "EXM".equals(type)
            || "GIF".equals(type) || "COS".equals(type)) {
            return "1";
        }
        return "2";
    }

	 /**
     * 药库参数信息
     * @return TParm
     */
    private TParm getSysParm(){
        return IndSysParmTool.getInstance().onQuery();
    }
    
    public void openSearch(){
    	
    	TParm parm = new TParm();
    	parm.setData("CABINET_ID",getValueString("CABINET_ID"));
    	Object result = openDialog("%ROOT%\\config\\spc\\SPCContainerStatic.x", parm);
	}
    
	/**
	 * 通过手动输入智能柜IP地址完成入库
	 * 
	 * @author wangbin 2014/11/24
	 */
	public void onQueryCabinet() {
		ip =  this.getValueString("CABINET_IP").trim();
		if (ip.length() < 1) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("CABINET_IP", ip);
		TParm result = SPCInStoreTool.getInstance().queryCabinet(parm);
		// add by wangbin 2014/11/19 非智能柜设备访问该页面时IP控件可编辑 START
		if (result.getCount() > 0) {
			guardIp = result.getValue("GUARD_IP", 0);
			setValue("CABINET_ID", result.getValue("CABINET_ID", 0));
			setValue("CABINET_DESC", result.getValue("CABINET_DESC", 0));
			setValue("ORG_CHN_DESC", result.getValue("ORG_CHN_DESC", 0));
			setValue("OPT_USER", Operator.getName());
		} else {
			this.messageBox("无此IP地址的智能柜信息");
			this.setValue("CABINET_IP", "");
			this.setValue("CABINET_ID", "");
			this.setValue("CABINET_DESC", "");
			this.setValue("ORG_CHN_DESC", "");
			this.grabFocus("CABINET_IP");
			return;
		}
	}
    
}
