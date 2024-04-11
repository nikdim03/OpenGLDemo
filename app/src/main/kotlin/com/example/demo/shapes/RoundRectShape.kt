package com.example.demo.shapes

import android.content.Context
import com.example.demo.R

class RoundRectShape(context: Context) : DefaultShape(context) {

    override var fragmentResId: Int = R.raw.round_rect_texture_shader
}