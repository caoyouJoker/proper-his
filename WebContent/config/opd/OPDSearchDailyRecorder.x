## TBuilder Config File ## Title:## Company:JavaHis## Author:yanj 2013.04.08## version 1.0#<Type=TFrame>UI.Title=医嘱执行状态修改UI.MenuConfig=%ROOT%\config\opd\OPDSearchRecorderMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.opd.OPDModifiyFlgControlUI.item=tPanel_0;tPanel_1UI.layout=nullUI.TopToolBar=YUI.ShowTitle=NUI.ShowMenu=NUI.TopMenu=YtPanel_1.Type=TPaneltPanel_1.X=4tPanel_1.Y=80tPanel_1.Width=1014tPanel_1.Height=501tPanel_1.AutoX=YtPanel_1.AutoY=NtPanel_1.AutoWidth=YtPanel_1.Border=组tPanel_1.Item=TABLETABLE.Type=TTableTABLE.X=5TABLE.Y=8TABLE.Width=748TABLE.Height=446TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoWidth=YTABLE.AutoX=YTABLE.Header=选,30,boolean;病案号,120;姓名,80;医嘱代码,100;医嘱,160;处方签,120;执行状态,100,EXEC_FLGTABLE.LockRows=TABLE.LockColumns=1,2,3,4,5,6TABLE.ParmMap=USE;MR_NO;PAT_NAME;ORDER_CODE;ORDER_DESC;RX_NO;EXEC_FLG;ORDERSET_GROUP_NO;ORDERSET_CODETABLE.Item=EXEC_FLGTABLE.ColumnHorizontalAlignmentData=1,left;2,left;3,left;4,left;5,left;6,lefttPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=66tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.Border=组tPanel_0.Item=tLabel_0;MR_NO;tLabel_1;PAT_NAME;tLabel_2;SEX_CODE;tLabel_38;STARTTIME;tLabel_36;AGE;CASE_NO;tLabel_3;CheckAll;EXEC_FLGEXEC_FLG.Type=TComboBoxEXEC_FLG.X=150EXEC_FLG.Y=38EXEC_FLG.Width=94EXEC_FLG.Height=23EXEC_FLG.Text=TButtonEXEC_FLG.showID=YEXEC_FLG.Editable=YEXEC_FLG.StringData=[[id,name],[,],[Y,已执行],[N,未执行]]EXEC_FLG.ShowName=YEXEC_FLG.TableShowList=nameCheckAll.Type=TCheckBoxCheckAll.X=10CheckAll.Y=40CheckAll.Width=56CheckAll.Height=23CheckAll.Text=全选CheckAll.Selected=YCheckAll.Action=onSelAlltLabel_3.Type=TLabeltLabel_3.X=79tLabel_3.Y=44tLabel_3.Width=72tLabel_3.Height=15tLabel_3.Text=执行状态：CASE_NO.Type=TTextFieldCASE_NO.X=900CASE_NO.Y=9CASE_NO.Width=77CASE_NO.Height=20CASE_NO.Text=CASE_NO.Visible=NAGE.Type=TTextFieldAGE.X=795AGE.Y=6AGE.Width=77AGE.Height=20AGE.Text=AGE.Visible=NtLabel_36.Type=TLabeltLabel_36.X=792tLabel_36.Y=15tLabel_36.Width=42tLabel_36.Height=15tLabel_36.Text=年龄：tLabel_36.Visible=NSTARTTIME.Type=TTextFormatSTARTTIME.X=82STARTTIME.Y=10STARTTIME.Width=123STARTTIME.Height=20STARTTIME.Text=STARTTIME.showDownButton=YSTARTTIME.FormatType=dateSTARTTIME.Format=yyyy/MM/ddSTARTTIME.Enabled=NtLabel_38.Type=TLabeltLabel_38.X=8tLabel_38.Y=15tLabel_38.Width=72tLabel_38.Height=15tLabel_38.Text=就诊时间：SEX_CODE.Type=性别下拉列表SEX_CODE.X=653SEX_CODE.Y=7SEX_CODE.Width=101SEX_CODE.Height=23SEX_CODE.Text=SEX_CODE.showID=YSEX_CODE.showName=YSEX_CODE.showText=NSEX_CODE.showValue=NSEX_CODE.showPy1=YSEX_CODE.showPy2=YSEX_CODE.Editable=YSEX_CODE.Tip=性别SEX_CODE.TableShowList=nameSEX_CODE.ModuleParmString=GROUP_ID:SYS_SEXSEX_CODE.ModuleParmTag=SEX_CODE.Enabled=NtLabel_2.Type=TLabeltLabel_2.X=606tLabel_2.Y=15tLabel_2.Width=48tLabel_2.Height=15tLabel_2.Text=性别：PAT_NAME.Type=TTextFieldPAT_NAME.X=484PAT_NAME.Y=10PAT_NAME.Width=94PAT_NAME.Height=20PAT_NAME.Text=PAT_NAME.Enabled=NtLabel_1.Type=TLabeltLabel_1.X=438tLabel_1.Y=15tLabel_1.Width=44tLabel_1.Height=15tLabel_1.Text=姓名：MR_NO.Type=TTextFieldMR_NO.X=276MR_NO.Y=10MR_NO.Width=140MR_NO.Height=20MR_NO.Text=MR_NO.Action=onQureytLabel_0.Type=TLabeltLabel_0.X=218tLabel_0.Y=15tLabel_0.Width=58tLabel_0.Height=15tLabel_0.Text=病案号：tLabel_0.Color=蓝