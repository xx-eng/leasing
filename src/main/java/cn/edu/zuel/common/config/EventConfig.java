package cn.edu.zuel.common.config;

import com.jfinal.config.Plugins;
import net.dreamlu.event.EventPlugin;

/**
 * @author aila
 */
public class EventConfig {
    /**
     * 设置事件驱动插件
     *
     * @param plugins     当前插件
     * @param packageName 扫描的包名
     */
    public void setEventPlugin(Plugins plugins, String packageName) {
        // 添加事件驱动插件
        EventPlugin plugin = new EventPlugin();
        // 设置为异步，默认同步，或者使用`threadPool(ExecutorService executorService)`自定义线程池。
        plugin.async();
        // 设置扫描jar包，默认不扫描
        plugin.scanJar();
        // 设置监听器默认包，多个包名使用;分割，默认全扫描
        plugin.scanPackage(packageName);
        plugins.add(plugin);
    }
}
