#
  # Title: 医保上传结算
  #
  # Description:医保上传结算
  #
  # Copyright: ProperSoft(c) 2012
  #
  # @author pangben 2012-2-3
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;apply;|;upload;|;detailupload;|;changeInfo;|;onSave;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;apply;|;upload;|;detailupload;|;changeInfo;|;onSave;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif

apply.Type=TMenuItem
apply.Text=转日明细
apply.Tip=转日明细
apply.M=A
apply.Action=onApply
apply.pic=sta-1.gif

upload.Type=TMenuItem
upload.Text=分割
upload.Tip=分割
upload.M=U
upload.Action=onUpdate
upload.pic=016.gif

detailupload.Type=TMenuItem
detailupload.Text=日明细上传
detailupload.Tip=日明细上传
detailupload.M=U
detailupload.Action=ondetailUpdate
detailupload.pic=Commit.gif

changeInfo.Type=TMenuItem
changeInfo.Text=转病患基本资料
changeInfo.Tip=转病患基本资料
changeInfo.M=I
changeInfo.Action=onQueryInfo
changeInfo.pic=pat.gif


onSave.Type=TMenuItem
onSave.Text=结算
onSave.Tip=结算
onSave.M=S
onSave.Action=onSettlement
onSave.pic=018.gif

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