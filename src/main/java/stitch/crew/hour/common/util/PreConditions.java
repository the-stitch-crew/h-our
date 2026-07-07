package stitch.crew.hour.common.util;

import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;

public final class PreConditions {
    public static void validate(boolean expression, ErrorCode errorCode){
        if (!expression) throw new BusinessException(errorCode);
    }
    public static void check(boolean expression, ErrorCode errorCode){
        if (expression) throw new BusinessException(errorCode);
    }
}
