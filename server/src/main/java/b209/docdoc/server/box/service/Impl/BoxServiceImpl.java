package b209.docdoc.server.box.service.Impl;

import b209.docdoc.server.box.service.BoxService;
import b209.docdoc.server.config.utils.SecurityManager;
import b209.docdoc.server.entity.Receiver;
import b209.docdoc.server.entity.Template;
import b209.docdoc.server.exception.ErrorCode;
import b209.docdoc.server.exception.TemplateNotFoundException;
import b209.docdoc.server.repository.BoxRepository;
import b209.docdoc.server.repository.ReceiverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor

public class BoxServiceImpl implements BoxService {

    private final BoxRepository boxRepository;

    private final ReceiverRepository receiverRepository;


    @Transactional
    @Override
    public Page<Template> getTemplates(String userEmail, List<String> keywords, String nameSort, String createdDateSort, String updatedDateSort, Pageable pageable) {
        String keyword = keywords.size() > 0 ? keywords.get(0) : "";

        Sort sort = Sort.by(Sort.Direction.fromString(nameSort), "templateName")
                .and(Sort.by(Sort.Direction.fromString(createdDateSort), "createdDate"))
                .and(Sort.by(Sort.Direction.fromString(updatedDateSort), "updatedDate"));

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);

        return boxRepository.findAllByKeyword(userEmail, keyword, sortedPageable);
    }

    @Transactional
    @Override
    public Object deleteTemplates(Long templateId) {
        Template template = boxRepository.findById(templateId)
                .orElseThrow(() -> new TemplateNotFoundException(ErrorCode.TEMPLATE_NOT_FOUND));

        Optional.of(template)
                .filter(t -> t.getMember().getMemberIdx() == SecurityManager.getCurrentMember().getIdx())
                .orElseThrow(InvalidParameterException::new);
        template.updateDelete(true);
        boxRepository.save(template);
        return null;
    }

    @Override
    @Transactional
    public Object deleteReceiverTemplates(Long receiverId) {
        Receiver template = receiverRepository.findById(receiverId)
                .orElseThrow(() -> new TemplateNotFoundException(ErrorCode.TEMPLATE_NOT_FOUND));
        template.updateDelete(true);
        receiverRepository.save(template);
        return null;
    }
}