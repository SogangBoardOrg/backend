package board.dev.boardproject.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/api")
class FileController {

    @GetMapping("/test")
    @ResponseBody
    fun test(): String {
        return "hello"
    }
}
