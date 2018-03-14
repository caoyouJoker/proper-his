## TBuilder Config File ## Title:## Company:JavaHis## Author:杨菁菁 2015.04.08## version 1.0#<Type=TFrame>UI.Title=科室血袋接收UI.MenuConfig=%ROOT%\config\ope\OPEBloodbagReceiveMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.ope.OPEBloodbagReceiveControlUI.item=tPanel_0;TABLEUI.layout=nullUI.ShowMenu=NUI.ShowTitle=NUI.TopMenu=YUI.TopToolBar=YUI.FocusList=BAR_CODETABLE.Type=TTableTABLE.X=10TABLE.Y=159TABLE.Width=1002TABLE.Height=412TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoY=NTABLE.AutoHeight=YTABLE.AutoWidth=YTABLE.Header=选,30,boolean;院外码,120;院内条码,100;出库单号,100;血品,150,BLD_CODE;血型,50;RH,40;规格,180,SUBCAT_CODE;病案号,100;姓名,60;性别,60,SEX;年龄,50;床号,100;出库人员,80,RECEIVER;出库时间,150,timestamp,yyyy/MM/dd HH:mm:ss;接收人员,80,RECEIVER;接收日期,150,timestamp,yyyy/MM/dd HH:mm:ssTABLE.ParmMap=SELECT_FLG;ORG_BARCODE;BLOOD_NO;OUT_NO;BLD_CODE;BLD_TYPE;RH_FLG;SUBCAT_CODE;MR_NO;NAME;SEX;AGE;BED_NO;OUT_USER;OUT_DATE;RECEIVED_USER;RECEIVED_DATETABLE.Item=RECEIVER;SEX;BLD_CODE;SUBCAT_CODETABLE.LockColumns=1,2,3,4,5,6,7,8,9,10,11,12,13,14TABLE.LockRows=TABLE.ColumnHorizontalAlignmentData=1,left;2,left;3,left;4,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,lefttPanel_0.Type=TPaneltPanel_0.X=11tPanel_0.Y=24tPanel_0.Width=1008tPanel_0.Height=130tPanel_0.Border=组tPanel_0.Item=tLabel_0;tLabel_1;RECEIVER;CONFIRM;UNCONFIRM;tLabel_32;CONFIRM_START_DATE;tLabel_33;CONFIRM_END_DATE;tLabel_34;OUT_NO;REGION_CODE;SEX;EXEC_CODE;tLabel_2;BLOOD_NO;BLD_CODE;SUBCAT_CODE;tLabel_3;tLabel_4;CHECK_SUM;SUMtPanel_0.AutoWidth=YSUM.Type=TTextFieldSUM.X=555SUM.Y=87SUM.Width=77SUM.Height=20SUM.Text=SUM.Enabled=NCHECK_SUM.Type=TTextFieldCHECK_SUM.X=348CHECK_SUM.Y=87CHECK_SUM.Width=77CHECK_SUM.Height=20CHECK_SUM.Text=CHECK_SUM.Enabled=NtLabel_4.Type=TLabeltLabel_4.X=495tLabel_4.Y=90tLabel_4.Width=57tLabel_4.Height=15tLabel_4.Text=总数量:tLabel_3.Type=TLabeltLabel_3.X=258tLabel_3.Y=89tLabel_3.Width=87tLabel_3.Height=15tLabel_3.Text=已核收数量:SUBCAT_CODE.Type=血品规格SUBCAT_CODE.X=751SUBCAT_CODE.Y=53SUBCAT_CODE.Width=81SUBCAT_CODE.Height=23SUBCAT_CODE.Text=TButtonSUBCAT_CODE.showID=YSUBCAT_CODE.showName=YSUBCAT_CODE.showText=NSUBCAT_CODE.showValue=NSUBCAT_CODE.showPy1=YSUBCAT_CODE.showPy2=YSUBCAT_CODE.Editable=NSUBCAT_CODE.Tip=诊室SUBCAT_CODE.TableShowList=nameSUBCAT_CODE.ModuleParmTag=SUBCAT_CODE.Visible=NSUBCAT_CODE.Enabled=NBLD_CODE.Type=血品BLD_CODE.X=650BLD_CODE.Y=50BLD_CODE.Width=81BLD_CODE.Height=23BLD_CODE.Text=TButtonBLD_CODE.showID=YBLD_CODE.showName=YBLD_CODE.showText=NBLD_CODE.showValue=NBLD_CODE.showPy1=YBLD_CODE.showPy2=YBLD_CODE.Editable=NBLD_CODE.Tip=血品BLD_CODE.TableShowList=nameBLD_CODE.ModuleParmTag=BLD_CODE.Enabled=NBLD_CODE.Visible=NBLOOD_NO.Type=TTextFieldBLOOD_NO.X=573BLOOD_NO.Y=12BLOOD_NO.Width=148BLOOD_NO.Height=20BLOOD_NO.Text=BLOOD_NO.Action=onBloodNotLabel_2.Type=TLabeltLabel_2.X=495tLabel_2.Y=14tLabel_2.Width=72tLabel_2.Height=15tLabel_2.Text=院内条码：tLabel_2.Color=蓝EXEC_CODE.Type=人员EXEC_CODE.X=553EXEC_CODE.Y=96EXEC_CODE.Width=81EXEC_CODE.Height=23EXEC_CODE.Text=EXEC_CODE.HorizontalAlignment=2EXEC_CODE.PopupMenuHeader=代码,100;名称,100EXEC_CODE.PopupMenuWidth=300EXEC_CODE.PopupMenuHeight=300EXEC_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1EXEC_CODE.FormatType=comboEXEC_CODE.ShowDownButton=YEXEC_CODE.Tip=人员EXEC_CODE.ShowColumnList=NAMEEXEC_CODE.Visible=NEXEC_CODE.Enabled=NSEX.Type=性别下拉列表SEX.X=551SEX.Y=50SEX.Width=81SEX.Height=23SEX.Text=TButtonSEX.showID=YSEX.showName=YSEX.showText=NSEX.showValue=NSEX.showPy1=YSEX.showPy2=YSEX.Editable=NSEX.Tip=性别SEX.TableShowList=nameSEX.ModuleParmString=GROUP_ID:SYS_SEXSEX.ModuleParmTag=SEX.Visible=NREGION_CODE.Type=区域下拉列表REGION_CODE.X=90REGION_CODE.Y=11REGION_CODE.Width=147REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=YREGION_CODE.showPy2=YREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=REGION_CODE.Enabled=YOUT_NO.Type=TTextFieldOUT_NO.X=318OUT_NO.Y=11OUT_NO.Width=148OUT_NO.Height=20OUT_NO.Text=OUT_NO.Required=NOUT_NO.Action=onOutNotLabel_34.Type=TLabeltLabel_34.X=259tLabel_34.Y=13tLabel_34.Width=57tLabel_34.Height=15tLabel_34.Text=出库单：tLabel_34.Color=蓝CONFIRM_END_DATE.Type=TTextFormatCONFIRM_END_DATE.X=272CONFIRM_END_DATE.Y=50CONFIRM_END_DATE.Width=150CONFIRM_END_DATE.Height=22CONFIRM_END_DATE.Text=CONFIRM_END_DATE.Format=yyyy/MM/dd HH:mm:ssCONFIRM_END_DATE.FormatType=dateCONFIRM_END_DATE.showDownButton=YCONFIRM_END_DATE.HorizontalAlignment=2tLabel_33.Type=TLabeltLabel_33.X=255tLabel_33.Y=53tLabel_33.Width=25tLabel_33.Height=15tLabel_33.Text=至CONFIRM_START_DATE.Type=TTextFormatCONFIRM_START_DATE.X=102CONFIRM_START_DATE.Y=49CONFIRM_START_DATE.Width=150CONFIRM_START_DATE.Height=22CONFIRM_START_DATE.Text=CONFIRM_START_DATE.Format=yyyy/MM/dd HH:mm:ssCONFIRM_START_DATE.FormatType=dateCONFIRM_START_DATE.showDownButton=YCONFIRM_START_DATE.HorizontalAlignment=2tLabel_32.Type=TLabeltLabel_32.X=30tLabel_32.Y=52tLabel_32.Width=72tLabel_32.Height=15tLabel_32.Text=接收时间：tLabel_32.Color=蓝UNCONFIRM.Type=TRadioButtonUNCONFIRM.X=471UNCONFIRM.Y=50UNCONFIRM.Width=81UNCONFIRM.Height=23UNCONFIRM.Text=未接收UNCONFIRM.Group=confirmUNCONFIRM.Action=onUnConfirmUNCONFIRM.Selected=YUNCONFIRM.Color=蓝CONFIRM.Type=TRadioButtonCONFIRM.X=558CONFIRM.Y=50CONFIRM.Width=81CONFIRM.Height=23CONFIRM.Text=已接收CONFIRM.Group=confirmCONFIRM.Action=onConfirmCONFIRM.Color=蓝RECEIVER.Type=人员RECEIVER.X=93RECEIVER.Y=85RECEIVER.Width=104RECEIVER.Height=23RECEIVER.Text=RECEIVER.HorizontalAlignment=2RECEIVER.PopupMenuHeader=代码,100;名称,100RECEIVER.PopupMenuWidth=300RECEIVER.PopupMenuHeight=300RECEIVER.PopupMenuFilter=ID,1;NAME,1;PY1,1RECEIVER.FormatType=comboRECEIVER.ShowDownButton=YRECEIVER.Tip=人员RECEIVER.ShowColumnList=NAMERECEIVER.HisOneNullRow=YRECEIVER.Enabled=NtLabel_1.Type=TLabeltLabel_1.X=30tLabel_1.Y=90tLabel_1.Width=56tLabel_1.Height=15tLabel_1.Text=接收人：tLabel_1.Color=蓝tLabel_0.Type=TLabeltLabel_0.X=30tLabel_0.Y=16tLabel_0.Width=45tLabel_0.Height=15tLabel_0.Text=区域：