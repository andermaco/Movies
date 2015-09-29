package com.example.movies.util;

public class TaskParams {
    public enum Function {
        TRAILERS {
            public String toString() {
                return "trailers";
            }
        },
        REVIEWS {
            public String toString() {
                return "reviews";
            }
        }
    }

    private Integer mId;
    private Function function;

    public TaskParams(Integer mId, Function function) {
        this.mId = mId;
        this.function = function;
    }

    public final Integer getmId() {
        return mId;
    }

    public final void setmId(Integer mId) {
        this.mId = mId;
    }

    public final Function getFunction() {
        return function;
    }

    public final void setFunction(Function function) {
        this.function = function;
    }
}
