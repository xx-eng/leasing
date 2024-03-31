package cn.edu.zuel.common.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.Plugins;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import cn.edu.zuel.common.module._MappingKit;

public class ModuleConfig {
    private WallFilter wallFilter;
    /**
     * 设置数据库插件
     *
     * @param plugins 全局插件
     */
    public void setDbPlugin(Plugins plugins) {
        // 读取数据库配置文件
        Prop prop = PropKit.use("db_config.properties");
        // 使用druid数据库连接池进行操作
        DruidPlugin druidPlugin = new DruidPlugin(prop.get("database_url").trim(),
                prop.get("database_user").trim(), prop.get("database_password").trim());
        // 加强数据库安全
        wallFilter = new WallFilter();
        wallFilter.setDbType("mysql");
        druidPlugin.addFilter(wallFilter);
        // 添加 StatFilter 才会有统计数据
        druidPlugin.addFilter(new StatFilter());

        druidPlugin.setMaxActive(20);
        druidPlugin.setMinIdle(5);
        druidPlugin.setInitialSize(5);
        druidPlugin.setConnectionInitSql("set names utf8mb4");
        druidPlugin.setValidationQuery("select 1 from dual");
        plugins.add(druidPlugin);
        // 配置数据库活动记录插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        Engine engine = arp.getEngine();
        // 上面的代码获取到了用于 sql 管理功能的 Engine 对象，接着就可以开始配置了
        engine.setToClassPathSourceFactory();
        engine.addSharedMethod(StrKit.class);
        arp.setShowSql(PropKit.getBoolean("dev", true));
        //设置sql文件存储的基础路径，此段代码表示设置为classpath目录
        arp.setBaseSqlTemplatePath(null);
        //所有模块sql
        arp.addSqlTemplate("sql/all.sql");
        // 对应关系映射（需等待运行Generator语句生成后替换）
        _MappingKit.mapping(arp);
        plugins.add(arp);
    }

    public WallFilter getWallFilter() {
        return wallFilter;
    }

    public void setWallFilter(WallFilter wallFilter) {
        this.wallFilter = wallFilter;
    }
}
