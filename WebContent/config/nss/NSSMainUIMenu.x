<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;bedcard;|;card;|;tpr;|;newtpr;pdf;|;cxMrshow;|;showpat;|;clear;|;fee;|;close

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
File.Item=query;|;card;|;bedcard;|;tpr;|;newtpr;pdf;|;cxMrshow;|;showpat;|;clear;|;fee;|;close

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
tpr.key=Ctrl+T
tpr.Action=onVitalSign
tpr.pic=023.gif

newtpr.Type=TMenuItem
newtpr.Text=新生儿体温单
newtpr.Tip=新生儿体温单
newtpr.M=J
newtpr.key=Ctrl+P
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


fee.Type=TMenuItem
fee.Text=补充计费
fee.zhText=补充计费
fee.enText=fee
fee.Tip=补充计费
fee.zhTip=补充计费
fee.enTip=fee
fee.M=F
fee.Action=onFee
fee.pic=fee.gif


cxMrshow.Type=TMenuItem
cxMrshow.Text=时间轴CDR
cxMrshow.Tip=时间轴CDR(Ctrl+Q)
cxMrshow.M=Q
cxMrshow.key=Ctrl+Q
cxMrshow.Action=onCxShow
cxMrshow.pic=038.gif

showpat.Type=TMenuItem
showpat.Text=CDR
showpat.zhText=CDR
showpat.enText=Pat Info
showpat.Tip=CDR
showpat.zhTip=CDR
showpat.enTip=Pat Info
showpat.M=P
showpat.key=Ctrl+P
showpat.Action=onQuerySummaryInfo
showpat.pic=patlist.gif