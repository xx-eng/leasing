package cn.edu.zuel.common.interceptor;

import cn.edu.zuel.user.UserController;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.common.pojo.BaseResult;
import com.jfinal.aop.Aop;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;
import cn.edu.zuel.common.module.UserSession;
import cn.edu.zuel.common.session.SessionService;
import cn.edu.zuel.common.constant.UserConstants;

import java.lang.reflect.Method;

/**
 * @author fye
 * @email fh941005@163.com
 * @date 2019-06-28 14:44
 * @description 认证拦截器
 */

public class AuthInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation invocation) {
        System.out.println("进入AuthInterceptor");
        //用户中的某些方法要通过
        Method method = invocation.getMethod();
        System.out.println(method.getDeclaringClass().toString());
        if("class cn.edu.zuel.user.UserController".equals(method.getDeclaringClass().toString())){
            if(checkMethod(method.getName())){
                invocation.invoke();
                return;
            }
//            invocation.invoke();
//            return; //记得改回来！！！！！！！！！！！
        }
        if("class cn.edu.zuel.common.file.FileController".equals(method.getDeclaringClass().toString())){
            invocation.invoke();
            return;
        }

        if("add".equals(method.getName())){
            invocation.invoke();
            return;
        }

        Controller controller = invocation.getController();
        if (StrKit.notBlank(controller.getHeader(BaseConstants.TOKEN_NAME))) {
            String token = controller.getHeader(BaseConstants.TOKEN_NAME);
            SessionService sessionService = Aop.get(SessionService.class);
            //获取session实体
            try{
                UserSession userSession = CacheKit.get(BaseConstants.ACCOUNT_CACHE_NAME, token,
                        () -> sessionService.getByToken(token));
                if(userSession != null){
                    System.out.println(userSession);
                    System.out.println(userSession.isExpired());
                }


                if (userSession == null) {
                    controller.renderJson(BaseResult.res(UserConstants.Result.ILLEGAL_TOKEN));
                    return;
                }
                if (userSession.isExpired()) {
                    controller.renderJson(BaseResult.res(UserConstants.Result.EXPIRED_TOKEN));
                    return;
                }
                controller.setAttr(BaseConstants.ACCOUNT, userSession);
                controller.setAttr(BaseConstants.ACCOUNT_ID, userSession.getUserId().longValue());

                System.out.println(userSession.getUserId().longValue());

                invocation.invoke();
                return;
            }catch (Exception e){
                controller.renderJson(BaseResult.res(UserConstants.Result.ILLEGAL_TOKEN));
            }

        }
        controller.renderJson(BaseResult.res(UserConstants.Result.ILLEGAL_TOKEN));
    }

    public boolean checkMethod(String methodName){
        boolean flag = false;
        if("add".equals(methodName)){
            flag = true;
        }
        if("getAllAdvert".equals(methodName)){
            flag = true;
        }
        if("getAllAritcle".equals(methodName)){
            flag = true;
        }

        if("getPopularGoods".equals(methodName)){
            flag = true;
        }
        if("getNewGoods".equals(methodName)){
            flag = true;
        }
        if("getSelectedGoods".equals(methodName)){
            flag = true;
        }
        if("getSelectGoods".equals(methodName)){
            flag = true;
        }
        if("getAllComments".equals(methodName)){
            flag = true;
        }
        if("getAllCategory".equals(methodName)){
            flag = true;
        }
        if("getACategory".equals(methodName)){
            flag = true;
        }
        if("getGoodsInfo".equals(methodName)){
            flag = true;
        }


        return flag;
    }
}

