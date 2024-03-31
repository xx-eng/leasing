package cn.edu.zuel.common.module.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseArticle<M extends BaseArticle<M>> extends Model<M> implements IBean {

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
	 * 标题
	 */
	public void setTitle(java.lang.String title) {
		set("title", title);
	}
	
	/**
	 * 标题
	 */
	public java.lang.String getTitle() {
		return getStr("title");
	}
	
	/**
	 * 简介
	 */
	public void setContent(java.lang.String content) {
		set("content", content);
	}
	
	/**
	 * 简介
	 */
	public java.lang.String getContent() {
		return getStr("content");
	}
	
	/**
	 * 所属分类
	 */
	public void setType(java.lang.Integer type) {
		set("type", type);
	}
	
	/**
	 * 所属分类
	 */
	public java.lang.Integer getType() {
		return getInt("type");
	}
	
	/**
	 * 状态 0-待发布，1-已显示，2-未显示
	 */
	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}
	
	/**
	 * 状态 0-待发布，1-已显示，2-未显示
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