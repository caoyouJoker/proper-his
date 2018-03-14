package jdo.nss;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;

/**
 * 
 * <p>
 * Title: 肠内营养配方细项DataStore
 * </p>
 * 
 * <p>
 * Description: 肠内营养配方细项DataStore
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wangb 2015/06/01
 * @version 1.0
 */
public class NSSENFormulaDataStore extends TDataStore {

	/**
	 * 得到其他列数据
	 * @param parm TParm
	 * @param row int
	 * @param column String
	 * @return Object
	 */
	public Object getOtherColumnValue(TParm parm, int row, String column) {
		String nutritionCode = parm.getValue("NUTRITION_CODE", row);
		TParm result = new TParm();
		
		if (StringUtils.isNotEmpty(nutritionCode)) {
			TParm queryParm = new TParm();
			queryParm.setData("NUTRITION_CODE", nutritionCode);
			result = NSSEnteralNutritionTool.getInstance().selectDataCF(queryParm);
		}
		
		if (StringUtils.equals("NUTRITION_CHN_DESC", column)) {
			return result.getValue("NUTRITION_CHN_DESC", 0);
		} else if (StringUtils.equals("NUTRITION_ENG_DESC", column)) {
			return result.getValue("NUTRITION_ENG_DESC", 0);
		} else if (StringUtils.equals("UNIT_CODE", column)) {
			return result.getValue("UNIT_CODE", 0);
		} else if (StringUtils.equals("FLG", column)) {
			return "N";
		}
		return "";
	}
}
