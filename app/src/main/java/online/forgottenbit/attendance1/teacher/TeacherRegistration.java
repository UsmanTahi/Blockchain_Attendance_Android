package online.forgottenbit.attendance1.teacher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.provider.Settings;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import online.forgottenbit.attendance1.MainActivity;
import online.forgottenbit.attendance1.R;
import online.forgottenbit.attendance1.StudentRegistration;
import online.forgottenbit.attendance1.contract.Attendees;
import online.forgottenbit.attendance1.util.Alice;
import online.forgottenbit.attendance1.util.Web3jConstants;

public class TeacherRegistration extends AppCompatActivity {

    TextView singIn, signUp;

    RelativeLayout signInLayout, signUpLayout;

    EditText name,email,mobile;
    String nameStr,emailStr,mobStr;
    Button submit;

    TeacherDB teacherDB;

    int permission_req_code = 1000;

    static final String ERROR = "Error";
    Attendees contract = null;
    static ECKeyPair KEY_PAIR =null;
    public static Credentials CREDENTIALS =null;
    public static String ADDRESS = null;
    static public Web3j web3j = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);

        if (!defaultPermissionCheck()) {
            askForPermission();
        }

        teacherDB = new TeacherDB(TeacherRegistration.this);

        singIn = findViewById(R.id.singin);
        signUp = findViewById(R.id.singup);

        signInLayout = findViewById(R.id.signinLayout);
        signUpLayout = findViewById(R.id.signupLayout);

        name = findViewById(R.id.editTeacherName);
        email = findViewById(R.id.editTeacherEmail);
        mobile = findViewById(R.id.editTeacherMob);
        submit = findViewById(R.id.submitTeacherDetails);


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpLayout.setVisibility(View.VISIBLE);
                signInLayout.setVisibility(View.GONE);
                signUp.setTextColor(Color.parseColor("#57CAD5"));
                singIn.setTextColor(Color.parseColor("#999999"));

            }
        });


        singIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpLayout.setVisibility(View.GONE);
                signInLayout.setVisibility(View.VISIBLE);
                signUp.setTextColor(Color.parseColor("#999999"));
                singIn.setTextColor(Color.parseColor("#57CAD5"));

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameStr = name.getText().toString().trim();
                emailStr = email.getText().toString().trim();
                mobStr = mobile.getText().toString().trim();

                if(nameStr == null || nameStr.isEmpty() || emailStr == null || emailStr.isEmpty() || mobStr == null || mobStr.isEmpty()){
                    showDialog("Empty Field(s)","All fields are mandatory. Fill all fields to reigster");
                    return;
                }

                if(!isValidEmail(emailStr)){
                    showDialog("Invalid Email ID", "Please enter valid eamil ID to register.");
                }

                long a=teacherDB.insertTRegistrationDetails(nameStr,emailStr,mobStr,getDeviceIMEI());

                Log.e("insert",".. "+a+"  ");
                // create new private/public key pair
                final ECKeyPair[] keyPair = {null};
                try {
                    keyPair[0] = Keys.createEcKeyPair();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    Log.e(ERROR,"No Such Algorithm Exception");
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                    Log.e(ERROR,"No Such Provider Exception");
                }

                createWallet();
                startActivity(new Intent(TeacherRegistration.this, TeacherDashboard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

    }

    public void showDialog(String title, String message){
        new AlertDialog.Builder(TeacherRegistration.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }


    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    @SuppressLint("MissingPermission")
    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            if (!defaultPermissionCheck()) {
                askForPermission();
            } else {
                deviceUniqueIdentifier = tm.getDeviceId();
            }

        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }


    private void askForPermission() {

        //asking  for storage permission from user at runtime

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_PHONE_STATE
        }, permission_req_code);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //checking if user granted the permissions or not

        if (requestCode == permission_req_code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted :)", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "App will not work without permissions, Grant these permissions from settings. :|", Toast.LENGTH_LONG).show();
                startActivity(new Intent(TeacherRegistration.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        }
    }

    private boolean defaultPermissionCheck() {
        //checking if permissions is already granted
        int external_storage_write = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE);
        return external_storage_write == PackageManager.PERMISSION_GRANTED;
    }

    private void createWallet(){
        // create new private/public key pair
        final ECKeyPair[] keyPair = {null};
        try {
            keyPair[0] = Keys.createEcKeyPair();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.e(ERROR,"No Such Algorithm Exception");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            Log.e(ERROR,"No Such Provider Exception");
        }

        //save the fey pair to the class for further use
        BigInteger publicKey = keyPair[0].getPublicKey();
        Alice.PUBLIC_KEY = Numeric.toHexStringWithPrefix(publicKey);

        BigInteger privateKey = keyPair[0].getPrivateKey();
        Alice.PRIVATE_KEY = Numeric.toHexStringWithPrefix(privateKey);
        saveFile(Alice.PUBLIC_KEY, "public_key.pem");
        Log.d("Public",Alice.PUBLIC_KEY);
        saveFile(Alice.PRIVATE_KEY, "private_key.pem");
        KEY_PAIR = new ECKeyPair(Numeric.toBigInt(Alice.PRIVATE_KEY), Numeric.toBigInt(Alice.PUBLIC_KEY));

        //generate the credantionals and address
        CREDENTIALS = Credentials.create(KEY_PAIR);
        ADDRESS = CREDENTIALS.getAddress();
    }

    //save the data into internal storage
    public void saveFile(String message, String filename) {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(message.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //load the contract using user credential
    private Attendees loadContract() throws Exception {
        System.out.println("// Deploy contract");

        contract = Attendees
                .load(Web3jConstants.CONTRACT_ADDRESS, TeacherDashboard.web3j, CREDENTIALS, new DefaultGasProvider());

        String contractAddress = contract.getContractAddress();
        System.out.println("Contract address: " + contractAddress);
        return contract;
    }

}
