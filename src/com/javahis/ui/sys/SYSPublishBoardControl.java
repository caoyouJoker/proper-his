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
 * <p>Title: ������</p>
 *
 * <p>Description: ���������ͣ����գ���ѯ���޸ģ�ɾ���ͷ����ʼ���Ϣ����</p>
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
    //���չ���ÿҳȱʡ��С
    private static final int DEFAULT_PAGE_SIZE = 10;
    //��ҳ��¼����
    private int pageSize = DEFAULT_PAGE_SIZE;
    //��¼������
    private long totalCount = 0;
    //��ʼ����
    private int start = 0;

    private int pageNo = 1;


    //��ǰ�����û�
    String optUser = Operator.getID();
    //��ǰ�ն�IP
    String optTerm = Operator.getIP();
    //��ǰԺ��
    String region = Operator.getRegion();

    //���յ��ռ��ˣ�����������йأ�
    String recipients = "";
    //��������
    String receiveType = "P";

    //��������
    //����
    TRadioButton radioPostTypePerson;
    //����
    TRadioButton radioPostTypeDept;
    //��ɫ
    TRadioButton radioPostTypeRole;
    //����
    TRadioButton radioPostTypeAll;
    //�ռ�����������
    TextFormatSYSOperator tfOperator;
    //�ռ�����������
    TextFormatDept tfDept;
    //�ռ���������
    TTextFormat tfRole;

    //д��������Ť
    TButton btnWriteSave;
    TButton btnWriteDelete;
    TButton btnWriteSearch;
    TButton btnWriteClear;
    TButton btnWriteOut;

    TButton btnSave;
    TButton btnCancel;
    TButton btnDelete;
    //�ѷ��Ͳ������б�
    TTable tableWritePostGrid;
    //�ѷ��ͽ����˵Ĺ����б�
    TTable tableWriteRecipient;
    //��ǰ�û��ѽ��չ�����Ϣ
    TTable tableRecipientMessage;
    //��������
    TTextField txtWriteSubject;
    //�Ƿ񼱼�
    TCheckBox chkWriteURGFlg;
    //��������
    TTextArea txtAreaPostInfo;
    //�Ƿ���ʾ��ʷ����
    TCheckBox chkHistoryFlag;
    TLabel labPage;
    TButton btnUp;
    TButton btnDown;
    //��������Ϣ����
    TTextField txtReadSubject;
    //��������Ϣ����
    TTextArea txtAreaReadInfo;
    //������Ϣ�ܼ�¼����
    TLabel labTotalCount;
    //�������水Ť
    TButton btnWrite;
    //д�������
    TPanel panelWrite;
    //���������
    TPanel panelRead;
    //��������洦�뿪��Ť
    TButton btnReadOut;
    //�Ƿ�ͬʱ�����ʼ�
    TCheckBox chkSendMailFlg;
    //������ʼ���ڣ�
    TTextFormat tfStartPostDate;
    //�����������ڣ�
    TTextFormat tfEndPostDate;

    public SYSPublishBoardControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        //�õ��ⲿ�Ĳ���
        //������ĳ�ʼ��;
        this.initControler();
        //�����յ�������Ϣ�б�
        this.onLoadReceiveMessTable();
        //���ص�ǰ�û��ѷ��͹���;
        this.onloadPostedMessTable();

        //���س�ʼʱ����ʾ�����
        panelWrite.setVisible(false);
        panelRead.setVisible(false);

    }

    /**
     * �����յ�������Ϣ�б�(����ҳ10��һҳ)
     */
    public void onLoadReceiveMessTable() {
        totalCount = this.getTotalCount();
        labPage.setText("ҳ�� " + pageNo + "/" + getTotalPageCount());
        labTotalCount.setText("��" + totalCount + "��");
        start = getStartOfPage(pageNo);
        //��Ť����
        //��һҳ
        if (this.hasNextPage()) {
            btnDown.setEnabled(true);
        }
        else {
            btnDown.setEnabled(false);
        }
        //ǰһҳ
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
     * ��һҳ
     */
    public void onUpPage() {

        pageNo = pageNo - 1;
        //���ü����б�
        onLoadReceiveMessTable();
    }

    /**
     * ��һҳ
     */
    public void onDownPage() {
        pageNo = pageNo + 1;
        //���ü����б�
        onLoadReceiveMessTable();
    }

    /**
     * ����б�������
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
     * ȡ����ҳ����
     * @return long
     */
    private long getTotalPageCount() {
        return totalCount % (long) pageSize != 0L ? totalCount
            / (long) pageSize + 1L : totalCount / (long) pageSize;
    }

    /**
     * ��õ�ǰҳ��
     * @return int
     */
    private int getCurrentPageNo() {
        return start / pageSize + 1;
    }

    /**
     * �Ƿ���һҳ
     * @return boolean
     */
    private boolean hasNextPage() {
        return (long) getCurrentPageNo() < getTotalPageCount();
    }

    /**
     * �Ƿ���ǰһҳ
     * @return boolean
     */
    private boolean hasPreviousPage() {
        return getCurrentPageNo() > 1;
    }

    /**
     * �����ʼҳ����
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
     * ����������뿪����
     *
     */
    public void onReadOut() {
        //���
        txtReadSubject.setValue("");
        txtAreaReadInfo.setValue("");
        //����
        panelRead.setVisible(false);
        //���¼��ؽ����б�
    }

    /**
     * ������Ť���ܣ���ʾд����
     */
    public void onShowWriteMessage() {
        //������ʾ
        panelWrite.setVisible(true);
    }

    /**
     * ������������뿪��Ť����
     */
    public void onWriteOut() {
        //���
        onClear();
        //����
        panelWrite.setVisible(false);
    }


    /**
     * �����ʷ����checkbox����,���¼����ѽ����б��¼
     */
    public void onChkedHistoryMessage() {

        //���¼����յ�������Ϣ�б�
        pageNo = 1;
        this.onLoadReceiveMessTable();

    }

    /**
     * �յ�������Ϣ�б�˫�����Ķ��깫����ϸ����
     */
    public void onReceiveMessTableDbClick(int row) {
        if (row < 0) {
            return;
        }
        panelRead.setVisible(true);
        //��ʾ���������棻MESSAGE_NO
        final String messageNo = (String) tableRecipientMessage.getParmValue().
            getData("MESSAGE_NO", row);
        final String userId = (String) tableRecipientMessage.getParmValue().
            getData("USER_ID", row);
        //this.messageBox("messageNo"+messageNo);
        TParm parm = new TParm();
        parm.setData("MESSAGE_NO", messageNo);
        parm.setData("USER_ID", userId);
        //����Action�����������ݣ�
        //����actionִ������
        TParm result = TIOM_AppServer.executeAction(
            "action.sys.SYSPublishBoardAction",
            "onReadMessage", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("�Ķ�����ʧ�ܣ�");
            return;
        }
        //���ÿؼ�ֵ��
        txtReadSubject.setValue(result.getValue("POST_SUBJECT", 0));
        txtAreaReadInfo.setValue(result.getValue("POST_INFOMATION", 0));
        //���¼��ؽ��չ�����(���е�����Ϊ�Ѷ�)setValueAt("Y", i, 0);
        tableRecipientMessage.setValueAt(true, row, 0);
    }

    /**
     * �浵
     * ���ѷ��͹�������޸�
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
        //���ø��¹���
        TParm result = SYSPublishBoardTool.getInstance().
            updateMessageByMessageNo(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("�浵ʧ�ܣ�");
            return;
        }
        //�����Ժ�()��
        this.onQuery();
        this.messageBox("�浵�ɹ���");

    }


    /**
     * ���͹���
     */
    public void onSave() {
        //������⼰���ݲ���Ϊ�գ����߲��ܳ�����Χ��
        if (checkPublishMessage()) {
            //���칫��
            TParm publishMessage = new TParm();
            this.populatePublishMessage(publishMessage);
            //����Action�����������ݣ�
            //����actionִ������
            TParm result = TIOM_AppServer.executeAction(
                "action.sys.SYSPublishBoardAction",
                "onPublishMessage", publishMessage);

            if (result.getErrCode() < 0) {
                this.messageBox("����ʧ�ܣ�");
                return;
            }

            //�Ƿ�ͬʱ�����ʼ�֪ͨ���棻
            if (chkSendMailFlg.isSelected()) {
                MailVO mailVO = new MailVO();
                //ȡ�跢���û��������ַ��
                TParm users = SYSPublishBoardTool.getInstance().getReceiveUsers(
                    publishMessage);
                if (users != null && users.getCount() > 0) {
                    String email = "";
                    for (int i = 0; i < users.getCount(); i++) {
                        email = (String) users.getData("E_MAIL", i);
                        //System.out.println("E_MAIL===============" + email);
                        //email��Ϊ�ռ���
                        if (email != null && !email.equals("")) {
                            mailVO.getToAddress().add(email);
                        }
                    }
                }
                //�������跢���ʼ���ַ�����ٷ����ʼ���
                if (mailVO.getToAddress() != null &&
                    mailVO.getToAddress().size() > 0) {
                    //1.ȡ�ʼ����񹤾���
                    MailUtil mailUtil = MailUtil.getInstance();
                    //����Mail VO��
                    //����
                    mailVO.setSubject( (String) publishMessage.getData(
                        "POST_SUBJECT"));
                    //��ʼ�����ʼ����ݣ�
                    StringBuffer mailContent = new StringBuffer(optUser +
                        "����������Ϣ");
                    String isURG = (String) publishMessage.getData("URG_FLG");
                    if (isURG.equalsIgnoreCase("Y")) {
                        mailContent.append("(����)\r\n");
                    }
                    else {
                        mailContent.append("\r\n");
                    }
                    String strMailContent = (String) publishMessage.getData(
                        "POST_INFOMATION");
                    mailContent.append(strMailContent);
                    //�ʼ����ݹ�����ɣ�
                    //��������
                    mailVO.setContent(mailContent.toString());

                    //�ʼ��������ԣ�
                    /**
                                         try {
                        byte[] data = FileTool.getByte(
                            "c:\\000000273030_���鱨�浥_2010-11-23-10-50.pdf");
                        MailAttachment ma=new MailAttachment();
                        ma.setName("000000273030_���鱨�浥_2010-11-23-10-50.pdf");
                        ma.setData(data);
                        mailVO.getAttachByteArrays().add(ma);

                                         }
                                         catch (IOException ex) {
                        ex.printStackTrace();
                                         }**/

                    //
                    //�����ʼ�
                    TParm MailSendResult = mailUtil.sendMail(mailVO);

                    if (MailSendResult.getErrCode() < 0) {
                        this.messageBox("���淢�ͳɹ����ʼ�����ʧ�ܣ�");
                        this.onSaveAfter();
                        return;
                    }

                }
            }

            this.messageBox("���ͳɹ���");
            this.onSaveAfter();

        }
    }

    /**
     * ����ɾ����
     */
    private void onDeleteAfter() {
        this.onSaveAfter();
        this.onloadSendUsersTable("");
        //btnWriteDelete.setEnabled(false);
        //ȡ���浵
        btnWriteSave.setEnabled(false);
    }


    /**
     * ���淢���ɹ���
     */
    private void onSaveAfter() {
        //��շ�������
        txtWriteSubject.setValue("");
        chkWriteURGFlg.setSelected(false);
        txtAreaPostInfo.setValue("");
        radioPostTypePerson.setSelected(true);
        this.onPostType();
        //���¼����ѷ����б�Table_Write_PL
        this.onloadPostedMessTable();
        //ȡ���浵
        btnWriteSave.setEnabled(false);
        //���ѷ������ѵ������ˢ���ѽ��չ����б�
        pageNo = 1;
        this.onLoadReceiveMessTable();
        chkSendMailFlg.setSelected(false);

    }

    /**
     * ���칫��
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
        //������
        publishMessage.setData("RECIPIENTS", recipients);
        //��������
        publishMessage.setData("RECEIVE_TYPE", receiveType);
        //��Ӧ����
        publishMessage.setData("RESPONSE_NO", 0);
        publishMessage.setData("POST_ID", optUser);
        //������Ա��Ϣ
        publishMessage.setData("OPT_USER", optUser);
        publishMessage.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
        publishMessage.setData("OPT_TERM", optTerm);
        publishMessage.setData("REGION", region);

    }


    /**
     * ɾ���ѷ��͹���
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
        //û��ѡ���κι���ɾ������Ϣ
        if (delMessageNos.equals("")) {
            this.messageBox("��ѡ��Ҫɾ���Ĺ��棡");
            return;

            //����ִ��ɾ������
        }
        else {
            delMessageNos = delMessageNos.substring(0,
                delMessageNos.length() - 1);
        }

        //this.messageBox("delMessageNos===="+delMessageNos);
        if (this.messageBox("ѯ��", "ȷ��ɾ����", 2) == 0) {
            TParm parm = new TParm();
            parm.setData("DEL_MESSAGE_NOS", delMessageNos);
            //����actionִ������
            TParm result = TIOM_AppServer.executeAction(
                "action.sys.SYSPublishBoardAction",
                "onBatchDeletePublishMessage", parm);

            if (result.getErrCode() < 0) {
                this.messageBox("ɾ��ʧ�ܣ�");
                return;
            }

            this.messageBox("ɾ���ɹ���");
            this.onDeleteAfter();

        }
        else {
            return;
        }

    }

    /**
     * ��ѯ�ѷ��͹���
     */
    public void onQuery() {
        //TDataStore dataStroe = tableWritePostGrid.getDataStore();
        StringBuffer sb = new StringBuffer("SELECT '' as CHK,(CASE WHEN URG_FLG='Y' THEN '!' WHEN URG_FLG='N' THEN '' END) as URG_FLG_TITLE,POST_SUBJECT,POST_TIME,MESSAGE_NO,URG_FLG,POST_INFOMATION FROM SYS_BOARD WHERE POST_ID = '"
                                           + optUser + "'");

        String subject = txtWriteSubject.getValue();
        String startPostDate=tfStartPostDate.getText();
        String endPostDate=tfEndPostDate.getText();

        //�����ִ�Сд
        if (!StringUtil.isNullString(subject)) {
            sb.append(" AND UPPER(POST_SUBJECT) like '%" + subject.toUpperCase() +
                      "%'");
        }
        //��ʼ��������
        if(!StringUtil.isNullString(startPostDate)){
            sb.append(" AND POST_TIME>=TO_DATE ('" + startPostDate + " 000000" +"', 'yyyy/MM/dd hh24miss')");
        }

        //��������
        if(!StringUtil.isNullString(endPostDate)){
            sb.append(" AND POST_TIME<=TO_DATE ('" + endPostDate + " 235959" +"', 'yyyy/MM/dd hh24miss')");
        }
        sb.append(" ORDER BY POST_TIME DESC");

        //System.out.println("SQL====="+sb.toString());

        TParm parm = new TParm(this.getDBTool().select(sb.toString()));

        tableWritePostGrid.setParmValue(parm,"CHK;URG_FLG_TITLE;POST_SUBJECT;POST_TIME;MESSAGE_NO;URG_FLG;POST_INFOMATION");

        //���ô浵��Ť��Ч
        btnWriteSave.setEnabled(false);

    }

    /**
     * ��չ���
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

        //ȡ���浵
        btnWriteSave.setEnabled(false);

    }

    /**
     * ȡ���ѷ����б�
     */
    public void onCancel() {

        tableWriteRecipient.clearSelection();
        btnCancel.setEnabled(false);
        btnDelete.setEnabled(false);

    }


    /**
     * ɾ�������߹�����Ϣ
     */
    public void onDeleteRecipientMessage() {
        //��ʾ��ȷ���Ƿ�ɾ����;
        //�ǣ���ͨ��messageNo��������USER_IDɾ����
        int selectRow = tableWriteRecipient.getSelectedRow();
        if (this.messageBox("ѯ��", "ȷ��ɾ����", 2) == 0) {
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
            //ͨ���û�ID�͹�����Ϣ��ɾ�����յ���¼
            TParm result = SYSPublishBoardTool.getInstance().
                deleteReceiveMessage(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("ɾ��ʧ�ܣ�");
                return;
            }

            this.messageBox("ɾ���ɹ���");

            this.afterDeleteRecipientMessage();

        }
        else {
            return;
        }

    }

    /**
     * ɾ�����յ���¼��
     */
    private void afterDeleteRecipientMessage() {
        //ɾ����
        final int row = tableWritePostGrid.getSelectedRow();
        this.onPostedMessTableClicked(row);
        btnCancel.setEnabled(false);
        btnDelete.setEnabled(false);

    }


    /**
     * �����ѷ��͹���Table_Write_PL�б��¼
     */
    private void onloadPostedMessTable() {
        onQuery();
    }

    /**
     * �����ѷ��͹���Table_Write_RL�������б��¼
     */
    private void onloadSendUsersTable(String messageNo) {
        String sql =
            "SELECT (CASE WHEN POST_TYPE='P' THEN '����' WHEN POST_TYPE='D' THEN '����' WHEN POST_TYPE='R' THEN '��ɫ' WHEN POST_TYPE='A' THEN 'ȫ��' END) POST_TYPE,";
        sql += "o.USER_NAME,p.USER_ID,MESSAGE_NO";
        sql += " FROM SYS_POSTRCV p left join SYS_OPERATOR o on p.USER_ID=o.USER_ID WHERE MESSAGE_NO = '"
            + messageNo + "' ORDER BY p.OPT_DATE DESC";
        TParm query = new TParm(getDBTool().select(sql));
        tableWriteRecipient.setParmValue(query,
                                         "POST_TYPE;USER_NAME;USER_ID;MESSAGE_NO");

    }

    /**
     * �ѷ�����ѡ���¼�
     */
    public void onSendUsersTableClicked(int row) {
        if (row < 0) {
            return;
        }
        btnCancel.setEnabled(true);
        btnDelete.setEnabled(true);
    }


    /**
     * �ѷ��͹���ѡ���¼�
     */
    public void onPostedMessTableClicked(int row) {
        //ͨ��messageNo������ѷ�����Ϣ��Ӧ�û�
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
        //���ݼ��ص�Table_Write_RL�������б�
        this.onloadSendUsersTable(messageNo);
        //�浵��Ť����,���޸Ĺ�������;
        btnWriteSave.setEnabled(true);

    }

    /**
     * ѡ��������
     */
    public void onPostType() {
        //����
        if (radioPostTypePerson.isSelected()) {
            this.clearPostTypes();
            radioPostTypePerson.setSelected(true);

            this.setEnableRecipientSelect(true);
            this.hideRecipientSelect();
            tfOperator.setVisible(true);
        }
        //����
        else if (radioPostTypeDept.isSelected()) {
            this.clearPostTypes();
            radioPostTypeDept.setSelected(true);

            this.setEnableRecipientSelect(true);
            this.hideRecipientSelect();
            tfDept.setVisible(true);

        }
        //��ɫ
        else if (radioPostTypeRole.isSelected()) {
            this.clearPostTypes();
            radioPostTypeRole.setSelected(true);

            this.setEnableRecipientSelect(true);
            this.hideRecipientSelect();
            tfRole.setVisible(true);
        }
        //����
        else if (radioPostTypeAll.isSelected()) {
            this.clearPostTypes();
            radioPostTypeAll.setSelected(true);
            this.setEnableRecipientSelect(false);

        }
        this.onRecipientSelected();

    }

    /**
     * �ռ���ѡ����¼�
     */
    public void onRecipientSelected() {
        //����
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
        //����
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
        //��ɫ
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
        //����
        else if (radioPostTypeAll.isSelected()) {
            receiveType = "A";
            btnSave.setEnabled(true);
        }

    }

    /**
     * �浵ǰ��������Ч�Լ�飻
     * @return boolean
     */
    private boolean checkPublishMessage() {
        //��鹫������;
        if (txtWriteSubject.getValue().toString().equals("")) {
            this.messageBox("���ⲻ��Ϊ�հף�");
            txtWriteSubject.grabFocus();
            return false;
        }
        else {
            if (txtWriteSubject.getValue().toString().length() > 200) {
                this.messageBox("���ⳤ�ȳ�����Χ��");
                txtWriteSubject.grabFocus();
                return false;
            }
        }
        //��鹫������;
        if (txtAreaPostInfo.getValue().toString().equals("")) {
            this.messageBox("���ݲ���Ϊ�հף�");
            txtAreaPostInfo.grabFocus();
            return false;
        }
        else {
            if (txtAreaPostInfo.getValue().toString().length() > 1000) {
                this.messageBox("���ݳ��ȳ�����Χ��");
                txtAreaPostInfo.grabFocus();
                return false;
            }
        }
        //����ռ��߱���ѡ��
        if (recipients.equals("") && !radioPostTypeAll.isSelected()) {
            this.messageBox("�ռ��߲���Ϊ�հף�");
            return false;
        }

        return true;
    }

    /**
     * ��ʼ������ؼ�
     */
    private void initControler() {
        //��ʼ���ؼ�
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



        //��ʼֵ
        btnWriteSave.setEnabled(false);
        //btnWriteDelete.setEnabled(false);
        btnSave.setEnabled(false);
        btnCancel.setEnabled(false);
        btnDelete.setEnabled(false);

        //�����¼�
        callFunction("UI|tPanel_Write|Table_Write_PL|addEventListener",
                     "Table_Write_PL->" + TTableEvent.CLICKED, this,
                     "onPostedMessTableClicked");

        callFunction(
            "UI|tPanel_Write|tPanel_Send|Table_Write_RL|addEventListener",
            "Table_Write_RL->" + TTableEvent.CLICKED, this,
            "onSendUsersTableClicked");

        // ��Table_RL��˫���������¼�
        callFunction("UI|tPanel_PL|Table_RL|addEventListener",
                     "Table_RL->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onReceiveMessTableDbClick");

    }

    /**
     * ����������
     */
    private void clearPostTypes() {
        radioPostTypePerson.setSelected(false);
        radioPostTypeDept.setSelected(false);
        radioPostTypeRole.setSelected(false);
        radioPostTypeAll.setSelected(false);
    }

    /**
     * ���ز�ͬ��������ռ���������
     */
    private void hideRecipientSelect() {
        tfOperator.setVisible(false);
        tfDept.setVisible(false);
        tfRole.setVisible(false);
    }

    /**
     * ȫ���ռ�����������Ч
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
     * �õ�ҳ����Table����
     * @param tag
     * @return
     */
    private TTable getTable(String tag) {
        return (TTable) callFunction("UI|" + tag + "|getThis");
    }

    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }


}
