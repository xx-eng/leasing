#sql("adminUserList")
select * from user where is_deleted = 0
#end

#sql("getByUserLogin")
select id,salt,password,status from user where account = #para(account) and is_deleted = 0
#end


#sql("getUserInfo")
select * from user where id = #para(id) and is_deleted = 0
#end