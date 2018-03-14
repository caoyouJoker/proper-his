/**
 * @className Dd.java 
 * @author litong
 * @Date 2013-5-19 
 * @version V 1.0 
 */
package com.javahis.ui.inv;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jdo.clp.intoPathStatisticsTool;

import com.javahis.device.Uitltool;

/**
 * @author litong
 * @Date 2013-5-19 
 */
public class Dd {
	public static void main(String[] args) {
		//System.out.println(Uitltool.decode("313330363237303030303032"));
//		String cString="2111-11-11 11:11:11";
//		cString=cString.substring(0,10);administrator
//		System.out.println(cString);2R0300010002
		//System.out.println(Uitltool.encode("E30000101011"));
//		for (i nt i = 0; i < 100; i++) {
//			System.out.println("");
//		}
		
//		Map<String, String> map=new TreeMap<String, String>();
//		map.put("08.04.002122E", "");
//		map.put("08.01.00212EF", "");
//		
//		map.put("08.01.00212E", "");
//		map.put("08.02.00212G", "");
//		map.put("08.03.00212", "");
//		map.put("08.04.00212", "");
//		map.put("08.01.002112", "");
//		map.put("08.01.002312", "");
//		map.put("08.01.010212", "");
//		map.put("08.01.100212", "");
//		map.put("08.02.002212", "");
//		for (String string : map.keySet()) {
//			System.out.println(string);
//		}
		String string="2222/22/22";
		System.out.println(string.substring(0, 10).replaceAll("/", ""));
		System.out.println(new BigDecimal("2").multiply(new BigDecimal("4")));
		
		
	}
	private static int jisuan(int c) {
		//int
		return 1;
		
	}

}
