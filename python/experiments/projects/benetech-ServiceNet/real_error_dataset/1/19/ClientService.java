package org.benetech.servicenet.service;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.benetech.servicenet.client.ServiceNetAuthClient;
import org.benetech.servicenet.domain.ClientProfile;
import org.benetech.servicenet.domain.SystemAccount;
import org.benetech.servicenet.errors.BadRequestAlertException;
import org.benetech.servicenet.errors.ErrorConstants;
import org.benetech.servicenet.errors.IdAlreadyUsedException;
import org.benetech.servicenet.repository.ClientProfileRepository;
import org.benetech.servicenet.repository.SystemAccountRepository;
import org.benetech.servicenet.service.dto.ClientDTO;
import org.benetech.servicenet.service.dto.ClientProfileDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing clients.
 */
@Service
@Transactional
public class ClientService {

    private final Logger log = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    private ClientProfileRepository clientProfileRepository;

    @Autowired
    private ClientProfileService clientProfileService;

    @Autowired
    private ServiceNetAuthClient authClient;

    @Autowired
    private SystemAccountRepository systemAccountRepository;

    /**
     * Create a new client.
     *
     * @param clientDto client to create
     * @return created client
     */
    public ClientDTO createClient(ClientDTO clientDto) throws BadRequestAlertException {
        try {
            ClientDTO result = authClient.createClient(clientDto);
            this.createOrUpdateClientProfile(result, clientDto);
            return this.getRealDto(result);
        } catch (HystrixBadRequestException e) {
            if (e instanceof IdAlreadyUsedException) {
                throw new BadRequestAlertException(ErrorConstants.EMAIL_ALREADY_USED_TYPE, "Id already used!", "clientManagement", "clientexists");
            }
            throw e;
        }
    }

    /**
     * Update all information for a specific user, and return the modified client.
     *
     * @param clientDto client to update
     * @return updated user
     */
    public ClientDTO updateClient(ClientDTO clientDto) {
        ClientDTO result = authClient.updateClient(clientDto);
        this.createOrUpdateClientProfile(result, clientDto);
        return this.getRealDto(result);
    }

    public void deleteClient(String clientId) {
        Optional<ClientProfile> system = clientProfileService.findById(clientId);
        if (system.isPresent()) {
            authClient.deleteClient(clientId);
            clientProfileService.delete(clientId);
            log.debug("Deleted Client: {}", clientId);
        } else {
            throw new IllegalStateException("Client can not be deleted");
        }
    }

    public ClientDTO getClient(String clientId) {
        return this.getRealDto(authClient.getClient(clientId));
    }

    @Transactional(readOnly = true)
    public List<ClientDTO> getAllClients(Pageable pageable) {
        List<ClientDTO> clients = authClient.getAllClients(pageable);
        return clients.stream()
            .map(this::getRealDto)
            .collect(Collectors.toList());
    }

    private ClientDTO getRealDto(ClientDTO clientDTO) {
        clientProfileRepository.findByClientId(clientDTO.getClientId()).ifPresent(clientProfile -> {
            clientDTO.setSystemAccountId(clientProfile.getSystemAccount().getId());
        });
        return clientDTO;
    }

    private void createOrUpdateClientProfile(ClientDTO authClientDTO, ClientDTO clientDto) {
        ClientProfile clientProfile = this.getOrCreateClientProfile(authClientDTO);
        clientProfile.setSystemAccount(getSystemAccount(clientDto));
        clientProfile.setClientId(clientDto.getClientId());
        clientProfileService.save(clientProfile);
    }

    private SystemAccount getSystemAccount(ClientDTO clientDto) {
        UUID systemAccountId = clientDto.getSystemAccountId();
        if (systemAccountId != null) {
            return systemAccountRepository.findById(systemAccountId).orElse(null);
        }
        return null;
    }

    public ClientProfile getOrCreateClientProfile(ClientDTO clientDTO) {
        Optional<ClientProfile> existingProfile = clientProfileRepository.findByClientId(clientDTO.getClientId());
        if (existingProfile.isPresent()) {
            return existingProfile.get();
        } else {
            ClientProfileDto clientProfileDto = new ClientProfileDto();
            clientProfileDto.setClientId(clientDTO.getClientId());
            clientProfileDto.setSystemAccount(clientDTO.getSystemAccountId());
            return clientProfileService.save(clientProfileDto);
        }
    }
}
