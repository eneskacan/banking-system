package com.eneskacan.bankingsystem.repository;

import com.eneskacan.bankingsystem.model.Log;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrudLogsRepository extends CrudRepository<Log, Long> {
}
