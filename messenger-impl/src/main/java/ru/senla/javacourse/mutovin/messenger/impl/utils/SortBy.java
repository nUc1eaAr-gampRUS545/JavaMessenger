package ru.senla.javacourse.mutovin.messenger.impl.utils;

public class SortBy {
    private String field;
    private SortDirection direction;

    public SortBy(String field,SortDirection direction) {
        this.field = field;
        this.direction = direction;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public void setDirection(SortDirection direction) {
        this.direction = direction;
    }

    public static SortBy defaultSort() {
        return new SortBy("id",SortDirection.ASC);
    }

    public enum SortDirection {
        ASC,DESC;
    }

}
