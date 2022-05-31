/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.treemeasure.cameras


//class ImageViewerFragment : Fragment() {
//
//    /** AndroidX navigation arguments */
////    private val args: ImageViewerFragmentArgs by navArgs()
//
//    /** Default Bitmap decoding options */
//    private val bitmapOptions = BitmapFactory.Options().apply {
//        inJustDecodeBounds = false
//        // Keep Bitmaps at less than 1 MP
//        if (max(outHeight, outWidth) > DOWNSAMPLE_SIZE) {
//            val scaleFactorX = outWidth / DOWNSAMPLE_SIZE + 1
//            val scaleFactorY = outHeight / DOWNSAMPLE_SIZE + 1
//            inSampleSize = max(scaleFactorX, scaleFactorY)
//        }
//    }
//
//    /** Bitmap transformation derived from passed arguments */
//    private val bitmapTransformation: Matrix by lazy { decodeExifOrientation(args.orientation) }
//
//    /** Flag indicating that there is depth data available for this image */
//    private val isDepth: Boolean by lazy { args.depth }
//
//    /** Data backing our Bitmap viewpager */
//    private val bitmapList: MutableList<Bitmap> = mutableListOf()
//
//    private fun imageViewFactory() = ImageView(requireContext()).apply {
//        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View? = ViewPager2(requireContext()).apply {
//        Log.i("camera2", "ImageViewerFragment onCreateView()")
//
//
//        // Populate the ViewPager and implement a cache of two media items
//        // 填充查看应用程序并实现两个媒体项的缓存
//        offscreenPageLimit = 2
//        adapter = GenericListAdapter(
//            bitmapList,
//            itemViewFactory = { imageViewFactory() }) { view, item, _ ->
//            view as ImageView
//            Glide.with(view).load(item).into(view)
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        Log.i("camera2", "ImageViewerFragment onViewCreated()")
//
//
//        view as ViewPager2
//        lifecycleScope.launch(Dispatchers.IO) {
//
//            // Load input image file
//            val inputBuffer = loadInputBuffer()
//
//            // Load the main JPEG image
//            addItemToViewPager(view, decodeBitmap(inputBuffer, 0, inputBuffer.size))
//
//            // If we have depth data attached, attempt to load it
//            if (isDepth) {
//                try {
//                    val depthStart = findNextJpegEndMarker(inputBuffer, 2)
//                    addItemToViewPager(
//                        view, decodeBitmap(
//                            inputBuffer, depthStart, inputBuffer.size - depthStart
//                        )
//                    )
//
//                    val confidenceStart = findNextJpegEndMarker(inputBuffer, depthStart)
//                    addItemToViewPager(
//                        view, decodeBitmap(
//                            inputBuffer, confidenceStart, inputBuffer.size - confidenceStart
//                        )
//                    )
//
//                } catch (exc: RuntimeException) {
//                    Log.e(TAG, "Invalid start marker for depth or confidence data")
//                }
//            }
//        }
//    }
//
//    /**
//     * Utility function used to read input file into a byte array
//     * 用于将输入文件读入字节数组的实用程序函数
//     */
//    private fun loadInputBuffer(): ByteArray {
//        val inputFile = File(args.filePath)
//        return BufferedInputStream(inputFile.inputStream()).let { stream ->
//            ByteArray(stream.available()).also {
//                stream.read(it)
//                stream.close()
//            }
//        }
//    }
//
//    /** Utility function used to add an item to the viewpager and notify it, in the main thread */
//    private fun addItemToViewPager(view: ViewPager2, item: Bitmap) = view.post {
//        bitmapList.add(item)
//        view.adapter!!.notifyDataSetChanged()
//    }
//
//    /** Utility function used to decode a [Bitmap] from a byte array */
//    private fun decodeBitmap(buffer: ByteArray, start: Int, length: Int): Bitmap {
//
//        // Load bitmap from given buffer
//        val bitmap = BitmapFactory.decodeByteArray(buffer, start, length, bitmapOptions)
//
//        // Transform bitmap orientation using provided metadata
//        return Bitmap.createBitmap(
//            bitmap, 0, 0, bitmap.width, bitmap.height, bitmapTransformation, true
//        )
//    }
//
//    companion object {
//        private val TAG = ImageViewerFragment::class.java.simpleName
//
//        /** Maximum size of [Bitmap] decoded */
//        private const val DOWNSAMPLE_SIZE: Int = 1024  // 1MP
//
//        /** These are the magic numbers used to separate the different JPG data chunks */
//        private val JPEG_DELIMITER_BYTES = arrayOf(-1, -39)
//
//        /**
//         * Utility function used to find the markers indicating separation between JPEG data chunks
//         * 用于查找指示JPEG数据块之间分离的标记的实用程序函数
//         */
//        private fun findNextJpegEndMarker(jpegBuffer: ByteArray, start: Int): Int {
//
//            // Sanitize input arguments
//            assert(start >= 0) { "Invalid start marker: $start" }
//            assert(jpegBuffer.size > start) {
//                "Buffer size (${jpegBuffer.size}) smaller than start marker ($start)"
//            }
//
//            // Perform a linear search until the delimiter is found
//            // 执行线性搜索，直到找到分隔符为止
//            for (i in start until jpegBuffer.size - 1) {
//                if (jpegBuffer[i].toInt() == JPEG_DELIMITER_BYTES[0] &&
//                    jpegBuffer[i + 1].toInt() == JPEG_DELIMITER_BYTES[1]
//                ) {
//                    return i + 2
//                }
//            }
//
//            // If we reach this, it means that no marker was found
//            throw RuntimeException("Separator marker not found in buffer (${jpegBuffer.size})")
//        }
//    }
//}
