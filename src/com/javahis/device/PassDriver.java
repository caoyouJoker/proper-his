package com.javahis.device;

public class PassDriver {
  static {
    System.loadLibrary("PassDriver_JavaHis"); //载入dll
  }

  public PassDriver() {
	  
  }


  /**
   * PASS客户端共享应用摸式(作废)
   * HIS系统工作站正确登陆之后，第一个被动调用的接口。
   * @return int PASS客户端共享应用摸式注册是否成功
   * 0 注册成功，否则失败
   */
  public native static int RegisterServer();


  /**
   * PASS初始化
   * PASS客户端共享应用摸式注册成功之后，调用该接口
   * @param UserName String 用户工号/登录用户 （必须传值）
   * @param DepartmentName String 科室代码/科室中文名称 （必须传值）
   * @param WorkStationType int 工作站类型(10-医生工作站 20-药学工作站) （必须传值）
   * @return int 初始化PASS是否正确 0 初始化PASS失败 1 初始化PASS正常通过
   */
  public native static int PassInit(String UserName, String DepartmentName,
                                    int WorkStationType);


  /**
   * PASS系统功能有效性设置
   * 在刷新界面、切换病人、弹出右键菜单，总之是需要刷新查询功能时，都根据此函数的返回值来设置相应控件Enabled属性。
   * @param QueryItemNo String 查询的项目标识或项目编号，其中带”/”的部分，表示两上标识都可以用来查询此项目的状态(项目标识不区分大小写)，见下表
   * 0 PASSENABLE PASS连接是否可用 审查相关命令状态
   * 11 SYS-SET 系统参数设置
   * 12 DISQUISITION 用药研究
   * 13 MATCH-DRUG 药品配对信息查询
   * 14 MATCH-ROUTE 药品给药途径信息查询 合理用药辅助功能
   * 24 AlleyEnable 病生状态/过敏史管理 过敏史/病生状态管理
   * 101 CPRRes/CPR 临床用药指南查询
   * 102 Directions 药品说明书查询
   * 103 CPERes/CPE 病人用药教育查询
   * 104 CheckRes/CHECKINFO 药物检验值查询
   * 105 HisDrugInfo 医院药品信息查询
   * 106 MEDInfo 药物信息查询中心
   * 107 Chp 中国药典
   * 501 DISPOSE 处理意见设置
   * 220 LMIM 临床检验信息参考查询 一级菜单查询状态
   * 201 DDIM 药物与药物相互作用查询
   * 202 DFIM 药物与食物相互作用查询
   * 203 MatchRes/IV 国内注射剂体外配伍查询
   * 204 TriessRes/IVM 国外注射剂体外配伍查询
   * 205 DDCM 禁忌症查询
   * 206 SIDE 副作用查询
   * 207 GERI 老年人用药警告查询
   * 208 PEDI 儿童用药警告查询
   * 209 PREG 妊娠期用药警告查询
   * 210 LACT 哺乳期用药警告查询 二级菜单查询状态(专项信息)
   * 301 HELP Pass系统帮助 合理用药帮助系统状态
   * @return int PASS系统功能是否有效性 0 无效 1 有效
   */
  public native static int PassGetState(String QueryItemNo);


  /**
   * PASS应用摸式设置
   * 在PASS初始化成功之后，调用该接口。
   * 注意事项：在嵌套时没有特殊要求的情况下，建议用户使用默认值，默认值为：1,2,0,2,1
   * @param SaveCheckResult int 是否进行采集，取值： 0-不采集 1-依赖系统设置 （必须传值）
   * @param AllowAllegen int 是否管理病人过敏史/病生状态，取值：0-不管理1-由用户传入 2-PASS管理 3-PASS强制管理（必须传值）
   * @param CheckMode int 审查模式，取值：0-普通模式 1-IV模式 （必须传值）
   * @param DisqMode int 调用药研究时是否传入医嘱信息，取值：0-不传 1-要传 2-提示 （必须传值）
   * @param UseDiposeIdea int 是否使用处理意见，取值：0-不使用处理 1-根据设置（必须传值）
   * @return int ：暂无意义
   */
  public native static int PassSetControlParam(int SaveCheckResult,
                                               int AllowAllegen, int CheckMode,
                                               int DisqMode, int UseDiposeIdea);


  /**
   * 传入病人基本信息
   * 调用：病人的基本信息发生变化之后，调用该接口。
   * @param PatientID String 病人病案编号（必须传值）
   * @param VisitID String 当前就诊次数（必须传值）
   * @param Name String 病人姓名 （必须传值）
   * @param Sex String 病人性别 （必须传值）如：男、女，其他值传：未知。
   * @param Birthday String 出生日期 （必须传值）格式：2005-09-20
   * @param Weight String 体重 （可以不传值）单位：KG
   * @param Height String 身高 （可以不传值）单位：CM
   * @param DepartmentName String 医嘱科室ID/医嘱科室名称 （可以不传值）
   * @param Doctor String 主治医生ID/主治医生姓名 （可以不传值）
   * @param LeaveHospitalDate String 出院日期 （可以不传值）
   * @return int 暂无意义
   */
  public native static int PassSetPatientInfo(String PatientID, String VisitID,
                                              String Name, String Sex,
                                              String Birthday, String Weight,
                                              String Height,
                                              String DepartmentName,
                                              String Doctor,
                                              String LeaveHospitalDate);


  /**
   * 传入病人过敏史
   * 调用：当病人过敏史信息由HIS系统管理，并传入到PASS系统进行审查时，调用该接口。
   * 注意事项：如果当前病人有多条过敏史信息时，循环传入。
   * @param AllergenIndex String 过敏原在医嘱中的顺序编号（必须传值）
   * @param AllergenCode String 过敏原编码（必须传值）
   * @param AllergenDesc String 过敏原名称（必须传值）
   * @param AllergenType String 过敏原类型（必须传值）取值如下(判断不分大小写)：
   * AllergenGroup PASS过敏组
   * USER_Drug 用户药品
   * DrugName PASS药物名称
   * @param Reaction String 过敏症状 （可以不传值）
   * @return int ：暂无意义
   */
  public native static int PassSetAllergenInfo(String AllergenIndex,
                                               String AllergenCode,
                                               String AllergenDesc,
                                               String AllergenType,
                                               String Reaction);


  /**
   * 传入病生状态
   * 调用：当病人病生状态信息由HIS系统管理，并传入到PASS系统进行审查时，调用该接口。
   * 注意事项：如果当前病人有多条病生状态信息时，循环传入。
   * @param MedCondIndex String 医疗条件在医嘱中的顺序编号（必须传值
   * @param MedCondCode String 医疗条件编码（必须传值）
   * @param MedCondDesc String 医疗条件名称（必须传值）
   * @param MedCondType String 医疗条件类型（必须传值）传值如下(判断不分大小写)：
   * USER_MedCond 用户医疗条件
   * ICD ICD-9CM编码
   * @param StartDate String 开始日期 格式：yyyy-mm-dd（可以不传值）
   * @param EndDate String 结束日期 格式：yyyy-mm-dd（可以不传值）
   * @return int 暂无意义
   */
  public native static int PassSetMedCond(String MedCondIndex,
                                          String MedCondCode,
                                          String MedCondDesc,
                                          String MedCondType,
                                          String StartDate,
                                          String EndDate);


  /**
   * 传入当前查询药品信息
   * 调用：当需要实现药物信息查询或浮动窗口功能，调用该接口。
   * @param DrugCode String 药品编码（必须传值）
   * @param DrugName String 药品名称（必须传值）
   * @param DoseUnit String 剂量单位（必须传值）
   * @param RouteName String 用药途径中文名称（必须传值）
   * @return int 暂无意义
   */
  public native static int PassSetQueryDrug(String DrugCode,
                                            String DrugName,
                                            String DoseUnit,
                                            String RouteName);


  /**
   * 传入当前药品浮动窗口显示位置
   * 调用：传入当前药品浮动窗口显示位置时，调用本接口，但是调用本接口之前首先调用PassSetQueryDrug() 函数，传入当前药品查询信息。
   * @param left int
   * @param top int
   * @param right int
   * @param bottome int
   * 表示当前编辑框的屏幕范围(要取屏幕的绝对值)，分别为当前编辑框的左上角x,y和右下角x,y（不是width,height）
   * @return int 暂无意义
   */
  public native static int PassSetFloatWinPos(int left,
                                              int top,
                                              int right,
                                              int bottome);


  /**
   * 传入病人用药信息
   * 调用：当需要进行用药医嘱审查时，调用该接口。
   * 注意事项：如果当前病人有多条用药医嘱时，循环传入。传入的医嘱为用药医嘱，
   * 对于工作站类型为10即时性审查时(如：住院医生站或门诊医生站)，
   * 用药结束日期可以不用传值，默认为当天；而对于工作站类型为20回顾性审查时(如：临床药学工作或查询统计)，
   * 用药结束日期必须传值。
   * @param OrderUniqueCode String 医嘱唯一码（必须传值）
   * @param DrugCode String 药品编码 （必须传值）
   * @param DrugName String 药品名称 （必须传值）
   * @param SingleDose String 每次用量 （必须传值）
   * @param DoseUnit String 剂量单位 （必须传值）
   * @param Frequency String 用药频率(次/天)（必须传值）
   * @param StartOrderDate String 用药开始日期，格式：yyyy-mm-dd （必须传值）
   * @param StopOrderDate String 用药结束日期，格式：yyyy-mm-dd （可以不传值），默认值为当天
   * @param RouteName String 给药途径中文名称 （必须传值）
   * @param GroupTag String 成组医嘱标志 （必须传值）
   * @param OrderType String 是否为临时医嘱 1-是临时医嘱 0或空 长期医嘱 （必须传值）
   * @param OrderDoctor String 下嘱医生ID/下嘱医生姓名 （必须传值）
   * @return int 暂无意义
   */
  public native static int PassSetRecipeInfo(String OrderUniqueCode,
                                             String DrugCode,
                                             String DrugName,
                                             String SingleDose,
                                             String DoseUnit,
                                             String Frequency,
                                             String StartOrderDate,
                                             String StopOrderDate,
                                             String RouteName,
                                             String GroupTag,
                                             String OrderType,
                                             String OrderDoctor);


  /**
   * PASS功能调用
   * @param CommandNo int 传入参数：CommandNo命令号，表示调用PASS系统功能命令号。详见下表：
   * 审查类
   *
   * 命令号 命令描述 返回值
   * 1 住院医生工作站保存自动审查 1-审查正常通过 0-存在未处理问题*
   * 2 住院医生工作站提交自动审查 1-审查正常通过 0-存在未处理问题
   * 33 门诊医生工作站保存自动审查 1-审查正常通过 0-存在未处理问题*
   * 34 门诊医生工作站提交自动审查 1-审查正常通过 0-存在未处理问题
   * 3 手工审查 暂无意义*
   * 4 临床药学单病人审查 暂无意义
   * 5 临床药学多病人审查 暂无意义
   * 6 单药警告 暂无意义*
   * 7 手动审查,不显结果界面 暂无意义*
   * 查询类
   * 命令号 说明 返回值
   * 13 药品配对信息查询 暂无意义
   * 14 药品给药途径信息查询 暂无意义
   * 101 临床用药指南查询 暂无意义
   * 102 药品说明书查询 暂无意义
   * 103 病人用药教育查询 暂无意义
   * 104 药物检验值查询 暂无意义
   * 105 医院药品信息查询 暂无意义
   * 106 药物信息查询中心 暂无意义
   * 107 中国药典 暂无意义
   * 201 药物与药物相互作用查询 暂无意义
   * 202 药物与食物相互作用查询 暂无意义
   * 203 国内注射剂体外配伍查询 暂无意义
   * 204 国外注射剂体外配伍查询 暂无意义
   * 205 禁忌症查询 暂无意义
   * 206 副作用查询 暂无意义
   * 207 老年人用药警告查询 暂无意义
   * 208 儿童用药警告查询 暂无意义
   * 209 妊娠期用药警告查询 暂无意义
   * 210 哺乳期用药警告查询 暂无意义
   * 220 临床检验信息参考查询 暂无意义
   * 窗口控制类
   * 命令号 说明 返回值
   * 401 药品信息查询显示浮动窗口 暂无意义
   * 402 药品信息查询关闭所有浮动窗口 暂无意义
   * 403 显示单药最近一次审查结果浮动提示窗口 暂无意义
   * 综合类
   * 命令号 说明 返回值
   * 11 系统参数设置 暂无意义
   * 12 用药研究 暂无意义
   * 21 病生状态/过敏史管理(只读) 暂无意义
   * 22 病生状态/过敏史管理(编辑) 病人过敏史/病生状态是否发生了变化。2-发生了变化，1-未发生变化，<=0-出现程序错误。
   * 23 病生状态/过敏史管理(强制) 病人过敏史/病生状态是否发生了变化。2-发生了变化，1-未发生变化，<=0-出现程序错误。
   * 301 Pass系统帮助 暂无意义
   * 501 调用处理意见设置功能
   * @return int
   */
  public native static int PassDoCommand(int CommandNo);


  /**
   * 获取PASS审查结果警示级别
   * 调用：审查之后该接口。
   * 注意事项：嵌套时用户可以根据审查返回警告值，进行对医嘱是否需要保存或提交控制，
   * 同时还可以将该警告值保存到HIS系统数据库中，便于其它工作站查看等。
   * 如果一条用药医嘱经过PASS审查发现可能存在多个潜在用药问题，系统将以其中警示级别最高的警示色标示该条医嘱 。
   * 需要注意的是，审查返回警告值不是最高代表最严重，而是警示级别最高的警示色标代表最严重。
   * @param DrugUniqueCode int 医嘱唯一编码
   * @return int 警示级别，取值如下
   * 审查返回警告值 警示色 说明
   * -3 无灯 表示PASS出现错误，未进行审查
   * -2 无灯 表示该药品在处方传送时被过滤
   * -1 无灯 表示未经过PASS系统的监测
   * 0 蓝灯 PASS监测未提示相关用药问题
   * 1 黄灯 危害较低或尚不明确，适度关注
   * 2 红灯 不推荐或较严重危害，高度关注
   * 4 橙灯 慎用或有一定危害，较高度关注
   * 3 黑灯 绝对禁忌、错误或致死性危害，严重关注
   */
  public native static int PassGetWarn(String DrugUniqueCode);


  /**
   * 设置需要进行单药警告的药品
   * 调用：审查之后，需要查看警告浮动窗口或单药警告详细信息时，调用该接口。
   * @param DrugUniqueCode int 医嘱唯一编码
   * @return int 暂无意义
   */
  public native static int PassSetWarnDrug(String DrugUniqueCode);


  /**
   * PASS退出
   * @return int 暂无意义
   * 调用：在程序退出时，调用该接口。
   */
  public native static int PassQuit();

  /**
   * 初始化连接DLL
   * @return int 1 成功 0 失败
   */
  public native static int init();
  /**
   * 释放DLL
   * @return int 1 成功 0 失败
   */
  public native static int close();

  public void test()
  {
//    JFrame f = new JFrame();
//    f.setVisible(true);
    System.out.println("test()");
    //new Thread(new Runnable(){
    //  public void run()
    //  {
    for(int i=0;i<3;i++){
    	 init();
         System.out.println("PassInit()" + PassInit("AAA","BBB",10));
         System.out.println(PassGetState("0")+"服务器");
         System.out.println("PassSetControlParam()" + PassSetControlParam(1,2,0,2,1));
         PassSetPatientInfo("A1","1","张张","男","2001-04-05","","","","","");
         PassSetRecipeInfo("1NCB0005", //医嘱唯一码
                        "1NCB0005", //药品编码
                        "维生素C注射液", //药品名称
                        "1", //每次用量
                        "支", //剂量单位
                        "1/1", //用药频率
                        "2009-3-26", //用药开始日期
                        "2009-3-26", //用药结束日期
                        "静脉点滴", //给药途径中文名称
                        "1", //成组医嘱标志(必须传)
                        "1","111"); //是否为临时医嘱
         PassSetRecipeInfo("1KA00015", //医嘱唯一码
                        "1KA00015", //药品编码
                        "维生素K1注射液", //药品名称
                        "1", //每次用量
                        "支", //剂量单位
                        "1/1", //用药频率
                        "2009-3-26", //用药开始日期
                        "2009-3-26", //用药结束日期
                        "静脉点滴", //给药途径中文名称
                        "1", //成组医嘱标志(必须传)
                        "1","111"); //是否为临时医嘱
           PassDoCommand(1);
//         System.out.println("PassSetWarnDrug()" + this.PassSetWarnDrug("1CE00002"));
//         System.out.println("PassSetWarnDrug()" + this.PassSetWarnDrug("1CHB0001"));
         System.out.println("PassGetWarn()aspl"+this.PassGetWarn("1NCB0005"));
//           System.out.println("PassGetWarn()jadl"+this.PassGetWarn("1KA00015"));
           
           
//           System.out.println("PassSetQueryDrug()" + PassSetQueryDrug("1CCA0001","25%葡萄糖酸钙针","",""));
//           this.PassDoCommand(106);
//           System.out.println("PassGetState()"+this.PassGetState("201"));
//           System.out.println("PassDoCommand()" + PassDoCommand(201));
//         System.out.println("PassGetWarn(1) "+PassGetWarn("1"));
//         System.out.println("PassGetWarn(2) "+PassGetWarn("2"));
//         System.out.println("PassSetQueryDrug()" + PassSetQueryDrug("2C010001","25%葡萄糖酸钙针","",""));
//         System.out.println("PassDoCommand(401)" + PassDoCommand(401));
     //  }
     //}).start();
     //System.out.println("PassDoCommand(401)" + PassDoCommand(401));
//     System.out.println("PassDoCommand(102)" + PassDoCommand(102));
//     System.out.println("PassDoCommand(12)" + PassDoCommand(12));
     close();
    }
       
  }
  public static void main(String args[]) {
    PassDriver driver = new PassDriver();
    System.out.println(driver.init());
    driver.test();
  }
}
