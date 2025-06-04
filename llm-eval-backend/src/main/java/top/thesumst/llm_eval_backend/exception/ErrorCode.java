package top.thesumst.llm_eval_backend.exception;

/**
 * Error code definitions for the application
 */
public enum ErrorCode {
    // General errors
    VALIDATION_ERROR("VALIDATION_ERROR", "请求参数无效"),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "资源未找到"),
    DUPLICATE_RESOURCE("DUPLICATE_RESOURCE", "资源已存在"),
    INTERNAL_ERROR("INTERNAL_ERROR", "内部服务器错误"),
    
    // Business logic errors
    INVALID_STATUS_TRANSITION("INVALID_STATUS_TRANSITION", "状态转换无效"),
    MISSING_REQUIRED_ASSOCIATION("MISSING_REQUIRED_ASSOCIATION", "缺少必需的关联"),
    CONSTRAINT_VIOLATION("CONSTRAINT_VIOLATION", "数据约束违反"),
    
    // Data import errors
    IMPORT_DATA_INVALID("IMPORT_DATA_INVALID", "导入数据格式无效"),
    IMPORT_PARSE_ERROR("IMPORT_PARSE_ERROR", "导入数据解析失败"),
    
    // Database errors
    DATABASE_ERROR("DATABASE_ERROR", "数据库操作失败"),
    FOREIGN_KEY_CONSTRAINT("FOREIGN_KEY_CONSTRAINT", "外键约束违反");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
} 