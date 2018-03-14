<Type=TFrame>
UI.Title=TDialog
UI.Width=500
UI.Height=420
UI.Layout=BL
UI.Modal=true
UI.Item=PWork|中;PButton|下

BL.Type=BorderLayout

PWork.Type=TPanel
PWork.Layout=BL
PWork.Item=PicPanel|左;WorkPanel|中;Line|下

PicPanel.Type=TPanel
PicPanel.Layout=BFL
PicPanel.Item=LPic

LPic.Type=TLabel
LPic.Pic=p1.JPG

WorkPanel.Type=TPanel
WorkPanel.Layout=null

Line.Type=TLine

PButton.Type=TPanel
PButton.Layout=BFL
PButton.Item=OK;Cancel;Refurbish

BFL.Type=FlowLayout|<i>2|<i>10|<i>10

OK.Type=TButton
OK.Text=确定
OK.Action=onOK

Cancel.Type=TButton
Cancel.Text=取消
Cancel.Key=ESC
Cancel.Action=onClose

Refurbish.Type=TButton
Refurbish.Text=刷新
Refurbish.Action=onReset