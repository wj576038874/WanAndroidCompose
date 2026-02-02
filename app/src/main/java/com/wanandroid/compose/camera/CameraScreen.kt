package com.wanandroid.compose.camera

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.exifinterface.media.ExifInterface
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by wenjie on 2026/01/28.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    onTakePhoto: (ByteArray) -> Unit
) {
    val permissionState = rememberMultiplePermissionsState(
        listOf(android.Manifest.permission.CAMERA)
    )
    val context = LocalContext.current
    LaunchedEffect(permissionState) {
        permissionState.launchMultiplePermissionRequest()
    }

//    val lifecycleOwner = LocalLifecycleOwner.current
//    DisposableEffect(lifecycleOwner) {
//        val observer = object : DefaultLifecycleObserver {
//            override fun onResume(owner: LifecycleOwner) {
//                super.onResume(owner)
//                permissionState.launchMultiplePermissionRequest()
//            }
//        }
//        lifecycleOwner.lifecycle.addObserver(observer = observer)
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer = observer)
//        }
//    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val perm = permissionState.permissions[0]
            when (perm.status) {
                PermissionStatus.Granted -> {
                    CameraPreviewView(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        innerPadding = innerPadding,
                        onTakePhoto = onTakePhoto
                    )
                }

                PermissionStatus.Denied(false) -> {
                    Text(
                        text = "请去设置页面授权相机权限",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clickable {
                                goAppDetailSetting(
                                    context = context
                                )
                            },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                PermissionStatus.Denied(true) -> {
                    Text(
                        text = "请授权相机权限",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> {}
            }
        }
    }
}

fun goAppDetailSetting(context: Context) {
    val intent = Intent()
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.setData(Uri.fromParts("package", context.packageName, null))
    context.startActivity(intent)
}

@Composable
private fun CameraPreviewView(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    onTakePhoto: (ByteArray) -> Unit
) {
    val context = LocalContext.current
    val resolutionSelector = ResolutionSelector.Builder()
        .setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)  // 优先最高分辨率
        .build()
    val cameraController = remember {
        LifecycleCameraController(context.applicationContext).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE
            )
//            imageCaptureMode = ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
            setImageCaptureResolutionSelector(resolutionSelector)
        }
    }
    var isTorchOn by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf<Recording?>(null) }
    var recordUiState by remember { mutableStateOf(UiState.IDLE) }
    CameraPreview(
        controller = cameraController,
        modifier = Modifier.fillMaxSize()
    )
    IconButton(
        modifier = Modifier
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
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            modifier = Modifier
                .padding(innerPadding),
            onClick = {
                takePhotoBitmap(
                    cameraController = cameraController,
                    onPhotoTaken = { byteArray ->
                        onTakePhoto(byteArray)
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
        IconButton(
            modifier = Modifier
                .padding(innerPadding),
            onClick = {
                takePhotoCacheFile(
                    cameraController = cameraController,
                    onPhotoTaken = { byteArray ->
                        onTakePhoto(byteArray)
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
        IconButton(
            modifier = Modifier
                .padding(innerPadding),
            onClick = {
                takePhotoPhotoAlbum(
                    cameraController = cameraController,
                    context = context,
                    onPhotoTaken = { byteArray ->
                        onTakePhoto(byteArray)
                    },
                )
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.PhotoCamera,
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = null
            )
        }
        IconButton(
            modifier = Modifier
                .padding(innerPadding),
            onClick = {
                if (recording != null) {
                    recording?.stop()
                    recording = null
                    return@IconButton
                }
                recording = recordVideo(
                    cameraController = cameraController,
                    onVideoRecorded = { file ->
                        Log.e("CameraScreen", "recordVideo: ${file.absolutePath}")
                    },
                    onRecordEvent = {
                        when (it) {
                            is VideoRecordEvent.Start -> {
                                recordUiState = UiState.RECORDING
                            }

                            is VideoRecordEvent.Finalize -> {
                                recordUiState = UiState.FINALIZED
                            }
                        }
                    },
                    context = context
                )
            }
        ) {
            val color = when (recordUiState) {
                UiState.FINALIZED -> MaterialTheme.colorScheme.onPrimary
                UiState.RECORDING -> Color.Red
                UiState.IDLE -> MaterialTheme.colorScheme.onPrimary
            }
            Icon(
                imageVector = Icons.Outlined.Videocam,
                tint = color,
                contentDescription = null
            )
        }
        IconButton(
            modifier = Modifier
                .padding(innerPadding),
            onClick = {
                isTorchOn = !isTorchOn
                cameraController.enableTorch(isTorchOn)
            }
        ) {
            Icon(
                imageVector = if (isTorchOn) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = null
            )
        }
        IconButton(
            modifier = Modifier
                .padding(innerPadding),
            onClick = {
                /**
                 * 这种方式切换闪光灯 没效果？
                 */
                cameraController.imageCaptureFlashMode =
                    if (cameraController.imageCaptureFlashMode == ImageCapture.FLASH_MODE_ON) {
                        isTorchOn = false
                        ImageCapture.FLASH_MODE_OFF
                    } else {
                        isTorchOn = true
                        ImageCapture.FLASH_MODE_ON
                    }
            }
        ) {
            Icon(
                imageVector = if (isTorchOn) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = null
            )
        }
    }
}

private enum class UiState {
    IDLE,       // 未录制
    RECORDING,  // 摄像机正在录制时，只显示停止按钮
    FINALIZED,  // 录制完成
}

@SuppressLint("MissingPermission")
private fun recordVideo(
    cameraController: LifecycleCameraController,
    onVideoRecorded: (File) -> Unit,
    onRecordEvent: (VideoRecordEvent) -> Unit,
    context: Context
): Recording {
    val qualitySelector = QualitySelector.from(Quality.FHD)
    val outputFile = File(context.cacheDir, "camerax.mp4")
    val outputOptions = FileOutputOptions.Builder(outputFile)
        .build()
    cameraController.videoCaptureQualitySelector = qualitySelector
    return cameraController.startRecording(
        outputOptions,
        AudioConfig.create(false),
        ContextCompat.getMainExecutor(context.applicationContext),
    ) {
        onRecordEvent.invoke(it)
        when (it) {
            is VideoRecordEvent.Finalize -> {
                Log.e("CameraScreen", "recordVideo: Finalize")
                if (it.hasError()) {
                    Log.e("CameraScreen", "recordVideo: ", it.cause)
                } else {
                    onVideoRecorded.invoke(outputFile)
                }
            }

//            is VideoRecordEvent.Status -> {
//                Log.e("CameraScreen", "recordVideo: Status")
//            }

            is VideoRecordEvent.Start -> {
                Log.e("CameraScreen", "recordVideo: Start")
            }

            is VideoRecordEvent.Pause -> {
                Log.e("CameraScreen", "recordVideo: Pause")
            }

            is VideoRecordEvent.Resume -> {
                Log.e("CameraScreen", "recordVideo: Resume")
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
private fun takePhotoBitmap(
    cameraController: LifecycleCameraController,
    onPhotoTaken: (ByteArray) -> Unit,
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
                val convertedBitmap =
                    convertBitmap(rotationDegrees, image.toBitmap(), cameraController)
                onPhotoTaken.invoke(convertedBitmap.toByteArray())
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("CameraScreen", "takePhoto: ", exception)
            }
        }
    )
}


fun Bitmap.toByteArray(
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 100
): ByteArray {
    ByteArrayOutputStream().use { stream ->
        compress(format, quality, stream)
        return stream.toByteArray()
    }
}

/**
 * 拍照 缓存到文件
 */
private fun takePhotoCacheFile(
    cameraController: LifecycleCameraController,
    onPhotoTaken: (ByteArray) -> Unit,
    context: Context
) {
    val name = SimpleDateFormat(
        "yyyyMMdd_HHmmss", Locale.CHINA
    ).format(System.currentTimeMillis()) + ".jpg"
    val outputDir = File(context.filesDir, "camerax")
    if (!outputDir.exists()) {
        outputDir.mkdirs()
    }
    val outputFile = File(outputDir, name)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
    cameraController.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context.applicationContext),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.d("CameraScreen", "onImageSaved: $outputFileResults")
                val savedUri = outputFileResults.savedUri ?: return
                val file = savedUri.toFile()
                onPhotoTaken.invoke(file.readBytes())
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "takePhoto: ", exception)
            }
        }
    )
}

/**
 * 拍照存储到相册
 * @param cameraController 相机控制器
 * @param onPhotoTaken 拍照回调
 * @param context 上下文
 */
private fun takePhotoPhotoAlbum(
    cameraController: LifecycleCameraController,
    onPhotoTaken: (ByteArray) -> Unit,
    context: Context
) {
    val name = SimpleDateFormat(
        "yyyyMMdd_HHmmss", Locale.CHINA
    ).format(System.currentTimeMillis()) + ".jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/wanAndroidCompose"
            )
        }
    }
    // 创建图片捕获
    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()
    cameraController.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context.applicationContext),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.d("CameraScreen", "onImageSaved: $outputFileResults")
                val savedUri = outputFileResults.savedUri ?: return
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, savedUri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, savedUri)
                }

                //byteArray
                val inputStream = context.contentResolver.openInputStream(savedUri)
                inputStream?.use {
                    val exif = ExifInterface(inputStream)
                    val rotationDegrees = when (exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        else -> 0
                    }
                    Log.d("CameraX", "图片旋转角度: $rotationDegrees° (EXIF Orientation)")
//                    val convertedBitmap = convertBitmap(rotationDegrees, bitmap, cameraController)
//                    onPhotoTaken.invoke(convertedBitmap.toByteArray())
                }

                onPhotoTaken.invoke(inputStream?.readBytes() ?: byteArrayOf())

                // Implicit broadcasts will be ignored for devices running API level >= 24
                // so if you only target API level 24+ you can remove this statement
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//                    // Suppress deprecated Camera usage needed for API level 23 and below
//                    context.sendBroadcast(Intent(Camera.ACTION_NEW_PICTURE, savedUri))
//                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "takePhoto: ", exception)
            }
        }
    )
}


private fun convertBitmap(
    rotationDegrees: Int,
    bitmap: Bitmap,
    cameraController: CameraController
): Bitmap {
    val rotatedBitmap = rotatedBitmap(bitmap, rotationDegrees)
    return if (cameraController.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
        horizontalMirror(rotatedBitmap)
    } else {
        rotatedBitmap
    }
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
