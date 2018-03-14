 #
  # Title: 验收入库管理
  #
  # Description:验收入库管理
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009-05-06
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;updateData;|;delete;|;query;|;clear;|;export;|;toExcel1;|printNo;|;print;|;printBarcode;|;printBarcodeBatch;|;printBarcodeAll;|;printList;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;updateData;|;delete;|;query;|;clear;|;export;|;toExcel1;|printNo;|;print;|;printBarcode;|;printBarcodeBatch;|;printBarcodeAll;|;printList;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S   
save.key=Ctrl+S
save.Action=onSave  
save.pic=save.gif
updateData


updateData.Type=TMenuItem  
updateData.Text=效期
updateData.Tip=效期(Ctrl+P)
updateData.M=S
updateData.key=Ctrl+P
updateData.Action=onChangeData
updateData.pic=save.gif



delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

export.Type=TMenuItem
export.Text=引用订单
export.Tip=引用订单
execrpt.M=E
export.Action=onExport
export.pic=045.gif


toExcel1.Type=TMenuItem
toExcel1.Text=导入物资单
toExcel1.Tip=导入物资单
toExcel1.zhTip=导入物资单
toExcel1.enTip=导入物资单
toExcel1.M=S
toExcel1.Action=onImpExcel
toExcel1.pic=export.gif  

printNo.Type=TMenuItem
printNo.Text=打印单据
printNo.Tip=打印单据
printNo.M=P
printNo.Action=onPrint
printNo.pic=print.gif

printList.Type=TMenuItem
printList.Text=打印入库单
printList.Tip=打印入库单
printList.M=P
printList.Action=onPrintList
printList.pic=print.gif

print.Type=TMenuItem
print.Text=条码打印
print.Tip=条码打印
print.M=P
print.Action=onPrintBarcode
print.pic=print.gif
print.Type=TMenuItem
print.Text=条码打印
print.Tip=条码打印
print.M=P
print.Action=onPrintBarcode
print.pic=print.gif


printBarcode.Type=TMenuItem
printBarcode.Text=赋码
printBarcode.Tip=赋码
printBarcode.M=P
printBarcode.Action=onAddRfid
printBarcode.pic=barcode.gif

printBarcodeBatch.Type=TMenuItem
printBarcodeBatch.Text=赋码(批量)
printBarcodeBatch.Tip=赋码(批量)
printBarcodeBatch.M=P
printBarcodeBatch.Action=onAddRfidBatch
printBarcodeBatch.pic=barcode.gif


printBarcodeAll.Type=TMenuItem  
printBarcodeAll.Text=条码打印(批量)
printBarcodeAll.Tip=条码打印(批量)
printBarcodeAll.M=A
printBarcodeAll.Action=onPrintBarcodeAll
printBarcodeAll.pic=print_red.gif


