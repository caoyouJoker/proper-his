package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import com.dongyang.ui.TWord;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TFontCombo;
import com.dongyang.ui.TFontSizeCombo;
import com.dongyang.ui.TToolButton;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.util.Date;
import jdo.sys.Operator;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.IBlock;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.EMicroField;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EInputText;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.ENumberChoose;
import com.dongyang.tui.text.EMultiChoose;
import com.dongyang.tui.text.ETextFormat;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.tui.text.EHasChoose;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.text.ETable;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EText;
import org.w3c.dom.Node;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.EImage;
import com.dongyang.tui.text.div.VPic;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EMREditComPhraseControl
    extends TControl {
    /**
     * 拿到TWORD对象
     */
    private static final String WORD = "WORD";
    /**
     * 操作类型，入参
     */
    private static final String REQUEST_PARAM_OPTYPE = "opType";
    /**
     * 片语文件路径
     */
    private static final String REQUEST_PARAM_PHRASE_FILE_PATH =
        "phraseFilePath";
    /**
     * 片语文件名
     */
    private static final String REQUEST_PARAM_PHRASE_FILE_NAME =
        "phraseFileName";
    /**
     *
     */
    private static final String REQUEST_PARAM_PHRASE_ID =
        "classCode";


    /**
     * 新增
     */
    private static final String NEW_OP = "NEW";
    /**
     * 编辑
     */
    private static final String EDIT_OP = "EDIT";


    /**
     * 操作类型
     */
    private String strOpType = "";
    /**
     * 片语文件路径;
     */
    private String strPhraseFilePath = "";
    /**
     * 片语文件名称;
     */
    private String strPhraseFileName = "";

    private String strPhraseID = "";


    /**
     * 当前模版状态
     */
    private String onlyType;


    /**
     * 当前编辑文件
     */
    private String onlyEditfileName = "";


    /**
     * WORD对象
     */
    private TWord word;

    public EMREditComPhraseControl() {
    }

    public void onInit() {
        //
        super.onInit();
        //初始化WORD
        initWord();
        //取得参数;
        TParm inParm = (TParm)this.getParameter();
        //
        strOpType = inParm.getValue(REQUEST_PARAM_OPTYPE);
        strPhraseFilePath = inParm.getValue(REQUEST_PARAM_PHRASE_FILE_PATH);
        strPhraseFileName = inParm.getValue(REQUEST_PARAM_PHRASE_FILE_NAME);
        strPhraseID = inParm.getValue(REQUEST_PARAM_PHRASE_ID);

        /**System.out.println("===strOpType====" + strOpType);
         System.out.println("===strPhraseFilePath====" + strPhraseFilePath);
         System.out.println("===strPhraseFileName====" + strPhraseFileName);
                 System.out.println("===strPhraseID====" + strPhraseID);**/
        //
        if (strOpType.equals(NEW_OP)) {
            //创建文件；
            onlyEditfileName = strPhraseFileName;
            this.setOnlyType(NEW_OP);
            if (!onSave()) {
                return;
            }

        }
        //打开EMR结构化片语
        onEditTemplet();

    }

    /**
     * 保存
     */
    public boolean onSave() {
        //保存
        if (onlyEditfileName.length() != 0) {
            word.setMessageBoxSwitch(false);
            //设置文件作者
            word.setFileAuthor(Operator.getID());
            //日期
            String dateStr = StringTool.getString(StringTool.getTimestamp(new
                Date()), "yyyy年MM月dd日 HH时mm分ss秒");
            //设置创建日期
            word.setFileCreateDate(dateStr);
            //设置公司信息
            word.setFileCo("JAVAHIS");
            //设置文件标题
            word.setFileTitle(strPhraseFileName);
            //保存
            if (word.onSaveAs(strPhraseFilePath,
                              strPhraseFileName, 2)) {

                //取word纯文本，保存;

                String strWord=getWordString();
                //this.messageBox("str==" + strWord);

                String sql = "UPDATE OPD_COMTEMPLATE_PHRASE SET FILE_PATH='" +
                    strPhraseFilePath + "',";
                    sql+="FILE_NAME='" +strPhraseFileName + "',";
                    sql+="TEMPLATE_DESC='" +strWord + "'";
                    sql+=" WHERE CLASS_CODE='" + strPhraseID + "'";

                //System.out.println("==========sql=========="+sql);
                TParm parm = new TParm(this.getDBTool().update(sql));

                if (parm.getErrCode() < 0) {
                    this.messageBox("保存失败！");
                    word.setMessageBoxSwitch(true);
                    //删除文件
                    delFileTempletFile(word.getFileOpenPath(),
                                       strPhraseFileName);

                    return false;
                }
                else {
                    //不是新增
                    if (!this.getOnlyType().equals(NEW_OP)) {
                        word.setMessageBoxSwitch(true);
                        this.messageBox("保存成功！");
                    }

                }

                this.setOnlyType(EDIT_OP);
                return true;
            }
            else {
                this.messageBox("保存失败！");
                word.setMessageBoxSwitch(true);
                return false;
            }

        }
        else {
            this.messageBox("没有需要保存的模版！");
            word.setMessageBoxSwitch(true);
            return true;
        }

    }

    /**
     * 删除模版文件
     * @param templetPath String
     * @param templetName String
     * @return boolean
     */
    public boolean delFileTempletFile(String templetPath, String templetName) {
        //目录表第一个根目录FILESERVER
        String rootName = TIOM_FileServer.getRoot();
        //模板路径服务器
        String templetPathSer = TIOM_FileServer.getPath("EmrTemplet");
        //拿到Socket通讯工具
        TSocket socket = TIOM_FileServer.getSocket();
        //删除文件
        return TIOM_FileServer.deleteFile(socket,
                                          rootName + templetPathSer +
                                          templetPath +
                                          "\\" + templetName + ".jhw");
    }


    /**
     * 初始化WORD
     */
    public void initWord() {
        word = this.getTWord(WORD);
        //缩放
        word.setShowZoomComboTag("ShowZoom");
        //字体
        word.setFontComboTag("ModifyFontCombo");
        //字体
        word.setFontSizeComboTag("ModifyFontSizeCombo");
        //变粗
        word.setFontBoldButtonTag("FontBMenu");
        //斜体
        word.setFontItalicButtonTag("FontIMenu");
        //取消编辑
        this.word.setCanEdit(false);
        //初始化菜单
        initMenu();
    }

    /**
     * 初始化菜单
     */
    public void initMenu() {
        if (!this.word.canEdit()) {
            this.getTFontCombo("ModifyFontCombo").setEnabled(false);
            this.getTFontSizeCombo("ModifyFontSizeCombo").setEnabled(false);
            this.getTToolButton("AlignmentLeft").setEnabled(false);
            this.getTToolButton("AlignmentCenter").setEnabled(false);
            this.getTToolButton("AlignmentRight").setEnabled(false);
            this.getTMenuItem("insertFixText").setEnabled(false);
            this.getTMenuItem("FixTextProperty").setEnabled(false);
            this.getTMenuItem("InsertSingleChoose").setEnabled(false);
            this.getTMenuItem("InsertRelateChoose").setEnabled(false);
            this.getTMenuItem("InsertMultiChoose").setEnabled(false);
            this.getTMenuItem("InsertHasChoose").setEnabled(false);
            this.getTMenuItem("InsertMicroField").setEnabled(false);
            this.getTMenuItem("MicroFieldProperty").setEnabled(false);
            this.getTMenuItem("InsertTable").setEnabled(false);
            this.getTMenuItem("DelTable").setEnabled(false);
            this.getTMenuItem("InsertTableRow").setEnabled(false);
            this.getTMenuItem("AddTableRow").setEnabled(false);
            this.getTMenuItem("DelTableRow").setEnabled(false);
            this.getTMenuItem("delFixText").setEnabled(false);
            this.getTMenuItem("addDLText").setEnabled(false);
            this.getTMenuItem("TableProperty").setEnabled(false);
            this.getTMenuItem("addInputMessage").setEnabled(false);
            this.getTMenuItem("InsertCaptureObject").setEnabled(false);
            this.getTMenuItem("CaptureDataProperty").setEnabled(false);
            this.getTMenuItem("InsertCheckBoxChooseObject").setEnabled(false);
            this.getTMenuItem("CustomScriptDialog").setEnabled(false);
            this.getTMenuItem("InsertPictureObject").setEnabled(false);
            this.getTMenuItem("InsertNumberChooseObject").setEnabled(false);
            this.getTMenuItem("CutMenu").setEnabled(false);
            this.getTMenuItem("CopyMenu").setEnabled(false);
            this.getTMenuItem("PasteMenu").setEnabled(false);
            this.getTMenuItem("DeleteMenu").setEnabled(false);
            this.getTMenuItem("DIVProperty").setEnabled(false);
        }
        else {
            this.getTFontCombo("ModifyFontCombo").setEnabled(true);
            this.getTFontSizeCombo("ModifyFontSizeCombo").setEnabled(true);
            this.getTToolButton("AlignmentLeft").setEnabled(true);
            this.getTToolButton("AlignmentCenter").setEnabled(true);
            this.getTToolButton("AlignmentRight").setEnabled(true);
            this.getTMenuItem("insertFixText").setEnabled(true);
            this.getTMenuItem("FixTextProperty").setEnabled(true);
            this.getTMenuItem("InsertSingleChoose").setEnabled(true);
            this.getTMenuItem("InsertRelateChoose").setEnabled(true);
            this.getTMenuItem("InsertMultiChoose").setEnabled(true);
            this.getTMenuItem("InsertHasChoose").setEnabled(true);
            this.getTMenuItem("InsertMicroField").setEnabled(true);
            this.getTMenuItem("MicroFieldProperty").setEnabled(true);
            this.getTMenuItem("InsertTable").setEnabled(true);
            this.getTMenuItem("DelTable").setEnabled(true);
            this.getTMenuItem("InsertTableRow").setEnabled(true);
            this.getTMenuItem("AddTableRow").setEnabled(true);
            this.getTMenuItem("DelTableRow").setEnabled(true);
            this.getTMenuItem("delFixText").setEnabled(true);
            this.getTMenuItem("addDLText").setEnabled(true);
            this.getTMenuItem("TableProperty").setEnabled(true);
            this.getTMenuItem("addInputMessage").setEnabled(true);
            this.getTMenuItem("InsertCaptureObject").setEnabled(true);
            this.getTMenuItem("CaptureDataProperty").setEnabled(true);
            this.getTMenuItem("InsertCheckBoxChooseObject").setEnabled(true);
            this.getTMenuItem("CustomScriptDialog").setEnabled(true);
            this.getTMenuItem("InsertPictureObject").setEnabled(true);
            this.getTMenuItem("InsertNumberChooseObject").setEnabled(true);
            this.getTMenuItem("CutMenu").setEnabled(true);
            this.getTMenuItem("CopyMenu").setEnabled(true);
            this.getTMenuItem("PasteMenu").setEnabled(true);
            this.getTMenuItem("DeleteMenu").setEnabled(true);
            this.getTMenuItem("DIVProperty").setEnabled(true);
        }
    }

    /**
     * 拿到菜单
     * @param tag String
     * @return TMenuItem
     */
    public TMenuItem getTMenuItem(String tag) {
        return (TMenuItem)this.getComponent(tag);
    }

    /**
     * 拿到字体Combo
     * @param tag String
     * @return TFontCombo
     */
    public TFontCombo getTFontCombo(String tag) {
        return (TFontCombo)this.getComponent(tag);
    }

    /**
     * 拿到字体大小Combo
     * @param tag String
     * @return TFontSizeCombo
     */
    public TFontSizeCombo getTFontSizeCombo(String tag) {
        return (TFontSizeCombo)this.getComponent(tag);
    }

    /**
     * 拿到TToolButton
     * @param tag String
     * @return TToolButton
     */
    public TToolButton getTToolButton(String tag) {
        return (TToolButton)this.getComponent(tag);
    }


    /**
     * 得到WORD对象
     * @param tag String
     * @return TWord
     */
    public TWord getTWord(String tag) {
        return (TWord)this.getComponent(tag);
    }

    /**
     * 编辑模版
     */
    public void onEditTemplet() {
        //打开模版
        this.onOpenTemplet();
        //编辑状态(非整洁)
        this.word.onEditWord();
        //设置可编辑
        this.word.setCanEdit(true);
        //编辑状态
        this.setOnlyType("EDIT");
        //初始化菜单
        initMenu();

    }

    /**
     * 打开模版
     */
    public void onOpenTemplet() {

        word.onOpen(strPhraseFilePath, strPhraseFileName, 2, false);
        this.word.setCanEdit(false);
        onlyEditfileName = strPhraseFileName;
        this.setOnlyType("OPEN");
        //初始化菜单
        initMenu();

    }

    public String getOnlyType() {
        return onlyType;
    }

    public void setOnlyType(String onlyType) {
        this.onlyType = onlyType;
    }

    /**
     * 删除固定文本
     */
    public void onDelFixText() {
        if (onlyEditfileName.length() != 0) {
            word.deleteFixed();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 固定文本属性
     */
    public void onFixTextProperty() {
        if (onlyEditfileName.length() != 0) {
            word.onOpenFixedProperty();
        }
        else {
            this.messageBox("请选择模版！");
        }
        setCapName();
    }

    private void setCapName() {
        EComponent com = word.getFocusManager().getFocus();
        if (! (com instanceof ECapture)) {
            return;
        }
        ECapture capture = (ECapture) com;
        if (capture.getCaptureType() == 0) {
            setNCapName(capture);
        }
        else {
            setPCapName(capture);
        }
        word.getFileManager().update();
    }

    private void setPCapName(ECapture capture) {
        IBlock block = capture.getPreviousTrueBlock();
        while (block != null) {
            if (isSetStartCapture(block)) {
                ( (ECapture) block).setName(capture.getName());
                return;
            }
            IBlock preBlock = block.getPreviousTrueBlock();
            if (preBlock != null) {
                block = preBlock;
                continue;
            }
            EPanel panel = block.getPanel().getPreviousTruePanel();
            if (panel == null || panel.size() == 0) {
                return;
            }
            block = panel.get(0);
        }
    }

    private boolean isSetStartCapture(IBlock block) {
        if (block == null) {
            return false;
        }
        if (block.getObjectType() != EComponent.CAPTURE_TYPE) {
            return false;
        }
        ECapture capture = (ECapture) block;
        if (capture.getCaptureType() != 0) {
            return false;
        }
        return true;
    }

    private void setNCapName(ECapture capture) {
        IBlock block = capture.getNextTrueBlock();
        while (block != null) {
            if (isSetEndCapture(block)) {
                ( (ECapture) block).setName(capture.getName());
                return;
            }
            IBlock nextBlock = block.getNextTrueBlock();
            if (nextBlock != null) {
                block = nextBlock;
                continue;
            }
            EPanel panel = block.getPanel().getNextTruePanel();
            if (panel == null || panel.size() == 0) {
                return;
            }
            block = panel.get(0);
        }
    }

    private boolean isSetEndCapture(IBlock block) {
        if (block == null) {
            return false;
        }
        if (block.getObjectType() != EComponent.CAPTURE_TYPE) {
            return false;
        }
        ECapture capture = (ECapture) block;
        if (capture.getCaptureType() != 1) {
            return false;
        }
        return true;
    }

    /**
     * 图层控制
     */
    public void onDIVProperty() {
        if (onlyEditfileName.length() != 0) {
            word.onDIVProperty();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 宏属性
     */
    public void onMicroFieldProperty() {
        if (onlyEditfileName.length() != 0) {
            word.onOpenMicroFieldProperty();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 居左
     */
    public void onAlignmentLeft() {
        if (onlyEditfileName.length() != 0) {
            word.setAlignment(0);
        }
        else {
            return;
        }

    }

    /**
     * 居中
     */
    public void onAlignmentCenter() {
        if (onlyEditfileName.length() != 0) {
            word.setAlignment(1);
        }
        else {
            return;
        }

    }

    /**
     * 居右
     */
    public void onAlignmentRight() {
        if (onlyEditfileName.length() != 0) {
            word.setAlignment(2);
        }
        else {
            return;
        }
    }

    /**
     * 加入段落
     */
    public void onAddDLText() {
        if (onlyEditfileName.length() != 0) {
            word.onParagraphDialog();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 剪切
     */
    public void onCutMenu() {
        if (onlyEditfileName.length() != 0) {
            word.onCut();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 复制
     */
    public void onCopyMenu() {
        if (onlyEditfileName.length() != 0) {
            word.onCopy();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 粘贴
     */
    public void onPasteMenu() {
        if (onlyEditfileName.length() != 0) {
            word.onPaste();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 删除
     */
    public void onDeleteMenu() {
        if (onlyEditfileName.length() != 0) {
            word.onDelete();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入固定文本
     */
    public void onInsertFixText() {
        if (onlyEditfileName.length() != 0) {
            word.insertFixed();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入单选
     */
    public void onInsertSingleChoose() {
        if (onlyEditfileName.length() != 0) {
            word.insertSingleChoose();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入多选
     */
    public void onInsertMultiChoose() {
        if (onlyEditfileName.length() != 0) {
            word.insertMultiChoose();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 有无选择
     */
    public void onInsertHasChoose() {
        if (onlyEditfileName.length() != 0) {
            word.insertHasChoose();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入宏
     */
    public void onInsertMicroField() {
        if (onlyEditfileName.length() != 0) {
            word.insertMicroField();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入表格
     */
    public void onInsertTable() {
        if (onlyEditfileName.length() != 0) {
            ETable etable = word.insertBaseTableDialog();
            //System.out.println("===etable ==="+etable.isShowBorder());
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 删除表格
     */
    public void onDelTable() {
        if (onlyEditfileName.length() != 0) {
            word.deleteTable();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入表格行
     */
    public void onInsertTableRow() {
        if (onlyEditfileName.length() != 0) {
            word.insertTR();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 追加表格行
     */
    public void onAddTableRow() {
        if (onlyEditfileName.length() != 0) {
            word.appendTR();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 删除表格行
     */
    public void onDelTableRow() {
        if (onlyEditfileName.length() != 0) {
            word.deleteTR();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }


    /**
     * 插入输入提示组件
     */
    public void onAddInputMessage() {
        if (onlyEditfileName.length() != 0) {
            word.insertInputText();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 表格属性
     */
    public void onTableProperty() {
        if (onlyEditfileName.length() != 0) {
            word.onTablePropertyDialog();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }


    /**
     * 插入抓取
     */
    public void onInsertCaptureObject() {
        if (onlyEditfileName.length() != 0) {
            ECapture obj = word.insertCaptureObject();
            word.insertCaptureObject();
            obj.setCaptureType(1);
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 抓取测试对话框//String word.getCaptureValue(String name)抓取方法
     */
    public void onCaptureDataProperty() {
        openDialog("%ROOT%\\config\\database\\CaptureDataDialog.x", word);
    }

    /**
     * 选择框
     */
    public void onInsertCheckBoxChooseObject() {
        if (onlyEditfileName.length() != 0) {
            word.insertCheckBoxChoose();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 自定义脚本
     */
    public void onCustomScriptDialog() {
        if (onlyEditfileName.length() != 0) {
            word.onCustomScriptDialog();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入图片
     */
    public void onInsertPictureObject() {
        if (onlyEditfileName.length() != 0) {
            word.insertPicture();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 数字选择
     */
    public void onInsertNumberChooseObject() {
        if (onlyEditfileName.length() != 0) {
            word.insertNumberChoose();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 下拉框
     */
    public void onInsertETextFormatObject() {
        if (onlyEditfileName.length() != 0) {
            word.insertTextFormat();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入病历元素;
     */
    public void onInserElement() {
        //传入文档类型参数；
        String documentType = null;
        //this.messageBox("documentType==="+documentType);
        TParm inParm = new TParm();
        inParm.setData("DOCUMENT_TYPE", documentType);

        //打开插入病历元素窗体；
        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\database\\InsertElementDialog.x", inParm);
        //返回参数是否  必须的|类型
        insertElement(reParm);

    }

    /**
     * 通过类型插入对应元素；
     * 文本 0
        图区 2
        固定文本 3
        单选 4
        多选 5
        有无选择 6
        输入提示 7
        输入宏 8
        抓取对象 9
        选择框 10
        图片编辑 11
        图片 13
        数字 14
        下拉框 15
     */
    private void insertElement(TParm reParm) {
        //System.out.println("====reParm====="+reParm);
        //控件类型;
        String componentType = (String) reParm.getData(
            "SELECTED_COMPONENT_TYPE", 0);
        //大类名子
        String groupCode = (String) reParm.getData("GROUP_CODE", 0);

        //数据编号 名子
        String dataCode = (String) reParm.getData("DATA_CODE", 0);
        //数据值
        String dataDesc = (String) reParm.getData("DATA_DESC", 0);
        //宏名
        String macroName = (String) reParm.getData("MACRO_NAME", 0);

        //是否为必要
        boolean isAllowNull = isAllowNull(reParm);

        //this.messageBox("==groupCode=="+groupCode);
        //this.messageBox("==macroName=="+macroName);

        //codesystem
        String codesystem = (String) reParm.getData("CODE_SYSTEM", 0);

        //取出textFormat类型
        if (componentType.equals("0")) {

        }
        else if (componentType.equals("2")) {

        }
        else if (componentType.equals("3")) {
            if (onlyEditfileName.length() != 0) {
                EFixed eword = word.insertFixed();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        else if (componentType.equals("4")) {
            if (onlyEditfileName.length() != 0) {
                ESingleChoose eword = word.insertSingleChoose();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }
        }
        else if (componentType.equals("5")) {
            if (onlyEditfileName.length() != 0) {
                EMultiChoose eword = word.insertMultiChoose();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        else if (componentType.equals("6")) {
            if (onlyEditfileName.length() != 0) {
                EHasChoose eword = word.insertHasChoose();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        else if (componentType.equals("7")) {
            if (onlyEditfileName.length() != 0) {
                EInputText eword = word.insertInputText();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        else if (componentType.equals("8")) {
            if (onlyEditfileName.length() != 0) {
                EMicroField eword = word.insertMicroField();
                eword.setName(dataCode);
                eword.setText(dataDesc);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        //抓取
        else if (componentType.equals("9")) {
            if (onlyEditfileName.length() != 0) {
                ECapture obj = word.insertCaptureObject();
                obj.setName(dataCode);
                //obj.setText(dataDesc);

                ECapture obj1 = word.insertCaptureObject();
                obj1.setName(dataCode);
                //obj1.setText(dataDesc);
                obj1.setAllowNull(isAllowNull);
                obj1.setDataElements(true);
                obj1.setGroupName(groupCode);
                //this.messageBox("macroName=========="+macroName);
                obj1.setMicroName(macroName);

                obj.setCaptureType(1);
                obj.setAllowNull(isAllowNull);
                obj.setDataElements(true);
                obj.setGroupName(groupCode);
                obj.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        else if (componentType.equals("10")) {
            if (onlyEditfileName.length() != 0) {
                // word.inserc.insertCheckBoxChoose();
                ECheckBoxChoose eword = word.insertCheckBoxChoose();
                eword.setName(dataCode);
                eword.setText(dataDesc);

                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        else if (componentType.equals("11")) {

        }
        else if (componentType.equals("13")) {

        }
        //数字选择
        else if (componentType.equals("14")) {
            if (onlyEditfileName.length() != 0) {
                ENumberChoose eword = word.insertNumberChoose();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);
            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        //下拉框
        else if (componentType.equals("15")) {
            if (onlyEditfileName.length() != 0) {
                ETextFormat eword = word.insertTextFormat();
                eword.setName(dataCode);
                //this.messageBox("dataDesc"+dataDesc);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);
                //this.messageBox(codesystem);
                eword.setData(codesystem);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }

    }

    /**
     * 是允许空
     * @param reParm TParm
     * @return boolean
     */
    private boolean isAllowNull(TParm reParm) {
        String strRequire = (String) reParm.getData("REPEAT_COUNT", 0);
        String flag = strRequire.substring(0, 1);
        //this.messageBox("==flag==" + flag);
        //不必填  允许空；
        if (flag.equals("0")) {
            return true;
            //必填   不允许空；
        }
        else {
            return false;
        }

    }

    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    private TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * 插入关联选项
     */
    public void onInsertRelateChoose() {
        if (onlyEditfileName.length() != 0) {
            word.insertAssociateChoose();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 获取word纯文档;
     * @param word TWord
     * @return String
     */
    public String getWordString() {
        String wordText = "";
        //取页
        for (int pageIndex = 0; pageIndex < getPageCount(); pageIndex++) {
            //取panels
            for (int panelIndex = 0; panelIndex < getPagePanelCount(pageIndex);
                 panelIndex++) {
                //取panel下的值;
                for (int blockIndex = 0;
                     blockIndex < getPagePanelBlockCount(pageIndex,
                    panelIndex); blockIndex++) {
                    if (!isTable(pageIndex, panelIndex, blockIndex)) {
                        IBlock block = getIBlock(pageIndex, panelIndex,
                                                 blockIndex);
                        if (block.getNextTrueBlock() != null &&
                            (block.getNextTrueBlock().findIndex()) - blockIndex >
                            1) {
                            continue;
                        }
                        wordText += setElement(block);
                        continue;
                    }

                }
            }

        }
        return wordText;
    }

    /**
     * 取得页数
     * @return int
     */
    private int getPageCount() {
        return word.getPageManager().getComponentList().size();
    }

    /**
     * 取得页面板个数
     * @param pageIndex int
     * @return int
     */
    private int getPagePanelCount(int pageIndex) {
        return getEPage(pageIndex).getComponentList().size();
    }

    /**
     * 的到页
     * @param pageIndex int
     * @return EPage
     */
    private EPage getEPage(int pageIndex) {
        return word.getPageManager().get(pageIndex);
    }

    /**
     * 取得页面板元素个数
     * @param pageIndex int
     * @param panelIndex int
     * @return int
     */
    private int getPagePanelBlockCount(int pageIndex, int panelIndex) {
        return getEPanel(pageIndex, panelIndex).getComponentList().size();
    }

    /**
     * 得到面板
     * @param pageIndex int
     * @param panelIndex int
     * @return EPanel
     */
    private EPanel getEPanel(int pageIndex, int panelIndex) {
        return getEPage(pageIndex).get(panelIndex);
    }


    /**
     * 判断是否是表格
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @return boolean
     */
    private boolean isTable(int pageIndex, int panelIndex, int blockIndex) {
        IBlock block = getIBlock(pageIndex, panelIndex, blockIndex);
        if (block == null) {
            return false;
        }
        return (block.getObjectType() == EComponent.TABLE_TYPE);
    }

    /**
     * 得到基础元素
     * @param pageIndex int
     * @param panelIndex int
     * @param blockIndex int
     * @return IBlock
     */
    private IBlock getIBlock(int pageIndex, int panelIndex, int blockIndex) {
        return getEPanel(pageIndex, panelIndex).get(blockIndex);
    }

    /**
     * 设置元素相关信息
     * @param block IBlock
     * @param parent Node
     */
    private String setElement(IBlock block) {
        if (block instanceof EMacroroutine && ( (EMacroroutine) block).isPic()) {
            return ( (VPic) ( (EMacroroutine) block).getModel().getMVList().get(
                0).get(0)).getPictureName();
        }
        if (block instanceof EImage) {
            return ( (EImage) block).getBlockValue();
        }
        if (block.getObjectType() == EComponent.TEXT_TYPE) {
            return ( (EText) block).getString();
        }
        if (! (block instanceof EFixed)) {
            return "";
        }
        //this.messageBox("object type"+block.getObjectType());
        switch (block.getObjectType()) {
            case EComponent.CAPTURE_TYPE:
                //this.messageBox("come in1");
                if ( ( (ECapture) block).getCaptureType() == 1) {
                    return ( (ECapture) block).getValue();
                }
                // this.messageBox("come in2");
                 return "";
            case EComponent.CHECK_BOX_CHOOSE_TYPE:
                return ( (ECheckBoxChoose) block).isChecked() ? "是" : "否";
            case EComponent.TEXTFORMAT_TYPE:
                return block.getBlockValue();

            default:
                return block.getBlockValue();
        }
    }


}
