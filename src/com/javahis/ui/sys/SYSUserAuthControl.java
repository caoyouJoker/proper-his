package com.javahis.ui.sys;





import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTreeEvent;
import com.javahis.util.StringUtil;

/**
 *
 * @author whaosoft
 *
 */
public class SYSUserAuthControl extends TControl {

	private static String TREE = "tTree_0";
	private TTreeNode treeRoot;
    private TParm insertData = new TParm();
    private TParm deleteData = new TParm();
	/** 初始数据 */
	private TParm initParm = new TParm();
	private String userId = null;

	/**
	 *
	 */
	public void onInit() {

		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			initParm = (TParm) obj;
			this.onInitData();
		}
	}

	/**
	 *
	 */
	private void onInitData() {

		this.setValue("username", initParm.getValue("USER_NAME"));

		userId = initParm.getValue("USER_ID");

		this.onInitTree();
	}

	/**
	 *
	 */
	private void onInitTree() {

		String roleId = initParm.getValue("ROLE_ID");

        //单击选中树项目
        addEventListener(TREE + "->" + TTreeEvent.CLICKED,"onTreeClicked");

		//
		treeRoot = (TTreeNode) callMessage("UI|" + TREE + "|getRoot");
		if (treeRoot == null) return;

		treeRoot.setText("系统权限");
		treeRoot.setGroup("ROOT");
		treeRoot.setID("SYS_SUBSYSTEM");
		treeRoot.setType("PATH");
		treeRoot.setValue("ROOT:SYS_SUBSYSTEM");
		treeRoot.removeAllChildren();

		//
		TParm authData = this.getUserAuth();

		//
		TParm parm = new TParm();
		parm.setData("ROLE_CODE", roleId);
		TParm result = TIOM_AppServer.executeAction(
				"action.sys.SYSRolePopedomAction", "queryRoleTree", parm);
		downloadRootTree(treeRoot, result, "SYS_SUBSYSTEM",authData);
	}

	/**
	 * 下载树数据
	 * @param parentNode TTreeNode
	 * @param parm TParm
	 * @param group String
	 * @param authData TParm
	 */
	private void downloadRootTree(TTreeNode parentNode, TParm parm, String group,TParm authData) {
		if (parentNode == null) return;

		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			String id = parm.getValue("ID", i);
			String name = parm.getValue("NAME", i);
			String type = parm.getValue("TYPE", i);
			String data = parm.getValue("DATA", i);
			String value = parm.getValue("VALUE", i);
			TParm child = parm.getParm("CHILD", i);
			TTreeNode node = new TTreeNode(name, type);
		 	node.setGroup(group);
			node.setID(id);
			node.setValue(group + ":" + id);
			node.setData(data);

            //
			this.doProcessAuthTree(authData, id, group,value, node);

			/** //测试用-角色带的都不显示checkbox

			//角色自带的不显示checkbox
			if( !"Y".equals(value) ){
				node.setShowType("checkbox");
			}

			node.setValue(value);
            */

			parentNode.add(node);
			downloadRootTree(node, child, id,authData);
		}
	}

    /**
     *
     * @param authData
     * @param id
     * @param group
     * @param value
     * @param node
     */
	private void doProcessAuthTree(TParm authData, String id, String group,
			String value, TTreeNode node) {

		int count = authData.getCount();

		if( count >0 ){

			for( int i = 0; i < count; i++ ){

				String tmpCode = authData.getValue("CODE", i);
				String tmpGroup = authData.getValue("GROUP_CODE", i);

	            //
				if( tmpCode.equals(id) && tmpGroup.equals(group) ){
					node.setShowType("checkbox");
					node.setValue("Y");
				}
				//角色数据
				else{
					//角色自带的不显示checkbox
					if( !"Y".equals(value) ){
						node.setShowType("checkbox");
					}
				}
			}

		}else{

			//角色自带的不显示checkbox
			if( !"Y".equals(value) ){
				node.setShowType("checkbox");
			}

		}
	}

	/**
	 *
	 * @param parm
	 */
	public void onTreeClicked(Object parm) {

        TTreeNode node = (TTreeNode) parm;
        if (node == null) return;

        //角色的数据不可以操作
        if( StringUtil.isNullString(node.getShowType()) ) return;

        String group = node.getGroup();
        String id = node.getID();
        if(TCM_Transform.getBoolean(node.getValue()))
            addPopedom(group,id);
        else
            removePopedom(group,id);

        /*
        this.messageBox_(insertData);
        this.messageBox_(deleteData);
        */

	}

	/**
	 *
	 * @param group
	 * @param id
	 */
	private void addPopedom(String group,String id)
    {
		deleteData(deleteData, group, id);

		if(doProcessInsertData(group,id)) return;

        if(existData(insertData,group,id))  return;

        insertData.addData("GROUP_CODE",group);
        insertData.addData("CODE",id);
        insertData.setData("ACTION","COUNT",insertData.getCount("CODE"));
    }

	/**
	 *
	 * @param group
	 * @param id
	 */
    private void removePopedom(String group, String id) {

		deleteData(insertData, group, id);

		if (existData(deleteData, group, id)) return;

		deleteData.addData("GROUP_CODE", group);
		deleteData.addData("CODE", id);
		deleteData.setData("ACTION","COUNT",deleteData.getCount("CODE"));
	}

    /**
     * 存在要更新的数据
     * @param parm TParm
     * @param group String
     * @param id String
     * @return boolean
     */
    private boolean existData(TParm parm, String group, String id) {
		if (parm == null) return false;
		int count = parm.getData().size();
		for (int i = 0; i < count; i++)
			if (group.equals(parm.getValue("GROUP_CODE", i))
					&& id.equals(parm.getValue("CODE", i)))
				return true;
		return false;
	}

    /**
	 * 删除数据
	 *
	 * @param parm
	 *            TParm
	 * @param group
	 *            String
	 * @param id
	 *            String
	 */
	private void deleteData(TParm parm, String group, String id) {
		if (parm == null) return;
		int count = parm.getData().size();
		for (int i = 0; i < count; i++)
			if (group.equals(parm.getValue("GROUP_CODE", i))
					&& id.equals(parm.getValue("CODE", i))) {
				parm.removeRow(i);
				return;
			}
	}

	/**
	 *
	 */
	public void onSave(){

        TParm parm = new TParm();
        parm.setData("INSERT",insertData.getData());
        parm.setData("DELETE",deleteData.getData());
        parm.setData("USERID",userId );

        TParm result = TIOM_AppServer.executeAction("action.sys.SYSUserAuthAction", "onSaveAuth", parm);

        if(result.getErrCode() < 0) {
            err(result.getErrCode() + result.getErrText());
            messageBox("E0001");
            return;
        }

        //
        insertData = new TParm();
        deleteData = new TParm();

        //
        messageBox("P0001");
	}

	/**
	 *
	 * @param userId
	 * @return
	 */
    private TParm getUserAuth(){

    	String sql = "select GROUP_CODE ,AUTH_CODE AS CODE from SYS_USER_AUTH " +
    			"where USER_ID ='"+ userId + "' ORDER BY GROUP_CODE";
    	TParm result = new TParm( TJDODBTool.getInstance().select( sql ) );
    	return result;
    }

    /**
     *
     * @param group
     * @param id
     * @return
     */
    private boolean doProcessInsertData(String group,String id){

    	int i = this.getUserAuth(group, id);

    	if( i>0 ){
    		return true;
    	}

    	return false;
    }

    /**
     *
     * @param group
     * @param id
     * @return
     */
    private int getUserAuth(String group,String id){

    	String sql = "select AUTH_CODE from SYS_USER_AUTH " +
    			"where USER_ID ='"+ userId + "' AND" +
    			" AUTH_CODE ='"+id+"' AND" +
    			" GROUP_CODE ='"+group+"'";
    	return  new TParm( TJDODBTool.getInstance().select( sql ) ).getCount();
    }

}
