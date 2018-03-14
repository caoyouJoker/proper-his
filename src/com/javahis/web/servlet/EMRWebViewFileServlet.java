package com.javahis.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.javahis.web.bean.EmrFileIndexBean;
import com.javahis.web.util.CommonUtil;

public class EMRWebViewFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public EMRWebViewFileServlet() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); 
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String viewType = request.getParameter("VIEW_TYPE");
		//文件服务器EmrData目录绝对路径
        String EmrDataDir = TIOM_FileServer.getRoot() + TIOM_FileServer.getPath("EmrData");
        //文件服务器EmrTemplet目录绝对路径
        String EmrTempletDir = TIOM_FileServer.getRoot() + TIOM_FileServer.getPath("EmrTemplet");
        TSocket socket = TIOM_FileServer.getSocket();
        if ("ONE".equals(viewType)) {
        	//查看单个病历
			String viewPattern = request.getParameter("VIEW_PATTERN");
			String caseNo = request.getParameter("CASE_NO");
			String fileSeq = request.getParameter("FILE_SEQ");
			EmrFileIndexBean bean = new EmrFileIndexBean();
			bean.setViewType(viewType);
			bean.setViewPattern(viewPattern);
			bean.setCaseNo(caseNo);
			bean.setFileSeq(fileSeq);
	        TParm result = CommonUtil.getEmrFileIndexObject(caseNo, fileSeq);
	        
	        //浏览格式
			if("HTML".equals(viewPattern)) {
				String filePath = result.getValue("FILE_PATH", 0).replace("JHW", "XML");
				String fileName = result.getValue("FILE_NAME", 0);
				//验证xml文件是否存在
				String path = EmrDataDir + filePath + "\\" + fileName + ".xml";
		    	path = path.replace("\\", "/");
		    	if (CommonUtil.isFileExists(path)) {
		    		//组装xml文件相对路径
			    	String xmlPath = "/EmrData/" + filePath + "\\" + fileName + ".xml";
					String xslPath = "/web/jsp/emr/xml/cdaemr.xsl";
					xmlPath = xmlPath.replace("\\", "/");
					xslPath = xslPath.replace("\\", "/");
					bean.setXmlPath(xmlPath);
					bean.setXslPath(xslPath);
		    	}
			}
			if("PDF".equals(viewPattern)) {
				String filePath = result.getValue("FILE_PATH", 0).replace("JHW", "PDF");
				String fileName = result.getValue("FILE_NAME", 0);
				//验证pdf文件是否存在
				String path = EmrDataDir + filePath + "\\" + fileName + ".pdf";
		    	path = path.replace("\\", "/");
		    	
		    	System.out.println("path:"+path);

		    	if (CommonUtil.isFileExists(path)) {
		    		//组装pdf文件相对路径
					//String pdfPath = "/EmrData/" + filePath + "\\" + fileName + ".pdf";
					//pdfPath = pdfPath.replace("\\", "/");
					bean.setPdfPath(path);
				}
			}
			if("JHW".equals(viewPattern)) {
				String tmpFilePath = EmrDataDir + "tmpFile.x";
				String content = "";
				
		        //add by yangjj 20161027
		        String type = request.getParameter("TYPE");
		        if("LEVEL".equals(type)){
		        	content = request.getParameter("LEVEL_FILE_PATH");
		        }else{
		        	String filePath = result.getValue("FILE_PATH", 0);
					String fileName = result.getValue("FILE_NAME", 0);
					content = caseNo + "," + fileSeq + "," + filePath + "," + fileName;
		        }
		        
		        TIOM_FileServer.writeFile(socket, tmpFilePath, content.getBytes());
		        
		        
			}
			if("CDA".equals(viewPattern)) {
				String filePath = result.getValue("FILE_PATH", 0).replace("JHW", "XML");
				String fileName = result.getValue("FILE_NAME", 0);
				String path1 = EmrDataDir + filePath + "\\" + fileName + "_CDA.xml";
		    	path1 = path1.replace("\\", "/");
		    	String templetPath = result.getValue("TEMPLET_PATH", 0);
		    	String emtFileName = result.getValue("EMT_FILENAME", 0);
		    	String path2 = EmrTempletDir + templetPath + "\\" + emtFileName + ".xsl";
		    	path2 = path2.replace("\\", "/");
		    	if (new File(path1).exists() && new File(path2).exists()) {
		    		String cdaXml = CommonUtil.changToXML(path1, path2);
			    	bean.setCdaXml(cdaXml);
		    	}
			}
			
			JSONObject jsonObject = JSONObject.fromObject(bean);
			out.println(jsonObject.toString());
			out.flush();
			out.close();
		} else if ("ALL".equals(viewType)) {
			//查看合并病历
			String viewPattern = request.getParameter("VIEW_PATTERN");
			String caseNo = request.getParameter("CASE_NO");
			String mrNo = request.getParameter("Mr_No");
			String fileSeq = request.getParameter("FILE_SEQ");
			EmrFileIndexBean bean = new EmrFileIndexBean();
			bean.setViewType(viewType);
			bean.setViewPattern(viewPattern);
			bean.setCaseNo(caseNo);
			bean.setMrNo(mrNo);
			bean.setFileSeq(fileSeq);
	        TParm result = CommonUtil.getEmrFileIndexObject(caseNo, fileSeq);
	        String filePath = result.getValue("FILE_PATH", 0).replace("JHW", "PDF");
			String fileName = result.getValue("FILE_NAME", 0);
			//验证pdf文件是否存在
			String path = EmrDataDir + filePath + "\\" + fileName + ".pdf";
	    	path = path.replace("\\", "/");
	    	if (CommonUtil.isFileExists(path)) {
	    		//组装pdf文件相对路径
				String pdfPath = "/EmrData/" + filePath + "\\" + fileName + ".pdf";
				pdfPath = pdfPath.replace("\\", "/");
				bean.setPdfPath(pdfPath);
			}
	    	JSONObject jsonObject = JSONObject.fromObject(bean);
			out.println(jsonObject.toString());
			out.flush();
			out.close();
		} else {
			//HTML病历解析
			if ("HTML".equals(viewType)) {
				String xmlPath = request.getParameter("xmlPath");
				xmlPath = URLDecoder.decode(xmlPath,"UTF-8");  
				String xslPath = request.getParameter("xslPath");
				request.setAttribute("xmlPath", xmlPath);
				request.setAttribute("xslPath", xslPath);
				request.getRequestDispatcher("jsp/emr/xml/xml.jsp").forward(request, response);
			} else if ("XML".equals(viewType)) {
		        String xmlPath = request.getParameter("xmlPath");
		        xmlPath = URLDecoder.decode(xmlPath,"UTF-8");  
				String uri = EmrDataDir + xmlPath.replace("/EmrData/", "");
				uri = uri.replace("\\", "/");
				String xml = CommonUtil.getXMLString(uri);
				out.println(xml);
				out.flush();
				out.close();
			} else if ("XSL".equals(viewType)) {
				String xslPath = request.getParameter("xslPath");
				String uri = request.getRealPath("/") + xslPath.replace("/web/", "");
				uri = uri.replace("\\", "/");
				String xsl = CommonUtil.getXMLString(uri);
				out.println(xsl);
				out.flush();
				out.close();
			} else if("PDF".equals(viewType)){
				String pdfPath = request.getParameter("pdfPath");
				pdfPath = URLDecoder.decode(pdfPath,"UTF-8");  
				request.setAttribute("pdfPath", pdfPath);
				request.getRequestDispatcher("jsp/emr/pdf/pdf.jsp").forward(request, response);
			}
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); 
		doGet(request, response);
	}

	public void init() throws ServletException {

	}

}
