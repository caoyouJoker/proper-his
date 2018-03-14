 #
 # Title: 固定资产明细表
 #
 # Description: 固定资产明细表
 #
 # Copyright: JavaHis (c) 2008
 #
 # @author sundx
 # @version 1.0

<Type=TMenuBar>
UI.Item=File;Window
UI.button= query; print; export;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;Refresh;query;|;clear;|;close

delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
execrpt.M=E
export.Action=onExport
export.pic=exportexcel.gif

clear.Type=TMenuItem
clear.Text=汇出
clear.Tip=汇出 
clear.M=K 
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

