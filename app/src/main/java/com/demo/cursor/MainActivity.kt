package com.demo.cursor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.view.Gravity
import android.widget.LinearLayout
import android.view.ViewGroup
import android.widget.FrameLayout
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Space

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 创建根布局
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#F5F5F5"))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(32, 32, 32, 32)
        }

        // 创建标题文本
        val titleView = TextView(this).apply {
            text = "欢迎使用"
            textSize = 32f
            setTextColor(Color.parseColor("#333333"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 48
            }
        }

        // 创建副标题
        val subtitleView = TextView(this).apply {
            text = "这是一个示例应用"
            textSize = 18f
            setTextColor(Color.parseColor("#666666"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 32
            }
        }

        // 创建按钮
        val button = Button(this).apply {
            text = "开始使用"
            setBackgroundColor(Color.parseColor("#2196F3"))
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
                topMargin = 24
            }
        }

        // 添加所有视图到根布局
        rootLayout.addView(titleView)
        rootLayout.addView(subtitleView)
        rootLayout.addView(button)

        // 设置内容视图
        setContentView(rootLayout)
    }
}