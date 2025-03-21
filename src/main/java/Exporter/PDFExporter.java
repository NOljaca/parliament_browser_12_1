package Exporter;

import Bundestag.Persons.Int.SpeakerInt;
import Bundestag.Session.Int.SessionInt;
import Database.MongoDBHandler;
import Database.MongoDB_Impl.Speaker_MongoDB_Impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PDFExporter {
    private final MongoDBHandler mongoDBHandler;
    private static final String TEX_DIR = "src/main/resources/public/static/tex_output";
    private static final String PDFLATEX_CMD = "pdflatex";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public PDFExporter(MongoDBHandler mongoDBHandler) {
        this.mongoDBHandler = mongoDBHandler;
        File texDir = new File(TEX_DIR);
        if (!texDir.exists()) {
            System.out.println("Creating " + texDir.getAbsolutePath());
            texDir.mkdirs();
        }
    }

    public void exportSpeakerPdf(String speakerId) {
        SpeakerInt speaker = new Speaker_MongoDB_Impl(mongoDBHandler.getDatabase(), speakerId);
        StringBuilder latex = new StringBuilder();
        latex.append(latexBegin());
        latex.append(speaker.toTexSpeaker());
        latex.append(latexEnd());
        String texFileName = "speaker.tex";
        File texFile = new File(TEX_DIR, texFileName);

        try {
            Files.write(texFile.toPath(), latex.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Perform double compilation because LaTex needs to compile at least twice for the expected result
        File workingDir = new File(TEX_DIR);
        try {
            compileLatex(texFileName, workingDir);
            compileLatex(texFileName, workingDir);
            System.out.println("PDF generated successfully in " + TEX_DIR);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportSessionsToPDF(List<String> sessionIds) {
        List<SessionInt> sessions = getSessions(sessionIds);
        StringBuilder latex = new StringBuilder();
        latex.append(latexBegin());
        for (SessionInt session : sessions) {
            latex.append(session.toTex());
        }
        latex.append(latexEnd());
        String texFileName = "sessions.tex";
        File texFile = new File(TEX_DIR, texFileName);

        try {
            Files.write(texFile.toPath(), latex.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Perform double compilation because LaTex needs to compile at least twice for the expected result
        File workingDir = new File(TEX_DIR);
        try {
            compileLatex(texFileName, workingDir);
            compileLatex(texFileName, workingDir);
            System.out.println("PDF generated successfully in " + TEX_DIR);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<SessionInt> getSessions(List<String> sessionIds) {
        List<SessionInt> sessions = new ArrayList<>();
        for (String sessionId : sessionIds) {
            SessionInt session = mongoDBHandler.getSessionById(sessionId);
            sessions.add(session);
        }
        return sessions;
    }

    private String latexBegin() {
        StringBuilder latex = new StringBuilder();
        latex.append("\\documentclass{article}\n")
                .append("\\usepackage[utf8]{inputenc}\n")
                .append("\\usepackage[T1]{fontenc}\n")
                .append("\\usepackage{xcolor}\n")
                .append("\\DeclareUnicodeCharacter{202F}{\\,}\n")
                .append("\\DeclareUnicodeCharacter{02BC}{'}\n")
                .append("\\begin{document}\n");
        return latex.toString();
    }

    private String latexEnd() {
        return "\\end{document}\n";
    }

    /**
     * Compiles the given TeX file using pdflatex.
     * @param texFileName the name of the TeX file (e.g. "sessions.tex")
     * @param workingDir the working directory where the TeX file is located
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the process is interrupted
     */
    private void compileLatex(String texFileName, File workingDir) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                PDFLATEX_CMD,
                "-interaction=nonstopmode",
                texFileName
        );
        processBuilder.directory(workingDir);
        processBuilder.inheritIO(); // Redirects output to console (optional)
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("LaTeX compilation error, exit code: " + exitCode);
        }
    }

    public void deleteFiles() {
        File texDir = new File(TEX_DIR);
        if (texDir.exists() && texDir.isDirectory()) {
            deleteDirectoryContents(texDir);
            System.out.println("All files in " + TEX_DIR + " have been deleted.");
        } else {
            System.out.println("Directory " + TEX_DIR + " does not exist or is not a directory.");
        }
    }

    private void deleteDirectoryContents(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectoryContents(file);
                }
                if (!file.delete()) {
                    System.err.println("Failed to delete " + file.getAbsolutePath());
                }
            }
        }
    }

}
