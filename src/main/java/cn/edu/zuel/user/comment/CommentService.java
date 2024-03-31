package cn.edu.zuel.user.comment;

import cn.edu.zuel.common.module.Comment;
import cn.fabrice.jfinal.service.BaseService;

/**
 * @author aila
 */
public class CommentService extends BaseService<Comment> {
    public CommentService(){
        super("comment.", Comment.class, "comment");
    }
}
