package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import com.dongyang.ui.TMenuItem;
import com.dongyang.data.TParm;
import jdo.clp.ClpVarMoncatTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import jdo.sys.SYSHzpyTool;
import java.util.Date;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TDataStore;
import java.sql.Timestamp;
import jdo.clp.ClpchkTypeTool;
import java.util.ArrayList;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.event.TKeyListener;
import java.awt.Component;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.datawindow.DataStore;
import com.dongyang.ui.TTextFormat;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title: 变异字典</p>
 *
 * <p>Description: 变异字典</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 2011-05-02
 * @version 1.0
 */
public class ClpVarMoncatControl extends TControl {
    public ClpVarMoncatControl() {
    }
    //区域
    TComboBox REGION_CODE;
    //第一组中的文本控件
    //变异类别代码、序号、变异类别中文说明、变异类别英文说明,变异类别拼音,注记,备注
    TTextField MONCAT_CODE, SEQ, MONCAT_CHN_DESC,MONCAT_ENG_DESC, PY1,PY2,DESCRIPTION;
    //表格
    TTable TABLE_VARMONCAT,TABLE_VARIANCE;
    //第二个表格中的数据
    TParm varianceParm;
    //判断是否删除主档数据 true:删除、false:不删除
    boolean flg =true;
    //获得第一个表格中点击的单元行数
    int selectRow=-1;
     //获得第二个表格中点击的单元行数
    int selectRowTwo=-1;
    //第二个表中的列
    String variance="MONCAT_CODE,VARIANCE_CODE,SEQ,PY2,VARIANCE_CHN_DESC,PY1,VARIANCE_ENG_DESC,CLNCPATH_CHN_DESC,DESCRIPTION,CLNCPATH_CODE,REGION_CODE,OPT_USER,OPT_TERM,OPT_DATE";
    String variances[]=null;
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        onPage();
        variances=variance.split(",");
        //添加第二个表格table值改变事件
        this.addEventListener("TABLE_VARIANCE->" + TTableEvent.CHANGE_VALUE,
                              "onTABLEVARIANCEChargeValue");
        //适用临床路径项目列添加监听事件
        callFunction("UI|TABLE_VARIANCE|addEventListener",
                     TTableEvent.CREATE_EDIT_COMPONENT, this,
                     "onCreateVARIANCE");
        // 添加子表table单击事件
       callFunction("UI|TABLE_VARIANCE|addEventListener",
                    "TABLE_VARIANCE->" + TTableEvent.CLICKED, this,
                    "onVarianceClicked");
        getRegionShow();
        onQuery();
    }
    public void getRegionShow(){
        //区域初始值
      REGION_CODE.setValue(Operator.getRegion());
      //之后要使用现在.8上没有后台类会出现错误
      //权限添加
//       TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
//       cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
//               getValueString("REGION_CODE")));


    }
    /**
     * 子表单击事件
     * 判断是否删除子表信息
     * @param row int
     */
    public void onVarianceClicked(int row){
        flg=false;// 只删除子表信息
        ((TMenuItem)this.getComponent("delete")).setEnabled(true);
        selectRowTwo = row;
        //移除主档表格所选择行
        TABLE_VARMONCAT.clearSelection();
        if (row < 0)
            return;
    }
    /**
     * TABLE_VARIANCE表格监听事件：值改变事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTABLEVARIANCEChargeValue(Object obj){
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        //如果改变的节点数据和原来的数据相同就不改任何数据
        if (node.getValue().equals(node.getOldValue()))
            return false;
        //拿到table上的parmmap的列名
        String columnName = node.getTable().getParmMap(node.
            getColumn());
        //得到改变的行
        int row = node.getRow();
        //拿到当前改变后的数据
        String value = "" + node.getValue();
        //如果是名称改变了拼音1自动带出,并且原因说明不能为空
        if ("VARIANCE_CHN_DESC".equals(columnName)) {
            if (value.equals("") || value == null) {
                messageBox("原因说明不能为空!");
                return true;
            }
//            //默认变异主键
//            table.setItem(row, "VARIANCE_CHN_DESC", value);
            //变异类别中文列赋值拼音简称
            String py = SYSHzpyTool.getInstance().charToCode(value);
            varianceParm.setData("PY1", row, py);
            varianceParm.setData("VARIANCE_CHN_DESC", row, value);
        }
        //子表主键
        if("VARIANCE_CODE".equals(columnName)){
            if (value.equals("") || value == null) {
                messageBox("原因代码不能为空!");
                return true;
            }
            varianceParm.setData("VARIANCE_CODE", row, value);
        }
        //注记
        if ("PY2".equals(columnName)) {
            varianceParm.setData("PY2", row, value);
        }
        //原因英文说明
        if ("VARIANCE_ENG_DESC".equals(columnName)) {
            varianceParm.setData("VARIANCE_ENG_DESC", row, value);
        }
        //备注
        if ("DESCRIPTION".equals(columnName)) {
            varianceParm.setData("DESCRIPTION", row, value);
        }
        TABLE_VARIANCE.setParmValue(varianceParm);
        return false;

    }
    /**
     * 初始化页面
     */
    public void onPage() {
        //删除按钮初始值不可以点击
        ((TMenuItem)this.getComponent("delete")).setEnabled(false);
        //第一组中的数据
        //表格
        TABLE_VARMONCAT=(TTable)getComponent("TABLE_VARMONCAT");
        TABLE_VARIANCE=(TTable)getComponent("TABLE_VARIANCE");
        //文本框
        //变异类别代码
        MONCAT_CODE=(TTextField)getComponent("MONCAT_CODE");
        //序号
        SEQ=(TTextField)getComponent("SEQ");
        //变异类别中文说明
        MONCAT_CHN_DESC=(TTextField)getComponent("MONCAT_CHN_DESC");
        //变异类别拼音
        PY1=(TTextField)getComponent("PY1");
        //变异类别英文说明
        MONCAT_ENG_DESC=(TTextField)getComponent("MONCAT_ENG_DESC");
        //注记
        PY2=(TTextField)getComponent("PY2");
        //备注
        DESCRIPTION=(TTextField)getComponent("DESCRIPTION");
        //区域
        REGION_CODE=(TComboBox)getComponent("REGION_CODE");
        //第二个表格中的数据
        varianceParm=new TParm();
    }
    /**
     * 第二个表格添加监听显示适用临床路径项目中的数据
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateVARIANCE(Component com, int row, int column) {

        if (row < 0) {
            return;
        }
        if (column != 7) {
            return;
        }
        selectRow=row;//第一个表格当前行
        TTextField textFilter = (TTextField) com;
        //初始化
        textFilter.onInit();
        //打开界面
        textFilter.setPopupMenuParameter("BSCINFO",getConfigParm().newConfig("%ROOT%\\config\\sys\\ClpBscInfoPopupUI.x"));
        //定义接受返回值方法
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");

    }
    /**
     * 接受返回值方法
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TABLE_VARIANCE.acceptText();
        // 判断对象是否为空和是否为TParm类型
        if (obj == null && ! (obj instanceof TParm)) {
            return;
        }
        // 类型转换成TParm
        TParm result = (TParm) obj;

        //将当前列赋值CLNCPATH_CHN_DESC
        TABLE_VARIANCE.setValueAt(result.getValue(
                "CLNCPATH_CHN_DESC"), selectRow, 7);
        varianceParm.setData("CLNCPATH_CODE", selectRow,
                             result.getValue("CLNCPATH_CODE"));
        varianceParm.setData("CLNCPATH_CHN_DESC", selectRow,
                             result.getValue(
                                     "CLNCPATH_CHN_DESC"));
        //显示数据
     //  TABLE_VARIANCE.setParmValue(varianceParm);
      }

    /**
     * 查询方法
     */
    public void onQuery(){
        String regionCode = "";
        String moncatCode = "";
        //区域条件
        if (null != REGION_CODE.getValue() && !REGION_CODE.getValue().equals("")){
            regionCode = " AND A.REGION_CODE='"+REGION_CODE.getValue()+"'";
        }
        //变异类别代码
        if (null != MONCAT_CODE.getValue() && !MONCAT_CODE.getValue().equals(""))
            moncatCode = " AND A.MONCAT_CODE LIKE '%" + MONCAT_CODE.getValue().trim() +
                         "%'";
        //主档表sql语句
        String sql = "SELECT A.REGION_CODE ,A.MONCAT_CODE,A.PY1 " +
                     ",A.MONCAT_CHN_DESC, A.SEQ ,A.PY2,A.DESCRIPTION,A.MONCAT_ENG_DESC,B.REGION_CHN_DESC FROM  CLP_VARMONCAT A ,SYS_REGION B WHERE "
                     + "A.REGION_CODE=B.REGION_CODE(+)" + regionCode +
                     moncatCode;
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        //填充第一个表格数值
        TABLE_VARMONCAT.setParmValue(result);
    }
    /**
     * 清空方法
     */
    public void onClear(){
        //删除按钮
        ((TMenuItem)this.getComponent("delete")).setEnabled(false);
        //第一个表格的主键：变异类别代码
        MONCAT_CODE.setEnabled(true);
        this.clearValue("MONCAT_CODE;MONCAT_CHN_DESC;PY1;REGION_CODE;SEQ;PY2;MONCAT_ENG_DESC;DESCRIPTION");
        getRegionShow();
        selectRow = -1;
        selectRowTwo = -1;

        //表格
        TABLE_VARMONCAT.setParmValue(new TParm());
        TABLE_VARIANCE.setParmValue(new TParm());
    }
    /**
     * 添加和修改方法
     */
    public boolean onSave(){
        TTable tablevarmoncat=(TTable)this.getComponent("TABLE_VARMONCAT");
        tablevarmoncat.acceptText();
        TTable tablevariance=(TTable)this.getComponent("TABLE_VARIANCE");
        tablevariance.acceptText();
        //判断是否添加主档数据
        if(MONCAT_CODE.isEnabled()){
            if (null == REGION_CODE.getValue() ||
                "".equals(REGION_CODE.getValue())) {
                this.messageBox("请输入区域");
                return false;
            }
            if (null == MONCAT_CODE.getValue() ||
                "".equals(MONCAT_CODE.getValue())) {
                this.messageBox("请输入变异类别代码");
                return false;
            }
        }
        TParm parm=new TParm();
        //变异类别代码
        parm.setData("MONCAT_CODE", MONCAT_CODE.getValue());
        //区域
        parm.setData("REGION_CODE", REGION_CODE.getValue());
        //序号
        parm.setData("SEQ", SEQ.getValue());
        //变异类别中文说明
        parm.setData("MONCAT_CHN_DESC", MONCAT_CHN_DESC.getValue());
        //备注
        parm.setData("DESCRIPTION", DESCRIPTION.getValue());
        //变异类别拼音
        parm.setData("PY1", PY1.getValue());
        //注记
        parm.setData("PY2", PY2.getValue());
        //变异类别英文说明
        parm.setData("MONCAT_ENG_DESC", MONCAT_ENG_DESC.getValue());
        //操作时间
        Timestamp date = StringTool.getTimestamp(new Date());
        parm.setData("OPT_USER", Operator.getID()); //操作人员
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP()); //操作终端
        //查询主档表是否存在此条数据
        TParm result =ClpVarMoncatTool.getInstance().selectIsExist(parm);
        //不存在执行添加方法
        if(null==result||result.getCount("REGION_CODE")<=0){
            InsertRow(parm);
        }else
            updateRow(parm);
        //第二个表格操作
        for (int i = 0; i < varianceParm.getCount("MONCAT_CODE"); i++) {
            varianceParm.setData("OPT_USER", i, Operator.getID());
            varianceParm.setData("OPT_TERM", i, Operator.getIP());
            varianceParm.setData("OPT_DATE", i, date);
        }
        //实现多数据添加修改
        TParm resultVariance = TIOM_AppServer.executeAction("action.clp.ClpVarMoncatAction",
             "saveVariance", varianceParm);
         if (resultVariance.getErrCode() < 0) {
             err(resultVariance.getErrName() + " " + resultVariance.getErrText());
             this.messageBox("E0001");
             return false;
         }
         else
             this.messageBox("P0001");
       this.onClear();
       this.onQuery();
       return true;
    }
    /**
    *  添加方法主档表格
    */
   public void InsertRow(TParm parm) {
       TParm result =ClpVarMoncatTool.getInstance().saveClpVarMoncat(parm);
       if (null != result) {
           this.messageBox("变异字典主档表添加成功");
           //table上加入新增的数据显示
           callFunction("UI|TABLE_VARMONCAT|addRow", parm,
                        "MONCAT_CODE;SEQ;PY2;MONCAT_CHN_DESC;PY1;MONCAT_ENG_DESC;DESCRIPTION;REGION_CODE");

       } else
           this.messageBox("变异字典主档表添加失败");

   }
   /**
    * 修改方法主档表格
    * @param parm TParm
    */
   public void updateRow(TParm parm) {
       //获得焦点
      // int row = (Integer) callFunction("UI|TABLE_VARIANCE|getSelectedRow");
       if (!flg)
           this.messageBox("变异字典主档表无更新");
       else {
           TParm result = ClpVarMoncatTool.getInstance().updateClpVarMoncat(
                   parm);
           if (null != result) {
               this.messageBox("变异字典主档表修改成功");
           } else {
               this.messageBox("变异字典主档表修改失败");
           }
       }
   }
    /**
     * 删除方法
     */
    public void onDelete() {
        TParm parm = new TParm();
        //获得区域
        String regionCode = this.getValueString("REGION_CODE");
        //获得变异类别代码
        String moncatCode = this.getValueString("MONCAT_CODE");
        if (null != regionCode && !"".equals(regionCode))
            parm.setData("REGION_CODE", regionCode);
        if (null != moncatCode && !"".equals(moncatCode))
            parm.setData("MONCAT_CODE", moncatCode);
        //子表主键原因代码
        String varianceCode = varianceParm.getValue("VARIANCE_CODE",selectRowTwo);
        String delMoncatSql = ""; //主档
        String delVatianceSql = ""; //子表
        ArrayList list = new ArrayList();
        //删除操作
        //flg=true 可以删除主档信息
        if (flg) {
            if (this.messageBox("询问", "是否删除变异字典主档表中的数据", 2) == 0) {
                //删除主档字典
                delMoncatSql = "DELETE FROM CLP_VARMONCAT WHERE MONCAT_CODE='" +
                               moncatCode + "' AND REGION_CODE='" + regionCode +
                               "'";
                //删除子表字典
                delVatianceSql = "DELETE FROM CLP_VARIANCE WHERE MONCAT_CODE='" +
                                 moncatCode + "'";
                list.add(delMoncatSql);
                list.add(delVatianceSql);
                String[] allSql = (String[]) list.toArray(new String[] {});
                TParm delMoncatParm = new TParm(TJDODBTool.getInstance().update(
                        allSql));
                if (delMoncatParm.getErrCode() < 0) {
                    err(delMoncatParm.getErrName() + " " +
                        delMoncatParm.getErrText());
                    return;
                }
            }
        } else {
             moncatCode = varianceParm.getValue( "MONCAT_CODE",selectRowTwo);
            //删除子表字典
            delVatianceSql =
                    "DELETE FROM CLP_VARIANCE WHERE MONCAT_CODE='" +
                    moncatCode + "' AND VARIANCE_CODE='" + varianceCode +
                    "'";
            TParm delVarianceParm = new TParm(TJDODBTool.getInstance().
                                              update(
                    delVatianceSql));
            if (delVarianceParm.getErrCode() < 0) {
                err(delVarianceParm.getErrName() + " " +
                    delVarianceParm.getErrText());
                return;
            }
        }
        this.messageBox("删除成功");
        this.onClear();
        this.onQuery();
    }
    /**
     * 主档table监听事件
     * @param row int
     */
    public void onTableVarMoncatClicked() {
        //删除按钮初始值不可以点击
        ((TMenuItem)this.getComponent("delete")).setEnabled(true);
        //获得当前行
        int row = TABLE_VARMONCAT.getSelectedRow();
        flg = true;//点击主档表可以删除操作
        if (row < 0)
            return;
        //修改操作
        MONCAT_CODE.setEnabled(false);
        REGION_CODE.setEnabled(false);
        TParm data = (TParm) callFunction("UI|TABLE_VARMONCAT|getParmValue");
        setValueForParm(
                "MONCAT_CODE;MONCAT_CHN_DESC;PY1;REGION_CODE;PY2;MONCAT_ENG_DESC;DESCRIPTION",
                data, row);
        this.setValue("SEQ", data.getValue("SEQ", row));
        //第二个表格显示数据
        String sql = "SELECT 'N' AS FLG, A.MONCAT_CODE,A.VARIANCE_CODE,A.SEQ,A.PY2,A.VARIANCE_CHN_DESC,A.PY1,A.VARIANCE_ENG_DESC,B.CLNCPATH_CHN_DESC,"+
                     "A.DESCRIPTION,A.CLNCPATH_CODE,A.REGION_CODE,A.OPT_USER,A.OPT_TERM,A.OPT_DATE FROM CLP_VARIANCE A,CLP_BSCINFO B WHERE A.CLNCPATH_CODE =B.CLNCPATH_CODE(+)";
        if (!getValue("MONCAT_CODE").toString().equals(""))
            sql += " AND A.MONCAT_CODE='" + getValue("MONCAT_CODE").toString() +
                    "'";
        if (!getValueString("REGION_CODE").equals(""))
            sql += " AND A.REGION_CODE='" + getValue("REGION_CODE").toString() +
                    "'";
        //第二个表格中查询值
        varianceParm=new TParm(TJDODBTool.getInstance().select(sql));
        TABLE_VARIANCE.setParmValue(varianceParm);
        //获得表格中的所有行
        int roomAllRow = TABLE_VARIANCE.getParmValue().getCount("MONCAT_CODE");
        //判断是否需要添加新行 varianceeCode有值添加一行
        String varianceeCode = TABLE_VARIANCE.getParmValue().getValue("VARIANCE_CODE",
            roomAllRow - 1);
        //如果存在值或者没有数据都将添加一行数据
        if ( (varianceeCode != null && varianceeCode.length() != 0) ||
            roomAllRow == -1) {
            this.onNew();
        }
    }

    /**
     * 新增方法第二个表格中添加一条新数据
     */
    public void onNew() {
        //获得最大序号
        int seq=getMaxSeq(TABLE_VARIANCE.getParmValue(),"SEQ");
        // 新添加数据的顺序编号
        if (getValue("MONCAT_CODE").toString().length() > 0) {
            // 获得当前的表格数据
          // TParm oldParm= TABLE_VARIANCE.getParmValue();
            // 当前选中的行
            varianceParm.addData("FLG", "Y");
            for (int i = 0; i < variances.length; i++) {
                // 变异类别代码
                if (variances[i].equals("MONCAT_CODE"))
                    varianceParm.addData(variances[i], getValueString("MONCAT_CODE"));
                //序号
                else if(variances[i].equals("SEQ"))
                    varianceParm.addData(variances[i], seq);
                //区域
                else if(variances[i].equals("REGION_CODE"))
                    varianceParm.addData(variances[i], REGION_CODE.getValue());
                else
                     varianceParm.addData(variances[i], "");
            }
           // varianceParm = oldParm;//第二个表格中所有的数据
            //显示新行
            TABLE_VARIANCE.setParmValue(varianceParm);
        } else {
            this.messageBox("无归属");
            return;
        }
    }
    /**
     * 得到最大的编号
     *
     * @param dataStore
     *            TDataStore
    * @param columnName
    *            String
    * @return String
    */
   public int getMaxSeq(TParm parm, String columnName) {
       if (parm == null)
           return 0;
       // 保存数据量
       int count = parm.getCount();
       // 保存最大号
       int s = 0;
       for (int i = 0; i < count; i++) {
           int value = parm.getInt(columnName,i);
           // 保存最大值
           if (s < value) {
               s = value;
               continue;
           }
       }
       // 最大号加1
       s++;
       return s;
   }

}
