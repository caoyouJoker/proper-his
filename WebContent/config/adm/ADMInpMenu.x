##############################################
# <p>Title:住院登记Menu </p>
#
# <p>Description:住院登记Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:Javahis </p>
#
# @author JiaoY
# @version 1.0
##############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;stop;|;picture;|;idpicture;|;patinfo;|;bilpay;|;greenpath;|;query;|;immunity;|;print;|;AdultWrist;|;clear;|;regist;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;stop;|;picture;|;idpicture;|;patinfo;|;bilpay;|;greenpath;|;query;|;immunity;|;print;|;AdultWrist;|;clear;|;regist;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

stop.Type=TMenuItem
stop.Text=取消住院
stop.Tip=取消住院
stop.M=
stop.key=
stop.Action=onStop
stop.pic=030.gif

picture.Type=TMenuItem
picture.Text=二代身份证
picture.Tip=二代身份证
picture.M=
picture.key=
picture.Action=onIdCardNo
picture.pic=idcard.gif

idpicture.Type=TMenuItem
idpicture.Text=身份证拍照
idpicture.Tip=身份证拍照
idpicture.M=
idpicture.key=
idpicture.Action=onIdentificationPic
idpicture.pic=Picture.gif

patinfo.Type=TMenuItem
patinfo.Text=病患资料
patinfo.Tip=病患资料
patinfo.M=
patinfo.key=
patinfo.Action=onPatInfo
patinfo.pic=038.gif

bed.Type=TMenuItem
bed.Text=包床管理
bed.Tip=包床管理
bed.M=
bed.key=
bed.Action=onBed
bed.pic=048.gif

bilpay.Type=TMenuItem
bilpay.Text=预交金
bilpay.Tip=预交金
bilpay.M=
bilpay.key=
bilpay.Action=onBilpay
bilpay.pic=openbill-2.gif

greenpath.Type=TMenuItem
greenpath.Text=绿色通道
greenpath.Tip=绿色通道
greenpath.M=
greenpath.key=
greenpath.Action=onGreenPath
greenpath.pic=017.gif

query.Type=TMenuItem
query.Text=病患查询
query.Tip=病患查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=search-1.gif

print.Type=TMenuItem
print.Text=住院证打印
print.Tip=住院证打印
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif

child.Type=TMenuItem
child.Text=新生儿登记
child.Tip=新生儿登记
child.M=
child.key=
child.Action=onChild
child.pic=035.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=Z
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif


Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

regist.Type=TMenuItem
regist.Text=注册照相组件
regist.Tip=注册照相组件
regist.M=
regist.key=
regist.Action=onRegist
regist.pic=007.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

immunity.Type=TMenuItem
immunity.Text=新生儿免疫
immunity.Tip=新生儿免疫
immunity.Action=onImmunity
immunity.pic=013.gif

Wrist.Type=TMenuItem
Wrist.Text=打印腕带
Wrist.Tip=打印腕带
Wrist.M=
Wrist.key=
Wrist.Action=onWrist
Wrist.pic=print-1.gif

ChildWrist.Type=TMenuItem
ChildWrist.Text=儿童腕带
ChildWrist.Tip=儿童腕带
ChildWrist.M=
ChildWrist.key=
ChildWrist.Action=onChildWrist
ChildWrist.pic=print-1.gif

AdultWrist.Type=TMenuItem
AdultWrist.Text=腕带打印
AdultWrist.Tip=腕带打印
AdultWrist.M=
AdultWrist.key=
AdultWrist.Action=onAdultWrist
AdultWrist.pic=print-1.gif
