declare namespace API {
  type acceptParams = {
    /** 订单ID */
    orderId: number;
  };

  type add5Params = {
    /** 队列名 */
    queueName: string;
    /** 订单号 */
    orderNum: string;
    /** 延迟时间(秒) */
    time: number;
  };

  type Address = {
    /** 请求参数 */
    params?: Record<string, any>;
    id?: number;
    uid?: number;
    /** 地点 */
    title?: string;
    /** 地址详情 */
    detail?: string;
    /** 经度 */
    lon?: string;
    /** 纬度 */
    lat?: string;
    /** 姓名 */
    name?: string;
    /** 电话 */
    phone?: string;
    /** 默认 0 否 1 是 */
    isDefault?: number;
    /** 创建时间 */
    createTime?: string;
    /** 创建人 */
    createId?: number;
    /** 修改时间 */
    updateTime?: string;
    /** 修改人 */
    updateId?: number;
  };

  type AddressVO = {
    /** 地点 */
    title?: string;
    /** 地址详情 */
    detail?: string;
    /** 经度 */
    lon?: string;
    /** 纬度 */
    lat?: string;
    /** 姓名 */
    name?: string;
    /** 电话 */
    phone?: string;
  };

  type bindPhoneParams = {
    phoneCode: string;
  };

  type cancelbeforeParams = {
    /** 订单ID */
    orderId: number;
  };

  type CapitalFlow = {
    id?: number;
    /** 订单id */
    orderId?: number;
    /** 代理id */
    agentId?: number;
    /** 代理收益 */
    profitAgent?: number;
    /** 跑腿id */
    runnerId?: number;
    /** 跑腿收益 */
    profitRunner?: number;
    /** 用户id */
    userId?: number;
    /** 用户收益 */
    profitUser?: number;
    /** 平台收益 */
    profitPlat?: number;
    /** 类型 订单收益 跑腿提现 代理提现 */
    type?: number;
    createTime?: string;
  };

  type confirmParams = {
    /** 订单ID */
    orderId: number;
  };

  type deliveryParams = {
    /** 订单ID */
    orderId: number;
  };

  type destroyParams = {
    /** 队列名 */
    queueName: string;
  };

  type detailParams = {
    /** 订单ID */
    orderId: number;
  };

  type downloadParams = {
    /** OSS对象ID */
    ossId: number;
  };

  type FuuConfig = {
    /** 项目名称 */
    name?: string;
    /** 版本 */
    version?: string;
    /** 版权年份 */
    copyrightYear?: string;
    /** 超时未支付取消时长 (分钟) */
    payCancelTtl?: number;
    /** 超时未完成自动完成时长 (小时) */
    autoCompleteTtl?: number;
    /** 完成订单凭证上限 （张） */
    completionImagesLimit?: number;
    /** 信用分上限（初始） */
    creditUpperLimit?: number;
    /** 信用分下限 */
    creditLowerLimit?: number;
    /** 信用分每次扣除 */
    creditDeduction?: number;
  };

  type get1Params = {
    /** 要获取的校区ID */
    id: number;
  };

  type getAppealParams = {
    /** 要查询的订单ID */
    orderId: number;
  };

  type getByIdParams = {
    /** 要获取的地址ID */
    addressId: number;
  };

  type getInfoParams = {
    /** OSS配置ID */
    ossConfigId: number;
  };

  type getParams = {
    schoolId: number;
    serviceType: number;
  };

  type initchatParams = {
    /** 订单ID */
    orderId: number;
  };

  type list1Params = {
    runnerApply: RunnerApply;
    pageQuery: PageQuery;
  };

  type list2Params = {
    bo: OssBo;
    pageQuery: PageQuery;
  };

  type list3Params = {
    bo: OssConfigBo;
    pageQuery: PageQuery;
  };

  type list4Params = {
    tags: Tags;
    pageQuery: PageQuery;
  };

  type list5Params = {
    /** 包含查询参数的订单查询对象 */
    orderQuery: OrderQuery;
    /** 包含分页详细信息的分页查询对象 */
    pageQuery: PageQuery;
  };

  type list6Params = {
    /** 包含查询参数的订单申诉对象 */
    orderAppeal: OrderAppeal;
    /** 包含分页详细信息的分页查询对象 */
    pageQuery: PageQuery;
  };

  type list7Params = {
    /** 包含查询参数的校区对象 */
    school: School;
    /** 包含分页详细信息的分页查询对象 */
    pageQuery: PageQuery;
  };

  type list8Params = {
    /** 校区ID */
    schoolId: number;
  };

  type list9Params = {
    /** 包含查询参数的地址对象 */
    address: Address;
    /** 包含分页详细信息的分页查询对象 */
    pageQuery: PageQuery;
  };

  type listCurr1Params = {
    /** 包含分页详细信息的分页查询对象 */
    pageQuery: PageQuery;
  };

  type listCurrParams = {
    capitalFlow: CapitalFlow;
    pageQuery: PageQuery;
  };

  type listParams = {
    userType: number;
  };

  type listPcParams = {
    userPcQuery: UserPcQuery;
    pageQuery: PageQuery;
  };

  type listUser1Params = {
    /** 包含查询参数的校区区域对象 */
    schoolRegion: SchoolRegion;
  };

  type listUserParams = {
    /** 包含查询参数的订单查询对象 */
    orderQuery: OrderQuery;
    /** 包含分页详细信息的分页查询对象 */
    pageQuery: PageQuery;
  };

  type listWxParams = {
    userWxQuery: UserWxQuery;
    pageQuery: PageQuery;
  };

  type LoginBody = {
    /** 用户名 */
    username: string;
    /** 用户密码 */
    password: string;
  };

  type MoneyRecode = {
    id?: number;
    /** uid */
    uid?: number;
    /** 提现金额 */
    cash?: number;
    /** 提现去向平台 */
    platform?: string;
    /** 卡号 */
    card?: string;
    /** 状态 0 驳回 1 通过 2 审核中 */
    status?: number;
    /** 用户类型 */
    type?: number;
    /** 用户备注 */
    remark?: string;
    /** 审核人 */
    updateId?: number;
    /** 审核时间 */
    updateTime?: string;
    /** 创建时间 */
    createTime?: string;
    /** 审核反馈 */
    feedback?: string;
  };

  type OrderAppeal = {
    /** 请求参数 */
    params?: Record<string, any>;
    id?: number;
    /** 订单id */
    orderId?: string;
    /** 学校id */
    schoolId?: number;
    /** 申诉时间 */
    appealTime?: string;
    /** 申诉理由 */
    appealReason?: string;
    /** 申诉状态 0 不通过 1 通过 2 申诉中 */
    appealStatus?: number;
    /** 申诉更新时间 */
    updateTime?: string;
    /** 申诉驳回原因 */
    remarks?: string;
    /** 更新人id */
    updateId?: number;
    /** 更新人类型 */
    updateType?: number;
  };

  type OrderAppealDTO = {
    orderId?: number;
    /** 申诉理由 */
    appealReason?: string;
    ossAppealList?: number[];
  };

  type OrderAppealVO = {
    orderAppeal?: OrderAppeal;
    imageUrls?: string[];
  };

  type OrderAttachment = {
    id?: number;
    /** 订单id */
    orderId?: number;
    /** 文件原始名 */
    fileName?: string;
    /** 文件地址 */
    fileUrl?: string;
    /** 文件后缀 */
    fileType?: string;
    /** 文件大小字节 */
    fileSize?: number;
    /** 类型 1订单附件 2完成凭证 3申诉凭证 */
    type?: number;
  };

  type OrderCancelDTO = {
    /** 订单id */
    orderId?: number;
    /** 取消原因 */
    cancelReason?: string;
  };

  type OrderChat = {
    id?: number;
    orderId?: number;
    /** 发送者id */
    senderId?: number;
    /** 发送者类型 */
    senderType?: number;
    /** 接收者ids */
    recipients?: string;
    /** 消息类型 */
    msgType?: number;
    /** 消息体 */
    message?: string;
    /** 已读ids */
    readIds?: string;
    createTime?: string;
  };

  type OrderCompleteDTO = {
    completionImages?: number[];
    orderId?: number;
  };

  type OrderDetailVO = {
    avatarRunner?: string;
    nicknameRunner?: string;
    avatarUser?: string;
    nicknameUser?: string;
    orderMain?: OrderMain;
    orderPayment?: OrderPayment;
    progress?: OrderProgress;
    /** 附件图片 */
    attachImages?: OrderAttachment[];
    /** 附件文件 */
    attachFiles?: OrderAttachment[];
    /** 完成凭证 */
    completionImages?: OrderAttachment[];
  };

  type OrderMain = {
    id?: number;
    /** 学校id */
    schoolId?: number;
    /** 服务类型 0 帮取送 1 代买 2 万能服务 */
    serviceType?: number;
    /** 标签 */
    tag?: string;
    /** 物品重量 */
    weight?: string;
    startAddress?: AddressVO;
    endAddress?: AddressVO;
    /** 具体描述（暴露） */
    detail?: string;
    /** 是否指定时间 0 否 1 是 */
    isTimed?: number;
    /** 指定时间 */
    specifiedTime?: string;
    /** 未结单取消时间（秒） */
    autoCancelTtl?: number;
    /** 0女 1男 2不限 */
    gender?: number;
    /** 预估商品价格 */
    estimatedPrice?: number;
    /** 订单总金额 */
    totalAmount?: number;
    /** 订单状态 */
    status?: number;
    /** 创建时间 */
    createTime?: string;
    /** 用户id */
    userId?: number;
    /** 跑腿id */
    runnerId?: number;
  };

  type OrderPayment = {
    /** 订单ID */
    orderId?: number;
    /** 附加金额 */
    additionalAmount?: number;
    /** 实付金额 */
    actualPayment?: number;
    /** 支付状态 0未支付 1已支付 2退款中 3已退款 */
    paymentStatus?: number;
    /** 付款时间 */
    paymentTime?: string;
    /** 退款中时间 */
    refundPendingTime?: string;
    /** 退款时间 */
    refundTime?: string;
    /** 是否使用优惠券 0 否 1 是 */
    isCouponed?: number;
    /** 优惠券ID */
    couponId?: number;
    /** 优惠金额 */
    discountAmount?: number;
  };

  type OrderProgress = {
    /** 订单id */
    orderId?: number;
    /** 接单时间 */
    acceptedTime?: string;
    /** 开始配送时间 */
    deliveringTime?: string;
    /** 送达时间 */
    deliveredTime?: string;
    /** 完成时间 */
    completedTime?: string;
    /** 由谁完成 */
    completedType?: number;
    /** 取消时间 */
    cancelTime?: string;
    /** 取消原因 */
    cancelReason?: string;
    /** 取消人类型 */
    cancelUserType?: number;
    /** 取消人ID */
    cancelUserId?: number;
  };

  type OrderQuery = {
    id?: number;
    orderUid?: number;
    takerUid?: number;
    /** 下单或接单 */
    orderOrTake?: number;
    /** 学校id */
    schoolId?: number;
    /** 服务类型 0 帮取送 1 代买 2 万能服务 */
    serviceType?: number;
    /** 标签 */
    tag?: string;
    /** 订单状态 */
    status?: number;
    /** 起始时间 */
    beginTime?: string;
    /** 结束时间 */
    endTime?: string;
  };

  type OrderSubmitDTO = {
    /** 学校id */
    schoolId: number;
    /** 服务类型 0 帮取送 1 代买 2 万能服务 */
    serviceType: number;
    /** 标签 */
    tag: string;
    /** 物品重量 */
    weight?: string;
    /** 详情 */
    detail: string;
    /** 是否指定时间 0 否 1 是 */
    isTimed: number;
    /** 指定时间 */
    specifiedTime?: string;
    /** 未结单取消时间（秒） */
    autoCancelTtl: number;
    /** 0女 1男 2不限 */
    gender: number;
    /** 追加金额 */
    additionalAmount: number;
    /** 预估商品价格 */
    estimatedPrice?: number;
    /** 附件图片的ossIds */
    attachImages?: number[];
    /** 附件文件的ossIds */
    attachFiles?: number[];
    startAddress?: AddressVO;
    endAddress: AddressVO;
  };

  type OssBo = {
    /** 请求参数 */
    params?: Record<string, any>;
    /** ossId */
    ossId?: number;
    /** 文件名 */
    fileName?: string;
    /** 原名 */
    originalName?: string;
    /** 文件后缀名 */
    fileSuffix?: string;
    /** URL地址 */
    url?: string;
    /** 服务商 */
    service?: string;
    /** 创建者 */
    createId?: string;
    /** 创建时间 */
    createTime?: string;
    /** 更新者 */
    updateId?: string;
    /** 更新时间 */
    updateTime?: string;
  };

  type OssConfigBo = {
    /** 请求参数 */
    params?: Record<string, any>;
    /** 主建 */
    ossConfigId: number;
    /** 配置key */
    configKey: string;
    /** accessKey */
    accessKey: string;
    /** 秘钥 */
    secretKey: string;
    /** 桶名称 */
    bucketName: string;
    /** 前缀 */
    prefix?: string;
    /** 访问站点 */
    endpoint: string;
    /** 自定义域名 */
    domain?: string;
    /** 是否https（Y=是,N=否） */
    isHttps?: string;
    /** 是否默认（0=是,1=否） */
    status?: string;
    /** 域 */
    region?: string;
    /** 扩展字段 */
    ext1?: string;
    /** 备注 */
    remark?: string;
    /** 桶权限类型(0private 1public 2custom) */
    accessPolicy: string;
    /** 创建者 */
    createId?: string;
    /** 创建时间 */
    createTime?: string;
    /** 更新者 */
    updateId?: string;
    /** 更新时间 */
    updateTime?: string;
  };

  type OssConfigVo = {
    /** 主建 */
    ossConfigId?: number;
    /** 配置key */
    configKey?: string;
    /** accessKey */
    accessKey?: string;
    /** 秘钥 */
    secretKey?: string;
    /** 桶名称 */
    bucketName?: string;
    /** 前缀 */
    prefix?: string;
    /** 访问站点 */
    endpoint?: string;
    /** 自定义域名 */
    domain?: string;
    /** 是否https（Y=是,N=否） */
    isHttps?: string;
    /** 域 */
    region?: string;
    /** 是否默认（0=是,1=否） */
    status?: number;
    /** 扩展字段 */
    ext1?: string;
    /** 备注 */
    remark?: string;
    /** 桶权限类型(0private 1public 2custom) */
    accessPolicy?: string;
  };

  type OssVo = {
    /** 对象存储主键 */
    ossId?: string;
    /** 文件类型 */
    type?: number;
    /** 文件大小 */
    fileSize?: number;
    /** 文件名 */
    fileName?: string;
    /** 原名 */
    originalName?: string;
    /** 文件后缀名 */
    fileSuffix?: string;
    /** URL地址 */
    url?: string;
    /** 创建时间 */
    createTime?: string;
    /** 上传人 */
    createId?: string;
    updateTime?: string;
    updateId?: number;
    /** 服务商 */
    service?: string;
  };

  type pageChatParams = {
    /** 订单ID */
    orderId: number;
    /** 包含分页详细信息的分页查询对象 */
    pageQuery: PageQuery;
  };

  type PageQuery = {
    /** 分页大小 */
    pageSize?: number;
    /** 当前页数 */
    pageNum?: number;
    /** 排序列 */
    orderByColumn?: string;
    /** 排序的方向desc或者asc */
    isAsc?: string;
  };

  type payAgainParams = {
    /** 订单ID */
    orderId: number;
  };

  type PayedVO = {
    orderId?: number;
    appId?: string;
    timeStamp?: string;
    nonceStr?: string;
    packageValue?: string;
    signType?: string;
    paySign?: string;
  };

  type Perm = {
    id?: number;
    /** 权限名称 */
    name?: string;
    /** 父级id */
    parentId?: number;
    /** 排序字段 */
    sort?: number;
    /** 权限标识 */
    perms?: string;
    children?: Perm[];
  };

  type phoneParams = {
    /** 订单ID */
    orderId: number;
  };

  type ProfileUpdateDTO = {
    avatar?: string;
    nickname?: string;
    emailEnable?: number;
  };

  type R = {
    code?: number;
    msg?: string;
    data?: Record<string, any>;
  };

  type RAddress = {
    code?: number;
    msg?: string;
    data?: Address;
  };

  type RBoolean = {
    code?: number;
    msg?: string;
    data?: boolean;
  };

  type refundParams = {
    /** 订单ID */
    orderId: number;
    /** 退款金额 */
    amount: number;
  };

  type RegionVO = {
    id?: number;
    schoolId?: number;
    /** 0 区域 1 楼栋 */
    type?: number;
    /** 名称 */
    name?: string;
    /** 备注 */
    remark?: string;
    /** 创建时间 */
    createTime?: string;
    /** 创建人 */
    createId?: number;
    /** 修改时间 */
    updateTime?: string;
    /** 修改人 */
    updateId?: number;
    childrens?: SchoolRegion[];
  };

  type remove1Params = {
    /** 用户ID列表，逗号分隔 */
    uIds: string;
  };

  type remove2Params = {
    /** OSS对象ID串 */
    ossIds: number[];
  };

  type remove3Params = {
    /** OSS配置ID串 */
    ossConfigIds: number[];
  };

  type remove5Params = {
    /** 要删除的校区区域ID数组 */
    schoolRegionIds: number[];
  };

  type remove6Params = {
    /** 要删除的地址ID数组 */
    addressIds: number[];
  };

  type removeByIdParams = {
    /** 要删除的地址ID */
    addressId: number;
  };

  type removeParams = {
    /** 队列名 */
    queueName: string;
    /** 订单号 */
    orderNum: string;
  };

  type resetPwdParams = {
    uId: number;
  };

  type RListAddress = {
    code?: number;
    msg?: string;
    data?: Address[];
  };
  type RListString = {
    code?: number;
    msg?: string;
    data?: String[];
  };

  type RListOrderAppealVO = {
    code?: number;
    msg?: string;
    data?: OrderAppealVO[];
  };

  type RListPerm = {
    code?: number;
    msg?: string;
    data?: Perm[];
  };

  type RListRegionVO = {
    code?: number;
    msg?: string;
    data?: RegionVO[];
  };

  type RListRunnerApply = {
    code?: number;
    msg?: string;
    data?: RunnerApply[];
  };

  type RListSchoolRegion = {
    code?: number;
    msg?: string;
    data?: SchoolRegion[];
  };

  type RListTags = {
    code?: number;
    msg?: string;
    data?: Tags[];
  };

  type RMapStringObject = {
    code?: number;
    msg?: string;
    data?: Record<string, any>;
  };

  type RMapStringString = {
    code?: number;
    msg?: string;
    data?: Record<string, any>;
  };

  type RMoneyRecode = {
    code?: number;
    msg?: string;
    data?: MoneyRecode;
  };

  type RObject = {
    code?: number;
    msg?: string;
    data?: Record<string, any>;
  };

  type rolepermsParams = {
    userType: number;
  };

  type ROrderDetailVO = {
    code?: number;
    msg?: string;
    data?: OrderDetailVO;
  };

  type ROssConfigVo = {
    code?: number;
    msg?: string;
    data?: OssConfigVo;
  };

  type RPayedVO = {
    code?: number;
    msg?: string;
    data?: PayedVO;
  };

  type RSchool = {
    code?: number;
    msg?: string;
    data?: School;
  };

  type RString = {
    code?: number;
    msg?: string;
    data?: string;
  };

  type RunnerApply = {
    /** 请求参数 */
    params?: Record<string, any>;
    id?: number;
    uid?: number;
    /** 学校id */
    schoolId?: number;
    /** 学校名称 */
    schoolName?: string;
    /** 姓名 */
    realname?: string;
    /** 性别 0 女 1 男 */
    gender?: number;
    /** 学生证 */
    studentCardUrl?: string;
    /** 创建时间 */
    createTime?: string;
    /** 申请状态 0驳回 1 通过 2申请中 */
    status?: number;
    /** 备注 */
    remarks?: string;
    /** 更新时间 */
    updateTime?: string;
    /** 更新人 */
    updateId?: number;
  };

  type RVoid = {
    code?: number;
    msg?: string;
    data?: Record<string, any>;
  };

  type RWallet = {
    code?: number;
    msg?: string;
    data?: Wallet;
  };

  type School = {
    /** 请求参数 */
    params?: Record<string, any>;
    id?: number;
    /** 属于谁管理 */
    belongUid?: number;
    /** 城市编码表 */
    adcode?: string;
    /** 学校名称 */
    name?: string;
    /** 学校logo */
    logo?: string;
    createTime?: string;
    updateTime?: string;
    /** 状态 0 禁用 1 启用 */
    status?: number;
    /** 平台收益占比 */
    profitPlat?: number;
    /** 代理收益占比 */
    profitAgent?: number;
    /** 跑腿收益占比 */
    profitRunner?: number;
    /** 底价 */
    floorPrice?: number;
  };

  type SchoolRegion = {
    /** 请求参数 */
    params?: Record<string, any>;
    id?: number;
    schoolId?: number;
    /** 0 区域 1 楼栋 */
    type?: number;
    /** 名称 */
    name?: string;
    /** 经度 */
    lon?: string;
    /** 纬度 */
    lat?: string;
    /** 区域id */
    parentId?: number;
    /** 备注 */
    remark?: string;
    /** 创建时间 */
    createTime?: string;
    /** 创建人 */
    createId?: number;
    /** 修改时间 */
    updateTime?: string;
    /** 修改人 */
    updateId?: number;
  };

  type sendEmailCodeParams = {
    email: string;
  };

  type statisticParams = {
    statisticsDaily: StatisticsDaily;
    pageQuery: PageQuery;
  };

  type StatisticsDaily = {
    /** 主键 */
    id?: number;
    /** 总订单量 */
    totalOrders?: number;
    /** 取消订单量 */
    canceledOrders?: number;
    /** 申诉订单量 */
    appealedOrders?: number;
    /** 完成订单量 */
    completedOrders?: number;
    /** 完单率(%) */
    completionRate?: number;
    /** 帮取送订单量 */
    deliveryOrders?: number;
    /** 代买订单量 */
    purchaseOrders?: number;
    /** 万能服务订单量 */
    universalOrders?: number;
    /** 帮取送订单占比(%) */
    deliveryRate?: number;
    /** 代买订单占比(%) */
    purchaseRate?: number;
    /** 万能订单占比(%) */
    universalRate?: number;
    /** 总收款金额 */
    totalPayment?: number;
    /** 总退款金额 */
    totalRefund?: number;
    /** 平台总收益 */
    platformProfit?: number;
    /** 代理总收益 */
    agentProfit?: number;
    /** 跑腿总收益 */
    runnerProfit?: number;
    /** 总访问量 */
    totalVisits?: number;
    /** 独立访问用户数 */
    uniqueVisitors?: number;
    /** 恶意请求数量 */
    maliciousRequests?: number;
    /** 新增用户数 */
    newUsers?: number;
    /** 活跃用户数 */
    activeUsers?: number;
    /** 新增跑腿用户数 */
    newRunners?: number;
    /** 活跃跑腿用户数 */
    activeRunners?: number;
    /** 创建时间 */
    createTime?: string;
  };

  type subscribeParams = {
    /** 队列名 */
    queueName: string;
  };

  type TableDataInfoAddress = {
    /** 总记录数 */
    total?: number;
    /** 列表数据 */
    rows?: Address[];
    /** 消息状态码 */
    code?: number;
    /** 消息内容 */
    msg?: string;
  };

  type TableDataInfoCapitalFlow = {
    /** 总记录数 */
    total?: number;
    /** 列表数据 */
    rows?: CapitalFlow[];
    /** 消息状态码 */
    code?: number;
    /** 消息内容 */
    msg?: string;
  };

  type TableDataInfoMoneyRecode = {
    /** 总记录数 */
    total?: number;
    /** 列表数据 */
    rows?: MoneyRecode[];
    /** 消息状态码 */
    code?: number;
    /** 消息内容 */
    msg?: string;
  };

  type TableDataInfoOrderAppeal = {
    /** 总记录数 */
    total?: number;
    /** 列表数据 */
    rows?: OrderAppeal[];
    /** 消息状态码 */
    code?: number;
    /** 消息内容 */
    msg?: string;
  };

  type TableDataInfoOrderChat = {
    /** 总记录数 */
    total?: number;
    /** 列表数据 */
    rows?: OrderChat[];
    /** 消息状态码 */
    code?: number;
    /** 消息内容 */
    msg?: string;
  };

  type TableDataInfoOrderMain = {
    /** 总记录数 */
    total?: number;
    /** 列表数据 */
    rows?: OrderMain[];
    /** 消息状态码 */
    code?: number;
    /** 消息内容 */
    msg?: string;
  };

  type TableDataInfoOssConfigVo = {
    /** 总记录数 */
    total?: number;
    /** 列表数据 */
    rows?: OssConfigVo[];
    /** 消息状态码 */
    code?: number;
    /** 消息内容 */
    msg?: string;
  };

  type TableDataInfoOssVo = {
    /** 总记录数 */
    total?: number;
    /** 列表数据 */
    rows?: OssVo[];
    /** 消息状态码 */
    code?: number;
    /** 消息内容 */
    msg?: string;
  };

  type TableDataInfoRunnerApply = {
    /** 总记录数 */
    total?: number;
    /** 列表数据 */
    rows?: RunnerApply[];
    /** 消息状态码 */
    code?: number;
    /** 消息内容 */
    msg?: string;
  };

  type TableDataInfoSchool = {
    /** 总记录数 */
    total?: number;
    /** 列表数据 */
    rows?: School[];
    /** 消息状态码 */
    code?: number;
    /** 消息内容 */
    msg?: string;
  };

  type TableDataInfoStatisticsDaily = {
    /** 总记录数 */
    total?: number;
    /** 列表数据 */
    rows?: StatisticsDaily[];
    /** 消息状态码 */
    code?: number;
    /** 消息内容 */
    msg?: string;
  };

  type TableDataInfoTags = {
    /** 总记录数 */
    total?: number;
    /** 列表数据 */
    rows?: Tags[];
    /** 消息状态码 */
    code?: number;
    /** 消息内容 */
    msg?: string;
  };

  type TableDataInfoUser = {
    /** 总记录数 */
    total?: number;
    /** 列表数据 */
    rows?: User[];
    /** 消息状态码 */
    code?: number;
    /** 消息内容 */
    msg?: string;
  };

  type TableDataInfoWallet = {
    /** 总记录数 */
    total?: number;
    /** 列表数据 */
    rows?: Wallet[];
    /** 消息状态码 */
    code?: number;
    /** 消息内容 */
    msg?: string;
  };

  type Tags = {
    /** 请求参数 */
    params?: Record<string, any>;
    id?: number;
    schoolId?: number;
    /** tag */
    name?: string;
    /** 备注 */
    remark?: string;
    /** 服务类型 0 帮取送 1 代买 2 万能服务 */
    serviceType?: number;
    createTime?: string;
    createId?: number;
    updateTime?: string;
    updateId?: number;
  };

  type updatePwdParams = {
    oldPassword: string;
    newPassword: string;
  };

  type uploadParams = {
    type: number;
    name: string;
  };

  type User = {
    /** 全局uid */
    uid?: number;
    userPc?: UserPc;
    userWx?: UserWx;
    /** 0 pc 1 小程序 */
    deviceType?: number;
    /** 创建时间 */
    createTime?: string;
    /** 上次登录时间 */
    loginTime?: string;
    /** 登录ip */
    loginIp?: string;
    /** 登录地址 */
    loginRegion?: string;
    /** 用户类型 0 超级管理员 1 校区管理员 2 普通管理员 3 普通用户 4 跑腿用户 */
    userType?: number;
    /** 创建人 */
    createId?: number;
    /** 更新时间 */
    updateTime?: string;
    /** 更新人 */
    updateId?: number;
    admin?: boolean;
  };

  type UserPc = {
    id?: number;
    uid?: number;
    userType?: number;
    /** 用户名 */
    username: string;
    password?: UserPc;
    /** 手机号 */
    phone?: string;
    /** 真实姓名 */
    name?: string;
    /** 学生证 */
    studentCardUrl?: string;
    /** 身份证 */
    idCardUrl?: string;
    /** 0 女 1 男 */
    sex?: number;
    /** 0 禁用 1 启用 */
    status?: number;
    /** 头像 */
    avatar?: string;
    /** 0 禁用 1 启用 */
    emailEnable?: number;
    /** 头像 */
    email?: string;
    /** 请求参数 */
    params?: Record<string, any>;
  };

  type UserPcQuery = {
    /** 请求参数 */
    params?: Record<string, any>;
    /** 全局uid */
    uid?: number;
    /** 0 女 1 男 */
    sex?: number;
    /** 0 禁用 1 启用 */
    status?: number;
    /** 手机号 */
    phone?: string;
    /** 用户名 */
    username?: string;
    /** 真实姓名 */
    name?: string;
    /** 登录地址 */
    loginRegion?: string;
    /** 用户类型 0 超级管理员 1 校区管理员 2 普通管理员 3 普通用户 4 跑腿用户 */
    userType?: number;
    /** 创建人 */
    createId?: number;
  };

  type UserWx = {
    id?: number;
    uid?: number;
    /** 小程序唯一id */
    openid?: string;
    /** 头像 */
    avatar?: string;
    /** 昵称 */
    nickname?: string;
    /** 手机 */
    phone?: string;
    /** 积分 */
    points?: number;
    /** 是否跑腿 0 否 1 是 */
    isRunner?: number;
    /** 是否可以下单 0 否 1 是 */
    canOrder?: number;
    /** 是否可以接单 0 否 1 是 */
    canTake?: number;
    /** 跑腿绑定学校id */
    schoolId?: number;
    /** 跑腿绑定学校名字 */
    schoolName?: string;
    /** 跑腿真实姓名 */
    realname?: string;
    /** 跑腿性别 */
    gender?: number;
    /** 信用分 */
    creditScore?: number;
  };

  type UserWxQuery = {
    /** 请求参数 */
    params?: Record<string, any>;
    /** 全局uid */
    uid?: number;
    openid?: string;
    /** 昵称 */
    nickname?: string;
    /** 手机 */
    phone?: string;
    /** 是否跑腿 0 否 1 是 */
    isRunner?: number;
    /** 是否可以下单 0 否 1 是 */
    canOrder?: number;
    /** 是否可以接单 0 否 1 是 */
    canTake?: number;
    /** 跑腿绑定学校id */
    schoolId?: number;
    /** 跑腿真实姓名 */
    realname?: string;
    /** 跑腿性别 */
    gender?: number;
    /** 登录地址 */
    loginRegion?: string;
    /** 用户类型 0 超级管理员 1 校区管理员 2 普通管理员 3 普通用户 4 跑腿用户 */
    userType?: number;
    /** 创建人 */
    createId?: number;
  };

  type Wallet = {
    /** uid */
    uid?: number;
    /** 当前余额 */
    withdrawn?: number;
    /** 已提现 */
    balance?: number;
    /** 创建时间 */
    createTime?: string;
    /** 更新时间 */
    updateTime?: string;
  };

  type walletPageParams = {
    wallet: Wallet;
    pageQuery: PageQuery;
  };

  type weatherParams = {
    adcode: string;
  };

  type withdrawPageParams = {
    moneyRecode: MoneyRecode;
    pageQuery: PageQuery;
  };

  type EmailLoginBody = {
    /** 邮箱 */
    email: string;
    /** 验证码 */
    code: string;
  };
}
