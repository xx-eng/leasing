package cn.edu.zuel.admin.advert;

import cn.edu.zuel.common.module.Advert;
import cn.fabrice.jfinal.service.BaseService;

/**
 * @author aila
 */
public class AdvertService extends BaseService<Advert> {
    public AdvertService(){
        super("advert.", Advert.class, "advert");
    }
}
