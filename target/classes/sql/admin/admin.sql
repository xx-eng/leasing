#sql("getByAdminLogin")
select id,salt,password,status from admin where account = #para(account) and is_deleted = 0
#end

#sql("getAdminInfo")
select * from admin where id = #para(id) and is_deleted = 0
#end