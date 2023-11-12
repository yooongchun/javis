DROP TABLE IF EXISTS `ums_admin`;
CREATE TABLE `ums_admin`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `username`    varchar(64)     NULL COMMENT '用户名',
    `password`    varchar(64)     NULL COMMENT 'hash密码',
    `nick_name`   varchar(200)    NULL COMMENT '昵称',
    `phone`       varchar(20)     NULL COMMENT '手机号',
    `email`       varchar(100)    NULL COMMENT '邮箱',
    `icon`        varchar(500)    NULL COMMENT '头像',
    `note`        varchar(500)    NULL COMMENT '备注信息',
    `status`      tinyint(1)      NULL DEFAULT 1 COMMENT '账号启用状态：0->禁用；1->启用',
    `create_time` datetime        NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `login_time`  datetime        NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后登录时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4 COMMENT = '用户表'
  ROW_FORMAT = DYNAMIC;

INSERT INTO `ums_admin`
VALUES (1, 'admin', '$2a$10$nLBA0P7yBSh9csajrFoUGedUJOhM0CjoUo3H1i6FpHamw5AY35S9O', 'Javis', 18217235290,
        '1729465178@qq.com', 'https://cravatar.cn/avatar/4dc14aeb1f51b357657b9a15da59dbd3?d=monsterid&s=200',
        '系统管理员', 1, NOW(), NOW());
INSERT INTO `ums_admin`
VALUES (2, 'test', '$2a$10$nLBA0P7yBSh9csajrFoUGedUJOhM0CjoUo3H1i6FpHamw5AY35S9O', 'Jerry', NULL, 'yooongchun@qq.com',
        'https://cravatar.cn/avatar/4dc14aeb1f51b357657b9a15da59dbd3?d=monsterid&s=200', '用户', 1, NOW(), NOW());

DROP TABLE IF EXISTS `ums_admin_login_log`;
CREATE TABLE `ums_admin_login_log`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `admin_id`    bigint          NULL COMMENT '用户ID',
    `create_time` datetime        NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `ip`          varchar(255)    NULL COMMENT '登陆IP',
    `address`     varchar(255)    NULL COMMENT '登陆地址',
    `user_agent`  varchar(255)    NULL COMMENT '浏览器信息',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4 COMMENT = '用户登录日志';

DROP TABLE IF EXISTS `ums_admin_role_relation`;
CREATE TABLE `ums_admin_role_relation`
(
    `id`       bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `admin_id` bigint          NULL COMMENT '用户id',
    `role_id`  bigint          NULL COMMENT '角色id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4 COMMENT = '用户角色关系表'
  ROW_FORMAT = DYNAMIC;

INSERT INTO `ums_admin_role_relation`
VALUES (1, 1, 1);
INSERT INTO `ums_admin_role_relation`
VALUES (2, 2, 2);

DROP TABLE IF EXISTS `ums_menu`;
CREATE TABLE `ums_menu`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `parent_id`   bigint          NULL COMMENT '父级菜单ID',
    `create_time` datetime        NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `title`       varchar(100)    NULL COMMENT '菜单名称',
    `level`       int             NULL COMMENT '菜单级数',
    `sort`        int             NULL COMMENT '菜单排序',
    `name`        varchar(100)    NULL COMMENT '前端名称',
    `icon`        varchar(200)    NULL COMMENT '前端图标',
    `hidden`      tinyint(1)      NULL COMMENT '前端隐藏',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4 COMMENT = '菜单表'
  ROW_FORMAT = DYNAMIC;

INSERT INTO `ums_menu`
VALUES (1, 0, NOW(), '权限管理', 0, 0, 'ums', 'ums', 0);
INSERT INTO `ums_menu`
VALUES (2, 1, NOW(), '用户列表', 1, 0, 'admin', 'ums-admin', 0);
INSERT INTO `ums_menu`
VALUES (3, 1, NOW(), '角色列表', 1, 0, 'role', 'ums-role', 0);
INSERT INTO `ums_menu`
VALUES (4, 1, NOW(), '菜单列表', 1, 0, 'menu', 'ums-menu', 0);
INSERT INTO `ums_menu`
VALUES (5, 1, NOW(), '资源列表', 1, 0, 'resource', 'ums-resource', 0);

DROP TABLE IF EXISTS `ums_resource`;
CREATE TABLE `ums_resource`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category_id` bigint          NULL COMMENT '资源分类ID',
    `create_time` datetime        NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `name`        varchar(255)    NULL COMMENT '资源名称',
    `url`         varchar(255)    NULL COMMENT '资源URL',
    `description` varchar(500)    NULL COMMENT '描述',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4 COMMENT = '资源表'
  ROW_FORMAT = DYNAMIC;

INSERT INTO `ums_resource`
VALUES (1, 1, NOW(), '后台用户管理', '/admin/**', '');
INSERT INTO `ums_resource`
VALUES (2, 1, NOW(), '获取登录用户信息', '/admin/info', '用户登录必配');
INSERT INTO `ums_resource`
VALUES (3, 1, NOW(), '用户登出', '/admin/logout', '用户登出必配');
INSERT INTO `ums_resource`
VALUES (4, 1, NOW(), '后台用户角色管理', '/role/**', '');
INSERT INTO `ums_resource`
VALUES (5, 1, NOW(), '后台菜单管理', '/menu/**', '');
INSERT INTO `ums_resource`
VALUES (6, 1, NOW(), '后台资源分类管理', '/resourceCategory/**', '');
INSERT INTO `ums_resource`
VALUES (7, 1, NOW(), '后台资源管理', '/resource/**', '');

DROP TABLE IF EXISTS `ums_resource_category`;
CREATE TABLE `ums_resource_category`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `create_time` datetime        NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `name`        varchar(255)    NULL COMMENT '分类名称',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4 COMMENT = '资源分类表'
  ROW_FORMAT = DYNAMIC;

INSERT INTO `ums_resource_category`
VALUES (1, NOW(), '权限模块');
INSERT INTO `ums_resource_category`
VALUES (2, NOW(), '商品模块');
INSERT INTO `ums_resource_category`
VALUES (3, NOW(), '订单模块');
INSERT INTO `ums_resource_category`
VALUES (4, NOW(), '内容模块');
INSERT INTO `ums_resource_category`
VALUES (5, NOW(), '其他模块');

DROP TABLE IF EXISTS `ums_role`;
CREATE TABLE `ums_role`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_name`   varchar(100)    NULL COMMENT '角色名称',
    `description` varchar(500)    NULL COMMENT '描述',
    `create_time` datetime        NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `status`      tinyint(1)      NULL COMMENT '启用状态：0->禁用；1->启用',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4 COMMENT = '角色表'
  ROW_FORMAT = DYNAMIC;

INSERT INTO `ums_role`
VALUES (1, 'admin', '超级管理员', NOW(), 1);
INSERT INTO `ums_role`
VALUES (2, '商品管理员', '只能查看及操作商品', NOW(), 1);
INSERT INTO `ums_role`
VALUES (3, '订单管理员', '只能查看及操作订单', NOW(), 1);

DROP TABLE IF EXISTS `ums_role_menu_relation`;
CREATE TABLE `ums_role_menu_relation`
(
    `id`      bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_id` bigint          NULL COMMENT '角色ID',
    `menu_id` bigint          NULL COMMENT '菜单ID',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4 COMMENT = '角色菜单关系表'
  ROW_FORMAT = DYNAMIC;

INSERT INTO `ums_role_menu_relation`
VALUES (1, 1, 1);
INSERT INTO `ums_role_menu_relation`
VALUES (2, 1, 2);
INSERT INTO `ums_role_menu_relation`
VALUES (3, 1, 3);
INSERT INTO `ums_role_menu_relation`
VALUES (4, 1, 4);
INSERT INTO `ums_role_menu_relation`
VALUES (5, 1, 5);
INSERT INTO `ums_role_menu_relation`
VALUES (6, 2, 4);
INSERT INTO `ums_role_menu_relation`
VALUES (7, 2, 5);

DROP TABLE IF EXISTS `ums_role_resource_relation`;
CREATE TABLE `ums_role_resource_relation`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_id`     bigint          NULL COMMENT '角色ID',
    `resource_id` bigint          NULL COMMENT '资源ID',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4 COMMENT = '角色资源关系表'
  ROW_FORMAT = DYNAMIC;

INSERT INTO `ums_role_resource_relation`
VALUES (1, 1, 1);
INSERT INTO `ums_role_resource_relation`
VALUES (2, 1, 2);
INSERT INTO `ums_role_resource_relation`
VALUES (3, 1, 3);
INSERT INTO `ums_role_resource_relation`
VALUES (4, 1, 4);
INSERT INTO `ums_role_resource_relation`
VALUES (5, 1, 5);
INSERT INTO `ums_role_resource_relation`
VALUES (6, 1, 6);
INSERT INTO `ums_role_resource_relation`
VALUES (7, 1, 7);

DROP TABLE IF EXISTS `oms_transaction_record`;
CREATE TABLE `oms_transaction_record`
(
    `id`                 bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`            bigint UNSIGNED NOT NULL COMMENT '用户ID',
    `transaction_id`     varchar(255)    NOT NULL COMMENT '交易ID',
    `transaction_amount` decimal(10, 2)  NOT NULL COMMENT '交易金额',
    `transaction_type`   varchar(255)    NOT NULL COMMENT '交易类型',
    `transaction_time`   datetime        NOT NULL COMMENT '交易时间',
    `transaction_status` varchar(255)    NOT NULL COMMENT '交易状态:SUCCEED-成功，FAILED-失败，CANCELED-取消',
    `remark`             varchar(255) DEFAULT NULL COMMENT '备注信息',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4 COMMENT = '用户交易记录表'
  ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `oms_account_balance`;
CREATE TABLE `oms_account_balance`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     bigint UNSIGNED NOT NULL UNIQUE COMMENT '用户ID',
    `balance`     decimal(10, 2) DEFAULT 0 COMMENT '账户余额',
    `create_time` datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4 COMMENT = '订单表'
  ROW_FORMAT = DYNAMIC;

INSERT INTO `oms_account_balance`
VALUES (1, 1, 1, NOW());
INSERT INTO `oms_account_balance`
VALUES (2, 2, 0, NOW());

DROP TABLE IF EXISTS `oms_alipay_order`;
CREATE TABLE `oms_alipay_order`
(
    `id`               bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`          bigint UNSIGNED NOT NULL COMMENT '用户ID',
    `order_id`         varchar(64)     NOT NULL COMMENT '订单ID',
    `qr_code`          varchar(255)   DEFAULT NULL COMMENT '二维码链接',
    `subject`          varchar(255)   DEFAULT NULL COMMENT '订单标题/商品标题/交易标题',
    `total_amount`     decimal(10, 2) DEFAULT NULL COMMENT '订单总金额',
    `trade_status`     varchar(255)   DEFAULT NULL COMMENT '交易状态',
    `trade_no`         varchar(255)   DEFAULT NULL COMMENT '支付宝交易号',
    `buyer_id`         varchar(255)   DEFAULT NULL COMMENT '买家支付宝账号',
    `gmt_payment`      datetime       DEFAULT NULL COMMENT '交易付款时间',
    `create_time`      datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
    `buyer_pay_amount` decimal(10, 2) DEFAULT NULL COMMENT '用户在交易中支付的金额',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4 COMMENT = '订单表'
  ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `pms_invoice_info`;
CREATE TABLE `pms_invoice_info`
(
    `id`          bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     bigint UNSIGNED     NOT NULL COMMENT '用户ID',
    `status`      tinyint(1)     DEFAULT NULL COMMENT '解析状态,0-->初始化，1-->处理中，2-->成功，-1-->失败',
    `checked`     tinyint(1)     DEFAULT 0 COMMENT '是否已人工校验,0-->否，1-->是',
    `reimbursed`  tinyint(1)     DEFAULT 0 COMMENT '是否已报销：0->未报销；1->已报销',
    `inv_code`    varchar(32)    DEFAULT NULL COMMENT '发票代码',
    `inv_num`     varchar(32)    DEFAULT NULL COMMENT '发票号码',
    `inv_chk`     varchar(32)    DEFAULT NULL COMMENT '校验码',
    `inv_date`    date           DEFAULT NULL COMMENT '开票日期',
    `inv_money`   decimal(10, 2) DEFAULT NULL COMMENT '开具金额',
    `inv_tax`     varchar(32)    DEFAULT NULL COMMENT '税额',
    `inv_total`   varchar(32)    DEFAULT NULL COMMENT '价税合计',
    `inv_detail`  text           DEFAULT NULL COMMENT '详细信息',
    `inv_type`    varchar(64)    DEFAULT NULL COMMENT '发票类型:增值税专用发票、增值税电子专用发票、增值税普通发票、增值税电子普通发票、增值税普通发票(卷票)、增值税电子普通发票(通行费)',
    `method`      varchar(32)    DEFAULT NULL COMMENT '解析方式',
    `file_hash`   varchar(128)   DEFAULT NULL COMMENT '文件hash',
    `file_path`   varchar(1000)  DEFAULT NULL COMMENT '文件路径',
    `file_name`   varchar(200)   DEFAULT NULL COMMENT '文件名',
    `file_type`   varchar(64)    DEFAULT NULL COMMENT '文件类型',
    `bucket_name` varchar(50)    DEFAULT NULL COMMENT 'minio bucket name',
    `object_name` varchar(1000)  DEFAULT NULL COMMENT 'minio object name',
    `create_time` datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
    `is_delete`   tinyint(1)     DEFAULT NULL COMMENT '是否删除:0-->否，1-->是',
    `remark`      varchar(255)   DEFAULT NULL COMMENT '备注信息',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4 COMMENT = '发票信息表'
  ROW_FORMAT = DYNAMIC;

INSERT INTO `pms_invoice_info`
VALUES (1, 1, 1, 0, 0, '033002300511', '14137752', '07289142160796407320', '2023-11-08', 792.08, 7.92, 800.00,
        '{"InvoiceNumDigit": "", "CommodityUnit": [], "PurchaserAddress": "", "SheetNum": "", "CommodityType": [], "TotalAmount": "792.08", "Checker": "洪泽宁", "PurchaserBank": "", "Agent": "否", "Password": "00595*/<8<1858957*4843/*7/8*/4927>25096041*2/8<81775+2->341+32912/6/62/207+/+/06<24+>876*-6/4>8>>6017*-919+32-2+", "InvoiceTypeOrg": "浙江增值税电子普通发票", "InvoiceCodeConfirm": "033002300511", "TotalTax": "7.92", "ServiceType": "餐饮", "CommodityTaxRate": [{"row": "1", "word": "1%"}], "CommodityTax": [{"row": "1", "word": "7.92"}], "SellerBank": "中国工商银行股份有限公司杭州厚仁路支行1202006209000054815", "Remarks": "", "SellerAddress": "浙江省杭州市西湖区三墩镇欣然街42号110室13566905340", "NoteDrawer": "郑艺平", "InvoiceTag": "其他", "InvoiceNumConfirm": "14137752", "OnlinePay": "", "Payee": "洪祥", "CommodityName": [{"row": "1", "word": "*餐饮服务*餐费"}], "CommodityVehicleType": [], "InvoiceCode": "033002300511", "AmountInWords": "捌佰圆整", "AmountInFiguers": "800.00", "City": "", "InvoiceType": "电子普通发票", "CommodityEndDate": [], "PurchaserName": "浙江 云途飞行器技术有限公司", "InvoiceDate": "2023年11月08日", "CommodityNum": [{"row": "1", "word": "1"}], "PurchaserRegisterNum": "91330523MA2JJ58F9E", "MachineCode": "497011746939", "CommodityPlateNum": [], "CheckCode": "07289142160796407320", "SellerRegisterNum": "91330106MA7GG4HY9U", "CommodityPrice": [{"row": "1", "word": "792.079208"}], "CommodityStartDate": [], "SellerName": "杭州云端餐饮有限责任公司", "CommodityAmount": [{"row": "1", "word": "792.08"}], "Province": "浙江 省", "InvoiceNum": "14137752"}',
        '增值税电子普通发票', 'ocr', '3c4b128e7d0245488f6a54f43af1acb2',
        '/home/yczha/upload/2023_11_25/3c4b128e7d0245488f6a54f43af1acb2/full/餐饮6.pdf', '餐饮6.pdf', 'application/pdf',
        NULL, NULL,
        '2023-11-25 06:44:19', 0, NULL);

DROP TABLE IF EXISTS `oms_product_price`;
CREATE TABLE `oms_product_price`
(
    `id`           bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `product_name` varchar(100)    NOT NULL COMMENT '产品名称',
    `price`        decimal(10, 2)  NOT NULL COMMENT '产品价格',
    `create_time`  datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4 COMMENT ='产品定价表'
  ROW_FORMAT = DYNAMIC;

INSERT INTO `oms_product_price`
VALUES (1, '发票识别', 0.01, NOW(), NOW());
INSERT INTO `oms_product_price`
VALUES (2, '发票查验', 0.05, NOW(), NOW());
