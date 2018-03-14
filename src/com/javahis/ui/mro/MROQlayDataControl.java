package com.javahis.ui.mro;

import java.sql.Timestamp;
import jdo.sys.SYSRuleTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import jdo.mro.MROQlayControlMTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.ExportExcelUtil;
import jdo.mro.MROChrtvetstdTool;



/**
 * <p>Title: 手动质控</p>
 *
 * <p>Description: 手动质控</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110730
 * @version 1.0
 */
public class MROQlayDataControl extends TControl {
    private String MR_NO; //病患病案号
    private String CASE_NO; //病患就诊序号
    private String type = "";//参数显示类型：在院、出院
    private String PAT_NAME; //病患名称
    private String OPEN_USER; //操作人姓名
    private String IPD_NO;//住院号
    private Timestamp ADM_DATE;//就诊时间
    private String DEPT_CODE;//科室 add by wanglong 20121127
    private String STATION_CODE;//病区 add by wanglong 20121127
    private String OPT_USER; //操作人id
    private String TYPERESULT; //提交字段
    private String VS_DR_CODE; //经治医生  add by wanglong 20121105
    TParm data;//查询所有的MROChrtvetstd表数据
    int selectRow = -1;//当前选择的行数
    private TParm parmValue; //左侧树所有分支在右侧表格中的全部数据
    private TParm result; //查询该就诊病患的所有扣分项目
    private TTable table;
    /**
     * 树根
     */
    private TTreeNode treeRoot;
    /**
     * 编号规则类别工具
     */
    SYSRuleTool ruleTool;
    //添加数据之后显示此类型的最大编号父节点
    private String id = null;
    TParm  selParm ;//所有子节点的默认值
    /**
     * 树的数据放入datastore用于对树的数据管理
     */
    TDataStore treeDataStore = new TDataStore();
    TParm parmData = new TParm();//add by wanglong 20130819

    public void onInit() {
        super.onInit();
        TFrame tf=(TFrame)this.getComponent("UI");//add by wanglong 20121129
        tf.setSize(1104, 758);//当使用openWindow()调用此界面时，必须这样重新设置窗口大小，否则显示不全。
        Object obj = this.getParameter();
        if (obj != null) {
            parmData = (TParm) obj;
            MR_NO = parmData.getValue("MR_NO"); //获取病案号
            CASE_NO = parmData.getValue("CASE_NO"); //获取就诊序号
            PAT_NAME = parmData.getValue("PAT_NAME"); //获取就诊序号
            type = parmData.getValue("TYPE"); //调用权限
            OPEN_USER = parmData.getValue("OPEN_USER"); //操作人姓名
            OPT_USER = parmData.getValue("OPT_USER"); //操作人id
            IPD_NO = parmData.getValue("IPD_NO"); //住院号
            ADM_DATE = parmData.getTimestamp("ADM_DATE"); //就诊时间
            DEPT_CODE = parmData.getValue("DEPT_CODE"); //科室
            STATION_CODE = parmData.getValue("STATION_CODE"); //住院号
            TParm result = MROQlayControlMTool.getInstance().queryQlayControlSUM(parmData);
            if (result.getErrCode() < 0) {
                this.messageBox(result.getErrText());
                this.closeWindow();
                return;
            }
//            TYPERESULT = parmData.getValue("TYPERESULT"); //提交字段
            TYPERESULT = result.getValue("TYPERESULT", 0); //提交字段
            VS_DR_CODE = parmData.getValue("VS_DR_CODE"); //经治医生  add by wanglong 20121105
            data = MROChrtvetstdTool.getInstance().selectdata(""); //查询所有的MROChrtvetstd表数据
            parmValue = new TParm(); //提交数据时的所有数据
            showQlayM();//为result赋值
            getShowTParm();//将已经质控的数据放到表格中，显示所有的质控项目             
            if ("1".equals(TYPERESULT)) {// add by wanglong 20131111
                ((TCheckBox) getComponent("TYPERESULT")).setSelected(true);
                this.callFunction("UI|SELECTQUERYSTATUS|setEnabled", false);
                this.callFunction("UI|SELECTSTATUS|setEnabled", false);
                table = (TTable) callFunction("UI|Table|getThis");
                table.setLockColumns("all");
            }else{
                this.callFunction("UI|SELECTQUERYSTATUS|setEnabled", true);
                this.callFunction("UI|SELECTSTATUS|setEnabled", true);
                table = (TTable) callFunction("UI|Table|getThis");
                table.setLockColumns("0,1,3,4,5,6,7");
            }
            if (MR_NO.trim().length() > 0 && CASE_NO.trim().length() > 0) {
                ((TTable) getComponent("Table")).addEventListener("Table->" + TTableEvent.CLICKED, 
                        this, "onTableClicked");
                onInitSelectTree();// 初始化树
                addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");// 给tree添加监听事件
                table = (TTable) callFunction("UI|Table|getThis");
                //添加表格table值改变事件:修改状态、检核结果列值
                callFunction("UI|Table|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this,
                             "onTableCheckBoxClicked"); // 给TABLEDEPT中的CHECKBOX添加侦听事件
                this.addEventListener("Table->" + TTableEvent.CHANGE_VALUE,
                      "onTableChargeValue");//添加第二个表格table值改变事件
                onCreatTree();// 种树
                this.setValue("MR_NO", MR_NO);
                this.setValue("IPD_NO", IPD_NO);
                this.setValue("PAT_NAME", PAT_NAME);
                TParm parmValueM = MROQlayControlMTool.getInstance().queryQlayControlSUM(parmData);//查总分
                this.setValue("RESULTPRICE", parmValueM.getDouble("SUMSCODE",0)+"");
            }
        }
    }
    
    /**
     * TABLE_VARIANCE表格监听事件：值改变事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableChargeValue(Object obj) {
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
        TTableNode node = (TTableNode) obj;
        if (node == null) return false;
        //如果改变的节点数据和原来的数据相同就不改任何数据
        if (node.getValue().equals(node.getOldValue())) return false;
        //拿到table上的parmmap的列名
        String columnName = node.getTable().getParmMap(node.getColumn());
        //得到改变的行
        int row = node.getRow();
        //拿到当前改变后的数据
        String value = "" + node.getValue();
        //如果是名称改变了拼音1自动带出,并且原因说明不能为空
        int rows = getRow("EXAMINE_CODE", table.getParmValue().getValue("EXAMINE_CODE", row));
       // TParm parm=table.getParmValue();
        //人工质控累计项
        if ("DEDUCT_SCORECOUNT".equals(columnName)) {
            if ("Y".equals(table.getParmValue().getValue("FLG", row))) {
                this.messageBox("自动质控项不可以修改");
//                parm.setDataN(columnName,row,1);
//                table.setParmValue(parm);
//                table.acceptText();
            } else
                parmValue.setData("DEDUCT_SCORECOUNT", rows, value);
        }
      //  onRadionClick();
        return false;
    }
    
    /**
     * 查找所有的自动质控信息
     */
    public void showQlayM() {
        String sql = "SELECT DISTINCT A.CASE_NO, A.MR_NO, A.IPD_NO,"//modify by wanglong 20121120
                    + " A.EXAMINE_CODE,B.EXAMINE_DESC,A.EXAMINE_DATE,A.QUERYSTATUS,A.STATUS,B.SCORE,"
                    + " A.CHECK_USER,A.CHECK_DATE,A.CHECK_RANGE, C.USER_NAME,B.DESCRIPTION,B.URG_FLG,B.TYPE_CODE,B.CHECK_FLG, "
                    + " D.VS_CODE,D.DEDUCT_SCORECOUNT,D.REPLY_DTTM,D.REPLY_DR_CODE,D.REPLY_REMK "//modify by wanglong 20131111
                    + " FROM MRO_QLAYCONTROLM A, MRO_CHRTVETSTD B, SYS_OPERATOR C,MRO_CHRTVETREC D "
                    + " WHERE A.EXAMINE_CODE = B.EXAMINE_CODE "
                    + " AND A.CASE_NO = D.CASE_NO    "
                    + " AND A.EXAMINE_CODE = D.EXAMINE_CODE "
                    + " AND A.CHECK_USER = C.USER_ID "
                    + " AND A.CASE_NO = '" + CASE_NO + "' ORDER BY A.CHECK_DATE";
      //  System.out.println("====================查找所有的自动质控信息=================="+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
    }

    /**
     * 初始化树
     */
    public void onInitSelectTree() {
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");// 得到树根
        if (treeRoot == null)
            return;
        if (type.equals("TYPE_IN"))
            treeRoot.setText("在院");// 给根节点添加文字显示
        else
            treeRoot.setText("出院");
        treeRoot.setType("Root");// 给根节点赋tag
        treeRoot.setID("");// 设置根节点的id
        treeRoot.removeAllChildren();// 清空所有节点的内容
        callMessage("UI|TREE|update");// 调用树点初始化方法
    }

    /**
     * 初始化树上的节点
     */
    public void onCreatTree() {
        StringBuffer sql = new StringBuffer();
        //显示审核标准中子节点
        sql.append("SELECT RULE_TYPE, CATEGORY_CODE,"
                   + " CATEGORY_CHN_DESC, CATEGORY_ENG_DESC,"
                   + " PY1, PY2, SEQ, DESCRIPTION, DETAIL_FLG,"
                   + " OPT_USER, OPT_DATE, OPT_TERM"
                   + " FROM SYS_CATEGORY WHERE RULE_TYPE='MRO_CHRTVETSTD' AND LENGTH(CATEGORY_CODE)=3 ");
        if (type.equals("TYPE_IN"))
            sql.append(" AND CATEGORY_CODE LIKE '21%'");//显示在院的子节点数据,按照编码规则显示，必须住院为2开头的数据
        else
            sql.append(" AND CATEGORY_CODE LIKE '22%'");//显示出院的子节点数据
     //   System.out.println("=============初始化树上的节点==============="+sql);
        selParm = new TParm(TJDODBTool.getInstance().select(sql.toString()));
        treeDataStore.setSQL(sql.toString());// 给dataStore赋值
        if (treeDataStore.retrieve() <= 0)// 如果从dataStore中拿到的数据小于0
            return;
        ruleTool = new SYSRuleTool("MRO_CHRTVETSTD"); // 过滤数据,是质量考评标准数据
        if (ruleTool.isLoad()) { // 给树篡节点参数:datastore，节点代码,节点显示文字,,节点排序
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore, "CATEGORY_CODE", "CATEGORY_CHN_DESC", "Path", "SEQ");
            for (int i = 0; i < node.length; i++) { // 循环给树安插节点
                treeRoot.addSeq(node[i]);
            }
        }
        TTree tree = (TTree) callMessage("UI|TREE|getThis");// 得到界面上的树对象
        tree.update();// 更新树
        tree.setSelectNode(treeRoot);// 设置树的默认选中节点
    }

    /**
     * 单击树
     *
     * @param parm Object
     */
    public void onTreeClicked(Object parm) { // 新增按钮不能用
        TTreeNode node = (TTreeNode) parm;// 得到点击树的节点对象
        ((TRadioButton)this.getComponent("RADIONSUM")).setSelected(true); //全部单选按钮选择
        if (node == null)
            return;
        // 得到table对象
        if (node.getType().equals("Root")) {// 如果点击的是树的根结点
        } else { // 如果点的不是根结点
            id = node.getID();// 拿到当前选中的节点的id值
            String scode="";
            for(int i=0;i<selParm.getCount();i++ ){
                if(id.equals(selParm.getValue("CATEGORY_CODE",i))){
                    scode=selParm.getValue("DESCRIPTION",i);//获得当前节点的分数值
                    break;
                }
            }
            double score = 0;
            for (int i = 0; i < parmValue.getCount(); i++) {
                if (parmValue.getValue("EXAMINE_CODE", i).contains(id)
                        && parmValue.getValue("QUERYSTATUS", i).equals("1")
                        && parmValue.getValue("STATUS", i).equals("0")) {
                    score += parmValue.getDouble("SCORE", i);
                }
            }
            score = StringTool.round(score, 2);
            this.setValue("SUMPRICE", scode);//默认分值显示
            this.setValue("SUBPRICE", "" + score);//当前项目扣分显示
            // 根据过滤条件过滤Tablet上的数据
            TParm parmTemp= showTParm(id,"-1");//筛选数据显示
            table.setParmValue(parmTemp);
        }
    }

    /**
     * 参数方法
     */
    private void getShowTParm() {
        String date = StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss");
        for (int i = 0; i < data.getCount(); i++) {//data记录所有的质控项目
            boolean isAuto = false;//是否为自动质控项
            for (int j = 0; j < result.getCount(); j++) {//result为当前病患的所有扣分项目
                if (data.getValue("EXAMINE_CODE", i).equals(result.getValue("EXAMINE_CODE", j))) {
                    //自动质控的数据和已经添加的项目数据
                    parmValue.addData("EXAMINE_DESC", result.getValue("EXAMINE_DESC", j)); //审核项目名称
                    parmValue.addData("EXAMINE_CODE", result.getValue("EXAMINE_CODE", j)); //审核编号   
                    parmValue.addData("EXAMINE_DATE", result.getValue("EXAMINE_DATE", j)); //审核时间
                    parmValue.addData("MR_NO", result.getValue("MR_NO", j)); //病案号
                    parmValue.addData("CASE_NO", result.getValue("CASE_NO", j)); //就诊号
                    parmValue.addData("IPD_NO", result.getValue("IPD_NO", j)); //住院号
                    parmValue.addData("USER_NAME", result.getValue("USER_NAME", j)); //操作人
                    parmValue.addData("CHECK_RANGE", result.getValue("CHECK_RANGE", j)); //审核范围
                    parmValue.addData("CHECK_USER", result.getValue("CHECK_USER", j)); //创建人
                    parmValue.addData("CHECK_DATE", StringTool.getString(result.getTimestamp("CHECK_DATE", j), "yyyy/MM/dd HH:mm:ss"));
                    parmValue.addData("SCORE", result.getData("SCORE", j)); //分数
                    parmValue.addData("DEDUCT_SCORECOUNT", result.getInt("DEDUCT_SCORECOUNT", j)); //项数统计
                    parmValue.addData("QUERYSTATUS", result.getValue("QUERYSTATUS", j)); //自动指控的数据显示已查
                    parmValue.addData("STATUS", result.getValue("STATUS", j)); //检核结果：通过、未通过
                    parmValue.addData("FLG", result.getValue("CHECK_FLG", j)); //筛选显示：区分自动指控、人工指控
                    parmValue.addData("VS_CODE", result.getValue("VS_CODE", j)); //经治医生
                    //add by wanglong 20131111
                    parmValue.addData("REPLY_DTTM", result.getData("REPLY_DTTM", j)); //回复时间
                    parmValue.addData("REPLY_DR_CODE", result.getValue("REPLY_DR_CODE", j)); //回复医师
                    parmValue.addData("REPLY_REMK", result.getValue("REPLY_REMK", j)); //回复备注
                    //add end
                    parmValue.addData("ISTRUE", "1"); //"修改"操作标记
                    if (result.getValue("QUERYSTATUS", j).equals("1"))
                        parmValue.addData("QUERYSTATUS_FLG", "1"); //勾选状态显示已查
                    else
                        parmValue.addData("QUERYSTATUS_FLG", "0");
                    if (result.getValue("STATUS", j).equals("1"))
                        parmValue.addData("STATUS_FLG", "1"); //勾选检核结果显示通过
                    else
                        parmValue.addData("STATUS_FLG", "0");
                    parmValue.addData("URG_FLG",null == result.getValue("URG_FLG", j)?"N":result.getValue("URG_FLG", j)); //急件否注记
                    isAuto = true;
                    break;
                }
            }
            if (!isAuto) {
                //人工质控的数据
                parmValue.addData("EXAMINE_DESC", data.getValue("EXAMINE_DESC", i)); //审核项目名称
                parmValue.addData("EXAMINE_CODE", data.getValue("EXAMINE_CODE", i)); //审核编号
                parmValue.addData("EXAMINE_DATE", StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss")); //审核时间
                parmValue.addData("MR_NO", MR_NO); //病案号
                parmValue.addData("CASE_NO", CASE_NO); //就诊号
                parmValue.addData("IPD_NO", IPD_NO); //住院号
                parmValue.addData("USER_NAME", OPEN_USER); //医生名称
                parmValue.addData("CHECK_RANGE", data.getValue("CHECK_RANGE", i)); //审核范围
                parmValue.addData("CHECK_USER", OPT_USER); //创建人
                parmValue.addData("CHECK_DATE", date);
                parmValue.addData("SCORE", data.getValue("SCORE", i)); //分数
                parmValue.addData("DEDUCT_SCORECOUNT", "1"); //项数统计
                parmValue.addData("QUERYSTATUS", "0"); //显示：未查、已查
                parmValue.addData("STATUS", "0"); //显示：通过、未通过
                parmValue.addData("FLG", data.getValue("CHECK_FLG", i)); //筛选显示：区分自动指控、人工指控
                parmValue.addData("VS_CODE", VS_DR_CODE); //经治医生
                //add by wanglong 20131111
                parmValue.addData("REPLY_DTTM", ""); //回复时间
                parmValue.addData("REPLY_DR_CODE", ""); //回复医师
                parmValue.addData("REPLY_REMK", ""); //回复备注
                //add end
                parmValue.addData("ISTRUE", "0"); //"添加"操作标记
                parmValue.addData("QUERYSTATUS_FLG", "0"); //查询复选框
                parmValue.addData("STATUS_FLG", "0"); //检核复选框
                parmValue.addData("URG_FLG", null==data.getValue("URG_FLG", i)?"N":data.getValue("URG_FLG", i)); //急件否注记
            }
            parmValue.addData("OPT_USER", Operator.getID()); //操作人
            parmValue.addData("OPT_TERM", Operator.getIP()); //操作时间
//            parmValue.addData("VS_DR_CODE", VS_DR_CODE); //经治医生 add by wanglong 20121105
        }
        parmValue.setCount(data.getCount());
//        System.out.println("============所有数据======================="+parmValue);
    }

    /**
     * 筛选显示：自动质控、人工质控、全部筛选
     */
    public void onRadionClick() {
        TParm parm = null;
        if (((TRadioButton)this.getComponent("RADIONQLAY")).isSelected()) {
            parm = showTParm(id,"Y");//自动质控
        } else if (((TRadioButton)this.getComponent("RADIONPOWER")).isSelected()) {
            parm = showTParm(id,"N");//人工质控
        } else {
            parm = showTParm(id,"-1");//全部
        }
        table.setParmValue(parm);
    }

    /**
     * 筛选方法
     * @param id String
     * @return TParm
     */
    public TParm showTParm(String id,String flg) {
        TParm parm = new TParm();
        int count = 0;
        //显示此项目中所有数据
        if("-1".equals(flg)){
            for(int i = 0; i < parmValue.getCount(); i++){
                if(parmValue.getValue("EXAMINE_CODE",i).contains(id)){
                    showParmTemp(parm, i);
                    count++;
                }
            }
        }
        //显示自动质控或人工质控数据
        else{
            for (int i = 0; i < parmValue.getCount(); i++) {
                //筛选显示：区分自动质控、人工质控
                if (parmValue.getValue("FLG", i).equals(flg) && parmValue.getValue("EXAMINE_CODE",i).contains(id)) {
                    showParmTemp(parm, i);
                    count++;
                }
            }
        }
        parm.setCount(count);
        return parm;
    }
    
    /**
     * 筛选方法：将数据显示在表格中
     */
    private void showParmTemp(TParm parm, int i) {
        parm.addData("EXAMINE_DESC", parmValue.getValue("EXAMINE_DESC", i)); //审核项目名称
        parm.addData("EXAMINE_CODE", parmValue.getValue("EXAMINE_CODE", i)); //审核编号
        parm.addData("EXAMINE_DATE", parmValue.getValue("EXAMINE_DATE", i)); //审核时间
        parm.addData("MR_NO", parmValue.getValue("MR_NO", i)); //病案号
        parm.addData("CASE_NO", parmValue.getValue("CASE_NO", i)); //就诊号
        parm.addData("IPD_NO", parmValue.getValue("IPD_NO", i)); //住院号
        parm.addData("SCORE", parmValue.getValue("SCORE", i)); //分数
        parm.addData("DEDUCT_SCORECOUNT", parmValue.getInt("DEDUCT_SCORECOUNT", i)); //项数统计
        parm.addData("USER_NAME", parmValue.getValue("USER_NAME", i)); //医生名称
        parm.addData("QUERYSTATUS", parmValue.getValue("QUERYSTATUS", i)); //显示：未查、已查
        parm.addData("STATUS", parmValue.getValue("STATUS", i)); //显示：通过、未通过
        parm.addData("FLG", parmValue.getValue("FLG", i)); //筛选显示：区分自动指控、人工指控
        parm.addData("QUERYSTATUS_FLG", parmValue.getValue("QUERYSTATUS_FLG", i)); //查询复选框
        parm.addData("STATUS_FLG", parmValue.getValue("STATUS_FLG", i)); //检核复选框
        parm.addData("CHECK_RANGE", parmValue.getValue("CHECK_RANGE", i));//审核范围
        parm.addData("CHECK_USER", parmValue.getValue("CHECK_USER", i));//审核人员
        parm.addData("CHECK_DATE", parmValue.getValue("CHECK_DATE", i));
        parm.addData("OPT_USER", parmValue.getValue("OPT_USER", i));
        parm.addData("OPT_TERM", parmValue.getValue("OPT_TERM", i));
        parm.addData("ISTRUE", parmValue.getValue("ISTRUE", i)); //操作：修改、添加
        parm.addData("URG_FLG", parmValue.getValue("URG_FLG", i)); //急件否注记
    }
    
    /**
     * 表格值改变事件：修改状态、检核结果列值
     * @param obj Object
     * @return boolean
     */
    public void onTableCheckBoxClicked(Object obj) {
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
        table.acceptText();
        TTable node = (TTable) obj;
        if (node == null)
            return;
        //如果改变的节点数据和原来的数据相同就不改任何数据
        int column = node.getSelectedColumn();
        int selectRow = node.getSelectedRow();
        int row = getRow("EXAMINE_CODE", node.getParmValue().getValue("EXAMINE_CODE", selectRow));
        //拿到当前改变后的数据
        String value = null;
        //拿到table上的parmmap的列名
        String columnName = node.getParmMap(column);
        //如果是检核状态复选框改变
        if ("QUERYSTATUS_FLG".equals(columnName)) {
            value = parmValue.getValue("STATUS_FLG", row);
            if("1".equals(value) || "Y".equals(value)){
                this.messageBox("已通过检核的项目不可以取消已查状态");
                parmValue.setData("QUERYSTATUS_FLG", row,"Y");
                onRadionClick();
                return ;
            }
            value = getStatusValue("QUERYSTATUS_FLG", node, selectRow);
            parmValue.setData("QUERYSTATUS", row, value);//总数据中检查状态改变值
            parmValue.setData("QUERYSTATUS_FLG", row, node.getParmValue().getValue(columnName, selectRow));
        }
        //如果是检查状态复选框改变
        if ("STATUS_FLG".equals(columnName)) {
            value = getStatusValue("STATUS_FLG", node, selectRow);
            parmValue.setData("STATUS", row, value);//总数据中检查状态改变值
            parmValue.setData("STATUS_FLG", row, node.getParmValue().getValue(columnName, selectRow));
            parmValue.setData("QUERYSTATUS", row, value); //总数据中检查状态改变值
            parmValue.setData("QUERYSTATUS_FLG", row, node.getParmValue().getValue(columnName, selectRow));
        }
        onRadionClick();
        return;
    }

    private int getRow(String colName, String code) {
        int rowCount = parmValue.getCount();
        for (int i = 0; i < rowCount; i++) {
            if (!parmValue.getValue(colName, i).equals(code))
                continue;
            return i;
        }
        return rowCount;
    }

    /**
     * 修改列值
     * @param columnName String
     * @param node TTable
     * @param row int
     * @return String
     */
    private String getStatusValue(String columnName, TTable node, int row) {
        String value = node.getParmValue().getValue(columnName, row);
        value = value.equals("Y") ? "1" : "0";
        return value;
    }

    /**
     * 审核通过
     */
    public void onSave() {
//        TParm parm = table.getParmValue();
//        if (parm.getCount() <= 0) {
//            this.messageBox("请选择要修改的数据");
//            return;
//        }
        TParm parmValues = new TParm();
        // 提交选中
        TParm parmr = new TParm();
        parmr.setData("CASE_NO", CASE_NO);
        parmr.setData("MR_NO", MR_NO);
        parmr = MROQlayControlMTool.getInstance().queryQlayControlSUM(parmr);//在MRO_RECORD中查询是否完成，以及总分数信息
        //查找是否已经提交完成
        if (TYPERESULT.equals("0") && !getCheckBox("TYPERESULT").isSelected()) {
            if ("1".equals(parmr.getValue("TYPERESULT", 0))) {
                this.messageBox("提交状态异常，请退出界面重进，再进行操作");
                this.closeWindow();
                return;
            }else{
                parmValues.setData("TYPE", "0"); // 不提交
            }
        }
        if (TYPERESULT.equals("0") && getCheckBox("TYPERESULT").isSelected()) {// 提交提示
            if (!"1".equals(parmr.getValue("TYPERESULT", 0))) {
                if (this.messageBox("提示", "是否确定已经完成此病患的所有质控项目,提交后将不可以修改", 2) == 0) {
                    parmValues.setData("TYPE", "1"); // 提交字段
                } else return;
            } else {
                parmValues.setData("TYPE", "1"); // 提交字段
            }
        }
        if (TYPERESULT.equals("1") && getCheckBox("TYPERESULT").isSelected()) {// 提交提示
            parmValues.setData("TYPE", "1"); // 提交字段
        } else if (TYPERESULT.equals("1") && !getCheckBox("TYPERESULT").isSelected()) {// 只保存数据不提交
            parmValues.setData("TYPE", "0"); // 不提交
        }
        parmValues.setData("SCODEPARM", parmValue.getData());//当前病患的所有质控信息
        parmValues.setData("selPrice", selParm.getData());//获得每个分支的总分数
        TParm result = TIOM_AppServer.executeAction("action.mro.MROQlayDataAction", "saveQlayControlm", parmValues);
        if (result == null || !result.getErrText().equals("")) {// 保存判断
            this.messageBox("保存失败" + " , " + result.getErrText());
            return;
        }
        this.setValue("RESULTPRICE", result.getValue("RESULTPRICE"));
        this.messageBox("P0001");//保存成功
        table.setParmValue(new TParm());
        this.clearValue("TYPERESULT;SELECTQUERYSTATUS;SELECTSTATUS");
        ((TRadioButton) this.getComponent("RADIONSUM")).setSelected(true); // 全部单选按钮选择
        this.setValue("SUMPRICE", "0");
        this.setValue("SUBPRICE", "0");
        onInit();//add by wanglong 20131201
        parmData.runListener("onReturnContent", CASE_NO + ";" + result.getValue("RESULTPRICE") + ";" + result.getValue("TYPERESULT"));//
//        parmValue = new TParm(); //提交数据是的所有数据
//        showQlayM();
//        //将已经质控的数据放到表格中，显示所有的质控项目
//        getShowTParm();
//        //========= add by wanglong 20130819
//        TParm caseParm = new TParm();
//        caseParm.addData("CASE_NO", CASE_NO);
//        result =
//                TIOM_AppServer.executeAction("action.mro.MROQlayControlAction", "updateScore", caseParm);//更新得分
//        if (result.getErrCode() != 0) {
//            this.messageBox(result.getErrText());
//            return;
//        }
//        result = MROQlayControlMTool.getInstance().queryQlayControlSUM(caseParm.getRow(0));//查询得分
//        if(result.getErrCode() != 0){
//            this.messageBox(result.getErrText());
//            return;
//        }
//        this.setValue("RESULTPRICE", result.getValue("SUMSCODE", 0));//显示得分到界面
//        parmData.runListener("onReturnContent", CASE_NO+";"+ result.getValue("SUMSCODE", 0) );
        //========= add end
    }

    /**
     * 病历书写
     */
    public void onEmrRead() {
        TParm parm = new TParm();
        parm.setData("SYSTEM_TYPE", "ODI");
        parm.setData("ADM_TYPE", "I");
        parm.setData("CASE_NO", CASE_NO);
        parm.setData("PAT_NAME", PAT_NAME);
        parm.setData("MR_NO", MR_NO);
        parm.setData("IPD_NO", IPD_NO);
        parm.setData("ADM_DATE", ADM_DATE);
        parm.setData("DEPT_CODE", DEPT_CODE);
        parm.setData("STATION_CODE", STATION_CODE);
        //parm.setData("STYLETYPE", "1");//设置是否出现左侧树
        parm.setData("RULETYPE", "1");
        parm.setData("WRITE_TYPE", "ODIR");// 写入类型“会诊” → 相当于“只读”
        parm.setData("EMR_DATA_LIST", new TParm());
        parm.addListener("EMR_LISTENER", this, "emrListener");
        parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
        //System.out.println("=====================病历查看============="+parm);
        openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    }

    
    /**
    * 状态复选框全选
    */
   public void selectQueryAll(){
       selectTemp("SELECTQUERYSTATUS","QUERYSTATUS_FLG","QUERYSTATUS");
   }
   
   /**
    * 检核结果复选框全选
    */
   public void selectStatusAll(){
       selectTemp("SELECTSTATUS","STATUS_FLG","STATUS");
       if(getCheckBox("SELECTSTATUS").isSelected())
           getCheckBox("SELECTQUERYSTATUS").setSelected(true);
       selectTemp("SELECTQUERYSTATUS","QUERYSTATUS_FLG","QUERYSTATUS");
   }
   
   private void selectTemp(String checkName,String flgName,String name){
       String flg = "N";
       String flgValue="0";
       if (getCheckBox(checkName).isSelected()) {
           flg = "Y";
           flgValue="1";
       } else {
           flg = "N";
           flgValue="0";
       }
       for (int i = 0; i < table.getRowCount(); i++) {
           int row = getRow("EXAMINE_CODE", table.getParmValue().getValue("EXAMINE_CODE", i));
           parmValue.setData(flgName, row, flg);
           parmValue.setData(name, row, flgValue);
       }
       onRadionClick();
   }
   
   /**
    * 得到CheckBox对象
    *
    * @param tagName 元素TAG名称
    * @return
    */
   private TCheckBox getCheckBox(String tagName) {
       return (TCheckBox) getComponent(tagName);
   }
   
   /**
    * 汇出Excel
    */
   public void onExport() {//add by wanglong 20130819
       if (table.getRowCount()<1) {
           return;
       }
       ExportExcelUtil.getInstance().exportExcel(table, "手动质控项目清单");
   }
   
    
}
