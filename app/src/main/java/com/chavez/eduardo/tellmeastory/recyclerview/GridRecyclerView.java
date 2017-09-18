package com.chavez.eduardo.tellmeastory.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;

/**
 * Created by eduardo3150 on 9/18/17.
 */

public class GridRecyclerView extends RecyclerView {
    public GridRecyclerView(Context context) {
        super(context);
    }

    public GridRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GridRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params, int index, int count) {
        final LayoutManager layoutManager = getLayoutManager();
        if (getAdapter() != null && layoutManager instanceof GridLayoutManager) {
            GridLayoutAnimationController.AnimationParameters animationParameters =
                    (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;

            if (animationParameters == null) {
                animationParameters = new GridLayoutAnimationController.AnimationParameters();
                params.layoutAnimationParameters = animationParameters;
            }

            animationParameters.count = count;
            animationParameters.index = index;

            final int columns = ((GridLayoutManager) layoutManager).getSpanCount();
            animationParameters.columnsCount = columns;
            animationParameters.rowsCount = count / columns;

            final int invertedIntex = count - 1 - index;
            animationParameters.column = columns - 1 - (invertedIntex % columns);
            animationParameters.row = animationParameters.rowsCount - 1 - invertedIntex / columns;

        } else {
            super.attachLayoutAnimationParameters(child, params, index, count);
        }
    }
}
