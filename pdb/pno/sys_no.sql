/*
Navicat MySQL Data Transfer

Source Server         : 192.168.0.105
Source Server Version : 50717
Source Host           : 192.168.0.105:3306
Source Database       : test0504

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-12-26 16:36:07
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_no
-- ----------------------------
DROP TABLE IF EXISTS `sys_no`;
CREATE TABLE `sys_no` (
  `id` varchar(64) NOT NULL COMMENT 'uuid',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `clazz` varchar(255) DEFAULT NULL COMMENT '编号处理类',
  `keyss` varchar(255) NOT NULL COMMENT '编号惟一标识',
  `max_byte` char(1) DEFAULT NULL COMMENT '最大值占用列数：(默认：7）',
  `sysmax_val` int(11) NOT NULL COMMENT '全局编号最大值',
  `format` varchar(255) DEFAULT NULL,
  `prefix` varchar(64) DEFAULT NULL COMMENT '前缀',
  `postfix` varchar(64) DEFAULT NULL COMMENT '后缀',
  `update_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '最后更新人',
  `create_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `remarks` varchar(255) DEFAULT NULL COMMENT '编号功能说明',
  `del_flag` char(1) DEFAULT NULL COMMENT 'del_flag',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统全局编号表';

-- ----------------------------
-- Records of sys_no
-- ----------------------------
INSERT INTO `sys_no` VALUES ('1', '机构唯一标识', '', 'OFFICE_NO', '5', '1', 'yyyyMMddHHmmss${max}', 'SNOE', '', '2017-07-24 18:48:09', '1', '2017-07-24 18:48:09', '1', '机构唯一标识', '0');
INSERT INTO `sys_no` VALUES ('10', '流程唯一标识', '', 'FWNO', '5', '157', 'yyMM${max}', 'SNFW', '', '2017-08-07 14:47:34', '1', '2017-12-13 14:39:08', '1', '流程唯一标识', '0');
INSERT INTO `sys_no` VALUES ('20', '项目唯一标识', '', 'PRONO', '5', '108', 'yyMM${max}', 'SNPT', '', '2017-08-07 14:47:20', '1', '2017-12-14 16:21:25', '1', '项目唯一标识', '0');
INSERT INTO `sys_no` VALUES ('30', '站点唯一标识', '', 'SITE_NO', '5', '1', 'yyyyMMddHHmmss${max}', 'SNST', '', '2017-07-24 18:48:10', '1', '2017-07-24 18:48:10', '1', '站点唯一标识', '0');
INSERT INTO `sys_no` VALUES ('40', '订单唯一标识', '', 'ORDER_NO', '5', '1', 'yyyyMMddHHmmss${max}', 'SNOR', '', '2017-07-24 18:48:11', '1', '2017-07-24 18:48:11', '1', '订单唯一标识', '0');
INSERT INTO `sys_no` VALUES ('50', '入驻申报唯一标识', '', 'PW_ENTER_NO', '1', '262', 'yyyyMMddHHmmss${max}', 'SNPER', '', '2017-11-29 15:41:40', '1', '2017-12-26 10:02:06', '1', '入驻申报唯一标识', '0');
INSERT INTO `sys_no` VALUES ('60', '流程申报唯一标识', '', 'YW_APPLY_NO', '1', '328', 'yyyyMMddHHmmss${max}', 'SNAY', '', '2017-12-08 11:16:00', '1', '2017-12-22 21:01:34', '1', '流程申报唯一标识', '0');
