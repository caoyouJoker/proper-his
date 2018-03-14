<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;clear;print;toExcel;|;close

Window.Type=TMenu
Window.Text=窗口
Window.zhText=窗口
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=query;clear;print;toExcel;|;close

query.Type=TMenuItem
query.Text=查询
query.zhText=查询
query.enText=Query
query.Tip=查询
query.zhTip=查询
query.enTip=Query
query.M=S
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=清空
clear.zhText=清空
clear.enText=Clear
clear.Tip=清空
clear.zhTip=清空
clear.enTip=Clear
clear.M=S
clear.key=F5
clear.Action=onClear
clear.pic=clear.gif

print.Type=TMenuItem
print.Text=打印
print.zhText=打印
print.enText=Print
print.Tip=打印
print.zhTip=打印
print.enTip=Print
print.M=S
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

toExcel.Type=TMenuItem
toExcel.Text=绘出EXCEL
toExcel.zhText=绘出EXCEL
toExcel.enText=Expt to Excel
toExcel.Tip=绘出EXCEL
toExcel.zhTip=绘出EXCEL
toExcel.enTip=Expt to Excel
toExcel.M=S
toExcel.key=Ctrl+E
toExcel.Action=onExcel
toExcel.pic=export.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出
close.zhTip=退出
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif