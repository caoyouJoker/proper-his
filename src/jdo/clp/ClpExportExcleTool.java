package jdo.clp;


import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import javax.swing.JOptionPane;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.*;
import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WritableSheet;
import jxl.write.Label;
import jxl.write.WritableFont;
import jxl.write.WritableCellFormat;
import jxl.write.*;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.control.TControl;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class ClpExportExcleTool extends TControl{

    private static final ClpExportExcleTool INSTANCE =new ClpExportExcleTool();

    private ClpExportExcleTool() {
    }

    public static ClpExportExcleTool getInstance(){

        return INSTANCE;
    }

//    private TTable table;
    /**
     * 导出EXCEL的方法的接口
     * @param table TTable table对象
     * @param defName String 默认文件的名字
     */
//    public void exportExcel(TTable mainTable, String defName) {
//        table=mainTable;
//        //得到talble的头
//        String header = mainTable.getHeader();
//        //只得到table上显示的数据（PS：隐藏数据得不到）
//        TParm mainDate = mainTable.getShowParmValue();
//        if (mainDate.getCount() <= 0) {
//            this.messageBox("无导出EXCEL数据！");
//            return;
//        }
//        //调用导出excel方法（标题头，主数据，文件名字）
//        exeSaveExcel(header, mainDate, defName);
//
//    }

    /**
     * 导出excel文件
     * @param headerDate String
     * @param mainDate TParm
     * @param fileDefName 默认文件名字
     */

    public void exeSaveExcel(String headerDate, TParm mainDate,String fileDefName) {

        //重新整理标题头数据（只留下标题）
        Vector arrHeader = arrHeader(headerDate);
        //标准SWING
        JFileChooser chooser = new JFileChooser();
        //设置当前的目录（目前写死）
        File dir= new File("C:\\JavaHis\\Excel");
        if(!dir.exists())
            dir.mkdirs();
        chooser.setCurrentDirectory(dir);
        fileDefName=fileDefName;
        //默认的文件保存名字
        chooser.setSelectedFile(new File(fileDefName));//提供默认名
        //设置对话框的标题
        chooser.setDialogTitle("导出EXCEL界面");
        //设置过滤（扩展名）
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.getName().toUpperCase().endsWith(".XLS");
            }

            public String getDescription() {
                return "Excel (*.xls)";
            }
        });

        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            this.writeFile(arrHeader, mainDate, path);
        }
    }

    /**
     * 打开保存对话框选择路径
     * @param header Vector
     * @param date TParm
     * @param FileName String
     */
    public void writeFile(Vector header, TParm date, String FileName) {
        //创建一个文件对象
        File file;
        try {
            //过路径中包含.xls就不加后缀
            if (!FileName.contains(".xls")) {
                file = new File(FileName + ".xls");
            }
            else
                file = new File(FileName);
            //判断该文件是否存在，如果已经存在提示是否覆盖（不覆盖就退出操作）
            if (file.exists() &&
                JOptionPane.showConfirmDialog(null, "save", "该文件已经存在,是否覆盖？",
                                              JOptionPane.YES_NO_OPTION) == 1)
                return;
            //把文件变成输出流
            FileOutputStream fileOutStream = null;
            fileOutStream = new FileOutputStream(file);
            //执行数据整理并导出
            exportToExcel(header, date, fileOutStream);

        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "转档失败");
        }
    }

    /**
     * 导出excel整理数据和设置格式（主方法）
     * @param headerDate Vector
     * @param date TParm
     * @param os OutputStream
     */

    public void exportToExcel(Vector headerDate, TParm date, OutputStream os) {

        //取得需要的数据
        Vector header = (Vector) headerDate.get(0);
        Vector colView = (Vector) headerDate.get(1);
        Vector dateType = (Vector) headerDate.get(2);
        //取得列数
        int colNum = header.size();

        //设置格式化列标题
        WritableFont fontHeader = new WritableFont(WritableFont.createFont(
            "楷体 _GB2312"), 12, WritableFont.BOLD);
        WritableCellFormat formatHeader = new WritableCellFormat(fontHeader);
        //设置格式化主数据
        WritableFont fontDate = new WritableFont(WritableFont.createFont(
            "楷体 _GB2312"), 10, WritableFont.NO_BOLD);
        //格式化一般的数据（靠左）
        WritableCellFormat formatDateLeft = new WritableCellFormat(fontDate);
        //格式化数字（靠右）
        WritableCellFormat formatDateRight = new WritableCellFormat(fontDate);

        try {
            //设定列标题居中
            formatHeader.setAlignment(jxl.format.Alignment.CENTRE);
            //设置自动换行
            formatHeader.setWrap(true);

            formatDateRight.setAlignment(jxl.format.Alignment.RIGHT);
            formatDateRight.setWrap(true);

            formatDateLeft.setAlignment(jxl.format.Alignment.LEFT);
            formatDateLeft.setWrap(true);
        }
        catch (WriteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "转档失败");
        }
        try {
            //打开文件
            WritableWorkbook workbook = Workbook.createWorkbook(os);
            //生成名为“报 表”的工作表，参数0表示这是第一页（sheet的名字）
            WritableSheet sheet1 = workbook.createSheet("报  表", 0);
            //设置列宽
            for (int n = 0; n < colNum; n++) {
                int colViwe = Integer.parseInt( (String) colView.get(n));
                //循环设置列宽（是table上面长度的10分之一）
                sheet1.setColumnView(n, colViwe / 10);
            }

            //添加表头
            for (int i = 0; i < colNum; i++) {
                //添加表头的一行（标题）
                Label row1 = new Label(i, 0, (String) header.get(i),
                                       formatHeader);
                sheet1.addCell(row1);
            }

            //添加主数据
            int dateRow = date.getCount();
            //得到默认组的map对照表，通过它来依次拿数据（因为map的排序是不准的）
//            String map = (String)table.getParmMap();
            String map = "A;B";
            String[] title = map.split(";");
            //循环添加主数据
            for (int j = 0; j < dateRow; j++) { //行
                for (int n = 0; n < colNum; n++) { //列
                    //每一个格的数据
                    Label dateCell;
                    //得到该列的类型
                    String type = (String) dateType.get(n);
                    //如果是数字类型就靠右
                    if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("float") ||
                        type.equalsIgnoreCase("double") || type.equalsIgnoreCase("long")) {
                        //设置该数据靠右
                        dateCell = new Label(n, j + 1,
                                             date.getValue(title[n], j),
                                             formatDateRight);
                    }
                    else{
                        //设置该数据靠左
                        dateCell = new Label(n, j + 1,
                                             date.getValue(title[n], j),
                                             formatDateLeft);
                    }
                    sheet1.addCell(dateCell);

//                    this.messageBox_(dateCell);
                }
            }

            workbook.write();
            workbook.close();
            JOptionPane.showMessageDialog(null, "转档成功");
        }
        catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "转档失败");
        }
    }

    /**
     * 整理table的标题头，使之只省下标题
     * @param date String 缺身省状态下的头
     * @return Vector
     */
    public Vector arrHeader(String date) {

        //把标题数据按；区间为字符数组
        String[] indate = date.split(";");
        Vector colAndType = getColumnView(indate);

        Vector result = new Vector();
        Vector header = new Vector();
        //循环去掉--> ，长度，类型等
        for (int j = 0; j < indate.length; j++) {
            //截取‘第一个’逗号前的文字--标题
            String a = indate[j].substring(0, (int) (indate[j].indexOf(",")));
            header.add(a);
        }
        //第一组是第一行的题目
        result.add(header);
        //第二组是列宽
        result.add(colAndType.get(0));
        //第三行是数据类型
        result.add(colAndType.get(1));

        return result;
    }

    public Vector getColumnView(String[] date) {

        Vector result = new Vector();
        //存储列宽和数据类型
        Vector colView = new Vector();
        Vector dateType = new Vector();
        //行数
        int col = date.length;
        for (int i = 0; i < col; i++) {
            //从第一个逗号后面一位开始
            int start = date[i].indexOf(",") + 1;
            int end;
            String type;
            //没有第二个逗号的时候说明没有数据类型
            if (date[i].indexOf(",", start) == -1) {
                //没有第二个逗号取到最后--长度
                end = date[i].length();
                //没有类型就默认为字符串
                type = "String";
            }
            else {
                //有就取到第二个逗号以前--长度
                end = date[i].indexOf(",", start);
                //取得省下的字符--类型
                type = date[i].substring(end + 1);
            }
            //存储该列的长度
            String view = date[i].substring(start, end);
            colView.add(view);
            dateType.add(type);

        }
        result.add(colView);
        result.add(dateType);
        return result;
    }


//-----------------------------导出EXCEL的方法---end-----------------------------

}
