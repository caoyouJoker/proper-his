 #
  # Title: HRM套餐设定
  #
  # Description:HRM套餐设定
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;query;newPats|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;query;newPats;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=S
query.key=
query.Action=onQuery
query.pic=query.gif

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif



newPats.Type=TMenuItem
newPats.Text=导入编码信息
newPats.Tip=导入编码信息
newPats.M=Q
newPats.key=
newPats.Action=onInsertPatByExl
newPats.pic=convert.gif

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
