import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {

    private static final String FILE_DIRECTORY = "C://Games";
    private static final String SAVE_DIRECTORY = FILE_DIRECTORY + "/savegames";
    private static final String ZIP_FILE_PATH = SAVE_DIRECTORY + "/games.zip";

    public static void main(String[] args) {

        GameProgress game1 = new GameProgress(100, 5, 1, 10.5);
        GameProgress game2 = new GameProgress(80, 3, 2, 20.1);
        GameProgress game3 = new GameProgress(120, 7, 3, 30.2);


        saveGame(SAVE_DIRECTORY + "/save1.dat", game1);
        saveGame(SAVE_DIRECTORY + "/save2.dat", game2);
        saveGame(SAVE_DIRECTORY + "/save3.dat", game3);

       
        List<String> filesToZip = Arrays.asList(
                SAVE_DIRECTORY + "/save1.dat",
                SAVE_DIRECTORY + "/save2.dat",
                SAVE_DIRECTORY + "/save3.dat"
        );
        zipFiles(ZIP_FILE_PATH, filesToZip);

        deleteFilesOutsideArchive(SAVE_DIRECTORY, filesToZip);
    }

    public static void saveGame(String filePath, GameProgress game) {
        try (FileOutputStream zipFileOutputStream = new FileOutputStream(filePath);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(zipFileOutputStream)) {
            objectOutputStream.writeObject(game);
        } catch (IOException e) {
            System.err.println("О-ой! Кажется произошла ошибка сохранения игры: " + e.getMessage());
        }
    }

    public static void zipFiles(String zipFilePath, List<String> filesToZip) {
        try (FileOutputStream zipFileOutputStream = new FileOutputStream(zipFilePath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(zipFileOutputStream)) {
            for (String file : filesToZip) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    Path filePath = Paths.get(file);
                    String fileName = filePath.getFileName().toString();
                    ZipEntry zipEntry = new ZipEntry(fileName);
                    zipOutputStream.putNextEntry(zipEntry);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        zipOutputStream.write(buffer, 0, bytesRead);
                    }
                    zipOutputStream.closeEntry();
                } catch (IOException e) {

                    System.err.println("О-ой! Кажется произошла ошибка zip: " + e.getMessage());
                }
            }
        } catch (IOException e) {

            System.err.println("Упс! Не получилось создать файл zip: " + e.getMessage());
        }
    }

    public static void deleteFilesOutsideArchive(String directory, List<String> filesToZip) {
        try {

            File dir = new File(directory);
            File[] files = dir.listFiles();

            List<File> fileList = Arrays.asList(files);
            List<Path> filePaths = new ArrayList<>();
            for (File file : fileList) {
                filePaths.add(file.toPath());
            }

            for (Path filePath : filePaths) {
                String fileName = filePath.getFileName().toString();
                if (!filesToZip.contains(fileName)) {
                    Files.delete(filePath);
                }
            }
        } catch (IOException e) {
            System.err.println("Ой! Произошла ошибка удаления файлов: " + e.getMessage());
        }
    }
}
