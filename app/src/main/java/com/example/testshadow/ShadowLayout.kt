package com.example.testshadow


import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import java.util.*


/**
 * Android custom shadow layout, can replace your CardView
 *
 * @author Henley
 * @date 2019/4/19 17:49
 */
class ShadowLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ViewGroup(context, attrs, defStyleAttr) {
    private var foregroundDrawable: Drawable? = null
    private val selfBounds = Rect()
    private val overlayBounds = Rect()
    private var foregroundDrawGravity = Gravity.FILL
    private val foregroundDrawInPadding = true
    private var foregroundDrawBoundsChanged = false
    private val bgPaint = Paint()
    private var shadowColor: Int
    private var foregroundColor: Int
    private var backgroundColor: Int
    private var shadowRadius: Float
    private var shadowDx: Float
    private var shadowDy: Float
    private var cornerRadiusTL = 0f
    private var cornerRadiusTR = 0f
    private var cornerRadiusBL = 0f
    private var cornerRadiusBR = 0f
    private var shadowMarginTop = 0
    private var shadowMarginLeft = 0
    private var shadowMarginRight = 0
    private var shadowMarginBottom = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var maxHeight = 0
        var maxWidth = 0
        var childState = 0
        val layoutParams = layoutParams
        setMeasuredDimension(
            getDefaultSize(0, widthMeasureSpec),
            getDefaultSize(0, heightMeasureSpec)
        )
        val shadowMeasureWidthMatchParent =
            layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT
        val shadowMeasureHeightMatchParent =
            layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT
        var widthSpec = widthMeasureSpec
        if (shadowMeasureWidthMatchParent) {
            val childWidthSize = measuredWidth - shadowMarginRight - shadowMarginLeft
            widthSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY)
        }
        var heightSpec = heightMeasureSpec
        if (shadowMeasureHeightMatchParent) {
            val childHeightSize = measuredHeight - shadowMarginTop - shadowMarginBottom
            heightSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY)
        }
        val child = getChildAt(0)
        if (child.visibility != GONE) {
            measureChildWithMargins(child, widthSpec, 0, heightSpec, 0)
            val lp = child.layoutParams as LayoutParams
            maxWidth = if (shadowMeasureWidthMatchParent) {
                Math.max(
                    maxWidth,
                    child.measuredWidth + lp.leftMargin + lp.rightMargin
                )
            } else {
                Math.max(
                    maxWidth,
                    child.measuredWidth + shadowMarginLeft + shadowMarginRight + lp.leftMargin + lp.rightMargin
                )
            }
            maxHeight = if (shadowMeasureHeightMatchParent) {
                Math.max(maxHeight, child.measuredHeight + lp.topMargin + lp.bottomMargin)
            } else {
                Math.max(
                    maxHeight,
                    child.measuredHeight + shadowMarginTop + shadowMarginBottom + lp.topMargin + lp.bottomMargin
                )
            }
            childState = combineMeasuredStates(childState, child.measuredState)
        }
        maxWidth += paddingLeft + paddingRight
        maxHeight += paddingTop + paddingBottom
        maxHeight = Math.max(maxHeight, suggestedMinimumHeight)
        maxWidth = Math.max(maxWidth, suggestedMinimumWidth)
        val drawable = foreground
        if (drawable != null) {
            maxHeight = Math.max(maxHeight, drawable.minimumHeight)
            maxWidth = Math.max(maxWidth, drawable.minimumWidth)
        }
        setMeasuredDimension(
            resolveSizeAndState(
                maxWidth,
                if (shadowMeasureWidthMatchParent) widthMeasureSpec else widthSpec,
                childState
            ),
            resolveSizeAndState(
                maxHeight,
                if (shadowMeasureHeightMatchParent) heightMeasureSpec else heightSpec,
                childState shl MEASURED_HEIGHT_STATE_SHIFT
            )
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        layoutChildren(left, top, right, bottom, false /* no force left gravity */)
        if (changed) {
            foregroundDrawBoundsChanged = changed
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun layoutChildren(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        forceLeftGravity: Boolean
    ) {
        val count = childCount
        val parentLeft = paddingLeftWithForeground
        val parentRight = right - left - paddingRightWithForeground
        val parentTop = paddingTopWithForeground
        val parentBottom = bottom - top - paddingBottomWithForeground
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                val lp = child.layoutParams as LayoutParams
                val width = child.measuredWidth
                val height = child.measuredHeight
                var childLeft = 0
                var childTop: Int
                var gravity = lp.gravity
                if (gravity == -1) {
                    gravity = DEFAULT_CHILD_GRAVITY
                }
                val layoutDirection = layoutDirection
                val absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection)
                val verticalGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
                when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                    Gravity.CENTER_HORIZONTAL -> childLeft =
                        parentLeft + (parentRight - parentLeft - width) / 2 +
                                lp.leftMargin - lp.rightMargin + shadowMarginLeft - shadowMarginRight
                    Gravity.RIGHT -> if (!forceLeftGravity) {
                        childLeft = parentRight - width - lp.rightMargin - shadowMarginRight
                    }
                    Gravity.LEFT -> childLeft = parentLeft + lp.leftMargin + shadowMarginLeft
                    else -> childLeft = parentLeft + lp.leftMargin + shadowMarginLeft
                }
                childTop = when (verticalGravity) {
                    Gravity.TOP -> parentTop + lp.topMargin + shadowMarginTop
                    Gravity.CENTER_VERTICAL -> parentTop + (parentBottom - parentTop - height) / 2 +
                            lp.topMargin - lp.bottomMargin + shadowMarginTop - shadowMarginBottom
                    Gravity.BOTTOM -> parentBottom - height - lp.bottomMargin - shadowMarginBottom
                    else -> parentTop + lp.topMargin + shadowMarginTop
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (canvas != null) {
            val w = measuredWidth
            val h = measuredHeight
            val path = ShapeUtils.roundedRect(
                shadowMarginLeft.toFloat(),
                shadowMarginTop.toFloat(),
                (w - shadowMarginRight).toFloat(),
                (h - shadowMarginBottom).toFloat(),
                cornerRadiusTL,
                cornerRadiusTR,
                cornerRadiusBR,
                cornerRadiusBL
            )
            canvas.drawPath(path, bgPaint)
            canvas.clipPath(path)
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (canvas != null) {
            canvas.save()
            val w = measuredWidth
            val h = measuredHeight
            val path = ShapeUtils.roundedRect(
                shadowMarginLeft.toFloat(),
                shadowMarginTop.toFloat(),
                (w - shadowMarginRight).toFloat(),
                (h - shadowMarginBottom).toFloat(),
                cornerRadiusTL,
                cornerRadiusTR,
                cornerRadiusBR,
                cornerRadiusBL
            )
            canvas.clipPath(path)
            drawForeground(canvas)
            canvas.restore()
        }
    }

    private fun updatePaintShadow(
        radius: Float = shadowRadius,
        dx: Float = shadowDx,
        dy: Float = shadowDy,
        color: Int = shadowColor
    ) {
        bgPaint.setShadowLayer(radius, dx, dy, color)
        invalidate()
    }

    private val shadowMarginMax: Float
        private get() {
            var max = 0f
            val margins = Arrays.asList(
                shadowMarginLeft,
                shadowMarginTop,
                shadowMarginRight,
                shadowMarginBottom
            )
            for (value in margins) {
                max = Math.max(max, value.toFloat())
            }
            return max
        }

    private fun drawForeground(canvas: Canvas) {
        if (foregroundDrawable != null) {
            if (foregroundDrawBoundsChanged) {
                foregroundDrawBoundsChanged = false
                val w = right - left
                val h = bottom - top
                if (foregroundDrawInPadding) {
                    selfBounds[0, 0, w] = h
                } else {
                    selfBounds[paddingLeft, paddingTop, w - paddingRight] = h - paddingBottom
                }
                Gravity.apply(
                    foregroundDrawGravity, foregroundDrawable!!.intrinsicWidth,
                    foregroundDrawable!!.intrinsicHeight, selfBounds, overlayBounds
                )
                foregroundDrawable!!.bounds = overlayBounds
            }
            foregroundDrawable!!.draw(canvas)
        }
    }

    override fun getForeground(): Drawable {
        return foregroundDrawable!!
    }

    override fun setForeground(foreground: Drawable) {
        if (foregroundDrawable != null) {
            foregroundDrawable!!.callback = null
            unscheduleDrawable(foregroundDrawable)
        }
        foregroundDrawable = foreground
        updateForegroundColor()
        if (foreground != null) {
            setWillNotDraw(false)
            foreground.callback = this
            if (foreground.isStateful) {
                foreground.state = drawableState
            }
            if (foregroundDrawGravity == Gravity.FILL) {
                val padding = Rect()
                foreground.getPadding(padding)
            }
        }
        requestLayout()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        foregroundDrawBoundsChanged = true
    }

    override fun getForegroundGravity(): Int {
        return foregroundDrawGravity
    }

    override fun setForegroundGravity(foregroundGravity: Int) {
        var foregroundGravity = foregroundGravity
        if (foregroundDrawGravity != foregroundGravity) {
            if (foregroundGravity and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK == 0) {
                foregroundGravity = foregroundGravity or Gravity.START
            }
            if (foregroundGravity and Gravity.VERTICAL_GRAVITY_MASK == 0) {
                foregroundGravity = foregroundGravity or Gravity.TOP
            }
            foregroundDrawGravity = foregroundGravity
            if (foregroundDrawGravity == Gravity.FILL && foregroundDrawable != null) {
                val padding = Rect()
                foregroundDrawable!!.getPadding(padding)
            }
            requestLayout()
        }
    }

    override fun verifyDrawable( who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === foregroundDrawable
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        if (foregroundDrawable != null) {
            foregroundDrawable!!.jumpToCurrentState()
        }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (foregroundDrawable != null) {
            if (foregroundDrawable!!.isStateful) {
                foregroundDrawable!!.state = drawableState
            }
        }
    }

    private fun updateForegroundColor() {
        if (foregroundDrawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (foregroundDrawable is RippleDrawable) {
                    (foregroundDrawable as RippleDrawable).setColor(
                        ColorStateList.valueOf(
                            foregroundColor
                        )
                    )
                }
            } else {
                foregroundDrawable!!.setColorFilter(foregroundColor, PorterDuff.Mode.SRC_ATOP)
            }
        }
    }

    override fun drawableHotspotChanged(x: Float, y: Float) {
        super.drawableHotspotChanged(x, y)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (foregroundDrawable != null) {
                foregroundDrawable!!.setHotspot(x, y)
            }
        }
    }

    /**
     * Gets the shadow color.
     *
     * @attr ref R.styleable#ShadowLayout_shadowColor
     * @see .setShadowColor
     */
    fun getShadowColor(): Int {
        return shadowColor
    }

    /**
     * Sets the shadow color.
     *
     * @param shadowColor A color value in the form 0xAARRGGBB.
     * @attr ref R.styleable#ShadowLayout_shadowColor
     * @see .getShadowColor
     */
    fun setShadowColor( shadowColor: Int) {
        this.shadowColor = shadowColor
        updatePaintShadow(shadowRadius, shadowDx, shadowDy, shadowColor)
    }

    /**
     * Gets the foreground color.
     *
     * @attr ref R.styleable#ShadowLayout_foregroundColor
     * @see .setForegroundColor
     */
    fun getForegroundColor(): Int {
        return foregroundColor
    }

    /**
     * Sets the foreground color.
     *
     * @param foregroundColor A color value in the form 0xAARRGGBB.
     * @attr ref R.styleable#ShadowLayout_foregroundColor
     * @see .getForegroundColor
     */
    fun setForegroundColor( foregroundColor: Int) {
        this.foregroundColor = foregroundColor
        updateForegroundColor()
    }

    /**
     * Gets the background color.
     *
     * @attr ref R.styleable#ShadowLayout_backgroundColor
     * @see .setBackgroundColor
     */
    fun getBackgroundColor(): Int {
        return backgroundColor
    }

    /**
     * Sets the background color.
     *
     * @param backgroundColor A color value in the form 0xAARRGGBB.
     * @attr ref R.styleable#ShadowLayout_backgroundColor
     * @see .getBackgroundColor
     */
    override fun setBackgroundColor(backgroundColor: Int) {
        this.backgroundColor = backgroundColor
        invalidate()
    }

    /**
     * Gets the shadow radius in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_shadowRadius
     * @see .setShadowRadius
     */
    fun getShadowRadius(): Float {
        return if (shadowRadius > shadowMarginMax && shadowMarginMax != 0f) {
            shadowMarginMax
        } else {
            shadowRadius
        }
    }

    /**
     * Sets the shadow radius in pixels.
     *
     * @param shadowRadius The shadow radius in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowRadius
     * @see .getShadowRadius
     */
    fun setShadowRadius(shadowRadius: Float) {
        var shadowRadius = shadowRadius
        if (shadowRadius > shadowMarginMax && shadowMarginMax != 0f) {
            shadowRadius = shadowMarginMax
        }
        this.shadowRadius = shadowRadius
        updatePaintShadow(shadowRadius, shadowDx, shadowDy, shadowColor)
    }

    /**
     * Gets the shadow dx in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_shadowDx
     * @see .setShadowDx
     */
    fun getShadowDx(): Float {
        return shadowDx
    }

    /**
     * Sets the shadow dx in pixels.
     *
     * @param shadowDx The shadow dx in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowDx
     * @see .getShadowDx
     */
    fun setShadowDx(shadowDx: Float) {
        this.shadowDx = shadowDx
        updatePaintShadow(shadowRadius, shadowDx, shadowDy, shadowColor)
    }

    /**
     * Gets the shadow dy in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_shadowDy
     * @see .setShadowDy
     */
    fun getShadowDy(): Float {
        return shadowDy
    }

    /**
     * Sets the shadow dy in pixels.
     *
     * @param shadowDy The shadow dy in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowDy
     * @see .getShadowDy
     */
    fun setShadowDy(shadowDy: Float) {
        this.shadowDy = shadowDy
        updatePaintShadow(shadowRadius, shadowDx, shadowDy, shadowColor)
    }

    /**
     * Gets the top shadow margin in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_shadowMarginTop
     * @see .setShadowMarginTop
     */
    fun getShadowMarginTop(): Int {
        return shadowMarginTop
    }

    /**
     * Sets the top shadow margin in pixels.
     *
     * @param shadowMarginTop The top shadow margin in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowMarginTop
     * @see .getShadowMarginTop
     */
    fun setShadowMarginTop(shadowMarginTop: Int) {
        this.shadowMarginTop = shadowMarginTop
        updatePaintShadow()
    }

    /**
     * Gets the left shadow margin in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_shadowMarginLeft
     * @see .setShadowMarginLeft
     */
    fun getShadowMarginLeft(): Int {
        return shadowMarginLeft
    }

    /**
     * Sets the left shadow margin in pixels.
     *
     * @param shadowMarginLeft The left shadow margin in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowMarginLeft
     * @see .getShadowMarginLeft
     */
    fun setShadowMarginLeft(shadowMarginLeft: Int) {
        this.shadowMarginLeft = shadowMarginLeft
        updatePaintShadow()
    }

    /**
     * Gets the right shadow margin in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_shadowMarginRight
     * @see .setShadowMarginRight
     */
    fun getShadowMarginRight(): Int {
        return shadowMarginRight
    }

    /**
     * Sets the right shadow margin in pixels.
     *
     * @param shadowMarginRight The right shadow margin in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowMarginRight
     * @see .getShadowMarginRight
     */
    fun setShadowMarginRight(shadowMarginRight: Int) {
        this.shadowMarginRight = shadowMarginRight
        updatePaintShadow()
    }

    /**
     * Gets the bottom shadow margin in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_shadowMarginBottom
     * @see .setShadowMarginBottom
     */
    fun getShadowMarginBottom(): Int {
        return shadowMarginBottom
    }

    /**
     * Sets the bottom shadow margin in pixels.
     *
     * @param shadowMarginBottom The bottom shadow margin in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowMarginBottom
     * @see .getShadowMarginBottom
     */
    fun setShadowMarginBottom(shadowMarginBottom: Int) {
        this.shadowMarginBottom = shadowMarginBottom
        updatePaintShadow()
    }

    /**
     * Sets the shadow margin in pixels.
     *
     * @param left   The left shadow margin in pixels.
     * @param top    The top shadow margin in pixels.
     * @param right  The right shadow margin in pixels.
     * @param bottom The bottom shadow margin in pixels.
     * @attr ref R.styleable#ShadowLayout_shadowMargin
     */
    fun setShadowMargin(left: Int, top: Int, right: Int, bottom: Int) {
        shadowMarginLeft = left
        shadowMarginTop = top
        shadowMarginRight = right
        shadowMarginBottom = bottom
        requestLayout()
        invalidate()
    }

    /**
     * Gets the top-left corner radius in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_cornerRadiusTL
     * @see .setCornerRadiusTL
     */
    fun getCornerRadiusTL(): Float {
        return cornerRadiusTL
    }

    /**
     * Sets the top-left corner radius in pixels.
     *
     * @param cornerRadiusTL The top-left corner radius in pixels.
     * @attr ref R.styleable#ShadowLayout_cornerRadiusTL
     * @see .getCornerRadiusTL
     */
    fun setCornerRadiusTL(cornerRadiusTL: Float) {
        this.cornerRadiusTL = cornerRadiusTL
        invalidate()
    }

    /**
     * Gets the top-right corner radius in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_cornerRadiusTR
     * @see .setCornerRadiusTR
     */
    fun getCornerRadiusTR(): Float {
        return cornerRadiusTR
    }

    /**
     * Sets the top-right corner radius in pixels.
     *
     * @param cornerRadiusTR The top-right corner radius in pixels.
     * @attr ref R.styleable#ShadowLayout_cornerRadiusTR
     * @see .getCornerRadiusTR
     */
    fun setCornerRadiusTR(cornerRadiusTR: Float) {
        this.cornerRadiusTR = cornerRadiusTR
        invalidate()
    }

    /**
     * Gets the bottom-left corner radius in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_cornerRadiusBL
     * @see .setCornerRadiusBL
     */
    fun getCornerRadiusBL(): Float {
        return cornerRadiusBL
    }

    /**
     * Sets the bottom-left corner radius in pixels.
     *
     * @param cornerRadiusBL The bottom-left corner radius in pixels.
     * @attr ref R.styleable#ShadowLayout_cornerRadiusBL
     * @see .getCornerRadiusBL
     */
    fun setCornerRadiusBL(cornerRadiusBL: Float) {
        this.cornerRadiusBL = cornerRadiusBL
        invalidate()
    }

    /**
     * Gets the bottom-right corner radius in pixels.
     *
     * @attr ref R.styleable#ShadowLayout_cornerRadiusBR
     * @see .setCornerRadiusBR
     */
    fun getCornerRadiusBR(): Float {
        return cornerRadiusBR
    }

    /**
     * Sets the bottom-right corner radius in pixels.
     *
     * @param cornerRadiusBR The bottom-right corner radius in pixels.
     * @attr ref R.styleable#ShadowLayout_cornerRadiusBR
     * @see .getCornerRadiusBR
     */
    fun setCornerRadiusBR(cornerRadiusBR: Float) {
        this.cornerRadiusBR = cornerRadiusBR
        invalidate()
    }

    /**
     * Sets the corner radius in pixels.
     *
     * @param tl The top-left corner radius in pixels.
     * @param tr The top-right corner radius in pixels.
     * @param br The bottom-right corner radius in pixels.
     * @param bl The bottom-left corner radius in pixels.
     * @attr ref R.styleable#ShadowLayout_cornerRadius
     */
    fun setCornerRadius(tl: Float, tr: Float, br: Float, bl: Float) {
        cornerRadiusTL = tl
        cornerRadiusTR = tr
        cornerRadiusBR = br
        cornerRadiusBL = bl
        invalidate()
    }

    private val paddingLeftWithForeground: Int
        private get() = paddingLeft
    private val paddingRightWithForeground: Int
        private get() = paddingRight
    private val paddingTopWithForeground: Int
        private get() = paddingTop
    private val paddingBottomWithForeground: Int
        private get() = paddingBottom

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    override fun getAccessibilityClassName(): CharSequence {
        return ShadowLayout::class.java.name
    }

    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

    /**
     * Per-child layout information for layouts that support margins.
     *
     * @attr ref android.R.styleable#ShadowLayout_Layout_layout_gravity
     */
    class LayoutParams : MarginLayoutParams {
        var gravity = UNSPECIFIED_GRAVITY

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.ShadowLayout_Layout)
            gravity = a.getInt(R.styleable.ShadowLayout_Layout_layout_gravity, UNSPECIFIED_GRAVITY)
            a.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height) {}

        /**
         * Creates a new set of layout parameters with the specified width, height and weight.
         *
         * @param width   the width, either [.MATCH_PARENT], [.WRAP_CONTENT] or a fixed size in pixels
         * @param height  the height, either [.MATCH_PARENT], [.WRAP_CONTENT] or a fixed size in pixels
         * @param gravity the gravity
         * @see android.view.Gravity
         */
        constructor(width: Int, height: Int, gravity: Int) : super(width, height) {
            this.gravity = gravity
        }

        constructor( source: ViewGroup.LayoutParams?) : super(source) {}
        constructor( source: MarginLayoutParams?) : super(source) {}

        /**
         * Copy constructor. Clones the width, height, margin values, and
         * gravity of the source.
         *
         * @param source The layout params to copy from.
         */
        constructor( source: LayoutParams) : super(source) {
            gravity = source.gravity
        }

        companion object {
            const val UNSPECIFIED_GRAVITY = -1
        }
    }

    companion object {
        private const val DEFAULT_CHILD_GRAVITY = Gravity.TOP or Gravity.START
        private const val SIZE_UNSET = -1
        private const val SIZE_DEFAULT = 0
    }

    init {
        val a =
            getContext().obtainStyledAttributes(attrs, R.styleable.ShadowLayout, defStyleAttr, 0)
        shadowColor = a.getColor(
            R.styleable.ShadowLayout_shadowColor,
            ContextCompat.getColor(context!!, R.color.shadow_view_default_shadow_color)
        )
        foregroundColor = a.getColor(
            R.styleable.ShadowLayout_foregroundColor,
            ContextCompat.getColor(context!!, R.color.shadow_view_foreground_color_dark)
        )
        backgroundColor = a.getColor(R.styleable.ShadowLayout_backgroundColor, Color.WHITE)
        shadowDx = a.getFloat(R.styleable.ShadowLayout_shadowDx, 0f)
        shadowDy = a.getFloat(R.styleable.ShadowLayout_shadowDy, 1f)
        shadowRadius =
            a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowRadius, SIZE_DEFAULT).toFloat()
        val drawable = a.getDrawable(R.styleable.ShadowLayout_android_foreground)
        if (drawable != null) {
            foreground = drawable
        }
        val shadowMargin =
            a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowMargin, SIZE_UNSET)
        if (shadowMargin >= 0) {
            shadowMarginTop = shadowMargin
            shadowMarginLeft = shadowMargin
            shadowMarginRight = shadowMargin
            shadowMarginBottom = shadowMargin
        } else {
            shadowMarginTop =
                a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowMarginTop, SIZE_DEFAULT)
            shadowMarginLeft =
                a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowMarginLeft, SIZE_DEFAULT)
            shadowMarginRight =
                a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowMarginRight, SIZE_DEFAULT)
            shadowMarginBottom =
                a.getDimensionPixelSize(R.styleable.ShadowLayout_shadowMarginBottom, SIZE_DEFAULT)
        }
        val cornerRadius =
            a.getDimensionPixelSize(R.styleable.ShadowLayout_cornerRadius, SIZE_UNSET).toFloat()
        if (cornerRadius >= 0) {
            cornerRadiusTL = cornerRadius
            cornerRadiusTR = cornerRadius
            cornerRadiusBL = cornerRadius
            cornerRadiusBR = cornerRadius
        } else {
            cornerRadiusTL =
                a.getDimensionPixelSize(R.styleable.ShadowLayout_cornerRadiusTL, SIZE_DEFAULT)
                    .toFloat()
            cornerRadiusTR =
                a.getDimensionPixelSize(R.styleable.ShadowLayout_cornerRadiusTR, SIZE_DEFAULT)
                    .toFloat()
            cornerRadiusBL =
                a.getDimensionPixelSize(R.styleable.ShadowLayout_cornerRadiusBL, SIZE_DEFAULT)
                    .toFloat()
            cornerRadiusBR =
                a.getDimensionPixelSize(R.styleable.ShadowLayout_cornerRadiusBR, SIZE_DEFAULT)
                    .toFloat()
        }
        a.recycle()
        bgPaint.color = backgroundColor
        bgPaint.isAntiAlias = true
        bgPaint.style = Paint.Style.FILL
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        setWillNotDraw(false)
        ViewCompat.setBackground(this, null)
    }
}
