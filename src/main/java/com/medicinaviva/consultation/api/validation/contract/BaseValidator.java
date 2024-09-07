package com.medicinaviva.consultation.api.validation.contract;

public abstract class BaseValidator implements Validator {
    protected String fieldName;
    protected Object fieldValue;

    @Override
    public String validate() {
        return "";
    }
}
