package com.javahis.ui.emr;

import java.util.List;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTreeNode;
import com.javahis.ui.sys.orm.bean.EmrTreePO;
import com.javahis.ui.sys.orm.bean.EmrTypePO;
import com.javahis.ui.sys.orm.ext.QueryToolExt;


/**
 *
 * @author whaosoft
 *
 */
public class TEmrTreeOperate {

	//
	private QueryToolExt qt = QueryToolExt.getInstance();

    //
    private final String WRITE_NO = "Œ¥ÃÓ–¥-";
    private final String WRITE_YES = "“—ÃÓ–¥-";
    private final String ROOT_ID = "0";
    protected final String TEMPLATE = "TEMPLATE";

	/**
	 *
	 * @param treeRoot
	 * @param typeId
	 */
	public void doLoadTreeByTypeId(TTreeNode treeRoot,String typeId){

		String sql = "select ID,NAME,PARENT_ID,CLASS_CODE ";
		sql += " from EMR_NEW_TREE WHERE TYPE_ID ='"+typeId+"' ORDER BY PARENT_ID,T_SEQ ASC";

		this.doLoadTree(treeRoot,sql,null);
	}

	/**
	 *
	 * @param apo
	 * @param all
	 * @return
	 */
	private TTreeNode treeRecursive(TTreeNode treeRoot,EmrTreePO et,List<EmrTreePO> all){

		TTreeNode node = new TTreeNode();

		node.setID(et.id);
		node.setText(et.name);

       //
		for( EmrTreePO po:all ){

			if( po.parentId.equals( et.id ) ){

				TTreeNode sub_node = this.treeRecursive(treeRoot,po, all);

				//
				if( !et.id.equals(ROOT_ID) ){
					node.add(sub_node);
				}else{
					treeRoot.add(sub_node);
				}
			}
		}

		return node;
	}

    /**
     *
     * @param treeRoot
     * @param typeName
     * @param caseNo
     * @param MrNo
     */
	public void doLoadTreeByTypeName(TTreeNode treeRoot, String typeName,
			String caseNo, String MrNo) {

		TParm fi = this.getFileIndex(caseNo, MrNo);

        this.doLoadTreeByTypeName(treeRoot, typeName, fi);
	}

	/**
	 *
	 * @param caseNo
	 * @param MrNo
	 * @return
	 */
    private TParm getFileIndex(String caseNo, String MrNo){


		String sql = " select SUBCLASS_CODE,max(FILE_SEQ) from EMR_FILE_INDEX ";
		sql += " WHERE CASE_NO = '"+caseNo+"' AND MR_NO ='"+MrNo+"' ";
		sql += "group by SUBCLASS_CODE,FILE_SEQ ";

		return new TParm( TJDODBTool.getInstance().select(sql) );
    }

	/**
	 *
	 * @param treeRoot
	 * @param typeName
	 * @param fi
	 */
	private void doLoadTreeByTypeName(TTreeNode treeRoot,String typeName,TParm fi){

		String sql = "select * from EMR_NEW_TYPE WHERE NAME ='"+typeName+"'";
		EmrTypePO et = qt.queryOneBySql(sql, EmrTypePO.class);

		this.doLoadTreeByTypeId(treeRoot, et.id,fi);
	}

    /**
     *
     * @param treeRoot
     * @param typeId
     * @param fi
     */
	private void doLoadTreeByTypeId(TTreeNode treeRoot,String typeId,TParm fi){

		String sql = "select ID,NAME,PARENT_ID,CLASS_CODE,T_STATUS ";
		sql += " from EMR_NEW_TREE WHERE TYPE_ID ='"+typeId+"' ORDER BY PARENT_ID,T_SEQ ASC";

		this.doLoadTree(treeRoot,sql,fi);
	}

	/**
	 *
	 * @param treeRoot
	 * @param sql
	 * @param fi
	 */
    private void doLoadTree(TTreeNode treeRoot, String sql,TParm fi) {

		List<EmrTreePO> list = qt.queryBySql(sql, EmrTreePO.class);

		EmrTreePO et = new EmrTreePO(ROOT_ID, treeRoot.getName());

		if( null!=fi ){
			this.treeRecursive(treeRoot, et, list,fi);
		}else{
			this.treeRecursive(treeRoot, et, list);
		}
	}

	/**
	 *
	 * @param treeRoot
	 * @param et
	 * @param all
	 * @param fi
	 * @return
	 */
	private TTreeNode treeRecursive(TTreeNode treeRoot,EmrTreePO et,List<EmrTreePO> all,TParm fi){

		TTreeNode node = new TTreeNode();

		node.setID(et.id);

		//
		if( "Y".equals(et.tStatus) ){
	        if( 0< fi.getCount() ){
	        	boolean jj = false;
	    		for( int i=0;i<fi.getCount();i++){

	    			String classCode = fi.getData("SUBCLASS_CODE", i)+"";
	    			if( classCode.equals(et.classCode) ){
	    				node.setText(WRITE_YES+et.name);

	    				jj = true;
	    				break;
	    			}
	    		}
	    		if( !jj ){
	    			node.setText(WRITE_NO+et.name);
	    		}

	        }else{
	        	node.setText(WRITE_NO+et.name);
	        }

	        node.setValue(et.classCode);
	        node.setShowType(TEMPLATE);

		} else{
			node.setText(et.name);
		}

        //
		for( EmrTreePO po:all ){

			if( po.parentId.equals( et.id ) ){

				TTreeNode sub_node = this.treeRecursive(treeRoot,po, all,fi);

				//
				if( !et.id.equals(ROOT_ID) ){
					node.add(sub_node);
				}else{
					treeRoot.add(sub_node);
				}
			}
		}

		return node;
	}

	// ** singleton ** //

	/**
	 *
	 * @return
	 */
	static public TEmrTreeOperate getInstance(){

		return TEmrTreeOperateSub.to;
	}

	/** */
	private TEmrTreeOperate(){}

	/** */
	static private class TEmrTreeOperateSub {
		static private TEmrTreeOperate to = new TEmrTreeOperate();
	}

}
