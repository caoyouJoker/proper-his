## TBuilder Config File ## Title:物资验收入库统计表## Company:JavaHis## Author:zhangh 2013.05.03## version 1.0#<Type=TFrame>UI.Title=物资出库统计查询报表UI.MenuConfig=%ROOT%\config\inv\INVOutStockReportMenu.xUI.Width=1366UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.inv.INVOutStockReportControlUI.item=tPanel_2;PAN1;PAN2;tMovePane_0UI.layout=nullUI.TopMenu=YUI.TopToolBar=YUI.X=5UI.AutoX=YUI.Y=9UI.AutoY=YUI.AutoWidth=YUI.AutoHeight=NUI.AutoW=NUI.AutoH=NtMovePane_0.Type=TMovePanetMovePane_0.X=5tMovePane_0.Y=363tMovePane_0.Width=1353tMovePane_0.Height=3tMovePane_0.Text=tMovePane_0.MoveType=2tMovePane_0.Style=3tMovePane_0.DoubleClickType=1tMovePane_0.Border=凸tMovePane_0.AutoWidth=YtMovePane_0.EntityData=PAN1,2;PAN2,1PAN2.Type=TPanelPAN2.X=4PAN2.Y=365PAN2.Width=1357PAN2.Height=375PAN2.Border=组|物资出库明细PAN2.AutoWidth=YPAN2.AutoHeight=YPAN2.Item=TABLE1TABLE1.Type=TTableTABLE1.X=8TABLE1.Y=20TABLE1.Width=1339TABLE1.Height=348TABLE1.SpacingRow=1TABLE1.RowHeight=20TABLE1.AutoWidth=YTABLE1.AutoHeight=YTABLE1.Header=入库部门,120,TO_ORG_CODE;RFID,100;物资编码,140;物资名称,310;规格,140;数量,70;单位,70,STOCK_UNIT;采购单价,80,double,########0.0000;采购金额,80,double;供应商,300;上级供应商,300;生产商,260,MAN_CODE;零售单价,80,double,########0.0000;零售金额,80,double;进销差价,80,double;出库单号,100;入库日期,100;效期,80;入库单号,100TABLE1.LockColumns=ALLTABLE1.ParmMap=TO_ORG_CODE;RFID;INV_CODE;INV_CHN_DESC;DESCRIPTION;QTY;STOCK_UNIT;CONTRACT_PRICE;CONTRACT_AMT;SUP_ABS_DESC;UPSUP_ABS_DESC;MAN_CODE;COST_PRICE;COST_AMT;DIFFERENCE_AMT;DISPENSE_NO;DISPENSE_DATE;VALID_DATE;OUTDISPENSE_NOTABLE1.Item=STOCK_UNIT;MAN_CODE;TO_ORG_CODETABLE1.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,right;6,left;7,right;8,right;9,left;10,left;11,left;12,right;13,right;14,right;15,left;16,right;17,right;18,leftPAN1.Type=TPanelPAN1.X=4PAN1.Y=133PAN1.Width=1357PAN1.Height=230PAN1.Border=组|物资出库汇总PAN1.AutoWidth=YPAN1.Item=TABLETABLE.Type=TTableTABLE.X=10TABLE.Y=20TABLE.Width=1341TABLE.Height=204TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.Header=入库部门,120;出库单号,120;物资编码,140;物资名称,310;规格,140;数量,70;单位,70,STOCK_UNIT;采购单价,80,double,########0.0000;采购金额,80,double;供应商,300,INV_FACTOR;上级供应商,300,INV_FACTOR;生产商,260,MAN_CODE;零售单价,80,double,########0.0000;零售金额,80,double;进销差价,80,doubleTABLE.LockColumns=ALLTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,right;4,left;5,right;6,right;7,left;8,left;9,left;10,right;11,right;12,rightTABLE.ParmMap=TO_ORG_CODE;OUTDISPENSE_NO;INV_CODE;INV_CHN_DESC;DESCRIPTION;QTY;STOCK_UNIT;CONTRACT_PRICE;CONTRACT_AMT;SUP_CODE;UP_SUP_CODE;MAN_CODE;COST_PRICE;COST_AMT;DIFFERENCE_AMTTABLE.Item=STOCK_UNIT;MAN_CODE;INV_FACTORTABLE.AutoWidth=YTABLE.AutoHeight=YtPanel_2.Type=TPaneltPanel_2.X=6tPanel_2.Y=5tPanel_2.Width=1355tPanel_2.Height=130tPanel_2.Border=组tPanel_2.AutoY=YtPanel_2.AutoWidth=YtPanel_2.Item=tLabel_2;INV_CODE;tLabel_4;tLabel_1;tLabel_9;tLabel_10;START_TIME;END_TIME;FROM_ORG_CODE;SUP_CODE;INV_DESC;UNIT;tLabel_0;UP_SUP_CODE;物资分类下拉列表_0;物资分类下拉列表_0;INV_KIND;tLabel_3;H_FLG;L_FLG;CONSIGN_FLG;tLabel_5;TO_ORG_CODE;tLabel_6;TOT_AMT;tLabel_7;TOT_NUM;STOCK_UNIT;MAN_CODE;INV_FACTOR;tLabel_8;tLabel_11;tTextField_0tTextField_0.Type=TTextFieldtTextField_0.X=86tTextField_0.Y=98tTextField_0.Width=123tTextField_0.Height=20tTextField_0.Text=tLabel_11.Type=TLabeltLabel_11.X=13tLabel_11.Y=101tLabel_11.Width=72tLabel_11.Height=15tLabel_11.Text=出库单号:tLabel_11.Color=bluetLabel_8.Type=TLabeltLabel_8.X=430tLabel_8.Y=100tLabel_8.Width=72tLabel_8.Height=15tLabel_8.Text=元INV_FACTOR.Type=供应厂商INV_FACTOR.X=711INV_FACTOR.Y=134INV_FACTOR.Width=81INV_FACTOR.Height=23INV_FACTOR.Text=INV_FACTOR.HorizontalAlignment=2INV_FACTOR.PopupMenuHeader=代码,100;名称,100INV_FACTOR.PopupMenuWidth=300INV_FACTOR.PopupMenuHeight=300INV_FACTOR.PopupMenuFilter=ID,1;NAME,1;PY1,1INV_FACTOR.FormatType=comboINV_FACTOR.ShowDownButton=YINV_FACTOR.Tip=供应厂商INV_FACTOR.ShowColumnList=NAMEMAN_CODE.Type=生产厂商下拉区域MAN_CODE.X=623MAN_CODE.Y=134MAN_CODE.Width=81MAN_CODE.Height=23MAN_CODE.Text=MAN_CODE.HorizontalAlignment=2MAN_CODE.PopupMenuHeader=代码,100;名称,100MAN_CODE.PopupMenuWidth=300MAN_CODE.PopupMenuHeight=300MAN_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1MAN_CODE.FormatType=comboMAN_CODE.ShowDownButton=YMAN_CODE.Tip=生产厂商MAN_CODE.ShowColumnList=NAMESTOCK_UNIT.Type=计量单位STOCK_UNIT.X=503STOCK_UNIT.Y=130STOCK_UNIT.Width=81STOCK_UNIT.Height=23STOCK_UNIT.Text=STOCK_UNIT.HorizontalAlignment=2STOCK_UNIT.PopupMenuHeader=代码,100;名称,100STOCK_UNIT.PopupMenuWidth=300STOCK_UNIT.PopupMenuHeight=300STOCK_UNIT.PopupMenuFilter=ID,1;NAME,1;PY1,1STOCK_UNIT.FormatType=comboSTOCK_UNIT.ShowDownButton=YSTOCK_UNIT.Tip=计量单位STOCK_UNIT.ShowColumnList=NAMETOT_NUM.Type=TTextFieldTOT_NUM.X=523TOT_NUM.Y=97TOT_NUM.Width=57TOT_NUM.Height=20TOT_NUM.Text=TOT_NUM.Enabled=NTOT_NUM.HorizontalAlignment=4tLabel_7.Type=TLabeltLabel_7.X=457tLabel_7.Y=100tLabel_7.Width=72tLabel_7.Height=15tLabel_7.Text=合计笔数：TOT_AMT.Type=TTextFieldTOT_AMT.X=303TOT_AMT.Y=98TOT_AMT.Width=120TOT_AMT.Height=20TOT_AMT.Text=TOT_AMT.Enabled=NTOT_AMT.HorizontalAlignment=4tLabel_6.Type=TLabeltLabel_6.X=234tLabel_6.Y=101tLabel_6.Width=72tLabel_6.Height=15tLabel_6.Text=金额汇总：TO_ORG_CODE.Type=物资部门下拉区域TO_ORG_CODE.X=731TO_ORG_CODE.Y=10TO_ORG_CODE.Width=130TO_ORG_CODE.Height=23TO_ORG_CODE.Text=TO_ORG_CODE.HorizontalAlignment=2TO_ORG_CODE.PopupMenuHeader=代码,100;名称,100TO_ORG_CODE.PopupMenuWidth=300TO_ORG_CODE.PopupMenuHeight=300TO_ORG_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1TO_ORG_CODE.FormatType=comboTO_ORG_CODE.ShowDownButton=YTO_ORG_CODE.Tip=物资部门TO_ORG_CODE.ShowColumnList=NAMETO_ORG_CODE.HisOneNullRow=YtLabel_5.Type=TLabeltLabel_5.X=659tLabel_5.Y=15tLabel_5.Width=72tLabel_5.Height=15tLabel_5.Text=入库部门：tLabel_5.Color=蓝CONSIGN_FLG.Type=TCheckBoxCONSIGN_FLG.X=816CONSIGN_FLG.Y=72CONSIGN_FLG.Width=57CONSIGN_FLG.Height=23CONSIGN_FLG.Text=寄售CONSIGN_FLG.Color=BLUEL_FLG.Type=TRadioButtonL_FLG.X=753L_FLG.Y=71L_FLG.Width=63L_FLG.Height=23L_FLG.Text=低值L_FLG.Group=L_FLG.Color=BLUEH_FLG.Type=TRadioButtonH_FLG.X=693H_FLG.Y=71H_FLG.Width=59H_FLG.Height=23H_FLG.Text=高值H_FLG.Group=H_FLG.Color=BLUEH_FLG.Selected=YtLabel_3.Type=TLabeltLabel_3.X=563tLabel_3.Y=46tLabel_3.Width=72tLabel_3.Height=15tLabel_3.Text=物资分类：tLabel_3.Color=蓝INV_KIND.Type=物资分类下拉列表INV_KIND.X=630INV_KIND.Y=41INV_KIND.Width=232INV_KIND.Height=23INV_KIND.Text=INV_KIND.HorizontalAlignment=2INV_KIND.PopupMenuHeader=代码,100;名称,100INV_KIND.PopupMenuWidth=300INV_KIND.PopupMenuHeight=300INV_KIND.PopupMenuFilter=ID,1;NAME,1;PY1,1INV_KIND.FormatType=comboINV_KIND.ShowDownButton=YINV_KIND.Tip=物资种类INV_KIND.ShowColumnList=NAMEINV_KIND.HisOneNullRow=Y物资分类下拉列表_0.Type=物资分类下拉列表物资分类下拉列表_0.X=97物资分类下拉列表_0.Y=114物资分类下拉列表_0.Type=物资分类下拉列表物资分类下拉列表_0.X=499物资分类下拉列表_0.Y=108UP_SUP_CODE.Type=供应厂商UP_SUP_CODE.X=417UP_SUP_CODE.Y=71UP_SUP_CODE.Width=254UP_SUP_CODE.Height=21UP_SUP_CODE.Text=UP_SUP_CODE.HorizontalAlignment=2UP_SUP_CODE.PopupMenuHeader=代码,100;名称,100UP_SUP_CODE.PopupMenuWidth=300UP_SUP_CODE.PopupMenuHeight=300UP_SUP_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1UP_SUP_CODE.FormatType=comboUP_SUP_CODE.ShowDownButton=YUP_SUP_CODE.Tip=供应厂商UP_SUP_CODE.ShowColumnList=NAMEUP_SUP_CODE.HisOneNullRow=YtLabel_0.Type=TLabeltLabel_0.X=331tLabel_0.Y=74tLabel_0.Width=91tLabel_0.Height=15tLabel_0.Text=上级供应商：tLabel_0.Color=蓝UNIT.Type=计量单位下拉列表UNIT.X=909UNIT.Y=10UNIT.Width=81UNIT.Height=23UNIT.Text=TButtonUNIT.showID=YUNIT.showName=YUNIT.showText=NUNIT.showValue=NUNIT.showPy1=YUNIT.showPy2=YUNIT.Editable=YUNIT.Tip=计量单位UNIT.TableShowList=nameUNIT.Enabled=NUNIT.Visible=NINV_DESC.Type=TTextFieldINV_DESC.X=226INV_DESC.Y=43INV_DESC.Width=324INV_DESC.Height=20INV_DESC.Text=INV_DESC.Enabled=NSUP_CODE.Type=供应厂商SUP_CODE.X=86SUP_CODE.Y=71SUP_CODE.Width=239SUP_CODE.Height=21SUP_CODE.Text=SUP_CODE.HorizontalAlignment=2SUP_CODE.PopupMenuHeader=代码,100;名称,100SUP_CODE.PopupMenuWidth=300SUP_CODE.PopupMenuHeight=300SUP_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1SUP_CODE.FormatType=comboSUP_CODE.ShowDownButton=YSUP_CODE.Tip=供应商SUP_CODE.ShowColumnList=NAMESUP_CODE.HisOneNullRow=YFROM_ORG_CODE.Type=物资部门下拉区域FROM_ORG_CODE.X=517FROM_ORG_CODE.Y=12FROM_ORG_CODE.Width=130FROM_ORG_CODE.Height=21FROM_ORG_CODE.Text=FROM_ORG_CODE.HorizontalAlignment=2FROM_ORG_CODE.PopupMenuHeader=代码,100;名称,100FROM_ORG_CODE.PopupMenuWidth=300FROM_ORG_CODE.PopupMenuHeight=300FROM_ORG_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1FROM_ORG_CODE.FormatType=comboFROM_ORG_CODE.ShowDownButton=YFROM_ORG_CODE.Tip=物资部门FROM_ORG_CODE.ShowColumnList=NAMEFROM_ORG_CODE.Enabled=YFROM_ORG_CODE.HisOneNullRow=YEND_TIME.Type=TTextFormatEND_TIME.X=275END_TIME.Y=12END_TIME.Width=155END_TIME.Height=21END_TIME.Text=END_TIME.FormatType=dateEND_TIME.Format=yyyy/MM/dd HH:mm:ssEND_TIME.showDownButton=YEND_TIME.HorizontalAlignment=2START_TIME.Type=TTextFormatSTART_TIME.X=85START_TIME.Y=12START_TIME.Width=160START_TIME.Height=21START_TIME.Text=START_TIME.Format=yyyy/MM/dd HH:mm:ssSTART_TIME.FormatType=dateSTART_TIME.showDownButton=YSTART_TIME.HorizontalAlignment=2tLabel_10.Type=TLabeltLabel_10.X=448tLabel_10.Y=12tLabel_10.Width=71tLabel_10.Height=21tLabel_10.Text=出库部门：tLabel_10.Color=蓝tLabel_9.Type=TLabeltLabel_9.X=253tLabel_9.Y=11tLabel_9.Width=17tLabel_9.Height=21tLabel_9.Text=至tLabel_9.Color=蓝tLabel_1.Type=TLabeltLabel_1.X=14tLabel_1.Y=10tLabel_1.Width=70tLabel_1.Height=21tLabel_1.Text=出库日期：tLabel_1.Color=蓝tLabel_4.Type=TLabeltLabel_4.X=25tLabel_4.Y=72tLabel_4.Width=71tLabel_4.Height=21tLabel_4.Text=供应商：tLabel_4.Color=蓝INV_CODE.Type=TTextFieldINV_CODE.X=85INV_CODE.Y=43INV_CODE.Width=130INV_CODE.Height=20INV_CODE.Text=INV_CODE.Action=onFiltertLabel_2.Type=TLabeltLabel_2.X=14tLabel_2.Y=44tLabel_2.Width=66tLabel_2.Height=15tLabel_2.Text=物资名称:tLabel_2.Color=蓝