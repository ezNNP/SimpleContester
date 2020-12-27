package ru.stray27.scontester.services.implementations.executors;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import ru.stray27.scontester.entities.Attempt;
import ru.stray27.scontester.entities.AttemptStatus;
import ru.stray27.scontester.entities.ProgrammingLanguage;
import ru.stray27.scontester.services.annotations.Executor;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log4j2
@Executor(language = ProgrammingLanguage.JAVA)
public class JavaExecutor extends AbstractExecutor {

    @Value("${JAVA}")
    private String JAVA;
    @Value("${JAVAC}")
    private String JAVAC;

    private String sourceCodeFilename;
    private String compiledFilename;

    @Override
    public boolean testExecutablePath() {
        processBuilderService.startProcess(JAVAC, "--version");
        if (!processBuilderService.isError()) {
            processBuilderService.startProcess(JAVA, "--version");
            return !processBuilderService.isError();
        }
        return false;
    }

    @Override
    public AttemptStatus preExecute(Attempt attempt) {
        this.sourceCodeFilename = testDirectoryPath + "Main.java";
        this.compiledFilename = "Main";
        copySourceFile(attempt.getSourceCodeFilename(), this.sourceCodeFilename);
        if (!compile()) {
            return AttemptStatus.COMPILATION_ERROR;
        }
        return AttemptStatus.COMPILED;
    }

    @SneakyThrows
    @Override
    protected void postExecute(Attempt attempt) {
        Files.deleteIfExists(Paths.get(testDirectoryPath + "Main.java"));
        Files.deleteIfExists(Paths.get(testDirectoryPath + "Main.class"));
    }

    protected boolean compile() {
        processBuilderService.startProcess(JAVAC, this.sourceCodeFilename);
        return !processBuilderService.isError();
    }

    @Override
    protected boolean runWithStdInput(String[] inputs) {
        processBuilderService.startProcess(JAVA, "-cp", testDirectoryPath, compiledFilename);
        for (String input : inputs) {
            processBuilderService.writeInput(input);
        }
        return !processBuilderService.isError();
    }

    @Override
    protected boolean runWithFileInput() {
        processBuilderService.startProcess(JAVA, "-cp", testDirectoryPath, compiledFilename);
        return !processBuilderService.isError();
    }
}
