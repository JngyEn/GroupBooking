package app.xmum.xplorer.backend.groupbooking.utils;

import app.xmum.xplorer.backend.groupbooking.client.TranslateClient;
import app.xmum.xplorer.backend.groupbooking.constants.PromptConstants;
import app.xmum.xplorer.backend.groupbooking.pojo.dto.TranslateDto;

public class PromptUtil {

    public static String generateTranslatePrompt(TranslateDto translateDto) {
        // 你可以在 PromptConstants.translatePrompt 中放一些通用前缀，
        // 比如 “You are a professional translator...” 等，也可以在这里自行写。
        StringBuilder prompt = new StringBuilder(PromptConstants.translatePrompt);

        // 如果你的业务需要指定源语言，也可以拼接 sourceLang
        // 例如：Source Language: + translateDto.getSourceLang()
        // 如果不需要，可以忽略这部分

        prompt.append("\n")
                .append("Destination Language: ")
                .append(translateDto.getDestLang())
                .append("\n");

        prompt.append("User Input: ")
                .append(translateDto.getText())
                .append("\n\n");

        // 也可以在结尾加一些对翻译要求的提示
        prompt.append("Please translate the above text accurately while preserving its original meaning, tone, and context.")
                .append("\n")
                .append("Return only the translated text, without any additional commentary or formatting.");

        // 返回拼接完成的 prompt
        return prompt.toString();
    }
}
