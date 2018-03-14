package action.sys;

import jdo.sys.BuildSqlTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 *
 * <p>Title: 自定义查询保存动作</p>
 *
 * <p>Description:自定义查询保存动作</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p> Company: </p>
 *
 * @author ehui 20090817
 * @version 1.0
 */
public class BuildSqlAction extends TAction{
	public TParm onSave(TParm parm){
		////System.out.println("in actrion");
		TParm result=new TParm();
		//取得链接
		TConnection conn = getConnection();
		/**
		 * parm.setData("TEMPLATE",template);
		parm.setData("SELECT",selectParm);
		parm.setData("WHERE",whereParm);
		parm.setData("ORDERBY",orderbyParm);
		parm.setData("GROUPBY",groupbyParm);
		 */
		TParm select=parm.getParm("SELECT");
		TParm template=parm.getParm("TEMPLATE");
		TParm orderby=parm.getParm("ORDERBY");
		TParm groupby=parm.getParm("GROUPBY");
		TParm where=parm.getParm("WHERE");

		String tableId=template.getValue("TABLE_ID");
		String tempCode=template.getValue("TEMP_CODE");
		////System.out.println("in Action.tempCode========="+tempCode);

		////System.out.println("template============"+template);
		if(tableId==null||tempCode==null||tableId.trim().length()<1||tempCode.trim().length()<1){
			conn.rollback();
			conn.close();
			result.setErrCode(-1);
			////System.out.println("inParameter is nulllllllllllll");
			return result;
		}
		//保存SYS_VIEW_TEMPLATE
		result=BuildSqlTool.getInstance().onSaveTemplate(template, tableId, tempCode, conn);
		if(result.getErrCode()<0){
			conn.rollback();
			conn.close();
			return result;
		}

		//保存SYS_VIEW_COLUMN
		result=BuildSqlTool.getInstance().onSaveSelect(select, tableId, tempCode, conn);
		if(result.getErrCode()<0){
			conn.rollback();
			conn.close();
			return result;
		}

		//保存SYS_VIEW_ORDERBY
		if(orderby!=null&&orderby.getCount()>0){
			result=BuildSqlTool.getInstance().onSaveOrderBy(orderby, tableId, tempCode, conn);
			if(result.getErrCode()<0){
				conn.rollback();
				conn.close();
				return result;
			}
		}else{
			////System.out.println("order by is null");
		}


		//保存SYS_VIEW_GROUPBY
		if(groupby!=null&&groupby.getCount()>0){
			result=BuildSqlTool.getInstance().onSaveGroupBy(groupby, tableId, tempCode, conn);
			if(result.getErrCode()<0){
				conn.rollback();
				conn.close();
				return result;
			}
		}else{
			////System.out.println("groupby by is null");
		}


		//保存SYS_VIEW_WHERE_DETAIL
		if(where!=null&&where.getCount()>0){
			result=BuildSqlTool.getInstance().onSaveWhere(where, tableId, tempCode, conn);
			if(result.getErrCode()<0){
				conn.rollback();
				conn.close();
				return result;
			}
		}else{
			////System.out.println("where by is null");
		}

		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * 删除所有SYS_VIEW_表
	 * @param parm
	 * @return
	 */
	public TParm onDelete(TParm parm){
		////System.out.println("in Delete=================");
		TParm result=new TParm();
		if(parm==null){
			result.setErrCode(-1);
			return result;
		}
		String tempCode=parm.getValue("TEMP_CODE");
		if(tempCode==null||tempCode.trim().length()<1){
			result.setErrCode(-1);
			return result;
		}
		//取得链接
		TConnection conn = getConnection();
		result=BuildSqlTool.getInstance().onDelete(tempCode, conn);
		if(result==null||result.getErrCode()<0){
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}
}
