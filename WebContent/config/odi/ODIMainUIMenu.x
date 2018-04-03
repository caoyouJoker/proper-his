<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;bedcard;|;card;|;tpr;|;tprC;newtpr;pdf;|;tnb;|;EKTcard;|;clear;|;exportxml;|;print;|;queryMem;|;close

Window.Type=TMenu
Window.Text=窗口
Window.zhText=窗口
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=query;|;card;|;bedcard;|;tpr;|;tprC;newtpr;pdf;|;tnb;|;EKTcard;|;clear;|;exportxml;|;print;|;queryMem;|;close

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.zhText=刷新
Refresh.enText=Refresh
Refresh.Tip=刷新
Refresh.zhTip=刷新
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

query.Type=TMenuItem
query.Text=查询
query.zhText=查询
query.enText=Query
query.Tip=查询
query.zhTip=查询
query.enTip=Query
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

card.Type=TMenuItem
card.Text=床头卡
card.zhText=床头卡
card.enText=bed card
card.Tip=床头卡
card.zhTip=床头卡
card.enTip=bed card
card.M=B
card.Action=onBedCard
card.pic=card.gif

clear.Type=TMenuItem
clear.Text=清空
clear.zhText=清空
clear.enText=Clear
clear.Tip=清空
clear.zhTip=清空
clear.enTip=Clear
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出
close.zhTip=退出
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

bedcard.Type=TMenuItem
bedcard.Text=病患信息
bedcard.zhText=病患信息
bedcard.enText=Pat Info
bedcard.Tip=病患信息
bedcard.zhTip=病患信息
bedcard.enTip=Pat Info
bedcard.M=P
bedcard.Action=onPatInfo
bedcard.pic=bedcard.gif

tpr.Type=TMenuItem
tpr.Text=体温单
tpr.Tip=体温单
tpr.M=J
tpr.Action=onVitalSign
tpr.pic=023.gif

tprC.Type=TMenuItem
tprC.Text=儿童体温单
tprC.Tip=儿童体温单
tprC.M=N
tprC.Action=onVitalSignChild
tprC.pic=048.gif

newtpr.Type=TMenuItem
newtpr.Text=新生儿体温单
newtpr.Tip=新生儿体温单
newtpr.M=N
newtpr.Action=onNewArrival
newtpr.pic=035.gif

pdf.Type=TMenuItem
pdf.Text=病历整理
pdf.zhText=病历整理
pdf.enText=病历整理
pdf.Tip=病历整理
pdf.zhTip=病历整理
pdf.enTip=病历整理
pdf.M=X
pdf.Action=onSubmitPDF
pdf.pic=005.gif

tnb.Type=TMenuItem
tnb.Text=血糖报告
tnb.zhText=血糖报告
tnb.enText=血糖报告
tnb.Tip=血糖报告
tnb.zhTip=血糖报告
tnb.enTip=血糖报告
tnb.M=S
tnb.Action=onTnb
tnb.pic=modify.gif

exportxml.Type=TMenuItem
exportxml.Text=导出数据
exportxml.Tip=导出数据
exportxml.M=P
exportxml.Action=onExport
exportxml.pic=export.gif

print.Type=TMenuItem
print.Text=床头卡打印
print.Tip=床头卡打印
print.M=F
print.Action=onPrintO
print.pic=Print.gif

EKTcard.Type=TMenuItem
EKTcard.Text=医疗卡
EKTcard.Tip=医疗卡
EKTcard.M=E
EKTcard.Action=onEKTcard
EKTcard.pic=042.gif


#del by lili 20180402
#queryMem.Type=TMenuItem
#queryMem.Text=套餐查询
#queryMem.zhText=套餐查询
#queryMem.enText=
#queryMem.Tip=套餐查询
#queryMem.zhTip=套餐查询
#queryMem.enTip=
#queryMem.M=Q
#queryMem.Action=onQueryMemPackage
#queryMem.pic=query.gif