package com.example.treemeasure.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraCharacteristics
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.android.camera.utils.decodeExifOrientation
import com.example.treemeasure.Dao.AppDatabase
import com.example.treemeasure.Dao.TreeHeight
import com.example.treemeasure.Dao.TreeHeightDao
import com.example.treemeasure.MyApplication
import com.example.treemeasure.R
import com.example.treemeasure.databinding.FragmentImageSaveBinding
import com.example.treemeasure.treeHeight.TreeHeightActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedInputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max


/**
 * 图片保存页面
 */
class ImageSaveFragment : Fragment(), View.OnClickListener, SensorEventListener {

    /** AndroidX navigation arguments （Navigation代理） */
    private val args: ImageSaveFragmentArgs by navArgs()

    private var _binding: FragmentImageSaveBinding? = null
    private val binding get() = _binding!!

    /** Default Bitmap decoding options */
    private val bitmapOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = false
        // Keep Bitmaps at less than 1 MP
        if (max(outHeight, outWidth) > DOWNSAMPLE_SIZE) {
            val scaleFactorX = outWidth / DOWNSAMPLE_SIZE + 1
            val scaleFactorY = outHeight / DOWNSAMPLE_SIZE + 1
            inSampleSize = max(scaleFactorX, scaleFactorY)
        }
    }

    /** Bitmap transformation derived from passed arguments */
    private val bitmapTransformation: Matrix by lazy { decodeExifOrientation(args.orientation) }

    /** 传感器管理器 */
    private val systemService: SensorManager by lazy { requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    /** 加速度传感器 */
    private val sensorAccelerometer: Sensor by lazy { systemService.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    /** 标记哪个按钮打开加速度传感器 */
    private var sensorAccessFlag: String = ""

    /**  图片路径  */
    private val filePath: String by lazy { args.filePath }

    /** DatabaseHelper对象，用于对数据库进行操作 */
//    private lateinit var dbHelper: DatabaseHelper

    /** 拍摄时间 */
    private lateinit var shootingDate: String

    /** 图片是否已经保存成功 */
    private var saveSuccess: Boolean = false

    /** 加载图片 */
    private val INLOAD_IMAGE = 1

    /** 设置默认名 */
    private val EDITTREENAME_HINT = 2

    /** ORM对象关系映像 数据访问对象 */
    private val treeHeightDao: TreeHeightDao by lazy {
        AppDatabase.getDatabase(MyApplication.context).treeHeightDao()
    }

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // 在这里可以进行UI操作
            when (msg.what) {
                INLOAD_IMAGE -> {
                    binding.imageTree.setImageBitmap(decodeBitmap(msg.obj as ByteArray,
                        msg.arg1,
                        msg.arg2))
                }
                EDITTREENAME_HINT -> {
                    binding.editTreeName.hint = "未命名" + msg.arg1.toString()
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        dbHelper = DatabaseHelper(MyApplication.context, "MeasureTree.db", 1)
//        val db = dbHelper.writableDatabase
//        val cursor = db.rawQuery("select * from TreeHeight order by id desc limit 1", null)
//        if (cursor.moveToFirst())
//            binding.editTreeName.hint = "未命名" + cursor.getInt(cursor.getColumnIndex("id"))
//        else binding.editTreeName.hint = "未命名0"
//
//        cursor.close()
//        db.close()

//        binding.editTreeName.hint = "未命名" + treeHeightDao.getMaxId()
        lifecycleScope.launch(Dispatchers.IO) {
            val maxId: Int = treeHeightDao.getMaxId()
            handler.sendMessage(Message().apply {
                what = EDITTREENAME_HINT
                arg1 = maxId
            })
        }

//        binding.textLowerAngle.text = args.bottomAngleValue
//        binding.textTopAngle.text = args.topAngleValue
        binding.textPhoneAngle.text = args.phoneAngleValue
        binding.btnConfirm.setOnClickListener(this)
        binding.btnCancle.setOnClickListener(this)
        binding.btnPhoneHeight.setOnClickListener(this)
        binding.btnSlopeAngle.setOnClickListener(this)

        shootingDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        binding.textShootingDate.text = shootingDate


        lifecycleScope.launch(Dispatchers.IO) {
            // Load input image file
            val inputBuffer = loadInputBuffer()
            Log.d("ImageSaveFragment", "loadInputBuffer() end")

            // Load the main JPEG image
            val message = Message().apply {
                what = INLOAD_IMAGE
                arg1 = 0
                arg2 = inputBuffer.size
                obj = inputBuffer
            }
            handler.sendMessage(message)
        }

//        binding.editPhoneHeight.setOn
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentImageSaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            /**  确认按钮事件   */
            R.id.btn_confirm -> {
                if (binding.editPhoneHeight.text.toString() == "" || binding.editSlopeAngle.text.toString() == "") {
                    Toast.makeText(activity, "请输入必要信息！", Toast.LENGTH_SHORT).show()
                    return
                }

                // 获取需要更改的文件名
                var name: String = binding.editTreeName.text.toString()
                if (name == "") name = binding.editTreeName.hint.toString()

                // 找出照片路径
                var length: Int = filePath.length
                for (i in filePath.length - 1 downTo 0) {
                    if (filePath.get(i) == '/') {
                        length = i
                        break
                    }
                }
                // 形成更改后的完整文件路径
                val newPath: String = filePath.substring(0, length + 1) + name + ".jpg"

//                var db = dbHelper.writableDatabase
//                val cursor = db.rawQuery("select * from TreeHeight where name = '$name'", null)
//                // 若已存在记录，说明名字已存在
//                if (cursor.moveToFirst()) {
//                    Toast.makeText(activity, "文件名字已存在！", Toast.LENGTH_SHORT).show()
////                    Toast.makeText(activity, "${cursor.count}", Toast.LENGTH_SHORT).show()
//                    return
//                }
//                cursor.close()
                var checkTreeName: Int = 1
                runBlocking {
                    checkTreeName = treeHeightDao.checkTreeName(name)
                }
                if (checkTreeName != 0) {   //若文件名已存在，则保存失败
                    Toast.makeText(activity, "文件名字已存在！", Toast.LENGTH_SHORT).show()
                    return
                }

                runBlocking {
                    val data = TreeHeight(
                        name,
                        args.phoneAngleValue,
                        binding.editSlopeAngle.text.toString(),
                        binding.editPhoneHeight.text.toString(),
                        shootingDate,
                        newPath,
                        "0",
                        "0"
                    )
                    treeHeightDao.insertTreeHeight(data)
                }

//                db = dbHelper.writableDatabase
//                // 开启事务
//                db.beginTransaction()
//                try {
//                    val values = ContentValues().apply {
//                        put("name", name)
//                        put("bottomAngle", args.bottomAngleValue)
//                        put("topAngle", args.topAngleValue)
//                        put("shootingDate", shootingDate)
//                        put("filePath", newPath)
//                        put("heightValue", "0")
//                    }
//                    db.insert("TreeHeight", null, values)
//                    db.setTransactionSuccessful()
////                    db.execSQL("insert into TreeHeight (name, bottomAngle, topAngle, shootingDate, filePath) values(?,?,?,?,?)",
////                        arrayOf(name,
////                            args.bottomAngleValue,
////                            args.topAngleValue,
////                            shootingDate,
////                            newPath))
//                } catch (e: Exception) {
//                    Toast.makeText(MyApplication.context, "数据出错，插入失败！", Toast.LENGTH_LONG).show()
//                    return
//                } finally {
//                    db.endTransaction()
//                    db.close()
//                }

                // 更改文件名字
                if (File(filePath).renameTo(File(newPath))) {
                    saveSuccess = true
                    Toast.makeText(MyApplication.context, "保存成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "保存失败", Toast.LENGTH_SHORT).show()
                    return
                }

                // 以栈内复用模式打开TreeHeightActivity
                val intent = Intent(activity, TreeHeightActivity::class.java)
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

            /**   取消按钮事件   */
            R.id.btn_cancle -> {
                // 删除照片
                val file = File(filePath)
                file.delete()

                // 返回相机预览界面
                Navigation.findNavController(requireActivity(), R.id.fragment_container)
                    .navigate(ImageSaveFragmentDirections.actionImageSaveFragmentToCameraFragment(
                        CameraCharacteristics.LENS_FACING_FRONT.toString(),
                        ImageFormat.JPEG))
            }

            /** 手机高度测量 */
            R.id.btn_phoneHeight -> {
                when (sensorAccessFlag) {
                    // 如果正在测试坡面斜度，不触发
                    "slopeAngle" -> {
                        Toast.makeText(activity, "正在进行坡面倾斜角度测量", Toast.LENGTH_SHORT).show()
                        return
                    }
                    // 如果正在测试手机高度
                    "phoneHeight" -> {
                        sensorAccessFlag = ""
                        // 注销加速度传感器
                        systemService.unregisterListener(this)
                    }
                    // 未进行测试
                    "" -> {
                        sensorAccessFlag = "phoneHeight"
                        // 注册加速度传感器
                        systemService.registerListener(
                            this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL
                        )
                    }
                }
            }

            /** 坡面倾斜角度测量 */
            R.id.btn_slopeAngle -> {
                when (sensorAccessFlag) {
                    // 如果正在测试手机高度，不触发
                    "phoneHeight" -> {
                        Toast.makeText(activity, "正在进行手机高度测量", Toast.LENGTH_SHORT).show()
                        return
                    }
                    // 如果正在测试坡面斜度
                    "slopeAngle" -> {
                        sensorAccessFlag = ""
                        // 注销加速度传感器
                        systemService.unregisterListener(this)
                    }
                    // 未进行测试
                    "" -> {
                        sensorAccessFlag = "slopeAngle"
                        speed = floatArrayOf(0f, 0f, 0f)
                        shift = floatArrayOf(0f, 0f, 0f)
                        // 注册加速度传感器
                        systemService.registerListener(
                            this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL
                        )
                    }
                }
            }

        }

    }


    /**
     * Utility function used to read input file into a byte array
     * 用于将输入文件读入字节数组的实用程序函数
     */
    private fun loadInputBuffer(): ByteArray {
        Log.d("ImageSaveFragment", "loadInputBuffer()")
        val inputFile = File(args.filePath)
        return BufferedInputStream(inputFile.inputStream()).let { stream ->
            ByteArray(stream.available()).also {
                stream.read(it)
                stream.close()
            }
        }
    }

    /** Utility function used to decode a [Bitmap] from a byte array */
    private fun decodeBitmap(buffer: ByteArray, start: Int, length: Int): Bitmap {
        Log.d("ImageSaveFragment", "BitmapFactory.decodeByteArray begin")

        // Load bitmap from given buffer
        val bitmap = BitmapFactory.decodeByteArray(buffer, start, length, bitmapOptions)

        Log.d("ImageSaveFragment", "BitmapFactory.decodeByteArray end")

        // Transform bitmap orientation using provided metadata
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, bitmapTransformation, true
        )
    }

    companion object {

        private val TAG = ImageViewerFragment::class.java.simpleName

        /** Maximum size of [Bitmap] decoded */
        private const val DOWNSAMPLE_SIZE: Int = 1024  // 1MP

        /** These are the magic numbers used to separate the different JPG data chunks */
        private val JPEG_DELIMITER_BYTES = arrayOf(-1, -39)

        /**
         * Utility function used to find the markers indicating separation between JPEG data chunks
         * 用于查找指示JPEG数据块之间分离的标记的实用程序函数
         */
        private fun findNextJpegEndMarker(jpegBuffer: ByteArray, start: Int): Int {

            // Sanitize input arguments
            assert(start >= 0) { "Invalid start marker: $start" }
            assert(jpegBuffer.size > start) {
                "Buffer size (${jpegBuffer.size}) smaller than start marker ($start)"
            }

            // Perform a linear search until the delimiter is found
            // 执行线性搜索，直到找到分隔符为止
            for (i in start until jpegBuffer.size - 1) {
                if (jpegBuffer[i].toInt() == JPEG_DELIMITER_BYTES[0] &&
                    jpegBuffer[i + 1].toInt() == JPEG_DELIMITER_BYTES[1]
                ) {
                    return i + 2
                }
            }

            // If we reach this, it means that no marker was found
            throw RuntimeException("Separator marker not found in buffer (${jpegBuffer.size})")
        }
    }

    private var speed = floatArrayOf(0f, 0f, 0f)
    private var shift = floatArrayOf(0f, 0f, 0f)
    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                if (sensorAccessFlag == "slopeAngle")
                    binding.editSlopeAngle.setText(
                        String.format("%.2f", Math.acos(z / 10.0) * 180 / Math.PI) + "°")
                else if (sensorAccessFlag == "phoneHeight") {
                    speed[0] += x * 0.02f //速度分量，0.2f对应采样周期，单位s //normal延迟时间200ms
                    speed[1] += y * 0.02f
                    speed[2] += z * 0.02f

                    shift[0] += speed[0] * 0.02f //位移分量
                    shift[1] += speed[1] * 0.02f
                    shift[2] += speed[2] * 0.02f

                    binding.editPhoneHeight.setText(String.format("%.2f", shift[1]))
//                    val accelerometer = """
//                        acceleration
//                        X：${event.values[0]}
//                        Y:${event.values[1]}
//                        Z:${event.values[2]}
//
//                        velocity
//                        X：${speed[0]}
//                        Y:${speed[1]}
//                        Z:${speed[2]}
//
//                        shift
//                        X：${shift[0]}
//                        Y:${shift[1]}
//                        Z:${shift[2]}
//
//                        """.trimIndent()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onPause() {
        super.onPause()
        sensorAccessFlag = ""
        // 注销加速度传感器
        systemService.unregisterListener(this)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // 未点击确认保存图片就退出，则不保存该图片
        if (!saveSuccess) File(filePath).delete()
        _binding = null
    }
}