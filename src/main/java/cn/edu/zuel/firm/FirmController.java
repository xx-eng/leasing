package cn.edu.zuel.firm;

import cn.edu.zuel.common.constant.UserConstants;
import cn.edu.zuel.common.event.LoginLogEvent;
import cn.edu.zuel.common.interceptor.AuthInterceptor;
import cn.edu.zuel.common.module.*;
import cn.edu.zuel.common.order.OrderService;
import cn.edu.zuel.common.order.trade.TradeService;
import cn.edu.zuel.common.session.LoginInfoService;
import cn.edu.zuel.common.session.SessionService;
import cn.edu.zuel.firm.brand.BrandService;
import cn.edu.zuel.firm.category.CategoryService;
import cn.edu.zuel.firm.goods.GoodsService;
import cn.edu.zuel.firm.goods.RequestParams;
import cn.edu.zuel.firm.industry.IndustryService;
import cn.edu.zuel.firm.stock.StockService;
import cn.edu.zuel.user.UserService;
import cn.edu.zuel.user.address.AddressService;
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @author aila
 */
@Path("/firm")
@ValidateParam
public class FirmController extends Controller {
    @Inject
    FirmService service;
    @Inject
    SessionService sessionService;
    @Inject
    LoginInfoService loginInfoService;
    @Inject
    BrandService brandService;
    @Inject
    CategoryService categoryService;
    @Inject
    GoodsService goodsService;
    @Inject
    IndustryService industryService;
    @Inject
    StockService stockService;
    @Inject
    OrderService orderService;
    @Inject
    TradeService tradeService;
    @Inject
    UserService userService;
    @Inject
    AddressService addressService;


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
        BaseResult result = service.login(keepLogin, account, password);
        if (result.isOk()) {
            loginInfo.setStatus(BaseConstants.Status.NORMAL.ordinal());
            loginInfo.setMsg("登录成功");
            Firm user = (Firm) ((DataResult) result).getData();
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
     * 更改密码
     * <p>
     */
    @Param(name = "id",required = true)
    @Param(name = "password")
    @Param(name = "newPassword")
    public void updateUserPassword(long id, String password,String newPassword) {
        Kv cond = Kv.by("id", id);
        Firm user = service.get(cond, "getFirmInfo");
        System.out.println("user:"+user.getPassword());
        System.out.println("password:"+HashKit.sha256(user.getSalt()+ HashKit.md5(password)));
        if (user == null) {
            System.out.println("用户不存在");
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, "用户不存在"));
            return;
        }

        String hashPwd = HashKit.sha256(user.getSalt() + password);
        if (!hashPwd.equals(user.getPassword())) {
            System.out.println("密码错误");
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, "密码错误"));
            return;
        }
        System.out.println(HashKit.md5(newPassword));
        user.setPassword(HashKit.sha256(user.getSalt()+newPassword));
        renderJson(user.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }



    /**
     * 根据token获取用户信息
     */
    public void getInfo() {
        long id = getAttr(BaseConstants.ACCOUNT_ID);
        System.out.println("id"+id);
        Kv cond = Kv.by("id",id);
        Firm user = service.get(cond,"getFirmInfo");
        user = service.encrypt(user);
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
        Firm user = service.get(cond,"getFirmInfo");
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
     * @param firm 对应实体
     */
    @Param(name = "name",required = true)
    @Param(name = "account", required = true)
    @Param(name = "bankCard",required = true)
    @Param(name = "idCard",required = true)
    @Param(name = "idCardPicId",required = true)
    @Param(name = "idCardPicName", required = true)
    @Param(name = "email",required = true)
    @Param(name = "mobile",required = true)
    @Param(name = "firmName",required = true)
    @Param(name = "registration",required = true)
//    @Param(name = "industryId",required = true)
    @Param(name = "password",required = true)
    @Param(name = "licenseId",required = true)
    @Param(name = "licenseName",required = true)
    public void add(@Para("") Firm firm){
        String salt = HashKit.generateSaltForSha256();
        firm.setSalt(salt);
        System.out.println(HashKit.md5(firm.getPassword()));
        firm.setPassword(HashKit.sha256(salt+HashKit.md5(firm.getPassword())));
        renderJson(firm.save() ? BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
    }

    /**
     * 添加卖家法人身份证复印件
     * <p>
     * @Param id
     */
    @Param(name = "id", required = true)
    public void getCardPic(long id){
        Kv cond = Kv.by("firmId",id);
        Firm firm = service.get(cond,"getFirmName");
        if(firm == null){
            renderJson(BaseResult.fail("厂商用户不存在"));
        }
        try {
            UploadFile file = getFile();
            String filep = file.getFileName();
            System.out.println(filep);
            //firm.setIdCardPic("/file/"+filep);
        }catch (Exception e){
            e.printStackTrace();
        }
        renderJson(firm.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }

    /**
     * 添加营业执照
     * <p>
     * @Param id
     */
    @Param(name = "id", required = true)
    public void getLicense(long id){
        Kv cond = Kv.by("firmId",id);
        Firm firm = service.get(cond,"getFirmName");
        if(firm == null){
            renderJson(BaseResult.fail("厂商用户不存在"));
        }
        try {
            UploadFile file = getFile();
            String filep = file.getFileName();
            System.out.println(filep);
            // firm.setLicense("/file/"+filep);
        }catch (Exception e){
            e.printStackTrace();
        }
        renderJson(firm.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }

    /**
     * 添加地址
     * <p>
     * @param address 对应实体
     */
    @Param(name = "userId",required = true)
    @Param(name = "isDefault")
    @Param(name = "province",required = true)
    @Param(name = "city",required = true)
    @Param(name = "county")
    @Param(name = "street")
    @Param(name = "town")
    @Param(name = "addressInfo",required = true)
    public void addAddress(@Para("") UserAddress address){
        if(address.save()){
            System.out.println(address.getId());
            System.out.println(address.getIsDefault());
            if(address.getIsDefault() == 1){
                System.out.println(address.getUserId());
                Kv cond = Kv.by("id",address.getUserId());
                Firm user = service.get(cond,"getFirmInfo");
                System.out.println(user);
                user.setAddressId(address.getId());
                user.update();
            }

            renderJson(BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS));
        }else{
            renderJson(BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
        }

    }

    /**
     * 查找地址
     * <p>
     *
     */
    @Param(name = "id", required = true, rule = ValidateRuleConstants.Key.ID)
    public void getAddress(long id) {
        Kv cond = Kv.by("id",id);
        Firm user = service.get(cond,"getFirmInfo");
        long addressId = user.getAddressId().longValue();
        Kv cond1 = Kv.by("addressId", addressId);
        UserAddress address = addressService.get(cond1, "getUserAddress");
        renderJson(DataResult.data(address));
    }


    /**
     * 查看所有商品
     * <p>
     */
    @Param(name = "firmId",required = true)
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getAllGoods(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<Goods> page = goodsService.list(params.getPageNumber(),params.getPageSize(),cond,"firmGoodsList");
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }

    /**
     * 查看某状态商品
     * <p>
     */
    @Param(name = "firmId",required = true)
    @Param(name = "status",required = true)
    @Param(name = "categoryId")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getGoods(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        System.out.println(params.getCategoryId());
        Page<Goods> page = goodsService.list(params.getPageNumber(),params.getPageSize(),cond,"firmGoodsListStatus");
        for(Goods goods: page.getList()){
            long brandId = goods.getBrandId().longValue();
            Kv cond1 = Kv.by("id", brandId);
            Brand brand = brandService.get(cond1, "getBrandName");
            goods.put("brandName", brand.getTitle());
            long categoryId = goods.getCategoryId().longValue();
            cond1 = Kv.by("id",categoryId);
            Category category = categoryService.get(cond1, "getCategoryInfo");
            goods.put("categoryName",category.getName());
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
            renderJson(BaseResult.res(UserConstants.Result.DB_FIND_FAILURE, UserConstants.Message.DB_FIND_FAILURE));
        }
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, goods));
    }

    /**
     * 下架商品
     * <p>
     * @Param id
     */
    @Param(name = "id", required = true)
    public void downshelf(long id) {
        Kv cond = Kv.by("id",id);
        Goods goods = goodsService.get(cond, "getGoodsInfo");
        goods.setGrounding(0);
        renderJson(goods.update() ? BaseResult.ok() : BaseResult.fail());
    }

    /**
     * 上架商品
     * <p>
     * @Param id
     */
    @Param(name = "id", required = true)
    public void upshelf(long id) {
        Kv cond = Kv.by("id",id);
        Goods goods = goodsService.get(cond, "getGoodsInfo");
        goods.setGrounding(1);
        renderJson(goods.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }


    /**
     * 添加商品
     * <p>
     */
    @Param(name = "firmId", required = true)
    @Param(name = "title",required = true)
    @Param(name = "brandId",required = true)
    @Param(name = "categoryId",required = true)
    @Param(name = "abstract",required = true)
    @Param(name = "isSale",required = true)
    @Param(name = "deposit",required = true)
    @Param(name = "rental",required = true)
    @Param(name = "rentTime",required = true)
    @Param(name = "price",required = true)
    @Param(name = "stock",required = true)
    @Param(name = "imageId", required = true)
    @Param(name = "image", required = true)
    public void addGoods(@Para("") Goods goods) {
//        try {
//            UploadFile file = getFile();
//            String filep = file.getFileName();
//            System.out.println(filep);
//            goods.setImage("/file/"+filep);
//            //goods.setThumbnail("/file/"+filep);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        if(goods.save()){
            for(int i=0;i<goods.getStock();i++){
                Stock stock = new Stock();
                stock.setGoodsId(goods.getId());
                stock.setStatus(0);
                stock.save();
            }
            renderJson(BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS));
        }else{
            renderJson(BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
        }

    }

    /**
     * 添加库存
     * <p>
     */
    @Param(name = "id", required = true)
    @Param(name = "number")
    public void addStock(long id, int number) {
        Kv cond = Kv.by("id", id);
        Goods goods = goodsService.get(cond, "getGoodsInfo");
        int stockNum = goods.getStock();
        goods.setStock(stockNum + number);
        if(goods.update()){
            for(int i=0;i<number;i++){
                Stock stock = new Stock();
                stock.setGoodsId(goods.getId());
                stock.setStatus(0);
                stock.save();
            }

            renderJson(BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS));
        }else{
            renderJson(BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
        }

    }

    /**
     * 修改商品
     * <p>
     */
    @Param(name = "id", required = true)
    @Param(name = "title")
    @Param(name = "brandId")
    @Param(name = "category")
    @Param(name = "abstract")
    @Param(name = "is_sale")
    @Param(name = "deposit")
    @Param(name = "rental")
    @Param(name = "rentTime")
    @Param(name = "price")
    @Param(name = "price")
    @Param(name = "stock")
    @Param(name = "imageId")
    @Param(name = "image")
    public void updateGoods(@Para("") Goods goods) {
        renderJson(goods.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }

    /**
     * 查看所有订单
     * <p>
     *
     */
    @Param(name = "firmId",required = true)
    @Param(name = "orderState")
    @Param(name = "type")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getAllOrders(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"firmOrderList");
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
     * 查看所有已完成订单
     * <p>
     *
     */
    @Param(name = "firmId",required = true)
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getFinishedOrders(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"firmFinishedOrderList");
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
     * 查看所有待付款订单
     * <p>
     *
     */
    @Param(name = "firmId",required = true)
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getArrearageOrders(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"firmArrearageOrderList");
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
     * 查看所有待发货订单
     * <p>
     *
     */
    @Param(name = "firmId",required = true)
    @Param(name = "type")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getShippedOrders(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"firmShippedOrderList");
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
     * 查看所有待收货订单
     * <p>
     *
     */
    @Param(name = "firmId",required = true)
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getDispatchedOrders(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"firmDispatchedOrderList");
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
     * 查看所有待归还订单
     * <p>
     *
     */
    @Param(name = "firmId",required = true)
    @Param(name = "type")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getNotReturnOrders(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"firmNotReturnOrderList");
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
     * 查看所有续租订单
     * <p>
     *
     */
    @Param(name = "firmId",required = true)
    @Param(name = "type")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getReletOrders(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"firmReletOrderList");
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
     * 查看所有退租订单
     * <p>
     *
     */
    @Param(name = "firmId",required = true)
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getTerminalOrders(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"firmTerminalOrderList");
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
     * 查看所有购买订单
     * <p>
     *
     */
    @Param(name = "firmId",required = true)
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getSaleOrders(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"firmSaleOrderList");
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
     * 查看订单详情
     * <p>
     */
    @Param(name = "id", required = true)
    public void getOrderInfo(long id){
        Kv cond = Kv.by("id",id);
        OrderInfo order = orderService.get(cond,"getOrderInfo");
        if(order == null){
            renderJson(BaseResult.res(UserConstants.Result.DB_FIND_FAILURE, UserConstants.Message.DB_FIND_FAILURE));
        }

        //商品信息
        long goodsId = order.getGoodsId().longValue();
        Kv cond1 = Kv.by("id",goodsId);
        System.out.println(goodsId);
        Goods goods = goodsService.get(cond1,"getGoodsInfo");
        order.put("image",goods.getImage());
        order.put("goodsTitle",goods.getTitle());
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, order));
        BaseResult.ok();
    }

    /**
     * 发货
     * <p>
     */
    @Param(name = "id",required = true)
    @Param(name = "logisticsId",required = true)
    public void consignment(long id, BigInteger logisticsId){
        Kv cond = Kv.by("id",id);
        OrderInfo order = orderService.get(cond,"getOrderInfo");

        if(order == null){
            renderJson(BaseResult.res(UserConstants.Result.DB_FIND_FAILURE, UserConstants.Message.DB_FIND_FAILURE));
        }
        order.setOrderState(4);
        order.setLogisticsId(logisticsId);

        long firmId = order.getFirmId().longValue();
        Kv cond1 = Kv.by("id", firmId);
        Firm firm = service.get(cond1, "getFirmInfo");
        long balance = firm.getBalance().longValue();
        long rental = order.getRental().longValue();
        BigDecimal balDecimal = new BigDecimal(balance + rental);
        firm.setBalance(balDecimal);
        if(!firm.update()){
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
        }

        renderJson(order.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }

    /**
     * 正常退款
     * <p>
     */
    @Param(name = "id",required = true)
    @Param(name = "level",required = true)
    public void refund(long id, long level){
        Kv cond = Kv.by("id",id);
        OrderInfo order = orderService.get(cond,"getOrderInfo");
        if(order == null){
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
        }

        long deposit = order.getDeposit().longValue();
        long userId = order.getUserId().longValue();
        Kv cond1 = Kv.by("id", userId);
        User user = userService.get(cond1, "getUserInfo");
        long balance = user.getBalance().longValue() * level;
        BigDecimal balDecimal = new BigDecimal(balance + deposit);
        user.setBalance(balDecimal);
        if(!user.update()){
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
        }

        order.setOrderState(10);

        long stockId = order.getStockId().longValue();
        Kv cond2 = Kv.by("id", stockId);
        Stock stock = stockService.get(cond2, "getStockInfo");
        stock.setStatus(0);
        stock.update();

        renderJson(order.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }

    /**
     * 提前退款,重写！！！！
     * <p>
     */
    @Param(name = "id",required = true)
    @Param(name = "remainTime", required = true)
    public void refundBefore(long id, int remainTime){
        Kv cond = Kv.by("id",id);
        OrderInfo order = orderService.get(cond,"getOrderInfo");
        if(order == null){
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
        }
        long deposit = order.getDeposit().longValue();
        long rental = order.getRental().longValue();

        long userId = order.getUserId().longValue();
        Kv cond1 = Kv.by("id", userId);
        User user = userService.get(cond1, "getUserInfo");
        long balance = user.getBalance().longValue();
        BigDecimal balDecimal = new BigDecimal(balance + deposit + rental*remainTime);
        user.setBalance(balDecimal);
        if(!user.update()){
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
        }

        long firmId = order.getFirmId().longValue();
        Kv cond2 = Kv.by("id", firmId);
        Firm firm = service.get(cond2, "getFirmInfo");
        long firmBalance = firm.getBalance().longValue();
        BigDecimal bal = new BigDecimal(firmBalance - rental*remainTime);
        firm.setBalance(bal);
        if(!firm.update()){
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
        }

        long stockId = order.getStockId().longValue();
        Kv cond3 = Kv.by("id", stockId);
        Stock stock = stockService.get(cond3, "getStockInfo");
        stock.setStatus(0);
        stock.update();

        order.setOrderState(10);
        renderJson(order.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }


    /**
     * 查看所有交易
     * <p>
     */
    @Param(name = "firmId",required = true)
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getAllTrade(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        System.out.println("params"+params.getFirmId());
        try{
            Page<Trade> page = tradeService.list(params.getPageNumber(),params.getPageSize(),cond,"firmTradeList");
            System.out.println("page"+page.getList());
            System.out.println(page.getList());
            renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
        }catch (Exception e){
            System.out.println("发生错误"+e);
            renderJson(BaseResult.res(UserConstants.Result.DB_FIND_FAILURE, UserConstants.Message.DB_FIND_FAILURE));
        }

    }

    /**
     * 新增
     * <p>
     * 品牌
     *
     * @param brand 对应实体
     */
    @Param(name = "title",required = true)
    @Param(name = "firmId",required = true)
    @Param(name = "description",required = true)
    @Param(name = "logoId", required = true)
    @Param(name = "logo", required = true)
    public void addBrand(@Para("") Brand brand){
//        try {
//            UploadFile file = getFile();
//            String filep = file.getFileName();
//            System.out.println(filep);
//            brand.setLogo("/file/"+filep);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        renderJson(brand.save() ? BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
    }

    /**
     *
     * <p>
     * 获取该商户所有品牌
     */
    @Param(name = "firmId", required = true)
    @Param(name = "categoryId")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getFirmBrand(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        List<Brand> brands = brandService.list(cond, "getFirmBrand");
        for(Brand brand: brands){
            long categoryId = brand.getCategoryId().longValue();
            Kv cond1 = Kv.by("id", categoryId);
            Category category = categoryService.get(cond1, "getCategoryInfo");
            brand.put("categoryName", category.getName());
        }
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS,UserConstants.Message.DB_FIND_SUCCESS, brands));
    }


    /**
     *
     * <p>
     * 获取所有；品牌
     */
    public void getAllBrand(){
        List<Brand> brands = brandService.list("getAllBrand");
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS,UserConstants.Message.DB_FIND_SUCCESS, brands));
    }

    /**
     *
     * <p>
     * 获取所有；分类
     */
    public void getAllCategory(){
        List<Category> categoryList = categoryService.list("getALLCategory");
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS,UserConstants.Message.DB_FIND_FAILURE, categoryList));
    }


}
