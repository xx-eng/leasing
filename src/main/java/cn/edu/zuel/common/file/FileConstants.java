package cn.edu.zuel.common.file;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fye
 * @email fh941005@163.com
 * @date 2019-09-04 16:34
 * @description 文件相关常理类
 */
public class FileConstants {
    /**
     * 文件路径MAP类
     */
    public static final Map<Integer, String> FILE_PATH_MAP = new HashMap<Integer, String>() {
        {
            put(1, "/file");
            put(2, "/image");
            put(3, "/audio");
            put(4, "/video");
            put(5, "/other");
        }
    };

    /**
     * 文件来源
     */
    public interface Source {

    }

    /**
     * 文件类型
     */
    public enum Type {
        /**
         * 所有的文件类型
         */
        ALL,
        /**
         * 文本文件
         */
        FILE,
        /**
         * 图片文件
         */
        IMAGE,
        /**
         * 音频文件
         */
        AUDIO,
        /**
         * 视频文件
         */
        VIDEO
    }

    public static final String FILE_TYPE_NAME = "fileTypeName";

    /**
     * 相关提示信息常量类
     */
    public static class Message {
        public static final String ERROR_FILE_NOT_EXIST = "文件不存在";
        public static final String CREATE_FILE_FAIL = "创建文件失败";
    }
}
