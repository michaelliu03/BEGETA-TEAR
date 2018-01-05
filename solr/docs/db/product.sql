CREATE TABLE `product` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT,
  `pid` varchar(32) NOT NULL  COMMENT '商品编号',
  `name` varchar(32) NOT NULL  COMMENT '商品名称',
  `catalog` varchar(32) NOT NULL DEFAULT '' COMMENT '商品分类名称',
  `price` varchar(32) NOT NULL DEFAULT ''  COMMENT '商品价格',
  `description` varchar(255) NOT NULL DEFAULT '' COMMENT '商品描述',
  `picture` varchar(32) NOT NULL DEFAULT ''  COMMENT '图片',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP  ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(3) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;