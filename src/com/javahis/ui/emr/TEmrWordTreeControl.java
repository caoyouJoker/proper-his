package com.javahis.ui.emr;



import java.math.BigDecimal;
import java.util.UUID;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.wcomponent.util.TiString;
import com.javahis.ui.sys.orm.bean.EmrTreePO;
import com.javahis.ui.sys.orm.tools.SqlTool;
import com.javahis.ui.sys.orm.tools.Type;


/**
 *
 * @author whaosoft
 *
 */
public class TEmrWordTreeControl extends TControl{


	/**  */
	private final static String TAG_TREE = "tTree_0";
	/**  */
	private final static String TAG_CB_BL = "tComboBox_1";
	/**  */
	private final static String TAG_TFT_MB = "tTextFormat_0";
	/**  */
	private final static String TAG_CB_ZT = "tComboBox_3";
	/**  */
	private final static String TAG_TF_SORT = "tTextField_1";

    //
	private TEmrTreeOperate to = TEmrTreeOperate.getInstance();

	//
	private TComboBox cb_et;
	private TTextFormat tft_mb;
	private TComboBox cb_zt;
	private TTextField tf_xh;
	private TTreeNode treeRoot;
	private TTree tree;


	/**
	 *
	 */
	public void onInit() {

		cb_et = (TComboBox) getComponent(TAG_CB_BL);
		tft_mb = (TTextFormat) getComponent(TAG_TFT_MB);
		cb_zt = (TComboBox) getComponent(TAG_CB_ZT);
		tf_xh = (TTextField) getComponent(TAG_TF_SORT);
		tree = (TTree) this.getComponent(TAG_TREE);
		treeRoot = tree.getRoot();

		//
		doInitEmrType();
		doInitEmrTemplate();
		doInitEmrStatus();

		//
		treeRoot.removeAllChildren();
		treeRoot.setText("��ѡ��������");
		treeRoot.setType("Root");
		treeRoot.setID("0");
	}

	/**
	 *
	 */
	private void doInitEmrType(){

		cb_et.setParmMap("id:ID;name:NAME;");

		String sql = "select ID,NAME from EMR_NEW_TYPE";
		TParm parm =  new TParm( TJDODBTool.getInstance().select(sql));
		cb_et.setParmValue(parm);
		cb_et.setShowName(true);
		cb_et.setEditable(false);
	}

	/**
	 *
	 */
	private void doInitEmrTemplate(){

		String sql = "select EMT_FILENAME NAME, SUBCLASS_CODE ID from EMR_TEMPLET WHERE IPD_FLG = 'Y'";
		TParm parm =  new TParm( TJDODBTool.getInstance().select(sql));
		parm.insertData("NAME", 0, "");
		parm.insertData("ID", 0, "");

		//
		tft_mb.setFormatType("combo");
		tft_mb.setPopupMenuHeader("����,405");
		tft_mb.setPopupMenuWidth(430);
		tft_mb.setPopupMenuHeight(300);
		tft_mb.setPopupMenuFilter("ID,1;NAME,1");
		tft_mb.setValueColumn("ID");
		tft_mb.setShowColumnList("NAME");
		tft_mb.setHorizontalAlignment(2);
		tft_mb.setPopupMenuData(parm);
	}

	/**
	 *
	 */
	private void doInitEmrStatus(){

		cb_zt.setParmMap("id:ID;name:NAME;");
		TParm parm = new TParm();
 		parm.addData("NAME", "����");
 		parm.addData("NAME", "ģ��");
 		parm.addData("ID", "N");
 		parm.addData("ID", "Y");
 		parm.setCount(2);
 		//
 		cb_zt.setParmValue(parm);
 		cb_zt.setShowName(true);
 		cb_zt.setEditable(false);
	}

	/**
	 *
	 */
	public void onSelectEmrType(){

		String id = cb_et.getSelectedID();
		treeRoot.removeAllChildren();
		if( TiString.isEmpty(id) ){
			treeRoot.setText("��ѡ��������");
			return;
		}

		//
		treeRoot.setText(cb_et.getSelectedName());

		//
		to.doLoadTreeByTypeId( treeRoot,cb_et.getSelectedID() );

		//
		tree.update();
	}

	/**
	 *
	 */
	public void onSelectEmrStatus(){

		if( "Y".equals( cb_zt.getSelectedID() ) ){
			tft_mb.setShowDownButton(true);
		}else{
			tft_mb.setShowDownButton(false);
		}
	}

	/**
	 *
	 */
	public void onSave(){

        EmrTreePO et = new EmrTreePO();
        et.modifyState = Type.INSERT;
        et.id = UUID.randomUUID().toString();

        et.typeId = cb_et.getSelectedID();

        //
        TTreeNode tn = tree.getSelectNode();
        if( !this.doCheckTTreeNodeData(tn) ) return;
        et.parentId = tn.getID();

        //
        if( ! doCheckStatusData(et) ) return;

        //
		if( !this.doCheckTemplateData(et) ) return;

        //
        if( !this.doCheckSeqData(et) ) return;

        //
		try {
			String sql = SqlTool.getInstance().getSql(et);

			TJDODBTool.getInstance().update(sql);

		} catch (Exception e) {
			this.messageBox("����ʧ��!");
			throw new RuntimeException(e);
		}

		//
		doCLearAll();

		//
		onSelectEmrType();

		//
		this.messageBox("����ɹ�!");

	}

    /**
	 *
	 * @return
	 */
	private boolean doCheckTTreeNodeData(TTreeNode tn) {

		if (null == tn) {
			this.messageBox("��ѡ��ڵ�!");
			return false;
		}

		if ("��ѡ��������".equals(tn.getText())) {
			this.messageBox("����ѡ��������,��ѡ��ڵ�!");
			return false;
		}

		return true;
	}

    /**
	 *
	 * @param et
	 * @return
	 */
	private boolean doCheckStatusData(EmrTreePO et) {

		String zt = cb_zt.getSelectedID();
		if (TiString.isEmpty(zt)) {
			this.messageBox("��ѡ��ڵ�״̬!");
			return false;
		}

		et.tStatus = zt;

		return true;
	}

	/**
	 *
	 * @param et
	 * @return
	 */
    private boolean doCheckTemplateData(EmrTreePO et){

		String id = tft_mb.getValue()+"";
		String name = tft_mb.getText();
		et.name = name;

		//
    	if( "Y".equals( et.tStatus ) ){

        	String sql = "select 'X' X from EMR_TEMPLET WHERE EMT_FILENAME='"+name+"' AND SUBCLASS_CODE='"+id+"'";
    		TParm parm =  new TParm( TJDODBTool.getInstance().select(sql));

            int exist = parm.getCount();

            if( exist == -1 ){
            	this.messageBox("��ѡ����ȷ����ģ������!");
            	return false;
            }

            et.classCode = id;

    	}else{

    		et.classCode = null;
    	}

        return true;
    }

    /**
	 *
	 * @param et
	 * @return
	 */
    private boolean doCheckSeqData(EmrTreePO et){

    	String xh = tf_xh.getText();
        if( TiString.isEmpty(xh)) {
    		this.messageBox("���������!");
    		return false;
        }

        //
        try{
        	et.tSeq = new BigDecimal(xh);
        }catch (Exception e) {
    		this.messageBox("��ű���Ϊ����!");
    		return false;
		}


    	return true;
    }

    /**
     *
     */
    private void doCLearAll(){

    	tft_mb.setValue(null);
    	tft_mb.setText(null);

    	//
    	cb_zt.setSelectedIndex(-1);

    	//
    	tf_xh.setText(null);
    }

    /**
     *
     */
    public void onDelete(){

        TTreeNode tn = tree.getSelectNode();

        if( !this.doCheckTTreeNodeData(tn) ) return;

        //
        String id = tn.getID();
        if( id.equals("0") && tn.getType().equals("Root") ){
        	this.messageBox("������ɾ�����ڵ�!");
        	return;
        }

        //
        if( !this.doCheckNodeSub(id) ) return;

        //
        EmrTreePO et = new EmrTreePO();
        et.modifyState = Type.DELETE;
        et.id = id;

        //
		try {
			String sql = SqlTool.getInstance().getSql(et);

			TJDODBTool.getInstance().update(sql);

		} catch (Exception e) {
			this.messageBox("ɾ��ʧ��!");
			throw new RuntimeException(e);
		}

		//
		onSelectEmrType();

		//
		this.messageBox("ɾ���ɹ�!");

    }

    /**
     *
     * @param id
     * @return
     */
    private boolean doCheckNodeSub(String id){

    	String sql = "select 'X' X from EMR_NEW_TREE WHERE PARENT_ID='"+id+"'";
		TParm parm =  new TParm( TJDODBTool.getInstance().select(sql));

        int exist = parm.getCount();

        if( exist > 0 ){
        	this.messageBox("�˽ڵ���������ӽڵ�,����ɾ��!");
        	return false;
        }

    	return true;
    }



}
