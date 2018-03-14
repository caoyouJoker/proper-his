package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.tui.text.div.DIV;
import com.dongyang.tui.text.div.MV;
import com.dongyang.tui.text.div.VTable;
import com.dongyang.tui.text.div.VText;
import com.dongyang.ui.TWord;

/**
 * 
 * @author Administrator
 *
 */
public class EMRTitleUpdateControl extends TControl{
	/**
	 * WORD对象
	 */
	private static final String TWORD = "WORD";
	
	/**
	 * 
	 */
	 private TWord word;
	
	
	/**
	 * 
	 */
	public void  onUpdate(){
		//this.messageBox("--come in---");
		word = this.getTWord(TWORD);
		//关闭
		word.setMessageBoxSwitch(false);
		String sql="select EMT_FILENAME,TEMPLET_PATH from emr_templet where subclass_code like 'EMR%'";
		//1.取得 emr_templet
		TParm parm = new TParm(getDBTool().select(sql));
		
		/**
		 * 
		 */
		for (int i = 0; i < parm.getCount(); i++) {
			//1
			//System.out.println("---EMT_FILENAME["+i+"]---"+parm.getValue("EMT_FILENAME", i));
			//2
			//System.out.println("---TEMPLET_PATH["+i+"]---"+parm.getValue("TEMPLET_PATH", i));
			
			/**
			 * 
			 */
			batchUpdate(parm.getValue("TEMPLET_PATH", i),parm.getValue("EMT_FILENAME", i));
			
		}
		
		
		
		
		
	}
	
	/**
	 * 
	 * @param templatePath
	 * @param emtFileName
	 */
	private void batchUpdate(String templatePath,String emtFileName){
		//
		//1.打开指定的 word模版 
		word.onOpen(templatePath, emtFileName, 2, false);
		//
		//this.messageBox("mv size"+word.getPageManager().getMVList().size());
		//
		//2.查找住院号_及_住院号宏值
		for (int i = 0; i < word.getPageManager().getMVList().size(); i++) {
			MV mv = word.getPageManager().getMVList().get(i);
			//
			//System.out.println("mv name"+mv.getName());
			//System.out.println("mv name"+mv.get(0).getName());
			//FILE_TITLE
			if(mv.get(0)!=null){
				if(mv.get(0).getName().equals("FILE_TITLE")){
					if (mv.get(0) instanceof VTable) {
						VTable vtale=(VTable)mv.get(0);
						MV tableMV = vtale.getMV();
						for(int j = 0; j < tableMV.size(); j++){
							DIV div1 = tableMV.get(j);
							System.out.println("---table div---"+div1.getName());
							if(div1.getName().equals("住院号:")|| div1.getName().equals("住院号：")|| div1.getName().equals("住院号")){
								if (div1 instanceof VText) {
									VText  vtext=(VText)div1;
									vtext.setText("姓  名:");
									//
									word.update();
								}
							}
							
						}
						
					}
					
				}
				
			}
			
			//
			/*if (mv.getName().equals("UNVISITABLE_ATTR")) {
				for (int j = 0; j < mv.size(); j++) {
					DIV div = mv.get(j);
						if (div instanceof VText) {
					
						}
					}
			}*/
		}		
		
		//3.保存模版文件
		if (word.onSaveAs(templatePath,
				emtFileName, 2)) {			
		}else{
			System.out.println(emtFileName+"JHW文件保存失败\r\n");
		}
	}
	
	
    /**
     * 得到WORD对象
     * @param tag String
     * @return TWord
     */
    public TWord getTWord(String tag) {
        return (TWord)this.getComponent(tag);
    }
    
	/**
	 * 返回数据库操作工具
	 *
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

}
