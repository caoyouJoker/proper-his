package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextFormat;
import jdo.clp.ClpchkTypeTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TComboBox;
import jdo.sys.Operator;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title:查核类别字典 </p>
 *
 * <p>Description:查核类别字典单档 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110429
 * @version 1.0
 */
public class ClpchkTypeControl extends TControl{
    public ClpchkTypeControl() {
    }
    //查核类别代码，变别拼音，中文说明，英文说明，序号,注记,备注
    TTextField CHKTYPE_CODE,PY1,CHKTYPE_CHN_DESC,CHKTYPE_ENG_DESC,SEQ,PY2,DESCRIPTION;
    //细分类
    TTextFormat ORDER_CAT1;
    //表格
    TTable TABLE;
    //区域
    TComboBox REGION_CODE;
    /**
     * 初始化
     */
    public void onInit(){
       super.onInit();
       onPage();
       //======== 权限添加
       REGION_CODE.setValue(Operator.getRegion());
       //之后要使用现在.8上没有后台类会出现错误
//       TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
//       cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
//               getValueString("REGION_CODE")));
       onQuery();
    }
    /**
     * 初始化页面
     */
    public void onPage(){
        ((TMenuItem)this.getComponent("delete")).setEnabled(false);
        //查核类别代码
        CHKTYPE_CODE = (TTextField)this.getComponent("CHKTYPE_CODE");
        //变别拼音
        PY1 = (TTextField) getComponent("PY1");
        //中文说明
        CHKTYPE_CHN_DESC = (TTextField) getComponent("CHKTYPE_CHN_DESC");
        //英文说明
        CHKTYPE_ENG_DESC= (TTextField) getComponent("CHKTYPE_ENG_DESC");
        //序号
        SEQ = (TTextField) getComponent("SEQ");
        //注记
        PY2 = (TTextField) getComponent("PY2");
        //备注
        DESCRIPTION = (TTextField) getComponent("DESCRIPTION");
        //细分类
        ORDER_CAT1 = (TTextFormat) getComponent("ORDER_CAT1");
        //区域
        REGION_CODE = (TComboBox) getComponent("REGION_CODE");
        //表格
        TABLE = (TTable) getComponent("TABLE");

    }
    /**
     * 查询方法
     */
    public void onQuery() {
        // 查询方法
        TParm result = new TParm(TJDODBTool.getInstance().select(getSQL()));
        callFunction("UI|TABLE|setParmValue",result);
    }
    /**
     * 保存方法
     */
    public void onSave(){
        //主键必须输入不可以为空值
        if (null == this.getValue("REGION_CODE") || this.getValueString("REGION_CODE").equals("")) {
            this.messageBox("请输入区域");
            return;
        }
        if (null ==this.getValue("CHKTYPE_CODE")  || this.getValueString("CHKTYPE_CODE").equals("")) {
            this.messageBox("请输入查核类别代码");
            return;
        }
        if (null == this.getValue("ORDER_CAT1") || this.getValueString("ORDER_CAT1").equals("")) {
            this.messageBox("请输入细分类");
            return;
        }

        TParm parm = new TParm();
        //查核类别代码
        parm.setData("CHKTYPE_CODE", CHKTYPE_CODE.getValue());
        //细分类
        parm.setData("ORDER_CAT1", ORDER_CAT1.getValue());
        //区域
        parm.setData("REGION_CODE", REGION_CODE.getValue());
        //序号
        parm.setData("SEQ", SEQ.getValue());
        //拼音
        parm.setData("PY1", PY1.getValue());
        //中文说明
        parm.setData("CHKTYPE_CHN_DESC", CHKTYPE_CHN_DESC.getValue());
        //英文说明
        parm.setData("CHKTYPE_ENG_DESC", CHKTYPE_ENG_DESC.getValue());
        //注记
        parm.setData("PY2", PY2.getValue());
        //备注
        parm.setData("DESCRIPTION", DESCRIPTION.getValue());
        //用户修改
        parm.setData("OPT_USER", Operator.getRegion());
        //当前时间
        parm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
        //IP
        parm.setData("OPT_TERM", Operator.getIP());

       TParm result=ClpchkTypeTool.getInstance().selectIsExist(parm);
       if (null != result&&result.getCount("REGION_CODE")>0)
           updateRow(parm);
       else
           InsertRow(parm);
       onClear();
       onQuery();
    }
    /**
     *  添加方法
     */
    public void InsertRow(TParm parm) {
        TParm result = ClpchkTypeTool.getInstance().saveClpchkType(parm);
        if (null != result) {
            this.messageBox("添加成功");
        } else
            this.messageBox("添加失败");
    }
    /**
     * 修改方法
     * @param parm TParm
     */
    public void updateRow(TParm parm) {

        TParm result = ClpchkTypeTool.getInstance().updateClpchkType(parm);
        if (null != result) {
            this.messageBox("修改成功");
        } else
            this.messageBox("修改失败");

    }
    /**
     * 查询sql语句
     * @return String
     */
    public String getSQL() {
        String sql = "SELECT B.REGION_CHN_DESC,A.REGION_CODE,A.CHKTYPE_CODE,A.ORDER_CAT1,A.CHKTYPE_CHN_DESC, " +
                     "A.PY1,A.SEQ,A.CHKTYPE_ENG_DESC,A.DESCRIPTION,A.PY2 FROM CLP_CHKTYPE A,SYS_REGION B WHERE A.REGION_CODE=B.REGION_CODE(+) ";
        //查核类别代码
        String chkTypeCode = "";
        //区域
        String regionCode = "";
        //细分类
        String orderCat = "";
        //查核类别代码
        if (null != CHKTYPE_CODE.getValue() && !"".equals(CHKTYPE_CODE.getValue()))
            chkTypeCode = " AND A.CHKTYPE_CODE LIKE '%" +
                          CHKTYPE_CODE.getValue().trim() + "%'";
        //细分类
        if (null != ORDER_CAT1.getValue() &&
            !"".equals(ORDER_CAT1.getValue()))
            orderCat = " AND A.ORDER_CAT1= '" + ORDER_CAT1.getValue() + "'";
        //区域
        if (null != REGION_CODE.getValue() &&
            !"".equals(REGION_CODE.getValue()))
            regionCode = " AND A.REGION_CODE ='" + REGION_CODE.getValue() + "'";
        sql += chkTypeCode + orderCat + regionCode;
        return sql;
    }

    /**
     * 清空方法
     */
    public void onClear(){

       // String clearObject="CHKTYPE_CODE,CHKTYPE_PYCODE,CHKTYPE_DESC,SEQ";
        this.clearValue("SEQ;PY1;CHKTYPE_CODE;ORDER_CAT1;CHKTYPE_CHN_DESC;PY2;CHKTYPE_ENG_DESC;DESCRIPTION;REGION_CODE");
        // clearValue(clearObject);
        //可以添加
        //查核类别代码
        CHKTYPE_CODE.setEnabled(true);
        //细分类
        ORDER_CAT1.setEnabled(true);
        //区域
        REGION_CODE.setValue(Operator.getRegion());
        //删除按钮
        ((TMenuItem)this.getComponent("delete")).setEnabled(false);
        //清空表格
        TABLE.setParmValue(new TParm());
    }
    /**
     * 单击表格一行数据
     */
    public void onTableClicked() {
        //如果修改方法将主键锁起来
        //查核类别代码
        CHKTYPE_CODE.setEnabled(false);
        //细分类
        ORDER_CAT1.setEnabled(false);
        //删除按钮可以选择
        ((TMenuItem)this.getComponent("delete")).setEnabled(true);
        //当前行
        int row = TABLE.getSelectedRow();
        //获得表格所有属性值
        TParm parm = TABLE.getParmValue();
        setValueForParm(
           "PY1;CHKTYPE_CODE;ORDER_CAT1;CHKTYPE_CHN_DESC;PY2;CHKTYPE_ENG_DESC;DESCRIPTION;REGION_CODE",
           parm, row);
        this.setValue("SEQ",parm.getValue("SEQ",row));
    }
    /**
     * 删除方法
     */
    public void onDelete() {
        TParm parm=new TParm();
        //查核类别代码
        if (null != CHKTYPE_CODE.getValue() && !"".equals(CHKTYPE_CODE.getValue()))
           parm.setData("CHKTYPE_CODE",CHKTYPE_CODE.getValue());
        //细分类
        if (null != ORDER_CAT1.getValue() &&
            !"".equals(ORDER_CAT1.getValue()))
          parm.setData("ORDER_CAT1",ORDER_CAT1.getValue());
        //区域
        if (null != REGION_CODE.getValue() &&
            !"".equals(REGION_CODE.getValue()))
             parm.setData("REGION_CODE",REGION_CODE.getValue());

        TParm result = ClpchkTypeTool.getInstance().deleteClpchkType(parm);
        if (null != result) {
            this.messageBox("删除成功");
        } else
            this.messageBox("删除失败");
        onClear();
        onQuery();
    }
}
