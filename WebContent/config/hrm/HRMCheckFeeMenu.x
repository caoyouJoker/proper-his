 #
  # Title: HRM费用清单
  #
  # Description:HRM费用清单
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;print;export;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;print;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=N
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=打印账单
print.Tip=打印账单
print.M=S
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.GIF

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

export.Type=TMenuItem
export.Text=导出Excel
export.Tip=导出Excel
export.M=
export.Action=onExport
export.pic=export.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
