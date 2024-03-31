package cn.edu.zuel.common.module.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUserAddress<M extends BaseUserAddress<M>> extends Model<M> implements IBean {

	/**
	 * 编号
	 */
	public void setId(java.math.BigInteger id) {
		set("id", id);
	}
	
	/**
	 * 编号
	 */
	public java.math.BigInteger getId() {
		return get("id");
	}
	
	/**
	 * 用户编号
	 */
	public void setUserId(java.math.BigInteger userId) {
		set("user_id", userId);
	}
	
	/**
	 * 用户编号
	 */
	public java.math.BigInteger getUserId() {
		return get("user_id");
	}
	
	/**
	 * 是否为默认地址 0-所有，1-是，2-否
	 */
	public void setIsDefault(java.lang.Integer isDefault) {
		set("is_default", isDefault);
	}
	
	/**
	 * 是否为默认地址 0-所有，1-是，2-否
	 */
	public java.lang.Integer getIsDefault() {
		return getInt("is_default");
	}
	
	/**
	 * 省
	 */
	public void setProvince(java.lang.String province) {
		set("province", province);
	}
	
	/**
	 * 省
	 */
	public java.lang.String getProvince() {
		return getStr("province");
	}
	
	/**
	 * 市
	 */
	public void setCity(java.lang.String city) {
		set("city", city);
	}
	
	/**
	 * 市
	 */
	public java.lang.String getCity() {
		return getStr("city");
	}
	
	/**
	 * 区/县
	 */
	public void setCounty(java.lang.String county) {
		set("county", county);
	}
	
	/**
	 * 区/县
	 */
	public java.lang.String getCounty() {
		return getStr("county");
	}
	
	/**
	 * 街道
	 */
	public void setStreet(java.lang.String street) {
		set("street", street);
	}
	
	/**
	 * 街道
	 */
	public java.lang.String getStreet() {
		return getStr("street");
	}
	
	/**
	 * 镇/乡
	 */
	public void setTown(java.lang.String town) {
		set("town", town);
	}
	
	/**
	 * 镇/乡
	 */
	public java.lang.String getTown() {
		return getStr("town");
	}
	
	/**
	 * 地址详细信息
	 */
	public void setAddressInfo(java.lang.String addressInfo) {
		set("address_info", addressInfo);
	}
	
	/**
	 * 地址详细信息
	 */
	public java.lang.String getAddressInfo() {
		return getStr("address_info");
	}
	
	/**
	 * 是否删除 0-未删除,1-已删除
	 */
	public void setIsDeleted(java.lang.Integer isDeleted) {
		set("is_deleted", isDeleted);
	}
	
	/**
	 * 是否删除 0-未删除,1-已删除
	 */
	public java.lang.Integer getIsDeleted() {
		return getInt("is_deleted");
	}
	
	/**
	 * 创建时间
	 */
	public void setCreatedTime(java.util.Date createdTime) {
		set("created_time", createdTime);
	}
	
	/**
	 * 创建时间
	 */
	public java.util.Date getCreatedTime() {
		return get("created_time");
	}
	
	/**
	 * 更新时间
	 */
	public void setUpdatedTime(java.util.Date updatedTime) {
		set("updated_time", updatedTime);
	}
	
	/**
	 * 更新时间
	 */
	public java.util.Date getUpdatedTime() {
		return get("updated_time");
	}
	
}
