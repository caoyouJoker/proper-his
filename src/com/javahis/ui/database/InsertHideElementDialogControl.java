package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.div.MVList;
import com.dongyang.tui.text.div.MV;
import com.dongyang.ui.TWord;
import com.dongyang.data.TParm;
import java.util.Vector;
import com.dongyang.ui.TTable;
import com.dongyang.tui.text.div.DIV;
import com.dongyang.tui.text.div.VText;
import java.awt.Color;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TComboBox;

/**
 * <p>Title: 插入隐藏元素</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2011.06.22</p>
 *
 * <p>Company: javahis</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class InsertHideElementDialogControl
    extends TControl {

    /**
     * 隐藏图层名称
     */
    public static final String UNVISIBLE_MV = "UNVISITABLE_ATTR";

    //当前图层列表；
    private MVList mvList;

    /**
     * WORD对象
     */
    private TWord word;
    /**
     * 隐藏图层
     */
    private MV hiddenMv;
    /**
     * 隐藏元素表
     */
    private TTable hiddenElmTable;

    /**
     * 是否是宏
     */
    private TCheckBox chkMacroFlg;

    /**
     * 自由文本
     */
    private TTextField txtWord;
    /**
     * 元素分为控件;
     */
    private TTextFormat tfElementCate;
    /**
     * 元素数据控件;
     */
    private TTextFormat tfElementData;

    private TComboBox  cmbMacro;

    public InsertHideElementDialogControl() {
    }

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        TParm parm = (TParm) getParameter();
        word = (TWord) parm.getData("theWord", 0);
        //.getPageManager().getMVList().get(i).getName()
        mvList = (MVList) word.getPageManager().getMVList();
        //是否存在隐藏属性的图层
        // MV unvisibleMV=mvList.get("UNVISIBLE_MV");
        //System.out.println("mvList size++" + mvList.size());
        chkMacroFlg = (TCheckBox) getComponent("chkMacroFlg");

        hiddenElmTable = (TTable) getComponent("Table");

        txtWord = (TTextField) getComponent("txtWord");

        tfElementCate = (TTextFormat) getComponent("ELEMENT_CATE");
        tfElementData = (TTextFormat) getComponent("ELEMENT_DATA");
        cmbMacro=(TComboBox)getComponent("cmb_macro");

        //注册表格单击事件;
        /**callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");**/

        boolean isExist = false;
        for (int i = 0; i < mvList.size(); i++) {
            //System.out.println("mv name:" + mvList.get(i).getName());
            if (mvList.get(i).getName().equals(UNVISIBLE_MV)) {
                //存在
                hiddenMv = mvList.get(i);
                isExist = true;
                break;
            }
        }
        //有，则列出对应的隐藏属性
        if (isExist) {
            onClickedVL();

            //DIV div = hiddenMv.get(1);
            //div.openProperty();

        }
        else {
            //无，则创建图层；
            hiddenMv = new MV();
            hiddenMv.setName(UNVISIBLE_MV);
            hiddenMv.setVisible(false);
            mvList.add(hiddenMv);

        }


        /**addEventListener("TABLE->" + TTableEvent.CLICKED,
                         "onTableClicked");**/
    }
    /**
     * 点击
     */
    public void onTableClicked(){
        //this.messageBox("come in");
         int row = hiddenElmTable.getSelectedRow();
         //this.messageBox("select elem"+hiddenElmTable.getParmValue().getData( "ELEMENT_CATE",row));
         tfElementCate.setValue(hiddenElmTable.getParmValue().getData( "ELEMENT_CATE",row));
         tfElementData.setValue(hiddenElmTable.getParmValue().getData( "ELEMENT_DATA",row));
         macroQuery();

         //图层元素
         VText text = (VText)hiddenMv.get(row);
         //是宏
         if(text.isTextT()){
             chkMacroFlg.setSelected(true);
             txtWord.setText("");
             txtWord.setEnabled(false);
             cmbMacro.setValue(text.getMicroName());

         }else{
             chkMacroFlg.setSelected(false);
             txtWord.setEnabled(true);
             txtWord.setText(text.getText());
         }

         //chkMacroFlg

    }

    /**
     *选择是否是宏
     */
    public void onClickedChkMacro() {
        if (chkMacroFlg.isSelected()) {
            txtWord.setText("");
            txtWord.setEnabled(false);
        }
        else {
            txtWord.setEnabled(true);
        }

    }

    /**
     * 保存元素
     */
    public void onSave() {
        //校验
        if (tfElementCate.getValue() == null ||
            tfElementCate.getValue().equals("")) {
            this.messageBox("请选择元素分类!");
            return;
        }

        if (tfElementData.getValue() == null ||
            tfElementData.getValue().equals("")) {
            this.messageBox("请选择元素数据!");
            return;
        }
        if (!chkMacroFlg.isSelected()) {
            if (txtWord.getText().equals("")) {
                this.messageBox("不是宏，请输入文本数据!");
                return;
            }
        //是宏，请选择宏
        }else{
            if(cmbMacro.isEnabled()&&cmbMacro.getValue().equals("")){
                this.messageBox("请选择宏!");
                return;
            }

        }

        int row = hiddenElmTable.getSelectedRow();
        //this.messageBox("row++" + row);
        //新增
        if (row == -1) {
            //可有重名宏元素
           //this.messageBox("新增元素."+(String)tfElementData.getValue());
            VText text = hiddenMv.addText(100, 100, new Color(0, 0, 0));
            //名子
            text.setName( (String) tfElementData.getValue());
            text.setGroupName((String)tfElementCate.getValue());

            //this.messageBox("组名:"+(String)tfElementCate.getValue());
            //this.messageBox("宏名是:"+cmbMacro.getValue());
            //文本
            if (chkMacroFlg.isSelected()) {
                //带入
                text.setTextT(true);
                //设置宏名;

                text.setMicroName(cmbMacro.getValue());

            }
            else {
                text.setTextT(false);
                text.setText(txtWord.getValue());
            }
            text.setVisible(false);
        //修改;
        }
        else {
            VText text = (VText)hiddenMv.get(row);
            //名子
            text.setName( (String) tfElementData.getValue());
            text.setGroupName((String)tfElementCate.getValue());
            //文本
            if (chkMacroFlg.isSelected()) {
                //this.messageBox("chkMacroFlg===="+chkMacroFlg);
                //带入
                text.setTextT(true);
                //设置宏名;
                text.setMicroName(cmbMacro.getValue());
            }
            else {
                text.setTextT(false);
                text.setText(txtWord.getValue());
            }
            text.setVisible(false);


        }
        //刷新表格；
        onClickedVL();
        this.onClear();
        this.messageBox("保存成功!");
        //返回word对象到主窗口;
    }

    /**
     * 删除元素
     */
    public void onDelete() {

        int row = hiddenElmTable.getSelectedRow();
        if (row == -1) {
            return;
        }
        DIV div = hiddenMv.get(row);
        if (messageBox("提示信息", "是否删除隐藏元素" + div.getName() + "?", YES_NO_OPTION) ==
            1) {
            return;
        }
        hiddenMv.remove(div);
        hiddenElmTable.removeRow(row);
        if (hiddenElmTable.getRowCount() > 0) {
            hiddenElmTable.setSelectedRow(row >= hiddenElmTable.getRowCount() ?
                                          hiddenElmTable.getRowCount() - 1 :
                                          row);
        }
        mvList.update();
        div.DC();

    }

    /**
     * 清空
     */
    public void onClear() {
        hiddenElmTable.clearSelection();
        tfElementCate.setValue("");
        tfElementData.setValue("");
        chkMacroFlg.setSelected(false);
        txtWord.setEnabled(true);
        txtWord.setText("");
        cmbMacro.setValue("");

    }

    /**
     * 隐藏图层，元素数据
     */
    public void onClickedVL() {

        TParm parm = new TParm();
        TParm elementParm = new TParm();
        if (hiddenMv.size() == 0) {
            parm.setData("ID", new Vector());
            parm.setData("SHOW", new Vector());
            parm.setData("NAME", new Vector());
            parm.setData("TYPE", new Vector());
            parm.setCount(0);
        }
        else {
            for (int i = 0; i < hiddenMv.size(); i++) {
                DIV div = hiddenMv.get(i);
                parm.addData("ID", i);
                parm.addData("SHOW", div.isVisible());
                //this.messageBox("通过名子取：" + div.getName());
                parm.addData("NAME", div.getName());
                parm.addData("TYPE", div.getType());
                //从库中取对应的隐藏元素；
                TParm theElem=this.dataElementByName(div.getName());
                //ID;ELEMENT_CATE;ELEMENT_DATA;DEFINE_DESC
                elementParm.addData("ID",i);
                elementParm.addData("ELEMENT_CATE",theElem.getData("GROUP_CODE",0));
                elementParm.addData("ELEMENT_DATA",theElem.getData("DATA_CODE",0));
                elementParm.addData("DEFINE_DESC",theElem.getData("DEFINE_DESC",0));

            }
            parm.setCount(hiddenMv.size());
        }
        hiddenElmTable.setParmValue(elementParm);
    }

    /**
     *宏查询
     */
    public void macroQuery() {
        String elemCate = (String) tfElementCate.getValue();
        String elemData = (String) tfElementData.getValue();
        //通过EMR_MICRO_CONVERT表取宏名称；
       cmbMacro.setSQL("SELECT MICRO_NAME FROM EMR_MICRO_CONVERT WHERE DATA_ELEMENT_CODE='" + elemData + "'" );
       cmbMacro.retrieve();
        //如果大于0
        //this.messageBox("==count=="+cmbMacro.getDataStore().rowCount());
        //有值，则宏chk,固定值不可用;
        //不是宏,
        if(cmbMacro.getDataStore().rowCount()==0){
            chkMacroFlg.setSelected(false);
            cmbMacro.setValue("");
            cmbMacro.setEnabled(false);
            txtWord.setEnabled(true);
        //有多个宏的情况
        }else if(cmbMacro.getDataStore().rowCount()>1){
            chkMacroFlg.setSelected(true);
            cmbMacro.setEnabled(true);
            cmbMacro.setValue("");
            txtWord.setEnabled(false);
        //只有一个值情况
        }else{
            chkMacroFlg.setSelected(true);
            cmbMacro.setEnabled(true);
            cmbMacro.setValue("");
            cmbMacro.setSelectedIndex(1);
            txtWord.setEnabled(false);

        }



    }


    /**
     * 通过name得到数据元素
     * @param name String
     * @return TParm
     */
    private TParm dataElementByName(String name){
         StringBuffer sb = new StringBuffer("SELECT GROUP_CODE,DATA_CODE,DEFINE_DESC FROM EMR_CLINICAL_DATAGROUP WHERE 1=1");
         sb.append(" AND DATA_CODE='"+name+"'");
         //System.out.println("======sql======"+sb.toString());
        TParm parm = new TParm(this.getDBTool().select(sb.toString()));
        return parm;
    }

    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    private TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }


}
