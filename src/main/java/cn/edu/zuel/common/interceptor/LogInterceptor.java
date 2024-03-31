package cn.edu.zuel.common.interceptor;


import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.common.constant.BaseResultConstants;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.jfinal.ext.log.annotation.Log;
import cn.fabrice.kit.http.HttpKit;
import cn.fabrice.kit.http.vo.RequestInfo;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.render.JsonRender;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fye
 * @email fh941005@163.com
 * @date 2020-10-20 08:14
 * @description
 */
public class LogInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation invocation) {
        invocation.invoke();
        Log log = invocation.getMethod().getAnnotation(Log.class);
        //不存在日志注解，不需要记录日志
        if (log == null) {
            return;
        }
//        //存在日志注解，记录日志信息
//        LogRecord logRecord = new LogRecord();
//        logRecord.setUserId(BigInteger.valueOf(invocation.getController().getAttr(BaseConstants.ACCOUNT_ID)));
//        logRecord.setControllerName(invocation.getControllerPath());
//        logRecord.setActionName(invocation.getActionKey());
//        logRecord.setDescription(log.desc());
//        //设置请求浏览器信息
//        RequestInfo requestInfo = HttpKit.getRequestInfo(invocation.getController().getRequest());
//        logRecord.setOs(requestInfo.getOs());
//        logRecord.setBrowser(requestInfo.getBrowser());
//        logRecord.setIpAddress(requestInfo.getIp());
//        logRecord.setLocation(requestInfo.getLocation());
        //设置请求参数信息
        if (invocation.getController().getParaMap() != null && !invocation.getController().getParaMap().isEmpty()) {
            Map<String, String> realParaMap = new HashMap<>(invocation.getController().getParaMap().size());
            invocation.getController().getParaMap().forEach((k, v) -> realParaMap.put(k, v[0]));
//            logRecord.setParameterStr(JSONObject.toJSONString(realParaMap));
        }
        String res = ((JsonRender) invocation.getController().getRender()).getJsonText();
        BaseResult result = JSONObject.parseObject(res, BaseResult.class);
//        logRecord.setResult(res);
//        if (result.getCode() != BaseResultConstants.OK) {
//            logRecord.setStatus(BaseConstants.Status.ABNORMAL.ordinal());
//        }
//        logRecord.save();
    }
}
