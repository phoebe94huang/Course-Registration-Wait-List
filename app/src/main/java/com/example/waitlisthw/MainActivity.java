package com.example.waitlisthw;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private waitListAdapter mAdapter;
    private List<waitList> wait_list = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noEntriesView;
    private Button addStudentBtn;

    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noEntriesView = findViewById(R.id.empty_list_view);

        addStudentBtn = findViewById(R.id.addStudent);

        db = new DBHelper(this);

        wait_list.addAll(db.getAllEntries());

        addStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEntryDialog(false, null, -1);
            }
        });

        mAdapter = new waitListAdapter(this, wait_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        noEntriesList();

        // long press to edit entry
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showEditDialog(position);
            }

        }));

        // swipe left to delete entry
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder target, int direction) {
                int position = target.getAdapterPosition();
                deleteEntry(position);

            }
        });
        helper.attachToRecyclerView(recyclerView);
    }

    // wait list with no students on it
    private void noEntriesList() {

        if (db.getWaitListCount() == 0) {
            noEntriesView.setVisibility(View.VISIBLE);
        } else {
            noEntriesView.setVisibility(View.GONE);
        }
    }

    // inserts new entry
    private void addEntry(String student, String priority, String studentID, String email) {
        // inserting entry in db and getting
        // newly inserted entry id
        long id = db.insertEntry(student, priority, studentID, email);

        // get the newly inserted entry from db
        waitList wl = db.getEntry(id);

        // Snackbar to alert user that student has been added
        Snackbar addSnack = Snackbar.make(coordinatorLayout, "Student added to wait list", Snackbar.LENGTH_SHORT);
        addSnack.show();

        if (wl != null) {
            // adding new entry to array list at 0 position
            wait_list.add(0, wl);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            noEntriesList();
        }
    }

    // updates entry in list
    private void updateEntry(String student, String priority, String studentID, String email, int position) {
        waitList wl = wait_list.get(position);
        // updating student text
        wl.setStudent(student);

        // update priority text
        wl.setPriority(priority);

        // update student id
        wl.setStudentID(studentID);

        // update email
        wl.setEmail(email);

        // updating entry in db
        db.updateEntry(wl);

        // refreshing the list
        wait_list.set(position, wl);
        mAdapter.notifyItemChanged(position);

        // Snackbar to confirm that student info was edited
        Snackbar updateSnack = Snackbar.make(coordinatorLayout, "Student info edited", Snackbar.LENGTH_SHORT);
        updateSnack.show();

        noEntriesList();
    }

    // deletes entry
    private void deleteEntry(int position) {
        // deleting the entry from db
        db.deleteEntry(wait_list.get(position));

        // removing the entry from the list
        wait_list.remove(position);
        mAdapter.notifyItemRemoved(position);

        // Snackbar to let user know that student was deleted
        Snackbar deleteSnack = Snackbar.make(coordinatorLayout, "Student deleted from wait list", Snackbar.LENGTH_SHORT);
        deleteSnack.show();

        noEntriesList();
    }

    // Dialog to confirm edit entry
    private void showEditDialog(final int position) {
        CharSequence choices[] = new CharSequence[]{"Yes, I want to edit this student's info", "No, go back to the list"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Would you like to edit this entry?");
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showEntryDialog(true, wait_list.get(position), position);
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    // Dialog to enter or edit an entry
    private void showEntryDialog(final boolean shouldUpdate, final waitList entry, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.entry_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputStudent = view.findViewById(R.id.studentInfo);
        final EditText inputPriority = view.findViewById(R.id.studentPriority);
        final EditText inputStudentID = view.findViewById(R.id.studentID);
        final EditText inputEmail = view.findViewById(R.id.studentEmail);

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.newStudentTitle) : getString(R.string.editStudentTitle));

        if (shouldUpdate && entry != null) {
            inputStudent.setText(entry.getStudent());
            inputPriority.setText(entry.getPriority());
            inputStudentID.setText(entry.getStudentID());
            inputEmail.setText(entry.getEmail());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputStudent.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter student info!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(inputPriority.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter student priority level!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(inputStudentID.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter student ID number!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(inputEmail.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter student email!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user is updating entry
                if (shouldUpdate && entry != null) {
                    // update entry by its id
                    updateEntry(inputStudent.getText().toString(), inputPriority.getText().toString(), inputStudentID.getText().toString(),
                            inputEmail.getText().toString(), position);
                } else {
                    // create new entry
                    addEntry(inputStudent.getText().toString(), inputPriority.getText().toString(), inputStudentID.getText().toString(),
                            inputEmail.getText().toString());
                }
            }
        });
    }
}