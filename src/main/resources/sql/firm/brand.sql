#sql("adminBrandList")
select id,title,firm_id,logo,description from brand where is_deleted = 0
#end

#sql("getBrandName")
select * from brand where id = #para(id)
#end

#sql("getAllBrand")
select * from brand where is_deleted = 0
#end

#sql("getFirmBrand")
    select * from brand
    where firm_id = #para(rq.firmId) and is_deleted = 0
    #if(rq.categoryId != 0)
     and category_id = #para(rq.categoryId)
    #end
#end