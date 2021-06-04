package com.example.im.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.example.im.R;
import com.example.im.activity.contacts.ContactInfoActivity;
import com.example.im.activity.contacts.ContactSearchActivity;
import com.example.im.activity.contacts.GroupCreatingActivity;
import com.example.im.adapter.contacts.ContactAdapter;
import com.example.im.bean.contacts.Contact;
import com.example.im.listener.OnItemClickListener;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {
    public static final String EXTRA_MESSAGE = "extra_message";
    public static final int TEXT_REQUEST = 1;
    private static final int CONTACT_TYPE_LIST = 0x00001;  // 列表中的联系人

    private ContactAdapter contactAdapter;
    private LinkedList<Contact> contacts;
    private RecyclerView recyclerView;
    private LinearLayout searchLayout;
    private LinearLayout groupLayout;

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContactsFragment.
     */
    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Context context = getActivity();
        recyclerView = view.findViewById(R.id.contacts_recylerview);
        searchLayout = view.findViewById(R.id.layout_new_contact);
        groupLayout = view.findViewById(R.id.layout_new_group);

        // 点击事件：跳转至联系人搜索界面
        searchLayout.setOnClickListener(new  View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, ContactSearchActivity.class);
                startActivityForResult(intent, TEXT_REQUEST);
            }
        });

        // 点击事件：跳转至创建界面
        groupLayout.setOnClickListener(new  View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupCreatingActivity.class);
                startActivityForResult(intent, TEXT_REQUEST);
            }
        });

        // 添加数据，为recyclerView绑定Adapter、LayoutManager
        contacts = new LinkedList<Contact>();
        contacts.add(new Contact(getString(R.string.nickname1), R.drawable.avatar1));
        contacts.add(new Contact(getString(R.string.nickname2), R.drawable.avatar2));

        contactAdapter = new ContactAdapter(contacts, context);
        contactAdapter.setOnItemClickListener(new OnItemClickListener() {
            // 每个联系人的点击事件：跳转至联系人信息界面
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(getActivity(), "Position: " + position, 5000).show();
                Intent intent = new Intent(getActivity(), ContactInfoActivity.class);
                intent.putExtra("Type", CONTACT_TYPE_LIST);
                intent.putExtra("Position", position);
                startActivityForResult(intent, TEXT_REQUEST);
            }
        });
        recyclerView.setAdapter(contactAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }
}