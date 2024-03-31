package cn.edu.zuel.common.order;

import cn.edu.zuel.common.module.OrderInfo;
import cn.fabrice.jfinal.service.BaseService;

/**
 * @author aila
 */
public class OrderService extends BaseService<OrderInfo> {
    public OrderService() {
        super("order.", OrderInfo.class, "order_info");
    }
}
