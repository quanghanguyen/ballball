package com.example.ballball.utils

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation

object LocationAddress {
    val khoaHocAddress = "77 Nguyễn Huệ, Phú Nhuận, Thành phố Huế, Thừa Thiên Huế, Vietnam"
    val khoaHocLat = 16.45868048048192
    val khoaHocLong = 107.59229846267155
    val khoaHocHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(khoaHocLat, khoaHocLong))

    val monacoAddress = "FJJ2+M47, Vỹ Dạ, Thành phố Huế, Thừa Thiên Huế, Vietnam"
    val monacoLat = 16.481930939916225
    val monacoLong = 107.6002500320571
    val monacoHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(monacoLat, monacoLong))


    val lamHoangAddress = "FHJX+5V, Vỹ Dạ, Thành phố Huế, Thừa Thiên Huế, Vietnam"
    val lamHoangLat = 16.48061238555775
    val lamHoangLong = 107.59960166652385
    val lamHoangHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(lamHoangLat, lamHoangLong))


    val anCuuAddress = "97 An Dương Vương, An Đông, Thành phố Huế, Thừa Thiên Huế, Vietnam"
    val anCuuLat = 16.457833770534524
    val anCuuLong = 107.61396960304414
    val anCuuHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(anCuuLat, anCuuLong))


    val luatAddress = "CJP5+VJ2, Võ Văn Kiệt, An Tây, Thành phố Huế, Thừa Thiên Huế, Vietnam"
    val luatLat = 16.437365876364755
    val luatLong = 107.6090080972104
    val luatHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(luatLat, luatLong))


    val uyenPhuongAddress = "11 La Sơn Phu Tử, Tây Lộc, Thành phố Huế, Thừa Thiên Huế, Vietnam"
    val uyenPhuongLat = 16.47517585410717
    val uyenPhuongLong = 107.57097478972814
    val uyenPhuongHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(uyenPhuongLat, uyenPhuongLong))

    val yDuocAddress = "6 Ngô Quyền, Vĩnh Ninh, Thành phố Huế, Thừa Thiên Huế, Vietnam"
    val yDuocLat = 16.459234837283876
    val yDuocLong = 107.58737621070306
    val yDuocHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(yDuocLat, yDuocLong))

    val xuanPhuAddress = "FJ82+RRW, Xuân Phú, Thành phố Huế, Thừa Thiên Huế, Vietnam"
    val xuanPhuLat = 16.46735880765263
    val xuanPhuLong = 107.60208212604677
    val xuanPhuHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(xuanPhuLat, xuanPhuLong))
}