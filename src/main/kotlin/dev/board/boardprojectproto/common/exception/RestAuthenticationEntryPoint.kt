package dev.board.boardprojectproto.common.exception

import com.nimbusds.jose.shaded.json.JSONObject
import dev.board.boardprojectproto.common.enums.ErrorCode
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RestAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        // log.info("Responding with unauthorized error. Message ${authException.message}")
        setResponse(response)
    }

    private fun setResponse(response: HttpServletResponse) {
        response.contentType = "application/json;charset=UTF-8"
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        val responseJson = JSONObject()
        responseJson["status"] = "fail"
        responseJson["data"] = ErrorCode.UNAUTHORIZED.message
        response.writer.print(responseJson)
    }
}
