package com.javahis.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.caigen.security.e;
import com.javahis.web.form.SysEmrIndexForm;
import com.javahis.web.jdo.CommonTool;
import com.javahis.web.util.CommonUtil;

public class EMRWebQueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public EMRWebQueryServlet() {
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
		String SysEmrIndexTree = CommonUtil.getSysEmrIndexTree(form);
		
		//add by yangjj 20161017
		String type = request.getParameter("TYPE");
		if("level".equals(type)){
			try {
				SysEmrIndexTree = removeOtherCaseNo(SysEmrIndexTree,form.getCase_no());
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		out.print(SysEmrIndexTree);
		out.flush();
		out.close();
	}
	
	public String removeOtherCaseNo(String str , String caseNo) throws DocumentException{
		Document document = DocumentHelper.parseText(str);
		System.out.println("str:"+str);
		Element root = document.getRootElement();
		
		List nodes = root.elements("item");
		Element e = null;
		Element e2 = null;
		for(Object obj : nodes){
			e = (Element) obj;
			List items = e.elements("item");
			for(Object obj2 : items){
				e2 = (Element) obj2;
				if(!caseNo.equals(e2.attributeValue("id"))){
					e.remove(e2);
				}
			}
		}
		
		return document.asXML().trim();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void init() throws ServletException {

	}

}
