#sql("firmGoodsList")
select id,title,deposit,rental,status,stock from goods where  firm_id = #para(rq.firmId) and is_deleted = 0
#end

#sql("firmGoodsListStatus")
    select * from goods
    where  firm_id = #para(rq.firmId)
    and status = #para(rq.status)
    and is_deleted = 0
    #if(rq.categoryId != 0)
     and category_id = #para(rq.categoryId)
    #end
#end

#sql("getGoodsInfo")
select * from goods where id = #para(id) and is_deleted = 0
#end

#sql("adminGoodsList")
select * from goods where is_deleted = 0
#end

#sql("adminOnGoodsList")
select * from goods where grounding = 0 and is_deleted = 0
#end

#sql("adminOffGoodsList")
select * from goods where grounding = 1 and is_deleted = 0
#end

#sql("adminUncheckedGoodsList")
select * from goods where status = 0 and is_deleted = 0 and stu_goods = 0
#end

#sql("popularGoodsList")
select * from goods where status = 1 and is_deleted = 0 and stu_goods = 0
order by rental_num DESC limit 10
#end

#sql("NewGoodsList")
select * from goods where status = 1 and is_deleted = 0 and stu_goods = 0
order by created_time DESC limit 10
#end

#sql("SelectedGoodsList")
select * from
goods join category on goods.category_id = category.id and stu_goods = 0
 where category.parent_id = #para(0);
#end

#sql("SelectGoodsList")
select * from goods where category_id = #para(rq.categoryId) and is_deleted = 0 and stu_goods = 0
#end

#sql("getSchoolGoods")
select * from goods where is_deleted = 0 and stu_goods = 1
#end

#sql("getUserSchoolGoods")
select * from goods where firm_id=#para(userId) and is_deleted = 0 and (stu_goods = 1 or stu_goods = 2)
#end