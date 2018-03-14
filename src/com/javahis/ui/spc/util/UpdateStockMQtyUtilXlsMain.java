package com.javahis.ui.spc.util;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.dongyang.data.TParm;

public class UpdateStockMQtyUtilXlsMain {

  public static void main( String[] args) throws IOException {
	  UpdateStockMQtyUtilXlsMain xlsMain = new UpdateStockMQtyUtilXlsMain();
    
    xlsMain.readXls();
  }

  public  TParm readXls() throws IOException{
    InputStream is = new FileInputStream( "c:\\auto.xls");
    HSSFWorkbook hssfWorkbook = new HSSFWorkbook( is); 
    TParm parm = new TParm();
    // 循环工作表Sheet
    //for(int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++){
    for(int numSheet = 0; numSheet < 1; numSheet++){    	
      HSSFSheet hssfSheet = hssfWorkbook.getSheetAt( numSheet);
      if(hssfSheet == null){
        continue;
      }
      
      String qty = "";
      // 循环行Row 
      for(int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++){
        HSSFRow hssfRow = hssfSheet.getRow( rowNum);
        if(hssfRow == null){
          continue;
        }
        parm.setData("ORG_CODE", rowNum, ""+getValue(hssfRow.getCell(0)));
        parm.setData("ORDER_CODE", rowNum, ""+getValue(hssfRow.getCell(1)));
        parm.setData("MAX_QTY", rowNum, ""+getValue(hssfRow.getCell(2)));
        parm.setData("SAFE_QTY", rowNum, ""+getValue(hssfRow.getCell(3)));
        parm.setData("MIN_QTY", rowNum, ""+getValue(hssfRow.getCell(4)));
        parm.setData("ECONOMICBUY_QTY", rowNum, ""+getValue(hssfRow.getCell(5)));
        // 循环列Cell  
        for(int cellNum = 0; cellNum <= hssfRow.getLastCellNum(); cellNum++){
          HSSFCell hssfCell = hssfRow.getCell( cellNum);
          if(hssfCell == null){
            continue;
          }
          
          System.out.print("    " + getValue( hssfCell));
        }
/*        if(rowNum == 3)
        	break;*/
        System.out.println();
      }
      System.out.println(parm);
      
    }
    return parm;
  }
  
  @SuppressWarnings("static-access")
  private String getValue(HSSFCell hssfCell){
    if(hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN){
    	System.out.println("1111111");
      return String.valueOf( hssfCell.getBooleanCellValue());
    }else if(hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC){
    	System.out.println("2222");
      return String.valueOf( hssfCell.getNumericCellValue());
    }else{
    	System.out.println("3333");
      return String.valueOf( hssfCell.getStringCellValue());
    }
  }
  
}