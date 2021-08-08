package com.wwd.tgdb.dto;

import lombok.Data;

@Data
public class BaseResponse implements Response {

    private Boolean result;

    public BaseResponse(Boolean result) {
        this.result = result;
    }
}
