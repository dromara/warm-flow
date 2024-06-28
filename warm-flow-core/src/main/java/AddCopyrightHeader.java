/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
 
public class AddCopyrightHeader {
    private static final String COPYRIGHT =
            "/*\n" +
                    " *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).\n" +
                    " *\n" +
                    " *    Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                    " *    you may not use this file except in compliance with the License.\n" +
                    " *    You may obtain a copy of the License at\n" +
                    " *\n" +
                    " *       https://www.apache.org/licenses/LICENSE-2.0\n" +
                    " *\n" +
                    " *    Unless required by applicable law or agreed to in writing, software\n" +
                    " *    distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    " *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    " *    See the License for the specific language governing permissions and\n" +
                    " *    limitations under the License.\n" +
                    " */";

    public static void addCopyright(File file) {
        if (file.isFile() && file.canWrite() && file.getName().endsWith(".java")) {
            try (FileWriter writer = new FileWriter(file, true)) {
                // 确保在文件开始添加注释
                writer.write(COPYRIGHT);
                writer.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void traverse(File root) {
        if (root.isDirectory()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    traverse(file);
                }
            }
        } else {
            addCopyright(root);
        }
    }
//
//    public static void main(String[] args) {
//        // 替换此路径为你想要遍历的根目录
//        File root = new File("/Users/minliuhua/Desktop/mdata/file/IdeaProjects/min/RuoYi-Vue-Warm-Flow/warm-flow");
//        traverse(root);
//    }

    public static void main(String[] args) throws Exception{
        //项目的绝对路径，也就是想修改的文件路径
        String filePath = "/Users/minliuhua/Desktop/mdata/file/IdeaProjects/min/RuoYi-Vue-Warm-Flow/warm-flow";
        File f = new File(filePath);
        String content = "/*\n" +
                " *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).\n" +
                " *\n" +
                " *    Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                " *    you may not use this file except in compliance with the License.\n" +
                " *    You may obtain a copy of the License at\n" +
                " *\n" +
                " *       https://www.apache.org/licenses/LICENSE-2.0\n" +
                " *\n" +
                " *    Unless required by applicable law or agreed to in writing, software\n" +
                " *    distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                " *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                " *    See the License for the specific language governing permissions and\n" +
                " *    limitations under the License.\n" +
                " */\n";
        fileTree(f,content);
    }

    /**
     * 取出所有的文件及文件夹
     * @param f 文件夹对象
     * @throws Exception
     */
    public static void fileTree(File f,String content) throws Exception{
        File [] t = f.listFiles();
        for (int i = 0; i < t.length; i++) {
            if(t[i].isDirectory()){
                fileTree(t[i],content);
            }else{
                insert(t[i],content);
            }
        }
    }

    /*public static void main(String[] args) {
        try {
            readFileByLines();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 开始插入内容
     * @param f 文件对象
     * @throws IOException
     */
    public static void insert(File f,String content) throws IOException{
        File temp = File.createTempFile("temp", null);
        if (f.getName().endsWith(".java")) {
            temp.deleteOnExit();
            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            FileOutputStream tempOut = new FileOutputStream(temp);
            FileInputStream tempInput = new FileInputStream(temp);
            raf.seek(0);
            byte[] buf = new byte[64];
            int hasRead = 0;
            while ((hasRead = raf.read(buf))>0) {
                tempOut.write(buf, 0, hasRead);
            }
            raf.seek(0);

            raf.write(content.getBytes());
            while ((hasRead = tempInput.read(buf))>0) {
                raf.write(buf,0,hasRead);
            }
            raf.close();
            tempOut.close();
            tempInput.close();
        }

    }
}
