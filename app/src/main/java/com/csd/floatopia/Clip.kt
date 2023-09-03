package com.csd.floatopia

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.sll.lib_framework.ext.view.gone
import com.sll.lib_framework.ext.view.visible

class Clip : AppCompatActivity(), View.OnClickListener {
    var requestList = ArrayList<String>();
    private var bitmap : Bitmap? =null;
    val REQUEST_MEDIA_PROJECTION = 10001
    lateinit var mProjectionManager:MediaProjectionManager;
    var my_cavas: BitmapClippingView? =null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clip)
        my_cavas=findViewById<BitmapClippingView>(R.id.my_clip);
        var but_set=findViewById<Button>(R.id.set)
        var but_get_bit=findViewById<Button>(R.id.get)
        var but_get_text=findViewById<Button>(R.id.get_text)
        var img=findViewById<ImageView>(R.id.main_imageview)
        bitmap = BitmapFactory. decodeResource (getResources(),R.drawable.text_tast );

        img.setImageBitmap(bitmap)


        but_set.setOnClickListener(this)
        but_get_bit.setOnClickListener(this)
        but_get_text.setOnClickListener(this)
    }

    private fun getPermision() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mProjectionManager =
                getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            if (mProjectionManager != null) {
                startActivityForResult(
                    mProjectionManager.createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestList.add(Manifest.permission.CAMERA);
        }
        if (requestList.size != 0) {
            ActivityCompat.requestPermissions(   this, requestList.toTypedArray<String>(), 1)
        }
    }

    public fun showToast(text:String){
        var mToast= Toast(this);
        // 若Toast控件未初始化
        // 若Toast控件未初始化
        if (mToast == null) {
            // 则初始化
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        } else {
            // 修改显示文本
            mToast.setText(text)
        }
        // 显示
        mToast.show()
    }

    private fun selectImg() {
        var pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, 2);
    }

    @SuppressLint("WrongViewCast", "Range")
    override protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== RESULT_OK){
            when(requestCode){
                2->{
                    if (data!=null){

                        showToast("onActivityResult: " + data.getData())
                        Log.d("DEBUG",data.getData().toString());
                        bitmap= BitmapFactory.decodeStream(data.getData()
                            ?.let { contentResolver.openInputStream(it) })
                        if (bitmap!=null) my_cavas!!.setBitmap(bitmap,bitmap!!.width,bitmap!!.height);
                    }

                }


            }
        }
    }


    override fun onClick(v: View?) {
        var img=findViewById<ImageView>(R.id.main_imageview)
        when(v?.id){
            R.id.get_text->{
                var text_view=findViewById<TextView>(R.id.main_textview)

                val recognizer =
                    TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())

                recognizer.process(InputImage.fromBitmap(bitmap!!, 90))
                    .addOnSuccessListener { visionText ->
                        if (visionText!=null){
                            img.gone()
                            my_cavas!!.gone()
                            text_view.setText(visionText.text)
                        }
                    }
                    .addOnFailureListener { showToast("识别失败") }

            }
            R.id.get->{
                    bitmap=my_cavas!!.getBitmap(this,10,10)
                    img.setImageBitmap(bitmap)
            }
            R.id.set->{
               // selectImg()
                my_cavas!!.setBitmap(bitmap, bitmap!!.width,bitmap!!.height)
            }
        }
    }

}