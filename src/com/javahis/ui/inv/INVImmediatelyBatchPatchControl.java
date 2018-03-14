package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TComboBox;
import jdo.sys.Operator;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TCheckBox;
import java.util.Vector;
import jdo.inv.INVSQL;
import com.javahis.system.combo.TComboOrgCode;
import com.dongyang.util.TMessage;
import jdo.sys.SystemTool;
import com.javahis.system.textFormat.TextFormatINVOrg;
import com.dongyang.util.TypeTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: ��������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy
 * @version 1.0
 */
public class INVImmediatelyBatchPatchControl
    extends TControl {
    public INVImmediatelyBatchPatchControl() {
    }



    /**
     * ��ʼ������
     */
    public void onInit() {
        
    	this.setValue("DEPT_CODE",Operator.getDept());
    }
    
    public void onBatch(){
    	
    	
    	TParm patchParm = new TParm();
        patchParm.setData("PATCH_CODE", "140114000001");
        patchParm.setData("PATCH_SRC", "action.inv.INVImmediatelyBatchPatchAction"); 
        patchParm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
        TParm result = TIOM_AppServer.executeAction(
            "action.sys.SYSPatchAction", "onImmeServerAction", patchParm);
        if (result == null) {
            this.messageBox("ִ��ʧ��");
            return;
        }
        if(result.getErrCode()<0){
            this.messageBox(result.getValue("MESSAGE"));
            return;
        }
        this.messageBox("ִ�гɹ�");
    }

    /**
     * �õ�CheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }


}
