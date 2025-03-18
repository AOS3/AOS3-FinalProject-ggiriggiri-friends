package com.friends.ggiriggiri.ui.custom

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import androidx.core.view.isVisible
import com.friends.ggiriggiri.databinding.DialogCustomLoginBinding

class CustomLoginDialog(
    context: Context,
    private val onPositiveClick: () -> Unit,
    private val positiveText: String = "확인",
    private val onNegativeClick: () -> Unit? = {},
    private val negativeText: String? = null,
    private val onViewPrivacyPolicy: () -> Unit,  // 웹뷰 프래그먼트로 이동하는 콜백 추가
    private val contentText: String,
    private val icon: Int,
) : Dialog(context) {

    private lateinit var binding: DialogCustomLoginBinding

    fun showCustomDialog() {
        binding = DialogCustomLoginBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(context)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        dialog.setCancelable(false)

        binding.imageViewDialogIcon.setImageResource(icon)
        binding.textViewDialogContent.text = contentText

        // 체크박스 상태에 따라 확인 버튼 활성화 설정
        binding.buttonDialogPositive.isEnabled = false  // 기본적으로 비활성화
        binding.cbCustomDialogPrivacyPolicyCheck.setOnCheckedChangeListener { _, isChecked ->
            binding.buttonDialogPositive.isEnabled = isChecked
        }

        // 네거티브 버튼 설정
        if (negativeText == null) {
            binding.buttonDialogNegative.isVisible = false
        } else {
            binding.buttonDialogNegative.text = negativeText
            binding.buttonDialogNegative.setOnClickListener {
                onNegativeClick()
                dialog.dismiss()
            }
        }

        // 포지티브 버튼 설정
        binding.buttonDialogPositive.text = positiveText
        binding.buttonDialogPositive.setOnClickListener {
            onPositiveClick()
            dialog.dismiss()
        }

        // 웹뷰 이동 버튼 추가
        binding.buttonViewPrivacyPolicy.setOnClickListener {
            onViewPrivacyPolicy()
            dialog.dismiss()
        }

        dialog.show()
    }
}

