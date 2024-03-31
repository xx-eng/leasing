package cn.edu.zuel.common;

import cn.edu.zuel.common.config.EventConfig;
import cn.edu.zuel.common.config.ModuleConfig;
import cn.edu.zuel.common.constant.UserConstants;
import cn.edu.zuel.common.interceptor.AuthInterceptor;
import cn.edu.zuel.common.module._MappingKit;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.jfinal.ext.cros.interceptor.CrossInterceptor;
import cn.fabrice.jfinal.ext.render.MyRenderFactory;
import cn.fabrice.jfinal.interceptor.ParaValidateInterceptor;
import cn.fabrice.jfinal.plugin.ValidationPlugin;
import cn.fabrice.kit.json.FJsonFactory;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.aop.Aop;
import com.jfinal.config.*;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;

/**
 * Hello world!
 *
 * @author aila
 */
public class App extends JFinalConfig
{
    private ModuleConfig moduleConfig;
    @Override
    public void configConstant(Constants me) {
        PropKit.use("base_config.properties");
        //设置开发模式
        me.setDevMode(PropKit.getBoolean("devMode", true));
        //设置日志
        me.setToSlf4jLogFactory();
        //设置render工厂类
        me.setRenderFactory(new MyRenderFactory());
        //设置json
        me.setJsonFactory(new FJsonFactory());
        //允许AOP注入
        me.setInjectDependency(true);
        UserConstants.Result.init();
        //设置上传文档
        me.setBaseUploadPath("src/main/webapp/file");
        me.setMaxPostSize(10485760);

        //UserConstants.Result.init();
    }

    @Override
    public void configRoute(Routes me) {
        me.scan("cn.edu.zuel.");
    }

    @Override
    public void configEngine(Engine me) {

    }

    @Override
    public void configPlugin(Plugins me) {
      /*  // 读取数据库配置文件
        Prop prop = PropKit.use("db_config.properties");
        // 使用druid数据库连接池进行操作
        DruidPlugin druidPlugin = new DruidPlugin(prop.get("database_url").trim(),
                prop.get("database_user").trim(), prop.get("database_password").trim());
        // 加强数据库安全
        WallFilter wallFilter = new WallFilter();
        wallFilter.setDbType("mysql");
        druidPlugin.addFilter(wallFilter);
        // 添加 StatFilter 才会有统计数据
        druidPlugin.addFilter(new StatFilter());

        druidPlugin.setMaxActive(20);
        druidPlugin.setMinIdle(5);
        druidPlugin.setInitialSize(5);
        druidPlugin.setConnectionInitSql("set names utf8mb4");
        druidPlugin.setValidationQuery("select 1 from dual");
        me.add(druidPlugin);
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
        me.add(arp);*/

        //配置数据库插件
        moduleConfig = new ModuleConfig();
        moduleConfig.setDbPlugin(me);
        //添加规则校验插件
        me.add(new ValidationPlugin());
        //增加缓存插件
        me.add(new EhCachePlugin());
        //添加事件驱动插件
        new EventConfig().setEventPlugin(me, "cn.edu.zuel.common.event.listener");

    }

    @Override
    public void configInterceptor(Interceptors me) {
        //添加跨越解决方案
        me.add(new CrossInterceptor(BaseConstants.TOKEN_NAME, true));
        //全局认证拦截器类
        me.add(new AuthInterceptor());
        //参数校验拦截器
        me.add(new ParaValidateInterceptor());
        //日志记录拦截器
        //me.add(new LogInterceptor());
    }

    @Override
    public void configHandler(Handlers me) {

    }

    @Override
    public void onStart() {
        System.out.println("app starting...");
        // 让 druid 允许在 sql 中使用 union
        // https://github.com/alibaba/druid/wiki/%E9%85%8D%E7%BD%AE-wallfilter
        moduleConfig.getWallFilter().getConfig().setSelectUnionCheck(false);

    }

    @Override
    public void onStop() {
        System.out.println("app stopping...");
    }

    public static void main(String[] args) {
        UndertowServer.start(App.class);
    }
}
