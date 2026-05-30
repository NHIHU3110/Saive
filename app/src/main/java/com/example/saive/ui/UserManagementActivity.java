package com.example.saive.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import com.example.saive.R;
import com.example.saive.adapters.UserAdapter;
import com.example.saive.models.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.saive.base.BaseActivity;
public class UserManagementActivity extends BaseActivity {

    private List<User> userList;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnExport).setOnClickListener(v -> 
            Toast.makeText(this, "Exporting user directory...", Toast.LENGTH_SHORT).show()
        );

        RecyclerView rvUsers = findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userList.add(new User("1", "Huỳnh Thảo Nhi", "nhi.huynh@saive.com", "ADMIN", ""));
        userList.add(new User("2", "Lê Minh Tâm", "tam.le@gmail.com", "VIP CUSTOMER", ""));
        userList.add(new User("3", "Trần Hoàng Nam", "nam.tran@outlook.com", "CUSTOMER", ""));
        userList.add(new User("4", "Nguyễn Thu Hà", "ha.nguyen@saive.com", "MANAGER", ""));

        adapter = new UserAdapter(new ArrayList<>(userList));
        rvUsers.setAdapter(adapter);

        EditText etSearch = findViewById(R.id.etUserSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterUsers(String query) {
        List<User> filtered;
        if (query.isEmpty()) {
            filtered = new ArrayList<>(userList);
        } else {
            String q = query.toLowerCase();
            filtered = userList.stream()
                    .filter(u -> u.getName().toLowerCase().contains(q) || u.getEmail().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }
        adapter.updateList(filtered);
    }
}
