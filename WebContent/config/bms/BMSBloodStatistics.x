## TBuilder Config File ## Title:血液库存统计表## Company:JavaHis## Author:WangQing 2018.01.17## version 1.0#<Type=TFrame>UI.Title=血液库存统计表UI.MenuConfig=%ROOT%\config\bms\BMSBloodStatisticsMenu.x UI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.bms.BMSBloodStatisticsControlUI.item=TABLEUI.layout=nullUI.TopMenu=YUI.TopToolBar=YTABLE.Type=TTableTABLE.X=21TABLE.Y=14TABLE.Width=81TABLE.Height=729TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoY=YTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=血品名称,200;单位,100;A型血Rh阳性,100;A型血Rh阴性,100;B型血Rh阳性,100;B型血Rh阴性,100;O型血Rh阳性,100;O型血Rh阴性,100;AB型血Rh阳性,100;AB型血Rh阴性,100;总量,100;近效期量,150TABLE.ParmMap=BLDCODE_DESC;UNIT_CHN_DESC;A_POSITIVE;A_NEGATIVE;B_POSITIVE;B_NEGATIVE;O_POSITIVE;O_NEGATIVE;AB_POSITIVE;AB_NEGATIVE;AB_TOTAL;AB_NEAR_TERM_EFFECTTABLE.enHeader=TABLE.LockColumns=allTABLE.ColumnSelectionAllowed=YTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right