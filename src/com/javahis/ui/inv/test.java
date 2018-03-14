package com.javahis.ui.inv;
//  
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//
//
//import com.dongyang.control.TControl;
//import com.dongyang.data.TParm;
//
//public class test extends TControl{
//
//	public static void main(String[] args) {
//		String[] a1 = {"aaa","aaa","aa","aa","a"};
//		String[] b1 = {"bbb","bbb","bbb","bb","bbb"};
//		String[] c1 = {"ccc","ccc","cc","ccc","cc"};
//		int qty = 0;
//		Map map = new HashMap(); 
//		Map mapInvCode = new HashMap();
//		Map mapBatchNo = new HashMap();
//		Map mapVaildate = new HashMap();
//		Map mapParm = new HashMap();
//		String a2="";
//		String b2="";
//		String c2="";
//		TParm parm = new TParm();
//		for (int j = 0; j <2; j++) {
//			parm.setData("DATE", j, a1[j]);
//		for(int i =0 ;i<5;i++){ 
//			 a2=a1[i];
//			 b2=b1[i];
//			 c2=c1[i]; 
//			if(map.containsKey(a2+"+"+b2+"+"+c2)){
//
//				qty =(Integer) map.get(a2+"+"+b2+"+"+c2);
//				qty = qty + 1;
//				System.out.println("qty"+qty);
//				map.remove(a2+"+"+b2+"+"+c2);
//				//map.put(a2+"+"+b2+"+"+c2, qty);
//				//qty = qty/2; 
//				map.put(a2+"+"+b2+"+"+c2, qty);
//				mapInvCode.put(a2+"+"+b2+"+"+c2, a2);
//				mapBatchNo.put(a2+"+"+b2+"+"+c2, b2); 
//				mapVaildate.put(a2+"+"+b2+"+"+c2, c2);
//				mapParm.put(a2+"+"+b2+"+"+c2, j);
//			}else{   
//				//map.put(a2+"+"+b2+"+"+c2, 1);   
//				map.put(a2+"+"+b2+"+"+c2, 1);
//				mapInvCode.put(a2+"+"+b2+"+"+c2, a2);
//				mapBatchNo.put(a2+"+"+b2+"+"+c2, b2);
//				mapVaildate.put(a2+"+"+b2+"+"+c2, c2);
//				mapParm.put(a2+"+"+b2+"+"+c2, j);
//			}									
//		}		
//		} 
//		System.out.println(map);	  
//		System.out.println(mapInvCode);
//		System.out.println(mapBatchNo);
//		System.out.println(mapVaildate);
//		System.out.println(mapParm);
//		System.out.println(map.size()); 
//		System.out.println(mapInvCode.size());
//		System.out.println(mapBatchNo.size()); 
//		System.out.println(mapVaildate.size());   
//		System.out.println(mapParm.size()); 
//		
//		Object s[] = map.keySet().toArray();
//		Object sInv[] = mapInvCode.keySet().toArray(); 
//		Object sBatch[] = mapBatchNo.keySet().toArray();
//		Object sVaild[] = mapVaildate.keySet().toArray();
//		for(int i = 0; i < map.size(); i++) {
//			if(i>0){
//				
//			}
//			else{
//				
//			}
//			
//		}
//		
////		for(Map.Entry<String, List> entry : map.entrySet()) {
////            System.out.println(entry.getKey());
////            List ls=entry.getValue();         
////        }
////        Iterator it = map.keySet().iterator();
////        while(it.hasNext()){
////            String key = (String) it.next();
////            List value = map.get(key);
////            }
//	}
//}



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;           
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
 
public class test {
    public static void main(String[] args) {
//        Map<String, Integer> map = new HashMap<String, Integer>();
//        map.put("ee", 3);
//        map.put("b", 1);
//        map.put("d", 2);
//        map.put("eee", 3);
//        map.put("A", 1);
//        map.put("K", 2);
//        map.put("ade", 1);
//        map.put("c", 2);
//        map.put("aee", 3);
//        map.put("a", 1);
//        map.put("faed", 2);
//        map.put("bdd", 1);
//        map.put("qec", 2);
//        map.put("eade", 3);
//        map.put("Aadf", 1);
//        map.put("Kqe", 2);
// 
//        Map<String, Integer> sortMap = new test().sortMap(map);
// 
//        for(Map.Entry<String, Integer> entry : sortMap.entrySet()) {
//            System.out.println(entry.getKey() + " --> " + entry.getValue());
//        }
//    }
// 
//    public <K, V extends Number> Map<String, V> sortMap(Map<String, V> map) {
//        class MyMap<M, N> {
//            private M key;
//            private N value;
//            private M getKey() {
//                return key;
//            }
//            private void setKey(M key) {
//                this.key = key;
//            }
//            private N getValue() {
//                return value;
//            }
//            private void setValue(N value) {
//                this.value = value;
//            }
//        }
// 
//        List<MyMap<String, V>> list = new ArrayList<MyMap<String, V>>();
//        for (Iterator<String> i = map.keySet().iterator(); i.hasNext(); ) {
//            MyMap<String, V> my = new MyMap<String, V>();
//            String key = i.next();
//            my.setKey(key);
//            my.setValue(map.get(key));
//            list.add(my);
//        }
// 
//        Collections.sort(list, new Comparator<MyMap<String, V>>() {
//            public int compare(MyMap<String, V> o1, MyMap<String, V> o2) {
//                if(o1.getValue() == o2.getValue()) {
//                    return o1.getKey().compareTo(o2.getKey());
//                }else{
//                    return (int)(o1.getValue().doubleValue() - o2.getValue().doubleValue());
//                }
//            }
//        });
// 
//        Map<String, V> sortMap = new LinkedHashMap<String, V>();
//        for(int i = 0, k = list.size(); i < k; i++) {
//            MyMap<String, V> my = list.get(i);
//            sortMap.put(my.getKey(), my.getValue());
//        }
//        return sortMap;
//    }
    	 
    	
    	Map<String, Integer> map = new HashMap<String, Integer>();
    	map.put("d", 2);
    	map.put("c", 1);
    	map.put("b", 1);
    	map.put("a", 3); 

    	List<Map.Entry<String, Integer>> infoIds =
    	    new ArrayList<Map.Entry<String, Integer>>(map.entrySet());

    	//≈≈–Ú«∞
    	for (int i = 0; i < infoIds.size(); i++) {
    	    String id = infoIds.get(i).toString();
    	    System.out.println(id);
    	}
    	//d 2
    	//c 1
    	//b 1
    	//a 3

    	//≈≈–Ú
    	Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {   
    	    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {      
    	        //return (o2.getValue() - o1.getValue()); 
    	        return (o1.getKey()).toString().compareTo(o2.getKey());
    	    }
    	}); 

    	//≈≈–Ú∫Û
    	for (int i = 0; i < infoIds.size(); i++) {
    	    String id = infoIds.get(i).toString();
    	    System.out.println(id);
    	}
    	
    	
    }
    	
    	
    	
    	
}
