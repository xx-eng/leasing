#sql("getUnused")
select * from stock where goods_id=#para(goodsId) and status = 0 and is_deleted = 0
#end

#sql("getStockInfo")
select * from stock where id = #para(id) and is_deleted = 0
#end