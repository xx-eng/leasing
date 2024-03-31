package cn.edu.zuel.firm.category;

import cn.edu.zuel.common.module.Category;
import cn.fabrice.jfinal.service.BaseService;
import cn.fabrice.jfinal.service.BaseTreeService;
import com.jfinal.kit.Kv;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author aila
 * 商品分类
 */
public class CategoryService extends BaseTreeService<Category> {
    private final String TREE_CHILDREN_KEY = "children";

    public CategoryService() {
        super("category.", Category.class, "category");
    }


    /**
     * 根据父ID获取子集
     *
     * @param parentId 父节点ID
     * @return 子节点集合
     */
    public List<Category> listChildren(long parentId) {
        Kv cond = Kv.by("parentId", parentId);
        return list(cond, "listChildren");
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     当前菜单列表
     * @param parentId 传入的父节点ID
     * @return 子菜单列表
     */
    public List<Category> generateTree(List<Category> list, int parentId) {
        List<Category> treeList = new ArrayList<>();
        for (Category t : list) {
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId().longValue() == parentId) {
                recursionFn(list, t);
                treeList.add(t);
            }
        }
        return treeList;
    }

    /**
     * 根据已有列表，获取指定父节点的所有子节点
     *
     * @param list   当前所有列表
     * @param parent 父节点
     * @return 返回当前父节点的所有子节点列表
     */
    private List<Category> getChildrenFromList(List<Category> list, Category parent) {
        return list.parallelStream()
                .filter(item -> item.getParentId().longValue() == parent.getId().longValue())
                .collect(Collectors.toList());
    }

    /**
     * 递归生成当前节点的所有自己点信息
     *
     * @param list 所有菜单列表
     * @param t    当前节点
     */
    private void recursionFn(List<Category> list, Category t) {
        // 得到子节点列表
        List<Category> childList = getChildrenFromList(list, t);
        t.put(TREE_CHILDREN_KEY, childList);
        for (Category tChild : childList) {
            if (tChild.getIsParent() == 1) {
                // 判断是否有子节点
                for (Category m : childList) {
                    recursionFn(list, m);
                }
            }
        }
    }

    /**
     * 获取所有
     */
    public List<Category> listAll() {
        List<Category> treeList = list("listAll");
        return generateTree(treeList, 0);
    }

    /**
     * 获取全称
     *
     * @param id 部门ID
     * @return 部门全称
     */
    public String getFullName(long id) {
        Category category = get(Kv.by("id", id), "getFullName");
        return category != null ? category.getName() : "";
    }



}
