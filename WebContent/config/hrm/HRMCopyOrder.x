## TBuilder Config File ## Title: 健检复制他人医嘱界面## Company:BlueCore## Author:wanglong 2013.05.07## version 1.0#<Type=TFrame>UI.Title=复制他人医嘱UI.MenuConfig=UI.Width=600UI.Height=625UI.toolbar=YUI.controlclassname=com.javahis.ui.hrm.HRMCopyOrderControlUI.item=tPanel_0;tPanel_1UI.layout=nullUI.AutoSize=0UI.Border=OWN_AMT.Type=TTextFieldOWN_AMT.X=422OWN_AMT.Y=47OWN_AMT.Width=77OWN_AMT.Height=22OWN_AMT.Text=OWN_AMT.Enabled=NtLabel_3.Type=TLabeltLabel_3.X=359tLabel_3.Y=51tLabel_3.Width=65tLabel_3.Height=15tLabel_3.Text=应收金额PAT_PACKAGE.Type=健检人员套餐组合下拉区域PAT_PACKAGE.X=57PAT_PACKAGE.Y=47PAT_PACKAGE.Width=230PAT_PACKAGE.Height=22PAT_PACKAGE.Text=PAT_PACKAGE.HorizontalAlignment=2PAT_PACKAGE.PopupMenuHeader=序号,40;姓名,130;性别,50;套餐,180PAT_PACKAGE.PopupMenuWidth=415PAT_PACKAGE.PopupMenuHeight=300PAT_PACKAGE.PopupMenuFilter=SEQ_NO,1;PAT_NAME,1;SEX_DESC,1;PACKAGE_DESC,1PAT_PACKAGE.FormatType=comboPAT_PACKAGE.ShowDownButton=YPAT_PACKAGE.Tip=人员套餐组合PAT_PACKAGE.ShowColumnList=PAT_NAME;PACKAGE_DESCPAT_PACKAGE.Action=onShowDetailPAT_PACKAGE.ValueColumn=MR_NOtLabel_2.Type=TLabeltLabel_2.X=23tLabel_2.Y=51tLabel_2.Width=38tLabel_2.Height=15tLabel_2.Text=人员CONTRACT_CODE.Type=健康检查合同下拉区域CONTRACT_CODE.X=396CONTRACT_CODE.Y=15CONTRACT_CODE.Width=155CONTRACT_CODE.Height=22CONTRACT_CODE.HorizontalAlignment=2CONTRACT_CODE.PopupMenuHeader=代码,100;名称,100CONTRACT_CODE.PopupMenuWidth=321CONTRACT_CODE.PopupMenuHeight=300CONTRACT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1CONTRACT_CODE.FormatType=comboCONTRACT_CODE.ShowDownButton=YCONTRACT_CODE.Tip=健康检查合同CONTRACT_CODE.ShowColumnList=NAMECONTRACT_CODE.HisOneNullRow=NCONTRACT_CODE.Action=onContractChooseCONTRACT_CODE.Enabled=NtLabel_1.Type=TLabeltLabel_1.X=359tLabel_1.Y=19tLabel_1.Width=36tLabel_1.Height=15tLabel_1.Text=合同COMPANY_CODE.Type=健康检查团体下拉区域COMPANY_CODE.X=57COMPANY_CODE.Y=15COMPANY_CODE.Width=269COMPANY_CODE.Height=22COMPANY_CODE.HorizontalAlignment=2COMPANY_CODE.PopupMenuHeader=代码,100;名称,100COMPANY_CODE.PopupMenuWidth=305COMPANY_CODE.PopupMenuHeight=300COMPANY_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1COMPANY_CODE.FormatType=comboCOMPANY_CODE.ShowDownButton=YCOMPANY_CODE.Tip=健康检查团体COMPANY_CODE.ShowColumnList=NAMECOMPANY_CODE.HisOneNullRow=NCOMPANY_CODE.Action=onCompanyChooseCOMPANY_CODE.Enabled=NtLabel_0.Type=TLabeltLabel_0.X=23tLabel_0.Y=19tLabel_0.Width=37tLabel_0.Height=15tLabel_0.Text=团体tPanel_0.Type=TPaneltPanel_0.X=0tPanel_0.Y=0tPanel_0.Width=599tPanel_0.Height=574tPanel_0.Border=tPanel_0.item=TABLE;tLabel_0;COMPANY_CODE;tLabel_1;CONTRACT_CODE;tLabel_2;PAT_PACKAGE;tLabel_3;OWN_AMTtPanel_0.AutoWidth=YtPanel_0.AutoHSize=50tPanel_0.AutoHeight=YtPanel_0.AutoSize=1TABLE.Type=TTableTABLE.X=2TABLE.Y=80TABLE.Width=594TABLE.Height=481TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=NTABLE.AutoY=NTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=医嘱名称,400;单价,60,double,#########0.00;数量,50,int//;应收金额,60,double,#########0.00;折扣,40,double,#########0.0;实收金额,60,double,#########0.00TABLE.ParmMap=ORDER_DESC;OWN_PRICE;DISPENSE_QTY//;OWN_AMT;DISCOUNT_RATE;AR_AMTTABLE.LockColumns=allTABLE.ColumnHorizontalAlignmentData=0,left;1,right;2,right//;3,right;4,right;5,rightTABLE.AutoSize=3OPEN.Type=TButtonOPEN.X=175OPEN.Y=22OPEN.Width=81OPEN.Height=23OPEN.Text=展开OPEN.Action=onCopyCANCEL.Type=TButtonCANCEL.X=321CANCEL.Y=22CANCEL.Width=81CANCEL.Height=23CANCEL.Text=取消CANCEL.Action=onCanceltPanel_1.Type=TPaneltPanel_1.X=0tPanel_1.Y=564tPanel_1.Width=599tPanel_1.Height=60tPanel_1.Border=tPanel_1.Item=OPEN;CANCELtPanel_1.AutoWidth=YtPanel_1.AutoX=NtPanel_1.AutoY=YtPanel_1.AutoW=NtPanel_1.AutoH=YtPanel_1.AutoHeight=NtPanel_1.AutoSize=1tPanel_1.AutoHSize=1