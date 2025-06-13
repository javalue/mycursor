package com.demo.cursor

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import java.util.Random

class MainActivity : AppCompatActivity() {
    private lateinit var bannerViewPager: ViewPager2
    private lateinit var recyclerView: RecyclerView
    private val handler = Handler(Looper.getMainLooper())
    private val bannerRunnable = object : Runnable {
        override fun run() {
            val currentItem = bannerViewPager.currentItem
            bannerViewPager.setCurrentItem(if (currentItem == 1) 0 else currentItem + 1, true)
            handler.postDelayed(this, 3000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBanner()
        setupWaterfall()
    }

    private fun setupBanner() {
        bannerViewPager = findViewById(R.id.bannerViewPager)
        bannerViewPager.adapter = BannerAdapter()
        bannerViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        handler.postDelayed(bannerRunnable, 3000)
    }

    private fun setupWaterfall() {
        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = WaterfallAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(bannerRunnable)
    }
}

class BannerAdapter : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
    private val colors = listOf(
        "#FF5722", "#2196F3"
    )

    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.bannerImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.imageView.setBackgroundColor(android.graphics.Color.parseColor(colors[position]))
    }

    override fun getItemCount() = colors.size
}

class WaterfallAdapter : RecyclerView.Adapter<WaterfallAdapter.WaterfallViewHolder>() {
    private val random = Random()
    private val colors = listOf(
        "#FF5722", "#2196F3", "#4CAF50", "#FFC107", "#9C27B0",
        "#E91E63", "#00BCD4", "#FF9800", "#795548", "#607D8B"
    )
    private val items = List(20) { colors[random.nextInt(colors.size)] }
    private val heights = List(20) { random.nextInt(200) + 200 }

    class WaterfallViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorView: View = view.findViewById(R.id.colorView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterfallViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_waterfall, parent, false)
        return WaterfallViewHolder(view)
    }

    override fun onBindViewHolder(holder: WaterfallViewHolder, position: Int) {
        val color = android.graphics.Color.parseColor(items[position])
        holder.colorView.setBackgroundColor(color)
        
        val layoutParams = holder.colorView.layoutParams
        layoutParams.height = heights[position]
        holder.colorView.layoutParams = layoutParams
    }

    override fun getItemCount() = items.size
}