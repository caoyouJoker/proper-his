package com.javahis.ui.sys;

import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import jdo.sys.JbpmTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;

/**
 *
 * <p>Title: 消息框</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author EHUI 2009.04.19
 * @version 1.0
 */
public class SysMessage
    extends TControl {
    JEditorPane html;
    TPanel urlPnl;
    TTable table;
    TTable taskList;
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        new Thread(){
            public void run()
            {
                initComponent();
            }
        }.start();
    }

    /**
     * 行点击事件
     */
    public void onClick() {
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();
        String columnName = table.getParmMap(column);
        if ("FILE_NAME".equalsIgnoreCase(columnName)) {

            try {
//				Runtime.getRuntime().exec("D:\\adobe\\Reader\\AcroRd32.exe e:\\cancerCase.pdf");
                Runtime.getRuntime().exec("cmd /c start e:\\cancerCase.pdf");
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void initComponent() {
        /*
         * {PREVIOUS_ACTOR_ID=[null],
         *  ACTOR_ID=[liudy],
         *  TOKEN=[Token(/)],
         *  END=[null],
         *  NAME=[评估病历借阅申请],
         *   START=[null],
         *    VARIABLES=[{CASE NO=1234567890,
         *    comment=null}], ID=[89],
         *     CREATE=[2009-05-12 17:11:41.578]
         */
        table = (TTable)this.getComponent("TABLE");
        taskList = (TTable)this.getComponent("TASK_LIST");
        TParm parmList = JbpmTool.findTaskInstances("liudy");
        if (parmList == null) {
            //System.out.println("parmlist is null");
            return;
        }
        taskList.setParmValue(parmList);
        //JbpmTool.nextTask("liudy",0);
    }

    public void initProcessDefinition() {
    }

    public HyperlinkListener createHyperLinkListener() {
        return new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (e instanceof HTMLFrameHyperlinkEvent) {
                        ( (HTMLDocument) html.getDocument()).
                            processHTMLFrameHyperlinkEvent(
                                (HTMLFrameHyperlinkEvent) e);
                    }
                    else {
                        try {
                            html.setPage(e.getURL());
                        }
                        catch (IOException ioe) {
                            System.out.println("IOE: " + ioe);
                        }
                    }
                }
            }
        };
    }

    public void onAddMsg() {

    }

    public void onWorkFlow() {
        try {
            Runtime.getRuntime().exec("\"" + System.getenv("ProgramFiles") +
                                      "\\Internet   Explorer\\IEXPLORE.EXE\"");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        com.javahis.util.JavaHisDebug.initClient();
        TParm parmList = JbpmTool.findTaskInstances("liudy");
        //System.out.println("parmlist" + parmList);
    }
}
