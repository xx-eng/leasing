package cn.edu.zuel.user.complain;

import cn.edu.zuel.common.module.Complain;
import cn.fabrice.jfinal.service.BaseService;

/**
 * @author aila
 */
public class ComplainService extends BaseService<Complain> {
    public ComplainService(){
        super("complain.", Complain.class, "complain");
    }
}
