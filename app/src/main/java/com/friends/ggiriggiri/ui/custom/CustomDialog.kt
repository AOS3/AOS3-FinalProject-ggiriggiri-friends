package com.friends.ggiriggiri

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import androidx.core.view.isVisible
import com.friends.ggiriggiri.databinding.DialogCustomBinding

class CustomDialog(
    context: Context,
    private val onPositiveClick: () -> Unit,
    private val positiveText: String = "확인",
    private val onNegativeClick: () -> Unit? = {},
    private val negativeText: String? = null,
    private val contentText: String,
    private val icon: Int,
): Dialog(context) {
    private lateinit var binding: DialogCustomBinding


    fun showCustomDialog() {
        // ViewBinding을 사용하여 레이아웃 inflate
        binding = DialogCustomBinding.inflate(LayoutInflater.from(context))

        val dialog = Dialog(context)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        // 아이콘 설정
        binding.imageViewDialogIcon.setImageResource(icon)

        // 내용 설정
        binding.textViewDialogContent.text = contentText

        // Negative 버튼 설정
        if(negativeText == null) {
            binding.buttonDialogNegative.isVisible = false
        }
        else {
            binding.buttonDialogNegative.text = negativeText
            binding.buttonDialogNegative.setOnClickListener {
                onNegativeClick()
                dialog.dismiss()
            }
        }

        // Positive 버튼 설정
        binding.buttonDialogPositive.text = positiveText
        binding.buttonDialogPositive.setOnClickListener {
            onPositiveClick()
            dialog.dismiss()
        }

        dialog.show()
    }
}