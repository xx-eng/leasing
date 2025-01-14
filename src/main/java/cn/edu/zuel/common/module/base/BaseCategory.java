package cn.edu.zuel.common.module.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCategory<M extends BaseCategory<M>> extends Model<M> implements IBean {

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
	 * 分类名称
	 */
	public void setName(java.lang.String name) {
		set("name", name);
	}
	
	/**
	 * 分类名称
	 */
	public java.lang.String getName() {
		return getStr("name");
	}
	
	/**
	 * 父节点 如果存在父节点，则对应父节点id
	 */
	public void setParentId(java.math.BigInteger parentId) {
		set("parent_id", parentId);
	}
	
	/**
	 * 父节点 如果存在父节点，则对应父节点id
	 */
	public java.math.BigInteger getParentId() {
		return get("parent_id");
	}
	
	/**
	 * 父节点集合 该节点的所有父节点集合，从根节点一直到当前节点的直接父节点，父节点直接以,分隔
	 */
	public void setParentIdList(java.lang.String parentIdList) {
		set("parent_id_list", parentIdList);
	}
	
	/**
	 * 父节点集合 该节点的所有父节点集合，从根节点一直到当前节点的直接父节点，父节点直接以,分隔
	 */
	public java.lang.String getParentIdList() {
		return getStr("parent_id_list");
	}
	
	/**
	 * 当前节点是否为父节点 0-否 1-是
	 */
	public void setIsParent(java.lang.Integer isParent) {
		set("is_parent", isParent);
	}
	
	/**
	 * 当前节点是否为父节点 0-否 1-是
	 */
	public java.lang.Integer getIsParent() {
		return getInt("is_parent");
	}
	
	/**
	 * 层级 根节点为第一层级
	 */
	public void setLevel(java.lang.Integer level) {
		set("level", level);
	}
	
	/**
	 * 层级 根节点为第一层级
	 */
	public java.lang.Integer getLevel() {
		return getInt("level");
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
	
	/**
	 * 0-未启用， 1-启用
	 */
	public void setIsUsed(java.lang.Integer isUsed) {
		set("is_used", isUsed);
	}
	
	/**
	 * 0-未启用， 1-启用
	 */
	public java.lang.Integer getIsUsed() {
		return getInt("is_used");
	}
	
}
