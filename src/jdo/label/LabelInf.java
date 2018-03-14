package jdo.label;

import java.util.List;
import java.util.Map;

public interface LabelInf {
	
	/**
	 * 功能描述：基站区域
	 * @param map
	 * @return
	 */
	public Map<String, Object> apRegion(List<Map<String, Object>> list,String url);
	
	/**
	 * 功能描述：标签设定
	 * @param map
	 * @return
	 */
	public Map<String, Object> apTag(List<Map<String, Object>> list,String url);
	
	/**
	 * 功能描述：检查要发送数据的标签区域是否对应
	 * @param map
	 * @return
	 */
	public Map<String, Object> checkRegionLabel(List<Map<String, Object>> list,String url);
	
	/**
	 * 功能描述：发送标签显示数据
	 * @param map
	 * @return
	 */
	public Map<String, Object> labelData(List<Map<String, Object>> list,String url);
	
	
	/**
	 * 功能描述：通过事件号查询发送结果
	 * @param map
	 * @return
	 */
	public Map<String, Object> sendResult(String url);
	
	/**
	 * 功能描述：查询区域下基站号和IP
	 * @param map
	 * @return
	 */
	public Map<String, Object> getRegionAP(String url);
	
	
	/**
	 * 功能描述：查询区域下全部标签最后一次通讯状态
	 * @param map
	 * @return
	 */
	public Map<String, Object> getRegionLabel(String url);
	 
	/**
	 * 功能描述：查询标签最后一次通讯状态
	 * @param map
	 * @return
	 */
	public Map<String, Object> getLabel(String labelNo,String url);
	
}
