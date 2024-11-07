package org.dromara.warm.flow.core.utils;

import java.io.*;

public class AddCopyrightHeader {
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
