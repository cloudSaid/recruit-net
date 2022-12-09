package com.imooc.exceptions;

import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Leo
 * @version 1.0
 * @description: TODO
 * @date 2022-12-06 20:41
 */
@ControllerAdvice
public class GraceExceptionHandler
{

    //自定有运行时异常处理器
    @ExceptionHandler(MyCustomException.class)
    @ResponseBody
    public GraceJSONResult returnMyCustomException(MyCustomException e)
    {
        e.printStackTrace();
        return GraceJSONResult.errorCustom(e.getResponseStatusEnum());
    }

    //token异常处理
    @ExceptionHandler({
            SignatureException.class,
            ExpiredJwtException.class,
            UnsupportedJwtException.class,
            MalformedJwtException.class,
            io.jsonwebtoken.security.SignatureException.class
    })
    @ResponseBody
    public GraceJSONResult returnSignatureException(SignatureException e) {
        e.printStackTrace();
        return GraceJSONResult.exception(ResponseStatusEnum.JWT_SIGNATURE_ERROR);
    }


    //字段校验错误异常处理器
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public GraceJSONResult returnMethodArgumentNotValid(MethodArgumentNotValidException e)
    {
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errorMessages = getErrors(bindingResult);
        return GraceJSONResult.errorMap(errorMessages);
    }
    //解析字段错误信息
    public Map<String,String> getErrors(BindingResult bindingResult)
    {
        Map<String, String> fieldMap = new HashMap<>();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        //将错误所对应的属性字段名和错误信息放入map
        fieldErrors.forEach(fieldError -> fieldMap.put(fieldError.getField(),fieldError.getDefaultMessage()));
        return fieldMap;
    }
}
