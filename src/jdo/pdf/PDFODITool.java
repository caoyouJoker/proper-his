package jdo.pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.FileTool;
import com.javahis.util.StringUtil;

public class PDFODITool extends PdfTool {
	/**
	 * 下载临时数据到本地
	 * 
	 * @param mrno
	 *            String
	 * @param caseno
	 *            String
	 * @param path
	 *            String
	 * @return boolean
	 */
	public boolean downLoadTempFile(String filePath, String tempPath, TParm parm,List list) {
		if (!filePath.endsWith("/"))
			filePath += "/";
		if (!tempPath.endsWith("/"))
			tempPath += "/";

		String s[] = listPDFFile(filePath);
		for (int parmIndex = 0; parmIndex < parm.getCount("FileName"); parmIndex++) {

			for (int i = 0; i < s.length; i++) {
				if (!StringUtil.isNullString(s[i])
						&& s[i].equals(parm.getData("FileName", parmIndex)) && "Y".equalsIgnoreCase(parm.getData("FLG", parmIndex).toString())) {
					byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
							.getSocket(), filePath + "\\" + s[i]);
					//s[i] = "";  
					if (data == null)
						break;
					try {
						if(StringUtil.isNullString((String)parm.getData("ORDER_DESC", parmIndex))){
							list.add(parm.getData("Type", parmIndex));
						}else{
							list.add(parm.getData("ORDER_DESC", parmIndex));
						}
						
						FileTool.setByte(tempPath + parmIndex + ".pdf", data);
						break;
					} catch (Exception e) {
						break;
					}
				}
			}
		}
		return true;
	}

	public boolean downLoadTempFile1(String filePath, String tempPath) {
		if (!filePath.endsWith("/"))
			filePath += "/";
		if (!tempPath.endsWith("/"))
			tempPath += "/";

		String s[] = listPDFFile(filePath);
		for (int i = 0; i < s.length; i++) {
			byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					filePath + "\\" + s[i]);
			if (data == null)
				return false;
			try {
				String[] str = s[i].split("_");
				FileTool.setByte(tempPath + str[str.length - 1], data);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 列表文件
	 * 
	 * @param mrno
	 *            String
	 * @param caseno
	 *            String
	 * @return String[]
	 */
	public String[] listPDFFile(String filePath) {
		return TIOM_FileServer.listFile(TIOM_FileServer.getSocket(), filePath);
	}

	/**
	 * 合并病历文件
	 * 
	 * @param parm
	 *            TParm
	 * @param path
	 *            String
	 * @param caseno
	 *            String
	 * @return TParm
	 */
	public TParm addPdf(TParm parm, String path, String caseno) {
		//System.out.println("--------come in---------");
		TParm p = new TParm();
		File f = new File(path);
		String[] listFile=f.list();
		if (!f.exists())
			f.mkdirs();
		int c = parm.getCount("FileName");
		// 下载执行文件
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				getRoot() + "\\pdftk");
		if (data == null) {
			p.setErr(-1, "服务器上没有找到文件 " + getRoot() + "\\pdftk");
			return p;
		}
		try {
			FileTool.setByte(path + "\\pdftk.exe", data);
		} catch (Exception e) {
			p.setErr(-1, e.getMessage());
			return p;
		}
		String s = "";
		// 小于1000份病历
		if (c <= 1000) {
			// 制作批处理文件
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < c; i++) {
				for (int j = 0; j < listFile.length; j++) {
					if ((i + ".pdf").equals(listFile[j]))
						sb.append(i + ".pdf ");
					// break;
				}
			}
			
			s = path.substring(0, 2) + " \n" + "cd " + path + " \n"
					+ "pdftk.exe " + sb.toString() + " cat output " + caseno
					+ ".pdf \n exit ";
		//  add by lx 2013/01/18 大于1000份病历处理
		} else {
			s = path.substring(0, 2) + " \n" + "cd " + path + " \n";
			//制作批处理文件
			StringBuffer sb = new StringBuffer();		
			//						
			int sum=0;			
			for (int i = 0; i < c; i++) {				
				//假如小于1000			
				for (int j = 0; j < listFile.length; j++) {
					if ((i + ".pdf").equals(listFile[j]))
						sb.append(i + ".pdf ");
					// break;					
				}
				//add by lx 2013/01/18 假如多个的时候
					if((i!=0)&&(i%1000)==0){
						s+= "pdftk.exe " + sb.toString() + " cat output " + caseno+"_"+sum
						+ ".pdf \n";
						//System.out.println("------s111------"+s);
						//
						sb=null;
						sb=new StringBuffer();
						sum++;
					}
				//或者取后
				if(i==c-1){
					s+= "pdftk.exe " + sb.toString() + " cat output " + caseno+"_"+sum
					+ ".pdf \n";
					//System.out.println("----s最后------"+s);
					//sum++;
				}
				
			}
			//System.out.println("----sum-----"+sum);
			s+= "pdftk.exe " ;
			//汇总所有的pdf
			for(int z=0;z<=sum;z++){
				//System.out.println("----z-----"+z);
				s+= caseno+"_"+z+".pdf ";
			}
			
			s+="cat output " + caseno
			+ ".pdf \n exit";
			/*s+= "pdftk.exe " + sb.toString() + " cat output " + caseno
				+ ".pdf \n exit ";*/
			//System.out.println("==final s=="+s);
		}
		try {
			FileTool.setByte(path + "/pdf.bat", s.replaceAll("/", "\\\\")
					.getBytes());
		} catch (Exception e) {
			p.setErr(-1, e.getMessage());
			return p;
		}
		// 执行批处理文件
		p = new TParm(exec(path + "\\","pdf.bat",caseno));//修改方法
		if (p.getErrCode() != 0)
			return p;
		return p;
	}
	
	/**
	 * add by lx 新增方法
	 * 执行批处理合并操作
	 * @param com
	 * @param caseNo
	 * @return
	 */
	public Map exec(String path,String batFile,String caseNo){
		//1.清除之前合并后的文件
		this.deleteFiles(path, caseNo);
		//2.客户机合并文件
		TParm reset = new TParm();
		try {
			String command = "cmd /k "+path+batFile;
			Process proc = Runtime.getRuntime().exec(command);
			proc.getOutputStream().close();
			StreamGobbler errorGobbler = new StreamGobbler(proc
					.getErrorStream(), "Error");
			StreamGobbler outputGobbler = new StreamGobbler(proc
					.getInputStream(), "Output");
			errorGobbler.start();
			outputGobbler.start();

			int a = proc.waitFor();
			//System.out.println("-----a-------" + a);// 0 是成功 //1是失败
			if (a != 0) {
				// System.out.println("errorGobbler"+errorGobbler.getErrors());
				String strErrors="";
				for (String v : errorGobbler.getErrors()) {
					//System.out.println("------Errors-----" + v);
					strErrors+=v+"\n";
				}
				reset.setErr(-1, strErrors);
			}else{
				//reset.setErr(-1, strErrors)
				//System.out.println("-----success------");
			}
		} catch (Exception e) {
			reset.setErr(-1, e.getMessage());
		}
		//
		return reset.getData();
	}

	public Map exec(String com) {
		TParm reset = new TParm();
		try {
			Runtime rt = Runtime.getRuntime();
			Process p=rt.exec( "cmd   /c start "+com);
			//p=rt.exec( "cmd   /c start "+com);
			// 输出屏幕结果;
//			try {
//				// 阻塞等待完全完成;
//				p.waitFor();
//				p.destroy();
//			} catch (InterruptedException e) {
//				// e.printStackTrace();
//				reset.setErr(-1, e.getMessage());
//			}

		} catch (Exception e) {
			reset.setErr(-1, e.getMessage());
		}
		return reset.getData();
	}

	/**
	 * 输出
	 * 
	 * @param input
	 *            InputStream
	 * @return String
	 */
	public String out(InputStream input) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String s = br.readLine();
			while (s != null) {
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(s);
				s = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
    /**
     * 插入病历主档
     * @param parm TParm
     * @return TParm
     */
    public TParm insertMRV(TParm parm){
        //判断是否存在病历主档 如果存在则不新建
        String select = "SELECT MR_NO,CASE_NO FROM MRO_MRV_TECH WHERE MR_NO='"+parm.getValue("MR_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"'";
        //System.out.println("selectSQL:"+select);
        TParm re = new TParm(TJDODBTool.getInstance().select(select));
        //System.out.println("re:"+re);
        if(re.getCount("MR_NO")>0){//已存在病历主档
            return new TParm();
        }
    	String insert = "insert into MRO_MRV_TECH (MR_NO,CASE_NO,CHECK_FLG,IN_FLG, "
			+ " BOX_CODE,"
			+ " OPT_USER,OPT_TERM) values ("
			+ " '"
			+ parm.getValue("MR_NO")
			+ "','"
			+ parm.getValue("CASE_NO")
			+ "','"
			+ parm.getValue("CHECK_FLG")
			+ "','"
			+ parm.getValue("IN_FLG")
			+ "',"
			+ " '"
			+ parm.getValue("BOX_CODE")
			+ "',"
			+ " '"
			+ Operator.getID() + "','" + Operator.getIP() + "')";
        //System.out.println("insertSQL:"+insert);
        TParm result = new TParm(TJDODBTool.getInstance().update(insert));
        //System.out.println("result:"+result);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
