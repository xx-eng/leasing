package cn.edu.zuel.common.session;


import cn.edu.zuel.common.module.LoginInfo;
import cn.fabrice.jfinal.service.BaseService;
import cn.fabrice.kit.http.HttpKit;
import cn.fabrice.kit.http.vo.RequestInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * @author aila
 */
public class LoginInfoService extends BaseService<LoginInfo> {
    public LoginInfoService() {
        super("loginInfo.", LoginInfo.class, "login_info");
    }

    /**
     * 获取登录信息
     *
     * @param request http请求信息
     * @param account 登录账号
     * @return 返回登录信息
     */
    public LoginInfo getLoginInfo(HttpServletRequest request, String account) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setAccount(account);
        RequestInfo requestInfo = HttpKit.getRequestInfo(request);
        loginInfo.setOs(requestInfo.getOs());
        loginInfo.setBrowser(requestInfo.getBrowser());
        loginInfo.setIpAddress(requestInfo.getIp());
        loginInfo.setLoginLocation(requestInfo.getLocation());
        System.out.println("LOGIN_INFO:"+loginInfo);
        return loginInfo;
    }
}