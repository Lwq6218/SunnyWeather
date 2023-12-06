package io.github.sunny_weather.ui.place

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.sunny_weather.databinding.FragmentPlaceBinding
import io.github.sunny_weather.ui.MainActivity
import io.github.sunny_weather.ui.weather.WeatherActivity

class PlaceFragment : Fragment() {
    private var _binding: FragmentPlaceBinding? = null

    private val binding get() = _binding!!

    val viewModel by lazy { ViewModelProvider(this)[PlaceViewModel::class.java] }

    private lateinit var adapter: PlaceAdapter

    //onCreateView()方法用于创建该碎片对应的视图
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    //onViewCreated()方法在onCreateView()方法执行完后立即执行
    //在这里可以进行一些视图相关的初始化操作
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity is WeatherActivity) {
            Log.d("placeFragment", "in weather_activity")

        }
        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            Log.d("PlaceFragment", "in main_activity")
            val place = viewModel.getSavedPlace()
            val intent = Intent(activity, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        //获取RecyclerView的LayoutManager对象
        val layoutManager = LinearLayoutManager(activity)
        //设置RecyclerView的布局方式为LinearLayout
        binding.recyclerView.layoutManager = layoutManager
        //初始化PlaceAdapter实例
        adapter = PlaceAdapter(this, viewModel.placeList)
        //设置RecyclerView的适配器
        binding.recyclerView.adapter = adapter

        binding.searchPlaceEdit.addTextChangedListener {
            val content = it.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                binding.recyclerView.visibility = View.GONE
                binding.bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            val places = result.getOrNull()
            if (places != null) {
                binding.recyclerView.visibility = View.VISIBLE
                binding.bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}