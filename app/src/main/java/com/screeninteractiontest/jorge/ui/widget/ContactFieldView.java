package com.screeninteractiontest.jorge.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.screeninteractiontest.jorge.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Jorge Antonio Diaz-Benito Soriano (github.com/Stoyicker).
 */
public final class ContactFieldView extends LinearLayout {

    @InjectView(R.id.field_type)
    TextView mFieldTypeView;

    @InjectView(R.id.field_value)
    TextView mFieldValueView;

    @InjectView(R.id.field_value_ripple)
    ListenableRippleView mFieldValueRippleView;

    private OnClickListener mOnClickListener;

    public ContactFieldView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.widget_contact_field, this);
        setOrientation(VERTICAL);
        ButterKnife.inject(this);

        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        final TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ContactFieldView,
                0, 0);

        String fieldType, fieldValue;

        try {
            fieldType = a.getString(R.styleable.ContactFieldView_fieldType);
            fieldValue = a.getString(R.styleable.ContactFieldView_fieldValue);
        } finally {
            a.recycle();
        }

        if (!TextUtils.isEmpty(fieldType))
            mFieldTypeView.setText(fieldType);

        if (!TextUtils.isEmpty(fieldValue))
            mFieldValueView.setText(fieldValue);

        mFieldValueRippleView.setOnRippleCompleteListener(new ListenableRippleView.IRippleComplete() {
            @Override
            public void onComplete(final ListenableRippleView rippleView) {
                ContactFieldView.this.onRippleEnd(ContactFieldView.this);
            }
        });
    }

    public void setFieldValue(final String mFieldValue) {
        this.mFieldValueView.setText(mFieldValue);
        invalidate();
        requestLayout();
    }

    private void onRippleEnd(final View v) {
        if (mOnClickListener != null)
            mOnClickListener.onClick(v);
    }

    public void setOnClickListener(final OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }
}
