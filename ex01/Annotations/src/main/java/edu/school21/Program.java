package edu.school21;


import edu.school21.annotation.HtmlForm;
import edu.school21.annotation.HtmlInput;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

public class Program {
  public static void main(String[] args) throws IOException {
    UserForm userForm = new UserForm();
    checkAnnotation(userForm.getClass());
  }

  private static void checkAnnotation(Class clazz) throws IOException {
    if (clazz.isAnnotationPresent(HtmlForm.class)) {
      clazz.getDeclaredFields();
      HtmlForm htmlForm = (HtmlForm) clazz.getAnnotation(HtmlForm.class);
      String fileName = htmlForm.fileName();
      String method = htmlForm.method();
      String action = htmlForm.action();
      String dir = Program.class.getResource("/").getFile();
      File file = new File(dir + fileName);
      FileOutputStream fileOutputStream = new FileOutputStream(file);
      StringBuilder form = new StringBuilder("<form action = \"" + action + "\" method = \"" + method + "\">");
      for (Field field: clazz.getDeclaredFields()) {
        if (field.isAnnotationPresent(HtmlInput.class)) {
          HtmlInput htmlInput = field.getAnnotation(HtmlInput.class);
          form
            .append("\n")
            .append("<input type = \"" + htmlInput.type() + "\" name = \"" + htmlInput.name() + "\" placeholder = \"" + htmlInput.placeholder() + "\">");
        }
      }
      form
        .append("\n")
        .append("<input type = \"submit\" value = \"Send\">\n")
        .append("</form>");

      fileOutputStream.write(form.toString().getBytes());
      fileOutputStream.close();
    }
  }
}
