package max.dirscan.output.format;

import max.dirscan.output.FileReader;
import max.dirscan.output.FileWriter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class FileFormatter {

    private Path file;
    private Charset charset;

    public FileFormatter(Path file, Charset charset) {
        this.file = file;
        this.charset = charset;
    }

    protected abstract String formatEntry(Path path) throws IOException;

    public void format() {
        String formattedFilePath = file.toFile().getAbsolutePath() + "_formatted";
        Path formattedFile = Paths.get(formattedFilePath);
        try (
                FileReader reader = new FileReader(file, charset);
                FileWriter writer = new FileWriter(formattedFile, charset)
        ) {
            String filePath;
            while ((filePath = reader.readLine()) != null) {
                Path path = Paths.get(filePath);
                String formattedEntry = formatEntry(path);
                if(!formattedEntry.isEmpty()){
                    writer.write(formattedEntry);
                }
            }
            writer.write("\n");
            Files.deleteIfExists(file);
            Files.move(formattedFile, file);
        } catch (NoSuchFileException e) {
            throw new RuntimeException(String.format("File %s not Found!", file.toFile().getAbsolutePath()), e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}