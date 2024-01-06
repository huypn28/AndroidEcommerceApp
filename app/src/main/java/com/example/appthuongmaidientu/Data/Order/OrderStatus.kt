package com.example.appthuongmaidientu.Data.Order

sealed class OrderStatus(val status:String) {
    object Ordered: OrderStatus("Đã đặt hàng")
    object Paid: OrderStatus("Đã thanh toán")
    object Canceled: OrderStatus("Đã hủy")
    object Confirmed: OrderStatus("Đã xác nhận")
    object Shipped: OrderStatus("Đã vận chuyển")
    object Delivered: OrderStatus("Đã giao")
    object Returned: OrderStatus("Đã trả về")


}

fun getOrderStatus(status: String):OrderStatus{
    return when (status){
        "Đã đặt hàng"->{
            OrderStatus.Ordered
        }
        "Đã thanh toán"->{
            OrderStatus.Paid
        }
        "Đã hủy"->{
            OrderStatus.Canceled
        }
        "Đã xác nhận"->{
            OrderStatus.Confirmed
        }
        "Đã vận chuyển"->{
            OrderStatus.Shipped
        }
        "Đã giao"->{
            OrderStatus.Delivered
        }
        else->OrderStatus.Returned
    }
}