Pools.item=javahis

//住连接池名称默认MAIN
Pools.MainPool=javahis

//连接 oracle sql db2 catch
javahis.Type=oracle
javahis.Address=127.0.0.1
javahis.DBName=orcl
javahis.UserName=javahis
javahis.Password=javahis

//延时关闭连接时间(秒) 0不出发延时关闭
javahis.CloseTime=0

//默认初始化连接数
javahis.DefaultConnectCount=30
//自动恢复到初始连接值
javahis.isResumeConnectCount=Y

//超长时间
javahis.sqltime=50

//运行缓慢的SQL保存的日志
javahis.sqllog=C:\JavaHis\logs\dbTime.log

//优化开关
javahis.checkOFF=Y

//对象吞吐量
javahis.checkObjCount=100

//优化行数
javahis.checkRowCount=1000

//休眠时间
javahis.checkSheepTime=100

//监听时间
javahis.checkTime=300

//==========db2测试库=================
javahisdb2.Type=db2
javahisdb2.Address=192.168.1.6
javahisdb2.DBName=TEDAICH
javahisdb2.UserName=oral
javahisdb2.Password=oral

//延时关闭连接时间(秒) 0不出发延时关闭
javahisdb2.CloseTime=0

//默认初始化连接数
javahisdb2.DefaultConnectCount=30
//自动恢复到初始连接值
javahisdb2.isResumeConnectCount=Y

//超长时间
javahisdb2.sqltime=50

//运行缓慢的SQL保存的日志
javahisdb2.sqllog=C:\JavaHis\logs\dbTime3.log

//优化开关
javahisdb2.checkOFF=Y

//对象吞吐量
javahisdb2.checkObjCount=100

//优化行数
javahisdb2.checkRowCount=1000

//休眠时间
javahisdb2.checkSheepTime=100

//监听时间
javahisdb2.checkTime=300
