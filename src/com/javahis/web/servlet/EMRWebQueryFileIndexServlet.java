package com.javahis.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdo.sys.Operator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.w3c.dom.Node;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.javahis.ui.mro.MROLevelQueryControl;
import com.javahis.web.form.SysEmrIndexForm;
import com.javahis.web.jdo.CommonTool;
import com.javahis.web.util.CommonUtil;

public class EMRWebQueryFileIndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public EMRWebQueryFileIndexServlet() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		SysEmrIndexForm form = CommonUtil.getForm(request, response);
		String EmrFileIndexTree = CommonUtil.getEmrFileIndexTree(form);
		
		String type = request.getParameter("TYPE");
		if("level".equals(type)){
			String caseNo = form.getCase_no();
			String mrNo = form.getMr_no();
			String viewPattern = form.getView_pattern();
			String user = request.getParameter("USER_ID");
			System.out.println("user:"+user);
			EmrFileIndexTree = getEmr(caseNo,viewPattern,mrNo,user);
		}
		
		System.out.println("EmrFileIndexTree:"+EmrFileIndexTree);
		
		out.print(EmrFileIndexTree);
		out.flush();
		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void init() throws ServletException {

	}
	
	public String getEmr(String caseNo,String viewPattern,String mrNo,String user){
		//获取EmrData文件路径
		TSocket tSocket = TIOM_FileServer.getSocket();
		String EmrDataDir = TConfig.getSystemValue("FileServer.Main.Root")+ "/" + TIOM_FileServer.getPath("EmrData");
		String dir = (EmrDataDir +viewPattern+"/"+caseNo.substring(0, 2)+"/"+caseNo.substring(2, 4)+"/"+mrNo).replace("\\", "/");
		
		
		String[] files = new String[0];
		files = TIOM_FileServer.listFile(tSocket, dir);
		if(files == null){
			files = new String[0];
		}
		
		System.out.println("files:"+Arrays.toString(files));
		
		
		List<String> lstFileDir = null;
		Map<String , List<String>> map = new HashMap<String,List<String>>();
		
		//获取当前角色所能看见的病历类型
		TParm emrSortDic = getEmrSortDic(user);
		
		//根据病历类型获取caseNo对应的病历文件
		for(int i = 0 ; i < emrSortDic.getCount() ; i++){
			
			String emrSortDicName = emrSortDic.getValue("EMR_SORTDIC_NAME", i);
			String emrSortDicCode = emrSortDic.getValue("EMR_SORTDIC_CODE", i);
			
			lstFileDir = new ArrayList<String>();
			for(int j = 0 ; j < files.length ; j++){
				TParm emrSortDicDetail = getEmrSortDicDetail(emrSortDicCode);
				
				for(int k = 0 ; k < emrSortDicDetail.getCount() ; k++){
					if(files[j].contains((caseNo+"_"+emrSortDicDetail.getValue("FILENAME_KEYWORD", k)))){
						if("JHW".equals(viewPattern)){
							System.out.println("lstFileDir:"+dir+"/"+files[j]+","+files[j]);
							lstFileDir.add(dir+"/"+files[j]+","+files[j]);
						}else if("PDF".equals(viewPattern)){
							lstFileDir.add(TConfig.getSystemValue("MRO_VIRTUAL_PATH")+viewPattern+"/"+caseNo.substring(0, 2)+"/"+caseNo.substring(2, 4)+"/"+mrNo+"/"+files[j]+","+files[j]);
						}
					}
				}
			}
			
			map.put(emrSortDicCode+","+emrSortDicName, lstFileDir);
		}
	
		System.out.println("emr map:"+map);
		
		return getXml(map);
	}
	
	public String getXml(Map<String,List<String>> map){
		String xml = "";
		
		Document document = DocumentHelper.createDocument();
		
		Element tree = document.addElement("tree");
		tree.addAttribute("id", "0");
		
		//创建根节点
		Element item0 = tree.addElement("item");
		item0.addAttribute("ROOT", "ROOT");
		item0.addAttribute("call", "1");
		item0.addAttribute("id", "ROOT");
		item0.addAttribute("open", "1");
		item0.addAttribute("select", "1");
		item0.addAttribute("text", "电子病历");
		
		//遍历map中的所有节点
		Set<String> set = map.keySet();
		for(String key : set){
			//创建病历分类节点
			Element item1 = item0.addElement("item");
			
			String[] k = key.split(",");
			item1.addAttribute("id", k[0]);
			item1.addAttribute("text", k[1]);
			
			List<String> lst = map.get(key);
			for(String value : lst){
				String[] v = value.split(",");
				Element item2 = item1.addElement("item");
				item2.addAttribute("id", v[0]+";leaf");
				item2.addAttribute("text", v[1]);
			}
			
		}
		
		
		
		
		
		
		xml = document.asXML();
		
		System.out.println("xml:"+xml);
		
		
		return xml;
	}
	
	public TParm getEmrSortDic(String user){
		String sql = " SELECT * FROM EMR_SORTDIC WHERE EMR_SORTDIC_CODE IN ("+getEmrSortDicStr(user)+") ORDER BY SORT ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public TParm getEmrSortDicDetail(String code){
		String sql = " SELECT * FROM EMR_SORTDIC_DETAIL WHERE EMR_SORTDIC_CODE='"+code+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public String getEmrSortDicStr(String user){
		String str = "";
		Set<String> emrSet = new HashSet<String>();
		
		TParm parm = getLevel(user);
		emrSet = (Set<String>) parm.getData("EMR");
		
		if(emrSet.size() > 0){
			for(String s : emrSet){
				str += "'"+s+"',";
			}
			
			str = str.substring(0, str.length()-1);
		}else{
			str = "''";
					
		}
		
		return str;
	}
	
	public TParm getLevel(String user){
		TParm parm = new TParm();
		
		String authoritySql = " SELECT A.* FROM EMR_RULE_AUTHORITY A , EMR_RULE_USER B WHERE A.EMR_RULE_CODE = B.EMR_RULE_CODE AND B.USER_EMR_ID='"+user+"' ";
		TParm authority = new TParm(TJDODBTool.getInstance().select(authoritySql));
		Set<String> scopeSet = new HashSet<String>();
		Set<String> securitySet = new HashSet<String>();
		Set<String> emrSet = new HashSet<String>();
		TParm temp = null;
		
		for(int i = 0 ; i < authority.getCount() ; i++){
			temp = authority.getRow(i);
			scopeSet.add(temp.getValue("EMR_SCOPE_CODE"));
			securitySet.add(temp.getValue("SECURITY_CATEGORY_CODE"));
			emrSet.add(temp.getValue("EMR_CLASS_CODE"));
		}
		
		parm.setData("SCOPE", scopeSet);
		parm.setData("SECURITY", securitySet);
		parm.setData("EMR", emrSet);
		
		return parm;
	}

}
