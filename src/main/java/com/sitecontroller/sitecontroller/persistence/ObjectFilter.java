package com.sitecontroller.sitecontroller.persistence;

public class ObjectFilter extends Filter {

    private boolean placeholder;
    private Object[] values;

    public ObjectFilter() {
    }

    public ObjectFilter(final String name, final FilterType filterType, final boolean placeholder) {
        this(name, null, filterType);
        this.placeholder = placeholder;
    }

    public ObjectFilter(final String name, final Object value, final FilterType filterType) {
        this(name, (value == null ? null :  new Object[]{value}), filterType);
    }

    public ObjectFilter(final String name, final Object[] values, final FilterType filterType) {
        super(name, null, filterType);
        this.values = values;
    }

    public ObjectFilter(final ObjectFilter leftFilter, final ObjectFilter rightFilter, final boolean or) {
        super(leftFilter, rightFilter, or);
    }

    public Object[] getObjectValues() {
        return values;
    }

    public void setObjectValues(final Object[] values) {
        this.values = values;
    }

    public void setObjectValue(final Object value) {
        if (value == null) {
            setObjectValues(null);
        }
        else {
            setObjectValues(new Object[]{value});
        }
    }

    public final boolean isPlaceholder() {
        return placeholder;
    }

    public final void setPlaceholder(final boolean placeholder) {
        this.placeholder = placeholder;
    }
}
