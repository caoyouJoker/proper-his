package com.javahis.ui.bms;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.dongyang.control.TControl;
import com.javahis.system.combo.TComboBMSBldsubcat;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import jdo.bms.BMSBloodTool;

/**
 * <p>
 * Title: ����ѯ
 * </p>
 *
 * <p>
 * Description: ����ѯ
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
public class BMSStockQueryControl
    extends TControl {
    public BMSStockQueryControl() {
    }
	private int sortColumn = -1;
	private boolean ascending = false;
	private Compare compare = new Compare();
    /**
     * ��ʼ������
     */
    public void onInit() {
        // ȡ�ô������
    	//��ͷ����
		addListener((TTable)this.getComponent("TABLE"));
        Object obj = getParameter();
        if (obj != null) {
            TParm parm = (TParm) obj;
            if (!"".equals(parm.getValue("BLOOD_NO"))) {
                this.setValue("BLOOD_NO", parm.getValue("BLOOD_NO"));
            }
            if (!"".equals(parm.getValue("BLD_CODE"))) {
                this.setValue("BLD_CODE", parm.getValue("BLD_CODE"));
            }
            if (!"".equals(parm.getValue("BLD_TYPE"))) {
                this.setValue("BLD_TYPE", parm.getValue("BLD_TYPE"));
            }
            if (!"".equals(parm.getValue("SUBCAT_CODE"))) {
                this.setValue("SUBCAT_CODE", parm.getValue("SUBCAT_CODE"));
            }
            this.onQuery();
        }
      
    }
    /**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				 //System.out.println("+i+"+i);
				 //System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = table.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				 //System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				//System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
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
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		((TTable)this.getComponent("TABLE")).setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}
	
	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}
    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("BLOOD_NO"))) {
            parm.setData("BLOOD_NO", this.getValue("BLOOD_NO"));
        }
        if (!"".equals(this.getValueString("BLD_CODE"))) {
            parm.setData("BLD_CODE", this.getValue("BLD_CODE"));
        }
        if (!"".equals(this.getValueString("BLD_TYPE"))) {
            parm.setData("BLD_TYPE", this.getValue("BLD_TYPE"));
        }
        if (!"".equals(this.getValueString("SUBCAT_CODE"))) {
            parm.setData("SUBCAT_CODE", this.getValue("SUBCAT_CODE"));
        }
        if (!"".equals(this.getValueString("END_DATE"))) {
            parm.setData("END_DATE", this.getValue("END_DATE"));
        }
        TParm result = BMSBloodTool.getInstance().queryBloodStockOrderBy(parm);
        //modify by lim 2012/04/27 begin
        for (int i = 0; i < result.getCount(); i++) {
			result.addData("FLG", "N") ;
		}
        //modify by lim 2012/04/27 end
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        this.getTable("TABLE").setParmValue(result);
    }

    /**
     * ���ط���
     */
    public void onReturn() {
//        TTable table = this.getTable("TABLE");
//        if (table.getSelectedRow() < 0) {
//            this.messageBox("û��ѡ����");
//            return;
//        }
//        setReturnValue(table.getParmValue().getRow(table.getSelectedRow()));
//        this.closeWindow();
    	
        TTable table = this.getTable("TABLE");
        table.acceptText();
        boolean bool = false ;
        List<TParm> returnVal = new ArrayList<TParm>() ;
        for (int i = 0; i < table.getRowCount(); i++) {
			String flg = table.getItemString(i, "FLG") ;
			if("Y".equals(flg)){
				bool = true ;
				returnVal.add(table.getParmValue().getRow(i)) ;
			}
		}
        if (!bool) {
            this.messageBox("û��ѡ����");
            return;
        }
        setReturnValue(returnVal);
        this.closeWindow();    	
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "BLOOD_NO;BLD_CODE;BLD_TYPE;SUBCAT_CODE;END_DATE";
        this.clearValue(clearStr);
        getTable("TABLE").removeRowAll();
    }

    /**
     * ���ѪƷ
     */
    public void onChangeBld() {
        String bld_code = getComboBox("BLD_CODE").getSelectedID();
        ( (TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).setBldCode(
            bld_code);
        ( (TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).onQuery();
    }

    /**
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
}
