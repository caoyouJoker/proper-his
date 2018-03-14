 #
  # Title: HRM排班
  #
  # Description:HRM排班
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=Window
UI.button=save;|;query;|;consApplyDetail;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;consApplyDetail;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

consApplyDetail.Type=TMenuItem
consApplyDetail.Text=会诊报告
consApplyDetail.zhText=会诊报告
consApplyDetail.enText=consapply Profile
consApplyDetail.Tip=会诊报告
consApplyDetail.zhTip=会诊报告
consApplyDetail.enTip=consapply Profile
consApplyDetail.M=
consApplyDetail.key=
consApplyDetail.Action=onConsReport
consApplyDetail.pic=detail-1.gif

new.Type=TMenuItem
new.Text=新增
new.Tip=新增
new.M=S
new.key=Ctrl+S
new.Action=onNew
new.pic=new.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=N
query.key=Delete
query.Action=onQuery
query.pic=query.gif

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
