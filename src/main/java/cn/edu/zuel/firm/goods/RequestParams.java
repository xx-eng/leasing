package cn.edu.zuel.firm.goods;

import cn.edu.zuel.common.module.Category;
import cn.fabrice.common.pojo.BasePageRequestParams;

import java.math.BigInteger;
import java.util.List;

/**
 * @author aila
 */
public class RequestParams extends BasePageRequestParams {
    private long firmId;
    private long categoryId;
    private long brandId;
    private long userId;
    private List<Category> categoryList;
    private long[] categoryListId;
    private int status;
    private int type;
    private int orderState;
    // 有关分类的
    private long parentId;
    private String name;
    //有关聊天
    private long clientId;
    /**
            * 创建开始时间
     */
    private String startTime;
    /**
     * 创建结束时间
     */
    private String endTime;

    public RequestParams() {
    }

    public long getFirmId() {
        return firmId;
    }
    public void setFirmId(long firmId) {
        this.firmId = firmId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getBrandId() {
        return brandId;
    }

    public void setBrandId(long brandId) {
        this.brandId = brandId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public long[] getCategoryListId() {
        return categoryListId;
    }

    public void setCategoryListId(long[] categoryListId) {
        this.categoryListId = categoryListId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    public long getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
