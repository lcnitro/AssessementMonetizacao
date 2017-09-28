package infnet.edu.br.assessementmonetizacao.helper;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

/**
 * Created by joaoluisdomingosxavier on 27/09/17.
 */

public class SaveOrOpenTextFile {

    private static final String FILE_NAME = "anotations.txt";
    private FileOutputStream fileOutputStream;

    public void saveTxt(String name,String email, String password, String cpf, Context context) {

        try {

            fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeChars("nome: " + name);
            objectOutputStream.writeChars(" email: " + email);
            objectOutputStream.writeChars(" senha: " + password);
            objectOutputStream.writeChars(" cpf: " + cpf);

            objectOutputStream.close();
            fileOutputStream.close();

        } catch (IOException e) {
            Log.i("Erro: ", e.toString());
        }
    } // end saveTxt()

    public String openTxt(Context context) {
        String result = "";
        try {

            InputStream inputStream = context.openFileInput(FILE_NAME);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                // generate buffer
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                // recover data
                String lineFile = "";
                while ((lineFile = bufferedReader.readLine()) != null) {
                    result += lineFile;
                }
                inputStream.close();
            }

        } catch (IOException e) {
            Log.i("Erro ao ler txt", e.toString());
        }

        return result;
    } // End openTxt()

    public void deleteTxt(Context context){
        context.deleteFile(FILE_NAME);
    }
}
