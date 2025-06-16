package com.telemed.service;

import com.telemed.model.SystemLog;
import com.telemed.repository.SystemLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemLogService {

    private final SystemLogRepository repo;

    public SystemLogService(SystemLogRepository repo) {
        this.repo = repo;
    }

    public void log(String action, String performedBy) {
        repo.save(new SystemLog(action, performedBy));
    }

    public List<SystemLog> getAllLogs() {
        return repo.findAll();
    }
}
