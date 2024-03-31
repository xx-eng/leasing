package cn.edu.zuel.common.file;

import cn.edu.zuel.common.module.File;
import cn.fabrice.jfinal.service.BaseService;
import cn.fabrice.kit.Kits;
import cn.fabrice.kit.file.FileKit;
import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.upload.UploadFile;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fye
 * @team occam
 * @email fh941005@163.com
 * @date 2019-04-07 14:01
 * @description 文件处理逻辑类
 */
public class FileService extends BaseService<File> {
//    @Inject
//    private TextContentService contentService;

    public FileService() {
        super("file.", File.class, "file");
    }

    /**
     * 添加文件实体类
     *
     * @param file        文件实体类
     * @param contentType 文件ContentType
     * @param type        文件类型
     * @param source      文件来源
     * @param userId      文件操作者
     * @return 操作成功-file module/操作失败-null
     */
    private File add(java.io.File file, String contentType, int type, int source, long userId) {
        String path = JFinal.me().getConstants().getBaseUploadPath() +
                FileConstants.FILE_PATH_MAP.getOrDefault(type, "/file");
        //防止路径不存在，新建
        FileKit.createDirectory(path);
        File savedFile = new File();
        boolean flag = Db.tx(() -> {
            savedFile.setUserId(BigInteger.valueOf(userId));
            savedFile.setSize(file.length());
            savedFile.setMimeType(contentType);
            //文件原始名
            savedFile.setOriginalName(file.getName());
            //获取后缀
            String ext = FileKit.getFileSuffix(file.getName());
            savedFile.setSuffix(ext);
            //文件来源
            savedFile.setSource(source);
            //类型
            savedFile.setType(type);
            String nameUuid = Kits.getUuid();
            String newName = nameUuid + "." + ext;
            savedFile.setNewName(newName);
            //文件存储URL
            String savedPath = path + "/" + newName;
            savedFile.setSavedPath(savedPath);
            return file.renameTo(new java.io.File(savedPath)) && savedFile.save();
        });
        return flag ? savedFile : null;
    }

    /**
     * 上传文件
     *
     * @param uploadFile 上传的文件
     * @param type       文件类型：1-文本文件 2-图片文件 3-音频文件 4-视频文件
     * @param source     文件来源：1-其他 2-标注文书
     * @param userId     文件上传人ID
     * @return 返回上传成功后的文件实体类
     */
    public File upload(UploadFile uploadFile, int type, int source, long userId) {
        return add(uploadFile.getFile(), uploadFile.getContentType(), type, source, userId);
    }

    /**
     * 添加下载文件
     *
     * @param downloadFile 下载的文件实体
     * @param contentType  文件Content-Type
     * @param type         文件所属类型
     * @param source       文件来源
     * @param userId       用户ID
     * @return 返回上传成功后的文件实体类
     */
    public File addLocalFile(java.io.File downloadFile, String contentType, int type, int source, long userId) {
        return add(downloadFile, contentType, type, source, userId);
    }

    /**
     * 通过UUID获取对应文件列表
     *
     * @param uuid 文件UUID
     * @return 返回对应文件列表
     */
    public List<File> listByUuid(String uuid) {
        Kv cond = Kv.by("uuid", uuid);
        return list(cond, "listByUuid");
    }

    /**
     * 设置文件的uuid
     *
     * @param uuid    uuid
     * @param fileIds 文件id对应的LIST
     * @return 操作成功-true/操作失败-false
     */
    public boolean setUuid(String uuid, List<Long> fileIds) {
        Kv cond = Kv.by("idList", fileIds).set("uuid", uuid);
        return update(cond, "setUuid") >= 1;
    }

    /**
     * 设置文件的uuid
     *
     * @param uuid      uuid
     * @param fileIdStr 文件id字符串，多个id之间以','分隔
     * @return 操作成功-true/操作失败-false
     */
    public boolean setUuid(String uuid, String fileIdStr) {
        List<Long> fileIds = Kits.parseObjStrToList(fileIdStr).stream().map(Long::parseLong).collect(Collectors.toList());
        return setUuid(uuid, fileIds);
    }

    /**
     * 删除文件
     *
     * @param fileIdStr 文件id字符串，多个id之间以','分隔
     * @return 操作成功-true/操作失败-false
     */
    public boolean delete(String fileIdStr) {
        List<String> fileIds = Kits.parseObjStrToList(fileIdStr);
        Kv cond = Kv.by("idList", fileIds);
        return update(cond, "delete") >= 1;
    }

    /**
     * 获取上传的文件新生成的文件名
     *
     * @param id 文件ID
     * @return 文件名称
     */
    public String getName(long id) {
        File file = getDao().findByIdLoadColumns(id, "new_name");
        return file != null ? file.getNewName() : "";
    }
}
