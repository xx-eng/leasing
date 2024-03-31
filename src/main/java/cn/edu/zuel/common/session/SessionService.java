package cn.edu.zuel.common.session;

import cn.edu.zuel.common.module.UserSession;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.jfinal.service.BaseService;
import cn.fabrice.kit.Kits;
import com.jfinal.kit.Kv;

import java.math.BigInteger;
import java.util.List;

/**
 * @author aila
 */
public class SessionService extends BaseService<UserSession> {
    public SessionService() {
        super("user_session.", UserSession.class, "user_session");
    }

    /**
     * 添加用户登陆账号session存储
     *
     * @param userId 登录用户ID
     * @param expiresIn session有效时间（s）
     * @param type 用户类型
     * @return 操作成功-session实体类/操作失败-null
     */
    public UserSession add(long userId, long expiresIn, int type) {
        UserSession userSession = new UserSession();
        userSession.setUserId(BigInteger.valueOf(userId));
        BigInteger bigExpiresIn = new BigInteger(Long.toString(expiresIn));
//        userSession.setExpiresIn(expiresIn);
        userSession.setExpiresIn(bigExpiresIn);
        userSession.setType(type);
        //记录过期时间戳（毫秒）
        BigInteger bigExpiresOut = new BigInteger(Long.toString(System.currentTimeMillis() + expiresIn * 1000));
//        userSession.setExpiresTime(System.currentTimeMillis() + expiresIn * 1000);
        userSession.setExpiresTime(bigExpiresOut);
        userSession.setSessionId(Kits.getUuid());
        return userSession.save() ? userSession : null;
    }

    /**
     * 通过token获取用户session信息
     *
     * @param token token信息
     * @return 存在-返回对应实体类/不存在-null
     */
    public UserSession getByToken(String token) {
        return get(Kv.by("sessionId", token), "getByToken");
    }

    /**
     * 通过账号ID获取用户session信息
     *
     * @param accountId 账号ID
     * @return 存在-返回对应实体类/不存在-null
     */
    public UserSession getByAccount(long accountId) {
        return get(Kv.by("accountId", accountId), "getByAccount");
    }

    /**
     * 获取某个账号登录的所有设备信息
     *
     * @param accountId 账号ID
     * @return 登录的设备列表
     */
    public List<UserSession> listByAccount(long accountId) {
        Kv cond = Kv.by("accountId", accountId).set("loginType", 0);
        return list(cond, "getByAccount");
    }

    /**
     * 根据账号删除UserSession
     *
     * @param accountId 账号ID
     * @return 操作成功-true/操作失败-false
     */
    public boolean deleteByAccount(long accountId) {
        return update(Kv.by("accountId", accountId), "deleteByAccount") >= 1;
    }

    /**
     * 根据token删除登录信息
     *
     * @param token 对应数据表的session_id
     * @return 操作成功-true/操作失败-false
     */
    public boolean deleteByToken(String token) {
        return update(Kv.by("token", token), "deleteByToken") == 1;
    }

    /**
     * 获取账号有效的登录session信息
     *
     * @param accountId       账号ID
     * @param exceptLoginType 排除指定的登录方式
     * @return 对应的所有session列表
     */
    public List<UserSession> listEffectiveByAccount(long accountId, int exceptLoginType) {
        Kv cond = Kv.by("accountId", accountId).set("exceptLoginType", exceptLoginType);
        return list(cond, "listEffectiveByAccount");
    }
//   /**
//     * 添加用户登陆账号session存储
//     *
//     * @param userId 登录用户ID
//     * @param expiresIn session有效时间（s）
//     * @return 操作成功-session实体类/操作失败-null
//     */
//    public UserSession add(long userId, long expiresIn) {
//        UserSession userSession = new UserSession();
//        userSession.setUserId(BigInteger.valueOf(userId));
//        //userSession.setExpiresIn(expiresIn);
//        //记录过期时间戳（毫秒）
//        //userSession.setExpiresTime(System.currentTimeMillis() + expiresIn * 1000);
//        //userSession.setSessionId(Kits.getUuid());
//        return userSession.save() ? userSession : null;
//    }
//
//    /**
//     * 通过token获取用户session信息
//     *
//     * @param token token信息
//     * @return 存在-返回对应实体类/不存在-null
//     */
//    public UserSession getByToken(String token) {
//        return get(Kv.by("sessionId", token), "getByToken");
//    }
//
//    /**
//     * 通过账号ID获取用户session信息
//     *
//     * @param accountId 账号ID
//     * @return 存在-返回对应实体类/不存在-null
//     */
//    public UserSession getByAccount(long accountId) {
//        return get(Kv.by("accountId", accountId), "getByAccount");
//    }
//
//    /**
//     * 获取某个账号登录的所有设备信息
//     *
//     * @param accountId 账号ID
//     * @return 登录的设备列表
//     */
//    public List<UserSession> listByAccount(long accountId) {
//        Kv cond = Kv.by("accountId", accountId).set("loginType", 0);
//        return list(cond, "getByAccount");
//    }
//
//    /**
//     * 根据账号删除UserSession
//     *
//     * @param accountId 账号ID
//     * @return 操作成功-true/操作失败-false
//     */
//    public boolean deleteByAccount(long accountId) {
//        return update(Kv.by("accountId", accountId), "deleteByAccount") >= 1;
//    }
//
//    /**
//     * 根据token删除登录信息
//     *
//     * @param token 对应数据表的session_id
//     * @return 操作成功-true/操作失败-false
//     */
//    public boolean deleteByToken(String token) {
//        return update(Kv.by("token", token), "deleteByToken") == 1;
//    }
//
//    /**
//     * 获取账号有效的登录session信息
//     *
//     * @param accountId       账号ID
//     * @param exceptLoginType 排除指定的登录方式
//     * @return 对应的所有session列表
//     */
//    public List<UserSession> listEffectiveByAccount(long accountId, int exceptLoginType) {
//        Kv cond = Kv.by("accountId", accountId).set("exceptLoginType", exceptLoginType);
//        return list(cond, "listEffectiveByAccount");
//    }


}
