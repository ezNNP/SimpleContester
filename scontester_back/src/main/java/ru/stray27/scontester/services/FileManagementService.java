package ru.stray27.scontester.services;

import java.util.List;

public interface FileManagementService {
    String saveSourceCodeFile(Long taskId, String uid, Long attemptId, String[] sourceCode);
    String saveTestsFile(String title, String[] tests);
    List<String> readFile(String path);
}
