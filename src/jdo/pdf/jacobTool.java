package jdo.pdf;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.dongyang.config.TRegistry;
import java.io.PrintStream;
import java.util.List;
import org.pdfbox.pdmodel.*;
import org.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import java.util.ArrayList;
import java.io.FileInputStream;
import org.pdfbox.pdfparser.PDFParser;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import org.pdfbox.util.PDFTextStripper;
import com.dongyang.data.TParm;

public class jacobTool {
    private static final Variant False = new Variant(false);
    private static final Variant True = new Variant(true);
    /**
     * 注册输出目录
     * @param path String
     */
    public static void Registry(String path)
    {
        TRegistry.set("HKEY_CURRENT_USER\\Software\\FinePrint Software\\pdfFactory3\\AutoSaveDir",path);
    }
    /**
     * 打印成PDF文件
     * @param filename String
     * @return boolean
     */
    public static boolean print(String filename)
    {
        try{
            ActiveXComponent wordCom = new ActiveXComponent("Word.Application");
            Dispatch wrdDocs = wordCom.getProperty("Documents").toDispatch();
            Object wordDoc = Dispatch.invoke(wrdDocs, "Open", Dispatch.Method,
                    new Object[]{ filename }, new int[1]).toDispatch();

            //是否在后台运行
            Variant Background = False;
            //是否追加打印
            Variant Append = False;
            //打印所有文档
            int wdPrintAllDocument = 0;
            Variant Range = new Variant(wdPrintAllDocument);

            wordCom.invoke("PrintOut", new Variant[]{Background, Append, Range});
            wordCom.invoke("Quit", new Variant[]{});
        }catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static String getPath(String fileName)
    {
        int index = fileName.lastIndexOf("\\");
        return fileName.substring(0,index);
    }
    public static int getCount(String fileName)
    {
        int count = 0;
        PDDocument document;
        document = null;
        try {
            document = PDDocument.load(fileName);
            List pages = document.getDocumentCatalog().getAllPages();
            count = pages.size();
            document.close();
        }
        catch (Exception e) {
        }
        return count;
    }

    public static String setBookmarks(String fileName,List marks)
    {
        String path = getPath(fileName);
        int rows[] = new int[marks.size()];
        int row = 0;
        for(int i = 0;i < marks.size();i++)
        {
            rows[i] = row;
            row += getCount(path + "\\" + i + ".pdf");
        }
        PDDocument document;
        document = null;
        try {
            document = PDDocument.load(fileName);
            if (document.isEncrypted())
                return "Error: Cannot add bookmarks to encrypted document.";
            PDDocumentOutline outline = new PDDocumentOutline();
            document.getDocumentCatalog().setDocumentOutline(outline);
            PDOutlineItem pagesOutline = new PDOutlineItem();
            pagesOutline.setTitle("All Pages");
            outline.appendChild(pagesOutline);
            List pages = document.getDocumentCatalog().getAllPages();
            for (int i = 0; i < rows.length; i++) {
                PDPage page = (PDPage) pages.get(rows[i]);
                PDPageFitWidthDestination dest = new PDPageFitWidthDestination();
                dest.setPage(page);
                PDOutlineItem bookmark = new PDOutlineItem();
                bookmark.setDestination(dest);
                bookmark.setTitle((String)marks.get(i));
                pagesOutline.appendChild(bookmark);
            }

            pagesOutline.openNode();
            outline.openNode();
            document.save(fileName);
            document.close();
        }
        catch (Exception e) {
            return e.getMessage();
        }
        return "";
    }
    /**
     * 解析PDF文件
     * @param filename String
     * @return String
     */
    public static String getTextFromPdf(String filename)
    {
        try{
            FileInputStream is = new FileInputStream(filename);
            String s = getTextFromPdf(is);
            is.close();
            return s;
        }catch(Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }
    /**
     * 解析PDF文件
     * @param stream InputStream
     * @return String
     */
    public static String getTextFromPdf(InputStream stream)
    {
        try{
            PDFParser parser = new PDFParser(stream);
            parser.parse();
            PDDocument nbsp = parser.getPDDocument();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(out);
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.writeText(nbsp, writer);
            byte[] contents = out.toByteArray();
            //nbsp.clearWillEncryptWhenSaving();
            writer.close();
            return new String(contents);
        }catch(Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }
    public static void main1(String args[])
    {
        //String s = getTextFromPdf("C:\\Documents and Settings\\li\\桌面\\pdf\\新建文件夹\\1\\血常规.pdf");//体温单.pdf");//
        //TParm p = PDFT.check(s);
        //System.out.println(p);
        //TParm p = PDFT.look("D:\\PDF\\新建文件夹","D:\\PDF\\新建文件夹");
        //System.out.println(p);
        System.out.println(jacobTool.getTextFromPdf("D:\\PDF\\新建文件夹\\000000273030_检验报告单_2010-11-23-10-50.pdf"));

    }
    public static void main(String[] argv){
        List list = new ArrayList();
        list.add("首次病程记录");
        list.add("住院医保病人自费项目确认同意书");
        list.add("入院72小时内病情告知书");
        list.add("入院记录");
        list.add("LIS检查报告2");
        setBookmarks("D:\\temp\\data\\100113000016.pdf",list);
        //print("D:\\lzk\\Project\\word\\11.doc");
    }
}
