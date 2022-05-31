package com.example.treemeasure.cameras

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.treemeasure.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {

    private lateinit var activityCameraBinding: ActivityCameraBinding

    /** 启用相机的用途，如用来拍摄树高树冠或树冠或树种识别 */
    lateinit var cameraType: String

    /** 共享viewModel,主要用于在Fragment之间传递信息 */
    lateinit var shareViewModel: CameraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCameraBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(activityCameraBinding.root)

        cameraType = intent.getStringExtra("cameraType").toString()

        shareViewModel = ViewModelProvider(this).get(CameraViewModel::class.java)

        Log.i("CameraActivity", "CameraActivity onCreate()")
    }

    override fun onResume() {
        super.onResume()
        // Before setting full screen flags, we must wait a bit to let UI settle; otherwise, we may
        // be trying to set app to immersive mode before it's ready and the flags do not stick
        // 在设置全屏标志之前，我们必须等待一点时间，让UI解决;
        // 否则，我们可能会尝试在应用程序准备好之前设置为沉浸式模式，而标志不会粘住
        activityCameraBinding.fragmentContainer.postDelayed({
            activityCameraBinding.fragmentContainer.systemUiVisibility = FLAGS_FULLSCREEN
        }, IMMERSIVE_FLAG_TIMEOUT)
    }

    companion object {
        /** Combination of all flags required to put activity into immersive mode
         * 将活动置于沉浸式模式所需的所有标志的组合
         */
        const val FLAGS_FULLSCREEN =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        /** Milliseconds used for UI animations 用于UI动画的毫秒  */
        const val ANIMATION_FAST_MILLIS = 50L
        const val ANIMATION_SLOW_MILLIS = 100L
        private const val IMMERSIVE_FLAG_TIMEOUT = 500L
    }
}
