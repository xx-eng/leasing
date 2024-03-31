#sql("getUserAddress")
select * from user_address where id = #para(addressId) and is_deleted = 0
#end

#sql("getAddresses")
select * from user_address where user_id = #para(userId) and is_deleted = 0
#end

#sql("getDefaultAddresses")
select * from user_address where user_id = #para(userId) and is_default = 1 and is_deleted = 0
#end