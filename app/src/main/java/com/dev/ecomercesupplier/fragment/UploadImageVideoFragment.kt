package com.dev.ecomercesupplier.fragment

import android.Manifest
import android.R.attr
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.androidnetworking.interfaces.UploadProgressListener
import com.dev.ecomercesupplier.BuildConfig
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.UploadImageVideoAdapter
import com.dev.ecomercesupplier.custom.FetchPath
import com.dev.ecomercesupplier.custom.Utility.Companion.IMAGE_DIRECTORY_NAME
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_upload_image_video.view.*
import kotlinx.android.synthetic.main.item_uploaded_img_vdo.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UploadImageVideoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadImageVideoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mView: View?=null
    var PICK_IMAGE_MULTIPLE = 1
    var mArrayUri: ArrayList<Uri>? = null
    private var uri: Uri? = null
    val MEDIA_TYPE_IMAGE = 1
    val PICK_IMAGE_FROM_GALLERY = 10
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    private var imagePath = ""
    private val galleryPhotos = ArrayList<String>()
    var pathList=ArrayList<String>()
    private val PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE = 301
    private val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val PICK_DOC = 234
    lateinit var uploadImageVideoAdapter: UploadImageVideoAdapter
    var uploadCount=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if(mView==null){
            mView = inflater.inflate(R.layout.fragment_upload_image_video, container, false)
            setUpViews()

        }
        return mView

    }

    private fun setUpViews() {
        requireActivity().other_frag_backImg.visibility= View.VISIBLE


        requireActivity().other_frag_backImg.setOnClickListener {
             requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(
                requireContext(),
                requireActivity().other_frag_backImg
            )
            findNavController().popBackStack()
        }
        mView!!.imgAttach.setOnClickListener {
            mView!!.imgAttach.startAnimation(AlphaAnimation(1f, 0.5f))
            requestToUploadProfilePhoto()
        }

       /* mView!!.btnstartUploading.setOnClickListener {
            mView!!.btnstartUploading.startAnimation(AlphaAnimation(1f, 0.5f))
            for (i in 0 until pathList.size){
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    uploadFile(i)
                }, 100)
            }

        }*/

       /* mView!!.rvList.layoutManager=LinearLayoutManager(requireContext())
        uploadImageVideoAdapter= UploadImageVideoAdapter(
            requireContext(),
            pathList,
            object : ClickInterface.ClickPosItemViewInterface {
                override fun clickPosItemView(pos: Int, itemView: View) {
                    *//* if(type=="remove"){
                    pathList.removeAt(pos)
                    uploadImageVideoAdapter.notifyDataSetChanged()
                }*//*

                    uploadFile(pathList[pos], itemView)
                }


            })
        mView!!.rvList.adapter=uploadImageVideoAdapter*/
    }

    fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission!!) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    fun requestToUploadProfilePhoto() {
        if (!hasPermissions(requireContext(), *PERMISSIONS)) {
            requestPermissions(PERMISSIONS, PERMISSION_CAMERA_EXTERNAL_STORAGE_CODE)
        } else if (hasPermissions(requireContext(), *PERMISSIONS)) {
            openCameraDialog()
        }

/*        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(
                    requireContext(),
                    FilePickerConst.PERMISSIONS_FILE_PICKER
                )!=PackageManager.PERMISSION_GRANTED){
                return
            }else{
//                chooseImageVideo()
                openCameraDialog()
            }
        }*/


    }



    private fun openCameraDialog() {
        val items = arrayOf<CharSequence>(getString(R.string.camera), getString(R.string.gallery), getString(R.string.cancel))
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.add_photo))
        builder.setItems(items) { dialogInterface, i ->
            if (items[i] == getString(R.string.camera)) {
                captureImage()
            } else if (items[i] == getString(R.string.gallery)) {
                chooseImage()
            } else if (items[i] == getString(R.string.cancel)) {
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }

    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
    }

    fun getOutputMediaFileUri(type: Int): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID.toString() + ".provider", getOutputMediaFile(type)!!)
        } else {
            Uri.fromFile(getOutputMediaFile(type))
        }
    }

    private fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME)
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }
        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(Date())
        val mediaFile: File
        mediaFile = if (type == MEDIA_TYPE_IMAGE) {
            File(mediaStorageDir.path + File.separator
                    + "IMG_" + timeStamp + ".png")
        } else if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            File(mediaStorageDir.path + File.separator
                    + "VID_" + timeStamp + ".mp4")
        } else {
            return null
        }
        return mediaFile
    }


    private fun chooseImage() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.type = "image/*"
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        intent.action = Intent.ACTION_GET_CONTENT
//        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        startActivityForResult(intent, PICK_IMAGE_FROM_GALLERY)

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, PICK_IMAGE_FROM_GALLERY)
    }




    private fun chooseImageVideo() {
        if (PICK_DOC == FilePickerConst.REQUEST_CODE_DOC){
            val intent= Intent(requireContext(), FilePickerActivity::class.java)
            intent.putExtra(
                FilePickerActivity.CONFIGS, Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowFiles(false)
                    .setShowImages(true)
                    .setShowAudios(false)
                    .setShowVideos(true)
                    .setMaxSelection(5)
                    .enableImageCapture(true)
                    .enableVideoCapture(true)
                    .setSkipZeroSizeFiles(true)
                    .build()
            )
            startActivityForResult(intent, PICK_DOC)
        }else{
            val intent= Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivityForResult(intent, PICK_DOC)
        }

//        val intent = Intent()
//        intent.type = "image/*"
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);


    }
    fun uploadFile(imagePath: String, itemView: View) {
//        val imagePath=pathList[pos]
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val imageFile = File(imagePath)

        AndroidNetworking.upload(ApiClient.baseUrl + "uploadPhotoVideo")
                .addMultipartFile("files", imageFile)
                .addMultipartParameter(
                    "user_id",
                    SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString()
                )
                .addMultipartParameter(
                    "lang",
                    SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]
                )
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(object : UploadProgressListener {
                    override fun onProgress(bytesUploaded: Long, totalBytes: Long) {
                        itemView.progressBar.progress = (bytesUploaded * 100 / totalBytes).toInt()
                        itemView.txtProgress.text =
                            (bytesUploaded * 100 / totalBytes).toString() + " % " + getString(
                                R.string.uploaded
                            )
                    }
                })
                .getAsString(object : StringRequestListener {
                    override fun onResponse(response: String?) {
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        uploadCount += 1
                        if (uploadCount == pathList.size) {
                            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }

//                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(anError: ANError) {
                        Toast.makeText(context, anError.message, Toast.LENGTH_SHORT).show()
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                })

    }


    fun hasAllPermissionsGranted(grantResults: IntArray): Boolean {
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PICK_DOC) {
            if (grantResults.size > 0) { /*  if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {*/
                if (hasAllPermissionsGranted(grantResults)) {
                   //chooseImageVideo()
                    openCameraDialog()
                } else {
                    LogUtils.shortToast(
                        requireContext(),
                        "Please grant both Camera and Storage permissions"
                    )

                }
            } else if (!hasAllPermissionsGranted(grantResults)) {
                LogUtils.shortToast(
                    requireContext(),
                    "Please grant both Camera and Storage permissions"
                )
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == PICK_DOC && resultCode == Activity.RESULT_OK && data != null) {
            val files: java.util.ArrayList<MediaFile> = data.getParcelableArrayListExtra(
                FilePickerActivity.MEDIA_FILES
            )!!
            if (files.size != 0) {
                pathList.clear()
                uploadCount=0
                for (i in 0 until files.size) {
                    val filePath = FetchPath.getPath(requireContext(), files[i].uri)
                    pathList.add(filePath.toString())
                }
                if(pathList.size==5){
                    mView!!.imgAttach.alpha=0.5f
                    mView!!.imgAttach.isEnabled=false
                }
                else{
                    mView!!.imgAttach.alpha=1f
                    mView!!.imgAttach.isEnabled=true
                }
                mView!!.rvList.layoutManager=LinearLayoutManager(requireContext())
                uploadImageVideoAdapter= UploadImageVideoAdapter(
                    requireContext(),
                    pathList,
                    object : ClickInterface.ClickPosItemViewInterface {
                        override fun clickPosItemView(pos: Int, itemView: View) {
                            /* if(type=="remove"){
                            pathList.removeAt(pos)
                            uploadImageVideoAdapter.notifyDataSetChanged()
                        }*/

                            uploadFile(pathList[pos], itemView)
                        }


                    })
                mView!!.rvList.adapter=uploadImageVideoAdapter
                uploadImageVideoAdapter.notifyDataSetChanged()
            }
        }
        else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) { //previewCapturedImage();
                if (uri != null) {
                    imagePath = ""
                    Log.e("uri", uri.toString())
                    imagePath = uri!!.path!!
                    galleryPhotos.add(uri!!.path!!)
                    setUploadPhotos(galleryPhotos)
                } else {
                    LogUtils.shortToast(requireActivity(), "something went wrong! please try again")
                }
            }
        } else if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == RESULT_OK && data != null) {
//            pathList.clear()
            imagePath = ""
           /* if (data != null) {
                val clipdata = data.clipData
                if (clipdata != null) {
                    for (i in 0 until clipdata.itemCount) {
                        val uri = clipdata.getItemAt(i).uri
                        imagePath = if (uri.toString().startsWith("content")) {
                            FetchPath.getPath(requireActivity(), uri!!)!!
                        } else {
                            uri!!.path!!
                        }

                        if (galleryPhotos.size>10){
                           // LogUtils.longToast(requireContext(), getString(R.string.max_ten_image))
                        }else{
                            galleryPhotos.add(uri.path!!)
                        }
                        *//*    Glide.with(this).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.user)).load("file:///$imagePath").into(civ_profile)*//*
                    }
                    setUploadPhotos(galleryPhotos)
                } else { // handle single photo
                    val uri = data.data
                    imagePath = if (uri.toString().startsWith("content")) {
                        FetchPath.getPath(requireActivity(), uri!!)!!
                    } else {
                        uri!!.path!!
                    }
                    galleryPhotos.add(uri.path!!)
                    setUploadPhotos(galleryPhotos)
                }
            }*/
            val clipdata = data.clipData
            if (clipdata != null) {
                for (i in 0 until clipdata.itemCount) {
                    val uri = clipdata.getItemAt(i).uri
                    imagePath = if (uri.toString().startsWith("content")) {
                        getRealPath(uri)!!
                    } else {
                        uri!!.path!!
                    }

                    if (galleryPhotos.size>10){
                        // LogUtils.longToast(requireContext(), getString(R.string.max_ten_image))
                    }else{
                        galleryPhotos.add(imagePath)
                    }
                    /*    Glide.with(this).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.user)).load("file:///$imagePath").into(civ_profile)*/
                }
                setUploadPhotos(galleryPhotos)
            } else { // handle single photo
                val uri = data.data
                imagePath = if (uri.toString().startsWith("content")) {
                    getRealPath(uri)!!
                } else {
                    uri!!.path!!
                }
                galleryPhotos.add(imagePath)
                setUploadPhotos(galleryPhotos)
            }
        } else {
            // show this if no image is selected
            Toast.makeText(requireContext(), "You haven't picked Image", Toast.LENGTH_LONG).show()
        }
    }

    private fun getRealPath(ur: Uri?): String? {
        var realpath = ""
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

        // Get the cursor
        val cursor: Cursor = requireContext().contentResolver.query(ur!!,
                filePathColumn, null, null, null
        )!!
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        //Log.e("columnIndex", String.valueOf(MediaStore.Images.Media.DATA));
        realpath = cursor.getString(columnIndex)
        cursor.close()
        return realpath
    }

    private fun setUploadPhotos(galleryPhotos: ArrayList<String>) {
        mView!!.rvList.layoutManager=LinearLayoutManager(requireContext())
        uploadImageVideoAdapter= UploadImageVideoAdapter(
                requireContext(),
                galleryPhotos,
                object : ClickInterface.ClickPosItemViewInterface {
                    override fun clickPosItemView(pos: Int, itemView: View) {
                        /* if(type=="remove"){
                        pathList.removeAt(pos)
                        uploadImageVideoAdapter.notifyDataSetChanged()
                    }*/

                        uploadFile(galleryPhotos[pos], itemView)
                    }


                })
        mView!!.rvList.adapter=uploadImageVideoAdapter
        uploadImageVideoAdapter.notifyDataSetChanged()
    }


    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UploadImageVideoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                UploadImageVideoFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}