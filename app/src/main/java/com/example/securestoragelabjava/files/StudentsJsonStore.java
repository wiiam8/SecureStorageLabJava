package com.example.securestoragelabjava.files;

import android.content.Context;

import com.example.securestoragelabjava.model.Student;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class StudentsJsonStore {

    public static final String FILE_NAME = "students.json";

    private StudentsJsonStore() {}

    public static void save(Context context, List<Student> students) throws Exception {
        String json = toJson(students);
        InternalTextStore.writeUtf8(context, FILE_NAME, json);
    }

    public static List<Student> load(Context context) {
        try {
            String json = InternalTextStore.readUtf8(context, FILE_NAME);
            return fromJson(json);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static boolean delete(Context context) {
        return context.deleteFile(FILE_NAME);
    }

    private static String toJson(List<Student> students) throws Exception {
        JSONArray array = new JSONArray();

        for (Student student : students) {
            JSONObject object = new JSONObject();
            object.put("id", student.id);
            object.put("name", student.name);
            object.put("age", student.age);
            array.put(object);
        }

        return array.toString();
    }

    private static List<Student> fromJson(String json) throws Exception {
        JSONArray array = new JSONArray(json);
        List<Student> students = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            students.add(new Student(
                    object.getInt("id"),
                    object.getString("name"),
                    object.getInt("age")
            ));
        }

        return students;
    }
}