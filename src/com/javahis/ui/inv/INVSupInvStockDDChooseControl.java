package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import jdo.inv.INVSQL;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 供应室出库选择序号管理的一般物资</p>
 *
 * <p>Description: 供应室出库选择序号管理的一般物资</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.3.8
 * @version 1.0
 */
public class INVSupInvStockDDChooseControl
    extends TControl {
    /**
     * 表
     */
    private TTable table;

    private String org_code = "";

    private String inv_code = "";

    private String inv_chn_desc = "";

    private String stock_unit = "";

    private double qty = 0;

    public INVSupInvStockDDChooseControl() {
    }

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        table = (TTable) callFunction("UI|TABLE|getThis");
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        org_code = parm.getValue("ORG_CODE");
        inv_code = parm.getValue("INV_CODE");
        inv_chn_desc = parm.getValue("INV_CHN_DESC");
        qty = parm.getDouble("QTY");
        stock_unit = parm.getValue("STOCK_UNIT");
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INVSQL.getInvSupInvChoose(org_code, inv_code)));
        table.setParmValue(result);
        this.setValue("QTY", qty);
        this.setValue("INV_CHN_DESC", inv_chn_desc);
    }

    /**
     * 返回方法
     */
    public void onReturn() {
        table.acceptText();
        TParm result = table.getParmValue();
        TParm resultParm = new TParm();
        for (int i = result.getCount("INVSEQ_NO") - 1; i >= 0; i--) {
            if ("Y".equals(table.getItemString(i, "SELECT_FLG"))) {
                resultParm.addData("INVSEQ_NO",
                                   result.getRow(i).getInt("INVSEQ_NO"));
                resultParm.addData("ORG_CODE",
                                   result.getRow(i).getValue("ORG_CODE"));
                resultParm.addData("COST_PRICE",
                                   result.getRow(i).getDouble("COST_PRICE"));
                resultParm.addData("BATCH_SEQ",
                                   result.getRow(i).getInt("BATCH_SEQ"));
                resultParm.addData("BATCH_NO",
                                   result.getRow(i).getValue("BATCH_NO"));
                resultParm.addData("VALID_DATE",
                                   result.getRow(i).getData("VALID_DATE"));
                resultParm.addData("QTY", 1);
                resultParm.addData("INV_CODE", inv_code);
                resultParm.addData("STOCK_UNIT", stock_unit);
                resultParm.addData("INV_CHN_DESC",inv_chn_desc);
            }
        }
        double return_qty = resultParm.getCount("INVSEQ_NO");
        if (return_qty <= 0) {
            this.messageBox("没有选择物资");
            return;
        }
        if (return_qty > qty) {
            this.messageBox("选择数量超过请领数量");
            return;
        }
        setReturnValue(resultParm);
        this.closeWindow();
    }

    /**
	 * 扫描条码
	 */
	public void onScream() {
		
		
		this.messageBox("进入扫描");
	}
    
    
}
