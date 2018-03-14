package action.odi;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import action.odi.spcUddClient.OdiDspndPkVo;
import action.odi.client.SpcOdiService_SpcOdiServiceImplPort_Client;
import action.odi.spcUddClient.SpcUddService_SpcUddServiceImplPort_Client;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

import jdo.odi.OdiMainTool;
import jdo.odi.OdiOrderTool;
import jdo.pda.PDATool;
import jdo.pha.PhaAntiTool;
import jdo.sys.Operator;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ODIAction extends TAction{
    public ODIAction() {
    }
    /**
     * 保存EMR文件
     * @param parm TParm
     * @return TParm
     */
    public TParm saveEmrFile(TParm parm){
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = OdiMainTool.getInstance().saveEmrFile(parm,connection);
        //System.out.println(""+result);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 保存新建病例
     * @param parm TParm
     * @return TParm
     */
    public TParm saveNewEmrFile(TParm parm){
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = OdiMainTool.getInstance().saveNewEmrFile(parm,connection);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        //add by wangjc 20171013 start
        if(StringUtils.isNotEmpty(parm.getValue("OPE_SAVE_CHECK"))){
        	if("Y".equals(parm.getValue("OPE_SAVE_CHECK"))){
        		String sql = "UPDATE OPE_IPROOM SET "
        				+ " OPBOOK_SEQ = '"+parm.getValue("OPBOOK_SEQ")+"', "
        				+ " OPT_USER = '"+parm.getValue("OPT_USER")+"',"
        				+ " OPT_DATE = SYSDATE, "
        				+ " OPT_TERM = '"+parm.getValue("OPT_TERM")+"' "
        				+ " WHERE IP ='"+parm.getValue("OPT_TERM")+"'";
                result = new TParm(TJDODBTool.getInstance().update(sql, connection));
                if(result.getErrCode()<0){
                    connection.close();
                    return result;
                }
        	}
        }
        //add by wangjc 20171013 end
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 保存住院医嘱
     * @param parm TParm
     * @return TParm
     */
    public TParm saveOrder(TParm parm){
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = OdiOrderTool.getInstance().saveOrder(parm,connection);
        //System.out.println(""+result);
        if(result.getErrCode()<0){
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 更新EMR文件状态
     * @param parm TParm
     * @return TParm
     */
    public TParm updateEmrFile(TParm parm){
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = OdiMainTool.getInstance().updateEmrFile(parm,connection);
        //System.out.println(""+result);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 更新EMR文件状态（病历书写）
     * @param parm TParm
     * @return TParm
     */
    public TParm writeEmrFile(TParm parm){
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = OdiMainTool.getInstance().writeEmrFile(parm,connection);
        //System.out.println(""+result);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        //add by wangjc 20171013 start
        if(StringUtils.isNotEmpty(parm.getValue("OPE_SAVE_CHECK"))){
        	if("Y".equals(parm.getValue("OPE_SAVE_CHECK"))){
        		String sql = "UPDATE OPE_IPROOM SET "
        				+ " OPBOOK_SEQ = '"+parm.getValue("OPBOOK_SEQ")+"', "
        				+ " OPT_USER = '"+parm.getValue("OPT_USER")+"',"
        				+ " OPT_DATE = SYSDATE, "
        				+ " OPT_TERM = '"+parm.getValue("OPT_TERM")+"' "
        				+ " WHERE IP ='"+parm.getValue("OPT_TERM")+"'";
                result = new TParm(TJDODBTool.getInstance().update(sql, connection));
                if(result.getErrCode()<0){
                    connection.close();
                    return result;
                }
        	}
        }
        //add by wangjc 20171013 end
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 更新EMR PDF
     * @param parm TParm
     * @return TParm
     */
    public TParm writePDFEmrFile(TParm parm){
    	TParm result = new TParm();
    	TConnection connection = getConnection();
    	result = OdiMainTool.getInstance().writePDFEmrFile(parm,connection);
    	//System.out.println(""+result);
    	if(result.getErrCode()<0){
    		connection.close();
    		return result;
    	}
    	connection.commit();
    	connection.close();
    	return result;
    }
    
    public TParm updateEmrFileByFile(TParm parm){
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = OdiMainTool.getInstance().updateEmrFileByFile(parm,connection);
        //System.out.println(""+result);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    public TParm sendElectronicTag(TParm parm) {
        TParm reparm = new TParm();
        String result = "";
        try {
            String caseno = parm.getValue("CASE_NO");
            String mrNo = parm.getValue("MR_NO");// add by wanglong 20130607
            String patName = parm.getValue("PAT_NAME");
            String stationDesc = parm.getValue("STATION_DESC");
            String ip = parm.getValue("OPT_TERM");// add by wanglong 20130607
            String bedNoDesc = parm.getValue("BED_NO_DESC");
            result =
                    SpcOdiService_SpcOdiServiceImplPort_Client.sendElectronicTag(caseno, patName,
                                                                                 stationDesc,
                                                                                 bedNoDesc, mrNo,
                                                                                 ip);
            // System.out.println("=====护士单次执行WSresult==========="+result);
        }
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            // System.out.println("=====护士单次执行WS失败===========");
            reparm.setErrCode(-1);
            reparm.setErrText("调用物联网WS失败");
        }
        reparm.setData("RESULT", result);
        return reparm;
    }
    
    /**
     * 向物联网请求医嘱单次执行信息
     * @param parm
     * @return
     */
    public TParm onQueryDspnDSpc(TParm parm) {// add by wanglong 20130604
        TParm result = new TParm();
        List<OdiDspndPkVo> receiptList = new ArrayList<OdiDspndPkVo>();
        try {
            receiptList =
                    SpcUddService_SpcUddServiceImplPort_Client.getOdiDspndInfo(parm
                            .getValue("CASE_NO"), parm.getValue("BAR_CODE"));
            if (receiptList == null || receiptList.size() == 0) {
                result.setErr(-1, "没有数据");
                return result;
            }
        }
        catch (Exception e) {
            result.setErr(-1, "调用物联网Service失败");
            return result;
        }
        for (int i = 0; i < receiptList.size(); i++) {
            OdiDspndPkVo odpv = receiptList.get(i);
            result.addData("CASE_NO", odpv.getCaseNo());
            result.addData("ORDER_NO", odpv.getOrderNo());
            result.addData("ORDER_SEQ", odpv.getOrderSeq());
            result.addData("ORDER_DATE", odpv.getOrderDate());
            result.addData("ORDER_DATETIME", odpv.getOrderDateTime());
            result.addData("ORDER_CODE", odpv.getOrderCode());
            result.addData("START_DTTM", odpv.getStartDttm());
            result.addData("END_DTTM", odpv.getEndDttm());
            result.addData("BARCODE_1", odpv.getBarCode1());
            result.addData("BARCODE_2", odpv.getBarCode2());
            result.addData("BARCODE_3", odpv.getBarCode3());
        }
        result.setCount(receiptList.size());
        return result;
    }
    /**
     * 更新PHA_ANTI状态
     * 向odi_order中写数据
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveToOdi(TParm parm){
    	TParm odiParm = new TParm();
    	odiParm = parm;
        TParm result = new TParm();
        TConnection connection = getConnection();
        //向odi_order中写入数据
        result = OdiMainTool.getInstance().onSaveOdiOrder(odiParm,connection);
        	if(result.getErrCode()<0){
            	connection.rollback();
                connection.close();
                return result;
            }
        result = PhaAntiTool.getInstance().updatePhaAnti(parm,connection);
        //System.out.println(""+result);
        if(result.getErrCode()<0){
        	connection.rollback();
            connection.close();
            return result;
        }
       
        connection.commit();
        connection.close();
        return result;
    } 
}
