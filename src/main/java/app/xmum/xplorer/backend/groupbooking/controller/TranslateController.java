package app.xmum.xplorer.backend.groupbooking.controller;

import app.xmum.xplorer.backend.groupbooking.enums.ErrorCode;
import app.xmum.xplorer.backend.groupbooking.pojo.dto.TranslateDto;
import app.xmum.xplorer.backend.groupbooking.response.ApiResponse;
import app.xmum.xplorer.backend.groupbooking.service.TranslateService;
import app.xmum.xplorer.backend.groupbooking.vo.TranslateVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class TranslateController {
    private final TranslateService translateService;

    public TranslateController(TranslateService translateService) {
        this.translateService = translateService;
    }

    @PostMapping("/api/translate")
    public ApiResponse<TranslateVo> translate(@RequestBody TranslateDto translateDto){
        // 参数校验
        if(translateDto.getText()==null||translateDto.getText().isEmpty()){
            return ApiResponse.fail(ErrorCode.COMMON_INPUT_EMPTY);
        }
        if(translateDto.getDestLang()==null||translateDto.getDestLang().isEmpty()){
            // 目标语言为空则默认为英语
            translateDto.setDestLang("English");
        }

        // 调用接口翻译
        TranslateVo translateVo=translateService.translate(translateDto);
        return ApiResponse.success(translateVo);
    }
}
