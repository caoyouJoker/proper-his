## TBuilder Config File ## Title:## Company:JavaHis## Author:fuxin 2013.04.17## version 1.0#<Type=TFrame>UI.Title=出库明细查询UI.MenuConfig=%ROOT%\config\dev\DevVerifyOutDetailQueryMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.dev.DevVerifyOutDetailControlUI.item=tPanel_11UI.layout=nullUI.TopMenu=YUI.TopToolBar=YtPanel_11.Type=TPaneltPanel_11.X=16tPanel_11.Y=25tPanel_11.Width=993tPanel_11.Height=703tPanel_11.Border=组|tPanel_11.Item=tPanel_12;tPanel_15tPanel_15.Type=TPaneltPanel_15.X=8tPanel_15.Y=106tPanel_15.Width=975tPanel_15.Height=587tPanel_15.Border=组|出库明细表tPanel_15.Item=TABLE;DEVPRO_CODEDEVPRO_CODE.Type=设备属性下拉区域DEVPRO_CODE.X=105DEVPRO_CODE.Y=40DEVPRO_CODE.Width=81DEVPRO_CODE.Height=23DEVPRO_CODE.Text=DEVPRO_CODE.HorizontalAlignment=2DEVPRO_CODE.PopupMenuHeader=代码,100;名称,100DEVPRO_CODE.PopupMenuWidth=300DEVPRO_CODE.PopupMenuHeight=300DEVPRO_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DEVPRO_CODE.FormatType=comboDEVPRO_CODE.ShowDownButton=YDEVPRO_CODE.Tip=设备属性DEVPRO_CODE.ShowColumnList=NAMETABLE.Type=TTableTABLE.X=10TABLE.Y=24TABLE.Width=953TABLE.Height=552TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoY=YTABLE.AutoHeight=YTABLE.AutoWidth=YTABLE.Header=序列管理,60,boolean;属性,60,DEVPRO_CODE;设备编号,120;顺序号,50;序号,60;设备名称,120;规格,120;出库数量,60;库存数,60;保管人,80;使用人,80;存放地点,80;依附主设备,100;单位,80;单价,80;财产价值,100;残值,100;有效日期,100; 消毒日期,100;备注1,60;备注2,60TABLE.ParmMap=SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;BATCH_SEQ;DEVSEQ_NO;DEV_CHN_DESC;DESCRIPTION;QTY;STOREQTY;CARE_USER;USE_USER;LOC_CODE;SETDEV_CODE; UNIT_CODE;UNIT_PRICE;TOT_PRICE;SCRAP_VALUE;GUAREP_DATE;DEP_DATE;REMARK1;REMARK2TABLE.Item=DEVPRO_CODETABLE.ColumnHorizontalAlignmentData=1,left;2,left;3,right;4,left;5,left;6,right;7,right;8,right;9,right;10,left;11,leftTABLE.LockColumns=alltPanel_12.Type=TPaneltPanel_12.X=9tPanel_12.Y=5tPanel_12.Width=975tPanel_12.Height=104tPanel_12.Border=组|查询条件tPanel_12.Item=tLabel_42;tTextField_8;tLabel_43;tLabel_44;tTextField_9;tTextField_10;tLabel_45;tTextFormat_2;tLabel_46;tTextFormat_3;tLabel_47;tLabel_48;供应厂商下拉列表_0;设备种类下拉区域_3;成本中心下拉区域_1成本中心下拉区域_1.Type=成本中心下拉区域成本中心下拉区域_1.X=91成本中心下拉区域_1.Y=58成本中心下拉区域_1.Width=111成本中心下拉区域_1.Height=23成本中心下拉区域_1.Text=成本中心下拉区域_1.HorizontalAlignment=2成本中心下拉区域_1.PopupMenuHeader=代码,100;名称,100成本中心下拉区域_1.PopupMenuWidth=300成本中心下拉区域_1.PopupMenuHeight=300成本中心下拉区域_1.FormatType=combo成本中心下拉区域_1.ShowDownButton=Y成本中心下拉区域_1.Tip=成本中心成本中心下拉区域_1.ShowColumnList=NAME设备种类下拉区域_3.Type=设备种类下拉区域设备种类下拉区域_3.X=628设备种类下拉区域_3.Y=21设备种类下拉区域_3.Width=117设备种类下拉区域_3.Height=23设备种类下拉区域_3.Text=设备种类下拉区域_3.HorizontalAlignment=2设备种类下拉区域_3.PopupMenuHeader=代码,100;名称,100设备种类下拉区域_3.PopupMenuWidth=300设备种类下拉区域_3.PopupMenuHeight=300设备种类下拉区域_3.PopupMenuFilter=ID,1;NAME,1;PY1,1设备种类下拉区域_3.FormatType=combo设备种类下拉区域_3.ShowDownButton=Y设备种类下拉区域_3.Tip=设备种类设备种类下拉区域_3.ShowColumnList=NAME供应厂商下拉列表_0.Type=供应厂商下拉列表供应厂商下拉列表_0.X=819供应厂商下拉列表_0.Y=21供应厂商下拉列表_0.Width=129供应厂商下拉列表_0.Height=23供应厂商下拉列表_0.Text=TButton供应厂商下拉列表_0.showID=Y供应厂商下拉列表_0.showName=Y供应厂商下拉列表_0.showText=N供应厂商下拉列表_0.showValue=N供应厂商下拉列表_0.showPy1=Y供应厂商下拉列表_0.showPy2=Y供应厂商下拉列表_0.Editable=Y供应厂商下拉列表_0.Tip=供应厂商供应厂商下拉列表_0.TableShowList=name供应厂商下拉列表_0.ModuleParmString=供应厂商下拉列表_0.ModuleParmTag=tLabel_48.Type=TLabeltLabel_48.X=751tLabel_48.Y=26tLabel_48.Width=72tLabel_48.Height=15tLabel_48.Text=生产厂商：tLabel_48.Color=蓝tLabel_47.Type=TLabeltLabel_47.X=563tLabel_47.Y=26tLabel_47.Width=72tLabel_47.Height=15tLabel_47.Text=设备类别：tLabel_47.Color=蓝tTextFormat_3.Type=TTextFormattTextFormat_3.X=433tTextFormat_3.Y=23tTextFormat_3.Width=120tTextFormat_3.Height=20tTextFormat_3.Text=tTextFormat_3.showDownButton=YtTextFormat_3.FormatType=datetTextFormat_3.Format=yyyy/MM/ddtLabel_46.Type=TLabeltLabel_46.X=414tLabel_46.Y=31tLabel_46.Width=16tLabel_46.Height=15tLabel_46.Text=~tTextFormat_2.Type=TTextFormattTextFormat_2.X=289tTextFormat_2.Y=23tTextFormat_2.Width=119tTextFormat_2.Height=20tTextFormat_2.Text=tTextFormat_2.showDownButton=YtTextFormat_2.FormatType=datetTextFormat_2.Format=yyyy/MM/ddtLabel_45.Type=TLabeltLabel_45.X=218tLabel_45.Y=24tLabel_45.Width=72tLabel_45.Height=15tLabel_45.Text=出库时间：tLabel_45.Color=蓝tTextField_10.Type=TTextFieldtTextField_10.X=433tTextField_10.Y=60tTextField_10.Width=204tTextField_10.Height=20tTextField_10.Text=tTextField_10.Enabled=NtTextField_9.Type=TTextFieldtTextField_9.X=288tTextField_9.Y=60tTextField_9.Width=137tTextField_9.Height=20tTextField_9.Text=tLabel_44.Type=TLabeltLabel_44.X=218tLabel_44.Y=62tLabel_44.Width=72tLabel_44.Height=15tLabel_44.Text=设备编码：tLabel_44.Color=蓝tLabel_43.Type=TLabeltLabel_43.X=19tLabel_43.Y=61tLabel_43.Width=72tLabel_43.Height=15tLabel_43.Text=出库科室：tLabel_43.Color=蓝tTextField_8.Type=TTextFieldtTextField_8.X=92tTextField_8.Y=21tTextField_8.Width=109tTextField_8.Height=20tTextField_8.Text=tLabel_42.Type=TLabeltLabel_42.X=20tLabel_42.Y=24tLabel_42.Width=72tLabel_42.Height=15tLabel_42.Text=出库单号：tLabel_42.Color=蓝