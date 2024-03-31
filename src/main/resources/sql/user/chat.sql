#sql("getAllChats")
select * from chat
where client_id = #para(rq.clientId) and firm_id = #para(rq.firmId) and is_deleted = 0
order by created_time
#end

#sql("getAllFirm")
select * from chat where client_id = #para(rq.clientId)  and is_deleted = 0 group by firm_id
#end