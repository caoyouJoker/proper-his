 #
  # Title: 备血申请
  #
  # Description: 备血申请
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.04.22
 # @version 1.0
<Type=TMenuBar>  
UI.Item=File;Window
UI.button=save;|;delete;|;query;|;clear;|;print;|;examine;|;removeExamine;|;close

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
File.Item=save;|;delete;|;query;|;clear;|;print;|;close;|;examine;|;removeExamine

save.Type=TMenuItem
save.Text=保存
save.zhText=保存
save.enText=Save
save.Tip=保存(Ctrl+S)
save.zhTip=保存(Ctrl+S)
save.enTip=Save(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.zhText=删除
delete.enText=Delete
delete.Tip=删除(Delete)
delete.zhTip=删除
delete.enTip=Delete
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


query.Type=TMenuItem
query.Text=查询
query.zhText=查询
query.enText=Query
query.Tip=查询(Ctrl+F)
query.zhTip=查询(Ctrl+F)
query.enTip=Query(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.zhText=刷新
Refresh.enText=Refresh
Refresh.Tip=刷新
Refresh.zhTip=刷新
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.zhText=清空
clear.enText=Clear
clear.Tip=清空(Ctrl+Z)
clear.zhTip=清空(Ctrl+Z)
clear.enTip=Clear(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Close
close.Tip=退出(Alt+F4)
close.zhTip=退出(Alt+F4)
close.enTip=Close(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

print.Type=TMenuItem
print.Text=打印
print.zhText=打印
print.enText=Print
print.Tip=打印
print.zhTip=打印
print.enTip=Print
print.M=P
print.Action=onPrint
print.pic=print.gif


take.Type=TMenuItem
take.Text=取血单
take.zhText=取血单
take.enText=take
take.Tip=取血单
take.zhTip=取血单
take.enTip=take
take.M=P
take.Action=onTake
take.pic=I.gif

examine.Type=TMenuItem
examine.Text=审核
examine.zhText=审核
examine.Tip=审核
examine.zhTip=审核
examine.Action=onExamine
examine.pic=correct.gif

removeExamine.Type=TMenuItem
removeExamine.Text=取消审核
removeExamine.zhText=取消审核
removeExamine.Tip=取消审核
removeExamine.zhTip=取消审核
removeExamine.Action=onRemoveExamine
removeExamine.pic=Create.gif

