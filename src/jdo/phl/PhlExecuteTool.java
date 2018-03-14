package jdo.phl;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.onw.ONWTool;

/**
 * <p>
 * Title: ������ִ��
 * </p>
 *
 * <p>
 * Description: ������ִ��
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class PhlExecuteTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static PhlExecuteTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return IndAgentTool
     */
    public static PhlExecuteTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhlExecuteTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PhlExecuteTool() {
        setModuleName("phl\\PHLExecuteModule.x");
        onInit();
    }

    /**
     * ��ѯ
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryExecute", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ������ִ�б���
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onPhlExecute(TParm parm, TConnection conn) {
        // ���ݼ��
        if (parm == null)
            return null;
        TParm result = new TParm();
        // ����ִ��ҽ��
        TParm orderParm = parm.getParm("ORDER_PARM");
        for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
            TParm inparm = orderParm.getRow(i);
            result = PhlOrderTool.getInstance().onUpdate(inparm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        // ���²���״̬
        TParm bedParm = parm.getParm("BED_PARM");
        result = PhlBedTool.getInstance().onUpdatePatStatus(bedParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // ���»�ʿ��ע
        for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
            TParm inparm = new TParm();
            if (!"".equals(orderParm.getValue("NS_NOTE", i))) {
                inparm.setData("CASE_NO", orderParm.getData("CASE_NO", i));
                inparm.setData("RX_NO", orderParm.getValue("ORDER_NO", i));
                inparm.setData("SEQ_NO", orderParm.getValue("SEQ_NO", i));
                inparm.setData("NS_NOTE", orderParm.getValue("NS_NOTE", i));
                result = ONWTool.getInstance().updateNS_NOTE(inparm, conn);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText()
                        + result.getErrName());
                    return result;
                }
            }
        }
        return result;
    }

}
