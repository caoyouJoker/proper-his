## TBuilder Config File ## Title:采购计划## Company:JavaHis## Author:zhangy 2009.04.28## version 1.0#<Type=TFrame>UI.Title=采购计划UI.MenuConfig=%ROOT%\config\spc\INDPurplanMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.spc.INDPurPlanControlUI.item=tPanel_0;tPanel_1;tPanel_4;tMovePane_2UI.layout=nullUI.Text=采购计划UI.Tip=采购计划UI.Name=UI.TopMenu=YUI.TopToolBar=YUI.ShowTitle=NUI.ShowMenu=NUI.FocusList=ORG_CODE;PLAN_MONTH;PLAN_NO;PLAN_DESC;PLAN_FLG;PUR_FLG;CHECK_FLG;PLANEND_FLG;DESCRIPTIONtMovePane_2.Type=TMovePanetMovePane_2.X=5tMovePane_2.Y=282tMovePane_2.Width=1014tMovePane_2.Height=5tMovePane_2.Text=tMovePane_2.MoveType=2tMovePane_2.AutoX=YtMovePane_2.AutoWidth=YtMovePane_2.Border=凸tMovePane_2.Style=3tMovePane_2.EntityData=tPanel_1,2;tPanel_4,1tPanel_4.Type=TPaneltPanel_4.X=5tPanel_4.Y=286tPanel_4.Width=1014tPanel_4.Height=457tPanel_4.Border=组|计划单明细tPanel_4.AutoX=YtPanel_4.AutoWidth=YtPanel_4.AutoSize=5tPanel_4.Item=tPanel_5tPanel_4.AutoHeight=YtPanel_5.Type=TPaneltPanel_5.X=6tPanel_5.Y=19tPanel_5.Width=1002tPanel_5.Height=432tPanel_5.Border=凹tPanel_5.AutoX=YtPanel_5.AutoY=YtPanel_5.AutoHeight=YtPanel_5.AutoWidth=YtPanel_5.AutoSize=0tPanel_5.Item=TABLE_D;tPanel_6tPanel_6.Type=TPaneltPanel_6.X=5tPanel_6.Y=5tPanel_6.Width=992tPanel_6.Height=29tPanel_6.Border=组tPanel_6.AutoX=YtPanel_6.AutoY=YtPanel_6.AutoWidth=YtPanel_6.AutoSize=3tPanel_6.AutoHeight=NtPanel_6.Item=tLabel_4;tLabel_6;tLabel_7;DIFF_FLG;ORDER_CODE;ORDER_DESC;SUM_TOTIL;tLabel_5;PURCH_UNIT;SUP_CODESUP_CODE.Type=供应厂商SUP_CODE.X=88SUP_CODE.Y=3SUP_CODE.Width=181SUP_CODE.Height=23SUP_CODE.Text=SUP_CODE.HorizontalAlignment=2SUP_CODE.PopupMenuHeader=ID,100;NAME,100SUP_CODE.PopupMenuWidth=300SUP_CODE.PopupMenuHeight=300SUP_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1SUP_CODE.FormatType=comboSUP_CODE.ShowDownButton=YSUP_CODE.Tip=供应厂商SUP_CODE.ShowColumnList=NAMESUP_CODE.PhaFlg=YSUP_CODE.ValueColumn=IDPURCH_UNIT.Type=计量单位下拉列表PURCH_UNIT.X=22PURCH_UNIT.Y=61PURCH_UNIT.Width=81PURCH_UNIT.Height=23PURCH_UNIT.Text=TButtonPURCH_UNIT.showID=YPURCH_UNIT.showName=YPURCH_UNIT.showText=NPURCH_UNIT.showValue=NPURCH_UNIT.showPy1=NPURCH_UNIT.showPy2=NPURCH_UNIT.Editable=YPURCH_UNIT.Tip=计量单位PURCH_UNIT.TableShowList=nametLabel_5.Type=TLabeltLabel_5.X=831tLabel_5.Y=7tLabel_5.Width=150tLabel_5.Height=15tLabel_5.Text=生成修改确认量不同tLabel_5.Color=blueSUM_TOTIL.Type=TNumberTextFieldSUM_TOTIL.X=681SUM_TOTIL.Y=4SUM_TOTIL.Width=100SUM_TOTIL.Height=20SUM_TOTIL.Text=0SUM_TOTIL.Format=#########0.00SUM_TOTIL.Enabled=NORDER_DESC.Type=TTextFieldORDER_DESC.X=444ORDER_DESC.Y=4ORDER_DESC.Width=140ORDER_DESC.Height=20ORDER_DESC.Text=ORDER_DESC.Enabled=NORDER_CODE.Type=TTextFieldORDER_CODE.X=361ORDER_CODE.Y=4ORDER_CODE.Width=80ORDER_CODE.Height=20ORDER_CODE.Text=ORDER_CODE.Enabled=YDIFF_FLG.Type=TCheckBoxDIFF_FLG.X=805DIFF_FLG.Y=3DIFF_FLG.Width=20DIFF_FLG.Height=23DIFF_FLG.Text=tLabel_7.Type=TLabeltLabel_7.X=610tLabel_7.Y=7tLabel_7.Width=72tLabel_7.Height=15tLabel_7.Text=合计金额:tLabel_7.Color=黑tLabel_6.Type=TLabeltLabel_6.X=290tLabel_6.Y=7tLabel_6.Width=72tLabel_6.Height=15tLabel_6.Text=药品代码:tLabel_6.Color=bluetLabel_4.Type=TLabeltLabel_4.X=21tLabel_4.Y=7tLabel_4.Width=72tLabel_4.Height=15tLabel_4.Text=供应厂商:tLabel_4.Color=blueTABLE_D.Type=TTableTABLE_D.X=1TABLE_D.Y=36TABLE_D.Width=998TABLE_D.Height=394TABLE_D.SpacingRow=1TABLE_D.RowHeight=20TABLE_D.AutoX=YTABLE_D.AutoY=NTABLE_D.AutoWidth=YTABLE_D.AutoHeight=YTABLE_D.AutoSize=0TABLE_D.Header=药品代码,80;药品名称,200;规格,120;单位,60,PURCH_UNIT;上期采购量,80,double,#####0.000;上期销售量,80,double,#####0.000;库存量,80,double,#####0.000;计划生成量,80,double,#####0.000;修改量,80,double,#####0.000;确认量,80,double,#####0.000;采购单价,80,double,#####0.0000;计划采购金额,100,double,#####0.00;供货商,160,SUP_CODE_2;生产厂商,160,MAN_CODE;安全库存量,80,double,#####0.000;最高存量,80,double,#####0.000;在途量,80,double,#####0.000;统计区间起,100;统计区间迄,100TABLE_D.ParmMap=ORDER_CODE;ORDER_DESC;SPECIFICATION;PURCH_UNIT;LASTPUR_QTY;LASTCON_QTY;STOCK_QTY;PLAN_QTY;PUR_QTY;CHECK_QTY;STOCK_PRICE;PLAN_SUM;SUP_CODE;MAN_CODE;SAFE_QTY;MAX_QTY;BUY_UNRECEIVE_QTY;START_DATE;END_DATETABLE_D.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,left;13,left;14,right;15,right;16,right;17,left;18,leftTABLE_D.LockColumns=0,1,2,3,4,5,6,10,11,12,13,14,15,16,17,18TABLE_D.Item=PURCH_UNIT;SUP_CODE_2;MAN_CODETABLE_D.AutoModifyDataStore=YTABLE_D.ClickedAction=onTableDClickedTABLE_D.ChangeAction=tPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=72tPanel_1.Width=1014tPanel_1.Height=211tPanel_1.AutoX=YtPanel_1.AutoWidth=YtPanel_1.Border=组|计划单tPanel_1.Item=tPanel_2tPanel_1.AutoSize=5tPanel_2.Type=TPaneltPanel_2.X=6tPanel_2.Y=19tPanel_2.Width=1002tPanel_2.Height=186tPanel_2.Border=凹tPanel_2.AutoX=YtPanel_2.AutoY=YtPanel_2.AutoWidth=YtPanel_2.AutoHeight=YtPanel_2.AutoSize=0tPanel_2.Item=TABLE_MTABLE_M.Type=TTableTABLE_M.X=77TABLE_M.Y=52TABLE_M.Width=998TABLE_M.Height=182TABLE_M.SpacingRow=1TABLE_M.RowHeight=20TABLE_M.AutoX=YTABLE_M.AutoY=YTABLE_M.AutoWidth=YTABLE_M.AutoHeight=YTABLE_M.AutoSize=0TABLE_M.Header=状态,120;计划单号,100;计划部门,100,ORG_CODE;计划月份,100;生成订购单,100,boolean;计划人员,100,USER_ID;计划时间,120;采确人员,100,USER_ID;采确时间,120;审核人员,100,USER_ID;审核时间,120TABLE_M.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;5,left;6,left;7,left;8,left;9,left;10,leftTABLE_M.ParmMap=PLAN_STATUS;PLAN_NO;ORG_CODE;PLAN_MONTH;CREATE_FLG;PLAN_USER;PLAN_DATE;PUR_USER;PUR_DATE;CHECK_USER;CHECK_DATETABLE_M.Item=ORG_CODE;USER_IDTABLE_M.LockColumns=0,1,2,3,4,5,6,7,8,9,10TABLE_M.ClickedAction=onTableMClickedtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=68tPanel_0.Border=组tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.AutoSize=5tPanel_0.Item=tLabel_0;tLabel_1;tLabel_2;tLabel_3;ORG_CODE;PLAN_MONTH;PLAN_NO;PLAN_DESC;PLAN_FLG;PUR_FLG;CHECK_FLG;PLANEND_FLG;tLabel_8;DESCRIPTION;SUP_CODE_2;MAN_CODE;USER_IDUSER_ID.Type=人员下拉列表USER_ID.X=552USER_ID.Y=82USER_ID.Width=10USER_ID.Height=23USER_ID.Text=TButtonUSER_ID.showID=YUSER_ID.showName=YUSER_ID.showText=NUSER_ID.showValue=NUSER_ID.showPy1=NUSER_ID.showPy2=NUSER_ID.Editable=YUSER_ID.Tip=人员USER_ID.TableShowList=nameUSER_ID.ModuleParmString=USER_ID.ModuleParmTag=USER_ID.Classify=MAN_CODE.Type=生产厂商MAN_CODE.X=885MAN_CODE.Y=80MAN_CODE.Width=81MAN_CODE.Height=23MAN_CODE.Text=TButtonMAN_CODE.showID=YMAN_CODE.showName=YMAN_CODE.showText=NMAN_CODE.showValue=NMAN_CODE.showPy1=NMAN_CODE.showPy2=NMAN_CODE.Editable=YMAN_CODE.Tip=生产厂商MAN_CODE.TableShowList=nameMAN_CODE.ModuleParmString=MAN_CODE.ModuleParmTag=SUP_CODE_2.Type=供应厂商下拉列表SUP_CODE_2.X=883SUP_CODE_2.Y=82SUP_CODE_2.Width=81SUP_CODE_2.Height=23SUP_CODE_2.Text=TButtonSUP_CODE_2.showID=YSUP_CODE_2.showName=YSUP_CODE_2.showText=NSUP_CODE_2.showValue=NSUP_CODE_2.showPy1=NSUP_CODE_2.showPy2=NSUP_CODE_2.Editable=YSUP_CODE_2.Tip=供应厂商SUP_CODE_2.TableShowList=nameSUP_CODE_2.ModuleParmString=SUP_CODE_2.ModuleParmTag=DESCRIPTION.Type=TTextFieldDESCRIPTION.X=706DESCRIPTION.Y=41DESCRIPTION.Width=150DESCRIPTION.Height=20DESCRIPTION.Text=tLabel_8.Type=TLabeltLabel_8.X=633tLabel_8.Y=44tLabel_8.Width=72tLabel_8.Height=15tLabel_8.Text=备    注:tLabel_8.Color=黑PLANEND_FLG.Type=TCheckBoxPLANEND_FLG.X=456PLANEND_FLG.Y=40PLANEND_FLG.Width=88PLANEND_FLG.Height=23PLANEND_FLG.Text=计划完成PLANEND_FLG.Action=onPlanendActionCHECK_FLG.Type=TCheckBoxCHECK_FLG.X=316CHECK_FLG.Y=40CHECK_FLG.Width=115CHECK_FLG.Height=23CHECK_FLG.Text=审核确认完成CHECK_FLG.Action=onCheckActionPUR_FLG.Type=TCheckBoxPUR_FLG.X=155PUR_FLG.Y=40PUR_FLG.Width=129PUR_FLG.Height=23PUR_FLG.Text=采购量确认完成PUR_FLG.Action=onPurActionPLAN_FLG.Type=TCheckBoxPLAN_FLG.X=10PLAN_FLG.Y=40PLAN_FLG.Width=113PLAN_FLG.Height=23PLAN_FLG.Text=计划编辑完成PLAN_FLG.Action=onPlanActionPLAN_DESC.Type=TTextFieldPLAN_DESC.X=706PLAN_DESC.Y=10PLAN_DESC.Width=120PLAN_DESC.Height=20PLAN_DESC.Text=PLAN_NO.Type=TTextFieldPLAN_NO.X=508PLAN_NO.Y=10PLAN_NO.Width=100PLAN_NO.Height=20PLAN_NO.Text=PLAN_MONTH.Type=TTextFormatPLAN_MONTH.X=307PLAN_MONTH.Y=10PLAN_MONTH.Width=100PLAN_MONTH.Height=20PLAN_MONTH.Text=PLAN_MONTH.FormatType=datePLAN_MONTH.Format=yyyy/MMPLAN_MONTH.showDownButton=NORG_CODE.Type=药房下拉列表ORG_CODE.X=85ORG_CODE.Y=8ORG_CODE.Width=120ORG_CODE.Height=23ORG_CODE.Text=TButtonORG_CODE.showID=YORG_CODE.showName=YORG_CODE.showText=NORG_CODE.showValue=NORG_CODE.showPy1=NORG_CODE.showPy2=NORG_CODE.Editable=YORG_CODE.Tip=药房ORG_CODE.TableShowList=nameORG_CODE.ModuleParmTag=ORG_CODE.OrgType=AORG_CODE.ExpandWidth=30tLabel_3.Type=TLabeltLabel_3.X=633tLabel_3.Y=12tLabel_3.Width=72tLabel_3.Height=15tLabel_3.Text=计划名称:tLabel_3.Color=bluetLabel_2.Type=TLabeltLabel_2.X=435tLabel_2.Y=12tLabel_2.Width=72tLabel_2.Height=15tLabel_2.Text=计划单号:tLabel_2.Color=bluetLabel_1.Type=TLabeltLabel_1.X=234tLabel_1.Y=12tLabel_1.Width=72tLabel_1.Height=15tLabel_1.Text=计划月份:tLabel_1.Color=bluetLabel_0.Type=TLabeltLabel_0.X=11tLabel_0.Y=12tLabel_0.Width=72tLabel_0.Height=15tLabel_0.Text=计划部门:tLabel_0.AutoX=NtLabel_0.AutoY=NtLabel_0.AutoSize=15tLabel_0.Color=blue