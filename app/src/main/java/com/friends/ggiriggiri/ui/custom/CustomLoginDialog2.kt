package com.friends.ggiriggiri.ui.custom

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.view.isVisible
import com.friends.ggiriggiri.databinding.DialogCustomLogin2Binding

class CustomLoginDialog2(
    context: Context,
    private val onPositiveClick: () -> Unit? = {},
    private val positiveText: String = "확인",
    private val onNegativeClick: () -> Unit? = {},
    private val negativeText: String? = null,
    private val contentText: String,
    private val agreeTypeText:String,
    private val loadUrl:String
) :Dialog(context){

    private lateinit var binding: DialogCustomLogin2Binding

    fun showCustomDialog() {
        binding = DialogCustomLogin2Binding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        dialog.setCancelable(false)

        //제목설정
        binding.contentText.text = contentText
        binding.buttonDialogPositive.text = positiveText
        binding.cbPrivacyPolicyCheck.text = agreeTypeText

        // 체크박스 상태에 따라 확인 버튼 분기
        binding.buttonDialogPositive.setOnClickListener {
            //둘다 동의했어야 확인버튼 활성화
            if(binding.cbPrivacyPolicyCheck.isChecked) {
                onPositiveClick()
                dialog.dismiss()
            }else{
                Toast.makeText(context,"동의해주셔야 서비스 이용이 가능합니다",Toast.LENGTH_SHORT).show()
            }
        }

        //취소버튼
        if (negativeText == null) {
            binding.buttonDialogNegative.isVisible = false
        } else {
            binding.buttonDialogNegative.text = negativeText
            binding.buttonDialogNegative.setOnClickListener {
                onNegativeClick()
                dialog.dismiss()
            }
        }

        // 웹뷰 설정
        binding.wbPrivacyPolicy.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            loadUrl(loadUrl)
        }
//        binding.wbTermsOfUse.apply {
//            webViewClient = WebViewClient()
//            settings.javaScriptEnabled = true
//            loadUrl("https://sites.google.com/view/ggiriggiri-privacy-policy")
//        }

        dialog.show()




    }

}