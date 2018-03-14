package com.javahis.ui.sys;

import com.dongyang.control.*;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import jdo.sys.SYSNewRegionTool;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TMenuItem;
import com.javahis.util.JavaHisDebug;

/**
 * <p>Title:区域 </p>
 *
 * <p>Description: 区域</p>
 *
 * <p>Copyright: Copyright (c)  Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author zhangkun 20090226
 * @version 1.0
 */
public class SYSNewRegionControl extends TControl {

    //查询字典表中的关于 statelist 列的数据
    TParm stateList = SYSNewRegionTool.getInstance().getStateList("SYS_REGION_FLG");
    //记录表的选中行数
    int selectedRowIndex = -1;
    public SYSNewRegionControl() {
    }

    public void onInit(){
        super.onInit();
        this.getTable("TABLE").onQuery();
        //删除权限
        if(!this.getPopedem("delPopedem")){
            ((TMenuItem)this.getComponent("delete")).setVisible(false);
        }
    }
    /**
     * 获取指定名称的Table
     * @param tableName String Table控件名
     * @return TTable
     */
    private TTable getTable(String tableName){
        TTable table = (TTable)this.getComponent(tableName);
        return table;
    }

    /**
     * 行点击事件
     */
    public void RowClicked(){
//        this.messageBox("行点击事件");
        TTextField txt = (TTextField)this.getComponent("REGION_CODE");
        txt.setEditable(false);//设置“区域代码”不可编辑
        TTable table = this.getTable("TABLE");
        TParm data = table.getParmValue();//获取数据
        selectedRowIndex = table.getSelectedRow();//获取选中行号
        String STATE_LIST = data.getValue("STATE_LIST",selectedRowIndex);//获取“标记位列表”数据
        int count = stateList.getCount("ID");//要标示的字段个数
        char[] s_list = STATE_LIST.toCharArray();
        data.setDataN("ss",getValue("sss"));
        if(count>0&&count==s_list.length){
            for(int i=0;i<count;i++){
                this.setValue(stateList.getValue("ID",i),String.valueOf(s_list[i]));
            }
        }
    }
    /**
     * 保存事件
     */
    public void onSave(){
        //判断必填字段
        if(this.getValue("REGION_CODE").equals("")|| this.getValue("REGION_CHN_DESC").equals("")||this.getValue("REGION_CHN_ABN").equals("")){
            this.messageBox("请填写中文名称和中文简称！");
            return;
        }
        //-----------给参数付值-----------------
        TParm parm = new TParm();
        parm.setData("REGION_CODE",this.getValue("REGION_CODE"));
        parm.setData("NHI_NO",this.getValue("NHI_NO"));
        parm.setData("MAIN_FLG",this.getValue("MAIN_FLG"));
        parm.setData("HOSP_CLASS",this.getValue("HOSP_CLASS"));//列表框
        parm.setData("REGION_CHN_DESC",this.getValue("REGION_CHN_DESC"));
        parm.setData("REGION_CHN_ABN",this.getValue("REGION_CHN_ABN"));
        parm.setData("PY1",this.getValue("PY1"));
        parm.setData("REGION_ENG_DESC",this.getValue("REGION_ENG_DESC"));
        parm.setData("REGION_ENG_ABN",this.getValue("REGION_ENG_ABN"));
        parm.setData("DESCRIPTION",this.getValue("DESCRIPTION"));
        parm.setData("PY2",this.getValue("PY2"));
        parm.setData("SEQ",this.getValue("SEQ"));
        parm.setData("SUPERINTENDENT",this.getValue("SUPERINTENDENT"));
        parm.setData("NHIMAIN_NAME",this.getValue("NHIMAIN_NAME"));
        parm.setData("REGION_TEL",this.getValue("REGION_TEL"));
        parm.setData("REGION_FAX",this.getValue("REGION_FAX"));
        parm.setData("REGION_ADDR",this.getValue("REGION_ADDR"));
        parm.setData("E_MAIL",this.getValue("E_MAIL"));
        parm.setData("ACTIVE_FLG",this.getValue("ACTIVE_FLG"));
        parm.setData("IP_RANGE_START",this.getValue("IP_RANGE_START"));
        parm.setData("IP_RANGE_END",this.getValue("IP_RANGE_END"));
        parm.setData("AP_IP_ADDR",this.getValue("AP_IP_ADDR"));
        //判断数字框是否为null
        if(this.getValue("TOP_BEDFEE")==null){
            parm.setData("TOP_BEDFEE", "");
        }
        else{
            parm.setData("TOP_BEDFEE", this.getValue("TOP_BEDFEE"));
        }
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        //获取标识列表个数
        int list_num = stateList.getCount("ID");
        String state = "";
        if(list_num>0){
            for(int i=0;i<list_num;i++){
                state += this.getValue(stateList.getValue("ID",i));
            }
        }
        //如果字符串长度与字段列表不相同默认设置为“全否”
        if(state.length()!=list_num){
            state = "NNNNNNNNNN";
        }
        parm.setData("STATE_LIST",state);
        //-----------给参数付值结束-----------------
        //判断“REGION_CODE”控件是否是只读属性，如果是则“修改”，否则“新建”
        TParm result = new TParm();
        if(((TTextField)this.getComponent("REGION_CODE")).isEditable()){
            result = SYSNewRegionTool.getInstance().onInsert(parm);
            this.messageBox("新建成功！");
            this.onClear();
            this.getTable("TABLE").onQuery(); //表刷新
        }
        else{
            result = SYSNewRegionTool.getInstance().onUpdate(parm);
            this.messageBox("修改成功！");
            this.getTable("TABLE").onQuery(); //表刷新
            this.getTable("TABLE").setSelectedRow(selectedRowIndex);
        }
        if(result.getErrCode()<0){
            //this.messageBox("错误！",result.getErrText(),-1);
            return;
        }

    }
    /**
     * 查询事件
     */
    public void onQuery(){
        TParm parm  = new TParm();
        parm.setDataN("REGION_CODE",this.getValue("REGION_CODE"));
        parm.setDataN("NHI_NO",this.getValue("NHI_NO"));
        parm.setDataN("HOSP_CLASS",this.getValue("HOSP_CLASS"));//列表框
        parm.setDataN("REGION_CHN_DESC",this.getValue("REGION_CHN_DESC"));
        parm.setDataN("REGION_CHN_ABN",this.getValue("REGION_CHN_ABN"));
        parm.setDataN("PY1",this.getValue("PY1"));
        parm.setDataN("REGION_ENG_DESC",this.getValue("REGION_ENG_DESC"));
        parm.setDataN("REGION_ENG_ABN",this.getValue("REGION_ENG_ABN"));
        parm.setDataN("DESCRIPTION",this.getValue("DESCRIPTION"));
        parm.setDataN("PY2",this.getValue("PY2"));
        parm.setDataN("SUPERINTENDENT",this.getValue("SUPERINTENDENT"));
        parm.setDataN("NHIMAIN_NAME",this.getValue("NHIMAIN_NAME"));
        parm.setDataN("REGION_TEL",this.getValue("REGION_TEL"));
        parm.setDataN("REGION_FAX",this.getValue("REGION_FAX"));
        parm.setDataN("REGION_ADDR",this.getValue("REGION_ADDR"));
        parm.setDataN("E_MAIL",this.getValue("E_MAIL"));
        parm.setDataN("IP_RANGE_START",this.getValue("IP_RANGE_START"));
        parm.setDataN("IP_RANGE_END",this.getValue("IP_RANGE_END"));
        parm.setDataN("AP_IP_ADDR",this.getValue("AP_IP_ADDR"));
        TParm result = SYSNewRegionTool.getInstance().onQuery(parm);
        if(result.getErrCode()<0){
            //this.messageBox(result.getErrText());
            return;
        }
        this.getTable("TABLE").setParmValue(result);
        selectedRowIndex = -1;
    }

    /**
     * 删除事件
     */
    public void onDelete(){
        //确认删除
        if (this.messageBox("询问", "确定删除?", 0) == 1) {
            return;
        }
        //检查删除信息不能为空
        if(this.getValue("REGION_CODE").equals("")||this.getValue("REGION_CODE").equals(null)){
            this.messageBox("请选择要删除的项！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("REGION_CODE",this.getValue("REGION_CODE"));
        //检查系统是否启用
        TParm re = SYSNewRegionTool.getInstance().onQuery(parm);
        if(re.getValue("ACTIVE_FLG",0).equals("Y")){
            this.messageBox("系统已启用不能删除！");
            return;
        }
        TParm result = SYSNewRegionTool.getInstance().onDelete(parm);
        if(result.getErrCode()<0){
            //this.messageBox(result.getErrText());
            return;
        }
        this.messageBox("删除成功！");
        this.onClear();
        this.getTable("TABLE").onQuery();
    }
    /**
     * 清空事件
     */
    public void onClear(){
        this.clearValue("REGION_CODE;NHI_NO;MAIN_FLG;HOSP_CLASS;REGION_CHN_DESC;REGION_CHN_ABN;SEQ;PY1;PY2;REGION_ENG_DESC;REGION_ENG_ABN;DESCRIPTION");
        this.clearValue("SUPERINTENDENT;NHIMAIN_NAME;REGION_TEL;REGION_FAX;REGION_ADDR;E_MAIL;ACTIVE_DATE;ACTIVE_FLG;IP_RANGE_START;IP_RANGE_END;AP_IP_ADDR;EMR;EKT;LDAP;INS;ONLINE;ODO;REASONABLEMED;REG;HANDINMED;CHARGE;TOP_BEDFEE");
        ((TTextField)this.getComponent("REGION_CODE")).setEditable(true);//设置“区域代码”为可编辑状态
        selectedRowIndex = -1;
        this.getTable("TABLE").clearSelection();//清空TABLE选中状态
    }
//    public static void main(String[] args) {
//        JavaHisDebug.runFrame("sys\\SYSNewRegion.x");
//        System.out.println(SYSNewRegionTool.getInstance().isEKT("2"));
//        System.out.println(SYSNewRegionTool.getInstance().isINS("2"));
//        System.out.println(SYSNewRegionTool.getInstance().isODO("2"));
//        System.out.println(SYSNewRegionTool.getInstance().isREG("2"));
//        System.out.println(SYSNewRegionTool.getInstance().isCHARGE("2"));
//    }
}
