package cn.edu.zuel.user.message;

import cn.edu.zuel.common.module.Message;
import cn.fabrice.jfinal.service.BaseService;

/**
 * @author aila
 */
public class MessageService extends BaseService<Message> {
    public MessageService(){
        super("message.", Message.class, "message");
    }
}
