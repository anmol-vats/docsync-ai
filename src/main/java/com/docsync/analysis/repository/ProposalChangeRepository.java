package com.docsync.analysis.repository;

import com.docsync.analysis.entity.ProposalChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProposalChangeRepository
        extends JpaRepository<ProposalChange, UUID> {

    List<ProposalChange> findAllByProposal_Id(UUID proposalId);
}
