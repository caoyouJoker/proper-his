package com.javahis.ui.opd;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import jdo.sys.Operator;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import com.dongyang.util.TMessage;
import com.dongyang.data.TSocket;

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
public class OPDComTemplatePhraseControl
    extends TControl {
    private static final String PHRASE_FILE_PATH = "JHW/片语";
    //进参中科室医师区别
    private String deptOrDr = "";
    //进参中科室、医师代码
    private String deptOrDrCode = "";

    //TABLE
    private TTable table;

    //COMBOBOX 部门
    private TComboBox combo;
    //片语类型(以后可能是多层)
    private TComboBox comboPhraseType;

    //片语简码
    private TTextField phraseCode;

    //当前选中的片语模版路径
    private String seletedPhraseFilePath;
   //当前选中的片语文件名;
    private String seletedPhraseFileName;
    //原片语简码
    private String oldPhraseCode;
    //
    private int selectRow = -1;


    public OPDComTemplatePhraseControl() {
    }


    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        deptOrDr = this.getParameter() + "";
        //this.messageBox("===deptOrDr==="+deptOrDr);
        if ("1".equalsIgnoreCase(deptOrDr)) {
            combo = (TComboBox)this.getComponent("DEPT");
            //心血管需更改，传入操作医生所在部分??????
            deptOrDrCode = Operator.getDept();
            combo.setSelectedID(deptOrDrCode);
            combo.setVisible(true);
            combo.setEnabled(false);
        }
        comboPhraseType = (TComboBox)this.getComponent("PHRASE_TYPE");
        //初始化类型
        comboPhraseType.setStringData(
            "[[id,text],['',''],[1,主诉],[2,现病史],[3,体征],[4,家族史],[5,既往史],[6,个人史],[7,婚育史],[8,家族史]]");
        //
        table = (TTable)this.getComponent("TABLECOM");

        //片语简码
        phraseCode = (TTextField)this.getComponent("PHRASE_CODE");

        callFunction("UI|TABLECOM|addEventListener",
                     "TABLECOM->" + TTableEvent.CLICKED, this, "onTableClicked");

        //加入双击打开片语编辑
        table.addEventListener("TABLECOM->" + TTableEvent.DOUBLE_CLICKED, this,
                               "onTableDoubleClicked");
        //初始化表格
        initTable();

    }

    private void initTable() {
        TParm parm = new TParm(this.getDBTool().select(
            "SELECT * FROM OPD_COMTEMPLATEPHRASE WHERE DEPT_OR_DR='" + deptOrDr +
            "' AND DEPTORDR_CODE='" + deptOrDrCode +
            "' ORDER BY PHRASE_CODE"));
        table.setParmValue(parm);

    }

    /**
     * 单击修改
     * @param row int
     */
    public void onTableClicked(int row) {
        //// System.out.println("=========onTableClicked============" + row);
        if (row < 0) {
            return;
        }

        //片语简码
        phraseCode.setValue(table.getParmValue().getValue("PHRASE_CODE", row));
        //片语类型
        comboPhraseType.setSelectedID(table.getParmValue().getValue(
            "PHRASE_TYPE", row));

        oldPhraseCode=phraseCode.getValue();

    }


    /**
     * 打开EMR片语编辑窗体
     * @param row int
     */
    public void onTableDoubleClicked(int row) {
        //// System.out.println("=========OpenEMRTemplateEdit============" + row);
        //打开结构化片语编辑窗体;
        if (row < 0) {
            return;
        }
        //是否存在已保存路径
        //NEW|EDIT
        seletedPhraseFilePath = table.getParmValue().getValue(
            "PHRASE_FILE_PATH", row);
        seletedPhraseFileName = table.getParmValue().getValue(
            "PHRASE_FILE_NAME", row);
       /** this.messageBox("===onTableDoubleClicked seletedPhraseFilePath===" +
                        seletedPhraseFilePath);**/
        TParm inParm = new TParm();
        //修改
        if (seletedPhraseFilePath != null && !seletedPhraseFilePath.equals("")) {
            inParm.setData("opType", "EDIT");
            inParm.setData("phraseFilePath", seletedPhraseFilePath);
            inParm.setData("phraseFileName", seletedPhraseFileName);
        }
        else {
            //新增;
            inParm.setData("opType", "NEW");
            inParm.setData("phraseFilePath", PHRASE_FILE_PATH);
            inParm.setData("phraseFileName",
                           deptOrDr + "_" + deptOrDrCode + "_" +
                           table.getParmValue().getValue("PHRASE_CODE", row));
        }

        inParm.setData("deptOrDr", deptOrDr);
        inParm.setData("deptOrDrCode", deptOrDrCode);
        inParm.setData("phraseCode",
                       table.getParmValue().getValue("PHRASE_CODE", row));

        /** System.out.println("=====seletedPhraseFilePath======" +
                           seletedPhraseFilePath);
        // System.out.println("=====seletedPhraseFileName======" +
                           seletedPhraseFileName);**/
        this.openDialog("%ROOT%\\config\\emr\\EMREditComPhrase.x", inParm);
        initTable();

    }

    /**
     * 清空操作
     */
    public void onClear() {
        //设置片语简码为空
        setValue("PHRASE_CODE", "");
        //片语类型为空
        setValue("PHRASE_TYPE", "");
        ( (TTable) getComponent("TABLECOM")).clearSelection();
        selectRow = -1;

    }

    /**
     * 删除记录;
     */
    public void onDelete() {
        //删除数据库
        if (this.messageBox( "询问","是否删除", 2) == 0) {
            //this.messageBox("table row"+table.getSelectedRow());
            if (table.getSelectedRow() == -1)
                return;
            int selRow = table.getSelectedRow();
            TParm tableParm = table.getParmValue();
            String selPhraseCode = tableParm.getValue("PHRASE_CODE", selRow);
            String selPhraseFilePath = tableParm.getValue("PHRASE_FILE_PATH", selRow);
            String selPhraseFileName = tableParm.getValue("PHRASE_FILE_NAME", selRow);

            //deptOrDr  //
            String sql="DELETE FROM  OPD_COMTEMPLATEPHRASE  WHERE DEPT_OR_DR ='"+deptOrDr+"'";
                   sql+=" AND DEPTORDR_CODE='"+deptOrDrCode+"'";
                   sql+=" AND PHRASE_CODE='"+selPhraseCode+"'";

            TParm result =new TParm(this.getDBTool().update(sql));
            if (result.getErrCode() < 0) {
                //messageBox(result.getErrText());
                this.messageBox("删除失败！");
                return;
            }
            //成功则删除对应片语文件;
            this.delFileTempletFile(selPhraseFilePath,selPhraseFileName);
            //删除单行显示
            int row = (Integer) callFunction("UI|TABLECOM|getSelectedRow");
            if (row < 0)
                return;
            this.callFunction("UI|TABLECOM|removeRow", row);
            this.callFunction("UI|TABLECOM|setSelectRow", row);

            this.messageBox("P0003");
            this.onClear();
        }
        else {
            return;
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
     * 保存
     */
    public void onSave() {
        int selected = table.getSelectedRow();
        //this.messageBox("selected" + selected);
        //校检;
        if(phraseCode.getValue()==null||phraseCode.getValue().equals("")){
            this.messageBox("请填写片语简码！");
            return;
        }
        //this.messageBox("comboPhraseType.getSelectedID()"+comboPhraseType.getSelectedID());
        if(comboPhraseType.getSelectedID()==null ||comboPhraseType.getSelectedID().equals("") ){
            this.messageBox("请选择片语类型！");
            return;
        }


        String userID = Operator.getID();
        String userIP = Operator.getIP();
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd hh:mm:ss");

        String py = TMessage.getPy(phraseCode.getValue());

        //新增
        if (selected == -1) {
            //// System.out.println("===========insert================");
            String sql="INSERT INTO OPD_COMTEMPLATEPHRASE(DEPT_OR_DR,DEPTORDR_CODE,PHRASE_CODE,PHRASE_TYPE,OPT_USER,OPT_DATE,OPT_TERM,PY1) VALUES('" +
                                    deptOrDr + "','" + deptOrDrCode + "','" +
                                    phraseCode.getValue() + "','" +
                                    comboPhraseType.getSelectedID() + "','" +
                                    userID + "',TO_DATE('" +sysDate+"','YYYY/MM/DD HH24:MI:SS') ,'" + userIP +
                                    "','" + py + "')";

            //// System.out.println("=====INSERT INTO sql======"+sql);
            TParm parm = new TParm(this.getDBTool().update(sql));
             if (parm.getErrCode() < 0) {
                 this.messageBox("保存失败！");
                 return;

             }else{
                 this.messageBox("保存成功！");
                 //return;
             }



        }
        else {
            //更新库记录；
            //// System.out.println("===========update================");
            String sql="UPDATE OPD_COMTEMPLATEPHRASE SET PHRASE_CODE='"+phraseCode.getValue() +"',";
                   sql+="PHRASE_TYPE='"+comboPhraseType.getSelectedID()+"',";
                   sql+="OPT_USER='"+userID+"',";
                   sql+="OPT_DATE=TO_DATE('" +sysDate+"','YYYY/MM/DD HH24:MI:SS') ,";
                   sql+="OPT_TERM='"+userIP+"',";
                   sql+="PY1='"+py+"'";
                   sql+=" WHERE DEPT_OR_DR='"+deptOrDr+"'";
                   sql+=" AND DEPTORDR_CODE='"+deptOrDrCode+"'";
                   sql+=" AND PHRASE_CODE='"+oldPhraseCode+"'";
            //// System.out.println("====update sql====="+sql);
            TParm parm = new TParm(this.getDBTool().update(sql));

             if (parm.getErrCode() < 0) {
                 this.messageBox("保存失败！");
                 return;

             }else{
                 //记录更新成功并且假如片语简码与原片语简码不一致，则对应片语文件更名;
                 if (!oldPhraseCode.equals(phraseCode.getValue())) {
                     //复制一个一个新文件??????

                     //删除原文件??????
                 }
                  this.messageBox("保存成功！");

             }
        }
        this.onClear();
        //刷新列表;
        initTable();

    }

    /**
     *
     */
    public void onEdit() {
        int row = table.getSelectedRow();
        onTableDoubleClicked(row);
    }

    /**
     * 获得片语全路径
     * @return String
     */
    /** private String getPhraseFullPath(int row) {
         //目录表第一个根目录FILESERVER
         String rootName = TIOM_FileServer.getRoot();
         //模板路径服务器
         String templetPathSer = TIOM_FileServer.getPath("EmrTemplet");
         //
         //// System.out.println("===rootName==="+rootName);
         //// System.out.println("===templetPathSer==="+templetPathSer);
         //1_01020101_慢性胃炎-现病史    类型_科室编码_XXXX
         String fileName = deptOrDr + "_" + deptOrDrCode + "_" +
             table.getParmValue().getValue("PHRASE_CODE", row);
         String fullPath = rootName + templetPathSer + PHRASE_FILE_PATH +
             fileName + ".jhw";
         //格式化fullPath
         fullPath = fullPath.replaceAll("\\\\", "/");
         // System.out.println("======fullPath=======" + fullPath);

         return fullPath;
     }**/


    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    private TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

}
