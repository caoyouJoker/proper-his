 #
  # Title: 转诊申请查询
  #
  # Description:
  #
  # Copyright: JavaHis (c) 2015
  #
  # @author wangb 2015.8.10
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;referralApply;|;referralApplyHl7;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
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

referralApply.Type=TMenuItem
referralApply.Text=转诊单
referralApply.Tip=转诊单
referralApply.M=
referralApply.key=
referralApply.Action=onShowReferral
referralApply.pic=010.gif

referralApplyHl7.Type=TMenuItem
referralApplyHl7.Text=转诊申请HL7
referralApplyHl7.Tip=转诊申请HL7
referralApplyHl7.M=
referralApplyHl7.key=
referralApplyHl7.Action=onOpenRefHL7
referralApplyHl7.pic=Create.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
