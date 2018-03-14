package com.javahis.ui.sys;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import jdo.clp.CLPCliProjectTool;
import jdo.sys.ClinicalSortTool;
import jdo.sys.Operator;
import jdo.sys.SYSSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.ui.sys.orm.tools.PinyinTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * title:临床研究分类
 * </p>
 * <p>
 * ContentDiscription:临床研究分类
 * </p>
 * <p>
 * Company:javahis
 * </p>
 * <p>
 * Time:20160519
 * </p>
 * 
 * @author wukai
 * 
 */
public class ClinicalSortControl extends TControl {

	/**
	 * 表格名称
	 */
	private static final String TABLE = "TABLE";
	/**
	 * 动作名称
	 */
	private String action = "save";
	/**
	 * 临床类别表格
	 */
	private TTable table;

	private ClinicalSortTool tool;

	@Override
	public void onInit() {
		super.onInit();
		tool = ClinicalSortTool.getNewInstance();
		initPage();
	}

	/**
	 * 初始化页面
	 */
	private void initPage() {
		// 获取SYS_CLINICALSORT中所有的数据并加到table中
		table = getTable(TABLE);
		table.removeRowAll();
		table.setParmValue(tool.onQuery(new TParm()));
		TDataStore tds = new TDataStore();
		tds.setSQL(SYSSQL.getSYSClinicalSQL());
		tds.retrieve();
		// 初始化顺序号，想获取表中最大的顺序号+1 delete置灰
		int seq = getMaxSeq(tds, "SEQ", tds.isFilter() ? tds.FILTER
				: tds.PRIMARY);
		this.setValue("SEQ", seq);
		((TMenuItem) getComponent("delete")).setEnabled(false);
	}

	/**
	 * 得到最大的编号 +1
	 * 
	 * @param dataStore
	 *            TDataStore
	 * @param columnName
	 *            String
	 * @return String
	 */
	public int getMaxSeq(TDataStore dataStore, String columnName,
			String dbBuffer) {
		if (dataStore == null)
			return 0;
		// 保存数据量
		int count = dataStore.getBuffer(dbBuffer).getCount();
		// 保存最大号
		int max = 0;
		for (int i = 0; i < count; i++) {
			int value = TCM_Transform.getInt(dataStore.getItemData(i,
					columnName, dbBuffer));
			// 保存最大值
			if (max < value) {
				max = value;
				continue;
			}
		}
		// 最大号加1
		max++;
		return max;
	}

	/**
	 * 获取页面表格
	 * 
	 * @param table
	 * @return
	 */
	private TTable getTable(String table) {
		return (TTable) getComponent(table);
	}

	/**
	 * 更新或者插入新数据 这块就用最原始SQL语句插入的方式吧
	 */
	public void onSave() {
		int row = 0;
		if ("save".equals(action)) { // 保存
			TTextField code = getTextField("CLINICAL_CODE");
			boolean flg = code.isEnabled(); // 能够被获取到
			TParm parm = new TParm();
			parm.setData("CLINICAL_CODE", getValueString("CLINICAL_CODE"));
			parm.setData("CLINICAL_DESC", getValueString("CLINICAL_DESC"));
			parm.setData("PY1", this.getText("PY1"));
			parm.setData("PY2", this.getText("PY2"));
			parm.setData("SEQ", this.getText("SEQ"));
			parm.setData("DESCRIPTION", this.getText("DESCRIPTION"));
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
			parm.setData("OPT_TERM", Operator.getIP());
			if (!flg) { // 更新数据
				if (!checkData()) {
					return;
				}
				if (!tool.onUpdate(parm)) {
					this.messageBox("更新失败，请检查数据合理性");
					return;
				}
				row = table.getSelectedRow();
			} else { // 保存新数据
				if (!checkData()) {
					return;
				}
				if (!tool.onSave(parm)) {

					this.messageBox("保存失败，编码不可重复");
					return;
				}
				row = table.addRow();
			}
			table.setItem(row, "CLINICAL_CODE", parm.getData("CLINICAL_CODE"));
			table.setItem(row, "CLINICAL_DESC", parm.getData("CLINICAL_DESC"));
			table.setItem(row, "PY1", parm.getData("PY1"));
			table.setItem(row, "PY2", parm.getData("PY2"));
			table.setItem(row, "SEQ", parm.getData("SEQ"));
			table.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
			table.setItem(row, "OPT_USER", parm.getData("OPT_USER"));
			table.setItem(row, "OPT_DATE", parm.getData("OPT_DATE"));
			table.setItem(row, "OPT_TERM", parm.getData("OPT_TERM"));
			this.messageBox("保存成功");
		}
		onClear();
	}

	/**
	 * 查询like
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String code = getText("CLINICAL_CODE");
		if (!StringUtils.isEmpty(code)) {
			parm.setData("CLINICAL_CODE", "%" + code + "%");
		}
		String desc = getText("CLINICAL_DESC");
		if (!StringUtils.isEmpty(desc)) {
			parm.setData("CLINICAL_DESC", "%" + desc + "%");
		}
		String py1 = getText("PY1");
		if (!StringUtils.isEmpty(py1)) {
			parm.setData("PY1", "%" + py1 + "%");
		}
		String py2 = getText("PY2");
		if (!StringUtils.isEmpty(py2)) {
			parm.setData("PY2", "%" + py2 + "%");
		}
		String description = getText("DESCRIPTION");
		if (!StringUtils.isEmpty(description)) {
			parm.setData("DESCRIPTION", "%" + description + "%");
		}
		TParm res = tool.onQuery(parm);
		// this.messageBox(res.toString());
		if(res == null || res.getCount() <=0) {
			this.messageBox("暂无此查询数据！");
		}
		table.setParmValue(res);
	}

	/**
	 * 检查数据是否为空
	 * 
	 * @return
	 */
	private boolean checkData() {
		if (StringUtils.isEmpty(getValueString("CLINICAL_CODE"))) {
			this.messageBox("编码不能为空");
			return false;
		}

		if (StringUtils.isEmpty(getValueString("CLINICAL_DESC"))) {
			this.messageBox("编码名称不能为空");
			return false;
		}
		return true;
	}

	/**
	 * 清空内容，删除置灰
	 */
	public void onClear() {
		// 清空画面内容
		String clearString = "CLINICAL_CODE;CLINICAL_DESC;PY1;"
				+ "PY2;SEQ;DESCRIPTION";
		clearValue(clearString);
		// 产生序号,重新获取所有的数据
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(SYSSQL.getSYSClinicalSQL());
		dataStore.retrieve();
		int seq = getMaxSeq(dataStore, "SEQ",
				dataStore.isFilter() ? dataStore.FILTER : dataStore.PRIMARY);
		setValue("SEQ", seq);

		table.setSelectionMode(0);
		getTextField("CLINICAL_CODE").setEnabled(true);
		getTextField("CLINICAL_DESC").setEnabled(true);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "save";
	}

	/**
	 * 获取TTextField
	 * 
	 * @param tag
	 *            : 标签
	 * @return
	 */
	private TTextField getTextField(String tag) {
		return (TTextField) getComponent(tag);
	}

	/**
	 * 删除数据 alter by wukai on 20160530
	 */
	public void onDelete() {
		boolean flg = getTextField("CLINICAL_CODE").isEnabled();
		if (!flg) { // 选中一个可以删除
			int row = table.getSelectedRow();
			String code = table.getParmValue().getValue("CLINICAL_CODE", row);
			// 判断项目数据库中是否已经有了这个code
			TParm p = CLPCliProjectTool.getNewInstance().onQuery(new TParm());
			for (int i = 0; i < p.getCount(); i++) {
				Object o = p.getData("CLASSIFY_CODE", i);
				if(code.equals(o)) {
					this.messageBox("临床项目中已经使用了该词条，不可删除");
					return;
				}
			}
			int res = this.messageBox("提示", "确认删除？", YES_NO_OPTION);
			if (res == OK_OPTION) {
				if (row != -1) {
					TParm parm = new TParm();
					parm.setData("CLINICAL_CODE", code);
					if (tool.onDelete(parm)) {
						table.removeRow(row);
						onClear();
						this.messageBox("删除成功");
					}
				}
			} else {
				return;
			}
		} else {
			this.messageBox("请选择一条进行删除");
		}

	}

	/**
	 * Table点击事件 将所选表格内容填充到界面中
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row != -1) {
			TParm parm = table.getParmValue().getRow(row);
			String linkNames = "CLINICAL_CODE;CLINICAL_DESC;PY1;"
					+ "PY2;SEQ;DESCRIPTION";
			this.setValueForParm(linkNames, parm);
			getTextField("CLINICAL_CODE").setEnabled(false);
			getTextField("CLINICAL_DESC").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
			action = "save";
		}
	}

	/**
	 * 编码名称输入完后
	 */
	public void onDescFinish() {
		// this.messageBox("name finish");
		String desc = this.getValueString("CLINICAL_DESC");
		if (desc == null) {
			return;
		} else {
			String py1 = PinyinTool.getPinyin(desc);
			this.setValue("PY1", py1);
		}
	}
}
