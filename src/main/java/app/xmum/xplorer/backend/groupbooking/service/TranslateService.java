package app.xmum.xplorer.backend.groupbooking.service;

import app.xmum.xplorer.backend.groupbooking.client.TranslateClient;
import app.xmum.xplorer.backend.groupbooking.pojo.dto.TranslateDto;
import app.xmum.xplorer.backend.groupbooking.utils.PromptUtil;
import app.xmum.xplorer.backend.groupbooking.vo.TranslateVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TranslateService {

    private final TranslateClient translateClient;

    public TranslateService(TranslateClient translateClient) {
        this.translateClient = translateClient;
    }

    public TranslateVo translate(TranslateDto translateDto) {
        // 1. 生成对应的prompt
        String prompt= PromptUtil.generateTranslatePrompt(translateDto);

        // 2. 调用AI进行翻译
        TranslateVo translateVo=translateClient.translateByAi(prompt);

        // TODO:考虑进行对应的审核过滤机制或者写库。。。

        return translateVo;
    }
}
