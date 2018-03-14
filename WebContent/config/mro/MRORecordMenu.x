#############################################
# <p>Title:病案首页Menu </p>
#
# <p>Description:病案首页Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.10.14
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;print;|;outhospital;|;into;|;finance;|;child;|;studycase;|;patlog;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;print;|;outhospital;|;into;|;finance;|;child;|;studycase;|;patlog;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.Action=onPrint
print.pic=print.gif

outhospital.Type=TMenuItem
outhospital.Text=出院卡片
outhospital.Tip=出院卡片
outhospital.M=O
outhospital.Action=onOutHospital
outhospital.pic=idcard.gif

into.Type=TMenuItem
into.Text=住院处汇入
into.Tip=住院处汇入
into.M=0
into.Action=onInto
into.pic=012.gif

intoDr.Type=TMenuItem
intoDr.Text=医师汇入
intoDr.Tip=医师汇入
intoDr.M=0
intoDr.Action=onIntoDr
intoDr.pic=odidrimg.gif

finance.Type=TMenuItem
finance.Text=财务汇入
finance.Tip=财务汇入
finance.M=I
finance.Action=onFinance
finance.pic=bill.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=关闭
close.Tip=关闭
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif


Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

child.Type=TMenuItem
child.Text=新生儿免疫
child.Tip=新生儿免疫
child.Action=onChild
child.pic=013.gif

studycase.Type=TMenuItem
studycase.Text=生成研究病例
studycase.Tip=生成研究病例
studycase.Action=onCase
studycase.pic=Line.gif


patlog.Type=TMenuItem
patlog.Text=病患日志
patlog.Tip=病患日志
patlog.Action=onPatlog
patlog.pic=modify.gif