package cn.edu.zuel.common.order.trade;

import cn.edu.zuel.common.module.Trade;
import cn.fabrice.jfinal.service.BaseService;

/**
 * @author aila
 * 交易模块
 */
public class TradeService extends BaseService<Trade> {
    public TradeService() {
        super("trade.", Trade.class, "trade");
    }
}
