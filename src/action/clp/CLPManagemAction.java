package action.clp;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

import jdo.clp.CLPCauseHistoryTool;
import jdo.clp.CLPManagemTool;
import java.util.Map;
import com.dongyang.data.TNull;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title:临床路径准进准出Action </p>
 *
 * <p>Description:临床路径准进准出Action </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPManagemAction extends TAction {
    public CLPManagemAction() {
    }

    /**
     * 变更临床路径
     * @param parm TParm
     * @return TParm
     */
    public TParm changeCLPManagem(TParm parm) {
        TParm parmForUpadate = parm.getParm("parmForUpadate");
        String clpPath = parmForUpadate.getValue("CLNCPATH_CODE");
        String caseNo = parmForUpadate.getValue("CASE_NO");
        TConnection conn = getConnection();
        TParm result = CLPManagemTool.getInstance().updateChangedManagem(
                parmForUpadate, conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        //得到要移动的Manager信息并向历史表插入数据
        TParm parmForSelect = new TParm();
        parmForSelect.setData("CASE_NO", caseNo);
        parmForSelect.setData("CLNCPATH_CODE", clpPath);
        result = CLPManagemTool.getInstance().managermToHistory(parmForSelect,
                conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
//        //向历史表插入数据
//        TParm  hisParm=parmForSelectResult.getRow(0);
////        TNull tnull = new TNull(Timestamp.class);
////        hisParm.setData("IN_DATE",tnull);
//        if(!this.checkNullAndEmpty(hisParm.getValue("START_DTTM"))){
//             TNull tnull = new TNull(Timestamp.class);
//             hisParm.setData("START_DTTM",tnull);
//        }
//        if (!this.checkNullAndEmpty(hisParm.getValue("DELETE_DTTM"))) {
//            TNull tnull = new TNull(Timestamp.class);
//            hisParm.setData("DELETE_DTTM", tnull);
//        }
//        if (!this.checkNullAndEmpty(hisParm.getValue("END_DTTM"))) {
//            TNull tnull = new TNull(Timestamp.class);
//            hisParm.setData("END_DTTM", tnull);
//        }
//        result= CLPManagemTool.getInstance().insertManagemHistory(hisParm,conn);
//        if (result.getErrCode() < 0) {
//            conn.close();
//            return result;
//        }
        //System.out.println("删除数据-----------------");
        //删除数据
        TParm parmForDel = new TParm();
        parmForDel.setData("CASE_NO", caseNo);
        parmForDel.setData("CLNCPATH_CODE", clpPath);
        result = CLPManagemTool.getInstance().deleteData(parmForDel, conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        //System.out.println("插入新数据数据-----------------");
        //插入新数据
        TParm parmForInsert = new TParm((Map) parm.getData("parmForInsert"));
        //System.out.println("parmForInsert" + parmForInsert);
        result = CLPManagemTool.getInstance().insertManagem(this.cloneTParm(parmForInsert), conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        result = CLPManagemTool.getInstance().insertAMDINPIntoCLNCPathCode(this.cloneTParm(parmForInsert),conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        //卢海20110905加入
        //加入该临床路径的默认时程
        result = CLPManagemTool.getInstance().insertDefaultDurationConfig(this.cloneTParm(parmForInsert), conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        //===pangben 2015-5-8 修改路径操作，将已经进入时程的计费医嘱重新转入新时程
        
        if (null!=parm.getValue("CLP_BILL_FLG") && parm.getValue("CLP_BILL_FLG").equals("Y")) {
        	result = CLPManagemTool.getInstance().updateIBsOrddClnPathCode(
        			parmForInsert, conn);
			if (result.getErrCode() < 0) {
				conn.close();
				return result;
			}
		}else{
			TParm billParm=parm.getParm("billParm");
	        if (null!=billParm&&billParm.getCount()>0) {
	        	TParm temp=null;
	        	String sameSchdCode=parm.getValue("sameSchdCode");//相同时程代码直接替换
	        	if(null!=sameSchdCode&& sameSchdCode.length()>0){
		        	TParm sameParm=new TParm();
		        	sameParm.setData("sameSchdCode",sameSchdCode);
		        	sameParm.setData("CLNCPATH_CODE",parmForInsert.getValue("CLNCPATH_CODE"));
		        	sameParm.setData("CASE_NO",caseNo);
		        	result =  CLPManagemTool.getInstance().updateIbsOrddSameSchdCode(sameParm, conn);
		        	if (result.getErrCode() < 0) {
						conn.close();
						return result;
					}
	        	}
				for (int i = 0; i < billParm.getCount(); i++) {
					temp = billParm.getRow(i);
					temp.setData("CASE_NO", caseNo);
					result = CLPManagemTool.getInstance().updateIbsOrddSchdCode(
							temp, conn);
					if (result.getErrCode() < 0) {
						conn.close();
						return result;
					}
				}
			}
		}
        conn.commit();
        conn.close();
        return result;
    }


    /**
     * 实际时程设定
     * @param parm TParm
     * @return TParm
     */
    public TParm setDuring(TParm inParm) {
        //System.out.println("------------------------------------------------");
        TParm parm = inParm.getParm("tableParm");
        Map basicMap = (Map) inParm.getData("basicMap");
        //System.out.println("保存数据条数:" + parm.getCount());
        TConnection conn = getConnection();
        TParm result = null;
        int seq=0;
        for (int i = 0; i < parm.getCount(); i++) {
            TParm rowParm = parm.getRow(i);
            TParm delParm = new TParm();
           
            delParm.setData("CASE_NO", rowParm.getValue("CASE_NO"));
            delParm.setData("CLNCPATH_CODE", rowParm.getValue("CLNCPATH_CODE"));
            delParm.setData("SCHD_CODE", rowParm.getValue("SCHD_CODE"));
            result = CLPManagemTool.getInstance().delDurationData(delParm);
            if (result.getErrCode() < 0) {
                conn.close();
                return result;
            }
            //=============pangben 2012-5-25 start
            TParm maxSeqParm=new TParm(TJDODBTool.getInstance().select(
            "SELECT MAX(SEQ) SEQ FROM CLP_THRPYSCHDM_REAL WHERE  CASE_NO='"+rowParm.getValue("CASE_NO")+"' AND CLNCPATH_CODE='"+rowParm.getValue("CLNCPATH_CODE")+"' AND SCHD_CODE='"+rowParm.getValue("SCHD_CODE")+"'"));
            if (maxSeqParm.getErrCode() < 0) {
                conn.close();
                return maxSeqParm;
            }
            if (null!=maxSeqParm.getValue("SEQ",0) && maxSeqParm.getInt("SEQ",0)>0) {
            	seq=maxSeqParm.getInt("SEQ",0);	
			}          
            //=============pangben 2012-5-25 stop
            //处理添加的TParm
            rowParm.setData("REGION_CODE", basicMap.get("REGION_CODE"));
            rowParm.setData("OPT_USER", basicMap.get("OPT_USER"));
            rowParm.setData("OPT_TERM", basicMap.get("OPT_TERM"));
            rowParm.setData("OPT_DATE", basicMap.get("OPT_DATE"));
            rowParm.setData("SEQ",seq);  
            String startDate = rowParm.getValue("START_DATE");
            if (startDate.length() >= 19) {
                startDate = startDate.substring(0, 19).replace("-", "").replace(":", "").replace(" ","").replace("/","");
                rowParm.setData("START_DATE", startDate);
            } else {
                rowParm.setData("START_DATE", new TNull(String.class));
            }
            String endDate = rowParm.getValue("END_DATE");
            if (endDate.length() >= 19) {
                endDate = endDate.substring(0, 19).replace("-", "").replace(":", "").replace(" ","").replace("/","");
                rowParm.setData("END_DATE", endDate);
            } else {
                rowParm.setData("END_DATE", new TNull(String.class));
            }
            //System.out.println("-----------保存的数据:" + rowParm);
            result = CLPManagemTool.getInstance().savaDurationData(rowParm,
                    conn);
            if (result.getErrCode() < 0) {
                conn.close();
                return result;
            }
            seq++;
        }

        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 展开时程
     * @param parm TParm
     * @return TParm
     */
    public TParm openDuraction(TParm inParm) {
        TParm parm = new TParm((Map) inParm.getData("tableParm"));
        Map basicMap = (Map) inParm.getData("basicMap");
        //System.out.println("保存数据条数:" + parm.getCount());
        TConnection conn = getConnection();
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount(); i++) {
            TParm rowParm = parm.getRow(i);
            //处理时间
            rowParm.setData("START_DATE",
                            rowParm.getValue("START_DATE").replace("/", "").replace(" ","").replace(":",""));
            rowParm.setData("END_DATE",
                            rowParm.getValue("END_DATE").replace("/", "").replace(" ","").replace(":",""));
            //处理保存数据
            rowParm.setData("REGION_CODE", basicMap.get("REGION_CODE"));
            rowParm.setData("OPT_USER", basicMap.get("OPT_USER"));
            rowParm.setData("OPT_TERM", basicMap.get("OPT_TERM"));
            rowParm.setData("OPT_DATE", basicMap.get("OPT_DATE"));
            result = CLPManagemTool.getInstance().openDuraction(rowParm,
                    conn);
            if (result.getErrCode() < 0) {
                conn.close();
                return result;
            }
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 保存准进准出信息
     * @param parm TParm
     * @return TParm
     */
    public TParm updateManagem(TParm inParm) {
        //System.out.println("保存准进准出信息-------------------------");
        TParm parm = new TParm((Map) inParm.getData("tableParm"));
        Map basicMap = (Map) inParm.getData("basicMap");
        String currentDateStr = inParm.getValue("currentDateStr");
        TConnection conn = getConnection();
        TParm result = new TParm();
        //记录数据是否需要移动
        boolean isNeedMove = false;
        String is_in = (String) parm.getData("IS_IN");
        String isCancelation = (String) parm.getData("IS_CANCELLATION");
        String isOverFlow = (String) parm.getData("IS_OVERFLOW");
        String statusStr = (String) parm.getData("STATUS");
        String outIssuCode=(String)parm.getData("OUTISSUE_CODE");
        TParm saveParm = new TParm();
        //进入操作 删除20110714 begin
        //在添加临床路径时已经默认进入路径，故在编辑时不再重新进入路径
//        String isInStr = "Y";
//        if ("Y".equals(is_in)) {
//            saveParm.setData("START_DTTM", currentDateStr);
//        }
        //进入操作 删除20110714 end
        //作废操作
        if ("Y".equals(isCancelation)) {
            saveParm.setData("DELETE_DTTM", currentDateStr);
            isNeedMove = true;
        }
        //溢出操作
        if ("Y".equals(isOverFlow)) {
            saveParm.setData("END_DTTM", currentDateStr);
            saveParm.setData("OUTISSUE_CODE",outIssuCode);
            isNeedMove = true;
        }
        //状态操作
        String statusSetStr = "0";
        if ("Y".equals(statusStr)) {
            statusSetStr = "1";
        }
        saveParm.setData("STATUS", statusSetStr);
        saveParm.setData("CASE_NO", parm.getData("CASE_NO"));
        saveParm.setData("CLNCPATH_CODE", parm.getData("CLNCPATH_CODE"));
        TParm parmresult = CLPManagemTool.getInstance().updateManagem(
                saveParm, conn);
        if (parmresult.getErrCode() < 0) {
            conn.close();
            return parmresult;
        }
        TParm moveRow = cloneTParm(parm);
        //System.out.println("-----------移动的数据："+moveRow);
        //TParm delRow = cloneTParm(parm);
        //String deleteDttm = moveRow.getValue("DELETE_DTTM");
        //String endDttm = parm.getValue("END_DTTM");
        //移动到历史表中时需要提交改动数据，以保证存入历史表的数据正确
        conn.commit();
        if (isNeedMove) {
            //处理保存数据
            moveRow.setData("REGION_CODE", basicMap.get("REGION_CODE"));
            moveRow.setData("OPT_USER", basicMap.get("OPT_USER"));
            moveRow.setData("OPT_TERM", basicMap.get("OPT_TERM"));
            moveRow.setData("OPT_DATE", basicMap.get("OPT_DATE"));
            result = CLPManagemTool.getInstance().
                     moveDeleteAndEndManagemIntoHistory(moveRow, conn);
            if (result.getErrCode() < 0) {
                conn.close();
                return result;
            }
//            result = CLPManagemTool.getInstance().deleteData(delRow, conn);
//            if (result.getErrCode() < 0) {
//                conn.close();
//                return result;
//            }
        }
        //作废或溢出时需要更新ADM_INP
        if("Y".equals(isCancelation)||"Y".equals(isOverFlow)){
            //更新ADM_INP表数据
            TParm admUpdateParm = this.cloneTParm(parm);
            admUpdateParm.setData("CLNCPATH_CODE", new TNull(String.class));
            admUpdateParm.setData("SCHD_CODE", new TNull(String.class));
            result = CLPManagemTool.getInstance().insertAMDINPIntoCLNCPathCode(
                    admUpdateParm, conn);
            if (result.getErrCode() < 0) {
                conn.close();
                return result;
            }
        }
        if ("Y".equals(isOverFlow)) {//溢出操作清空路径数据
        	String cancleSql = "UPDATE IBS_ORDD SET CLNCPATH_CODE='',SCHD_CODE='' WHERE CASE_NO = '"
        		+parm.getValue("CASE_NO")+"'";
			TParm cancleParm = new TParm(TJDODBTool.getInstance().update(cancleSql,conn));
			if (cancleParm.getErrCode() < 0) {
				 conn.close();
				return cancleParm;
			}
		}
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 删除准入信息
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteManagem(TParm inParm) {
        //System.out.println("删除准进准出信息-------------------------");
        TParm result=null;
        TParm parm = new TParm((Map) inParm.getData("parm"));
        Map basicMap = (Map) inParm.getData("basicMap");
        TConnection conn = getConnection();
        //删除数据移动历史表的情况
        TParm moveRow = cloneTParm(parm);
        //处理保存数据
        moveRow.setData("REGION_CODE", basicMap.get("REGION_CODE"));
        moveRow.setData("OPT_USER", basicMap.get("OPT_USER"));
        moveRow.setData("OPT_TERM", basicMap.get("OPT_TERM"));
        moveRow.setData("OPT_DATE", basicMap.get("OPT_DATE"));
        result = CLPManagemTool.getInstance().
                 moveDeleteAndEndManagemIntoHistory(moveRow, conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        //删除准入数据
        result = CLPManagemTool.getInstance().deleteData(cloneTParm(parm));
         if (result.getErrCode() < 0) {
             conn.close();
             return result;
         }
         TParm admUpdateParm=this.cloneTParm(parm);
         admUpdateParm.setData("CLNCPATH_CODE",new TNull(String.class));
         admUpdateParm.setData("SCHD_CODE",new TNull(String.class));
         result=CLPManagemTool.getInstance().insertAMDINPIntoCLNCPathCode(admUpdateParm,conn);
         if (result.getErrCode() < 0) {
             conn.close();
             return result;
         }
         conn.commit();
         conn.close();
        return result;
    }
    /**
     * 进入临床路径
     * @param inParm TParm
     * @return TParm
     */
    public TParm insertManagem(TParm inParm){
        TConnection conn = getConnection();
        TParm result = new TParm();
        result=CLPManagemTool.getInstance().insertManagem(this.cloneTParm(inParm),conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        if (null==inParm.getValue("SCHD_CODE")||inParm.getValue("SCHD_CODE").length()<=0) {
        	inParm.setData("SCHD_CODE","");
		}
        result= CLPManagemTool.getInstance().insertAMDINPIntoCLNCPathCode(this.cloneTParm(inParm),conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        //卢海20110905加入
        //加入该临床路径的默认时程
        result=CLPManagemTool.getInstance().insertDefaultDurationConfig(this.cloneTParm(inParm),conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        String sql="SELECT SCHD_CODE FROM CLP_THRPYSCHDM WHERE CLNCPATH_CODE='"+inParm.getValue("CLNCPATH_CODE")+"' ORDER BY SEQ";
     	TParm clpParm= new TParm(TJDODBTool.getInstance().select(sql));
        //第一次进入医生站操作，更新转科表ADM_TRANS_LOG
        if (null!=inParm.getValue("CLP_NEW_FLG")&&inParm.getValue("CLP_NEW_FLG").equals("Y")) {
        	//转科更新转时程字段
        	sql = "UPDATE ADM_TRANS_LOG SET CLP_SCHD_FLG = 'Y' WHERE CASE_NO= '"
        		+ inParm.getValue("CASE_NO") + "' AND CLP_SCHD_FLG = 'N'";
        	result = new TParm(TJDODBTool.getInstance().update(sql,conn));
        	if (result.getErrCode()<0) {
        		conn.rollback();
        		conn.close();
    			return result;
    		}
        	//住院医生站保存医嘱校验是否进入路径，保存以后的临时医嘱需要更新路径时程
        	sql = "UPDATE ODI_ORDER SET SCHD_CODE = '"+inParm.getValue("SCHD_CODE")+"' WHERE CASE_NO= '"
         		+ inParm.getValue("CASE_NO") + "' AND RX_KIND = 'ST'";
         	result = new TParm(TJDODBTool.getInstance().update(sql,conn));
         	if (result.getErrCode()<0) {
         		conn.rollback();
         		conn.close();
     			return result;
     		}
		}else{
			//开立的临时医嘱如果没有进入时程，默认进入
	     	//默认进入时程
	     	//住院医生站保存医嘱校验是否进入路径，保存以后的临时医嘱需要更新路径时程
			if(null==inParm.getValue("SCHD_CODE")||inParm.getValue("SCHD_CODE").length()<=0){
				inParm.setData("SCHD_CODE",clpParm.getValue("SCHD_CODE",0));
			}
	    	sql = "UPDATE ODI_ORDER SET SCHD_CODE = '"+inParm.getValue("SCHD_CODE")+"' WHERE CASE_NO= '"
	     		+ inParm.getValue("CASE_NO") + "' AND RX_KIND = 'ST' AND SCHD_CODE IS NULL";
	     	result = new TParm(TJDODBTool.getInstance().update(sql,conn));
	     	if (result.getErrCode()<0) {
	     		conn.rollback();
	     		conn.close();
	 			return result;
	 		}
		}

     	//已计费医嘱默认进入时程
     	sql="UPDATE IBS_ORDD SET CLNCPATH_CODE='"+inParm.getValue("CLNCPATH_CODE")+"',SCHD_CODE = '"+inParm.getValue("SCHD_CODE")
     	+"' WHERE CASE_NO= '"+ inParm.getValue("CASE_NO") + "' AND SCHD_CODE IS NULL ";
     	result = new TParm(TJDODBTool.getInstance().update(sql,conn));
     	if (result.getErrCode()<0) {
     		conn.rollback();
     		conn.close();
 			return result;
 		}
     	//默认进入时程
     	sql="UPDATE ADM_INP SET SCHD_CODE ='"+inParm.getValue("SCHD_CODE")+
     	"' WHERE CASE_NO= '"+ inParm.getValue("CASE_NO") + "' AND SCHD_CODE IS NULL ";
     	result = new TParm(TJDODBTool.getInstance().update(sql,conn));
     	if (result.getErrCode()<0) {
     		conn.rollback();
     		conn.close();
 			return result;
 		}
        conn.commit();
        conn.close();
        return result;
    }
    /**
     * 检查是否为空或空串
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr) {
        if (checkstr == null) {
            return false;
        }
        if ("".equals(checkstr)) {
            return false;
        }
        return true;
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
    /**
     * 
    * @Title: onExeInDuration
    * @Description: TODO(转时程操作：添加转科更新CLP_SCHD_FLG=Y)
    * @author pangben 2015-8-13
    * @param parm
    * @return
    * @throws
     */
    public TParm onExeInDuration(TParm parm){
    	TConnection conn = getConnection();
    	String sql = "UPDATE ADM_INP SET SCHD_CODE = '" +  parm.getValue("SCHD_CODE")
		+ "' WHERE CASE_NO= '" + parm.getValue("CASE_NO") + "' ";
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
    	if (result.getErrCode()<0) {
    		conn.rollback();
    		conn.close();
			return result;
		}
    	//转科更新转时程字段
    	sql = "UPDATE ADM_TRANS_LOG SET CLP_SCHD_FLG = 'Y' WHERE CASE_NO= '"
    		+ parm.getValue("CASE_NO") + "' AND CLP_SCHD_FLG = 'N'";
    	result = new TParm(TJDODBTool.getInstance().update(sql,conn));
    	if (result.getErrCode()<0) {
    		conn.rollback();
    		conn.close();
			return result;
		}
    	conn.commit();
		conn.close();
		return result;
    }
    /**
     * 
    * @Title: insertCauseHistory
    * @Description: TODO(不进入)
    * @author pangben
    * @param inParm
    * @return
    * @throws
     */
    public TParm insertCauseHistory(TParm inParm){
    	TConnection conn = getConnection();
    	//操作不进入原因
		TParm result=CLPCauseHistoryTool.getInstance().insertClpCauseHistory(inParm,conn);
		if (result.getErrCode()< 0) {
			conn.rollback();
    		conn.close();
			return result;
		}
    	//第一次进入医生站操作，更新转科表ADM_TRANS_LOG
        if (null!=inParm.getValue("CLP_NEW_FLG")&&inParm.getValue("CLP_NEW_FLG").equals("Y")) {
        	//转科更新转时程字段
        	String sql = "UPDATE ADM_TRANS_LOG SET CLP_SCHD_FLG = 'Y' WHERE CASE_NO= '"
        		+ inParm.getValue("CASE_NO") + "' AND CLP_SCHD_FLG = 'N'";
        	result = new TParm(TJDODBTool.getInstance().update(sql,conn));
        	if (result.getErrCode()<0) {
        		conn.rollback();
        		conn.close();
    			return result;
    		}
		}
        conn.commit();
		conn.close();
        return result;
    }
}
