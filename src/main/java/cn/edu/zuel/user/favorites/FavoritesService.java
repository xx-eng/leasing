package cn.edu.zuel.user.favorites;

import cn.edu.zuel.common.module.Favorites;
import cn.fabrice.jfinal.service.BaseService;

/**
 * @author aila
 */
public class FavoritesService extends BaseService<Favorites> {
    public FavoritesService(){
        super("favorites.", Favorites.class, "favorites");
    }
}
