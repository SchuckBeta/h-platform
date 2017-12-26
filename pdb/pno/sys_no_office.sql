/*
Navicat MySQL Data Transfer

Source Server         : 192.168.0.105
Source Server Version : 50717
Source Host           : 192.168.0.105:3306
Source Database       : test0504

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-12-26 16:36:14
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_no_office
-- ----------------------------
DROP TABLE IF EXISTS `sys_no_office`;
CREATE TABLE `sys_no_office` (
  `id` varchar(64) NOT NULL COMMENT 'uuid',
  `no_id` varchar(64) NOT NULL COMMENT '虚脱编号ID',
  `max_val` int(11) NOT NULL COMMENT '机构编号最大值',
  `office_id` varchar(64) NOT NULL COMMENT '机构ID',
  `update_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'update_date',
  `update_by` varchar(64) DEFAULT NULL COMMENT 'update_by',
  `create_date` timestamp NULL DEFAULT NULL COMMENT 'create_date',
  `create_by` varchar(64) DEFAULT NULL COMMENT 'create_by',
  `remarks` varchar(255) DEFAULT NULL COMMENT 'create_by',
  `del_flag` char(1) DEFAULT NULL COMMENT 'del_flag',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统机构编号表';

-- ----------------------------
-- Records of sys_no_office
-- ----------------------------
