package cn.edu.zuel.common.event;

import cn.edu.zuel.common.module.LoginInfo;
import net.dreamlu.event.core.ApplicationEvent;

/**
 * @author aila
 */
public class LoginLogEvent extends ApplicationEvent<LoginInfo> {
    public LoginLogEvent(LoginInfo source) {
        super(source);
    }
}