package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TDPanel;
import com.dongyang.tui.DText;
import com.dongyang.tui.text.MFocus;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EText;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.ETable;
import com.dongyang.tui.text.ETR;
import com.dongyang.tui.text.ETD;
import com.dongyang.tui.text.DString;
import com.dongyang.util.TypeTool;
import com.dongyang.tui.text.ui.CTable;
import com.dongyang.data.TParm;
import com.dongyang.ui.TFrame;
import com.dongyang.tui.text.MFile;
import com.dongyang.tui.text.MSyntax;
import com.dongyang.tui.text.MPage;
import com.dongyang.ui.TWindow;
import com.dongyang.tui.text.EPic;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.EFixed;
import com.dongyang.ui.TShowZoomCombo;
import com.dongyang.ui.TWord;
import com.dongyang.ui.TToolButton;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TPanel;
import com.dongyang.tui.text.EPage;


/**
 *
 * <p>Title: 报表工具控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.3.30
 * @version 1.0
 */
public class WordControl extends TControl{
    //主面板对象
    private TWord word;
    private DText text;
    /**
     * 初始化
     */
    public void onInit()
    {
        word = (TWord)getComponent("WORD");
        text = word.getWordText();
        word.setShowZoomComboTag("ShowZoom");
        word.setFontComboTag("ModifyFontCombo");
        word.setFontSizeComboTag("ModifyFontSizeCombo");
        word.setFontBoldButtonTag("FontBMenu");
        word.setFontItalicButtonTag("FontIMenu");
        word.setAlignmentLeftButtonTag("AlignmentLeft");
        word.setAlignmentCenterButtonTag("AlignmentCenter");
        word.setAlignmentRightButtonTag("AlignmentRight");
    }
    /**
     * 初始化面板
     */
    /*public void initPanel()
    {
        text = new DText();
        text.setTag("text");
        text.setBorder("凹");
        text.setAutoBaseSize(true);
        panel.addDComponent(text);
    }*/
    /**
     * 插入Table
     */
    public void onInsertTableBase()
    {
        word.insertBaseTableDialog();
    }
    /**
     * 插入表格
     */
    public void onInsertTable()
    {
        Object obj[] = (Object[])openDialog("%ROOT%\\config\\database\\InsertTableDialog.x");
        if(obj == null)
            return;
        int columnCount = TypeTool.getInt(obj[0]);
        int rowCount = TypeTool.getInt(obj[1]);
        String sql = TypeTool.getString(obj[2]);
        boolean isRetrieve = TypeTool.getBoolean(obj[3]);
        boolean isSum = TypeTool.getBoolean(obj[4]);
        int insertDataRow = TypeTool.getInt(obj[5]);
        TParm group = (TParm)obj[6];
        boolean groupShowData = (Boolean)obj[7];
        boolean inputData = TypeTool.getBoolean(obj[8]);
        String tableID = TypeTool.getString(obj[9]);
        MFocus focusManager = text.getFocusManager();
        if(focusManager == null)
            return;
        ETable table = focusManager.insertTable(rowCount, columnCount);
        if(inputData || sql.length() > 0)
        {
            CTable cTable = table.createControl();
            //设置TableID
            cTable.setTableID(tableID);
            //设置SQL
            cTable.setSQL(sql);
            //设置传入数据
            cTable.setInputData(inputData);
            //设置是否显示总计
            cTable.setHasSum(isSum);
            //设置插入数据的行号
            cTable.setInsertDataRow(insertDataRow);
            //设置分组
            cTable.setGroup(group);
            //分组显示基础数据
            cTable.setGroupShowData(groupShowData);
            //初始化
            cTable.init();
        }
        focusManager.update();
    }
    /**
     * 插入宏
     */
    public void onInsertMacroroutine()
    {
        Object value[] = (Object[])openDialog("%ROOT%\\config\\database\\MacroroutineEditDialog.x",
                new Object[]{"<none>","",1,false,0,0,"",""});
        if(value == null)
            return;
        MFocus focusManager = text.getFocusManager();
        if(focusManager == null)
            return;
        //插入宏
        EMacroroutine macroroutine = focusManager.insertMacroroutine(TypeTool.getString(value[0]),TypeTool.getString(value[1]),TypeTool.getInt(value[2]));
        macroroutine.getModel().setLockSize(TypeTool.getBoolean(value[3]));
        macroroutine.getModel().setWidth(TypeTool.getInt(value[4]));
        macroroutine.getModel().setHeight(TypeTool.getInt(value[5]));

        macroroutine.getModel().setGroupName(TypeTool.getString(value[6]));
        macroroutine.getModel().setCdaName(TypeTool.getString(value[7]));

        //System.out.println("group name===="+TypeTool.getString(value[6]));
        //System.out.println("cda name===="+TypeTool.getString(value[7]));

        //更新
        focusManager.update();
    }
    /**
     * 察看宏脚本
     */
    public void onLookScript()
    {
        String script = text.getPM().getSyntaxManager().createScript();
        openDialog("%ROOT%\\config\\database\\LookScriptDialog.x",script);
    }
    /**
     * 察看宏错误
     */
    public void onLookScriptErr()
    {
        String err = text.getPM().getSyntaxManager().getMakeErrText();
        openDialog("%ROOT%\\config\\database\\LookScriptErrDialog.x",err);
    }
    /**
     * 运行宏
     */
    public void onRunMacroroutine()
    {
        text.getPM().getMacroroutineManager().run();
        text.getPM().getFocusManager().update();
    }
    /**
     * 编辑宏
     */
    public void onEditMacroroutine()
    {
        text.getPM().getMacroroutineManager().edit();
        text.getPM().getFocusManager().update();
    }
    /**
     * 删除宏
     */
    public void onDeleteMacroroutine()
    {
        EComponent com = text.getFocusManager().getFocus();
        if(!(com instanceof EMacroroutine))
            return;
        ((EMacroroutine)com).delete();
        text.getPM().getFocusManager().update();
    }
    /**
     * 保存
     */
    public void onSave()
    {
        text.getPM().getFileManager().onSave();
    }
    /**
     * 另存为
     */
    public void onSaveAs()
    {
        text.getPM().getFileManager().onSaveAsDialog();
    }
    /**
     * 打开文件
     */
    public void onOpenFile()
    {
        text.getPM().getFileManager().onOpenDialog();
    }
    /**
     * 页面设置
     */
    public void onPageSetup()
    {
        openDialog("%ROOT%\\config\\database\\PageSetupDialog.x",word.getPageManager());
    }
    /**
     * 删除表格
     */
    public void onDeleteTable()
    {
        word.deleteTable();
    }
    /**
     * 插入表格行
     */
    public void onInsertTR()
    {
        word.insertTR();
    }
    /**
     * 追加表格行
     */
    public void onAppendTR()
    {
        word.appendTR();
    }
    /**
     * 删除表格行
     */
    public void onDeleteTR()
    {
        word.deleteTR();
    }
    /**
     * 自定义脚本对话框
     */
    public void onCustomScript()
    {
        word.onCustomScriptDialog();
    }
    /**
     * 查询表格
     */
    public void onRetrieveTable()
    {
        text.getPM().getMacroroutineManager().setIsRun(true);
        ETable table = text.getPM().getFocusManager().getFocusTable();
        if(table == null)
            return;
        CTable ctable = table.getCTable();
        if(ctable == null)
            return;
        ctable.retrieve();
        ctable.showData();
        text.getPM().getFocusManager().update();
    }
    /**
     * 编辑表格
     */
    public void onEditTable()
    {
        text.getPM().getMacroroutineManager().setIsRun(false);
        ETable table = text.getPM().getFocusManager().getFocusTable();
        if(table == null)
            return;
        CTable ctable = table.getCTable();
        if(ctable == null)
            return;
        //显示编辑状态
        ctable.showEdit();
        text.getPM().getFocusManager().update();
    }
    /**
     * 打印设置对话框
     */
    public void onPrintSetup()
    {
        word.printSetup();
    }
    /**
     * 续印
     */
    public void onPrintXDDialog()
    {
        word.printXDDialog();
    }
    /**
     * 显示行号开关
     */
    public void onShowRowIDSwitch()
    {
        word.setShowRowID(!word.isShowRowID());
        word.update();
    }
    /**
     * 打印
     */
    public void onPrint()
    {
        word.print();
    }
    /**
     * 页面设置对话框
     */
    public void onPrintDialog()
    {
        word.printDialog();
    }
    /**
     * 段落
     */
    public void onParagraphEdit()
    {
        word.onParagraphDialog();
    }
    /**
     * 插入图区
     */
    public void onInsertPic()
    {
        MFocus focusManager = text.getFocusManager();
        if(focusManager == null)
            return;
        //插入图区
        focusManager.insertPic();
        //更新
        focusManager.update();
    }
    /**
     * Table属性
     */
    public void onTableProperty()
    {
        ETable table = text.getPM().getFocusManager().getFocusTable();
        if(table == null)
            return;
        openDialog("%ROOT%\\config\\database\\TablePropertyDialog.x",table);
    }
    /**
     * TR属性
     */
    public void onTRProperty()
    {
        ETR tr = text.getPM().getFocusManager().getFocusTR();
        if(tr == null)
            return;
        openDialog("%ROOT%\\config\\database\\TRPropertyDialog.x",tr);
    }
    /**
     * 编辑
     */
    public void onEditWord()
    {
        word.onEditWord();
    }
    /**
     * 阅览
     */
    public void onPreviewWord()
    {
        word.onPreviewWord();
    }
    /**
     * 新建
     */
    public void onNewFile()
    {
        text.getFileManager().onNewFile();
    }
    /**
     * 文件属性
     */
    public void onFileProperty()
    {
        MFile file = text.getPM().getFileManager();
        if(file == null)
            return;
        openDialog("%ROOT%\\config\\database\\FileProperty.x",file);
    }
    /**
     * 打印预览窗口
     */
    public void onPreviewWindow()
    {
        MFile file = text.getPM().getFileManager();
        if(file == null)
            return;
        if(!file.isOpen())
        {
            messageBox("文件没有保存!");
            return;
        }
        openPrintWindow(file.getPath() + "\\" + file.getFileName(),"DEFAULT");
    }
    /**
     * 调试语法
     */
    public void onDebugSyntax()
    {
        MSyntax msyntax = text.getPM().getSyntaxManager();
        openDialog("%ROOT%\\config\\database\\DebugSyntax.x",msyntax);
    }
    /**
     * 图层控制
     */
    public void onDIVProperty()
    {
        text.getPM().getPageManager().getMVList().openProperty();
    }
    /**
     * 删除图区
     */
    public void onDeletePic()
    {
        EComponent com = text.getFocusManager().getFocus();
        if(!(com instanceof EPic))
            return;
        ((EPic)com).removeThis();
        text.getPM().getFocusManager().update();
    }
    /**
     * 图区图层控制
     */
    public void onPicDIVProperty()
    {
        EComponent com = text.getFocusManager().getFocus();
        if(!(com instanceof EPic))
            return;
        ((EPic)com).getMVList().openProperty();
    }
    /**
     * 图区图表控制
     */
    public void onPicDataProperty()
    {
        EComponent com = text.getFocusManager().getFocus();
        if(!(com instanceof EPic))
            return;
        ((EPic)com).openDataProperty();
    }
    /**
     * 宏图层控制
     */
    public void onMacroroutineDIVProperty()
    {
        EComponent com = text.getFocusManager().getFocus();
        if(!(com instanceof EMacroroutine))
            return;
        EMacroroutine m = (EMacroroutine)com;
        if(m.getModel() == null)
            return;
        m.getModel().getMVList().openProperty();
    }
    /**
     * 调试Table
     */
    public void onDebugTable()
    {
        ETable table = text.getPM().getFocusManager().getFocusTable();
        if(table == null)
            return;
        table.debugModify(2);
    }
    /**
     * 显示全部对象
     */
    public void onDebugShowObject()
    {
        text.getPM().getPageManager().debugShow();
    }
    /**
     * 显示修改对象
     */
    public void onDebugShowModify()
    {
        text.getPM().getPageManager().debugModify();
    }
    /**
     * 调试选蓝对象
     */
    public void onDebugShowSelected()
    {
        text.getPM().getFocusManager().getSelectedModel().debugShow();
    }
    /**
     * 插入固定文本
     */
    public void onInsertFixedObject()
    {
        word.insertFixed();
    }
    /**
     * 插入单选
     */
    public void onInsertSingleChooseObject()
    {
        word.insertSingleChoose();
    }
    /**
     * 插入多选
     */
    public void onInsertMultiChooseObject()
    {
        word.insertMultiChoose();
    }
    /**
     * 插入有无选择
     */
    public void onInsertHasChooseObject()
    {
        word.insertHasChoose();
    }
    /**
     * 插入输入提示组件
     */
    public void onInsertInputTextObject()
    {
        word.insertInputText();
    }
    /**
     * 插入宏
     */
    public void onInsertMicroFieldObject()
    {
        word.insertMicroField();
    }
    /**
     * 插入抓取
     */
    public void onInsertCaptureObject()
    {
        word.insertCaptureObject();
    }
    /**
     * 插入选择框
     */
    public void onInsertCheckBoxChooseObject()
    {
        word.insertCheckBoxChoose();
    }
    /**
     * 插入图片
     */
    public void onInsertPictureObject()
    {
        word.insertPicture();
    }
    /**
     * 固定文本属性
     */
    public void onOpenFixedProperty()
    {
        word.onOpenFixedProperty();
    }
    /**
     * 删除固定文本
     */
    public void onRemoveFixed()
    {
        word.deleteFixed();
    }
    /**
     * 宏数据属性
     */
    public void OpenMicroFieldProperty()
    {
        word.onOpenMicroFieldProperty();
    }
    /**
     * 剪切
     */
    public void onCutMenu()
    {
        word.onCut();
    }
    /**
     * 复制
     */
    public void onCopyMenu()
    {
        word.onCopy();
    }
    /**
     * 粘贴
     */
    public void onPasteMenu()
    {
        word.onPaste();
    }
    /**
     * 删除
     */
    public void onDeleteMenu()
    {
        word.onDelete();
    }
    /**
     * 抓取测试对话框
     */
    public void onCaptureDataProperty()
    {
        openDialog("%ROOT%\\config\\database\\CaptureDataDialog.x",word);
    }
    /**
     * 调试窗口
     */
    public void onDebugDialog()
    {
        openDialog("%ROOT%\\config\\database\\DebugDialog.x",word);
    }
    /**
     * 合并单元格
     */
    public void onUniteTD()
    {
        word.onUniteTD();
    }
    /**
     * 数字签字
     */
    public void onKeyWordMenu()
    {
        openDialog("%ROOT%\\config\\database\\KeyWordDialog.x",word);
    }
    /**
     * 数字选择
     */
    public void onInsertNumberChooseObject()
    {
        word.insertNumberChoose();
    }
    /**
     * 插入文件
     */
    public void onInsertFile()
    {
        //word.onInsertFileFrontFixed("AAA","%ROOT%\\config\\prt","a1.jhw",1,false);
        word.onInsertFileDialog();
    }
    /*public void onFontBMenu()
    {
        TToolButton button = (TToolButton)getComponent("FontBMenu");
        System.out.println(button.isSelected());
        button.setSelected(!button.isSelected());
        word.modifyBold(button.isSelected());
        System.out.println(getComponent("FontBMenu").getClass().getName());

        System.out.println("xxxxxxxxxxxxx");
    }*/
    /**
     * 插入图片编辑区
     */
    public void onInsertImageEdit()
    {
        word.insertImage();
    }
    /**
     * 删除图片编辑区
     */
    public void onDeleteImageEdit()
    {
        word.deleteImage();
    }
    /**
     * 插入块
     */
    public void onInsertGBlock()
    {
        word.insertGBlock();
    }
    /**
     * 插入线
     */
    public void onInsertGLine()
    {
        word.insertGLine();
    }
    /**
     * 调整块尺寸
     * @param index int
     */
    public void onSizeBlockMenu(String index)
    {
        word.onSizeBlockMenu(StringTool.getInt(index));
    }
    /**
     * 表格前插入空行
     */
    public void onTableInsertPanel()
    {
        ETable table = word.getFocusManager().getFocusTable();
        if(table == null)
            return;
        EPanel panel = table.getPanel();
        int index = panel.findIndex();
        EPage page = panel.getPage();
        EPanel p = page.newPanel(index);
        p.newText();
        word.update();
    }
    public static void main(String args[])
    {
        TFrame frame = com.javahis.util.JavaHisDebug.runFrame("database\\word.x");
        //TFrame frame = com.javahis.util.JavaHisDebug.runFrame("ekt\\aaa.x");
        //com.javahis.util.JavaHisDebug.runWindow("database\\OpenDialog.x",frame);
        //com.javahis.util.JavaHisDebug.runWindow("aaa\\aaa.x",frame);
        //com.javahis.util.JavaHisDebug.runDialog("database\\word.x");
    }
}
