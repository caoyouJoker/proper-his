package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TRadioButton;
import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextArea;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import jdo.sys.SYSPublishBoardTool;
import com.dongyang.ui.TLabel;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TPanel;
import jdo.sys.MailUtil;
import jdo.sys.MailVO;

/**
 * <p>Title: 公告栏</p>
 *
 * <p>Description: 公告栏发送，接收，查询，修改，删除和发送邮件消息功能</p>
 *
 * <p>Copyright: Copyright (c) 2011-04-20</p>
 *
 * <p>Company: javahis</p>
 *
 * @author li.xiang790130@gmail.com
 * @version 1.0
 */
public class SYSPublishBoardControl
    extends TControl {
    //接收公告每页缺省大小
    private static final int DEFAULT_PAGE_SIZE = 10;
    //第页记录数量
    private int pageSize = DEFAULT_PAGE_SIZE;
    //记录总数；
    private long totalCount = 0;
    //开始索引
    private int start = 0;

    private int pageNo = 1;


    //当前操作用户
    String optUser = Operator.getID();
    //当前终端IP
    String optTerm = Operator.getIP();
    //当前院区
    String region = Operator.getRegion();

    //最终的收件人（与接收类型有关）
    String recipients = "";
    //接收类型
    String receiveType = "P";

    //发送类型
    //个人
    TRadioButton radioPostTypePerson;
    //部门
    TRadioButton radioPostTypeDept;
    //角色
    TRadioButton radioPostTypeRole;
    //所有
    TRadioButton radioPostTypeAll;
    //收件个人下拉框
    TextFormatSYSOperator tfOperator;
    //收件部门下拉框
    TextFormatDept tfDept;
    //收件组下拉框
    TTextFormat tfRole;

    //写公告区域按扭
    TButton btnWriteSave;
    TButton btnWriteDelete;
    TButton btnWriteSearch;
    TButton btnWriteClear;
    TButton btnWriteOut;

    TButton btnSave;
    TButton btnCancel;
    TButton btnDelete;
    //已发送布公告列表
    TTable tableWritePostGrid;
    //已发送接收人的公告列表
    TTable tableWriteRecipient;
    //当前用户已接收公告消息
    TTable tableRecipientMessage;
    //公布主题
    TTextField txtWriteSubject;
    //是否急件
    TCheckBox chkWriteURGFlg;
    //发送内容
    TTextArea txtAreaPostInfo;
    //是否显示历史资料
    TCheckBox chkHistoryFlag;
    TLabel labPage;
    TButton btnUp;
    TButton btnDown;
    //读公告消息标题
    TTextField txtReadSubject;
    //读公告消息内容
    TTextArea txtAreaReadInfo;
    //接收消息总记录数；
    TLabel labTotalCount;
    //张贴公告按扭
    TButton btnWrite;
    //写公告面版
    TPanel panelWrite;
    //读公告面版
    TPanel panelRead;
    //读公告面版处离开按扭
    TButton btnReadOut;
    //是否同时发送邮件
    TCheckBox chkSendMailFlg;
    //公布开始日期；
    TTextFormat tfStartPostDate;
    //公布结束日期；
    TTextFormat tfEndPostDate;

    public SYSPublishBoardControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        //得到外部的参数
        //本界面的初始化;
        this.initControler();
        //加载收到公告消息列表
        this.onLoadReceiveMessTable();
        //加载当前用户已发送公告;
        this.onloadPostedMessTable();

        //隐藏初始时不显示的面版
        panelWrite.setVisible(false);
        panelRead.setVisible(false);

    }

    /**
     * 加载收到公告消息列表(带分页10条一页)
     */
    public void onLoadReceiveMessTable() {
        totalCount = this.getTotalCount();
        labPage.setText("页次 " + pageNo + "/" + getTotalPageCount());
        labTotalCount.setText("共" + totalCount + "条");
        start = getStartOfPage(pageNo);
        //按扭设置
        //后一页
        if (this.hasNextPage()) {
            btnDown.setEnabled(true);
        }
        else {
            btnDown.setEnabled(false);
        }
        //前一页
        if (this.hasPreviousPage()) {
            btnUp.setEnabled(true);
        }
        else {
            btnUp.setEnabled(false);
        }

        TParm parm = new TParm();
        if (chkHistoryFlag.isSelected()) {
            parm.setData("chkHistoryFlag", "Y");
        }
        else {
            parm.setData("chkHistoryFlag", "N");
        }
        parm.setData("startIndex", start);
        parm.setData("endIndex", pageSize);
        parm.setData("OPT_USER", optUser);

        TParm query = SYSPublishBoardTool.getInstance().getReceiveMessageList(
            parm);
        for (int i = 0; i < query.getCount(); i++) {
            if (query.getData("READ_FLG", i).equals("Y")) {
                query.setData("READ_FLG", i, true);
            }
            else {
                query.setData("READ_FLG", i, false);
            }
        }

        tableRecipientMessage.setParmValue(query,
                                           "READ_FLG;URG_FLG;POST_TIME;POST_SUBJECT;USER_NAME;MESSAGE_NO;USER_ID");

    }

    /**
     * 上一页
     */
    public void onUpPage() {

        pageNo = pageNo - 1;
        //调用加载列表
        onLoadReceiveMessTable();
    }

    /**
     * 下一页
     */
    public void onDownPage() {
        pageNo = pageNo + 1;
        //调用加载列表
        onLoadReceiveMessTable();
    }

    /**
     * 获得列表总数；
     * @return long
     */
    private long getTotalCount() {
        //this.messageBox("come in getTotalCount");
        TParm parm = new TParm();
        parm.setData("OPT_USER", optUser);
        if (chkHistoryFlag.isSelected()) {
            parm.setData("chkHistoryFlag", "Y");
        }
        else {
            parm.setData("chkHistoryFlag", "N");
        }
        parm = SYSPublishBoardTool.getInstance().getReceiveMessageCount(parm);
        //this.messageBox("messCount===="+parm.getLong(0,0));
        return parm.getLong(0, 0);
    }

    /**
     * 取得总页数；
     * @return long
     */
    private long getTotalPageCount() {
        return totalCount % (long) pageSize != 0L ? totalCount
            / (long) pageSize + 1L : totalCount / (long) pageSize;
    }

    /**
     * 获得当前页号
     * @return int
     */
    private int getCurrentPageNo() {
        return start / pageSize + 1;
    }

    /**
     * 是否下一页
     * @return boolean
     */
    private boolean hasNextPage() {
        return (long) getCurrentPageNo() < getTotalPageCount();
    }

    /**
     * 是否有前一页
     * @return boolean
     */
    private boolean hasPreviousPage() {
        return getCurrentPageNo() > 1;
    }

    /**
     * 获得起始页索引
     * @param pageNo int
     * @return int
     */
    protected static int getStartOfPage(int pageNo) {
        return getStartOfPage(pageNo, DEFAULT_PAGE_SIZE);
    }

    private static int getStartOfPage(int pageNo, int pageSize) {
        return (pageNo - 1) * pageSize;
    }


    /**
     * 读公告面版离开功能
     *
     */
    public void onReadOut() {
        //清空
        txtReadSubject.setValue("");
        txtAreaReadInfo.setValue("");
        //隐藏
        panelRead.setVisible(false);
        //重新加载接收列表
    }

    /**
     * 张贴按扭功能，显示写公告
     */
    public void onShowWriteMessage() {
        //张帖显示
        panelWrite.setVisible(true);
    }

    /**
     * 发布公告面版离开按扭功能
     */
    public void onWriteOut() {
        //清空
        onClear();
        //隐藏
        panelWrite.setVisible(false);
    }


    /**
     * 点击历史资料checkbox功能,重新加载已接收列表记录
     */
    public void onChkedHistoryMessage() {

        //重新加载收到公告消息列表
        pageNo = 1;
        this.onLoadReceiveMessTable();

    }

    /**
     * 收到公告消息列表双击，阅读详公告详细内容
     */
    public void onReceiveMessTableDbClick(int row) {
        if (row < 0) {
            return;
        }
        panelRead.setVisible(true);
        //显示读公告的面版；MESSAGE_NO
        final String messageNo = (String) tableRecipientMessage.getParmValue().
            getData("MESSAGE_NO", row);
        final String userId = (String) tableRecipientMessage.getParmValue().
            getData("USER_ID", row);
        //this.messageBox("messageNo"+messageNo);
        TParm parm = new TParm();
        parm.setData("MESSAGE_NO", messageNo);
        parm.setData("USER_ID", userId);
        //调用Action方法发送内容；
        //调用action执行事务
        TParm result = TIOM_AppServer.executeAction(
            "action.sys.SYSPublishBoardAction",
            "onReadMessage", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("阅读公告失败！");
            return;
        }
        //设置控件值；
        txtReadSubject.setValue(result.getValue("POST_SUBJECT", 0));
        txtAreaReadInfo.setValue(result.getValue("POST_INFOMATION", 0));
        //重新加载接收公告表格(显中的设置为已读)setValueAt("Y", i, 0);
        tableRecipientMessage.setValueAt(true, row, 0);
    }

    /**
     * 存档
     * 对已发送公告进行修改
     */
    public void onMessageSave() {
        TParm parm = new TParm();
        int selectRow = tableWritePostGrid.getSelectedRow();
        if (selectRow < 0) {
            return;
        }

        String messageNo=(String)tableWritePostGrid.getParmValue().getData( "MESSAGE_NO",selectRow);

        parm.setData("MESSAGE_NO", messageNo);
        parm.setData("POST_SUBJECT", txtWriteSubject.getValue());
        if (chkWriteURGFlg.isSelected()) {
            parm.setData("URG_FLG", "Y");
        }
        else {
            parm.setData("URG_FLG", "N");
        }
        parm.setData("POST_INFOMATION", txtAreaPostInfo.getValue());
        //调用更新公告
        TParm result = SYSPublishBoardTool.getInstance().
            updateMessageByMessageNo(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("存档失败！");
            return;
        }
        //更新以后()；
        this.onQuery();
        this.messageBox("存档成功！");

    }


    /**
     * 发送公告
     */
    public void onSave() {
        //检查主题及内容不能为空，或者不能超出范围；
        if (checkPublishMessage()) {
            //构造公告
            TParm publishMessage = new TParm();
            this.populatePublishMessage(publishMessage);
            //调用Action方法发送内容；
            //调用action执行事务
            TParm result = TIOM_AppServer.executeAction(
                "action.sys.SYSPublishBoardAction",
                "onPublishMessage", publishMessage);

            if (result.getErrCode() < 0) {
                this.messageBox("发送失败！");
                return;
            }

            //是否同时发送邮件通知公告；
            if (chkSendMailFlg.isSelected()) {
                MailVO mailVO = new MailVO();
                //取需发送用户的邮箱地址；
                TParm users = SYSPublishBoardTool.getInstance().getReceiveUsers(
                    publishMessage);
                if (users != null && users.getCount() > 0) {
                    String email = "";
                    for (int i = 0; i < users.getCount(); i++) {
                        email = (String) users.getData("E_MAIL", i);
                        //System.out.println("E_MAIL===============" + email);
                        //email不为空加入
                        if (email != null && !email.equals("")) {
                            mailVO.getToAddress().add(email);
                        }
                    }
                }
                //假如有需发送邮件地址，则再发送邮件；
                if (mailVO.getToAddress() != null &&
                    mailVO.getToAddress().size() > 0) {
                    //1.取邮件服务工具类
                    MailUtil mailUtil = MailUtil.getInstance();
                    //构造Mail VO；
                    //主题
                    mailVO.setSubject( (String) publishMessage.getData(
                        "POST_SUBJECT"));
                    //开始构造邮件内容；
                    StringBuffer mailContent = new StringBuffer(optUser +
                        "发布公告消息");
                    String isURG = (String) publishMessage.getData("URG_FLG");
                    if (isURG.equalsIgnoreCase("Y")) {
                        mailContent.append("(急件)\r\n");
                    }
                    else {
                        mailContent.append("\r\n");
                    }
                    String strMailContent = (String) publishMessage.getData(
                        "POST_INFOMATION");
                    mailContent.append(strMailContent);
                    //邮件内容构造完成；
                    //设置内容
                    mailVO.setContent(mailContent.toString());

                    //邮件附件测试；
                    /**
                                         try {
                        byte[] data = FileTool.getByte(
                            "c:\\000000273030_检验报告单_2010-11-23-10-50.pdf");
                        MailAttachment ma=new MailAttachment();
                        ma.setName("000000273030_检验报告单_2010-11-23-10-50.pdf");
                        ma.setData(data);
                        mailVO.getAttachByteArrays().add(ma);

                                         }
                                         catch (IOException ex) {
                        ex.printStackTrace();
                                         }**/

                    //
                    //发送邮件
                    TParm MailSendResult = mailUtil.sendMail(mailVO);

                    if (MailSendResult.getErrCode() < 0) {
                        this.messageBox("公告发送成功，邮件发送失败！");
                        this.onSaveAfter();
                        return;
                    }

                }
            }

            this.messageBox("发送成功！");
            this.onSaveAfter();

        }
    }

    /**
     * 公告删除后
     */
    private void onDeleteAfter() {
        this.onSaveAfter();
        this.onloadSendUsersTable("");
        //btnWriteDelete.setEnabled(false);
        //取消存档
        btnWriteSave.setEnabled(false);
    }


    /**
     * 公告发布成功后
     */
    private void onSaveAfter() {
        //清空发布公告
        txtWriteSubject.setValue("");
        chkWriteURGFlg.setSelected(false);
        txtAreaPostInfo.setValue("");
        radioPostTypePerson.setSelected(true);
        this.onPostType();
        //重新加载已发送列表Table_Write_PL
        this.onloadPostedMessTable();
        //取消存档
        btnWriteSave.setEnabled(false);
        //自已发给自已的情况，刷新已接收公告列表
        pageNo = 1;
        this.onLoadReceiveMessTable();
        chkSendMailFlg.setSelected(false);

    }

    /**
     * 构造公告
     * @param publishMessage TParm
     */
    private void populatePublishMessage(TParm publishMessage) {
        publishMessage.setData("POST_SUBJECT", txtWriteSubject.getValue());
        if (chkWriteURGFlg.isSelected()) {
            publishMessage.setData("URG_FLG", "Y");
        }
        else {
            publishMessage.setData("URG_FLG", "N");
        }
        publishMessage.setData("POST_INFOMATION", txtAreaPostInfo.getValue());
        //接收人
        publishMessage.setData("RECIPIENTS", recipients);
        //接收类型
        publishMessage.setData("RECEIVE_TYPE", receiveType);
        //响应数量
        publishMessage.setData("RESPONSE_NO", 0);
        publishMessage.setData("POST_ID", optUser);
        //操作人员信息
        publishMessage.setData("OPT_USER", optUser);
        publishMessage.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
        publishMessage.setData("OPT_TERM", optTerm);
        publishMessage.setData("REGION", region);

    }


    /**
     * 删除已发送公告
     */
    public void onDelete() {
        tableWritePostGrid.acceptText();
        String flag = "";
        String delMessageNos = "";

        for (int i = 0; i < tableWritePostGrid.getRowCount(); i++) {
            flag = (String) tableWritePostGrid.getItemData(i, 0);

            if (flag.equalsIgnoreCase("Y")) {
                delMessageNos += "'" +
                    (String) tableWritePostGrid.getParmValue().getData("MESSAGE_NO",
                    i) + "',";

            }

        }
        //没有选择任何公告删除则报消息
        if (delMessageNos.equals("")) {
            this.messageBox("请选择要删除的公告！");
            return;

            //否则执行删除功能
        }
        else {
            delMessageNos = delMessageNos.substring(0,
                delMessageNos.length() - 1);
        }

        //this.messageBox("delMessageNos===="+delMessageNos);
        if (this.messageBox("询问", "确定删除吗？", 2) == 0) {
            TParm parm = new TParm();
            parm.setData("DEL_MESSAGE_NOS", delMessageNos);
            //调用action执行事务
            TParm result = TIOM_AppServer.executeAction(
                "action.sys.SYSPublishBoardAction",
                "onBatchDeletePublishMessage", parm);

            if (result.getErrCode() < 0) {
                this.messageBox("删除失败！");
                return;
            }

            this.messageBox("删除成功！");
            this.onDeleteAfter();

        }
        else {
            return;
        }

    }

    /**
     * 查询已发送公告
     */
    public void onQuery() {
        //TDataStore dataStroe = tableWritePostGrid.getDataStore();
        StringBuffer sb = new StringBuffer("SELECT '' as CHK,(CASE WHEN URG_FLG='Y' THEN '!' WHEN URG_FLG='N' THEN '' END) as URG_FLG_TITLE,POST_SUBJECT,POST_TIME,MESSAGE_NO,URG_FLG,POST_INFOMATION FROM SYS_BOARD WHERE POST_ID = '"
                                           + optUser + "'");

        String subject = txtWriteSubject.getValue();
        String startPostDate=tfStartPostDate.getText();
        String endPostDate=tfEndPostDate.getText();

        //不区分大小写
        if (!StringUtil.isNullString(subject)) {
            sb.append(" AND UPPER(POST_SUBJECT) like '%" + subject.toUpperCase() +
                      "%'");
        }
        //开始发布日期
        if(!StringUtil.isNullString(startPostDate)){
            sb.append(" AND POST_TIME>=TO_DATE ('" + startPostDate + " 000000" +"', 'yyyy/MM/dd hh24miss')");
        }

        //结束日期
        if(!StringUtil.isNullString(endPostDate)){
            sb.append(" AND POST_TIME<=TO_DATE ('" + endPostDate + " 235959" +"', 'yyyy/MM/dd hh24miss')");
        }
        sb.append(" ORDER BY POST_TIME DESC");

        //System.out.println("SQL====="+sb.toString());

        TParm parm = new TParm(this.getDBTool().select(sb.toString()));

        tableWritePostGrid.setParmValue(parm,"CHK;URG_FLG_TITLE;POST_SUBJECT;POST_TIME;MESSAGE_NO;URG_FLG;POST_INFOMATION");

        //设置存档安扭无效
        btnWriteSave.setEnabled(false);

    }

    /**
     * 清空功能
     */
    public void onClear() {
        //test
        //String str=BpelUtil.getInstance().getFileText("/2011/04/08/110408000006/201104081101004711040800000110.HL7");
        //System.out.println("test str"+str);


        tableWritePostGrid.clearSelection();
        this.onloadSendUsersTable("");
        txtWriteSubject.setValue("");
        chkWriteURGFlg.setSelected(false);
        txtAreaPostInfo.setValue("");
        tfStartPostDate.setValue("");
        tfEndPostDate.setValue("");

        radioPostTypePerson.setSelected(true);
        this.onPostType();

        onloadPostedMessTable();

        //取消存档
        btnWriteSave.setEnabled(false);

    }

    /**
     * 取消已发送列表
     */
    public void onCancel() {

        tableWriteRecipient.clearSelection();
        btnCancel.setEnabled(false);
        btnDelete.setEnabled(false);

    }


    /**
     * 删除接收者关联消息
     */
    public void onDeleteRecipientMessage() {
        //提示“确定是否删除”;
        //是，则通过messageNo及接收者USER_ID删除；
        int selectRow = tableWriteRecipient.getSelectedRow();
        if (this.messageBox("询问", "确定删除吗？", 2) == 0) {
            if (selectRow < 0) {
                return;
            }
            final String userID = (String) tableWriteRecipient.getParmValue().
                getData("USER_ID", selectRow);
            final String messageNo = (String) tableWriteRecipient.getParmValue().
                getData("MESSAGE_NO", selectRow);
            TParm parm = new TParm();
            parm.setData("USER_ID", userID);
            parm.setData("MESSAGE_NO", messageNo);
            //通过用户ID和公告消息号删除接收档记录
            TParm result = SYSPublishBoardTool.getInstance().
                deleteReceiveMessage(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("删除失败！");
                return;
            }

            this.messageBox("删除成功！");

            this.afterDeleteRecipientMessage();

        }
        else {
            return;
        }

    }

    /**
     * 删除接收档记录后
     */
    private void afterDeleteRecipientMessage() {
        //删除后
        final int row = tableWritePostGrid.getSelectedRow();
        this.onPostedMessTableClicked(row);
        btnCancel.setEnabled(false);
        btnDelete.setEnabled(false);

    }


    /**
     * 加载已发送公告Table_Write_PL列表记录
     */
    private void onloadPostedMessTable() {
        onQuery();
    }

    /**
     * 加载已发送公告Table_Write_RL接收人列表记录
     */
    private void onloadSendUsersTable(String messageNo) {
        String sql =
            "SELECT (CASE WHEN POST_TYPE='P' THEN '个人' WHEN POST_TYPE='D' THEN '部门' WHEN POST_TYPE='R' THEN '角色' WHEN POST_TYPE='A' THEN '全体' END) POST_TYPE,";
        sql += "o.USER_NAME,p.USER_ID,MESSAGE_NO";
        sql += " FROM SYS_POSTRCV p left join SYS_OPERATOR o on p.USER_ID=o.USER_ID WHERE MESSAGE_NO = '"
            + messageNo + "' ORDER BY p.OPT_DATE DESC";
        TParm query = new TParm(getDBTool().select(sql));
        tableWriteRecipient.setParmValue(query,
                                         "POST_TYPE;USER_NAME;USER_ID;MESSAGE_NO");

    }

    /**
     * 已发送人选择事件
     */
    public void onSendUsersTableClicked(int row) {
        if (row < 0) {
            return;
        }
        btnCancel.setEnabled(true);
        btnDelete.setEnabled(true);
    }


    /**
     * 已发送公告选中事件
     */
    public void onPostedMessTableClicked(int row) {
        //通过messageNo，获得已发送消息对应用户
        if (row < 0) {
            return;
        }

        String messageNo = (String) tableWritePostGrid.getParmValue().getData("MESSAGE_NO",
                    row);

        String postSubject = (String) tableWritePostGrid.getParmValue().getData("POST_SUBJECT",row);
        String urgFlg = (String) tableWritePostGrid.getParmValue().getData( "URG_FLG",row);
        String postInfo = (String) tableWritePostGrid.getParmValue().getData("POST_INFOMATION", row);

        txtWriteSubject.setValue(postSubject);
        if (urgFlg.equalsIgnoreCase("Y")) {
            chkWriteURGFlg.setSelected(true);
        }
        else {
            chkWriteURGFlg.setSelected(false);
        }
        txtAreaPostInfo.setValue(postInfo);

        radioPostTypePerson.setSelected(true);

        this.onPostType();
        //数据加载到Table_Write_RL接收者列表；
        this.onloadSendUsersTable(messageNo);
        //存档按扭可用,可修改公告内容;
        btnWriteSave.setEnabled(true);

    }

    /**
     * 选择方送类型
     */
    public void onPostType() {
        //个人
        if (radioPostTypePerson.isSelected()) {
            this.clearPostTypes();
            radioPostTypePerson.setSelected(true);

            this.setEnableRecipientSelect(true);
            this.hideRecipientSelect();
            tfOperator.setVisible(true);
        }
        //部门
        else if (radioPostTypeDept.isSelected()) {
            this.clearPostTypes();
            radioPostTypeDept.setSelected(true);

            this.setEnableRecipientSelect(true);
            this.hideRecipientSelect();
            tfDept.setVisible(true);

        }
        //角色
        else if (radioPostTypeRole.isSelected()) {
            this.clearPostTypes();
            radioPostTypeRole.setSelected(true);

            this.setEnableRecipientSelect(true);
            this.hideRecipientSelect();
            tfRole.setVisible(true);
        }
        //所有
        else if (radioPostTypeAll.isSelected()) {
            this.clearPostTypes();
            radioPostTypeAll.setSelected(true);
            this.setEnableRecipientSelect(false);

        }
        this.onRecipientSelected();

    }

    /**
     * 收件人选择后事件
     */
    public void onRecipientSelected() {
        //个人
        if (radioPostTypePerson.isSelected()) {
            receiveType = "P";
            if (!tfOperator.getValue().toString().equals("")) {
                recipients = tfOperator.getValue().toString();
                btnSave.setEnabled(true);
            }
            else {
                btnSave.setEnabled(false);
            }

        }
        //部门
        else if (radioPostTypeDept.isSelected()) {
            receiveType = "D";
            if (!tfDept.getValue().toString().equals("")) {
                recipients = tfDept.getValue().toString();
                btnSave.setEnabled(true);
            }
            else {
                btnSave.setEnabled(false);
            }

        }
        //角色
        else if (radioPostTypeRole.isSelected()) {
            receiveType = "R";
            if (!tfRole.getValue().toString().equals("")) {
                recipients = tfRole.getValue().toString();
                btnSave.setEnabled(true);
            }
            else {
                btnSave.setEnabled(false);
            }

        }
        //所有
        else if (radioPostTypeAll.isSelected()) {
            receiveType = "A";
            btnSave.setEnabled(true);
        }

    }

    /**
     * 存档前的数据有效性检查；
     * @return boolean
     */
    private boolean checkPublishMessage() {
        //检查公告主题;
        if (txtWriteSubject.getValue().toString().equals("")) {
            this.messageBox("主题不可为空白！");
            txtWriteSubject.grabFocus();
            return false;
        }
        else {
            if (txtWriteSubject.getValue().toString().length() > 200) {
                this.messageBox("主题长度超出范围！");
                txtWriteSubject.grabFocus();
                return false;
            }
        }
        //检查公告内容;
        if (txtAreaPostInfo.getValue().toString().equals("")) {
            this.messageBox("内容不可为空白！");
            txtAreaPostInfo.grabFocus();
            return false;
        }
        else {
            if (txtAreaPostInfo.getValue().toString().length() > 1000) {
                this.messageBox("内容长度超出范围！");
                txtAreaPostInfo.grabFocus();
                return false;
            }
        }
        //检查收件者必须选择
        if (recipients.equals("") && !radioPostTypeAll.isSelected()) {
            this.messageBox("收件者不可为空白！");
            return false;
        }

        return true;
    }

    /**
     * 初始化窗体控件
     */
    private void initControler() {
        //初始化控件
        radioPostTypePerson = (TRadioButton)this.getComponent("Radio_Person");
        radioPostTypeDept = (TRadioButton)this.getComponent("Radio_Dept");
        radioPostTypeRole = (TRadioButton)this.getComponent("Radio_Role");
        radioPostTypeAll = (TRadioButton)this.getComponent("Radio_All");

        tfOperator = (TextFormatSYSOperator)this.getComponent("TF_Operator");
        tfDept = (TextFormatDept)this.getComponent("TF_Dept");
        tfRole = (TTextFormat)this.getComponent("TF_Role");

        btnWriteSave = (TButton) getComponent("Btn_Write_Save");
        btnWriteDelete = (TButton) getComponent("Btn_Write_Delete");
        btnWriteSearch = (TButton) getComponent("Btn_Write_Search");
        btnWriteClear = (TButton) getComponent("Btn_Write_Clear");

        btnSave = (TButton) getComponent("Btn_Save");
        btnCancel = (TButton) getComponent("Btn_Cancel");
        btnDelete = (TButton) getComponent("Btn_Delete");

        tableWritePostGrid = getTable("Table_Write_PL");
        tableWriteRecipient = getTable("Table_Write_RL");

        txtWriteSubject = (TTextField) getComponent("Txt_Write_Title");

        chkWriteURGFlg = (TCheckBox) getComponent("Chk_Write_URG_FLG");

        txtAreaPostInfo = (TTextArea) getComponent("TxtArea_Write_Info");
        chkHistoryFlag = (TCheckBox) getComponent("Chk_HistoryFlag");

        labPage = (TLabel) getComponent("Lab_Page");
        btnUp = (TButton) getComponent("Btn_Up");
        btnDown = (TButton) getComponent("Btn_Down");

        tableRecipientMessage = getTable("Table_RL");

        txtReadSubject = (TTextField) getComponent("Txt_Read_Title");
        txtAreaReadInfo = (TTextArea) getComponent("TxtArea_Read_Info");
        labTotalCount = (TLabel) getComponent("Lab_TotalCount");

        btnWrite = (TButton) getComponent("Btn_Write");
        panelWrite = (TPanel) getComponent("tPanel_Write");
        panelRead = (TPanel) getComponent("tPanel_Read");
        btnReadOut = (TButton) getComponent("Btn_Read_Out");

        btnWriteOut = (TButton) getComponent("Btn_Write_Out");

        chkSendMailFlg = (TCheckBox) getComponent("Chk_SendMail");
        tfStartPostDate=(TTextFormat) getComponent("TF_StartPostDate");
        tfEndPostDate=(TTextFormat) getComponent("TF_EndPostDate");



        //初始值
        btnWriteSave.setEnabled(false);
        //btnWriteDelete.setEnabled(false);
        btnSave.setEnabled(false);
        btnCancel.setEnabled(false);
        btnDelete.setEnabled(false);

        //加入事件
        callFunction("UI|tPanel_Write|Table_Write_PL|addEventListener",
                     "Table_Write_PL->" + TTableEvent.CLICKED, this,
                     "onPostedMessTableClicked");

        callFunction(
            "UI|tPanel_Write|tPanel_Send|Table_Write_RL|addEventListener",
            "Table_Write_RL->" + TTableEvent.CLICKED, this,
            "onSendUsersTableClicked");

        // 给Table_RL中双击加侦听事件
        callFunction("UI|tPanel_PL|Table_RL|addEventListener",
                     "Table_RL->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onReceiveMessTableDbClick");

    }

    /**
     * 清除传送类别
     */
    private void clearPostTypes() {
        radioPostTypePerson.setSelected(false);
        radioPostTypeDept.setSelected(false);
        radioPostTypeRole.setSelected(false);
        radioPostTypeAll.setSelected(false);
    }

    /**
     * 隐藏不同传送类别收件人下拉框
     */
    private void hideRecipientSelect() {
        tfOperator.setVisible(false);
        tfDept.setVisible(false);
        tfRole.setVisible(false);
    }

    /**
     * 全部收件人下拉框无效
     */
    private void setEnableRecipientSelect(boolean isEnable) {
        if (isEnable) {
            tfOperator.setValue("");
            tfOperator.setEnabled(true);
            tfDept.setValue("");
            tfDept.setEnabled(true);
            tfRole.setValue("");
            tfRole.setEnabled(true);

        }
        else {
            tfOperator.setValue("");
            tfOperator.setEnabled(false);
            tfDept.setValue("");
            tfDept.setEnabled(false);
            tfRole.setValue("");
            tfRole.setEnabled(false);
        }
    }

    /**
     * 得到页面中Table对象
     * @param tag
     * @return
     */
    private TTable getTable(String tag) {
        return (TTable) callFunction("UI|" + tag + "|getThis");
    }

    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }


}
