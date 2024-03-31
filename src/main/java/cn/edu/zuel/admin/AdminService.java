package cn.edu.zuel.admin;

import cn.edu.zuel.common.module.Admin;
import cn.edu.zuel.common.module.User;
import cn.edu.zuel.common.module.UserSession;
import cn.edu.zuel.common.session.SessionService;
import cn.edu.zuel.common.constant.UserConstants;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.common.pojo.DataResult;
import cn.fabrice.jfinal.service.BaseService;
import com.jfinal.aop.Inject;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * @author aila
 */
public class AdminService extends BaseService<Admin> {
    @Inject
    SessionService sessionService;

    public AdminService(){
        super("admin.", Admin.class, "admin");
    }

    /**
     * 登录结果
     *
     * @param keepLogin 是否保持登录状态
     * @param account   登录账号
     * @param password  登录密码
     * @return 登录结果集
     */
    public BaseResult login(boolean keepLogin, String account, String password) {

        Kv cond = Kv.by("account", account);
        Admin user = get(cond, "getByAdminLogin");
        if (user == null) {
            return BaseResult.res(UserConstants.Result.ACCOUNT_NOT_EXIST);
        }
        String salt = user.getSalt();
        String hashedPass = HashKit.sha256(salt + password);
        if (!user.getPassword().equals(hashedPass)) {
            return BaseResult.res(UserConstants.Result.ERROR_PASSWORD);
        }
        //只允许单个地方登录，此时判断该用户是否存在session信息
        UserSession userSession = sessionService.getByAccount(user.getId().longValue());
        if (userSession != null) {
            //判断信息是否过期
            if (userSession.isExpired()) {
                //信息过期，直接删除
                if (!sessionService.deleteByInnerSql(userSession.getId().longValue())) {
                    return BaseResult.res(UserConstants.Result.ACCOUNT_IS_LOGON);
                }
            } else {
                String token = userSession.getSessionId();
                //信息未过期，则处理
                if (sessionService.deleteByInnerSql(userSession.getId().longValue())) {
                    CacheKit.remove(BaseConstants.ACCOUNT_CACHE_NAME, token);
                } else {
                    return BaseResult.res(UserConstants.Result.ACCOUNT_IS_LOGON);
                }
            }
        }
        // 如果用户勾选保持登录，暂定过期时间为 3 年，否则为 120 分钟，单位为秒
        long liveSeconds = keepLogin ? 94608000 : 7200;
        userSession = sessionService.add(user.getId().longValue(), liveSeconds, 1);
        if (userSession == null) {
            return BaseResult.fail(UserConstants.Message.ACCOUNT_SESSION_SAVED_FAIL);
        }
        //登录成功，放入缓存
        CacheKit.put(BaseConstants.ACCOUNT_CACHE_NAME, userSession.getSessionId(), userSession);
        user.put(BaseConstants.TOKEN_NAME, userSession.getSessionId());
        user.put(BaseConstants.SESSION_ID, userSession.getId());
        return DataResult.data(user);
    }
}
