<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;clear;|;print;code;barCode;paster;Wrist;|;charge;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;query;clear;|;print;code;barCode;paster;Wrist;|;charge;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

code.Type=TMenuItem
code.Text=条码打印
code.Tip=条码打印
code.M=CO
code.Action=onBarCode
code.pic=barcode.gif

barCode.Type=TMenuItem
barCode.Text=药品条码
barCode.Tip=药品条码
barCode.Action=generatePhaBarcode
barCode.pic=PHL.gif

paster.Type=TMenuItem
paster.Text=打印贴纸
paster.Tip=打印贴纸
paster.Action=onPrintPaster
paster.pic=048.gif

Wrist.Type=TMenuItem
Wrist.Text=打印腕带
Wrist.Tip=打印腕带
Wrist.M=
Wrist.key=
Wrist.Action=onWrist
Wrist.pic=print-1.gif

charge.Type=TMenuItem
charge.Text=补充计费
charge.Tip=补充计费
charge.M=H
charge.key=Ctrl+H
charge.Action=onCharge
charge.pic=bill-1.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif