package com.example.guru_app_.activities

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.guru_app_.R
import com.example.guru_app_.database.MemoDao
import com.example.guru_app_.models.Memo
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class MemoDetailActivity : AppCompatActivity() {
    private var memoId: Int = -1
    private var bookId: Int = -1
    private lateinit var memoDao: MemoDao
    private lateinit var memoTitle: EditText
    private lateinit var memoContent: EditText
    private lateinit var addCameraImageButton: ImageButton
    private lateinit var addGalleryImageButton: ImageButton
    private lateinit var saveButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    // 갤러리에서 이미지를 선택한 결과를 처리하는 런처
    private val requestGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            imageView.setImageURI(imageUri)
            imageView.visibility = ImageView.VISIBLE
        }
    }
    // 카메라로 찍은 이미지를 처리하는 런처
    private val requestCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            imageUri = saveImageToExternalStorage(imageBitmap)
            imageView.setImageURI(imageUri)
            imageView.visibility = ImageView.VISIBLE
        }
    }
    // 권한을 체크하고 요청하는 함수
    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), REQUEST_PERMISSIONS)
        }
    }
    // 액티비티가 생성될 때 호출되는 함수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_detail)

        checkAndRequestPermissions()

        memoDao = MemoDao(this)

        memoId = intent.getIntExtra("MEMO_ID", -1)
        bookId = intent.getIntExtra("BOOK_ID", -1)

        memoTitle = findViewById(R.id.memo_title)
        memoContent = findViewById(R.id.memo_content)
        addCameraImageButton = findViewById(R.id.add_camera_image_button)
        addGalleryImageButton = findViewById(R.id.add_gallery_image_button)
        saveButton = findViewById(R.id.save_button)
        backButton = findViewById(R.id.back_button)
        imageView = findViewById(R.id.image_view)

        if (memoId != -1) {
            val memo: Memo? = memoDao.getMemoById(memoId)
            memo?.let {
                memoTitle.setText(it.title)
                memoContent.setText(it.content)
                imageUri = it.imagePath?.let { path -> Uri.parse(path) }
                if (imageUri != null) {
                    if (isValidUri(imageUri!!)) {
                        imageView.setImageURI(imageUri)
                        imageView.visibility = ImageView.VISIBLE
                    } else {
                        imageView.visibility = ImageView.GONE
                    }
                } else {
                    imageView.visibility = ImageView.GONE
                }
            }
        }

        addCameraImageButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            } else {
                openCamera()
            }
        }

        addGalleryImageButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_GALLERY_PERMISSION)
                } else {
                    openGallery()
                }
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_GALLERY_PERMISSION)
                } else {
                    openGallery()
                }
            }
        }

        saveButton.setOnClickListener {
            saveMemo()
        }

        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        requestCameraLauncher.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        requestGalleryLauncher.launch(intent)
    }

    private fun saveMemo() {
        val title = memoTitle.text.toString().ifBlank { "Untitled Memo" }
        val content = memoContent.text.toString().ifBlank { "No content" }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = dateFormat.format(Date())

        if (memoId == -1) {
            // 새로운 메모 추가
            val newMemo = Memo(
                bookId = bookId,
                title = title,
                content = content,
                page = null,
                imagePath = imageUri?.toString(),
                createdAt = date,
                updatedAt = date
            )
            memoDao.addMemo(newMemo)
        } else {
            // 기존 메모 업데이트
            val updatedMemo = Memo(
                id = memoId,
                bookId = bookId,
                title = title,
                content = content,
                page = null,
                imagePath = imageUri?.toString(),
                createdAt = date,
                updatedAt = date
            )
            memoDao.updateMemo(updatedMemo)
        }

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun saveImageToExternalStorage(bitmap: Bitmap): Uri {
        val imagesFolder = File(getExternalFilesDir(null), "images")
        imagesFolder.mkdirs()
        val file = File(imagesFolder, "${System.currentTimeMillis()}.jpg")
        val stream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
        return Uri.fromFile(file)
    }
    // URI가 유효한지 확인하는 함수
    private fun isValidUri(uri: Uri): Boolean {
        return try {
            contentResolver.openInputStream(uri)?.close()
            true
        } catch (e: Exception) {
            false
        }
    }
    // 권한 요청 결과를 처리하는 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openCamera()
                } else {
                    // 권한이 거부된 경우 처리
                }
            }
            REQUEST_GALLERY_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openGallery()
                } else {
                    // 권한이 거부된 경우 처리
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1
        private const val REQUEST_GALLERY_PERMISSION = 2
        private const val REQUEST_PERMISSIONS = 100
    }
}




