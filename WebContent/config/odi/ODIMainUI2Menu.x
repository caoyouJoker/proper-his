<Type=TMenuBar>
UI.Item=File;Tool;Window
UI.button=query;|;card;bedcard;|;cln;|;emr;|;bas;|;bab;|;sel;|;twd;|;hl;|;smz;|;res;|;opd;|;lis;|;ris;|;hos;|;ibs;|;pdf;|;export;|;clear;|;close

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
File.Item=query;|;card;bedcard;|;hl;|;opd;|;lis;|;ris;|;tnb;|;export;|;clear;|;close

Tool.Type=TMenu
Tool.Text=工具
Tool.zhText=工具
Tool.enText=Tool
Tool.M=F
Tool.Item=cln;|;emr;|;bas;|;bab;|;sel;|;twd;|;smz;|;res;|;hos;|;ibs;|;pdf

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
bedcard.Text=病患
bedcard.zhText=病患信息
bedcard.enText=Pat Info
bedcard.Tip=病患信息
bedcard.zhTip=病患信息
bedcard.enTip=Pat Info
bedcard.M=P
bedcard.Action=onPatInfo
bedcard.pic=bedcard.gif



pdf.Type=TMenuItem
pdf.Text=合病历
pdf.zhText=病历整理
pdf.enText=病历整理
pdf.Tip=病历整理
pdf.zhTip=病历整理
pdf.enTip=病历整理
pdf.M=X
pdf.Action=onSubmitPDF
pdf.pic=005.gif


cln.Type=TMenuItem
cln.Text=诊断
cln.zhText=诊断
cln.enText=诊断
cln.Tip=诊断
cln.zhTip=诊断
cln.enTip=诊断
cln.M=Q
cln.Action=onAddCLNCPath
cln.pic=009.gif

emr.Type=TMenuItem
emr.Text=写病历
emr.zhText=病历书写
emr.enText=病历书写
emr.Tip=病历书写
emr.zhTip=病历书写
emr.enTip=病历书写
emr.M=S
emr.Action=onAddEmrWrite
emr.pic=emr-1.gif

bas.Type=TMenuItem
bas.Text=病案
bas.Tip=病案编目
bas.zhTip=病案编目
bas.enTip=病案编目
bas.M=A
bas.Action=onAddBASY
bas.pic=012.gif


bab.Type=TMenuItem
bab.Text=审病历
bab.zhText=病历审查
bab.enText=病历审查
bab.Tip=病历审查
bab.zhTip=病历审查
bab.enTip=病历审查
bab.M=S
bab.Action=onBABM
bab.pic=029.gif

sel.Type=TMenuItem
sel.Text=医嘱单
sel.zhText=医嘱单
sel.enText=医嘱单
sel.Tip=医嘱单
sel.zhTip=医嘱单
sel.enTip=医嘱单
sel.M=S
sel.Action=onSelYZD
sel.pic=017.gif

twd.Type=TMenuItem
twd.Text=体温表
twd.zhText=体温表
twd.enText=体温表
twd.Tip=体温表
twd.zhTip=体温表
twd.enTip=体温表
twd.M=S
twd.Action=onSelTWD
twd.pic=037.gif

hl.Type=TMenuItem
hl.Text=护理
hl.zhText=护理记录
hl.enText=护理记录
hl.Tip=护理记录
hl.zhTip=护理记录
hl.enTip=护理记录
hl.M=S
hl.Action=onHLSel
hl.pic=048.gif

smz.Type=TMenuItem
smz.Text=手麻
smz.zhText=手术麻醉
smz.enText=手术麻醉
smz.Tip=手术麻醉
smz.zhTip=手术麻醉
smz.enTip=手术麻醉
smz.M=S
smz.Action=onSSMZ
smz.pic=051.gif

res.Type=TMenuItem
res.Text=备血
res.zhText=备血申请
res.enText=备血申请
res.Tip=备血申请
res.zhTip=备血申请
res.enTip=备血申请
res.M=S
res.Action=onBXResult
res.pic=blood.gif

opd.Type=TMenuItem
opd.Text=门急病历
opd.zhText=门急诊病历
opd.enText=门急诊病历
opd.Tip=门急诊病历
opd.zhTip=门急诊病历
opd.enTip=门急诊病历
opd.M=S
opd.Action=onOpdBL
opd.pic=032.gif

lis.Type=TMenuItem
lis.Text=检验
lis.zhText=检验报告
lis.enText=检验报告
lis.Tip=检验报告
lis.zhTip=检验报告
lis.enTip=检验报告
lis.M=S
lis.Action=onLis
lis.pic=LIS.gif

ris.Type=TMenuItem
ris.Text=检查
ris.zhText=检查报告
ris.enText=检查报告
ris.Tip=检查报告
ris.zhTip=检查报告
ris.enTip=检查报告
ris.M=S
ris.Action=onRis
ris.pic=RIS.gif

hos.Type=TMenuItem
hos.Text=出院
hos.zhText=出院通知
hos.enText=出院通知
hos.Tip=出院通知
hos.zhTip=出院通知
hos.enTip=出院通知
hos.M=S
hos.Action=onOutHosp
hos.pic=015.gif

ibs.Type=TMenuItem
ibs.Text=费用
ibs.zhText=费用查询
ibs.enText=费用查询
ibs.Tip=费用查询
ibs.zhTip=费用查询
ibs.enTip=费用查询
ibs.M=S
ibs.Action=onSelIbs
ibs.pic=fee.gif

tnb.Type=TMenuItem
tnb.Text=血糖
tnb.zhText=血糖报告
tnb.enText=血糖报告
tnb.Tip=血糖报告
tnb.zhTip=血糖报告
tnb.enTip=血糖报告
tnb.M=S
tnb.Action=onTnb
tnb.pic=modify.gif

export.Type=TMenuItem
export.Text=导出
export.Tip=导出(Ctrl+E)
export.M=E
export.Action=onExport
export.pic=export.gif