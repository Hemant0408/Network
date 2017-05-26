package com.network.networklibrary.inherit;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by aksha_000 on 12/24/2015.
 */
public class Response extends ResponseValidator {
    private static final String KEY_AUTHENTICATION = "Authentication";
    private static final String KEY_ACCESS = "Access";
    private static final String KEY_SYSTEM = "System";
    private static final String KEY_VERSION = "version";
    private static final String KEY_NETWORK = "Network";
    private static final String KEY_INVALID = "Invalid";
    private static final String KEY_UNKNOWN = "Unknown";
    private static final String KEY_NONE = "None";

    public enum Type {
        NETWORK(KEY_NETWORK),
        INVALID(KEY_INVALID),
        AUTHENTICATION(KEY_AUTHENTICATION),
        ACCESS(KEY_ACCESS),
        SYSTEM(KEY_SYSTEM),
        VERSION(KEY_VERSION),
        NONE(KEY_NONE),
        UNKNOWN(KEY_UNKNOWN);

        private String errorType;

        Type(String errorType) {
            this.errorType = errorType;
        }

        @Override
        public String toString() {
            return errorType;
        }
    }

    @SerializedName("status")
    private Boolean status = false;
    @SerializedName("error")
    private HashMap<String, String[]> errors;

    public Boolean getStatus() {
        return status;
    }

    public boolean hasErrors() {
        return errors != null;
    }

    public Type getErrorType() {
        Type type;

        if (!hasErrors())
            type = Type.NONE;
        else if (errors.containsKey(KEY_AUTHENTICATION))
            type = Type.AUTHENTICATION;
        else if (errors.containsKey(KEY_VERSION))
            type = Type.VERSION;
        else if (errors.containsKey(KEY_ACCESS))
            type = Type.ACCESS;
        else if (errors.containsKey(KEY_SYSTEM))
            type = Type.SYSTEM;
        else if (errors.containsKey(KEY_NETWORK))
            type = Type.NETWORK;
        else if (errors.containsKey(KEY_INVALID))
            type = Type.INVALID;
        else
            type = Type.UNKNOWN;

        return type;
    }

    public boolean containsError(Type type) {

        if (hasErrors())
            return errors.containsKey(type.toString());

        return false;
    }

    public void setErrors(String key, String error) {
        errors = new HashMap<>();
        errors.put(key, new String[]{error});
    }

    public String generateErrorMessage() {
        if (!hasErrors()) {
            return "No errors found";
        }

        String message = null;

        for (String key : errors.keySet()) {
            for (String reason : errors.get(key)) {
                if (TextUtils.isEmpty(message))
                    message = reason;
                else
                    message = message.concat("\n" + reason);
            }
        }

        return message;
    }
}
