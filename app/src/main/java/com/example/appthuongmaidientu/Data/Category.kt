package com.example.appthuongmaidientu.Data

sealed class Category(val category:String) {

    object Chair: Category("Ghế")
    object Cupboard: Category("Tủ")
    object Table: Category("Bàn")
    object Accessory: Category("Phụ kiện")
    object Furniture: Category("Đồ nội thất")


}