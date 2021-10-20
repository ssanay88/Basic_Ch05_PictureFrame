package com.example.basic_ch05_pictureframe

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.basic_ch05_pictureframe.databinding.ActivityMainBinding
import java.util.zip.Inflater

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    // 이미지 뷰들을 리스트 형식으로 불러온다.
    private val imageViewList: List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            add(mainBinding.imageView11)
            add(mainBinding.imageView12)
            add(mainBinding.imageView13)
            add(mainBinding.imageView21)
            add(mainBinding.imageView22)
            add(mainBinding.imageView23)
        }
    }

    // 저장소에서 불러온 사진의 Uri를 저장할 리스트
    private val imageUriList: MutableList<Uri> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {

        mainBinding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        initAddPhotoBtn()
        initStartPhotoFrameModeBtn()

    }

    // 사진 추가 버튼 클릭 시 이벤트
    private fun initAddPhotoBtn() {
        mainBinding.addPhotoBtn.setOnClickListener {

            when {
                // 외부 저장소에 접근에 대한 권한이 허용되었는지 확인하고 허락되었을 경우
                ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)  == PackageManager.PERMISSION_GRANTED ->
                {
                    // todo 권한이 잘 부여되었을 때 갤러리에서 사진을 선택하는 기능
                    navigatePhotos()

                }

                // 권한에 대한 교육용 팝업을 띄어야 하는 경우
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) ->
                {
                    // todo 교육용 팝업 확인 후 권한 팝업을 띄우는 기능
                    showPermissionContextPopup()
                }

                // 위의 두 가지 경우가 아닌 경우 권한 요청을 해야한다
                else ->
                {
                    // 권한 요청 함수 , 첫번째 인자로 array가 들어가는데 함수 이름을 보면 알 수 있듯이 요청하는 권한들을 배열로 넣어줘야한다.
                    // 두번째 인자
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1000)
                }
            }

        }
    }

    private fun initStartPhotoFrameModeBtn() {

        mainBinding.startPhotoFrameModeBtn.setOnClickListener {
            val intent = Intent(this,PhotoFrameActivity::class.java)
            imageUriList.forEachIndexed { index, uri ->
                intent.putExtra("Photo$index" , uri.toString())
            }
            intent.putExtra("photoListSize", imageUriList.size)

            startActivity(intent)

        }

    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("전자액자에 앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의하기", { _,_ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1000)
            })
            .setNegativeButton("취소하기", { _,_ ->
                // 팝업 닫기
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 부여된 경우
                    navigatePhotos()
                } else {
                    // 권한이 부여되지 않은 경우
                    Toast.makeText(this,"권한이 거부되었습니다.",Toast.LENGTH_SHORT).show()
                }
            }

            else -> {

            }
        }
    }

    // 컨텐츠 프로바이더에서 스토리지 엑세스 프레임워크 기능 사용
    private fun navigatePhotos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"

        startActivityForResult(intent,2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 사진 추가하는 과정에서 사진 선택이 정상적이지 않을 경우
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            2000 -> {
                val selectedImageUri: Uri? = data?.data    // ? : 널가능

                if (selectedImageUri != null) {

                    if (imageUriList.size > 6) {
                        Toast.makeText(this,"사진 갯수가 최대입니다.",Toast.LENGTH_SHORT).show()
                        return
                    }

                    imageUriList.add(selectedImageUri)
                    imageViewList[imageUriList.size - 1].setImageURI(selectedImageUri)
                } else {
                    Toast.makeText(this,"사진을 가져오지 못했습니다.",Toast.LENGTH_SHORT).show()
                }

            }

            else -> {
                Toast.makeText(this,"사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }



}