package com.docsync.analysis.entity;

import com.docsync.analysis.entity.ProposalChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProposalChangeRepository
        extends JpaRepository<ProposalChange, UUID> {
}