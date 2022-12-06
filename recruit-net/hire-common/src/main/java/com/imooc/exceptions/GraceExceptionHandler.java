package com.imooc.exceptions;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Leo
 * @version 1.0
 * @description: TODO
 * @date 2022-12-06 20:41
 */
@ControllerAdvice
public class GraceExceptionHandler
{

    @ExceptionHandler(MyCustomException.class)
    @ResponseBody
    public GraceJSONResult returnMyCustomException(MyCustomException e)
    {
        e.printStackTrace();
        return GraceJSONResult.errorCustom(e.getResponseStatusEnum());

    }
}
