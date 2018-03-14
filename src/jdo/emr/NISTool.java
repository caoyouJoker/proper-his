package jdo.emr;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jdo.bms.BMSApplyDTool;


import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class NISTool extends TJDOTool{
	
	public static NISTool instanceObject;
	
	/**
     * 得到实例
     *
     * @return
     */
    public static NISTool getInstance() {
        if (instanceObject == null)
            instanceObject = new NISTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public NISTool() {
        setModuleName("emr\\NisTQL.x");
        onInit();
    }
    
    public synchronized boolean saveNis(TParm parm,TConnection con)throws Exception{
		try {
			TParm action = this.update("saveNis", parm, con);
			System.out.println("save:"+action);
			if (action.getErrCode() < 0) {
				con.rollback();
				con.close();
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}finally{
			con.commit();
			con.close();
			return true;
		}
	}
    
    /**
     * 
     * @param parm
     * @return
     */
	public boolean saveNis1(TParm parm) {
		TParm result = this.update("saveNis", parm);
		if (result.getErrCode() < 0)
			return false;
		return true;
	}
    
	
	public synchronized boolean deleteNis(TParm parm,TConnection con)throws Exception{
		try {
			TParm action = this.update("deleteNis", parm, con);
			System.out.println("delete:"+action);
			if (action.getErrCode() < 0) {
				con.rollback();
				con.close();
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}finally{
			con.commit();
			con.close();
			return true;
		}
	}
	
	public synchronized TParm updateNis(TParm parm,TConnection con)throws Exception{
		TParm action = new TParm();
		try {
			action = this.update("updateNis", parm, con);
			System.out.println("update:"+action);
			if (action.getErrCode() < 0) {
				con.rollback();
				con.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}finally{
			con.commit();
			con.close();
			return action;
		}		
	}
	
	public boolean updateNis1(TParm parm) {
		TParm result = this.update("updateDr", parm);
		if (result.getErrCode() < 0)
			return false;
		return true;
	}
	
	public synchronized TParm selectNis(TParm parm ,TConnection con)throws Exception{
		TParm action = new TParm();
		try {
			action = this.query("selectNis", parm, con);
			System.out.println("select:"+action);
			if (action.getErrCode() < 0) {
				con.rollback();
				con.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}finally{
			con.commit();
			con.close();
			return action;
		}
		
		
	}
	
	public synchronized TParm getDict(TParm parm,TConnection con)throws Exception{
		TParm action = new TParm();
		try {
			action = this.query("getDict", parm, con);
			System.out.println("getDict:"+action);
			if (action.getErrCode() < 0) {
				con.rollback();
				con.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}finally{
			con.commit();
			con.close();
			return action;
		}
	}
	/**
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getDict(TParm parm) {
		return query("getDict", parm);
	}
	
	
	public synchronized TParm updateAdmInp(TParm parm ,TConnection con)throws Exception{
		TParm action = new TParm();
		try {
			action = this.update("updateAdmInp", parm, con);
			System.out.println("updateAdmInp:"+action);
			if (action.getErrCode() < 0) {
				con.rollback();
				con.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}finally{
			con.commit();
			con.close();
			return action;
		}
		
		
	}
	
	 public TParm checkWarning(TParm parm,TConnection con) throws Exception{
	    	TParm result = new TParm();
	    	result = getDict(parm,con);
	    	
	    	TParm p = new TParm();
	    	
	    	String str = "( "+parm.getValue("SCORE")+result.getValue("LOGIC1", 0)+result.getValue("SCORE1", 0)+")";
	        ScriptEngineManager manager = new ScriptEngineManager();
	        ScriptEngine engine = manager.getEngineByName("js");
	        Object r = engine.eval(str);
	    	if(Boolean.parseBoolean(r.toString())){
	    		p.setData("WARNING_FLG", "1");
	    	}else{
	    		p.setData("WARNING_FLG", "0");
	    	}
	    	
	    	p.setData("SCORE_DESC", result.getValue("SCORE_DESC", 0));
	    	
	    	return p;
	    }

	 
	 /**
	  * 检测
	  * @param parm
	  * @return
	  * @throws Exception
	  */
	 public TParm checkWarning1(TParm parm) {
		 	//
	    	TParm result = new TParm();
	    	result = getDict(parm);	    	
	    	TParm p = new TParm();	    	
	    	String str = "( "+parm.getValue("SCORE")+result.getValue("LOGIC1", 0)+result.getValue("SCORE1", 0)+")";
	    	//System.out.println("1111111111111111111111111"+str);
	        ScriptEngineManager manager = new ScriptEngineManager();
	       // System.out.println("2222222222222222222222222"+str);
	        ScriptEngine engine = manager.getEngineByName("js");
	       // System.out.println("333333333333333333333333"+str);
	        Object r=null;
			try {
				r = engine.eval(str);
			} catch (ScriptException e) {
				e.printStackTrace();
			}
	    	if(Boolean.parseBoolean(r.toString())){
	    		p.setData("WARNING_FLG", "1");
	    	}else{
	    		p.setData("WARNING_FLG", "0");
	    	}
	    	
	    	p.setData("SCORE_DESC", result.getValue("SCORE_DESC", 0));
	    	p.setData("EVALUTION_DESC", result.getValue("EVALUTION_DESC", 0));
	    	//System.out.println("444444444444444444444444444"+p);
	    	
	    	return p;
	 }
	 
	
	public synchronized String getNo(String regionCode, String systemCode,
                                     String operation, String section) {
        TParm parm = new TParm();
        parm.setData("REGION_CODE", regionCode);
        parm.setData("SYSTEM_CODE", systemCode);
        parm.setData("OPERATION", operation);
        parm.setData("SECTION", section);
        return getResultString(call("getNo", parm), "NO");
	}
    
}
