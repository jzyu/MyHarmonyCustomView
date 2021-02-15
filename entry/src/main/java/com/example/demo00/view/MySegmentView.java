package com.example.demo00.view;

import com.example.demo00.utils.Log;
import com.example.demo00.utils.Misc;
import ohos.agp.colors.RgbColor;
import ohos.agp.components.Attr;
import ohos.agp.components.AttrHelper;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.agp.utils.Point;
import ohos.agp.utils.Rect;
import ohos.app.Context;
import ohos.media.image.common.Size;

import java.util.Optional;

public class MySegmentView extends Component {
    public static final String TAG = MySegmentView.class.getSimpleName();

    private String[] mTexts = new String[]{};
    private Rect[] mCacheBounds;
    private Rect[] mTextBounds;

    private ShapeElement mBackgroundDrawable;
    private ShapeElement mSelectedDrawable;

    private int mCurrentIndex;

    private int mTouchSlop;
    private boolean inTapRegion;
    private float mStartX;
    private float mStartY;
    private float mCurrentX;
    private float mCurrentY;

    private int mHorizonGap;
    private int mVerticalGap;

    /**
     * 外边框的width
     */
    private int mBoundWidth = 4;
    /**
     * 内边框的width
     */
    private int mSeparatorWidth = mBoundWidth / 2;

    private int mSingleChildWidth;
    private int mSingleChildHeight;

    private Paint mPaint = new Paint();

    private int mTextSize;
    private boolean mTextBold;
    private Color mBackgroundColors;
    private Color mTextColors;
    private int mCornerRadius;

    private Color DEFAULT_SELECTED_COLOR = new Color(0xFF32ADFF);
    private Color DEFAULT_NORMAL_COLOR = new Color(0xFFFFFFFF);

    private Paint.FontMetrics mCachedFM;

    public enum Direction {
        HORIZONTAL(0), VERTICAL(1);
        int value;

        Direction(int v) {
            value = v;
        }
    }

    private Direction mDirection = Direction.HORIZONTAL;

    private Size estimateSize;
    private boolean hasSetSizeBeforeDraw;


    private DrawTask mDrawTask = new DrawTask() {
        @Override
        public void onDraw(Component component, Canvas canvas) {
            if (!hasSetSizeBeforeDraw) {
                setComponentSize(estimateSize.width, estimateSize.height);
                hasSetSizeBeforeDraw = true;
            }

            doDraw(component, canvas);
        }
    };

    public MySegmentView(Context context) {
        this(context, null, null);
    }

    public MySegmentView(Context context, AttrSet attrSet) {
        this(context, attrSet, null);
    }

    public MySegmentView(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        init(attrSet);
    }

    private void initAttr(AttrSet attrSet) {
        if (attrSet.getAttr("text").isPresent()) {
            mTexts = attrSet.getAttr("text").get().getStringValue().split("\\|");
        }
    }

    private void init(AttrSet attrSet) {
        initAttr(attrSet);

        initProperties();

        addDrawTask(mDrawTask);

        setEstimateSizeListener(new EstimateSizeListener() {
            @Override
            public boolean onEstimateSize(int widthMeasureSpec, int heightMeasureSpec) {
                estimateSize = doEstimateSize(widthMeasureSpec, heightMeasureSpec);
                Log.debug(TAG, "onEstimateSize size=%s", estimateSize);
                return false;
            }
        });
    }

    private void initProperties() {
        mBackgroundDrawable = new ShapeElement();
        mBackgroundDrawable.setCornerRadius(AttrHelper.vp2px(6, getContext()));
        mBackgroundDrawable.setStroke(2, RgbColor.fromArgbInt(getSelectedBGColor().getValue()));
        mBackgroundDrawable.setRgbColor(RgbColor.fromArgbInt(getNormalBGColor().getValue()));
        setBackground(mBackgroundDrawable);

        mSelectedDrawable = new ShapeElement();
        mSelectedDrawable.setRgbColor(RgbColor.fromArgbInt(getSelectedBGColor().getValue()));

        mTextSize = AttrHelper.fp2px(16, getContext());

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        mPaint.setFakeBoldText(mTextBold);
        mCachedFM = mPaint.getFontMetrics();
    }

    private Size doEstimateSize(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = EstimateSpec.getMode(widthMeasureSpec);
        int heightMode = EstimateSpec.getMode(heightMeasureSpec);

        int widthSize = EstimateSpec.getSize(widthMeasureSpec);
        int heightSize = EstimateSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        Log.debug(TAG,
                "doEstimateSize widthSize=%d, heightSize=%d, widthMode=%d, heightMode=%d",
                widthSize, heightSize, widthMode, heightMode);

        if (mTexts != null && mTexts.length > 0) {
            mSingleChildHeight = 0;
            mSingleChildWidth = 0;

            if (mCacheBounds == null || mCacheBounds.length != mTexts.length) {
                mCacheBounds = new Rect[mTexts.length];
            }

            if (mTextBounds == null || mTextBounds.length != mTexts.length) {
                mTextBounds = new Rect[mTexts.length];
            }

            for (int i = 0; i < mTexts.length; i++) {
                String text = mTexts[i];

                if (text != null) {
                    if (mTextBounds[i] == null) {
                        mTextBounds[i] = new Rect();
                    }

                    mTextBounds[i].modify(mPaint.getTextBounds(text));

                    if (mSingleChildWidth < mTextBounds[i].getWidth() + mHorizonGap * 2) {
                        mSingleChildWidth = mTextBounds[i].getWidth() + mHorizonGap * 2;
                    }
                    if (mSingleChildHeight < mTextBounds[i].getHeight() + mVerticalGap * 2) {
                        mSingleChildHeight = mTextBounds[i].getHeight() + mVerticalGap * 2;
                    }

                    Log.debug(TAG,
                            "doEstimateSize i=%d, text=%s, textBound=%s, mSingleChildWidth=%d, mSingleChildHeight=%d",
                            i, mTexts[i], mTextBounds[i], mSingleChildWidth, mSingleChildHeight);
                }
            }

            switch (widthMode) {
                case EstimateSpec.UNCONSTRAINT:
                    if (mDirection == Direction.HORIZONTAL) {
                        if (widthSize <= mSingleChildWidth * mTexts.length) {
                            mSingleChildWidth = widthSize / mTexts.length;
                            width = widthSize;
                        } else {
                            width = mSingleChildWidth * mTexts.length;
                        }
                    } else {
                        width = widthSize <= mSingleChildWidth ? widthSize : mSingleChildWidth;
                    }
                    break;
                case EstimateSpec.PRECISE:
                    width = widthSize;
                    break;
                case EstimateSpec.NOT_EXCEED:
                default:
                    if (mDirection == Direction.HORIZONTAL) {
                        width = mSingleChildWidth * mTexts.length;
                    } else {
                        width = mSingleChildWidth;
                    }
                    break;
            }

            switch (heightMode) {
                case EstimateSpec.NOT_EXCEED:
                    if (mDirection == Direction.VERTICAL) {
                        if (heightSize <= mSingleChildHeight * mTexts.length) {
                            mSingleChildHeight = heightSize / mTexts.length;
                            height = heightSize;
                        } else {
                            height = mSingleChildHeight * mTexts.length;
                        }
                    } else {
                        height = heightSize <= mSingleChildHeight ? heightSize : mSingleChildHeight;
                    }
                    break;
                case EstimateSpec.PRECISE:
                    height = heightSize;
                    break;
                case EstimateSpec.UNCONSTRAINT:
                default:
                    if (mDirection == Direction.VERTICAL) {
                        height = mSingleChildHeight * mTexts.length;
                    } else {
                        height = mSingleChildHeight;
                    }

                    break;
            }

            switch (mDirection) {
                case HORIZONTAL:
                    if (mSingleChildWidth != width / mTexts.length) {
                        mSingleChildWidth = width / mTexts.length;
                    }
                    mSingleChildHeight = height;
                    break;
                case VERTICAL:
                    if (mSingleChildHeight != height / mTexts.length) {
                        mSingleChildHeight = height / mTexts.length;
                    }
                    mSingleChildWidth = width;
                    break;
                default:
                    break;
            }

            for (int i = 0; i < mTexts.length; i++) {
                if (mCacheBounds[i] == null) {
                    mCacheBounds[i] = new Rect();
                }

                if (mDirection == Direction.HORIZONTAL) {
                    mCacheBounds[i].left = i * mSingleChildWidth;
                    mCacheBounds[i].top = 0;
                } else {
                    mCacheBounds[i].left = 0;
                    mCacheBounds[i].top = i * mSingleChildHeight;
                }

                mCacheBounds[i].right = mCacheBounds[i].left + mSingleChildWidth;
                mCacheBounds[i].bottom = mCacheBounds[i].top + mSingleChildHeight;

                Log.debug(TAG,"doEstimateSize i=%d, text=%s, cacheBound=%s", i, mTexts[i], mCacheBounds[i]);
            }
        } else {
            width = widthMode == EstimateSpec.UNCONSTRAINT ? 0 : widthSize;
            height = heightMode == EstimateSpec.UNCONSTRAINT ? 0 : heightSize;
        }

        return new Size(width, height);
    }

    private Color getSelectedTextColor() {
        return DEFAULT_NORMAL_COLOR;
    }

    private Color getNormalTextColor() {
        return DEFAULT_SELECTED_COLOR;
    }

    private Color getSelectedBGColor() {
        return DEFAULT_SELECTED_COLOR;
    }

    private Color getNormalBGColor() {
        return DEFAULT_NORMAL_COLOR;
    }

    private void doDraw(Component component, Canvas canvas) {
        Log.debug(TAG, "doDraw begin");

        if (Misc.isEmpty(mTexts)) {
            Log.debug(TAG, "doDraw text is Empty");
            return;
        }

        for (int i = 0; i < mTexts.length; i++) {
            Log.debug(TAG,
                    "doDraw i=%d, text=%s, textBound=%s, cacheBound=%s",
                    i, mTexts[i], mTextBounds[i], mCacheBounds[i]);

            //draw separate lines
            if (i < mTexts.length - 1) {
                mPaint.setColor(getSelectedBGColor());
                mPaint.setStrokeWidth(mSeparatorWidth);
                if (mDirection == Direction.HORIZONTAL) {
                    canvas.drawLine(
                            new Point(mCacheBounds[i].right, 0),
                            new Point(mCacheBounds[i].right, getHeight()),
                            mPaint);
                } else {
                    canvas.drawLine(
                            new Point(mCacheBounds[i].left, mSingleChildHeight * (i + 1)),
                            new Point(mCacheBounds[i].right, mSingleChildHeight * (i + 1)),
                            mPaint);
                }
            }

            //draw selected drawable
            if (i == mCurrentIndex && mSelectedDrawable != null) {
                int topLeftRadius = 0;
                int topRightRadius = 0;
                int bottomLeftRadius = 0;
                int bottomRightRadius = 0;

                if (mTexts.length == 1) {
                    topLeftRadius = mCornerRadius;
                    bottomLeftRadius = mCornerRadius;
                    topRightRadius = mCornerRadius;
                    bottomRightRadius = mCornerRadius;
                } else {
                    if (mDirection == Direction.HORIZONTAL) {
                        if (i == 0) {
                            topLeftRadius = mCornerRadius;
                            bottomLeftRadius = mCornerRadius;
                        } else if (i == mTexts.length - 1) {
                            topRightRadius = mCornerRadius;
                            bottomRightRadius = mCornerRadius;
                        }
                    } else {
                        if (i == 0) {
                            topLeftRadius = mCornerRadius;
                            topRightRadius = mCornerRadius;
                        } else if (i == mTexts.length - 1) {
                            bottomLeftRadius = mCornerRadius;
                            bottomRightRadius = mCornerRadius;
                        }
                    }
                }

                mSelectedDrawable.setCornerRadiiArray(new float[]{topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius});
                mSelectedDrawable.setBounds(mCacheBounds[i]);
                mSelectedDrawable.drawToCanvas(canvas);

                mPaint.setColor(getSelectedTextColor());
            } else {
                mPaint.setColor(getNormalTextColor());
            }

            //draw texts
            float baseline = mCacheBounds[i].top + ((mSingleChildHeight - mCachedFM.ascent + mCachedFM.descent) / 2) - mCachedFM.descent;
            canvas.drawText(mPaint,
                    mTexts[i],
                    mCacheBounds[i].left + (mSingleChildWidth - mTextBounds[i].getWidth()) / 2, baseline);
        }
    }

    public void setText(String... texts) {
        mTexts = texts;
        invalidate();
    }
}
