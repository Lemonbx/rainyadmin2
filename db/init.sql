/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 90500 (9.5.0)
 Source Host           : localhost:3306
 Source Schema         : rainyadmin

 Target Server Type    : MySQL
 Target Server Version : 90500 (9.5.0)
 File Encoding         : 65001

 Date: 23/12/2025 16:26:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `create_by` bigint DEFAULT NULL,
                            `create_time` datetime(6) NOT NULL,
                            `update_by` bigint DEFAULT NULL,
                            `update_time` datetime(6) NOT NULL,
                            `component` varchar(255) DEFAULT NULL,
                            `logo` varchar(255) DEFAULT NULL,
                            `name` varchar(255) DEFAULT NULL,
                            `parent_id` bigint DEFAULT NULL,
                            `path` varchar(255) DEFAULT NULL,
                            `perms` varchar(255) DEFAULT NULL,
                            `sort` int DEFAULT NULL,
                            `type` int DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FK2jrf4gb0gjqi8882gxytpxnhe` (`parent_id`),
                            CONSTRAINT `FK2jrf4gb0gjqi8882gxytpxnhe` FOREIGN KEY (`parent_id`) REFERENCES `sys_menu` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `component`, `logo`, `name`, `parent_id`, `path`, `perms`, `sort`, `type`) VALUES (1, NULL, '2025-12-23 15:00:09.000000', NULL, '2025-12-23 15:00:11.000000', NULL, NULL, '系统管理', NULL, NULL, NULL, 1, 1);
INSERT INTO `sys_menu` (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `component`, `logo`, `name`, `parent_id`, `path`, `perms`, `sort`, `type`) VALUES (2, NULL, '2025-12-23 15:01:09.000000', NULL, '2025-12-23 15:01:12.000000', 'sys/User', NULL, '用户管理', 1, '/sysuser', 'sys:user:query', 1, 2);
INSERT INTO `sys_menu` (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `component`, `logo`, `name`, `parent_id`, `path`, `perms`, `sort`, `type`) VALUES (3, NULL, '2025-12-23 15:54:23.000000', NULL, '2025-12-23 15:54:25.000000', NULL, NULL, '用户保存', 2, NULL, 'sys:user:save', 1, 3);
INSERT INTO `sys_menu` (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `component`, `logo`, `name`, `parent_id`, `path`, `perms`, `sort`, `type`) VALUES (4, NULL, '2025-12-23 15:54:30.000000', NULL, '2025-12-23 15:54:32.000000', NULL, NULL, '用户删除', 2, NULL, 'sys:user:delete', 2, 3);
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `create_by` bigint DEFAULT NULL,
                            `create_time` datetime(6) NOT NULL,
                            `update_by` bigint DEFAULT NULL,
                            `update_time` datetime(6) NOT NULL,
                            `name` varchar(255) DEFAULT NULL,
                            `rolekey` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `name`, `rolekey`) VALUES (1, 1, '2025-12-18 19:32:34.000000', NULL, '2025-12-18 19:32:36.000000', '管理员', 'a');
COMMIT;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
                                 `role_id` bigint NOT NULL,
                                 `menu_id` bigint NOT NULL,
                                 KEY `FKf3mud4qoc7ayew8nml4plkevo` (`menu_id`),
                                 KEY `FKrlvro3cc7mtq6cirsbv2m4e71` (`role_id`),
                                 CONSTRAINT `FKf3mud4qoc7ayew8nml4plkevo` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`),
                                 CONSTRAINT `FKrlvro3cc7mtq6cirsbv2m4e71` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 1);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 3);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 2);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 4);
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `create_by` bigint DEFAULT NULL,
                            `create_time` datetime(6) NOT NULL,
                            `update_by` bigint DEFAULT NULL,
                            `update_time` datetime(6) NOT NULL,
                            `nickname` varchar(255) DEFAULT NULL,
                            `password` varchar(255) DEFAULT NULL,
                            `username` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `nickname`, `password`, `username`) VALUES (1, NULL, '2025-12-18 19:30:27.575718', NULL, '2025-12-18 19:30:27.575718', 'admin', '$2a$10$W.RcXqjs6XAwQcfxgTirLuwgk3yimimBRtYpcMh/p156slEI5xT.u', 'admin');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
                                 `user_id` bigint NOT NULL,
                                 `role_id` bigint NOT NULL,
                                 KEY `FKhh52n8vd4ny9ff4x9fb8v65qx` (`role_id`),
                                 KEY `FKsbjvgfdwwy5rfbiag1bwh9x2b` (`user_id`),
                                 CONSTRAINT `FKhh52n8vd4ny9ff4x9fb8v65qx` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`),
                                 CONSTRAINT `FKsbjvgfdwwy5rfbiag1bwh9x2b` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);
COMMIT;

-- ----------------------------
-- Add new menus for Role and Menu management
-- ----------------------------
BEGIN;
-- Role Management (Menu) - Parent 1 (System Management)
INSERT INTO `sys_menu` (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `component`, `logo`, `name`, `parent_id`, `path`, `perms`, `sort`, `type`) VALUES (5, NULL, '2025-12-23 17:00:00.000000', NULL, '2025-12-23 17:00:00.000000', 'sys/Role', NULL, '角色管理', 1, '/sysrole', 'sys:role:query', 2, 2);
-- Role Save (Button) - Parent 5
INSERT INTO `sys_menu` (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `component`, `logo`, `name`, `parent_id`, `path`, `perms`, `sort`, `type`) VALUES (6, NULL, '2025-12-23 17:00:00.000000', NULL, '2025-12-23 17:00:00.000000', NULL, NULL, '角色保存', 5, NULL, 'sys:role:save', 1, 3);
-- Role Delete (Button) - Parent 5
INSERT INTO `sys_menu` (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `component`, `logo`, `name`, `parent_id`, `path`, `perms`, `sort`, `type`) VALUES (7, NULL, '2025-12-23 17:00:00.000000', NULL, '2025-12-23 17:00:00.000000', NULL, NULL, '角色删除', 5, NULL, 'sys:role:delete', 2, 3);

-- Menu Management (Menu) - Parent 1 (System Management)
INSERT INTO `sys_menu` (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `component`, `logo`, `name`, `parent_id`, `path`, `perms`, `sort`, `type`) VALUES (8, NULL, '2025-12-23 17:00:00.000000', NULL, '2025-12-23 17:00:00.000000', 'sys/Menu', NULL, '菜单管理', 1, '/sysmenu', 'sys:menu:query', 3, 2);
-- Menu Save (Button) - Parent 8
INSERT INTO `sys_menu` (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `component`, `logo`, `name`, `parent_id`, `path`, `perms`, `sort`, `type`) VALUES (9, NULL, '2025-12-23 17:00:00.000000', NULL, '2025-12-23 17:00:00.000000', NULL, NULL, '菜单保存', 8, NULL, 'sys:menu:save', 1, 3);
-- Menu Delete (Button) - Parent 8
INSERT INTO `sys_menu` (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `component`, `logo`, `name`, `parent_id`, `path`, `perms`, `sort`, `type`) VALUES (10, NULL, '2025-12-23 17:00:00.000000', NULL, '2025-12-23 17:00:00.000000', NULL, NULL, '菜单删除', 8, NULL, 'sys:menu:delete', 2, 3);

-- Add permissions to Administrator (Role 1)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 5);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 6);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 7);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 8);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 9);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 10);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
