 #
  # Title: 转诊转入查询菜单
  #
  # Description:
  #
  # Copyright: JavaHis (c) 2015
  #
  # @author wangb 2015.8.10
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;referralApply;|;emrFileExtract;|;emrFile;|;close

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

referralApply.Type=TMenuItem
referralApply.Text=转诊单
referralApply.Tip=转诊单
referralApply.M=
referralApply.key=
referralApply.Action=onShowReferral
referralApply.pic=010.gif

emrFileExtract.Type=TMenuItem
emrFileExtract.Text=转诊病历提取
emrFileExtract.Tip=转诊病历提取
emrFileExtract.M=
emrFileExtract.key=
emrFileExtract.Action=onExtractEmrFile
emrFileExtract.pic=008.gif

emrFile.Type=TMenuItem
emrFile.Text=转诊病历浏览
emrFile.Tip=转诊病历浏览
emrFile.M=
emrFile.key=
emrFile.Action=onShowEmrFile
emrFile.pic=012.gif

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

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
