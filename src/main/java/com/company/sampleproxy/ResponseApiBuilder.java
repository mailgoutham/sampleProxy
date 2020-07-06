package com.company.sampleproxy;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpStatus.OK;

@Component
public class ResponseApiBuilder {

    public ResponseApi clientValidationFailure(){
        return responseMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Some of the input fields have failed validation");
    }

    public ResponseApi postSuccessful(){
        return responseMessage(OK.value(), "Success");
    }

    public ResponseApi serverError(){
        return responseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error");
    }

    private ResponseApi responseMessage(int code, String message) {
        return new ResponseApi(code, message);
    }
}
