package com.javahis.ui.clp;


import jdo.clp.CLPAssessStandardTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

/**
 * <p>Title: 临床路径评估标准</p>
 *
 * <p>Description: 临床路径评估标准 </p>
 *
 * <p>Copyright: Copyright (c) 深圳中航 2011</p>
 *
 * <p>Company: javahis </p>
 * com.javahis.ui.clp.CLPAssessStandardControl
 * 
 * @author ZhenQin
 * @version 4.0
 */
public class CLPAssessStandardControl
    extends TControl {

    /**
     * 显示的TABle-CLP_EVL_CAT1
     */
    private TTable CLP_EVL_CAT1 = null;

    /**
     * 显示的TABle-CLP_EVL_CAT3
     */
    private TTable CLP_EVL_CAT2 = null;

    /**
     * 显示的TABle-CLP_EVL_CAT3
     */
    private TTable CLP_EVL_CAT3 = null;

    private String action = "update";
    
    private int index = 1;

    /**
     * 构造方法
     */
    public CLPAssessStandardControl() {
        super();
    }

    /**
     * 初始化
     */
    public void onInit() {
        CLP_EVL_CAT1 = (TTable)this.getComponent("CLP_EVL_CAT1");
        CLP_EVL_CAT2 = (TTable)this.getComponent("CLP_EVL_CAT2");
        CLP_EVL_CAT3 = (TTable)this.getComponent("CLP_EVL_CAT3");
        ((TMenuItem)this.getComponent("delete")).setEnabled(false);
        ((TMenuItem)this.getComponent("save")).setEnabled(false);
    }

    /**
     * 查询,当查询时,当输入了code,以code进行查询<br>
     * 当输入了desc(中,英),以其进行模糊查询<br>
     * 否则进行全部查询
     */
    public void onQuery() {
        TParm param = new TParm();
        //取得code和desc的值
        TParm progress = this.getParmForTag("CAT1_CODE;CAT1_CHN_DESC;CAT1_ENG_DESC");
        //如果输入了code,以code查询
        if (!progress.getValue("CAT1_CODE").equals("")) {
            param.setData("CAT1_CODE", progress.getValue("CAT1_CODE"));
        }
        //如果输入了中文desc,以中文desc模糊查询
        if (!progress.getValue("CAT1_CHN_DESC").equals("")) {
            param.setData("CAT1_CHN_DESC", progress.getValue("CAT1_CHN_DESC") + "%");
        }
        //如果输入了英文code,以英文code模糊查询
        if (!progress.getValue("CAT1_ENG_DESC").equals("")) {
            param.setData("CAT1_ENG_DESC", progress.getValue("CAT1_ENG_DESC") + "%");
        }
        param.setData("REGION_CODE", Operator.getRegion());
        //查询动作
        param = CLPAssessStandardTool.getInstance().queryCLP_EVL_CAT(
            "queryCLP_EVL_CAT1", param);
        //查询异常和没有查询到结果
        if (param.getErrCode() < 0 || param.getCount("CAT1_CODE") <= 0) {
            this.messageBox("没有查询到数据!");
            return;
        }
        //查询有数据则显示在table上
        CLP_EVL_CAT1.setParmValue(param);
        ((TMenuItem)this.getComponent("delete")).setEnabled(true);
        //查询到结果后立即清除CLP_EVL_CAT2,CLP_EVL_CAT3
        CLP_EVL_CAT2.removeRowAll();
        CLP_EVL_CAT3.removeRowAll();

    }

    /**
     * 保存数据
     */
    public void onSave() {
    	
        //取得TAg的值
        TParm param = this.getParmForTag("CAT1_CODE;CAT1_CHN_DESC;"
        		+ "CAT1_ENG_DESC;CAT2_CODE;CAT2_CHN_DESC;CAT2_ENG_DESC;"
        		+ "CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;SCORE;"
        		+ "PY1;PY2;PY3;DESCRIPTION1;DESCRIPTION2;DESCRIPTION3;"
        		+ "SEQ1;SEQ2;SEQ3");
        //设置ZJ值,代表PY2
        param.setData("ZJ1", "");
        param.setData("ZJ2", "");
        param.setData("ZJ3", "");
        //取得院区
        param.setData("REGION_CODE", Operator.getRegion());
        //操作者ID
        param.setData("OPT_USER", Operator.getID());
        //取得当前时间,无论是新增还是更新,时间都是当前时间
        param.setData("OPT_DATE", SystemTool.getInstance().getDate());
        //操作者IP
        param.setData("OPT_TERM", Operator.getIP());
        //判断是更新动作还是新增
        if(action.equals("update")){
        	update(param);
        }else{
        	//无论新增成功与否,都把action动作设置成默认的更新
        	action = "update";
        	insert(param);
        }
        ((TMenuItem)this.getComponent("add")).setEnabled(true);
    }

    /**
     * 清除数据
     */
    public void onClear() {
    	index = 1;
    	action = "update";
        this.clearValue("CAT1_CODE;CAT1_CHN_DESC;"
        		+ "CAT1_ENG_DESC;CAT2_CODE;CAT2_CHN_DESC;CAT2_ENG_DESC;"
        		+ "CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;SCORE;"
        		+ "PY1;PY2;PY3;DESCRIPTION1;DESCRIPTION2;DESCRIPTION3;"
        		+ "SEQ1;SEQ2;SEQ3");
        CLP_EVL_CAT1.removeRowAll();
        CLP_EVL_CAT2.removeRowAll();
        CLP_EVL_CAT3.removeRowAll();
        TTextField CAT1_CODE = (TTextField)this.getComponent("CAT1_CODE");
        CAT1_CODE.setEnabled(true);
        ((TMenuItem)this.getComponent("delete")).setEnabled(false);
        ((TMenuItem)this.getComponent("save")).setEnabled(false);
        ((TMenuItem)this.getComponent("add")).setEnabled(true);
    }

    /**
     * 删除
     */
    public void onDelete() {
        //取得TAg的值
        TParm param = this.getParmForTag("CAT1_CODE;CAT2_CODE;CAT3_CODE");
        param.setData("REGION_CODE", Operator.getRegion());
        param.setData("INDEX", index);
        //当点击了第一个列表,action=2,这时删除的是第一个表CLP_EVL_CAT1的数据
        //当删除第一个表中的数据,则把CLP_EVL_CAT2和CLP_EVL_CAT3中以CAT1_CODE为外键的数据都删除
        if (index == 2) {
            TParm parm = CLP_EVL_CAT1.getParmValue();
            //没有数据,返回i
            if (parm == null || parm.getCount("CAT1_CODE") == 0) {
                this.messageBox("没有数据!");
                return;
            }
            int select = CLP_EVL_CAT1.getSelectedRow();
            //没有选中,返回
            if (select < 0) {
                this.messageBox("请选中行!");
                return;
            }
            //再次询问是否删除
            if(this.messageBox("警告", "删除大分类会删除此分类下的所有子分类,你确定要删除吗?",
                               TControl.OK_CANCEL_OPTION) ==
               TControl.CANCEL_OPTION){
                return ;
            }
            //调用事务删除,当完全删除commit
            TParm result = TIOM_AppServer.executeAction("action.clp.CLPAssessStandardAction",
                                              "deleteCLP_EVL_CAT", param);
            if(result.getErrCode() < 0){
                this.messageBox("删除失败!");
                return;
            }else{
                this.messageBox("删除成功!");
                CLP_EVL_CAT1.removeRow(select);
                CLP_EVL_CAT2.removeRowAll();
                CLP_EVL_CAT3.removeRowAll();
                //删除数据成功,则清除列表上对多余的数据
                this.clearValue("CAT1_CODE;CAT1_CHN_DESC;CAT1_ENG_DESC;"
                		+ "CAT2_CODE;CAT2_CHN_DESC;CAT2_ENG_DESC;"
                		+ "CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;SCORE;PY1;PY2;PY3");
                return;
            }
            //当点击了第二个列表,action=3,这时删除的是第二个表CLP_EVL_CAT2的数据
        } else if(index == 3){
            TParm parm = CLP_EVL_CAT2.getParmValue();
            //没有数据,返回i
            if (parm == null || parm.getCount("CAT2_CODE") == 0) {
                this.messageBox("没有数据!");
                return;
            }
            int select = CLP_EVL_CAT2.getSelectedRow();
            //没有选中,返回
            if (select < 0) {
                this.messageBox("请选中行!");
                return;
            }
            if(this.messageBox("警告", "删除中分类会删除此分类下的所有小分类,你确定要删除吗?",
                               TControl.OK_CANCEL_OPTION) ==
               TControl.CANCEL_OPTION){
                return ;
            }
            //当删除第二张表的单个数据时,要同时删除第三张表中的以第二张表CAT2_CODE为外键的所有数据
            TParm result = TIOM_AppServer.executeAction("action.clp.CLPAssessStandardAction",
                                              "deleteCLP_EVL_CAT", param);
            if(result.getErrCode() < 0){
                this.messageBox("删除失败!");
                return;
            }else{
                this.messageBox("删除成功!");
                CLP_EVL_CAT2.removeRow(select);
                CLP_EVL_CAT3.removeRowAll();
                this.clearValue("CAT2_CODE;CAT2_CHN_DESC;CAT2_ENG_DESC;"
                		+ "CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;SCORE;PY2;PY3");
                return;
            }
            //当点击了第三个列表,action=-1,这时删除的是第三个表CLP_EVL_CAT3的数据
        }else if(index == -1){
            TParm parm = CLP_EVL_CAT3.getParmValue();
            //没有数据,返回i
            if (parm == null || parm.getCount("CAT3_CODE") == 0) {
                this.messageBox("没有数据!");
                return;
            }
            int select = CLP_EVL_CAT3.getSelectedRow();
            //没有选中,返回
            if (select < 0) {
                this.messageBox("请选中行!");
                return;
            }
            if(this.messageBox("警告", "你确定要删除选中行的数据吗?",
                               TControl.OK_CANCEL_OPTION) ==
               TControl.CANCEL_OPTION){
                return ;
            }
            //直接删除,因为单表操作,不用事务
            TParm result = CLPAssessStandardTool.getInstance().deleteCLP_EVL_CAT(
            "deleteCLP_EVL_CAT3", param);
            if(result.getErrCode() < 0){
                this.messageBox("删除失败!");
                return;
            }else{
                this.messageBox("删除成功!");
                CLP_EVL_CAT3.removeRow(select);
                this.clearValue("CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;SCORE;PY3");
                return;
            }
            //其他值则是程序运行期错误
        } else {
            this.messageBox("操作错误!");
            return ;
        }
    }

    /**
     * 当在第一个Table上发生点击事件
     */
    public void onSelect_1() {
        TParm parm = CLP_EVL_CAT1.getParmValue();
        //没有数据,返回i
        if (parm == null || parm.getCount("CAT1_CODE") == 0) {
            return;
        }
        int select = CLP_EVL_CAT1.getSelectedRow();
        //没有选中,返回
        if (select < 0) {
            return;
        }
//        把索引置成2,表示要操作的是CLP_EVL_CAT1
        //因为其他原因,这里这个索引位置使用频繁,不能随便赋值
        index = 2;
        //更新动作
        action = "update";
        //取得操作者选中行的TParm
        TParm selectParm = parm.getRow(select);
        TTextField CAT1_CODE = (TTextField)this.getComponent("CAT1_CODE");
        this.setValueForParm("CAT1_CODE;CAT1_CHN_DESC;CAT1_ENG_DESC;PY1;DESCRIPTION1;SEQ1", selectParm);
        //当用户选中时,锁定CAT1_CODE,这时,用户保存时是更新动作
        //可能是因为系统原因,SEQ这个空间无法赋值,这里采取强制赋值的方式
        String seq = selectParm.getValue("SEQ1");
        this.setValue("SEQ1",seq);
        //选中了一行数据,提取该行数据,并把数据显示在控件上,并把code锁住
        CAT1_CODE.setEnabled(false);
        TParm param = new TParm();
        param.setData("CAT1_CODE", selectParm.getValue("CAT1_CODE"));
        ((TMenuItem)this.getComponent("add")).setEnabled(true);
        ((TMenuItem)this.getComponent("save")).setEnabled(true);
        this.clearValue("CAT2_CODE;CAT2_CHN_DESC;CAT2_ENG_DESC;"
        		+ "CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;SCORE;PY2;PY3;"
        		+ "PY3;DESCRIPTION2;DESCRIPTION3;"
        		+ "SEQ2;SEQ3");
        //清除第三表的数据
        CLP_EVL_CAT3.removeRowAll();
        //查询选中行上以选中行code为外键的数据,并显示在CLP_EVL_CAT2
        TParm result = CLPAssessStandardTool.getInstance().queryCLP_EVL_CAT(
        		"queryCLP_EVL_CAT2", param);
        if (result.getErrCode() >= 0) {
            CLP_EVL_CAT2.setParmValue(result);
        }
        
    }

    /**
     * 同上,一样的结构
     */
    public void onSelect_2() {
        TParm parm = CLP_EVL_CAT2.getParmValue();
        //没有数据,返回i
        if (parm == null || parm.getCount("CAT2_CODE") == 0) {
            return;
        }
        int select = CLP_EVL_CAT2.getSelectedRow();
        //没有选中,返回
        if (select < 0) {
            return;
        }
        index = 3;
        action = "update";
        TParm selectParm = parm.getRow(select);
        selectParm.setData("PY2", selectParm.getValue("PY1"));
        TTextField CAT2_CODE = (TTextField)this.getComponent("CAT2_CODE");
        this.setValueForParm("CAT2_CODE;CAT2_CHN_DESC;CAT2_ENG_DESC;PY2;DESCRIPTION2;SEQ2", selectParm);
        //当用户选中时,锁定CHKUSER_CODE,这时,用户保存时是更新动作
        CAT2_CODE.setEnabled(false);
        TParm param = this.getParmForTag("CAT1_CODE;CAT2_CODE");
        ((TMenuItem)this.getComponent("add")).setEnabled(true);
        ((TMenuItem)this.getComponent("save")).setEnabled(true);
        this.clearValue("CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;"
        		+ "SCORE;PY3;DESCRIPTION3;SEQ3");
        this.setValue("SEQ2",selectParm.getValue("SEQ2"));
        TParm result = CLPAssessStandardTool.getInstance().queryCLP_EVL_CAT(
        		"queryCLP_EVL_CAT3", param);
        if (result.getErrCode() >= 0) {
            CLP_EVL_CAT3.setParmValue(result);
        }

    }

    /**
     * 同上
     */
    public void onSelect_3() {
        TParm parm = CLP_EVL_CAT3.getParmValue();
        //没有数据,返回i
        if (parm == null || parm.getCount("CAT3_CODE") == 0) {
            return;
        }
        int select = CLP_EVL_CAT3.getSelectedRow();
        //没有选中,返回
        if (select < 0) {
            return;
        }
        index = -1;
        action = "update";
        TParm selectParm = parm.getRow(select);
        selectParm.setData("PY3", selectParm.getValue("PY1"));
        TTextField CAT3_CODE = (TTextField)this.getComponent("CAT3_CODE");
        CAT3_CODE.setText(selectParm.getValue("CAT3_CODE"));
        this.setValueForParm("CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;SCORE;PY3;DESCRIPTION3;SEQ3", selectParm);
        //当用户选中时,锁定CHKUSER_CODE,这时,用户保存时是更新动作
        CAT3_CODE.setEnabled(false);
        this.setValue("SEQ3",selectParm.getValue("SEQ3"));
        ((TMenuItem)this.getComponent("add")).setEnabled(false);
        ((TMenuItem)this.getComponent("save")).setEnabled(true);
    }

    /**
     * 添加数据
     */
    public void onAdd() {
        //取得TAg的值
        TParm param = this.getParmForTag("CAT1_CODE;CAT2_CODE;CAT3_CODE");
        //取得操作者的活动表
        if (index == 1) {
        	//取得第一表的最大行数,新增行以这个数字+1
        	TParm result = CLPAssessStandardTool.getInstance().queryMaxCode(
                "queryMaxCode1", param);
            if (result.getErrCode() >= 0) {
                String maxcode = "";
                //如果没有值则用00表示
                if (result.getCount("MAXCODE") == 0) {
                    maxcode = "00";
                }
                else {
                	//有值则取得该值
                    maxcode = result.getValue("MAXCODE", 0);
                }
                //通过这个方法,我们取得一个唯一个索引键
                maxcode = getNextCode("", maxcode);
                if (maxcode != null) {
                	//构造数据
                    TParm temp = new TParm();
                    temp.setData("CAT1_CODE", maxcode);
                    temp.setData("SEQ1", getSEQ(maxcode));
                    temp.setData("DESCRIPTION1", "");
                    temp.setData("CAT1_CHN_DESC", "");
                    temp.setData("CAT1_ENG_DESC", "");
                    temp.setData("OPT_USER", Operator.getID());
                    temp.setData("OPT_DATE", SystemTool.getInstance().getDate());
                    temp.setData("OPT_TERM", Operator.getIP());
                    temp.setData("PY1", "");
                    //添加以新航
                    CLP_EVL_CAT1.addRow(temp);
                    ((TTextField)this.getComponent("CAT1_CODE")).setEnabled(false);
                    this.setValueForParm("CAT1_CODE;CAT1_CHN_DESC;PY1;SEQ1;DESCRIPTION1", temp);
                }
            }
        }
        else if (index == 2) {
        	//验证需要添加子项的code是否已经在父表中存在,如果不存在
        	//从第二个表开始,不同于第一张表,有的时候存在想第一张表中增加数据,
        	//没有保存便增加第二表中的子项,这个是不允许的
        	TParm result = CLPAssessStandardTool.getInstance().queryMaxCode(
                    "isExists1", param);
        	//没有保存父项的情况
        	if(result.getErrCode() < 0 || result.getCount("MAXCODE") == 0 
        			|| result.getInt("MAXCODE", 0) != 1){
        		this.messageBox("你目前不能对该分类增加子分类!");
        		return;
        	}
        	//执行到这里,则是已经有了父项
            result = CLPAssessStandardTool.getInstance().queryMaxCode(
                "queryMaxCode2", param);
            //一下代码同上
            if (result.getErrCode() >= 0) {
                String maxcode = "";
                if (result.getCount("MAXCODE") == 0) {

                    maxcode = "00";
                }
                else {
                    maxcode = result.getValue("MAXCODE", 0);
                }
                maxcode = getNextCode(param.getValue("CAT1_CODE"), maxcode);
                if (maxcode != null) {
                    TParm temp = new TParm();
                    temp.setData("CAT2_CODE", maxcode);
                    temp.setData("SEQ2", getSEQ(maxcode));
                    temp.setData("CAT2_CHN_DESC", "");
                    temp.setData("CAT2_ENG_DESC", "");
                    temp.setData("DESCRIPTION2", "");
                    temp.setData("OPT_USER", Operator.getID());
                    temp.setData("OPT_DATE", SystemTool.getInstance().getDate());
                    temp.setData("OPT_TERM", Operator.getIP());
                    temp.setData("PY2", "");
                    CLP_EVL_CAT2.addRow(temp);
                    this.setValueForParm("CAT2_CODE;CAT2_CHN_DESC;PY2;SEQ2;DESCRIPTION2", temp);
                }
            }
        }
        else if (index == 3) {
        	//同上
        	TParm result = CLPAssessStandardTool.getInstance().queryMaxCode(
                    "isExists2", param);
        	if(result.getErrCode() < 0 || result.getCount("MAXCODE") == 0 
        			|| result.getInt("MAXCODE", 0) != 1){
        		this.messageBox("你目前不能对该分类增加子分类!");
        		return;
        	}
            result = CLPAssessStandardTool.getInstance().queryMaxCode(
                "queryMaxCode3", param);
            if (result.getErrCode() >= 0) {
            	String maxcode = "";
                if (result.getCount("MAXCODE") == 0) {
                    maxcode = "00";
                }
                else {
                    maxcode = result.getValue("MAXCODE", 0);
                }
                maxcode = getNextCode(param.getValue("CAT2_CODE"), maxcode);
                if (maxcode != null) {
                    TParm temp = new TParm();
                    temp.setData("CAT3_CODE", maxcode);
                    temp.setData("SEQ3", getSEQ(maxcode));
                    temp.setData("CAT3_CHN_DESC", "");
                    temp.setData("CAT3_ENG_DESC", "");
                    temp.setData("DESCRIPTION3", "");
                    temp.setData("SCORE", 1);
                    temp.setData("OPT_USER", Operator.getID());
                    temp.setData("OPT_DATE", SystemTool.getInstance().getDate());
                    temp.setData("OPT_TERM", Operator.getIP());
                    temp.setData("PY3", "");
                    CLP_EVL_CAT3.addRow(temp);
                    this.setValueForParm("CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;"
                    		+ "SCORE;PY3;SEQ3;DESCRIPTION3", temp);
                }
            }
            //第三个表是最小节点,不能再添加
        } else {
            this.messageBox("已经在最小节点,你已经不能再添加!");
            return;
        }
        action = "insert";
        ((TMenuItem)this.getComponent("add")).setEnabled(false);
        ((TMenuItem)this.getComponent("save")).setEnabled(true);
    }

    /**
     * 构造下一个号码,注意,这里用到了递归,知道寻找到一个合适的号码<br>
     * 如果按照默认的情况下,系统按照流水排行,不会出错.但是当用户强制写数据库,则会出现错误<br>
     * 放置这种情况的出现
     * 
     * @param maxCode String
     * @return String
     */
    private String getNextCode(String oldValue, String maxCode) {
        String code = "";
        if (maxCode == null || maxCode.equals("")) {
            maxCode = "00";
        }
        try {
            int num = Integer.parseInt(maxCode);
            if(num < 9){
                maxCode =  "0" + (num + 1);
            }else{
                maxCode = "" + (num + 1);
            }

        }catch (Exception e) {
            maxCode += "1";
        }
        code = oldValue + maxCode;
        TParm data = new TParm();
        data.setData("CAT3_CODE", code);
        data.setData("REGION_CODE", Operator.getRegion());
        TParm result = CLPAssessStandardTool.getInstance().queryMaxCode(
                "verify", data);
        if(result.getValue("COUNTS", 0).equals("0")){
            return code;
        }else{
            return getNextCode(oldValue, maxCode);
        }
    }
    
    
    /**
     * 添加数据
     * @param param
     */
    private void insert(TParm param){
    	if (index == 1) {
        	if(param.getValue("CAT1_CHN_DESC").equals("")){
        		this.messageBox("评估大分类说明不能为空!");
        		return;
        	}
            TParm result = CLPAssessStandardTool.getInstance().
                insertCLP_EVL_CAT("insertCLP_EVL_CAT1", param);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + result.getErrText());
                this.messageBox("保存失败!" + result.getErrName() +
                                result.getErrText());
                return;
            }
            else {
                this.messageBox("保存成功!");
                onQuery();
                return;
            }
        }
        else if (index == 2) {
        	if(param.getValue("CAT2_CHN_DESC").equals("")){
        		this.messageBox("评估大分类说明不能为空!");
        		return;
        	}
            TParm result = CLPAssessStandardTool.getInstance().
                insertCLP_EVL_CAT("insertCLP_EVL_CAT2", param);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + result.getErrText());
                this.messageBox("保存失败!" + result.getErrName() +
                                result.getErrText());
                return;
            }
            else {
                this.messageBox("保存成功!");
                onSelect_1();
                return;
            }

        }
        else if (index == 3) {
        	
        	if(param.getValue("CAT3_CHN_DESC").equals("")){
        		this.messageBox("评估大分类说明不能为空!");
        		return;
        	}
        	try{
            	Double.parseDouble(param.getValue("SCORE"));
            } catch(NumberFormatException e) {
            	this.messageBox("您的分值输入的不正确!");
            	return;
            }
            TParm result = CLPAssessStandardTool.getInstance().
                insertCLP_EVL_CAT("insertCLP_EVL_CAT3", param);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + result.getErrText());
                this.messageBox("保存失败!" + result.getErrName() +
                                result.getErrText());
                return;
            }
            else {
                this.messageBox("保存成功!");
                onSelect_2();
                return;
            }
        }
        else {
            this.messageBox("保存失败,您保存的条件不可到达!");
            return;
        }
    }
    
    
    /**
     * 更新数据
     * @param param
     */
    private void update(TParm param){
    	//取得TAg的值
        param.setData("INDEX", index);
        //当点击了第一个列表,action=2,这时删除的是第一个列表的数据
        if (index == 2) {
        	if(param.getValue("CAT1_CHN_DESC").equals("")){
        		this.messageBox("评估大分类说明不能为空!");
        		return;
        	}
        	try{
            	int seq = Integer.parseInt(param.getValue("SEQ1"));
            	if(seq <= 0){
            		throw new RuntimeException("顺序号必须是一个大于0的整数!");
            	}
            } catch(Exception e) {
            	if(e instanceof RuntimeException){
            		this.messageBox("顺序号必须是一个大于0的整数!");
            		this.setValue("SEQ1", 1);
            	} else {
            		this.messageBox("保存失败,位置异常!");
            	}
            	return;
            }
            TParm result = CLPAssessStandardTool.getInstance().updateCLP_EVL_CAT(
                    "updateCLP_EVL_CAT1", param);
            if(result.getErrCode() < 0){
                this.messageBox("更新失败!");
                return;
            }else{
                this.messageBox("更新成功!");
                onQuery();
                
                return;
            }
            //当点击了第二个列表,action=3,这时删除的是第二个列表的数据
        } else if(index == 3){
        	if(param.getValue("CAT2_CHN_DESC").equals("")){
        		this.messageBox("评估中分类说明不能为空!");
        		return;
        	}
        	try{
            	int seq = Integer.parseInt(param.getValue("SEQ2"));
            	if(seq <= 0){
            		throw new RuntimeException("顺序号必须是一个大于0的整数!");
            	}
            } catch(Exception e) {
            	if(e instanceof RuntimeException){
            		this.messageBox("顺序号必须是一个大于0的整数!");
            		this.setValue("SEQ2", 1);
            	} else {
            		this.messageBox("保存失败,位置异常!");
            	}
            	return;
            }
            //当删除第二张表的单个数据时,要同时删除第三张表中的以第二张表CAT2_CODE为外键的所有数据
            TParm result = CLPAssessStandardTool.getInstance().updateCLP_EVL_CAT(
                    "updateCLP_EVL_CAT2", param);
            if(result.getErrCode() < 0){
                this.messageBox("更新失败!");
                return;
            }else{
                this.messageBox("更新成功!");
                onSelect_1();
                
                return;
            }
            //当点击了第三个列表,action=-1,这时删除的是第三个列表的数据
        }else if(index == -1){
        	if(param.getValue("CAT3_CHN_DESC").equals("")){
        		this.messageBox("评估小分分类说明不能为空!");
        		return;
        	}
        	try{
            	Double.parseDouble(param.getValue("SCORE"));
            	int seq = Integer.parseInt(param.getValue("SEQ3"));
            	if(seq <= 0){
            		throw new RuntimeException("顺序号必须是一个大于0的整数!");
            	}
            } catch(Exception e) {
            	if(e instanceof NumberFormatException){
            		this.messageBox("您的分值输入的不正确!");
            		this.setValue("SCORE", 1);
            	} else if(e instanceof RuntimeException){
            		this.messageBox("顺序号必须是一个大于0的整数!");
            		this.setValue("SEQ3", 1);
            	} else {
            		this.messageBox("保存失败,位置异常!");
            	}
            	return;
            }
            
            TParm result = CLPAssessStandardTool.getInstance().updateCLP_EVL_CAT(
            "updateCLP_EVL_CAT3", param);
            if(result.getErrCode() < 0){
                this.messageBox("更新失败!");
                return;
            }else{
                this.messageBox("更新成功!");
                onSelect_2();
                
                return;
            }
        }else{
            this.messageBox("操作错误!");
            return ;
        }
        
        
    }
    
    
    /**
     * 生成默认的顺序号
     * @param code
     * @return
     */
    private String getSEQ(String code){
    	if(code == null || code.equals("")){
    		return "0";
    	}
    	String seq = code.substring(code.length() - 2);
    	return Integer.parseInt(seq) + "";
    }
}
