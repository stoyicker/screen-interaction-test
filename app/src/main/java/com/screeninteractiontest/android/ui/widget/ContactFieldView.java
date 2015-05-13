package com.screeninteractiontest.android.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.screeninteractiontest.android.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * View for a plan-text contact field
 *
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

    /**
     * Standard constructor.
     *
     * @param context {@link Context} Context
     * @param attrs   {@link AttributeSet} Attributes specified through XML
     */
    public ContactFieldView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.widget_contact_field, this);
        setOrientation(VERTICAL);
        ButterKnife.inject(this);

        init(context, attrs);
    }

    /**
     * Initializes the values specified through XML, if any
     *
     * @param context {@link Context} Context
     * @param attrs   {@link AttributeSet} Attributes specified through XML
     */
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

    /**
     * Sets the value of the field that this view corresponds to. Because it is known only when
     * the contact is, it is not possible to establish it through XML (yet a custom attribute for
     * doing so is offered for possible future default-cause scenarios).
     *
     * @param mFieldValue {@link String} The field value
     */
    public void setFieldValue(final String mFieldValue) {
        this.mFieldValueView.setText(mFieldValue);
        invalidate();
        requestLayout();
    }

    /**
     * Reacts to the ripple animation end by triggering the click notification on the previously
     * registered listener, if any
     *
     * @param v {@link View} The view receiving the click event
     */
    private void onRippleEnd(final View v) {
        if (mOnClickListener != null)
            mOnClickListener.onClick(v);
    }

    /**
     * Sets a listener for click events.
     *
     * @param mOnClickListener {@link OnClickListener} The listener
     */
    public void setOnClickListener(final OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }
}
