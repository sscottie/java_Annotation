package edu.school21;

import edu.school21.classes.car.Car;
import edu.school21.classes.user.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class Program {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        User user = null;
        try {
            System.out.println("Classes:");
            Class[] classes = getClasses("edu.school21.classes");
            for (Class c : classes){
                System.out.println(c.getSimpleName());
            }
            System.out.println("---------------------");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Enter class name:");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        String className = reader.readLine();
        if (className.equals("User")) {
            createMessage(reader, User.class);
        } else if (className.equals("Car")) {
            createMessage(reader, Car.class);
        } else {
            throw new RuntimeException("There is no such class!");
        }
        reader.close();
    }

    private static void createMessage(BufferedReader reader, Class clazz) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException, NoSuchFieldException {
        userClassFields(clazz);
        userClassMethods(clazz);
        System.out.println("---------------------");
        Object object = createObject(clazz, reader);
        System.out.println("Object created: " + object);
        updateField(object, reader);
        System.out.println("Object updated: " + object);
        invokeMethod(object, reader);
    }

    private static void invokeMethod(Object object, BufferedReader reader) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        System.out.println("----------");
        System.out.println("Enter name of the method for call:");
        Class clazz = object.getClass();
        String methodName = reader.readLine();
        Method[] methods = clazz.getDeclaredMethods();
        Method method = null;
        for (Method method1: methods) {
            if (method1.getName().equals(methodName)) {
                method = method1;
                break;
            }
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        for (int i = 0; i < method.getParameterCount(); i++) {
            System.out.println("Enter " + method.getParameterTypes()[i].getSimpleName() + " value:");
            arrayList.add(getNextArgument(method.getParameterTypes()[i], reader));
        }
        Object invoke = method.invoke(object, arrayList.toArray(new Object[arrayList.size()]));
        System.out.println("Method returned:");
        System.out.println(invoke);
    }

    public static void userClassFields(Class clazz) {
        Field[] allFields = clazz.getDeclaredFields();
        System.out.println("fields:");
        for (Field f : allFields) {
            System.out.println("\t" + f.getType().getSimpleName() + " " + f.getName());
        }
    }

    static private Map.Entry<String, String> getNameAndPacket(Class clazz) {
        return new AbstractMap.SimpleEntry<>(clazz.getSimpleName(), clazz.getName().replaceAll("." + clazz.getSimpleName(), ""));
    }

    public static void userClassMethods(Class clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        System.out.println("methods :");
        if (methods.length != 0) {
            for (Method method: methods) {
                if (getNameAndPacket(method.getReturnType()).getKey().equals("void") || method.getName().equals("toString")) continue;
                System.out.print("    ");
                System.out.print(getNameAndPacket(method.getReturnType()).getKey() + " ");
                System.out.print(method.getName() + "(");
                for (int i = 0; i < method.getParameterTypes().length; i++) {
                    Class clazzz = method.getParameterTypes()[i];
                    System.out.print(getNameAndPacket(clazzz).getKey());
                    if (i != method.getParameterCount() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.print(")");
                System.out.println();
            }
        }
    }

    private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration resources = classLoader.getResources(path);
        List dirs = new ArrayList();
        while (resources.hasMoreElements()) {
            URL resource = (URL) resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList classes = new ArrayList();
        for (Object directory : dirs) {
            classes.addAll(findClasses((File) directory, packageName));
        }
        return (Class[]) classes.toArray(new Class[classes.size()]);
    }

    private static List findClasses(File directory, String packageName) throws ClassNotFoundException {
        List classes = new ArrayList();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    private static Object createObject(Class clazz, BufferedReader scanner) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
        System.out.println("----------");
        System.out.println("Let's create an object.");
        Field[] fields = clazz.getDeclaredFields();
        ArrayList<Class> paramsType = new ArrayList<>();
        ArrayList<Object> params = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            System.out.println(field.getName() + ":");
            Map.Entry<String ,String> paramType = getNameAndPacket(field.getType());
            params.add(getNextArgument(field.getType(), scanner));
            if (field.getType().isPrimitive()) {
                paramsType.add(parseType(field.getType().getName()));
            } else {
                paramsType.add(Class.forName(paramType.getValue() + "." + paramType.getKey()));
            }
        }
        Constructor constructor = clazz.getConstructor(paramsType.toArray(new Class[paramsType.size()]));
        return constructor.newInstance(params.toArray(new Object[params.size()]));
    }

    public static Class<?> parseType(final String className) {
        switch (className) {
            case "boolean":
                return boolean.class;
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "char":
                return char.class;
            case "void":
                return void.class;
            default:
                return null;
        }
    }

    private static void updateField(Object object, BufferedReader scanner) throws NoSuchFieldException, IllegalAccessException, IOException {
        System.out.println("----------");
        System.out.println("Enter name of the field for changing:");
        String fieldName = scanner.readLine();
        Class clazz = object.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        System.out.println("Enter " + field.getType().getSimpleName() + " value:");
        Object argument = getNextArgument(field.getType(), scanner);
        field.setAccessible(true);
        field.set(object, argument);
    }

    private static Object getNextArgument(Class clazz, BufferedReader scanner) throws IOException {
        switch (clazz.getSimpleName()) {
            case "int":
                return Integer.parseInt(scanner.readLine());
            case "Integer":
                return Integer.parseInt(scanner.readLine());
            case "long":
                return Long.parseLong(scanner.readLine());
            case "Long":
                return Long.parseLong(scanner.readLine());
            case "Double":
                return Double.parseDouble(scanner.readLine());
            case "double":
                return Double.parseDouble(scanner.readLine());
            case "Boolean":
                return Boolean.parseBoolean(scanner.readLine());
            case "boolean":
                return Boolean.parseBoolean(scanner.readLine());
            case "String":
                return scanner.readLine();
            default:
                return null;
        }
    }
}
