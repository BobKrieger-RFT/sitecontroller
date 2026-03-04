package com.sitecontroller.sitecontroller.persistence;

public class Filter {

    private String name;
    private String value;
    private FilterType filterType;
    private boolean group;
    private boolean or;
    private Filter leftFilter;
    private Filter rightFilter;

    public Filter() {
    }

    public Filter(final String name, final String value, final FilterType filterType) {
        this.name = name;
        this.value = value;
        this.filterType = filterType;
    }

    public Filter(final Filter leftFilter, final Filter rightFilter, final boolean or) {
        this.leftFilter = leftFilter;
        this.rightFilter = rightFilter;
        this.group = true;
        this.or = or;
    }

    // Gets/Sets

    public final String getName() {
        return name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final String getValue() {
        return value;
    }

    public final void setValue(final String value) {
        this.value = value;
    }

    public final FilterType getFilterType() {
        return filterType;
    }

    public final void setFilterType(final FilterType filterType) {
        this.filterType = filterType;
    }

    public final boolean isGroup() {
        return group;
    }

    public final void setGroup(final boolean group) {
        this.group = group;
    }

    public final boolean isOr() {
        return or;
    }

    public final void setOr(final boolean or) {
        this.or = or;
    }

    public final Filter getLeftFilter() {
        return leftFilter;
    }

    public final void setLeftFilter(final Filter leftFilter) {
        this.leftFilter = leftFilter;
    }

    public final Filter getRightFilter() {
        return rightFilter;
    }

    public final void setRightFilter(final Filter rightFilter) {
        this.rightFilter = rightFilter;
    }
}
