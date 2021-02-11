package com.example.demo00.view;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.app.Context;

public class MySquareView extends Component {
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

                int shortBorderLength = Math.min(width, height);
                setEstimatedSize(shortBorderLength, shortBorderLength);
                return false;
            }
        });
    }
}
