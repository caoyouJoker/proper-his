  # Title: 
  #
  # Description:会诊申请
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author yanj 2013-08-26
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;consApplyDetail;|;delete;|;email;|;message;|;query;|;cancel;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;consApplyDetail;|;delete;|;email;|;message;|;query;|;cancel;|;clear;|;close


save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

consApplyDetail.Type=TMenuItem
consApplyDetail.Text=会诊申请内容
consApplyDetail.zhText=会诊申请内容
consApplyDetail.enText=consapply Profile
consApplyDetail.Tip=会诊申请内容
consApplyDetail.zhTip=会诊申请内容
consApplyDetail.enTip=consapply Profile
consApplyDetail.M=
consApplyDetail.key=
consApplyDetail.Action=onConsApplyDetail
consApplyDetail.pic=detail-1.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

email.Type=TMenuItem
email.Text=发送邮件
email.Tip=发送邮件
email.M=E
email.key=EMAIL
email.Action=onBoardMessage
email.pic=010.gif

message.Type=TMenuItem
message.Text=发送短信
message.Tip=发送短信
message.M=M
message.key=Message
message.Action=onMessage
message.pic=014.gif


cancel.Type=TMenuItem
cancel.Text=取消会诊
cancel.Tip=取消会诊
cancel.M=U
cancel.key=
cancel.Action=onCancel
cancel.pic=030.gif

query.Type=TMenuItem
query.Text=排班查询
query.Tip=排班查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif