package b209.docdoc.server.api.template.dto.Request;

import b209.docdoc.server.api.template.dto.Response.WidgetResDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DocumentTemplateSaveReqDTO {
	List<String> toEmail;
	List<String> toName;
	String templateDeadline;
	String templateName;

	List<WidgetResDTO> widgetResDTO;

	@Builder
	public DocumentTemplateSaveReqDTO(List<String> toEmail, List<String> toName,
									  String templateDeadline, String templateName, List<WidgetResDTO> widgetResDTO) {
		this.toEmail = toEmail;
		this.toName = toName;
		this.templateDeadline = templateDeadline;
		this.templateName = templateName;
		this.widgetResDTO = widgetResDTO;
	}

	public static DocumentTemplateSaveReqDTO of(List<String> toEmail, List<String> toName,
												String templateDeadline, String templateName, List<WidgetResDTO> widgetResDTO) {
		return DocumentTemplateSaveReqDTO.builder()
				.toEmail(toEmail)
				.toName(toName)
				.templateDeadline(templateDeadline)
				.templateName(templateName)
				.widgetResDTO(widgetResDTO)
				.build();
	}
}