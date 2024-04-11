package com.example.demo.renderer

import android.graphics.SurfaceTexture
import android.util.Log
import com.example.demo.utils.GlUtil

class SurfaceComponent {

    private var textureId = -1
    private var surfaceTexture: SurfaceTexture? = null
    private val fFrameLock = Any()
    private var frameAvailable = false
    private var textureMatrix = FloatArray(16)

    fun createSurface() {
        textureId = GlUtil.createExternalTexture()
        Log.d(TAG, "createSurface textureId: $textureId")
        surfaceTexture = SurfaceTexture(textureId).apply {
            setOnFrameAvailableListener {
                synchronized(fFrameLock) {
                    frameAvailable = true
                }
            }
        }
    }

    fun updateTexImage() {
        synchronized(fFrameLock) {
            if (frameAvailable) {
                frameAvailable = false
                surfaceTexture?.also {
                    it.updateTexImage()
                    it.getTransformMatrix(textureMatrix)
                }
            }
        }
    }

    fun getSurfaceTexture(): SurfaceTexture? {
        return surfaceTexture
    }

    fun getTextureMatrix(): FloatArray {
        return textureMatrix
    }

    fun getTextureId(): Int {
        return textureId
    }

    private companion object {
        private const val TAG = "SurfaceComponent"
    }
}