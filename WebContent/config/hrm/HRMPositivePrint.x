## TBuilder Config File ## Title:## Company:JavaHis## Author:ehui 2010.02.28## version 1.0#<Type=TFrame>UI.Title=阳性检查结果UI.MenuConfig=%ROOT%\config\hrm\HRMPositivePrintMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.hrm.HRMPositivePrintControlUI.item=tPanel_1UI.layout=nullUI.TopMenu=YUI.TopToolBar=YUI.ShowMenu=YtPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=5tPanel_1.Width=1014tPanel_1.Height=738tPanel_1.AutoX=YtPanel_1.AutoY=YtPanel_1.AutoWidth=YtPanel_1.AutoHeight=YtPanel_1.Border=组tPanel_1.Title=阳性检查结果报告tPanel_1.TopMenu=YtPanel_1.TopToolBar=YtPanel_1.ShowMenu=YtPanel_1.Item=tLabel_2;MR_NO;tLabel_3;tLabel_4;CONTRACT_CODE;tLabel_5;CASE_NO;WORD;COMPANY_CODECOMPANY_CODE.Type=TTextFormatCOMPANY_CODE.X=75COMPANY_CODE.Y=55COMPANY_CODE.Width=138COMPANY_CODE.Height=20COMPANY_CODE.Text=COMPANY_CODE.HorizontalAlignment=2COMPANY_CODE.FormatType=comboCOMPANY_CODE.showDownButton=YCOMPANY_CODE.HisOneNullRow=YCOMPANY_CODE.PopupMenuHeader=代码,100;名称,200COMPANY_CODE.PopupMenuWidth=325COMPANY_CODE.PopupMenuHeight=300COMPANY_CODE.PopupMenuFilter=ID,1;PY1,1COMPANY_CODE.ShowColumnList=NAMECOMPANY_CODE.ValueColumn=IDCOMPANY_CODE.Action=onCompanyChooseWORD.Type=TWordWORD.X=14WORD.Y=93WORD.Width=300WORD.Height=635WORD.AutoWidth=YWORD.AutoHeight=YWORD.PageBorderSize=16WORD.Preview=YCASE_NO.Type=TTextFormatCASE_NO.X=501CASE_NO.Y=56CASE_NO.Width=124CASE_NO.Height=20CASE_NO.Text=CASE_NO.HorizontalAlignment=2CASE_NO.showDownButton=YCASE_NO.FormatType=comboCASE_NO.PopupMenuHeader=病案号,120;姓名,100;身份证号,120CASE_NO.PopupMenuFilter=MR_NO,1;ID_NO,1;PY1,1CASE_NO.ShowColumnList=NAMECASE_NO.ValueColumn=MR_NOCASE_NO.Action=onCaseNoCASE_NO.HisOneNullRow=NtLabel_5.Type=TLabeltLabel_5.X=410tLabel_5.Y=59tLabel_5.Width=89tLabel_5.Height=15tLabel_5.Text=体检人员名称tLabel_5.Color=蓝CONTRACT_CODE.Type=TTextFormatCONTRACT_CODE.X=283CONTRACT_CODE.Y=56CONTRACT_CODE.Width=119CONTRACT_CODE.Height=20CONTRACT_CODE.Text=CONTRACT_CODE.HorizontalAlignment=2CONTRACT_CODE.showDownButton=YCONTRACT_CODE.FormatType=comboCONTRACT_CODE.PopupMenuHeader=合同代码,100;合同名称,100CONTRACT_CODE.PopupMenuFilter=ID,1;PY1,1CONTRACT_CODE.ShowColumnList=NAMECONTRACT_CODE.ValueColumn=IDCONTRACT_CODE.HisOneNullRow=YCONTRACT_CODE.Action=onContractChoosetLabel_4.Type=TLabeltLabel_4.X=219tLabel_4.Y=59tLabel_4.Width=59tLabel_4.Height=15tLabel_4.Text=合同名称tLabel_4.Color=蓝tLabel_3.Type=TLabeltLabel_3.X=15tLabel_3.Y=59tLabel_3.Width=72tLabel_3.Height=15tLabel_3.Text=团体名称tLabel_3.Color=蓝MR_NO.Type=TTextFieldMR_NO.X=75MR_NO.Y=17MR_NO.Width=135MR_NO.Height=20MR_NO.Text=MR_NO.Action=onMrNotLabel_2.Type=TLabeltLabel_2.X=15tLabel_2.Y=20tLabel_2.Width=46tLabel_2.Height=15tLabel_2.Text=病案号tLabel_2.Color=蓝