package app.xmum.xplorer.backend.groupbooking.client;

import app.xmum.xplorer.backend.groupbooking.constants.ApiConstant;
import app.xmum.xplorer.backend.groupbooking.enums.ErrorCode;
import app.xmum.xplorer.backend.groupbooking.exception.HttpException;
import app.xmum.xplorer.backend.groupbooking.exception.TranslateException;
import app.xmum.xplorer.backend.groupbooking.vo.TranslateVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.stereotype.Service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TranslateClient {
    //private final ChatLanguageModel gemini;
    private final ObjectMapper objectMapper;

    public TranslateClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public TranslateVo translateByAi(String prompt) {
        // 定义 JSON Schema 的结构
        JsonSchema jsonSchema = createJsonSchema();

        // 定义模型的响应格式
        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(ResponseFormatType.JSON)
                .jsonSchema(jsonSchema)
                .build();

        // 调用 Gemini 模型生成内容
        ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(ApiConstant.API_KEY)
                .modelName("gemini-1.5-flash")
                .responseFormat(responseFormat)
                .build();

        // 获取模型输出并解析JSON
        String jsonResponse = gemini.generate(prompt);
        TranslateVo result;
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            // 创建并填充Result
            result = new TranslateVo();
            result.setTranslateText(rootNode.get("text").toString());
            result.setText(rootNode.get("translateText").toString());
        } catch (JsonProcessingException e) {
            throw new TranslateException(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.getMessage());
        }

        return result;
    }
    /**
     * 创建 JSON Schema
     *
     * @return JsonSchema 对象
     */
    private JsonSchema createJsonSchema() {
        return JsonSchema.builder()
                .rootElement(JsonObjectSchema.builder()
                        .addStringProperty("text") // 原始文本
                        .addStringProperty("translateText") // 翻译后的文本
                        .build())
                .build();
    }
}
