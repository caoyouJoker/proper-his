package com.javahis.ui.inv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import jdo.inv.INV;
import jdo.inv.INVNewRepackTool;
import jdo.inv.INVPackDTool;
import jdo.inv.INVPackMTool;
import jdo.inv.INVPublicTool;
import jdo.inv.INVSQL;
import jdo.inv.InvBaseTool;
import jdo.inv.InvPackStockMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.datawindow.DataStore;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 *               
 * <p>Title: 手术包打包入库</p>
 *  
 * <p>Description:手术包打包入库 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *  
 * <p>Company:javahis  </p>  
 *
 * @author wangm 2013-07-01   
 * @version 1.0
 */
  //条码号区号原则调整：
public class INVPackageControl
    extends TControl {
    /**
     * 主表  
     */
    private TTable tableM;
    /**
     * 细表
     */
    private TTable tableD;
    /**
     * 计算库存的方法
     */
    private INV inv = new INV();
    /**
     * 记录手术包返回属性
     */
    private TParm returnParm;
    /**
     * 记录物资来自序号管理
     */
    private Map map = new HashMap();
    /**
     * 是否新增注记
     */
    private boolean isNew = false;
    /**
     * 手术包条码
     */
    private String barcode = "";
    /**
     * 库存是否够本次打包  
     * true 够
     * false 不够
     */
    private boolean enough = true; 	//2014-01-12添加
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
//        //物资弹出窗口
//        callFunction("UI|PACK_CODE|setPopupMenuParameter", "PACKCODE",
//                     "%ROOT%\\config\\inv\\INVChoose.x");
//        //接受回传值
//        callFunction("UI|PACK_CODE|addEventListener",
//                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
         
        TParm parm = new TParm();
        // 设置弹出菜单
        getTextField("PACK_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
        // 定义接受返回值方法
        getTextField("PACK_CODE").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn");
        
        
        
        //zhangyong20091110
        // 添加侦听事件
        addEventListener("TABLEM->" + TTableEvent.CHANGE_VALUE,
                         "onTableMChangeValue");

        //table不可编辑
        tableM = (TTable) getComponent("TABLEM");
        tableD = (TTable) getComponent("TABLED");
        //初始化table
        onInitTable();
        //初始化界面上的数据
        initValue();
        initCombox();
        //添加观察者
        observer();
        this.callFunction("UI|new|setEnabled", true);
        this.callFunction("UI|save|setEnabled", false);
        //设置默认科室
        TTextFormat tf = (TTextFormat)getComponent("ORG_CODE");
        tf.setValue(Operator.getDept());
    }

    /**
     * zhangyong20091110
     * 表格值改变事件
     *
     * @param obj
     *            Object
     */
    public boolean onTableMChangeValue(Object obj) {
        // 值改变的单元格
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // 判断数据改变
        if (node.getValue().equals(node.getOldValue()))
            return true;
        int column = node.getColumn();
        int row = node.getRow();
        if (column == 2) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("打包数量不能小于或等于0");
                return true;
            }
            if(tableM.getDataStore().getItemInt(row, "PACK_SEQ_NO")>0&&qty!=1){
            	this.messageBox("手术包打包数量必须为1！");
                return true;
            }
//            double amt1 = StringTool.round(qty *
//                                           this.getValueDouble("USE_COST"),
//                                           2);
//            double amt2 = StringTool.round(qty *
//                                           this.getValueDouble("ONCE_USE_COST"),
//                                           2);
//            // 主项信息变更
//            tableM.getDataStore().setItem(row, "USE_COST", amt1);
//            tableM.getDataStore().setItem(row, "ONCE_USE_COST", amt2);
//            tableM.setItem(row, "USE_COST", amt1);
//            tableM.setItem(row, "ONCE_USE_COST", amt2);
            // 细项信息变更
            for (int i = 0; i < tableD.getDataStore().rowCount(); i++) {
                tableD.setItem(i, "QTY", tableD.getDataStore().
                               getItemDouble(i, "QTY") * qty /
                               TypeTool.getDouble(node.getOldValue()));
                tableM.removeRowAll();
                tableD.removeRowAll();
                checkStockQty(qty);   //重新计算库存
                
            }
            return false;
        }
        return true;
    }

    /**
     * 添加观察者
     */
    public void observer() {
        //给明细添加观察者
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(
            "SELECT INV_CODE,INV_ABS_DESC AS INV_CHN_DESC,DESCRIPTION FROM INV_BASE");
        dataStore.retrieve();
        //明细表添加观察者
        TDS tds = (TDS) tableD.getDataStore();
        tds.addObserver(new INVBaseTob(dataStore));

        //给主表添加观察者
        dataStore = new TDataStore();
        dataStore.setSQL("SELECT PACK_CODE,PACK_DESC FROM INV_PACKM");
        dataStore.retrieve();
        //明细表添加观察者
        tds = (TDS) tableM.getDataStore();
        tds.addObserver(new INVPackTob(dataStore));

    }

    /**
     * 物资科室
     */
    public void initCombox() {
    	
//2013-6-5注释    	
//        TParm parm = INVOrgTool.getInstance().getDept();
//        TComboBox comboBox = (TComboBox)this.getComponent("ORG_CODE");
//        comboBox.setParmValue(parm);
//        comboBox.updateUI();
//        //默认入库科室
//        String deptCode = "";
//        TParm toOrgParm = INVPublicTool.getInstance().getOrgCode("B");
//        if (toOrgParm.getErrCode() >= 0) {
//            deptCode = toOrgParm.getValue("ORG_CODE", 0);
//            comboBox.setValue(deptCode);
//        }
//        comboBox.setValue(deptCode);


        TParm parmUser = INVPublicTool.getInstance().getInvOperator();
        TComboBox comboBox = new TComboBox();
        comboBox.setParmMap("id:USER_ID;name:USER_NAME;py1:NAME_PYCODE");
        comboBox.setParmValue(parmUser);
        comboBox.setShowID(true);
        comboBox.setShowName(true);
        comboBox.setExpandWidth(30);
        comboBox.setTableShowList("name");

        tableD.addItem("USER", comboBox);


    }

//    /**
//     * 默认查询时间
//     */
//    public void setDate() {
//        String disinfectionUser = getValueString("DISINFECTION_USER");
//        if (disinfectionUser == null | disinfectionUser.length() == 0) {
//            this.setValue("DISINFECTION_DATE", null);
//            this.setValue("VALUE_DATE", null);
//            return;
//        }
//        //取系统事件
//        Timestamp date = SystemTool.getInstance().getDate();
//        this.setValue("DISINFECTION_DATE", date);
//        this.setValue("VALUE_DATE", date);
//    }
    /**
     * 初始化table
     */
    public void onInitTable() {
        //主表
        tableM.setSQL("select * from INV_PACKSTOCKM WHERE PACK_CODE IS NULL");
        tableM.retrieve();
        //细表
        tableD.setSQL("select * from INV_PACKSTOCKD WHERE PACK_CODE IS NULL");
        tableD.retrieve();
    }

    /**
     * 初始化界面上的数据
     */
    public void initValue() {
        //状态
        setValue("STATUS", "0");
        initCombox();
    }

    /**
     * 查询PACK_CODE
     */
    public void onQuery() {
    	
    	tableM.setLockColumns("0,1,2,3,4,5,7,8,9,10,11");//2014-01-16
    	
        String sql = "SELECT * FROM INV_PACKSTOCKM WHERE 1=1 ";
        //手术包
        String value = getValueString("PACK_CODE");
        //打包科室
        String orgCode = getValueString("ORG_CODE");
        //20141231 wangjingchun add start
        //使用状态
        String status = this.getValueString("STATUS");
        if(status != null && status.length() != 0){
        	sql += " AND STATUS = '"+status+"' ";
        }
        //20141231 wangjingchun add end
        if (value != null && value.length() != 0)
            sql += " AND PACK_CODE = '" + value + "' ";
        if(orgCode != null && orgCode.length() != 0){
        	sql += " AND ORG_CODE = '" + orgCode + "' ";
        }
        sql += " ORDER BY PACK_CODE,PACK_SEQ_NO,PACK_BATCH_NO DESC";
        //刷新table
        retrieveTable(tableM, sql);
        //查询后的处理
        afterQuery();
    }

    /**
     * 查询后的处理
     */
    public void afterQuery() {
        //可编辑状态
        clearEnable(false);
        //其它不可编辑
        setOtherEnable(false);
        this.callFunction("UI|new|setEnabled", false);
        this.callFunction("UI|save|setEnabled", false);
    }

    /**  
     * 其它的不可编辑
     * @param statuse boolean
     */
    public void setOtherEnable(boolean statuse) {
        //新增
        this.callFunction("UI|new|setEnabled", statuse);
        //保存
        this.callFunction("UI|save|setEnabled", statuse);
        //固定成本
        this.callFunction("UI|USE_COST|setEnabled", statuse);
        //消毒效期
        this.callFunction("UI|VALUE_DATE|setEnabled", statuse);
        //消毒人员
        this.callFunction("UI|DISINFECTION_USER|setEnabled", statuse);
        //使用状态
        this.callFunction("UI|STATUS|setEnabled", statuse);
        //消毒日期
        this.callFunction("UI|DISINFECTION_DATE|setEnabled", statuse);

    }

    /**
     * 主表的点击事件
     */
    public void onTableMClicked() {
        //新增检核数据变更
//        if (!this.checkValueChange())
//            return;
        //zhangyong20100913 begin
        int row = tableM.getSelectedRow();
        TParm parm = tableM.getDataStore().getRowParm(row);
//        setValueForParm("PACK_CODE;PACK_SEQ_NO;QTY;ORG_CODE;" +
//                        "DISINFECTION_DATE;VALUE_DATE;DISINFECTION_USER;STATUS",
//                        parm);
        setValueForParm("PACK_CODE;PACK_SEQ_NO;QTY;" +
        		"DISINFECTION_DATE;VALUE_DATE;DISINFECTION_USER;STATUS",
        		parm);
        String pack_code = parm.getValue("PACK_CODE");
        String packM_sql = "SELECT * FROM INV_PACKM WHERE PACK_CODE = '" + pack_code + "'";
        TParm packM = new TParm(TJDODBTool.getInstance().select(packM_sql));
        //System.out.println("packM---"+packM);
        this.setValue("PACK_DESC",packM.getValue("PACK_DESC",0));
        //zhangyong20100913 end
        TMenuItem save_item = (TMenuItem)this.getComponent("save");
        if (!save_item.isEnabled()) {
            //包号    
            String sql = "select * from INV_PACKSTOCKD WHERE PACK_CODE='" +
                getValue("PACK_CODE") + "' AND PACK_SEQ_NO=" +
                getValueInt("PACK_SEQ_NO") + " AND PACK_BATCH_NO = " + parm.getValue("PACK_BATCH_NO");
            //刷新table
            retrieveTable(tableD, sql);
        }
        //zhangyong20091110 end
    }
    
    /**
	 * 查看图片
	 * wangjingchun add 20150226
	 */
	public void onPicture(){

		TTable table = (TTable) this.getComponent("TABLEM");
		int row = table.getSelectedRow();
		if(row < 0){
			this.messageBox("请选择手术包");
			return;
		}
		TDataStore dataStore = table.getDataStore();
		TParm oneRow = dataStore.getRowParm(row);
		String packCode = oneRow.getValue("PACK_CODE");
		TParm parm = new TParm();
		parm.setData("PACK_CODE", packCode);
		String photoName = parm.getValue("PACK_CODE")+ ".jpg";
		String root = TIOM_FileServer.getRoot();
		String dir = TIOM_FileServer.getPath("OpeInfPIC.ServerPath");
		dir = root + dir ;
		byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				dir + photoName);
		if(data == null){
			this.messageBox("此手术包未上传图片");
			return;
		}
		String pic_dir = "C:\\JavaHisFile\\OpePic\\";
		try {
			  File file = new File(pic_dir);
			  if(!file.exists()){
				  file.mkdir();
			  }
		      // 源文件  
		      FileOutputStream fileOutputStream = new FileOutputStream(new File(pic_dir+photoName));  
		      fileOutputStream.write(data);  
		      // 关闭流  
		      fileOutputStream.close();  
		      Runtime.getRuntime().exec("rundll32 c:\\Windows\\System32\\shimgvw.dll,ImageView_Fullscreen "+pic_dir+photoName);
		  } catch (FileNotFoundException e) {  
		      e.printStackTrace();  
		  } catch (IOException e) {  
		      // TODO Auto-generated catch block  
		      e.printStackTrace();
		  }
	}

	
	/**
	 * 导入图片
	 * wangjingchun add 20150226
	 */
	public void onImportPicture(){
		TTable table = (TTable) this.getComponent("TABLEM");
		int row = table.getSelectedRow();
		if(row < 0){
			this.messageBox("请选择手术包");
			return;
		}
		TDataStore dataStore = table.getDataStore();
		TParm oneRow = dataStore.getRowParm(row);
		String packCode = oneRow.getValue("PACK_CODE");
		TParm parm = new TParm();
		parm.setData("PACK_CODE",packCode);
		this.openDialog("%ROOT%\\config\\inv\\ImportPicture.x",parm);
	}

    /**
     * 刷新table
     * @param table TTable
     * @param sql String
     */
    public void retrieveTable(TTable table, String sql) {
        table.setSQL(sql);
        table.retrieve();
        table.setDSValue();
    }

//    /**
//     * 手术包选择返回数据处理
//     * @param tag String
//     * @param obj Object
//     */
//    public void popReturn(String tag, Object obj) {
//        if (obj == null)
//            return;
//        returnParm = (TParm) obj;
//        //调用处理返回数据的方法
//        tableD.acceptText();
//        onDealRetrunValue(returnParm);
//    }
    //fux modify 2014  --- 取得最大序号 然后放入条码中
    /**
     * 接受返回值方法wm2013
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("请选择打包部门");
            getTextField("PACK_CODE").setValue("");
            return;
        }
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        returnParm = (TParm) obj;  //wm2013-06-05  添加
        String pack_code = parm.getValue("PACK_CODE");
        if (!StringUtil.isNullString(pack_code))
            getTextField("PACK_CODE").setValue(pack_code);
        String pack_desc = parm.getValue("PACK_DESC");
        int packSeqNo = 0;
        if (!StringUtil.isNullString(pack_desc))
            getTextField("PACK_DESC").setValue(pack_desc);
        if ("0".equals(parm.getValue("SEQ_FLG"))) {
            this.setValue("PACK_SEQ_NO", 0);
//            seq_flg = "0";
        }
        else {
            TParm packStockM = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getPackMaxSeq(this.getValueString("ORG_CODE"),
                              this.getValueString("PACK_CODE"))));
            packSeqNo = packStockM.getInt("PACK_SEQ_NO", 0) + 1;
            this.setValue("PACK_SEQ_NO",
            		packSeqNo); 
            
 //           seq_flg = "1";
        }
        this.setValue("USE_COST", parm.getValue("USE_COST"));
        TParm packD = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getINVPackDByCostPrice(getValueString("PACK_CODE"))));
        double once_use_cost = 0;
        for (int i = 0; i < packD.getCount("COST_PRICE"); i++) {
        	
        	if(packD.getValue("PACK_TYPE", i).equals("1")){
        		once_use_cost += packD.getDouble("COST_PRICE", i) * packD.getDouble("QTY", i);
        	}
        }
        returnParm.setData("PACK_SEQ_NO", packSeqNo);
        this.setValue("ONCE_USE_COST", once_use_cost);
    }
    

    /**  
     * 处理手术包选择返回数据
     * @param parm TParm
     */
    public void onDealRetrunValue(TParm parm) {
        //包号
        setValue("PACK_CODE", parm.getValue("PACK_CODE"));
        //包名
        setValue("PACK_DESC", parm.getValue("PACK_DESC"));
        //序号
        setValue("PACK_SEQ_NO", onPackSeqNo(parm.getValue("PACK_CODE")));
        //固定价值
        setValue("USE_COST", parm.getValue("USE_COST"));
        this.callFunction("UI|PACK_CODE|setEnabled", false);
        this.callFunction("UI|PACK_SEQ_NO|setEnabled", false);
    }

    /**
     * 查找手术包序号
     * @param packCode String
     * @return int
     */
    public int onPackSeqNo(String packCode) {
        int packSeqNo = 0;
        //如果有序号管理
        if (onCheckSeqFlg()) {
            packSeqNo = getPackSeqNo(packCode);
        }
        return packSeqNo;
    }

    /**
     * 从数据库中计算手术包序号
     * @param packCode String
     * @return int
     */
    public int getPackSeqNo(String packCode) {
        //查找手术包
        TParm result = InvPackStockMTool.getInstance().getStockSeq(packCode);	//wm2013-06-04    INVPackStockMTool.
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return -1;
        }
        //获得序号
        int packSeqNo = result.getInt("MAX(PACK_SEQ_NO)", 0) + 1;
        return packSeqNo;
    }

    /**
     * 检核手术包是否有序号管理
     * @return boolean
     */
    public boolean onCheckSeqFlg() {
        String seqManFlg = returnParm.getValue("SEQ_FLG");
        if (seqManFlg == null || seqManFlg.length() == 0 ||
            seqManFlg.equals("0"))
            return false;
        return true;
    }

    /**
     * 新增
     */
    public void onNew() {
        //新增检核数据变更
        if (!this.checkValueChange())
            return;
        //新增检核数据录入
        if (!newTableMCheckValue())
            return;
        //给主表添加属性
        //zhangyong20091110 begin
        String returnStr = setTabaleMValue();
        if ("0".equals(returnStr)) {
            // 非序号管理
            //处理物资明细数据
            if (!onGetInv()) {
                this.callFunction("UI|new|setEnabled", false);
                this.callFunction("UI|save|setEnabled", false);
                return;
            }
            //不可编辑
            clearEnable(false);
            this.callFunction("UI|new|setEnabled", false);
            this.callFunction("UI|save|setEnabled", true);
        }
        else if ("1".equals(returnStr)) {
            // 序号管理
            //处理物资明细数据
            if (!onGetInv()) {
                this.callFunction("UI|new|setEnabled", false);
                this.callFunction("UI|save|setEnabled", false);
                return;
            }
            //不可编辑
            clearEnable(false);
            this.callFunction("UI|new|setEnabled", false);
            this.callFunction("UI|save|setEnabled", true);
        }
        //zhangyong20091110 end
    }

    /**
     * 新增手术包时检核数据
     * @return boolean
     */
    public boolean newTableMCheckValue() {
        //手术包
        String packCode = this.getValueString("PACK_CODE");
        if (packCode == null || packCode.length() == 0) {
            messageBox("请输入手术包号!");
            return false;
        }
        //打包科室
        String orgCode = getValueString("ORG_CODE");
        if (orgCode == null || orgCode.length() == 0) {
            messageBox("请输入打包科室!");
            return false;
        }
        //固定成本
        double useCost = getValueDouble("USE_COST");
        if (useCost < 0) {   // if (useCost <= 0) {
            messageBox("请输入固定成本!");
            return false;
        }
        //打包科室
        String status = getValueString("STATUS");
        if (status == null || status.length() == 0) {
            messageBox("请选择状态!");
            return false;
        }
        return true;
    }

    /**
     * zhangyong20091110
     * 新增时给主表添加录入属性
     */
    public String setTabaleMValue() {
        //zhangyong20091110 begin 添加序号管理和非序号管理的区别
        String sql = INVSQL.getInvPackM(this.getValueString("PACK_CODE"));
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if ("0".equals(parm.getValue("SEQ_FLG", 0))) {
            // 非序号管理
            // 打开数量的锁定列
            tableM.setLockColumns("0,1,3,4,5,7,8,9,10,11");
            int row = tableM.addRow();
            TDataStore dataStore = tableM.getDataStore();
            //包号
            dataStore.setItem(row, "PACK_CODE", getValueString("PACK_CODE"));
            //数量(默认数值为1，可修改)
            dataStore.setItem(row, "QTY", 1);
            //序号
            dataStore.setItem(row, "PACK_SEQ_NO", 0);
            //固定成本
            dataStore.setItem(row, "USE_COST", getValueDouble("USE_COST"));
            Timestamp valueDate = TCM_Transform.getTimestamp(getValue(
                "DISINFECTION_DATE"));
            //消毒日期
            dataStore.setItem(row, "DISINFECTION_DATE", valueDate);
            if (valueDate == null)
                dataStore.setItem(row, "DISINFECTION_DATE", new TNull(Timestamp.class));
            //消毒效期
            valueDate = TCM_Transform.getTimestamp(getValue("VALUE_DATE"));
            dataStore.setItem(row, "VALUE_DATE", valueDate);
            if (valueDate == null)
                dataStore.setItem(row, "VALUE_DATE", new TNull(Timestamp.class));
            //消毒人员
            String valueStr = getValueString("DISINFECTION_USER");
            dataStore.setItem(row, "DISINFECTION_USER", valueStr);
            if (valueStr == null)
                dataStore.setItem(row, "DISINFECTION_USER", new TNull(String.class));
            //使用状态
            dataStore.setItem(row, "STATUS", getValueString("STATUS"));
            tableM.setDSValue();
            return "0";
        }
        else if ("1".equals(parm.getValue("SEQ_FLG", 0))) {
            // 序号管理
            // 关闭数量的锁定列
            tableM.setLockColumns("0,1,3,4,5,7,8,9,10,11");
            int row = tableM.addRow();
            TDataStore dataStore = tableM.getDataStore();
            //包号
            dataStore.setItem(row, "PACK_CODE", getValueString("PACK_CODE"));
            //数量(一次只能打一个包)
            dataStore.setItem(row, "QTY", 1);
            //序号
            dataStore.setItem(row, "PACK_SEQ_NO", getValueInt("PACK_SEQ_NO"));
            //固定成本
            dataStore.setItem(row, "USE_COST", getValueDouble("USE_COST"));
            Timestamp valueDate = TCM_Transform.getTimestamp(getValue(
                "DISINFECTION_DATE"));
            //消毒日期
            dataStore.setItem(row, "DISINFECTION_DATE", valueDate);
            if (valueDate == null)
                dataStore.setItem(row, "DISINFECTION_DATE", new TNull(Timestamp.class));
            //消毒效期
            valueDate = TCM_Transform.getTimestamp(getValue("VALUE_DATE"));
            dataStore.setItem(row, "VALUE_DATE", valueDate);
            if (valueDate == null)
                dataStore.setItem(row, "VALUE_DATE", new TNull(Timestamp.class));
            //消毒人员
            String valueStr = getValueString("DISINFECTION_USER");
            dataStore.setItem(row, "DISINFECTION_USER", valueStr);
            if (valueStr == null)
                dataStore.setItem(row, "DISINFECTION_USER", new TNull(String.class));
            //使用状态
            dataStore.setItem(row, "STATUS", getValueString("STATUS"));
            tableM.setDSValue();
            return "1";
        }
        return "1";
        //zhangyong20091110 end
    }

    /**
     * 处理物资明细数据
     * @return boolean
     */
    public boolean onGetInv() {
        String packCode = getValueString("PACK_CODE");
        //查找手术包明细物资
        TParm result = INVPackDTool.getInstance().getPackDetial(packCode);
        if (result == null || result.getErrCode() < 0)
            return false;
        //把物资明细带入明细表
        return onAddTableD(result);
    }

    /**
     * 给明细插入数据
     * @param parm TParm
     * @return boolean
     */
    public boolean onAddTableD(TParm parm) {
        //行数
        int rowCount = parm.getCount();
        for (int i = 0; i < rowCount; i++) {
            //拿出一个物资
            TParm oneParm = parm.getRow(i);
            //20140104 wangjingchun add start
            String sql = "SELECT INV_CHN_DESC FROM INV_BASE WHERE INV_CODE = '"+
            				oneParm.getValue("INV_CODE")+"' ";
            TParm invParm = new TParm(TJDODBTool.getInstance().select(sql));
            //20140104 wangjingchun add end
            //检核库存量
            if (!onChangeQty(oneParm.getValue("INV_CODE"),
            				 invParm.getValue("INV_CHN_DESC", 0),//20140104 wangjingchun add
                             oneParm.getDouble("QTY")))
                return false;
            //插入明细表
            if (!onAddTableDOneRow(oneParm))
                return false;
        }
        getFee();
        return true;
    }

    /**
     * 检核库存量是否够用
     * @param invCode String
     * @param qty double
     * @return boolean
     */
    public boolean onChangeQty(String invCode,String invCHNdesc,double qty) {
        //科室代码
        String orgCode = getValueString("ORG_CODE");
        //总库存量
        double stockQty = inv.getStockQty(orgCode, invCode);
        if (stockQty < 0 || qty > stockQty) {
//            messageBox("物资" + invCode + "库存量不足!");
            messageBox("物资" + invCHNdesc+"("+invCode+")" + "库存量不足!");
            enough = false;	//2014-01-12添加
            return false;
        }
        return true;
    }

    /**
     * 插入一行数据
     * @param parm TParm
     * @return boolean
     */
    public boolean onAddTableDOneRow(TParm parm) {
        //如果有序号管理则打开序号管理界面
        if (parm.getValue("SEQMAN_FLG").equals("Y")) {
            if (!onOpenDilog(parm)) {
                return false;
            }
            //记录是序号管理的物资
            if (map.get(parm.getValue("INV_CODE")) == null)
                map.put(parm.getValue("INV_CODE"), "Y");
            return true;
        }
        //没有序号管理的处理
        if (!chooseBatchSeq(parm))
            return false;
        return true;
    }

    /**
     * 给物资明细添加一条数据
     * @param parm TParm
     * @return int
     */
    public int onNewTableDDOneRow(TParm parm) {
        int row = tableD.addRow();
        //返回的数据
        setTableDValue(parm, row);
        return row;
    }

    /**
     * 给物资明细添加基本数据
     * @param parm TParm
     * @param row int
     */
    public void setTableDValue(TParm parm, int row) {
        TDataStore dataStore = tableD.getDataStore();
        String packCode = getValueString("PACK_CODE");
        String invCode = parm.getValue("INV_CODE");
        //包号
        dataStore.setItem(row, "PACK_CODE", packCode);
        //包序号(应该走自动生成)
        dataStore.setItem(row, "PACK_SEQ_NO", getValueInt("PACK_SEQ_NO"));
        //物资代码
        dataStore.setItem(row, "INV_CODE", invCode);

        dataStore.setItem(row, "INVSEQ_NO", parm.getInt("INVSEQ_NO"));

        //批次序号
        dataStore.setItem(row, "BATCH_SEQ", parm.getData("BATCH_SEQ"));
        //总量
        dataStore.setItem(row, "QTY", parm.getInt("STOCK_QTY"));
//        COST_PRICE
        double costPrice = parm.getDouble("UNIT_PRICE");
//        String discription=parm.getValue("DESCRIPTION");
        if (costPrice < 0) {
            TParm result = InvBaseTool.getInstance().getCostPrice(invCode);//wm2013-06-04	INVBaseTool.
            if (result.getErrCode() >= 0)
                costPrice = result.getDouble("COST_PRICE", 0);
        }
        dataStore.setItem(row, "COST_PRICE", costPrice);
        dataStore.setItem(row, "DESCRIPTION", parm.getValue("DESCRIPTION"));

        //单位
        String valueStr = parm.getValue("STOCK_UNIT");
//        String valueStr = parm.getValue("UNIT_CHN_DESC");   //wm2013-06-17
        dataStore.setItem(row, "STOCK_UNIT", valueStr);
        if (valueStr == null)
            dataStore.setItem(row, "STOCK_UNIT", new TNull(String.class));
        //一次性注记
        TParm result = INVPackDTool.getInstance().getPackType(packCode, invCode);
        String packtype = "";
        if (result != null && result.getErrCode() >= 0)
            packtype = result.getValue("PACK_TYPE",0);  
        String onceUseFlg = "Y";
//wm2013-06-17注释        
//        if (packtype.equals("1"))
//            onceUseFlg = "N";
//        else
//            onceUseFlg = "Y";
        if (packtype.equals("0"))
        	 onceUseFlg = "N";
        else
        	onceUseFlg = "Y";
        dataStore.setItem(row, "ONCE_USE_FLG", onceUseFlg);
        tableD.setDSValue();
    }

    /**
     * 选择批次序号
     * @param parm TParm
     * @return boolean
     */
    public boolean chooseBatchSeq(TParm parm) {
        //需要的物资数量
        double qty = parm.getDouble("QTY");
        //得到所有此物资的批次序号
        TParm result = getBatchSeqInv(parm);
        if (result == null || result.getErrCode() < 0)
            return false;
        //查出的物资批次个数
        int rowCount = result.getCount();
        //循环取出所有批次
        for (int i = 0; i < rowCount; i++) {
            //拿出一个
            TParm oneRow = result.getRow(i);
            double stockQty = oneRow.getDouble("STOCK_QTY");
            //如果物资足够(首先不能为0)
            if (stockQty > 0) {
                if (stockQty >= qty) {
                    oneRow.setData("STOCK_QTY", qty);
                    //调用插入一行的方法
                    onNewTableDDOneRow(oneRow);
                    //够了就走
                    return true;
                }
                //如果不足
                if (stockQty < qty) {
                    //存贮差值
                    qty = qty - stockQty;
                    //调用插入一行的方法
                    onNewTableDDOneRow(oneRow);  
                }
            }
        }
        return true;
    }

    /**
     * 得到无序号管理的物资
     * @param parm TParm
     * @return TParm
     */
    public TParm getBatchSeqInv(TParm parm) {
        //科室代码
        String orgCode = getValueString("ORG_CODE");
        //物资代码
        String invCode = parm.getValue("INV_CODE");
        //得到所有此物资的批次序号
        TParm result = inv.getAllStockQty(orgCode, invCode);
        if (result == null || result.getErrCode() < 0)
            return result;
        int count = result.getCount();
        if (count == 0) {
            //清空选择的部分数据
            clearNoEnoughInv();
            messageBox("物资不足!");
            this.callFunction("UI|new|setEnabled", false);
            this.callFunction("UI|save|setEnabled", false);
            return result;
        }
        return result;
    }

    /**
     * 打开序号管理选择界面
     * @param parm TParm
     * @return boolean
     */
    public boolean onOpenDilog(TParm parm) {
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        parm.setData("INVDISPENSE", this);
        int qty = (Integer)this.openDialog("%ROOT%\\config\\inv\\INVStockDD.x",
                                           parm);
        if (qty == parm.getInt("QTY")) {
            //记录物资已经选则完毕
            return true;
        }
        messageBox("没有选择足够的物资!");
        //清空选择的部分数据
        clearNoEnoughInv();
        return false;
    }

    /**
     * 计算一次性物资的费用
     * @return boolean
     */
    public boolean getFee() {
        double fee = 0.0;
        int rowCount = tableD.getRowCount();
        String invCode = "";
        for (int i = 0; i < rowCount; i++) {
        	
        		invCode = tableD.getItemString(i, "INV_CODE");
                if (map.get(invCode) == null) {
                    double invPrice = getInvPrice(invCode);
                    if (invPrice < 0)
                        return false;
                    //给物资写上单价
                    tableD.setItem(i, "COST_PRICE", invPrice);
                    //总量
                    int qty = tableD.getItemInt(i, "QTY");
                    if(tableD.getItemString(i, "ONCE_USE_FLG").equals("Y")){
                    	fee = fee + invPrice * qty;
                    }
                }
        }
        //一次性材料总费用
        this.setValue("ONCE_USE_COST", fee);
        //给主表加上
        tableM.setItem(0, "ONCE_USE_COST", fee);
        return true;
    }

    /**
     * 得到物资价格
     * @param invCode String
     * @return double
     */
    public double getInvPrice(String invCode) {
        TParm result = InvBaseTool.getInstance().getCostPrice(invCode);//wm2013-06-04	INVBaseTool.
        if (result.getErrCode() < 0)
            return -1;
        return result.getDouble("COST_PRICE", 0);
    }

    /**
     * 清空
     */
    public void onClear() {
    	
    	if(enough){		//2014-01-12添加
    		//检核数据变更
            if (!checkValueChange())
                return;
    	}
    	
        //请空属性
        onClearText();
        //清空部分属性
        clearNoEnoughInv();
        //可编辑状态
        clearEnable(true);
        //其它不可编辑
        setOtherEnable(true);
        //初始化界面上的数据
        initValue();
        this.callFunction("UI|new|setEnabled", true);
        this.callFunction("UI|save|setEnabled", false);
        //zhangyong20091110
        // 关闭数量的锁定列
        tableM.setLockColumns("0,1,3,4,5,7,8,9,10,11");
//         setDate();
        enough = true;	//2014-01-12添加
    }

    /**
     * 清空没有选择足够的物资
     */
    public void clearNoEnoughInv() {
        //清空主表
        onClearTable(tableM);
        //清空细表
        onClearTable(tableD);
        //清空新增状态
        isNew = false;
        this.callFunction("UI|new|setEnabled", true);
        this.callFunction("UI|save|setEnabled", false);
    }

    /**
     * 改变编辑状态
     * @param statuse boolean
     */
    public void clearEnable(boolean statuse) {
        //科室
        this.callFunction("UI|ORG_CODE|setEnabled", statuse);
        //包号
        this.callFunction("UI|PACK_CODE|setEnabled", statuse);
        //序号
        this.callFunction("UI|PACK_SEQ_NO|setEnabled", statuse);
    }

    /**
     * 清空界面
     */
    public void onClearText() {
        //清空过滤条件
        clearValue("PACK_CODE;PACK_DESC;PACK_SEQ_NO;QTY;USE_COST;ONCE_USE_COST;" +
                   "DISINFECTION_DATE;VALUE_DATE;DISINFECTION_USER");
    }

    /**
     * 清空table
     * @param table TTable
     */
    public void onClearTable(TTable table) {
        //接收最后此更改
        table.acceptText();
        //清空选中行
        table.clearSelection();
        //删除所有行
        table.removeRowAll();
        //清空修改记录
        table.resetModify();
    }

    /**
     * 保存
     * @return boolean
     */
    public boolean onUpdate() {
        if (isNew) {
            messageBox("请重新选择物资!");
            return false;
        }

        //zhnngyong20091110 begin
        //检查库存量
        String inv_code = "";
        String inv_chn_desc = "";//20140104 wangjingchun add
        double qty = 0;
        for (int i = 0; i < tableD.getDataStore().rowCount(); i++) {
            inv_code = tableD.getDataStore().getItemString(i, "INV_CODE");
            inv_chn_desc = tableD.getDataStore().getItemString(i, "INV_CHN_DESC");//20140104 wangjingchun add
            qty = tableD.getDataStore().getItemDouble(i, "QTY");
            if (!onChangeQty(inv_code,inv_chn_desc, qty)) {//20140104 wangjingchun modify
                return false;
            }
        }
        //zhangyong20091110 end

        String seqFlg = returnParm.getValue("SEQ_FLG");
        if(seqFlg.equals("1")){//有序管手术包
        	//barcode = returnParm.getValue("PACK_CODE") + returnParm.getValue("PACK_SEQ_NO")+"(次数000)(累加)"; 
        	//101010 + 003 + 次数     扫描取条码取到101010 + 003  次数用于单独统计
        	//140424   000   001   回收一次 usenumble++
        	//获得该手术包条码 
        	//101010   000005     
        	String packSeqNo =  returnParm.getValue("PACK_SEQ_NO");
        	packSeqNo =StringTool.fill("0", 3 - packSeqNo.length())+packSeqNo;
        	barcode = returnParm.getValue("PACK_CODE")+packSeqNo+"000";       
        	//101010000006 
        	//101010006001
        	//fux modify 20140425
            //barcode = this.getPackageBarcode();      
            //每次  回收一次   加一次  次数(更新条码的次数选项 但扫描项(条码)不变)
        }else if(seqFlg.equals("0")){//无序管诊疗包  
        	barcode = returnParm.getValue("PACK_CODE") + "000000";
        }
          
        TParm saveParm = getSaveParm();
        if (saveParm == null)
            return false;  
        //调用保存事务
        TParm result = TIOM_AppServer.executeAction("action.inv.INVAction",
            "onSavePackAge", saveParm);
        if (result.getErrCode() < 0) {  
            err(result.getErrCode() + " " + result.getErrText());
            //保存失败
            messageBox("保存失败!");
            return false;
        }
        //保存成功   
        messageBox("保存成功!");     
        //器械包打印单据                      
        if(seqFlg.equals("1")){ 
//        //打印入库单  
//        onPrint();          
//        //再一次打印入库单   
//        onPrint(); 
        onBarcode();      
        }
        //保存后的数据清空
        resertSave();
        this.onClear();
        onInitTable();    //2013-12-10添加
        return true;
    }

    /**
     * 处理保存后的数据清理
     */
    public void resertSave() {
        //清空主表保存记录
        tableM.resetModify();
        tableM.setDSValue();
        //清空细表保存记录
        tableD.resetModify();   
        tableD.setDSValue();
        //清空主表选择行记录
        //保存一次则需要清空重新打下一个手术包
        isNew = true;
        this.callFunction("UI|new|setEnabled", false);
        this.callFunction("UI|save|setEnabled", false);
    }


    /**
     * 获得保存parm
     * @return TParm
     */
    public TParm getSaveParm() {
    	int batchNo = 0;  //批次
        //要保存的数据
        TParm saveParm = new TParm();
        //取当前时间
        Timestamp date = SystemTool.getInstance().getDate();

        //调用处理手术包库存主档处理方法
        TParm packMParm = dealPackMSaveParm(date);
        if (packMParm == null)
            return null;
        packMParm.setData("BARCODE", barcode);	//添加barcode条码
        if(!barcode.substring(6).equals("000000")){//类型为手术包
        	batchNo = 0;
        	packMParm.setData("PACK_BATCH_NO", batchNo);
        }else{//类型为诊疗包
        	TParm p = new TParm();
        	p.setData("PACK_CODE", 0, packMParm.getData("PACK_CODE"));
        	
        	TParm tp = InvPackStockMTool.getInstance().queryPackBatch(p.getRow(0));
//        	System.out.println("tp:"+tp);
        	if (tp.getErrCode() < 0)
                err(tp.getErrCode() + " " + tp.getErrText());
//        	System.out.println("PACK_BATCH_NO:"+tp.getData("MAXNO", 0));
        	if(tp.getData("MAXNO", 0) == null){ 
        		batchNo = 1;
        		packMParm.setData("PACK_BATCH_NO", batchNo);
        	}else{
        		batchNo = Integer.parseInt(tp.getData("MAXNO", 0).toString())+1;
        		packMParm.setData("PACK_BATCH_NO", batchNo);
        	}
        }
        //添加保存parm
        saveParm.setData("PACKMSAVEPARM", packMParm.getData());
        //手术包明细
        TParm packDSaveParm = getPackDSaveParm(date);
        if (packDSaveParm == null)
            return null;
        //添加barcode条码，已用数量，未使用数量，批次
        for(int i=0;i<packDSaveParm.getCount();i++){
        	packDSaveParm.setData("BARCODE", i, barcode);
        	packDSaveParm.setData("USED_QTY", i, 0);
        	packDSaveParm.setData("NOTUSED_QTY", i, 0);
        	packDSaveParm.setData("PACK_BATCH_NO", i, batchNo);
        }
        //手术包保存明细
        saveParm.setData("PACKDSAVEPARM", packDSaveParm.getData());
        //得到要保存的parm
        TParm stockParm = getStockSaveParm();
        if (stockParm == null)
            return null;
        //添加保存parm
        saveParm.setData("SAVEPARM", stockParm.getData());

        return saveParm;
    }

    /**
     * 得到消毒手术包
     * @param date Timestamp
     * @return TParm
     */
    public TParm getDisinfectionParm(Timestamp date) {
        TParm disinfectionParm = new TParm();
        disinfectionParm.setCount(0);
        return disinfectionParm;
    }

    /**
     * 处理消毒保存的数据
     * @param date Timestamp
     * @return TParm
     */
    public TParm dealdisinfectionParm(Timestamp date) {
        //取系统事件
        TParm dispenfectionParm = new TParm();
        //状态 0 在库，1 出库, 2 已回收 3 已消毒 4:维修中
        //状态更改为已经消毒
        tableM.setItem(0, "STATUS", "3");
        //消毒记录
        dispenfectionParm = tableM.getDataStore().getRowParm(0);
        //消毒时间
        dispenfectionParm.setData("DISINFECTION_DATE",
                                  TCM_Transform.getString(date));
        //设置效期 VALID_DATE
        Timestamp validDate = dispenfectionParm.getTimestamp("VALID_DATE");
        if (validDate == null)
            dispenfectionParm.setData("VALUE_DATE",
                                      new TNull(Timestamp.class));
        dispenfectionParm.setData("DISINFECTION_USER",
                                  getValueString("DISINFECTION_USER"));
        //数量
        dispenfectionParm.setData("QTY", 1);
        //操作人
        dispenfectionParm.setData("OPT_USER", Operator.getID());
        //时间
        dispenfectionParm.setData("OPT_DATE", date);
        //操作IP
        dispenfectionParm.setData("OPT_TERM", Operator.getIP());
        dispenfectionParm.setCount(1);
        return dispenfectionParm;
    }

    /**
     * 得到手术包明细保存parm
     * @param date Timestamp
     * @return TParm
     */
    public TParm getPackDSaveParm(Timestamp date) {
        //接收最后一次修改
        tableD.acceptText();
        //取出数据
        TDataStore dataStore = tableD.getDataStore();
        //总行数
        int rowCount = tableD.getRowCount();
        //给固定数据配数据
        for (int i = 0; i < rowCount; i++) {
            //备注也不能为空
            String eescription = dataStore.getItemString(i, "DESCRIPTION");
            if (eescription == null || eescription.length() == 0)
                dataStore.setItem(i, "DESCRIPTION", new TNull(String.class));
            //操作员
            dataStore.setItem(i, "OPT_USER",
                              Operator.getID());
            //操作日期
            dataStore.setItem(i, "OPT_DATE", date);
            //操作地址
            dataStore.setItem(i, "OPT_TERM",
                              Operator.getIP());
            dataStore.setItem(i, "ORG_CODE", getValueString("ORG_CODE"));  //wm2013-06-06添加
        }
        //取出全部数据
        return dataStore.getBuffer(dataStore.PRIMARY);

    }

    /**
     * 处理手术包库存主档保存数据
     * @param date Timestamp
     * @return TParm
     */
    public TParm dealPackMSaveParm(Timestamp date) {
        //如果是没有序号管理的手术包
        if (!onCheckSeqFlg()) {
            return getNoseqManParm(date);
        }
        else //有序号管理的手术包
            return getHaveSeqManParm(date);
    }

    /**
     * 处理无序号管理的手术包库存主档保存数据
     * @param date Timestamp
     * @return TParm
     */
    public TParm getNoseqManParm(Timestamp date) {
        //拿到无序号管理的手术包保存数据
        TParm packMSaveParm = getPackMSaveParm(date);
        if (packMSaveParm == null)
            return null;
        //添加保存方式为更新
        packMSaveParm.setData("SAVETYPE", "U");
        return packMSaveParm;
    }

    /**
     * 处理有序号管理的手术包库存主档保存数据
     * @param date Timestamp
     * @return TParm
     */
    public TParm getHaveSeqManParm(Timestamp date) {
        //拿到序号管理的手术包保存数据
        TParm packMSaveParm = getPackMSaveParm(date);
        if (packMSaveParm == null)
            return null;
        //添加保存方式为更新
        packMSaveParm.setData("SAVETYPE", "I");
        return packMSaveParm;
    }

    /**
     * 处理手术包库存主档的最终parm
     * @param date Timestamp
     * @return TParm
     */
    public TParm getPackMSaveParm(Timestamp date) {
        //接收最后一次修改
        tableM.acceptText();
        TDataStore dateStore = tableM.getDataStore();
        int rowCount = tableM.getRowCount();
        //给固定数据配数据
        for (int i = 0; i < rowCount; i++) {
            dateStore.setItem(i, "OPT_USER",
                              Operator.getID());
            dateStore.setItem(i, "OPT_DATE", date);
            dateStore.setItem(i, "OPT_TERM",
                              Operator.getIP());
            dateStore.setItem(i,"ORG_CODE",getValueString("ORG_CODE"));     //wm2013-06-06    添加
        }
        //返回全数据
        return dateStore.getBuffer(dateStore.PRIMARY).getRow(0);
    }

    /**
     * 处理parm
     * @return TParm
     */
    public TParm getStockSaveParm() {
        //打包科室不能为空
        String orgCode = getValueString("ORG_CODE");
        //检核
        if (orgCode == null || orgCode.length() == 0) {
            messageBox("请选择打包科室!");
            return null;
        }
        //库存数据
        TParm stockParm = new TParm();
        //明细表的数据
        TDataStore dataStore = tableD.getDataStore();
        //数据行数
        int rowCount = tableD.getRowCount();
        //所有的物资
        TParm tableDParm = dataStore.getBuffer(dataStore.PRIMARY);
        TParm stockDDParm = new TParm();
        //处理每行数据
        for (int i = 0; i < rowCount; i++) {
            //得到一行数据
            TParm oneRow = dataStore.getRowParm(i);
            //处理序号管理
            oneRow = setStockDDParm(oneRow);
            if (oneRow != null) {
                int row = stockDDParm.insertRow();
                oneRow.setData("PACK_FLG", "Y");
                stockDDParm.setRowData(row, oneRow);
                stockDDParm.setCount(row + 1);
            }
        }
        stockParm.setData("STOCKM",
                          dealSaveStockMParm(tableDParm, orgCode).getData());
        stockParm.setData("STOCKD",
                          dealStockDSaveParm(tableDParm, orgCode).getData());
        stockParm.setData("STOCKDD", stockDDParm.getData());

        return stockParm;
    }

    /**
     * 处理序号管理数据
     * @param parm TParm
     * @return TParm
     */
    public TParm setStockDDParm(TParm parm) {
        //物资代码
        String invCode = parm.getValue("INV_CODE");
        if (map.get(invCode) != null)
            return dealStockDDParm(parm);
        return null;
    }

    /**
     * 处理序号管理
     * @param parm TParm
     * @return TParm
     */
    public TParm dealStockDDParm(TParm parm) {
        TParm stockDDParm = new TParm();
        //物资代码
        stockDDParm.setData("INV_CODE", parm.getData("INV_CODE"));
        //序号
        stockDDParm.setData("INVSEQ_NO", parm.getData("INVSEQ_NO"));
        //手术包号
        stockDDParm.setData("PACK_CODE", parm.getData("PACK_CODE"));
        //手术包序号
        stockDDParm.setData("PACK_SEQ_NO", parm.getData("PACK_SEQ_NO"));
        //入包标记
        stockDDParm.setData("PACK_FLG", "Y");
        return stockDDParm;
    }

    /**
     * 得到保存库存主档的parm
     * @param parm TParm
     * @param orgCode String
     * @return TParm
     */
    public TParm dealSaveStockMParm(TParm parm, String orgCode) {
        //存储物资类别
        Map stockMMap = new HashMap();
        TParm returnParm = new TParm();
        //总个数
        int rowCount = parm.getCount();
        String invCode;
        TParm oneParm;
        //循环
        for (int i = 0; i < rowCount; i++) {
            oneParm = parm.getRow(i);
            invCode = oneParm.getValue("INV_CODE");
            //两个主键
            String str = invCode;
            //如果没有此类物资则一条数据
            if (stockMMap.get(str) == null) {
                int row = returnParm.insertRow();
                stockMMap.put(str, row);
                oneParm.setData("ORG_CODE", orgCode);
                oneParm.setData("STOCK_QTY", oneParm.getInt("QTY"));
                returnParm.setRowData(row, oneParm);
                returnParm.setCount(row + 1);
            }
            else { //已经存在就改变总量
                int row = (Integer) stockMMap.get(str);
                returnParm.setData("STOCK_QTY", row,
                                   (returnParm.getInt("STOCK_QTY", row) +
                                    oneParm.getInt("QTY")));
            }
        }
        return returnParm;
    }

    /**
     * 处理入库库存明细当的数据
     * @param parm TParm
     * @param orgCode String
     * @return TParm
     */
    public TParm dealStockDSaveParm(TParm parm, String orgCode) {
        //存储物资类别
        Map stockMMap = new HashMap();
        TParm returnParm = new TParm();
        //总个数
        int rowCount = parm.getCount();
        String invCode;
        int batchSeq;
        TParm oneParm;
        //循环
        for (int i = 0; i < rowCount; i++) {
            oneParm = parm.getRow(i);
            invCode = oneParm.getValue("INV_CODE");
            batchSeq = oneParm.getInt("BATCH_SEQ");
            //两个主键
            String str = invCode + "|" + batchSeq;
            //如果没有此类物资则一条数据
            if (stockMMap.get(str) == null) {
                int row = returnParm.insertRow();
                stockMMap.put(str, row);         
                oneParm.setData("ORG_CODE", orgCode);
                oneParm.setData("STOCK_QTY", oneParm.getInt("QTY"));
                returnParm.setRowData(row, oneParm);
                returnParm.setCount(row + 1);
            }
            else { //已经存在就改变总量
                int row = (Integer) stockMMap.get(str);
                returnParm.setData("STOCK_QTY", row,
                                   (returnParm.getInt("STOCK_QTY", row) +
                                    oneParm.getInt("QTY")));
            }
        }
        return returnParm;

    }
    
    /** 
	 * 生成条码单号 
	 *  */
	private String getPackageBarcode() {
		String barcode = SystemTool.getInstance().getNo("ALL", "INV",
				"INV_PACKSTOCKM", "No");
		return barcode;
	}

    /**
     * 检核数据变更提示信息（变更主细表时提示数据的变更）
     * @return boolean
     */
    public boolean checkValueChange() {
        // 如果有数据变更
        if (checkData())
            switch (this.messageBox("提示信息",
                                    "数据变更是否保存", this.YES_NO_CANCEL_OPTION)) {
                //保存
                case 0:
                    if (!onUpdate())
                        return false;
                    return true;
                    //不保存
                case 1:
                    return true;
                    //撤销
                case 2:
                    return false;
            }
        return true;
    }

    /**
     * 检核数据变更
     * @return boolean
     */
    public boolean checkData() {
        //主表
        if (tableM.isModified())
            return true;
        //细表
        if (tableD.isModified())
            return true;
        return false;
    }

    /**
     * 是否关闭窗口(当有数据变更时提示是否保存)
     * @return boolean true 关闭 false 不关闭
     */
    public boolean onClosing() {
        // 如果有数据变更
        if (checkData())
            switch (this.messageBox("提示信息",
                                    "是否保存", this.YES_NO_CANCEL_OPTION)) {
                //保存
                case 0:
                    if (!onUpdate())
                        return false;
                    break;
                    //不保存
                case 1:
                    return true;
                    //撤销
                case 2:
                    return false;
            }
        //没有变更的数据
        return true;
    }

    /**
     * 打印入库单
     */
    public void onPrintInfo() {
        TParm parm = new TParm();
        int row = tableM.getSelectedRow();
        if (row < 0)
            row = 0;
        String packCode = tableM.getItemString(row, "PACK_CODE");
        if (packCode == null || packCode.length() == 0) {
            messageBox("手术包选择错误!");
            return;
        }
        //手术包序号:
        int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
        String packCodeSeq = "" + packSeqNo;
        for (int i = packCodeSeq.length(); i < 4; i++) {
            packCodeSeq = "0" + packCodeSeq;
        }
        packCodeSeq = "" + packCodeSeq;
        
        int packBatchNo = tableM.getItemInt(row, "PACK_BATCH_NO");
        
        parm.setData("PACKCODESEQ", packCode + packCodeSeq);
        //手术包号
        parm.setData("PACK_CODE", packCode);
        //包名
        parm.setData("PACK_DESC", getValueString("PACK_DESC"));
        //手术包序号
        parm.setData("PACK_SEQ_NO", packSeqNo);
        TParm packParm = INVPackMTool.getInstance().getPackDesc(packCode);
        String packDesc = packParm.getValue("PACK_DESC", 0);

        //消毒日期
        TParm result = InvPackStockMTool.getInstance().getPackDateBatch(packCode,
            packSeqNo,packBatchNo);		 
        String disinfectionDate = StringTool.getString( (Timestamp) result.
            getData(
                "DISINFECTION_DATE", 0), "yyyy/MM/dd");
        String valueDate = StringTool.getString( (Timestamp) result.getData(
            "VALUE_DATE", 0), "yyyy/MM/dd");
        String optUser = result.getValue("OPT_USER", 0);
        String disinfectionUser = result.getValue("DISINFECTION_USER", 0);

        parm.setData("PACK_DESC", packDesc);
        parm.setData("DISINFECTION_DATE", disinfectionDate);
        parm.setData("VALUE_DATE", valueDate);
        parm.setData("OPT_USER", getUserName(optUser));
        parm.setData("DISINFECTION_USER", getUserName(disinfectionUser));

        parm.setData("OPT_DATE",
                     StringTool.getString( (Timestamp) tableM.
                                          getItemData(row, "OPT_DATE"),
                                          "yyyy/MM/dd"));
        //手术包序号
        parm.setData("T1", "PACK_SEQ_NO", packSeqNo);
        //手术包号
        parm.setData("T1", "PACK_CODE", packCode);
        //手术包序号
        parm.setData("USE_COST", tableM.getItemDouble(row, "USE_COST"));
        //手术包序号
        parm.setData("ONCE_USE_COST",
                     tableM.getItemDouble(row, "ONCE_USE_COST"));
        TComboBox value;
        //出库科室
        value = (TComboBox)this.getComponent("ORG_CODE");
        parm.setData("ORG_CODE", value.getSelectedName());
        parm.setData("HOSP_AREA", getHospArea());
        //调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVPackPackage.jhw",
                             parm);
    }

    
    
    /**
     * 打印信息单
     */
    public void onPrint() {
    	TParm parm = new TParm();
    	int row = tableM.getSelectedRow();                   
    	if (row < 0)
    		row = 0;
    	String packCode = tableM.getItemString(row, "PACK_CODE");
    	if (packCode == null || packCode.length() == 0) {
    		messageBox("手术包选择错误!");
    		return;
    	}
    	//手术包序号:
    	int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
    	//手术包号
    	parm.setData("PACK_CODE", packCode);
    	//手术包序号
    	parm.setData("PACK_SEQ_NO", packSeqNo);
    	
    	TParm packList = new TParm();
		packList = INVNewRepackTool.getInstance().queryPackList(parm);
		
		TParm packInfo = new TParm();  
    	packInfo=INVNewRepackTool.getInstance().queryPackageInfo(parm);
    	  
    	TParm reportTParm = new TParm();
		reportTParm.setData("PACK_DESC", "TEXT", packInfo.getValue("PACK_DESC", 0));	//包名  
		 
		//queryPackageInfo   -------   packInfo.getValue("SEQ_FLG", 0).equals("0");   packInfo.getValue("BARCODE", 0)       
		//                             packInfo.getValue("SEQ_FLG", 0).equals("1");  packInfo.getValue("BARCODE", 0).substring(0, 9)   
		reportTParm.setData("PACK_BARCODE", "TEXT", packInfo.getValue("BARCODE", 0));	//条码    
		reportTParm.setData("PACK_USER", "TEXT", packInfo.getValue("USER_NAME", 0));	//打包人员
		reportTParm.setData("PACK_DATE", "TEXT", packInfo.getValue("OPT_DATE", 0).toString().substring(0, 10).replaceAll("-", "/"));	//打包日期
		     
		int allCount = packList.getCount("QTY");
		//报表每页显示80条数据，分两列显示，每列40条
		int remainder = allCount%80;//余数
		int addCount = 0;
		if(remainder!=0){
			addCount = 80 - remainder;
			for (int i = 0; i < addCount; i++){
				packList.addData("QTY", "");   
				packList.addData("INV_CHN_DESC", "");
			}
		}
		DecimalFormat df1 = new DecimalFormat("0");
		//表格数据
        TParm tableParm = new TParm();
        for(int i=0; i<packList.getCount("QTY")/2;i++ ){  
        	tableParm.addData("SEQ_F", i+1);
        	tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", i) ); 
        	     
        	if(null == packList.getData("QTY", i)||"".equals(packList.getData("QTY", i))){
        		tableParm.addData("QTY_F", "");
        	}else{
        		tableParm.addData("QTY_F", df1.format(packList.getDouble("QTY", i)));
        	}
        	
        	tableParm.addData("SEQ_S", (i+1)+40);
        	tableParm.addData("CHN_DESC_S", packList.getData("INV_CHN_DESC", i+40));
        	
        	if(null == packList.getData("QTY", i+40)||"".equals(packList.getData("QTY", i+40))){
        		tableParm.addData("QTY_S", "");
        	}else{
        		tableParm.addData("QTY_S", df1.format(packList.getDouble("QTY", i+40)));
        	}
        	
        }

		
		tableParm.setCount(tableParm.getCount("SEQ_F"));
		
	    tableParm.addData("SYSTEM", "COLUMNS", "SEQ_F");
	    tableParm.addData("SYSTEM", "COLUMNS", "CHN_DESC_F");
	    tableParm.addData("SYSTEM", "COLUMNS", "QTY_F");
	    tableParm.addData("SYSTEM", "COLUMNS", "SEQ_S");
	    tableParm.addData("SYSTEM", "COLUMNS", "CHN_DESC_S");
	    tableParm.addData("SYSTEM", "COLUMNS", "QTY_S");
 
	    reportTParm.setData("TABLE", tableParm.getData());
		//不预览
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVNewPackageList.jhw",
				reportTParm,true);     
    	       
    } 
 
    
//2013-12-29注释的打印方法    
//    /**
//     * 打印信息单
//     */
//    public void onPrint() {
//    	TParm parm = new TParm();
//    	int row = tableM.getSelectedRow();
//    	if (row < 0)
//    		row = 0;
//    	String packCode = tableM.getItemString(row, "PACK_CODE");
//    	if (packCode == null || packCode.length() == 0) {
//    		messageBox("手术包选择错误!");
//    		return;
//    	}
//    	//手术包序号:
//    	int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
//    	//手术包号
//    	parm.setData("PACK_CODE", packCode);
//    	//手术包序号
//    	parm.setData("PACK_SEQ_NO", packSeqNo);
//    	
//    	TParm packList = new TParm();
//		packList = INVNewRepackTool.getInstance().queryPackList(parm);
//		
//		TParm packInfo = new TParm();
//    	packInfo=INVNewRepackTool.getInstance().queryPackageInfo(parm);
//    	
//    	TParm reportTParm = new TParm();
//		reportTParm.setData("PACK_DESC", "TEXT", packInfo.getValue("PACK_DESC", 0));	//包名
//		reportTParm.setData("PACK_BARCODE", "TEXT", packInfo.getValue("BARCODE", 0));	//条码
//		reportTParm.setData("PACK_USER", "TEXT", packInfo.getValue("USER_NAME", 0));	//打包人员
//		reportTParm.setData("PACK_DATE", "TEXT", packInfo.getValue("OPT_DATE", 0).toString().substring(0, 10));	//打包日期
//		
//		int tag=packList.getCount("QTY")%2;
//		//表格数据
//        TParm tableParm = new TParm();
//		if(packList.getCount("QTY") == 1){
//			tableParm.addData("SEQ_F", 1);
//			tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", 0));
//			tableParm.addData("QTY_F", packList.getData("QTY", 0));
//			tableParm.addData("SEQ_S", "");
//			tableParm.addData("CHN_DESC_S", "");
//			tableParm.addData("QTY_S", "");
//		}else if(packList.getCount("QTY") > 1){
//			
//			if(tag == 0){
//				for(int i=0;i<packList.getCount("QTY");){
//					tableParm.addData("SEQ_F", i+1);
//					tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", i));
//					tableParm.addData("QTY_F", packList.getData("QTY", i));
//					tableParm.addData("SEQ_S", i+2);
//					tableParm.addData("CHN_DESC_S", packList.getData("INV_CHN_DESC", i+1));
//					tableParm.addData("QTY_S", packList.getData("QTY", i+1));
//					i=i+2;
//				}
//			}
//			if(tag == 1){
//				for(int i=0;i<packList.getCount("QTY")-1;){
//					tableParm.addData("SEQ_F", i+1);
//					tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", i));
//					tableParm.addData("QTY_F", packList.getData("QTY", i));
//					tableParm.addData("SEQ_S", i+2);
//					tableParm.addData("CHN_DESC_S", packList.getData("INV_CHN_DESC", i+1));
//					tableParm.addData("QTY_S", packList.getData("QTY", i+1));
//					i=i+2;
//				}
//				tableParm.addData("SEQ_F", packList.getCount("QTY"));
//				tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", packList.getCount("QTY")-1));
//				tableParm.addData("QTY_F", packList.getData("QTY", packList.getCount("QTY")-1));
//				tableParm.addData("SEQ_S", "");
//				tableParm.addData("CHN_DESC_S", "");
//				tableParm.addData("QTY_S", "");
//			}
//		}
//		
//		
//		
//		tableParm.setCount(tableParm.getCount("SEQ_F"));
//		
//	    tableParm.addData("SYSTEM", "COLUMNS", "SEQ_F");
//	    tableParm.addData("SYSTEM", "COLUMNS", "CHN_DESC_F");
//	    tableParm.addData("SYSTEM", "COLUMNS", "QTY_F");
//	    tableParm.addData("SYSTEM", "COLUMNS", "SEQ_S");
//	    tableParm.addData("SYSTEM", "COLUMNS", "CHN_DESC_S");
//	    tableParm.addData("SYSTEM", "COLUMNS", "QTY_S");
//
//	    reportTParm.setData("TABLE", tableParm.getData());
//		
//		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVNewPackageList.jhw",
//				reportTParm);
//    	
//    }
    
//2013-8-1注释   最老版的打印方法    
//    /**
//     * 打印信息单
//     */
//    public void onPrint() {
//        TParm parm = new TParm();
//        int row = tableM.getSelectedRow();
//        if (row < 0)
//            row = 0;
//        String packCode = tableM.getItemString(row, "PACK_CODE");
//        if (packCode == null || packCode.length() == 0) {
//            messageBox("手术包选择错误!");
//            return;
//        }
//        //手术包序号:
//        int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
//        String packCodeSeq = "" + packSeqNo;
//        for (int i = packCodeSeq.length(); i < 4; i++) {
//            packCodeSeq = "0" + packCodeSeq;
//        }
//        packCodeSeq = "" + packCodeSeq;
//        parm.setData("PACKCODESEQ", packCode + packCodeSeq);
//        //手术包号
//        parm.setData("PACK_CODE", packCode);
//        //包名
//        parm.setData("PACK_DESC", getValueString("PACK_DESC"));
//        //手术包序号
//        parm.setData("PACK_SEQ_NO", packSeqNo);
//
//
//        //消毒日期
//        TParm result = InvPackStockMTool.getInstance().getPackDate(packCode,
//            packSeqNo);   //wm2013-06-04   INVPackStockMTool.
//        String disinfectionDate = StringTool.getString( (Timestamp) result.
//            getData(
//                "DISINFECTION_DATE", 0), "yyyy/MM/dd");
//
//        String optUser = result.getValue("OPT_USER", 0);
//        String disinfectionUser = result.getValue("DISINFECTION_USER", 0);
//
//        //parm.setData("PACK_DESC", packDesc);
//        parm.setData("DISINFECTION_DATE", disinfectionDate);
//        parm.setData("VALUE_DATE", "");  //2013-07-17
//        parm.setData("OPT_USER", getUserName(optUser));
//        parm.setData("DISINFECTION_USER", getUserName(disinfectionUser));
//
//        parm.setData("OPT_DATE",
//                     StringTool.getString( (Timestamp) tableM.
//                     getItemData(row, "OPT_DATE"),"yyyy/MM/dd"));
//        //手术包序号
//        parm.setData("T1", "PACK_SEQ_NO", packSeqNo);
//        //手术包号
//        parm.setData("T1", "PACK_CODE", packCode);
//        //手术包序号
//        parm.setData("USE_COST", tableM.getItemDouble(row, "USE_COST"));
//        //手术包序号
//        parm.setData("ONCE_USE_COST",
//                     tableM.getItemDouble(row, "ONCE_USE_COST"));
//
//        String org_code = ((TTextFormat)this.getComponent("ORG_CODE")).getText();  //wm2013-06-17
//        parm.setData("ORG_CODE", org_code);			//wm2013-06-17
//        parm.setData("HOSP_AREA", getHospArea());
//        //表格数据
//        TParm tableParm = new TParm();
//        String sql = "";
//        double sum_amt = 0;
//        for (int i = 0; i < tableD.getRowCount(); i++) {
//
//            sql = INVSQL.getInvInfo(tableD.getDataStore().getItemString(i,
//            "INV_CODE"));
//            TParm inv_base = new TParm(TJDODBTool.getInstance().select(sql));
//            tableParm.addData("INV_DESC", inv_base.getValue("INV_CHN_DESC", 0));
//            tableParm.addData("INVSEQ_NO",
//                              tableD.getDataStore().getItemInt(i, "INVSEQ_NO"));
//            tableParm.addData("PRICE",
//                              tableD.getDataStore().getItemDouble(i,
//                "COST_PRICE"));
//            tableParm.addData("QTY",
//                              tableD.getDataStore().getItemDouble(i, "QTY"));
//            tableParm.addData("UNIT", inv_base.getValue("UNIT_CHN_DESC", 0));
//            tableParm.addData("AMT", StringTool.round(tableD.getDataStore().
//                getItemDouble(i, "COST_PRICE") *
//                tableD.getDataStore().getItemDouble(i, "QTY"), 2));
//            sum_amt += StringTool.round(tableD.getDataStore().
//                                        getItemDouble(i, "COST_PRICE") *
//                                        tableD.getDataStore().getItemDouble(i,
//                "QTY"), 2);
//        }
//        tableParm.addData("INV_DESC", "合计:");
//        tableParm.addData("INVSEQ_NO","");
//        tableParm.addData("PRICE","");
//        tableParm.addData("QTY","");
//        tableParm.addData("UNIT", "");
//        tableParm.addData("AMT", sum_amt);
//
//        tableParm.setCount(tableParm.getCount("INV_DESC"));
//        tableParm.addData("SYSTEM", "COLUMNS", "INV_DESC");
//        tableParm.addData("SYSTEM", "COLUMNS", "INVSEQ_NO");
//        tableParm.addData("SYSTEM", "COLUMNS", "PRICE");
//        tableParm.addData("SYSTEM", "COLUMNS", "QTY");
//        tableParm.addData("SYSTEM", "COLUMNS", "UNIT");
//        tableParm.addData("SYSTEM", "COLUMNS", "AMT");
//
//        parm.setData("TABLE", tableParm.getData());
//
//        //调用打印方法
//        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVPackPackage2.jhw",
//                             parm);
//    }


    /**
     * 得到医院简称
     * @return String
     */
    public String getHospArea() {
        String sql = INVSQL.getHospArea();
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        String hospChnAbn = result.getValue("HOSP_CHN_ABN", 0);
        return hospChnAbn;
    }

    /**
     * 得到用户姓名
     * @param userId String
     * @return String
     */
    public String getUserName(String userId) {
        String sql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='" +
            userId + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0)
            return userId;
        return result.getValue("USER_NAME", 0);

    }

    /**
     * 打印条码
     */
    public void onBarcode() {
        int row = tableM.getSelectedRow();
        if (row < 0)
            row = 0;
        String packCode = tableM.getItemString(row, "PACK_CODE");
        if (packCode == null || packCode.length() == 0) {
            messageBox("手术包选择错误!");
            return;
        }
        //手术包序号 
        int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
        if(packSeqNo == 0){//诊疗包条码打印               
        	Object obj = this.openDialog("%ROOT%\\config\\inv\\INVPrintCount.x");
        	if (obj == null)
        		return;  
        	TParm parm = (TParm) obj;     
        	int times =  Integer.parseInt(parm.getValue("PRINTCOUNT"));
      
        	for(int i=0;i<times;i++){
        		this.printDisposablePackageBarcode();
        	}
        	
        }else if(packSeqNo != 0){//手术包条码打印
        	this.printPackageBarcode();
        }
    }
    /**
     * 一次性诊疗包条码打印方法
     */
    private void printDisposablePackageBarcode(){
    	int row = tableM.getSelectedRow();
        if (row < 0)
            row = 0;  
        String packCode = tableM.getItemString(row, "PACK_CODE");
    	//手术包序号
        int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");   
        //手术包批号（主要指诊疗包，手术包批号都为0）
        int packBatchNo = tableM.getItemInt(row, "PACK_BATCH_NO");
        //出库科室 
        TParm packCodeSeqParm = new TParm();
        TParm packParm = INVPackMTool.getInstance().getPackDesc(packCode);              
        String packDesc = packParm.getValue("PACK_DESC", 0);
        String packDept = ((TTextFormat)this.getComponent("ORG_CODE")).getText(); 
//        System.out.println(packCode+"///"+packSeqNo+"///"+packBatchNo);
        //消毒日期                
        TParm result = InvPackStockMTool.getInstance().getPackDateBatch(packCode,packSeqNo,packBatchNo);    
        String packageDate = StringTool.getString( (Timestamp) result.getData("OPT_DATE", 0), "yyyy/MM/dd");
        int valid = Integer.parseInt(result.getData("VALUE_DATE",0).toString());   
        Calendar cal = new GregorianCalendar();  
        cal.set(Integer.parseInt(packageDate.substring(0, 4)), Integer.parseInt(packageDate.substring(5, 7))-1, Integer.parseInt(packageDate.substring(8)));
        cal.add(cal.DATE,valid);//把日期往后增加N天
        Date d=cal.getTime();      
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(d);
        
        String optUser = result.getValue("OPT_USER", 0);
              
        TParm reportParm = new TParm();                                          
        reportParm.setData("PACK_CODE_SEQ","TEXT", result.getData("BARCODE", 0));
        reportParm.setData("PACK_DESC","TEXT",packDesc);           
        reportParm.setData("PACK_DEPT","TEXT","(" + packDept + ")");
        reportParm.setData("PACKAGE_DATE","TEXT",packageDate);  
        reportParm.setData("VALUE_DATE","TEXT",dateString);      
        reportParm.setData("OPT_USER","TEXT",getUserName(optUser));
        reportParm.setData("PACK_DATE","TEXT",packageDate);
        reportParm.setData("PACK_CODE_SEQ_SEC","TEXT", result.getData("BARCODE", 0));
        //调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVGeneralOncePackageBarcode.jhw", reportParm, true);

    }
    
    /** 
     * 手术器械包条码打印方法
     */
    private void printPackageBarcode(){
    	int row = tableM.getSelectedRow();
        if (row < 0)
            row = 0;
        String packCode = tableM.getItemString(row, "PACK_CODE");
    	//手术包序号
        int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
        //手术包批号（主要指诊疗包，手术包批号都为0）
        int packBatchNo = tableM.getItemInt(row, "PACK_BATCH_NO"); 
        //出库科室
        TParm packCodeSeqParm = new TParm();
        TParm packParm = INVPackMTool.getInstance().getPackDesc(packCode);
        String packDesc = packParm.getValue("PACK_DESC", 0);
        String packDept = ((TTextFormat)this.getComponent("ORG_CODE")).getText();   
        //消毒日期
        TParm result = InvPackStockMTool.getInstance().getPackDateBatch(packCode,packSeqNo,packBatchNo);    
        String packageDate = StringTool.getString( (Timestamp) result.getData("OPT_DATE", 0), "yyyy/MM/dd");
        
        int valid = Integer.parseInt(result.getData("VALUE_DATE",0).toString());
        Calendar cal = new GregorianCalendar();
        cal.set(Integer.parseInt(packageDate.substring(0, 4)), Integer.parseInt(packageDate.substring(5, 7))-1, Integer.parseInt(packageDate.substring(8)));
        cal.add(cal.DATE,valid);//把日期往后增加N天
        Date d=cal.getTime();  
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(d);
        
        String optUser = result.getValue("OPT_USER", 0);
          
        TParm reportParm = new TParm();
        //修改后 但是由于条码现在打印只有半张,所以我需要先测试下，看看12位条码效果如何？
        reportParm.setData("PACK_CODE_SEQ","TEXT", result.getData("BARCODE", 0).toString().substring(0, 9));
        reportParm.setData("PACK_DESC","TEXT",packDesc);  
        reportParm.setData("PACK_DEPT","TEXT","(" + packDept + ")"); 
        reportParm.setData("PACKAGE_DATE","TEXT",packageDate);      
        reportParm.setData("VALUE_DATE","TEXT",dateString);   
        reportParm.setData("OPT_USER","TEXT",getUserName(optUser)); 
        reportParm.setData("PACK_DATE","TEXT",packageDate);
        reportParm.setData("PACK_CODE_SEQ_SEC","TEXT", result.getData("BARCODE", 0).toString().substring(0, 9));
            
//        reportParm.setData("PACK_CODE_SEQ","TEXT", result.getData("BARCODE", 0));
//        reportParm.setData("PACK_DESC","TEXT",packDesc);    
//        reportParm.setData("PACK_DEPT","TEXT","(" + packDept + ")"); 
//        reportParm.setData("PACKAGE_DATE","TEXT",packageDate);        
//        reportParm.setData("VALUE_DATE","TEXT",dateString);    
//        reportParm.setData("OPT_USER","TEXT",getUserName(optUser));
//        reportParm.setData("PACK_DATE","TEXT",packageDate);
//        reportParm.setData("PACK_CODE_SEQ_SEC","TEXT", result.getData("BARCODE", 0));
        //调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVGeneralPackageBarcode.jhw", reportParm);

    }
    
    
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
    
    
    public void checkStockQty(double qty){
    	
    	
    	//给主表添加属性
        String returnStr = setTabaleMQTYValue(qty);
        if ("0".equals(returnStr)) {
            // 非序号管理
            //处理物资明细数据
            if (!onGetInvQTY(qty)) {
                this.callFunction("UI|new|setEnabled", false);
                this.callFunction("UI|save|setEnabled", false);
                return;
            }
            //不可编辑
            clearEnable(false);
            this.callFunction("UI|new|setEnabled", false);
            this.callFunction("UI|save|setEnabled", true);
        }
        else if ("1".equals(returnStr)) {
            // 序号管理
            //处理物资明细数据
            if (!onGetInv()) {
                this.callFunction("UI|new|setEnabled", false);
                this.callFunction("UI|save|setEnabled", false);
                return;
            }
            //不可编辑
            clearEnable(false);
            this.callFunction("UI|new|setEnabled", false);
            this.callFunction("UI|save|setEnabled", true);
        }
       
    }
    /**
     * zhangyong20091110
     * 新增时给主表添加录入属性             
     */
    public String setTabaleMQTYValue(double qty) {
        //zhangyong20091110 begin 添加序号管理和非序号管理的区别
        String sql = INVSQL.getInvPackM(this.getValueString("PACK_CODE"));
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if ("0".equals(parm.getValue("SEQ_FLG", 0))) {
            // 非序号管理
            // 打开数量的锁定列
            tableM.setLockColumns("0,1,3,4,5,7,8,9,10,11");
            int row = tableM.addRow();
            TDataStore dataStore = tableM.getDataStore();
            //包号
            dataStore.setItem(row, "PACK_CODE", getValueString("PACK_CODE"));
            //数量(默认数值为1，可修改)
            dataStore.setItem(row, "QTY", qty);
            //序号
            dataStore.setItem(row, "PACK_SEQ_NO", 0);
            //固定成本
            dataStore.setItem(row, "USE_COST", getValueDouble("USE_COST"));
            Timestamp valueDate = TCM_Transform.getTimestamp(getValue(
                "DISINFECTION_DATE"));
            //消毒日期
            dataStore.setItem(row, "DISINFECTION_DATE", valueDate);
            if (valueDate == null)
                dataStore.setItem(row, "DISINFECTION_DATE", new TNull(Timestamp.class));
            //消毒效期
            valueDate = TCM_Transform.getTimestamp(getValue("VALUE_DATE"));
            dataStore.setItem(row, "VALUE_DATE", valueDate);
            if (valueDate == null)
                dataStore.setItem(row, "VALUE_DATE", new TNull(Timestamp.class));
            //消毒人员
            String valueStr = getValueString("DISINFECTION_USER");
            dataStore.setItem(row, "DISINFECTION_USER", valueStr);
            if (valueStr == null)
                dataStore.setItem(row, "DISINFECTION_USER", new TNull(String.class));
            //使用状态
            dataStore.setItem(row, "STATUS", getValueString("STATUS"));
            tableM.setDSValue();
            return "0";
        }
        else if ("1".equals(parm.getValue("SEQ_FLG", 0))) {
            // 序号管理
            // 关闭数量的锁定列
            tableM.setLockColumns("0,1,3,4,5,7,8,9,10,11");
            int row = tableM.addRow();
            TDataStore dataStore = tableM.getDataStore();
            //包号
            dataStore.setItem(row, "PACK_CODE", getValueString("PACK_CODE"));
            //数量(一次只能打一个包)
            dataStore.setItem(row, "QTY", 1);
            //序号
            dataStore.setItem(row, "PACK_SEQ_NO", getValueInt("PACK_SEQ_NO"));
            //固定成本
            dataStore.setItem(row, "USE_COST", getValueDouble("USE_COST"));
            Timestamp valueDate = TCM_Transform.getTimestamp(getValue(
                "DISINFECTION_DATE"));
            //消毒日期
            dataStore.setItem(row, "DISINFECTION_DATE", valueDate);
            if (valueDate == null)
                dataStore.setItem(row, "DISINFECTION_DATE", new TNull(Timestamp.class));
            //消毒效期
            valueDate = TCM_Transform.getTimestamp(getValue("VALUE_DATE"));
            dataStore.setItem(row, "VALUE_DATE", valueDate);
            if (valueDate == null)
                dataStore.setItem(row, "VALUE_DATE", new TNull(Timestamp.class));
            //消毒人员
            String valueStr = getValueString("DISINFECTION_USER");
            dataStore.setItem(row, "DISINFECTION_USER", valueStr);
            if (valueStr == null)
                dataStore.setItem(row, "DISINFECTION_USER", new TNull(String.class));
            //使用状态
            dataStore.setItem(row, "STATUS", getValueString("STATUS"));
            tableM.setDSValue();
            return "1";
        }
        return "1";
        //zhangyong20091110 end
    }
    /**
     * 处理物资明细数据
     * @return boolean
     */
    public boolean onGetInvQTY(double qty) {
        String packCode = getValueString("PACK_CODE");
        //查找手术包明细物资
        TParm result = INVPackDTool.getInstance().getPackDetial(packCode);
        if (result == null || result.getErrCode() < 0)
            return false;
        //把物资明细带入明细表
        for(int i=0;i<result.getCount("INV_CODE");i++){
        	double d = result.getDouble("QTY", i);
        	result.setData("QTY", i, d*qty);
        }
             
        return onAddTableD(result);
    }
}
