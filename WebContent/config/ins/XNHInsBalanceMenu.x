#
  # Title: 新农合联网结算
  # Description:新农合联网结算
  # Copyright: 2017
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;apply;|;upload;|;onCancel;|;onSaveY;|;onSave;|;onSaveC;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;apply;|;upload;|;onCancel;|;onSaveY;|;onSave;|;onSaveC;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif

apply.Type=TMenuItem
apply.Text=转明细
apply.Tip=转明细
apply.M=A
apply.Action=onApply
apply.pic=sta-1.gif

upload.Type=TMenuItem
upload.Text=明细上传
upload.Tip=明细上传
upload.M=U
upload.Action=onUpload
upload.pic=016.gif

onCancel.Type=TMenuItem
onCancel.Text=明细撤销
onCancel.Tip=明细撤销
onCancel.M=I
onCancel.Action=onCancelDetail
onCancel.pic=pat.gif

onSaveY.Type=TMenuItem
onSaveY.Text=预结算
onSaveY.Tip=预结算
onSaveY.M=S
onSaveY.Action=onSettlementY
onSaveY.pic=017.gif


onSave.Type=TMenuItem
onSave.Text=结算
onSave.Tip=结算
onSave.M=S
onSave.Action=onSettlement
onSave.pic=018.gif

onSaveC.Type=TMenuItem
onSaveC.Text=退结算
onSaveC.Tip=退结算
onSaveC.M=S
onSaveC.Action=onSettlementC
onSaveC.pic=019.gif

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