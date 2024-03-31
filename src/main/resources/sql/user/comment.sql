#sql("userCommentList")
select * from comment where goods_id = #para(goodsId) and is_deleted = 0
#end