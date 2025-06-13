package com.demo.cursor

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
        bannerViewPager.adapter = BannerAdapter { position ->
            if (position == 0) {
                startActivity(Intent(this, BlankActivity::class.java))
            }
        }
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

class BannerAdapter(private val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
    private val items = listOf(
        BannerItem(
            "#4CAF50",
            "图片拼接",
            "将两张图片上下拼接成一张长图"
        ),
        BannerItem(
            "#2196F3",
            "更多功能",
            "敬请期待"
        )
    )

    data class BannerItem(
        val color: String,
        val title: String,
        val description: String
    )

    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.bannerImage)
        val titleView: TextView = view.findViewById(R.id.bannerTitle)
        val descriptionView: TextView = view.findViewById(R.id.bannerDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val item = items[position]
        holder.imageView.setBackgroundColor(Color.parseColor(item.color))
        holder.titleView.text = item.title
        holder.descriptionView.text = item.description
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount() = items.size
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