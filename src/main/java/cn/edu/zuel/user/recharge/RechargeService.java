package cn.edu.zuel.user.recharge;

import cn.edu.zuel.common.module.Recharge;
import cn.fabrice.jfinal.service.BaseService;

/**
 * @author aila
 */
public class RechargeService extends BaseService<Recharge> {
    public RechargeService(){
        super("recharge.", Recharge.class, "recharge");
    }
}
