#--------------------厂商模块相关SQL操作--------------------#
#namespace("brand")
#include("firm/brand.sql")
#end

#namespace("category")
#include("firm/category.sql")
#end

#namespace("firm")
#include("firm/firm.sql")
#end

#namespace("firmSession")
#include("firm/firm_session.sql")
#end

#namespace("goods")
#include("firm/goods.sql")
#end

#namespace("industry1")
#include("firm/industry1.sql")
#end

#namespace("stock")
#include("firm/stock.sql")
#end
#--------------------厂商模块相关SQL操作--------------------#


#--------------------订单模块相关SQL操作--------------------#
#namespace("order")
#include("order/order.sql")
#end

#namespace("trade")
#include("order/trade.sql")
#end


#--------------------订单模块相关SQL操作--------------------#


#--------------------管理员模块相关SQL操作--------------------#
#namespace("admin")
#include("admin/admin.sql")
#end

#namespace("advert")
#include("admin/advert.sql")
#end

#namespace("article")
#include("admin/article.sql")
#end
#--------------------管理员模块相关SQL操作--------------------#


#--------------------用户模块相关SQL操作--------------------#
#namespace("user")
#include("user/user.sql")
#end

#namespace("complain")
#include("user/complain.sql")
#end

#namespace("favorites")
#include("user/favorites.sql")
#end

#namespace("comment")
#include("user/comment.sql")
#end

#namespace("user_address")
#include("user/user_address.sql")
#end

#namespace("user_session")
#include("user/user_session.sql")
#end

#namespace("recharge")
#include("user/recharge.sql")
#end

#namespace("chat")
#include("user/chat.sql")
#end

#namespace("message")
#include("user/message.sql")
#end

#--------------------用户模块相关SQL操作--------------------#

#namespace("file")
#include("file/file.sql")
#end