package com.friends.ggiriggiri.ui.mypages.mypage

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.friends.ggiriggiri.R


class FullScreenImageDialogFragment : DialogFragment() {


    private var imageUri: Uri? = null

    companion object {
        fun newInstance(imageUri: Uri): FullScreenImageDialogFragment {
            val fragment = FullScreenImageDialogFragment()
            val args = Bundle()
            args.putParcelable("imageUri", imageUri)
            fragment.arguments = args
            return fragment
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageUri = arguments?.getParcelable("imageUri")
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_full_screen_image_dialog, container, false)

        // PhotoView 설정
        val photoView = view.findViewById<com.github.chrisbanes.photoview.PhotoView>(R.id.photoView)
        imageUri?.let { uri ->
            photoView.setImageURI(uri)
        }

        // 뒤로가기 버튼 클릭 리스너 설정
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }

        return view
    }
}