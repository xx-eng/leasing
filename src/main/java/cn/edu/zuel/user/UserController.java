package cn.edu.zuel.user;

import cn.edu.zuel.admin.advert.AdvertService;
import cn.edu.zuel.admin.article.ArticleService;
import cn.edu.zuel.common.chat.ChatService;
import cn.edu.zuel.common.constant.UserConstants;
import cn.edu.zuel.common.event.LoginLogEvent;
import cn.edu.zuel.common.interceptor.AuthInterceptor;
import cn.edu.zuel.common.module.*;
import cn.edu.zuel.common.order.OrderService;
import cn.edu.zuel.common.order.trade.TradeService;
import cn.edu.zuel.common.session.LoginInfoService;
import cn.edu.zuel.common.session.SessionService;
import cn.edu.zuel.firm.FirmService;
import cn.edu.zuel.firm.category.CategoryService;
import cn.edu.zuel.firm.goods.GoodsService;
import cn.edu.zuel.firm.goods.RequestParams;
import cn.edu.zuel.firm.stock.StockService;
import cn.edu.zuel.user.UserService;
import cn.edu.zuel.user.address.AddressService;
import cn.edu.zuel.user.comment.CommentService;
import cn.edu.zuel.user.complain.ComplainService;
import cn.edu.zuel.user.favorites.FavoritesService;
import cn.edu.zuel.user.message.MessageService;
import cn.edu.zuel.user.recharge.RechargeService;
import cn.fabrice.common.constant.BaseConstants;
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
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;
import net.dreamlu.event.EventKit;

import javax.xml.crypto.Data;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @author aila
 */
@Path("/user")
@ValidateParam
public class UserController extends Controller {
    @Inject
    UserService userService;
    @Inject
    SessionService sessionService;
    @Inject
    LoginInfoService loginInfoService;
    @Inject
    AdvertService advertService;
    @Inject
    ArticleService articleService;
    @Inject
    GoodsService goodsService;
    @Inject
    OrderService orderService;
    @Inject
    FavoritesService favoritesService;
    @Inject
    FirmService firmService;
    @Inject
    AddressService addressService;
    @Inject
    StockService stockService;
    @Inject
    ComplainService complainService;
    @Inject
    CommentService commentService;
    @Inject
    CategoryService categoryService;
    @Inject
    RechargeService rechargeService;
    @Inject
    TradeService tradeService;
    @Inject
    ChatService chatService;
    @Inject
    MessageService messageService;

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
        BaseResult result = userService.login(keepLogin, account, password);
        if (result.isOk()) {
            loginInfo.setStatus(BaseConstants.Status.NORMAL.ordinal());
            loginInfo.setMsg("登录成功");
            User user = (User) ((DataResult) result).getData();
            loginInfo.setSessionId(user.getBigInteger(BaseConstants.SESSION_ID));
            user.remove(BaseConstants.SESSION_ID);
            EventKit.post(new LoginLogEvent(loginInfo));
        } else {
            System.out.println();
            loginInfo.setStatus(BaseConstants.Status.ABNORMAL.ordinal());
            loginInfo.setMsg(result.getMsg());
            System.out.println("loginInfo:"+loginInfo);
            EventKit.post(new LoginLogEvent(loginInfo));
        }

        System.out.println("BaseConstants.ACCOUNT_ID"+ getAttr(BaseConstants.ACCOUNT_ID));
        System.out.println("result");
        renderJson(result);
    }

    /**
     * 根据token获取用户信息
     */
    public void getInfo() {
        long id = getAttr(BaseConstants.ACCOUNT_ID);
        System.out.println("id"+id);
        Kv cond = Kv.by("id",id);
        User user = userService.get(cond,"getUserInfo");
        user = userService.encrypt(user);
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
        User user = userService.get(cond,"getUserInfo");
        renderJson(DataResult.data(user));
    }

    /**
     * 退出登录
     */
    public void logout() {
        UserSession session = getAttr(BaseConstants.ACCOUNT);
        String token = session.getSessionId();
        System.out.println("token"+token);
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
     * @param user 对应实体
     */
    @Param(name = "name",required = true)
    @Param(name = "sex",required = true)
    @Param(name = "account",required = true)
    @Param(name = "email",required = true)
    @Param(name = "mobile",required = true)
    @Param(name = "Alipay")
    @Param(name = "bankCard",required = true)
    @Param(name = "password",required = true)
    @Param(name = "isStudent",required = true)
    public void add(@Para("") User user){
        String salt = HashKit.generateSaltForSha256();
        user.setSalt(salt);
        System.out.println(HashKit.md5(user.getPassword()));
        user.setPassword(HashKit.sha256(salt+user.getPassword()));
        renderJson(user.save() ? BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
    }

    /**
     * 充值
     * <p>
     */
    @Param(name = "userId", required = true)
    @Param(name = "money", required = true)
    @Param(name = "pass", required = true)
    public void recharge(@Para("") Recharge recharge, String pass){
        Kv cond = Kv.by("id",recharge.getUserId());
        User user = userService.get(cond,"getUserInfo");
        String salt = user.getSalt();
        String hashedPass = HashKit.sha256(salt + pass);
        if (!user.getPassword().equals(hashedPass)) {
            System.out.println("密码错误");
            System.out.println(UserConstants.Result.ERROR_PASSWORD);
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, "密码错误"));
            return;
        }


        long money = user.getBalance().longValue() + recharge.getMoney().longValue();
        BigDecimal balance = new BigDecimal(money);
        user.setBalance(balance);
        if(user.update()){
            renderJson(recharge.save() ? BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS)
                    : BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
        }else{
            renderJson(BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
        }
    }

    /**
     * 付款
     * <p>
     */
    @Param(name = "id", required = true)
    public void pay(long id){
        Kv cond = Kv.by("id", id);
        OrderInfo order = orderService.get(cond, "getOrderInfo");
        Kv cond1 = Kv.by("id", order.getUserId());
        User user = userService.get(cond1, "getUserInfo");
        Kv condfirm = Kv.by("id", order.getFirmId());
        Firm firm = firmService.get(condfirm, "getFirmInfo");
        System.out.println("user" + user);
        long money = order.getRental().longValue() + order.getDeposit().longValue();
        if(user.getBalance().longValue() <= money){
            renderJson(BaseResult.res(UserConstants.Result.DB_MONEY, UserConstants.Message.DB_MONEY));
        }else{
            //添加付款记录
            Trade trade = new Trade();
            trade.setClientId(order.getUserId()); trade.setOrderId(order.getId()); trade.setFirmId(order.getFirmId());
            BigDecimal tradeMoney = new BigDecimal(money);
            trade.setType(1); trade.setMoney(tradeMoney); trade.setDepositPosition(0); trade.setRentalPosition(2);
            boolean flag1 = trade.save();
            // 减钱,更新用户信息
            long balance = user.getBalance().longValue();
            BigDecimal balDecimal = new BigDecimal(balance-money);
            user.setBalance(balDecimal);
            boolean flag2 = user.update();
            //商户加钱
            long firmBalance = firm.getBalance().longValue();
            BigDecimal firmBal = new BigDecimal(firmBalance + order.getRental().longValue());
            firm.setBalance(firmBal);
            firm.update();
            order.setTradeId(trade.getId());
            order.setPayState(1);
            order.setOrderState(3);
            boolean flag3 = order.update();
            if(flag1 && flag2 && flag3){
                renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS));
            }else{
                renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
            }

        }
    }

    /**
     * 续租付款
     * <p>
     */
    @Param(name = "id", required = true)
    public void continuePay(long id){
        Kv cond = Kv.by("id", id);
        OrderInfo order = orderService.get(cond, "getOrderInfo");
        Kv cond1 = Kv.by("id", order.getUserId());
        User user = userService.get(cond1, "getUserInfo");
        Kv condfirm = Kv.by("id", order.getFirmId());
        Firm firm = firmService.get(condfirm, "getFirmInfo");
        System.out.println("user" + user);
        long money = order.getRental().longValue();
        if(user.getBalance().longValue() <= money){
            renderJson(BaseResult.res(UserConstants.Result.DB_MONEY, UserConstants.Message.DB_MONEY));
        }else{
            //添加付款记录
            Trade trade = new Trade();
            trade.setClientId(order.getUserId()); trade.setOrderId(order.getId()); trade.setFirmId(order.getFirmId());
            BigDecimal tradeMoney = new BigDecimal(money);
            trade.setType(1); trade.setMoney(tradeMoney); trade.setDepositPosition(0); trade.setRentalPosition(2);
            boolean flag1 = trade.save();
            // 减钱,更新用户信息
            long balance = user.getBalance().longValue();
            BigDecimal balDecimal = new BigDecimal(balance-money);
            user.setBalance(balDecimal);
            boolean flag2 = user.update();
            //商户加钱
            long firmBalance = firm.getBalance().longValue();
            BigDecimal firmBal = new BigDecimal(firmBalance + money);
            firm.setBalance(firmBal);
            firm.update();
            order.setTradeId(trade.getId());
            order.setPayState(1);
            order.setOrderState(9);
            boolean flag3 = order.update();
            if(flag1 && flag2 && flag3){
                renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS));
            }else{
                renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
            }

        }
    }

    /**
     * 取消订单
     * <p>
     * @Param id
     */
    @Param(name = "id", required = true)
    public void deleteOrder(long id) {
        Kv cond = Kv.by("id", id);
        OrderInfo order = orderService.get(cond, "getOrderInfo");
        Kv cond1 = Kv.by("id", order.getUserId());
        User user = userService.get(cond1, "getUserInfo");
        Kv condfirm = Kv.by("id", order.getFirmId());
        Firm firm = firmService.get(condfirm, "getFirmInfo");
        long money = order.getRental().longValue() + order.getDeposit().longValue();

        Kv condgoods = Kv.by("id",order.getGoodsId());
        Goods goods = goodsService.get(condgoods, "getGoodsInfo");
        int stockNum = goods.getStock();
        int rentalNum = goods.getRentalNum();
        goods.setRentalNum(rentalNum - 1);
        goods.setStock(stockNum + 1);
        goods.update();

        Kv stockcond = Kv.by("id", order.getStockId());
        Stock stock = stockService.get(stockcond, "getStockInfo");
        stock.setStatus(0);
        stock.update();


        if(order.getOrderState() == 3){
            BigDecimal userbal = new BigDecimal(money + user.getBalance().longValue());
            user.setBalance(userbal);

            BigDecimal firmbal = new BigDecimal( firm.getBalance().longValue() - order.getRental().longValue());
            firm.setBalance(firmbal);

            if(!(user.update() && firm.update())){
                renderJson(BaseResult.res(UserConstants.Result.DB_DELETE_FAILURE, UserConstants.Message.DB_DELETE_FAILURE));
            }

        }


        renderJson(orderService.deleteByInnerSql(id) ? BaseResult.res(UserConstants.Result.DB_DELETE_SUCCESS, UserConstants.Message.DB_DELETE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_DELETE_FAILURE, UserConstants.Message.DB_DELETE_FAILURE));
    }

    /**
     * 购买
     * <p>
     */
    @Param(name = "id", required = true)
    public void buy(long id){
        Kv cond = Kv.by("id", id);
        OrderInfo order = orderService.get(cond, "getOrderInfo");
        Kv cond2 = Kv.by("id", order.getGoodsId());
        Goods goods = goodsService.get(cond2, "getGoodsInfo");
        int saleNum = goods.getSaleNum();
        int rentalNum = goods.getRentalNum();
        goods.setSaleNum(saleNum + 1);
        goods.setRentalNum(rentalNum - 1);

        Kv cond1 = Kv.by("id", order.getUserId());
        User user = userService.get(cond1, "getUserInfo");
        Kv cond3 = Kv.by("id", order.getFirmId());
        Firm firm = firmService.get(cond3, "getFirmInfo");
        System.out.println("user" + user);
        long price = goods.getPrice().longValue();
        long money = order.getRental().longValue() + order.getDeposit().longValue();
        long balance = user.getBalance().longValue();
        if(balance <= price - money){
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, "余额不足"));
        }else{
            long userbal = balance + money - price;
            BigDecimal usebalBig = new BigDecimal(userbal);
            user.setBalance(usebalBig);

            long firmbal = firm.getBalance().longValue();
            BigDecimal firmbalBig = new BigDecimal(firmbal + price - order.getRental().longValue());
            firm.setBalance(firmbalBig);

            order.setOrderState(7);
            if(user.update() && firm.update() && order.update()){
                renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS));
            }else{
                renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
            }
        }

    }

    /**
     * 确认收货
     * <p>
     */
    @Param(name = "id", required = true)
    public void confirm(long id){
        Kv cond = Kv.by("id", id);
        OrderInfo order = orderService.get(cond, "getOrderInfo");
        order.setOrderState(9);
        if(order.update()){
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS));
        }else{
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
        }
    }

    /**
     * 返还
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
        order.setOrderState(9);
        order.setLogisticsId(logisticsId);

        renderJson(order.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }



    /**
     * 查看所有广告
     * <p>
     */
    //@Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    //@Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getAllAdvert(){
        List<Advert> adverts = advertService.list("userAdvertList");
        System.out.println(adverts);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, adverts));
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
        Page<Article> page = articleService.list(params.getPageNumber(),params.getPageSize(),cond,"adminUserList");
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }

    /**
     * 热门短租
     * <p>
     */
    //@Param(name = "firmId",required = true)
    //@Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    //@Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getPopularGoods(){
        List<Goods> goods = goodsService.list("popularGoodsList");
        System.out.println(goods);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, goods));
    }

    /**
     * 最新优选
     * <p>
     */
    public void getNewGoods(){
        List<Goods> goods = goodsService.list("NewGoodsList");
        System.out.println(goods);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, goods));
    }


    /**
     * 筛选页面大类
     * <p>
     */
    @Param(name = "categoryId",required = true)
    //@Param(name = "brandId",required = true)
    // @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    //@Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getSelectedGoods(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        System.out.println("categoryId:"+params.getCategoryId());
        List<Category> categoryList = categoryService.listChildren(params.getCategoryId());
        //Page<Record> page = goodsService.listRecord(params.getPageNumber(),params.getPageSize(),cond,"SelectGoodsList");
        List<Goods> goods = goodsService.getSelectedGoods(categoryList);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, goods));
    }

    /**
     * 筛选页面2
     * <p>
     */
    @Param(name = "categoryId",required = true)
    //@Param(name = "brandId",required = true)
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getSelectGoods(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<Goods> page = goodsService.list(params.getPageNumber(),params.getPageSize(),cond,"SelectGoodsList");
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }

    /**
     * 获取文件
     * <p>
     */
    public void upload() {
        try {
            UploadFile file = getFile();
            String filep = file.getFileName();
            System.out.println(filep);
            renderJson("status","0");
        }catch (Exception e){
            e.printStackTrace();
            renderJson("status","1");
        }
    }

    /**
     * 收藏
     * <p>
     *
     *
     * @param favorites 对应实体
     */
    @Param(name = "userId",required = true)
    @Param(name = "goodsId",required = true)
    public void addFavorites(@Para("") Favorites favorites){
        renderJson(favorites.save() ? BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
    }

    /**
     * 查看收藏  myUpdate04.07
     * <p>
     */
    //@Param(name = "firmId",required = true)
    @Param(name = "userId",required = true)
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getFavorites(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<Favorites> page = favoritesService.list(params.getPageNumber(),params.getPageSize(),cond,"userFavoritesList");
        for(Favorites favorites: page.getList()){
            Kv cond1 = Kv.by("id",favorites.getGoodsId().longValue());
            System.out.println(cond1);
            Goods goods = goodsService.get(cond1,"getGoodsInfo");
            //Goods goods = goodsService.get(favorites.getGoodsId().longValue());
            favorites.put("image",goods.getImage());
            favorites.put("title",goods.getTitle());
            favorites.put("stuGoods",goods.getStuGoods());   // 判断是否为校租租收藏
        }
        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }


    /**
     * 下单
     * <p>
     * @param order 对应实体
     */
    @Param(name = "userId",required = true)
    @Param(name = "goodsId",required = true)
    @Param(name = "type",required = true)
    @Param(name = "rentTime",required = true)
    @Param(name = "startTime",required = true)
    @Param(name = "endTime",required = true)
    @Param(name = "rental", required = true)
    @Param(name = "contractId")
    @Param(name = "userAddr", required = true)
    public void addOrder(@Para("") OrderInfo order){
        Kv cond = Kv.by("id",order.getGoodsId());
        Goods goods = goodsService.get(cond,"getGoodsInfo");
        System.out.println("goods:"+goods);
        if(goods.getStock() == 0){
            // 如果存货为0
            renderJson(BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
        }else{
            order.setFirmId(goods.getFirmId());
            order.setDeposit(goods.getDeposit());
            //需要计算
            //BigDecimal rentTime = new BigDecimal(order.getRentTime());
            //order.setRental(goods.getRental().multiply(rentTime));
            //添加返还地址
            Kv cond1 = Kv.by("firmId", order.getFirmId());
            Firm firm = firmService.get(cond1, "getFirmName");
            order.setFirmAddr(firm.getAddressId());
            //设置订单状态
            order.setOrderState(1);
            //添加具体商品编号
            Kv cond2 = Kv.by("goodsId",order.getGoodsId());
            Stock stock = stockService.get(cond2,"getUnused");
            if(stock == null){
                renderJson(BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
            }else{
                stock.setStatus(3);
                if(stock.update()){   // 更新成功
                    System.out.println(stock);
                    int rentalNum = goods.getRentalNum();
                    int stockNum = goods.getStock();
                    goods.setRentalNum(rentalNum+1);
                    goods.setStock(stockNum-1);
                    goods.update();
                    order.setStockId(stock.getId());
                    System.out.println(order);
                    renderJson(order.save() ? DataResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS, order)
                            : BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
                }else{
                    renderJson(BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
                }
                // order.setStockId(stock.getId().longValue());
            }
        }

    }

    /**
     * 续租
     * <p>
     */
    @Param(name = "id", required = true)
    @Param(name = "reletTime",required = true)
    @Param(name = "rental",required = true)
    public void continueOrder(@Para("") OrderInfo order) {
        Kv cond = Kv.by("id",order.getId());
        OrderInfo orderinfo = orderService.get(cond,"getOrderInfo");
        orderinfo.setReletTime(order.getReletTime());
        //orderinfo.setEndTime(order.getEndTime());
        orderinfo.setOrderState(6);
        orderinfo.setPayState(0);
        //BigDecimal reletTime = new BigDecimal(orderinfo.getReletTime());
        orderinfo.setRental(order.getRental());
        renderJson(orderinfo.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }

    /**
     * 退租
     * <p>
     */
    @Param(name = "id", required = true)
    @Param(name = "logisticsId",required = true)
    public void terminalOrder(@Para("") OrderInfo order) {
        order.setOrderState(8);
        Date date = new Date(System.currentTimeMillis());
        order.setEndTime(date);
        renderJson(order.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }

    /**
     * 查看所有订单
     * <p>
     */
    //@Param(name = "firmId",required = true)
    @Param(name = "userId",required = true)
    @Param(name = "type")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getAllOrders(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"userOrderList");
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
    //@Param(name = "firmId",required = true)
    @Param(name = "id",required = true)
    public void getOrderInfo(long id){
        Kv cond = Kv.by("id", id);
        OrderInfo order = orderService.get(cond, "getOrderInfo");
        System.out.println(order);
        long goodsId = order.getGoodsId().longValue();
        Kv cond1 = Kv.by("id",goodsId);
        System.out.println(goodsId);
        Goods goods = goodsService.get(cond1,"getGoodsInfo");
        order.put("image",goods.getImage());
        order.put("goodsTitle",goods.getTitle());
        System.out.println(order);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, order));
    }


    /**
     * 查看待付款订单
     * <p>
     */
    //@Param(name = "firmId",required = true)
    @Param(name = "userId",required = true)
    @Param(name = "type")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getUnpayOrders(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"unpayOrderList");
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
     * 查看待发货订单
     * <p>
     */
    //@Param(name = "firmId",required = true)
    @Param(name = "userId",required = true)
    @Param(name = "type")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getShippedOrders(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"userShippedOrderList");
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
     * 查看待收货订单
     * <p>
     */
    //@Param(name = "firmId",required = true)
    @Param(name = "userId",required = true)
    @Param(name = "type")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getDispatchedOrders(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"userDispatchedOrderList");
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
    @Param(name = "userId",required = true)
    @Param(name = "type")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getNotReturnOrders(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"userNotReturnOrderList");
        for(OrderInfo order: page.getList()){
            System.out.println(order);
            long goodsId = order.getGoodsId().longValue();
            Kv cond1 = Kv.by("id",goodsId);
            System.out.println(goodsId);
            Goods goods = goodsService.get(cond1,"getGoodsInfo");
            order.put("image",goods.getImage());
            order.put("goodsTitle",goods.getTitle());
        }        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }

    /**
     * 查看所有已完成订单
     * <p>
     *
     */
    @Param(name = "userId",required = true)
    @Param(name = "type")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getCompleteOrders(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"userCompleteOrderList");
        for(OrderInfo order: page.getList()){
            System.out.println(order);
            long goodsId = order.getGoodsId().longValue();
            Kv cond1 = Kv.by("id",goodsId);
            System.out.println(goodsId);
            Goods goods = goodsService.get(cond1,"getGoodsInfo");
            order.put("image",goods.getImage());
            order.put("goodsTitle",goods.getTitle());
        }        System.out.println(page.getList());
        renderJson(TableResult.set(UserConstants.Result.DB_FIND_SUCCESS,page.getTotalRow(), page.getList()));
    }

    /**
     * 查看退租订单
     * <p>
     */
    //@Param(name = "firmId",required = true)
    @Param(name = "userId",required = true)
    @Param(name = "type")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getTerminalOrder(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"userterminalOrderList");
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
     * 查看已购买订单
     * <p>
     */
    //@Param(name = "firmId",required = true)
    @Param(name = "userId",required = true)
    @Param(name = "type")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getBuyOrder(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"userBuyOrderList");
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
     * 查看续租订单
     * <p>
     */
    //@Param(name = "firmId",required = true)
    @Param(name = "userId",required = true)
    @Param(name = "type")
    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getContinueOrder(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        Page<OrderInfo> page = orderService.list(params.getPageNumber(),params.getPageSize(),cond,"userContinueOrderList");
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
     * 查看不同订单的数量
     * <p>
     *
     */
    @Param(name = "userId",required = true)
    public void getOrderNumber(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        List<OrderInfo> unpay = orderService.list(cond,"unpayOrderList");
        List<OrderInfo> shipped = orderService.list(cond,"userShippedOrderList");
        List<OrderInfo> dispatched = orderService.list(cond,"userDispatchedOrderList");
        List<OrderInfo> notReturn = orderService.list(cond,"userNotReturnOrderList");
        Record orderNumber = new Record();
        orderNumber.set("unpay", unpay.size());
        orderNumber.set("shipped",shipped.size());
        orderNumber.set("dispatched",dispatched.size());
        orderNumber.set("notReturn", notReturn.size());
        System.out.println(orderNumber);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, orderNumber));
    }

    /**
     * 投诉
     * <p>
     * @param complain 对应实体
     */
    @Param(name = "userId",required = true)
    @Param(name = "firmId",required = true)
    @Param(name = "target",required = true)
    @Param(name = "feedbackType",required = true)
    @Param(name = "content",required = true)
    @Param(name = "pictureId",required = true)
    @Param(name = "pictureName", required = true)
    public void addComplain(@Para("") Complain complain){
        renderJson(complain.save() ? BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
    }

    /**
     * 评价
     * <p>
     * @param comment 对应实体
     */
    @Param(name = "userId",required = true)
    @Param(name = "goodsId",required = true)
    @Param(name = "goodsComment",required = true)
    @Param(name = "mark",required = true)
    public void addComment(@Para("") Comment comment){
        renderJson(comment.save() ? BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
    }

    /**
     * 查看所有评论
     * <p>
     */
//    @Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
//    @Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "goodsId")
    public void getAllComments(long goodsId){
        Kv cond = Kv.by("goodsId", goodsId);
        List<Comment> comments = commentService.list(cond, "userCommentList");
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS,UserConstants.Message.DB_FIND_SUCCESS, comments));
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
     * 查看个人资料
     * <p>
     */
    @Param(name = "id", required = true)
    public void getUserInfo(long id){
        Kv cond = Kv.by("id",id);
        User user = userService.get(cond,"getUserInfo");
        if(user == null){
            renderJson(BaseResult.res(UserConstants.Result.DB_FIND_FAILURE, UserConstants.Message.DB_FIND_FAILURE));
        }
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, user));
    }

    /**
     * 更改资料  myUpdate 04.08
     * <p>
     */
    @Param(name = "id",required = true)
    @Param(name = "account")
    @Param(name = "sex")
    @Param(name = "mobile")
    @Param(name = "email")
    public void updateUserInfo(@Para("") User user) {
        renderJson(user.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
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
        User user = userService.get(cond, "getUserInfo");
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
                User user = userService.get(cond,"getUserInfo");
                System.out.println(user);
                user.setAddress(address.getId());
                user.update();
            }

            renderJson(BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS));
        }else{
            renderJson(BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
        }

    }

    /**
     * 删除地址
     * <p>
     * @Param id
     */
    @Param(name = "id", required = true)
    public void deleteAddress(long id) {
        renderJson(addressService.deleteByInnerSql(id) ? BaseResult.res(UserConstants.Result.DB_DELETE_SUCCESS, UserConstants.Message.DB_DELETE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_DELETE_FAILURE, UserConstants.Message.DB_DELETE_FAILURE));
    }

    /**
     * 设为默认地址
     * <p>
     */
    @Param(name = "id", required = true)
    @Param(name = "userId", required = true)
    @Param(name = "defaultId", required = true)
    public void updateAddress(long id, long userId, long defaultId){
        Kv cond = Kv.by("id",userId);
        User user = userService.get(cond,"getUserInfo");
//        long usedAddress = user.getAddress().longValue();

        Kv cond1 = Kv.by("addressId", id);
        UserAddress nowAddress= addressService.get(cond1, "getUserAddress");
        nowAddress.setIsDefault(1);

        Kv cond2 = Kv.by("addressId", defaultId);
        UserAddress beforeAddress= addressService.get(cond2, "getUserAddress");
        beforeAddress.setIsDefault(0);
        System.out.print("defaultId" + defaultId);

        BigInteger aId = new BigInteger(String.valueOf(id));
        user.setAddress(aId);


        if(user.update() && beforeAddress.update() && nowAddress.update()){
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS));
        }else{
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
        }
    }

    /**
     * 查看所有地址
     * <p>
     */
    @Param(name = "userId",required = true)
    //@Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    //@Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getAllAddress(long userId){
        Kv cond = Kv.by("userId", userId);
        System.out.println(cond);
        List<UserAddress> addresses = addressService.list(cond, "getAddresses");
        Kv cond1 = Kv.by("id",userId);
        User user = userService.get(cond1,"getUserInfo");
        for(UserAddress addr : addresses){
            addr.put("name", user.getName());
            addr.put("mobile",user.getMobile());
        }
        System.out.println(addresses);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, addresses));
    }

    /**
     * 查看默认地址
     * <p>
     */
    @Param(name = "userId",required = true)
    //@Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    //@Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getDefaultAddress(long userId){
        Kv cond = Kv.by("userId", userId);
        System.out.println(cond);
        UserAddress addresses = addressService.get(cond, "getDefaultAddresses");
        System.out.println(addresses);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, addresses));
    }

    /**
     * 查看商家地址
     * <p>
     */
    @Param(name = "firmId",required = true)
    //@Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    //@Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getFirmAddress(long firmId){
        Kv cond = Kv.by("id", firmId);
        System.out.println(cond);
        Firm firm = firmService.get(cond, "getFirmInfo");
        long addressId = firm.getAddressId().longValue();
        Kv cond1 = Kv.by("addressId", addressId);
        UserAddress addresses = addressService.get(cond1, "getUserAddress");
        System.out.println(addresses);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, addresses));
    }


    /**
     * 查看分类
     * <p>
     */
    //@Param(name = "parentId",required = true)
    public void getAllCategory(){
        List<Category> list = categoryService.list("listAllCategory");
        System.out.println("list"+list);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, list));
    }

    /**
     * 查看某一分类子分类
     * <p>
     */
    @Param(name = "parentId",required = true)
    public void getACategory(long parentId){
        List<Category> list = categoryService.listChildren(parentId);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, list));
    }

    /**
     * 新增数据
     *
     * @param category 对应实体类
     */
    @Param(name = "parentId", required = true)
    @Param(name = "name", required = true)
    @Param(name = "is_parent",required = true)
    public void addCategory(@Para("") Category category) {
        BaseResult result = judge(category);
        if (result.isFail()) {
            renderJson(result);
            return;
        }
        renderJson(categoryService.processAdd(category) ? BaseResult.ok() : BaseResult.fail());
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
        if (categoryService.isExistByInnerSql(id, "name", item.getName())) {
            //说明重复??????
            return BaseResult.fail("部门名称已存在");
        }
        return BaseResult.ok();
    }

    /**
     * 查看与某商户的聊天信息
     * <p>
     */
    @Param(name = "clientId",required = true)
    @Param(name = "firmId",required = true)
    public void getFirmChat(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        List<Chat> chats = chatService.list(cond,"getAllChats");
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, chats));
    }

    /**
     * 发消息
     * <p>
     */
    @Param(name = "clientId",required = true)
    @Param(name = "firmId",required = true)
    @Param(name = "initiator",required = true)
    @Param(name = "record",required = true)
    public void addChat(@Para("") Chat chat){
        renderJson(chat.save() ? BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
    }

    /**
     * 获取商户信息
     * <p>
     */
    @Param(name = "clientId",required = true)
    public void getChatFirmInfo(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        List<Chat> chats = chatService.list(cond,"getAllFirm");
        for(Chat chat : chats){
            long firmId = chat.getFirmId().longValue();
            Kv cond1 = Kv.by("id", firmId);
            Firm firm = firmService.get(cond1, "getFirmInfo");
            chat.put("firmName", firm.getFirmName());
            chat.put("firmAccount", firm.getAccount());
        }
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, chats));
    }

    /**
     * 获取通知信息
     * <p>
     */
    @Param(name = "clientId",required = true)
    public void getMessage(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        List<Message> chats = messageService.list(cond,"getAllMessage");
        for(Message chat : chats){
            long firmId = chat.getFirmId().longValue();
            Kv cond1 = Kv.by("id", firmId);
            Firm firm = firmService.get(cond1, "getFirmInfo");
            chat.put("firmName", firm.getFirmName());
            chat.put("firmAccount", firm.getAccount());
        }
        System.out.println(chats);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, chats));
    }

    /**
     * 添加商品
     * <p>
     */
    @Param(name = "firmId", required = true)
    @Param(name = "title",required = true)
//    @Param(name = "brandId",required = true)
//    @Param(name = "categoryId",required = true)
    @Param(name = "abstract",required = true)
//    @Param(name = "isSale",required = true)
    @Param(name = "deposit",required = true)
    @Param(name = "rental",required = true)
//    @Param(name = "rentTime",required = true)
//    @Param(name = "price",required = true)
//    @Param(name = "stock",required = true)
    @Param(name = "imageId", required = true)
    @Param(name = "image", required = true)
    public void addUserGoods(@Para("") Goods goods) {
        goods.setStuGoods(1);
        if(goods.save()){
            renderJson(BaseResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS));
        }else{
            renderJson(BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));
        }

    }

    /**
     * 更改商品
     * <p>
     */
    @Param(name = "firmId", required = true)
    @Param(name = "title")
    @Param(name = "abstract")
    @Param(name = "deposit")
    @Param(name = "rental")
//    @Param(name = "imageId", required = true)
//    @Param(name = "image", required = true)
    public void updateGoods(@Para("") Goods goods) {

        renderJson(goods.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));

    }

    /**
     * 查看所有学校商品
     * <p>
     */
    //@Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    //@Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    public void getAllSchoolGoods(){
        List<Goods> goods = goodsService.list("getSchoolGoods");
        System.out.println(goods);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, goods));
    }

    /**
     * 查看所有学生商品
     * <p>
     */
    //@Param(name = "pageNumber",rule = ValidateRuleConstants.Key.ID)
    //@Param(name = "pageSize",rule = ValidateRuleConstants.Key.ID)
    @Param(name = "userId", required = true)
    public void getUserSchoolGoods(long userId){
        Kv cond = Kv.by("userId", userId);
        List<Goods> goods = goodsService.list(cond,"getUserSchoolGoods");
        System.out.println(goods);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, goods));
    }

    /**
     * 删除商品
     * <p>
     * @Param id
     */
    @Param(name = "id", required = true)
    public void deleteUserGoods(long id) {
        renderJson(goodsService.deleteByInnerSql(id) ? BaseResult.res(UserConstants.Result.DB_DELETE_SUCCESS, UserConstants.Message.DB_DELETE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_DELETE_FAILURE, UserConstants.Message.DB_DELETE_FAILURE));
    }


    /**
     * 校园租赁下单
     * <p>
     * @param order 对应实体
     */
    @Param(name = "userId",required = true)
    @Param(name = "goodsId",required = true)
//    @Param(name = "type",required = true)
    @Param(name = "rentTime",required = true)
//    @Param(name = "startTime",required = true)
//    @Param(name = "endTime",required = true)
    @Param(name = "rental", required = true)
//    @Param(name = "contractId")
//    @Param(name = "userAddr", required = true)
    public void addSchoolOrder(@Para("") OrderInfo order){
        Kv cond = Kv.by("id",order.getGoodsId());
        Goods goods = goodsService.get(cond,"getGoodsInfo");
        goods.setStuGoods(2);
        goods.update();
        System.out.println("goods:"+goods);
        order.setFirmId(goods.getFirmId());
        order.setDeposit(goods.getDeposit());
        order.setType("4");
        order.setOrderState(1);
        renderJson(order.save() ? DataResult.res(UserConstants.Result.DB_ADD_SUCCESS, UserConstants.Message.DB_ADD_SUCCESS, order)
                : BaseResult.res(UserConstants.Result.DB_ADD_FAILURE, UserConstants.Message.DB_ADD_FAILURE));


    }


    /**
     * 查看待付款
     * <p>
     *
     */
    @Param(name = "userId",required = true)
    public void getUserUnpayOrder(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        List<OrderInfo> page = orderService.list(cond,"getUserUnpayOrder");
        for(OrderInfo order: page){
            System.out.println(order);
            long goodsId = order.getGoodsId().longValue();
            Kv cond1 = Kv.by("id",goodsId);
            System.out.println(goodsId);
            Goods goods = goodsService.get(cond1,"getGoodsInfo");
            order.put("image",goods.getImage());
            order.put("goodsTitle",goods.getTitle());
        }
        System.out.println(page);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, page));
    }

    /**
     * 查看别人待付款
     * <p>
     *
     */
    @Param(name = "firmId",required = true)
    public void getFirmUnpayOrder(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        List<OrderInfo> page = orderService.list(cond,"getFirmUnpayOrder");
        for(OrderInfo order: page){
            System.out.println(order);
            long goodsId = order.getGoodsId().longValue();
            Kv cond1 = Kv.by("id",goodsId);
            System.out.println(goodsId);
            Goods goods = goodsService.get(cond1,"getGoodsInfo");
            order.put("image",goods.getImage());
            order.put("goodsTitle",goods.getTitle());
        }
        System.out.println(page);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, page));
    }

    /**
     * 查看待退款
     * <p>
     *
     */
    @Param(name = "userId",required = true)
    public void getUserReturnOrder(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        List<OrderInfo> page = orderService.list(cond,"getUserReturnOrder");
        for(OrderInfo order: page){
            System.out.println(order);
            long goodsId = order.getGoodsId().longValue();
            Kv cond1 = Kv.by("id",goodsId);
            System.out.println(goodsId);
            Goods goods = goodsService.get(cond1,"getGoodsInfo");
            order.put("image",goods.getImage());
            order.put("goodsTitle",goods.getTitle());
        }
        System.out.println(page);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, page));
    }

    /**
     * 查看别人待退款
     * <p>
     *
     */
    @Param(name = "firmId",required = true)
    public void getFirmReturnOrder(@Para("") RequestParams params){
        Kv cond = Kv.by("rq", params);
        List<OrderInfo> page = orderService.list(cond,"getFirmReturnOrder");
        for(OrderInfo order: page){
            System.out.println(order);
            long goodsId = order.getGoodsId().longValue();
            Kv cond1 = Kv.by("id",goodsId);
            System.out.println(goodsId);
            Goods goods = goodsService.get(cond1,"getGoodsInfo");
            order.put("image",goods.getImage());
            order.put("goodsTitle",goods.getTitle());
        }
        System.out.println(page);
        renderJson(DataResult.res(UserConstants.Result.DB_FIND_SUCCESS, UserConstants.Message.DB_FIND_SUCCESS, page));
    }

    /**
     * 校园租赁付款
     * <p>
     */
    @Param(name = "id", required = true)
    public void schoolPay(long id){
        Kv cond = Kv.by("id", id);
        OrderInfo order = orderService.get(cond, "getOrderInfo");
        Kv cond1 = Kv.by("id", order.getUserId());
        User user = userService.get(cond1, "getUserInfo");
        Kv condfirm = Kv.by("id", order.getFirmId());
        User firm = userService.get(condfirm, "getUserInfo");
        System.out.println("user" + user);
        long money = order.getRental().longValue() + order.getDeposit().longValue();
        if(user.getBalance().longValue() <= money){
            renderJson(BaseResult.res(UserConstants.Result.DB_MONEY, UserConstants.Message.DB_MONEY));
        }else{
            //添加付款记录
            Trade trade = new Trade();
            trade.setClientId(order.getUserId()); trade.setOrderId(order.getId()); trade.setFirmId(order.getFirmId());
            BigDecimal tradeMoney = new BigDecimal(money);
            trade.setType(1); trade.setMoney(tradeMoney); trade.setDepositPosition(0); trade.setRentalPosition(2);
            boolean flag1 = trade.save();
            // 减钱,更新用户信息
            long balance = user.getBalance().longValue();
            BigDecimal balDecimal = new BigDecimal(balance-money);
            user.setBalance(balDecimal);
            boolean flag2 = user.update();
            //商户加钱
            long firmBalance = firm.getBalance().longValue();
            BigDecimal firmBal = new BigDecimal(firmBalance + order.getRental().longValue());
            firm.setBalance(firmBal);
            firm.update();
            order.setTradeId(trade.getId());
            order.setPayState(1);
            order.setOrderState(9);
            boolean flag3 = order.update();
            if(flag1 && flag2 && flag3){
                renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS));
            }else{
                renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
            }

        }
    }


    /**
     * 校园租赁退款
     * <p>
     */
    @Param(name = "id",required = true)
    public void refund(long id){
        Kv cond = Kv.by("id",id);
        OrderInfo order = orderService.get(cond,"getOrderInfo");
        if(order == null){
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
        }



        long deposit = order.getDeposit().longValue();
        long userId = order.getUserId().longValue();
        Kv cond1 = Kv.by("id", userId);
        User user = userService.get(cond1, "getUserInfo");
        long balance = user.getBalance().longValue();
        BigDecimal balDecimal = new BigDecimal(balance + deposit);
        user.setBalance(balDecimal);
        if(!user.update()){
            renderJson(BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
        }

        Kv cond2 = Kv.by("id", order.getGoodsId().longValue());
        Goods  goods = goodsService.get(cond2,"getGoodsInfo");
        goods.setStuGoods(1);
        goods.update();

        order.setOrderState(10);
        renderJson(order.update() ? BaseResult.res(UserConstants.Result.DB_UPDATE_SUCCESS, UserConstants.Message.DB_UPDATE_SUCCESS)
                : BaseResult.res(UserConstants.Result.DB_UPDATE_FAILURE, UserConstants.Message.DB_UPDATE_FAILURE));
    }

}
