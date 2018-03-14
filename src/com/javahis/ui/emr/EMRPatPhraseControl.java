package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWord;
import jdo.sys.Operator;
import com.dongyang.tui.text.CopyOperator;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;

import java.util.UUID;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.text.EComponent;

/**
 * <p>Title: 收藏编辑病患信息</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EMRPatPhraseControl
    extends TControl {
    private static String TABLE = "TABLE_PATINFO";
    /**
     * WORD对象
     */
    private static final String TWORD_NAME = "WORD";
    //病患信息收藏目录;
    private static final String PAT_INF_ROOT = "JHW/病患信息收藏夹/";


    private TParm evenParm;
    private String opType;

    private TButton btnOk;
    private TButton btnClose;
    private TButton btnSave;
    private TButton btnDel;
    private TTextField txtPhraseID;
    private TTextField txtMrNo;
    private TTextField txtPATName;
    private TTextField txtPhraseCode;
    private String strMrNo;
    private TTable tablePatInfo;


    /**
     * WORD对象
     */
    private TWord parmWord;
    private TWord word;


    /**
     * 当前编辑状态
     */
    private String onlyEditType;


    public EMRPatPhraseControl() {
    }

    public void onInit() {
        super.onInit();

        Object obj = this.getParameter();
        if (obj != null) {
            evenParm = (TParm) obj;
            opType = evenParm.getValue("OP_TYPE");
            parmWord = (TWord) evenParm.getData("TWORD");

            btnOk = (TButton)this.getComponent("BtnOk");
            btnClose = (TButton)this.getComponent("BtnClose");
            btnSave = (TButton)this.getComponent("BtnSave");
            btnDel = (TButton)this.getComponent("BtnDel");
            txtMrNo = (TTextField)this.getComponent("MRNO");
            txtPATName = (TTextField)this.getComponent("PATName");
            txtPhraseCode = (TTextField)this.getComponent("PhraseName");
            txtPhraseID = (TTextField)this.getComponent("PhraseID");

            word = (TWord)this.getComponent(TWORD_NAME);
            strMrNo = evenParm.getValue("MR_NO");
            txtMrNo.setText(strMrNo);
            txtPATName.setText(evenParm.getValue("PAT_NAME"));

            //收藏病患信息;
            if (opType.equals("SavePatInfo")) {
                txtPhraseCode.setEnabled(true);
                btnOk.setVisible(false);
                btnClose.setVisible(false);
                this.setOnlyEditType("NEW");

                //可编辑
                this.word.setCanEdit(true);
                //编辑状态(非整洁)
                this.word.onEditWord();
                this.word.onPaste();
                CopyOperator.clearComList();
                //插入病患信息;
            }
            else if (opType.equals("InsertPatInfo")) {
                btnSave.setVisible(false);
                btnDel.setVisible(false);

            }
            //初始化列表
            tablePatInfo = (TTable)this.getComponent(TABLE);
            initTable();
            //列表双击打开事件;
            //注册Table点击事件
            callFunction("UI|" + TABLE + "|addEventListener",
                         TABLE + "->" + TTableEvent.DOUBLE_CLICKED, this,
                         "onDoubleTableClicked");

        }

    }

    /**
     * 初始化列表
     */
    public void initTable() {
        //名字；文件路径；文件名
        TParm parm = new TParm(this.getDBTool().select(
            "SELECT PHRASE_NAME,FILE_NAME,FILE_PATH FROM EMR_PATINFO_FOLDER WHERE MR_NO='" +
            strMrNo + "' ORDER BY OPT_DATE DESC"));
        tablePatInfo.setParmValue(parm);
    }

    /**
     * 保存
     */
    public void onSave() {
        //this.messageBox("come in"+txtPhraseCode.getText());
        //判断个数最大值10个限止
        if(tablePatInfo.getRowCount()==10){
            this.messageBox("每个病患最多只能存10条信息！");
            return;
        }

        if (txtPhraseCode.getText().equals("")) {
            this.messageBox("请输入信息名称！");
            return;
        }
        //日期
        String dateStr = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy年MM月dd日 HH时mm分ss秒");
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd hh:mm:ss");

        //新增
        if ("NEW".equals(this.getOnlyEditType())) {
            word.setMessageBoxSwitch(false);
            this.word.setFileAuthor(Operator.getID());
            //公司
            this.word.setFileCo("JAVAHIS");
            //标题
            this.word.setFileTitle(txtPhraseCode.getText());
            //创建时间
            this.word.setFileCreateDate(dateStr);
            //最后修改人
            this.word.setFileLastEditUser(Operator.getID());
            //最后修改日期
            this.word.setFileLastEditDate(dateStr);
            //最后修改IP
            this.word.setFileLastEditIP(Operator.getIP());
            //另存为
            String id = this.getUUID();
            boolean success = word.onSaveAs(PAT_INF_ROOT + "/" + strMrNo,
                                            id, 3);
            if (success) {
                String sql = "INSERT INTO EMR_PATINFO_FOLDER (ID,MR_NO,PHRASE_NAME,FILE_PATH,FILE_NAME,OPT_USER,OPT_DATE,OPT_TERM)";
                sql += " VALUES('" + id + "',";
                sql += "'" + strMrNo + "',";
                sql += "'" + txtPhraseCode.getText() + "',";
                sql += "'" + PAT_INF_ROOT + strMrNo + "',";
                sql += "'" + id + "',";
                sql += "'" + Operator.getID() + "',";
                sql += "TO_DATE('" + sysDate +
                    "','YYYY/MM/DD HH24:MI:SS') ,";
                sql += "'" + Operator.getIP() + "')";
                // System.out.println("===sql====" + sql);
                //成功后，插入数据表EMR_PATINFO_FOLDER;
                TParm parm = new TParm(this.getDBTool().update(sql));

                if (parm.getErrCode() < 0) {
                    this.messageBox("保存失败！");
                    return;
                }

                this.messageBox("保存成功！");
                //更新主键
                txtPhraseID.setText(id);
                this.setOnlyEditType("EDIT");

            }
            //更新
        }
        else {
            //this.messageBox("更新编辑 text====" + txtPhraseID.getText());
            String id = txtPhraseID.getText();
            //设置提示不可用
            this.word.setMessageBoxSwitch(false);
            //最后修改人
            this.word.setFileLastEditUser(Operator.getID());
            //最后修改日期
            this.word.setFileLastEditDate(dateStr);
            //最后修改IP
            this.word.setFileLastEditIP(Operator.getIP());
            //保存
            boolean success = word.onSaveAs(PAT_INF_ROOT + "/" + strMrNo,
                                            txtPhraseID.getText(), 3);
            if (success) {
                String sql = "UPDATE EMR_PATINFO_FOLDER SET ";
                sql += "MR_NO='" + strMrNo + "',"; //,,FILE_PATH,FILE_NAME,OPT_USER,OPT_DATE,OPT_TERM)";
                sql += "PHRASE_NAME='" + txtPhraseCode.getText() + "',";
                sql += "FILE_PATH='" + PAT_INF_ROOT + strMrNo + "',";
                sql += "FILE_NAME='" + id + "',";
                sql += "OPT_USER='" + Operator.getID() + "',";
                sql += "OPT_DATE=TO_DATE('" + sysDate +
                    "','YYYY/MM/DD HH24:MI:SS') ,";
                sql += "OPT_TERM='" + Operator.getIP() + "'";
                sql += " WHERE ID='" + id + "'";

                //System.out.println("=====sql====="+sql);
                //成功后，插入数据表EMR_PATINFO_FOLDER;
                TParm parm = new TParm(this.getDBTool().update(sql));

                if (parm.getErrCode() < 0) {
                    this.messageBox("保存失败！");
                    return;
                }

                this.messageBox("保存成功！");
                this.setOnlyEditType("EDIT");

            }

        }
        initTable();
    }

    /**
     * 删除片语
     */
    public void onDelete() {
        final int row=tablePatInfo.getSelectedRow();
        //this.messageBox("==row="+row);
        if(row<0){
            this.messageBox("请先选择删除信息！");
            return;
        }

        //删除数据库
        if (this.messageBox("询问", "是否删除？", 2) == 0) {
            final String fileName=(String) tablePatInfo.getParmValue().getData("FILE_NAME",row);
            final String filePath=(String) tablePatInfo.getParmValue().getData("FILE_PATH",row);
            String sql="DELETE FROM EMR_PATINFO_FOLDER WHERE ID='"+fileName+"'";
            //删除数据库记录
              TParm parm = new TParm(this.getDBTool().update(sql));
              //清空名子，及ID；
              if (parm.getErrCode() < 0) {
                   this.messageBox("保存失败！");
                   return;
               }
              //删除相应的文件
              boolean flag= delFileTempletFile(filePath, fileName);
              this.messageBox("保存成功！");


            this.setOnlyEditType("NEW");
            //删除成功后，刷新表格;
            initTable();

        }



    }

    /**
     * 关闭
     */
    public boolean onClosing() {
        this.setReturnValue(this.getValue(TWORD_NAME));
        return true;
    }

    /**
     * 片语传回
     */
    public void onFetchBack() {
         final int row=tablePatInfo.getSelectedRow();
        if(row<0){
            this.messageBox("请先选择信息！");
            return;
        }
        final String fileName=(String) tablePatInfo.getParmValue().getData("FILE_NAME",row);
        final String filePath=(String) tablePatInfo.getParmValue().getData("FILE_PATH",row);
        //this.messageBox("filePath"+filePath);
        //this.messageBox("fileName"+fileName);

         EComponent com=parmWord.getFocusManager().getFocus();
         parmWord.getFocusManager().onInsertFile(filePath, fileName,
                                             3, false);
         parmWord.update();
    }

    public void setOnlyEditType(String onlyEditType) {
        this.onlyEditType = onlyEditType;
    }

    public String getOnlyEditType() {
        return this.onlyEditType;
    }

    public void onDoubleTableClicked(int row) {
        //this.messageBox("row" + row);
        //设置参数；
        if (row < 0) {
            return;
        }
        TParm data = (TParm) callFunction("UI|TABLE_PATINFO|getParmValue");
        String strPhraseName = data.getValue("PHRASE_NAME", row);
        txtPhraseCode.setText(strPhraseName);
        String strFileName = data.getValue("FILE_NAME", row);
        txtPhraseID.setText(strFileName);
        String strFilePath = data.getValue("FILE_PATH", row);

        //打开病历
        if (!this.word.onOpen(strFilePath,
                              strFileName, 3, true)) {
            return;
        }
        this.word.setCanEdit(true);
        this.setOnlyEditType("EDIT");

        //selectRow = row;



    }

    private synchronized String getUUID() {
        String s = UUID.randomUUID().toString();
        //去掉“-”符号
        return s.substring(0, 8) + s.substring(9, 13) + s.substring
            (14, 18) + s.substring(19, 23) + s.substring(24);
    }

    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }


    /**
    * 删除病历文件
    * @param templetPath String
    * @param templetName String
    * @return boolean
    */
   public boolean delFileTempletFile(String templetPath, String templetName) {
       //目录表第一个根目录FILESERVER
       String rootName = TIOM_FileServer.getRoot();
       //模板路径服务器
       String templetPathSer = TIOM_FileServer.getPath("EmrData");
       //拿到Socket通讯工具
       TSocket socket = TIOM_FileServer.getSocket();

       //删除文件
       boolean isDelFile = TIOM_FileServer.deleteFile(socket,
           rootName + templetPathSer +
           templetPath +
           "\\" + templetName + ".jhw");

       if (isDelFile) {
           return true;
       }

       return false;
   }



}
