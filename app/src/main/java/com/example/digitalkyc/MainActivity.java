package com.example.digitalkyc;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final String IP="10.0.0.3";
    static int k=0;
    private  static final int RESULT_LOAD_IMAGE=1, RESULT_CAP_IMG=0;
    String rgval="";
    ImageView imgupl,imgdwn;
    Button btnupl,btndwn,capture,send;
    TextView res;
    File photofile;
    String Path="";
//    EditText upltxt,dwntxt;
    Handler h;
    String currentPhotoPath;
    Bitmap Imagetotake;
    RadioGroup rg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgupl=(ImageView)findViewById(R.id.imgupl);
        btnupl=(Button)findViewById(R.id.btnupl);
        capture=(Button)findViewById(R.id.capture);
        res=(TextView)findViewById(R.id.result);
        send=(Button)findViewById(R.id.send);
        rg=(RadioGroup)findViewById(R.id.groupradio);
        rg.clearCheck();
        if(Build.VERSION.SDK_INT>=23){
            requestPermissions(new String[]
                    {Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2);
        }
//        btndwn=(Button)findViewById(R.id.btndwn);
//        imgdwn=(ImageView)findViewById(R.id.imgdwn);
//        upltxt=(EditText)findViewById(R.id.txtupl);
//        dwntxt=(EditText)findViewById(R.id.txtdwn);

        setOnClickListener();
    }

    public  void setOnClickListener()
    {
        btnupl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallaryIntent, RESULT_LOAD_IMAGE);
                    k=1;
//                    Toast.makeText(MainActivity.this,"Path= "+Path,Toast.LENGTH_LONG).show();
            }
        });
        imgupl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call any function here to execute when the image is clicked.
               if(k==0)
                Toast.makeText(MainActivity.this,"Insert Appropriate Image",Toast.LENGTH_SHORT).show();
            }
        });

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
                k=1;
//                Intent captureImg=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if(captureImg.resolveActivity(getPackageManager())!=null)
//                    startActivityForResult(captureImg,RESULT_CAP_IMG);
            }
        });

        rg.setOnCheckedChangeListener(
                new RadioGroup
                        .OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId)
                    {
                        RadioButton
                                radioButton
                                = (RadioButton)group
                                .findViewById(checkedId);
                        if(checkedId!=-1)
                            Toast.makeText(MainActivity.this,"Checked is: "+radioButton.getText().toString(),Toast.LENGTH_SHORT).show();
                    }
                });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rg.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(MainActivity.this,
                            "No card type has been selected",
                            Toast.LENGTH_SHORT)
                            .show();
                }
                else if(k==0)
                    Toast.makeText(MainActivity.this,
                            "No image has been selected",
                            Toast.LENGTH_SHORT)
                            .show();
                else {

                    RadioButton radioButton
                            = (RadioButton)rg
                            .findViewById(selectedId);
                    rgval=radioButton.getText().toString();
                    if(rgval.equals("Driving Licence"))
                        rgval="1";
                    else if(rgval.equals("Aadhar Card"))
                        rgval="2";
//                    Bitmap bitmap= BitmapFactory.decodeFile(currentPhotoPath);
                    ByteArrayOutputStream bos=new ByteArrayOutputStream();
                    Imagetotake.compress(Bitmap.CompressFormat.JPEG,0,bos);
                    byte[] array=bos.toByteArray();
                    // Upload the picture.
                    SendImageClient sc=new SendImageClient();
//                    Toast.makeText(MainActivity.this,"Path= "+Path,Toast.LENGTH_LONG).show();
//                   sc.execute();
                    sc.execute(array);
                    rg.clearCheck();
                }
            }
        });
    }
    public class SendImageClient extends AsyncTask<byte[],Void,Void>
    {
        Handler handler=new Handler();
        @Override
        protected Void doInBackground(byte[]... bytes) {
            try {
                Socket socket=new Socket(IP,7800);
                BufferedReader br=new BufferedReader
                        (new InputStreamReader
                                (socket.getInputStream()));

                PrintWriter pw=new PrintWriter(socket.getOutputStream());
                pw.println(rgval);
                pw.flush();
                while (true)
                {
                    final String check=br.readLine();
////                    handler.post(new Runnable() {
////                        @Override
////                        public void run() {
////                            res.setText("Check = "+check);
////                        }
//                    });
                    if(check.equals("1"))
                        break;
                }
//                OutputStream out=socket.getOutputStream();
//                DataOutputStream dos=new DataOutputStream(out);
//                dos.write(bytes[0],0,bytes[0].length);
//                dos.flush();
//                FileInputStream fis=new FileInputStream(Path);
//                byte[]buffer=new byte[fis.available()];
//                fis.read(buffer);
                ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(bytes[0]);
                oos.flush();
//                dos.writeInt(bytes[0].length);
//                dos.write(bytes[0],0,bytes[0].length);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"image sent",Toast.LENGTH_SHORT).show();
                    }
                });
                while (true)
                {
                    String result="";
                    String line="";
                    while((line=br.readLine())!=null)
                        result+="\n"+line;
                    final String show=result;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            res.setText(show);
                        }
                    });
                    break;
                }
//                dos.close();
//                out.close();
                pw.close();
                oos.close();
                br.close();
                socket.close();

            }catch(IOException Ioe)
            {
                Ioe.printStackTrace();
            }
            return  null;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK)
        {
            Uri selectedImage=data.getData();
            imgupl.setImageURI(selectedImage);
            Path=selectedImage.getPath();
            try {
                Imagetotake=BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
//            Toast.makeText(MainActivity.this,"Path= "+Path,Toast.LENGTH_LONG).show();
        }
//        if(requestCode==RESULT_CAP_IMG && resultCode==RESULT_OK)
//        {
//            Bundle bundle = data.getExtras();
//            final Bitmap bmp = (Bitmap) bundle.get("data");
//            imgupl.setImageBitmap(bmp);
//        }
        if(requestCode==RESULT_CAP_IMG && resultCode==RESULT_OK)
        {
//            Bundle bundle = data.getExtras();
            Bitmap bitmap= BitmapFactory.decodeFile(currentPhotoPath);
            imgupl.setImageBitmap(bitmap);
            Path=photofile.toString();
//            Toast.makeText(MainActivity.this,"Path= "+Path,Toast.LENGTH_LONG).show();
            Imagetotake=bitmap;
        }
    }
    private  void captureImage()
    {
        Intent takePic=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePic.resolveActivity(getPackageManager())!=null){
            photofile=null;
            try{
                photofile=saveImage();
                if(photofile!=null)
                {
                    currentPhotoPath=photofile.getAbsolutePath();

                    Uri photoUri = FileProvider.getUriForFile(MainActivity.this,
                            "com.example.android.fileprovider",
                            photofile);
                    takePic.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                    startActivityForResult(takePic, RESULT_CAP_IMG);
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private File saveImage() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "DigKYC" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
