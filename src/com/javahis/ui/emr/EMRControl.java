package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import com.javahis.device.EMRPad30;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TTree;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TTreeNode;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.FileTool;
import java.io.File;
import java.io.IOException;
import javax.swing.SwingUtilities;
import com.dongyang.data.TSocket;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import java.util.Map;
import java.util.StringTokenizer;

public class EMRControl extends TControl{
    /**
     * EMR类对象
     */
    static EMRPad30 emr;
    /**
     * 树的名字
     */
    private static final String TREE_NAME = "TREE";
    /**
     * 系统类别
     */
    private String systemType;
    /**
     * 病案号
     */
    private String mrNo;
    /**
     * 住院号
     */
    private String ipdNo;
    /**
     * 姓名
     */
    private String patName;
    /**
     * 拿到年
     */
    private String admYear;
    /**
     * 拿到月
     */
    private String admMouth;
    /**
     * 就诊号
     */
    private String caseNo;
    /**
     * 子面板数据
     */
    private TParm emrChildParm = new TParm();
    /**
     * 树根
     */
    private TTreeNode treeRoot;
    /**
     * 当前编辑文件名
     */
    private String fileNameOnly="";
    /**
     * 是否只读
     */
    private boolean isReadOnly = true;
    /**
     * 经治医师
     */
    private String vsDrCode;
    /**
     * 主治医师
     */
    private String attendDrCode;
    /**
     * 科主任
     */
    private String directorDrCode;
    /**
     * 模版类型
     */
    private String emrTypeCode;
    /**
     * 动作类名字
     */
    private static final String actionName = "action.odi.ODIAction";
    public void onInit(){
        super.onInit();
        //初始化界面
        initPage();
        //注册事件
        initEven();
    }
    /**
     * 注册事件
     */
    public void initEven(){
        //单击选中树项目
        addEventListener(TREE_NAME+"->" + TTreeEvent.DOUBLE_CLICKED, "onTreeDoubled");
    }
    /**
     * 初始化界面
     */
    public void initPage(){
        //得到系统类别
        Object obj = this.getParameter();
//        this.messageBox(""+obj);
        if(obj!=null){
            this.setSystemType(((TParm)obj).getValue("SYSTEM_TYPE"));
            this.setMrNo(((TParm)obj).getValue("MR_NO"));
            this.setIpdNo(((TParm)obj).getValue("IPD_NO"));
            this.setPatName(((TParm)obj).getValue("PAT_NAME"));
            this.setCaseNo(((TParm)obj).getValue("CASE_NO"));
            String yearStr = StringTool.getString((Timestamp)((TParm)obj).getData("ADM_DATE"),"yyyy");
            this.setAdmYear(yearStr);
            String mouthStr = StringTool.getString((Timestamp)((TParm)obj).getData("ADM_DATE"),"MM");
            this.setAdmMouth(mouthStr);
        }else{
            this.setSystemType("ODI");
            this.setMrNo("000000000151");
            this.setIpdNo("000000000047");
            this.setPatName("焦一测试3");
            this.setCaseNo("090508000001");
            this.setAdmYear("2009");
            this.setAdmMouth("05");
        }
        //加载EMR
        initEMR();
        //加载树
        loadTree();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initOCX();
                //加载EMR后执行
                emrOpen();
            }
        });
    }
    /**
     * 加载EMR后执行
     */
    public void emrOpen(){
        //初始化调用EMR
        emrInterface();
    }
    /**
     *
     */
    public void initOCX(){
        emr.initOCX();
    }
    /**
     * 申请编辑
     */
    public void onEdit(){
        //是否有要编辑的文件
        TParm emrparm = this.getEmrChildParm();
        if (emrparm.getData("EMR_FILE_DATA") == null) {
            this.messageBox("没有要编辑的文件！");
            return;
        }
        if(!this.isReadOnly){
            this.messageBox("文件已经处于编辑状态！");
            return;
        }
        TParm parm = (TParm)this.getEmrChildParm().getData("EMR_FILE_DATA");
        String creatFileUser = parm.getValue("CREATOR_USER");
        //三级检诊
        if("ODI".equals(this.getSystemType())){
            //是否有编辑的权限
            //是经治医师
            if((this.getVsDrCode().equals(Operator.getID())&&creatFileUser.equals(this.getAttendDrCode()))||(this.getVsDrCode().equals(Operator.getID())&&creatFileUser.equals(this.getDirectorDrCode()))){
                this.messageBox("您没有权限编辑此文件！");
                return;
            }
            //主治医师
            if(this.getAttendDrCode().equals(Operator.getID())&&creatFileUser.equals(this.getDirectorDrCode())){
                this.messageBox("您没有权限编辑此文件！");
                return;
            }
        }
        //是否处于编辑状态
        if(isEdit()){
            this.messageBox("此文件正处于编辑状态！");
            return;
        }
        emr.setDocumentMode(emr.DOCUMENT_MODE_EDIT);
        //System.out.println("打开病例参数" + parm);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("CURRENT_USER", Operator.getID());
        TParm result = TIOM_AppServer.executeAction(actionName,
            "updateEmrCurrentUser", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("调用错误！");
            return;
        }
        this.isReadOnly=false;
        this.fileNameOnly = this.getEmrChildParm().getValue("EMR_FILE_NAME");
    }
    /**
     * 是否可以查看
     * @return boolean
     */
    public boolean isCheckUserDr(){
        boolean falg = false;
        TParm parm = new TParm(this.getDBTool().select("SELECT VS_DR_CODE,ATTEND_DR_CODE,DIRECTOR_DR_CODE FROM ADM_INP WHERE CASE_NO='"+this.getCaseNo()+"' AND (VS_DR_CODE='"+Operator.getID()+"' OR ATTEND_DR_CODE='"+Operator.getID()+"' OR DIRECTOR_DR_CODE='"+Operator.getID()+"')"));
        if(parm.getInt("ACTION","COUNT")>0){
            //经治医师
            this.setVsDrCode(parm.getValue("VS_DR_CODE",0));
            //主治医师
            this.setAttendDrCode(parm.getValue("ATTEND_DR_CODE",0));
            //科主任
            this.setDirectorDrCode(parm.getValue("DIRECTOR_DR_CODE",0));
            falg = true;
        }
        return falg;
    }
    /**
     * 加载树
     */
    public void loadTree(){

        treeRoot = this.getTTree(TREE_NAME).getRoot();
        this.setValue("MR_NO", this.getMrNo());
        this.setValue("PAT_NAME", this.getPatName());
        //住院
        if(this.getSystemType().equals("ODI")){
           this.messageBox("ODI");
            this.setValue("IPD_NO", this.getIpdNo());
            //拿到树根
            treeRoot.setText("住院");
            treeRoot.setType("Root");
            treeRoot.removeAllChildren();
            //拿到节点数据
            TParm parm = this.getRootNodeData();
            if(parm.getInt("ACTION","COUNT")==0){
                this.messageBox("没有设置EMR模板！请检查数据库设置");
                return;
            }
            int rowCount = parm.getInt("ACTION","COUNT");
            for(int i=0;i<rowCount;i++){
                //加节点   EMR_CLASS表中的STYLE字段
                TTreeNode node = new TTreeNode(parm.getValue("CLASS_DESC",i),parm.getValue("CLASS_STYLE",i));
                //存储EMR_TREE表的CODE字段
                node.setID(parm.getValue("SEQ",i));
                //设置是否打开EMR,EMR_TREE表的EMR_CLASS字段
                node.setGroup(parm.getValue("CLASS_CODE",i));
                //存储EMR_TREE表的 SYS_PROGRAM字段
                node.setData(parm.getValue("SYS_PROGRAM",i));
                //add by lx
                node.setValue(parm.getValue("CLASS_CODE",i));
                //加入树根
                treeRoot.add(node);
                //其它节点放入对应节处；
                /**TParm allChildsParm = this.getAllChildsNode(parm.getValue("CLASS_CODE",i));
                int childRowCount = allChildsParm.getCount();
                this.messageBox("===childRowCount===="+childRowCount);
                for (int j = 0; j < rowCount; i++) {
                    TTreeNode childrenNode = new TTreeNode(allChildsParm.getValue("CLASS_DESC",j),parm.getValue("CLASS_STYLE",j));
                    childrenNode.setID(allChildsParm.getValue("SEQ",j));
                    childrenNode.setID(allChildsParm.getValue("CLASS_CODE",j));
                    childrenNode.setValue(allChildsParm.getValue("CLASS_CODE",i));

                    treeRoot.findNodeForID(allChildsParm.getValue(
                        "PARENT_CLASS_CODE", i)).add(childrenNode);
                }**/




                //查询子文件
                TParm childParm = this.getRootChildNodeData(parm.getValue("CLASS_CODE",i));
                int rowCldCount = childParm.getInt("ACTION","COUNT");
                for(int j=0;j<rowCldCount;j++){
                    TTreeNode nodeChlid = new TTreeNode(childParm.getValue("DESIGN_NAME",j),"4");
                    nodeChlid.setID(childParm.getValue("FILE_SEQ",j));
                    nodeChlid.setGroup(childParm.getValue("CLASS_CODE",j));
                    nodeChlid.setData(childParm.getRow(j));
                    node.add(nodeChlid);
                }
            }
            this.getTTree(TREE_NAME).update();
        }
        if(this.getSystemType().equals("ODO")){
            this.messageBox("ODO");
            ((TTextField)this.getComponent("IPD_NO")).setVisible(false);
            ((TLabel)this.getComponent("IPD_LAB")).setVisible(false);
            //拿到树根
            treeRoot.setText("门诊");
            treeRoot.setType("Root");
            treeRoot.removeAllChildren();
            //拿到节点数据
            TParm parm = this.getRootNodeData();
            if(parm.getInt("ACTION","COUNT")==0){
                this.messageBox("没有设置EMR模板！请检查数据库设置");
                return;
            }
            int rowCount = parm.getInt("ACTION","COUNT");
            for(int i=0;i<rowCount;i++){
                //加节点EMR_CLASS表中的STYLE字段
                TTreeNode node = new TTreeNode(parm.getValue("CLASS_DESC",i),parm.getValue("CLASS_STYLE",i));
                //存储EMR_TREE表的CODE字段
                node.setID(parm.getValue("SEQ",i));
                //设置是否打开EMR,EMR_TREE表的EMR_CLASS字段
                node.setGroup(parm.getValue("CLASS_CODE",i));
                //存储EMR_TREE表的 SYS_PROGRAM字段
                node.setData(parm.getValue("SYS_PROGRAM",i));
                //加入树根
                treeRoot.add(node);
                //查询子文件
                TParm childParm = this.getRootChildNodeData(parm.getValue("CLASS_CODE",i));
                int rowCldCount = childParm.getInt("ACTION","COUNT");
                for(int j=0;j<rowCldCount;j++){
                    TTreeNode nodeChlid = new TTreeNode(childParm.getValue("DESIGN_NAME",j),"4");
                    nodeChlid.setID(childParm.getValue("FILE_SEQ",j));
                    nodeChlid.setGroup(childParm.getValue("CLASS_CODE",j));
                    nodeChlid.setData(childParm.getRow(j));
                    node.add(nodeChlid);
                }
            }
            this.getTTree(TREE_NAME).update();
        }
    }

    /**
     *  结构化病例接口
     */
    public void emrInterface(){
        Object objOther = ( (TParm)this.getParameter()).getData("EMR_FILE_DATA");
        TParm action = new TParm();
        if (objOther != null) {
            TParm parm = (TParm)objOther;
            if(parm.getBoolean("FLG")){
                action.setData("EMR_TEMPLET_NAME",parm.getValue("SUBCLASS_DESC"));
                action.setData("EMR_FILE_NAME", parm.getValue("FILE_NAME"));
                action.setData("EMR_FILE_PATH", parm.getValue("FILE_PATH"));
                action.setData("EMR_FILE_DATA", parm);
//                this.getEmrFileData(action);
                this.getEmrFileTemp(action);
            }else{
                action.setData("EMR_TEMPLET_NAME",parm.getValue("SUBCLASS_DESC"));
                action.setData("EMR_FILE_NAME", parm.getValue("EMT_FILENAME"));
                action.setData("EMR_FILE_PATH", parm.getValue("TEMPLET_PATH"));
                action.setData("EMR_FILE_DATA", parm);
                this.getEmrFileTemp(action);
            }
        }
    }
    /**
     * 启动模版其他调用
     * @param parm TParm
     */
    public void getEmrFileTemp(TParm parm){
        //设置子面板数据
        this.setEmrChildParm(parm);
        //目录表第一个根目录FILESERVER
        String rootName = TIOM_FileServer.getRoot();
        //模板路径服务器
        String templetPath = TIOM_FileServer.getPath("EmrTemplet");
        //EMR文件名
        String emrFileName = parm.getValue("EMR_FILE_NAME");
        //要拿的模板文件路径
        String olinPath = parm.getValue("EMR_FILE_PATH");
        //本地保存的文件名及目录
        String emrLocal = TIOM_FileServer.getPath("EmrLocalTempFileName");
//        this.messageBox(rootName+templetPath+olinPath+"\\"+emrFileName+".emt");
        TSocket socket = TIOM_FileServer.getSocket();
        byte bytEmrData[] = TIOM_FileServer.readFile(socket,rootName+templetPath+olinPath+"\\"+emrFileName);
        if(bytEmrData==null){
            this.messageBox("模板文件不存在！");
            return;
        }
        File file = new File(emrLocal);
        file.mkdirs();
        try {
            FileTool.setByte(emrLocal+"EmrTemplet.emt", bytEmrData);
        }
        catch (IOException ex) {
            this.messageBox("保存临时文件失败！");
            ex.printStackTrace();
            return;
        }
        //设置宏
        setMicroField();
        emr.fileOpen(emrLocal+"EmrTemplet.emt");
        file = new File(emrLocal+"EmrTemplet.emt");
        file.delete();
        this.isReadOnly=false;
        this.fileNameOnly = parm.getValue("EMR_FILE_NAME");
    }
    /**
     * 拿到根节点数据
     * @return TParm
     */
    public TParm getRootNodeData() {
        TParm result = new TParm(this.getDBTool().select("SELECT B.CLASS_DESC,B.CLASS_STYLE,A.CLASS_CODE,A.SYS_PROGRAM,A.SEQ "+
            " FROM EMR_TREE A,EMR_CLASS B WHERE A.SYSTEM_CODE = '" +this.getSystemType() +
            "' AND A.CLASS_CODE=B.CLASS_CODE ORDER BY A.SEQ"));
        //System.out.println("返回数据" + result);
        return result;
    }
    /**
     * 拿到根节点数据
     * @return TParm
     */
    public TParm getRootChildNodeData(String classCode) {
        TParm result = new TParm(this.getDBTool().select("SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,B.SUBCLASS_DESC,A.CURRENT_USER,A.CREATOR_USER"+
            " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='"+this.getCaseNo()+"' AND A.CLASS_CODE='"+classCode+"' AND A.DISPOSAC_FLG<>'Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE"));
        /**System.out.println("==========sql test===========SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,B.SUBCLASS_DESC,A.CURRENT_USER,A.CREATOR_USER"+
            " FROM EMR_FILE_INDEX A,EMR_TEMPLET B WHERE A.CASE_NO='"+this.getCaseNo()+"' AND A.CLASS_CODE='"+classCode+"' AND A.DISPOSAC_FLG<>'Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE");

        System.out.println("返回数据" + result);**/
        return result;
    }

    /**
     * 得到父节点下所有子节点
     * @param parentClassCode String
     * @return TParm
     */
    private TParm getAllChildsNode(String parentClassCode){
         TParm result = new TParm();
         TParm childs = this.getChildsByParentNode(parentClassCode);
         if(childs!=null&&childs.getCount()>0){
             for(int i=0;i<childs.getCount();i++){
                 //CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE
                 result.addData("CLASS_CODE",childs.getData("CLASS_CODE",i));
                 result.addData("CLASS_DESC",childs.getData("CLASS_DESC",i));
                 result.addData("CLASS_STYLE",childs.getData("CLASS_STYLE",i));
                 result.addData("LEAF_FLG",childs.getData("LEAF_FLG",i));
                 result.addData("PARENT_CLASS_CODE",childs.getData("PARENT_CLASS_CODE",i));
                 result.addData("SEQ",childs.getData("SEQ",i));
             }
         }


         return result;
    }

    /**
     * add by lx
     * 通过父节点得到子节点;
     * @param parentClassCode String
     * @return TParm
     */
    private TParm getChildsByParentNode(String parentClassCode){
         TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ FROM EMR_CLASS WHERE PARENT_CLASS_CODE='"+parentClassCode+"' ORDER BY CLASS_CODE,SEQ"));

         //System.out.println("返回数据" + result);
         return result;
    }

    /**
     * 加载EMR
     */
    public void initEMR(){
        if(emr == null)
            emr = new EMRPad30();
        JPanel panel = (JPanel)callFunction("UI|PANEL|getThis");
        panel.setLayout(new BorderLayout());
        panel.add(emr);
    }
    /**
     * 点击树
     * @param parm Object
     */
    public void onTreeDoubled(Object parm){
        //三级检诊
        if("ODI".equals(this.getSystemType())){
            //是否有查看的权限
            if(!isCheckUserDr()){
                this.messageBox("您没有权限查看！");
                return;
            }
        }
        //System.out.println("object"+parm);
        TTreeNode node = (TTreeNode)parm;
        if(!node.getType().equals("4"))
            return;
        TParm action = new TParm();
        TParm emrParm = (TParm)node.getData();
        action.setData("EMR_TEMPLET_NAME", emrParm.getValue("SUBCLASS_DESC"));
        action.setData("EMR_FILE_NAME",emrParm.getValue("FILE_NAME"));
        action.setData("EMR_FILE_PATH",emrParm.getValue("FILE_PATH"));
        action.setData("EMR_FILE_DATA",emrParm);
        getEmrFileData(action);
    }
    /**
     *  得到树
     * @param tag String
     * @return TTree
     */
    public TTree getTTree(String tag){
        return (TTree)this.getComponent(tag);
    }
    /**
     * 新建病历(多个)
     */
    public void onCreatMenu(){
        openChildDialog();
    }
    /**
     * 打开子窗口
     */
    public void openChildDialog(){
        if(!checkSave())
             return;
        //模板类型
        String emrClass = this.getTTree(TREE_NAME).getSelectNode().getGroup();
        String nodeName = this.getTTree(TREE_NAME).getSelectNode().getText();
        Object programObj = this.getTTree(TREE_NAME).getSelectNode().getData();
        String emrType = this.getTTree(TREE_NAME).getSelectNode().getType();
        //如果是调用HIS程序则这样处理
        if(programObj.toString().length()!=0){
            return;
        }
        TParm parm = new TParm();
        if (this.getSystemType().equals("ODI")) {
            parm = new TParm(this.getDBTool().select(
                "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + emrClass +
                "' AND IPD_FLG='Y' ORDER BY SEQ"));
            parm.setData("SYSTEM_CODE","ODI");
            parm.setData("NODE_NAME",nodeName);
            parm.setData("TYPE",emrType);
        }
        if (this.getSystemType().equals("ODO")) {
            parm = new TParm(this.getDBTool().select(
                "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH,SEQ,DEPT_CODE,EMT_FILENAME FROM EMR_TEMPLET WHERE CLASS_CODE='" + emrClass +
                "' AND OPD_FLG='Y' ORDER BY SEQ"));
            parm.setData("SYSTEM_CODE","ODO");
            parm.setData("NODE_NAME",nodeName);
            parm.setData("TYPE",emrType);
        }
        Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRChildUI.x",parm);
        if(obj==null|| !(obj instanceof TParm)){
            return;
        }
        TParm action = (TParm)obj;
        //设置子面板数据
//        this.messageBox_("子窗口调用"+action);
        this.setEmrChildParm(action);
        //目录表第一个根目录FILESERVER
        String rootName = TIOM_FileServer.getRoot();
        //模板路径服务器
        String templetPath = TIOM_FileServer.getPath("EmrTemplet");
        //EMR文件名
        String emrFileName = action.getValue("EMR_FILE_NAME");
        //要拿的模板文件路径
        String olinPath = action.getValue("EMR_FILE_PATH");
        //本地保存的文件名及目录
        String emrLocal = TIOM_FileServer.getPath("EmrLocalTempFileName");
//        this.messageBox(rootName+templetPath+olinPath+"\\"+emrFileName+".emt");
        TSocket socket = TIOM_FileServer.getSocket();
        byte bytEmrData[] = TIOM_FileServer.readFile(socket,rootName+templetPath+olinPath+"\\"+emrFileName);
        if(bytEmrData==null){
            this.messageBox("模板文件不存在！");
            return;
        }
        File file = new File(emrLocal);
        file.mkdirs();
        try {
            FileTool.setByte(emrLocal+"EmrTemplet.emt", bytEmrData);
        }
        catch (IOException ex) {
            this.messageBox("保存临时文件失败！");
            ex.printStackTrace();
            return;
        }
        //设置宏
        setMicroField();
        emr.fileOpen(emrLocal+"EmrTemplet.emt");
        file = new File(emrLocal+"EmrTemplet.emt");
        file.delete();
        this.isReadOnly=false;
        this.fileNameOnly = emrFileName;
        //模版类型
        this.setEmrTypeCode(emrType);
    }
    /**
     * 设置宏
     */
    public void setMicroField(){
        Map map = this.getDBTool().select("SELECT A.PAT_NAME AS 姓名,A.IDNO AS 身份证号,TO_CHAR(A.BIRTH_DATE,'YYYY-MM-DD') AS 出生日期,"+
                                          " D.CTZ_DESC AS 付款方式,A.TEL_HOME AS 家庭电话,A.SEX_CODE AS 性别,A.CONTACTS_NAME AS 联系人,"+
                                          " TO_CHAR(A.FIRST_ADM_DATE,'YYYY-MM-DD') AS 初诊日期,A.CELL_PHONE AS 手机,"+
                                          " A.ADDRESS AS 家庭住址,A.OCC_CODE AS 职业,A.COMPANY_DESC AS 工作单位,"+
                                          " A.RELATION_CODE AS 与患者关系,A.CONTACTS_TEL AS 单位电话,"+
                                          " A.SPECIES_CODE AS 民族,A.HEIGHT AS 身高,A.WEIGHT AS 体重,A.RESID_POST_CODE AS 邮编,A.MARRIAGE_CODE AS 婚姻状况"+
                                          " FROM SYS_PATINFO A,SYS_CTZ D"+
                                          " WHERE A.CTZ1_CODE=D.CTZ_CODE(+)"+
                                          " AND A.MR_NO='"+this.getMrNo()+"'");
        TParm parm = new TParm(map);
        if(parm.getErrCode()<0){
            this.messageBox("取得病人基本资料失败！");
            return;
        }
        String sbithDay="";
        StringTokenizer stk = new StringTokenizer(parm.getValue(
        "出生日期",0), "-");
        while (stk.hasMoreTokens())
            sbithDay += stk.nextToken();
        parm.addData("就诊号",this.getCaseNo());
        parm.addData("病案号",this.getMrNo());
        parm.addData("病案号1",this.getMrNo());
        parm.addData("科室",this.getDeptDesc(Operator.getDept()));
        parm.addData("操作者",Operator.getName());
        if("ODI".equals(this.getSystemType())){
            parm.addData("住院",this.getSystemType());
        }
        if("ODO".equals(this.getSystemType())){
            parm.addData("门诊",this.getSystemType());
        }
        String names[] = parm.getNames();
        for(int i=0;i<names.length;i++){
            if("出生日期".equals(names[i])){
                emr.setMicroField(names[i],sbithDay);
                continue;
            }
            if("性别".equals(names[i])){
              emr.setMicroField(names[i],getDictionary("SYS_SEX",parm.getValue(names[i],0)));
              continue;
            }
            if("婚姻状况".equals(names[i])){
              emr.setMicroField(names[i],getDictionary("SYS_MARRIAGE",parm.getValue(names[i],0)));
              continue;
            }
            if("职业".equals(names[i])){
              emr.setMicroField(names[i],getDictionary("SYS_OCCUPATION",parm.getValue(names[i],0)));
              continue;
            }
            if("与患者关系".equals(names[i])){
              emr.setMicroField(names[i],getDictionary("SYS_RELATIONSHIP",parm.getValue(names[i],0)));
              continue;
            }
            if("民族".equals(names[i])){
              emr.setMicroField(names[i],getDictionary("SYS_SPECIES",parm.getValue(names[i],0)));
              continue;
            }
            emr.setMicroField(names[i],parm.getValue(names[i],0));
        }
        //更新宏
        emr.updateMicroField(true);
    }
    /**
     * 拿到字典信息
     * @param groupId String
     * @param id String
     * @return String
     */
    public String getDictionary(String groupId,String id){
        String result="";
        TParm parm = new TParm(this.getDBTool().select("SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"+groupId+"' AND ID='"+id+"'"));
        result = parm.getValue("CHN_DESC",0);
        return result;
    }
    /**
     * 拿到科室
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode){
        TParm parm = new TParm(this.getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'"));
        return parm.getValue("DEPT_CHN_DESC",0);
    }
    /**
     * 拿到EMR文件
     * @param parm TParm
     */
    public void getEmrFileData(TParm parm){
        this.setEmrChildParm(parm);
        //要拿的模板文件路径
        String olinPath = parm.getValue("EMR_FILE_PATH");
        //本地保存的文件名及目录
        String emrLocal = TIOM_FileServer.getPath("EmrLocalTempFileName");
        TSocket socket = TIOM_FileServer.getSocket();
        byte bytEmrData[] = TIOM_FileServer.readFile(socket,olinPath);
        if(bytEmrData==null){
            this.messageBox("模板文件不存在！");
            return;
        }
        File file = new File(emrLocal);
        file.mkdirs();
        try {
            FileTool.setByte(emrLocal+"EmrTemplet.emr", bytEmrData);
        }
        catch (IOException ex) {
            this.messageBox("保存临时文件失败！");
            ex.printStackTrace();
            return;
        }
        //设置宏
        setMicroField();
        emr.fileOpen(emrLocal+"EmrTemplet.emr");
        file = new File(emrLocal+"EmrTemplet.emr");
        file.delete();
        //设置EMR文件只读
        emr.setDocumentMode(emr.DOCUMENT_MODE_READ_ONLY);
        this.isReadOnly = true;
        this.fileNameOnly = "";
    }
    /**
     * 保存
     */
    public void onSave(){
        TParm parm = this.getEmrChildParm();
        if (parm.getData("EMR_FILE_DATA") == null) {
            this.messageBox("没有要保存的文件！");
            return;
        }
        //目录表第一个根目录FILESERVER
        String rootName = TIOM_FileServer.getRoot();
        //本地保存的文件名及目录
        String emrLocal = TIOM_FileServer.getPath("EmrLocalTempFileName");
        File file = new File(emrLocal);
        file.mkdirs();
        emr.fileSaveAs(emrLocal+"EmrTemplet.emt",0);
        //文件服务器保存的路径
        String emrFileServerPath = TIOM_FileServer.getPath("EmrData");
        byte[] fileData=null;
        TSocket socket = TIOM_FileServer.getSocket();
        try {
            fileData = FileTool.getByte(emrLocal+"EmrTemplet.emt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        if(fileData==null){
           return;
        }
        //拿到EMR文件信息
        TParm emrParm = this.getFileServerEmrName();
//        this.messageBox_(emrParm);
        //拿到EMR文件名
        String emrName = emrParm.getValue("FILE_NAME");
//        this.messageBox(emrName);
        //设置EMR路径
        String serverPath = rootName+emrFileServerPath+this.getAdmYear()+"\\"+this.admMouth+"\\"+this.getMrNo()+"\\"+emrName;
        //放入文件服务器
        boolean flg = TIOM_FileServer.writeFile(socket,serverPath,fileData);
        if(flg){
            if(this.saveEmrFile(emrParm,serverPath)){
                this.messageBox("保存成功！");
//                this.messageBox_(emrParm);
                TParm action = new TParm();
                action.setData("EMR_TEMPLET_NAME",emrParm.getValue("EMR_TEMPLET_NAME"));
                action.setData("EMR_FILE_NAME", emrParm.getValue("FILE_NAME"));
                action.setData("EMR_FILE_PATH", serverPath);
                action.setData("EMR_FILE_DATA", emrParm);
                //加载EMR文件
                this.getEmrFileData(action);
                //加载树
                this.loadTree();
            }
            else{
                this.messageBox("保存失败！");
                TIOM_FileServer.deleteFile(socket,serverPath);
            }
        }else{
            this.messageBox("保存失败！");
        }
        file = new File(emrLocal+"EmrTemplet.emt");
        file.delete();
        //设置EMR文件只读
        emr.setDocumentMode(emr.DOCUMENT_MODE_READ_ONLY);
        this.isReadOnly = true;
        this.fileNameOnly = "";
    }
    /**
     * 保存EMR文件
     * @param parm TParm
     */
    public boolean saveEmrFile(TParm parm,String filePath){
        boolean falg = true;
        parm.setData("CASE_NO",this.getCaseNo());
        parm.setData("MR_NO",this.getMrNo());
        parm.setData("IPD_NO",this.getIpdNo());
        parm.setData("FILE_PATH",filePath);
        parm.setData("DESIGN_NAME",parm.getValue("FILE_NAME"));
        parm.setData("DISPOSAC_FLG","N");
        parm.setData("CURRENT_USER",Operator.getID());
        parm.setData("CREATOR_USER",Operator.getID());
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_DATE",this.getDBTool().getDBTime());
        parm.setData("OPT_TERM",Operator.getIP());
        TParm result = TIOM_AppServer.executeAction(actionName,"saveEmrFile", parm);
        if(result.getErrCode()<0)
            falg = false;
        return falg;
    }
    /**
     * 拿到文件服务器路径
     * @param rootPath String
     * @param fileServerPath String
     * @return String
     */
    public TParm getFileServerEmrName(){
        TParm emrParm =new TParm();
        String emrName = "";
        String indexStr = "01";
        TParm childParm = this.getEmrChildParm();
//        this.messageBox_("调用"+childParm);
        TParm emrFileParm = (TParm)childParm.getData("EMR_FILE_DATA");
        String templetName = childParm.getValue("EMR_TEMPLET_NAME");
        TParm action = new TParm(this.getDBTool().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+this.getCaseNo()+"'"));
        int index = action.getInt("MAXFILENO",0);
        if(index<10){
           indexStr = "0"+index;
        }else{
            indexStr = ""+index;
        }
        emrName = this.getCaseNo()+"_"+templetName+"_"+indexStr+".emr";
        emrParm.setData("FILE_SEQ",indexStr);
        emrParm.setData("FILE_NAME",emrName);
        emrParm.setData("CLASS_CODE",emrFileParm.getData("CLASS_CODE"));
        emrParm.setData("SUBCLASS_CODE",emrFileParm.getData("SUBCLASS_CODE"));
        emrParm.setData("EMR_TEMPLET_NAME",templetName);
        return emrParm;
    }
    /**
     * 窗口关闭事件
     * @return boolean
     */
    public boolean onClosing(){
         if(!checkSave())
             return false;
         emr.closeOCX();
         return true;
     }
     /**
      * 检核是否保存
      * @return boolean
      */
     public boolean checkSave(){
         if(emr==null)
             return true;
         if(this.fileNameOnly.length()>0&&!this.isReadOnly){
             if (messageBox("提示信息", "是否保存"+this.fileNameOnly+"文件?", this.YES_NO_OPTION) != 0){

                 TParm parm = (TParm)this.getEmrChildParm().getData("EMR_FILE_DATA");
                 if(parm.getValue("FILE_SEQ").length()==0)
                     return true;
                 parm.setData("OPT_USER",Operator.getID());
                 parm.setData("OPT_TERM",Operator.getIP());
                 parm.setData("CURRENT_USER","");
                 TParm result = TIOM_AppServer.executeAction(actionName,"updateEmrCurrentUser",parm);
                 if(result.getErrCode()<0)
                     return false;
                 return true;
             }
             else{
                 //删除前一个文件
                 TParm childParm = (TParm)this.getEmrChildParm().getData(
                     "EMR_FILE_DATA");
                 //System.out.println("FILE_SEQ" + childParm.getValue("FILE_SEQ"));
                 //System.out.println("CASE_NO" + childParm.getValue("CASE_NO"));
                 childParm.setData("OPT_USER", Operator.getID());
                 childParm.setData("OPT_TERM", Operator.getIP());
                 childParm.setData("CURRENT_USER", "");
                 TParm result = TIOM_AppServer.executeAction(actionName,"updateEmrFile", childParm);

                 //保存
                 this.onSave();
                 //保存后的编辑人清空
                 TParm parm1 = (TParm)this.getEmrChildParm().getData(
                     "EMR_FILE_DATA");
                 parm1.setData("OPT_USER", Operator.getID());
                 parm1.setData("OPT_TERM", Operator.getIP());
                 parm1.setData("CURRENT_USER", "");
//                 this.messageBox_("后"+parm1);
                 TParm result1 = TIOM_AppServer.executeAction(actionName,
                     "updateEmrCurrentUser", parm1);
                 if (result1.getErrCode() < 0)
                     return false;
             }
         }
         return true;
     }
    /**
     * 打开病历(唯一)
     */
    public void onOpenMenu(){
        openChildDialog();
    }
    /**
     * 新增病历(可以追加)
     */
    public void onAddMenu(){
        openChildDialog();
    }
    /**
     * 删除
     */
    public void onDelete(){
        TParm parm = this.getEmrChildParm();
        if(parm.getData("EMR_FILE_DATA")==null){
            this.messageBox("没有要删除的文件！");
            return;
        }
        TParm childParm = (TParm)parm.getData("EMR_FILE_DATA");
        String creatFileUser = childParm.getValue("CREATOR_USER");
        //权限检核
        if(!creatFileUser.equals(Operator.getID())){
            this.messageBox("您没有权限删除此文件");
            return;
        }
        //System.out.println("FILE_SEQ"+childParm.getValue("FILE_SEQ"));
        //System.out.println("CASE_NO"+childParm.getValue("CASE_NO"));
        childParm.setData("OPT_USER",Operator.getID());
        childParm.setData("OPT_TERM",Operator.getIP());
        childParm.setData("CURRENT_USER","");
        TParm result = TIOM_AppServer.executeAction(actionName,"updateEmrFile",childParm);
        if(result.getErrCode()<0){
            this.messageBox("删除失败！");
            return;
        }
        this.messageBox("删除成功！");
        emr.fileNew();
        //重新加载树
        this.loadTree();
        this.isReadOnly=true;
        this.fileNameOnly = "";
    }
    public static void main(String args[])
    {
        /*JFrame f = new JFrame();
        f.getRootPane().add(new EMRPad30());
        f.getRootPane().setLayout(new BorderLayout());
        f.setVisible(true);*/
        JavaHisDebug.TBuilder();

    }
    /**
     * 判断是否有人在编辑
     * @return boolean
     */
    public boolean isEdit(){
        boolean falg = true;
        TParm action = (TParm)this.getEmrChildParm().getData("EMR_FILE_DATA");
        String caseNo = action.getValue("CASE_NO");
        String fileSeq = action.getValue("FILE_SEQ");
        TParm parm = new TParm(this.getDBTool().select("SELECT CURRENT_USER FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND FILE_SEQ='"+fileSeq+"'"));
//        this.messageBox_(parm);
        String currentUser = parm.getValue("CURRENT_USER",0);
        if(Operator.getID().equals(currentUser)||currentUser.length()==0)
            falg = false;
        return falg;
    }
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
        return TJDODBTool.getInstance();
    }
    /**
     * 得到系统类别
     * @return String
     */
    public String getSystemType() {
        return systemType;
    }

    public String getIpdNo() {
        return ipdNo;
    }

    public String getMrNo() {
        return mrNo;
    }

    public String getPatName() {
        return patName;
    }

    public String getAdmMouth() {
        return admMouth;
    }

    public String getAdmYear() {
        return admYear;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public TParm getEmrChildParm() {
        return emrChildParm;
    }

    public String getDirectorDrCode() {
        return directorDrCode;
    }

    public String getAttendDrCode() {
        return attendDrCode;
    }

    public String getVsDrCode() {
        return vsDrCode;
    }

    public String getEmrTypeCode() {
        return emrTypeCode;
    }

    /**
     * 得到系统类别
     * @param systemType String
     */
    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public void setIpdNo(String ipdNo) {
        this.ipdNo = ipdNo;
    }

    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public void setAdmMouth(String admMouth) {
        this.admMouth = admMouth;
    }

    public void setAdmYear(String admYear) {
        this.admYear = admYear;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public void setEmrChildParm(TParm emrChildParm) {
        this.emrChildParm = emrChildParm;
    }

    public void setDirectorDrCode(String directorDrCode) {
        this.directorDrCode = directorDrCode;
    }

    public void setAttendDrCode(String attendDrCode) {
        this.attendDrCode = attendDrCode;
    }

    public void setVsDrCode(String vsDrCode) {
        this.vsDrCode = vsDrCode;
    }

    public void setEmrTypeCode(String emrTypeCode) {

        this.emrTypeCode = emrTypeCode;
    }
}
