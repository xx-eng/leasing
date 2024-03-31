package cn.edu.zuel.firm.goods;

import cn.edu.zuel.common.module.Category;
import cn.edu.zuel.common.module.Goods;
import cn.fabrice.jfinal.service.BaseService;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aila
 * 商品
 */
public class GoodsService extends BaseService<Goods> {
    public GoodsService() {
        super("goods.", Goods.class, "goods");
    }

    public List<Goods> getSelectedGoods(List<Category> categoryList){
        List<Goods> goodsList= new ArrayList<>();
        for(Category category: categoryList){
            List<Goods> tempGoods = Goods.dao.find("select * from goods where category_id = ? and is_deleted = 0 and stu_goods = 0",category.getId());
            goodsList.addAll(tempGoods);
        }
        return goodsList;
    }
}
