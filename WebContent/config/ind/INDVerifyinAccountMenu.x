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
UI.button=save;|;delete;|;query;|;clear;|;export;|;toExcel;|;search|;print;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;delete;|;query;|;clear;|;export;|;toExcel;|;search|;print;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

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

toExcel.Type=TMenuItem
toExcel.Text=引入出货单
toExcel.zhText=引入出货单
toExcel.enText=引入出货单
toExcel.Tip=引入出货单
toExcel.zhTip=引入出货单
toExcel.enTip=引入出货单
toExcel.M=S
toExcel.key=Ctrl+E
toExcel.Action=onImpXML
toExcel.pic=export.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.Action=onPrint
print.pic=print.gif


search.Type=TMenuItem
search.Text=查询差异
search.Tip=查询差异
search.M=C
search.Action=onSearch
search.pic=search-2.gif
