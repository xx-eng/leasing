package cn.edu.zuel.admin.article;

import cn.edu.zuel.common.module.Article;
import cn.fabrice.jfinal.service.BaseService;

/**
 * @author aila
 */
public class ArticleService extends BaseService<Article> {
    public ArticleService(){
        super("article.", Article.class, "article");
    }
}
