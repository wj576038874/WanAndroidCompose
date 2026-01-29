package com.wanandroid.compose.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.impl.utils.futures.Futures.getUninterruptibly
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.wanandroid.compose.LocalNavigator
import com.wanandroid.compose.WanAndroidApplication.Companion.context
import com.wanandroid.compose.route.Route
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.ExecutionException
import kotlin.coroutines.resumeWithException

/**
 * Created by wenjie on 2026/01/28.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen2(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.current
    val context = LocalContext.current
    // 设置拍照
    val imageCapture =
        ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            // We request aspect ratio but no resolution to match preview config, but letting
            // CameraX optimize for whatever specific resolution best fits our use cases
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(Surface.ROTATION_0).setFlashMode(ImageCapture.FLASH_MODE_OFF).build()
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CameraPreview2(
                imageCapture = imageCapture,
                modifier = Modifier.fillMaxSize()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(innerPadding),
                    onClick = {

                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Cameraswitch,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = null
                    )
                }
                IconButton(
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(innerPadding),
                    onClick = {
                        imageCapture.takePicture(
                            ContextCompat.getMainExecutor(context.applicationContext),
                            object : ImageCapture.OnImageCapturedCallback() {

                                override fun onCaptureStarted() {
                                    super.onCaptureStarted()
                                    Log.d("CameraScreen", "onCaptureStarted: ")
                                }

                                override fun onCaptureSuccess(image: ImageProxy) {
                                    super.onCaptureSuccess(image)
                                    Log.d("CameraScreen", "onCaptureSuccess: $image")
                                    val rotationDegrees = image.imageInfo.rotationDegrees
                                    val bitmap = image.toBitmap()
                                    val matrix = Matrix()
                                    matrix.postRotate(rotationDegrees.toFloat())
                                    val rotatedBitmap = rotatedBitmap(bitmap, rotationDegrees)
                                    navigator.goTo(Route.CameraBitmapPreview(
                                        byteArray = rotatedBitmap.toByteArray()
                                    ))
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    super.onError(exception)
                                    Log.e("CameraScreen", "takePhoto: ", exception)
                                }
                            }
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = null
                    )
                }
            }
        }
    }

}


@Composable
private fun CameraPreview2(
    imageCapture: ImageCapture,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                post {
                    val displayMetrics = DisplayMetrics()
                    (context as Activity).windowManager?.defaultDisplay?.getMetrics(displayMetrics)
                    val screenHeight = displayMetrics.heightPixels.toFloat()
                    val screenWidth = displayMetrics.widthPixels.toFloat()
                    val screenRatio = screenHeight / screenWidth
                    // 宽高差别不大，近似折叠屏
                    if (screenRatio < 1.3f) {
                        val height = screenHeight.toInt()
                        layoutParams.height = height
                        layoutParams.width = (height * 0.75f).toInt()
                    }
                }
                scope.launch {
                    initCamera(
                        context,
                        imageCapture,
                        this@apply,
                        lifecycleOwner
                    )
                }
            }
        },
        modifier = modifier
    )
}


private suspend fun initCamera(
    context: Context,
    imageCapture: ImageCapture,
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner
) {
    val cameraProvider = ProcessCameraProvider.getInstance(context.applicationContext).await()

    //根据可用相机选择摄像镜头 默认后置镜头 没后置就用前置 前置也没有 就直接退出页面
    when {
        cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) -> CameraSelector.LENS_FACING_BACK
        cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) -> CameraSelector.LENS_FACING_FRONT
        else -> {
            Toast.makeText(context, "当前设备不支持相机", Toast.LENGTH_SHORT).show()
        }
    }
    bindCameraUseCases(
        previewView,
        cameraProvider,
        imageCapture,
        lifecycleOwner
    )
}


private fun bindCameraUseCases(
    previewView: PreviewView,
    cameraProvider: ProcessCameraProvider,
    imageCapture: ImageCapture,
    lifecycleOwner: LifecycleOwner
    ) {

//        // Get screen metrics used to setup camera for full screen resolution
//        val metrics = windowManager.getCurrentWindowMetrics().bounds
//        Log.d(TAG, "Screen metrics: ${metrics.width()} x ${metrics.height()}")
//
//        val screenAspectRatio = aspectRatio(metrics.width(), metrics.height())
    val screenAspectRatio = AspectRatio.RATIO_4_3
//        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

    val rotation = previewView.display.rotation


    val cameraSelector = CameraSelector.Builder().requireLensFacing(LENS_FACING_FRONT).build()

    // 设置预览
    val preview = Preview.Builder()
        // We request aspect ratio but no resolution
        .setTargetAspectRatio(screenAspectRatio)
        // Set initial target rotation
        .setTargetRotation(rotation).build()

    // ImageAnalysis
    val imageAnalyzer = ImageAnalysis.Builder()
        // We request aspect ratio but no resolution
        .setTargetAspectRatio(screenAspectRatio)
        // Set initial target rotation, we will have to call this again if rotation changes
        // during the lifecycle of this use case
        .setTargetRotation(rotation).build()
    // The analyzer can then be assigned to the instance
//            .also {
//                if (DevModeUtils.getDevMode().isDebug) {
//                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
//                        // Values returned from our analyzer are passed to the attached listener
//                        // We log image analysis results here - you should do something useful
//                        // instead!
//                        Log.d(TAG, "Average luminosity: $luma")
//                    })
//                }
//            }

    //先解除绑定
    cameraProvider.unbindAll()

//    if (camera != null) {
//        // 不为null说明之前有添加过 本次就先删除
//        camera!!.cameraInfo.cameraState.removeObservers(viewLifecycleOwner)
//    }

    try {
        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, imageCapture, imageAnalyzer
        )
        // Attach the viewfinder's surface provider to preview use case
        preview.surfaceProvider = previewView.surfaceProvider
    } catch (exc: Exception) {

    }
}

@SuppressLint("RestrictedApi")
suspend fun <T> ListenableFuture<T>.await(): T {
    return suspendCancellableCoroutine { continuation: CancellableContinuation<T> ->
        addListener(
            {
                if (isCancelled) {
                    continuation.cancel()
                } else {
                    try {
                        continuation.resumeWith(
                            Result.success(getUninterruptibly(this))
                        )
                    } catch (e: ExecutionException) {
                        // ExecutionException is the only kind of exception that can be thrown from a gotten
                        // Future. Anything else showing up here indicates a very fundamental bug in a
                        // Future implementation.
                        continuation.resumeWithException(e)
                    }
                }
            },
            ContextCompat.getMainExecutor(context)
        )
        continuation.invokeOnCancellation {
            cancel(false)
        }
    }
}

/**
 * 拍照
 * @param cameraController 相机控制器
 * @param onPhotoTaken 拍照回调
 * @param context 上下文
 */
private fun takePhoto(
    cameraController: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit,
    context: Context
) {

}

/**
 * 前置摄像头拍照 水平镜像翻转
 * @param bmp 原始 Bitmap
 * @return 水平镜像翻转后的 Bitmap
 */
private fun horizontalMirror(bmp: Bitmap): Bitmap {
    val w = bmp.width
    val h = bmp.height
    val matrix = Matrix()
    matrix.postScale(-1f, 1f) // 水平镜像翻转
    return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true)
}

/**
 * 旋转 Bitmap
 * @param bitmap 原始 Bitmap
 * @param rotationDegrees 旋转角度
 * @return 旋转后的 Bitmap
 */
private fun rotatedBitmap(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(rotationDegrees.toFloat())
    return Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
    )
}
