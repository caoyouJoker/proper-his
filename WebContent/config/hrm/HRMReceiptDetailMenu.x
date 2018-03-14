<Type=TMenuBar>
UI.Item=File;Window
UI.button=print;|;reprint;|;disCharge;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=print;|;reprint;|;disCharge;|;clear;|;close

print.Type=TMenuItem
print.Text=费用清单
print.Tip=费用清单
print.M=F
print.key=Ctrl+H
print.Action=onPrintDetail
print.pic=print-2.gif

reprint.Type=TMenuItem
reprint.Text=补打
reprint.Tip=票据补打
reprint.M=F
reprint.key=Ctrl+P
reprint.Action=onRePrint
reprint.pic=print_red.gif

disCharge.Type=TMenuItem
disCharge.Text=退费
disCharge.Tip=退费
disCharge.M=S
disCharge.key=Ctrl+S
disCharge.Action=onDisCharge
disCharge.pic=030.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
