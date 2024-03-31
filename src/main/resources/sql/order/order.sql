#sql("firmOrderList")
    select * from order_info
    where firm_id = #para(rq.firmId) and is_deleted = 0
    #if(rq.type != 0)
     and type = #para(rq.type)
    #end
    #if(rq.orderState != 0)
     and order_state = #para(rq.orderState)
    #end
#end

#sql("getOrderInfo")
select * from order_info where id = #para(id) and is_deleted = 0
#end

#sql("firmFinishedOrderList")
select * from order_info where firm_id = #para(rq.firmId) and is_deleted = 0 and order_state = 12
#end

#sql("firmArrearageOrderList")
select * from order_info where firm_id = #para(rq.firmId) and is_deleted = 0 and order_state = 2
#end

#sql("firmShippedOrderList")
    select * from order_info
    where firm_id = #para(rq.firmId) and is_deleted = 0 and order_state = 3
    #if(rq.type != 0)
     and type = #para(rq.type)
    #end
#end

#sql("firmDispatchedOrderList")
    select * from order_info where firm_id = #para(rq.firmId) and is_deleted = 0 and order_state = 4
#end

#sql("firmNotReturnOrderList")
    select * from order_info
    where firm_id = #para(rq.firmId) and is_deleted = 0 and order_state = 9
    #if(rq.type != 0)
     and type = #para(rq.type)
    #end
#end

#sql("firmReletOrderList")
    select * from order_info
    where firm_id = #para(rq.firmId) and is_deleted = 0 and order_state = 6
    #if(rq.type != 0)
     and type = #para(rq.type)
    #end
#end

#sql("firmTerminalOrderList")
select * from order_info where firm_id = #para(rq.firmId) and is_deleted = 0 and order_state = 8
#end

#sql("firmSaleOrderList")
select * from order_info where firm_id = #para(rq.firmId) and is_deleted = 0 and order_state = 7
#end

#sql("userOrderList")
    select * from order_info
    where user_id = #para(rq.userId) and is_deleted = 0
    #if(rq.type != 0)
     and type = #para(rq.type)
    #end
#end

#sql("unpayOrderList")
    select * from order_info
    where user_id = #para(rq.userId) and order_state = 1 and is_deleted = 0
    #if(rq.type != 0)
     and type = #para(rq.type)
    #end
#end

#sql("userShippedOrderList")
    select * from order_info
    where user_id = #para(rq.userId) and is_deleted = 0 and order_state = 3
    #if(rq.type != 0)
     and type = #para(rq.type)
    #end
#end

#sql("userDispatchedOrderList")
    select * from order_info
    where user_id = #para(rq.userId) and is_deleted = 0 and order_state = 4
    #if(rq.type != 0)
     and type = #para(rq.type)
    #end
#end

#sql("userNotReturnOrderList")
    select * from order_info
     where user_id = #para(rq.userId) and is_deleted = 0 and order_state = 9
     #if(rq.type != 0)
     and type = #para(rq.type)
    #end
#end

#sql("userBuyOrderList")
    select * from order_info
     where user_id = #para(rq.userId) and is_deleted = 0 and order_state = 7
     #if(rq.type != 0)
     and type = #para(rq.type)
    #end
#end

#sql("userContinueOrderList")
    select * from order_info
     where user_id = #para(rq.userId) and is_deleted = 0 and order_state = 6
     #if(rq.type != 0)
     and type = #para(rq.type)
    #end
#end

#sql("userCompleteOrderList")
    select * from order_info
     where user_id = #para(rq.userId) and is_deleted = 0 and order_state = 10
     #if(rq.type != 0)
     and type = #para(rq.type)
    #end
#end

#sql("userterminalOrderList")
    select * from order_info
     where user_id = #para(rq.userId) and is_deleted = 0 and order_state = 8
     #if(rq.type != 0)
     and type = #para(rq.type)
    #end
#end

#sql("adminGetAllOrders")
    select * from order_info
    where is_deleted = 0
    #if(rq.order_state != 0)
     and type = #para(rq.orderState)
    #end
    #if(notBlank(rq.startTime))
     and date_format(created_time,'%Y-%m-%d') >= #para(rq.startTime)
    #end
    #if(notBlank(rq.endTime))
     and date_format(created_time,'%Y-%m-%d') <= #para(rq.endTime)
    #end
#end

#sql("adminGetTypeOrder")
select * from order_info where type = #para(rq.type) and is_deleted = 0
#end

#sql("adminGetStateOrder")
select * from order_info where order_state = #para(rq.orderState) and is_deleted = 0
#end

#sql("adminGetTSOrder")
select * from order_info where type = #para(rq.type) and order_state = #para(rq.orderState) and is_deleted = 0
#end


#sql("getUserUnpayOrder")
select * from order_info where user_id = #para(rq.userId) and type = 4 and order_state = 1 and is_deleted = 0
#end

#sql("getFirmUnpayOrder")
select * from order_info where firm_id = #para(rq.firmId) and type = 4 and order_state = 1 and is_deleted = 0
#end

#sql("getUserReturnOrder")
select * from order_info where user_id = #para(rq.userId) and type = 4 and order_state = 9 and is_deleted = 0
#end

#sql("getFirmReturnOrder")
select * from order_info where firm_id = #para(rq.firmId) and type = 4 and order_state = 9 and is_deleted = 0
#end