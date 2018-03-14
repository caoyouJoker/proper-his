package action.spc;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import jdo.ind.ElectronicTagsImpl;
import jdo.ind.ElectronicTagsInf;
import jdo.spc.SPCUpdateElecTagPathTool;

import com.dongyang.data.TParm;
import com.dongyang.patch.Patch;

/**
 * <p>
 * Title: UPDATE 电子标签
 * </p>
 * 
 * <p>
 * Description: UPDATE 电子标签
 * </p>
 * @author liuzhen 2013.1.18
 * @version 1.0
 */

public class SPCUpdateElecTagPatch extends Patch {

	
	public boolean run() {
		
				
		try{
												
			TParm parm = new TParm();
			
			String orgCode ="040102";
			
			parm.setData("ORG_CODE",orgCode);
			
			TParm result = SPCUpdateElecTagPathTool.getInstance().query(parm);
			
			int count = result.getCount();
			
			System.out.println("---------total count -------"+count);
			
			for(int i = 0; i < count; i++){
										
//				String orgCode = result.getValue("ORG_CODE", i);
//				String orderCode = result.getValue("ORDER_CODE", i);
				String orderDesc = result.getValue("ORDER_DESC", i);
				String spec = result.getValue("SPECIFICATION", i);
				
				if(null!= spec && spec.length()>11){
					spec = spec.substring(0, 11);
				}
				
				String eletagCode = result.getValue("ELETAG_CODE", i);
				double qty = result.getDouble("QTY", i);
				
				System.out.println("-orderDesc:--"+orderDesc
									+"-spec:--"+spec+
									"-eletagCode:--"+eletagCode+
									"-qty:--"+Double.toString(qty));
				
				try{
					ElectronicTagsInf inf = new ElectronicTagsImpl();
					inf.login("admin", "123");
					Map<String, Object> m = new LinkedHashMap<String, Object>();
					UUID uuid = UUID.randomUUID();
					if (null == uuid) uuid = UUID.randomUUID();

					m.put("ObjectId", uuid.toString());
					m.put("ObjectType", 3);
					m.put("ObjectName", "medBasket");
					// m.put("LabelNo", "01048A");
					
					m.put("LabelNo",eletagCode );// 电子标签id					
					m.put("StationID", "2");// 基站id					
					m.put("ProductName", orderDesc);// 第一行 显示用户名和年龄					
					m.put("Spec", spec + " " + qty);// 第二回 显示病案号 和性别					
					m.put("ShelfNo", eletagCode);// 第三行 电子标签二维条码含义					
					m.put("Light", 1);// 闪烁次数					
					m.put("Enabled", true);// 是否亮灯：true:亮

					Iterator it = m.entrySet().iterator();

					ElectronicTagsInf eti = new ElectronicTagsImpl();
					
					// 调用电子标签接口
					Map<String, Object> map = eti.cargoUpdate(m);
					
					System.out.println("发药================================begin");
					
					Iterator it1 = map.entrySet().iterator();
					
					while (it.hasNext()) {
						Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it1.next();
						System.out.println(entry.getKey() + "===================="+ entry.getValue());
					}
					
					System.out.println("发药================================end");
				}catch(Exception e){
					e.printStackTrace();
				}

			}
			
		}catch (Exception e){
			e.printStackTrace();			
			
			return false;
		}		
		
		return true;
	}
	
	

}
