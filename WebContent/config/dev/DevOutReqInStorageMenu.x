 #
 # Title: 设备类别参数设定设定
 #
 # Description:设备类别参数设定设定
 #
 # Copyright: JavaHis (c) 2008
 #
 # @author sundx
 # @version 1.0 
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;quary;clear;addFile;generateReceipt;printReceipt;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W 
Window.Item=Refresh   
 
File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;quary;clear;addFile;generateReceipt;printReceipt;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

quary.Type=TMenuItem
quary.Text=查询
quary.Tip=查询
quary.M=Q
quary.key=Ctrl+F
quary.Action=onQuery
quary.pic=query.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

generateReceipt.Type=TMenuItem
generateReceipt.Text=生成入库单
generateReceipt.Tip=生成入库单 
generateReceipt.M=G
generateReceipt.key=Ctrl+G
generateReceipt.Action=onGenerateReceipt
generateReceipt.pic=037.gif

printReceipt.Type=TMenuItem
printReceipt.Text=打印入库单
printReceipt.Tip=打印入库单 
printReceipt.M=P
printReceipt.key=Ctrl+P
printReceipt.Action=onPrintReceipt 
printReceipt.pic=print.gif

barcodePrint.Type=TMenuItem
barcodePrint.Text=条码打印
barcodePrint.Tip=条码打印
barcodePrint.M=B
barcodePrint.key=Ctrl+B
barcodePrint.Action=onBarcodePrint
barcodePrint.pic=barcode.gif

printRFID.Type=TMenuItem
printRFID.Text=RFID条码打印
printRFID.Tip=RFID条码打印
printRFID.M=R
printRFID.key=Ctrl+R
printRFID.Action=onPrintRFID
printRFID.pic=012.gif


close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif