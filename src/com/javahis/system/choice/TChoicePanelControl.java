package com.javahis.system.choice;

import com.dongyang.control.TControl;
import jdo.sys.OperatorTool;
import com.dongyang.util.TSystem;
import jdo.sys.MessageTool;
import jdo.sys.SYSRegionTool;
import com.dongyang.ui.TComponent;
import com.dongyang.config.TRegistry;
import com.dongyang.config.TConfig;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.SYSOperatorTool;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>
 * Title: ��¼��������
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author lzk 2008.9.26
 * @version 1.0
 */
public class TChoicePanelControl extends TControl {
    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        TSystem.setObject("MessageObject", new MessageTool());
        String defaultRegion = SYSRegionTool.getInstance().getRegionByIP(
                OperatorTool.getInstance().getUserIP());
        this.callFunction("UI|REGION|setValue", defaultRegion);
        TComponent com = (TComponent) callFunction("UI|getParentComponent");

        //Ϊ��ֱ��CS��¼����DEBUG
        if( null!= com ){
        	  com.callFunction("addEventListener", "start", this, "start");
        }

        String userID = TRegistry
                .get("HKEY_CURRENT_USER\\Software\\JavaHis\\Login\\UserID");
        String language = TRegistry
                .get("HKEY_CURRENT_USER\\Software\\JavaHis\\Login\\Language");
        TSystem.setObject("ZhFontSizeProportion", StringTool.getDouble(TConfig
                .getSystemValue("ZhFontSizeProportion")));
        TSystem.setObject("EnFontSizeProportion", StringTool.getDouble(TConfig
                .getSystemValue("EnFontSizeProportion")));

        if (userID != null) {
            setValue("USER_ID", userID);
            callFunction("UI|DEPT|onQuery");
            onDefDept();
            callFunction("UI|STATION|onQuery");
            onDefStation();
            //============add by wanglong 20131122
            TParm optParm = SYSOperatorTool.getInstance().selectdata(userID);
            if (optParm.getErrCode() < 0) {
                this.messageBox(optParm.getErrText());
                return;
            }
            if (optParm.getCount("REGION_CODE") <= 0) {
                this.messageBox("�޴��û�");
                return;
            }
            this.callFunction("UI|USER_NAME|setValue", optParm.getValue("USER_NAME", 0));
            //===========add end
        }
        if (language == null)
            language = "zh";
        setValue("languageCombo_0", language);
        onChangeLanguage();
    }

    /**
     * ��ʼ
     */
    public void start() {
        if (getText("USER_ID").length() > 0)
            grabFocus("PASSWORD");
        else
            grabFocus("USER_ID");
    }

    /**
     * �����û���Ĭ��������
     */
    public void onDefDept() {
        String defDept = OperatorTool.getInstance().getMainDept(
                getText("USER_ID"));
        this.callFunction("UI|DEPT|setValue", defDept);
    }

    /**
     * �����û���Ĭ�ϲ���
     */
    public void onDefStation() {
        String defDept = OperatorTool.getInstance().getMainStation(
                getText("USER_ID"));
        this.callFunction("UI|STATION|setValue", defDept);
        // ============ add by wanglong 20131122
        TParm optParm = SYSOperatorTool.getInstance().selectdata(getText("USER_ID"));
        if (optParm.getErrCode() < 0) {
            this.messageBox(optParm.getErrText());
            return;
        }
        if (optParm.getCount("REGION_CODE") <= 0) {
            this.messageBox("�޴��û�");
            return;
        }
        this.callFunction("UI|USER_NAME|setValue", optParm.getValue("USER_NAME", 0));
        // ============ add end
    }

    /**
     * ��¼
     */
    public void onLogin() {
        String optCode = this.getValueString("USER_ID");
        TParm optParm = SYSOperatorTool.getInstance().selectdata(optCode);
        if (optParm.getErrCode() < 0) {
            this.messageBox(optParm.getErrText());
            return;
        }
        /**
         * if (optParm.getCount("REGION_CODE") <= 0) { this.messageBox("�޴��û�");
         * return; } String optRegion = optParm.getValue("REGION_CODE", 0); if
         * (!optRegion.equals(this.getValue("REGION"))) {
         * this.messageBox("��¼�������û��������򲻷�!"); return; }
         **/

        String userID = getText("USER_ID");
        if (userID == null || userID.length() == 0) {
            messageBox_("�������û�����");
            callFunction("UI|USER_ID|grabFocus");
            return;
        }

        // $$====== modified by lx 2012/03/16 �޸� �ǹ���Ա��ɫ������ɿ�======= $$//
        TParm roleParm = new TParm(TJDODBTool.getInstance().select(
                "SELECT ROLE_ID FROM SYS_OPERATOR WHERE USER_ID='" + optCode
                        + "'"));
        //System.out.println("=======roleParm=========" + roleParm);
        if (!roleParm.getValue("ROLE_ID", 0).equals("ADMIN")) {

            if (optParm.getCount("REGION_CODE") <= 0) {
                this.messageBox("�޴��û�");
                return;
            }
            String optRegion = optParm.getValue("REGION_CODE", 0);
            if(!optRegion.equals("")){
                if (!optRegion.equals(this.getValue("REGION"))) {
                    this.messageBox("��¼�������û��������򲻷�!");
                    return;
                }
            }

        }
        // $$======= modified by lx 2012/03/16 �޸��ǹ���Ա��ɫ������ɿ�end=== $$//

        String resgion = getText("REGION");
        // if(resgion == null || resgion.length() == 0)
        // {
        // messageBox_("�������¼����");
        // callFunction("UI|REGION|grabFocus");
        // return;
        // }
        /*
         * String dept = getText("DEPT"); if(dept == null || dept.length() == 0)
         * { messageBox_("�������¼����"); callFunction("UI|DEPT|grabFocus"); return;
         * }
         */
        // ==========pangben modify 20110428 start
        TParm resultPwd = OperatorTool.getInstance().getUserInfo(
                getText("USER_ID"));
        // ==========pangben modify 20110428 stop

        //System.out.println("======resultPwd========" + resultPwd);

        TParm result = OperatorTool.getInstance().login(getText("USER_ID"),
                getText("PASSWORD"), (String) getValue("REGION"),
                (String) getValue("DEPT"), (String) getValue("STATION"));
        if (result.getErrCode() != 0) {
            messageBox(result.getErrText());
            if (result.getErrCode() == -2) {
                openDialog("%ROOT%\\config\\system\\choice\\RegistryDialog.x");
                return;
            }
            callFunction("UI|PASSWORD|grabFocus");
            return;
        }
        // ������֤��Ч����
        // ==========pangben modify 20110428 start
        // ========���Ҵ��û�����������ʱ���Լ���ֹʱ��
        Timestamp loginTime = resultPwd.getTimestamp("PWD_STARTDATE", 0);
        Object obj = null;
        if (null == loginTime || loginTime.equals("")) {
            this.messageBox("�û���һ�ε�¼,���޸�����");
            // ��һ�ε�¼Ҫ���û���������,��ʾ�����������
            this.openDialog("%ROOT%\\config\\sys\\SYSUpdatePassword.x", "Y");
        }
        // �û���һ�ε�¼�����X�����Ե�½
        resultPwd = OperatorTool.getInstance().getUserInfo(getText("USER_ID"));
        loginTime = resultPwd.getTimestamp("PWD_STARTDATE", 0);
        if (null == loginTime || loginTime.equals("")) {
            this.messageBox("�û���һ�ε�¼,���޸�����");
            return;
        }
        if (resultPwd != null) {
            // ����ʧЧ�����Ե�¼
            if (OperatorTool.getInstance().affecttedTimePwd(resultPwd)) {
                this.messageBox("���볬����Ч�ڣ������Ա����������");
                return;
            }
            if (null != loginTime && !loginTime.equals("")) {

                // ���볬����������ʾ�û���������
                TParm parm = new TParm();
                // У����������
                if (Operator.getRegion() != null
                        && Operator.getRegion().length() > 0) {
                    parm = new TParm(TJDODBTool.getInstance().select(
                            "SELECT DETECTPWDTIME FROM SYS_REGION WHERE REGION_CODE='"
                                    + Operator.getRegion() + "'"));
                } else
                    parm.setData("DETECTPWDTIME", 0, 999);// û�������¼Ĭ��У������999��
                if (OperatorTool.getInstance().afterThreeMonthPwd(resultPwd,
                        parm)) {
                    this.messageBox("�����Ѿ�����" + parm.getInt("DETECTPWDTIME", 0)
                            + "��û�и���,���û�����������");
                }
            }
        }
        // ==========pangben modify 20110428 end
        String language = getText("languageCombo_0");
        if (language == null || language.length() == 0)
            language = "zh";
        TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\Login\\UserID",
                getText("USER_ID"));
        TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\Login\\Language",
                language);
        callFunction("UI|P1|visible", false);
        TSystem.setObject("Language", language);
        openWindow("%ROOT%\\config\\main\\SystemMain.x");
    }

    /**
     * ˫��֧��
     */
    public void onChangeLanguage() {
        callFunction("UI|changeLanguage", getValue("languageCombo_0"));
    }

    /**
     * ���õ�¼��Ա��������
     */
    public void selRegion() {
        String optCode = this.getValueString("USER_ID");
        TParm optParm = SYSOperatorTool.getInstance().selectdata(optCode);
        if (optParm.getErrCode() < 0) {
            this.messageBox(optParm.getErrText());
            return;
        }
        if (optParm.getCount("REGION_CODE") <= 0) {
            this.messageBox("�޴��û�");
            return;
        }
        this.callFunction("UI|USER_NAME|setValue", optParm.getValue("USER_NAME", 0));// add by wanglong 20131122
        String optRegion = optParm.getValue("REGION_CODE", 0);
        this.callFunction("UI|REGION|setValue", optRegion);
        this.grabFocus("PASSWORD");
    }
}