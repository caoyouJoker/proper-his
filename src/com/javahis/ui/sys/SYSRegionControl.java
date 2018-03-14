package com.javahis.ui.sys;

import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.SYSNewRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.TMessage;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;

/**
 * <p>
 * Title:区域
 * </p>
 *
 * <p>
 * Description: 区域
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author zhangkun 20090226
 * @version 1.0
 */
public class SYSRegionControl
    extends TControl {

    // 查询字典表中的关于 statelist 列的数据
    TParm stateList = SYSNewRegionTool.getInstance().getStateList(
        "SYS_REGION_FLG");

    // 记录表的选中行数
    int selectedRowIndex = -1;

    public SYSRegionControl() {
    }

    public void onInit() {
        super.onInit();
        //yanjing20130420添加查询注释表刷新
        this.onQuery();
//        this.getTable("TABLE").onQuery();
        // 删除权限
        if (!this.getPopedem("delPopedem")) {
            ( (TMenuItem)this.getComponent("delete")).setVisible(false);
        }
        this.setValue("DETECTPWDTIME",90);//密码校验
    }

    /**
     * 获取指定名称的Table
     *
     * @param tableName
     *            String Table控件名
     * @return TTable
     */
    private TTable getTable(String tableName) {
        TTable table = (TTable)this.getComponent(tableName);
        return table;
    }

    /**
     * 行点击事件
     */
    public void RowClicked() {
        // this.messageBox("行点击事件");
        TTextField txt = (TTextField)this.getComponent("REGION_CODE");
        txt.setEditable(false); // 设置“区域代码”不可编辑
        TTable table = this.getTable("TABLE");
        TParm data = table.getParmValue(); // 获取数据
        selectedRowIndex = table.getSelectedRow(); // 获取选中行号
        String STATE_LIST = data.getValue("STATE_LIST", selectedRowIndex); // 获取“标记位列表”数据
        this.setValue("PWD_STRENGTH",data.getValue("PWD_STRENGTH",selectedRowIndex));
        //=======pangben modify 20110601 密码校验天数
        this.setValue("DETECTPWDTIME",data.getValue("DETECTPWDTIME",selectedRowIndex));
        //=======yanjing modify 20130420 物联网标记
        this.setValue("SPC_FLG", data.getValue("SPC_FLG",selectedRowIndex));
        //=======shendr modify 20131014 静脉药物配置中心系统启动注记
        this.setValue("PIVAS_FLG", data.getValue("PIVAS_FLG",selectedRowIndex));
        int count = stateList.getCount("ID"); // 要标示的字段个数
        char[] s_list = STATE_LIST.toCharArray();
        data.setDataN("ss", getValue("sss"));
        if (count > 0 && count == s_list.length) {
            for (int i = 0; i < count; i++) {
                this.setValue(stateList.getValue("ID", i), String
                              .valueOf(s_list[i]));
            }
        }
    }
    /**
     * 保存事件
     */
    public void onSave() {
        // 判断必填字段
        if (this.getValue("REGION_CODE").equals("")
            || this.getValue("REGION_CHN_DESC").equals("")
            || this.getValue("REGION_CHN_ABN").equals("")) {
            this.messageBox("请填写中文名称和中文简称！");
            return;
        }

        // -----------给参数付值-----------------
        TParm parm = new TParm();
        parm.setData("REGION_CODE", this.getValue("REGION_CODE"));
        parm.setData("NHI_NO", this.getValue("NHI_NO"));
        parm.setData("SPC_FLG",this.getValueString("SPC_FLG"));//yanjing20130419物联网标记
        parm.setData("MAIN_FLG", this.getValue("MAIN_FLG"));
        parm.setData("HOSP_CLASS", this.getValue("HOSP_CLASS")); // 列表框
        parm.setData("REGION_CHN_DESC", this.getValue("REGION_CHN_DESC"));
        parm.setData("REGION_CHN_ABN", this.getValue("REGION_CHN_ABN"));
        parm.setData("PY1", this.getValue("PY1"));
        parm.setData("REGION_ENG_DESC", this.getValue("REGION_ENG_DESC"));
        parm.setData("REGION_ENG_ABN", this.getValue("REGION_ENG_ABN"));
        parm.setData("DESCRIPTION", this.getValue("DESCRIPTION"));
        parm.setData("PY2", this.getValue("PY2"));
        parm.setData("SEQ", this.getText("SEQ"));
        parm.setData("SUPERINTENDENT", this.getValue("SUPERINTENDENT"));
        parm.setData("NHIMAIN_NAME", this.getValue("NHIMAIN_NAME"));
        parm.setData("REGION_TEL", this.getValue("REGION_TEL"));
        parm.setData("REGION_FAX", this.getValue("REGION_FAX"));
        parm.setData("REGION_ADDR", this.getValue("REGION_ADDR"));
        parm.setData("E_MAIL", this.getValue("E_MAIL"));
        parm.setData("ACTIVE_FLG", this.getValue("ACTIVE_FLG"));
        parm.setData("IP_RANGE_START", this.getValue("IP_RANGE_START"));
        parm.setData("IP_RANGE_END", this.getValue("IP_RANGE_END"));
        parm.setData("AP_IP_ADDR", this.getValue("AP_IP_ADDR"));
        parm.setData("PWD_STRENGTH", this.getValue("PWD_STRENGTH"));
        //===========pangben modify 20110601 start  密码校验天数
        parm.setData("DETECTPWDTIME", this.getValue("DETECTPWDTIME"));
        //===========pangben modify 20110601 stop
        // 判断数字框是否为null
        if (this.getValue("TOP_BEDFEE") == null) {
            parm.setData("TOP_BEDFEE", "");
        }
        else {
            parm.setData("TOP_BEDFEE", this.getValue("TOP_BEDFEE"));
        }
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        // 获取标识列表个数
        int list_num = stateList.getCount("ID");
        String state = "";
        if (list_num > 0) {
            for (int i = 0; i < list_num; i++) {
                state += this.getValue(stateList.getValue("ID", i));
            }
        }
        // 如果字符串长度与字段列表不相同默认设置为“全否”
        if (state.length() != list_num) {
            state = "NNNNNNNNNN";
        }
        parm.setData("STATE_LIST", state);
        
        //======== shendr modify 20131014 静脉药物配置中心系统启动注记
        parm.setData("PIVAS_FLG", this.getValueString("PIVAS_FLG"));

        // -----------给参数付值结束-----------------
        // 判断“REGION_CODE”控件是否是只读属性，如果是则“修改”，否则“新建”
        TParm result = new TParm();
        if ( ( (TTextField)this.getComponent("REGION_CODE")).isEditable()) {
        	 //=====yanjing20140422区域代码校验
            TTable table = getTable("TABLE");
        	TParm tableParm=table.getParmValue();
        	for (int i = 0; i < tableParm.getCount(); i++) {
    			if (tableParm.getValue("REGION_CODE",i).equals(this.getValue("REGION_CODE"))){
    				this.messageBox("区域代码不能重复！");
    				return;
    			}			
    		}
            // 判断主院区
            if ("Y".equals(getValueString("MAIN_FLG"))) {
                TParm region = SYSNewRegionTool.getInstance().getAllRegion();
                if (region.getErrCode() < 0) {
                    this.messageBox("错误！", region.getErrText(), -1);
                    return;
                }
                for (int i = 0; i < region.getVector("MAIN_FLG").size(); i++) {
                    String flg = ( (Vector) region.getVector("MAIN_FLG").get(i))
                        .get(0).toString();
                    if (!region.getValue("REGION_CODE",
                        i).equals(this.getValue("REGION_CODE")) && "Y".equals(flg)) {
                        this.messageBox("已经存在主院区");
                        return;
                    }
                }
            }
            result = SYSNewRegionTool.getInstance().onInsert(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("错误！", result.getErrText(), -1);
                return;
            }
            this.messageBox("新建成功！");
            this.onClear();
            //yanjing20130420 修改
            this.onQuery();
//            this.getTable("TABLE").onQuery(); // 表刷新
        }
        else {
            result = SYSNewRegionTool.getInstance().onUpdate(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("错误！", result.getErrText(), -1);
                return;
            }
            this.messageBox("修改成功！");
            //20130419yanjing 修改
            this.onClear();
            this.onQuery();
//            this.getTable("TABLE").onQuery(); // 表刷新
           // this.getTable("TABLE").setSelectedRow(selectedRowIndex);
        }

    }

    /**
     * 查询事件
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("REGION_CODE", this.getValue("REGION_CODE"));
        parm.setDataN("NHI_NO", this.getValue("NHI_NO"));
        parm.setDataN("HOSP_CLASS", this.getValue("HOSP_CLASS")); // 列表框
        parm.setDataN("REGION_CHN_DESC", this.getValue("REGION_CHN_DESC"));
        parm.setDataN("REGION_CHN_ABN", this.getValue("REGION_CHN_ABN"));
        parm.setDataN("PY1", this.getValue("PY1"));
        parm.setDataN("REGION_ENG_DESC", this.getValue("REGION_ENG_DESC"));
        parm.setDataN("REGION_ENG_ABN", this.getValue("REGION_ENG_ABN"));
        parm.setDataN("DESCRIPTION", this.getValue("DESCRIPTION"));
        parm.setDataN("PY2", this.getValue("PY2"));
        parm.setDataN("SUPERINTENDENT", this.getValue("SUPERINTENDENT"));
        parm.setDataN("NHIMAIN_NAME", this.getValue("NHIMAIN_NAME"));
        parm.setDataN("REGION_TEL", this.getValue("REGION_TEL"));
        parm.setDataN("REGION_FAX", this.getValue("REGION_FAX"));
        parm.setDataN("REGION_ADDR", this.getValue("REGION_ADDR"));
        parm.setDataN("E_MAIL", this.getValue("E_MAIL"));
        parm.setDataN("IP_RANGE_START", this.getValue("IP_RANGE_START"));
        parm.setDataN("IP_RANGE_END", this.getValue("IP_RANGE_END"));
        parm.setDataN("AP_IP_ADDR", this.getValue("AP_IP_ADDR"));
        TParm result = SYSNewRegionTool.getInstance().onQuery(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        this.getTable("TABLE").setParmValue(result);
        selectedRowIndex = -1;
    }

    /**
     * 删除事件
     */
    public void onDelete() {
        // 确认删除
        if (this.messageBox("询问", "确定删除?", 0) == 1) {
            return;
        }
        // 检查删除信息不能为空
        if (this.getValue("REGION_CODE").equals("")
            || this.getValue("REGION_CODE").equals(null)) {
            this.messageBox("请选择要删除的项！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("REGION_CODE", this.getValue("REGION_CODE"));
        // 检查系统是否启用
        TParm re = SYSNewRegionTool.getInstance().onQuery(parm);
        if (re.getValue("ACTIVE_FLG", 0).equals("Y")) {
            this.messageBox("系统已启用不能删除！");
            return;
        }
        TParm result = SYSNewRegionTool.getInstance().onDelete(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        this.messageBox("删除成功！");
        this.onClear();
        this.getTable("TABLE").onQuery();
    }

    /**
     * 清空事件
     */
    public void onClear() {
        this
            .clearValue("REGION_CODE;NHI_NO;MAIN_FLG;HOSP_CLASS;REGION_CHN_DESC;REGION_CHN_ABN;SEQ;PY1;PY2;REGION_ENG_DESC;REGION_ENG_ABN;DESCRIPTION");
        this
            .clearValue("SUPERINTENDENT;NHIMAIN_NAME;REGION_TEL;REGION_FAX;REGION_ADDR;E_MAIL;ACTIVE_DATE;ACTIVE_FLG;IP_RANGE_START;IP_RANGE_END;AP_IP_ADDR;EMR;EKT;LDAP;INS;ONLINE;ODO;REASONABLEMED;REG;HANDINMED;CHARGE;TOP_BEDFEE;PWD_STRENGTH;PIVAS_FLG");
        ( (TTextField)this.getComponent("REGION_CODE")).setEditable(true); // 设置“区域代码”为可编辑状态
        selectedRowIndex = -1;
        this.getTable("TABLE").clearSelection(); // 清空TABLE选中状态
        this.setValue("DETECTPWDTIME",90);//密码校验
    }

    /**
     * 中文名称回车事件
     */
    public void onRegionChnDescAction() {
        String desc = this.getValueString("REGION_CHN_DESC");
        String py = TMessage.getPy(desc);
        this.setValue("PY1", py);
        ( (TTextField) getComponent("REGION_CHN_ABN")).grabFocus();
    }

}
