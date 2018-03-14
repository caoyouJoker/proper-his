#
  # Title: 医保三目字典对应
  #
  # Description:医保三目字典对应
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author pangben 2011-12-10
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;removeUpdate;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;removeUpdate;|;clear;|;close

save.Type=TMenuItem
save.Text=保存修改
save.Tip=保存修改
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

addUpdate.Type=TMenuItem
addUpdate.Text=添加修改医嘱
addUpdate.Tip=添加修改医嘱
addUpdate.M=N
addUpdate.Action=addUpdateSysFee
addUpdate.pic=Commit.gif

removeUpdate.Type=TMenuItem
removeUpdate.Text=移除修改医嘱
removeUpdate.Tip=移除修改医嘱
removeUpdate.M=N
removeUpdate.key=Delete
removeUpdate.Action=onRemoveUpdate
removeUpdate.pic=Commit.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=R
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif