#sql("getByAccount")
    select id,session_id,expires_in,expires_time
    from user_session
    where user_id = #para(accountId) and is_deleted = 0
    order by id desc
#end

#sql("deleteByToken")
    update user_session set is_deleted = 1
    where session_id = #para(token)
#end