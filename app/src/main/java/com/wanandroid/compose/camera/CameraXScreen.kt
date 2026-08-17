package com.wanandroid.compose.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.RectF
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.camera.view.TransformExperimental
import androidx.camera.view.transform.CoordinateTransform
import androidx.camera.view.transform.ImageProxyTransformFactory
import androidx.camera.view.transform.OutputTransform
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.roundToInt

@Composable
fun CameraScreen3(
    modifier: Modifier = Modifier,
    onCaptured: (Bitmap) -> Unit
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        logCameraSizes(context)
    }

    val cameraExecutor = remember {
        ContextCompat.getMainExecutor(context)
    }

    var previewView by remember {
        mutableStateOf<PreviewView?>(null)
    }

    var imageCapture by remember {
        mutableStateOf<ImageCapture?>(null)
    }

    DisposableEffect(Unit) {
        onDispose {
//            cameraExecutor.shutdown()
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),

            factory = { ctx ->

                PreviewView(ctx).apply {

                    previewView = this

                    scaleType = PreviewView.ScaleType.FILL_CENTER

                    implementationMode =
                        PreviewView.ImplementationMode.COMPATIBLE
                }
            }
        )

        // 身份证框和蒙层
        CameraScanBorder(
            modifier = Modifier.fillMaxSize()
        )

        // 拍照按钮
        Button(
            onClick = {

                val preview = previewView
                    ?: return@Button

                val capture = imageCapture
                    ?: return@Button

                takePictureAndCrop(
                    previewView = preview,
                    imageCapture = capture,
                    executor = cameraExecutor,
                    onSuccess = onCaptured
                )
            }
        ) {
            Text("拍照")
        }
    }

    LaunchedEffect(
        previewView,
        lifecycleOwner
    ) {

        val preview = previewView
            ?: return@LaunchedEffect

        startCamera(
            context = context,
            lifecycleOwner = lifecycleOwner,
            previewView = preview,
            onImageCaptureReady = {
                imageCapture = it
            }
        )
    }
}

private fun startCamera(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    previewView: PreviewView,
    onImageCaptureReady: (ImageCapture) -> Unit
) {

    val cameraProviderFuture =
        ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({

        val cameraProvider =
            cameraProviderFuture.get()

        val preview =
            Preview.Builder()
//                .setResolutionSelector(
//                    ResolutionSelector.Builder()
//                        .setAspectRatioStrategy(
//                            AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
//                        ).build())
                .build()
                .also {
                    it.surfaceProvider =
                        previewView.surfaceProvider
                }

        val imageCapture =
            ImageCapture.Builder()
                .setCaptureMode(
                    ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
                )
                .setResolutionSelector(
                    ResolutionSelector.Builder()
//                        .setAspectRatioStrategy(
//                            AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
//                        )
                        .setResolutionStrategy(
                            ResolutionStrategy(
                                android.util.Size(2336, 1080),
                                ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                            )
                        )
                        .build()
                )
                .build()

        val cameraSelector =
            CameraSelector.DEFAULT_BACK_CAMERA

        try {

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
//                preview,
                imageCapture
            )

            Log.d(
                "CameraScreen",
                "ImageCapture attached resolution = " +
                        imageCapture.attachedSurfaceResolution)

            onImageCaptureReady(
                imageCapture
            )

        } catch (e: Exception) {

            Log.e(
                "CameraScreen",
                "Camera bind failed",
                e
            )
        }

    }, ContextCompat.getMainExecutor(context))
}

@Composable
private fun IdCardOverlay(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
    ) {

        val cardWidth = size.width * 0.86f
        val cardHeight = cardWidth / 1.586f

        val left = (size.width - cardWidth) / 2f
        val top = (size.height - cardHeight) / 2f

        val right = left + cardWidth
        val bottom = top + cardHeight

        val overlayColor = Color.Black.copy(
            alpha = 0.55f
        )

        // 上方蒙层
        drawRect(
            color = overlayColor,
            topLeft = Offset(
                x = 0f,
                y = 0f
            ),
            size = androidx.compose.ui.geometry.Size(
                width = size.width,
                height = top
            )
        )

        // 左侧蒙层
        drawRect(
            color = overlayColor,
            topLeft = Offset(
                x = 0f,
                y = top
            ),
            size = androidx.compose.ui.geometry.Size(
                width = left,
                height = cardHeight
            )
        )

        // 右侧蒙层
        drawRect(
            color = overlayColor,
            topLeft = Offset(
                x = right,
                y = top
            ),
            size = androidx.compose.ui.geometry.Size(
                width = size.width - right,
                height = cardHeight
            )
        )

        // 下方蒙层
        drawRect(
            color = overlayColor,
            topLeft = Offset(
                x = 0f,
                y = bottom
            ),
            size = androidx.compose.ui.geometry.Size(
                width = size.width,
                height = size.height - bottom
            )
        )

        // 身份证白色边框
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(
                x = left,
                y = top
            ),
            size = androidx.compose.ui.geometry.Size(
                width = cardWidth,
                height = cardHeight
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                x = 16.dp.toPx(),
                y = 16.dp.toPx()
            ),
            style = Stroke(
                width = 3.dp.toPx()
            )
        )
    }
}

private fun takePictureAndCrop(
    previewView: PreviewView,
    imageCapture: ImageCapture,
    executor: Executor,
    onSuccess: (Bitmap) -> Unit
) {

    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {

            override fun onCaptureSuccess(
                image: ImageProxy
            ) {

                try {
                    val b = image.toBitmap()
                    print(b)
                    val cropBitmap =
                        cropToPreviewRect(
                            previewView = previewView,
                            imageProxy = image
                        )

                    onSuccess(
                        cropBitmap
                    )

                } catch (e: Exception) {

                    Log.e(
                        "CameraScreen",
                        "Crop failed",
                        e
                    )

                } finally {

                    image.close()
                }
            }

            override fun onError(
                exception: ImageCaptureException
            ) {

                Log.e(
                    "CameraScreen",
                    "Capture failed",
                    exception
                )
            }
        }
    )
}

@OptIn(TransformExperimental::class)
private fun cropToPreviewRect(
    previewView: PreviewView,
    imageProxy: ImageProxy
): Bitmap {

    /*
     * 1.
     * 获取 PreviewView 的 OutputTransform
     *
     * 这个 Transform 描述：
     *
     * Camera Sensor
     *      ↓
     * PreviewView
     */
    val previewOutputTransform =
        previewView.outputTransform
            ?: error(
                "PreviewView outputTransform is null"
            )

    /*
     * 2.
     * 创建 ImageProxy 的 Transform
     */
    val imageProxyTransform =
        ImageProxyTransformFactory()
            .getOutputTransform(
                imageProxy
            )

    /*
     * 3.
     * 创建坐标转换
     *
     * PreviewView 坐标
     *       ↓
     * ImageProxy 坐标
     */
    val coordinateTransform =
        CoordinateTransform(
            previewOutputTransform,
            imageProxyTransform
        )

    /*
     * 4.
     * 获取身份证框
     *
     * 注意：
     *
     * 这里的 Rect 必须和 Canvas 中画框的位置完全一致。
     *
     * 由于这里没有拿到 Compose Canvas 的 size，
     * 所以我们根据 PreviewView 实际尺寸重新计算。
     */
    val cropRect =
        getIdCardRect(
            previewView
        )

    /*
     * 5.
     * 将 PreviewView 坐标转换为 ImageProxy 坐标
     */
    coordinateTransform.mapRect(
        cropRect
    )

    /*
     * 6.
     * 获取 ImageProxy 对应 Bitmap
     */
    val bitmap =
        imageProxyToBitmap(
            imageProxy
        )

    /*
     * 7.
     * 坐标经过转换后，
     * cropRect 已经对应 Bitmap 的坐标系。
     */
    val left =
        cropRect.left
            .roundToInt()
            .coerceIn(
                0,
                bitmap.width
            )

    val top =
        cropRect.top
            .roundToInt()
            .coerceIn(
                0,
                bitmap.height
            )

    val right =
        cropRect.right
            .roundToInt()
            .coerceIn(
                left,
                bitmap.width
            )

    val bottom =
        cropRect.bottom
            .roundToInt()
            .coerceIn(
                top,
                bitmap.height
            )

    return Bitmap.createBitmap(
        bitmap,
        left,
        top,
        right - left,
        bottom - top
    )
}

private fun getIdCardRect(
    previewView: PreviewView
): RectF {

    val width =
        previewView.width.toFloat()

    val height =
        previewView.height.toFloat()

    val cardWidth =
        width * 0.672f

    val cardHeight =
        height * 0.536f

    val left =
        (width - cardWidth) / 2f

    val top =
        (height - cardHeight) / 2f

    return RectF(
        left,
        top,
        left + cardWidth,
        top + cardHeight
    )
}

private fun imageProxyToBitmap(
    image: ImageProxy
): Bitmap {

    val buffer =
        image.planes[0].buffer

    val bytes =
        ByteArray(buffer.remaining())

    buffer.get(bytes)

    return BitmapFactory.decodeByteArray(
        bytes,
        0,
        bytes.size
    )
}

@Composable
fun CameraScanBorder(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier.fillMaxSize()) {

        val rectWidth = 242.dp.toPx()
        val rectHeight = 386.dp.toPx()

        val left = (size.width - rectWidth) / 2f
        val top = (size.height - rectHeight) / 2f

        val radius = 16.dp.toPx()

        val normalStroke = 2.dp.toPx()
        val cornerStroke = 4.dp.toPx()

        val cornerLength = 38.dp.toPx()

        drawRect(
            color = Color.Black.copy(alpha = 0.6f)
        )

        //透明区域
//        drawRoundRect(
//            color = Color.Transparent,
//            topLeft = Offset(left, top),
//            size = Size(rectWidth, rectHeight),
//            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
//            blendMode = BlendMode.Clear
//        )

        //-------------------------
        // 四条边（细）
        //-------------------------

        drawLine(
            Color.White,
            Offset(left + radius, top),
            Offset(left + rectWidth - radius, top),
            strokeWidth = normalStroke
        )

        drawLine(
            Color.White,
            Offset(left + radius, top + rectHeight),
            Offset(left + rectWidth - radius, top + rectHeight),
            strokeWidth = normalStroke
        )

        drawLine(
            Color.White,
            Offset(left, top + radius),
            Offset(left, top + rectHeight - radius),
            strokeWidth = normalStroke
        )

        drawLine(
            Color.White,
            Offset(left + rectWidth, top + radius),
            Offset(left + rectWidth, top + rectHeight - radius),
            strokeWidth = normalStroke
        )

        //-------------------------
        // 圆角 左上
        //-------------------------

        drawArc(
            color = Color.White,
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(left, top),
            size = Size(radius * 2, radius * 2),
            style = Stroke(cornerStroke, cap = StrokeCap.Round)
        )

        drawLine(
            Color.White,
            Offset(left + radius, top),
            Offset(left + cornerLength, top),
            strokeWidth = cornerStroke,
            cap = StrokeCap.Round
        )

        drawLine(
            Color.White,
            Offset(left, top + radius),
            Offset(left, top + cornerLength),
            strokeWidth = cornerStroke,
            cap = StrokeCap.Round
        )

        //-------------------------
        // 圆角 右上
        //-------------------------

        drawArc(
            color = Color.White,
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(left + rectWidth - radius * 2, top),
            size = Size(radius * 2, radius * 2),
            style = Stroke(cornerStroke, cap = StrokeCap.Round)
        )

        drawLine(
            Color.White,
            Offset(left + rectWidth - radius, top),
            Offset(left + rectWidth - cornerLength, top),
            strokeWidth = cornerStroke,
            cap = StrokeCap.Round
        )

        drawLine(
            Color.White,
            Offset(left + rectWidth, top + radius),
            Offset(left + rectWidth, top + cornerLength),
            strokeWidth = cornerStroke,
            cap = StrokeCap.Round
        )

        //-------------------------
        // 圆角 左下
        //-------------------------

        drawArc(
            color = Color.White,
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(left, top + rectHeight - radius * 2),
            size = Size(radius * 2, radius * 2),
            style = Stroke(cornerStroke, cap = StrokeCap.Round)
        )

        drawLine(
            Color.White,
            Offset(left + radius, top + rectHeight),
            Offset(left + cornerLength, top + rectHeight),
            strokeWidth = cornerStroke,
            cap = StrokeCap.Round
        )

        drawLine(
            Color.White,
            Offset(left, top + rectHeight - radius),
            Offset(left, top + rectHeight - cornerLength),
            strokeWidth = cornerStroke,
            cap = StrokeCap.Round
        )

        //-------------------------
        // 圆角 右下
        //-------------------------

        drawArc(
            color = Color.White,
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(
                left + rectWidth - radius * 2, top + rectHeight - radius * 2
            ),
            size = Size(radius * 2, radius * 2),
            style = Stroke(cornerStroke, cap = StrokeCap.Round)
        )

        drawLine(
            Color.White,
            Offset(left + rectWidth - radius, top + rectHeight),
            Offset(left + rectWidth - cornerLength, top + rectHeight),
            strokeWidth = cornerStroke,
            cap = StrokeCap.Round
        )

        drawLine(
            Color.White,
            Offset(left + rectWidth, top + rectHeight - radius),
            Offset(left + rectWidth, top + rectHeight - cornerLength),
            strokeWidth = cornerStroke,
            cap = StrokeCap.Round
        )
    }
}

private fun logCameraSizes(context: Context) {

    val cameraManager =
        context.getSystemService(
            Context.CAMERA_SERVICE
        ) as CameraManager

    for (cameraId in cameraManager.cameraIdList) {

        val characteristics =
            cameraManager.getCameraCharacteristics(cameraId)

        val lensFacing =
            characteristics.get(
                CameraCharacteristics.LENS_FACING
            )

        if (
            lensFacing !=
            CameraCharacteristics.LENS_FACING_BACK
        ) {
            continue
        }

        val map =
            characteristics.get(
                CameraCharacteristics
                    .SCALER_STREAM_CONFIGURATION_MAP
            ) ?: continue

        Log.d(
            "CameraSize",
            "========== Camera $cameraId =========="
        )

        val jpegSizes =
            map.getOutputSizes(ImageFormat.JPEG)

        jpegSizes
            ?.sortedByDescending {
                it.width.toLong() * it.height.toLong()
            }
            ?.forEach {

                Log.d(
                    "CameraSize",
                    "JPEG: ${it.width} x ${it.height}"
                )
            }

        val yuvSizes =
            map.getOutputSizes(
                ImageFormat.YUV_420_888
            )

        yuvSizes
            ?.sortedByDescending {
                it.width.toLong() * it.height.toLong()
            }
            ?.forEach {

                Log.d(
                    "CameraSize",
                    "YUV: ${it.width} x ${it.height}"
                )
            }

        val privateSizes =
            map.getOutputSizes(
                ImageFormat.PRIVATE
            )

        privateSizes
            ?.sortedByDescending {
                it.width.toLong() * it.height.toLong()
            }
            ?.forEach {

                Log.d(
                    "CameraSize",
                    "PRIVATE: ${it.width} x ${it.height}"
                )
            }
    }
}