package com.javahis.device;

import java.awt.Canvas;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import com.dongyang.util.Log;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.event.AncestorListener;
import javax.swing.event.AncestorEvent;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import java.awt.Frame;
import javax.swing.JFrame;

/**
 *
 * <p>Title: EMR接口方法</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2008.12.30
 * @version 1.0
 */
public class EMRPad30
    extends Canvas implements ComponentListener {
    /**
     * 得到本地 Window 组件的 HWND
     * @param canvas Canvas
     * @return int HWND
     */
    private native static synchronized int getNativeWindowHandle(Canvas canvas);

    /**
     * 加载OCX组件
     * @param hwnd int HWND
     */
    private native static synchronized void initialize(int hwnd);

    /**
     * 调整组建尺寸
     * @param hwnd int HWND
     * @param nWidth int 宽度
     * @param nHeight int 高度
     */
    private native static synchronized void resizeControl(int hwnd, int nWidth,
        int nHeight);

    public static String OCX_ID = "G1182P-LNCAZT-XLIZZJ-RAPOJA";
    public static long INIT_ID = 2007110509;
    //=========================================================================//
    //EMRPad30方法
    private native static synchronized int closeOCX(int hwnd);

    private native static synchronized int FileNew(int hwnd);

    private native static synchronized int FileOpen(int hwnd, String strFile,
        int nReserved);

    private native static synchronized int FileSave(int hwnd);

    private native static synchronized int FileSaveAs(int hwnd, String strFile,
        int nFileType, int nReserved);

    private native static synchronized int FileInsert(int hwnd, String strFile,
        int nReserved);

    private native static synchronized String GetFileName(int hwnd);

    private native static synchronized int Print(int hwnd, int nPrintType);

    private native static synchronized int PageSetup(int hwnd);

    private native static synchronized void FileSetPageNumber(int hwnd);

    private native static synchronized void EditUnDo(int hwnd);

    private native static synchronized void EditReDo(int hwnd);

    private native static synchronized void EditCut(int hwnd);

    private native static synchronized int EditCopy(int hwnd);

    private native static synchronized int EditPaste(int hwnd);

    private native static synchronized void EditSelectAll(int hwnd);

    private native static synchronized void EditFind(int hwnd);

    private native static synchronized void EditReplace(int hwnd);

    private native static synchronized int EditCanUnDo(int hwnd);

    private native static synchronized int EditCanReDo(int hwnd);

    private native static synchronized int EditCanCopy(int hwnd);

    private native static synchronized int EditCanPaste(int hwnd);

    private native static synchronized void CleanUndoBuffer(int hwnd);

    private native static synchronized void CleanClipboard(int hwnd);

    private native static synchronized void EditDelete(int hwnd);

    private native static synchronized void EditQueryKeyword(int hwnd);

    private native static synchronized int EditAutoRange(int hwnd);

    private native static synchronized int EditClearUnuseField(int hwnd);

    private native static synchronized int EditClearSymtax(int hwnd);

    private native static synchronized int EditLineReadOnly(int hwnd);

    private native static synchronized int EditLineEditMode(int hwnd);

    private native static synchronized int EditImage(int hwnd);

    private native static synchronized void ViewToolbar(int hwnd);

    private native static synchronized void ViewStatusbar(int hwnd);

    private native static synchronized void ViewLineIndex(int hwnd);

    private native static synchronized int FontBold(int hwnd);

    private native static synchronized int FontItalic(int hwnd);

    private native static synchronized int FontUnderline(int hwnd);

    private native static synchronized int FontSubscript(int hwnd);

    private native static synchronized int FontSuperscript(int hwnd);

    private native static synchronized int LineSpaceSingle(int hwnd);

    private native static synchronized int LineSpaceOnehalf(int hwnd);

    private native static synchronized void LineAlignLeft(int hwnd);

    private native static synchronized void LineAlignCenter(int hwnd);

    private native static synchronized void LineAlignRight(int hwnd);

    private native static synchronized void ParagraphBegin(int hwnd);

    private native static synchronized void ParagraphEnd(int hwnd);

    private native static synchronized void ParagraphFormat(int hwnd);

    private native static synchronized void TableInsert(int hwnd);

    private native static synchronized int TableInsertColLeft(int hwnd);

    private native static synchronized int TableInsertColRight(int hwnd);

    private native static synchronized int TableInsertRowTop(int hwnd);

    private native static synchronized int TableInsertRowBottom(int hwnd);

    private native static synchronized int TableDelete(int hwnd);

    private native static synchronized int TableDeleteCol(int hwnd);

    private native static synchronized int TableDeleteRow(int hwnd);

    private native static synchronized int TableSelect(int hwnd);

    private native static synchronized int TableSelectCol(int hwnd);

    private native static synchronized int TableSelectRow(int hwnd);

    private native static synchronized int TableMergeCell(int hwnd);

    private native static synchronized int TableSplitCell(int hwnd);

    private native static synchronized int TableProp(int hwnd);

    private native static synchronized int TableCellProp(int hwnd);

    private native static synchronized int TableColProp(int hwnd);

    private native static synchronized int TableHideBorder(int hwnd);

    private native static synchronized int SetDocumentMode(int hwnd, int nMode);

    private native static synchronized long GetDocumentMode(int hwnd);

    private native static synchronized void ShowRevisionHistory(int hwnd);

    private native static synchronized void InsertPageBreaker(int hwnd);

    private native static synchronized void SetPadRedraw(int hwnd, int bRedraw);

    private native static synchronized int InsertLine(int hwnd,
        int bAfterCurLine);

    private native static synchronized int DeleteLines(int hwnd,
        long nStartLine, long nEndLine);

    private native static synchronized long GetBaseLineCount(int hwnd);

    private native static synchronized long GetLineCount(int hwnd);

    private native static synchronized long GetCurFieldCharCount(int hwnd);

    private native static synchronized int InsertField(int hwnd, String strName,
        String strContent, int nLayerNo, int nFieldType, int nPosition);

    private native static synchronized String GetFieldText(int hwnd,
        long nBaseLineIndex, long nCellIndex, long nLineIndex, long nFieldText,
        int bSelected);

    private native static synchronized int SetFieldText(int hwnd,
        long nBaseLineIndex, long nCellIndex, long nLineIndex, long nFieldIndex,
        String strText);

    private native static synchronized int FindField(int hwnd,
        String strFindText, int nLayerNo, int nFindType, int bFromBegin);

    private native static synchronized int SetMicroField(int hwnd,
        String strName, String strValue);

    private native static synchronized int UpdateMicroField(int hwnd,
        int bOnlyCanEdit);

    private native static synchronized int ReplaceFieldText(int hwnd,
        long nBaseLineIndex, long nCellIndex, long nLineIndex, long nFieldIndex,
        String strOld, String strNew);

    private native static synchronized long SetRevisalInfo(int hwnd,
        long nRevisialID, int nAuthorization, String strUserName,
        String strUserCode, String strExtent);

    private native static synchronized int SelectBaseLineRange(int hwnd,
        long nStartIndex, long nEndIndex);

    private native static synchronized long GetObjectCount(int hwnd,
        long nBaseLineIndex, long nCellIndex, long nLineIndex, long nType);

    private native static synchronized String UniversalStringFunction(int hwnd,
        String strParam1, String strParam2, String strParam3, String strParam4,
        String strParam5, String strParam6);

    private native static synchronized int SetSel(int hwnd, long BaseLineIndex1,
                                                  long CellIndex1,
                                                  long LineIndex1,
                                                  long FieldElemIndex1,
                                                  long CharPos1,
                                                  long BaseLineIndex2,
                                                  long CellIndex2,
                                                  long LineIndex2,
                                                  long FieldElemIndex2,
                                                  long CharPos2);

    private native static synchronized long GetCurObjectIndex(int hwnd,
        long nType);

    //private native static int GetCurCursorPos(int hwnd,long[] nBaseLineIndex, long[] nCellIndex, long[] nLineIndex, long[] nFieldElemIndex, long[] nCharPos, int bCursorBegin);//使用UniversalBoolFunction代替
    private native static synchronized int UniversalBoolFunction(int hwnd,
        String strParam1, String strParam2, long nParam1, long nParam2);

    /**
     * Window 组件的 HWND
     */
    private int m_hWnd = 0;
    /**
     * 弹出菜单所在的窗口
     */
    private TWindow popMenuWindow;
    /**
     * 日志系统
     */
    private Log log;
    private JFrame frame;
    private Thread thread = new Thread(new Eventlistener());
    private JPopupMenu popup;
    boolean threadStart;
    public class TWindow
        extends JWindow {
        public boolean b;
        public TWindow(Frame frame) {
            super(frame);
        }
    }


    /**
     * 构造器
     */
    public EMRPad30() {
        log = new Log();
        addComponentListener(this);
    }

    /**
     * 得到面板 Windows 的 HWND
     * @return int
     */
    private int getHWND() {
        return m_hWnd;
    }

    /**
     * 检测OCX组件是否初始化完毕
     * 窗口显示和OCX创建分线程处理的
     * 如果在OCX没有创建之前调用方法会产生严重错误
     * 所以让java窗口做延时同步等待
     */
    private void checkInit() {
        out("begin");
        while (UniversalBoolFunction(getHWND(), "", "", 3002, 0) == 0)
            try {
                Thread.sleep(1);
            }
            catch (Exception e1) {}
        out("end");
    }

    public void resizeControl(int nWidth, int nHeight) {
        out("begin nWidth=" + nWidth + " nHeight" + nHeight);
        resizeControl(getHWND(), nWidth, nHeight);
        out("end");
    }

    /**
     * java 内部加载组件使用
     * 在面板邦定OCX组件
     */
    public void addNotify() {
        out("begin");
        super.addNotify();
        //得到 Window HWND
        m_hWnd = getNativeWindowHandle(this);
        //初始化 OCX 组件
        initialize(m_hWnd);
        out("end");
    }

    public void componentHidden(ComponentEvent e) {

    }

    public void componentMoved(ComponentEvent e) {
        out("begin");
        checkInit();
        resizeControl(getWidth(), getHeight());
        out("end");
    }

    public void componentResized(ComponentEvent e) {
        out("begin");
        checkInit();
        out("getX=" + getX() + " getY=" + getY() + " getWidth=" + getWidth() +
            " getHeight=" + getHeight());
        resizeControl(getHWND(), getWidth(), getHeight());
        out("end");
    }

    public void componentShown(ComponentEvent e) {
    }

    //EMRPad30 Public方法
    /**
     * 新建文件
     * 本函数用于清空当前编辑器中的内容，初始化编辑器的默认设置，用于启动一个新文档的书写
     * @return boolean 成功true 失败false
     */
    public boolean fileNew() {
        int value;
        out("begin");
        value = FileNew(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 打开文件
     * 本函数用于打开一个已存在的电子病历文档
     * @param strFile String 文件名
     * @param nReserved int 保留
     * @return boolean 成功true 失败false
     */
    public boolean fileOpen(String strFile, int nReserved) {
        int value;
        out("begin strFile=" + strFile + " nReserved=" + nReserved);
        value = FileOpen(getHWND(), strFile, nReserved);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 打开文件
     * 本函数用于把当前编辑器中的内容保存成一个电子病历文件
     * @param strFile String 文件名
     *  指定打开的文档名，应当包含全路径名。例如“C:\住院病历.emr”
     * @return boolean 成功true 失败false
     */
    public boolean fileOpen(String strFile) {
        boolean value;
        out("begin strFile=" + strFile);
        value = fileOpen(strFile, 0);
        out("end value=" + value);
        return value;
    }

    /**
     * 保存文件
     * 本函数用于把当前编辑器中的内容保存成一个电子病历文件
     * @return boolean 成功true 失败false
     */
    public boolean fileSave() {
        int value;
        out("begin");
        value = FileSave(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 另存文件
     * 本函数用于把当前编辑器中的内容保存成一个电子病历文件
     * @param strFile String 文件名
     * 指定保存的文档名，应当包含全路径名。例如“C:\住院病历.emr”
     * @param nFileType int 文件类型
     * 0 病例文件
     * 1 模板文件
     * 2 关键字
     * 3 平面语法文件
     * 4 XML文件
     * 5 Text文件
     * 12 加密的Text文件
     * 13 把编辑器当前内容追加到指定的加密Text文件
     * 14 XML大纲文件
     * @param nReserved int 保留
     * @return boolean 成功true 失败false
     */
    public boolean fileSaveAs(String strFile, int nFileType, int nReserved) {
        int value;
        out("begin strFile=" + strFile + " nFileType=" + nFileType +
            " nReserved=" + nReserved);
        value = FileSaveAs(getHWND(), strFile, nFileType, nReserved);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 另存文件
     * 本函数用于把当前编辑器中的内容保存成一个电子病历文件
     * @param strFile String 文件名
     * 指定保存的文档名，应当包含全路径名。例如“C:\住院病历.emr”
     * @param nFileType int 文件类型
     * 0 病例文件
     * 1 模板文件
     * 2 关键字
     * 3 平面语法文件
     * 4 XML文件
     * 5 Text文件
     * 12 加密的Text文件
     * 13 把编辑器当前内容追加到指定的加密Text文件
     * 14 XML大纲文件
     * @return boolean 成功true 失败false
     */
    public boolean fileSaveAs(String strFile, int nFileType) {
        boolean value;
        out("begin strFile=" + strFile + " nFileType=" + nFileType);
        value = fileSaveAs(strFile, nFileType, 0);
        out("end value=" + value);
        return value;
    }

    /**
     * 插入指定文件
     * 本函数用于在编辑器当前的光标位置插入指定文件的内容
     * @param strFile String 文件名
     * 指定插入的文档名，应当包含全路径名
     * @param nReserved int 用于扩展，目前输入0
     * @return boolean 成功true 失败false
     */
    public boolean fileInsert(String strFile, int nReserved) {
        int value;
        out("begin strFile=" + strFile + " nReserved=" + nReserved);
        value = FileInsert(getHWND(), strFile, nReserved);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 插入指定文件
     * 本函数用于在编辑器当前的光标位置插入指定文件的内容
     * @param strFile String 文件名
     * 指定插入的文档名，应当包含全路径名
     * @return boolean 成功true 失败false
     */
    public boolean fileInsert(String strFile) {
        boolean value;
        out("begin strFile=" + strFile);
        value = fileInsert(strFile, 0);
        out("end value=" + value);
        return value;
    }

    /**
     * 返回当前文件名称
     * 本函数用于返回当前打开的文件名称
     * @return String
     */
    public String getFileName() {
        String value;
        out("begin");
        value = GetFileName(getHWND());
        out("end value=" + value);
        return value;
    }

    /**
     * 打印文档
     * @param nPrintType int 打印类型
     * 用于指定打印的类型，
     * 0 全文打印方式，会弹出一个打印对话框
     * 1 选择打印，只打印选中的部分
     * 2 区域打印、打印选择的行范围内容
     * @return boolean 成功true 失败false
     */
    public boolean print(int nPrintType) {
        int value;
        out("begin nPrintType=" + nPrintType);
        value = Print(getHWND(), nPrintType);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 设置页面
     * 本函数弹出页面设置窗口
     * @return boolean 成功true 失败false
     */
    public boolean pageSetup() {
        int value;
        out("begin");
        value = PageSetup(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 设置页面序号
     * 本函数弹出设置页面的起始序号,同时可设置首页是否显示页眉和页脚
     * (作废方法)
     */
    public void fileSetPageNumber() {
        out("begin");
        FileSetPageNumber(getHWND());
        out("end");
    }

    /**
     * 撤消
     * 本函数用于撤消操作的动作
     */
    public void editUnDo() {
        out("begin");
        EditUnDo(getHWND());
        out("end");
    }

    /**
     * 重新显示撤消的内容
     * 本函数用于重新显示撤消的内容
     */
    public void editReDo() {
        out("begin");
        EditReDo(getHWND());
        out("end");
    }

    /**
     * 剪切
     * 本函数用于剪切当前选中的内容
     */
    public void editCut() {
        out("begin");
        EditCut(getHWND());
        out("end");
    }

    /**
     * 复制
     * 本函数用于复制当前选中的内容
     * @return boolean 如果成功返回TRUE，否则返回FALSE
     */
    public boolean editCopy() {
        int value;
        out("begin");
        value = EditCopy(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 粘贴
     * 本函数用于从剪贴板把内容粘贴到当前位置
     * @return boolean 如果成功返回TRUE，否则返回FALSE
     */
    public boolean editPaste() {
        int value;
        out("begin");
        value = EditPaste(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 全部选中当前文档
     * 本函数用于全部选中当前文档的内容
     */
    public void editSelectAll() {
        out("begin");
        EditSelectAll(getHWND());
        out("end");
    }

    /**
     * 弹出标准的查找界面
     * 本函数用于弹出标准的查找界面，供查找使用
     */
    public void editFind() {
        out("begin");
        EditFind(getHWND());
        out("end");
    }

    /**
     * 弹出标准的替换界面
     * 本函数用于弹出标准的替换界面，供替换使用
     */
    public void editReplace() {
        out("begin");
        EditReplace(getHWND());
        out("end");
    }

    /**
     * 判断是否可以撤消
     * 本函数用于判断当前编辑缓冲区是否可以撤消
     * @return boolean 如果当前编辑缓冲区可以撤消返回TRUE，否则返回FALSE
     */
    public boolean editCanUnDo() {
        int value;
        out("begin");
        value = EditCanUnDo(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 判断是否可以重做
     * 本函数用于判断当前编辑缓冲区是否可以重做
     * @return boolean 如果当前编辑缓冲区可以重做返回TRUE，否则返回FALSE
     */
    public boolean editCanReDo() {
        int value;
        out("begin");
        value = EditCanReDo(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 判断是否可以复制
     * 本函数用于判断当前编辑缓冲区是否可以复制操作
     * @return boolean 如果当前编辑器有选中返回TRUE，否则返回FALSE
     */
    public boolean editCanCopy() {
        int value;
        out("begin");
        value = EditCanCopy(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 判断是否可以粘贴
     * 本函数用于判断当前是否可以粘贴操作
     * @return boolean 如果当前编辑器剪贴板有可以粘贴的数据选中返回TRUE，否则返回FALSE
     */
    public boolean editCanPaste() {
        int value;
        out("begin");
        value = EditCanPaste(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 清空编辑器剪贴板
     * 本函数用于清空编辑器剪贴板中的内容，执行本函数后, 粘贴功能将不起作用
     */
    public void cleanClipboard() {
        out("begin");
        CleanClipboard(getHWND());
        out("end");
    }

    /**
     * 清空编辑缓冲区
     * 本函数用于清空编辑缓冲区的内容，执行本函数后, Undo和Redo功能将不起作用
     */
    public void cleanUndoBuffer() {
        out("begin");
        CleanUndoBuffer(getHWND());
        out("end");
    }

    /**
     * 删除
     * 本函数功能操作为按下Delete键
     */
    public void editDelete() {
        out("begin");
        EditDelete(getHWND());
        out("end");
    }

    /**
     * 打开关键词字典
     * 本函数用于打开关键词字典
     * (作废方法)
     */
    public void editQueryKeyword() {
        out("begin");
        EditQueryKeyword(getHWND());
        out("end");
    }

    /**
     * 自动排版
     * 本函数用于自动排版
     * @return boolean 目前始终返回TRUE
     */
    public boolean editAutoRange() {
        int value;
        out("begin");
        value = EditAutoRange(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 清除未使用的元素
     * 本函数用于清除未使用的元素
     * @return boolean 目前始终返回TRUE
     */
    public boolean editClearUnuseField() {
        int value;
        out("begin");
        value = EditClearUnuseField(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 清除元素的语法
     * 本函数用于清除选择项目和注释元素中的语法
     * @return boolean 目前始终返回TRUE
     */
    public boolean editClearSymtax() {
        int value;
        out("begin");
        value = EditClearSymtax(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 设置行只读
     * 本函数用于设置行只读
     * @return boolean 目前始终返回TRUE
     */
    public boolean editLineReadOnly() {
        int value;
        out("begin");
        value = EditLineReadOnly(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 设置行可编辑
     * 本函数用于设置行可编辑
     * @return boolean 目前始终返回TRUE
     */
    public boolean editLineEditMode() {
        int value;
        out("begin");
        value = EditLineEditMode(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 编辑图像
     * 本函数用于编辑当前选种的图像
     * @return boolean 目前始终返回TRUE
     */
    public boolean editImage() {
        int value;
        out("begin");
        value = EditImage(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 显示或隐藏工具条
     * 本函数用于显示或隐藏工具条
     */
    public void viewToolbar() {
        out("begin");
        ViewToolbar(getHWND());
        out("end");
    }

    /**
     * 显示或隐藏状态条
     * 本函数用于显示或隐藏状态条
     */
    public void viewStatusbar() {
        out("begin");
        ViewStatusbar(getHWND());
        out("end");
    }

    /**
     * 显示或隐藏行号
     * 本函数用于显示或隐藏行号
     */
    public void viewLineIndex() {
        out("begin");
        ViewLineIndex(getHWND());
        out("end");
    }

    /**
     * 设置黑体
     * 本函数用于设置选中的部分为黑体,如果当前是黑体的,则设置为不黑体
     * @return boolean
     */
    public boolean fontBold() {
        int value;
        out("begin");
        value = FontBold(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 设置斜体
     * 本函数用于设置选中的部分为斜体,如果当前是斜体的,则设置为不斜体
     * @return boolean
     */
    public boolean fontItalic() {
        int value;
        out("begin");
        value = FontItalic(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 设置下划线
     * 本函数用于设置选中的部分为下划线,如果当前是下划线的,则设置为不下划线
     * @return boolean
     */
    public boolean fontUnderline() {
        int value;
        out("begin");
        value = FontUnderline(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 设置上角标
     * 本函数用于设置选中的部分为上角标,如果当前是上角标的,则设置为不上角标
     * @return boolean
     */
    public boolean fontSubscript() {
        int value;
        out("begin");
        value = FontSubscript(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 设置下脚标
     * 本函数用于设置选中的部分为下脚标,如果当前是下脚标的,则设置为不下脚标
     * @return boolean
     */
    public boolean fontSuperscript() {
        int value;
        out("begin");
        value = FontSuperscript(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 设置单倍行距
     * 本函数用于设置选中的部分的行为单倍行距
     * @return boolean
     */
    public boolean lineSpaceSingle() {
        int value;
        out("begin");
        value = LineSpaceSingle(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 设置1.5倍行距
     * 本函数用于设置选中的部分的行为1.5倍行距
     * @return boolean
     */
    public boolean lineSpaceOnehalf() {
        int value;
        out("begin");
        value = LineSpaceOnehalf(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 设置左对齐
     * 本函数用于设置选中的部分的行为左对齐
     */
    public void lineAlignLeft() {
        out("begin");
        LineAlignLeft(getHWND());
        out("end");
    }

    /**
     * 设置居中
     * 本函数用于设置选中的部分的行为居中对齐
     */
    public void lineAlignCenter() {
        out("begin");
        LineAlignCenter(getHWND());
        out("end");
    }

    /**
     * 设置右对齐
     * 本函数用于设置选中的部分的行为右对齐
     */
    public void lineAlignRight() {
        out("begin");
        LineAlignRight(getHWND());
        out("end");
    }

    /**
     * 设置段起始连接符
     * 本函数用于设置当前行为段起始连接符
     */
    public void paragraphBegin() {
        out("begin");
        ParagraphBegin(getHWND());
        out("end");
    }

    /**
     * 设置段结束连接符
     * 本函数用于设置当前行为段结束连接符
     */
    public void paragraphEnd() {
        out("begin");
        ParagraphEnd(getHWND());
        out("end");
    }

    /**
     * 段落的格式设置
     * 本函数用于弹出段落的格式设置,包括段的左右缩进和悬挂方式,用于设置排版的格式
     */
    public void paragraphFormat() {
        out("begin");
        ParagraphFormat(getHWND());
        out("end");
    }

    /**
     * 表格插入对话框
     * 本函数用于调出通用的表格插入对话框
     */
    public void tableInsert() {
        out("begin");
        TableInsert(getHWND());
        out("end");
    }

    /**
     * 在列的左边插入新列
     * 本函数用于在当前的列的左边插入新列
     * @return boolean 目前总返回TRUE
     */
    public boolean tableInsertColLeft() {
        int value;
        out("begin");
        value = TableInsertColLeft(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 在列的右边插入新列
     * 本函数用于在当前的列的右边插入新列
     * @return boolean 目前总返回TRUE
     */
    public boolean tableInsertColRight() {
        int value;
        out("begin");
        value = TableInsertColRight(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 在行上边插入新行
     * 本函数用于在当前的行的上边插入新行
     * @return boolean 目前总返回TRUE
     */
    public boolean tableInsertRowTop() {
        int value;
        out("begin");
        value = TableInsertRowTop(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 在行下边插入新行
     * 本函数用于在当前的行的下边插入新行
     * @return boolean 目前总返回TRUE
     */
    public boolean tableInsertRowBottom() {
        int value;
        out("begin");
        value = TableInsertRowBottom(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 删除表格
     * 本函数用于删除当前的表格
     * @return boolean 目前总返回TRUE
     */
    public boolean tableDelete() {
        int value;
        out("begin");
        value = TableDelete(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 删除表格选中的列
     * 本函数用于删除当前的表格选中的列
     * @return boolean 目前总返回TRUE
     */
    public boolean tableDeleteCol() {
        int value;
        out("begin");
        value = TableDeleteCol(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 删除表格选中的行
     * 本函数用于删除当前的表格选中的行
     * @return boolean 目前总返回TRUE
     */
    public boolean tableDeleteRow() {
        int value;
        out("begin");
        value = TableDeleteRow(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 选中当前的表格
     * 本函数用于选中当前的表格
     * @return boolean 目前总返回TRUE
     */
    public boolean tableSelect() {
        int value;
        out("begin");
        value = TableSelect(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 选中当前的表格列
     * 本函数用于选中当前的表格列
     * @return boolean 目前总返回TRUE
     */
    public boolean tableSelectCol() {
        int value;
        out("begin");
        value = TableSelectCol(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 选中当前的表格行
     * 本函数用于选中当前的表格行
     * @return boolean 目前总返回TRUE
     */
    public boolean tableSelectRow() {
        int value;
        out("begin");
        value = TableSelectRow(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 合并单元格
     * 本函数用于合并单元格操作,如果当前选中的单元格可以合并,则完成合并操作
     * @return boolean 目前总返回TRUE
     */
    public boolean tableMergeCell() {
        int value;
        out("begin");
        value = TableMergeCell(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 拆分单元格
     * 本函数用于拆分单元格操作,如果当前选中的单元格可以拆分,则提示是行拆分还是列拆分,完成拆分操作
     * @return boolean 目前总返回TRUE
     */
    public boolean tableSplitCell() {
        int value;
        out("begin");
        value = TableSplitCell(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 显示表格属性对话框
     * 本函数用于显示表格属性对话框,用于设置表格的边框属性、表格对齐方式、表格单元格的边距、表格名称、层次属性
     * @return boolean 目前总返回TRUE
     */
    public boolean tableProp() {
        int value;
        out("begin");
        value = TableProp(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 显示单元格属性对话框
     * 本函数用于显示单元格属性对话框,用于设置单元格的边框属性
     * @return boolean 目前总返回TRUE
     */
    public boolean tableCellProp() {
        int value;
        out("begin");
        value = TableCellProp(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 显示表格列属性对话框
     * 本函数用于显示表格列属性对话框,用于手工调整列宽度
     * @return boolean 目前总返回TRUE
     */
    public boolean tableColProp() {
        int value;
        out("begin");
        value = TableColProp(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 显示表格的隐含边框
     * 本函数用于设置是否显示表格的隐含边框线
     * @return boolean 目前总返回TRUE
     */
    public boolean tableHideBorder() {
        int value;
        out("begin");
        value = TableHideBorder(getHWND());
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 编辑状态
     */
    public final static int DOCUMENT_MODE_EDIT = 1;
    /**
     * 只读状态
     */
    public final static int DOCUMENT_MODE_READ_ONLY = 2;
    /**
     * 清洁显示状态
     */
    public final static int DOCUMENT_MODE_CLEAR = 3;
    /**
     * 设置文档的状态
     * 本函数用于设置文档的状态
     * @param nMode int 用于指定文档的状态
     * 1 编辑状态 DOCUMENT_MODE_EDIT
     * 2 只读状态 DOCUMENT_MODE_READ_ONLY
     * 3 清洁显示状态 DOCUMENT_MODE_CLEAR
     * @return boolean 如果成功返回TRUE，失败返回FALSE
     * 清洁显示状态是一种全文只读状态，它删除了文档中经修订删除掉的文字，
     * 同时重新排版，不显示修改痕迹的线条。三种状态通过调用此函数就可以相互切换，
     * 清洁打印不再需要单独开辟一个窗口处理了
     */
    public boolean setDocumentMode(int nMode) {
        int value;
        out("begin nMode=" + nMode);
        value = SetDocumentMode(getHWND(), nMode);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 返回文档的状态
     * 本函数用于返回文档的状态
     * @return long 如果是只读状态返回2 DOCUMENT_MODE_READ_ONLY，清洁显示状态返回3 DOCUMENT_MODE_CLEAR，其它状态返回1 DOCUMENT_MODE_EDIT。
     */
    public long getDocumentMode() {
        long value;
        out("begin");
        value = GetDocumentMode(getHWND());
        out("end value=" + value);
        return value;
    }

    /**
     * 显示文档的修订记录
     * 本函数用于显示文档的修订记录情况
     */
    public void showRevisionHistory() {
        out("begin");
        ShowRevisionHistory(getHWND());
        out("end");
    }

    /**
     * 插入分页符
     * 本函数在当前位置插入分页符
     */
    public void insertPageBreaker() {
        out("begin");
        InsertPageBreaker(getHWND());
        out("end");
    }

    /**
     * 强制编辑器重画
     * 本函数用于强制编辑器重画
     * @param bRedraw boolean 用于设置是否重画，目前始终默认为重画，忽略TRUE和FALSE
     */
    public void setPadRedraw(boolean bRedraw) {
        out("begin bRedraw=" + bRedraw);
        SetPadRedraw(getHWND(), bRedraw ? 1 : 0);
        out("end");
    }

    /**
     * 强制编辑器重画
     * 本函数用于强制编辑器重画
     */
    public void setPadRedraw() {
        out("begin");
        setPadRedraw(true);
        out("end");
    }

    /**
     * 插入新空白行
     * 本函数用于在当前行的位置插入一个新的空白行
     * @param bAfterCurLine boolean 用于设置新行与当前行的相对位置，TRUE在当前行的下面，FALSE在当前行的上面。
     * @return boolean
     */
    public boolean insertLine(boolean bAfterCurLine) {
        int value;
        out("begin bAfterCurLine=" + bAfterCurLine);
        value = InsertLine(getHWND(), bAfterCurLine ? 1 : 0);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 删除指定范围的行
     * 本函数用于删除指定范围的行
     * @param nStartLine long 起始行 用于设置删除行的起始位置
     * @param nEndLine long 结束行 用于设置删除行的结束位置
     * @return boolean 成功返回TRUE，失败返回FALSE
     */
    public boolean deleteLines(long nStartLine, long nEndLine) {
        int value;
        out("begin nStartLine=" + nStartLine + " nEndLine=" + nEndLine);
        value = DeleteLines(getHWND(), nStartLine, nEndLine);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 返回基行的行数
     * 本函数用于返回基行的行数
     * @return long 返回当前文档基行的行数
     */
    public long getBaseLineCount() {
        long value;
        out("begin");
        value = GetBaseLineCount(getHWND());
        out("end value=" + value);
        return value;
    }

    /**
     * 设置选择范围
     * 本函数用于设置选择范围，如起始位置和结束位置相同，则功能为设置光标位置
     * @param BaseLineIndex1 long 起始基行号
     * 起始基行号。第一个基行的序号是0,如果为-1,则说明起始位置为文档末尾，
     * 系统将自动计算出位置，忽略CellIndex1，LineIndex1，FieldElemIndex1，
     * CharPos1参数
     * @param CellIndex1 long 起始单元号
     * 起始单元号,如果不在表格内,则赋值-1,否则需要赋值单元格的序号
     * @param LineIndex1 long 起始行号
     * 起始行号
     * @param FieldElemIndex1 long 起始元素号
     *  起始元素号
     * @param CharPos1 long 起始字符位置
     * 起始字符位置
     * @param BaseLineIndex2 long 结束基行号
     *  结束基行号。第一个基行的序号是0,如果为-1,则说明结束位置为文档末尾，
     * 系统将自动计算出位置，忽略CellIndex2，LineIndex2，FieldElemIndex2，
     * CharPos2参数
     * @param CellIndex2 long 结束单元号
     * 结束单元号,如果不在表格内,则赋值-1,否则需要赋值单元格的序号
     * @param LineIndex2 long 结束行号
     *  结束行号
     * @param FieldElemIndex2 long 结束元素号
     * 结束元素号
     * @param CharPos2 long 结束字符位置
     * 结束字符位置
     * @return boolean 如果成功返回TRUE,否则返回FALSE
     */
    public boolean setSel(long BaseLineIndex1, long CellIndex1, long LineIndex1,
                          long FieldElemIndex1, long CharPos1,
                          long BaseLineIndex2, long CellIndex2, long LineIndex2,
                          long FieldElemIndex2, long CharPos2) {
        int value;
        out("begin BaseLineIndex1=" + BaseLineIndex1 + " CellIndex1=" +
            CellIndex1 + " LineIndex1=" + LineIndex1 + " FieldElemIndex1=" +
            FieldElemIndex1 + " CharPos1=" + CharPos1 +
            " BaseLineIndex2=" + BaseLineIndex2 + " CellIndex2=" + CellIndex2 +
            " LineIndex2=" + LineIndex2 + " FieldElemIndex2=" + FieldElemIndex2 +
            " CharPos2=" + CharPos2);
        value = SetSel(getHWND(), BaseLineIndex1, CellIndex1, LineIndex1,
                       FieldElemIndex1, CharPos1, BaseLineIndex2, CellIndex2,
                       LineIndex2, FieldElemIndex2, CharPos2);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 返回当前行容器的行数
     * 本函数用于返回当前行容器的行数
     * @return long 返回当前当前行容器的行数
     */
    public long getLineCount() {
        long value;
        out("begin");
        value = GetLineCount(getHWND());
        out("end value=" + value);
        return value;
    }

    /**
     * 返回当前元素的字符数
     * 本函数用于返回当前元素的字符数
     * @return long 返回当前元素的字符数
     */
    public long getCurFieldCharCount() {
        long value;
        out("begin");
        value = GetCurFieldCharCount(getHWND());
        out("end value=" + value);
        return value;
    }

    /**
     * 插入新域
     * 本函数用于在当前位置插入一个新的域
     * @param strName String 元素名称
     * 用于设置元素的名称，以后内容取值和XML输出需要使用到
     * @param strContent String 元素内容
     * 元素的内容
     * @param nLayerNo int 层次号
     * 元素的层次号，指定在XML输出时的层次，序号从1开始，1为最顶层
     * @param nFieldType int 元素类型
     * 元素的类型。0普通文字、1宏、2固定文本标题、3标题定位符
     * @param nPosition int 位置 元素的插入位置，如果>0则插入在当前元素的后面，否则插入到当前元素的前面
     * @return boolean 成功返回TRUE，失败返回FALSE
     */
    public boolean insertField(String strName, String strContent, int nLayerNo,
                               int nFieldType, int nPosition) {
        int value;
        out("begin strName=" + strName + " strContent=" + strContent +
            " nLayerNo=" + nLayerNo + " nFieldType=" + nFieldType +
            " nPosition=" + nPosition);
        value = InsertField(getHWND(), strName, strContent, nLayerNo,
                            nFieldType, nPosition);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 返回指定元素的文本内容
     * 本函数用于返回指定元素的文本内容
     * @param nBaseLineIndex long 基行行号
     * 元素所在基行的行号
     * @param nCellIndex long 表格单元序号
     * 元素所在表格单元的序号，如果不在表格单元中，此参数应设置为-1
     * @param nLineIndex long 行号
     * 所在行的行号，此行号相对与所在的容器
     * @param nFieldText long 元素序号
     * 元素的序号
     * @param bSelected boolean 选择标志
     * TRUE返回此元素选中部分的文本，FALSE返回此元素的所有文本
     * 如果nBaseLineIndex、nCellIndex、nLineIndex、nFieldIndex均为-1，则返回当前元素的内容
     * @return String 返回字符串
     */
    public String getFieldText(long nBaseLineIndex, long nCellIndex,
                               long nLineIndex, long nFieldText,
                               boolean bSelected) {
        String value;
        out("begin nBaseLineIndex=" + nBaseLineIndex + " nCellIndex=" +
            nCellIndex + " nLineIndex=" + nLineIndex + " nFieldText=" +
            nFieldText + " bSelected=" + bSelected);
        value = GetFieldText(getHWND(), nBaseLineIndex, nCellIndex, nLineIndex,
                             nFieldText, bSelected ? 1 : 0);
        out("end value=" + value);
        return value;
    }

    /**
     * 设置指定元素的文本内容
     * 本函数用于设置指定元素的文本内容
     * @param nBaseLineIndex long 基行行号
     * 元素所在基行的行号
     * @param nCellIndex long 表格单元序号
     * 元素所在表格单元的序号，如果不在表格单元中，此参数应设置为-1
     * @param nLineIndex long 行号
     * 所在行的行号，此行号相对与所在的容器
     * @param nFieldIndex long 元素序号
     * 元素的序号
     * @param strText String 内容
     * 需要设置的内容
     * @return boolean 成功返回TRUE，失败返回FALSE
     */
    public boolean setFieldText(long nBaseLineIndex, long nCellIndex,
                                long nLineIndex, long nFieldIndex,
                                String strText) {
        int value;
        out("begin nBaseLineIndex=" + nBaseLineIndex + " nCellIndex=" +
            nCellIndex + " nLineIndex=" + nLineIndex + " nFieldIndex" +
            nFieldIndex + " strText=" + strText);
        value = SetFieldText(getHWND(), nBaseLineIndex, nCellIndex, nLineIndex,
                             nFieldIndex, strText);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 查找元素
     * 本函数用于查找符合条件的元素
     * @param strFindText String 元素名称
     * 扩充如果元素名称后用％结尾,如“姓%”，则查找以“姓”为前缀的元素
     * @param nLayerNo int 查找的层次
     * 元素的层次好，如果为-1，则查找所有层次的元素
     * @param nFindType int 元素类型
     * 元素类型，1元素、2表格、3为表格或元素
     * 扩充：如果需要按定位符属性查找，则在上述值上+9,
     * 例如：查找元素有定位符属性的，则赋值为10，查找表格有定位符属性的，则赋值为11
     * @param bFromBegin boolean 从起始位置开始查找
     * TRUE从起始位置开始查找，FALSE从当前位置开始查找
     * @return boolean 成功返回TRUE，失败返回FALSE，并且光标定位到查找到的位置
     */
    public boolean findField(String strFindText, int nLayerNo, int nFindType,
                             boolean bFromBegin) {
        int value;
        out("begin strFindText=" + strFindText + " nLayerNo=" + nLayerNo +
            " nFindType=" + nFindType + " bFromBegin=" + bFromBegin);
        value = FindField(getHWND(), strFindText, nLayerNo, nFindType,
                          bFromBegin ? 1 : 0);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 设置宏
     * 本函数用于设置宏的值
     * @param strName String 名称
     * @param strValue String 值
     * @return boolean 成功返回TRUE，失败返回FALSE
     */
    public boolean setMicroField(String strName, String strValue) {
        int value;
        out("begin strName=" + strName + " strValue=" + strValue);
        value = SetMicroField(getHWND(), strName, strValue);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 替换宏
     * 本函数用于用当前宏的值替换文本中宏的内容
     * @param bOnlyCanEdit boolean 替换范围
     *  TRUE只处理可编辑状态下的宏，及宏所在的行不是只读状态。FALSE处理文档所有的宏
     * @return boolean 成功返回TRUE，失败返回FALSE
     */
    public boolean updateMicroField(boolean bOnlyCanEdit) {
        int value;
        out("begin bOnlyCanEdit=" + bOnlyCanEdit);
        value = UpdateMicroField(getHWND(), bOnlyCanEdit ? 1 : 0);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 替换元素内容
     * 本函数用于替换指定元素的部分内容
     * @param nBaseLineIndex long 基行行号
     * 元素所在基行的行号
     * @param nCellIndex long 表格单元序号
     * 元素所在表格单元的序号，如果不在表格单元中，此参数应设置为-1
     * @param nLineIndex long 行号
     * 所在行的行号，此行号相对与所在的容器
     * @param nFieldIndex long 元素序号
     * 元素的序号
     * @param strOld String 被替换的内容
     * 被替换的内容
     * @param strNew String 替换内容
     * 替换的内容
     * @return boolean 成功返回TRUE，失败返回FALSE
     * 如果nBaseLineIndex、nCellIndex、nLineIndex、nFieldIndex均为-1，则操作的对象是当前元素
     */
    public boolean replaceFieldText(long nBaseLineIndex, long nCellIndex,
                                    long nLineIndex, long nFieldIndex,
                                    String strOld, String strNew) {
        int value;
        out("begin nBaseLineIndex=" + nBaseLineIndex + " nCellIndex=" +
            nCellIndex + " nLineIndex=" + nLineIndex + " nFieldIndex=" +
            nFieldIndex + " strOld=" + strOld + " strNew=" + strNew);
        value = ReplaceFieldText(getHWND(), nBaseLineIndex, nCellIndex,
                                 nLineIndex, nFieldIndex, strOld, strNew);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 设置修订版本
     * 本函数用于设置修订版本信息
     * @param nRevisialID long 修订号
     * 修订号，-1,则修订号由系统自动生成,>0，则为手工设置修订号,如果此号已经存在,则忽略这个设置,将返回-1
     * @param nAuthorization int 权限等级
     * 权限等级，0为普通权限，这里的数值需>=0，等级的值决定了修改和删除的痕迹线条的数目
     * @param strUserName String 用户名
     * 当前操作的用户名
     * @param strUserCode String 用户代码
     * 当前操作的用户代码
     * @param strExtent String 备注信息
     * 需要登记的扩展信息。当nRevisialID>0时,内容格式为“修订时间;终端名”
     * @return long 成功返回返回当前的文档的修订次数，失败返回-1
     * 本函数用于设置修订的痕迹，系统默认取出文档的最后一次修订信息，因此如果需要改变修订，
     * 需要在用户能操作前就设定，同一次修订痕迹的信息相同。
     */
    public long setRevisalInfo(long nRevisialID, int nAuthorization,
                               String strUserName, String strUserCode,
                               String strExtent) {
        long value;
        out("begin nRevisialID=" + nRevisialID + " nAuthorization=" +
            nAuthorization + " strUserName=" + strUserName + " strUserCode=" +
            strUserCode + " strExtent=" + strExtent);
        value = SetRevisalInfo(getHWND(), nRevisialID, nAuthorization,
                               strUserName, strUserCode, strExtent);
        out("end value=" + value);
        return value;
    }

    /**
     * 设置基行选中范围
     * 本函数用于设置基行的选中范围
     * @param nStartIndex long 开始行号
     * 开始行号，从0开始
     * @param nEndIndex long 结束行号
     * 结束行号，最大为基行总数-1
     * @return boolean
     * 成功返回TRUE，失败返回FALSE
     */
    public boolean selectBaseLineRange(long nStartIndex, long nEndIndex) {
        int value;
        out("begin nStartIndex=" + nStartIndex + " nEndIndex=" + nEndIndex);
        value = SelectBaseLineRange(getHWND(), nStartIndex, nEndIndex);
        out("end value=" + value);
        return value == 1;
    }

    /**
     * 获取对象的数目
     * 本函数用于获取对象的数目
     * @param nBaseLineIndex long 基行行号
     * 基行的行号
     * @param nCellIndex long 表格单元格序号
     * 表格单元的序号，如果不是表格单元，则设置为-1
     * @param nLineIndex long 行号
     * 行号
     * @param nType long 类别
     * 统计的类别。0指定行的元素个数、
     * 1 如果当前行是表格则返回表格的单元数，否则返回-1、
     * 2 如果当前行是表格则返回表格的行数，否则返回-1、
     * 3 如果当前行是表格则返回表格的列数，否则返回-1、
     * 4 如果当前行是表格则返回表格的单元包含的行数，否则返回-1
     * @return long 成功返回数量>=0，失败返回-1
     */
    public long getObjectCount(long nBaseLineIndex, long nCellIndex,
                               long nLineIndex, long nType) {
        long value;
        out("begin nBaseLineIndex=" + nBaseLineIndex + " nCellIndex=" +
            nCellIndex + " nLineIndex=" + nLineIndex + " nType=" + nType);
        value = GetObjectCount(getHWND(), nBaseLineIndex, nCellIndex,
                               nLineIndex, nType);
        out("end value=" + value);
        return value;
    }

    /**
     * 获取光标位置索引
     * 本函数用于获取光标位置对象的索引
     * @param nType long 索引的类别。
     * 0 指定当前基行的索引、
     * 1 当前表格单元索引、
     * 2 当前行索引、
     * 3 当前元素索引
     * @return long 成功返回数量>=0，失败返回-1
     */
    public long getCurObjectIndex(long nType) {
        long value;
        out("begin nType=" + nType);
        value = GetCurObjectIndex(getHWND(), nType);
        out("end value=" + value);
        return value;
    }

    /**
     * 本函数用于为通用扩展，返回结果为布尔型
     * @param strParam1 String
     * @param strParam2 String
     * @param nParam1 long
     * @param nParam2 long
     * @return boolean
     * strParam1,strParam2,nParam1,nParam2 参数说明如下:
     * 2.10.1	文档修改标志 "","",1,0
     * 文档如果被修改过成功返回TRUE，否则返回FALSE
     * 2.10.2	在当前光标位置插入字符串 "需要插入的字符串","",2,0
     * 如果字符串插入成功返回TRUE，否则返回FALSE
     * 如果当前位置有选中状态,则执行此函数时会根据当前的权限先执行删除操作。
     * 2.10.3	判断当前光标位置的行是否只读 "","",3,0
     * 如果所在行只读或文档是只读状态，则返回TRUE，否则返回FALSE
     * ...
     * ...参数多多结果多多...
     * 请参考<<电子病历编辑器V3.0开发手册.doc>>文档
     */
    public boolean universalBoolFunction(String strParam1, String strParam2,
                                         long nParam1, long nParam2) {
        boolean value;
        out("begin strParam1=" + strParam1 + " strParam2=" + strParam2 +
            " nParam1=" + nParam1 + " nParam2=" + nParam2);
        value = UniversalBoolFunction(getHWND(), strParam1, strParam2, nParam1,
                                      nParam2) == 1;
        out("end value=" + value);
        return value;
    }

    /**
     * 本函数用于为通用扩展，返回结果为字符串
     * @param strParam1 String
     * @param strParam2 String
     * @param strParam3 String
     * @param strParam4 String
     * @param strParam5 String
     * @param strParam6 String
     * @return String
     * ...
     * ...参数多多结果多多...
     * 请参考<<电子病历编辑器V3.0开发手册.doc>>文档
     */
    public String universalStringFunction(String strParam1, String strParam2,
                                          String strParam3, String strParam4,
                                          String strParam5, String strParam6) {
        String value;
        out("begin strParam1=" + strParam1 + " strParam2=" + strParam2 +
            " strParam3=" + strParam3 + " strParam4=" + strParam4 +
            " strParam5=" + strParam5 + " " + strParam6);
        value = UniversalStringFunction(getHWND(), strParam1, strParam2,
                                        strParam3, strParam4, strParam5,
                                        strParam6);
        out("end value=" + value);
        return value;
    }

    /**
     * 返回当前的光标位置
     * 本函数用于返回当前的光标位置
     * @return EMRPadCurCursor
     */
    public EMRPadCurCursor getCurCursorPos() {
        out("begin");
        EMRPadCurCursor value = new EMRPadCurCursor();
        String s = universalStringFunction("1", "", "", "", "", "24");
        StringTokenizer stk = new StringTokenizer(s, "|");
        try {
            value.nBaseLineIndex = Long.parseLong(stk.nextToken());
            value.nCellIndex = Long.parseLong(stk.nextToken());
            value.nLineIndex = Long.parseLong(stk.nextToken());
            value.nFieldElemIndex = Long.parseLong(stk.nextToken());
            value.nCharPos = Long.parseLong(stk.nextToken());
        }
        catch (Exception e) {
            err("Exception:" + e.getMessage());
        }
        out("end value=" + value);
        return value;
    }

    /**
     * 初始化OCX组件
     * 注意此方法需要运行在父类窗口的setVisible(true)方法之后才有效.
     */
    public void initOCX() {
        out("begin");
        checkInit();
        UniversalBoolFunction(getHWND(), OCX_ID, "", 98, INIT_ID);
        ( (JComponent)this.getParent()).
            addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                hidePopup();
            }

            public void ancestorRemoved(AncestorEvent event) {
                hidePopup();
            }

            public void ancestorMoved(AncestorEvent event) {
                hidePopup();
            }
        });
        thread.start();
        out("end");
    }

    /**
     * 关闭OCX组件
     */
    public void closeOCX() {
        out("begin");
        thread = null;
        while (threadStart)
            try {
                Thread.sleep(1);
            }
            catch (Exception e) {}
        out("closeOCX=" + closeOCX(this.getHWND()));
        while (UniversalBoolFunction(getHWND(), "", "", 3002, 0) == 1) {
            try {
                Thread.sleep(1);
            }
            catch (Exception e) {}
            out("1");
        }
        out("end");
    }

    public void popMenu(JPopupMenu popup, int x, int y) {
        out("begin x=" + x + " y=" + y);
        if (frame == null)
            return;
        if (popMenuWindow == null) {
            popMenuWindow = new TWindow(frame);
            popMenuWindow.setVisible(true);
        }

        //Point p = getLocationOnScreen();
        popMenuWindow.setLocation(frame.getX(), frame.getY());
        popMenuWindow.setSize(0, 0);
        popup.show(popMenuWindow, x, y);
        popup.repaint();
        this.popup = popup;
        out("end");
    }

    public void hidePopup() {
        if (popup != null && popup.isVisible()) {
            popup.setVisible(false);
            popup = null;
        }
    }

    class Eventlistener
        implements Runnable {
        public void run() {
            int sleepCount = 1;
            out("begin");
            threadStart = true;
            while (thread != null) {
                try {
                    Thread.sleep(sleepCount);
                    //选中keyword
                    if (thread == null)
                        break;
                    if (UniversalBoolFunction(getHWND(), "", "", 3004, 0) == 1)
                        SelectKeyWordEvent();
                    Thread.sleep(sleepCount);
                    if (thread == null)
                        break;
                    //右键按下
                    if (UniversalBoolFunction(getHWND(), "", "", 3005, 0) == 1)
                        hidePopup();
                    Thread.sleep(sleepCount);
                    if (thread == null)
                        break;
                    //左键按下
                    if (UniversalBoolFunction(getHWND(), "", "", 3006, 0) == 1)
                        hidePopup();
                    Thread.sleep(sleepCount);
                    if (thread == null)
                        break;
                    //右键抬起
                    if (UniversalBoolFunction(getHWND(), "", "", 3007, 0) == 1)
                        RClickEvent();
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            threadStart = false;
            out("end");
        }
    }


    public void SelectKeyWordEvent() {
        out("begin");
        /*if(parentObject == null)
          return;
         parentObject.eventListener(tag,DyEventListener.ValueChanged,this);*/
        out("end");
        //System.out.println("SelectKeyWordEvent()" + GetFieldText(-1,-1,-1,-1,true));
    }

    public void RClickEvent() {
        out("begin");
        /*if(parentObject == null)
          return;
         parentObject.eventListener(tag,DyEventListener.MouseClicked,this);*/
        out("end");
    }

    /**
     * 静态块加在 EMRPad30_JavaHis.dll 文件
     */
    static {
        System.loadLibrary("EMRPad30_JavaHis");
    }

    /**
     * 日志输出
     * @param text String 日志内容
     */
    public void out(String text) {
        log.out(text);
    }

    /**
     * 日志输出
     * @param text String 日志内容
     * @param debug boolean true 强行输出 false 不强行输出
     */
    public void out(String text, boolean debug) {
        log.out(text, debug);
    }

    /**
     * 错误日志输出
     * @param text String 日志内容
     */
    public void err(String text) {
        log.err(text);
    }
}
