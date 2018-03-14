 #
  # Title: 手术介入麻精备药申请Menu
  #
  # Description:手术介入麻精备药申请Menu
  #
  # Copyright: bluecore (c) 2008
  #
  # @author shendr 2013.07.29
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;|;print;|;printD;|;exportD;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;clear;|;print;|;printD;|;exportD;|;close

save.Type=TMenuItem
save.Text=生成请领单
save.Tip=生成请领单
save.M=C
save.Action=onSave
save.pic=save.gif

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

print.Type=TMenuItem
print.Text=汇总打印
print.Tip=汇总打印
print.M=P
print.Action=onPrint
print.pic=print.gif

printD.Type=TMenuItem
printD.Text=明细打印
printD.Tip=明细打印
printD.M=P
printD.Action=onPrintD
printD.pic=print.gif

exportD.Type=TMenuItem
exportD.Text=明细汇出
exportD.Tip=明细汇出
exportD.M=P
exportD.Action=onExportD
exportD.pic=045.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
