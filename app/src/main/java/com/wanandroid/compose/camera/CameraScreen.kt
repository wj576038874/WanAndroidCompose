package com.wanandroid.compose.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.wanandroid.compose.LocalBackStack
import com.wanandroid.compose.route.Route

/**
 * Created by wenjie on 2026/01/28.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(modifier: Modifier = Modifier) {
    val backStack = LocalBackStack.current
    val context = LocalContext.current
    val cameraController = remember {
        LifecycleCameraController(context.applicationContext).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE
            )
        }
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CameraPreview(
                controller = cameraController,
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
                        cameraController.cameraSelector =
                            if (cameraController.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
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
                        takePhoto(
                            cameraController = cameraController,
                            onPhotoTaken = { bitmap ->
                                backStack.add(Route.CameraBitmapPreview(bitmap = bitmap.asImageBitmap()))
                            },
                            context = context
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
    cameraController.takePicture(
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
                if (cameraController.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                    onPhotoTaken.invoke(horizontalMirror(rotatedBitmap))
                } else {
                    onPhotoTaken.invoke(rotatedBitmap)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("CameraScreen", "takePhoto: ", exception)
            }
        }
    )
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
