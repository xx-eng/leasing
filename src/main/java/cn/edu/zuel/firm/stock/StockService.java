package cn.edu.zuel.firm.stock;

import cn.edu.zuel.common.module.Stock;
import cn.fabrice.jfinal.service.BaseService;

/**
 * @author aila
 * 库存
 */
public class StockService extends BaseService<Stock> {
    public StockService(){
        super("stock.", Stock.class, "stock");
    }
}
