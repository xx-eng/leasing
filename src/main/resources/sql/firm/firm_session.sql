#sql("getByToken")
select user_id from user_session where token = #para(token) and is_deleted = 0
#end