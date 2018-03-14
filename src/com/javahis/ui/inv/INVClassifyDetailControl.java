package com.javahis.ui.inv;

import jdo.inv.INVClassifyDetailTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ���ʷ�����ϸ��
 * </p>
 * 
 * <p>
 * Description: ���ʷ�����ϸ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author donglt 2016 03 11
 * @version 1.0
 */


public class INVClassifyDetailControl extends TControl {
		
		TTable table;
		int selectRow = -1;

		/**
		 * ��ʼ������
		 */
		public void onInit() {
			
			table = (TTable) this.getComponent("TABLE");// ��ȡ���е�����
			
		}
		
	
		
		/**
		 * ��ѯ����
		 */
		public void onQuery() {
			

			TParm parm = new TParm();
			
			// ���������
			if (getValueString("CATEGORY_CODE").length() > 0)
				parm.setData("CATEGORY_CODE", getValueString("CATEGORY_CODE"));
			
			//����Ӧ����
			if (getValueString("SUP_CODE").length() > 0)
				parm.setData("SUP_CODE", getValueString("SUP_CODE"));
			System.out.println(""+parm);
  
			TParm result = INVClassifyDetailTool.getInstance().onQuery(parm);
			//�鵽�����ݷŵ�ǰ̨
//			this.messageBox("%%%%%5"+result);
			table.setParmValue(result);
			// ����У��
			if (result.getErrCode() < 0) {
				this.messageBox("��ѯʧ��");
				return;  
			}
			if (result.getCount() <= 0) {
				this.messageBox("δ��ѯ������");
				return;
			}


		}



		/**
		 *�������
		 */
		public void onClear() {

			this.clearValue("SUP_CODE;CATEGORY_CODE");

			table.setParmValue(new TParm());
		}
		
		
		/**
		 *��������
		 */
		public void onExport() {
			if (table.getRowCount() <= 0) {
				messageBox("�޵�������");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table, "���ʷ�����ϸ��");
		}

		/**
		 * ��ӡ����
		 */
		public void onPrint() {
			TParm parm = new TParm();
			parm.setData("TITLE", "TEXT", "�����Ϻ�|������ҽԺ���ʷ�����ϸ");
			parm.setData("WZLB","TEXT", this.getText("CATEGORY_CODE"));
			parm.setData("GYCJ", "TEXT", this.getText("SUP_CODE"));

			parm.setData("NAME", "TEXT", "�Ʊ���: " +Operator.getName());

			TParm resultParm = table.getParmValue();
//			this.messageBox(""+resultParm);
//			
			resultParm.addData("SYSTEM", "COLUMNS", "INV_CODE");// 1
			resultParm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");// 2
			resultParm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");// 3
//			resultParm.addData("SYSTEM", "COLUMNS", "SUP_CHN_DESC");// 4
			resultParm.addData("SYSTEM", "COLUMNS", "MAN_CHN_DESC");// 5
			resultParm.addData("SYSTEM", "COLUMNS", "CONTRACT_PRICE");// 6
			resultParm.addData("SYSTEM", "COLUMNS", "COST_PRICE");// 7
			
//			parm.setData("TABLE", table.getParmValue().getData());
			
			parm.setData("TABLE", resultParm.getData());
		
			this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVClassifyDetail.jhw",parm);
		}

		public void onDeptSelect() {
			this.clearValue("DEPT_CODE");

		}
	   
		

	}

	


