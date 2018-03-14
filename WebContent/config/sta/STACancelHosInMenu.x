  #
  # Title: 取消入院查询表
  #
  # Description:取消入院查询表
  #
  # Copyright: Bluecore (c) 2016
  #
  # @author wukai on 20160831
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;print;export;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;print;export;|;clear;|;close

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
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

print.Type=TMenuItem
print.Text=汇出
print.Tip=汇出
print.M=P
print.key=F4
print.Action=onPrint
print.pic=print.gif

export.Type=TMenuItem
export.Text=导出Excel
export.Tip=导出Excel(Alt+E)
export.M=G
export.key=Alt+E
export.Action=onExport
export.pic=exportexcel.gif