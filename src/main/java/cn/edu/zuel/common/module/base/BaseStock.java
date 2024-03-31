package cn.edu.zuel.common.module.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseStock<M extends BaseStock<M>> extends Model<M> implements IBean {

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
	 * 关联的商品
	 */
	public void setGoodsId(java.math.BigInteger goodsId) {
		set("goods_id", goodsId);
	}
	
	/**
	 * 关联的商品
	 */
	public java.math.BigInteger getGoodsId() {
		return get("goods_id");
	}
	
	/**
	 * 状态 0-可使用，1-损害，2-已买，3-已租赁，4-正在返还
	 */
	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}
	
	/**
	 * 状态 0-可使用，1-损害，2-已买，3-已租赁，4-正在返还
	 */
	public java.lang.Integer getStatus() {
		return getInt("status");
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
