package hycu.investigator.msa.file;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class FilePathHandler { //파일 시스템을 순회하면서 특정 확장자의 파일만 찾아내는 프로그램(DFS:재귀)

        public static void main(String[] args) {
//		String dir = "c:\\";
            String dir = "/dev";
//		String extFilter = "(doc|docx|ppt|pptx|xls|xlsx|hwp|hwpx|pdf)";
            String extFilter = "(pdf)";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            System.out.println("START : "+sdf.format(new Date()));

            FilePathHandler s = new FilePathHandler();
            try {
                Set<String> set = s.listFilesUsingFileWalkAndVisitor(dir, extFilter);
                System.out.println(set.size());
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("END : "+sdf.format(new Date()));
        }

        public Set<String> listFilesUsingFileWalkAndVisitor(String dir, String extFilter) throws IOException {
            Set<String> fileList = new HashSet<>();
            Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    String dirName = dir.toString();
                    String[] split = dirName.split("\\\\");
                    if( split.length > 0 && split[split.length-1].indexOf("$") == 0 ) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (!Files.isDirectory(file)) {
                        String fname = file.getFileName().toString();
                        String ext = fname.lastIndexOf(".") > 0 ? fname.substring(fname.lastIndexOf(".")+1) : null;
                        ext = ext != null ? ext.toLowerCase() : null;

                        if( ext != null && ext.matches(extFilter) ) {
                            fileList.add(file.getFileName().toString());
                            System.out.println(file.toAbsolutePath().toString());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return super.visitFileFailed(file, exc);
                }
            });
            return fileList;
        }

    }


