#sql("firmTradeList")
select * from trade where firm_id = #para(rq.firmId) and is_deleted = 0
#end

#sql("adminTradeList")
select * from trade where is_deleted = 0
#end