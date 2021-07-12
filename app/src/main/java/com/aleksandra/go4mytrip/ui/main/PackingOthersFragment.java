package com.aleksandra.go4mytrip.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aleksandra.go4mytrip.lists.PackingListAdapter;
import com.aleksandra.go4mytrip.lists.PackingModel;
import com.aleksandra.go4mytrip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PackingOthersFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    List<PackingModel> packingList;
    RecyclerView recyclerView;
    FirebaseUser user;
    String uid;
    String idTrip;
    DatabaseReference referencePackingList;
    PackingModel deletedItem = null;
    Context context;
    Boolean isPacking = true;

    public static PackingOthersFragment newInstance(int index) {
        PackingOthersFragment fragment = new PackingOthersFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 4;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);*/

        packingList = new ArrayList<>();
        idTrip = getActivity().getIntent().getStringExtra("idTripToChange");
        referencePackingList = FirebaseDatabase.getInstance().getReference("packingList");
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tab_packing_others, container, false);
        //final TextView textView = root.findViewById(R.id.section_label);
        recyclerView = root.findViewById(R.id.rv_packing_item);
       /* pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        referencePackingList.child(uid).child("trips").child(idTrip).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                packingList.clear();
                for (DataSnapshot packingSnapshot : dataSnapshot.getChildren()) {
                    PackingModel packingModel = packingSnapshot.getValue(PackingModel.class);
                    if (packingModel.getCategory().equals("Others")) {
                        packingList.add(packingModel);
                    }
                }

                PackingListAdapter myAdapter = new PackingListAdapter(getActivity(), packingList, isPacking);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(myAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}