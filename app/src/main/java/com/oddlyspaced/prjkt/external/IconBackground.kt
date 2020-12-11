package com.oddlyspaced.prjkt.external

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.oddlyspaced.prjkt.R
import com.stkent.polygondrawingutil.PolygonDrawingUtil

// https://raw.githubusercontent.com/stkent/PolygonDrawingUtil/master/app/src/main/java/com/stkent/polygondrawingutildemo/DemoView.kt
class IconBackground @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val polygonDrawingUtil = PolygonDrawingUtil()
    private val polygonFillPaint = Paint(ANTI_ALIAS_FLAG)
    private val polygonStrokePaint = Paint(ANTI_ALIAS_FLAG)
    private val strokePath = Path()
    private var strokeWidth = 0f

    var numberOfSides = 3
        set(numberOfSides) {
            field = numberOfSides
            invalidate()
        }

    var cornerRadius = 120f
        set(cornerRadius) {
            field = cornerRadius
            invalidate()
        }

    var polygonRotation = 0f
        set(polygonRotation) {
            field = polygonRotation
            invalidate()
        }

    var scale = 1f
        set(scale) {
            field = scale
            invalidate()
        }

    init {
        strokeWidth = 4F

        polygonFillPaint.apply {
            color = ContextCompat.getColor(context, R.color.blue)
            style = Paint.Style.FILL
        }

        polygonStrokePaint.apply {
            color = ContextCompat.getColor(context, R.color.blue)
            strokeWidth = this@IconBackground.strokeWidth
            style = Paint.Style.STROKE
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = (width / 2).toFloat()
        val centerY = (height / 2).toFloat()
        val radius = scale * (width / 2 - strokeWidth)

        // Method 1 (simpler for direct drawing):
        polygonDrawingUtil.drawPolygon(
            canvas,
            this.numberOfSides,
            centerX,
            centerY,
            radius,
            cornerRadius,
            polygonRotation,
            polygonFillPaint
        )

        // Method 2 (allows polygon Path post-processing if desired):
        polygonDrawingUtil.constructPolygonPath(
            strokePath,
            this.numberOfSides,
            centerX,
            centerY,
            radius,
            cornerRadius,
            polygonRotation
        )

        canvas.drawPath(strokePath, polygonStrokePaint)
    }

}