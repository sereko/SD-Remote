package net.sereko.sd_remote.remote;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sereko on 4/21/14.
 */
public class EvenSpaceGridLayout extends ViewGroup {
    private int mNumColumns;

    public EvenSpaceGridLayout(Context context) {
        super(context);
    }

    public EvenSpaceGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.EvenSpaceGridLayout);
        try {
            mNumColumns = 4;
                    //a.getInteger(
                    //R.styleable.EvenSpaceGridLayout_num_columns, 1);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // Calculate how many cells we need
        int cellCount = countCellsNeeded();

        // Calculate number of rows needed given the number of cells
        int numRows = cellCount / mNumColumns;

        // Calculate width/height of each individual cell
        int cellWidth = widthSize / mNumColumns;
        int cellHeight = heightSize / numRows;

        // Measure children
        measureChildrenViews(cellWidth, cellHeight);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
        }
    }

    private int countCellsNeeded() {

        int cellCount = 0;
        final int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {

            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            int spanColumns = lp.spanColumns;

            // If it's trying to span too far, make it span the maximum possible
            if (spanColumns > mNumColumns) {
                spanColumns = mNumColumns;
            }

            int remainingCellsInRow = mNumColumns - (cellCount % mNumColumns);
            if (remainingCellsInRow - spanColumns < 0) {
                cellCount += remainingCellsInRow + spanColumns;
            } else {
                cellCount += spanColumns;
            }
        }

        // Round off the last row
        if ((cellCount % mNumColumns) != 0) {
            cellCount += mNumColumns - (cellCount % mNumColumns);
        }

        return cellCount;
    }

    private void measureChildrenViews(int cellWidth, int cellHeight) {

        int cellCount = 0;
        final int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {

            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            int spanColumns = lp.spanColumns;

            // If it's trying to span too far, make it span the maximum possible
            if (spanColumns > mNumColumns) {
                spanColumns = mNumColumns;
            }

            // If it can't fit on the current row, skip those cells
            int remainingCellsInRow = mNumColumns - (cellCount % mNumColumns);
            if (remainingCellsInRow - spanColumns < 0) {
                cellCount += remainingCellsInRow;
            }

            // Calculate x and y coordinates of the view
            int x = (cellCount % mNumColumns) * cellWidth;
            int y = (cellCount / mNumColumns) * cellHeight;

            lp.x = x;
            lp.y = y;

            child.measure(MeasureSpec.makeMeasureSpec(cellWidth * spanColumns, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(cellHeight, MeasureSpec.EXACTLY));

            cellCount += spanColumns;
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p.width, p.height);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        int x, y;

        public int spanColumns;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.EvenSpaceGridLayout_LayoutParams);
            try {
                spanColumns = a
                        .getInteger(R.styleable.EvenSpaceGridLayout_LayoutParams_span_columns,1);

                // Can't span less than one column
                if (spanColumns < 1) {
                    spanColumns = 1;
                }
            } finally {
                a.recycle();
            }
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }
    }
}

