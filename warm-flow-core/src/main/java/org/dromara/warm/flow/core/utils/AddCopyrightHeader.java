package org.dromara.warm.flow.core.utils;

import java.io.*;

public class AddCopyrightHeader {
    public static void main(String[] args) throws Exception {
        //项目的绝对路径，也就是想修改的文件路径
        String filePath = "D:\\IdeaProjects\\min\\RuoYi-Vue-Warm-Flow\\warm-flow";
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
        fileTree(f, content);
    }

    /**
     * 取出所有的文件及文件夹
     *
     * @param f 文件夹对象
     */
    public static void fileTree(File f, String content) throws Exception {
        File[] t = f.listFiles();
        if (t != null) {
            for (File file : t) {
                if (file.isDirectory()) {
                    fileTree(file, content);
                } else {
                    insert(file, content);
                }
            }
        }
    }

    /**
     * 开始插入内容（仅在没有版权信息时插入）
     *
     * @param f       文件对象
     * @param content 要插入的版权内容
     */
    public static void insert(File f, String content) throws IOException {
        // 只处理 .java 文件
        if (!f.getName().endsWith(".java")) {
            return;
        }

        // 创建临时文件用于内容备份
        File temp = File.createTempFile("temp", null);
        temp.deleteOnExit();

        try (
            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            FileOutputStream tempOut = new FileOutputStream(temp);
            FileInputStream tempInput = new FileInputStream(temp);
        ) {
            // 检查是否已经包含版权信息
            String copyrightLine = "Copyright 2024-2025, Warm-Flow";
            boolean hasCopyright = false;

            // 读取文件前 1024 字节，用于判断是否已包含版权信息
            byte[] buffer = new byte[1024];
            int bytesRead = raf.read(buffer);
            if (bytesRead > 0) {
                String fileStart = new String(buffer, 0, bytesRead);
                if (fileStart.contains(copyrightLine)) {
                    hasCopyright = true;
                }
            }

            if (hasCopyright) {
                System.out.println("跳过插入版权信息：" + f.getAbsolutePath());
                return;
            }

            // 如果没有版权信息，则进行插入操作
            raf.seek(0);
            while ((bytesRead = raf.read(buffer)) > 0) {
                tempOut.write(buffer, 0, bytesRead);
            }

            raf.seek(0);
            raf.write(content.getBytes());

            byte[] tempBuffer = new byte[64];
            int hasRead;
            while ((hasRead = tempInput.read(tempBuffer)) > 0) {
                raf.write(tempBuffer, 0, hasRead);
            }
        }
    }
}
