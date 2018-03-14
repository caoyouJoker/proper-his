package com.javahis.ui.sys;

import java.awt.event.KeyEvent;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;

/**
 *
 * <p>Title: SYS Fee 下拉选择框</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.1.16
 * @version 1.0
 */
public class SYSFeePopupForSpcControl
    extends TControl {
    private String oldText = "";
    private TTable table;
    /**
     * 门诊医生站主界面5个页签的处方类型
     */
    private int rxType;
    /**
     * 门诊医生站组套分为2个页签，除了中药的所有（检验检查主项、西成药、处置、管制药品）
     */
    private String W_group;

    /**
     * 住院
     */
    private String orTypeOdi;
    /**
     * 指定医嘱类型
     */
    private String inCat1Type;
    /**
     * 过敏类型
     */
    private String allergyType;
    //健检所用医嘱类型，应全为集合医嘱，但是LIS/RIS/OTH等不计
    private String hrmType;
    private int page = 0;
    private int index = 0;
    /**
     * 初始化
     */
    public void onInit() {
    	 super.onInit();
         table = (TTable) callFunction("UI|TABLE|getThis");
         //,ORDER_CODE ASC
         table.setSort("SEQ DESC,ORDER_CODE ASC");
         table.sort();
         table.filter();
         
         callFunction("UI|EDIT|addEventListener", TTextFieldEvent.KEY_RELEASED, this,
                      "onKeyReleased");
         callFunction("UI|EDIT|addEventListener",
                      "EDIT->" + TKeyListener.KEY_PRESSED, this, "onKeyPressed");
         table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                                "onDoubleClicked");
         initParamenter();
    }

    /**
     * 初始化参数
     */
    public void initParamenter() {
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        setEditText(text);
        rxType = parm.getInt("RX_TYPE");
        hrmType=parm.getValue("HRM_TYPE");
        //住院
        Object objOidOrder = parm.getData("ODI_ORDER_TYPE");
        if (objOidOrder != null) {
            orTypeOdi = "" + objOidOrder;
        }
        //组套
        Object wGroup = parm.getData("W_GROUP");
        if (wGroup != null) {
            W_group = "" + wGroup;
        }
        // 指定医嘱类型
        inCat1Type = parm.getValue("CAT1_TYPE");
        // 过敏史用医嘱类型
        allergyType = parm.getValue("ALLERGY_TYPE");
    }

    /**
     * 重新加载
     */
    public void onInitReset() {
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        String oldText = (String) callFunction("UI|EDIT|getText");
        if (oldText.equals(text)) {
            return;
        }

        setEditText(text);
    }

    /**
     * 设置输入文字
     * @param s String
     */
    public void setEditText(String s) {
        page = 0;
        index = 0;
        setValue("L_PAGE","" + (page + 1));
        callFunction("UI|EDIT|setText", s);
        int x = s.length();
        callFunction("UI|EDIT|select", x, x);
        onKeyReleased(s);
    }

    /**
     * 按键事件
     * @param s String
     */
    public void onKeyReleased(String s) {
        page = 0;
        index = 0;
        setValue("L_PAGE","" + (page + 1));
        s = s.toUpperCase();
        if (oldText.equals(s))
            return;
        oldText = s;
        table.filterObject(this, "filter");
        int count = table.getRowCount();
        if (count > 0)
            table.setSelectedRow(0);
    }

    /**
     * 过滤方法
     * @param parm TParm
     * @param row int
     * @return boolean
     */
    public boolean filter(TParm parm, int row) {
    	
    	//System.out.println("=====parm222222====="+parm);
        boolean result =
            (parm.getValue("ORDER_CODE", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("ORDER_DESC", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("TRADE_ENG_DESC", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("GOODS_DESC", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("GOODS_PYCODE", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("ALIAS_DESC", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("ALIAS_PYCODE", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("NHI_FEE_DESC", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("PY1", row).toUpperCase().indexOf(oldText) != -1 ||
             parm.getValue("PY2", row).toUpperCase().indexOf(oldText) != -1) &&
            ("Y".equals(parm.getValue("ACTIVE_FLG", row).toUpperCase()));
        String region_code = Operator.getRegion();
        if (!"".equals(region_code)) {
            result = result && (parm.getData("REGION_CODE", row) == null ||
                                "".equals(parm.getValue("REGION_CODE", row)) ||
                                region_code.equals(parm.getValue("REGION_CODE", row).
                                                   toUpperCase()));
        }

        String cat1Code = parm.getValue("ORDER_CAT1_CODE", row);
        String cat1Type = parm.getValue("CAT1_TYPE", row);
        if (!StringUtil.isNullString(orTypeOdi)) {
            //全部
            if ("A".equals(orTypeOdi)) {
                result = result;
            }
            //检验检查
            if ("B".equals(orTypeOdi)) {
                result = result &&
                    ("LIS".equalsIgnoreCase(cat1Type) ||
                     "RIS".equalsIgnoreCase(cat1Type));
            }
            //诊疗项目
            if ("C".equals(orTypeOdi)) {
                result = result &&
                    ("TRT".equalsIgnoreCase(cat1Type) ||
                     "PLN".equalsIgnoreCase(cat1Type));
            }
            //西药
            if ("D".equals(orTypeOdi)) {
                result = result && "PHA_W".equalsIgnoreCase(cat1Code);
            }
            //中成
            if ("E".equals(orTypeOdi)) {
                result = result && "PHA_C".equalsIgnoreCase(cat1Code);
            }
            //中草
            if ("F".equals(orTypeOdi)) {
                result = result && "PHA_G".equalsIgnoreCase(cat1Code);
            }
            //管制药品
            if ("G".equals(orTypeOdi)) {
                result = result &&
                    (parm.getValue("LCS_CLASS_CODE", row).length() != 0);
            }
        }
        else if (rxType > 0) {
            switch (rxType) {
                //西成药
                case 1:

                    result = result &&
                        (cat1Code.equalsIgnoreCase("PHA_W") ||
                         cat1Code.equalsIgnoreCase("PHA_C"));
                    break;
                    //毒麻药
                case 2:
                    result = result &&
                        (TypeTool.getBoolean(parm.getValue("CTRL_FLG", row)));
                    break;
                case 3:

                    //中药todo
                    result = result && (cat1Code.equalsIgnoreCase("PHA_G"));
                    break;
                    //处置
                case 4:
                    result = result && (cat1Type.equalsIgnoreCase("TRT") || cat1Type.equalsIgnoreCase("PLN")||cat1Type.equalsIgnoreCase("OTH"));
                    break;
                    //检验检查主项
                case 5:
                    String setmain = parm.getValue("ORDERSET_FLG", row);
                    result = result &&
                        ("Y".equalsIgnoreCase(setmain) &&
                         (cat1Type.equalsIgnoreCase("LIS") ||
                          cat1Type.equalsIgnoreCase("RIS")));
                    break;
                    //细相
                case 6:
                    String setMain = parm.getValue("ORDERSET_FLG", row);
                    result = result &&
                        ("N".equalsIgnoreCase(setMain) &&
                         cat1Code.equalsIgnoreCase("EXA"));
            }
        }
        //组套
        else if (!StringUtil.isNullString(W_group)) {

            result = result && ("PHA_W".equalsIgnoreCase(cat1Code) ||
                                "PHA_C".equalsIgnoreCase(cat1Code) ||
                                "TRT".equalsIgnoreCase(cat1Type) ||
                                "PLN".equalsIgnoreCase(cat1Type));
        }
        //指定医嘱类型
        else if (!StringUtil.isNullString(inCat1Type)) {
            //System.out.println("parm---"+parm);
            result = result && (cat1Type.equalsIgnoreCase(inCat1Type));
        }
        //指定过敏医嘱类型
        else if (!StringUtil.isNullString(allergyType)) {
            result = result && ("PHA".equalsIgnoreCase(cat1Type));
        }
        else if(!StringUtil.isNullString(hrmType)){
            result=(result && (StringTool.getBoolean(parm.getValue("ORDERSET_FLG",row)))) ||
            	   (result && "PHA_W".equals(cat1Code)) || (result && "MAT".equals(cat1Code));
        }
        result = result && ("N".equalsIgnoreCase(parm.getValue("ORDERSET_FLG",row))) ;
//        && (("MAT".equalsIgnoreCase(parm.getValue("ORDER_CAT1_CODE",row))) 
//        || ("PHA".equalsIgnoreCase(parm.getValue("CAT1_TYPE",row))));

        
        if(result)
        {
            index++;
            if(index < (page) * 19 || index > (page + 1) * 19)
                return false;
        }
        
        //this.messageBox("result"+result);
        //System.out.println("=========result============="+result);
    
        return result;
    }

    /**
     * 按键事件
     * @param e KeyEvent
     */
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            callFunction("UI|setVisible", false);
            return;
        }
        int count = table.getRowCount();
        if (count <= 0)
            return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                int row = table.getSelectedRow() - 1;
                if (row < 0)
                    row = 0;
                table.getTable().grabFocus();
                table.setSelectedRow(row);
                break;
            case KeyEvent.VK_DOWN:
                row = table.getSelectedRow() + 1;
                if (row >= count)
                    row = count - 1;
                table.getTable().grabFocus();
                table.setSelectedRow(row);
                break;
            case KeyEvent.VK_ENTER:
                callFunction("UI|setVisible", false);
                onSelected();
                break;
        }
    }

    /**
     * 行双击事件
     * @param row int
     */
    public void onDoubleClicked(int row) {
        if (row < 0)
            return;
        callFunction("UI|setVisible", false);
        onSelected();
    }

    /**
     * 选中
     */
    public void onSelected() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        TDataStore dataStore = table.getDataStore();
        String orderCode = dataStore.getItemString(row,"ORDER_CODE");
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            "SELECT * FROM SYS_FEE WHERE ORDER_CODE='" + orderCode +
            "' AND ACTIVE_FLG = 'Y' AND  ORDERSET_FLG = 'N'"));
        //System.out.println("====parm======"+parm);
        if (parm.getErrCode() < 0 || parm.getCount() <= 0)
            return;
        parm = parm.getRow(0);
        setReturnValue(parm);
    }

    /**
     * 更新本地 ," ORDER BY SEQ DESC, ORDER_CODE"
     */
    public void onResetDW() {
        TIOM_Database.removeLocalTable("SYS_FEE", false);
        table.retrieve();
    }

    /**
     * 重新下载全部(," ORDER BY SEQ DESC, ORDER_CODE")
     */
    public void onResetFile() {
        TIOM_Database.removeLocalTable("SYS_FEE", true);
        table.retrieve();
    }
    public void onUp()
    {
        page--;
        index = 0;
        if(page < 0)
        {
            page = 0;
            return;
        }
        setValue("L_PAGE","" + (page + 1));
        table.filterObject(this, "filter");
        int count = table.getRowCount();
        if (count > 0)
            table.setSelectedRow(0);
    }
    public void onDown()
    {
        page ++;
        index = 0;
        setValue("L_PAGE","" + (page + 1));
        table.filterObject(this, "filter");
        int count = table.getRowCount();
        if (count > 0)
            table.setSelectedRow(0);
    }

    /**
     * 右击MENU弹出事件
     * @param tableName
     */
    public void onShowPopMenu() {
        TTable table = (TTable)this.getComponent("TABLE");
        table.setPopupMenuSyntax(
            "显示集合医嘱细相|Show LIS/RIS Detail Items,onOrderSetShow");
    }

    /**
     * 右击MENU显示集合医嘱事件
     */
    public void onOrderSetShow(){
        TTable table = (TTable)this.getComponent("TABLE");
        int row = table.getSelectedRow();
        String order_code = table.getDataStore().getItemString(row, "ORDER_CODE");
        String sql = "SELECT A.ORDER_DESC, A.SPECIFICATION, DOSAGE_QTY, "
            + " UNIT_CODE AS MEDI_UNIT, OWN_PRICE, OWN_PRICE * DOSAGE_QTY "
            + " AS OWN_AMT, EXEC_DEPT_CODE, OPTITEM_CODE, INSPAY_TYPE "
            + " FROM SYS_FEE A, SYS_ORDERSETDETAIL B "
            + " WHERE A.ORDER_CODE = B.ORDERSET_CODE "
            + " AND B.ORDERSET_CODE = '" + order_code + "' "
            + " ORDER BY B.ORDERSET_CODE, B.ORDER_CODE";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", result);

//        TParm parm = new TParm();
//        TTextField textfield = (TTextField)this.getComponent("EDIT");
//        parm.setData("ORDER_CODE", textfield.getValue());
//        // 给table上的新text增加sys_fee弹出窗口
//        textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
//            "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
    }

}
