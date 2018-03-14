package com.javahis.ui.pha;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.javahis.util.JavaHisDebug;

import jdo.bil.BILComparator;
import jdo.pha.PhaMedSaleStaTool;
import com.dongyang.util.StringTool;
import jdo.sys.PatTool;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.jdo.TJDODBTool;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import jdo.util.Manager;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.util.Compare;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;

/**
 *
 * <p>Title: 门急诊药品销售分类查询报表
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author ZangJH 2009.01.20
 *
 * @version 1.0
 */

public class PhaMedSaleStaControl
    extends TControl {
  
    private TTextFormat START_DATE; //起始时间
    private TTextFormat END_DATE; //截止时间

    private TCheckBox OrdCat1_W; //西药
    private TCheckBox OrdCat1_C; //中成
    private TCheckBox OrdCat1_G; //草药
    private TCheckBox OrdCat1_M; //麻精

    private TTextField MR_NO;
    private TTextField NAME;
    private TNumberTextField TOT; //总计
    private TTextField ORDER_DESC; //药品名称
    private TTextField ORDER_CODE; //药品代码

    private TComboBox TYPE; //状态类型
    private TComboBox REGION_CODE; //病区
    private TTextFormat EXEC_DEPT_CODE; //执行科室（药房）//===zhangp 20120817

    private TRadioButton Master;
    private TRadioButton Detail;

    private TTable table;

    private String MedType;
    
    private String sysPope;
	
	//$$=============add by liyh 2012-07-10 加入排序功能start==================$$//
    //=====modify-begin (by wanglong 20120716)===============================
    //旧对比类有问题，重写
	//private Compare compare = new Compare();
	private BILComparator compare=new BILComparator();
	//======modify-end========================================================
	private boolean ascending = false;
	private int sortColumn = -1;
    //$$=============add by liyh 20120710 加入排序功能end==================$$//    

    public PhaMedSaleStaControl() {
    }

    public void onInit() { //初始化程序
        super.onInit();
        //===zhangp 201200726 start
        Object obj = getPopedem();
        TParm sysParm = (TParm) obj;
        sysPope = "";
        if(sysParm.getCount() > 0){
        	sysPope = sysParm.getValue("ID", 0);
        }
//		messageBox(""+sysPope);
        //===zhangp 201200726 start
        myInitControler();
        //===========pangben modify 20110511 stop
		//$$=====add by liyh 20120710加入排序方法start============$$//
		addListener(getTTable("TABLE"));
		//$$=====add by liyh 20120710 加入排序方法end============$$//

    }

    /**
     * 首先得到所有UI的控件对象/注册相应的事件
     * 设置
     */
    public void myInitControler() {

        START_DATE = (TTextFormat)this.getComponent("START_DATE");
        END_DATE = (TTextFormat)this.getComponent("END_DATE");

        OrdCat1_W = (TCheckBox)this.getComponent("OrdCat1_W");
        OrdCat1_C = (TCheckBox)this.getComponent("OrdCat1_C");
        OrdCat1_G = (TCheckBox)this.getComponent("OrdCat1_G");
        OrdCat1_M = (TCheckBox)this.getComponent("OrdCat1_M");
        

        MR_NO = (TTextField)this.getComponent("MR_NO");
        NAME = (TTextField)this.getComponent("NAME");
        TOT = (TNumberTextField)this.getComponent("TOT");
        ORDER_DESC = (TTextField)this.getComponent("ORDER_DESC");
        ORDER_CODE = (TTextField)this.getComponent("ORDER_CODE");

        TYPE = (TComboBox)this.getComponent("TYPE");
        REGION_CODE = (TComboBox)this.getComponent("REGION_CODE");
        EXEC_DEPT_CODE = (TTextFormat)this.getComponent("EXEC_DEPT_CODE");//===zhangp 20120817

        Master = (TRadioButton)this.getComponent("Master");
        Detail = (TRadioButton)this.getComponent("Detail");

        table = (TTable)this.getComponent("TABLE");

        //------------------------在某一个控件上注册SYSFeePopup-------------------
        // 注册激发SYSFeePopup弹出的事件
        ORDER_CODE.setPopupMenuParameter("TAG", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"));
        // 定义接受返回值方法
        ORDER_CODE.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");

        //初始化界面参数
        myInit();

    }
    /**
     * 初始化界面的值
     */
    public void myInit() {
        //初始化table--缺省是汇总
        onVarious("M");
        REGION_CODE.setValue(Operator.getRegion());
        //========pangben modify 20110421 start 权限添加
       this.callFunction("UI|REGION_CODE|setEnabled",SYSRegionTool.getInstance().getRegionIsEnabled(this.
              getValueString("REGION_CODE")));
       //========pangben modify 20110421 stop
        EXEC_DEPT_CODE.setValue(Operator.getDept());
        //luhai 2012-04-24 begin 加入已经发药的判断
        TYPE.setValue("04");
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("END_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 23:59:59");
		this.setValue("START_DATE", date.toString().substring(0, 10).replace('-', '/')
				+ " 00:00:00");
        //luhai 2012-04-24 end  
        //=====modify-begin (by wanglong 20120713)===============================
        //增加明细查看的权限控制。
        //System.out.println("角色："+Operator.getRole());//ADMIN
        if(sysPope.equals("PHAADMIN")){
        	this.callFunction("UI|Detail|setEnabled", true);
        }else{
        	this.callFunction("UI|Detail|setEnabled", false);
        }
        //======modify-end========================================================
    }

    /**
     * 查询函数
     * 1)查询
     * 2)当有数据的时候改变相关的控件状态
     */
    public void onQuery() {
        //清除先前的数据
        table.setParmValue(new TParm());

        if (!checkCondition())
            return;

        TParm tableDate = getQueryDate();

        if (tableDate.getCount() <= 0) {
        	// ====modify-begin (by wanglong 20120716) =====
            //onClear();
            // ====modify-end===============================
            this.messageBox("该查询条件无数据！");
            return;
        }
        //总金额
        double totalAmt = 0.0;
        int count = tableDate.getCount();
         //==========pangben modify 20110426 start
        int sumDispenseQty=0;//数量
        double sumContractPrice=0.00;//成本金额
         //==========pangben modify 20110426 stop
        //循环累加
        for (int i = 0; i < count; i++) {
            double temp = tableDate.getDouble("OWN_AMT", i);
            totalAmt += temp;
            //==========pangben modify 20110426 start
            sumDispenseQty+=tableDate.getInt("DISPENSE_QTY", i);
            tableDate.setData("DISPENSE_QTY",i,tableDate.getInt("DISPENSE_QTY", i));
           if (!Detail.isSelected())
            sumContractPrice+=tableDate.getDouble("CONTRACT_PRICE", i);
            //==========pangben modify 20110426 stop
        }
        TOT.setValue(totalAmt);

        //手动写入当前状态列
        if (Detail.isSelected()) {
            for (int i = 0; i < count; i++) {
                tableDate.setData("TYPE", i, TYPE.getSelectedName());
            }

        }
        //==========pangben modify 20110426 start
        tableDate.setData("REGION_CHN_DESC", count, "总计:");
        tableDate.setData("ORDER_CODE", count, "");
        tableDate.setData("ORDER_DESC", count, "");
        tableDate.setData("SPECIFICATION", count, "");
        tableDate.setData("DISPENSE_UNIT", count, "");
        tableDate.setData("DISPENSE_QTY", count, sumDispenseQty);
        tableDate.setData("CONTRACT_PRICE", count, sumContractPrice);
        tableDate.setData("OWN_AMT", count, totalAmt);
        //==========pangben modify 20110426 stop
        //加载table上的数据
        this.callFunction("UI|TABLE|setParmValue", tableDate);

        }

        /**
         * 获得查询/打印需要显示在table上的数据
         * @return TParm
         */
        public TParm getQueryDate() {
            TParm inParm = new TParm();
            //状态类型
            String type = TYPE.getValue();
            //得到当前统计时间
            Timestamp startDate = (Timestamp) START_DATE.getValue();
            String tempEnd = END_DATE.getValue().toString();
            Timestamp endDate = StringTool.getTimestamp(tempEnd.substring(0, 4) +
                    tempEnd.substring(5, 7) + tempEnd.substring(8, 10) + "235959",
                    "yyyyMMddHHmmss");
            //获得查询参数
            //===============pangben modify 20110416 strat 实现跨区域查询
            if (this.getValueString("REGION_CODE").length() > 0)
                inParm = this.getParmForTag("REGION_CODE");
            if (startDate != null)
                inParm.setData("START_DATE", startDate);
            if (endDate != null)
                inParm.setData("END_DATE", endDate);
            //===============pangben modify 20110416 stop
            String mrNo = MR_NO.getValue();
            if (!"".equals(mrNo))
                inParm.setData("MR_NO", mrNo);

            String execDept = getValueString("EXEC_DEPT_CODE");
            if (!"".equals(execDept))
                inParm.setData("EXEC_DEPT_CODE", execDept);

            String ordCode = ORDER_CODE.getValue();
            if (!"".equals(ordCode))
                inParm.setData("ORDER_CODE", ordCode);
            if(OrdCat1_M.isSelected()) {
            	inParm.setData("CTRLDRUGCLASS_CODE","'01','02'");
            }
            //判断选中的药品类型
            int x = 0;
            //当西药被选中的时候
            if (OrdCat1_W.isSelected())
                x |= 4; //x=x|4
            //当中成被选中的时候
            if (OrdCat1_C.isSelected())
                x |= 2;
            //当草药被选中的时候
            if (OrdCat1_G.isSelected())
                x |= 1;

            switch (x) {
            case 4:
    //                System.out.println("==只选西药=");
                inParm.setData("ORDER_CAT1_CODE1", "Y");
                inParm.setData("VALUE1", "PHA_W");

                MedType = "西药";
                break;
            case 2:
    //                System.out.println("==只选中成药=");
                inParm.setData("ORDER_CAT1_CODE1", "Y");
                inParm.setData("VALUE1", "PHA_C");

                MedType = "中成药";
                break;
            case 1:
    //                System.out.println("==只选草药=");
                inParm.setData("ORDER_CAT1_CODE1", "Y");
                inParm.setData("VALUE1", "PHA_G");

                MedType = "中草药";
                break;
            case 6:
    //                System.out.println("==选西/中成药=");
                inParm.setData("ORDER_CAT1_CODE2", "Y");
                inParm.setData("VALUE1", "PHA_W");
                inParm.setData("VALUE2", "PHA_C");

                MedType = "西药、中成药";
                break;
            case 3:
    //                System.out.println("==只选中成/草药=");
                inParm.setData("ORDER_CAT1_CODE2", "Y");
                inParm.setData("VALUE1", "PHA_G");
                inParm.setData("VALUE2", "PHA_C");

                MedType = "中成药、中草药";
                break;
            case 5:
    //                System.out.println("==只选西/草药=");
                inParm.setData("ORDER_CAT1_CODE2", "Y");
                inParm.setData("VALUE1", "PHA_W");
                inParm.setData("VALUE2", "PHA_G");

                MedType = "西药、中草药";
                break;
            case 7:
    //                System.out.println("==都选=");
                inParm.setData("ORDER_CAT1_CODE3", "Y");
                inParm.setData("VALUE1", "PHA_W");
                inParm.setData("VALUE2", "PHA_G");
                inParm.setData("VALUE3", "PHA_C");

                MedType = "西药、中成药、中草药";
                break;
            }
           /********************添加 抗生素查询条件 20120710 by liyh start*************************/ 
           //抗生素等级	           
           if (getCheckBox("ANTIBIOTIC").isSelected()) {//如果不选择 则没有这个查询条件
        	    String  antiblogticCode=this.getValueString("ANTIBIOTIC_CODE")+"";
				if (null != antiblogticCode &&  !"".equals(antiblogticCode)) {//查询具体抗生素等级
					inParm.setData("ANTIBIOTIC_CODE", antiblogticCode);
				}else
				{
					inParm.setData("ANTIBIOTIC_CODE1", "A");//查询全部
				}
           }
           /********************添加 抗生素查询条件 20120710 by liyh end***************************/
            inParm.setData("VARIOUS", Master.isSelected() ? "M" : "D");
            if (getValueString("DEPT_CODE").length() != 0)
                inParm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
            if (getValueString("DR_CODE").length() != 0)
                inParm.setData("DR_CODE", getValueString("DR_CODE"));
            //调用后台查询相应的数据
            TParm result = PhaMedSaleStaTool.getInstance().getQueryDate(inParm, type);
            return result;
        }
        
    /**
     * 得到CheckBox对象
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }
    
    
    /**
     * 变更复选框
     */
    public void onChangeCheckBox() {
        //抗生素等级
        if (getCheckBox("ANTIBIOTIC").isSelected()) {
        	getComBox("ANTIBIOTIC_CODE").setEnabled(true);
        }else
        {
        	  getComBox("ANTIBIOTIC_CODE").setEnabled(false);
              this.clearValue("ANTIBIOTIC_CODE");
        }
    }     
    
    /**
	 * 得到combox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TComboBox getComBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}    

    /**
     * 清空动作
     */
    public void onClear() {
    	//luhai 2012-04-24 删除状态类型的清空begin
//        this.clearValue(
//            "MR_NO;EXEC_DEPT_CODE;ORDER_CODE;OrdCat1_W;OrdCat1_G;OrdCat1_C;NAME;TOT;ORDER_DESC;ORDER_CODE;EXEC_DEPT_CODE;TYPE");
        this.clearValue(
            "MR_NO;EXEC_DEPT_CODE;ORDER_CODE;OrdCat1_W;OrdCat1_G;OrdCat1_C;NAME;TOT;ORDER_DESC;ORDER_CODE;EXEC_DEPT_CODE");
        //luhai 2012-04-24 删除状态类型的清空end 
        //清空table
        table.removeRowAll();
        //选择‘总汇’
        Master.setSelected(true);
        onVarious("M");
        //========pangben modify 20110416
        REGION_CODE.setValue(Operator.getRegion());

    }

    /**
     * 检测传入后台的参数是否合理
     * @return boolean
     */
    public boolean checkCondition() {
        //要么选择药品种类，要么有药品CODE
        if (!OrdCat1_M.isSelected()&&!OrdCat1_W.isSelected() && !OrdCat1_C.isSelected() &&
            !OrdCat1_G.isSelected()&&"".equals(ORDER_CODE.getValue())) {
            this.messageBox("请选择药品类型！");
            return false;
        }
        if ("".equals(TYPE.getValue())) {
            this.messageBox("请输入状态类型！");
            return false;
        }
        //===========pangben modify 20110416 start 实现跨区域查询
//        if ("".equals(REGION_CODE.getValue())) {
//            this.messageBox("请输入区域！");
//            return false;
//        }
        //===========pangben modify 20110416 stop
        return true;
    }

    /**
     * 补齐MR_NO
     */
    public void onMrNo() {
        String mrNo =PatTool.getInstance().checkMrno( MR_NO.getValue());
        MR_NO.setValue(mrNo);
      //modify by huangtt 20160927 EMPI患者查重提示  start
        Pat pat = Pat.onQueryByMrNo(mrNo);
        if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {// wanglong add 20150423
            this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
        }
        MR_NO.setValue(pat.getMrNo());
        NAME.setValue(pat.getName());
      //modify by huangtt 20160927 EMPI患者查重提示  end
        
//        //得到病患名字
//        getPatName(mrNo);
    }

    /**
     * 获得该病人的姓名
     * @param mrNo String
     */
    private void getPatName(String mrNo){

        NAME.setValue(PatTool.getInstance().getNameForMrno(mrNo));

    }

    /**
     * 切换主细表
     * 初始化主细表
     * @param ms Object
     */
    public void onVarious(Object ms) {
        String MSFlg = ms.toString();
        //切换主细表时清空table
        table.setParmValue(new TParm());
        if ("D".equals(MSFlg)) {
            //=========pangben modify 20110416 start 添加区域显示
            table.setHeader(
                "区域,120;开药日期,100;病案号,100;姓名,100;药品名称,240;规格,140;数量,60;单位,60,UNIT;发药金额,100,double,#########0.00;当前状态,90;开单科室,90,DEPT_CODE_VIEW;开单医生,90,DR_CODE");
            table.setParmMap("REGION_CHN_DESC;ORDER_DATE;MR_NO;PAT_NAME;ORDER_DESC;SPECIFICATION;DISPENSE_QTY;DISPENSE_UNIT;OWN_AMT;TYPE;DEPT_CODE;DR_CODE");
            table.setLockColumns("0,1,2,3,4,5,6,7,8,9,10,11");
            table.setColumnHorizontalAlignmentData(
                "0,left;1,left;2,left;3,left;4,left;5,left;6,right;7,left;8,right;9,left;10,left;11,left");
            //=========pangben modify 20110416 stop
        }
        if ("M".equals(MSFlg)) {
            //=========pangben modify 20110416 start 添加区域显示
    		// ==========modify-begin (by wanglong 20120716) 去掉汇总时的“开单科室”与“开单医生”列
//            table.setHeader(
//                "区域,180;药品代码,100;药品名称,250;规格,130;单位,70,UNIT;数量,70;发药金额,70,double,#########0.00;成本金额,90,double,#########0.00;开单科室,90,DEPT_CODE;开单医生,90,DR_CODE");
//            table.setParmMap(
//                "REGION_CHN_DESC;ORDER_CODE;ORDER_DESC;SPECIFICATION;DISPENSE_UNIT;DISPENSE_QTY;OWN_AMT;CONTRACT_PRICE;DEPT_CODE;DR_CODE");
//            table.setLockColumns("0,1,2,3,4,5,6,7,8,9");
//            table.setColumnHorizontalAlignmentData(
//                "0,left;1,left;2,left;3,left;4,left;5,right;6,right;7,right;8,left;9,left");
              table.setHeader(
              		"区域,120;药品代码,100;药品名称,240;规格,140;数量,60;单位,60,UNIT;发药金额,100,double,#########0.00;成本金额,100,double,#########0.00");
              table.setParmMap(
              		"REGION_CHN_DESC;ORDER_CODE;ORDER_DESC;SPECIFICATION;DISPENSE_QTY;DISPENSE_UNIT;OWN_AMT;CONTRACT_PRICE");
              table.setLockColumns("0,1,2,3,4,5,6,7");
              table.setColumnHorizontalAlignmentData(
              		"0,left;1,left;2,left;3,left;4,right;5,right;6,right;7,right");
            // ==========modify-end========================================
            //=========pangben modify 20110416 stop
        }
        TOT.setValue(0);
//        this.messageBox_(ms);

    }

    /**
     * 导出EXCEL
     */
    public void onExcel() {
        //得到UI对应控件对象的方法（UI|XXTag|getThis）
        ExportExcelUtil.getInstance().exportExcel(table, "门急诊药品销售分类统计报表");

    }

    /**
     * 打印报表
     */
    public void onPrint() {

        if (table.getRowCount() <= 0) {
            this.messageBox("没有相关打印数据！");
            return;
        }
        //拿到现实的数据(省去format动作)
        TParm printData = table.getShowParmValue();
        int rowCount = table.getRowCount();

        Timestamp nowTime = TJDODBTool.getInstance().getDBTime();

        //报表名字
        String prtName = "";
        String tableName = "";
        //程序名
        String proName = "【PhaMedSaleStaControl】";
        //打印时间
        String prtTime = StringTool.getString(nowTime, "yyyy/MM/dd HH:mm:ss");
        //医院简称
        String HospName = Manager.getOrganization().getHospitalCHNShortName(REGION_CODE.getValue());
        Timestamp startDate=(Timestamp) START_DATE.getValue();
        Timestamp endDate=(Timestamp) END_DATE.getValue();
        //统计区间
        String staSection = "统计区间: " + StringTool.getString(startDate,
            "yyyy/MM/dd") + " ～ " + StringTool.getString(endDate,
            "yyyy/MM/dd");
        //制表时间
        String prtDate = "制表时间:" + StringTool.getString(nowTime,"yyyy/MM/dd HH:mm:ss");
        //---------------------查询条件-----------------------------------------
        //区域
        String region=REGION_CODE.getSelectedName();
        //科室
        String dept=EXEC_DEPT_CODE.getSelectText();//====zhangp 20120817 start

        String prtType=TYPE.getSelectedName();

        String MDType=Master.isSelected()?"(汇总)":"(明细)";


        printData.setCount(rowCount);
        if (Master.isSelected()) {
            //7个字段
            //======pangben modify 20110418 start
            printData.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
            //======pangben modify 20110418 start
            printData.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
            printData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printData.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            printData.addData("SYSTEM", "COLUMNS", "DISPENSE_UNIT");
            printData.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            printData.addData("SYSTEM", "COLUMNS", "OWN_AMT");
            printData.addData("SYSTEM", "COLUMNS", "CONTRACT_PRICE");
    		// ==========modify-begin (by wanglong 20120716) 去掉汇总时的“开单科室”与“开单医生”列
            //printData.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
            //printData.addData("SYSTEM", "COLUMNS", "DR_CODE");
            // ==========modify-end========================================
            prtName = "PHAMedSaleStaBySort_M.jhw";
            tableName = "TABLE_M";

        }
        if (Detail.isSelected()) {
            //9个字段
            //======pangben modify 20110418 start
            printData.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
            //======pangben modify 20110418 start
            printData.addData("SYSTEM", "COLUMNS", "ORDER_DATE");
            printData.addData("SYSTEM", "COLUMNS", "MR_NO");
            printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
            printData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printData.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            printData.addData("SYSTEM", "COLUMNS", "DISPENSE_UNIT");
            printData.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            printData.addData("SYSTEM", "COLUMNS", "OWN_AMT");
            printData.addData("SYSTEM", "COLUMNS", "TYPE");
            printData.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
            printData.addData("SYSTEM", "COLUMNS", "DR_CODE");
            prtName = "PHAMedSaleStaBySort_D.jhw";
            tableName = "TABLE_D";
        }

        if ("".equals(prtName)) {
            this.messageBox("数据错误！");
            return;
        }

        TParm parm = new TParm();
        //实参-1:表名 2：Map型数据
        parm.setData(tableName, printData.getData());
        parm.setData("proName", "TEXT", proName);
        parm.setData("prtTime", "TEXT", prtTime);
        parm.setData("HospName", "TEXT", HospName);
        parm.setData("staSection", "TEXT", staSection);
        parm.setData("prtDate", "TEXT", prtDate);
        parm.setData("REGION", "TEXT", region.length()==0?"所有医院":region);
        parm.setData("DEPT", "TEXT", dept);
        parm.setData("MedType", "TEXT", MedType);
        parm.setData("staName", "TEXT", prtType + "药品销售分类统计"+MDType+"报表");

        this.openPrintWindow("%ROOT%\\config\\prt\\pha\\" + prtName, parm);

    }


    /**
     * 接受返回值方法
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {

        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            ORDER_CODE.setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            ORDER_DESC.setValue(order_desc);
    }

	/**
	 * 得到TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 * @author liyh
	 * @date 20120710
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	/**
	 * 加入表格排序监听方法
	 * @param table
	 * @author liyh
	 * @date 20120710
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// TParm tableData = getTTable("TABLE").getParmValue();
		 //System.out.println("===tableDate排序前==="+tableData);
		 
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				 //System.out.println("----+i:"+i);
				 //System.out.println("----+i:"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
			    // ==========modify-begin (by wanglong 20120716) 去掉汇总时的“开单科室”与“开单医生”列
				TParm tableData = getTTable("TABLE").getParmValue();
				//TParm tableData = getTTable("TABLE").getShowParmValue();
				if(Detail.isSelected()){
					tableData.removeData("Data", "ORDER_CODE");
					tableData.removeData("Data", "CONTRACT_PRICE");
					tableData.addData("ORDER_DATE", "");
					tableData.addData("DEPT_CODE", "");
					tableData.addData("PAT_NAME", "");
					tableData.addData("MR_NO", "");
					tableData.addData("DR_CODE", "");
					tableData.addData("TYPE", "");
				}else{
					tableData.removeData("Data", "ORDER_DATE");
					tableData.removeData("Data", "PAT_NAME");
					tableData.removeData("Data", "TYPE");
					tableData.removeData("Data", "MR_NO");
					//tableData.addData("DR_CODE", "MR_NO");
					//tableData.addData("DEPT_CODE", "MR_NO");
				}
				//“总计”行 不参与排序
				TParm totRowParm=new TParm();//记录“总计”行
				tableData.setCount(tableData.getCount("OWN_AMT")-1);
				totRowParm.addRowData(table.getShowParmValue(), tableData.getCount());
				tableData.removeRow(tableData.getCount());//去除最后一行(总计行)
				// ==========modify-end========================================
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = getTTable("TABLE").getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
			    // ==========modify-begin (by wanglong 20120716)===============
				//cloneVectoryParam(vct, new TParm(), strNames);
				TParm lastResultParm=new TParm();//记录最终结果
				lastResultParm=cloneVectoryParam(vct, new TParm(), strNames);//加入中间数据
				if(Detail.isSelected()){
					String colName[] = tableData.getNames("Data");
					for (String tmp : colName) {
						if(tmp.equals("REGION_CHN_DESC")||tmp.equals("OWN_AMT")) continue;
						else if(tmp.equals("DISPENSE_QTY")) totRowParm.setData("DISPENSE_QTY", 0,(int)Double.parseDouble(totRowParm.getValue("DISPENSE_QTY",0)));
						else{
							 totRowParm.setData(tmp, 0, "");
						}
					}
				}else{
					String colName[] = tableData.getNames("Data");
					for (String tmp : colName) {
						if(tmp.equals("REGION_CHN_DESC")||tmp.equals("CONTRACT_PRICE")||tmp.equals("OWN_AMT")) continue;
						else if(tmp.equals("DISPENSE_QTY")) totRowParm.setData("DISPENSE_QTY", 0, (int)Double.parseDouble(totRowParm.getValue("DISPENSE_QTY",0)));
						else{
							 totRowParm.setData(tmp, 0, "");
						}
					}
				}
				lastResultParm.addRowData(totRowParm, 0);//加入总计行
				lastResultParm.setCount(tableData.getCount("OWN_AMT")-1);
				table.setParmValue(lastResultParm);
				// ==========modify-end========================================
				// getTMenuItem("save").setEnabled(false);
			}
		});
	}	
	
	/**
	 * 加入排序功能
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 * @author liyh
	 * @date 20120710
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}
	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
	 * @return Vector
	 * @author liyh
	 * @date 20120710
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}	

	/**
	 * vectory转成param
	 * @author liyh
	 * @date 20120710
	 */
	    // ==========modify-begin (by wanglong 20120716)===============
//	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
//			String columnNames) {
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// ==========modify-end========================================
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		// ==========modify-begin (by wanglong 20120716)===============
		// getTTable("TABLE").setParmValue(parmTable);
		return parmTable;
		// ==========modify-end========================================
		// System.out.println("排序后===="+parmTable);

	}

    public static void main(String[] args) {

        //JavaHisDebug.initClient();
        //JavaHisDebug.initServer();
//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("pha\\PHAMedSaleSta.x");
    }

    public void onClickW() {
    	String flg = this.getValueString("OrdCat1_W");
    	if("Y".equals(flg)) {
    		this.setValue("OrdCat1_M", "N");			
    	}
    }
    
    public void onClickC() {
    	String flg = this.getValueString("OrdCat1_C");
    	if("Y".equals(flg)) {
    		this.setValue("OrdCat1_M", "N");
    	}
    }
    
    public void onClickG() {			
    	String flg = this.getValueString("OrdCat1_G");
    	if("Y".equals(flg)) {
    		this.setValue("OrdCat1_M", "N");
    	}
    }
    
    /**
     * 麻精点击事件			
     */
    public void onClick() {
    	String mjFlg = this.getValueString("OrdCat1_M");
    	if("Y".equals(mjFlg)) {
    		this.setValue("OrdCat1_W", "N");
    		this.setValue("OrdCat1_C", "N");
    		this.setValue("OrdCat1_G", "N");
    	}else {
    		this.setValue("OrdCat1_W", "Y");
    		this.setValue("OrdCat1_C", "Y");
    		this.setValue("OrdCat1_G", "Y");
    	}
    }

}
