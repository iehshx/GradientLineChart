package com.example.myapplication

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.random.Random
import kotlin.random.nextInt

class GradientLineChart : View {

    /**
     * Y轴数量
     */
    private val mYGridValues = 5

    /**
     * 图表实际宽度
     */
    private var mChartWidth: Int = 0

    /**
     * 图表实际高度
     */
    private var mChartHeight: Int = 0

    /**
     * icon和图表的间距
     */
    private var mChartIconGap: Int = DensityUtil.dp2px(12f)

    /**
     * 折线宽度
     */
    private var mLineWidth: Float = 0f

    /**
     * 网格线宽度
     */
    private var mGridLineWidth: Float = 0f

    /**
     * 网格线颜色
     */
    private var mGridLineColor: Int = 0

    /**
     * 背景颜色
     */
    private var mBackgroundColorRes: Int = 0

    /**
     * 各种画笔
     */
    private var mLinePaint: Paint? = null
    private var mBitMapPaint: Paint? = null
    private var mGridLinePaint: Paint? = null
    private var mGradientColor: IntArray? = null

    /**
     * 表格矩形
     */
    private val mRectF = RectF()
    private val mLinePath = Path()

    private val mIconWidth = DensityUtil.dp2px(16.5f)
    private val mIconHeight = DensityUtil.dp2px(16.5f)
    private val mChartTopPadding = mIconHeight / 2
    private val mLineValueList = ArrayList<LineEntity>()


    /**
     * left的icon
     */
    val kuangxiBitmap = (resources.getDrawable(R.drawable.emo_kuangxi_s) as BitmapDrawable).bitmap
    val kaixinBitmap = (resources.getDrawable(R.drawable.emo_kaixin_s) as BitmapDrawable).bitmap
    val defaultBitmap = (resources.getDrawable(R.drawable.emo_default_s) as BitmapDrawable).bitmap
    val bushuangBitmap = (resources.getDrawable(R.drawable.emo_bushuang_s) as BitmapDrawable).bitmap
    val lanBitmap = (resources.getDrawable(R.drawable.emo_lan_s) as BitmapDrawable).bitmap

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        for (i in 0 until 32) {
            mLineValueList.add(LineEntity(i.toFloat(), Random.nextInt(IntRange(0, 4)).toFloat()))
        }
        context?.let {
            mGradientColor = intArrayOf(
                ContextCompat.getColor(it, R.color.color_FF5373),
                ContextCompat.getColor(it, R.color.color_FF9272),
                ContextCompat.getColor(it, R.color.color_1BD7C9),
                ContextCompat.getColor(it, R.color.color_5B7EFF),
                ContextCompat.getColor(it, R.color.color_B65CE8)
            )
        }

        initAttr(attrs, defStyleAttr)
        initPaint()
    }

    private fun initAttr(attrs: AttributeSet?, defStyleAttr: Int) {
        val typeArray =
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.GradientLineChart,
                defStyleAttr,
                0
            )

        mLineWidth = typeArray.getDimension(
            R.styleable.GradientLineChart_tc_lineWidth,
            DensityUtil.dp2px(3f).toFloat()
        )
        mGridLineWidth = typeArray.getDimension(
            R.styleable.GradientLineChart_tc_grid_line_width,
            DensityUtil.dp2px(0.5f).toFloat()
        )
        mGridLineColor = typeArray.getColor(
            R.styleable.GradientLineChart_tc_grid_line_color,
            ContextCompat.getColor(context, R.color.color_EAEAEA)
        )
        mBackgroundColorRes = typeArray.getColor(
            R.styleable.GradientLineChart_tc_background_color,
            ContextCompat.getColor(context, R.color.white)
        )

        typeArray.recycle()
    }

    private fun initPaint() {
        mLinePaint = Paint()
        mLinePaint?.isAntiAlias = true
        mLinePaint?.style = Paint.Style.STROKE
        mLinePaint?.strokeWidth = mLineWidth
        mBitMapPaint = Paint()
        mBitMapPaint?.isAntiAlias = true
        mBitMapPaint?.isFilterBitmap = true
        mBitMapPaint?.isDither = true
        mGridLinePaint = Paint()
        mGridLinePaint?.isAntiAlias = true
        mGridLinePaint?.style = Paint.Style.FILL
        mGridLinePaint?.color = mGridLineColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(viewWidth, viewHeight)

        mChartWidth = viewWidth - paddingStart - paddingEnd - (mIconWidth + mChartIconGap)
        mChartHeight = viewHeight - paddingTop - paddingBottom - DensityUtil.dp2px(16f)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        //设置画布背景色
        canvas?.drawColor(mBackgroundColorRes)
        //绘制网格线
        drawGradLine(canvas)
        //绘制icon
        drawGradIcon(canvas)
        //绘制折线
        drawLine(canvas)
        canvas?.restore()
    }

    private fun drawGradIcon(canvas: Canvas?) {
        val yGridDistance =
            (mChartHeight - mYGridValues * mGridLineWidth.toInt()) / (mYGridValues - 1)
        var imageTop = 0
        var imageBottom = imageTop + this.mIconHeight
        canvas?.drawBitmap(
            kuangxiBitmap,
            Rect(0, 0, kuangxiBitmap.width, kuangxiBitmap.height),
            Rect(0, imageTop, mIconWidth, imageBottom),
            mBitMapPaint
        )
        imageTop =
            paddingTop + yGridDistance * 1 + mGridLineWidth.toInt() * 1 - (DensityUtil.dp2px(15f) / 2)
        imageBottom = imageTop + this.mIconHeight
        canvas?.drawBitmap(
            kaixinBitmap,
            Rect(0, 0, kaixinBitmap.width, kaixinBitmap.height),
            Rect(
                0,
                imageTop,
                mIconWidth,
                imageBottom
            ),
            mBitMapPaint
        )


        imageTop =
            paddingTop + yGridDistance * 2 + mGridLineWidth.toInt() * 2 - (DensityUtil.dp2px(15f) / 2)

        imageBottom = imageTop + this.mIconHeight
        canvas?.drawBitmap(
            defaultBitmap,
            Rect(0, 0, defaultBitmap.width, defaultBitmap.height),
            Rect(
                0,
                imageTop,
                mIconWidth,
                imageBottom
            ),
            mBitMapPaint
        )

        imageTop =
            paddingTop + yGridDistance * 3 + mGridLineWidth.toInt() * 3 - (DensityUtil.dp2px(15f) / 2)
        imageBottom = imageTop + this.mIconHeight
        canvas?.drawBitmap(
            bushuangBitmap,
            Rect(0, 0, bushuangBitmap.width, bushuangBitmap.height),
            Rect(
                0,
                imageTop,
                mIconWidth,
                imageBottom
            ),
            mBitMapPaint
        )
        imageTop =
            paddingTop + yGridDistance * 4 + mGridLineWidth.toInt() * 4 - (DensityUtil.dp2px(15f) / 2)
        imageBottom = imageTop + this.mIconHeight
        canvas?.drawBitmap(
            lanBitmap,
            Rect(0, 0, lanBitmap.width, lanBitmap.height),
            Rect(
                0,
                imageTop,
                mIconWidth,
                imageBottom
            ),
            mBitMapPaint
        )
    }


    /**
     * 绘制网格线
     */
    private fun drawGradLine(canvas: Canvas?) {
        mGridLinePaint?.let {

            //Y轴网格线间距
            val yGridDistance =
                (mChartHeight - mYGridValues * mGridLineWidth) / (mYGridValues - 1)
            for (index in 0 until mYGridValues) {
                val left = DensityUtil.dp2px(17f) + mChartIconGap + paddingStart.toFloat()
                val top =
                    paddingTop.toFloat() + index * yGridDistance + index * mGridLineWidth + (if (index == 0) mChartTopPadding else 0)
                val right = left + mChartWidth
                val bottom = top + mGridLineWidth
                mRectF.set(left, top, right, bottom)
                canvas?.drawRect(mRectF, it)
            }

            //X轴网格线间距
//            val xGridDistance = (chartWidth - xGridValues * gridLineWidth) / (xGridValues - 1)
//            for (index in 0 until xGridValues) {
//                val left = paddingStart + xGridDistance * index + gridLineWidth * index
//                val top = paddingTop.toFloat()
//                val right = left + gridLineWidth
//                val bottom = top + chartHeight
//                rectF.set(left, top, right, bottom)
//                canvas?.drawRect(rectF, gridLinePaint!!)
//            }
        }
    }

    /**
     * 绘制折线
     */
    private fun drawLine(canvas: Canvas?) {
        mLinePath.reset()
        val yGridDistance = (mChartHeight - mYGridValues * mGridLineWidth) / (mYGridValues - 1)

        for ((index, linePoint) in mLineValueList.withIndex()) {
            val pointX = DensityUtil.dp2px(17f) + mChartIconGap +
                    (linePoint.xValue / (mLineValueList.size - 1)) * mChartWidth + (if (linePoint.xValue == (mLineValueList.size - 1).toFloat()) 0f else mGridLineWidth)
            val pointY =
                mChartHeight - ((linePoint.yValue * yGridDistance) + (mGridLineWidth * (linePoint.yValue))) + (if (linePoint.yValue == 4f) mChartTopPadding else 0)

            Log.e(
                "tag",
                "pos = $index pointX = $pointX pointY = $pointY  yGridDistance = $yGridDistance  chartWidth=$mChartWidth  chartHeight= $mChartHeight   linePoint.yValue = ${linePoint.yValue}"
            )
            if (index == 0) {
                mLinePath.moveTo(pointX, pointY)
            } else {
                mLinePath.lineTo(pointX, pointY)
            }

        }

        mLinePaint?.shader = createLineGradient(mGradientColor!!)
        canvas?.drawPath(mLinePath, mLinePaint!!)
    }

    private fun createLineGradient(gradientColor: IntArray): LinearGradient {
        return LinearGradient(
            0f,
            0f,
            0f,
            height.toFloat(),
            gradientColor,
            null,
            Shader.TileMode.CLAMP
        )
    }

    fun refreshData() {
        mLineValueList.clear()
        for (i in 0 until 32) {
            mLineValueList.add(LineEntity(i.toFloat(), Random.nextInt(IntRange(0, 4)).toFloat()))
        }
        invalidate()
    }
}
