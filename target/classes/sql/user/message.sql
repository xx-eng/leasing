#sql("getAllMessage")
select * from message
where client_id = #para(rq.clientId) and is_deleted = 0
order by created_time
#end