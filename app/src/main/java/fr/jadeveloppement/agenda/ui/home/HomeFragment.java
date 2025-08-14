package fr.jadeveloppement.agenda.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import fr.jadeveloppement.agenda.MainActivity;
import fr.jadeveloppement.agenda.components.Popup;
import fr.jadeveloppement.agenda.components.PopupTask;
import fr.jadeveloppement.agenda.databinding.FragmentHomeBinding;
import fr.jadeveloppement.agenda.functions.Functions;
import fr.jadeveloppement.agenda.functions.sqlite.tables.TasksTable;

public class HomeFragment extends Fragment {

    private final String TAG = "JADagenda";

    private FragmentHomeBinding binding;

    private RecyclerView homeTaskDoneContainer, homeTaskDoneContainerJ1;
    private ImageButton homeAddTask;

    private TextView homeTodayTasksTv, homeJ1TasksTv;

    private ScrollView homeScrollview;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String date = Functions.getTodayDate();

        homeTaskDoneContainer = binding.homeTaskDoneContainer;
        homeTodayTasksTv = binding.homeTodayTasksTv;
        homeAddTask = binding.homeAddTask;
        homeScrollview = binding.homeScrollview;
        homeJ1TasksTv = binding.homeJ1TasksTv;

        homeTaskDoneContainerJ1 = binding.homeTaskDoneContainerJ1;

        String homeTitle = "Tâches du\n" + Functions.convertStdDateToLongLocale(date);
        homeTodayTasksTv.setText(homeTitle);

        String dateJ1 = Functions.addXDay(Functions.getTodayDate(), 1);

        String homeTitleJ1 = "Tâche du\n" + Functions.convertStdDateToLongLocale(dateJ1);
        homeJ1TasksTv.setText(homeTitleJ1);

//        TaskItemAdapter adapter = new TaskItemAdapter(requireContext(), date, null, homeTaskDoneContainer, false);
//        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(homeTaskDoneContainer);
//        homeTaskDoneContainer.setAdapter(adapter);
//        homeTaskDoneContainer.setLayoutManager(new LinearLayoutManager(requireContext()));

//        TaskItemAdapter adapterJ1 = new TaskItemAdapter(requireContext(), dateJ1, null, homeTaskDoneContainer, false);
//        ItemTouchHelper.Callback callbackJ1 = new SimpleItemTouchHelperCallback(adapterJ1);
//        ItemTouchHelper touchHelperJ1 = new ItemTouchHelper(callbackJ1);
//        touchHelperJ1.attachToRecyclerView(homeTaskDoneContainerJ1);
//        homeTaskDoneContainerJ1.setAdapter(adapterJ1);
//        homeTaskDoneContainerJ1.setLayoutManager(new LinearLayoutManager(requireContext()));

        homeAddTask.setOnClickListener(v -> {
            Popup addTask = new Popup(requireContext(), binding.getRoot());
            PopupTask addTaskContent = new PopupTask(requireContext(), binding.getRoot(), null);
            addTask.addContent(addTaskContent);

            addTaskContent.getBtnSave().setOnClickListener(v1 -> {
                String label = addTaskContent.getTaskLabel().getText().toString();
                if (label.isBlank()) Toast.makeText(requireContext(), "Le label ne peut pas être vide.", Toast.LENGTH_LONG).show();
                else {
                    TasksTable newTask = new TasksTable();
                    newTask.label = label;
                    newTask.date = date;

                    boolean formValid = true;

                    if (formValid){
                        addTaskContent.getTaskLabel().setText("");
                        Toast.makeText(requireContext(), "Tâche rajoutée avec succès", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(requireContext(), "Des informations sont manquantes, veuillez les complétez.", Toast.LENGTH_LONG).show();
                    }
                }
                addTask.closePopup();
            });
        });

        homeScrollview.post(() -> {
            homeScrollview.setPadding(
                    homeScrollview.getPaddingLeft(),
                    homeScrollview.getPaddingTop(),
                    homeScrollview.getPaddingRight(),
                    MainActivity.getNavViewHeight() + 10
            );
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}