package jdo.spc.accountinf;

import java.util.Iterator;

import jdo.ind.IndSysParmTool;

import org.dom4j.Document;
import org.dom4j.Element;

import com.dongyang.Service.Server;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

public class IndDispensDaoImpl extends TJDOTool {
	
	/**
	 * 实例
	 */
	public static IndDispensDaoImpl instanceObject;
	
	/**
	 * 得到实例
	 * 
	 * @return INDTool
	 */
	public static IndDispensDaoImpl getInstance() {
		if (instanceObject == null)
			instanceObject = new IndDispensDaoImpl();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public IndDispensDaoImpl() {
		onInit();
	}
	
	
	public  String onSave(String xml){
		if(xml == null  || xml.equals("") || xml.equals("null")){
			return  "fail";
		}
		try {
			Document doc = XmlUtil.getDocument(xml);
			Element rootElement = XmlUtil.getRootElement(doc);

			Element indmElement = rootElement.element("INDM");
			TParm parm = new TParm();
			// 主表
			TParm parmM = new TParm();
			for (Iterator it = indmElement.elementIterator(); it.hasNext();) {
				Element child = (Element) it.next();
				String name = child.getName(); // 获取节点的名字
				String text = child.getText(); // 读取节点的内容
				//System.out.println("节点名：" + name + "     内容：" + text);
				if(name != null && name.endsWith("DATE")){
					if(text != null && !text.equals("")){
						text = text.substring(0,text.length()-2);
						parmM.setData(name, StringTool.getTimestamp(text, "yyyy-MM-dd HH:mm:ss"));
					}else{
						parmM.setData(name, text);
					}
				}else{
					parmM.setData(name, text);
				}
			}
	        // 单位类型 // 申请单类型
			parm.setData("REQTYPE_CODE",parmM.getValue("REQTYPE_CODE"));
			parm.setData("ORG_CODE",parmM.getValue("TO_ORG_CODE"));
			parm.setData("UNIT_TYPE",parmM.getValue("UNIT_TYPE"));
			parm.setData("OUT_M", parmM.getData());
						 
			// 子表
			TParm parmD = new TParm();
			
			Element inddElement = rootElement.element("INDDS");
			Iterator iters  = inddElement.elementIterator("INDD");
			
			while(iters.hasNext()){
				Element child = (Element) iters.next();
				
				//得到子节点的所有内容 
				Iterator childIterator = child.elementIterator() ;
				int count = 0;
				while(childIterator.hasNext()){
				
					Element subChild = (Element)childIterator.next() ;
					
					String name = subChild.getName(); // 获取节点的名字
					String text = subChild.getText(); // 读取节点的内容
					//System.out.println("节点名：" + name + "     内容：" + text);
					
					if(name != null && name.endsWith("DATE")){
						if(text != null && !text.equals("")){
							text = text.substring(0,text.length()-2);
							parmD.setData(name, count, StringTool.getTimestamp(text, "yyyy-MM-dd HH:mm:ss"));
						}else{
							parmD.setData(name, count,text);
						}
					}else{
						parmD.setData(name, count,text);
					}
					count++;
				}
			}
			if (parmD != null) {
				parm.setData("OUT_D", parmD.getData());
			}
			
			System.out.println("parm---:" + parm);
			Server.autoInit(this);
			
	        TParm sysParm = getSysParm() ;
	     // 出入库作业状态判断(1-入库确认；2-出库即入库)
	        String dis_check = getDisCheckFlg(sysParm,parmM.getValue("TO_ORG_CODE"),parmM.getValue("APP_ORG_CODE"));
	        
	        if ("1".equals(dis_check)) {
		        parm = Server.executeAction("action.ind.INDDispenseAction",
		                "onInsertOutOn", parm);
	        }else{
	        	// 执行数据新增
	            parm = Server.executeAction("action.ind.INDDispenseAction",
	                                                "onInsertOutIn", parm);
	        }

			// 判断是否成功
			if (parm == null || parm.getErrCode() < 0) {
				return "fail";
			}

		} catch (Exception e) {
			// TODO: handle exceptions
			return "fail";
		}

		return "success";
	}
	
	/**
     * 药库参数信息
     * @return TParm
     */
    private TParm getSysParm() {
        return IndSysParmTool.getInstance().onQuery();
    }
    
    /**
     * 出入库作业状态判断
     *
     * @return
     */
    private String getDisCheckFlg(TParm parm,String out_org_code,String in_org_code) {
        // 出入库作业状态判断
        if ("Y".equals(parm.getValue("DISCHECK_FLG", 0))
            && !"".equals(out_org_code) && !"".equals(in_org_code)) {
            // 需进行入库确认者且申请单状态入出库部门皆不为空-->在途状态
            return "1";
        }
        else if ("N".equals(parm.getValue("DISCHECK_FLG", 0))
                 && !"".equals(out_org_code) && !"".equals(in_org_code)) {
            // 不需进行入库确认者且申请单状态入出库部门皆不为空-->出库即入库
            return "2";
        }
        return "1";
    }
    

}
