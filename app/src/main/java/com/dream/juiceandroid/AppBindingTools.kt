package com.dream.juiceandroid

import android.content.res.ColorStateList
import android.graphics.Color
import android.provider.CalendarContract.Colors
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter

@BindingAdapter(value = ["showLinksLabel"])
fun showLinksLabel(v: AppCompatImageView, links: Int) {

    when {
        links > 9 -> {
            ImageViewCompat.setImageTintList(v, ColorStateList.valueOf(Color.RED))
            v.setImageResource(R.drawable.ic_whatshot_black_96dp)
        }
        links > 4 -> {
            ImageViewCompat.setImageTintList(v, ColorStateList.valueOf(Color.GREEN))
            v.setImageResource(R.drawable.ic_whatshot_black_96dp)
        }
        else -> {
            v.setImageResource(R.drawable.ic_person_black_96dp)
        }
    }
}