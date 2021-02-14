package com.example.demo00.view;

import com.example.demo00.utils.Log;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.app.Context;

public class MySquareView extends Component {
    public static final String TAG = MySquareView.class.getSimpleName();

    public MySquareView(Context context) {
        this(context, null, null);
    }

    public MySquareView(Context context, AttrSet attrSet) {
        this(context, attrSet, null);
    }

    public MySquareView(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
    }

    private void init() {
        setEstimateSizeListener(new EstimateSizeListener() {
            @Override
            public boolean onEstimateSize(int widthSpec, int heightSpec) {
                int width = Component.EstimateSpec.getSize(widthSpec);
                int height = Component.EstimateSpec.getSize(widthSpec);
                Log.debug(TAG,"onEstimateSize width=%{public}d, height=%{public}d", width, height);

                int shortBorderLength = Math.min(width, height);
                setEstimatedSize(shortBorderLength, shortBorderLength);
                return true;
            }
        });
    }
}
