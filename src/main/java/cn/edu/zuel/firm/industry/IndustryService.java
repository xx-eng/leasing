package cn.edu.zuel.firm.industry;

import cn.edu.zuel.common.module.Industry;
import cn.fabrice.jfinal.service.BaseService;

/**
 * @author aila
 * 行业
 */
public class IndustryService extends BaseService<Industry> {
    public IndustryService() {
        super("industry1.", Industry.class, "industry");
    }
}
