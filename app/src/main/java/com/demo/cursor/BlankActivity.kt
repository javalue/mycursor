package com.demo.cursor

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BlankActivity : AppCompatActivity() {
    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var previewImage: ImageView
    private lateinit var previewHint: TextView
    private lateinit var previewProgress: ProgressBar
    private lateinit var image1Card: CardView
    private lateinit var image2Card: CardView

    private var bitmap1: Bitmap? = null
    private var bitmap2: Bitmap? = null
    private var currentImageNumber = 1

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                loadImage(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        image1 = findViewById(R.id.image1)
        image2 = findViewById(R.id.image2)
        previewImage = findViewById(R.id.previewImage)
        previewHint = findViewById(R.id.previewHint)
        previewProgress = findViewById(R.id.previewProgress)
        image1Card = findViewById(R.id.image1Card)
        image2Card = findViewById(R.id.image2Card)
    }

    private fun setupClickListeners() {
        image1Card.setOnClickListener {
            currentImageNumber = 1
            openImagePicker()
        }

        image2Card.setOnClickListener {
            currentImageNumber = 2
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }

    private fun loadImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (currentImageNumber == 1) {
                bitmap1 = bitmap
                image1.setImageBitmap(bitmap)
            } else {
                bitmap2 = bitmap
                image2.setImageBitmap(bitmap)
            }

            // 如果两张图片都已选择，自动合并并预览
            if (bitmap1 != null && bitmap2 != null) {
                showPreview()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "加载图片失败", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPreview() {
        previewProgress.visibility = View.VISIBLE
        previewHint.visibility = View.GONE
        previewImage.visibility = View.GONE

        // 在后台线程中处理图片合并
        Thread {
            try {
                val mergedBitmap = mergeImages()
                runOnUiThread {
                    previewImage.setImageBitmap(mergedBitmap)
                    previewImage.visibility = View.VISIBLE
                    previewProgress.visibility = View.GONE
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "合并图片失败", Toast.LENGTH_SHORT).show()
                    previewProgress.visibility = View.GONE
                    previewHint.visibility = View.VISIBLE
                }
            }
        }.start()
    }

    private fun mergeImages(): Bitmap {
        // 获取屏幕宽度作为目标宽度
        val targetWidth = resources.displayMetrics.widthPixels

        // 调整第一张图片的大小
        val scaledBitmap1 = scaleBitmap(bitmap1!!, targetWidth)
        // 调整第二张图片的大小
        val scaledBitmap2 = scaleBitmap(bitmap2!!, targetWidth)

        // 计算总高度（包括过渡区域）
        val transitionHeight = 20 // 过渡区域高度
        val totalHeight = scaledBitmap1.height + scaledBitmap2.height + transitionHeight

        // 创建合并后的位图
        val mergedBitmap = Bitmap.createBitmap(targetWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mergedBitmap)

        // 绘制第一张图片
        canvas.drawBitmap(scaledBitmap1, 0f, 0f, null)

        // 绘制过渡区域
        val paint = Paint().apply {
            color = Color.WHITE
            alpha = 255
        }
        canvas.drawRect(0f, scaledBitmap1.height.toFloat(), 
                      targetWidth.toFloat(), (scaledBitmap1.height + transitionHeight).toFloat(), 
                      paint)

        // 绘制第二张图片
        canvas.drawBitmap(scaledBitmap2, 0f, (scaledBitmap1.height + transitionHeight).toFloat(), null)

        // 保存图片到相册
        saveToGallery(mergedBitmap)

        return mergedBitmap
    }

    private fun saveToGallery(bitmap: Bitmap) {
        try {
            // 创建DCIM目录下的文件
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_$timeStamp.jpg"
            val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val file = File(dcimDir, fileName)

            // 保存图片
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }

            // 通知相册更新
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = Uri.fromFile(file)
            sendBroadcast(mediaScanIntent)

            runOnUiThread {
                Toast.makeText(this, "图片已保存到相册", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(this, "保存图片失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scaleBitmap(bitmap: Bitmap, targetWidth: Int): Bitmap {
        val aspectRatio = bitmap.height.toFloat() / bitmap.width.toFloat()
        val targetHeight = (targetWidth * aspectRatio).toInt()
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }
} 