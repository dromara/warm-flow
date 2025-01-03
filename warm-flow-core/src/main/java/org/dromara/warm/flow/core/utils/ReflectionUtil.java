package org.dromara.warm.flow.core.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;

public class ReflectionUtil {

    /**
     * 扫描指定包路径下的所有接口，并获取每个接口的第一个直接实现类的对象
     *
     * @param interfacePath 接口路径
     * @param implementPath 实现类路径
     * @return 实现了指定接口的类的对象集合
     */
    public static Map<Class<?>, Object> scanAndInstance(String interfacePath, String implementPath) {
        Map<Class<?>, Object> instances = new HashMap<>();
        Set<Class<?>> interfaces = scanInterfaces(interfacePath);
        for (Class<?> interfaceClass : interfaces) {
            Class<?> implementationClass = findFirstDirectImplementation(implementPath, interfaceClass);
            if (implementationClass != null) {
                try {
                    Object instance = implementationClass.getDeclaredConstructor().newInstance();
                    instances.put(interfaceClass, instance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return instances;
    }

    /**
     * 扫描指定包路径下的所有接口，并获取每个接口的第一个直接实现类的对象
     *
     * @param interfacePath 接口路径
     * @param implementPath 实现类路径
     * @return 实现了指定接口的类的对象集合
     */
    public static Map<Class<?>, Class<?>> scanAndClass(String interfacePath, String implementPath) {
        Map<Class<?>, Class<?>> instances = new HashMap<>();
        Set<Class<?>> interfaces = scanInterfaces(interfacePath);
        for (Class<?> interfaceClass : interfaces) {
            Class<?> implementationClass = findFirstDirectImplementation(implementPath, interfaceClass);
            if (implementationClass != null) {
                try {
                    instances.put(interfaceClass, implementationClass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return instances;
    }

    /**
     * 扫描指定包路径下的所有接口，并获取每个接口的第一个直接实现类的Supplier
     *
     * @param interfacePath 接口路径
     * @param implementPath 实现类路径
     * @return 实现了指定接口的类的Supplier集合
     */
    public static Map<Class<?>, Supplier<?>> scanAndSupplier(String interfacePath, String implementPath) {
        Map<Class<?>, Supplier<?>> suppliers = new HashMap<>();
        Set<Class<?>> interfaces = scanInterfaces(interfacePath);
        for (Class<?> interfaceClass : interfaces) {
            Class<?> implementationClass = findFirstDirectImplementation(implementPath, interfaceClass);
            if (implementationClass != null) {
                try {
                    Constructor<?> constructor = implementationClass.getDeclaredConstructor();
                    suppliers.put(interfaceClass, () -> {
                        try {
                            return constructor.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return suppliers;
    }


    /**
     * 扫描指定包路径下的所有接口的Class<?>
     *
     * @param packagePath 包路径
     * @return 接口集合
     */
    private static Set<Class<?>> scanInterfaces(String packagePath) {
        Set<Class<?>> interfaces = new HashSet<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packagePath.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String filePath = URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8.name());
                File directory = new File(filePath);
                if (directory.exists()) {
                    findInterfaces(directory, packagePath, interfaces);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return interfaces;
    }

    /**
     * 递归查找指定包路径下的所有接口
     *
     * @param directory 目录
     * @param packageName 包路径
     * @param interfaces 接口集合
     */
    private static void findInterfaces(File directory, String packageName, Set<Class<?>> interfaces) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    findInterfaces(file, packageName + "." + file.getName(), interfaces);
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isInterface()) {
                            interfaces.add(clazz);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 查找指定接口的第一个直接实现类
     *
     * @param packagePath 包路径
     * @param interfaceClass 指定接口的Class对象
     * @return 第一个直接实现类的Class对象
     */
    private static Class<?> findFirstDirectImplementation(String packagePath, Class<?> interfaceClass) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packagePath.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String filePath = URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8.name());
                File directory = new File(filePath);
                if (directory.exists()) {
                    return findFirstDirectImplementation(directory, packagePath, interfaceClass);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 递归查找指定包路径下的第一个直接实现类
     *
     * @param directory 目录
     * @param packageName 包路径
     * @param interfaceClass 指定接口的Class对象
     * @return 第一个直接实现类的Class对象
     */
    private static Class<?> findFirstDirectImplementation(File directory, String packageName, Class<?> interfaceClass) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    Class<?> implementation = findFirstDirectImplementation(file, packageName + "." + file.getName(), interfaceClass);
                    if (implementation != null) {
                        return implementation;
                    }
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (isDirectImplementation(clazz, interfaceClass) && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
                            return clazz;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 检查 clazz 是否直接实现了 interfaceClass
     *
     * @param clazz 类
     * @param interfaceClass 接口
     * @return 是否直接实现了接口
     */
    private static boolean isDirectImplementation(Class<?> clazz, Class<?> interfaceClass) {
        // 获取 clazz 直接实现的所有接口
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> iface : interfaces) {
            if (iface.equals(interfaceClass)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        // 示例：扫描org.dromara.warm.flow.core.entity包下的所有接口及其第一个直接实现类，并创建对象
        Map<Class<?>, Supplier<?>> suppliers = scanAndSupplier("org.dromara.warm.flow.core.entity", "org.dromara.warm.flow.orm.entity");
        for (Map.Entry<Class<?>, Supplier<?>> entry : suppliers.entrySet()) {
            System.out.println("Interface: " + entry.getKey().getName() + ", Implementation: " + entry.getValue().get());
        }

        // 示例：扫描org.dromara.warm.flow.core.orm.dao包下的所有接口及其第一个直接实现类，并创建对象
        Map<Class<?>, Object> instances1 = scanAndInstance("org.dromara.warm.flow.core.orm.dao", "org.dromara.warm.flow.orm.dao");
        for (Map.Entry<Class<?>, Object> entry : instances1.entrySet()) {
            System.out.println("Interface: " + entry.getKey().getName() + ", Implementation: " + entry.getValue().getClass().getName());
        }
    }
}
