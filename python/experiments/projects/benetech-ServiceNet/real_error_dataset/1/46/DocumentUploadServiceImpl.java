package org.benetech.servicenet.service.impl;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.benetech.servicenet.adapter.DataAdapterFactory;
import org.benetech.servicenet.adapter.MultipleDataAdapter;
import org.benetech.servicenet.adapter.SingleDataAdapter;
import org.benetech.servicenet.adapter.shared.model.FileInfo;
import org.benetech.servicenet.adapter.shared.model.MultipleImportData;
import org.benetech.servicenet.adapter.shared.model.SingleImportData;
import org.benetech.servicenet.converter.FileConverterFactory;
import org.benetech.servicenet.domain.DataImportReport;
import org.benetech.servicenet.domain.DocumentUpload;
import org.benetech.servicenet.domain.UserProfile;
import org.benetech.servicenet.repository.DocumentUploadRepository;
import org.benetech.servicenet.service.DataImportReportService;
import org.benetech.servicenet.service.DocumentUploadService;
import org.benetech.servicenet.service.MongoDbService;
import org.benetech.servicenet.service.StringGZIPService;
import org.benetech.servicenet.service.UserService;
import org.benetech.servicenet.service.dto.DocumentUploadDTO;
import org.benetech.servicenet.service.mapper.DocumentUploadMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing DocumentUpload.
 */
@Service
public class DocumentUploadServiceImpl implements DocumentUploadService {

    private final Logger log = LoggerFactory.getLogger(DocumentUploadServiceImpl.class);

    @Autowired
    private DocumentUploadRepository documentUploadRepository;

    @Autowired
    private DocumentUploadMapper documentUploadMapper;

    @Autowired
    private DataImportReportService dataImportReportService;

    @Autowired
    private UserService userService;

    @Autowired
    private MongoDbService mongoDbService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private StringGZIPService stringGZIPService;

    @Override
    public DocumentUploadDTO uploadFile(MultipartFile file, String delimiter, String providerName)
        throws IllegalArgumentException, IOException {
        DataImportReport report = new DataImportReport().startDate(ZonedDateTime.now())
            .systemAccount(providerName);

        String parsedDocument = FileConverterFactory.getConverter(file, delimiter).convert(file);
        byte[] parseDocumentBytes = stringGZIPService.compress(parsedDocument);
        String parsedDocumentId = mongoDbService.saveParsedDocument(encodeBase64String(parseDocumentBytes));
        String originalDocumentId = mongoDbService.saveOriginalDocument(file.getBytes());

        DocumentUpload documentUpload = saveForCurrentUser(new DocumentUpload(originalDocumentId, parsedDocumentId));
        report.setDocumentUpload(documentUpload);

        return importDataIfNeeded(getRealProviderName(providerName), parsedDocument, report, true);
    }

    @Override
    public DocumentUploadDTO uploadApiData(String json, String providerName, DataImportReport report)
        throws IllegalArgumentException {

        String originalDocumentId = mongoDbService.saveOriginalDocument(json.getBytes());
        DocumentUpload documentUpload = saveForSystemUser(new DocumentUpload(originalDocumentId, null));
        report.setDocumentUpload(documentUpload);

        return importDataIfNeeded(providerName, json, report, false);
    }

    @Override
    public boolean processFiles(final List<FileInfo> fileInfoList, final String providerName) {
        Optional<MultipleDataAdapter> adapter = new DataAdapterFactory(applicationContext)
            .getMultipleDataAdapter(providerName);
        if (adapter.isEmpty()) {
            // No need to process files again if provider is not of MultipleDataAdapter type
            return true;
        }


        DataImportReport report = new DataImportReport().startDate(ZonedDateTime.now()).systemAccount(providerName);
        List<String> parsedDocuments = new ArrayList<>();
        List<DocumentUpload> documentUploads = new ArrayList<>();

        fillLists(fileInfoList, parsedDocuments, documentUploads);

        long startTime = System.currentTimeMillis();
        log.info("Data upload for " + providerName + " has started");
        DataImportReport reportToSave = adapter
            .map(a -> a.importData(new MultipleImportData(parsedDocuments, documentUploads, report, providerName,
                true)))
            .orElse(report);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        //TODO: Remove time counting logic (#264)
        log.info("Data upload for " + providerName + " took: " + elapsedTime + "ms");
        saveReport(reportToSave);

        return true;
    }

    @Override
    public DocumentUploadDTO save(DocumentUploadDTO documentUploadDTO) {
        log.debug("Request to save DocumentUpload : {}", documentUploadDTO);

        DocumentUpload documentUpload = documentUploadMapper.toEntity(documentUploadDTO);
        documentUpload = documentUploadRepository.save(documentUpload);
        return documentUploadMapper.toDto(documentUpload);
    }

    @Override
    public DocumentUpload saveForCurrentUser(DocumentUpload documentUpload) {
        Optional<UserProfile> currentUser = userService.getCurrentUserProfileOptional();
        if (currentUser.isPresent()) {
            documentUpload.setDateUploaded(ZonedDateTime.now(ZoneId.systemDefault()));
            documentUpload.setUploader(currentUser.get());
            return documentUploadRepository.save(documentUpload);
        } else {
            throw new IllegalStateException("No current user found");
        }
    }

    @Override
    public DocumentUpload saveForSystemUser(DocumentUpload documentUpload) {
        Optional<UserProfile> currentUser = userService.getSystemUserProfile();
        if (currentUser.isPresent()) {
            documentUpload.setDateUploaded(ZonedDateTime.now(ZoneId.systemDefault()));
            documentUpload.setUploader(currentUser.get());
            return documentUploadRepository.save(documentUpload);
        } else {
            throw new IllegalStateException("No system user found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentUploadDTO> findAll() {
        log.debug("Request to get all DocumentUploads");
        return documentUploadRepository.findAll().stream()
            .map(documentUploadMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentUploadDTO> findAll(Pageable pageable) {
        log.debug("Request to get all DocumentUploads");
        return documentUploadRepository.findAll(pageable)
            .map(documentUploadMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DocumentUploadDTO> findOne(UUID id) {
        log.debug("Request to get DocumentUpload : {}", id);
        return documentUploadRepository.findById(id)
            .map(documentUploadMapper::toDto);
    }

    @Override
    public void delete(UUID id) {
        log.debug("Request to delete DocumentUpload : {}", id);
        documentUploadRepository.deleteById(id);
    }

    private DocumentUploadDTO importDataIfNeeded(String providerName, String parsedDocument, DataImportReport report,
                                                 boolean isFileUpload) {
        Optional<SingleDataAdapter> adapter = new DataAdapterFactory(applicationContext)
            .getSingleDataAdapter(providerName);

        DataImportReport reportToSave = adapter
            .map((a) -> {
                long startTime = System.currentTimeMillis();
                log.info("Data upload for " + providerName + " has started");
                DataImportReport importReport = a.importData(new SingleImportData(parsedDocument, report,
                    providerName, isFileUpload));
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                log.info("Data upload for " + providerName + " took: " + elapsedTime + "ms");
                return saveReport(importReport);
            })
            .orElse(report);

        return documentUploadMapper.toDto(reportToSave.getDocumentUpload());
    }

    private String getRealProviderName(String currentProviderName) {
        if (userService.isCurrentUserAdmin()) {
            return currentProviderName;
        }

        Optional<UserProfile> user = userService.getCurrentUserProfileOptional();
        if (user.isPresent()) {
            if (user.get().getSystemAccount() != null) {
                return user.get().getSystemAccount().getName();
            } else {
                throw new IllegalStateException("No System Account is attached to the user");
            }
        } else {
            throw new IllegalStateException("User has to be authorized to determine the provider");
        }
    }

    private void fillLists(List<FileInfo> fileInfoList, List<String> parsedDocuments, List<DocumentUpload> documentUploads) {
        for (FileInfo fileInfo : fileInfoList) {
            String parsedDoc = mongoDbService.findParsedDocumentById(fileInfo.getParsedDocumentId());
            parsedDocuments.add(parsedDoc);

            DocumentUpload docUpload = documentUploadRepository.findByParsedDocumentId(fileInfo.getParsedDocumentId());
            docUpload.setFilename(fileInfo.getFilename());
            documentUploads.add(docUpload);
        }
    }

    private DataImportReport saveReport(DataImportReport report) {
        report.setEndDate(ZonedDateTime.now());
        return dataImportReportService.save(report);
    }
}
