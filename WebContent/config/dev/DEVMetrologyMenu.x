 # # Title: 设备计量菜单 # # Description:设备计量菜单 # # Copyright: ProperSoft # # @author wangjc 20150619 # @version 1.0<Type=TMenuBar>UI.Item=File;WindowUI.button=save;|;update;|;query;|;clear;|;closeWindow.Type=TMenuWindow.Text=窗口Window.M=WWindow.Item=Refresh File.Type=TMenuFile.Text=文件File.M=F File.Item=save;|;update;|;query;|;clear;|;close new.Type=TMenuItemnew.Text=新增new.Tip=新增(Ctrl+N)new.M=Nnew.key=Ctrl+Nnew.Action=onNewnew.pic=new.gifsave.Type=TMenuItemsave.Text=计量保存save.Tip=计量保存save.M=Ssave.key=Ctrl+Ssave.Action=onSavesave.pic=save.gifupdate.Type=TMenuItemupdate.Text=修改计量时间update.Tip=修改计量时间update.M=Supdate.key=Ctrl+Supdate.Action=onUpdateDateupdate.pic=save.gifquery.Type=TMenuItemquery.Text=查询query.Tip=查询query.M=Qquery.key=Ctrl+Fquery.Action=onQueryquery.pic=query.gifdelete.Type=TMenuItemdelete.Text=删除delete.Tip=删除(Delete)delete.M=Ndelete.key=Deletedelete.Action=onDeletedelete.pic=delete.gifclear.Type=TMenuItemclear.Text=清空clear.Tip=清空(Ctrl+Z)clear.M=Cclear.key=Ctrl+Zclear.Action=onClearclear.pic=clear.gifgenerateReceipt.Type=TMenuItemgenerateReceipt.Text=生成入库单generateReceipt.Tip=生成入库单generateReceipt.M=GgenerateReceipt.key=Ctrl+GgenerateReceipt.Action=onGenerateReceiptgenerateReceipt.pic=037.gifprintReceipt.Type=TMenuItemprintReceipt.Text=打印入库单printReceipt.Tip=打印入库单printReceipt.M=PprintReceipt.key=Ctrl+PprintReceipt.Action=onPrintReceiptprintReceipt.pic=print.gifbarcodePrint.Type=TMenuItembarcodePrint.Text=条码打印barcodePrint.Tip=条码打印barcodePrint.M=BbarcodePrint.key=Ctrl+BbarcodePrint.Action=onPrintBarcodebarcodePrint.pic=barcode.gifprintRFID.Type=TMenuItemprintRFID.Text=RFID条码打印printRFID.Tip=RFID条码打印printRFID.M=RprintRFID.key=Ctrl+RprintRFID.Action=onPrintRFIDprintRFID.pic=012.gifopenbarcode.Type=TMenuItemopenbarcode.Text=条码扫描openbarcode.Tip=条码扫描openbarcode.M=Bopenbarcode.key=Ctrl+Bopenbarcode.Action=onOpenBarcodeopenbarcode.pic=023.gif    emr.Type=TMenuItememr.Text=填写维护记录单emr.zhText=填写维护记录单emr.enText=填写维护记录单emr.Tip=填写维护记录单emr.zhTip=填写维护记录单emr.enTip=填写维护记录单emr.M=Semr.Action=onAddEmrWriteemr.pic=emr-1.gifBarcodeAndRFID.Type=TMenuItemBarcodeAndRFID.Text=附码BarcodeAndRFID.Tip=附码BarcodeAndRFID.M= BarcodeAndRFID.key=BarcodeAndRFID.Action=onAddRfidBarcodeAndRFID.pic=024.gif  close.Type=TMenuItemclose.Text=退出close.Tip=退出close.M=Xclose.key=Alt+F4close.Action=onCloseclose.pic=close.gifRefresh.Type=TMenuItem Refresh.Text=刷新Refresh.Tip=刷新Refresh.M=RRefresh.key=F5Refresh.Action=onResetRefresh.pic=Refresh.gif