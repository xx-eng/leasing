#sql("tree")
    select d.id,d.name,d.parent_id,d.is_used as pName,d.is_parent
    from category d
    left join category t on d.parent_id = t.id
    where d.is_deleted = 0
    #if(isBlank(rq.name))
    and d.parent_id = #para(rq.parentId)
    #end
    #if(notBlank(rq.name))
    and position(#para(rq.name) in d.name) > 0
    #end
#end

#sql("listAllCategory")
select * from category where parent_id = 0
#end

#sql("listChildren")
select * from category where parent_id = #para(parentId)
#end


#sql("listAll")
    select d.id,d.id as value,d.name,d.name as label,d.parent_id,d.is_parent,t.name as pName
    from category d
    left join category t on d.parent_id = t.id
    where d.is_deleted = 0
#end

#sql("getCategoryInfo")
select * from category where id = #para(id) and is_deleted = 0
#end

#sql("getALLCategory")
select * from category where is_parent = 0 and is_deleted = 0
#end