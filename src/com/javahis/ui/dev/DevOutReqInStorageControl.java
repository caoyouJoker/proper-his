package com.javahis.ui.dev;

import java.awt.Component;
import java.sql.Timestamp;

import jdo.dev.DevDepTool;
import jdo.dev.DevInStorageTool;
import jdo.dev.DevTypeTool;
import jdo.sys.Operator; 
import jdo.sys.SystemTool;    
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.base.TTableBase;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;  
import com.javahis.util.OrderUtil;
import com.silionmodule.ReaderType.AntTypeE;
           
/**
 * <p>Title: 入库确认</p>  
 *
 * <p>Description: 入库确认</p>me
 *  
 * <p>Copyright: Copyright (c) 2013</p>                
 *
 * <p>Company:bluecore </p> 
 *  
 * @author  fux 
 * @version 4.0 
 */ 
public class DevOutReqInStorageControl extends TControl {
    //设备入库明细缓冲区
    TParm parmD = new TParm();
    //设备明细序号管理缓冲区
    TParm parmDD = new TParm();
    //出库数据细表parm          
    TParm parmExD = new TParm();
    //入库明细TABLE:DEV_INWAREHOUSED   
    //明细序号TABLE:DEV_INWAREHOUSEDD
    String outStorageNo = null;
    //入库确认数据细表parm 
    TParm tableParm = new TParm();
    /**
     * 初始化方法
     */  
    public void onInit() {  
        super.onInit();
        //设备明细表格编辑事件
        addEventListener("DEV_INWAREHOUSED->" + TTableEvent.CHANGE_VALUE,"onTableValueChange");
//        //设备明细表格编辑事件
//        addEventListener("DEV_INWAREHOUSEDD->" + TTableEvent.CHANGE_VALUE,"onTableValueChangeDD"); 
 		// 长期医嘱CHECK_BOX监听事件
		getTTable("DEV_INWAREHOUSEDD").addEventListener(TTableEvent.CHECK_BOX_CLICKED,
				this, "onCheckBoxValue");
        //设备录入事件
        getTTable("DEV_INWAREHOUSED").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,"onCreateEditComoponent");
        //设备录事件
        getTTable("DEV_INWAREHOUSED").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,"onTableComponent");
        //设备DD录事件
        getTTable("DEV_INWAREHOUSEDD").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,"onTableDDComponent");
        //初始化换面启动时控件默认值
        initComponent();
        //初始化设备入库明细表格
        initTableD();
        //初始化设备入库明细序号管理表格      
        initTableDD();
        //加入空行
        addDRow();
        //锁住表格不可编辑栏位  
        setTableLock();
        //设置权限
        onInitOperaotrDept(); 
        callFunction("UI|SCAN_BARCODE|addEventListener",TTextFieldEvent.KEY_PRESSED, this, "onChangeBarcode");
    }

    /**
     * 条码扫描事件 
     */  
    public void onChangeBarcode(){ 
    	//扫描BARCODE 
    	TParm ddParm=getTTable("DEV_INWAREHOUSEDD").getParmValue();
    	boolean flg = false; 
    	for (int i = 0; i < ddParm.getCount("BARCODE"); i++) {
    		String cString=ddParm.getData("BARCODE", i).toString().trim();
    		if (cString.equals(getValueString("SCAN_BARCODE").trim())) {
    			flg = true;
    			getTTable("DEV_INWAREHOUSEDD").setItem(i, "SELECT_FLG", flg);
				break; 
			} 
		}  
    	this.clearValue("SCAN_BARCODE");
    	getTextField("SCAN_BARCODE").setEditable(true);
    	getTextField("SCAN_BARCODE").grabFocus();
	}
    /**
     * 设置权限
     */
    private void onInitOperaotrDept(){
        // 显示全院药库部门 
        if (getPopedem("deptAll"))
            return; 
//        ((TextFormatDEVOrg)getComponent("INWAREHOUSE_DEPT")).setOperatorId(Operator.getID());
//        ((TextFormatDEVOrg)getComponent("DEPT")).setOperatorId(Operator.getID());
    }

    /**
     * 锁住表格不可编辑栏位
     */
    public void setTableLock(){
        ((TTable)getComponent("DEV_INWAREHOUSED")).setLockColumns("3,4,5,6,8,"+
                                                                  "9,10,12,16,17,18,"+
                                                                  "19,20,21,22,"+
                                                                  "24,25,26,27,28");
        ((TTable)getComponent("DEV_INWAREHOUSEDD")).setLockColumns("0,2,3,4,5,11,"+
                                                                   "12,13,14,15");
    }
    /**
     * 初始化界面控件默认值
     */
    public void initComponent(){
        Timestamp timestamp = SystemTool.getInstance().getDate();
        setValue("INWARE_START_DATE",timestamp);
        setValue("INWARE_END_DATE",timestamp);
        setValue("INWAREHOUSE_DATE",timestamp); 
        setValue("INWAREHOUSE_DEPT",Operator.getDept());
        setValue("DEPT",Operator.getDept()); 
        setValue("OPERATOR",Operator.getID());
    }
    /**   
     * 初始化设备入库明细表格 
     */
    public void initTableD(){
        String column = "DEL_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;" +
        		"QTY;SUM_QTY;RECEIPT_QTY;UNIT_CODE;DEPR_METHOD;" +
        		"USE_DEADLINE;SEQMAN_FLG;MEASURE_FLG;BENEFIT_FLG;" +
        		"FILES_WAY;VERIFY_NO;VERIFY_NO_SEQ;INWAREHOUSE_NO;" +
        		"SEQ_NO;DEVKIND_CODE"; 
        String stringMap[] = StringTool.parseLine(column,";");
        TParm tableDParm = new TParm();
        for(int i = 0;i<stringMap.length;i++){
            tableDParm.addData(stringMap[i],"");
        }
        ((TTable)getComponent("DEV_INWAREHOUSED")).setParmValue(tableDParm);
        ((TTable)getComponent("DEV_INWAREHOUSED")).removeRow(0);
    }
    /**
     * 初始化设备入库明细序号管理表格
     */
    public void initTableDD(){
    	//加入选择框，确认明细表是否正确 
        String column = "DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;" +
        		"DEV_DETAIL_CODE;DEV_CODE;DEVSEQ_NO;" +
        		"DEV_CHN_DESC;BARCODE;MAIN_DEV;SPECIFICATION;" +
        		"MODEL;MAN_CODE;BRAND;MAN_DATE;LAST_PRICE;" +
        		"GUAREP_DATE;DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;" +
        		"SEQ_NO;DEVSEQ_NO;SERIAL_NUM;IP;TERM;LOC_CODE;USE_USER";
        String stringMap[] = StringTool.parseLine(column,";");
        TParm tableDParm = new TParm();
        for(int i = 0;i<stringMap.length;i++){
            tableDParm.addData(stringMap[i],""); 
        }
        ((TTable)getComponent("DEV_INWAREHOUSEDD")).setParmValue(tableDParm);  
        ((TTable)getComponent("DEV_INWAREHOUSEDD")).removeRow(0);
    }
    /**
     * 加入空行
     */
    public void addDRow(){
        String column =  "DEL_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;" +
		                 "QTY;SUM_QTY;RECEIPT_QTY;UNIT_CODE;DEPR_METHOD;" +
		                 "USE_DEADLINE;SEQMAN_FLG;MEASURE_FLG;BENEFIT_FLG;" +
		                 "FILES_WAY;VERIFY_NO;VERIFY_NO_SEQ;INWAREHOUSE_NO;" +
		                 "SEQ_NO;DEVKIND_CODE"; 
        String stringMap[] = StringTool.parseLine(column,";");
        TParm tableDParm = new TParm();
        for(int i = 0;i<stringMap.length;i++){
            if(stringMap[i].equals("DEVPRO_CODE"))
                tableDParm.setData(stringMap[i],"A");
            else
                tableDParm.setData(stringMap[i],"");
        }
        ((TTable)getComponent("DEV_INWAREHOUSED")).addRow(tableDParm);
    }

    /**
     * 查询方法
     */
    public void onQuery(){
        ((TTable)getComponent("EXWAREHOUSE")).removeRowAll();
        ((TTable)getComponent("EXWAREHOUSE")).resetModify();
        TParm parm = new TParm();
        //入库单号
        if(getValueString("INWAREHOUSE_NO").length()!=0)
            parm.setData("INWAREHOUSE_NO",getValueString("INWAREHOUSE_NO"));
        //入库开始时间
        if(getValueString("INWARE_START_DATE").length()!=0)
            parm.setData("INWARE_START_DATE",StringTool.getTimestamp(getValueString("INWARE_START_DATE"),"yyyy-MM-dd"));
        //入库结束时间
        if(getValueString("INWARE_END_DATE").length()!=0)
            parm.setData("INWARE_END_DATE",StringTool.getTimestamp(getValueString("INWARE_END_DATE"),"yyyy-MM-dd"));
        //入库科室
        if(getValueString("INWAREHOUSE_DEPT").length()!=0)
            parm.setData("INWAREHOUSE_DEPT",getValueString("INWAREHOUSE_DEPT"));
        //入库人员
        if(getValueString("INWAREHOUSE_USER").length()!=0) 
            parm.setData("INWAREHOUSE_USER",getValueString("INWAREHOUSE_USER"));
        //入库确认不需要验收信息
//        //验收单号
//        if(getValueString("RECEIPT_NO").length()!=0)
//            parm.setData("RECEIPT_NO",getValueString("RECEIPT_NO"));
//        //验收开始时间 
//        if(getValueString("RECEIPT_START_DATE").length()!=0)
//            parm.setData("RECEIPT_START_DATE",StringTool.getTimestamp(getValueString("RECEIPT_START_DATE"),"yyyy-MM-dd"));
//        //验收结束时间
//        if(getValueString("RECEIPT_END_DATE").length()!=0)
//            parm.setData("RECEIPT_END_DATE",StringTool.getTimestamp(getValueString("RECEIPT_END_DATE"),"yyyy-MM-dd"));
        if(parm.getNames().length == 0)
            return;
        parm = DevInStorageTool.getInstance().selectDevInStorageInf(parm);
        if(parm.getErrCode() < 0 ){
        	messageBox("查询错误！");
            return;
        }
        if(parm.getCount()<=0) {
        	messageBox("无查询数据！");
            return;
        }
        ((TTable)getComponent("EXWAREHOUSE")).setParmValue(parm);

    }


    /**
     * 拿到供应商相关信息
     * @param receiptNo String
     * @return TParm
     */
    public TParm getSupInf(String receiptNo){
        TParm parm = new TParm(getDBTool().select(" SELECT B.SUP_CODE,B.SUP_SALES1,B.SUP_SALES1_TEL"+
                                                  " FROM DEV_RECEIPTM A, SYS_SUPPLIER B"+
                                                  " WHERE A.RECEIPT_NO = '"+receiptNo+"'"+
                                                  " AND   A.SUP_CODE = B.SUP_CODE"));
        return parm;
    }

    /**
     * 生成入库单信息(出库单)
     */ 
    public void onGenerateReceipt(){
    	//fux modify
        if(getValueString("INWAREHOUSE_DATE").length() == 0){
            messageBox("请录入入库日期");
            return;
        }   
        onClear();
        ((TTable)getComponent("EXWAREHOUSE")).setParmValue(((TTable)getComponent("EXWAREHOUSE")).getParmValue());
        //应该是出库单而不是验收ReceiptUI.x,请领入库这里换成出库单(验收作业->入库作业，出库作业->请领入库)  
        TParm parmDiag = (TParm)openDialog("%ROOT%\\config\\dev\\StorageMUI.x");
        if(parmDiag == null)
            return;    
        //出库单号   
        outStorageNo = parmDiag.getValue("EXWAREHOUSE_NO");      
        //原得到验收单信息现在改为得到出库单信息 
        //InStorageTool   selectDevexwarehouseD 
        TParm parmM = new TParm();
        //parmM = DevInStorageTool.getInstance().selectDevexwarehouseM(outStorageNo);
        parmExD = DevInStorageTool.getInstance().selectDevexwarehouseD(outStorageNo); 
        TParm parmDD = DevInStorageTool.getInstance().selectDevexwarehouseDD(outStorageNo);
        if(parmExD.getErrCode() < 0)  
            return;       
        if(parmExD.getCount() <= 0){
            messageBox("此入库单设备已经全部入库"); 
            return;
        }
//        setValue("SUP_CODE",parmDiag.getValue("SUP_CODE"));
//        setValue("SUP_BOSSNAME",parmDiag.getValue("SUP_SALES1"));
//        setValue("SUP_TEL",parmDiag.getValue("SUP_SALES1_TEL"));
        Timestamp timestamp = StringTool.getTimestamp(SystemTool.getInstance().getDate().toString(),"yyyy-MM-dd");
        
        TParm tableParmXH = new TParm();
        TParm tableParmDD = new TParm(); 
        for(int i = 0;i<parmExD.getCount();i++){
        	//DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;DEV_DETAIL_CODE;DEV_CODE;DEVSEQ_NO;DEV_CHN_DESC;
        	//BARCODE;MAIN_DEV;SPECIFICATION;MODEL;MAN_CODE;BRAND;MAN_DATE;LAST_PRICE;GUAREP_DATE;
        	//DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;DEVSEQ_NO;SERIAL_NUM;IP;TERM;LOC_CODE;USE_USER
            //取得入库日期
            String inWarehouseDate = getValueString("INWAREHOUSE_DATE"); 
            //根据使用年限计算折旧终止日期 
            int year = Integer.parseInt(inWarehouseDate.substring(0, 4)) + 
            parmExD.getInt("USE_DEADLINE", i);
            String depDate = year + inWarehouseDate.substring(4,inWarehouseDate.length());

            tableParm.addData("DEL_FLG","N"); 
            tableParm.addData("DEVPRO_CODE",parmExD.getData("DEVPRO_CODE",i));
            tableParm.addData("DEV_CODE",parmExD.getData("DEV_CODE",i));
            tableParm.addData("DEV_CHN_DESC",parmExD.getData("DEV_CHN_DESC",i));
            tableParm.addData("SPECIFICATION",parmExD.getData("SPECIFICATION",i));
            tableParm.addData("MAN_CODE",parmExD.getData("MAN_CODE",i));
            tableParm.addData("QTY",parmExD.getData("QTY",i));  
//            tableParm.addData("SUM_QTY",parm.getData("SUM_QTY",i));  
            //出库数   
            tableParm.addData("RECEIPT_QTY",parmExD.getData("QTY",i)); 
            tableParm.addData("UNIT_CODE",parmExD.getData("UNIT_CODE",i));
            tableParm.addData("UNIT_PRICE",parmExD.getData("UNIT_PRICE",i));
            tableParm.addData("TOT_VALUE",parmExD.getDouble("UNIT_PRICE",i) * parmExD.getDouble("QTY",i));
            tableParm.addData("MAN_DATE",parmExD.getData("MAN_DATE",i));  
            tableParm.addData("LAST_PRICE",0);      
            tableParm.addData("GUAREP_DATE",parmExD.getData("GUAREP_DATE",i));                       
            tableParm.addData("DEPR_METHOD",parmExD.getData("DEPR_METHOD",i));  
            tableParm.addData("USE_DEADLINE",parmExD.getData("USE_DEADLINE",i));
            //tableParm.addData("DEP_DATE",StringTool.getTimestamp(depDate,"yyyy-MM-dd"));
            tableParm.addData("DEP_DATE",parmExD.getData("GUAREP_DATE",i));  
            tableParm.addData("MAN_NATION",parmExD.getData("MAN_NATION",i));
            tableParm.addData("SEQMAN_FLG",parmExD.getData("SEQMAN_FLG",i));
            tableParm.addData("MEASURE_FLG",parmExD.getData("MEASURE_FLG",i));
            tableParm.addData("BENEFIT_FLG",parmExD.getData("BENEFIT_FLG",i));
            tableParm.addData("FILES_WAY","");   
            //出库好和序号  
            tableParm.addData("VERIFY_NO",outStorageNo);    
            tableParm.addData("VERIFY_NO_SEQ",parmExD.getData("SEQ_NO",i));
            //入库号还没生成
            tableParm.addData("INWAREHOUSE_NO","");  
            tableParm.addData("SEQ_NO",""); 
            tableParm.addData("DEVKIND_CODE",parmExD.getData("DEVKIND_CODE",i));  
            tableParm.addData("DEP_DATE",parmExD.getData("DEP_DATE",i)); 
            tableParm.addData("BRAND",parmExD.getData("BRAND",i)); 
            tableParm.addData("MODEL",parmExD.getData("MODEL",i));  
        }  
        for(int j = 0;j<parmDD.getCount() ;j++){
        	//DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;DEV_DETAIL_CODE;DEV_CODE;DEVSEQ_NO;
        	//DEV_CHN_DESC;BARCODE;MAIN_DEV;SPECIFICATION;MODEL;MAN_CODE;BRAND;MAN_DATE;LAST_PRICE;
        	//GUAREP_DATE;DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;DEVSEQ_NO;SERIAL_NUM;IP;TERM;LOC_CODE;USE_USER
        	tableParmXH.addData("DEL_FLG","N");  
        	tableParmXH.addData("SELECT_FLG","N");    
        	tableParmXH.addData("PRINT_FLG","N");    
        	tableParmXH.addData("DEVPRO_CODE",parmDD.getData("DEVPRO_CODE",j));
        	//DEV_CODE_DETAIL
        	tableParmXH.addData("DEV_CODE_DETAIL",parmDD.getData("DEV_CODE_DETAIL",j));  
        	tableParmXH.addData("DEV_CODE",parmDD.getData("DEV_CODE",j));
        	tableParmXH.addData("DEV_CHN_DESC",parmDD.getData("DEV_CHN_DESC",j));
        	tableParmXH.addData("SPECIFICATION",parmDD.getData("SPECIFICATION",j)); 
        	tableParmXH.addData("MAN_CODE",parmDD.getData("MAN_CODE",j)); 
        	tableParmXH.addData("QTY",parmDD.getData("QTY",j));   
//            tableParm.addData("SUM_QTY",parm.getData("SUM_QTY",i));  
//            tableParm.addData("RECEIPT_QTY",parm.getData("RECEIPT_QTY",i));  
        	tableParmXH.addData("UNIT_CODE",parmDD.getData("UNIT_CODE",j));  
        	tableParmXH.addData("UNIT_PRICE",parmDD.getData("UNIT_PRICE",j));
        	tableParmXH.addData("TOT_VALUE",parmDD.getDouble("UNIT_PRICE",j) * parmDD.getDouble("QTY",j));
        	//   
        	tableParmXH.addData("MAN_DATE",parmDD.getData("MAN_DATE",j)); 
        	tableParmXH.addData("LAST_PRICE",parmDD.getData("UNIT_PRICE",j));
        	//  
        	tableParmXH.addData("GUAREP_DATE",parmDD.getData("GUAREP_DATE",j));
        	tableParmXH.addData("DEPR_METHOD",parmDD.getData("DEPR_METHOD",j));
        	tableParmXH.addData("USE_DEADLINE",parmDD.getData("USE_DEADLINE",j));
        	//折旧  need modify 
        	 String inWarehouseDate = getValueString("INWAREHOUSE_DATE"); 
        	tableParmXH.addData("DEP_DATE",StringTool.getTimestamp(inWarehouseDate,"yyyy-MM-dd"));
        	tableParmXH.addData("MAN_NATION",parmDD.getData("MAN_NATION",j));
        	tableParmXH.addData("SEQMAN_FLG",parmDD.getData("SEQMAN_FLG",j));
        	tableParmXH.addData("MEASURE_FLG",parmDD.getData("MEASURE_FLG",j));
        	tableParmXH.addData("BENEFIT_FLG",parmDD.getData("BENEFIT_FLG",j));
        	tableParmXH.addData("FILES_WAY",""); 
        	//出库号可取 
        	tableParmXH.addData("VERIFY_NO",outStorageNo);  
        	tableParmXH.addData("VERIFY_NO_SEQ",parmDD.getData("SEQ_NO",j));
        	tableParmXH.addData("BARCODE",parmDD.getData("BARCODE",j));
            //入库号重新
        	tableParmXH.addData("INWAREHOUSE_NO","");         
        	tableParmXH.addData("SEQ_NO",parmDD.getData("SEQ_NO",j));   
        	tableParmXH.addData("DEVSEQ_NO",parmDD.getData("DEVSEQ_NO",j));
        	tableParmXH.addData("DEVKIND_CODE",parmDD.getData("DEVKIND_CODE",j));            	
        	tableParmXH.addData("MAIN_DEV",parmDD.getData("SETDEV_CODE",j));
        	 
        	tableParmXH.addData("SERIAL_NUM",parmDD.getData("SERIAL_NUM",j));
        	tableParmXH.addData("IP",parmDD.getData("IP",j));  
        	tableParmXH.addData("TERM",parmDD.getData("TERM",j));
        	tableParmXH.addData("LOC_CODE",parmDD.getData("LOC_CODE",j));
        	tableParmXH.addData("USE_USER",parmDD.getData("USE_USER",j));  
        }   
        
        setValue("INWAREHOUSE_USER",Operator.getID());     
        setValue("INWAREHOUSE_DEPT",parmM.getData("INWAREHOUSE_DEPT",0));
        
        //addDevDDTable(tableParmDD, tableParmXH, 1);  
        ((TTable)getComponent("DEV_INWAREHOUSED")).setParmValue(tableParm);
        ((TTable)getComponent("DEV_INWAREHOUSEDD")).setParmValue(tableParmXH); 
        ((TTextFormat)getComponent("INWAREHOUSE_DATE")).setEnabled(false);
        this.setValue("DEPT", parmM.getValue("INWAREHOUSE_DEPT", 0));
        setTableLock();      
    }

    /**
     * 设置序号管理明细表信息
     * @param tableParmDD TParm 
     * @param tableParm TParm
     * @param row int 
     */   
    public void addDevDDTable(TParm tableParmDD,TParm tableParm,int row){
        for(int i = 0;i<tableParm.getCount();i++){  
        	//DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;DEV_DETAIL_CODE;DEV_CODE;DEVSEQ_NO;DEV_CHN_DESC;BARCODE;MAIN_DEV;SPECIFICATION;
        	//MODEL;MAN_CODE;BRAND;MAN_DATE;LAST_PRICE;GUAREP_DATE;DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;
        	//DEVSEQ_NO;SERIAL_NUM;IP;TERM;LOC_CODE;USE_USER
        	tableParmDD.addData("DEL_FLG","N");
        	tableParmDD.addData("SELECT_FLG","N");  
        	tableParmDD.addData("PRINT_FLG","N");        
            tableParmDD.addData("DEVPRO_CODE",tableParm.getData("DEVPRO_CODE",i));
            tableParmDD.addData("DEV_DETAIL_CODE",tableParm.getData("DEV_DETAIL_CODE",i));
            tableParmDD.addData("DEV_CODE",tableParm.getData("DEV_CODE",i)); 
            tableParmDD.addData("DEV_CHN_DESC",tableParm.getData("DEV_CHN_DESC",i));
            tableParmDD.addData("BARCODE",tableParm.getData("BARCODE",i));
            tableParmDD.addData("MAIN_DEV","");
            tableParmDD.addData("MAN_DATE",tableParm.getData("MAN_DATE",i));
            tableParmDD.addData("MAN_SEQ","");
            tableParmDD.addData("LAST_PRICE",tableParm.getData("LAST_PRICE",i));
            tableParmDD.addData("GUAREP_DATE",tableParm.getData("GUAREP_DATE",i));
            tableParmDD.addData("DEP_DATE",tableParm.getData("DEP_DATE",i));
            tableParmDD.addData("TOT_VALUE",tableParm.getData("UNIT_PRICE",i));
            tableParmDD.addData("INWAREHOUSE_NO","");  
            tableParmDD.addData("SEQ_NO",tableParm.getData("SEQ_NO",i)); 
            tableParmDD.addData("DEVSEQ_NO",tableParm.getData("DEVSEQ_NO",i)); 
            tableParmDD.addData("SERIAL_NUM",tableParm.getData("SERIAL_NUM",i));
            tableParmDD.addData("IP",tableParm.getData("IP",i)); 
            tableParmDD.addData("TERM",tableParm.getData("TERM",i));
            tableParmDD.addData("LOC_CODE",tableParm.getData("LOC_CODE",i));
            tableParmDD.addData("USE_USER",tableParm.getData("USE_USER",i)); 
        } 
        //system.out.println("tableParmDD"+tableParmDD);
    }

    /**
     * D值改变事件
     * @param obj Object
     * @return boolean
     */ 
    public boolean onTableValueChange(Object obj) { 
    	//在table对象里填写需添加项 
        TTableNode node = (TTableNode)obj;
        //删除动作 
        onTableValueChange0(node); 
        //设备属性
        onTableValueChange1(node);   
        //设备编号动作
        onTableValueChange2(node);
        //入库量动作
        onTableValueChange4(node); 
        //保留代码
//        //单价动作(由于无法确定分类编码下出库东西的单价,去掉)
//        onTableValueChange11(node);
//        //保修日期动作 
//        onTableValueChange15(node);
//        //折旧日期动作
//        onTableValueChange18(node);
        return false;
   }
    //fux modify 20140612
    /**
     * DD值改变事件  
     * @param obj Object
     * @return boolean
     */ 
    public boolean onTableValueChangeDD(Object obj) { 
    	//在table对象里填写需添加项 
        TTableNode node = (TTableNode)obj;
        //选动作 
        onTableDDValueChange0(node); 
        return false;
   }
     
    /** 
     * 设备选动作
     * @param node TTableNode
     */  
    public void onTableDDValueChange0(TTableNode node){
    	
//    	if (columnName.equals("CHOOSE_FLG")) {
//			String flg = tableParm.getValue(columnName, row);
//			// System.out.println("flg::::::::"+flg);
//			if (flg.equals("N")) {}
    	
         if(node.getColumn() != 0 )
             return;   
         TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
         for(int i = 0;i < ddTable.getRowCount();i++){
                updateTableData("DEV_INWAREHOUSEDD", i, 0,node.getValue()); 
         }
    }
    
    
	/**
	 * checkBox值改变事件
	 * 
	 * @param obj
	 */
	public boolean onCheckBoxValue(Object obj) {
		TTable chargeTable = (TTable) obj;
		chargeTable.acceptText();
		 TParm tableParm = chargeTable.getParmValue();
		 int col = getTTable("DEV_INWAREHOUSEDD").getSelectedColumn();
		 String columnName =getTTable("DEV_INWAREHOUSEDD").getDataStoreColumnName(col);
		 int row = getTTable("DEV_INWAREHOUSEDD").getSelectedRow();
		 if (col == 0) {
		 String flg = tableParm.getValue(columnName, row);
		 if (flg.equals("Y")) {
		 tableParm.setData("FLG", row, "Y");
		 getTTable("DEV_INWAREHOUSEDD").setParmValue(tableParm);
		 return true;
		   } 
		 }
		 return true;        
		 }
		

   /** 
    * 设备删动作
    * @param node TTableNode
    */
   public void onTableValueChange0(TTableNode node){
        if(node.getColumn() != 0 )
            return; 
        TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
        TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
        for(int i = 0;i < ddTable.getRowCount();i++){
            if(ddTable.getValueAt(i,5).equals(dTable.getValueAt(dTable.getSelectedRow(),2))
               //&&ddTable.getValueAt(i,3).equals(dTable.getValueAt(dTable.getSelectedRow(),1))
               ) 
               updateTableData("DEV_INWAREHOUSEDD", i, 0,node.getValue()); 
        }
   }

   /**
    * 设备属性动作
    * @param node TTableNode
    */
   public void onTableValueChange1(TTableNode node){
	    //如果设备属性不是第一列
        if(node.getColumn() != 1 )
            return;
        TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
        TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
        for(int i = 0;i < ddTable.getRowCount();i++){
        if(ddTable.getValueAt(i,3).equals(dTable.getValueAt(dTable.getSelectedRow(),1)))
        	//如果设备批号不为空，从DEV_INWAREHOUSED上取值并赋值到node  
            node.setValue(dTable.getValueAt(node.getRow(),node.getColumn()));
        }
   }

   /**
    * 更新数据表数据连同后端Parm
    * @param tableTag String
    * @param row int
    * @param column int
    * @param obj Object
    */  
   public void updateTableData(String tableTag,int row,int column,Object obj){
       ((TTable)getComponent(tableTag)).setValueAt(obj,row,column);
       //getFactColumnName:得到表格列名
       ((TTable)getComponent(tableTag)).getParmValue().setData
       (getFactColumnName(tableTag,column),row,obj);
   }

   /**
    * 设备编号动作
    * @param node TTableNode
    */
   public void onTableValueChange2(TTableNode node){
	    //TTableNode node：table属性类
        if(node.getColumn() != 2 )
            return;
        //如果是第二行，设备编号，
        String devCodeOld = "" + getTTable("DEV_INWAREHOUSED")
        .getValueAt(node.getRow(),node.getColumn());
        //如果选中的行列有值
        if(("" +getTTable("DEV_INWAREHOUSED").getValueAt(node.getRow(),3)).
        		length() != 0){
            node.setValue(devCodeOld);
            return;
        }
   }

   /**
    * 设备入库数量动作检核
    * @param node TTableNode
    * @return boolean
    */
   private boolean onTableValueChange4Check(TTableNode node){
       TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
       if((""+node.getValue()).length()!= 0&&     
          (Double.parseDouble("" + dTable.getValueAt(node.getRow(),5)) -
           Double.parseDouble("" + dTable.getValueAt(node.getRow(),6)) <
           Double.parseDouble("" + node.getValue()))){  
           messageBox("第" + String.valueOf(node.getRow()+1) + "行入库数量不可大于累计入库数与出库数之差");
           node.setValue(dTable.getValueAt(node.getRow(),4));
           return true; 
       }   
       if((""+node.getValue()).length() == 0){
           messageBox("第" + String.valueOf(node.getRow()+1) + "行请入库数量不能为空");
           node.setValue(dTable.getValueAt(node.getRow(),4));
           return true; 
       }
       return false;
   }
   /**
    * 设备入库数量动作
    * @param node TTableNode
    */
   public void onTableValueChange4(TTableNode node) {
       if(node.getColumn() != 4) 
           return; 
       if(node.getValue().toString().length() == 0)
           return;
       if(onTableValueChange4Check(node))
           return;
       TParm parm = ((TTable)getComponent("DEV_INWAREHOUSED")).getParmValue();
       TParm tableParmDD = new TParm();
       TTable dTable = ((TTable) getComponent("DEV_INWAREHOUSED"));
       //根据单价以及入库量计算总价值 
//       double unitPrice = 0;
//       if(("" + dTable.getValueAt(node.getRow(), 11)).length() != 0)
//           unitPrice = Double.parseDouble("" + dTable.getValueAt(node.getRow(), 11));
//       double totValue = Double.parseDouble("" + node.getValue()) * unitPrice;
//       updateTableData("DEV_INWAREHOUSED", node.getRow(), 12, totValue);
       //重新将序号管理明细展开
       for(int i = 0; i< parm.getCount("DEVPRO_CODE");i++){
           if("N".equals(parm.getData("SEQMAN_FLG",i)))
               continue;
           int rowCount = 0; 
           String value = ("" + node.getValue()).length() == 0?"0":("" + node.getValue());
           if(i == node.getRow())
               rowCount = Integer.parseInt(value);
           else
               rowCount = parm.getInt("QTY",i);
           for(int j = 0;j<rowCount;j++){ 
        	   tableParmDD.addData("SELECT_FLG","Y");
        	   //DEL_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;QTY;SUM_QTY;RECEIPT_QTY;UNIT_CODE;
        	   //DEPR_METHOD;USE_DEADLINE;SEQMAN_FLG;MEASURE_FLG;
        	   //BENEFIT_FLG;FILES_WAY;VERIFY_NO;VERIFY_NO_SEQ;INWAREHOUSE_NO;SEQ_NO;DEVKIND_CODE
//               tableParmDD.addData("DEL_FLG",parm.getData("DEL_FLG",i));
                
//               tableParmDD.addData("PRINT_FLG","N");
//               tableParmDD.addData("DEVPRO_CODE",parm.getData("DEVPRO_CODE",i));
//               tableParmDD.addData("DEV_DETAIL_CODE",""); 
//               tableParmDD.addData("DEV_CODE",parm.getData("DEV_CODE",i));
//               tableParmDD.setData("DEVSEQ_NO",""); 
//               tableParmDD.addData("DEV_CHN_DESC",parm.getData("DEV_CHN_DESC",i));
//               tableParmDD.addData("BARCODE","");
//               tableParmDD.addData("MAIN_DEV","");
//               
//               tableParmDD.addData("SPECIFICATION","");
//               tableParmDD.addData("MODEL","");
//               tableParmDD.addData("MAN_CODE","");
//               tableParmDD.addData("BRAND","");
//               
//               tableParmDD.addData("MAN_DATE",parm.getData("MAN_DATE",i));
//               tableParmDD.addData("LAST_PRICE",parm.getData("LAST_PRICE",i));
//               tableParmDD.addData("GUAREP_DATE",parm.getData("GUAREP_DATE",i));
//               tableParmDD.addData("DEP_DATE",parm.getData("DEP_DATE",i));
//               tableParmDD.addData("TOT_VALUE",parm.getData("UNIT_PRICE",i));
//               tableParmDD.setData("INWAREHOUSE_NO","");
//               tableParmDD.setData("SEQ_NO","");
//               tableParmDD.setData("DEVSEQ_NO","");
//               tableParmDD.setData("SERIAL_NUM","");
//               tableParmDD.setData("IP","");
//               tableParmDD.setData("TERM","");
//               tableParmDD.setData("LOC_CODE","");
//               tableParmDD.setData("USE_USER","");
           }
       }
       //TABLE:DEV_INWAREHOUSEDD设置Parm值
       ((TTable)getComponent("DEV_INWAREHOUSEDD")).setParmValue(tableParmDD);
   }
   /**
    * 单价动作
    * @param node TTableNode
    */
   public void onTableValueChange11(TTableNode node) {
       if(node.getColumn() != 11)
           return;
       if(node.getValue().toString().length() == 0)
           return;
       TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
       TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
       //根据修改的单价以及入库数量计算总价值
       int qty = 0;
       if(("" + dTable.getValueAt(node.getRow(),7)).length() != 0)
           qty = Integer.parseInt("" + dTable.getValueAt(node.getRow(),7));
       double totValue = Double.parseDouble("" + node.getValue()) * qty;
       updateTableData("DEV_INWAREHOUSED", node.getRow(), 12, totValue);
       for(int i = 0;i < ddTable.getRowCount();i++){
           if(ddTable.getValueAt(i,2).equals(dTable.getValueAt(dTable.getSelectedRow(),3))&&
              ddTable.getValueAt(i,3).equals(dTable.getValueAt(dTable.getSelectedRow(),2)))
               updateTableData("DEV_INWAREHOUSEDD", i, 12, node.getValue());
        }
   }
   /**
    * 保修日期动作
    * @param node TTableNode  
    */
   public void onTableValueChange15(TTableNode node){
        if(node.getColumn() != 15 )
           return;  
        if(node.getValue().toString().length() == 0)             
           return; 
        TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
        TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
        Timestamp guarepDate = (Timestamp)node.getValue();  
        Timestamp depDate = (Timestamp)dTable.getValueAt(dTable.getSelectedRow(),18);
        String devCode = "" + dTable.getValueAt(dTable.getSelectedRow(),2);
        int batNo = DevInStorageTool.getInstance().getUseBatchSeq(devCode,depDate,guarepDate);
        for(int i = 0;i < ddTable.getRowCount();i++){
            if(ddTable.getValueAt(i,2).equals(dTable.getValueAt(dTable.getSelectedRow(),3))&&
               ddTable.getValueAt(i,3).equals(dTable.getValueAt(dTable.getSelectedRow(),2))){
                updateTableData("DEV_INWAREHOUSEDD", i, 10, node.getValue());
                if(batNo!=Integer.parseInt("" + ddTable.getValueAt(i,2)))
                    updateTableData("DEV_INWAREHOUSEDD", i, 2, batNo);
            }
        }
        if(batNo!=Integer.parseInt("" + dTable.getValueAt(dTable.getSelectedRow(),3)))
            updateTableData("DEV_INWAREHOUSED", dTable.getSelectedRow(), 3, batNo);
   }

   /**
    * 折旧日期动作
    * @param node TTableNode
    */
   public void onTableValueChange18(TTableNode node){
        if(node.getColumn() != 18 )
            return;
        if(node.getValue().toString().length() == 0)
           return;
        TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
        TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
        Timestamp depDate = (Timestamp)node.getValue();
        Timestamp guarepDate = (Timestamp)dTable.getValueAt(dTable.getSelectedRow(),15);
        String devCode = "" + dTable.getValueAt(dTable.getSelectedRow(),2);
        int batNo = DevInStorageTool.getInstance().getUseBatchSeq(devCode,depDate,guarepDate);
        for(int i = 0;i < ddTable.getRowCount();i++){
            if(ddTable.getValueAt(i,2).equals(dTable.getValueAt(dTable.getSelectedRow(),3))&&
               ddTable.getValueAt(i,3).equals(dTable.getValueAt(dTable.getSelectedRow(),2))){
                updateTableData("DEV_INWAREHOUSEDD", i, 11, node.getValue());
                if(batNo!=Integer.parseInt("" + ddTable.getValueAt(i,2)))
                    updateTableData("DEV_INWAREHOUSEDD", i, 2, batNo);
            }
        }
        if(batNo!=Integer.parseInt("" + dTable.getValueAt(dTable.getSelectedRow(),3)))
            updateTableData("DEV_INWAREHOUSED", dTable.getSelectedRow(), 3, batNo);
   }

   /**
    * 保存方法
    */
   public void onSave(){  
       //检核是否有保存数据
       if(onSaveCheck())
           return;
       if(getValueString("INWAREHOUSE_DATE").length() == 0){
           messageBox("入库日期不可为空");
           return;  
       }
       if(getValueString("OPERATOR").length() == 0){
           messageBox("入库人员不可为空");
           return;
       }
       if(getValueString("DEPT").length() == 0){
           messageBox("入库科室不可为空");
           return;
       }
       getTTable("DEV_INWAREHOUSED").acceptText();
       getTTable("DEV_INWAREHOUSEDD").acceptText();
       //判断是新增还是修改
       if(((TTable)getComponent("EXWAREHOUSE")).getSelectedRow()<0)
           onNew();
       else
           onUpdate();
   }

   /**
    * 保存修改动作
    */
   private void onUpdate(){ 
       Timestamp timestamp = SystemTool.getInstance().getDate();
       TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
       TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
       TParm tableParmD = dTable.getParmValue();
       TParm tableParmDD = ddTable.getParmValue();
       TParm parmInD = new TParm();
       TParm parmInDD = new TParm();
       for(int i = 0;i<tableParmD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmD,tableParmD,i))
               continue;
           cloneTParm(tableParmD,parmInD,i);
           parmInD.addData("OPT_USER",Operator.getID());
           parmInD.addData("OPT_DATE",timestamp);
           parmInD.addData("OPT_TERM",Operator.getIP());
       }
       for(int i = 0;i<tableParmDD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmDD,tableParmDD,i))
               continue;
           cloneTParm(tableParmDD,parmInDD,i);
           parmInDD.addData("OPT_USER",Operator.getID());
           parmInDD.addData("OPT_DATE",timestamp);
           parmInDD.addData("OPT_TERM",Operator.getIP());
       }
       TParm parm = new TParm();
       parm.setData("DEV_INWAREHOUSED",parmInD.getData());
       parm.setData("DEV_INWAREHOUSEDD",parmInDD.getData());
       parm = TIOM_AppServer.executeAction(
           "action.dev.DevAction","updateInStorageReceipt", parm);
       if(parm.getErrCode() < 0){
           messageBox("保存失败");
           return;
       }
       messageBox("保存成功");
       onDevInwarehouse();
       onPrintReceipt();
   }

   /**
    * 保存新增
    */
   private void onNew(){
	   //更新m d dd
       String inwarehouseNo = DevInStorageTool.getInstance().getInwarehouseNo();
       Timestamp timestamp = SystemTool.getInstance().getDate();
       TParm mParm = new TParm();
       TParm dParm = new TParm();
       TParm ddParm = new TParm();    
       TParm stockMParm = new TParm(); 
       TParm stockDParm = new TParm();  
       TParm stockDDParm = new TParm();
       //insertDevInwarehouseD
       TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
       //dTable:(根据TABLE值改变事件，将参数Parm放入TTable对象)
       TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
       
       //tableParm:{Data={BENEFIT_FLG=[N], MAN_NATION=[86], SEQ_NO=[], DEP_DATE=[2024-05-23 00:00:00.0], 
       //USE_DEADLINE=[10.0], QTY=[4], DEVPRO_CODE=[A], VERIFY_NO=[140523000001], DEL_FLG=[N], VERIFY_NO_SEQ=[1],
       //MEASURE_FLG=[Y], MAN_DATE=[2014-05-23 00:00:00.0], UNIT_PRICE=[null], LAST_PRICE=[null], DEVKIND_CODE=[B], 
       //DEPR_METHOD=[2], RECEIPT_QTY=[4], INWAREHOUSE_NO=[], DEV_CODE=[830110000001], TOT_VALUE=[0.0],
       //SEQMAN_FLG=[Y], FILES_WAY=[], DEV_CHN_DESC=[台式机], 
       //SPECIFICATION=[null], MAN_CODE=[100], UNIT_CODE=[158], GUAREP_DATE=[2014-05-23 00:00:00.0]}}
       for(int i = 0;i<dTable.getRowCount();i++){ 
           //DEV_CODE  
           if ((""+dTable.getValueAt(i, 2)).length() == 0) {
             continue;   
           }  
           if (dTable.getValueAt(i, 0).equals("Y"))
           {
             continue;
           } 
           Object obj = null;
           TParm parm = (TParm)obj;   
           parm = dTable.getParmValue(); 
           String inWarehouseDate = getValueString("INWAREHOUSE_DATE");
           int year = Integer.parseInt(inWarehouseDate.substring(0, 4)) + parm.getInt("USE_DEADLINE",i);
           String depDate = year + inWarehouseDate.substring(4,inWarehouseDate.length()); 
           dParm.addData("SEQ_NO", Integer.valueOf(i + 1));    
           dParm.addData("DEV_CODE", tableParm.getValue("DEV_CODE",i));
           dParm.addData("DEV_CHN_DESC", tableParm.getValue("DEV_CHN_DESC",i));   
           dParm.addData("SEQMAN_FLG", tableParm.getValue("SEQMAN_FLG",i));
           dParm.addData("QTY", dTable.getItemData(i, "QTY"));        
           dParm.addData("UNIT_PRICE", tableParm.getDouble("UNIT_PRICE",i));  
           dParm.addData("MAN_DATE", tableParm.getTimestamp("MAN_DATE",i));               
           // dParm.addData("SCRAP_VALUE", tableParm.getValue("LAST_PRICE",i));
           dParm.addData("SCRAP_VALUE", 0); 
           if(tableParm.getTimestamp("GUAREP_DATE",i)!=null){
        	   dParm.addData("GUAREP_DATE", tableParm.getTimestamp("GUAREP_DATE",i));
               }else{
               dParm.addData("GUAREP_DATE", "");   
               }   
           if(tableParm.getTimestamp("DEP_DATE",i)!=null){
        	   dParm.addData("DEP_DATE", tableParm.getTimestamp("DEP_DATE",i));
               }else{
               dParm.addData("DEP_DATE", "");   
               }  
           //dParm.addData("GUAREP_DATE", tableParm.getTimestamp("GUAREP_DATE",i));
           //dParm.addData("DEP_DATE", tableParm.getTimestamp("DEP_DATE",i));   
           dParm.addData("FILES_WAY", dTable.getItemData(i, "FILES_WAY"));
           dParm.addData("VERIFY_NO", tableParm.getValue("VERIFY_NO",i));
           dParm.addData("VERIFY_NO_SEQ", tableParm.getDouble("VERIFY_NO_SEQ",i));
           dParm.addData("INWAREHOUSE_NO", inwarehouseNo);
           dParm.addData("OPT_USER", Operator.getID());  
           dParm.addData("OPT_DATE", timestamp); 
           dParm.addData("OPT_TERM", Operator.getIP());   
           //打印准备 
           dParm.addData("DEVPRO_CODE", dTable.getItemData(i, "DEVPRO_CODE"));            
           dParm.addData("BRAND", tableParm.getValue("BRAND",i));  
           dParm.addData("SPECIFICATION", tableParm.getValue("SPECIFICATION",i)); 
           dParm.addData("MODEL", tableParm.getValue("MODEL",i));     
           dParm.addData("TOT_VALUE",tableParm.getValue("TOT_VALUE",i));
           stockMParm.addData("DEV_CODE", dTable.getItemData(i, "DEV_CODE"));
           stockMParm.addData("QTY", dTable.getItemData(i, "QTY"));
           stockMParm.addData("OPT_USER", Operator.getID()); 
           stockMParm.addData("OPT_DATE", timestamp);  
           stockMParm.addData("OPT_TERM", Operator.getIP()); 
           stockMParm.addData("REGION_CODE", Operator.getRegion());  
           stockMParm.addData("STOCK_FLG", ""); 
           //残值确认是否应该都为0     
           //stockMParm.addData("SCRAP_VALUE", dTable.getItemData(i, "LAST_PRICE"));
//           stockMParm.addData("MAN_DATE", dTable.getItemData(i, "MAN_DATE"));
//           stockMParm.addData("SCRAP_VALUE", 0); 
//           stockMParm.addData("GUAREP_DATE", dTable.getItemData(i, "GUAREP_DATE"));
//           stockMParm.addData("FILES_WAY", dTable.getItemData(i, "FILES_WAY"));
//           stockMParm.addData("CARE_USER", getValue("OPERATOR"));
//           stockMParm.addData("USE_USER", ""); 
//           stockMParm.addData("LOC_CODE", "");  
//           stockMParm.addData("INWAREHOUSE_DATE", StringTool.getTimestampDate(timestamp));
//           stockMParm.addData("UNIT_PRICE", dTable.getItemData(i, "UNIT_PRICE"));
           TParm devBase = getDevBase(""+dTable.getItemData(i, "DEV_CODE"));          
//           DEL_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;QTY;SUM_QTY;
//           RECEIPT_QTY;UNIT_CODE;DEPR_METHOD;USE_DEADLINE;
//           SEQMAN_FLG;MEASURE_FLG;BENEFIT_FLG;FILES_WAY;VERIFY_NO;
//           VERIFY_NO_SEQ;INWAREHOUSE_NO;SEQ_NO;DEVKIND_CODE
           stockDParm.addData("DEPT_CODE", getValue("DEPT"));
           stockDParm.addData("DEV_CODE", dTable.getItemData(i, "DEV_CODE"));
           stockDParm.addData("DEVKIND_CODE", dTable.getItemData(i, "DEVKIND_CODE"));
           stockDParm.addData("DEVTYPE_CODE", getDevTypeCode((String) dTable.getValueAt(i, 2)));
           stockDParm.addData("DEVPRO_CODE", dTable.getItemData(i, "DEVPRO_CODE"));
           if(devBase.getData("SETDEV_CODE", 0)!=null){  
        	       stockDParm.addData("SETDEV_CODE", devBase.getData("SETDEV_CODE", 0));
               } 
               else{ 
            	   stockDParm.addData("SETDEV_CODE", "");   
               } 
           if(dTable.getItemData(i, "SPECIFICATION")!=null){  
               stockDParm.addData("SPECIFICATION", dTable.getItemData(i, "SPECIFICATION"));
           }  
           else{  
               stockDParm.addData("SPECIFICATION", "");  
           } 
           stockDParm.addData("QTY", dTable.getItemData(i, "QTY"));
//           stockDParm.addData("UNIT_PRICE", dTable.getItemData(i, "UNIT_PRICE"));
//           stockDParm.addData("BUYWAY_CODE", devBase.getData("BUYWAY_CODE", 0));
//           stockDParm.addData("MAN_NATION", devBase.getData("MAN_NATION", 0));
//           stockDParm.addData("MAN_CODE", devBase.getData("MAN_CODE", 0));
//           stockDParm.addData("SUPPLIER_CODE", "");
//           stockDParm.addData("MAN_DATE", dTable.getItemData(i, "MAN_DATE"));
//           stockDParm.addData("MANSEQ_NO", Integer.valueOf(0));
//           stockDParm.addData("FUNDSOURCE", "");
//           stockDParm.addData("APPROVE_AMT", ""); 
//           stockDParm.addData("SELF_AMT", "");
//           stockDParm.addData("GUAREP_DATE", dTable.getItemData(i, "GUAREP_DATE"));
//           stockDParm.addData("DEPR_METHOD", devBase.getData("DEPR_METHOD", 0));
//           stockDParm.addData("DEP_DATE", dTable.getItemData(i, "DEP_DATE"));
//           //stockDParm.addData("SCRAP_VALUE", dTable.getItemData(i, "LAST_PRICE"));
//           stockDParm.addData("SCRAP_VALUE", 0);   
//           stockDParm.addData("QUALITY_LEVEL", "");
//           stockDParm.addData("DEV_CLASS", devBase.getData("DEV_CLASS", 0));
//           stockDParm.addData("STOCK_STATUS", "");
//           stockDParm.addData("SERVICE_STATUS", "");
//           stockDParm.addData("CARE_USER", getValue("OPERATOR"));  
//           stockDParm.addData("USE_USER", "");
//           stockDParm.addData("LOC_CODE", ""); 
//
//           stockDParm.addData("MEASURE_FLG", devBase.getData("MEASURE_FLG", 0));
//           stockDParm.addData("MEASURE_ITEMDESC", devBase.getData("MEASURE_ITEMDESC", 0));
//           stockDParm.addData("MEASURE_DATE", "");
           //1  
           stockDParm.addData("UNIT_PRICE", dTable.getItemData(i, "UNIT_PRICE"));  
           stockDParm.addData("BUYWAY_CODE", devBase.getData("BUYWAY_CODE", 0));
           stockDParm.addData("MAN_NATION", devBase.getData("MAN_NATION", 0));
           stockDParm.addData("MAN_CODE", devBase.getData("MAN_CODE", 0));
           stockDParm.addData("SUPPLIER_CODE", ""); 
           if(dTable.getItemData(i, "MAN_DATE")!=null){
        	   stockDParm.addData("MAN_DATE", dTable.getItemData(i, "MAN_DATE")); 
           } else{
        	   stockDParm.addData("MAN_DATE", "");
           }  
           stockDParm.addData("MANSEQ_NO", Integer.valueOf(0)); 
           stockDParm.addData("FUNDSOURCE", "");
           stockDParm.addData("APPROVE_AMT", ""); 
           stockDParm.addData("SELF_AMT", "");
           stockDParm.addData("DEPR_METHOD", devBase.getData("DEPR_METHOD", 0));
           //stockDParm.addData("SCRAP_VALUE", dTable.getItemData(i, "LAST_PRICE"));
           stockDParm.addData("SCRAP_VALUE", 0);   
           stockDParm.addData("QUALITY_LEVEL", "");
           stockDParm.addData("DEV_CLASS", devBase.getData("DEV_CLASS", 0));
           stockDParm.addData("STOCK_STATUS", "");
           stockDParm.addData("SERVICE_STATUS", "");
           stockDParm.addData("CARE_USER", getValue("OPERATOR"));
           //
           stockDParm.addData("USE_USER", "");
           stockDParm.addData("LOC_CODE", "");       
 
           stockDParm.addData("MEASURE_FLG", devBase.getData("MEASURE_FLG", 0));
           stockDParm.addData("MEASURE_ITEMDESC", devBase.getData("MEASURE_ITEMDESC", 0));
           stockDParm.addData("MEASURE_DATE", "");
           stockDParm.addData("OPT_USER", Operator.getID());
           stockDParm.addData("OPT_DATE", timestamp);
           stockDParm.addData("OPT_TERM", Operator.getIP()); 
           stockDParm.addData("INWAREHOUSE_DATE", StringTool.getTimestampDate(timestamp));
           //折旧终止日期
           stockDParm.addData("DEP_DATE", dTable.getItemData(i, "DEP_DATE")); 
           //DEP_DATE,BRAND,MODEL  厂牌  型号
           if(dTable.getItemData(i, "BRAND")!=null){   
               stockDParm.addData("BRAND", dTable.getItemData(i, "BRAND")); 
           }    
           else{      
               stockDParm.addData("BRAND", "");  
           } 
           if(dTable.getItemData(i, "MODEL")!=null){  
               stockDParm.addData("MODEL", dTable.getItemData(i, "MODEL"));
           }  
           else{    
               stockDParm.addData("MODEL", "");  
           } 
           //int maxDevSeqNo = 0;
           for (int j = 0; j < ddTable.getRowCount(); j++)
           { 
               if (dTable.getValueAt(i, 2).equals(ddTable.getValueAt(j, 5))) { 
               //DEL_FLG   
               if (ddTable.getValueAt(j, 0).equals("Y"))
                 continue;       
                 //fux  select_flg待修改  
//               //SELECT_FLG
//               if (ddTable.getValueAt(j, 1).equals("N"))
//                   continue; 
               //DEVKIND_CODE  IN_DEPT OUT_DEPT 
               String barCode = SystemTool.getInstance().getNo("ALL", "DEV", "BARCODE_NO", "BARCODE_NO");  
               ddParm.addData("INWAREHOUSE_NO", inwarehouseNo);
               ddParm.addData("SEQ_NO", Integer.valueOf(i + 1));      
               ddParm.addData("DEVSEQ_NO",ddTable.getItemData(j, "DEVSEQ_NO"));      
               ddParm.addData("DEV_CODE", ddTable.getItemData(j, "DEV_CODE")); 
               ddParm.addData("DEV_CODE_DETAIL", ddTable.getItemData(j, "DEV_CODE_DETAIL"));
               //序号管理明细序号                         
               ddParm.addData("SEQMAN_FLG", "Y"); 
               if(devBase.getData("SETDEV_CODE", 0)!=null){ 
            	   ddParm.addData("SETDEV_CODE", devBase.getData("SETDEV_CODE", 0));
                   }  
                   else{       
                   ddParm.addData("SETDEV_CODE", "");   
                   }  
               if(ddTable.getItemData(j, "MAN_DATE")!=null){
            	   ddParm.addData("MAN_DATE", ddTable.getItemData(j, "MAN_DATE")); 
               }else{
            	   ddParm.addData("MAN_DATE", "");  
               }
               //fux 20140523             
               if(ddTable.getItemData(j, "MANSEQ_NO")!=null){  
            	   ddParm.addData("MANSEQ_NO", ddTable.getItemData(j, "MAN_SEQ")); 
               }else{
            	   ddParm.addData("MANSEQ_NO", "");  
               }
               if(ddTable.getItemData(j, "SERIAL_NUM")!=null){
            	   ddParm.addData("SERIAL_NUM", ddTable.getItemData(j, "SERIAL_NUM")); 
               }else{
            	   ddParm.addData("SERIAL_NUM", "");  
               }
               if(ddTable.getItemData(j, "WIRELESS_IP")!=null){
            	   ddParm.addData("WIRELESS_IP", ddTable.getItemData(j, "WIRELESS_IP")); 
               }else{
            	   ddParm.addData("WIRELESS_IP", "");  
               }
               if(ddTable.getItemData(j, "IP")!=null){
            	   ddParm.addData("IP", ddTable.getItemData(j, "IP")); 
               }else{
            	   ddParm.addData("IP", "");  
               }
               if(ddTable.getItemData(j, "TERM")!=null){
            	   ddParm.addData("TERM", ddTable.getItemData(j, "TERM")); 
               }else{
            	   ddParm.addData("TERM", "");  
               }
               if(ddTable.getItemData(j, "LOC_CODE")!=null){
            	   ddParm.addData("LOC_CODE", ddTable.getItemData(j, "LOC_CODE")); 
               }else{  
            	   ddParm.addData("LOC_CODE", "");  
               }
               ddParm.addData("SCRAP_VALUE", 0);   
               ddParm.addData("GUAREP_DATE", ddTable.getItemData(j, "GUAREP_DATE"));
               ddParm.addData("DEP_DATE", ddTable.getItemData(j, "DEP_DATE"));
               ddParm.addData("UNIT_PRICE", ddTable.getItemData(i, "TOT_VALUE"));
               ddParm.addData("OPT_USER", Operator.getID());
               ddParm.addData("OPT_DATE", timestamp);  
               ddParm.addData("OPT_TERM", Operator.getIP()); 
               //默认rfid先为"" 加入普通条码           
               ddParm.addData("BARCODE", barCode);     
               ddParm.addData("RFID", "");  
               devBase = getDevBase(""+ddTable.getItemData(j, "DEV_CODE"));
               stockDDParm.addData("DEV_DETAIL_CODE", ddTable.getItemData(j, "DEV_DETAIL_CODE"));
               stockDDParm.addData("DEV_CODE", ddTable.getItemData(j, "DEV_CODE"));  
               stockDDParm.addData("DEVSEQ_NO",ddTable.getItemData(j, "DEVSEQ_NO"));   
               stockDDParm.addData("DEPT_CODE", getValue("DEPT")); 
               stockDDParm.addData("WAST_FLG", "N");   
               stockDDParm.addData("WAIT_ORG_CODE", "");  
               stockDDParm.addData("OPT_USER", Operator.getID()); 
               stockDDParm.addData("OPT_DATE", timestamp);
               stockDDParm.addData("OPT_TERM", Operator.getIP());   
             }    
           }       
         }  
       //设置入库单主表信息 
       mParm.addData("INWAREHOUSE_NO",inwarehouseNo); 
       mParm.addData("VERIFY_NO",dTable.getItemData(0, "VERIFY_NO"));     
       mParm.addData("INWAREHOUSE_DATE",StringTool.getTimestampDate(timestamp));
       mParm.addData("INWAREHOUSE_USER",getValue("OPERATOR"));
       mParm.addData("INWAREHOUSE_DEPT",getValue("DEPT")); 
       mParm.addData("OPT_USER",Operator.getID()); 
       mParm.addData("OPT_DATE",timestamp); 
       mParm.addData("OPT_TERM",Operator.getIP());
       TParm parm = new TParm();
       parm.setData("DEV_INWAREHOUSEM",mParm.getData());
       parm.setData("DEV_INWAREHOUSED",dParm.getData());
       parm.setData("DEV_INWAREHOUSEDD",ddParm.getData()); 
       //如果正好  主库的全部出库入库到二级库则   更新M表的科室   如果只申请一部分，则减少M表主库库存，然后插入一条新的二级库数据(D表也是这样)   
       //这样如果 
       //DEV_STOCKM 是不做变化的,因为只用dev_code的分类为主键,只有报废才变
       //出库减了 那入库确认只能加了
       parm.setData("DEV_STOCKM",stockMParm.getData()); 
       //插入新的D表数据（如030101入库，出库到0202，此时应该插入0202数据）
       parm.setData("DEV_STOCKD",stockDParm.getData());
       //更新动作，更新DEPT_CODE和WAIT_ORG_CODE 
       parm.setData("DEV_STOCKDD",stockDDParm.getData());   
       //加入回更出库M表和D表的DISCHECK_FLG     
       TParm  parmEx = new TParm(); 
       parmEx.setData("EXWAREHOUSE_NO",outStorageNo);   
       parm.setData("DEV_EXWAREHOUSED",getEXWareHouseDData(parmEx).getData()); 
       parm.setData("DEV_EXWAREHOUSEM",getEXWareHouseMData(parmEx).getData()); 
           TParm result = TIOM_AppServer.executeAction(
                   "action.dev.DevAction","generateOutReqInStorageReceipt", parm); 
           if(result.getErrCode() < 0){
               messageBox("保存失败");    
               return;  
           }   
           else{
           messageBox("保存成功"); 
           }
       onPrintNew(parm);       
       onClear(); 
       onQuery(); 
   }
    
   /**
    * 出库细项数据更新状态
    * @param parm TParm
    * @return TParm
    */
   public TParm getEXWareHouseDData(TParm parm) {         
       TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
       TParm exWareHouseD = new TParm(); 
       exWareHouseD.addData("EXWAREHOUSE_NO",parm.getData("EXWAREHOUSE_NO"));  
       for (int i = 0; i < dTable.getRowCount(); i++) {
           if ("Y".equals(dTable.getItemString(i, "DEL_FLG"))) {
               continue;
           }   
           else if ("".equals(dTable.getParmValue().getValue("DEV_CODE", i))) {
               continue;     
           }      
           else  {    
        	   exWareHouseD.addData("SEQ_NO",parmExD.getData("SEQ_NO",i));
               if (dTable.getItemDouble(i, "QTY") == 
               	dTable.getParmValue().getDouble("RECEIPT_QTY", i)  
               	||  dTable.getItemDouble(i, "SUM_QTY") ==
                   	dTable.getParmValue().getDouble("RECEIPT_QTY", i)) {
            	   exWareHouseD.addData("DISCHECK_FLG", "N");
               }        
               else {     
            	   exWareHouseD.addData("DISCHECK_FLG", "Y"); 
               } 
           }
       }
       return exWareHouseD; 
   }

   /**
    * 出库主项更新状态
    * @param parm TParm
    * @return TParm
    */
   public TParm getEXWareHouseMData(TParm parm) {
       TParm exWareHouseM = new TParm();  
       exWareHouseM.addData("EXWAREHOUSE_NO",parm.getData("EXWAREHOUSE_NO"));
       boolean flg = true; 
       TParm exWareHouseD = parm.getParm("DEV_EXWAREHOUSED");
       for (int i = 0; i < exWareHouseD.getCount("EXWAREHOUSE_NO"); i++) {
           if ("Y".equals(exWareHouseD.getValue("DISCHECK_FLG", i))) {
               flg = false;
               break; 
           }  
       }  
       if (flg) {
    	   exWareHouseM.addData("DISCHECK_FLG", "N");
       }       
       else {    
    	   exWareHouseM.addData("DISCHECK_FLG", "Y"); 
       }

       return exWareHouseM;   
   }

   
   /**
    * 得到设备基本属性信息
    * @param devCode String
    * @return TParm
    */
   public TParm getDevBase(String devCode){
       String SQL="SELECT * FROM DEV_BASE WHERE DEV_CODE = '"+devCode+"'";
       TParm parm = new TParm(getDBTool().select(SQL));
       return parm; 
    }
    /**
     * 得到设备编码规则信息
     * @param code String
     * @return String
     */
    private String getDevTypeCode(String code){
       TParm parm = DevTypeTool.getInstance().getDevRule();
       int classify1 = parm.getInt("CLASSIFY1",0);         
       return code.substring(0,classify1);
    } 
    /**
     * 保存后打印设备入库单(入库确认单打印也要想想)
     * @param inParm TParm
     */
    public void onPrintNew(TParm inParm){
       //设置表头信息  
       TParm printParm = new TParm();  
       printParm.setData("HOSP_NAME_T",Operator.getHospitalCHNFullName());
       printParm.setData("INWAREHOUSE_NO_T",inParm.getParm("DEV_INWAREHOUSEM").getData("INWAREHOUSE_NO",0));
       printParm.setData("MAN_CODE_T",getSupDesc(getValueString("SUP_CODE")));
       printParm.setData("INWAREHOUSE_DEPT_T",getDeptDesc(inParm.getParm("DEV_INWAREHOUSEM").getValue("INWAREHOUSE_DEPT",0)));
       printParm.setData("INWAREHOUSE_DATE_T", inParm.getParm("DEV_INWAREHOUSEM").getValue("INWAREHOUSE_DATE",0).substring(0,10).replace('-','/'));
       printParm.setData("CONTACT_T",getValue("SUP_BOSSNAME"));
       printParm.setData("INWAREHOUSE_USER_T",getOperatorName(inParm.getParm("DEV_INWAREHOUSEM").getValue("INWAREHOUSE_USER",0)));
       printParm.setData("VERIFY_NO_T",inParm.getParm("DEV_INWAREHOUSEM").getValue("VERIFY_NO",0));
       printParm.setData("TEL_NO_T",getValue("SUP_TEL"));
       TParm parm = new TParm();
       //将数据拷贝到入库单传入参当中
       //Caused by: java.lang.StringIndexOutOfBoundsException: String index out of range: 10
	   //at java.lang.String.substring(Unknown Source)
	   //at com.javahis.ui.dev.DevOutReqInStorageControl.onPrintNew(DevOutReqInStorageControl.java:1273)
       for(int i = 0;i<inParm.getParm("DEV_INWAREHOUSED").getCount("DEV_CODE");i++){
           cloneTParm(inParm.getParm("DEV_INWAREHOUSED"),parm,i);
       }
       //转换时间日期格式
       for(int i = 0;i<parm.getCount("DEVPRO_CODE");i++){
           parm.setData("MAN_DATE",i,parm.getValue("MAN_DATE",i).substring(0,10).replace('-','/'));
           //入库确认单上无法打印上折旧终止日期与维护终止日期
//           parm.setData("GUAREP_DATE",i,parm.getValue("GUAREP_DATE",i).substring(0,10).replace('-','/'));
//           parm.setData("DEP_DATE",i,parm.getValue("DEP_DATE",i).substring(0,10).replace('-','/'));
           parm.setData("DEVPRO_CODE",i,getDevProDesc(parm.getValue("DEVPRO_CODE",i)));
       }  
       //设置入库单表格信息
       parm.setCount(inParm.getParm("DEV_INWAREHOUSED").getCount("DEV_CODE"));
       parm.addData("SYSTEM", "COLUMNS", "DEVPRO_CODE");
       parm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
       parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
       parm.addData("SYSTEM", "COLUMNS", "QTY");
       parm.addData("SYSTEM", "COLUMNS", "TOT_VALUE");
       parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");  
       parm.addData("SYSTEM", "COLUMNS", "MAN_DATE");
       parm.addData("SYSTEM", "COLUMNS", "SCRAP_VALUE");
//       parm.addData("SYSTEM", "COLUMNS", "GUAREP_DATE");
//       parm.addData("SYSTEM", "COLUMNS", "DEP_DATE");
       printParm.setData("TABLE",parm.getData()); 
       printParm.setData("TITLE", "TEXT", Manager.getOrganization().
				getHospitalCHNFullName(Operator.getRegion()) +
				"入库确认单");      
       openPrintWindow("%ROOT%\\config\\prt\\dev\\DevInStorageReceipt.jhw",printParm);
   }
   /**
    * 清空方法  
    */
   public void onClear(){ 
       setValue("INWAREHOUSE_NO_QUARY","");  
       setValue("INWAREHOUSE_DATE","");
       ((TTextFormat)getComponent("INWAREHOUSE_DATE")).setEnabled(true);
       setValue("VERIFY_NO","");
       setValue("DEPT","");
       ((TTextFormat)getComponent("DEPT")).setEnabled(true);
       setValue("OPERATOR","");
       ((TTextFormat)getComponent("OPERATOR")).setEnabled(true);
       ((TTable)getComponent("EXWAREHOUSE")).removeRowAll();
       ((TTable)getComponent("EXWAREHOUSE")).resetModify();
       setValue("SUP_CODE","");
       setValue("SUP_BOSSNAME","");
       setValue("SUP_TEL","");
       parmD = new TParm();
       parmDD = new TParm();
       initComponent();
       initTableD();
       initTableDD();
       addDRow();
       setTableLock();
   }
   /**
    * 设备入库单主信息单击事件
    */ 
   public void onDevInwarehouse(){ 
       TTable table = ((TTable)getComponent("EXWAREHOUSE"));  
       int row = table.getSelectedRow(); 
       TParm parm = DevInStorageTool.getInstance().selectDevInwarehouseD("" + table.getValueAt(row,1));
       if(parm.getErrCode()<0)
           return;
       parmD = new TParm(); 
       for(int i = 0;i<parm.getCount();i++)   
           cloneTParm(parm,parmD,i);
       ((TTable)getComponent("DEV_INWAREHOUSED")).setParmValue(parm);
       parm = DevInStorageTool.getInstance().selectDevInwarehouseDD("" + table.getValueAt(row,1));
       if(parm.getErrCode()<0)
         return;
       parmDD = new TParm(); 
       for(int i = 0;i<parm.getCount();i++)
           cloneTParm(parm,parmDD,i); 
       ((TTable)getComponent("DEV_INWAREHOUSEDD")).setParmValue(parm);
       ((TTable)getComponent("DEV_INWAREHOUSED")).setLockColumns("0,1,2,3,4,5,6,"+
                                                                 "7,8,9,10,12,15,"+
                                                                 "16,17,18,19,20,"+
                                                                 "21,22,24,25,26,27,28");
       ((TTable)getComponent("DEV_INWAREHOUSEDD")).setLockColumns("0,2,3,4,5,10,"+
                                                                  "11,12,13,14,15");
       setValue("INWAREHOUSE_NO_QUARY",table.getValueAt(row,1));
       setValue("INWAREHOUSE_DATE",table.getValueAt(row,0));
       ((TTextFormat)getComponent("INWAREHOUSE_DATE")).setEnabled(false);
       setValue("VERIFY_NO",table.getValueAt(row,3));
       setValue("DEPT",table.getValueAt(row,2));
       ((TTextFormat)getComponent("DEPT")).setEnabled(false);
       setValue("OPERATOR",table.getValueAt(row,5)); 
       ((TTextFormat)getComponent("OPERATOR")).setEnabled(false);
       if(("" + table.getValueAt(row,3)).length() == 0)
           return;
       TParm supParm = getSupInf("" + table.getValueAt(row,3));
       if(supParm.getErrCode() < 0)
           return;
       if(supParm.getCount() < 0) 
           return;
       setValue("SUP_CODE",supParm.getValue("SUP_CODE",0));
       setValue("SUP_BOSSNAME",supParm.getValue("SUP_SALES1",0));
       setValue("SUP_TEL",supParm.getValue("SUP_SALES1_TEL",0));
   }
   //入中小库,条码也好,RFID标签只扫描不打印
//   /**
//    * 打印RFID标签
//    */
//   public void onPrintRFID() {
//	   TTable table = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
//       int row = table.getSelectedRow();
//       if(row < 0){
//           messageBox("请选择一笔入库单再打印条码");
//           return;
//       }
//       String strRFID = (String)table.getParmValue().getData("RFID", row);
//       if (null == strRFID) {
//    	   messageBox("请先保存入库单，再打印RFID标签");
//    	   return;
//       }
//       try {
//		ModuleDriver.write("COM5", AntTypeE.ONE_ANT, strRFID);
//	} catch (Exception e) {
//		messageBox("RFID标签打印时出错");
//		e.printStackTrace();
//		return;
//	}
//   }

   /**
    * 拷贝TParm
    * @param from TParm
    * @param to TParm
    * @param row int
    */
   private void cloneTParm(TParm from,TParm to,int row){
       String names[] = from.getNames();
       for(int i = 0;i < names.length;i++){
           Object obj = from.getData(names[i],row);
           if(obj == null)
               obj = "";
           to.addData(names[i],obj);
       }
    }

    /**
     * 比较相同列TParm的值是否改变
     * @param parmA TParm
     * @param parmB TParm
     * @param row int
     * @return boolean
     */
    private boolean compareTo(TParm parmA,TParm parmB,int row){
       String names[] = parmA.getNames();
       for(int i = 0;i < names.length;i++){
           if(parmA.getValue(names[i],row).equals(parmB.getValue(names[i],row)))
               continue;
           return false;
       }
       return true;
   }

   /**
    * 检核画面是否有需要保存的信息
    * @return boolean
    */
   private boolean onSaveCheck(){
       int rowCount = 0; 
       TTable tableD = getTTable("DEV_INWAREHOUSED");
       TTable tableDD = getTTable("DEV_INWAREHOUSEDD"); 
  
       //不可为删除并且设备编号不可为空
       for(int i = 0;i<tableD.getRowCount();i++){
           double contd = 0;
           double contdd = 0;  
           if(("" + tableD.getValueAt(i,0)).equals("N")&&
              ("" + tableD.getValueAt(i,3)).length()!=0)
              rowCount++;    
    	   if(tableD.getItemData(i,"SEQMAN_FLG").equals("N"))
    		   continue;     
           contd = tableD.getItemDouble(i, "QTY");
    	   for(int j = 0;j<tableDD.getRowCount();j++){    
         		if ("Y".equals(tableDD.getItemData(j, "SELECT_FLG").toString())&&
         			tableD.getItemData(i,"DEV_CODE").equals(tableDD.getItemData(j, "DEV_CODE"))) {
         			contdd++;
   			}             
         		else       
         			continue;    
       	   }     
       	   if(contd!=contdd){ 
       		   this.messageBox("设备"+tableD.getItemData(i,"DEV_CODE").toString()+"的细表勾选数量和主表出库数量不一致");
               return true;  
       	   } 
       } 
//	   for(int j = 0;j<tableDD.getRowCount();j++){    
//  		if ("Y".equals(tableDD.getItemData(j, "SELECT_FLG").toString())) {
//  			contdd++;    
//		}     
//  		else 
//  			continue; 
//	   }     
//	   if(contd != contdd){
//		   //"+tableD.getItemData(i,"DEV_CODE").toString()+"
//		   this.messageBox("设备的细表勾选数量和主表出库数量不一致");
//        return true;    
//	   } 
       if(getTTable("EXWAREHOUSE").getSelectedRow() < 0 &&
          rowCount == 0){
           messageBox("无保存信息");
           return true; 
       }  
       return false; 
   }
   /**
    * 设备入库单打印 
    */
   public void onPrintReceipt(){    
       TTable table = (TTable)getComponent("EXWAREHOUSE"); 
       int row = table.getSelectedRow();  
       if(row < 0){
    	   this.messageBox("无打印数据");
           return;
       }
       TParm tableParmD = getTTable("DEV_INWAREHOUSED").getParmValue();
       TParm tableParmDD = getTTable("DEV_INWAREHOUSEDD").getParmValue();
       for(int i = 0;i<tableParmD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmD,tableParmD,i))
               continue;
           messageBox("您对数据进行了修改请保存后再打印");
           return;
       }   
       for(int i = 0;i<tableParmDD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmDD,tableParmDD,i))
               continue;
           messageBox("您对数据进行了修改请保存后再打印");
           return;
       }
       TParm printParm = new TParm();
       printParm.setData("HOSP_NAME_T",Operator.getHospitalCHNFullName());
       printParm.setData("INWAREHOUSE_NO_T",table.getValueAt(row,1));
       printParm.setData("MAN_CODE_T",getSupDesc(getValueString("SUP_CODE")));
       printParm.setData("INWAREHOUSE_DEPT_T",getDeptDesc("" + table.getValueAt(row,2)));
       printParm.setData("INWAREHOUSE_DATE_T", ("" + table.getValueAt(row,0)).substring(0,10).replace('-','/'));
       printParm.setData("CONTACT_T",getValue("SUP_BOSSNAME"));
       printParm.setData("INWAREHOUSE_USER_T",getOperatorName("" + table.getValueAt(row,5)));
       printParm.setData("VERIFY_NO_T",table.getValueAt(row,3));
       printParm.setData("TEL_NO_T",getValue("SUP_TEL"));
       TParm tableParm = ((TTable)getComponent("DEV_INWAREHOUSED")).getParmValue();
       TParm parm = new TParm();
       for(int i = 0;i<tableParm.getCount("DEV_CODE");i++){
           cloneTParm(tableParm,parm,i); 
       }   
       for(int i = 0;i<parm.getCount("DEVPRO_CODE");i++){
           parm.setData("MAN_DATE",i,parm.getValue("MAN_DATE",i).substring(0,10).replace('-','/'));
//           parm.setData("GUAREP_DATE",i,parm.getValue("GUAREP_DATE",i).substring(0,10).replace('-','/'));
//           parm.setData("DEP_DATE",i,parm.getValue("DEP_DATE",i).substring(0,10).replace('-','/'));
           parm.setData("DEVPRO_CODE",i,getDevProDesc(parm.getValue("DEVPRO_CODE",i)));
       }  
       parm.setCount(tableParm.getCount("DEV_CODE"));   
       //入库单号  
       parm.addData("SYSTEM", "COLUMNS", "INWAREHOUSE_NO");
       //供货厂商 
       parm.addData("SYSTEM", "COLUMNS", "MAN_CODE_T");
       //入库日期
       parm.addData("SYSTEM", "COLUMNS", "INWAREHOUSE_DATE");
       //设备名称
       parm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
       //规格
       parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");  
       //单位 
       parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC"); 
       //数量
       parm.addData("SYSTEM", "COLUMNS", "QTY"); 
       //单价
       parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");  
       //总金额 
       parm.addData("SYSTEM", "COLUMNS", "TOT_VALUE");  
       //发票日期
       parm.addData("SYSTEM", "COLUMNS", "MAN_DATE");   
       //发票号码
       parm.addData("SYSTEM", "COLUMNS", "LAST_PRICE");
       printParm.setData("TITLE", "TEXT", Manager.getOrganization().  
				getHospitalCHNFullName(Operator.getRegion()) +
				"入库确认单");  
       printParm.setData("TABLE",parm.getData());  
       openPrintWindow("%ROOT%\\config\\prt\\dev\\DevInStorageReceipt.jhw",printParm);
   }
   //残兵已陌路，兵败如山倒

   /**
    * 取得设备属性中文描述
    * @param devProCode String
    * @return String
    */
   public String getDevProDesc(String devProCode){
       TParm parm = new TParm(getDBTool().select(" SELECT CHN_DESC FROM SYS_DICTIONARY "+
                                                 " WHERE GROUP_ID = 'DEVPRO_CODE'"+
                                                 " AND   ID = '"+devProCode+"'"));
       return parm.getValue("CHN_DESC",0);
    }

    /**
     * 生成设备条码
     */ 
    public void onBarcodePrint(){
       if(getTTable("EXWAREHOUSE").getSelectedRow() < 0){
           messageBox("请选择一笔入库单再打印条码");
           return; 
       } 
       TParm tableParmD = getTTable("DEV_INWAREHOUSED").getParmValue();
       TParm tableParmDD = getTTable("DEV_INWAREHOUSEDD").getParmValue();
       for(int i = 0;i<tableParmD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmD,tableParmD,i))
               continue;
           messageBox("您对数据进行了修改请保存后在打印");
           return;
       }
       for(int i = 0;i<tableParmDD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmDD,tableParmDD,i))
               continue;
           messageBox("您对数据进行了修改请保存后在打印");
           return;
       }
       TParm parm = new TParm();
       parm.setData("DEV_INWAREHOUSED",((TTable)getComponent("DEV_INWAREHOUSED")).getParmValue().getData());
       parm.setData("DEV_INWAREHOUSEDD",((TTable)getComponent("DEV_INWAREHOUSEDD")).getParmValue().getData());
       parm.setData("INWAREHOUSE_NO",getValue("INWAREHOUSE_NO_QUARY"));
       parm.setData("INWAREHOUSE_DATE",getValue("INWAREHOUSE_DATE"));
       parm.setData("INWAREHOUSE_DEPT",getValue("DEPT"));
       parm.setData("INWAREHOUSE_USER",getValue("OPERATOR"));
       openDialog("%ROOT%\\config\\dev\\DEVBarcodeUI.x", parm);
   }
   /**
    * 返回数据库操作工具
    * @return TJDODBTool
    */
   public TJDODBTool getDBTool() {
       return TJDODBTool.getInstance();
   }
   /**
    * 拿到科室
    * @param deptCode String
    * @return String 
    */
    public String getDeptDesc(String deptCode){
        TParm parm = new TParm(getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'"));
        return parm.getValue("DEPT_CHN_DESC",0);
    }
    /**
     * 拿到用户名  
     * @param userID String
     * @return String
     */
    public String getOperatorName(String userID){
        TParm parm = new TParm(getDBTool().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"+userID+"'"));
        return parm.getValue("USER_NAME",0);
    }
    /**
      * 拿到供应厂商
      * @param deptCode String
      * @return String
      */
     public String getSupDesc(String supCode){
         TParm parm = new TParm(getDBTool().select("SELECT SUP_CHN_DESC FROM SYS_SUPPLIER WHERE SUP_CODE='"+supCode+"'"));
         return parm.getValue("SUP_CHN_DESC",0);
    }
   /**
    * 拿到TTable
    * @param tag String
    * @return TTable
    */
   public TTable getTTable(String tag){
       return (TTable)getComponent(tag);
    }
   
   /**
    * 拿到TTextField
    * @param tag String
    * @return getTextField
    */
   public TTextField getTextField(String tag){
       return (TTextField)getComponent(tag);
    }
    /**
     * 设备录入事件
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateEditComoponent(Component com,int row,int column){
        //设备属性
        String devProCode = ""+getTTable("DEV_INWAREHOUSED").getValueAt(row,1);
        //状态条显示
        callFunction("UI|setSysStatus","");
        //拿到列名
        String columnName = getFactColumnName("DEV_INWAREHOUSED",column);
        if(!"DEV_CODE".equals(columnName))
            return;
        if(!(com instanceof TTextField))
            return;
        TTextField textFilter = (TTextField)com;
        textFilter.onInit();
        if(("" +getTTable("DEV_INWAREHOUSED").getValueAt(row,column)).length() != 0)
            return;
        TParm parm = new TParm();
        parm.setData("DEVPRO_CODE",devProCode);
        parm.setData("ACTIVE","Y");
        //设置弹出菜单
        textFilter.setPopupMenuParameter("DEVBASE",getConfigParm().newConfig("%ROOT%\\config\\sys\\DEVBASEPopupUI.x"),parm);
        //定义接受返回值方法
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE,this,"popReturn");
    }

    /**
     * 设备录入返回值事件
     * @param tag String 
     * @param obj Object
     */
    public void popReturn(String tag,Object obj){
        //判断对象是否为空和是否为TParm类型
        if (obj == null &&!(obj instanceof TParm))
            return ;
        if(getValueString("INWAREHOUSE_DATE").length() == 0){
            messageBox("请录入入库日期");
            return;
        }
        //类型转换成TParm
        TParm parm = (TParm)obj;   
        Timestamp timestamp = StringTool.getTimestamp(SystemTool.getInstance().getDate().toString(),"yyyy-MM-dd");
 
        String inWarehouseDate = getValueString("INWAREHOUSE_DATE");
        int year = Integer.parseInt(inWarehouseDate.substring(0, 4)) + parm.getInt("USE_DEADLINE");
        String depDate = year + inWarehouseDate.substring(4,inWarehouseDate.length()); 
        int batchNo = DevInStorageTool.getInstance().getUseBatchSeq(parm.getValue("DEV_CODE"), 
                                                                   StringTool.getTimestamp(depDate,"yyyy-MM-dd"),
                                                                   timestamp);
        //检核录入设备是否已经存在
        for(int i = 0;i< getTTable("DEV_INWAREHOUSED").getRowCount();i++){
            if (!parm.getValue("DEV_CODE").equals(getTTable("DEV_INWAREHOUSED").getValueAt(i, 2)))
                continue;
            if(batchNo != Integer.parseInt(""+getTTable("DEV_INWAREHOUSED").getValueAt(i, 3)))
                continue;
            messageBox("此设备已经存在,并已将数量添加到相应列中");
            int qty = 1 + Integer.parseInt("" + getTTable("DEV_INWAREHOUSED").getValueAt(i,7));
            updateTableData("DEV_INWAREHOUSED", i, 7, qty);
            updateTableData("DEV_INWAREHOUSED", i, 12, qty * Double.parseDouble("" + getTTable("DEV_INWAREHOUSED").getValueAt(i,11)));
            updateTableData("DEV_INWAREHOUSED", getTTable("DEV_INWAREHOUSED").getSelectedRow(), 2, "");
            resetDDTableData();
            return;
        }  
        //system.out.println("parm"+parm);
        callFunction("UI|setSysStatus",parm.getValue("DEV_CODE")+":"+parm.getValue("DEV_CHN_DESC")+parm.getValue("SPECIFICATION"));
        getTTable("DEV_INWAREHOUSED").acceptText();
        TParm tableParm =  new TParm();
        TParm tableParmDD = new TParm();
        tableParm.setData("DEL_FLG","N");
        tableParm.setData("DEVPRO_CODE",parm.getData("DEVPRO_CODE"));
        tableParm.setData("DEV_CODE",parm.getData("DEV_CODE"));
        tableParm.setData("DEV_CHN_DESC",parm.getData("DEV_CHN_DESC"));
        tableParm.setData("SPECIFICATION",parm.getData("SPECIFICATION"));
        tableParm.setData("MAN_CODE",parm.getData("MAN_CODE"));
        tableParm.setData("QTY",1);
        tableParm.setData("SUM_QTY",0);
        tableParm.setData("RECEIPT_QTY",0);
        tableParm.setData("UNIT_CODE",parm.getData("UNIT_CODE"));
        tableParm.setData("UNIT_PRICE",parm.getData("UNIT_PRICE"));
        tableParm.setData("TOT_VALUE",parm.getDouble("UNIT_PRICE"));
        tableParm.setData("MAN_DATE",timestamp);
        tableParm.setData("LAST_PRICE",parm.getData("UNIT_PRICE"));
        tableParm.setData("GUAREP_DATE",timestamp);
        tableParm.setData("DEPR_METHOD",parm.getData("DEPR_METHOD"));
        tableParm.setData("USE_DEADLINE",parm.getData("USE_DEADLINE"));
        tableParm.setData("DEP_DATE",StringTool.getTimestamp(depDate,"yyyy-MM-dd"));
        tableParm.setData("MAN_NATION",parm.getData("MAN_NATION"));
        tableParm.setData("SEQMAN_FLG",parm.getData("SEQMAN_FLG"));
        tableParm.setData("MEASURE_FLG",parm.getData("MEASURE_FLG"));
        tableParm.setData("BENEFIT_FLG",parm.getData("BENEFIT_FLG"));
        tableParm.setData("FILES_WAY","");
        tableParm.setData("VERIFY_NO","");
        tableParm.setData("VERIFY_NO_SEQ","");
        tableParm.setData("INWAREHOUSE_NO",""); 
        tableParm.setData("SEQ_NO",""); 
        tableParm.setData("DEVKIND_CODE",parm.getData("DEVKIND_CODE"));
        ((TTable)getComponent("DEV_INWAREHOUSED")).removeRow(((TTable)getComponent("DEV_INWAREHOUSED")).getRowCount()-1);
        ((TTable)getComponent("DEV_INWAREHOUSED")).addRow(tableParm);
        //做序号管理的设备展开序号管理明细
        if("Y".equals(parm.getData("SEQMAN_FLG"))){
            setDevDDTablePop(tableParmDD, tableParm);
            ((TTable)getComponent("DEV_INWAREHOUSEDD")).addRow(tableParmDD);
        }
        addDRow(); 
        setTableLock();     
        ((TTextFormat)getComponent("INWAREHOUSE_DATE")).setEnabled(false);
    }
    /**
     * 重新整理序号管理明细信息
     */
    public void resetDDTableData(){
       TTable tableD = getTTable("DEV_INWAREHOUSED");
       TParm parmD = tableD.getParmValue();
       TTable tableDD = getTTable("DEV_INWAREHOUSEDD");
       TParm parmDD = new TParm();
       for(int i = 0;i < parmD.getCount("DEV_CODE");i++){
           if(!"Y".equals(parmD.getValue("SEQMAN_FLG",i)))
               continue;
           addDevDDTable(parmDD, parmD, i);
       }
       tableDD.setParmValue(parmDD);
   }
   /**
    * 设置设备录入时序号管理明细信息
    * @param tableParmDD TParm
    * @param tableParm TParm
    */
   public void setDevDDTablePop(TParm tableParmDD,TParm tableParm){
            tableParmDD.setData("DEL_FLG","N");
            tableParmDD.setData("DEVPRO_CODE",tableParm.getData("DEVPRO_CODE"));
            tableParmDD.setData("DEV_CODE",tableParm.getData("DEV_CODE"));
            tableParmDD.setData("DDSEQ_NO",1);
            tableParmDD.setData("DEV_CHN_DESC",tableParm.getData("DEV_CHN_DESC"));
            tableParmDD.setData("MAIN_DEV","");
            tableParmDD.setData("MAN_DATE",tableParm.getData("MAN_DATE"));
            tableParmDD.setData("MAN_SEQ","");
            tableParmDD.setData("LAST_PRICE",tableParm.getData("LAST_PRICE"));
            tableParmDD.setData("GUAREP_DATE",tableParm.getData("GUAREP_DATE"));
            tableParmDD.setData("DEP_DATE",tableParm.getData("DEP_DATE"));
            tableParmDD.setData("TOT_VALUE",tableParm.getData("UNIT_PRICE"));
            tableParmDD.setData("INWAREHOUSE_NO","");
            tableParmDD.setData("SEQ_NO","");
            tableParmDD.setData("DEVSEQ_NO","");
    }
    /**
     * 得到表格列名
     * @param tableTag String
     * @param column int
     * @return String
     */
    public String getFactColumnName(String tableTag,int column){
        int col = getThisColumnIndex(column);
        return getTTable(tableTag).getDataStoreColumnName(col);
    }

    /**
     * 得到表格列索引
     * @param column int
     * @return int
     */
    public int getThisColumnIndex(int column){
        return getTTable("DEV_INWAREHOUSED").getColumnModel().getColumnIndex(column);
    }

    /**
     * table监听事件
     * @param obj Object  
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable chargeTable = (TTable) obj;
        chargeTable.acceptText();
        return true;
    }
    
    /**  
     * tabledd监听事件
     * @param obj Object  
     * @return boolean
     */
    public boolean onTableDDComponent(Object obj) {
        TTable chargeTable = (TTable) obj;
        chargeTable.acceptText();  
        return true;
    }
    
    
    /**
     * 开始扫描
     * @param obj Object  
     * @return 
     */
    public void onStartScan(Object obj) {
     messageBox("开始扫描");
    } 
    
    
    /**
     * 结束扫描
     * @param obj Object  
     * @return 
     */
    public void onStopScan(Object obj) {
     messageBox("未连接扫描程序！"); 
     messageBox("结束扫描！"); 
    }
}
