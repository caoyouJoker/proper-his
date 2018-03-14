package com.javahis.ui.sys.dictionary;

import java.util.Vector;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.DictionaryTool;

import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;

import jdo.sys.Operator;

import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;

public class TableEditControl extends TControl{
    private static final String TABLE = "Table";
    /**
     * 上级组编号
     */
    private String groupID;
    /**
     * 上级编号
     */
    private String parentID;
    /**
     * 当前动作
     */
    private String action;
    public void initRoot()
    {
        String s = (String)getParameter();
        if(s == null)
            s = "PERSON|SYS_SEX";
        String s1[] = StringTool.parseLine(s,"|");
        groupID = s1[0];
        parentID = s1[1];
    }
    public void onInit()
    {
        super.onInit();
        initRoot();
        //初始化数据
        initData();
        callFunction("UI|Table|addEventListener",TABLE + "->"+TTableEvent.CLICKED,this,"onTableClicked");
    }
    public void onInitReset()
    {
        initRoot();
        //初始化数据
        initData();
    }
    /**
     * 初始化数据
     */
    public void initData()
    {
        String name = getDicTool().getName(groupID,parentID);
        callFunction("UI|GROUP|setText",name);
        String title = (String)callFunction("UI|getTitle");
        callFunction("UI|setTitle",title + "-" + name);
        TParm result = getDicTool().getListAll(parentID);
        callFunction("UI|" + TABLE + "|setParmValue",result);
        onClear();
        int seq = 0;
        if (result.existData("SEQ")) {
			Vector vct = result.getVectorValue("SEQ");
			for (int i = 0; i < vct.size(); i++) {
				int a = TypeTool.getInt(vct.get(i));
				if (a > seq)
					seq = a;
			}
			this.setValue("SEQ", seq + 1);
		}
    }
    public void onTableClicked(int row)
    {
        if(row < 0)
            return;
        callFunction("UI|ID|setEnabled",false);
        callFunction("UI|delete|setEnabled",true);
        action = "EDIT";
    }
    /**
     * 得到字典工具类
     * @return DictionaryTool
     */
    public DictionaryTool getDicTool()
    {
        return DictionaryTool.getInstance();
    }
    /**
     * 清空
     */
    public void onClear()
    {
        clearValue("ID;NAME;ENG_DESC;TYPE;STATE;REMARK;PY1;PY2;SEQ;PARENT_ID;DATA;FLG;STA1_CODE;STA2_CODE;STA3_CODE");
        callFunction("UI|" + TABLE + "|clearSelection");
        callFunction("UI|ID|setEnabled",true);
        callFunction("UI|save|setEnabled",true);
        callFunction("UI|delete|setEnabled",false);
        action = "INSERT";
        TParm result = getDicTool().getListAll(parentID);
        int seq = 0;
        if (result.existData("SEQ")) {
			Vector vct = result.getVectorValue("SEQ");
			for (int i = 0; i < vct.size(); i++) {
				int a = TypeTool.getInt(vct.get(i));
				if (a > seq)
					seq = a;
			}
			this.setValue("SEQ", seq + 1);
		}
    }

    public void onSave() {
        TParm parm = new TParm();
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        if ("EDIT".equals(action)) {
            if (!emptyTextCheck("NAME"))
                return;
            if(updateFlg().getErrCode()<0){
            	messageBox("E0001");
            	return;
            }
            callFunction("UI|" + TABLE + "|setModuleParmUpdate", parm);
            if (! (Boolean) callFunction("UI|" + TABLE + "|onUpdate")) {
                messageBox("E0001");
                return;
            }

            String id = getText("ID");
            String name = getText("NAME");
            String type = getText("TYPE");
            //callFunction("UI|callEvent","UPDATE_ACTION",new Object[]{groupID,parentID,id,name,type},
            //             new String[]{"String","String","String","String","String"});
            messageBox("P0001");
        }
        else {
            if (parentID.length() == 0)
                return;
            if (!emptyTextCheck("ID,NAME"))
                return;
            if(updateFlg().getErrCode()<0){
            	messageBox("E0001");
            	return;
            }
            parm.setData("GROUP_ID", parentID);
            callFunction("UI|" + TABLE + "|setModuleParmInsert", parm);
            if (! (Boolean) callFunction("UI|" + TABLE + "|onInsert")) {
                messageBox("E0002");
                return;
            }
            String id = getText("ID");
            String name = getText("NAME");
            String type = getText("TYPE");
            //callFunction("UI|callEvent","INSERT_ACTION",new Object[]{groupID,parentID,id,name,type},
            //             new String[]{"String","String","String","String","String"});
            messageBox("P0002");
            callFunction("UI|ID|setEnabled", false);
            callFunction("UI|delete|setEnabled",true);
            action = "EDIT";
        }
        TParm result = getDicTool().getListAll(parentID);
        callFunction("UI|" + TABLE + "|setParmValue",result);
        onClear();
    }
    /**
     * 删除
     */
    public void onDelete()
    {
        int row = (Integer)callFunction("UI|Table|getSelectedRow");
        if(row < 0)
            return;
        String id = getText("ID");
        if(!(Boolean)callFunction("UI|" + TABLE + "|onDelete"))
        {
            messageBox("E0003");
            return;
        }
        //callFunction("UI|callEvent","DELETE_ACTION",new Object[]{groupID,parentID,id},
        //             new String[]{"String","String","String"});
        messageBox("P0003");
        onClear();
    }
    
    /**
     * 名称回车事件
     */
    public void onUserNameAction(){
    	String userName = getValueString("NAME");
    	String py = TMessage.getPy(userName);
    	setValue("PY1", py);
    	((TTextField) getComponent("PY1")).grabFocus();
    }
    
    /**
     * 查询
     */
    public void onQuery(){
    	String id = getValueString("ID");
    	TParm result = new TParm();
    	if (id.length() == 0)
    		result = getDicTool().getListAll(parentID);
    	else
    		result = getDicTool().query(parentID, id+"%");
    	callFunction("UI|" + TABLE + "|setParmValue",result);
    }
    /**
     * 修改默认注记 将其它的默认注记删除
     * @return
     */
    private TParm updateFlg(){
    	TParm tableParm =getTable(TABLE).getParmValue();
    	boolean flg=false;
    	for (int i = 0; i < tableParm.getCount(); i++) {
    		if(tableParm.getBoolean("FLG",i)){
    			flg=true;
    			break;
    		}
		}
    	if(!flg)
    		return new TParm();
    	TParm result = TIOM_AppServer.executeAction("action.sys.SYSPopedomAction",
				"updateSysDictionaryFlg", tableParm);
    	if (result.getErrCode()<0) {
			return result;
		}
    	return new TParm();
    }
    private TTable getTable(String table){
    	return (TTable)this.getComponent(table);
    }
}
