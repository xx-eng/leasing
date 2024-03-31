package cn.edu.zuel.common.interceptor;


import cn.edu.zuel.common.module.UserSession;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.jfinal.annotation.Permission;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import java.util.Arrays;
import java.util.List;

/**
 * @author fye
 * @email fh941005@163.com
 * @date 2020-01-09 11:41
 * @description
 */
public class PermissionInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        UserSession userSession = inv.getController().getAttr(BaseConstants.ACCOUNT, null);
        //如果用户未登录，直接放行
        if (userSession == null) {
            inv.invoke();
            return;
        }
        //判断是否需要进行权限检验
        Permission[] permissions = inv.getMethod().getAnnotationsByType(Permission.class);
        if (permissions.length == 0) {
            inv.invoke();
            return;
        }
        List<String> permissionList = userSession.get(BaseConstants.PERMISSIONS_NAME_KEY);
        boolean hasPermission = Arrays.stream(permissions)
                .anyMatch(permission -> hasPermission(permissionList, permission));
        if (hasPermission) {
            inv.invoke();
            return;
        }
        inv.getController().renderJson(BaseResult.error403());
    }

    /**
     * 判断是否有对应权限
     *
     * @param permissionList 当前用户拥有的权限列表
     * @param permission     注解的权限标志符
     * @return 有权限-true/无权限-false
     */
    private boolean hasPermission(List<String> permissionList, Permission permission) {
        //校验权限
        String permissionStr = permission.value();
        //解构需要的权限
        String[] needPermissionArray = permissionStr.split(",");
        return permissionList.contains(BaseConstants.PERMISSIONS_ALL_VALUE)
                || permissionList.parallelStream().allMatch(item -> Arrays.binarySearch(needPermissionArray, item) >= 0);
    }
}
