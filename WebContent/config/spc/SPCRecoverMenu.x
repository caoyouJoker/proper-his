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
UI.button=save;query;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;query;clear;|;close

saveCom.Type=TMenuItem
saveCom.Text=保存
saveCom.Tip=保存
saveCom.M=S
saveCom.key=Ctrl+S
saveCom.Action=onSaveCom
saveCom.pic=013.gif

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


saveContractM.Type=TMenuItem
saveContractM.Text=保存主项合同信息
saveContractM.Tip=保存主项合同信息
saveContractM.M=Q
saveContractM.key=Ctrl+F
saveContractM.Action=onSaveContractM
saveContractM.pic=038.gif

saveContractD.Type=TMenuItem
saveContractD.Text=保存员工信息
saveContractD.Tip=保存员工信息
saveContractD.M=Q
saveContractD.key=Ctrl+F
saveContractD.Action=onSaveContractD
saveContractD.pic=036.gif

newPat.Type=TMenuItem
newPat.Text=新建员工信息
newPat.Tip=新建员工信息
newPat.M=Q
newPat.key=
newPat.Action=onInsertPat
newPat.pic=sta-1.gif

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
