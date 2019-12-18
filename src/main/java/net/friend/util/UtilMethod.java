package net.friend.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UtilMethod {

  public static List<String> getListKeyNameFromFile(String linkFile) {
    File file = new File("D:\\listKeyName.txt");
    Scanner sc = null;
    try {
      sc = new Scanner(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    List<String> listKeyName = new ArrayList<>();
    while (sc.hasNextLine()) {
      listKeyName.add(sc.nextLine().trim());
    }

    return listKeyName;
  }

}
