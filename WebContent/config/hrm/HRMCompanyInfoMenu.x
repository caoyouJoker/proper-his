 #
  # Title: 健检团体信息设定
  #
  # Description:健检团体信息设定
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;query;newPats;recruitInfo;changePackagem|;readIdCard;|;excel;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;query;newPats;recruitInfo;changePackagem|;readIdCard;|;excel;|;clear;|;close

saveCom.Type=TMenuItem
saveCom.Text=保存团体信息
saveCom.Tip=保存团体信息
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
newPats.Text=导入员工信息
newPats.Tip=导入员工信息
newPats.M=Q
newPats.key=
newPats.Action=onInsertPatByExl
newPats.pic=002.gif

changePackagem.Type=TMenuItem
changePackagem.Text=合同变更
changePackagem.Tip=合同变更
changePackagem.M=C
changePackagem.key=
changePackagem.Action=onChangeContract
changePackagem.pic=convert.gif

readIdCard.Type=TMenuItem
readIdCard.Text=身份证
readIdCard.Tip=身份证
readIdCard.M=C
readIdCard.Action=onReadIdCard
readIdCard.pic=idcard.gif

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

excel.Type=TMenuItem
excel.Text=汇出Excel
excel.Tip=汇出Excel
excel.M=S
excel.key=Ctrl+S
excel.Action=onExcel
excel.pic=export.gif

recruitInfo.Type=TMenuItem
recruitInfo.Text=招募信息表
recruitInfo.Tip=招募信息表
recruitInfo.M=
recruitInfo.key=
recruitInfo.Action=onQueryRecruitInfo
recruitInfo.pic=patlist.gif