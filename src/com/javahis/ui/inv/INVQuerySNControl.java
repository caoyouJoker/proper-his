package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: 高值物资SN号统计报表 </p>
 * 
 * <p>  Description: 高值物资SN号统计报表 </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: JavaHis </p>
 * 
 * @author fux 20140513
 * @version 1.0
 */
public class INVQuerySNControl extends TControl {

	private static TTable mainTable;

	/**
	 * 初始化方法
	 */
    public void onInit() {
        super.init();
        // 获得TABLE对象
        mainTable = (TTable) getComponent("TABLE");
        TParm parm = new TParm();
        // 设置弹出菜单
        getTextField("INV_CODE")
                .setPopupMenuParameter("UD",
                                       getConfigParm()
                                               .newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
                                       parm);
        // 定义接受返回值方法
        getTextField("INV_CODE").addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        this.initPage();
        // if (this.getPopedem("ALL")) {
        // this.callFunction("UI|ORG_CODE|setEnabled", false);
        // }else{
        // this.callFunction("UI|ORG_CODE|setEnabled", true);
        // }
    }

	/**
	 * 初始画面数据
	 */
    private void initPage() {
        Timestamp date = StringTool.getTimestamp(new Date());
//        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        this.setValue("START_TIME", date.toString().substring(0, 10).replace('-', '/')
                + " 00:00:00");
        this.setValue("END_TIME", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("ORG_CODE", Operator.getDept());
    }

	/**
	 * 查询方法
	 */
	public void onQuery() {
		mainTable.removeRowAll();// 清除主表
		String org_code = "";
		String inv_code = "";
		String startTime = "";
		String endTime = "";
		// START_TIME END_TIME

		// 部门代码
		org_code = getValueString("ORG_CODE");
		if (org_code == null || org_code.length() <= 0) {
			this.messageBox("请选择验收部门");
			return;
		}
		// 物资编码
		inv_code = getValueString("INV_CODE");
		// if (inv_code == null || inv_code.length() <= 0) {
		// this.messageBox("请选择查询物资");
		// return;
		// }
		// 供应商
		String sup_code = getValueString("SUP_CODE");
		// 物资分类
		String kind = getValueString("KIND");

		// 查询sql
		// 物资编码,150;物资名称,200;使用日期,100;规格型号,180;编号(SN),200;产品批号,100;科室,100;供应厂商,200
		// INV_CODE;INV_CHN_DESC;BILL_DATE;DESCRIPTION;BARCODE;BATCH_NO;ORG_CODE;SUP_CODE
		// 右连接说明等号右侧的所有记录均会被显示
		// 左连接说明等号左侧的所有记录均会被显示
        String sql =
                "SELECT  T.INV_CODE,T.INV_CHN_DESC,F.BILL_DATE,T.DESCRIPTION,B.ORGIN_CODE,"
                        + "    B.BATCH_NO,B.ORG_CODE,T.SUP_CODE , F.ORDER_DEPT_CODE, F.ORDER_DR_CODE "// wanglong add 20140826
                        + "  FROM INV_BASE T, INV_STOCKDD B, SYS_DEPT C, SYS_SUPPLIER E, SPC_INV_RECORD F "// 高值
                        + " WHERE T.SEQMAN_FLG = 'Y'            "
                        + "   AND T.EXPENSIVE_FLG = 'Y'         "
                        + "   AND B.WAST_FLG = 'Y'              "
                        + "   AND B.ORGIN_CODE IS NOT NULL      "
                        + "	  AND B.INV_CODE = T.INV_CODE       "
                        + "	  AND B.ORG_CODE = C.DEPT_CODE      "
                        + "	  AND T.SUP_CODE = E.SUP_CODE       "
                        + "   AND B.RFID = F.BAR_CODE       ";
		startTime = this.getText("START_TIME");
		endTime = this.getText("END_TIME");
		// 使用日期
		 
		if (!"".equals(startTime) && !"".equals(endTime)) {
			startTime = startTime.substring(0, 10).replaceAll("/", "").trim();     
			endTime = endTime.substring(0, 10).replaceAll("/", "").trim();  
			sql += " AND F.BILL_DATE BETWEEN TO_DATE('"+startTime+"000000"+"','YYYYMMDDHH24MISS') " +
					" AND TO_DATE('"+endTime+"235959"+"','YYYYMMDDHH24MISS')";  
		}

		// 部门
		if (!"".equals(org_code)) {   
			sql += " AND B.ORG_CODE = '" + org_code + "'";
		}
		// 物资编码
		if (!"".equals(inv_code)) {
			sql += " AND T.INV_CODE = '" + inv_code + "'";
		}
		// 供应商
		if (!"".equals(sup_code)) {
			sql += " AND T.SUP_CODE = '" + sup_code + "'";
		}
		sql += " ORDER BY T.INV_CODE,F.BILL_DATE ";
//		System.out.println("sql----------------------->" + sql);
		TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));

		if (resultParm.getCount() < 0) {
			this.messageBox("没有要查询的数据");
			return;
		}

		mainTable.setParmValue(resultParm);

	}

	/**
	 * 每三位加逗号处理
	 */
	public String feeConversion(String fee) {
		String str1 = "";
		String[] s = fee.split("\\.");// 以"."来分割

		str1 = new StringBuilder(s[0].toString()).reverse().toString(); // 先将字符串颠倒顺序
		String str2 = "";
		for (int i = 0; i < str1.length(); i++) {
			if (i * 3 + 3 > str1.length()) {
				str2 += str1.substring(i * 3, str1.length());
				break;
			}
			str2 += str1.substring(i * 3, i * 3 + 3) + ",";
		}
		if (str2.endsWith(",")) {
			str2 = str2.substring(0, str2.length() - 1);
		}
		// 最后再将顺序反转过来
		String str3 = new StringBuilder(str2).reverse().toString();
		// 加上小数点后的数
		StringBuffer str4 = new StringBuffer(str3);
		str4 = str4.append(".").append(s[1]);
		return str4.toString();
	}

	/**
	 * 汇出方法
	 */
	public void onExcel() {   
		if (mainTable.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(mainTable, "高值物资SN号统计报表");
		} else {
			this.messageBox("没有汇出数据");
			return;
		}
	}
   
	/**
	 * 清空方法
	 */        
	public void onClear() {
		// fux modify 20140313   
		this.clearValue("START_TIME;END_TIME;ORG_CODE;SUP_CODE;INV_CODE;INV_DESC");
		mainTable.removeRowAll();
        this.initPage();
	}

	/**
	 * 变更单选框
	 */
	public void onChangeRadioButton() {
		if (getRadioButton("VALID_DATE_C").isSelected()) {
			getTextFormat("VALID_DATE").setEnabled(true);
		} else {
			getTextFormat("VALID_DATE").setEnabled(false);
			this.clearValue("VALID_DATE");
		}
	}

	/**
	 * 得到TextField对象
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
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

	/**
	 * 根据指定的月份和天数加减计算需要的月份和天数
	 * 
	 * @param Month
	 *            String 制定月份 格式:yyyyMM
	 * @param Day
	 *            String 制定月份 格式:dd
	 * @param num
	 *            String 加减的数量 以月为单位
	 * @return String
	 */
	public String rollMonth(String Month, String Day, int num) {
		if (Month.trim().length() <= 0) {
			return "";
		}
		Timestamp time = StringTool.getTimestamp(Month, "yyyyMM");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(time.getTime()));
		// 当前月＋num
		cal.add(cal.MONTH, num);
		// 将下个月1号作为日期初始值
		cal.set(cal.DATE, 1);
		Timestamp month = new Timestamp(cal.getTimeInMillis());
		String result = StringTool.getString(month, "yyyyMM");
		String lastDayOfMonth = getLastDayOfMonth(result);
		if (TypeTool.getInt(Day) > TypeTool.getInt(lastDayOfMonth)) {
			result += lastDayOfMonth;
		} else {
			result += Day;
		}
		return result;
	}

	/**
	 * 获取指定月份的最后一天的日期
	 * 
	 * @param date
	 *            String 格式 YYYYMM
	 * @return Timestamp
	 */
	public String getLastDayOfMonth(String date) {
		if (date.trim().length() <= 0) {
			return "";
		}
		Timestamp time = StringTool.getTimestamp(date, "yyyyMM");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(time.getTime()));
		// 当前月＋1，即下个月
		cal.add(cal.MONTH, 1);
		// 将下个月1号作为日期初始值
		cal.set(cal.DATE, 1);
		// 下个月1号减去一天，即得到当前月最后一天
		cal.add(cal.DATE, -1);
		Timestamp result = new Timestamp(cal.getTimeInMillis());
		return StringTool.getString(result, "dd");
	}

	/**
	 * 获取指定n个月前的日期
	 */
	public String getMonthDay(int no) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Calendar lastDate = Calendar.getInstance();
		// lastDate.set(Calendar.DATE, no);// 设为当前月的n号
		lastDate.add(Calendar.MONTH, +no);// 减n个月，变为下月的1号
		// lastDate.add(Calendar.DATE,-1);//减去一天，变为当月最后一天

		str = sdf.format(lastDate.getTime());
		return str;
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		if (parm == null) {
			return;
		}
		String order_code = parm.getValue("INV_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("INV_CODE").setValue(order_code);
		String order_desc = parm.getValue("INV_CHN_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("INV_DESC").setValue(order_desc);
	}

}
