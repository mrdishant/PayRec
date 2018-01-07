package com.nearur.payrec;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddEmployee extends Fragment {

    private static final int REQUEST_CODE = 1234;
    private EditText name, mobile, address, job, salary, date_of_join;
    private RadioButton piece_rate, salaried;
    private Button submit;
    public Boss boss;

    private CircleImageView imageView;
    private ProgressBar progressBar;
    private String profileURl;


    public AddEmployee() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_employee, container, false);

        name = (EditText) v.findViewById(R.id.name_employee);
        mobile = (EditText) v.findViewById(R.id.mobile_number);
        salary = (EditText) v.findViewById(R.id.salary);
        address = (EditText) v.findViewById(R.id.address);
        job = (EditText) v.findViewById(R.id.editJob);
        date_of_join = (EditText) v.findViewById(R.id.date_of_join);

        piece_rate = (RadioButton) v.findViewById(R.id.piece_rate);
        salaried = (RadioButton) v.findViewById(R.id.salried);

        progressBar = (ProgressBar) v.findViewById(R.id.progress);
        imageView = (CircleImageView) v.findViewById(R.id.profile_image_add);

        submit = (Button) v.findViewById(R.id.add_employee);

        Date d = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date_of_join.setText(dateFormat.format(d).toString());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String n = name.getText().toString().trim();
                final String m = mobile.getText().toString().trim();
                final String a = address.getText().toString().trim();
                final String j = job.getText().toString().trim();
                final String s = salary.getText().toString().trim();
                final String d = date_of_join.getText().toString().trim();
                if (validate(n, m, s, a, j, d)) {
                    final AlertDialog dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("Adding Employee");
                    builder.setMessage("Are you Sure to Add ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (salaried.isChecked()) {
                                Salaried worker = new Salaried();
                                worker.name = n;
                                worker.mobile = Long.parseLong(m);
                                worker.address = a;
                                worker.job = j;
                                worker.salary = Long.parseLong(s);
                                worker.date_join = d;
                                if (profileURl != null) {
                                    worker.picture = profileURl;
                                }

                                addemployee(worker, "Salaried");
                            } else {
                                DailyWaged worker = new DailyWaged();
                                worker.name = n;
                                worker.mobile = Long.parseLong(m);
                                worker.address = a;
                                worker.job = j;
                                worker.date_join = d;

                                if (profileURl != null) {
                                    worker.picture = profileURl;
                                }
                                addemployee(worker, "DailyWaged");
                            }

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });

                    dialog = builder.create();
                    dialog.show();

                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openchooser();
            }
        });

        salaried.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (salaried.isChecked()) {
                    salary.setVisibility(View.VISIBLE);
                } else {
                    salary.setVisibility(View.GONE);
                }
            }
        });


        return v;
    }

    private void openchooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);

    }

    private void addemployee(Worker worker, String type) {


        progressBar.setVisibility(View.VISIBLE);
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (documentReference != null) {
            documentReference = documentReference.collection(type).document();
            worker.id = documentReference.getId();
            documentReference.set(worker, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressBar.setVisibility(View.GONE);
                    clearall();
                    Toast.makeText(getContext(), "Worker added Successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Some Error Occured Please Try Again Later", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void clearall() {

        name.setText("");
        salary.setText("");
        job.setText("");
        mobile.setText("");
        address.setText("");
        imageView.setImageResource(R.drawable.ic_menu_camera);
    }

    private boolean validate(String n, String m, String s, String a, String j, String d) {

        if (n.isEmpty()) {
            name.setError("Name is Required");
            name.requestFocus();
            return false;
        }
        if (m.isEmpty()) {
            mobile.setError("Mobile Number is Required");
            mobile.requestFocus();
            return false;
        }
        if (a.isEmpty()) {
            address.setError("Address is Required");
            address.requestFocus();
            return false;
        }
        if (j.isEmpty()) {
            job.setError("Job is Required");
            job.requestFocus();
            return false;
        }
        if (d.isEmpty()) {
            date_of_join.setError("Date of Join is Required");
            date_of_join.requestFocus();
            return false;
        }
        if (salaried.isChecked() || piece_rate.isChecked()) {
            if (salaried.isChecked()) {
                if (s.isEmpty()) {
                    salary.setError("Salary is Required");
                    salary.requestFocus();
                    return false;
                }
            }
        } else {
            salaried.requestFocus();
            salaried.setError("Please Select One");
            return false;
        }


        if (m.length() != 10) {
            mobile.setError("Valid Mobile Number is Required");
            mobile.requestFocus();
            return false;
        }

        return true;
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                Glide.with(getContext()).load(data.getData()).into(imageView);
                uploadpic(data.getData());
            } else {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadpic(Uri uri) {

        progressBar.setVisibility(View.VISIBLE);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Workers/" + System.currentTimeMillis() + ".jpg");

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.GONE);
                profileURl = taskSnapshot.getDownloadUrl().toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Some Error Occured", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
