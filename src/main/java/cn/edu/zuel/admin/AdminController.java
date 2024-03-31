package cn.edu.zuel.admin;

import cn.edu.zuel.admin.advert.AdvertService;
import cn.edu.zuel.admin.article.ArticleService;
import cn.edu.zuel.common.constant.UserConstants;
import cn.edu.zuel.common.event.LoginLogEvent;
import cn.edu.zuel.common.interceptor.AuthInterceptor;
import cn.edu.zuel.common.module.*;
import cn.edu.zuel.common.order.OrderService;
import cn.edu.zuel.common.order.trade.TradeService;
import cn.edu.zuel.common.session.LoginInfoService;
import cn.edu.zuel.common.session.SessionService;
import cn.edu.zuel.firm.FirmService;
import cn.edu.zuel.firm.brand.BrandService;
import cn.edu.zuel.firm.category.CategoryService;
import cn.edu.zuel.firm.goods.GoodsService;
import cn.edu.zuel.firm.goods.RequestParams;
import cn.edu.zuel.user.UserService;
import cn.edu.zuel.user.complain.ComplainService;
import cn.edu.zuel.user.recharge.RechargeService;
import cn.fabrice.common.constant.BaseConstants;
import cn.fabrice.common.constant.BaseResultConstants;
import cn.fabrice.common.pojo.BaseResult;
import cn.fabrice.common.pojo.DataResult;
import cn.fabrice.common.pojo.TableResult;
import cn.fabrice.jfinal.annotation.Param;
import cn.fabrice.jfinal.annotation.ValidateParam;
import cn.fabrice.jfinal.constant.ValidateRuleConstants;
import cn.fabrice.kit.Kits;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;
import net.dreamlu.event.EventKit;

/**
 * @author aila
 */
@Path("/admin")
@ValidateParam
public class AdminController extends Controller {
    @Inject
    AdminService adminService;
    @Inject
    SessionService sessionService;
    @Inject
    LoginInfoService loginInfoService;
    @Inject
    GoodsService goodsService;
    @Inject
    FirmService firmService;
    @Inject
    BrandService brandService;
    @Inject
    AdvertService advertService;
    @Inject
    TradeService tradeService;
    @Inject
    UserService userService;
    @Inject
    ArticleService articleService;
    @Inject
    ComplainService complainService;
    @Inject
    OrderService orderService;
    @Inject
    CategoryService categoryService;
    @Inject
    RechargeService rechargeService;

    /**
     * 用户登录
     *
     * @param keepLogin 是否保持登陆：true/false
     * @param account  账号
     * @param password 密码
     */
    //@Clear(AuthInterceptor.class)
    @Clear(AuthInterceptor.class)
    @Param(name = "keepLogin", rule = ValidateRuleConstants.Key.BOOLEAN)
    @Param(name = "account", required = true)
    @Param(name = "password", required = true)
    public void login(boolean keepLogin, String account, String password) {
        /*        Kv cond = Kv.by("account", account);
        User user = userService.get(cond, "getByUserLogin");
        if (user == null) {
            renderJson(BaseResult.fail("用户名不存在"));
            return;
        }
        String hashPwd = HashKit.sha256(user.getSalt() + password);
        if (!hashPwd.equals(user.getPassword())) {
            renderJson(BaseResult.fail("密码错误"));
            return;
        }
        String token = Kits.getUuid();
        if (sessionService.add(user.getId().longValue(), token)) {
            user.put("token", token);
            renderJson(DataResult.data(user));
            return;
        }
        renderJson(BaseResult.fail("插入user_session表失败"));*/
        LoginInfo loginInfo = loginInfoService.getLoginInfo(getRequest(), account);
        BaseResult result = adminService.login(keepLogin, account, password);
        if (result.isOk()) {
            loginInfo.setStatus(BaseConstants.Status.NORMAL.ordinal());
            loginInfo.setMsg("登录成功");
            Admin user = (Admin) ((DataResult) result).getData();
            loginInfo.setSessionId(user.getBigInteger(BaseConstants.SESSION_ID));
            user.remove(BaseConstants.SESSION_ID);
            EventKit.post(new LoginLogEvent(loginInfo));
        } else {
            loginInfo.setStatus(BaseConstants.Status.ABNORMAL.ordinal());
            loginInfo.setMsg(result.getMsg());
            EventKit.post(new LoginLogEvent(loginInfo));
        }
        renderJson(result);

    }

    /**
     * 根据token获取用户信息
     */
    public void getInfo() {
        long id = getAttr(BaseConstants.ACCOUNT_ID);
        System.out.println("id"+id);
        Kv cond = Kv.by("id",id);
        Admin user = adminService.get(cond,"getAdminInfo");
        user = adminService.encrypt(user);
        renderJson(DataResult.data(user));
    }

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     */
    @Param(name = "id", required = true, rule = ValidateRuleConstants.Key.ID)
    public void get(long id) {
        Kv cond = Kv.by("id",id);
        Admin user = adminService.get(cond,"getAdminInfo");
        renderJson(DataResult.data(user));
    }

    /**
     * 退出登录
     */
    public void logout() {
        UserSession session = getAttr(BaseConstants.ACCOUNT);
        String token = session.getSessionId();
        if (sessionService.deleteByToken(token)) {
            //同时删除缓存token
            CacheKit.remove(BaseConstants.ACCOUNT_CACHE_NAME, token);
            renderJson(BaseResult.ok());
            return;
        }
        renderJson(BaseResult.fail());
    }




    /**
     * 新增
     * <p>
     * 用户名、登录账号、手机号码
     *
     * @Param Admin 对应实体
     */
    @Param(name = "name",required = true)
    @Param(name = "account",required = true)
    @Param(name = "mobile",required = true)
    @Param(name = "password",required = true)
    public void addAdmin(@Para("") Admin admin){
        System.out.println(admin);
        String salt = HashKit.generateSaltForSha256();
        admin.setSalt(salt);
        System.out.println(HashKit.md5(admin.getPassword()));
        admin.setPassword(HashKit.sha256(salt+HashKit.md5(admin.getPassword())));
        if(admin.save()){
            BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS);
        }else{
            BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE);
        }
//        renderJson(admin.save() ? BaseResult.ok() : BaseResult.fail());
    }

    /**
     * 查看所有商品
     * <p>
     */
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getAllGoods(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<Goods> page = goodsService.list(params.getPageNumber(),params.getPageSize(),cond,"adminGoodsList");
        for(Goods goods:page.getList()){
            //加入公司姓名
            Kv cond1 = Kv.by("firmId",goods.getFirmId());
            Firm firm = firmService.get(cond1,"getFirmName");
            goods.put("firm_name",firm.getFirmName());
        }
        System.out.println(page.getList());
//        if(page.getList() != null){
//            TableResult
//        }
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }

    /**
     * 查看上架商品
     * <p>
     */
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getOnGoods(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<Goods> page = goodsService.list(params.getPageNumber(),params.getPageSize(),cond,"adminOnGoodsList");
        for(Goods goods:page.getList()){
            Kv cond1 = Kv.by("firmId",goods.getFirmId());
            Firm firm = firmService.get(cond1,"getFirmName");
            goods.put("firm_name",firm.getFirmName());
        }
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }

    /**
     * 查看未审核商品
     * <p>
     */
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getUncheckedGoods(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<Goods> page = goodsService.list(params.getPageNumber(),params.getPageSize(),cond,"adminUncheckedGoodsList");
        for(Goods goods:page.getList()){
            Kv cond1 = Kv.by("firmId",goods.getFirmId());
            Firm firm = firmService.get(cond1,"getFirmName");
            goods.put("firm_name",firm.getFirmName());
        }
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }


    /**
     * 审核商品
     * <p>
     */
    @Param(name = "id", required = true)
    @Param(name = "status", required = true)
    public void checkGoods(@Para("") Goods goods) {
        renderJson(goods.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }


    /**
     * 查看下架商品
     * <p>
     */
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getOffGoods(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<Goods> page = goodsService.list(params.getPageNumber(),params.getPageSize(),cond,"adminOffGoodsList");
        for(Goods goods:page.getList()){
            Kv cond1 = Kv.by("firmId",goods.getFirmId());
            Firm firm = firmService.get(cond1,"getFirmName");
            goods.put("firm_name",firm.getFirmName());
        }
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }


    /**
     * 查看商品详情
     * <p>
     * @Param id
     */
    @Param(name = "id", required = true)
    public void getGoodsInfo(long id){
        Kv cond = Kv.by("id",id);
        Goods goods = goodsService.get(cond,"getGoodsInfo");
        if(goods == null){
            DataResult.res(UserConstants.Result.DB_FIND_FAILURE, UserConstants.Message.DB_FIND_FAILURE);
        }
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, goods));
    }

    /**
     * 查看所有品牌
     * <p>
     */
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getOnBrand(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<Brand> page = brandService.list(params.getPageNumber(),params.getPageSize(),cond,"adminBrandList");
        for(Brand brand:page.getList()){
            Kv cond1 = Kv.by("firmId",brand.getFirmId());
            Kv cond2 = Kv.by("categoryId",brand.getCategoryId());
            Firm firm = firmService.get(cond1,"getFirmName");
            Category c = categoryService.get(cond2,"getCategoryName");
            brand.put("firm_name",firm.getFirmName());
            brand.put("category_name",c.getName());

        }
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }

    /**
     * 审核品牌
     * <p>
     */
    @Param(name = "id", required = true)
    @Param(name = "status", required = true)
    public void checkBrand(@Para("") Brand brand) {

        renderJson(brand.update() ?BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS) : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }

    /**
     * 查看所有广告
     * <p>
     */
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getAllAdvert(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<Advert> page = advertService.list(params.getPageNumber(),params.getPageSize(),cond,"adminAdvertList");
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }

    /**
     * 启用，禁用广告
     * <p>
     */
    @Param(name = "id", required = true)
    @Param(name = "status", required = true)
    public void checkAdvert(@Para("") Advert advert) {
        renderJson(advert.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS,UserConstants.Message.DB_UPDATE_SUCCESS) : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE,UserConstants.Message.DB_UPDATE_FAILURE));
    }

    /**
     * 新增
     * <p>
     * 广告
     *
     * @Param advert 对应实体
     */
    //@Param(name = "image",required = true)
    @Param(name = "url",required = true)
    @Param(name = "image", required = true)
    @Param(name = "status", required = true)
    public void addAdvert(@Para("") Advert advert){
        System.out.println(advert);
//        try {
//            System.out.println("11111");
//            UploadFile file = getFile();
//            String filep = file.getFileName();
//            System.out.println(filep);
//            advert.setImage("/file/"+filep);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        renderJson(advert.save() ? BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS) :
                BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
    }

    /**
     * 删除广告
     * <p>
     * @Param id
     */
    @Param(name = "id", required = true)
    public void deleteAdvert(long id) {
        renderJson(advertService.deleteByInnerSql(id) ? BaseResult.res(UserConstants.Result.DB_DELETE_SUCCESS, UserConstants.Message.DB_DELETE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_DELETE_FAILURE, UserConstants.Message.DB_DELETE_FAILURE));
    }


    /**
     * 查看所有交易
     * <p>
     */
    //@Param(name = "firmId",required = true)
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getAllTrade(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<Trade> page = tradeService.list(params.getPageNumber(),params.getPageSize(),cond,"adminTradeList");
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }

    /**
     * 查看用户（信用资质）
     * <p>
     */
    //@Param(name = "firmId",required = true)
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getAllUsers(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<User> page = userService.list(params.getPageNumber(),params.getPageSize(),cond,"adminUserList");
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }

    /**
     * 启用，禁用用户
     * <p>
     */
    @Param(name = "id", required = true)
    @Param(name = "status", required = true)
    public void checkUser(@Para("") User user) {

        renderJson(user.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }

    /**
     * 查看商家
     * <p>
     */
    //@Param(name = "firmId",required = true)
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getAllFirm(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<Firm> page = firmService.list(params.getPageNumber(),params.getPageSize(),cond,"getAllFirm");
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }


    /**
     * 查看商家详情
     * <p>
     * @Param id
     */
    @Param(name = "id", required = true)
    public void getFirmInfo(long id){
        Kv cond = Kv.by("id",id);
        Firm firm = firmService.get(cond,"getFirmName");
        if(firm == null){
            renderJson(BaseResult.res(UserConstants.Result.DB_FIND_FAILURE, UserConstants.Message.DB_FIND_FAILURE));
        }
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, firm));
        BaseResult.ok();
    }

    /**
     * 审核商家
     * <p>
     */
    @Param(name = "id", required = true)
    @Param(name = "status", required = true)
    public void checkFirm(@Para("") Firm firm) {
        renderJson(firm.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }

    /**
     * 查看文章
     * <p>
     */
    //@Param(name = "firmId",required = true)
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getAllAritcle(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<Article> page = articleService.list(params.getPageNumber(),params.getPageSize(),cond,"adminAritcletList");
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }


    /**
     * 查看文章
     * <p>
     */
    @Param(name = "title", required = true)
    @Param(name = "content", required = true)
    @Param(name = "type",required = true)
    @Param(name = "status", required = true)
    public void addArticle(@Para("") Article article){
        if(article.save()){
            BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS);
        }else{
            BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE);
        }
    }


    /**
     * 删除文章
     * <p>
     * @Param id
     */
    @Param(name = "id", required = true)
    public void deleteArticle(long id) {
        renderJson(articleService.deleteByInnerSql(id) ? BaseResult.res(UserConstants.Result.DB_DELETE_SUCCESS, UserConstants.Message.DB_DELETE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_DELETE_FAILURE, UserConstants.Message.DB_DELETE_FAILURE));
    }


    /**
     * 查看投诉
     * <p>
     */
    //@Param(name = "firmId",required = true)
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getAllComplain(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<Complain> page = complainService.list(params.getPageNumber(),params.getPageSize(),cond,"adminComplainList");
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }

    /**
     * 处理投诉
     * <p>
     * @Param id
     */
    @Param(name = "id", required = true)
    public void deleteComplain(long id) {
        renderJson(complainService.deleteByInnerSql(id) ? BaseResult.res(UserConstants.Result.DB_DELETE_SUCCESS, UserConstants.Message.DB_DELETE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_DELETE_FAILURE, UserConstants.Message.DB_DELETE_FAILURE));
    }

    /**
     * 处理投诉,更改状态
     * <p>
     * @Param id
     */
    @Param(name = "id", required = true)
    @Param(name = "status", required = true)
    public void changeComplain(@Para("") Complain complain){
        renderJson(complain.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }


    /**
     * 查看所有订单
     * <p>
     */
    @Param(name = "orderState")
    @Param(name = "startTime")
    @Param(name = "endTime")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getAllOrder(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"adminGetAllOrders");
        //Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"userOrderList");
        for(OrderInfo order: page.getList()){
            System.out.println(order);
            long goodsId = order.getGoodsId().longValue();
            Kv cond1 = Kv.by("id",goodsId);
            System.out.println(goodsId);
            Goods goods = goodsService.get(cond1,"getGoodsInfo");
            order.put("image",goods.getImage());
            order.put("goodsTitle",goods.getTitle());
        }
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }

    /**
     * 查看某一类型的订单
     * <p>
     */
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "type", required = true)
    public void getOrder(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"adminGetTypeOrder");
        //Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"userOrderList");
        for(OrderInfo order: page.getList()){
            System.out.println(order);
            long goodsId = order.getGoodsId().longValue();
            Kv cond1 = Kv.by("id",goodsId);
            System.out.println(goodsId);
            Goods goods = goodsService.get(cond1,"getGoodsInfo");
            order.put("image",goods.getImage());
            order.put("goodsTitle",goods.getTitle());
        }
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }

    /**
     * 查看某一状态的订单
     * <p>
     */
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "orderState", required = true)
    public void getOrderState(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"adminGetStateOrder");
        //Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"userOrderList");
        for(OrderInfo order: page.getList()){
            System.out.println(order);
            long goodsId = order.getGoodsId().longValue();
            Kv cond1 = Kv.by("id",goodsId);
            System.out.println(goodsId);
            Goods goods = goodsService.get(cond1,"getGoodsInfo");
            order.put("image",goods.getImage());
            order.put("goodsTitle",goods.getTitle());
        }
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }


    /**
     * 查看某一类型状态的订单
     * <p>
     */
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "orderState", required = true)
    public void getTSOrder(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"adminGetTSOrder");
        //Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"userOrderList");
        for(OrderInfo order: page.getList()){
            System.out.println(order);
            long goodsId = order.getGoodsId().longValue();
            Kv cond1 = Kv.by("id",goodsId);
            System.out.println(goodsId);
            Goods goods = goodsService.get(cond1,"getGoodsInfo");
            order.put("image",goods.getImage());
            order.put("goodsTitle",goods.getTitle());
        }
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }

    // 关于分类的

    /**
     * 获取属性树状结构
     *
     * @param params 请求参数实体
     */
    public void tree(@Para("") RequestParams params) {
        Kv cond = Kv.by("rq", params);
        renderJson(DataResult.data(categoryService.list(cond, "tree")));
    }

    /**
     * 判断同一分支节点是否存在重复名称
     *
     * @param id       ID
     * @param parentId 父节点ID
     * @param name     名称
     */
    @Param(name = "id", required = true)
    @Param(name = "parentId", required = true)
    @Param(name = "name", required = true)
    public void judge(long id, long parentId, String name) {
        renderJson(categoryService.isExistByInnerSql(parentId, id, "name", name) ?
                DataResult.data("fail") : DataResult.data("ok"));
    }

    /**
     * 新增或修改数据时，内部判断
     *
     * @param item 对应实体类
     * @return 判断结果
     */
    private BaseResult judge(Category item) {
        long id = item.getId() == null ? 0 : item.getId().longValue();
        long parentId = item.getParentId() == null ? 0 : item.getParentId().longValue();
        if (categoryService.isExistByInnerSql(parentId, id, "name", item.getName())) {
            //说明重复
            return BaseResult.fail("分类名称已存在");
        }
        return BaseResult.ok();
    }

    /**
     * 新增数据
     *
     * @param category 对应实体类
     */
    @Param(name = "parentId", required = true)
    @Param(name = "name", required = true)
    public void add(@Para("") Category category) {
        BaseResult result = judge(category);
        if (result.isFail()) {
            renderJson(result);
            return;
        }
        renderJson(categoryService.processAdd(category) ? BaseResult.ok() : BaseResult.fail());
    }

    /**
     * 更新数据
     *
     * @param category 对应实体类
     */
    @Param(name = "id", required = true, rule = ValidateRuleConstants.Key.ID)
    @Param(name = "parentId", required = true)
    @Param(name = "name", required = true)
    public void update(@Para("") Category category) {
        BaseResult result = judge(category);
        if (result.isFail()) {
            renderJson(result);
            return;
        }
        renderJson(categoryService.processUpdate(category) ? BaseResult.ok() : BaseResult.fail());
    }

    /**
     * 删除节点数据，包括所有子节点
     *
     * @param ids 节点数据集合
     */
    @Param(name = "ids", required = true)
    public void delete(String ids) {
        renderJson(categoryService.deleteByInnerSql(ids) ? BaseResult.ok() : BaseResult.fail());
    }



    /**
     * 获取所有的部门列表
     */
    public void listAll() {
        renderJson(DataResult.data(categoryService.listAll()));
    }

    /**
     * 查看所有充值记录
     * <p>
     */
    //@Param(name = "firmId",required = true)
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getRecharge(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<Recharge> page = rechargeService.list(params.getPageNumber(),params.getPageSize(),cond,"getAllRecharge");
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }



}
