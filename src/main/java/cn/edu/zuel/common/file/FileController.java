package cn.edu.zuel.common.file;//package cn.fabrice.appname.common.file;

//import cn.fabrice.appname.common.interceptor.AuthInterceptor;
import cn.edu.zuel.common.module.File;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.common.constant.BaseEncryptedKey;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.common.pojo.DataResult;
import cn.fabrice.jfinal.annotation.Param;
import cn.fabrice.jfinal.annotation.ValidateParam;
import cn.fabrice.jfinal.constant.ValidateRuleConstants;
import cn.fabrice.jfinal.ext.render.MyFileRender;
import cn.fabrice.kit.crypto.SecureKit;
import cn.fabrice.kit.exception.CryptoException;
import cn.fabrice.kit.file.FileKit;
import cn.fabrice.kit.jfinal.FileRenderKit;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.core.Path;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.upload.UploadFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fye
 * @team occam
 * @email fh941005@163.com
 * @date 2019-04-07 14:01
 * @description 文件处理控制类
 */
@Path("/file")
@ValidateParam
public class FileController extends Controller {
    @Inject
    private FileService service;

    /**
     * 处理文件上传结果
     *
     * @param file 封装好的文件实体类
     */
    private void processUpload(File file) {
        if (file == null) {
            renderJson(BaseResult.fail());
            return;
        }
        //根据文件上传来源返回数据值
        try {
            String fileId = SecureKit.encryptData(BaseEncryptedKey.FILE_ID, file.getId().toString());
            Map<String, String> resMap = new HashMap<>(2);
            resMap.put("id",file.getId().toString());
            resMap.put("fileId", fileId);
            resMap.put("fileName", file.getNewName());
            renderJson(DataResult.data(resMap));
        } catch (CryptoException e) {
            renderJson(BaseResult.fail(e.getLocalizedMessage()));
        }
    }

    /**
     * 文件上传
     * <p>
     * 没有用action传参形式接收参数，是因为文件上传时options请求不会添加Content-Type，如果存在形参，options请求会进行判断
     * 因为会提示Content-Type类型报错
     * 展示图片：127.0.0.1:8080/file/image/图片名称.jpg
     */
    public void upload() {
        UploadFile file = getFile();
        System.out.println("file:"+ file);
        long uid = getAttr(BaseConstants.ACCOUNT_ID, 0L);
//        int type = getInt("type");
//        int source = getInt("source");
        int type = 2;
        int source = 1;
        File uploadedFile = service.upload(file, type, source, uid);
        processUpload(uploadedFile);
    }


    /**
     * 系统内部调用上传文件接口
     * <p>
     * 内部方法转发
     */
    public void localUpload() {
        UploadFile uploadFile = getAttr("uploadFile");
        int type = getAttrForInt("type");
        int source = getAttrForInt("source");
        //File uploadedFile = service.upload(uploadFile, type, source, getAttr(BaseConstants.ACCOUNT_ID));
        // processUpload(uploadedFile);
    }

//    /**
//     * 渲染文件资源
//     *
//     * @param id 文件ID
//     */
//    private void renderFile(long id) {
//        File file = service.get(id);
//        if (file == null) {
//            renderError(404);
//            return;
//        }
//        java.io.File discFile = new java.io.File(file.getSavedPath());
//        if (discFile.exists()) {
//            //根据文件类型进行渲染
//            if (getAttr(FileConstants.FILE_TYPE_NAME) != null) {
//                render(new FileRenderKit(file.getSavedPath(), file.getMimeType()));
//                return;
//            }
//            renderFile(discFile);
//        } else {
//            renderError(404);
//        }
//    }

//    /**
//     * 获取图片资源
//     */
//    @Clear(AuthInterceptor.class)
//    @Param(index = 0, required = true, illegalCheck = true, encryptedKey = BaseEncryptedKey.FILE_ID)
//    public void image() {
//        setAttr(FileConstants.FILE_TYPE_NAME, FileConstants.Type.IMAGE);
//        long id = Long.parseLong(getAttr(get(0)));
//        renderFile(id);
//    }

//    /**
//     * 获取音频文件资源
//     */
//    @Clear(AuthInterceptor.class)
//    @Param(index = 0, required = true, illegalCheck = true, encryptedKey = BaseEncryptedKey.FILE_ID)
//    public void audio() {
//        setAttr(FileConstants.FILE_TYPE_NAME, FileConstants.Type.AUDIO);
//        long id = Long.parseLong(getAttr(get(0)));
//        renderFile(id);
//    }

//    /**
//     * 下载文件
//     */
//    @Clear(AuthInterceptor.class)
//    @Param(index = 0, required = true, illegalCheck = true, encryptedKey = BaseEncryptedKey.FILE_ID)
//    public void download() {
//        long id = Long.parseLong(getAttr(get(0)));
//        renderFile(id);
//    }

//    /**
//     * 根据UUID获取文件信息
//     */
//    @Param(name = "uuid", required = true)
//    public void listByUuid(String uuid) {
//        List<File> fileList = service.listByUuid(uuid);
//        if (fileList.size() == 0) {
//            renderError(404);
//            return;
//        }
//        renderJson(DataResult.data(fileList));
//    }

//    /**
//     * 根据文件ID获取文件内容
//     *
//     * @param id ID
//     */
//    @Param(name = "id", required = true, rule = ValidateRuleConstants.Key.ID)
//    public void getContent(long id) {
//        File file = service.get(Kv.by("id", id), "getContent");
//        if (file == null) {
//            renderJson(BaseResult.fail(FileConstants.Message.ERROR_FILE_NOT_EXIST));
//            return;
//        }
//        renderJson(DataResult.data(file.get("content", "")));
//    }

//    /**
//     * 通过文件名下载文件
//     *
//     * @param fileName 文件名称
//     */
//    @Clear(AuthInterceptor.class)
//    @Param(name = "fileName", required = true)
//    @Param(name = "delete", rule = ValidateRuleConstants.Key.BOOLEAN)
//    public void downloadByName(String fileName, boolean delete) {
//        String ext = FileKit.getFileSuffix(fileName);
//        java.io.File file = new java.io.File(JFinal.me().getConstants().getBaseDownloadPath() + "/" + fileName);
//        MyFileRender fileRender = new MyFileRender(file,
//                DateKit.toStr(new Date(), "yyyyMMddHHmmss") + "." + ext, delete);
//        render(fileRender);
//    }
}
