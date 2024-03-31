#sql("userFavoritesList")
select * from favorites where is_deleted = 0 and user_id = #para(rq.userId) group by goods_id
#end