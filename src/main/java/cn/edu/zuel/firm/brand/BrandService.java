package cn.edu.zuel.firm.brand;

import cn.edu.zuel.common.module.Brand;
import cn.edu.zuel.common.module.Goods;
import cn.fabrice.jfinal.service.BaseService;
import com.jfinal.kit.Kv;

import java.util.List;

/**
 * @author aila
 * 厂商品牌
 */
public class BrandService extends BaseService<Brand> {
    public BrandService(){
        super("brand.", Brand.class, "brand");
    }

}
