package jdo.bil;

import java.util.Vector;

import com.dongyang.data.TParm;

public class String2TParmTool {

	private static final String KEYVALUELINE = ":#PKs:";
	private static final String VECTORLINE = ":d!#Vec#:";
	private static final String VECTORVALUELINE = "#VVDt#";
	private static final String EQUAL = ":eql:";
	private static final String DATALINE = "@@";
	private static final String KEYLINE = ";";
	private static final String NULLSTR = "null";

	public static void main(String[] args) {
		TParm parm = new TParm();
		parm.setData("CTZ3_CODE", 1.1111111);
		parm.setData("B", "2");
		parm.addData("B", "3");
		parm.addData("B", "3");
		parm.addData("B", "3");
		parm.addData("B", "3");
		parm.addData("B", "3");
		parm.addData("B", "3");
		parm.addData("C-DATE", "2013-07-26 23:59:59");// 需要变字符串 没测试过timestamp
		parm.addData("D-STR",
				"12345679890-=QWERTYUIOP[]SSSFGDGHFJGJEWEADAUWHRKJNF");// 字符串只要不是太奇怪的组合都没问题
		String str = tparm2String(parm);
		System.out.println("tparm2String-- " + str);
//		str = "BED_NO:eql:S13Y0305@@CTZ3_CODE:eql:@@FLG:eql:ADD@@CTZ2_CODE:eql:@@CTZ1_CODE:eql:12:#PKs:BED_NO;CTZ3_CODE;FLG;CTZ2_CODE;CTZ1_CODE";
		TParm parmR = string2Ttparm(str);
		System.out.println("string2Ttparm-- " + parmR);
	}

	public static String tparm2String(TParm parm) {
		String[] s = parm.getNames();
		StringBuilder sb = new StringBuilder();
		StringBuilder sbk = new StringBuilder(KEYVALUELINE);
		for (int i = 0; i < s.length; i++) {
			if (parm.getData(s[i]) instanceof Vector) {
				sb.append(s[i] + VECTORLINE);
				Vector v = (Vector) parm.getData(s[i]);
				for (int j = 0; j < v.size(); j++) {
					sb.append(VECTORVALUELINE + v.get(j));
				}
			} else {
				String sv = ""+parm.getData(s[i]);
				sb.append(s[i] + EQUAL + (sv.length()==0?NULLSTR:sv));
			}
			sbk.append(s[i]);
			if (i != s.length - 1) {
				sb.append(DATALINE);
				sbk.append(KEYLINE);
			}
		}
		return sb.toString() + sbk.toString();
	}

	public static TParm string2Ttparm(String str) {
		TParm parm = new TParm();
		String keys = str.split(KEYVALUELINE)[1];
		String[] keyList = keys.split(KEYLINE);
		String valuestr = str.split(KEYVALUELINE)[0];
		String[] strs = valuestr.split(DATALINE);
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].contains(EQUAL)) {
				System.out.println(strs[i]);
				System.out.println(strs[i].split(keyList[i] + EQUAL).length);
				parm.setData(keyList[i], (strs[i].split(keyList[i] + EQUAL)[1].equals(NULLSTR)?null:strs[i].split(keyList[i] + EQUAL)[1]));
			}
			if (strs[i].contains(VECTORLINE)) {
				String vvalues = strs[i].split(keyList[i] + VECTORLINE)[1];
				String[] vvalue = vvalues.split(VECTORVALUELINE);
				Vector v = new Vector();
				for (int j = 1; j < vvalue.length; j++) {
					v.add(vvalue[j]);
				}
				parm.setData(keyList[i], v);
			}

		}
		return parm;
	}
}
