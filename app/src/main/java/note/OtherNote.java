package note;

import android.content.Context;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 代码笔记
 */
public class OtherNote {

  /**
   * 打开文件流
   * @param context
   * @throws FileNotFoundException
   */
  private void openPrivateFile(Context context) throws FileNotFoundException {
    FileInputStream inputStream = context.openFileInput("path");
    FileOutputStream outputStream = context.openFileOutput("path", context.MODE_PRIVATE);
  }
}
