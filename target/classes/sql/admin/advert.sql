#sql("adminAdvertList")
select * from advert where is_deleted = 0
#end

#sql("userAdvertList")
select * from advert where is_deleted = 0 and status = 1
#end