package com.example.demo.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix
import android.util.Log
import android.view.Surface
import com.example.demo.utils.GlUtil
import com.example.demo.shapes.CurvedShape
import com.example.demo.shapes.DefaultShape
import com.example.demo.shapes.PerspectiveInvertShape
import com.example.demo.shapes.PerspectiveShape
import com.example.demo.shapes.RoundRectShape
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SampleRenderer(context: Context) : Renderer {
    private var textureSurfaceListener: TextureSurfaceListener? = null

    // private var backgroundComponent = SurfaceComponent()
    private var frontComponent = SurfaceComponent()

    private val rectShape = DefaultShape(context)
    private val roundRectShape = RoundRectShape(context)
    private val perspectiveShape = PerspectiveShape(context)
    private val perspectiveInvertShape = PerspectiveInvertShape(context)
    private val curvedShape = CurvedShape(context)

    private var surfaceWidth = 0
    private var surfaceHeight = 0
    private var widthSpace = 0
    private var heightSpace = 0
    private var videoWidth = 0
    private var videoHeight = 0

    private var curvedBufferId = -1
    private var curvedTextureId = -1

    private val mTextureMatrix = FloatArray(16) // texture matrix used for image rendering

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        rectShape.init()
        roundRectShape.init()
        perspectiveShape.init()
        perspectiveInvertShape.init()
        curvedShape.init()
        Matrix.setIdentityM(mTextureMatrix, 0);
        // backgroundComponent.createSurface()
        frontComponent.createSurface()

        textureSurfaceListener?.onSurfaceCreated(
            Surface(frontComponent.getSurfaceTexture()),
            // Surface(backgroundComponent.getSurfaceTexture()),
            Surface(frontComponent.getSurfaceTexture())
        )
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height
        widthSpace = (width * wSpaceRatio).toInt()
        heightSpace = (height * hSpaceRatio).toInt()
        videoWidth = (width - (widthSpace * 3)) / 2
        videoHeight = (height - (heightSpace * 3)) / 2

        perspectiveShape.setPerspectiveM(videoWidth, videoHeight, 10f)

        perspectiveInvertShape.setPerspectiveM(videoWidth, videoHeight, -10f)

        if (curvedBufferId < 0) {
            val framebufferId = IntArray(1)
            val frameTextureId = IntArray(1)
            GlUtil.genFrameBuffer(
                framebufferId, frameTextureId, surfaceWidth,
                surfaceHeight, GLES30.GL_RGBA, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE
            )
            curvedTextureId = frameTextureId[0]
            curvedBufferId = framebufferId[0]
            Log.d(TAG, "onSurfaceChanged curvedTextureId: $curvedTextureId, curvedBufferId: $curvedBufferId")
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glViewport(0, 0, surfaceWidth, surfaceHeight)
        GLES30.glClearColor(0f, 0.0f, 0f, 0.0f)
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT or GLES30.GL_COLOR_BUFFER_BIT)

        frontComponent.updateTexImage()

        onDrawOES()

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        GLES30.glClearColor(0f, 0.0f, 0f, 0.0f)
        GLES30.glViewport(0, 0, surfaceWidth, surfaceHeight)
        curvedShape.drawFrame(curvedTextureId, textureMatrix = mTextureMatrix)
    }

    private fun onDrawOES() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, curvedBufferId)
        GLES20.glClearColor(0.0f, 0.0f, 0f, 0.0f)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        GLES30.glViewport(0, 0, surfaceWidth, surfaceHeight)
        // rectShape.drawFrame(backgroundComponent.getTextureId(), textureMatrix = backgroundComponent.getTextureMatrix())

        var x = widthSpace
        var y = heightSpace
        GLES30.glViewport(x, y, videoWidth, videoHeight)
        perspectiveShape.drawFrame(
            frontComponent.getTextureId(),
            textureMatrix = frontComponent.getTextureMatrix()
        ) // bottom left corner

        x = videoWidth + widthSpace * 2
        GLES30.glViewport(x, y, videoWidth, videoHeight)
        perspectiveInvertShape.drawFrame(
            frontComponent.getTextureId(),
            textureMatrix = frontComponent.getTextureMatrix()
        ) // bottom right corner

        y = videoHeight + heightSpace * 2
        GLES30.glViewport(x, y, videoWidth, videoHeight)
        roundRectShape.drawFrame(
            frontComponent.getTextureId(),
            textureMatrix = frontComponent.getTextureMatrix()
        ) // top right corner

        x = widthSpace
        y = videoHeight + heightSpace * 2
        GLES30.glViewport(x, y, videoWidth, videoHeight)
        rectShape.drawFrame(
            frontComponent.getTextureId(),
            textureMatrix = frontComponent.getTextureMatrix()
        ) // top left corner
    }

    fun setTextureSurfaceListener(listener: TextureSurfaceListener) {
        this.textureSurfaceListener = listener
    }

    interface TextureSurfaceListener {
        fun onSurfaceCreated(surface1: Surface, surface2: Surface)
    }

    private companion object {
        private const val TAG = "SampleRenderer"
        const val wSpaceRatio = (1f / 11f)
        const val hSpaceRatio = (1f / 9f)
    }
}