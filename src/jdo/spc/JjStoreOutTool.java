package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jdo.ind.INDSQL;
import jdo.ind.IndStockDTool;
import jdo.sys.SystemTool;

public class JjStoreOutTool extends TJDOTool
{
  public static JjStoreOutTool instanceObject;

  public JjStoreOutTool()
  {
    onInit();
  }

  public static JjStoreOutTool getInstance()
  {
    if (instanceObject == null)
      instanceObject = new JjStoreOutTool();
    return instanceObject;
  }

  public TParm JjStockOut(TParm parm, TConnection conn)
  {
    String orgCode = (String)parm.getData("ORG_CODE");
    List list = (List)parm.getData("list");
    Set set = (Set)parm.getData("set");
    Iterator iterator = set.iterator();
    Object[] obj = set.toArray();

    String dispense_no = SystemTool.getInstance().getNo("ALL", "IND", 
      "IND_DISPENSE", "No");
    int count = 1;
    Map map = new HashMap();
    String orderCode;
    for (int i = 0; i < obj.length; ++i) {
      String containerId = String.valueOf(obj[i]);
      TParm inparm = new TParm();
      inparm.setData("CONTAINER_ID", containerId);
      TParm result = SPCContainerTool.getInstance().queryInfo(inparm);
      orderCode = (String)result.getData("ORDER_CODE", 0);
      String orderDesc = (String)result.getData("ORDER_DESC", 0);
      String containerDesc = (String)result.getData("CONTAINER_DESC", 0);
      inparm.setData("DISPENSE_NO", dispense_no);
      inparm.setData("DISPENSE_SEQ_NO", Integer.valueOf(count));
      inparm.setData("CONTAINER_DESC", containerDesc);
      inparm.setData("ORDER_CODE", orderCode);
      inparm.setData("BOX_ESL_ID", "");
      inparm.setData("IS_BOXED", "N");
      inparm.setData("BOXED_USER", "");
      inparm.setData("BOXED_DATE", null);
      inparm.setData("OPT_USER", parm.getData("OPT_USER"));
      inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
      inparm.setData("OPT_DATE", SystemTool.getInstance().getDate());
      result = SPCPoisonTool.getInstance().insertToxicm(inparm, conn);
      if (result.getErrCode() < 0) {
        return result;
      }
      map.put(containerId, Integer.valueOf(count));
      ++count;
    }
    String sql = "SELECT ORDER_CODE,BATCH_SEQ,COUNT(*) AS COUNT FROM IND_CONTAINERD WHERE TOXIC_ID IN(";
    for (int i = 0; i < list.size(); ++i) {
      List containerList = (List)list.get(i);
      String containerId = (String)containerList.get(6);
      TParm toxicdParm = new TParm();
      toxicdParm.setData("DISPENSE_NO", dispense_no);
      toxicdParm.setData("DISPENSE_SEQ_NO", map.get(containerId));
      toxicdParm.setData("CONTAINER_ID", containerId);
      toxicdParm.setData("TOXIC_ID", containerList.get(0));
      toxicdParm.setData("ORDER_CODE", containerList.get(7));
      toxicdParm.setData("BATCH_NO", containerList.get(3));
      toxicdParm.setData("VALID_DATE", containerList.get(4));
      toxicdParm.setData("VERIFYIN_PRICE", containerList.get(9));
      toxicdParm.setData("BATCH_SEQ", containerList.get(8));
      toxicdParm.setData("UNIT_CODE", containerList.get(10));
      toxicdParm.setData("IS_PACK", "N");
      toxicdParm.setData("OPT_USER", parm.getData("OPT_USER"));
      toxicdParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
      toxicdParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
      TParm result = SPCPoisonTool.getInstance().insertToxicdJj(
        toxicdParm, conn);
      if (result.getErrCode() < 0) {
        return result;
      }
    /*  result = SPCContainerTool.getInstance().deleteByToxic(toxicdParm);
      if (result.getErrCode() < 0) {
        return result;
      }*/
      if (i == list.size() - 1)
        sql = sql + containerList.get(0);
      else {
        sql = sql + containerList.get(0) + ",";
      }
    }
    sql = sql + ") GROUP BY ORDER_CODE,BATCH_SEQ";
    TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    for (int i = 0; i < result.getCount(); ++i) {
      int batchSeq = result.getInt("BATCH_SEQ", i);
      orderCode = (String)result.getData("ORDER_CODE", 0);
      int num = result.getInt("COUNT", i);
     /* TParm inParmSeq = new TParm(TJDODBTool.getInstance().select(
        INDSQL.getIndStock(orgCode, orderCode, batchSeq)));*/

    /*  double out_amt = StringTool.round(inParmSeq.getDouble(
        "STOCK_RETAIL_PRICE", 0) * 
        num, 2);*/
    /*  result = IndStockDTool.getInstance().onUpdateQtyRequestOut("EXM", 
        orgCode, orderCode, batchSeq, num, out_amt, 
        (String)parm.getData("OPT_USER"), 
        SystemTool.getInstance().getDate(), 
        (String)parm.getData("OPT_TERM"), conn);*/
    }
    return result;
  }
}