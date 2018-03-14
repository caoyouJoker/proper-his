package com.javahis.ui.spc.util;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.dongyang.data.TParm;

public class UpdateLocCodeXlsMain {

  public static void main( String[] args) throws IOException {
    UpdateLocCodeXlsMain xlsMain = new UpdateLocCodeXlsMain();
    
    xlsMain.readXls();
  }

  public  TParm readXls() throws IOException{
    InputStream is = new FileInputStream( "c:\\test2.xls");
    HSSFWorkbook hssfWorkbook = new HSSFWorkbook( is); 
    TParm parm = new TParm();
    // 循环工作表Sheet
    //for(int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++){
    for(int numSheet = 0; numSheet < 1; numSheet++){    	
      HSSFSheet hssfSheet = hssfWorkbook.getSheetAt( numSheet);
      if(hssfSheet == null){
        continue;
      }
      
      // 循环行Row 
      for(int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++){
        HSSFRow hssfRow = hssfSheet.getRow( rowNum);
        if(hssfRow == null){
          continue;
        }
        parm.setData("ORG_CODE", rowNum, getValue(hssfRow.getCell(0)));
        parm.setData("ORDER_CODE", rowNum, getValue(hssfRow.getCell(1)));
        parm.setData("LOC_CODE", rowNum, getValue(hssfRow.getCell(2)));
        parm.setData("LOC_DESC", rowNum, getValue(hssfRow.getCell(3)));
        parm.setData("ELETAG_CODE", rowNum, getValue(hssfRow.getCell(4)));
/*        // 循环列Cell  
        for(int cellNum = 0; cellNum <= hssfRow.getLastCellNum(); cellNum++){
          HSSFCell hssfCell = hssfRow.getCell( cellNum);
          if(hssfCell == null){
            continue;
          }
          
          System.out.print("    " + getValue( hssfCell));
        }*/
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
      return String.valueOf( hssfCell.getBooleanCellValue());
    }else if(hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC){
      return String.valueOf( hssfCell.getNumericCellValue());
    }else{
      return String.valueOf( hssfCell.getStringCellValue());
    }
  }
  
}