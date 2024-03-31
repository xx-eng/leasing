package cn.edu.zuel.common.constant;
import cn.fabrice.common.constant.BaseResultConstants;
import cn.fabrice.common.pojo.BaseResult;

import java.util.Collections;
import java.util.List;

/**
 * @author fye
 * @email fh941005@163.com
 * @date 2019-10-06 14:23
 * @description 用户相关常量类
 */
public class UserConstants {

    /**
     * 登录的用户信息
     */
    public static final String LOGIN_USER = "LoginUser";

    public static final String USER_CACHE_NAME = "loginUser";

    public static final String PERMISSIONS_NAME_KEY = "permissions";
    public static final String PERMISSIONS_ALL_VALUE = "*:*:*";
    public static final List<String> PERMISSIONS_ADMIN_VALUE = Collections.singletonList(PERMISSIONS_ALL_VALUE);

    /**
     * 结果常量类
     */
    public static final class Result {
        /**
         * 账号不存在
         */
        public static final int ACCOUNT_NOT_EXIST = 50001;
        /**
         * 密码错误
         */
        public static final int ERROR_PASSWORD = 50002;
        /**
         * 账号被禁用
         */
        public static final int ACCOUNT_IS_FORBIDDEN = 50003;
        /**
         * 违法的token信息
         */
        public static final int ILLEGAL_TOKEN = 50004;
        /**
         * 过期的token信息
         */
        public static final int EXPIRED_TOKEN = 50005;
        /**
         * 账号已被登录，清空已登录账号失败
         */
        public static final int ACCOUNT_IS_LOGON = 50006;
        /**
         * 用户被删除
         */
        public static final int USER_IS_DELETED = 50007;
        /**
         * 数据库查找成功
         */
        public static final int DB_FIND_SUCCESS = 2000;
        /**
         * 数据库查找失败
         */
        public static final int DB_FIND_FAILURE = 2001;
        /**
         * 数据库删除成功
         */
        public static final int DB_DELETE_SUCCESS = 2002;
        /**
         * 数据库删除失败
         */
        public static final int DB_DELETE_FAILURE = 2003;
        /**
         * 数据库修改成功
         */
        public static final int DB_UPDATE_SUCCESS = 2004;
        /**
         * 数据库修改失败
         */
        public static final int DB_UPDATE_FAILURE = 2005;
        /**
         * 数据库添加成功
         */
        public static final int DB_ADD_SUCCESS = 2006;
        /**
         * 数据库添加失败
         */
        public static final int DB_ADD_FAILURE = 2007;
        /**
         * 余额不足
         */
        public static final int DB_MONEY = 2009;





        /**
         * 赋值父类，填充返回消息值
         */
        public static void init() {
            BaseResultConstants.addResultInfo(ILLEGAL_TOKEN, Message.ILLEGAL_TOKEN);
            BaseResultConstants.addResultInfo(EXPIRED_TOKEN, Message.EXPIRED_TOKEN);
            BaseResultConstants.addResultInfo(ACCOUNT_NOT_EXIST, Message.ACCOUNT_NOT_EXIST);
            BaseResultConstants.addResultInfo(ERROR_PASSWORD, Message.ERROR_PASSWORD);
            BaseResultConstants.addResultInfo(ACCOUNT_IS_FORBIDDEN, Message.ACCOUNT_IS_FORBIDDEN);
            BaseResultConstants.addResultInfo(ACCOUNT_IS_LOGON, Message.ACCOUNT_IS_LOGON);
            BaseResultConstants.addResultInfo(USER_IS_DELETED, Message.USER_IS_DELETED);
            BaseResultConstants.addResultInfo(DB_FIND_SUCCESS, Message.DB_FIND_SUCCESS);
            BaseResultConstants.addResultInfo(DB_FIND_FAILURE, Message.DB_FIND_FAILURE);
            BaseResultConstants.addResultInfo(DB_ADD_SUCCESS, Message.DB_ADD_SUCCESS);
            BaseResultConstants.addResultInfo(DB_ADD_FAILURE, Message.DB_FIND_FAILURE);
            BaseResultConstants.addResultInfo(DB_DELETE_SUCCESS, Message.DB_DELETE_SUCCESS);
            BaseResultConstants.addResultInfo(DB_DELETE_FAILURE, Message.DB_DELETE_FAILURE);
            BaseResultConstants.addResultInfo(DB_UPDATE_SUCCESS, Message.DB_UPDATE_SUCCESS);
            BaseResultConstants.addResultInfo(DB_UPDATE_FAILURE, Message.DB_UPDATE_FAILURE);
            BaseResultConstants.addResultInfo(DB_MONEY, Message.DB_MONEY);
        }
    }

    /**
     * 消息常量类
     */
    public static final class Message {
        public static final String ILLEGAL_TOKEN = "违法的TOKEN信息";
        public static final String EXPIRED_TOKEN = "TOKEN信息已过期";
        public static final String ACCOUNT_NOT_EXIST = "账号不存在";
        public static final String ERROR_PASSWORD = "密码错误";
        public static final String ACCOUNT_IS_FORBIDDEN = "账号被禁用";
        public static final String ACCOUNT_SESSION_SAVED_FAIL = "账号session保存失败，请联系系统管理员";
        public static final String ACCOUNT_IS_LOGON = "账号已被登录，清空已登录账号失败";
        public static final String USER_IS_DELETED = "用户已被删除";
        public static final String MOBILE_PHONE_EXIST = "手机号码已存在";
        public static final String ACCOUNT_EXIST = "账号已存在";
        public static final String EMAIL_EXIST = "邮箱已存在";
        public static final String ID_CARD_EXIST = "身份证号已存在";
        public static final String DB_FIND_SUCCESS = "数据库查找成功";
        public static final String DB_FIND_FAILURE = "数据库查找失败，没有该条记录";
        public static final String DB_DELETE_SUCCESS = "数据库删除成功";
        public static final String DB_DELETE_FAILURE = "数据库删除失败";
        public static final String DB_UPDATE_SUCCESS = "数据库更新成功";
        public static final String DB_UPDATE_FAILURE = "数据库更新失败";
        public static final String DB_ADD_SUCCESS = "数据库增加成功";
        public static final String DB_ADD_FAILURE = "数据库增加失败";
        public static final String DB_MONEY = "余额不足";
    }
}
