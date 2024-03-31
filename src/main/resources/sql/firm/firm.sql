#sql("getByFirmLogin")
select id,salt,password,status from firm where account = #para(account) and is_deleted = 0
#end

#sql("getFirmName")
select * from firm where id = #para(firmId) and is_deleted = 0
#end

#sql("getFirmInfo")
select * from firm where id = #para(id) and is_deleted = 0
#end

#sql("getAllFirm")
    select * from firm
    where is_deleted = 0
    #if(notBlank(rq.name))
    and position(#para(rq.name) in name) > 0
    #end
#end

