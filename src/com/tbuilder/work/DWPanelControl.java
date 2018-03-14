package com.tbuilder.work;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TDWToolEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import com.dongyang.ui.datawindow.DText;
import javax.swing.JLabel;
import com.dongyang.ui.TComponent;
import com.dongyang.config.INode;
import com.dongyang.manager.TCM_Transform;

public class DWPanelControl extends TControl{
    public static final String DWT = "DWT";
    public static final String DW = "DW";
    /**
     * 文件名
     */
    private String filename;
    /**
     * 名称
     */
    private String name;
    /**
     * 初始化
     */
    public void onInit()
    {
        super.onInit();
        TParm parm = (TParm) getParameter();
        if(parm != null)
        {
            //得到名称
            name = parm.getValue("NAME");
            //得到文件名
            filename = parm.getValue("FILE_NAME");
            //设置名称
            callMessage("UI|setTitle|" + name);
            callFunction("UI|" + DWT + "|setFileName", filename);
        }
        callFunction("UI|" + DWT + "|addEventListener",TDWToolEvent.TEXT_EDIT_START,this,"onTextEditStart");
        callFunction("UI|" + DWT + "|addEventListener",TDWToolEvent.TEXT_EDIT_END,this,"onTextEditEnd");
        callFunction("UI|" + DWT + "|addEventListener",TDWToolEvent.SELECT_ONE_TEXT,this,"onSelectOneText");
        callFunction("UI|ModifyText|addEventListener",TTextFieldEvent.KEY_RELEASED,this,"onModifyText");
    }
    /**
     * 保存
     */
    public void onSave()
    {
        callFunction("UI|" + DWT + "|saveFile");
    }
    /**
     * 取消创建
     */
    public void onSOTBArrow()
    {
        callFunction("UI|" + DWT + "|setState","");
    }
    /**
     * 创建文本
     */
    public void onSOTBText()
    {
        callFunction("UI|" + DWT + "|setState","Create Text");
    }
    /**
     * 创建列
     */
    public void onSOTBColumn()
    {
        callFunction("UI|" + DWT + "|setState","Create Column");
    }
    /**
     * 创建线
     */
    public void onSOTBLine()
    {
        callFunction("UI|" + DWT + "|setState","Create Line");
    }
    /**
     * 创建图
     */
    public void onSOTBPicture()
    {
        callFunction("UI|" + DWT + "|setState","Create Picture");
    }
    /**
     * 创建按钮
     */
    public void onSOTBButton()
    {
        callFunction("UI|" + DWT + "|setState","Create Button");
    }
    /**
     * 创建组
     */
    public void onSOTBGroup()
    {
        callFunction("UI|" + DWT + "|setState","Create Group");
    }
    /**
     * 撤销
     */
    public void onUndo()
    {
        callFunction("UI|" + DWT + "|onUndo");
    }
    /**
     * 恢复
     */
    public void onRedo()
    {
        callFunction("UI|" + DWT + "|onRedo");
    }
    /**
     * 删除
     */
    public void onDelete()
    {
        callFunction("UI|" + DWT + "|onDelete");
    }
    /**
     * 文本编辑开始
     * @param text String
     */
    public void onTextEditStart(String text)
    {
        callFunction("UI|ModifyText|setEnabled",true);
        callFunction("UI|ModifyText|setText",text);
    }
    /**
     * 文本编辑停止
     */
    public void onTextEditEnd()
    {
        callFunction("UI|ModifyText|setEnabled",false);
        callFunction("UI|ModifyText|setText","");
    }
    /**
     * 编辑文本
     * @param text String
     */
    public void onModifyText(String text)
    {
        callFunction("UI|" + DWT + "|onEditText",text);
    }
    /**
     * 编辑字体
     */
    public void onModifyFont()
    {
        String font = (String)callFunction("UI|ModifyFont|getSelectedID");
        callFunction("UI|" + DWT + "|onEditFont",font);
    }
    /**
     * 编辑字体尺寸
     */
    public void onModifyFontSize()
    {
        String fontSize = (String)callFunction("UI|ModifyFontSize|getSelectedID");
        callFunction("UI|" + DWT + "|onEditFontSize",StringTool.getInt(fontSize));
    }
    /**
     * 选中一个文本
     * @param text DText
     */
    public void onSelectOneText(DText text)
    {
        callFunction("UI|ModifyFont|setText",text.getFont().getFontName());
        callFunction("UI|ModifyFontSize|setText","" + text.getFont().getSize());
        callFunction("UI|FontTypeB|setSelected",(text.getFont().getStyle()&1) == 1);
        callFunction("UI|FontTypeI|setSelected",(text.getFont().getStyle()&2) == 2);
    }
    /**
     * 居左
     */
    public void onAlignmentLeft()
    {
        callFunction("UI|" + DWT + "|editAlignment",JLabel.LEFT);
    }
    /**
     * 居中
     */
    public void onAlignmentCenter()
    {
        callFunction("UI|" + DWT + "|editAlignment",JLabel.CENTER);
    }
    /**
     * 居右
     */
    public void onAlignmentRight()
    {
        callFunction("UI|" + DWT + "|editAlignment",JLabel.RIGHT);
    }
    /**
     * 加粗
     */
    public void onFontTypeB()
    {
        boolean selected = (Boolean)callFunction("UI|FontTypeB|isSelected");
        callFunction("UI|" + DWT + "|onEditB",selected);
    }
    /**
     * 倾斜
     */
    public void onFontTypeI()
    {
        boolean selected = (Boolean)callFunction("UI|FontTypeI|isSelected");
        callFunction("UI|" + DWT + "|onEditI",selected);
    }
    /**
     * 翻页动作
     */
    public void onTABChangedAction()
    {
        TComponent com = (TComponent)callFunction("UI|TAB|getSelectedTComponent");
        if(com == null)
            return;
        if(DW.equals(com.getTag()))
        {
            INode node = (INode)callFunction("UI|" + DWT + "|getConfig");
            if(node == null)
                return;
            callFunction("UI|" + DW + "|setNodeData",node);
        }
        if("PSQL".equals(com.getTag()))
            initSQL();
        callFunction("UI|setMenuID",com.getTag());
        if(DW.equals(com.getTag()))
        {
            //初始化阅览尺寸
            initPriviewZoomChanged();
            //初始化缩放尺寸
            initPrintZoomChanged();
        }
    }
    /**
     * 打印设置
     */
    public void onPrintSetup()
    {
        callFunction("UI|" + DW + "|openPageDialog");
    }
    /**
     * 打印
     */
    public void onPrint()
    {
        callFunction("UI|" + DW + "|print");
    }
    /**
     * 阅览尺寸
     */
    public void onPriviewZoomChanged()
    {
        double zoom = (Integer)getValue("PriviewZoom");
        callFunction("UI|" + DW + "|setPriviewZoom",zoom);
    }
    /**
     * 初始化阅览尺寸
     */
    public void initPriviewZoomChanged()
    {
        int zoom = TCM_Transform.getInt(callFunction("UI|" + DW + "|getPriviewZoom"));
        setValue("PriviewZoom",zoom);
    }
    /**
     * 打印缩放尺寸
     */
    public void onPrintZoomChanged()
    {
        double zoom = (Integer)getValue("PrintZoom");
        callFunction("UI|" + DW + "|setPrintZoom",zoom);
    }
    /**
     * 初始化缩放尺寸
     */
    public void initPrintZoomChanged()
    {
        int zoom = TCM_Transform.getInt(callFunction("UI|" + DW + "|getPrintZoom"));
        setValue("PrintZoom",zoom);
    }
    /**
     * 提交
     */
    public void onSQLCommit()
    {
        String sql = getText("TSQL");
        callFunction("UI|ViewDataWindow|loadSQL",sql);
    }
    /**
     * 查询数据
     */
    public void onSQLRetrieve()
    {
        int count = (Integer)callFunction("UI|ViewDataWindow|retrieve");
        if(count < 0)
        {
            String err = (String)callFunction("UI|ViewDataWindow|getErrText");
            messageBox_(err);
        }
    }
    /**
     * 创建
     */
    public void onSQLCreate()
    {
        String sql = getText("TSQL");
        callFunction("UI|" + DWT + "|load",sql);
    }
    /**
     * 编辑SQL语句
     */
    public void onSQLEdit()
    {
        String sql = getText("TSQL");
        callFunction("UI|" + DWT + "|setSql",sql);
    }
    /**
     * 设置SQL语句
     */
    public void initSQL()
    {
        String sql = (String)callFunction("UI|" + DWT + "|getSql");
        setText("TSQL",sql);
    }
}
