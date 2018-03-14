#
# TBuilder Config File 
#
# Title:受试者招募表菜单栏
#
# Company:JavaHis
#
# Author:GUANGL 2016.06.08
#
# version 1.0
#

<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;delete;clear;|;import;export;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;query;delete;clear;|;import;export;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Save)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

import.Type=TMenuItem
import.Text=导入招募信息
import.Tip=导入招募信息
import.M=Q
import.key=
import.Action=onImport
import.pic=002.gif

export.Type=TMenuItem
export.Text=导出招募信息
export.Tip=导出招募信息
export.M=S
export.key=Ctrl+S
export.Action=onExport
export.pic=export.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
