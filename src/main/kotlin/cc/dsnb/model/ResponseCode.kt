package cc.dsnb.model

import io.ktor.http.*

enum class ResponseCode(
    var httpStatus: HttpStatusCode,
    var code: Int,
    var message: String,
    var data: Map<String, String> = emptyMap()
) {

    // Success (2xx)
    OK(HttpStatusCode.OK, 200, HttpStatusCode.OK.description),
    CREATED(HttpStatusCode.Created, 201, HttpStatusCode.Created.description),
    NO_CONTENT(HttpStatusCode.NoContent, 204, HttpStatusCode.NoContent.description),

    // Error
    // Client errors (4xx)
    // General errors
    INVALID_ID(HttpStatusCode.BadRequest, 400, "ID must be a Integer type number"),
    FORBIDDEN(HttpStatusCode.Forbidden, 403, "Forbidden"),
    TOO_MANY_REQUESTS(HttpStatusCode.TooManyRequests, 429, "Too many requests, please wait for {s}s"),

    // System errors
    INITIALIZE_FIRST(HttpStatusCode.BadRequest, 400, "Please initialize the system first"),
    ALREADY_INITIALIZED(HttpStatusCode.BadRequest, 400, "System already initialized"),
    AVATAR_PATH_MUST_CONTAIN_ID(HttpStatusCode.BadRequest, 400, "avatar_path must contain {id}"),
    SETTING_KEY_EXIST(HttpStatusCode.BadRequest, 400, "Setting key already exists"),
    SETTING_NOT_FOUND(HttpStatusCode.NotFound, 404, "The requested setting not found"),

    // Auth errors
    UNAUTHORIZED(HttpStatusCode.Unauthorized, 401, "Unauthorized"),
    INVALID_CREDENTIALS(HttpStatusCode.Unauthorized, 401, "Wrong username or password"),
    INVALID_TOKEN(HttpStatusCode.Unauthorized, 401, "Invalid Token"),
    TOKEN_EXPIRED(HttpStatusCode.Unauthorized, 401, "The Token has expired"),
    NO_PERMISSION(HttpStatusCode.Forbidden, 403, "You don't have permission to do this"),

    // Role errors
    ROLE_NAME_CAN_NOT_BE_EMPTY(HttpStatusCode.BadRequest, 400, "Role name can not be empty"),
    ROLE_NOT_FOUND(HttpStatusCode.NotFound, 404, "The requested role not found"),
    ROLE_NAME_IN_USE(HttpStatusCode.Conflict, 409, "The requested role name is already in use"),
    CAN_NOT_DELETE_DEFAULT_ROLE(HttpStatusCode.Conflict, 409, "Can not delete default role"),

    // User errors
    USER_NOT_FOUND(HttpStatusCode.NotFound, 404, "The requested user not found"),
    USERNAME_INVALID(
        HttpStatusCode.BadRequest,
        400,
        "Usernames can only start with a letter, and all letters must be lowercase."
    ),
    USERNAME_IN_USE(HttpStatusCode.Conflict, 409, "The requested username is already in use"),
    EMAIL_IN_USE(HttpStatusCode.Conflict, 409, "The requested email is already in use"),
    EMAIL_INVALID(HttpStatusCode.BadRequest, 400, "Invalid email format"),
    EMAIL_VERIFICATION_CODE_INVALID(
        HttpStatusCode.BadRequest,
        400,
        "The verification code is incorrect or has expired"
    ),
    PASSWORD_AT_LEAST_8_CHARACTERS(HttpStatusCode.BadRequest, 400, "Password must be at least 6 characters"),
    CURRENT_PASSWORD_INCORRECT(HttpStatusCode.BadRequest, 400, "The user's current password is incorrect"),
    NEW_PASSWORD_SAME_AS_CURRENT(HttpStatusCode.BadRequest, 400, "The user's new password is same as current password"),

    // Tag errors
    TAG_NOT_FOUND(HttpStatusCode.NotFound, 404, "The requested tag not found"),
    TAG_NAME_IN_USE(HttpStatusCode.Conflict, 409, "The requested tag name is already in use"),
    REQUIRED_USER_ID(HttpStatusCode.BadRequest, 400, "Required user id"),

    // Todo errors
    TODO_DUE_TIME_FORMAT_INVALID(HttpStatusCode.BadRequest, 400, "Invalid due time format"),
    TODO_NOT_FOUND(HttpStatusCode.NotFound, 404, "The requested todo not found"),
    TODO_TITLE_IN_USE(HttpStatusCode.Conflict, 409, "The requested todo title is already in use"),

    // Server errors (5xx)
    // General errors
    INTERNAL_SERVER_ERROR(HttpStatusCode.InternalServerError, 500, "Internal Server Error"),
    SERVICE_UNAVAILABLE(HttpStatusCode.ServiceUnavailable, 503, "Service Unavailable");

}