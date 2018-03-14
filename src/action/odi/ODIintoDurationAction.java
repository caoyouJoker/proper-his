package action.odi;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.odi.OdiMainTool;
import jdo.odi.ODIintoDurationTool;
import java.util.Map;

/**
 * <p>Title: 进入时程</p>
 *
 * <p>Description: 进入时程</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class ODIintoDurationAction extends TAction{
    public ODIintoDurationAction() {

    }

    /**
     * 保存进入下一时程
     * @param parm TParm
     * @return TParm
     */
    public TParm intoNextDuration(TParm sendParm) {
        TParm parm= new TParm((Map)sendParm.getData("saveData"));
        //处理参数
        Map basicMap=(Map)sendParm.getData("basicMap");
        //当前时间
        parm.setData("CUR_DATE",basicMap.get("OPT_DATE"));
        parm.setData("END_DATE",basicMap.get("OPT_DATE"));
        TParm result = new TParm();
        TConnection conn = getConnection();
//        result = ODIintoDurationTool.getInstance().insertIntoDurationReal(this.cloneTParm(parm),conn);
//        if (result.getErrCode() < 0) {
//            conn.close();
//            return result;
//        }
        //更新下一时程
        result= ODIintoDurationTool.getInstance().updateNextDuration(this.cloneTParm(parm),conn);
        if (result.getErrCode() < 0) {
        	// modified by WangQing at 20170209 -start
        	// conn rollback
        	conn.rollback();
        	// modified by WangQing at 20170209 -end
            conn.close(); 
            return result;
        }
        result=ODIintoDurationTool.getInstance().updateDurationReal(this.cloneTParm(parm),conn);
        if (result.getErrCode() < 0) {
        	// modified by WangQing at 20170209 -start
        	// conn rollback
        	conn.rollback();
        	// modified by WangQing at 20170209 -end
            conn.close();
            return result;
        }
        conn.commit();
        // modified by WangQing at 20170209 -start
    	// conn close
    	conn.close();
    	// modified by WangQing at 20170209 -end
        
        return result;
    }

    /**
     * 拷贝TParm
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from, TParm to, int row) {
        for (int i = 0; i < from.getNames().length; i++) {
            to.addData(from.getNames()[i],
                       from.getValue(from.getNames()[i], row));
        }
    }

    /**
     * 克隆对象
     * @param parm TParm
     * @return TParm
     */
    private TParm cloneTParm(TParm from) {
        TParm returnTParm = new TParm();
        for (int i = 0; i < from.getNames().length; i++) {
            returnTParm.setData(from.getNames()[i],
                                from.getValue(from.getNames()[i]));
        }
        return returnTParm;
    }

}
