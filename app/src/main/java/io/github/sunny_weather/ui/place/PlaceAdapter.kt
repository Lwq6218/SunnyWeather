package io.github.sunny_weather.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.sunny_weather.R
import io.github.sunny_weather.logic.model.Place
import io.github.sunny_weather.ui.weather.WeatherActivity

class PlaceAdapter(
    private val fragment: PlaceFragment,
    private val placeList: List<Place>
) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    //内部类ViewHolder，继承自RecyclerView.ViewHolder
    //view参数就是RecyclerView子项的最外层布局,也就是place_item.xml
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)
    }

    //onCreateViewHolder()方法用于创建ViewHolder实例
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //第一步，加载place_item.xml布局
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.place_item, parent, false)
        val holder = ViewHolder(view)
        //第二步，为每个子项设置点击事件
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val place = placeList[position]
            val activity = fragment.activity
            if (activity is WeatherActivity) {
                activity.binding.drawerLayout.closeDrawers()
                activity.viewModel.locationLang = place.location.lng
                activity.viewModel.locationLat = place.location.lat
                activity.viewModel.placeName = place.name
                activity.refreshWeather()
            } else {
                val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                    putExtra("location_lng", place.location.lng)
                    putExtra("location_lat", place.location.lat)
                    putExtra("place_name", place.name)

                }
                fragment.startActivity(intent)
                fragment.activity?.finish()
            }
            fragment.viewModel.savePlace(place)
        }
        //第三步，返回ViewHolder实例
        return holder
    }

    //getItemCount()方法用于告诉RecyclerView一共有多少子项
    override fun getItemCount(): Int {
        return placeList.size
    }

    //onBindViewHolder()方法用于对RecyclerView子项的数据进行赋值
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }
}