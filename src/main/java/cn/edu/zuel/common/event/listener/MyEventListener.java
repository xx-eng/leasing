package cn.edu.zuel.common.event.listener;

import cn.edu.zuel.common.event.LoginLogEvent;
import net.dreamlu.event.core.EventListener;
import org.apache.log4j.pattern.LogEvent;

/**
 * @author aila
 */
public class MyEventListener {

//    @EventListener(async = true)
//    public void recordLog(LogEvent event) {
//        event.getSource().save();
//    }

    /**
     * 记录登录日志
     *
     * @param event 日志事件
     */
    @EventListener(async = true)
    public void recordLoginInfo(LoginLogEvent event) {
        event.getSource().save();
    }
}

