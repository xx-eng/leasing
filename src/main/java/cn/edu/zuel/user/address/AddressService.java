package cn.edu.zuel.user.address;

import cn.edu.zuel.common.module.UserAddress;
import cn.fabrice.jfinal.service.BaseService;

/**
 * @author aila
 */
public class AddressService extends BaseService<UserAddress> {
    public AddressService(){
        super("user_address.", UserAddress.class, "user_address");
    }
}
