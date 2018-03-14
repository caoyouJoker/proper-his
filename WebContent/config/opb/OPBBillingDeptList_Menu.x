##############################################
# <p>Title:开单科室统计表 </p>
#
# <p>Description:开单科室统计表 </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:Javahis </p>
#
# @author zhangk  2010-4-8
# @version 1.0
##############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;detial;|;export;|;clear;|;print;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;detial;|;export;|;print;|;clear;|;close;|;Refresh

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
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

preview.Type=TMenuItem
preview.Text=预览
preview.Tip=预览
preview.M=R
preview.key=Ctrl+R
preview.Action=onPreview
preview.pic=Preview1.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

detial.Type=TMenuItem
detial.Text=明细
detial.Tip=明细
detial.M=D
detial.key=Ctrl+D
detial.Action=onDetial
detial.pic=detail.gif

