package org.benetech.servicenet.service.impl;

import java.util.UUID;
import org.benetech.servicenet.service.UserGroupService;
import org.benetech.servicenet.domain.UserGroup;
import org.benetech.servicenet.repository.UserGroupRepository;
import org.benetech.servicenet.service.dto.UserGroupDTO;
import org.benetech.servicenet.service.mapper.UserGroupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link UserGroup}.
 */
@Service
@Transactional
public class UserGroupServiceImpl implements UserGroupService {

    private final Logger log = LoggerFactory.getLogger(UserGroupServiceImpl.class);

    private final UserGroupRepository userGroupRepository;

    private final UserGroupMapper userGroupMapper;

    public UserGroupServiceImpl(UserGroupRepository userGroupRepository, UserGroupMapper userGroupMapper) {
        this.userGroupRepository = userGroupRepository;
        this.userGroupMapper = userGroupMapper;
    }

    @Override
    public UserGroupDTO save(UserGroupDTO userGroupDTO) {
        log.debug("Request to save UserGroup : {}", userGroupDTO);
        UserGroup userGroup = userGroupMapper.toEntity(userGroupDTO);
        userGroup = userGroupRepository.save(userGroup);
        return userGroupMapper.toDto(userGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserGroupDTO> findAll(Pageable pageable) {
        log.debug("Request to get all UserGroups");
        return userGroupRepository.findAll(pageable)
            .map(userGroupMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<UserGroupDTO> findOne(UUID id) {
        log.debug("Request to get UserGroup : {}", id);
        return userGroupRepository.findById(id)
            .map(userGroupMapper::toDto);
    }

    @Override
    public void delete(UUID id) {
        log.debug("Request to delete UserGroup : {}", id);
        userGroupRepository.deleteById(id);
    }

    public Optional<UserGroupDTO> findOneByName(String name) {
        log.debug("Request to get user group : {}", name);
        return  userGroupRepository.getByName(name)
            .map(userGroupMapper::toDto);
    }
}
