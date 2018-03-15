package com.javahis.ui.hrm;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import jdo.hrm.HRMRecruit;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: 受试者招募表 </p>
 *
 * <p> Description: 受试者招募信息导入导出 </p>
 *
 * <p> Copyright: Copyright (c) 2016 </p>
 *
 * <p> Company: ProperSoft </p>
 *
 * @author guangl 20160308
 * @version 1.0
 */
public class HRMRecruitControl extends TControl {
	
	private final static int[] li_SecPosValue = { 1601, 1637, 1833, 2078, 2274,  
        2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858,  
        4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590 };  
	private final static String[] lc_FirstLetter = { "a", "b", "c", "d", "e",  
        "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",  
        "t", "w", "x", "y", "z" };  
	
	private TTextFormat contractCode;
	
	
	private int insertrow;
	
	//主表格
	private TTable table;
	
	//TDataStore对象
	private HRMRecruit recruit;
	
	String contractcode;
	
	private Map<String,Integer> seq = new HashMap<String , Integer>();
	

	/**
	 * 构造方法
	 */
	public HRMRecruitControl() {
	}
	
	/**
	 * 初始化界面
	 */
	public void  initPage(){
		contractCode = (TTextFormat) getComponent("CONTRACT_CODE");
		
		table = (TTable) getComponent("TABLE");
		//表格增加值改变事件
		table.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE, this, "onTableChange");
		recruit = new HRMRecruit();
		table.setDataStore(recruit);
	}
	
	/**
	 * 初始化数据
	 * 
	 */
	public void initData(){
		freshTextFormat();
		//查询填充datastore
		recruit.onQuery();
		//新增一行用于新增
		insertrow = recruit.insertRow();
		recruit.setItem(insertrow, "CONTRACT_CODE", "本行用于新增");
		recruit.setItem(insertrow, "SEQ", null);
		recruit.onNew(insertrow);
		//显示
		table.setDSValue();
		
		seq = new HashMap<String,Integer>();
		this.setValue("TOT_COUNT", String.valueOf(recruit.rowCount() - 1));
	}
	
	@Override
	public void onInit() {
		super.onInit();
		initPage();
		initData();
		
	}
	
	/**
	 * 增加、更新和删除后重置两个空间的内容
	 */
	public void freshTextFormat(){
		contractCode.onQuery();
	}

	/**
	 * 保存招募信息
	 */
	public void onSave(){
		table.acceptText();
		//如果未新增行，将预留的空行删掉，再执行更新
		if("本行用于新增".equals(recruit.getItemData(insertrow, "CONTRACT_CODE"))){
			if(!checkInsertRow()){
				return;
			}
			recruit.deleteRow(insertrow);
			if(!recruit.update()){
				messageBox("保存失败");
				return;
			}
			
			table.setDSValue();
			messageBox("保存成功！");
			//将用于新增的空行加回来
			onQuery();
			return;
		}
		
		//如果主键信息完备，补充剩余的三个字段
		recruit.onNew(insertrow);
		if(!checkInsertRow()){
//			initData();
			return;
		}
		
		if(!recruit.update()){
			messageBox("保存失败！");
			
		}else{
			table.setDSValue();
			messageBox("保存成功！");
		}
		
		onQuery();
	}
	
	/**
	 * 校验必要信息
	 * @return
	 */
	public boolean checkInsertRow(){
		int count = table.getRowCount();
		for (int i = 0; i < count; i++) {
			if("本行用于新增".equals(table.getItemString(i, "CONTRACT_CODE"))){
				continue;
			}
			
			if ("".equals(table.getItemString(i, "CONTRACT_DESC"))) {
				messageBox("第" + (i + 1) + "行未填写方案名称，请补全后再保存。");
				table.setSelectedRow(i);
				return false;
			}
			if ("".equals(table.getItemString(i, "PAT_NAME"))) {
				messageBox("第" + (i + 1) + "行受试者姓名为必要信息，请补全后再保存。");
				table.setSelectedRow(i);
				return false;
			}
			if ("".equals(table.getItemString(i, "IDNO"))
					&& "N".equals(recruit.getItemString(insertrow,
							"FOREIGNER_FLG"))) {
				messageBox("第" + (i + 1) + "行身份证号为必要信息，请补全后再保存。");
				table.setSelectedRow(i);
				return false;
			}
			if ("".equals(table.getItemString(i, "SEX_CODE"))) {
				messageBox("第" + (i + 1) + "行性别为必要信息，请补全后再保存。");
				table.setSelectedRow(i);
				return false;
			}
			if ("".equals(table.getItemString(i, "BIRTH_DATE"))) {
				messageBox("第" + (i + 1) + "行请检查出生日期，如果需要重新计算，请重新输入正确的身份证号码。");
				table.setSelectedRow(i);
				return false;
			}
			if ("".equals(table.getItemString(i, "CELL_PHONE"))) {
				messageBox("第" + (i + 1) + "行联系电话为必要信息，请补全后再保存。");
				table.setSelectedRow(i);
				return false;
			}
			if (table.getItemDouble(i, "HEIGHT") == 0.0) {
				messageBox("第" + (i + 1) + "行身高为必要信息，否则无法计算BMI，请补全后再保存。");
				table.setSelectedRow(i);
				return false;
			}
			if (table.getItemDouble(i, "WEIGHT") == 0.0) {
				messageBox("第" + (i + 1) + "行体重为必要信息，否则无法计算BMI，请补全后再保存。");
				table.setSelectedRow(i);
				return false;
			}
			if (table.getItemDouble(i, "BMI") == 0.0) {
				messageBox("第" + (i + 1) + "行请检查BMI，如果需要重新计算，请重新输入身高体重。");
				table.setSelectedRow(i);
				return false;
			}
		}
		return true;
	}

	/**
	 * 查询招募信息
	 */
	public void onQuery(){
		String contractcode = this.getValueString("CONTRACT_CODE");
		String patname = this.getValueString("PAT_NAME");
		String idno = this.getValueString("ID_NO");
		String tel = this.getValueString("TEL");
		String sex = this.getValueString("SEX");
		
		String filter = " 1 = 1 ";
		if(!"".equals(patname)){
			filter += "AND PAT_NAME LIKE '%" + patname + "%' ";
		}
		if(!"".equals(contractcode)){
			filter += "AND CONTRACT_CODE = '" + contractcode + "' ";
		}
		if(!"".equals(idno)){
			filter += "AND IDNO = '" + idno + "' ";
		}
		
		if(StringUtils.isNotEmpty(tel)) {
			filter += "AND CELL_PHONE LIKE '%" + tel + "%' ";
		}
		if (StringUtils.isNotEmpty(sex)) {
			filter += "AND SEX_CODE = '" + sex + "' ";
		}
		
		recruit.onQuery();
		recruit.setFilter(filter);
		recruit.filter();
		table.setDSValue();
		
		this.setValue("TOT_COUNT", String.valueOf(recruit.rowCount()));
		
		insertrow = recruit.insertRow();
		recruit.setItem(insertrow, "CONTRACT_CODE", "本行用于新增");
		recruit.setItem(insertrow, "SEQ", null);
		recruit.onNew(insertrow);
		//显示
		table.setDSValue();
	}
	
	/**
	 * 删除表格条目
	 */
	public void onDelete(){
		int row = table.getSelectedRow();
		if(row < 0){
			messageBox("未选中记录");
			return;
		}
		
		if (this.messageBox("提示", "是否删除该行数据?", 2) == 0) {
			recruit.deleteRow(insertrow);
			recruit.deleteRow(row);
			recruit.update();
			table.setDSValue();
			recruit.resetModify();
			onQuery();
			this.messageBox("删除成功");
		}
	}
	
	/**
	 * 清空查询条件与表格内容
	 */
	public void onClear(){
		this.clearValue("CONTRACT_CODE;PAT_NAME;ID_NO;TEL;SEX;TOT_COUNT");
		initData();
	}
	
	/**
	 * 导出Excel
	 */
	public void onExport(){
		if(this.messageBox("注意！","请确认在导出Excel前执行过保存操作，避免造成导出数据与数据库不一致。",this.OK_CANCEL_OPTION)==this.CANCEL_OPTION){
			return ;
		}
		if("本行用于新增".equals(recruit.getItemData(insertrow, "CONTRACT_CODE"))){
			recruit.deleteRow(insertrow);
			recruit.update();
			table.setDSValue();
			ExportExcelUtil.getInstance().exportExcel(table, "临床招募信息表");
			return ;
		}
		//避免将最后一列打印到表格中
//		recruit.deleteRow(insertrow);
//		recruit.update();
//		table.setDSValue();
		ExportExcelUtil.getInstance().exportExcel(table, "临床招募信息表");
		initData();
	}
	
	/**
	 * 导入Excel
	 * @throws ParseException 
	 */
	public void onImport() throws ParseException {
		HashSet idno = new HashSet();
		// 手动新增行
		int addRow = insertrow;
		// 提示信息
		StringBuilder errMsg = new StringBuilder();

		// 日期格式化器
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		format.setLenient(false);

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new FileFilter() {// 过滤xls文件
					public boolean accept(File f) {
						if (f.isDirectory()) {// 忽略文件夹
							return true;
						}
						return f.getName().endsWith(".xls");
					}

					public String getDescription() {
						return ".xls";
					}
				});
		// 打开文件选择对话框，并记录状态码
		int option = fileChooser.showOpenDialog(null);
		File file = fileChooser.getSelectedFile();
		// 将Excel中的数据存入该parm中
		TParm parm = new TParm();
		// 如果打开对话框的状态码为“接受”，yes或者ok的按钮被按下
		if (option == JFileChooser.APPROVE_OPTION) {
			// 持有算选文件的对象
			try {
				// 工作簿，工作簿下包含Sheet工作表
				Workbook wb = Workbook.getWorkbook(file);
				Sheet st = wb.getSheet(0);
				// 得到表格有多少非空行
				int row = getRightRows(st);
				// 得到列数
				int column = st.getColumns();
				if (row <= 1 || column <= 0) {
					this.messageBox("excel中没有数据");
					return;
				}
				// 列号
				ArrayList<Integer> indexList = new ArrayList<Integer>();
				// 列标题
				ArrayList<String> titleList = new ArrayList<String>();
				for (int j = 0; j < column; j++) {
					// 第一行有标题，第二行为表头，getCell第一个参数便利所有列
					String cell = st.getCell(j, 1).getContents().trim();
					if (cell.indexOf("方案编号") != -1) {
						indexList.add(j);
						titleList.add("CONTRACT_CODE");
						continue;
					}
					if (cell.indexOf("方案名称") != -1) {
						indexList.add(j);
						titleList.add("CONTRACT_DESC");
						continue;
					}
					if (cell.indexOf("入组状态") != -1) {
						indexList.add(j);
						titleList.add("INGROUP_FLG");
						continue;
					}
					if (cell.indexOf("受试者名称") != -1) {
						indexList.add(j);
						titleList.add("PAT_NAME");
						continue;
					}
					if (cell.indexOf("姓名缩写") != -1) {
						indexList.add(j);
						titleList.add("PY1");
						continue;
					}
					if (cell.indexOf("性别") != -1) {
						indexList.add(j);
						titleList.add("SEX_CODE");
						continue;
					}
					if (cell.indexOf("民族") != -1) {
						indexList.add(j);
						titleList.add("SPECIES_CODE");
						continue;
					}
					if (cell.indexOf("身份证号") != -1) {
						indexList.add(j);
						titleList.add("IDNO");
						continue;
					}
					if (cell.indexOf("出生日期") != -1) {
						indexList.add(j);
						titleList.add("BIRTH_DATE");
						continue;
					}
					if (cell.indexOf("联系电话") != -1) {
						indexList.add(j);
						titleList.add("CELL_PHONE");
						continue;
					}
					if (cell.indexOf("身高") != -1) {
						indexList.add(j);
						titleList.add("HEIGHT");
						continue;
					}
					if (cell.indexOf("体重") != -1) {
						indexList.add(j);
						titleList.add("WEIGHT");
						continue;
					}
					if (cell.indexOf("BMI") != -1) {
						indexList.add(j);
						titleList.add("BMI");
						continue;
					}
					if (cell.indexOf("过敏史") != -1) {
						indexList.add(j);
						titleList.add("ALLERGY_FLG");
						continue;
					}
					if (cell.indexOf("近期是否献血") != -1) {
						indexList.add(j);
						titleList.add("BLOOD_FLG");
						continue;
					}
					if (cell.indexOf("病史") != -1) {
						indexList.add(j);
						titleList.add("MEDHISTORY_FLG");
						continue;
					}
					if (cell.indexOf("职业") != -1) {
						indexList.add(j);
						titleList.add("OCC_CODE");
						continue;
					}
					if (cell.indexOf("详细地址") != -1) {
						indexList.add(j);
						titleList.add("ADDRESS");
						continue;
					}
					if (cell.indexOf("入组时间") != -1) {
						indexList.add(j);
						titleList.add("INGROUP_DATE");
						continue;
					}
					if (cell.indexOf("出组时间") != -1) {
						indexList.add(j);
						titleList.add("OUTGROUP_DATE");
						continue;
					}
					if (cell.indexOf("未入组原因") != -1) {
						indexList.add(j);
						titleList.add("OUTGROUP_REASON");
						continue;
					}
					if (cell.indexOf("紧急联系人") != -1) {
						indexList.add(j);
						titleList.add("CONTACT_PERSON");
						continue;
					}
					if (cell.indexOf("联系方式") != -1) {
						indexList.add(j);
						titleList.add("CONTACT_TEL");
						continue;
					}
					if (cell.indexOf("报名状态") != -1) {
						indexList.add(j);
						titleList.add("SIGN_FLG");
						continue;
					}
					if (cell.indexOf("备注") != -1) {
						indexList.add(j);
						titleList.add("DESCRIPTION");
						continue;
					}
				}// 获得所有列信息
				// 列数统计
				column = indexList.size();
				// 暂时不判断Excel是否结构合理
				// 性别是由身份证号进行填写的，暂时不从的Excel中获取数据

				int count = 0;
				Cell cell;
				String cellContents = "";
				DateCell dc;
				
				// 一行一行加入excel中的数据，第一行是表格名称，第二行是表头，从第三行开始循环
				for (int i = 2; i < row; i++) {
					for (int j = 0; j < column; j++) {
						cell = st.getCell(j, i);
						if (cell.getType() == CellType.DATE) {
							dc = (DateCell) cell;
							cellContents = StringTool.getString(dc.getDate(), "yyyy/MM/dd");
						} else {
							cellContents = st.getCell(j, i).getContents();
						}
						parm.addData(titleList.get(j), cellContents);
					}
					count = parm.getCount("PAT_NAME");
					// 处理Boolean型数据
					// 过敏史
					if (parm.getData("ALLERGY_FLG", count - 1).equals("无")) {
						parm.setData("ALLERGY_FLG", count - 1, "N");
					} else if (parm.getData("ALLERGY_FLG", count - 1).equals(
							"有")) {
						parm.setData("ALLERGY_FLG", count - 1, "Y");
					}
					// 是否献血
					if (parm.getData("BLOOD_FLG", count - 1).equals("否")) {
						parm.setData("BLOOD_FLG", count - 1, "N");
					} else if (parm.getData("BLOOD_FLG", count - 1).equals("是")) {
						parm.setData("BLOOD_FLG", count - 1, "Y");
					}
					// 病史
					if (parm.getData("MEDHISTORY_FLG", count - 1).equals("无")) {
						parm.setData("MEDHISTORY_FLG", count - 1, "N");
					} else if (parm.getData("MEDHISTORY_FLG", count - 1)
							.equals("有")) {
						parm.setData("MEDHISTORY_FLG", count - 1, "Y");
					}
					// 入组状态
					if (parm.getData("INGROUP_FLG", count - 1).equals("未入组")) {
						parm.setData("INGROUP_FLG", count - 1, "N");
					} else if (parm.getData("INGROUP_FLG", count - 1).equals(
							"入组")) {
						parm.setData("INGROUP_FLG", count - 1, "Y");
					}
					// 报名状态
					if (parm.getData("SIGN_FLG", count - 1).equals("否")) {
						parm.setData("SIGN_FLG", count - 1, "N");
					} else if (parm.getData("SIGN_FLG", count - 1).equals("是")) {
						parm.setData("SIGN_FLG", count - 1, "Y");
					}

					parm.setCount(count);
				}
			} catch (BiffException e) {
				this.messageBox_("excel文件操作出错");
				e.printStackTrace();
				return;
			} catch (IOException e) {
				this.messageBox_("打开文件出错");
				e.printStackTrace();
				return;
			}
		} else {
			return;
		}
		
		int row = recruit.rowCount() - 1;
		Timestamp in = null;
		Timestamp out = null;
		TParm parmRow = new TParm();
		String contractCode = "";
		String contractDesc = "";
		String patName = "";
		String py = "";
		String idNo = "";
		String checkId = "";
		String sexCode = "";
		String birthDateFromIdNo = "";
		TParm idResult = new TParm();

		for (int i = 0; i < parm.getCount(); i++) {
			// 取出每一行的Parm
			parmRow = parm.getRow(i);

			contractCode = parmRow.getValue("CONTRACT_CODE");
			contractDesc = parmRow.getValue("CONTRACT_DESC");

			if ("".equals(contractCode)) {
				this.messageBox("错误：第" + (i + 3) + "行方案编号未填写，本行数据自动跳过，不予录入");
				errMsg.append("错误：第" + (i + 3) + "行方案编号未填写，本行数据自动跳过，不予录入\r\n");
				continue;
			}

			if ("".equals(contractDesc)) {
				this.messageBox("错误：第" + (i + 3) + "行方案名称未填写，本行数据自动跳过，不予录入");
				errMsg.append("错误：第" + (i + 3) + "行方案名称未填写，本行数据自动跳过，不予录入\r\n");
				continue;
			}

			patName = parmRow.getValue("PAT_NAME");
			if ("".equals(patName)) {
				this.messageBox("错误：第" + (i + 3) + "行姓名未填写，本行数据自动跳过，不予录入");
				errMsg.append("错误：第" + (i + 3) + "行姓名未填写，本行数据自动跳过，不予录入\r\n");
				continue;
			}

			// modify by wangb 2016/08/26 由于一期临床的特殊性，拼音由用户手动分配
			// 自动填写拼音
			py = parmRow.getData("PY1").toString();
			if (StringUtils.isEmpty(py)) {
				py = this.getAllFirstLetter(
						parmRow.getData("PAT_NAME").toString()).toUpperCase();
			}

			// 自动根据身份证号填写性别和生日
			idNo = parmRow.getValue("IDNO");
			if (StringUtils.isEmpty(idNo)) {
				this.messageBox("错误：第" + (i + 3) + "行身份证号码未填写，本行数据自动跳过，不予录入");
				errMsg.append("错误：第" + (i + 3) + "行身份证号码未填写，本行数据自动跳过，不予录入\r\n");
				continue;
			} else {
				checkId = checkID(idNo);
				if (!checkId.equals("TRUE") && !checkId.equals("FALSE")) {
					this.messageBox("错误：第" + (i + 3) + "行身份证" + idNo
							+ "校验位错误，最后一位应该为" + checkId.substring(17, 18) + "，本行数据自动跳过，不予录入");
					errMsg.append("错误：第" + (i + 3) + "行身份证" + idNo
							+ "校验位错误，最后一位应该为" + checkId.substring(17, 18)
							+ "，本行数据自动跳过，不予录入\r\n");
					continue;
				}

				if (!isId(idNo)) {
					this.messageBox("错误：第" + (i + 3) + "行身份证" + idNo + "不正确，本行数据自动跳过，不予录入");
					errMsg.append("错误：第" + (i + 3) + "行身份证" + idNo + "不正确，本行数据自动跳过，不予录入\r\n");
					continue;
				}

				sexCode = StringTool.isMaleFromID(parmRow.getData("IDNO")
						.toString());
				if (!"1".equals(sexCode) && !"2".equals(sexCode)) {// 如果身份证有问题
					this.messageBox("提示：第" + (i + 3)
							+ "行身份证号填写有问题，未能正确获得性别，本行数据自动跳过，不予录入");
					errMsg.append("提示：第" + (i + 3)
							+ "行身份证号填写有问题，未能正确获得性别，本行数据自动跳过，不予录入\r\n");
					continue;
				}
				
				// 身份证号中性别与表格中填写的性别校验
				if (("1".equals(sexCode) && !"男".equals(parmRow
						.getValue("SEX_CODE")))
						|| ("2".equals(sexCode) && !"女".equals(parmRow
								.getValue("SEX_CODE")))) {
					this.messageBox("错误：第" + (i + 3)
							+ "行填写的性别与身份证号中的性别不符，本行数据自动跳过，不予录入");
					errMsg.append("错误：第" + (i + 3)
							+ "行填写的性别与身份证号中的性别不符，本行数据自动跳过，不予录入\r\n");
					continue;
				}
				
				// 身份证号中出生日期与表格中填写的日期校验
				birthDateFromIdNo = StringTool.getBirdayFromID(
						parmRow.getValue("IDNO")).toString().substring(0, 10).replace("-", "/");
				if (!parmRow.getValue("BIRTH_DATE").equals(birthDateFromIdNo)) {
					this.messageBox("错误：第" + (i + 3)
							+ "行填写的出生日期与身份证号中的出生日期不符，本行数据自动跳过，不予录入");
					errMsg.append("错误：第" + (i + 3)
							+ "行填写的出生日期与身份证号中的出生日期不符，本行数据自动跳过，不予录入\r\n");
					continue;
				}
				
				// 验证excel中是否存在相同的身份证号
				if (!idno.add(idNo)) {
					this.messageBox("错误：第" + (i + 3) + "行身份证号" + idNo
							+ "已存在于本Excel中，本行数据自动跳过，不予录入");
					errMsg.append("错误：第" + (i + 3) + "行身份证号" + idNo
							+ "已存在于本Excel中，本行数据自动跳过，不予录入\r\n");
					continue;
				}

				String idSQL = "SELECT CONTRACT_CODE "
						+ "FROM HRM_RECRUIT WHERE IDNO = '" + idNo + "'";
				// 查询数据库中是否有该身份证号数据
				idResult = new TParm(TJDODBTool.getInstance().select(idSQL));
				
				if (idResult.getCount() > 0) {
					// 相同方案下不能存在相同身份证号的数据
					if (idResult.getValue("CONTRACT_CODE").contains(
							contractCode)) {
						this.messageBox("错误：第" + (i + 3) + "行身份证号" + idNo
								+ "在相同的方案号下已存在于数据库中，本行数据自动跳过，不予录入");
						errMsg.append("错误：第" + (i + 3) + "行身份证号" + idNo
								+ "在相同的方案号下已存在于数据库中，本行数据自动跳过，不予录入\r\n");
						continue;
					}
				}
			}

			if (StringUtils.isEmpty(parmRow.getValue("CELL_PHONE"))) {
				this.messageBox("错误：第" + (i + 3) + "行电话号码未填写，本行数据自动跳过，不予录入");
				errMsg.append("错误：第" + (i + 3) + "行电话号码未填写，本行数据自动跳过，不予录入\r\n");
				continue;
			}

			if (StringUtils.isEmpty(parmRow.getValue("HEIGHT"))
					|| parmRow.getDouble("HEIGHT") == 0.0) {
				this.messageBox("错误：第" + (i + 3) + "行身高为空或为0，本行数据自动跳过，不予录入");
				errMsg.append("错误：第" + (i + 3) + "行身高为空或为0，本行数据自动跳过，不予录入\r\n");
				continue;
			}

			if (StringUtils.isEmpty(parmRow.getValue("WEIGHT"))
					|| parmRow.getDouble("WEIGHT") == 0.0) {
				this.messageBox("错误：第" + (i + 3) + "行体重为空或为0，本行数据自动跳过，不予录入");
				errMsg.append("错误：第" + (i + 3) + "行体重为空或为0，本行数据自动跳过，不予录入\r\n");
				continue;
			}

			if (StringUtils.isEmpty(parmRow.getValue("BMI"))
					|| parmRow.getDouble("BMI") == 0.0) {
				this.messageBox("错误：第" + (i + 3) + "行BMI为空或为0，本行数据自动跳过，不予录入");
				errMsg.append("错误：第" + (i + 3) + "行BMI为空或为0，本行数据自动跳过，不予录入\r\n");
				continue;
			}

			// 要求单元格格式必须为yyyy/mm/dd否则无法转换到控件中
			try {
				in = null;
				out = null;
				if (StringUtils.isNotEmpty(parmRow.getValue("INGROUP_DATE"))) {
					in = new Timestamp(format.parse(
							parmRow.getValue("INGROUP_DATE")).getTime());
				}
				if (StringUtils.isNotEmpty(parmRow.getValue("OUTGROUP_DATE"))) {
					out = new Timestamp(format.parse(
							parmRow.getValue("OUTGROUP_DATE")).getTime());
				}
			} catch (ParseException e) {
				this.messageBox("提示：第" + (i + 3) + "行出入组时间格式不正确，本行数据自动跳过，不予录入");
				errMsg.append("提示：第" + (i + 3) + "行出入组时间格式不正确，本行数据自动跳过，不予录入\r\n");
				continue;
			}

			// 开始填充新增数据
			row = recruit.insertRow();
			insertrow = row;
			recruit.setItem(row, "CONTRACT_CODE", contractCode);
			recruit.setItem(row, "CONTRACT_DESC", contractDesc);
			recruit.setItem(row, "PAT_NAME", patName);
			recruit.setItem(row, "PY1", py);
			recruit.setItem(row, "IDNO", idNo);
			recruit.setItem(row, "SEX_CODE", sexCode);
			recruit.setItem(row, "BIRTH_DATE", birthDateFromIdNo);
			recruit.setItem(row, "SPECIES_CODE", parmRow
					.getData("SPECIES_CODE"));
			recruit.setItem(row, "CELL_PHONE", parmRow.getValue("CELL_PHONE"));
			recruit.setItem(row, "ADDRESS", parmRow.getData("ADDRESS"));
			recruit.setItem(row, "HEIGHT", parmRow.getData("HEIGHT"));
			recruit.setItem(row, "WEIGHT", parmRow.getData("WEIGHT"));
			recruit.setItem(row, "BMI", parmRow.getData("BMI"));
			recruit.setItem(row, "OCC_CODE", parmRow.getData("OCC_CODE"));
			recruit.setItem(row, "ALLERGY_FLG", parmRow.getData("ALLERGY_FLG"));
			recruit.setItem(row, "BLOOD_FLG", parmRow.getData("BLOOD_FLG"));
			recruit.setItem(row, "MEDHISTORY_FLG", parmRow
					.getData("MEDHISTORY_FLG"));
			recruit.setItem(row, "INGROUP_DATE", in);
			recruit.setItem(row, "OUTGROUP_DATE", out);
			recruit.setItem(row, "INGROUP_FLG", parmRow.getData("INGROUP_FLG"));
			recruit.setItem(row, "OUTGROUP_REASON", parmRow
					.getData("OUTGROUP_REASON"));
			recruit.setItem(row, "CONTACT_PERSON", parmRow
					.getData("CONTACT_PERSON"));
			recruit.setItem(row, "CONTACT_TEL", parmRow.getData("CONTACT_TEL"));
			recruit.setItem(row, "DESCRIPTION", parmRow.getData("DESCRIPTION"));
			recruit.setItem(row, "SIGN_FLG", parmRow.getData("SIGN_FLG"));
			recruit.setItem(row, "OPT_USER", Operator.getID());
			recruit
					.setItem(row, "OPT_DATE", SystemTool.getInstance()
							.getDate());
			recruit.setItem(row, "OPT_TERM", Operator.getIP());

			String sql = "SELECT CONTRACT_CODE , MAX(CONTRACT_DESC) AS CONTRACT_DESC , MAX(SEQ) AS SEQ "
					+ "FROM HRM_RECRUIT WHERE CONTRACT_CODE = '"
					+ contractCode
					+ "' GROUP BY CONTRACT_CODE";
			TParm contract_info = new TParm(TJDODBTool.getInstance()
					.select(sql));
			if (seq.containsKey(contractCode)) {
				seq.put(contractCode, seq.get(contractCode) + 1);
			} else {
				if (contract_info.getCount() < 0) {
					seq.put(contractCode, 1);
				} else {
					seq.put(contractCode, contract_info.getInt("SEQ", 0) + 1);
				}
			}

			recruit.setItem(row, "SEQ", seq.get(contractCode));
		}
		
		StringBuilder log = new StringBuilder();
		log.append("-------招募库导入输出日志Start--------\r\n");
		log.append("-------" + file.getName() + "--------\r\n");
		String err = errMsg.toString();
		if (!StringUtil.isNullString(err)) {
			err = log.toString() + err + "-------招募库导入输出日志End----------\r\n";
			String fileName = "临床招募表导入日志（" + file.getName() + "）" + ".txt";
			javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView
					.getFileSystemView();
			try {
				FileTool.setString(fsv.getHomeDirectory() + "\\" + fileName,
						err);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		recruit.deleteRow(addRow);
		//新增一行用于新增
		insertrow = recruit.insertRow();
		recruit.setItem(insertrow, "CONTRACT_CODE", "本行用于新增");
		recruit.setItem(insertrow, "SEQ", null);
		recruit.onNew(insertrow);
		table.setDSValue();
	}
	
	private int getRightRows(Sheet sheet) {
		int rsCols = sheet.getColumns(); // 列数
        int rsRows = sheet.getRows(); // 行数
        int nullCellNum;
        int afterRows = rsRows;
        for (int i = 1; i < rsRows; i++) { // 统计行中为空的单元格数
            nullCellNum = 0;
            for (int j = 0; j < rsCols; j++) {
                String val = sheet.getCell(j, i).getContents();
                val = StringUtils.trimToEmpty(val);
                if (StringUtils.isBlank(val)) nullCellNum++;
            }
            if (nullCellNum >= rsCols) { // 如果nullCellNum大于或等于总的列数
                afterRows--; // 行数减一
            }
        }
        return afterRows;
	}

	/**
	 * 自动填充与数据校验
	 */
	public boolean onTableChange(TTableNode tNode) {
		int row = tNode.getRow();
		int column = tNode.getColumn();
		String colName = table.getParmMap(column);
		Object value = tNode.getValue();
		Object oldValue = tNode.getOldValue();

		if ("本行用于新增".equals(recruit.getItemString(row, "CONTRACT_CODE"))
				&& !"CONTRACT_CODE".equals(colName)) {
			messageBox("请先填写方案编号，否则该条数据无效");
			return true;
		}

		// 试验代码自动填写试验名称,自动计算序列号,按照正确顺序填写，且不会更改自动填写的内容
		if ("CONTRACT_CODE".equals(colName)) {
			String contract_code = value.toString().trim();
			String sql = "SELECT CONTRACT_CODE , MAX(CONTRACT_DESC) AS CONTRACT_DESC , MAX(SEQ) AS SEQ FROM HRM_RECRUIT WHERE CONTRACT_CODE = '"
					+ contract_code + "' GROUP BY CONTRACT_CODE";
			TParm contract_info = new TParm(TJDODBTool.getInstance()
					.select(sql));
			if (contract_info.getCount() < 0) {
				recruit.setItem(row, "CONTRACT_DESC", "");
				recruit.setItem(row, "SEQ", "1");
			} else {
				recruit.setItem(row, "CONTRACT_DESC", contract_info.getValue(
						"CONTRACT_DESC", 0));
				recruit.setItem(row, "SEQ", Integer.parseInt(contract_info
						.getValue("SEQ", 0)) + 1);
			}
			table.setDSValue();
		}

		// 自动填写姓名拼音
		if ("PAT_NAME".equals(colName)) {
			String py = this.getAllFirstLetter(value.toString()).toUpperCase();
			recruit.setItem(row, "PY1", py);
			return false;
		}

		// 自动计算BMI
		if ("HEIGHT".equals(colName) || "WEIGHT".equals(colName)) {
			double height = Double.parseDouble(value.toString());
			double weight = Double.parseDouble(value.toString());
			if ("HEIGHT".equals(colName)) {
				height = Double.parseDouble(value.toString());
				weight = recruit.getItemDouble(row, "WEIGHT");
			}
			if ("WEIGHT".equals(colName)) {
				height = recruit.getItemDouble(row, "HEIGHT");
				weight = Double.parseDouble(value.toString());
			}
			if (height == 0) {
				messageBox("请先填写身高");
				return true;
			}
			// 把输入的厘米换算成米
			height /= 100;
			double bmi = weight / (height * height);
			DecimalFormat df = new DecimalFormat("#.0");
			recruit.setItem(row, "BMI", df.format(bmi));
			table.setDSValue();
			return false;
		}

		// 身份证信息验证
		// 身份证18位验证
		if ("IDNO".equals(colName)) {

			String strValue = ((String) value).trim();
			if (strValue.equals("")) {
				return true;
			}
			String isForeigner = recruit.getItemString(row, "FOREIGNER_FLG");
			String checkid = checkID(strValue);
			if (!checkid.equals("TRUE") && !checkid.equals("FALSE")) {
				this
						.messageBox_("身份证校验位错误，最后一位应该为"
								+ checkid.substring(17, 18));
				return false;
			}
			if (!isId(strValue)
					&& ("N".equalsIgnoreCase(isForeigner) || StringUtil
							.isNullString(isForeigner))) {
				this.messageBox_("身份证不正确");
				return true;
			}
			int rowCount = recruit.rowCount();
			// add by wangb 2017/3/21 相同方案下不允许人员重复，不同方案允许人员重复
			for (int i = 0; i < rowCount; i++) {
				if (!recruit.isActive(i))
					continue;
				if (i == row)
					continue;
				if (recruit.getItemString(i, "FOREIGNER_FLG").equals(
						isForeigner)
						&& recruit.getItemString(i, "IDNO").equals(strValue)
						&& recruit.getItemString(i, "CONTRACT_CODE").equals(
								recruit.getItemString(row, "CONTRACT_CODE"))) {
					this.messageBox("方案【"
							+ recruit.getItemString(row, "CONTRACT_DESC")
							+ "】中该身份证号已存在");
					return true;
				}
			}
			if (isId(strValue)) {
				String sexCode = StringTool.isMaleFromID(strValue);
				// 自动填充性别和出生日期
				recruit.setItem(row, "SEX_CODE", sexCode);
				recruit.setItem(row, "BIRTH_DATE", StringTool
						.getBirdayFromID(strValue));
			}

			table.setDSValue();
			tNode.getTable().grabFocus();
			return false;
		}
		String buffer = recruit.isFilter() ? recruit.FILTER : recruit.PRIMARY;
		// if (TCM_Transform.isNull(recruit.getItemData(row, "IDNO", buffer))
		// && !TCM_Transform.getBoolean(recruit.getItemData(row,
		// "FOREIGNER_FLG", buffer))) {
		// this.messageBox_("请先输入身份证号");
		// return false;
		// }
		return false;
	}
	
	/**
	 * 验证出生日期
	 * @param idcard
	 * @return
	 */
	private boolean isId(String idcard) {
        if ((idcard == null) || (idcard.length() == 0)) {
            return false;
        }
        if (idcard.length() == 15) {
            idcard = uptoeighteen(idcard);
        }
        if (idcard.length() != 18) {
            return false;
        }
        String birthday = idcard.substring(6, 14);
        String regexString =
                "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})"
                        + "(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))"
                        + "|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)";
        if (birthday.matches(regexString)) {
            if (StringTool.isId(idcard)) {
                return true;
            }
        }
        return false;
    }
	
	/**
	 * 如果身份证号为15自动提升到18位
	 * @param fifteencardid
	 * @return
	 */
	private String uptoeighteen(String fifteencardid) {
        String eightcardid = fifteencardid.substring(0, 6);
        eightcardid = eightcardid + "19";
        eightcardid = eightcardid + fifteencardid.substring(6, 15);
        eightcardid = eightcardid + getIDVerify(eightcardid);
        return eightcardid;
    }

	/**
	 * 整体校验
	 * @param idcard
	 * @return
	 */
	private String checkID(String idcard) {
        if (idcard.length() != 15 && idcard.length() != 18) {
            return "FALSE";
        }
        if (idcard.length() == 15) {
            if (StringTool.isId(idcard)) {
                return "TRUE";
            } else {
                return "FALSE";
            }
        }
        String date = idcard.substring(6, 14);
        String regexString =
                "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})"
                        + "(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))"
                        + "|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)";
        if (date.matches(regexString)) {
            if (!StringTool.isId(idcard)) {
                String verrifyChar = getIDVerify(idcard);
                if (verrifyChar.equals("")) {
                    return "FALSE";
                }
                idcard = idcard.substring(0, 17) + verrifyChar;
                return idcard;
            } else return "TRUE";
        } else return "FALSE";
    }

	/**
	 * 计算最后一位
	 * @param eightcardid
	 * @return
	 */
	private String getIDVerify(String eightcardid) {
        int[] wi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
        int[] vi = {1, 0, 88, 9, 8, 7, 6, 5, 4, 3, 2 };
        int[] ai = new int[18];
        int remaining = 0;
        if (eightcardid.length() == 18) {
            eightcardid = eightcardid.substring(0, 17);
        }
        if (eightcardid.length() == 17) {
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                String k = eightcardid.substring(i, i + 1);
                try {
                    ai[i] = Integer.parseInt(k);
                }
                catch (Exception e) {
                    return "";
                }
            }
            for (int i = 0; i < 17; i++) {
                sum += wi[i] * ai[i];
            }
            remaining = sum % 11;
        }
        return remaining == 2 ? "X" : String.valueOf(vi[remaining]);
    }

	/**
	 * 项目编号过滤试验编号
	 * @param projectcode
	 * @return;
	 */
	public TParm getContractParm(String projectcode){
		TParm result = new TParm();
		if(StringUtil.isNullString(projectcode)){
			return result;
		}
		String sql = 
			"SELECT CONTRACT_CODE AS ID,MAX(CONTRACT_DESC) AS NAME FROM HRM_RECRUIT WHERE PROJECT_CODE = '" 
			+ projectcode + "' GROUP BY CONTRACT_CODE";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/** 
     * 取得给定汉字串的首字母串,即声母串 
     * @param str 给定汉字串 
     * @return 声母串 
     */  
    public String getAllFirstLetter(String str) {  
        if (str == null || str.trim().length() == 0) {  
            return "";  
        }  
  
        String _str = "";  
        for (int i = 0; i < str.length(); i++) {  
            _str = _str + this.getFirstLetter(str.substring(i, i + 1));  
        }  
  
        return _str;  
    }  
  
    /** 
     * 取得给定汉字的首字母,即声母 
     * @param chinese 给定的汉字 
     * @return 给定汉字的声母 
     */  
    public String getFirstLetter(String chinese) {  
        if (chinese == null || chinese.trim().length() == 0) {  
            return "";  
        }  
        chinese = this.conversionStr(chinese, "GB2312", "ISO8859-1");  
  
        if (chinese.length() > 1) // 判断是不是汉字  
        {  
            int li_SectorCode = (int) chinese.charAt(0); // 汉字区码  
            int li_PositionCode = (int) chinese.charAt(1); // 汉字位码  
            li_SectorCode = li_SectorCode - 160;  
            li_PositionCode = li_PositionCode - 160;  
            int li_SecPosCode = li_SectorCode * 100 + li_PositionCode; // 汉字区位码  
            if (li_SecPosCode > 1600 && li_SecPosCode < 5590) {  
                for (int i = 0; i < 23; i++) {  
                    if (li_SecPosCode >= li_SecPosValue[i]  
                            && li_SecPosCode < li_SecPosValue[i + 1]) {  
                        chinese = lc_FirstLetter[i];  
                        break;  
                    }  
                }  
            } else // 非汉字字符,如图形符号或ASCII码  
            {  
                chinese = this.conversionStr(chinese, "ISO8859-1", "GB2312");  
                chinese = chinese.substring(0, 1);  
            }  
        }  
  
        return chinese;  
    }  
  
    /** 
     * 字符串编码转换 
     * @param str 要转换编码的字符串 
     * @param charsetName 原来的编码 
     * @param toCharsetName 转换后的编码 
     * @return 经过编码转换后的字符串 
     */  
    private String conversionStr(String str, String charsetName,String toCharsetName) {  
        try {  
            str = new String(str.getBytes(charsetName), toCharsetName);  
        } catch (UnsupportedEncodingException ex) {  
            System.out.println("字符串编码转换异常：" + ex.getMessage());  
        }  
        return str;  
    }
}
