package jdo.pdf;

import com.dongyang.config.TConfig;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import java.io.File;
import com.dongyang.util.FileTool;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import java.io.ByteArrayOutputStream;
import org.pdfbox.util.PDFTextStripper;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;

import jdo.sys.PatTool;


public class PdfTool extends TJDODBTool{
    /**
     * 实例
     */
    private static PdfTool instanceObject;
    /**
     * 文件服务器根目录
     */
    private String root;
    /**
     * 得到实例
     * @return PdfTool
     */
    public static PdfTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new PdfTool();
        return instanceObject;
    }
    /**
     * 得到文件目录的根目录
     * @return String
     */
    public String getRoot()
    {
        if(root == null)
            root = TConfig.getSystemValue("FileServer.Main.Root") + "\\" +
                TConfig.getSystemValue("Pdf.Path");
        return root;
    }
    /**
     * 得到His文档文件位置
     * @return String
     */
    public String getHisPath()
    {
        return TConfig.getSystemValue("Pdf.HisPath");
    }
    /**
     * 得到病人数据的根目录
     * @param mrno String
     * @param caseno String
     * @return String
     */
    public String getPatPath(String mrno,String caseno)
    {
        mrno = StringTool.fill0(mrno,PatTool.getInstance().getMrNoLength()); //=======  chenxi
        String mrnoPath = mrno.substring(0,9);
        return getRoot() + "\\" + mrnoPath + "\\" + mrno + "\\" + caseno;
    }
    /**
     * 得到临时目录
     * @param mrno String
     * @param caseno String
     * @return String
     */
    public String getTempPath(String mrno,String caseno)
    {
        return getPatPath(mrno,caseno) + "\\Temp";
    }
    /**
     * 建目录
     * @param mrno String
     * @param caseno String
     * @return boolean
     */
    public boolean mkdir(String mrno,String caseno)
    {
        String path = getPatPath(mrno,caseno);
        return TIOM_FileServer.mkdir(TIOM_FileServer.getSocket(),path + "\\Temp");
    }
    /**
     * 列表文件
     * @param mrno String
     * @param caseno String
     * @return String[]
     */
    public String[] listFile(String mrno,String caseno)
    {
        String path = getPatPath(mrno,caseno);
        return TIOM_FileServer.listFile(TIOM_FileServer.getSocket(),path + "\\Temp");
    }
    /**
     * 采集HIS文件
     * @param mrno String
     * @param caseno String
     * @return String
     */
    public String loadHisFile(String mrno,String caseno)
    {
        if(isClientlink())
            return (String)callServerMethod(mrno,caseno);
        StringBuffer sb = new StringBuffer();
        String sql = "SELECT PDF_CODE,PDF_CHN_DESC,PDF_KEY,TRY_PATH FROM PDF_STR WHERE PDF_SRC = 'HIS' ORDER BY PDF_CODE";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if(parm.getErrCode() != 0)
        {
            sb.append(parm.getErrText());
            return sb.toString();
        }
        int count = parm.getCount();
        String hisPath = getHisPath();
        File f = new File(hisPath);
        if(!f.exists())
            return hisPath + " His文件目录不存在";
        String listFiles[] = getFileList(f.listFiles());
        String fileServerTempPath = getTempPath(mrno,caseno);
        for(int i = 0;i < count;i++)
        {
            String path = parm.getValue("TRY_PATH",i);
            String name = parm.getValue("PDF_CHN_DESC",i);
            if(path == null || path.length() == 0)
                continue;
            String fileName = findHisFile(listFiles,path,caseno);
            if(fileName == null || fileName.length() == 0)
                continue;
            String hj = getHJ(fileName);
            if(!TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(),fileServerTempPath + "\\" + name + "." + hj,
                                          hisPath + "\\" +fileName))
                sb.append("上传\"" + fileName + "\"文件失败!");
        }
        if(sb.length() == 0)
            return "OK";
        return sb.toString();
    }
    private String getHJ(String s)
    {
        int index = s.indexOf(".");
        if(index == -1)
            return "";
        return s.substring(index + 1).toLowerCase();
    }
    private String[] getFileList(File f[])
    {
        if(f == null)
            return new String[0];
        String s[] = new String[f.length];
        for(int i = 0;i < f.length;i++)
            s[i] = f[i].getName();
        return s;
    }
    private String findHisFile(String fileName[],String path,String caseno)
    {
        int index = path.indexOf("#CASE_NO#");
        if(index != -1)
        {
            path = path.substring(0,index) + caseno + path.substring(index + 9);
        }
        index = path.indexOf("#MAXNO#");
        if(index != -1)
        {
            String start = path.substring(0,index);
            String end = path.substring(index + 7);
            int id = -1;
            for(int i = 0;i < fileName.length;i++)
            {
                String s = fileName[i];
                if(!s.startsWith(start))
                    continue;
                if(!s.endsWith(end))
                    continue;
                String s1 = s.substring(index,s.length() - end.length());
                try{
                    int x = Integer.parseInt(s1);
                    if(id < x)
                        id = x;
                }catch(Exception e)
                {
                }
            }
            if(id == -1)
                return "";
            return start + id + end;
        }
        //处理没有序号的文件
        for(int i = 0;i < fileName.length;i++)
        {
            String s = fileName[i];
            if(path.equals(s))
                return path;
        }
        return "";
    }
    /**
     * 下载临时数据到本地
     * @param mrno String
     * @param caseno String
     * @param path String
     * @return boolean
     */
    public boolean downLoadTempFile(String mrno,String caseno,String path)
    {
        if(!path.endsWith("\\"))
            path += "\\";
        String s[] = listFile(mrno,caseno);
        String p = getTempPath(mrno,caseno);
        for(int i = 0;i < s.length;i++)
        {
            byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),p + "\\" + s[i]);
            if(data == null)
                return false;
            try{
                FileTool.setByte(path + s[i], data);
            }catch(Exception e)
            {
                return false;
            }
        }
        return true;
    }
    /**
     * 转换成PDF
     * @param path String
     */
    public void toPDF(String path)
    {
        jacobTool.Registry(path);
        File f = new File(path);
        String listFiles[] = getFileList(f.listFiles());
        for(int i = 0;i < listFiles.length;i++)
        {
            String s = listFiles[i];
            String hj = getHJ(s);
            if(!"doc".equals(hj))
                continue;
            String name = s.substring(0,s.length() - hj.length() - 1);
            File f1 = new File(name + ".pdf");
            if(f1.exists())
                f1.delete();
            jacobTool.print(path + "\\" + s);
        }
    }
    /**
     * 合并病历文件
     * @param parm TParm
     * @param path String
     * @param caseno String
     * @return TParm
     */
    public TParm addPdf(TParm parm,String path,String caseno)
    {
        TParm p = new TParm();
        File f = new File(path + "\\data");
        if(!f.exists())
            f.mkdirs();
        int count = parm.getCount();
        List list = new ArrayList();
        int c = 0;
        for(int i = 0;i < count;i++)
        {
            String state = parm.getValue("STATE",i);
            if(!"存在".equals(state))
                continue;
            String name = parm.getValue("PDF_CHN_DESC",i);
            f = new File(path + "\\" + name + ".pdf");
            if(!f.exists())
                continue;
            try{
                byte[] data = FileTool.getByte(path + "\\" + name + ".pdf");
                FileTool.setByte(path + "\\data\\" + c + ".pdf", data);
                list.add(name);
                c++;
            }catch(Exception e)
            {
            }
        }
        //下载执行文件
        byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),getRoot() + "\\pdftk");
        if(data == null)
        {
            p.setErr(-1,"服务器上没有找到文件 " + getRoot() + "\\pdftk");
            return p;
        }
        try{
            FileTool.setByte(path + "\\data\\pdftk.exe", data);
        }catch(Exception e)
        {
            p.setErr(-1,e.getMessage());
            return p;
        }
        //制作批处理文件
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i < c;i++)
            sb.append(i + ".pdf ");
        String s = path.substring(0,2) + "\r\n" +
            "cd " + path + "\\data\r\n" +
            "pdftk.exe " + sb.toString() + " cat output " + caseno + ".pdf";
        try{
            FileTool.setByte(path + "\\data\\pdf.bat", s.getBytes());
        }catch(Exception e)
        {
            p.setErr(-1,e.getMessage());
            return p;
        }
        //执行批处理文件
        p = new TParm(exec(path + "\\data\\pdf.bat"));
        if(p.getErrCode() != 0)
            return p;
        String ss = jacobTool.setBookmarks(path + "\\data\\" + caseno + ".pdf",list);
        if(ss != null && ss.length() > 0)
            p.setErr(-1,ss);
        return p;
    }
    public Map exec(String com)
    {
        TParm reset = new TParm();
        try
        {
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec(com);
            String err = out(p.getErrorStream());
            if(p.exitValue() == 1)
                reset.setErr(-1,err);
            if(err.length() > 0)
                reset.setData("err",err);
            else
            {
                String out = out(p.getInputStream());
                if (out.length() > 0)
                    reset.setData("out", out);
            }
        } catch (Exception e)
        {
            reset.setErr(-1,e.getMessage());
        }
        return reset.getData();
    }
    /**
     * 输出
     * @param input InputStream
     * @return String
     */
    public String out(InputStream input)
    {
        StringBuffer sb = new StringBuffer();
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String s = br.readLine();
            while (s != null)
            {
                if(sb.length() > 0)
                    sb.append("\n");
                sb.append(s);
                s = br.readLine();
            }
            br.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }
    /**
     * 得到pdf文件中的文字
     * @param filename String
     * @return String
     */
    public String getTextFromPdf(String filename)
    {
        try {
            FileInputStream is = new FileInputStream(filename);
            PDFParser parser = new PDFParser(is);
            parser.parse();
            PDDocument nbsp = parser.getPDDocument();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(out);
            PDFTextStripper stripper = new PDFTextStripper();

            stripper.writeText(nbsp, writer);
            writer.close();
            byte[] contents = out.toByteArray();
            String ts = new String(contents);
            return ts;
        }
        catch (Exception e)
        {
        }
        return "";
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
        String insert = "insert into MRO_MRV_TECH (MR_NO,CASE_NO,CREATE_HOSP,CHECK_FLG,IN_FLG, "+
                        " PRINT_FLG,CURT_HOSP,CURT_LOCATION,TRAN_HOSP,BOX_CODE,"+
                        " OPT_USER,OPT_DATE,OPT_TERM,MERGE_CODE,MERGE_DATE,SUBMIT_CODE,SUBMIT_DATE) values ("+
                        " '"+parm.getValue("MR_NO")+"','"+parm.getValue("CASE_NO")+"','"+parm.getValue("CREATE_HOSP")+"','"+parm.getValue("CHECK_FLG")+"','"+parm.getValue("IN_FLG")+"',"+
                        " '"+parm.getValue("PRINT_FLG")+"','"+parm.getValue("CURT_HOSP")+"','"+parm.getValue("CURT_LOCATION")+"','"+parm.getValue("TRAN_HOSP")+"','"+parm.getValue("BOX_CODE")+"',"+
                        " '"+parm.getValue("OPT_USER")+"',SYSDATE,'"+parm.getValue("OPT_TERM")+"',"+
                        " '"+parm.getValue("MERGE_CODE")+"',SYSDATE,"+
        				" '"+parm.getValue("SUBMIT_CODE")+"',SYSDATE)";
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
    /**
     * 判断PDF是否已经通过审核  true:已经审核过   false：没有审核
     * @param parm TParm
     * @return boolean
     */
    public int checkFlg(TParm parm){
        String select = "SELECT MR_NO,CASE_NO,CHECK_FLG FROM MRO_MRV_TECH WHERE MR_NO='"+parm.getValue("MR_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"'";
        //System.out.println("selectSQL:"+select);
        TParm re = new TParm(TJDODBTool.getInstance().select(select));
        if(re.getCount()<=0){//没有数据 可以整理数据
            return -1;
        }
        //已经审核 不能再整理
//        if(re.getValue("CHECK_FLG",0).equalsIgnoreCase("3")){
//            return 1;
//        }
        return re.getInt("CHECK_FLG",0);
    }
    /**
     * 修改审核状态
     * @param parm TParm
     * @return TParm
     */
    public TParm updateCheckFlg(TParm parm){
    	
    	
        String sql = "UPDATE MRO_MRV_TECH SET CHECK_FLG='"+parm.getValue("CHECK_FLG")+"',"+
                     " OPT_USER='"+parm.getValue("OPT_USER")+"',"+
                     " OPT_DATE =SYSDATE,"+
                     " MERGE_CODE='"+parm.getValue("MERGE_CODE")+"',"+
                     " MERGE_DATE =SYSDATE,"+
                     " SUBMIT_CODE='"+parm.getValue("SUBMIT_CODE")+"',"+
                     " SUBMIT_DATE =SYSDATE,"+
                     " OPT_TERM='"+parm.getValue("OPT_TERM")+"' "+
                     " WHERE MR_NO='"+parm.getValue("MR_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"'";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 删除合并合的文件
     * @param path
     * @param fileName
     */
    public void deleteFiles(String path,String caseNo){
            //jacobTool.Registry(path);
            File f = new File(path);
            String listFiles[] = getFileList(f.listFiles());
            //System.out.println("-----listFiles size------"+listFiles.length);
            
            for(int i = 0;i < listFiles.length;i++)
            {
                String s = listFiles[i];
                String hj = getHJ(s);
                if(!"pdf".equalsIgnoreCase(hj))
                    continue;
                String name = s.substring(0,s.length() - hj.length() - 1);
                //System.out.println("----name11111------"+name);
                //
                //System.out.println("==name.indexOf(caseNo)=="+name.indexOf(caseNo));
                //存在的文件，则删除
                if(name.indexOf(caseNo)!=-1){
	                File f1 = new File(path+name + ".pdf");
	                //System.out.println("=====exists====="+f1.exists());
	                if(f1.exists()){
	                	//System.out.println("--删除文件---"+f1.getName());
	                    f1.delete();	                    
	                }
                }
            }          	
    }
    /**
     * 
     * @param args
     */
    public static void main(String args[])
    {
        PdfTool.getInstance().deleteFiles("C:\\JavaHisFile\\temp\\pdf\\", "120521000210");
    }
}
