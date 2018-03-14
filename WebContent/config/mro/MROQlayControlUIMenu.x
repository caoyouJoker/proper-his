#############################################
# <p>Title:自动质控Menu </p>
#
# <p>Description:自动质控Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangY 2011.05.16
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;execute;|;submit;|;board;|;email;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;execute;|;submit;|;board;|;email;|;export;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

execute.Type=TMenuItem
execute.Text=自动质控
execute.Tip=自动质控
save.M=S
execute.key=
execute.Action=onQlayControlAction
execute.pic=execute.gif

submit.Type=TMenuItem
submit.Text=完成提交
submit.Tip=完成提交
submit.M=Q
submit.key=
submit.Action=onSubmit
submit.pic=Commit.gif

board.Type=TMenuItem
board.Text=发布公布栏
board.Tip=发布公布栏
board.M=N
board.key=
board.Action=onBoardMessage
board.pic=044.gif

email.Type=TMenuItem
email.Text=发送邮件
email.Tip=发送邮件
email.M=N
email.key=
email.Action=onSendMessage
email.pic=emr-1.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新(F5)
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

export.Type=TMenuItem
export.Text=导出
export.Tip=导出(Ctrl+E)
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif