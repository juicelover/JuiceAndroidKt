package com.dream.juiceandroid.developers.codelab.activity

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dream.juiceandroid.R
import com.dream.juiceandroid.databinding.ActivityCameraxBinding
import com.juiceandroid.base_lib.activity.BaseActivity
import com.juiceandroid.base_lib.tool.AppTool
import com.juiceandroid.base_lib.tool.ToastTool
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author juice
 * @date 2023-01-11 16:19:58
 * @desc camerax
 * @link https://developers.google.cn/codelabs/camerax-getting-started#0
 */
class CameraxActivity : BaseActivity<ActivityCameraxBinding>() {

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val PERMISSION_CAMERA = arrayOf(Manifest.permission.CAMERA)
    }

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun getLayoutId(): Int = R.layout.activity_camerax

    override fun initView() {

        bindingView.lifecycleOwner = this
        bindingView.presenter = this

        bindingView.toolbarJuiceCreate.apply {
            setNavigationOnClickListener { finish() }
        }

        if (allPermissionGranted()) {

            startCamera()
        } else {

            ActivityCompat.requestPermissions(this, PERMISSION_CAMERA, REQUEST_CODE_PERMISSIONS)
        }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun loadData(isRefresh: Boolean) {

    }

    override fun onClick(v: View?) {
        v?.let {
            AppTool.singleClick(v) {
                when (v.id) {

                    R.id.btn_capture -> {
                        takePhoto()
                    }
                }
            }
        }
    }

    private fun allPermissionGranted() = PERMISSION_CAMERA.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {

        //创建 ProcessCameraProvider 的实例。此实例用于将相机的生命周期绑定到生命周期所有者。由于 CameraX 具有生命周期感知能力，所以这样可以省去打开和关闭相机的任务。
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        //向 cameraProviderFuture 中添加监听器。添加 Runnable 作为参数。我们将稍后为其填入数值。添加 ContextCompat.getMainExecutor() 作为第二个参数。这将返回在主线程上运行的 Executor。
        cameraProviderFuture.addListener(Runnable {

            //Used to bind the lifecycle of camera to the lifecycle owner
            //在 Runnable 中，添加 ProcessCameraProvider。此类用于将相机的生命周期绑定到应用进程内的 LifecycleOwner。
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            //Preview
            //初始化您的 Preview 对象，在该对象上调用 build，从取景器中获取表面提供程序，然后在预览中进行设置。
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(bindingView.cameraPreviewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            //Select back camera as a default
            //创建 CameraSelector 对象并选择 DEFAULT_BACK_CAMERA。
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            //创建 try 块。在该块中，确保任何内容都未绑定到您的 cameraProvider，然后将您的 cameraSelector 和预览对象绑定到 cameraProvider。
            try {

                //Unbind use cases before rebinding
                cameraProvider.unbindAll()

                //Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview,imageCapture)
            } catch (exc: Exception) {
                //在少数情况下，此代码会失败，例如应用不再处于焦点中。将此代码放入 catch 块中，以记录是否存在失败情况。
                Log.e(TAG, "Use case bind failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {

        //Get a stable reference of the modifiable image capture use case
        //首先，获取对 ImageCapture 用例的引用。如果用例为 null，则退出函数。如果您在设置拍摄图像之前点按拍照按钮，则这将为 null。如果没有 return 语句，则在用例为 null 的情况下，应用会崩溃。
        val imageCapture = imageCapture ?: return

        //Create time-stamped output file to hold the image
        //接下来，创建一个容纳图像的文件。添加时间戳，以避免文件名重复。
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT,
                Locale.CHINA
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        //Create output options objects which contains file + metadata
        //创建 OutputFileOptions 对象。您可以在此对象中指定有关输出方式的设置。如果您希望将输出内容保存在刚创建的文件中，则添加您的 photoFile。
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        //Set up image capture listener, which is triggered after photo has been taken
        //对 imageCapture 对象调用 takePicture()。传入执行程序 outputOptions 以及在保存图像时使用的回调。接下来，您将填写回调。
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {

                //在图像拍摄失败或图像拍摄结果保存失败的情况下，添加一个错误示例，以记录失败情况。
                override fun onError(exception: ImageCaptureException) {

                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }

                //如果拍摄未失败，则表示拍照成功！将照片保存到您先前创建的文件中，显示一个消息框以告知用户操作成功，然后输出日志语句。
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                    val saveUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $saveUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }

    private fun getOutputDirectory(): File {

        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.codelabs_camerax)).apply {
                mkdirs()
            }
        }

        return if (null != mediaDir && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {

            if (allPermissionGranted()) {

                startCamera()
            } else {

                ToastTool.showToast(
                    "Permissions not granted by the user.",
                    this,
                    Toast.LENGTH_SHORT
                )
                finish()
            }
        }
    }
}