package infnet.edu.br.assessementmonetizacao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import infnet.edu.br.assessementmonetizacao.R;
import infnet.edu.br.assessementmonetizacao.config.FirebaseConfiguration;
import infnet.edu.br.assessementmonetizacao.model.Usuario;

public class RegisterActivity extends AppCompatActivity {

    private EditText edit_name;
    private EditText edit_email;
    private EditText edit_pass;
    private EditText edit_confirm_pass;
    private EditText edit_cpf;
    private Button btnRegister;
    private Usuario usuario;

    private FirebaseAuth firebaseAuth;

    private static final String FILE_NAME = "anotations.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edit_name           = (EditText) findViewById(R.id.edit_name);
        edit_email          = (EditText) findViewById(R.id.edit_email);
        edit_pass           = (EditText) findViewById(R.id.edit_pass);
        edit_confirm_pass   = (EditText) findViewById(R.id.edit_confirm_pass);
        edit_cpf            = (EditText) findViewById(R.id.edit_cpf);
        btnRegister         = (Button) findViewById(R.id.btn_register);

        SimpleMaskFormatter simpleMaskCpf    = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        MaskTextWatcher maskTextWatcher      = new MaskTextWatcher(edit_cpf, simpleMaskCpf);
        edit_cpf.addTextChangedListener(maskTextWatcher);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name         = edit_name.getText().toString();
                String email        = edit_email.getText().toString();
                String pass         = edit_pass.getText().toString();
                String confirm_pass = edit_confirm_pass.getText().toString();
                String cpf          = edit_cpf.getText().toString();

                // check is email is valid
                if (!isEmailValid(email)) {
                    Toast.makeText(getApplicationContext(),
                            "Por favor insira um email válido",
                            Toast.LENGTH_LONG)
                            .show();
                } else {

                    // check if pass and confirm pass matches
                    if (!isPassMaches(pass, confirm_pass)) {
                        Toast.makeText(getApplicationContext(),
                                "Por favor insira senhas que se coicidem",
                                Toast.LENGTH_LONG)
                                .show();
                    } else {
                        String cpfWithoutFormater = cpf.replace(".", "");
                        cpfWithoutFormater = cpfWithoutFormater.replace("-", "");

                        if (name.isEmpty() || cpfWithoutFormater.isEmpty()) {
                            Toast.makeText(getApplicationContext(),
                                    "Todos os campos devem ser preenchidos",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else {

                            usuario = new Usuario();
                            usuario.setName(name);
                            usuario.setEmail(email);
                            usuario.setPassword(pass);
                            usuario.setCPF(cpfWithoutFormater);

                            hideSoftKeyboard(RegisterActivity.this);

                            registerUser();

                        } // End if name and cpf is empy
                    } // End else if pass match
                } // End else if email is valid
            } // End onClick
        }); // End btnRegister OnClickListener

    } // End onCreate

    private void registerUser() {

        firebaseAuth = FirebaseConfiguration.getFirebaseAuth();
        firebaseAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getPassword())
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            usuario.setId(task.getResult().getUser().getUid());

                            DatabaseReference databaseReference = FirebaseConfiguration
                                    .getFirebase()
                                    .child("users");

                            databaseReference.child(usuario.getId())
                                    .child("Email")
                                    .setValue(usuario.getEmail());

                            databaseReference.child(usuario.getId())
                                    .child("Name")
                                    .setValue(usuario.getName());

                            databaseReference.child(usuario.getId())
                                    .child("Password")
                                    .setValue(usuario.getPassword());

                            databaseReference.child(usuario.getId())
                                    .child("CPF")
                                    .setValue(usuario.getCPF());

                            Toast.makeText(getApplicationContext(),
                                    "Cadastro realizado com sucesso!",
                                    Toast.LENGTH_LONG)
                                    .show();

                            goToMainActivity();

                        } // End if isSuccessfull
                        else {
                            String erroExecao = "";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                erroExecao = "Digite uma senha mais forte de pelo menos 6 digitos, contendo mais caracteres com letras e números.";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                erroExecao = "E-mail ou senha inválidos.";
                            } catch (FirebaseAuthUserCollisionException e) {
                                erroExecao = "E-mail já está cadastrado!";
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), "Erro: " + erroExecao, Toast.LENGTH_SHORT).show();

                        } // End else if isSuccessfull
                    } // End onComplete
                }); // End addOnCompleteListener

    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public static boolean isPassMaches(String pass, String confirm_pass) {
        if (pass.equals(confirm_pass)) {
            return true;
        } else {
            return false;
        }
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

//    private void saveTxt(String nome) {
//
//        try {
//
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("anotacao.txt", Context.MODE_PRIVATE));
//
//            outputStreamWriter.write(nome);
////            outputStreamWriter.write(usuario.getEmail());
////            outputStreamWriter.write(usuario.getPassword());
////            outputStreamWriter.write(usuario.getCPF());
//            outputStreamWriter.close();
//
//        } catch (IOException e) {
//            Log.i("Erro ", e.toString());
//        }
//    }
}
