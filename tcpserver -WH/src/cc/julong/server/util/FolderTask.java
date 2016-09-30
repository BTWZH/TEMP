package cc.julong.server.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public abstract class FolderTask {

    // 重新查找文件夹的方法
    public static int GetTodoFiles(Queue<String> q, Set<String> currclq,
            String folderPath, String pattern) {
        // 查找需要处理的文件夹
        List<String> folders = new ArrayList<String>();
        findFils5(folders, new File(folderPath), pattern, 0);
        int count = 0;
        for (String f : folders) {
            if (!currclq.contains(f)) {
                currclq.add(f);
                q.add(f);
                count++;
            }
        }
        return count;
    }

    private static void findFils5(List<String> list, File path, String partten,
            int level) {
        int folderDepSize = 5;
        File[] files = path.listFiles();
        if (files != null) {
            level++;
            if (level > folderDepSize) {
                return;
            }
            for (File f : files) {
                if (!f.isDirectory()) {
                    if (f.getName().startsWith(partten)
                            && level == folderDepSize)
                        list.add(f.getAbsolutePath());
                } else {
                    findFils5(list, f, partten, level);
                }
            }

        }
    }

}
