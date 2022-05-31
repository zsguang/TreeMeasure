package com.example.treemeasure.treeCrown

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.android.camera.utils.decodeExifOrientation
import com.example.treemeasure.cameras.CameraViewModel
import com.example.treemeasure.Dao.AppDatabase
import com.example.treemeasure.Dao.TreeCrown
import com.example.treemeasure.Dao.TreeCrownDao
import com.example.treemeasure.MyApplication
import com.example.treemeasure.R
import com.example.treemeasure.data.CameraType
import com.example.treemeasure.databinding.FragmentCrownImageSaveBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedInputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import com.example.treemeasure.cameras.CameraActivity as CameraActivity


/**
 * 保存树冠图片和数据
 */
class CrownImageSaveFragment : Fragment(), View.OnClickListener, SensorEventListener {

    private var _binding: FragmentCrownImageSaveBinding? = null
    private val binding get() = _binding!!

//    private lateinit var viewModel: CrownImageSaveViewModel

    private val cameraActivity: CameraActivity by lazy { activity as CameraActivity }

    private val shareViewModel: CameraViewModel by lazy {
        (activity as CameraActivity).shareViewModel
    }

    /** 拍摄时间 */
    private lateinit var shootingDate: String

    /** AndroidX navigation arguments （Navigation代理） */
    private val args: CrownImageSaveFragmentArgs by navArgs()

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

    /** 图片是否已经保存成功 */
    private var saveSuccess: Boolean = false

    /** 传感器管理器 */
    private val systemService: SensorManager by lazy { requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    /** 加速度传感器 */
    private val sensorAccelerometer: Sensor by lazy { systemService.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    /** 标记哪个按钮打开加速度传感器 */
    private var sensorAccessFlag: String = ""

    /** DatabaseHelper对象，用于对数据库进行操作 */
//    private lateinit var dbHelper: DatabaseHelper

    /** ORM对象关系映像 数据访问对象 */
    private val treeCrownDao: TreeCrownDao by lazy {
        AppDatabase.getDatabase(MyApplication.context).treeCrownDao()
    }

    /** 加载图片 */
    private val INLOAD_IMAGE = 1

    /** 设置默认名 */
    private val EDIT_TREE_NAME_HINT = 2

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // 在这里可以进行UI操作
            when (msg.what) {
                INLOAD_IMAGE -> {
                    /** 加载第一张图 */
                    if (msg.arg1 == 1) {
                        binding.imageTree1.setImageBitmap(
                            decodeBitmap(msg.obj as ByteArray, 0, msg.arg2)
                        )
                    }
                    /** 加载第二张图 */
                    if (msg.arg1 == 2) {
                        binding.imageTree2.setImageBitmap(
                            decodeBitmap(msg.obj as ByteArray, 0, msg.arg2)
                        )
                    }
                }
                EDIT_TREE_NAME_HINT -> {
                    binding.editTreeName.hint = "未命名" + msg.arg1.toString()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "CrownImageSaveFragment onViewCreated()")

        /** 重新加载，需要清除相应的手机高度和坡面斜度数据，以防直接使用上一张图的数据而造成误差 */
        when (cameraActivity.cameraType) {
            CameraType.TreeCrownType1 -> {
                shareViewModel.phoneHeight1 = ""
                shareViewModel.slopeAngle1 = ""
            }
            CameraType.TreeCrownType2 -> {
                shareViewModel.phoneHeight2 = ""
                shareViewModel.slopeAngle2 = ""
            }
        }
        initUi()
    }

    @SuppressLint("SimpleDateFormat")
    private fun initUi() {
        Log.i(TAG, "CrownImageSaveFragment initUi()")
        binding.imageTree1.setOnClickListener(this)
        binding.imageTree2.setOnClickListener(this)
        binding.btnPhoneHeight1.setOnClickListener(this)
        binding.btnPhoneHeight2.setOnClickListener(this)
        binding.btnSlopeAngle1.setOnClickListener(this)
        binding.btnSlopeAngle2.setOnClickListener(this)
        binding.btnCrownConfirm.setOnClickListener(this)
        binding.btnCrownCancel.setOnClickListener(this)
        shootingDate = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date())
        binding.textShootingDate.text = shootingDate
        binding.editPhoneHeight1.setText(shareViewModel.phoneHeight1)
        binding.editPhoneHeight2.setText(shareViewModel.phoneHeight2)
        binding.editSlopeAngle1.setText(shareViewModel.slopeAngle1)
        binding.editSlopeAngle2.setText(shareViewModel.slopeAngle2)
        binding.textPhoneAngle1.text = shareViewModel.phoneAngle1
        binding.textPhoneAngle2.text = shareViewModel.phoneAngle2
        binding.textAzimuth1.text = shareViewModel.azimuth1
        binding.textAzimuth2.text = shareViewModel.azimuth2
        /* 异步更新editTreeName.hint、图片1、图片2  */
        lifecycleScope.launch(Dispatchers.IO) {
            /** 加载editTreeName的提示 */
            val maxId: Int = treeCrownDao.getMaxId()
            binding.editTreeName.hint = "未命名$maxId"

            Log.d(TAG, "CameraType:" + cameraActivity.cameraType)
            Log.d(TAG, "shareViewModel.imagePath1:" + shareViewModel.imagePath1)
            Log.d(TAG, "shareViewModel.imagePath2:" + shareViewModel.imagePath2)

            /** 加载图片1 */
            if (shareViewModel.imagePath1 != "") {
                val inputBuffer = loadInputBuffer(shareViewModel.imagePath1)
                val message = Message().apply {
                    what = INLOAD_IMAGE
                    arg1 = 1
                    arg2 = inputBuffer.size
                    obj = inputBuffer
                }
                handler.sendMessage(message)
            }
            /** 加载图片2 */
            if (shareViewModel.imagePath2 != "") {
                val inputBuffer = loadInputBuffer(shareViewModel.imagePath2)
                val message = Message().apply {
                    what = INLOAD_IMAGE
                    arg1 = 2
                    arg2 = inputBuffer.size
                    obj = inputBuffer
                }
                handler.sendMessage(message)
            }
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            /******************* 点击图片2 *******************/
            R.id.imageTree1 -> {
                /** 返回相机预览界面，并修改Activity的CameraType指明拍摄图片1 */
                cameraActivity.cameraType = CameraType.TreeCrownType1
                Navigation.findNavController(requireActivity(), R.id.fragment_container)
                    .navigate(CrownImageSaveFragmentDirections.actionCrownImageSaveFragmentToCameraFragment(
                        CameraCharacteristics.LENS_FACING_FRONT.toString(), ImageFormat.JPEG)
                        .setCrownImageNumber("crownImage1")
                    )
            }

            /******************* 点击图片2 *******************/
            R.id.imageTree2 -> {
                /** 返回相机预览界面，并修改Activity的CameraType指明拍摄图片2 */
                cameraActivity.cameraType = CameraType.TreeCrownType2
                Navigation.findNavController(requireActivity(), R.id.fragment_container)
                    .navigate(CrownImageSaveFragmentDirections.actionCrownImageSaveFragmentToCameraFragment(
                        CameraCharacteristics.LENS_FACING_FRONT.toString(), ImageFormat.JPEG)
                        .setCrownImageNumber("crownImage2")
                    )
            }

            /******************* 点击图片1的手机高度测量 *******************/
            R.id.btn_phoneHeight1 -> {
                if (shareViewModel.imagePath1 == "") {
                    Toast.makeText(activity, "请先拍摄图片！", Toast.LENGTH_SHORT).show()
                    return
                }
                // 如果测试完成，将数据记录在shareViewModel上
                if (accessSensorClick(AccessSensorType.PhoneHeight1)) {
                    shareViewModel.phoneHeight1 = binding.editPhoneHeight1.text.toString()
                }
            }

            /******************* 点击图片2的手机高度测量 *******************/
            R.id.btn_phoneHeight2 -> {
                if (shareViewModel.imagePath2 == "") {
                    Toast.makeText(activity, "请先拍摄图片！", Toast.LENGTH_SHORT).show()
                    return
                }
                // 如果测试完成，将数据记录在shareViewModel上
                if (accessSensorClick(AccessSensorType.PhoneHeight2)) {
                    shareViewModel.phoneHeight2 = binding.editPhoneHeight2.text.toString()
                }
            }

            /******************* 点击图片1的坡面倾斜角度测量 *******************/
            R.id.btn_slopeAngle1 -> {
                if (shareViewModel.imagePath1 == "") {
                    Toast.makeText(activity, "请先拍摄图片！", Toast.LENGTH_SHORT).show()
                    return
                }
                if (accessSensorClick(AccessSensorType.SlopeAngle1)) {
                    shareViewModel.slopeAngle1 = binding.editSlopeAngle1.text.toString()
                }
            }

            /******************* 点击图片2的坡面倾斜角度测量 *******************/
            R.id.btn_slopeAngle2 -> {
                if (shareViewModel.imagePath2 == "") {
                    Toast.makeText(activity, "请先拍摄图片！", Toast.LENGTH_SHORT).show()
                    return
                }
                if (accessSensorClick(AccessSensorType.SlopeAngle2)) {
                    shareViewModel.slopeAngle2 = binding.editSlopeAngle2.text.toString()
                }
            }

            /******************* 点击确认按钮 *******************/
            R.id.btn_crown_confirm -> {
                if (!dataAvailable()) {
                    Toast.makeText(activity, "请输入必要信息！", Toast.LENGTH_SHORT).show()
                    return
                }
                // 获取需要更改的数据名字
                var dataName: String = binding.editTreeName.text.toString()
                if (dataName == "") dataName = binding.editTreeName.hint.toString()

                // 找出照片路径上的最后一个‘/’的位置
                val length1: Int = findBiasIndex(shareViewModel.imagePath1)
                val length2: Int = findBiasIndex(shareViewModel.imagePath2)
                // 形成更改后的完整文件路径
                val newPath1: String =
                    shareViewModel.imagePath1.substring(0,
                        length1 + 1) + dataName + "_CrownImage1.jpg"
                val newPath2: String =
                    shareViewModel.imagePath2.substring(0,
                        length2 + 1) + dataName + "_CrownImage2.jpg"

                // 检查该命名是否已存在
                var checkTreeName: Int = 1
                runBlocking {
                    checkTreeName = treeCrownDao.checkTreeName(dataName)
                }
                // 若文件名已存在，则保存失败
                if (checkTreeName != 0) {
                    Toast.makeText(activity, "文件名字已存在！", Toast.LENGTH_SHORT).show()
                    return
                }
                // 文件命名合法，保存数据到数据库中
                lifecycleScope.launch(Dispatchers.IO) {
                    val data = TreeCrown(
                        dataName,
                        0.0, 0.0,
                        shootingDate,

                        shareViewModel.phoneAngle1,
                        binding.editSlopeAngle1.text.toString(),
                        binding.editPhoneHeight1.text.toString(),
                        shareViewModel.azimuth1,
                        newPath1,
                        "0", "0",

                        shareViewModel.phoneAngle2,
                        binding.editSlopeAngle2.text.toString(),
                        binding.editPhoneHeight2.text.toString(),
                        shareViewModel.azimuth2,
                        newPath2,
                        "0", "0"
                    )
                    treeCrownDao.insertTreeCrown(data)
                }

                // 更改文件名字
                if (File(shareViewModel.imagePath1).renameTo(File(newPath1))
                    && File(shareViewModel.imagePath2).renameTo(File(newPath2))
                ) {
                    saveSuccess = true
                    Toast.makeText(MyApplication.context, "保存成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(MyApplication.context, "保存失败", Toast.LENGTH_SHORT).show()
                    return
                }

                // 以栈内复用模式打开TreeCrownActivity
                val intent = Intent(activity, TreeCrownActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

            /******************* 点击取消按钮 *******************/
            R.id.btn_crown_cancel -> {
                // 删除照片
                File(shareViewModel.imagePath1).delete()
                File(shareViewModel.imagePath2).delete()

                // 以栈内复用模式打开TreeCrownActivity
                val intent = Intent(activity, TreeCrownActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }

    /** 判断必要的数据是否已经全部输入 */
    private fun dataAvailable(): Boolean =
        binding.editPhoneHeight1.text.toString() != "" && binding.editPhoneHeight2.text.toString() != ""
                && binding.editSlopeAngle1.text.toString() != "" && binding.editSlopeAngle2.text.toString() != ""
                && shareViewModel.imagePath1 != "" && shareViewModel.imagePath2 != ""

    /**
     * 找出照片路径上的最后一个‘/’字符的位置
     */
    private fun findBiasIndex(imagePath: String): Int {
        var length = imagePath.length
        for (i in length - 1 downTo 0) {
            if (imagePath[i] == '/') {
                length = i
                break
            }
        }
        return length
    }

    private fun accessSensorClick(accessSensorType: String): Boolean {
        when (sensorAccessFlag) {
            /** 如果加速度传感器未打开 */
            "" -> {
                sensorAccessFlag = accessSensorType
                /** 如果用于测量手机高度,需要初始化一些用用于计算的数据 */
                if (accessSensorType == AccessSensorType.PhoneHeight1 || accessSensorType == AccessSensorType.PhoneHeight2) {
                    speed = floatArrayOf(0f, 0f, 0f)
                    shift = floatArrayOf(0f, 0f, 0f)
                }
                // 注册加速度传感器
                systemService.registerListener(
                    this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL
                )
                // 测试未完成，返回false
                return false
            }
            /** 如果正在测试手机高度1 */
            accessSensorType -> {
                sensorAccessFlag = ""
                // 注销加速度传感器
                systemService.unregisterListener(this)
                // 测试完成，返回true
                return true
            }
            /** 正在进行其他测试 */
            else -> {
                Toast.makeText(activity, "正在进行其他测量", Toast.LENGTH_SHORT).show()
                // 测试未完成，返回false
                return false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_tree_crown_image_save, container, false)
        _binding = FragmentCrownImageSaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Utility function used to read input file into a byte array
     * 用于将输入文件读入字节数组的实用程序函数
     */
    private fun loadInputBuffer(filePath: String): ByteArray {
        Log.d("HeightImageSaveFragment", "loadInputBuffer()")
        val inputFile = File(filePath)
        return BufferedInputStream(inputFile.inputStream()).let { stream ->
            ByteArray(stream.available()).also {
                stream.read(it)
                stream.close()
            }
        }
    }

    /** Utility function used to decode a [Bitmap] from a byte array */
    private fun decodeBitmap(buffer: ByteArray, start: Int, length: Int): Bitmap {
//        Log.d("HeightImageSaveFragment", "BitmapFactory.decodeByteArray begin")

        // Load bitmap from given buffer
        val bitmap = BitmapFactory.decodeByteArray(buffer, start, length, bitmapOptions)

//        Log.d("HeightImageSaveFragment", "BitmapFactory.decodeByteArray end")

        // Transform bitmap orientation using provided metadata
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, bitmapTransformation, true
        )
    }

    private var speed = floatArrayOf(0f, 0f, 0f)
    private var shift = floatArrayOf(0f, 0f, 0f)

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                when (sensorAccessFlag) {
                    AccessSensorType.SlopeAngle1 -> {
                        binding.editSlopeAngle1.setText(
                            String.format("%.2f", Math.acos(z / 10.0) * 180 / Math.PI) + "°")
                    }
                    AccessSensorType.SlopeAngle2 -> {
                        binding.editSlopeAngle2.setText(
                            String.format("%.2f", Math.acos(z / 10.0) * 180 / Math.PI) + "°")
                    }
                    AccessSensorType.PhoneHeight1 -> {
                        speed[0] += x * 0.02f //速度分量，0.2f对应采样周期，单位s //normal延迟时间200ms
                        speed[1] += y * 0.02f
                        speed[2] += z * 0.02f

                        shift[0] += speed[0] * 0.02f //位移分量
                        shift[1] += speed[1] * 0.02f
                        shift[2] += speed[2] * 0.02f

                        binding.editPhoneHeight1.setText(String.format("%.2f", shift[1]))
                    }
                    AccessSensorType.PhoneHeight2 -> {
                        speed[0] += x * 0.02f //速度分量，0.2f对应采样周期，单位s //normal延迟时间200ms
                        speed[1] += y * 0.02f
                        speed[2] += z * 0.02f

                        shift[0] += speed[0] * 0.02f //位移分量
                        shift[1] += speed[1] * 0.02f
                        shift[2] += speed[2] * 0.02f

                        binding.editPhoneHeight2.setText(String.format("%.2f", shift[1]))
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 未点击确认保存图片就退出，则不保存该图片
//        if (!saveSuccess) {
//            File(shareViewModel.imagePath1).delete()
//            File(shareViewModel.imagePath2).delete()
//        }
        _binding = null
    }

    companion object {
        private val TAG = CrownImageSaveFragment::class.java.simpleName

        /** Maximum size of [Bitmap] decoded */
        private const val DOWNSAMPLE_SIZE: Int = 1024  // 1MP

        /** These are the magic numbers used to separate the different JPG data chunks */
        private val JPEG_DELIMITER_BYTES = arrayOf(-1, -39)
    }
}

/** 用于记录是哪个按打开了加速度传感器 */
object AccessSensorType {
    const val PhoneHeight1: String = "PhoneHeight1"
    const val PhoneHeight2: String = "PhoneHeight2"
    const val SlopeAngle1: String = "SlopeAngle1"
    const val SlopeAngle2: String = "SlopeAngle2"
}