package com.example.ballball.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ballball.R
import com.example.ballball.databinding.ActivityMapsBinding
import com.example.ballball.databinding.LayoutBottomSheetDialogBinding
import com.example.ballball.databinding.MapNavigationDialogBinding
import com.example.ballball.utils.LocationAddress.anCuuAddress
import com.example.ballball.utils.LocationAddress.anCuuLat
import com.example.ballball.utils.LocationAddress.anCuuLong
import com.example.ballball.utils.LocationAddress.khoaHocAddress
import com.example.ballball.utils.LocationAddress.khoaHocLat
import com.example.ballball.utils.LocationAddress.khoaHocLong
import com.example.ballball.utils.LocationAddress.lamHoangAddress
import com.example.ballball.utils.LocationAddress.lamHoangLat
import com.example.ballball.utils.LocationAddress.lamHoangLong
import com.example.ballball.utils.LocationAddress.luatAddress
import com.example.ballball.utils.LocationAddress.luatLat
import com.example.ballball.utils.LocationAddress.luatLong
import com.example.ballball.utils.LocationAddress.monacoAddress
import com.example.ballball.utils.LocationAddress.monacoLat
import com.example.ballball.utils.LocationAddress.monacoLong
import com.example.ballball.utils.LocationAddress.uyenPhuongAddress
import com.example.ballball.utils.LocationAddress.uyenPhuongLat
import com.example.ballball.utils.LocationAddress.uyenPhuongLong
import com.example.ballball.utils.LocationAddress.xuanPhuAddress
import com.example.ballball.utils.LocationAddress.xuanPhuLat
import com.example.ballball.utils.LocationAddress.xuanPhuLong
import com.example.ballball.utils.LocationAddress.yDuocAddress
import com.example.ballball.utils.LocationAddress.yDuocLat
import com.example.ballball.utils.LocationAddress.yDuocLong

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.maps.android.SphericalUtil
import okhttp3.OkHttpClient
import okhttp3.Request

class MapsActivity :
    AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapBottomSheetDialogBinding: MapNavigationDialogBinding
    private var currentLat : Double? = null
    private var currentLong : Double? = null
    private var currentAddress : String? = null
    private var destinationLat : Double? = null
    private var destinationLong : Double? = null
    private var destinationAddress : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        currentLat = intent.getDoubleExtra("currentLat", 0.1)
        currentLong = intent.getDoubleExtra("currentLong", 0.1)
        currentAddress = intent.getStringExtra("currentAddress")
        destinationLat = intent.getDoubleExtra("destinationLat", 0.1)
        destinationLong = intent.getDoubleExtra("destinationLong", 0.1)
        destinationAddress = intent.getStringExtra("destinationAddress")

        val originLocation = LatLng(currentLat!!, currentLong!!)
        val destinationLocation = LatLng(destinationLat!!, destinationLong!!)
//        val destinationLocation = LatLng(16.48194127564437, 107.60030369996487)

        mapFragment.getMapAsync {
            map = it
//            map.addMarker(MarkerOptions().position(destinationLocation))
            val urll = getDirectionURL(originLocation, destinationLocation, apiKey)
            GetDirection(urll).execute()
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 14F))
        }
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val originLocation = LatLng(currentLat!!, currentLong!!)
        val khoaHoc = LatLng(khoaHocLat, khoaHocLong)
        val monaco = LatLng(monacoLat, monacoLong)
        val lamHoang = LatLng(lamHoangLat, lamHoangLong)
        val anCuu = LatLng(anCuuLat, anCuuLong)
        val luat = LatLng(luatLat, luatLong)
        val uyenPhuong = LatLng(uyenPhuongLat, uyenPhuongLong)
        val yDuoc = LatLng(yDuocLat, yDuocLong)
        val xuanPhu = LatLng(xuanPhuLat, xuanPhuLong)

        map.addMarker(
            (MarkerOptions()
                .position(khoaHoc)
                .title("Sân Khoa Học Huế")
                .snippet(khoaHocAddress)
                    )
        )
        map.addMarker(
            MarkerOptions()
                .position(monaco)
                .title("Sân Monaco Huế")
                .snippet(monacoAddress)
        )
        map.addMarker(
            MarkerOptions()
                .position(lamHoang)
                .title("Sân Lâm Hoằng Huế")
                .snippet(lamHoangAddress)
        )
        map.addMarker(
            MarkerOptions()
                .position(anCuu)
                .title("Sân An Cựu Huế")
                .snippet(anCuuAddress)
        )
        map.addMarker(
            MarkerOptions()
                .position(luat)
                .title("Sân Đại Học Luật Huế")
                .snippet(luatAddress)
        )
        map.addMarker(
            MarkerOptions()
                .position(uyenPhuong)
                .title("Sân Uyên Phương Huế")
                .snippet(uyenPhuongAddress)
        )
        map.addMarker(
            MarkerOptions()
                .position(yDuoc)
                .title("Sân Đại Học Y Dược Huế")
                .snippet(yDuocAddress)
        )
        map.addMarker(
            MarkerOptions()
                .position(xuanPhu)
                .title("Sân Xuân Phú Huế")
                .snippet(xuanPhuAddress)
        )

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 15f))

        enableMyLocation()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        map.setOnMarkerClickListener(this)
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            return
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1
        )
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val navigationDialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
        mapBottomSheetDialogBinding = MapNavigationDialogBinding.inflate(layoutInflater)
        navigationDialog.setContentView(mapBottomSheetDialogBinding.root)
        mapBottomSheetDialogBinding.myLocationAddress.text = currentAddress
        mapBottomSheetDialogBinding.pitchLocationAddress.text = marker.snippet
        mapBottomSheetDialogBinding.pitchLocation.text = marker.title

        val originLocation = LatLng(currentLat!!, currentLong!!)
        val destinationLocation = LatLng(marker.position.latitude, marker.position.longitude)
        val distance = SphericalUtil.computeDistanceBetween(originLocation, destinationLocation)
        val meter = String.format("%.2f", distance / 1000)
        val time = ((distance/1000)/30) * 60
        val duration = time.toInt()
        if (duration == 0) {
            mapBottomSheetDialogBinding.duration.text = "1 Phút"
        } else {
            mapBottomSheetDialogBinding.duration.text = "$duration Phút"
        }
        mapBottomSheetDialogBinding.distance.text = "$meter Km"


        navigationDialog.show()
        return true
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Đây là vị trí của bạn", Toast.LENGTH_LONG).show()
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Đang tìm vị trí của bạn", Toast.LENGTH_SHORT).show()
        return false
    }

    private fun getDirectionURL(origin:LatLng, dest:LatLng, secret: String) : String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()

            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, MapData::class.java)
                val path = ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(ContextCompat.getColor(baseContext, R.color.colorPrimary))
                lineoption.geodesic(true)
            }
            map.addPolyline(lineoption)
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }
}