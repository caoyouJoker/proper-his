#############################################
# <p>Title:医务人员喷溅登记列表查询</p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author yanmm
# @version 1.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;regDetail;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;regDetail;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

regDetail.Type=TMenuItem
regDetail.Text=详情
regDetail.Tip=患者详情(Ctrl+R)
regDetail.M=R
regDetail.key=Ctrl+R
regDetail.Action=onRegDetail
regDetail.pic=search-2.gif


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
