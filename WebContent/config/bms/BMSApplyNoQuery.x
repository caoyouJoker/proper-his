## TBuilder Config File ## Title:备血申请单查询## Company:JavaHis## Author:zhangy 2009.10.10## version 1.0#<Type=TFrame>UI.Title=备血申请单查询UI.MenuConfig=%ROOT%\config\bms\BMSApplyNoQueryMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.bms.BMSApplyNoQueryControlUI.item=tPanel_0;TABLEUI.layout=nullUI.Name=备血申请单查询UI.Text=备血申请单查询UI.Tip=备血申请单查询UI.TopMenu=YUI.TopToolBar=YTABLE.Type=TTableTABLE.X=125TABLE.Y=107TABLE.Width=81TABLE.Height=636TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoY=NTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.AutoSize=5TABLE.Header=患者来源,120,ADM;备血单号,120;备血日期,120;用血日期,120;病案号,130;住院号,130;姓名,100;床号,100;紧急备血,80,boolean;审核,80,booleanTABLE.LockRows=TABLE.LockColumns=allTABLE.ParmMap=ADM_TYPE;APPLY_NO;PRE_DATE;USE_DATE;MR_NO;IPD_NO;PAT_NAME;BED_NO;URG_FLG;CHECK_FLG;CASE_NO;DEPT_CODETABLE.Item=ADMTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,leftTABLE.ClickedAction=onTableClicktPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=99tPanel_0.Border=组tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.Item=tLabel_0;ADM_TYPE_O;ADM_TYPE_E;ADM_TYPE_I;tLabel_1;MR_NO;tLabel_2;IPD_NO;tLabel_4;APPLY_NO;tLabel_5;START_DATE;tLabel_6;END_DATE;ADMADM.Type=TComboBoxADM.X=301ADM.Y=107ADM.Width=81ADM.Height=23ADM.Text=TButtonADM.showID=YADM.Editable=YADM.ShowText=NADM.ShowName=YADM.TableShowList=nameADM.StringData=[[id,name],[,],[O,门诊],[E,急诊],[I,住院]]END_DATE.Type=TTextFormatEND_DATE.X=582END_DATE.Y=69END_DATE.Width=160END_DATE.Height=20END_DATE.Text=END_DATE.Format=yyyy/MM/dd HH:mm:ssEND_DATE.FormatType=dateEND_DATE.showDownButton=YtLabel_6.Type=TLabeltLabel_6.X=549tLabel_6.Y=72tLabel_6.Width=15tLabel_6.Height=15tLabel_6.Text=～START_DATE.Type=TTextFormatSTART_DATE.X=376START_DATE.Y=69START_DATE.Width=160START_DATE.Height=20START_DATE.Text=START_DATE.Format=yyyy/MM/dd HH:mm:ssSTART_DATE.FormatType=dateSTART_DATE.showDownButton=YtLabel_5.Type=TLabeltLabel_5.X=302tLabel_5.Y=72tLabel_5.Width=72tLabel_5.Height=15tLabel_5.Text=备血日期tLabel_5.Color=blueAPPLY_NO.Type=TTextFieldAPPLY_NO.X=94APPLY_NO.Y=69APPLY_NO.Width=150APPLY_NO.Height=20APPLY_NO.Text=tLabel_4.Type=TLabeltLabel_4.X=14tLabel_4.Y=72tLabel_4.Width=72tLabel_4.Height=15tLabel_4.Text=备血单号tLabel_4.Color=blueIPD_NO.Type=TTextFieldIPD_NO.X=376IPD_NO.Y=39IPD_NO.Width=160IPD_NO.Height=20IPD_NO.Text=tLabel_2.Type=TLabeltLabel_2.X=303tLabel_2.Y=42tLabel_2.Width=72tLabel_2.Height=15tLabel_2.Text=住院号tLabel_2.Color=blueMR_NO.Type=TTextFieldMR_NO.X=94MR_NO.Y=39MR_NO.Width=150MR_NO.Height=20MR_NO.Text=MR_NO.Action=onMrNoActiontLabel_1.Type=TLabeltLabel_1.X=14tLabel_1.Y=42tLabel_1.Width=72tLabel_1.Height=15tLabel_1.Text=病案号tLabel_1.Color=blueADM_TYPE_I.Type=TRadioButtonADM_TYPE_I.X=232ADM_TYPE_I.Y=7ADM_TYPE_I.Width=64ADM_TYPE_I.Height=23ADM_TYPE_I.Text=住院ADM_TYPE_I.Group=group1ADM_TYPE_I.Selected=YADM_TYPE_E.Type=TRadioButtonADM_TYPE_E.X=161ADM_TYPE_E.Y=7ADM_TYPE_E.Width=60ADM_TYPE_E.Height=23ADM_TYPE_E.Text=急诊ADM_TYPE_E.Group=group1ADM_TYPE_O.Type=TRadioButtonADM_TYPE_O.X=90ADM_TYPE_O.Y=7ADM_TYPE_O.Width=68ADM_TYPE_O.Height=23ADM_TYPE_O.Text=门诊ADM_TYPE_O.Group=group1ADM_TYPE_O.Selected=NtLabel_0.Type=TLabeltLabel_0.X=14tLabel_0.Y=12tLabel_0.Width=72tLabel_0.Height=15tLabel_0.Text=门急住别tLabel_0.Color=blue