package com.example.treemeasure.cameras

import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {

    /** 图片地址路径 */
    var imagePath1: String = ""
    var imagePath2: String = ""

    /** 拍摄时的手机倾斜角 */
    var phoneAngle1: String = ""
    var phoneAngle2: String = ""

    /** 坡面倾斜角度 */
    var slopeAngle1: String = ""
    var slopeAngle2: String = ""

    /** 方位角 */
    var azimuth1: String = ""
    var azimuth2: String = ""

    /** 手机高度 */
    var phoneHeight1: String = ""
    var phoneHeight2: String = ""

}