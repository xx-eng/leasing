package cn.edu.zuel.common.chat;

import cn.edu.zuel.common.module.Chat;
import cn.fabrice.jfinal.service.BaseService;

/**
 * @author aila
 */
public class ChatService extends BaseService<Chat> {
    public ChatService() {
        super("chat.", Chat.class, "chat");
    }
}
